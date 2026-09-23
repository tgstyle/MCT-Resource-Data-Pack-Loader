package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class ContentCityClaim {
    private static final ResourceLocation CITY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE);
    private static final int LAKE_SPAN = 15;
    private static final int LAKE_OVER = 8;
    private static final int LAKE_UNDER = 4;
    private static final int ROAD_TALL = 2;
    private static final int MARGIN = 12;
    private static final String STREETS = "/streets/";
    private static final int DUNGEON_REACH = 4;
    private static final int DUNGEON_UNDER = 1;
    private static final int DUNGEON_OVER = 4;
    private static final int BERG_REACH = 11;
    private static final int PORTAL_SPREAD = 14;
    private static final ThreadLocal<Vein> VEIN = ThreadLocal.withInitial(Vein::new);

    private ContentCityClaim() {}

    public static boolean outside(CityGround ground, ChunkGenerator generator, RandomState random, RegistryAccess registries, int x, int z) {
        if (ContentCity.idle()) { return true; }
        Structure city = registries.registryOrThrow(Registries.STRUCTURE).get(CITY);
        if (city == null) { return true; }
        CityPlan plan = CityPlan.of(ground, CityPlan.districtOf(x, true), CityPlan.districtOf(z, false));
        if (plan == null) { return true; }
        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(plan.windowX()), QuartPos.fromBlock(generator.getSeaLevel()), QuartPos.fromBlock(plan.windowZ()), random.sampler());
        return !city.biomes().contains(biome);
    }

    public static boolean overrides(StructureStart start, long seed, ChunkGenerator generator, RandomState random, RegistryAccess registries) {
        if (ContentCity.idle() || !start.isValid()) { return false; }
        Structure structure = start.getStructure();
        ResourceLocation id = registries.registryOrThrow(Registries.STRUCTURE).getKey(structure);
        if (structure.type() == StructureType.OCEAN_RUIN || structure.type() == StructureType.OCEAN_MONUMENT) { return overBore(start, CityGround.of(seed, generator, random, registries), id); }
        if (structure.type() == StructureType.RUINED_PORTAL) { return overStreets(start, CityGround.of(seed, generator, random, registries), id); }
        if (structure instanceof ContentCityStructure || structure.step() != GenerationStep.Decoration.SURFACE_STRUCTURES) { return false; }
        if (id == null || !ContentStructureControl.structures("villages").contains(id)) { return false; }
        if (ContentStructureSpread.pinned(start.getChunkPos())) { return false; }
        CityGround ground = CityGround.of(seed, generator, random, registries);
        BoundingBox box = start.getBoundingBox();
        for (int districtX = CityPlan.districtOf(box.minX(), true); districtX <= CityPlan.districtOf(box.maxX(), true); districtX++) {
            for (int districtZ = CityPlan.districtOf(box.minZ(), false); districtZ <= CityPlan.districtOf(box.maxZ(), false); districtZ++) {
                if (outside(ground, generator, random, registries, CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false))) { continue; }
                ContentLog.LOGGER.debug("Village {} at chunk {} would reach into the city district at {}, {}, where the city is the village, so it is not founded", id, start.getChunkPos(), CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false));
                return true;
            }
        }
        return false;
    }

    public static boolean floods(WorldGenLevel level, BlockPos origin) {
        if (floodsVillageRoad(level, origin)) { return true; }
        if (ContentCity.idle()) { return false; }
        CityGround ground = CityGround.of(level);
        int leastX = origin.getX();
        int mostX = origin.getX() + LAKE_SPAN;
        int leastZ = origin.getZ();
        int mostZ = origin.getZ() + LAKE_SPAN;
        for (int districtX = CityPlan.districtOf(leastX, true); districtX <= CityPlan.districtOf(mostX, true); districtX++) {
            for (int districtZ = CityPlan.districtOf(leastZ, false); districtZ <= CityPlan.districtOf(mostZ, false); districtZ++) {
                CityPlan plan = CityPlan.of(ground, districtX, districtZ);
                if (plan == null) { continue; }
                for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
                    for (CityPlan.Line line : lines) {
                        int fromX = line.alongX() ? line.from() : line.at();
                        int toX = line.alongX() ? line.to() : line.last();
                        int fromZ = line.alongX() ? line.at() : line.from();
                        int toZ = line.alongX() ? line.last() : line.to();
                        if (floodsBox(level, origin, leastX, leastZ, mostX, mostZ, fromX, fromZ, toX, toZ, 0, ROAD_TALL)) { return true; }
                    }
                }
                for (CityPlan.Rail rail : plan.rails()) {
                    int from = rail.from();
                    int to = rail.to();
                    int fromX = rail.alongX() ? from : rail.at();
                    int toX = rail.alongX() ? to : rail.last();
                    int fromZ = rail.alongX() ? rail.at() : from;
                    int toZ = rail.alongX() ? rail.last() : to;
                    int under = rail.subway() ? ContentCity.subwayDepth() : 0;
                    if (floodsBox(level, origin, leastX, leastZ, mostX, mostZ, fromX, fromZ, toX, toZ, under, ContentCityRailPiece.CLEAR + 1)) { return true; }
                }
                for (CityPlan.Plot plot : plan.plots()) {
                    if (plot.toX() < leastX || plot.fromX() > mostX || plot.toZ() < leastZ || plot.fromZ() > mostZ) { continue; }
                    ContentLog.LOGGER.debug("A lake at {}, {}, {} would reach into the city plot {} at {}, {}, so it is not made", origin.getX(), origin.getY(), origin.getZ(), plot.def().key(), plot.fromX(), plot.fromZ());
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean floodsVillageRoad(WorldGenLevel level, BlockPos origin) {
        if (!ContentCity.wanted()) { return false; }
        List<ResourceLocation> villages = ContentStructureControl.structures("villages");
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        int leastX = origin.getX();
        int mostX = origin.getX() + LAKE_SPAN;
        int leastZ = origin.getZ();
        int mostZ = origin.getZ() + LAKE_SPAN;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int chunkX = SectionPos.blockToSectionCoord(leastX); chunkX <= SectionPos.blockToSectionCoord(mostX); chunkX++) {
            for (int chunkZ = SectionPos.blockToSectionCoord(leastZ); chunkZ <= SectionPos.blockToSectionCoord(mostZ); chunkZ++) {
                at.set(SectionPos.sectionToBlockCoord(chunkX), origin.getY(), SectionPos.sectionToBlockCoord(chunkZ));
                for (StructurePiece piece : pieces(level, at, structure -> villages.contains(registry.getKey(structure)))) {
                    if (!(piece instanceof PoolElementStructurePiece pool) || !pool.getElement().toString().contains(STREETS)) { continue; }
                    BoundingBox box = piece.getBoundingBox();
                    if (origin.getY() + LAKE_OVER < box.minY() - MARGIN || origin.getY() - LAKE_UNDER > box.maxY() + MARGIN || !box.intersects(leastX, leastZ, mostX, mostZ)) { continue; }
                    ContentLog.LOGGER.debug("A lake at {}, {}, {} would flood the village road at {}, {}, so it is not made", origin.getX(), origin.getY(), origin.getZ(), box.minX(), box.minZ());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean bergInCity(WorldGenLevel level, ChunkGenerator generator, BlockPos origin) {
        if (ContentCity.idle()) { return false; }
        CityGround ground = CityGround.of(level);
        ServerLevel server = level.getLevel();
        RandomState random = server.getChunkSource().randomState();
        RegistryAccess registries = server.registryAccess();
        for (int districtX = CityPlan.districtOf(origin.getX() - BERG_REACH, true); districtX <= CityPlan.districtOf(origin.getX() + BERG_REACH, true); districtX++) {
            for (int districtZ = CityPlan.districtOf(origin.getZ() - BERG_REACH, false); districtZ <= CityPlan.districtOf(origin.getZ() + BERG_REACH, false); districtZ++) {
                if (outside(ground, generator, random, registries, CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false))) { continue; }
                ContentLog.LOGGER.debug("An iceberg at {}, {} would stand in the city district at {}, {}, so it is not made", origin.getX(), origin.getZ(), CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false));
                return true;
            }
        }
        return false;
    }

    public static boolean intoBore(WorldGenLevel level, BlockPos origin) {
        if (ContentCity.idle() || !ContentCity.subways()) { return false; }
        List<CityRails.Laid> subways = CityRails.subways(CityGround.of(level), origin.getX() - DUNGEON_REACH, origin.getZ() - DUNGEON_REACH, origin.getX() + DUNGEON_REACH, origin.getZ() + DUNGEON_REACH);
        if (subways.isEmpty()) { return false; }
        for (int x = origin.getX() - DUNGEON_REACH; x <= origin.getX() + DUNGEON_REACH; x++) {
            for (int z = origin.getZ() - DUNGEON_REACH; z <= origin.getZ() + DUNGEON_REACH; z++) {
                for (int y = origin.getY() - DUNGEON_UNDER; y <= origin.getY() + DUNGEON_OVER; y++) {
                    if (!CityRails.insideBore(subways, x, y, z)) { continue; }
                    ContentLog.LOGGER.debug("A dungeon at {}, {}, {} would have opened into a subway bore, so it is left out", origin.getX(), origin.getY(), origin.getZ());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean springIntoCut(WorldGenLevel level, BlockPos origin) {
        if (ContentCity.idle()) { return false; }
        List<CityRails.Laid> lines = CityRails.railways(CityGround.of(level), origin.getX() - 1, origin.getZ() - 1, origin.getX() + 1, origin.getZ() + 1);
        List<ContentCityPiece> tunnels = tunnels(level, origin);
        if (lines.isEmpty() && tunnels.isEmpty()) { return false; }
        boolean met = CityRails.runsInto(lines, origin.getX(), origin.getY(), origin.getZ()) || intoTunnel(tunnels, origin);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos beside = origin.relative(side);
            met |= CityRails.runsInto(lines, beside.getX(), beside.getY(), beside.getZ()) || intoTunnel(tunnels, beside);
        }
        if (!met) { return false; }
        ContentLog.LOGGER.debug("A spring at {}, {}, {} would have run into a cut, bore or tunnel, so it is left out", origin.getX(), origin.getY(), origin.getZ());
        return true;
    }

    private static List<ContentCityPiece> tunnels(WorldGenLevel level, BlockPos origin) {
        List<ContentCityPiece> found = new ArrayList<>();
        if (ContentCity.tunnelDepth() <= 0) { return found; }
        for (StructurePiece piece : pieces(level, origin, structure -> structure instanceof ContentCityStructure)) {
            if (piece instanceof ContentCityPiece street && street.tunnelNear(origin)) { found.add(street); }
        }
        return found;
    }

    private static boolean intoTunnel(List<ContentCityPiece> tunnels, BlockPos at) {
        for (ContentCityPiece tunnel : tunnels) {
            if (tunnel.runsIntoTunnel(at.getX(), at.getY(), at.getZ())) { return true; }
        }
        return false;
    }

    public static List<StructurePiece> pieces(WorldGenLevel level, BlockPos at, Predicate<Structure> kind) {
        List<StructurePiece> found = new ArrayList<>();
        if (!ContentPlacer.loaded(level, at)) { return found; }
        for (Map.Entry<Structure, LongSet> reference : level.getChunk(SectionPos.blockToSectionCoord(at.getX()), SectionPos.blockToSectionCoord(at.getZ()), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences().entrySet()) {
            Structure structure = reference.getKey();
            if (!kind.test(structure)) { continue; }
            for (long packed : reference.getValue()) {
                ChunkPos start = new ChunkPos(packed);
                if (!level.hasChunk(start.x, start.z)) { continue; }
                StructureStart held = level.getChunk(start.x, start.z, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(structure);
                if (held == null || !held.isValid()) { continue; }
                found.addAll(held.getPieces());
            }
        }
        return found;
    }

    public static void veinStarts(WorldGenLevel level, OreConfiguration config, int leastX, int leastZ, int mostX, int mostZ) {
        Vein vein = VEIN.get();
        vein.lines = List.of();
        vein.ore = null;
        vein.spared = 0;
        if (ContentCity.idle()) { return; }
        for (OreConfiguration.TargetBlockState target : config.targetStates) {
            if (target.state.getBlock() instanceof FallingBlock) {
                vein.ore = target.state;
                break;
            }
        }
        if (vein.ore != null) { vein.lines = CityRails.railways(CityGround.of(level), leastX, leastZ, mostX, mostZ); }
    }

    public static boolean oreMayFill(BlockState ore, BlockPos at) {
        Vein vein = VEIN.get();
        if (vein.lines.isEmpty() || !(ore.getBlock() instanceof FallingBlock) || !CityRails.underBed(vein.lines, at.getX(), at.getY(), at.getZ())) { return true; }
        vein.spared++;
        return false;
    }

    public static void veinEnds(int x, int y, int z) {
        Vein vein = VEIN.get();
        if (vein.spared > 0 && vein.ore != null) { ContentLog.LOGGER.debug("A {} vein at {}, {}, {} was kept out of the ground under a railway bed {} time(s), where it would have fallen away", BuiltInRegistries.BLOCK.getKey(vein.ore.getBlock()), x, y, z, vein.spared); }
        vein.lines = List.of();
    }

    public static boolean fossilIntoBore(WorldGenLevel level, BoundingBox box) {
        if (ContentCity.idle() || !ContentCity.subways()) { return false; }
        List<CityRails.Laid> subways = CityRails.subways(CityGround.of(level), box.minX(), box.minZ(), box.maxX(), box.maxZ());
        if (subways.isEmpty()) { return false; }
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    if (!CityRails.insideBore(subways, x, y, z)) { continue; }
                    ContentLog.LOGGER.debug("A fossil at {}, {}, {} would have written into a subway bore, so it is left out", box.minX(), box.minY(), box.minZ());
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean overBore(StructureStart start, CityGround ground, @Nullable ResourceLocation id) {
        return ContentCity.subways() && refused(start, id, 0, "reach over a city subway", box -> !CityRails.subways(ground, box.minX(), box.minZ(), box.maxX(), box.maxZ()).isEmpty());
    }

    private static boolean overStreets(StructureStart start, CityGround ground, @Nullable ResourceLocation id) {
        return refused(start, id, PORTAL_SPREAD, "spread over a city railway or street", box -> railwayWithin(ground, box) || streetWithin(ground, box));
    }

    private static boolean refused(StructureStart start, @Nullable ResourceLocation id, int reach, String what, Predicate<BoundingBox> meets) {
        BoundingBox box = start.getBoundingBox().inflatedBy(reach);
        if (!meets.test(box)) { return false; }
        ContentLog.LOGGER.debug("{} at chunk {} would {} at {}, {} to {}, {}, where the city comes first, so it is not founded", id, start.getChunkPos(), what, box.minX(), box.minZ(), box.maxX(), box.maxZ());
        return true;
    }

    private static boolean railwayWithin(CityGround ground, BoundingBox box) {
        for (CityRails.Laid laid : CityRails.railways(ground, box.minX(), box.minZ(), box.maxX(), box.maxZ())) {
            if (!laid.rail().subway()) { return true; }
        }
        return false;
    }

    private static boolean streetWithin(CityGround ground, BoundingBox box) {
        for (int districtX = CityPlan.districtOf(box.minX(), true); districtX <= CityPlan.districtOf(box.maxX(), true); districtX++) {
            for (int districtZ = CityPlan.districtOf(box.minZ(), false); districtZ <= CityPlan.districtOf(box.maxZ(), false); districtZ++) {
                CityPlan plan = CityPlan.of(ground, districtX, districtZ);
                if (plan == null) { continue; }
                for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
                    for (CityPlan.Line line : lines) {
                        int fromX = line.alongX() ? line.from() : line.at();
                        int toX = line.alongX() ? line.to() : line.last();
                        int fromZ = line.alongX() ? line.at() : line.from();
                        int toZ = line.alongX() ? line.last() : line.to();
                        if (box.intersects(fromX, fromZ, toX, toZ)) { return true; }
                    }
                }
            }
        }
        return false;
    }

    private static boolean floodsBox(WorldGenLevel level, BlockPos origin, int leastX, int leastZ, int mostX, int mostZ, int fromX, int fromZ, int toX, int toZ, int under, int tall) {
        if (toX < leastX || fromX > mostX || toZ < leastZ || fromZ > mostZ) { return false; }
        int x = Mth.clamp(origin.getX(), fromX, toX);
        int z = Mth.clamp(origin.getZ(), fromZ, toZ);
        int bed = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1 - under;
        if (origin.getY() + LAKE_OVER < bed - MARGIN || origin.getY() - LAKE_UNDER > bed + tall + MARGIN) { return false; }
        ContentLog.LOGGER.debug("A lake at {}, {}, {} would flood the city's road or railway at {}, {}, so it is not made", origin.getX(), origin.getY(), origin.getZ(), x, z);
        return true;
    }

    private static final class Vein {
        private List<CityRails.Laid> lines = List.of();
        @Nullable private BlockState ore;
        private int spared;
    }
}
