package mctmods.resourcedatapackloader.core.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class ScratchRegion extends WorldGenRegion {
    private final ServerLevel world;
    private final Map<Long, ChunkAccess> chunks = new HashMap<>();

    public ScratchRegion(ServerLevel world, ProtoChunk center) {
        super(world, StaticCache2D.create(0, 0, 0, (_, _) -> null), ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.CARVERS), center);
        this.world = world;
        this.chunks.put(center.getPos().pack(), center);
    }

    @Override public ChunkAccess getChunk(int chunkX, int chunkZ, @Nonnull ChunkStatus status, boolean load) {
        return this.chunks.computeIfAbsent(ChunkPos.pack(chunkX, chunkZ), _ -> new ProtoChunk(new ChunkPos(chunkX, chunkZ), UpgradeData.EMPTY, this.world, this.world.palettedContainerFactory(), null));
    }
}
