package mctmods.resourcedatapackloader.common.ownership.claims;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

public final class ClaimTools {
    public static final String BLOCK_OVERLAP = "rdpl.claim.block.overlap";
    public static final String BLOCK_RELEASED = "rdpl.claim.block.released";

    private ClaimTools() {}

    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ignores(event.getItemStack())) { return; }
        BlockPos pos = event.getPos();
        use(player, event.getItemStack(), pos.getX() >> 4, pos.getZ() >> 4);
        event.setCancellationResult(InteractionResult.SUCCESS);
        ClaimProtection.deny(event);
    }

    public static void onClickAir(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ignores(event.getItemStack())) { return; }
        ChunkPos at = player.chunkPosition();
        use(player, event.getItemStack(), at.x, at.z);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof ServerPlayer player) || !ClaimSettings.allows(ClaimSettings.Method.BLOCK) || !ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        ClaimActions.Result result = place(player, level, event.getPos());
        if (!result.ok) { event.setCanceled(true); }
        ClaimCommand.say(player, result);
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !ClaimSettings.any() || !ClaimSettings.isClaimBlock(event.getState().getBlock())) { return; }
        ClaimIndex index = ClaimIndex.get(level);
        ClaimIndex.Anchor anchor = anchorAt(index, event.getPos());
        if (anchor == null) { return; }
        Player player = event.getPlayer();
        if (ClaimSettings.protectBreak() && !ClaimOwners.passes(player, anchor.h)) {
            event.setCanceled(true);
            ClaimMessages.deny(player, anchor.h);
            return;
        }
        if (player instanceof ServerPlayer target) { ClaimCommand.say(target, ClaimActions.Result.yes(BLOCK_RELEASED, dissolve(index, anchor.id))); }
        else { dissolve(index, anchor.id); }
    }

    static int dissolve(ClaimIndex index, int anchorId) {
        List<ChunkPos> named = index.chunksNamingAnchor(anchorId);
        for (ChunkPos pos : named) { index.release(pos.x, pos.z); }
        index.removeAnchor(anchorId);
        return named.size();
    }

    private static boolean ignores(ItemStack held) { return !ClaimSettings.allows(ClaimSettings.Method.ITEM) || !ClaimSettings.isClaimItem(held); }

    private static void use(ServerPlayer player, ItemStack held, int chunkX, int chunkZ) {
        if (player.isShiftKeyDown()) {
            ClaimCommand.say(player, ClaimActions.unclaim(player, player.serverLevel(), chunkX, chunkZ));
            return;
        }
        ClaimActions.Result result = ClaimActions.claim(player, player.serverLevel(), chunkX, chunkZ, 0);
        ClaimCommand.say(player, result);
        if (result.ok && ClaimSettings.itemConsume() && !player.getAbilities().instabuild) { held.shrink(1); }
    }

    private static ClaimActions.Result place(ServerPlayer player, ServerLevel level, BlockPos pos) {
        int handle = ClaimOwners.claimantOf(player);
        if (handle == 0) { return ClaimActions.Result.no(ClaimActions.NO_CLAIMANT); }
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        int radius = ClaimSettings.blockRadius();
        String denied = ClaimRules.canClaim(level, chunkX, chunkZ, handle, false);
        if (radius > 0 && ClaimRules.OWN.equals(denied)) { denied = null; }
        if (denied != null) { return ClaimActions.Result.no(denied); }
        ClaimIndex index = ClaimIndex.get(level);
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
        int claimed = ClaimActions.claimOpen(level, index, open, handle, anchor);
        if (claimed == 0) {
            index.removeAnchor(anchor);
            return ClaimActions.Result.no(ClaimRules.TAKEN);
        }
        MinecraftServer server = level.getServer();
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
