package mctmods.resourcedatapackloader.core.integration.ftbchunks;

import mctmods.resourcedatapackloader.common.ownership.claims.ForeignClaims;

import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.server.level.ServerLevel;

public final class FtbChunksClaims implements ForeignClaims {
    @Override public boolean isClaimed(ServerLevel level, int chunkX, int chunkZ) { return isActive() && FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(level.dimension(), chunkX, chunkZ)) != null; }

    @Override public boolean isActive() { return FTBChunksAPI.api().isManagerLoaded(); }
}
