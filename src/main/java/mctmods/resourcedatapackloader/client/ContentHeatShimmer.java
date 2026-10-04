package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IPostChain;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import java.io.IOException;
import javax.annotation.Nullable;

public final class ContentHeatShimmer {
    private static final ResourceLocation SHADER = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "shaders/post/heat.json");
    private static final float EASE = 0.05F;
    private static final float GONE = 0.002F;
    private static final float NIGHT = 0.2F;
    private static float strength;
    @Nullable private static SkyLookDef.Heat heat;
    @Nullable private static PostChain chain;
    private static boolean failed;
    private static int width;
    private static int height;

    private ContentHeatShimmer() {}

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        SkyLookDef look = ContentFogSampler.look(mc.level);
        float target = target(mc, look);
        strength = look == null || look.heat() == null ? 0.0F : strength + (target - strength) * EASE;
        if (target <= 0.0F && strength < GONE) { strength = 0.0F; }
        if (strength <= 0.0F) {
            close();
            return;
        }
        heat = look.heat();
        PostChain own = open(mc);
        if (own != null) { set(own, "Strength", strength); }
    }

    public static void onStage(RenderLevelStageEvent event) {
        PostChain own = chain;
        SkyLookDef.Heat look = heat;
        if (own == null || look == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) { return; }
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        if (window.getWidth() != width || window.getHeight() != height) {
            width = window.getWidth();
            height = window.getHeight();
            own.resize(width, height);
        }
        set(own, "Mode", look.followsWorld() ? 1.0F : 0.0F);
        if (look.followsWorld()) {
            Matrix4f projection = event.getProjectionMatrix();
            set(own, "StartDistance", look.startDistance());
            set(own, "EndDistance", Math.max(mc.options.getEffectiveRenderDistance() * 16.0F, look.startDistance() + 1.0F));
            set(own, "DepthBias", projection.m22() - 1.0F);
            set(own, "DepthLinear", projection.m32());
        }
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        own.process(event.getPartialTick().getGameTimeDeltaTicks());
        mc.getMainRenderTarget().bindWrite(true);
        RenderSystem.enableDepthTest();
    }

    @Nullable private static PostChain open(Minecraft mc) {
        if (chain != null || failed) { return chain; }
        try {
            chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), SHADER);
            width = 0;
            height = 0;
        }
        catch (IOException | JsonSyntaxException e) {
            failed = true;
            ContentLog.LOGGER.error("The heat shimmer shader {} could not be loaded, so no shimmer is drawn", SHADER, e);
        }
        return chain;
    }

    private static void set(PostChain own, String name, float value) {
        for (PostPass pass : ((IPostChain) own).getPasses()) { pass.getEffect().safeGetUniform(name).set(value); }
    }

    private static void close() {
        heat = null;
        if (chain == null) { return; }
        chain.close();
        chain = null;
    }

    private static float target(Minecraft mc, @Nullable SkyLookDef look) {
        Entity camera = mc.getCameraEntity();
        if (look == null || look.heat() == null || camera == null || mc.level == null || camera.isInWater()) { return 0.0F; }
        SkyLookDef.Heat heat = look.heat();
        if (mc.level.getBiome(camera.blockPosition()).value().getBaseTemperature() < heat.minTemperature()) { return 0.0F; }
        if (!heat.dayOnly()) { return heat.strength(); }
        return heat.strength() * Math.max(0.0F, (mc.level.getSkyDarken(1.0F) - NIGHT) / (1.0F - NIGHT));
    }
}
