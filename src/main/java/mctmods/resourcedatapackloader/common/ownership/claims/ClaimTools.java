package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.world.ChunkSpan;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

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
        use(player, event.getItemStack(), at.x(), at.z());
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof ServerPlayer player) || !ClaimSettings.allows(ClaimSettings.Method.BLOCK) || !ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        ClaimActions.Result result = place(player, level, event.getPos());
        if (!result.ok()) { event.setCanceled(true); }
        ClaimCommand.say(player.createCommandSourceStack(), result);
    }

    public static void onBreak(BreakBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getPlayer() instanceof ServerPlayer player) || !ClaimSettings.any() || !ClaimSettings.isClaimBlock(event.getState().getBlock())) { return; }
        ClaimIndex index = ClaimIndex.obtain(level);
        ClaimIndex.Anchor anchor = anchorAt(index, event.getPos());
        if (anchor == null) { return; }
        if (ClaimSettings.protectBreak() && !ClaimOwners.passes(player, anchor.h)) {
            event.setCanceled(true);
            ClaimMessages.deny(player, anchor.h);
            return;
        }
        ClaimCommand.say(player.createCommandSourceStack(), ClaimActions.Result.yes(BLOCK_RELEASED, dissolve(index, anchor.id)));
    }

    static int dissolve(ClaimIndex index, int anchorId) {
        List<ChunkPos> named = index.chunksNamingAnchor(anchorId);
        for (ChunkPos pos : named) { index.release(pos.x(), pos.z()); }
        index.removeAnchor(anchorId);
        return named.size();
    }

    private static boolean ignores(ItemStack held) { return !ClaimSettings.allows(ClaimSettings.Method.ITEM) || !ClaimSettings.isClaimItem(held); }

    private static void use(ServerPlayer player, ItemStack held, int chunkX, int chunkZ) {
        if (player.isShiftKeyDown()) {
            ClaimCommand.say(player.createCommandSourceStack(), ClaimActions.unclaim(player, player.level(), chunkX, chunkZ));
            return;
        }
        ClaimActions.Result result = ClaimActions.claim(player, player.level(), chunkX, chunkZ, 0);
        ClaimCommand.say(player.createCommandSourceStack(), result);
        if (result.ok() && ClaimSettings.itemConsume() && !player.isCreative()) { held.shrink(1); }
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
        ClaimIndex index = ClaimIndex.obtain(level);
        List<ChunkPos> open = new ArrayList<>();
        boolean clear = ChunkSpan.forEachWithin(chunkX, chunkZ, radius, (x, z) -> {
            int owner = index.ownerAt(x, z);
            if (owner == 0 && index.pendingAt(x, z) == null) {
                open.add(new ChunkPos(x, z));
                return true;
            }
            return owner == handle || ClaimSettings.blockOverlap() != ClaimSettings.Overlap.REFUSE;
        });
        if (!clear) { return ClaimActions.Result.no(BLOCK_OVERLAP); }
        int taken = (radius * 2 + 1) * (radius * 2 + 1) - open.size();
        int anchor = index.addAnchor(handle, pos.getX(), pos.getY(), pos.getZ(), radius);
        int claimed = ClaimActions.claimOpen(level, index, open, handle, anchor, chunkX, chunkZ);
        if (claimed == 0) {
            index.removeAnchor(anchor);
            return ClaimActions.Result.no(ClaimRules.TAKEN);
        }
        MinecraftServer server = level.getServer();
        if (radius == 0) { return ClaimActions.done(server, chunkX, chunkZ, handle, index); }
        return ClaimActions.radiusDone(server, claimed, handle, taken, open.size(), index);
    }

    @Nullable private static ClaimIndex.Anchor anchorAt(ClaimIndex index, BlockPos pos) {
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if (pos.equals(anchor.pos())) { return anchor; }
        }
        return null;
    }
}
