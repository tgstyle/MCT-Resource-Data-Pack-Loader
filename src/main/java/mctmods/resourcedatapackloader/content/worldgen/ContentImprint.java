package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.StructureLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Optional;
import javax.annotation.Nullable;

public final class ContentImprint implements IContentShape {
    private final ShapeDef shape;
    private final ResourceLocation key;
    private final boolean filtered;

    public ContentImprint(ShapeDef shape, ResourceLocation key, boolean filtered) {
        this.shape = shape;
        this.key = key;
        this.filtered = filtered;
    }

    public static int backX(Rotation rotation, Vec3i span) { return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0; }

    public static int backZ(Rotation rotation, Vec3i span) { return rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0; }

    public boolean pinned() { return shape.pinnedAt() != null; }

    @Nullable public Pin pin(WorldGenLevel level, boolean report) {
        int[] at = shape.pinnedAt();
        if (at == null) { return null; }
        RandomSource random = RandomSource.create(level.getSeed() ^ ChunkPos.asLong(at[0], at[1]) ^ key.hashCode());
        StructureTemplate loaded = load(level, random, report);
        if (loaded == null) { return null; }
        Rotation rotation = turn(random);
        Mirror mirror = mirror(random);
        Vec3i span = loaded.getSize(rotation);
        int cornerX = within(at[0], span.getX());
        int cornerZ = within(at[1], span.getZ());
        BlockPos fitted = loaded.getZeroPositionWithTransform(new BlockPos(cornerX, 0, cornerZ), mirror, rotation);
        ChunkPos home = new ChunkPos(SectionPos.blockToSectionCoord(at[0]), SectionPos.blockToSectionCoord(at[1]));
        boolean split = beyond(cornerX, cornerZ, home) || beyond(cornerX + span.getX() - 1, cornerZ + span.getZ() - 1, home);
        BoundingBox box = loaded.getBoundingBox(new StructurePlaceSettings().setRotation(rotation).setMirror(mirror), fitted);
        return new Pin(loaded, rotation, mirror, cornerX, cornerZ, span, fitted, home, split, box);
    }

    public boolean placePinned(ContentPlacer placer, ChunkPos center, BlockPos origin) {
        WorldGenLevel level = placer.level();
        Pin pin = pin(level, true);
        if (pin == null) { return false; }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(pin.rotation()).setMirror(pin.mirror());
        process(settings, placer);
        BlockPos fitted = pin.fitted().atY(origin.getY());
        RandomSource random = RandomSource.create(fitted.asLong());
        if (pin.split()) {
            settings.setBoundingBox(CityCrown.clip(level, center));
            if (!pin.loaded().placeInWorld(level, fitted, fitted, settings, random, Block.UPDATE_CLIENTS)) { return false; }
            int lowX = Math.max(pin.box().minX(), center.getMinBlockX());
            int lowZ = Math.max(pin.box().minZ(), center.getMinBlockZ());
            int highX = Math.min(pin.box().maxX(), center.getMaxBlockX());
            int highZ = Math.min(pin.box().maxZ(), center.getMaxBlockZ());
            stock(placer, random, lowX, origin.getY(), lowZ, new Vec3i(highX - lowX + 1, pin.span().getY(), highZ - lowZ + 1));
        }
        else {
            if (blocked(placer, pin.cornerX(), pin.cornerZ(), pin.span())) { return false; }
            if (!pin.loaded().placeInWorld(level, fitted, fitted, settings, random, Block.UPDATE_CLIENTS)) { return false; }
            stock(placer, random, pin.cornerX(), origin.getY(), pin.cornerZ(), pin.span());
        }
        if (!shape.locateAs().isEmpty() && center.equals(pin.home())) { ContentLocate.record(level.getLevel(), shape.locateAs(), fitted); }
        return true;
    }

