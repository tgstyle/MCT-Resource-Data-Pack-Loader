package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.mixin.rdpl.common.INoiseBasedChunkGenerator;
import mctmods.resourcedatapackloader.mixin.rdpl.common.INoiseChunk;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ISurfaceSystem;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Parallel;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.ChunkPos;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class CityGround {
    public static final List<String> VILLAGE_TYPES = List.of("plains", "desert", "savanna", "snowy", "taiga");
    private static final ResourceLocation MANSIONS = ResourceLocation.fromNamespaceAndPath("minecraft", "woodland_mansions");
    private static final int HEIGHTS_HELD = 1 << 18;
    private static final Map<Column, Integer> HEIGHTS = new ConcurrentHashMap<>();
    private static final int CELLS_HELD = 256;
    private static final Map<Cell, CellNoise> CELLS = new ConcurrentHashMap<>();
    private static final DensityFunctions.BeardifierOrMarker MARKER = (DensityFunctions.BeardifierOrMarker) DensityFunctions.BeardifierOrMarker.CODEC.codec().parse(JsonOps.INSTANCE, new JsonObject()).result().orElseThrow();
    private final long seed;
    private final ChunkGenerator generator;
    private final RandomState random;
    private final LevelHeightAccessor height;
    private final RegistryAccess registries;

    private CityGround(long seed, ChunkGenerator generator, RandomState random, LevelHeightAccessor height, RegistryAccess registries) {
        this.seed = seed;
        this.generator = generator;
        this.random = random;
        this.height = height;
        this.registries = registries;
    }

    private record Column(ChunkGenerator generator, RandomState random, int bottom, int depth, Heightmap.Types type, int x, int z) {}

    private record Cell(ChunkGenerator generator, RandomState random, int bottom, int depth, int x, int z) {}

    public static CityGround of(Structure.GenerationContext context) { return new CityGround(context.seed(), context.chunkGenerator(), context.randomState(), context.heightAccessor(), context.registryAccess()); }

    public static CityGround of(WorldGenLevel level) { return new CityGround(level.getSeed(), level.getLevel().getChunkSource().getGenerator(), level.getLevel().getChunkSource().randomState(), level, level.registryAccess()); }

    public static CityGround of(long seed, ChunkGenerator generator, RandomState random, RegistryAccess registries) { return new CityGround(seed, generator, random, LevelHeightAccessor.create(generator.getMinY(), generator.getGenDepth()), registries); }

    public long seed() { return seed; }

    public int sea() { return generator.getSeaLevel(); }

    public int surface(int x, int z) { return height(x, z, Heightmap.Types.WORLD_SURFACE_WG); }

    public int floor(int x, int z) { return height(x, z, Heightmap.Types.OCEAN_FLOOR_WG); }

    private int height(int x, int z, Heightmap.Types type) {
        Column key = column(type, x, z);
        Integer known = HEIGHTS.get(key);
        if (known != null) { return known; }
        ContentPregen.worldgenMoved();
        if (generator.getClass() != NoiseBasedChunkGenerator.class) {
            int found = generator.getBaseHeight(x, z, type, height, random) - 1;
            hold(key, found);
            return found;
        }
        NoiseBasedChunkGenerator noise = (NoiseBasedChunkGenerator) generator;
        NoiseSettings shape = shape(noise);
        if (Math.floorDiv(shape.height(), shape.getCellHeight()) <= 0) {
            hold(key, height.getMinBuildHeight() - 1);
            return height.getMinBuildHeight() - 1;
        }
        int width = shape.getCellWidth();
        Cell cellKey = new Cell(generator, random, height.getMinBuildHeight(), height.getHeight(), Math.floorDiv(x, width), Math.floorDiv(z, width));
        CellNoise cell = CELLS.get(cellKey);
        if (cell == null) {
            CellNoise made = new CellNoise(noise, random, shape, cellKey.x() * width, cellKey.z() * width);
            if (CELLS.size() >= CELLS_HELD) { CELLS.clear(); }
            CellNoise kept = CELLS.putIfAbsent(cellKey, made);
            cell = kept == null ? made : kept;
        }
        return held(cell, x, z, type);
    }

    public void warm(int minX, int minZ, int maxX, int maxZ) {
        if (generator.getClass() != NoiseBasedChunkGenerator.class) { return; }
        NoiseBasedChunkGenerator noise = (NoiseBasedChunkGenerator) generator;
        NoiseSettings shape = shape(noise);
        if (Math.floorDiv(shape.height(), shape.getCellHeight()) <= 0) { return; }
        int width = shape.getCellWidth();
        List<int[]> open = new ArrayList<>();
        for (int cellX = Math.floorDiv(minX, width); cellX <= Math.floorDiv(maxX, width); cellX++) {
            for (int cellZ = Math.floorDiv(minZ, width); cellZ <= Math.floorDiv(maxZ, width); cellZ++) {
                if (!known(cellX * width, cellZ * width, width)) { open.add(new int[] {cellX, cellZ}); }
            }
        }
        if (open.isEmpty()) { return; }
        ContentPregen.worldgenMoved();
        Parallel.each(open.size(), at -> {
            int originX = open.get(at)[0] * width;
            int originZ = open.get(at)[1] * width;
            CellNoise cell = new CellNoise(noise, random, shape, originX, originZ);
            for (int x = originX; x < originX + width; x++) {
                for (int z = originZ; z < originZ + width; z++) { held(cell, x, z, Heightmap.Types.OCEAN_FLOOR_WG); }
            }
        });
    }

    private boolean known(int originX, int originZ, int width) {
        for (int x = originX; x < originX + width; x++) {
            for (int z = originZ; z < originZ + width; z++) {
                if (!HEIGHTS.containsKey(column(Heightmap.Types.WORLD_SURFACE_WG, x, z)) || !HEIGHTS.containsKey(column(Heightmap.Types.OCEAN_FLOOR_WG, x, z))) { return false; }
            }
        }
        return true;
    }

    private int held(CellNoise cell, int x, int z, Heightmap.Types type) {
        int[] tops = cell.tops(x, z, height.getMinBuildHeight());
        hold(column(Heightmap.Types.WORLD_SURFACE_WG, x, z), tops[0] - 1);
        hold(column(Heightmap.Types.OCEAN_FLOOR_WG, x, z), tops[1] - 1);
        return (type == Heightmap.Types.WORLD_SURFACE_WG ? tops[0] : tops[1]) - 1;
    }

    private static void hold(Column key, int found) {
        if (HEIGHTS.size() >= HEIGHTS_HELD) { HEIGHTS.clear(); }
        HEIGHTS.put(key, found);
    }

    private Column column(Heightmap.Types type, int x, int z) { return new Column(generator, random, height.getMinBuildHeight(), height.getHeight(), type, x, z); }

    private NoiseSettings shape(NoiseBasedChunkGenerator noise) { return noise.generatorSettings().value().noiseSettings().clampToHeightAccessor(height); }

    public static void forget() {
        HEIGHTS.clear();
        CELLS.clear();
    }

    public int bottom() { return height.getMinBuildHeight(); }

    public int surfaceDepth(int x, int z) { return ((ISurfaceSystem) random.surfaceSystem()).rdpl$getSurfaceDepth(x, z); }

    public double surfaceNoise(int x, int z) { return ((ISurfaceSystem) random.surfaceSystem()).rdpl$getSurfaceNoise().getValue(x, 0.0, z); }

    public BlockState band(int x, int y, int z) { return ((ISurfaceSystem) random.surfaceSystem()).rdpl$getBand(x, y, z); }

    public BlockState stone() { return generator instanceof NoiseBasedChunkGenerator noise ? noise.generatorSettings().value().defaultBlock() : Blocks.STONE.defaultBlockState(); }

    public Holder<Biome> biome(int x, int z) { return generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(sea()), QuartPos.fromBlock(z), random.sampler()); }

    @Nullable public String villageType(int x, int z) { return villageTag(biome(x, z)); }

    public String villageStyle(int x, int z) {
        Holder<Biome> held = biome(x, z);
        String pack = held.unwrapKey().map(key -> ContentBiomes.villageKind(key.location())).orElse(null);
        if (pack != null) { return pack; }
        String tagged = villageTag(held);
        if (tagged != null) { return tagged; }
        if (held.is(BiomeTags.IS_SAVANNA)) { return "savanna"; }
        if (held.is(BiomeTags.IS_TAIGA)) { return "taiga"; }
        return VILLAGE_TYPES.get(0);
    }

    @Nullable private static String villageTag(Holder<Biome> held) {
        for (String type : VILLAGE_TYPES) {
            if (held.is(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("minecraft", "has_structure/village_" + type)))) { return type; }
        }
        return null;
    }

    public boolean barren(int x, int z) {
        String entry = biomeEntry();
        if (entry == null) { return villageType(x, z) == null; }
        Holder<Biome> held = biome(x, z);
        String[] parts = ContentStructureControl.split(entry, "structureBiomes");
        if (parts == null) { return villageType(x, z) == null; }
        return listed(held, parts[1]) == ContentStructureControl.blacklisted(parts[0]);
    }

    @Nullable private static String biomeEntry() {
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomes", Config.worldgen.structureBiomes())) {
            int at = entry.indexOf('=');
            if (at <= 0) { continue; }
            String name = entry.substring(0, at).trim().toLowerCase(Locale.ROOT);
            if (name.equals("villages") || name.equals(ContentCity.STRUCTURE)) { return entry; }
        }
        return null;
    }

    private static boolean listed(Holder<Biome> held, String names) {
        ResourceLocation id = held.unwrapKey().map(ResourceKey::location).orElse(null);
        for (String named : names.split(",")) {
            String biome = named.trim().toLowerCase(Locale.ROOT);
            if (biome.isEmpty()) { continue; }
            Set<String> found = new LinkedHashSet<>();
            if (biome.contains(":")) { found.add(biome); }
            else {
                String tag = ContentFormats.biomeTag(biome);
                if (tag != null) { found.add("#" + tag); }
                found.add("minecraft:" + biome);
                found.addAll(BiomeNames.ids(List.of(biome)));
            }
            for (String value : found) {
                if (value.startsWith("#")) {
                    ResourceLocation tag = ResourceLocation.tryParse(value.substring(1));
                    if (tag != null && held.is(TagKey.create(Registries.BIOME, tag))) { return true; }
                }
                else if (id != null && id.toString().equals(value)) { return true; }
            }
        }
        return false;
    }

    public boolean mansionNear(int chunkX, int chunkZ) {
        StructureSet set = registries.registryOrThrow(Registries.STRUCTURE_SET).get(MANSIONS);
        if (set == null || set.structures().isEmpty()) { return false; }
        StructurePlacement placement = set.placement();
        if (!(placement instanceof RandomSpreadStructurePlacement spread)) { return false; }
        for (int x = chunkX - 6; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 6; z <= chunkZ + 2; z++) {
                ChunkPos potential = spread.getPotentialStructureChunk(seed, x, z);
                if (potential.x != x || potential.z != z) { continue; }
                Holder<Biome> held = biome(potential.getMiddleBlockX(), potential.getMiddleBlockZ());
                for (StructureSet.StructureSelectionEntry entry : set.structures()) {
                    if (entry.structure().value().biomes().contains(held)) { return true; }
                }
            }
        }
        return false;
    }

    private static final class CellNoise {
        private final NoiseChunk noise;
        private final NoiseSettings shape;
        private final BlockState fallback;

        private CellNoise(NoiseBasedChunkGenerator generator, RandomState random, NoiseSettings shape, int originX, int originZ) {
            NoiseGeneratorSettings settings = generator.generatorSettings().value();
            this.shape = shape;
            this.fallback = settings.defaultBlock();
            this.noise = new NoiseChunk(1, random, originX, originZ, shape, MARKER, settings, ((INoiseBasedChunkGenerator) generator).rdpl$getGlobalFluidPicker().get(), Blender.empty());
            noise.initializeForFirstCellX();
            noise.advanceCellX(0);
        }

        private synchronized int[] tops(int x, int z, int bottom) {
            int tall = shape.getCellHeight();
            int wide = shape.getCellWidth();
            int lowest = Math.floorDiv(shape.minY(), tall);
            double alongX = (double) Math.floorMod(x, wide) / wide;
            double alongZ = (double) Math.floorMod(z, wide) / wide;
            Predicate<BlockState> surface = Heightmap.Types.WORLD_SURFACE_WG.isOpaque();
            Predicate<BlockState> floor = Heightmap.Types.OCEAN_FLOOR_WG.isOpaque();
            int[] tops = {bottom, bottom};
            boolean surfaced = false;
            for (int cellY = Math.floorDiv(shape.height(), tall) - 1; cellY >= 0; cellY--) {
                noise.selectCellYZ(cellY, 0);
                for (int inY = tall - 1; inY >= 0; inY--) {
                    int y = (lowest + cellY) * tall + inY;
                    noise.updateForY(y, (double) inY / tall);
                    noise.updateForX(x, alongX);
                    noise.updateForZ(z, alongZ);
                    BlockState state = ((INoiseChunk) noise).rdpl$getInterpolatedState();
                    BlockState found = state == null ? fallback : state;
                    if (!surfaced && surface.test(found)) {
                        tops[0] = y + 1;
                        surfaced = true;
                    }
                    if (surfaced && floor.test(found)) {
                        tops[1] = y + 1;
                        return tops;
                    }
                }
            }
            return tops;
        }
    }
}
