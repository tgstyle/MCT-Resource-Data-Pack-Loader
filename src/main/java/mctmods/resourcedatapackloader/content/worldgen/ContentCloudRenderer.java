package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IRenderGlobalClouds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentCloudRenderer extends IRenderHandler {
    private static final int PASS = 2;
    private final SkyLookDef look;

    public ContentCloudRenderer(SkyLookDef look) { this.look = look; }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        Entity view = mc.getRenderViewEntity();
        if (view == null || !(world.provider instanceof ContentWorldProvider)) { return; }
        ContentWorldProvider provider = (ContentWorldProvider) world.provider;
        double x = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double y = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double z = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        IRenderGlobalClouds clouds = (IRenderGlobalClouds) mc.renderGlobal;
        int counter = clouds.getCloudTickCounter();
        double ticks = counter + partialTicks;
        provider.drawClouds(true);
        try {
            if (look.cloudLayers.isEmpty()) { draw(mc, clouds, ticks, look.cloudSpeed, partialTicks, x, y, z); }
            for (SkyLookDef.CloudLayer layer : look.cloudLayers) {
                provider.drawLayer(layer);
                draw(mc, clouds, ticks, layer.speed, partialTicks, x, y, z);
            }
        }
        finally {
            clouds.setCloudTickCounter(counter);
            provider.drawLayer(null);
            provider.drawClouds(false);
        }
    }

    private static void draw(Minecraft mc, IRenderGlobalClouds clouds, double ticks, double speed, float partialTicks, double x, double y, double z) {
        clouds.setCloudTickCounter((int) Math.round(ticks * speed - partialTicks));
        mc.renderGlobal.renderClouds(partialTicks, PASS, x, y, z);
    }
}
