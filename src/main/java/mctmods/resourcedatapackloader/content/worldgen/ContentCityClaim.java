package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class ContentCityClaim {
    private static final ResourceLocation CITY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE);
    private static final int LAKE_LOW = 8;
    private static final int LAKE_HIGH = 7;
    private static final int LAKE_UNDER = 4;
    private static final int GROUND_MARGIN = 12;

    private ContentCityClaim() {}

    public static boolean covers(long seed, ChunkGenerator generator, RandomState random, RegistryAccess registries, int x, int z) {
        if (ContentCity.idle()) { return false; }
        Structure city = registries.registryOrThrow(Registries.STRUCTURE).get(CITY);
        if (city == null) { return false; }
        CityPlan plan = CityPlan.of(seed, Math.floorDiv(x, CityPlan.district()), Math.floorDiv(z, CityPlan.district()));
        if (plan == null) { return false; }
        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(plan.windowX()), QuartPos.fromBlock(generator.getSeaLevel()), QuartPos.fromBlock(plan.windowZ()), random.sampler());
        return city.biomes().contains(biome);
    }

    public static boolean overrides(StructureStart start, long seed, ChunkGenerator generator, RandomState random, RegistryAccess registries) {
        if (ContentCity.idle() || !start.isValid()) { return false; }
        Structure structure = start.getStructure();
        if (structure instanceof ContentCityStructure || structure.step() != GenerationStep.Decoration.SURFACE_STRUCTURES) { return false; }
        if (ContentStructureSpread.pinned(start.getChunkPos())) { return false; }
        BoundingBox box = start.getBoundingBox();
        int size = CityPlan.district();
        for (int districtX = Math.floorDiv(box.minX(), size); districtX <= Math.floorDiv(box.maxX(), size); districtX++) {
            for (int districtZ = Math.floorDiv(box.minZ(), size); districtZ <= Math.floorDiv(box.maxZ(), size); districtZ++) {
                if (!covers(seed, generator, random, registries, districtX * size, districtZ * size)) { continue; }
                ContentLog.LOGGER.debug("Structure {} at chunk {} would reach into the city district at {}, {}, so the city keeps the ground and it is not founded", registries.registryOrThrow(Registries.STRUCTURE).getKey(structure), start.getChunkPos(), districtX * size, districtZ * size);
                return true;
            }
        }
        return false;
    }

    public static boolean floods(WorldGenLevel level, ChunkGenerator generator, BlockPos origin) {
        if (ContentCity.idle()) { return false; }
        int ground = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        if (origin.getY() + LAKE_HIGH < ground - deepest() - GROUND_MARGIN || origin.getY() - LAKE_UNDER > ground + GROUND_MARGIN) { return false; }
        RandomState random = level.getLevel().getChunkSource().randomState();
        for (int x : new int[] {origin.getX() - LAKE_LOW, origin.getX() + LAKE_HIGH}) {
            for (int z : new int[] {origin.getZ() - LAKE_LOW, origin.getZ() + LAKE_HIGH}) {
                if (!covers(level.getSeed(), generator, random, level.registryAccess(), x, z)) { continue; }
                ContentLog.LOGGER.debug("A lake at {}, {}, {} would flood city ground, so it is not made", origin.getX(), origin.getY(), origin.getZ());
                return true;
            }
        }
        return false;
    }

    private static int deepest() {
        int sewer = ContentCity.sewerDepth() + ContentCity.sewerHeight();
        return ContentCity.subwayLines() > 0 ? Math.max(sewer, ContentCity.subwayDepth()) : sewer;
    }
}
