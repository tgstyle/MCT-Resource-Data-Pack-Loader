package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.world.Coords;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ClaimTools {
    public static final String BLOCK_OVERLAP = "rdpl.claim.block.overlap";
    public static final String BLOCK_RELEASED = "rdpl.claim.block.released";

    private ClaimTools() {}

    @SubscribeEvent public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (ignores(player, event.getItemStack())) { return; }
        BlockPos pos = event.getPos();
        use(player, event.getItemStack(), Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getZ()));
        event.setCancellationResult(EnumActionResult.SUCCESS);
        ClaimProtection.deny(event);
    }

    @SubscribeEvent public static void onClickAir(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (ignores(player, event.getItemStack())) { return; }
        use(player, event.getItemStack(), Coords.getCubeXForEntity(player), Coords.getCubeZForEntity(player));
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        World world = event.getWorld();
        if (world.isRemote || !(event.getEntity() instanceof EntityPlayer) || !ClaimSettings.allows(ClaimSettings.Method.BLOCK) || !ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        ClaimActions.Result result = place(player, world, event.getPos());
        if (!result.ok) { event.setCanceled(true); }
        ClaimCommand.say(player, result);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBreak(BlockEvent.BreakEvent event) {
        World world = event.getWorld();
        if (world.isRemote || !ClaimSettings.any() || !ClaimSettings.isClaimBlock(event.getState().getBlock())) { return; }
        ClaimIndex index = ClaimIndex.obtain(world);
        ClaimIndex.Anchor anchor = index == null ? null : anchorAt(index, event.getPos());
        if (anchor == null) { return; }
        EntityPlayer player = event.getPlayer();
        if (ClaimSettings.protectBreak() && !ClaimOwners.passes(player, anchor.h)) {
            event.setCanceled(true);
            ClaimMessages.deny(player, anchor.h);
            return;
        }
        ClaimCommand.say(player, ClaimActions.Result.yes(BLOCK_RELEASED, dissolve(index, anchor.id)));
    }

    static int dissolve(ClaimIndex index, int anchorId) {
        List<ChunkPos> named = index.chunksNamingAnchor(anchorId);
        for (ChunkPos pos : named) { index.release(pos.x, pos.z); }
        index.removeAnchor(anchorId);
        return named.size();
    }

    private static boolean ignores(EntityPlayer player, ItemStack held) {
        return player.world.isRemote || !ClaimSettings.allows(ClaimSettings.Method.ITEM) || !ClaimSettings.isClaimItem(held);
    }

    private static void use(EntityPlayer player, ItemStack held, int chunkX, int chunkZ) {
        if (player.isSneaking()) {
            ClaimCommand.say(player, ClaimActions.unclaim(player, player.world, chunkX, chunkZ));
            return;
        }
        ClaimActions.Result result = ClaimActions.claim(player, player.world, chunkX, chunkZ, 0);
        ClaimCommand.say(player, result);
        if (result.ok && ClaimSettings.itemConsume() && !player.capabilities.isCreativeMode) { held.shrink(1); }
    }

    private static ClaimActions.Result place(EntityPlayer player, World world, BlockPos pos) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return ClaimActions.Result.no(ClaimActions.NO_CLAIMANT); }
        int chunkX = Coords.blockToCube(pos.getX());
        int chunkZ = Coords.blockToCube(pos.getZ());
        int radius = ClaimSettings.blockRadius();
        String denied = ClaimRules.canClaim(world, chunkX, chunkZ, handle, false);
        if (radius > 0 && ClaimRules.OWN.equals(denied)) { denied = null; }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (denied != null || index == null) { return ClaimActions.Result.no(denied == null ? ClaimRules.DIMENSION : denied); }
        List<ChunkPos> open = new ArrayList<>();
        int taken = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int owner = index.ownerAt(chunkX + dx, chunkZ + dz);
                if (owner == 0 && index.pendingAt(chunkX + dx, chunkZ + dz) == null) { open.add(new ChunkPos(chunkX + dx, chunkZ + dz)); }
                else if (owner != handle && ClaimSettings.blockOverlap() == ClaimSettings.Overlap.REFUSE) { return ClaimActions.Result.no(BLOCK_OVERLAP); }
                else { taken++; }
            }
        }
        open.sort(Comparator.comparingInt(chunk -> Math.max(Math.abs(chunk.x - chunkX), Math.abs(chunk.z - chunkZ))));
        int anchor = index.addAnchor(handle, pos.getX(), pos.getY(), pos.getZ(), radius);
        int claimed = ClaimActions.claimOpen(world, index, open, handle, anchor);
        if (claimed == 0) {
            index.removeAnchor(anchor);
            return ClaimActions.Result.no(ClaimRules.TAKEN);
        }
        MinecraftServer server = world.getMinecraftServer();
        String owner = ClaimOwners.labelOf(server, handle);
        int used = index.claimCountOf(handle);
        int limit = ClaimRules.limitFor(server, handle);
        if (radius == 0) { return ClaimActions.Result.yes(ClaimActions.DONE, chunkX, chunkZ, owner, used, limit); }
        return ClaimActions.Result.yes(ClaimActions.RADIUS_DONE, claimed, owner, taken, open.size(), used, limit);
    }

    @Nullable private static ClaimIndex.Anchor anchorAt(ClaimIndex index, BlockPos pos) {
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if (anchor.x == pos.getX() && anchor.y == pos.getY() && anchor.z == pos.getZ()) { return anchor; }
        }
        return null;
    }
}