    private static boolean beyond(int x, int z, ChunkPos home) { return Math.abs(SectionPos.blockToSectionCoord(x) - home.x) > 1 || Math.abs(SectionPos.blockToSectionCoord(z) - home.z) > 1; }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        WorldGenLevel level = placer.level();
        StructureTemplate loaded = load(level, random, true);
        if (loaded == null) { return false; }
        Rotation rotation = turn(random);
        Mirror mirror = mirror(random);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror).setRandom(random);
        process(settings, placer);
        Vec3i span = loaded.getSize(rotation);
        int cornerX = within(origin.getX(), span.getX());
        int cornerZ = within(origin.getZ(), span.getZ());
        if (blocked(placer, cornerX, cornerZ, span)) { return false; }
        BlockPos fitted = loaded.getZeroPositionWithTransform(new BlockPos(cornerX, origin.getY(), cornerZ), mirror, rotation);
        if (!loaded.placeInWorld(level, fitted, fitted, settings, random, Block.UPDATE_CLIENTS)) { return false; }
        stock(placer, random, cornerX, origin.getY(), cornerZ, span);
        if (!shape.locateAs().isEmpty()) { ContentLocate.record(level.getLevel(), shape.locateAs(), fitted); }
        return true;
    }

    @Nullable private StructureTemplate load(WorldGenLevel level, RandomSource random, boolean report) {
        String named = shape.structures().isEmpty() ? shape.structure() : PickDef.pick(shape.structures(), random);
        ResourceLocation template = named == null || named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template == null) { return null; }
        Optional<StructureTemplate> held = level.getLevel().getStructureManager().get(template);
        if (held.isPresent()) { return held.get(); }
        if (report) { ContentLog.LOGGER.error("Worldgen {} places structure '{}', which could not be loaded, so nothing generates", key, named); }
        return null;
    }

    private void process(StructurePlaceSettings settings, ContentPlacer placer) {
        if (shape.integrity() < ShapeDef.FULL_INTEGRITY) { settings.addProcessor(new BlockRotProcessor(shape.integrity() / (float) ShapeDef.FULL_INTEGRITY)); }
        if (filtered) { settings.addProcessor(new ContentImprintProcessor(placer)); }
    }

    private void stock(ContentPlacer placer, RandomSource random, int cornerX, int baseY, int cornerZ, Vec3i span) {
        if (shape.lootTable().isEmpty()) { return; }
        ResourceLocation table = ResourceLocation.tryParse(shape.lootTable());
        if (table == null) {
            ContentLog.LOGGER.error("Worldgen {} names loot table '{}', which is not a valid id, so the containers are left as the template holds them", key, shape.lootTable());
            return;
        }
        stock(placer, random, table, cornerX, baseY, cornerZ, span);
    }

    public static void stock(ContentPlacer placer, RandomSource random, ResourceLocation table, int cornerX, int baseY, int cornerZ, Vec3i span) {
        WorldGenLevel level = placer.level();
        BoundingBox box = new BoundingBox(cornerX, baseY, cornerZ, cornerX + span.getX() - 1, baseY + span.getY() - 1, cornerZ + span.getZ() - 1);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int stocked = 0;
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    at.set(x, y, z);
                    if (placer.unreadable(at)) { continue; }
                    BlockEntity found = level.getBlockEntity(at);
                    if (StructureLoot.stock(found, table, random.nextLong())) { stocked++; }
                }
            }
        }
        if (stocked > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Stocked {} container(s) from {}, {}, {} to {}, {}, {} with the loot table {}", stocked, box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ(), table); }
    }

    private static boolean blocked(ContentPlacer placer, int cornerX, int cornerZ, Vec3i span) { return placer.unwritable(cornerX, cornerZ) || placer.unwritable(cornerX + span.getX() - 1, cornerZ + span.getZ() - 1); }

    private Rotation turn(RandomSource random) {
        String named = PickDef.pick(shape.turns(), random);
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return Turns.ALL[random.nextInt(Turns.ALL.length)];
    }

    private Mirror mirror(RandomSource random) {
        String named = PickDef.pick(shape.mirrors(), random);
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }
        return Mirror.NONE;
    }

    private static int within(int origin, int span) {
        int start = origin - span / 2;
        if (span >= 16) { return start; }
        int corner = (origin >> 4) * 16;
        return Mth.clamp(start, corner, corner + 16 - span);
    }

    public record Pin(StructureTemplate loaded, Rotation rotation, Mirror mirror, int cornerX, int cornerZ, Vec3i span, BlockPos fitted, ChunkPos home, boolean split, BoundingBox box) {
        public boolean covers(ChunkPos chunk) { return box.intersects(chunk.getMinBlockX(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), chunk.getMaxBlockZ()); }
    }
}
