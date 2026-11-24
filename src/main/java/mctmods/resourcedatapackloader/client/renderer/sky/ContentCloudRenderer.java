package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.client.util.CameraPos;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        Vec3d at = CameraPos.viewAt(view, partialTicks);
        RenderGlobal clouds = mc.renderGlobal;
        int counter = clouds.cloudTickCounter;
        double ticks = counter + partialTicks;
        rendering = true;
        try {
            if (look.cloudLayers.isEmpty()) { draw(mc, clouds, ticks, look.cloudSpeed, partialTicks, at.x, at.y, at.z); }
            for (SkyLookDef.CloudLayer layer : look.cloudLayers) {
                drawing = layer;
                draw(mc, clouds, ticks, layer.speed, partialTicks, at.x, at.y, at.z);
            }
        }
        finally {
            clouds.cloudTickCounter = counter;
            drawing = null;
            rendering = false;
        }
    }

    private static void draw(Minecraft mc, RenderGlobal clouds, double ticks, double speed, float partialTicks, double x, double y, double z) {
        clouds.cloudTickCounter = (int) Math.round(ticks * speed - partialTicks);
        mc.renderGlobal.renderClouds(partialTicks, PASS, x, y, z);
    }
}
