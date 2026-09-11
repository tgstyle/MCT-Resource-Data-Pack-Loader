package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Optional;

public final class ContentCaveStructureFeature extends Feature<ContentCaveStructureFeature.Setup> {
    public static final ContentCaveStructureFeature INSTANCE = new ContentCaveStructureFeature();
    private static final int FLAGS = 2;
    private static final Rotation[] TURNS = Rotation.values();

    private ContentCaveStructureFeature() { super(Setup.CODEC); }

    @Override public boolean place(FeaturePlaceContext<Setup> context) {
        CaveRegionDef region = ContentCaveRegions.def(context.config().region());
        if (region == null || !region.hasStructures()) { return false; }
        WorldGenLevel level = context.level();
        ChunkPos center = new ChunkPos(context.origin());
        int cellsXZ = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCells", Config.worldgen.caveRegionCells()));
        int cellsY = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCellsY", Config.worldgen.caveRegionCellsY()));
        int spanXZ = cellsXZ >> 2;
        int spanY = cellsY >> 2;
        int blockX0 = center.getMinBlockX();
        int blockZ0 = center.getMinBlockZ();
        int floor = level.getMinBuildHeight();
        int ceiling = level.getMaxBuildHeight();
        int cellY0 = Math.floorDiv(Math.floorDiv(floor, 4), spanY);
        int cellY1 = Math.floorDiv(Math.floorDiv(ceiling - 1, 4), spanY);
        int qx0 = blockX0 >> 2;
        int qz0 = blockZ0 >> 2;
        ResourceKey<Biome> biome = ResourceKey.create(Registries.BIOME, region.key());
        ContentPlacer placer = new ContentPlacer(level, ContentCaveRegions.palette(region.key()), center);
        boolean placed = false;
        for (int cellX = Math.floorDiv(qx0 + 3 - (spanXZ - 1), spanXZ); cellX <= Math.floorDiv(qx0 + 3, spanXZ); cellX++) {
            for (int cellZ = Math.floorDiv(qz0 + 3 - (spanXZ - 1), spanXZ); cellZ <= Math.floorDiv(qz0 + 3, spanXZ); cellZ++) {
                for (int cellY = cellY0; cellY <= cellY1; cellY++) {
                    long cellHash = Hashes.mix(level.getSeed(), cellX, cellY, cellZ);
                    int wx = (int) ((cellX * (long) spanXZ + Math.floorMod(cellHash, spanXZ)) << 2);
                    int wz = (int) ((cellZ * (long) spanXZ + Math.floorMod(cellHash >>> 40, spanXZ)) << 2);
                    if (wx < blockX0 || wx > blockX0 + 15 || wz < blockZ0 || wz > blockZ0 + 15) { continue; }
                    int centerY = (int) ((cellY * (long) spanY + Math.floorMod(cellHash >>> 20, spanY)) << 2);
                    int lowest = Math.max(region.minHeight() == CaveRegionDef.WORLD_FLOOR ? floor + 1 : region.minHeight(), floor + 1);
                    int highest = Math.min(region.maxHeight(), ceiling - 1);
                    if (lowest > highest) { continue; }
                    int start = Mth.clamp(centerY, lowest, highest);
                    if (!level.getBiome(new BlockPos(wx, start, wz)).is(biome)) { continue; }
                    if (region.structureChance() < 1.0F && ((cellHash >>> 24) & 0xFFFFF) / (float) (1 << 20) >= region.structureChance()) { continue; }
                    int seat = caveFloor(level, wx, wz, start, lowest, highest);
                    if (seat == Integer.MIN_VALUE) { continue; }
                    if (place(placer, region, cellHash, wx, seat, wz)) { placed = true; }
                }
            }
        }
        return placed;
    }

    private static int caveFloor(WorldGenLevel level, int x, int z, int start, int lowest, int highest) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int y = start; y >= lowest; y--) {
            if (seated(level, at.set(x, y, z))) { return y; }
        }
        for (int y = start + 1; y <= highest; y++) {
            if (seated(level, at.set(x, y, z))) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean seated(WorldGenLevel level, BlockPos at) {
        if (!ContentPlacer.loaded(level, at) || !level.getBlockState(at).isAir()) { return false; }
        BlockState below = level.getBlockState(at.below());
        return !below.isAir() && below.getFluidState().isEmpty();
    }

    private static boolean place(ContentPlacer placer, CaveRegionDef region, long cellHash, int x, int y, int z) {
        RandomSource random = RandomSource.create(cellHash);
        String named = PickDef.pick(region.structures(), random);
        ResourceLocation template = named == null || named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template == null) { return false; }
        WorldGenLevel level = placer.level();
        Optional<StructureTemplate> held = level.getLevel().getStructureManager().get(template);
        if (held.isEmpty()) {
            ContentLog.LOGGER.error("Cave region {} places structure '{}', which could not be loaded, so nothing generates", region.key(), named);
            return false;
        }
        StructureTemplate loaded = held.get();
        Rotation rotation = TURNS[random.nextInt(TURNS.length)];
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setRandom(random);
        Vec3i span = loaded.getSize(rotation);
        int cornerX = x - span.getX() / 2;
        int cornerZ = z - span.getZ() / 2;
        if (!placer.writable(cornerX, cornerZ) || !placer.writable(cornerX + span.getX() - 1, cornerZ + span.getZ() - 1)) { return false; }
        BlockPos fitted = new BlockPos(cornerX + ContentImprint.backX(rotation, span), y, cornerZ + ContentImprint.backZ(rotation, span));
        if (!loaded.placeInWorld(level, fitted, fitted, settings, random, FLAGS)) { return false; }
        if (!region.structureLoot().isEmpty()) {
            ResourceLocation table = ResourceLocation.tryParse(region.structureLoot());
            if (table == null) { ContentLog.LOGGER.error("Cave region {} names structureLoot '{}', which is not a valid id, so the containers are left as the template holds them", region.key(), region.structureLoot()); }
            else { ContentImprint.stock(placer, random, table, cornerX, y, cornerZ, span); }
        }
        ContentLog.LOGGER.debug("Cave region {} placed {} at {} {} {} turned {}", region.key(), named, x, y, z, rotation);
        return true;
    }


    public record Setup(ResourceLocation region) implements FeatureConfiguration {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("region").forGetter(Setup::region)).apply(instance, Setup::new));
    }
}
