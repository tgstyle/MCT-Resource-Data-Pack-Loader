package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.Random;

public final class ContentImprint implements IContentShape {
    private static final int WORLDGEN_FLAGS = 2;
    private static final int OFFSET = 8;
    private static final Rotation[] TURNS = Rotation.values();
    private final ContentPlacer placer;
    private final ShapeDef shape;
    private final ResourceLocation key;

    public int[] pinnedAt() { return shape.at != null && shape.at.length == 2 ? shape.at : null; }

    public ContentImprint(ContentPlacer placer, ShapeDef shape, ResourceLocation key) {
        this.placer = placer;
        this.shape = shape;
        this.key = key;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        String named = PickDef.pick(shape.structures, random, shape.structure);
        if (named == null || named.isEmpty() || !(world instanceof WorldServer)) { return false; }

        ResourceLocation template = new ResourceLocation(named);
        WorldServer server = (WorldServer) world;
        MinecraftServer host = server.getMinecraftServer();
        Template loaded = server.getStructureTemplateManager().get(host, template);
        if (loaded == null) {
            ContentLog.LOGGER.error("Worldgen {} places structure '{}', which could not be loaded, so nothing generates", key, named);
            return false;
        }

        Rotation rotation = turn(random);
        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(rotation);
        settings.setMirror(mirror(random));
        settings.setIntegrity(shape.integrity / 100.0F);
        settings.setRandom(random);

        BlockPos span = loaded.transformedSize(rotation);
        int backX = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0;
        int backZ = rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0;

        int cornerX = within(origin.getX(), origin.getX() - span.getX() / 2, span.getX());
        int cornerZ = within(origin.getZ(), origin.getZ() - span.getZ() / 2, span.getZ());
        BlockPos fitted = new BlockPos(cornerX + backX, origin.getY(), cornerZ + backZ);
        if (span.getX() > 16 || span.getZ() > 16) {
            if (!ContentCascade.loaded(world, fitted, Math.max(span.getX(), span.getZ()))) { return false; }
        }

        loaded.addBlocksToWorld(world, fitted, onlyOverReplaceable(), settings, WORLDGEN_FLAGS);
        if (shape.locateAs != null && !shape.locateAs.isEmpty()) { ContentLocate.record(world, shape.locateAs, fitted); }
        return true;
    }

    private ITemplateProcessor onlyOverReplaceable() {
        return (holder, at, info) -> placer.occupied(holder, at.getX(), at.getY(), at.getZ()) ? null : info;
    }

    private Rotation turn(Random random) {
        String named = PickDef.pick(shape.turns, random, "");
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }

        return TURNS[random.nextInt(TURNS.length)];
    }

    private Mirror mirror(Random random) {
        String named = PickDef.pick(shape.mirrors, random, "");
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }

        return Mirror.NONE;
    }

    private static int within(int origin, int start, int span) {
        if (span >= 16) { return start; }

        int corner = ((origin - OFFSET) >> 4) * 16 + OFFSET;
        return Math.max(corner, Math.min(start, corner + 16 - span));
    }
}
