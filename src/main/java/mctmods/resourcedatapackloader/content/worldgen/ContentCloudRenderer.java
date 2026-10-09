package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IRenderGlobalClouds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentCloudRenderer extends IRenderHandler {
    private static final int PASS = 2;
    @Nullable private static SkyLookDef.CloudLayer drawing;
    private static boolean rendering;
    private final SkyLookDef look;

    public ContentCloudRenderer(SkyLookDef look) { this.look = look; }

    @Nullable public static SkyLookDef.CloudLayer drawing() { return drawing; }

    public static boolean rendering() { return rendering; }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        Entity view = mc.getRenderViewEntity();
        if (view == null) { return; }
        double x = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double y = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double z = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        IRenderGlobalClouds clouds = (IRenderGlobalClouds) mc.renderGlobal;
        int counter = clouds.getCloudTickCounter();
        double ticks = counter + partialTicks;
        rendering = true;
        try {
            if (look.cloudLayers.isEmpty()) { draw(mc, clouds, ticks, look.cloudSpeed, partialTicks, x, y, z); }
            for (SkyLookDef.CloudLayer layer : look.cloudLayers) {
                drawing = layer;
                draw(mc, clouds, ticks, layer.speed, partialTicks, x, y, z);
            }
        }
        finally {
            clouds.setCloudTickCounter(counter);
            drawing = null;
            rendering = false;
        }
    }

    private static void draw(Minecraft mc, IRenderGlobalClouds clouds, double ticks, double speed, float partialTicks, double x, double y, double z) {
        clouds.setCloudTickCounter((int) Math.round(ticks * speed - partialTicks));
        mc.renderGlobal.renderClouds(partialTicks, PASS, x, y, z);
    }
}
