package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.Random;

public final class ContentImprint implements IContentShape {
    private static final int WORLDGEN_FLAGS = 2;
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

        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(TURNS[random.nextInt(TURNS.length)]);
        settings.setIntegrity(shape.integrity / 100.0F);
        settings.setRandom(random);

        BlockPos size = loaded.getSize();
        loaded.addBlocksToWorld(world, origin.add(-(size.getX() / 2), 0, -(size.getZ() / 2)), settings, WORLDGEN_FLAGS);
        return true;
    }
}
