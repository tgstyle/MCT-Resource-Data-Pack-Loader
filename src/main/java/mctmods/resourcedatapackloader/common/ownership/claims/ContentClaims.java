package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;

public final class ContentClaims {
    private ContentClaims() {}

    public static boolean enabled() { return ClaimSettings.any(); }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!enabled() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        ClaimIndex index = ClaimIndex.get(level);
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if ((anchor.x >> 4) == event.getChunk().getPos().x && (anchor.z >> 4) == event.getChunk().getPos().z) { verify(index, anchor, event.getChunk().getBlockState(new BlockPos(anchor.x, anchor.y, anchor.z))); }
        }
    }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !enabled()) { return; }
        MinecraftServer server = event.getServer();
        ClaimDissolve.drain();
        if (server.getTickCount() % ClaimSettings.checkTicks() != 0) { return; }
        for (ServerLevel level : server.getAllLevels()) { sweep(level); }
    }

    private static void sweep(ServerLevel level) {
        ClaimIndex index = ClaimIndex.get(level);
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            BlockPos pos = new BlockPos(anchor.x, anchor.y, anchor.z);
            if (level.isLoaded(pos)) { verify(index, anchor, level.getBlockState(pos)); }
        }
    }

    private static void verify(ClaimIndex index, ClaimIndex.Anchor anchor, BlockState state) {
        if (!ClaimSettings.isClaimBlock(state.getBlock())) { ClaimTools.dissolve(index, anchor.id); }
    }
}
