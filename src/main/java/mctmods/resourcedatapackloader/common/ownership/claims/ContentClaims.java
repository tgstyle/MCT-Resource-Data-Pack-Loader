package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;

public final class ContentClaims {
    private ContentClaims() {}

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!ClaimSettings.any() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        ClaimIndex index = ClaimIndex.get(level);
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if ((anchor.x >> 4) == event.getChunk().getPos().x && (anchor.z >> 4) == event.getChunk().getPos().z) { verify(index, anchor, event.getChunk().getBlockState(anchor.pos())); }
        }
    }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ClaimSettings.any()) { return; }
        MinecraftServer server = event.getServer();
        ClaimDissolve.drain();
        if (server.getTickCount() % ClaimSettings.checkTicks() != 0) { return; }
        for (ServerLevel level : server.getAllLevels()) { sweep(level); }
    }

    private static void sweep(ServerLevel level) {
        ClaimIndex index = ClaimIndex.get(level);
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            BlockPos pos = anchor.pos();
            if (level.isLoaded(pos)) { verify(index, anchor, level.getBlockState(pos)); }
        }
    }

    private static void verify(ClaimIndex index, ClaimIndex.Anchor anchor, BlockState state) {
        if (!ClaimSettings.isClaimBlock(state.getBlock())) { ClaimTools.dissolve(index, anchor.id); }
    }
}
