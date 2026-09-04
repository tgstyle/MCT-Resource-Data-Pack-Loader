package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IChunkProviderClient;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.XYZMap;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CubeProviderClient extends ChunkProviderClient implements ICubeProviderInternal {
    @Nonnull private final IRubicWorldInternal.IClient world;
    @Nonnull private final Cube blankCube;
    @Nonnull private final XYZMap<Cube> cubeMap = new XYZMap<>(0.7f, 8000);

    public CubeProviderClient(@Nonnull IRubicWorldInternal.IClient world) {
        super((World) world);
        this.world = world;
        this.blankCube = new BlankCube(super.provideChunk(Integer.MAX_VALUE, 0));
    }

    @Nullable public Chunk getLoadedColumn(int x, int z) { return getLoadedChunk(x, z); }

    public Chunk provideColumn(int x, int z) { return provideChunk(x, z); }

    @Override @Nonnull public Chunk provideChunk(int x, int z) { return super.provideChunk(x, z); }

    @Nullable @Override public Chunk getLoadedChunk(int x, int z) { return super.getLoadedChunk(x, z); }

    @Override @Nonnull public Chunk loadChunk(int cubeX, int cubeZ) {
        Chunk column = new Chunk((World) this.world, cubeX, cubeZ);
        ((IChunkProviderClient) this).getLoadedChunks().put(ChunkPos.asLong(cubeX, cubeZ), column);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(column));
        column.markLoaded(true);
        return column;
    }

    @Override public boolean tick() {
        long i = System.currentTimeMillis();
        for (Cube cube : cubeMap) { cube.tickCubeCommon(); }
        if (System.currentTimeMillis() - i > 100L) { Rubic.LOGGER.info("Warning: Clientside chunk ticking took {} ms", System.currentTimeMillis() - i); }
        return false;
    }

    @Nullable public Cube loadCube(CubePos pos) {
        Cube cube = getLoadedCube(pos);
        if (cube != null) { return cube; }
        Chunk column = getLoadedColumn(pos.getX(), pos.getZ());
        if (column == null) { return null; }
        cube = new Cube(column, pos.getY());
        ((IColumn) column).addCube(cube);
        this.cubeMap.put(cube);
        world.rdpl$getLightingManager().onCubeLoad(cube, false);
        cube.setCubeLoaded();
        return cube;
    }

    public void unloadCube(CubePos pos) {
        Cube cube = getLoadedCube(pos);
        if (cube == null) { return; }
        cube.onUnload();
        cubeMap.remove(pos.getX(), pos.getY(), pos.getZ());
        cube.getColumn().removeCube(pos.getY());
    }

    @Override public Cube getCube(int cubeX, int cubeY, int cubeZ) {
        Cube cube = getLoadedCube(cubeX, cubeY, cubeZ);
        if (cube == null) { return blankCube; }
        return cube;
    }

    @Nullable @Override public Cube getLoadedCube(int cubeX, int cubeY, int cubeZ) { return cubeMap.get(cubeX, cubeY, cubeZ); }

    @Override @Nonnull public String makeString() {
        return "MultiplayerChunkCache: " + ((IChunkProviderClient) this).getLoadedChunks().values()
                .stream()
                .map(c -> ((IColumn) c).getLoadedCubes().size())
                .reduce(Integer::sum)
                .orElse(-1) + "/" + ((IChunkProviderClient) this).getLoadedChunks().size();
    }
}
