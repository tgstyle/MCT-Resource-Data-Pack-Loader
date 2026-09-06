package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Optional;

public final class ContentImprint implements IContentShape {
    private static final int FLAGS = 2;
    private static final Rotation[] TURNS = Rotation.values();
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

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        String named = shape.structures().isEmpty() ? shape.structure() : PickDef.pick(shape.structures(), random);
        ResourceLocation template = named == null || named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template == null) { return false; }
        WorldGenLevel level = placer.level();
        Optional<StructureTemplate> held = level.getLevel().getStructureManager().get(template);
        if (held.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen {} places structure '{}', which could not be loaded, so nothing generates", key, named);
            return false;
        }
        StructureTemplate loaded = held.get();
        Rotation rotation = turn(random);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror(random)).setRandom(random);
        if (shape.integrity() < 100) { settings.addProcessor(new BlockRotProcessor(shape.integrity() / 100.0F)); }
        if (filtered) { settings.addProcessor(new ContentImprintProcessor(placer)); }
        Vec3i span = loaded.getSize(rotation);
        int cornerX = within(origin.getX(), origin.getX() - span.getX() / 2, span.getX());
        int cornerZ = within(origin.getZ(), origin.getZ() - span.getZ() / 2, span.getZ());
        if (!fits(placer, cornerX, cornerZ, span)) { return false; }
        BlockPos fitted = new BlockPos(cornerX + backX(rotation, span), origin.getY(), cornerZ + backZ(rotation, span));
        if (!loaded.placeInWorld(level, fitted, fitted, settings, random, FLAGS)) { return false; }
        stock(placer, random, cornerX, origin.getY(), cornerZ, span);
        if (!shape.locateAs().isEmpty()) { ContentLocate.record(level.getLevel(), shape.locateAs(), fitted); }
        return true;
    }

    private void stock(ContentPlacer placer, RandomSource random, int cornerX, int baseY, int cornerZ, Vec3i span) {
        if (shape.lootTable().isEmpty()) { return; }
        ResourceLocation table = ResourceLocation.tryParse(shape.lootTable());
        if (table == null) {
            ContentLog.LOGGER.error("Worldgen {} names loot table '{}', which is not a valid id, so the containers are left as the template holds them", key, shape.lootTable());
            return;
        }
        WorldGenLevel level = placer.level();
        BoundingBox box = new BoundingBox(cornerX, baseY, cornerZ, cornerX + span.getX() - 1, baseY + span.getY() - 1, cornerZ + span.getZ() - 1);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    at.set(x, y, z);
                    if (placer.unreadable(at)) { continue; }
                    BlockEntity found = level.getBlockEntity(at);
                    if (found instanceof RandomizableContainerBlockEntity container) { container.setLootTable(table, random.nextLong()); }
                }
            }
        }
    }

    private static boolean fits(ContentPlacer placer, int cornerX, int cornerZ, Vec3i span) {
        return placer.writable(cornerX, cornerZ) && placer.writable(cornerX + span.getX() - 1, cornerZ + span.getZ() - 1);
    }

    private Rotation turn(RandomSource random) {
        String named = PickDef.pick(shape.turns(), random);
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return TURNS[random.nextInt(TURNS.length)];
    }

    private Mirror mirror(RandomSource random) {
        String named = PickDef.pick(shape.mirrors(), random);
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }
        return Mirror.NONE;
    }

    private static int within(int origin, int start, int span) {
        if (span >= 16) { return start; }
        int corner = (origin >> 4) * 16;
        return Mth.clamp(start, corner, corner + 16 - span);
    }
}
