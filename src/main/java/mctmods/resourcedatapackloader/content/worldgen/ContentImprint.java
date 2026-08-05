package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.Random;

public final class ContentImprint implements IContentShape {
    private static final int WORLDGEN_FLAGS = 2;
    private static final int OFFSET = 8;
    private static final Rotation[] TURNS = Rotation.values();
    private final ShapeDef shape;
    private final ResourceLocation key;
    private final ResourceLocation template;

    public ContentImprint(ShapeDef shape, ResourceLocation key) {
        this.shape = shape;
        this.key = key;
        this.template = shape.structure.isEmpty() ? null : new ResourceLocation(shape.structure);
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        if (template == null || !(world instanceof WorldServer)) { return false; }

        WorldServer server = (WorldServer) world;
        MinecraftServer host = server.getMinecraftServer();
        Template loaded = server.getStructureTemplateManager().get(host, template);
        if (loaded == null) {
            ContentLog.LOGGER.error("Worldgen {} places structure '{}', which could not be loaded, so nothing generates", key, shape.structure);
            return false;
        }

        Rotation rotation = TURNS[random.nextInt(TURNS.length)];
        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(rotation);
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

        loaded.addBlocksToWorld(world, fitted, settings, WORLDGEN_FLAGS);
        if (shape.locateAs != null && !shape.locateAs.isEmpty()) { ContentLocate.record(world, shape.locateAs, fitted); }
        return true;
    }

    private static int within(int origin, int start, int span) {
        if (span >= 16) { return start; }

        int corner = ((origin - OFFSET) >> 4) * 16 + OFFSET;
        return Math.max(corner, Math.min(start, corner + 16 - span));
    }
}
