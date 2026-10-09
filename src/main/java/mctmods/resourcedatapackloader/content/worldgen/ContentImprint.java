package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Turns;
import mctmods.resourcedatapackloader.util.world.BlockFlags;
import mctmods.resourcedatapackloader.util.world.StructureLoot;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.Random;

public final class ContentImprint implements IContentShape {
    private static final int WORLDGEN_FLAGS = BlockFlags.UPDATE_CLIENTS;
    private static final int OFFSET = 8;
    private final ContentPlacer placer;
    private final ShapeDef shape;
    private final ResourceLocation key;
    private final boolean filtered;

    public ContentImprint(ContentPlacer placer, ShapeDef shape, ResourceLocation key, boolean filtered) {
        this.placer = placer;
        this.shape = shape;
        this.key = key;
        this.filtered = filtered;
    }

    public static int backX(Rotation rotation, BlockPos span) { return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0; }

    public static int backZ(Rotation rotation, BlockPos span) { return rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0; }

    public int[] pinnedAt() { return shape.at != null && shape.at.length == 2 ? shape.at : null; }

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
        Mirror mirror = mirror(random);
        settings.setMirror(mirror);
        settings.setIntegrity(shape.integrity / (float) ShapeDef.FULL_INTEGRITY);
        settings.setRandom(random);
        BlockPos span = loaded.transformedSize(rotation);
        int cornerX = within(origin.getX(), origin.getX() - span.getX() / 2, span.getX());
        int cornerZ = within(origin.getZ(), origin.getZ() - span.getZ() / 2, span.getZ());
        BlockPos fitted = loaded.getZeroPositionWithTransform(new BlockPos(cornerX, origin.getY(), cornerZ), mirror, rotation);
        if (span.getX() > 16 || span.getZ() > 16) {
            int reach = Math.max(span.getX(), span.getZ());
            if (!ContentPlacer.areaLoaded(world, fitted.getX() - reach, fitted.getZ() - reach, fitted.getX() + reach, fitted.getZ() + reach)) { return false; }
        }
        if (filtered) { loaded.addBlocksToWorld(world, fitted, onlyOverReplaceable(), settings, WORLDGEN_FLAGS); }
        else { loaded.addBlocksToWorld(world, fitted, settings, WORLDGEN_FLAGS); }
        StructureLoot.stock(world, cornerX, origin.getY(), cornerZ, cornerX + span.getX() - 1, origin.getY() + span.getY() - 1, cornerZ + span.getZ() - 1, shape.lootTable, random);
        if (shape.locateAs != null && !shape.locateAs.isEmpty()) { ContentLocate.record(world, shape.locateAs, fitted); }
        return true;
    }

    private ITemplateProcessor onlyOverReplaceable() { return (holder, at, info) -> placer.occupied(holder, at.getX(), at.getY(), at.getZ()) ? null : info; }

    private Rotation turn(Random random) {
        String named = PickDef.pick(shape.turns, random, "");
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return Turns.ALL[random.nextInt(Turns.ALL.length)];
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
        return MathHelper.clamp(start, corner, corner + 16 - span);
    }
}
