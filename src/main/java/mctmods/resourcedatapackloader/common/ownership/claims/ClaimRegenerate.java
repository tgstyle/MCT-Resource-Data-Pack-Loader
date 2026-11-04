package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.core.util.Compat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;

public final class ClaimRegenerate {
    private static ServerLevel scratchWorld;
    private static int scratchX;
    private static int scratchZ;
    private static ProtoChunk scratch;

    private ClaimRegenerate() {}

    public static void section(ServerLevel world, int chunkX, int chunkZ, int sectionY) {
        LevelChunk live = world.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (live == null || sectionY < world.getMinSectionY() || sectionY > world.getMaxSectionY()) { return; }
        int index = world.getSectionIndexFromSectionY(sectionY);
        ProtoChunk fresh = scratchFor(world, chunkX, chunkZ);
        ClaimRestore.clear(live, sectionY);
        live.getSections()[index] = fresh.getSection(index).copy();
        live.markUnsaved();
    }

    private static ProtoChunk scratchFor(ServerLevel world, int chunkX, int chunkZ) {
        if (scratch == null || scratchWorld != world || scratchX != chunkX || scratchZ != chunkZ) {
            scratch = generate(world, chunkX, chunkZ);
            scratchWorld = world;
            scratchX = chunkX;
            scratchZ = chunkZ;
        }
        return scratch;
    }

    private static ProtoChunk generate(ServerLevel world, int chunkX, int chunkZ) {
        ProtoChunk chunk = new ProtoChunk(new ChunkPos(chunkX, chunkZ), UpgradeData.EMPTY, world, world.palettedContainerFactory(), null);
        Compat.generate(world, chunk);
        return chunk;
    }
}
