package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CraterDef;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentCraters {
    private static final long SALT = 0x435241544552L;
    private static final int MARGIN = 12;
    private static final int STARTS_REACH = 8;
    private static final Map<ServerLevel, ContentCraterVerdicts> VERDICTS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final ThreadLocal<Sunk> SUNK = new ThreadLocal<>();

    private ContentCraters() {}

    private record Sunk(int chunkX, int chunkZ, int[] depth) {}

    private record Crater(int x, int z, int radius, int depth) {
        boolean near(int minX, int minZ, int maxX, int maxZ, int keep) {
            long dx = Math.max(0, Math.max(minX - x, x - maxX));
            long dz = Math.max(0, Math.max(minZ - z, z - maxZ));
            return dx * dx + dz * dz < (long) keep * keep;
        }

        boolean near(BoundingBox box, int keep) { return near(box.minX(), box.minZ(), box.maxX(), box.maxZ(), keep); }
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { VERDICTS.put(level, ContentCraterVerdicts.of(level)); }
    }

    public static void carve(WorldGenLevel level, ChunkAccess chunk) {
        SUNK.remove();
        int around = ContentBiomes.craterReach();
        if (around <= 0 || ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        ChunkGenerator generator = level.getLevel().getChunkSource().getGenerator();
        ChunkPos pos = chunk.getPos();
        boolean roofed = level.getLevel().dimensionType().hasCeiling();
        int[] sunk = new int[256];
        for (int craterX = pos.x - around; craterX <= pos.x + around; craterX++) {
            for (int craterZ = pos.z - around; craterZ <= pos.z + around; craterZ++) {
                Crater crater = at(level, generator, craterX, craterZ);
                if (crater == null || !crater.near(pos.getMinBlockX(), pos.getMinBlockZ(), pos.getMaxBlockX(), pos.getMaxBlockZ(), crater.radius())) { continue; }
                if (claimed(level, generator, crater, craterX, craterZ)) { continue; }
                bowl(crater, chunk, roofed, sunk);
            }
        }
        SUNK.set(new Sunk(pos.x, pos.z, sunk));
    }

    public static void carveFlat(WorldGenLevel level, ChunkAccess chunk) {
        carve(level, chunk);
        surfaced();
    }

    public static void surfaced() { SUNK.remove(); }

    public static int sunk(int blockX, int blockZ) {
        Sunk sunk = SUNK.get();
        if (sunk == null || sunk.chunkX() != SectionPos.blockToSectionCoord(blockX) || sunk.chunkZ() != SectionPos.blockToSectionCoord(blockZ)) { return 0; }
        return sunk.depth()[((blockZ & 15) << 4) | (blockX & 15)];
    }

    @Nullable private static Crater at(WorldGenLevel level, ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random(Hashes.mix(level.getSeed() ^ SALT, chunkX, 0, chunkZ));
        int x = SectionPos.sectionToBlockCoord(chunkX) + random.nextInt(16);
        int z = SectionPos.sectionToBlockCoord(chunkZ) + random.nextInt(16);
        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(generator.getSeaLevel()), QuartPos.fromBlock(z), level.getLevel().getChunkSource().randomState().sampler());
        BiomeDef def = biome.unwrapKey().map(key -> ContentBiomes.def(key.location())).orElse(null);
        CraterDef craters = def == null ? null : def.craters();
        if (craters == null || random.nextFloat() >= craters.chance()) { return null; }
        return new Crater(x, z, craters.minRadius() + random.nextInt(craters.maxRadius() - craters.minRadius() + 1), craters.depth());
    }

    private static boolean claimed(WorldGenLevel level, ChunkGenerator generator, Crater crater, int chunkX, int chunkZ) {
        ContentCraterVerdicts verdicts = VERDICTS.get(level.getLevel());
        long key = ChunkPos.asLong(chunkX, chunkZ);
        Boolean known = verdicts == null ? null : verdicts.claimed(key);
        if (known != null) { return known; }
        int keep = crater.radius() + MARGIN;
        BoundingBox area = new BoundingBox(crater.x() - keep, level.getMinBuildHeight(), crater.z() - keep, crater.x() + keep, level.getMaxBuildHeight(), crater.z() + keep);
        boolean claimed = ContentCityClaim.claims(CityGround.of(level), generator, level.getLevel().getChunkSource().randomState(), level.registryAccess(), area, box -> crater.near(box, keep)) || pieceNear(level.getLevel(), generator, crater, keep);
        return verdicts == null ? claimed : verdicts.settle(key, claimed);
    }

    private static boolean pieceNear(ServerLevel level, ChunkGenerator generator, Crater crater, int keep) {
        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        int fromX = SectionPos.blockToSectionCoord(crater.x() - keep) - STARTS_REACH;
        int toX = SectionPos.blockToSectionCoord(crater.x() + keep) + STARTS_REACH;
        int fromZ = SectionPos.blockToSectionCoord(crater.z() - keep) - STARTS_REACH;
        int toZ = SectionPos.blockToSectionCoord(crater.z() + keep) + STARTS_REACH;
        for (Holder<StructureSet> held : state.possibleStructureSets()) {
            StructureSet set = held.value();
            if (set.structures().stream().noneMatch(entry -> guarded(registry, entry.structure().value()))) { continue; }
            for (int chunkX = fromX; chunkX <= toX; chunkX++) {
                for (int chunkZ = fromZ; chunkZ <= toZ; chunkZ++) {
                    if (!set.placement().isStructureChunk(state, chunkX, chunkZ)) { continue; }
                    StructureStart start = founded(level, generator, state, set, new ChunkPos(chunkX, chunkZ));
                    if (start != null && guarded(registry, start.getStructure()) && reaches(start, crater, keep)) { return true; }
                }
            }
        }
        return false;
    }

    @Nullable private static StructureStart founded(ServerLevel level, ChunkGenerator generator, ChunkGeneratorStructureState state, StructureSet set, ChunkPos pos) {
        List<StructureSet.StructureSelectionEntry> left = new ArrayList<>(set.structures());
        if (left.size() == 1) { return tried(level, generator, state, left.getFirst(), pos); }
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        random.setLargeFeatureSeed(state.getLevelSeed(), pos.x, pos.z);
        int total = left.stream().mapToInt(StructureSet.StructureSelectionEntry::weight).sum();
        while (!left.isEmpty()) {
            int pick = random.nextInt(total);
            int index = 0;
            for (StructureSet.StructureSelectionEntry entry : left) {
                pick -= entry.weight();
                if (pick < 0) { break; }
                index++;
            }
            StructureSet.StructureSelectionEntry entry = left.get(index);
            StructureStart start = tried(level, generator, state, entry, pos);
            if (start != null) { return start; }
            left.remove(index);
            total -= entry.weight();
        }
        return null;
    }

    @Nullable private static StructureStart tried(ServerLevel level, ChunkGenerator generator, ChunkGeneratorStructureState state, StructureSet.StructureSelectionEntry entry, ChunkPos pos) {
        Structure structure = entry.structure().value();
        if (ContentPopulateControl.refusesStructure(generator, structure) || ContentVoidWorld.voidRefuses(level, structure) || ContentStructureMaps.refuses(level, structure)) { return null; }
        RandomState random = state.randomState();
        long seed = state.getLevelSeed();
        StructureStart start = structure.generate(level.registryAccess(), generator, generator.getBiomeSource(), random, level.getStructureManager(), seed, pos, 0, level, structure.biomes()::contains);
        if (!start.isValid() || ContentCityClaim.overrides(start, seed, generator, random, level.registryAccess()) || ContentRoughGround.refuses(start, seed, generator, random, level.registryAccess())) { return null; }
        return start;
    }

    private static boolean reaches(StructureStart start, Crater crater, int keep) {
        if (!crater.near(start.getBoundingBox(), keep)) { return false; }
        for (StructurePiece piece : start.getPieces()) {
            if (crater.near(piece.getBoundingBox(), keep)) { return true; }
        }
        return false;
    }

    private static boolean guarded(Registry<Structure> registry, Structure structure) { return structure instanceof ContentCityStructure || structure instanceof ContentMapStructure || registry.wrapAsHolder(structure).is(StructureTags.VILLAGE); }

    private static void bowl(Crater crater, ChunkAccess chunk, boolean roofed, int[] sunk) {
        long widest = (long) crater.radius() * crater.radius();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                long dx = chunk.getPos().getBlockX(x) - crater.x();
                long dz = chunk.getPos().getBlockZ(z) - crater.z();
                long apart = dx * dx + dz * dz;
                if (apart >= widest) { continue; }
                double share = (double) apart / (double) widest;
                int deep = (int) Math.round(crater.depth() * (1.0D - share * share));
                if (deep > 0) { sunk[(z << 4) | x] += dig(chunk, at.set(chunk.getPos().getBlockX(x), 0, chunk.getPos().getBlockZ(z)), deep, roofed); }
            }
        }
    }

    private static int dig(ChunkAccess chunk, BlockPos.MutableBlockPos at, int deep, boolean roofed) {
        int bottom = chunk.getMinBuildHeight();
        int y = surface(chunk, at, roofed);
        if (y <= bottom) { return 0; }
        BlockState above = y + 1 < chunk.getMaxBuildHeight() ? chunk.getBlockState(at.setY(y + 1)) : Blocks.AIR.defaultBlockState();
        BlockState fill = above.getFluidState().isEmpty() ? Blocks.AIR.defaultBlockState() : above;
        int dug = 0;
        while (dug < deep && y > bottom + 1 && solid(chunk, at, y - 1)) {
            chunk.setBlockState(at.setY(y), fill, false);
            y--;
            dug++;
        }
        return dug;
    }

    private static int surface(ChunkAccess chunk, BlockPos.MutableBlockPos at, boolean roofed) {
        int bottom = chunk.getMinBuildHeight();
        int y = chunk.getMaxBuildHeight() - 1;
        while (y > bottom && !solid(chunk, at, y)) { y--; }
        if (!roofed) { return y; }
        while (y > bottom && solid(chunk, at, y)) { y--; }
        while (y > bottom && !solid(chunk, at, y)) { y--; }
        return y;
    }

    private static boolean solid(ChunkAccess chunk, BlockPos.MutableBlockPos at, int y) { return chunk.getBlockState(at.setY(y)).canOcclude(); }
}
