package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.AsyncBatchingCubeIO;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.async.CubeIoQueue;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.WorldSavedRubicData;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.world.storage.StorageFormatProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;
import mctmods.resourcedatapackloader.util.Box;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.XYZMap;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.StartupQuery;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Detainted;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CubeProviderServer extends ChunkProviderServer implements ICubeProviderServer, ICubeProviderInternal.IServer {
    @Nonnull private final EmptyColumn emptyColumn;
    @Nonnull private final BlankCube emptyCube;
    @Nonnull private final WorldServer worldServer;
    @Nonnull private final ICubeIO cubeIO;
    @Nonnull private final XYZMap<Cube> cubeMap = new XYZMap<>(0.7f, 8000);
    @Nonnull private final CubePrimer cubePrimer;
    @Nonnull private final ICubeGenerator cubeGen;
    @Nonnull private final Profiler profiler;
    @Nonnull private final XYZMap<Cube> loadingCubes = new XYZMap<>(0.7f, 16);
    @Nonnull private final Set<CubePos> generatingCubes = new HashSet<>();
    private boolean primerInUse;
    private Chunk currentlyLoadingColumn;

    public CubeProviderServer(@Nonnull WorldServer worldServer, @Nonnull ICubeGenerator cubeGen) {
        super(worldServer,
                worldServer.getSaveHandler().getChunkLoader(worldServer.provider),
                worldServer.provider.createChunkGenerator());
        this.cubePrimer = new CubePrimer();
        this.cubeGen = cubeGen;
        this.worldServer = worldServer;
        this.profiler = worldServer.profiler;
        try {
            Path path = worldServer.getSaveHandler().getWorldDirectory().toPath();
            if (worldServer.provider.getSaveFolder() != null) { path = path.resolve(worldServer.provider.getSaveFolder()); }
            World overworld = Objects.requireNonNull(worldServer.getMinecraftServer(), "server").getEntityWorld();
            WorldSavedRubicData savedData = Objects.requireNonNull(
                    (WorldSavedRubicData) overworld.getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, Rubic.DATA_ID),
                    Rubic.DATA_ID + " missing for rubic world");
            StorageFormatProviderBase format = StorageFormatProviderBase.REGISTRY.getValue(savedData.storageFormat);
            if (format == null) {
                StartupQuery.notify("unsupported storage format \"" + savedData.storageFormat + '"');
                StartupQuery.abort();
                throw new IllegalStateException("aborted");
            }
            this.cubeIO = new AsyncBatchingCubeIO(worldServer, format.provideStorage(worldServer, path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.emptyColumn = new EmptyColumn(worldServer, 0, 0);
        this.emptyCube = new BlankCube(emptyColumn);
    }

    @Override @Detainted public void queueUnload(@Nonnull Chunk chunk) {}

    @Override @Detainted public void queueUnloadAll() {}

    @Nullable public Chunk getLoadedColumn(int columnX, int columnZ) {
        Chunk chunk = this.loadedChunks.get(ChunkPos.asLong(columnX, columnZ));
        if (chunk != null) { return chunk; }
        Chunk loading = currentlyLoadingColumn;
        return loading != null && loading.x == columnX && loading.z == columnZ ? loading : null;
    }

    @Nullable @Override @Deprecated public Chunk getLoadedChunk(int columnX, int columnZ) { return getLoadedColumn(columnX, columnZ); }

    @Nullable @Override @Deprecated public Chunk loadChunk(int columnX, int columnZ) { return this.loadChunk(columnX, columnZ, null); }

    @Nullable @Override @Deprecated public Chunk loadChunk(int columnX, int columnZ, @Nullable Runnable runnable) {
        if (runnable == null) { return getColumn(columnX, columnZ, Requirement.LOAD); }
        asyncGetColumn(columnX, columnZ, Requirement.LOAD, col -> runnable.run());
        return null;
    }

    public Chunk provideColumn(int cubeX, int cubeZ) {
        Chunk column = getColumn(cubeX, cubeZ, Requirement.GENERATE);
        return column == null ? emptyColumn : column;
    }

    @Override @Nonnull @Deprecated public Chunk provideChunk(int cubeX, int cubeZ) { return provideColumn(cubeX, cubeZ); }

    @Override public boolean saveChunks(boolean alwaysTrue) {
        for (Cube cube : cubeMap) {
            if (cube.needsSaving()) { this.cubeIO.saveCube(cube); }
        }
        for (Chunk chunk : loadedChunks.values()) {
            if (chunk.needsSaving(alwaysTrue)) { this.cubeIO.saveColumn(chunk); }
        }
        return true;
    }

    @Override public boolean tick() {
        profiler.startSection("providerTick");
        PlayerCubeMap playerCubeMap = ((PlayerCubeMap) this.world.getPlayerChunkMap());
        Iterator<Cube> watchersIterator = playerCubeMap.getCubeIterator();
        while (watchersIterator.hasNext()) { watchersIterator.next().tickCubeServer(); }
        profiler.endSection();
        return false;
    }

    @Override @Nonnull public String makeString() {
        return "CubeProviderServer: " + this.loadedChunks.size() + " columns, "
                + this.cubeMap.getSize() + " cubes";
    }

    @Override @Nonnull public List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull final EnumCreatureType type, @Nonnull final BlockPos pos) { return cubeGen.getPossibleCreatures(type, pos); }

    @Nullable @Override public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String name, @Nonnull BlockPos pos, boolean findUnexplored) { return cubeGen.getClosestStructure(name, pos, findUnexplored); }

    @Override public boolean chunkExists(int cubeX, int cubeZ) { return this.loadedChunks.get(ChunkPos.asLong(cubeX, cubeZ)) != null; }

    @Override public boolean isInsideStructure(@Nonnull World p_193413_1_, @Nonnull String p_193413_2_, @Nonnull BlockPos p_193413_3_) { return false; }

    @Override public Cube getCube(int cubeX, int cubeY, int cubeZ) {
        Cube cube = getCube(cubeX, cubeY, cubeZ, Requirement.GENERATE);
        return cube == null ? emptyCube : cube;
    }

    @Nullable @Override public Cube getLoadedCube(int cubeX, int cubeY, int cubeZ) { return cubeMap.get(cubeX, cubeY, cubeZ); }

    public void asyncGetCube(int cubeX, int cubeY, int cubeZ, Requirement req, Consumer<Cube> callback) {
        Cube cube = getLoadedCube(cubeX, cubeY, cubeZ);
        if (req == Requirement.GET_CACHED || (cube != null && req.compareTo(Requirement.GENERATE) <= 0)) {
            callback.accept(cube);
            return;
        }
        Cube loading = loadingCubes.get(cubeX, cubeY, cubeZ);
        if (loading != null) {
            callback.accept(loading);
            return;
        }
        if (cube == null) { CubeIoQueue.queueCubeLoad(worldServer, cubeIO, this, cubeX, cubeY, cubeZ, req == Requirement.LOAD ? callback : loaded -> callback.accept(getCube(cubeX, cubeY, cubeZ, req))); }
    }

    @Nullable @Override public Cube getCube(int cubeX, int cubeY, int cubeZ, Requirement req) {
        Cube cube = getLoadedCube(cubeX, cubeY, cubeZ);
        if (req == Requirement.GET_CACHED ||
                (cube != null && req.compareTo(Requirement.GENERATE) <= 0)) { return cube; }
        Cube loading = loadingCubes.get(cubeX, cubeY, cubeZ);
        if (loading != null) { return loading; }
        Chunk column = getColumn(cubeX, cubeZ, req);
        if (column == null) { return cube; }
        if (column.isEmpty()) { return emptyCube; }
        if (cube == null) { cube = getLoadedCube(cubeX, cubeY, cubeZ); }
        if (cube != null) { return postCubeLoadAttempt(cubeX, cubeY, cubeZ, cube, column, req); }
        return CubeIoQueue.syncCubeLoad(worldServer, cubeIO, this, cubeX, cubeY, cubeZ, loaded -> postCubeLoadAttempt(cubeX, cubeY, cubeZ, loaded, column, req));
    }

    public void onCubeLoaded(Cube cube) {
        cube.getColumn().addCube(cube);
        cubeMap.put(cube);
        cube.onLoad();
    }

    public void placeReadCube(Cube cube, Runnable readSyncPart) {
        int x = cube.getX();
        int y = cube.getY();
        int z = cube.getZ();
        if (getLoadedColumn(x, z) != cube.getColumn()) { throw new IllegalStateException("Cube at " + x + ", " + y + ", " + z + " was read for a column that is no longer loaded"); }
        if (loadingCubes.get(x, y, z) != null) { throw new IllegalStateException("Cube at " + x + ", " + y + ", " + z + " is already being loaded"); }
        loadingCubes.put(cube);
        try { readSyncPart.run(); }
        finally { loadingCubes.remove(x, y, z); }
        onCubeLoaded(cube);
    }

    @Nullable private Cube postCubeLoadAttempt(int cubeX, int cubeY, int cubeZ, @Nullable Cube cube, Chunk column, Requirement req) {
        if (req == Requirement.LOAD) { return cube; }
        if (req == Requirement.GENERATE && cube != null) { return cube; }
        if (cube == null) {
            cube = generateCube(cubeX, cubeY, cubeZ, column);
            if (req == Requirement.GENERATE) { return cube; }
        }
        if (!cube.isFullyPopulated()) {
            if (!populateCube(cube)) { return cube; }
            if (req == Requirement.POPULATE) { return cube; }
        }
        if (!cube.isInitialLightingDone() || !cube.isSurfaceTracked()) { calculateDiffuseSkylight(cube); }
        if (!cube.isSurfaceTracked()) { cube.trackSurface(); }
        return cube;
    }

    private void generateSkyAbove(int cubeX, int cubeY, int cubeZ) {
        int highest = (((IMinMaxHeight) worldServer).rdpl$getMaxHeight() >> 4) - 1;
        for (int above = highest; above > cubeY; above--) { getCube(cubeX, above, cubeZ, Requirement.GENERATE); }
    }

    private Cube generateCube(int cubeX, int cubeY, int cubeZ, Chunk column) {
        CubePos at = new CubePos(cubeX, cubeY, cubeZ);
        if (!generatingCubes.add(at)) { throw new IllegalStateException("Cube at " + cubeX + ", " + cubeY + ", " + cubeZ + " was asked for while it was being generated; a generator read its own unfinished column"); }
        boolean sharedPrimer = !primerInUse;
        primerInUse = true;
        Cube cube;
        try {
            CubePrimer primer = cubeGen.generateCube(cubeX, cubeY, cubeZ, sharedPrimer ? this.cubePrimer : new CubePrimer());
            cube = new Cube(column, cubeY, primer);
            if (primer == this.cubePrimer) { primer.reset(); }
        }
        finally {
            if (sharedPrimer) { primerInUse = false; }
            generatingCubes.remove(at);
        }
        onCubeLoaded(cube);
        cubeGen.generateAlongside(cube);
        return cube;
    }

    private boolean populateCube(Cube cube) {
        int cubeX = cube.getX();
        int cubeY = cube.getY();
        int cubeZ = cube.getZ();
        Box fullPopulation = cubeGen.getFullPopulationRequirements(cube);
        Set<Cube> newlyPopulatedCubes = new HashSet<>();
        boolean success = fullPopulation.allMatch((x, y, z) -> {
            Cube fullPopulationCube = getCube(x + cubeX, y + cubeY, z + cubeZ);
            Box newBox = cubeGen.getPopulationPregenerationRequirements(fullPopulationCube);
            boolean generated = newBox.allMatch((nx, ny, nz) -> {
                int genX = cubeX + x + nx;
                int genY = cubeY + y + ny;
                int genZ = cubeZ + z + nz;
                return !(getCube(genX, genY, genZ, Requirement.GENERATE) instanceof BlankCube);
            });
            if (!generated) { return false; }
            if (!fullPopulationCube.isPopulated()) {
                cubeGen.populate(fullPopulationCube);
                newlyPopulatedCubes.add(fullPopulationCube);
            }
            return true;
        });
        if (!success) {
            for (Cube newlyPopulatedCube : newlyPopulatedCubes) { newlyPopulatedCube.setPopulated(true); }
            return false;
        }
        for (Cube newlyPopulatedCube : newlyPopulatedCubes) { newlyPopulatedCube.setPopulated(true); }
        cube.setFullyPopulated(true);
        return true;
    }

    private void calculateDiffuseSkylight(Cube cube) {
        generateSkyAbove(cube.getX(), cube.getY(), cube.getZ());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) { continue; }
                Chunk beside = getLoadedColumn(cube.getX() + dx, cube.getZ() + dz);
                if (beside == null || beside.isEmpty()) { continue; }
                generateSkyAbove(cube.getX() + dx, cube.getY(), cube.getZ() + dz);
            }
        }
        ((IRubicWorldInternal) this.worldServer).rdpl$getLightingManager().doFirstLight(cube);
        cube.setInitialLightingDone(true);
    }

    public void asyncGetColumn(int columnX, int columnZ, Requirement req, Consumer<Chunk> callback) {
        Chunk column = getLoadedColumn(columnX, columnZ);
        if (column != null || req == Requirement.GET_CACHED) {
            callback.accept(column);
            return;
        }
        CubeIoQueue.queueColumnLoad(worldServer, cubeIO, columnX, columnZ, col -> {
            col = postProcessColumn(columnX, columnZ, col, req);
            callback.accept(col);
        }, col -> currentlyLoadingColumn = col);
    }

    @Nullable public Chunk getColumn(int columnX, int columnZ, Requirement req) {
        Chunk column = getLoadedColumn(columnX, columnZ);
        if (column != null || req == Requirement.GET_CACHED) { return column; }
        column = CubeIoQueue.syncColumnLoad(worldServer, cubeIO, columnX, columnZ, col -> currentlyLoadingColumn = col);
        column = postProcessColumn(columnX, columnZ, column, req);
        return column;
    }

    @Nullable private Chunk postProcessColumn(int columnX, int columnZ, @Nullable Chunk column, Requirement req) {
        Chunk loaded = getLoadedColumn(columnX, columnZ);
        if (loaded != null) {
            if (column != null && loaded != column) { throw new IllegalStateException("Duplicate column at " + columnX + ", " + columnZ + "!"); }
            return loaded;
        }
        if (column != null) {
            loadedChunks.put(ChunkPos.asLong(columnX, columnZ), column);
            column.setLastSaveTime(this.worldServer.getTotalWorldTime());
            column.onLoad();
            return column;
        }
        else if (req == Requirement.LOAD) { return null; }
        column = new Chunk(world, columnX, columnZ);
        cubeGen.generateColumn(column);
        loadedChunks.put(ChunkPos.asLong(columnX, columnZ), column);
        column.setLastSaveTime(this.worldServer.getTotalWorldTime());
        column.onLoad();
        return column;
    }

    @Override @Nonnull public ICubeIO getCubeIO() { return cubeIO; }

    Iterator<Cube> cubesIterator() { return cubeMap.iterator(); }

    Iterator<Chunk> columnsIterator() { return loadedChunks.values().iterator(); }

    public void unloadColumnCubes(Chunk column) {
        for (Object each : ((IColumn) column).getLoadedCubes().toArray()) {
            Cube cube = (Cube) each;
            if (tryUnloadCube(cube)) { cubeMap.remove(cube.getX(), cube.getY(), cube.getZ()); }
        }
    }

    boolean tryUnloadCube(Cube cube) {
        if (ForgeChunkManager.getPersistentChunksFor(world).containsKey(cube.getColumn().getPos())) { return false; }
        if (!cube.getTickets().canUnload()) { return false; }
        cube.onUnload();
        if (cube.needsSaving()) { this.cubeIO.saveCube(cube); }
        if (cube.getColumn().removeCube(cube.getY()) == null) { throw new RuntimeException(); }
        return true;
    }

    boolean tryUnloadColumn(Chunk column) {
        if (ForgeChunkManager.getPersistentChunksFor(world).containsKey(column.getPos())) { return false; }
        if (((IColumn) column).hasLoadedCubes()) { return false; }
        if (world.getPlayerChunkMap().contains(column.x, column.z)) { return false; }
        if (CubeIoQueue.holdsReads(worldServer, column.x, column.z)) { return false; }
        column.unloadQueued = true;
        column.onUnload();
        if (column.needsSaving(true)) { this.cubeIO.saveColumn(column); }
        return true;
    }

    public ICubeGenerator getCubeGenerator() { return cubeGen; }

    public int getLoadedCubeCount() { return cubeMap.getSize(); }
}
