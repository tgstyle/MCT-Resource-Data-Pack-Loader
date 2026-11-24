package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.TileScan;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;

public final class ClaimRegenerate {
    private static WorldServer scratchWorld;
    private static int scratchX;
    private static int scratchZ;
    private static Chunk scratch;

    private ClaimRegenerate() {}

    public static void section(WorldServer world, int chunkX, int chunkZ, int sectionY) {
        ChunkProviderServer provider = world.getChunkProvider();
        if (provider instanceof CubeProviderServer) { cube(world, (CubeProviderServer) provider, chunkX, chunkZ, sectionY); }
        else { vanilla(world, provider, chunkX, chunkZ, sectionY); }
    }

    private static void vanilla(WorldServer world, ChunkProviderServer provider, int chunkX, int chunkZ, int sectionY) {
        Chunk live = provider.getLoadedChunk(chunkX, chunkZ);
        if (live == null || sectionY < 0 || sectionY >= live.getBlockStorageArray().length) { return; }
        Chunk fresh = scratchFor(world, provider, chunkX, chunkZ);
        ExtendedBlockStorage source = fresh.getBlockStorageArray()[sectionY];
        ExtendedBlockStorage copy = null;
        if (source != null && !source.isEmpty()) {
            copy = new ExtendedBlockStorage(Coords.cubeToMinBlock(sectionY), world.provider.hasSkyLight());
            for (int y = 0; y < ICube.SIZE; y++) {
                for (int z = 0; z < ICube.SIZE; z++) {
                    for (int x = 0; x < ICube.SIZE; x++) { copy.set(x, y, z, source.get(x, y, z)); }
                }
            }
        }
        TileScan.clearSection(world, live.getTileEntityMap().keySet(), sectionY);
        live.getBlockStorageArray()[sectionY] = copy;
        live.markDirty();
    }

    private static Chunk scratchFor(WorldServer world, ChunkProviderServer provider, int chunkX, int chunkZ) {
        if (scratch == null || scratchWorld != world || scratchX != chunkX || scratchZ != chunkZ) {
            scratch = provider.chunkGenerator.generateChunk(chunkX, chunkZ);
            scratchWorld = world;
            scratchX = chunkX;
            scratchZ = chunkZ;
        }
        return scratch;
    }

    private static void cube(WorldServer world, CubeProviderServer provider, int cubeX, int cubeZ, int cubeY) {
        Cube live = provider.getLoadedCube(cubeX, cubeY, cubeZ);
        if (live == null) { return; }
        CubePrimer primer = provider.getCubeGenerator().generateCube(cubeX, cubeY, cubeZ, new CubePrimer());
        IBlockState air = Blocks.AIR.getDefaultState();
        ExtendedBlockStorage copy = null;
        for (int y = 0; y < ICube.SIZE; y++) {
            for (int z = 0; z < ICube.SIZE; z++) {
                for (int x = 0; x < ICube.SIZE; x++) {
                    IBlockState state = primer.getBlockState(x, y, z);
                    if (state == air) { continue; }
                    if (copy == null) { copy = new ExtendedBlockStorage(Coords.cubeToMinBlock(cubeY), world.provider.hasSkyLight()); }
                    copy.set(x, y, z, state);
                }
            }
        }
        TileScan.clearSection(world, live.getTileEntityMap().keySet(), cubeY);
        live.setStorage(copy);
        live.pushOpacity(copy);
    }

}
