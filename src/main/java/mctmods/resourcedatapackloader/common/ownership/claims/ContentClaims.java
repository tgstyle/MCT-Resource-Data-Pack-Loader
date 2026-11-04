package mctmods.resourcedatapackloader.common.ownership.claims;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ContentClaims {
    private ContentClaims() {}

    public static boolean enabled() { return ClaimSettings.any(); }

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) {
        if (!enabled() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        ClaimIndex index = ClaimIndex.obtain(level);
        if (index == null) { return; }
        int chunkX = event.getChunk().getPos().x;
        int chunkZ = event.getChunk().getPos().z;
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if (anchor.x >> 4 == chunkX && anchor.z >> 4 == chunkZ) { verify(index, anchor, event.getChunk().getBlockState(new BlockPos(anchor.x, anchor.y, anchor.z))); }
        }
    }

    @SubscribeEvent public static void onTick(ServerTickEvent.Post event) {
        if (!enabled()) { return; }
        MinecraftServer server = event.getServer();
        ClaimDissolve.drain(server);
        if (server.getTickCount() % ClaimSettings.checkTicks() != 0) { return; }
        for (ServerLevel level : server.getAllLevels()) { sweep(level); }
    }

    private static void sweep(ServerLevel level) {
        ClaimIndex index = ClaimIndex.obtain(level);
        if (index == null) { return; }
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            BlockPos pos = new BlockPos(anchor.x, anchor.y, anchor.z);
            if (level.isLoaded(pos)) { verify(index, anchor, level.getBlockState(pos)); }
        }
    }

    private static void verify(ClaimIndex index, ClaimIndex.Anchor anchor, BlockState state) {
        if (!ClaimSettings.isClaimBlock(state.getBlock())) { ClaimTools.dissolve(index, anchor.id); }
    }
}
