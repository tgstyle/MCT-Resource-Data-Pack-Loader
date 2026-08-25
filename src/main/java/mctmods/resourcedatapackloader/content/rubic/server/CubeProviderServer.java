package mctmods.resourcedatapackloader.content.rubic.server;

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
import mctmods.resourcedatapackloader.util.XYZMap;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Detainted;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CubeProviderServer extends ChunkProviderServer implements ICubeProviderServer, ICubeProviderInternal.Server {
    @Nonnull private final EmptyColumn emptyColumn;
    @Nonnull private final BlankCube emptyCube;
    @Nonnull private final WorldServer worldServer;
    @Nonnull private final ICubeIO cubeIO;
    @Nonnull private final XYZMap<Cube> cubeMap = new XYZMap<>(0.7f, 8000);
    @Nonnull private final CubePrimer cubePrimer;
    @Nonnull private final ICubeGenerator cubeGen;
    @Nonnull private final Profiler profiler;
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
                    (WorldSavedRubicData) overworld.getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, "rdplRubicData"),
                    "rdplRubicData missing for rubic world");
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

    @Override @Detainted public void queueUnload(@Nonnull Chunk chunk) {
    }

    @Override @Detainted public void queueUnloadAll() {
    }

    @Nullable @Override public Chunk getLoadedColumn(int columnX, int columnZ) {
        Chunk chunk = this.loadedChunks.get(ChunkPos.asLong(columnX, columnZ));
        return chunk == null ? currentlyLoadingColumn : chunk;
    }

    @Nullable @Override @Deprecated public Chunk getLoadedChunk(int columnX, int columnZ) { return getLoadedColumn(columnX, columnZ); }

    @Nullable @Override @Deprecated public Chunk loadChunk(int columnX, int columnZ) { return this.loadChunk(columnX, columnZ, null); }

    @Nullable @Override @Deprecated public Chunk loadChunk(int columnX, int columnZ, @Nullable Runnable runnable) {
        if (runnable == null) { return getColumn(columnX, columnZ, Requirement.LOAD); }
        asyncGetColumn(columnX, columnZ, Requirement.LOAD, col -> runnable.run());
        return null;
    }

    @Override public Chunk provideColumn(int cubeX, int cubeZ) {
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

    @Nullable @Override public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String name, @Nonnull BlockPos pos, boolean findUnexplored) {
        return cubeGen.getClosestStructure(name, pos, findUnexplored);
    }

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
        if (cube == null) {
            CubeIoQueue.queueCubeLoad(worldServer, cubeIO, this, cubeX, cubeY, cubeZ, loaded -> {
                Cube resident = getLoadedCube(cubeX, cubeY, cubeZ);
                if (resident != null) {
                    callback.accept(postCubeLoadAttempt(cubeX, cubeY, cubeZ, resident, resident.getColumn(), req, false));
                    return;
                }
                Chunk col = getLoadedColumn(cubeX, cubeZ);
                if (col != null) {
                    assert !col.isEmpty();
                    onCubeLoaded(loaded, col);
                    loaded = postCubeLoadAttempt(cubeX, cubeY, cubeZ, loaded, col, req, false);
                }
                callback.accept(loaded);
            });
        }
    }

    @Nullable @Override public Cube getCube(int cubeX, int cubeY, int cubeZ, Requirement req) { return getCube(cubeX, cubeY, cubeZ, req, false); }

    @Nullable @Override public Cube getCubeNow(int cubeX, int cubeY, int cubeZ, Requirement req) { return getCube(cubeX, cubeY, cubeZ, req, true); }

    @Nullable private Cube getCube(int cubeX, int cubeY, int cubeZ, Requirement req, boolean forceNow) {
        Cube cube = getLoadedCube(cubeX, cubeY, cubeZ);
        if (req == Requirement.GET_CACHED ||
                (cube != null && req.compareTo(Requirement.GENERATE) <= 0)) { return cube; }
        Chunk column = getColumn(cubeX, cubeZ, req, forceNow);
        if (column == null) { return cube; }
        if (column.isEmpty()) { return emptyCube; }
        if (cube == null) { cube = getLoadedCube(cubeX, cubeY, cubeZ); }
        if (cube == null) {
            cube = CubeIoQueue.syncCubeLoad(worldServer, cubeIO, this, cubeX, cubeY, cubeZ);
            onCubeLoaded(cube, column);
        }
        return postCubeLoadAttempt(cubeX, cubeY, cubeZ, cube, column, req, forceNow);
    }

    private void onCubeLoaded(@Nullable Cube cube, Chunk column) {
        if (cube != null) {
            cubeMap.put(cube);
            if (!((IColumn) column).getLoadedCubes().contains(cube)) {
                ((IColumn) column).addCube(cube);
                cube.onLoad();
            }
        }
    }

    @Nullable private Cube postCubeLoadAttempt(int cubeX, int cubeY, int cubeZ, @Nullable Cube cube, Chunk column, Requirement req, boolean forceNow) {
        if (cube == null) { cube = getLoadedCube(cubeX, cubeY, cubeZ); }
        if (req == Requirement.LOAD) { return cube; }
        if (req == Requirement.GENERATE && cube != null) { return cube; }
        if (cube == null) {
            if (!forceNow && cubeGen.pollAsyncCubeGenerator(cubeX, cubeY, cubeZ) != ICubeGenerator.GeneratorReadyState.READY) { return emptyCube; }
            cube = generateCube(cubeX, cubeY, cubeZ, column, forceNow).orElse(null);
            if (cube == null) { return emptyCube; }
            if (req == Requirement.GENERATE) { return cube; }
        }
        if (!cube.isFullyPopulated()) {
            if (!forceNow && cubeGen.pollAsyncCubePopulator(cubeX, cubeY, cubeZ) != ICubeGenerator.GeneratorReadyState.READY) { return emptyCube; }
            if (!populateCube(cube, forceNow)) { return cube; }
            if (req == Requirement.POPULATE) { return cube; }
        }
        if (!cube.isInitialLightingDone() || !cube.isSurfaceTracked()) { calculateDiffuseSkylight(cube); }
        if (!cube.isSurfaceTracked()) { cube.trackSurface(); }
        return cube;
    }

    private void generateSkyAbove(int cubeX, int cubeY, int cubeZ, Chunk column) {
        int highest = (((IMinMaxHeight) worldServer).rdpl$getMaxHeight() >> 4) - 1;
        for (int above = highest; above > cubeY; above--) {
            if (getLoadedCube(cubeX, above, cubeZ) != null) { continue; }
            generateCube(cubeX, above, cubeZ, column, true);
        }
    }

    private Optional<Cube> generateCube(int cubeX, int cubeY, int cubeZ, Chunk column, boolean forceGenerate) {
        return cubeGen.tryGenerateCube(cubeX, cubeY, cubeZ, this.cubePrimer, forceGenerate)
                .map(primer -> {
                    Cube already = getLoadedCube(cubeX, cubeY, cubeZ);
                    if (already != null) {
                        if (primer == this.cubePrimer) { primer.reset(); }
                        return already;
                    }
                    Cube cube = new Cube(column, cubeY, primer);
                    onCubeLoaded(cube, column);
                    if (primer == this.cubePrimer) { primer.reset(); }
                    return cube;
                });
    }

    private boolean populateCube(Cube cube, boolean forceNow) {
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
                return !(getCube(genX, genY, genZ, Requirement.GENERATE, forceNow) instanceof BlankCube);
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
        generateSkyAbove(cube.getX(), cube.getY(), cube.getZ(), cube.getColumn());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) { continue; }
                Chunk beside = getLoadedColumn(cube.getX() + dx, cube.getZ() + dz);
                if (beside == null || beside.isEmpty()) { continue; }
                generateSkyAbove(cube.getX() + dx, cube.getY(), cube.getZ() + dz, beside);
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
            col = postProcessColumn(columnX, columnZ, col, req, false);
            callback.accept(col);
        }, col -> currentlyLoadingColumn = col);
    }

    @Nullable @Override public Chunk getColumn(int columnX, int columnZ, Requirement req) { return getColumn(columnX, columnZ, req, false); }

    @Nullable private Chunk getColumn(int columnX, int columnZ, Requirement req, boolean forceNow) {
        Chunk column = getLoadedColumn(columnX, columnZ);
        if (column != null || req == Requirement.GET_CACHED) { return column; }
        column = CubeIoQueue.syncColumnLoad(worldServer, cubeIO, columnX, columnZ, col -> currentlyLoadingColumn = col);
        column = postProcessColumn(columnX, columnZ, column, req, forceNow);
        return column;
    }

    @Nullable private Chunk postProcessColumn(int columnX, int columnZ, @Nullable Chunk column, Requirement req, boolean force) {
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
        if (!force && cubeGen.pollAsyncColumnGenerator(columnX, columnZ) != ICubeGenerator.GeneratorReadyState.READY) { return emptyColumn; }
        column = cubeGen.tryGenerateColumn(world, columnX, columnZ, new ChunkPrimer(), force).orElse(null);
        if (column == null) { return emptyColumn; }
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
        if (!CubeIoQueue.canDropColumn(worldServer, column.x, column.z)) { return false; }
        column.unloadQueued = true;
        column.onUnload();
        if (column.needsSaving(true)) { this.cubeIO.saveColumn(column); }
        return true;
    }

    public ICubeGenerator getCubeGenerator() { return cubeGen; }

    public int getLoadedCubeCount() { return cubeMap.getSize(); }
}
