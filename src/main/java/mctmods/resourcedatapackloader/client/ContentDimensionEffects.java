package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.RainSplash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDimensionEffects extends DimensionSpecialEffects {
    private static final double DRIFT = 0.03D;
    private static final float SKY_FLOOR = 0.35F;
    private static final float GRAY_PULL = 0.04F;
    private static final Vector3f BOSS_SHADE = new Vector3f(0.7F, 0.6F, 0.6F);
    private static final Vector3f GRAY = new Vector3f(0.75F, 0.75F, 0.75F);
    @Nullable private final DimensionDef def;
    private final DimensionSpecialEffects base;
    @Nullable private final ContentSkyRenderer sky;
    @Nullable private final RainDef rain;
    @Nullable private final ContentWeatherRenderer weather;
    @Nullable private SkyLookDef.CloudLayer drawing;

    private ContentDimensionEffects(@Nullable DimensionDef def, DimensionSpecialEffects base, float cloudHeight, SkyType skyType) {
        super(cloudHeight, base.hasGround(), skyType, base.forceBrightLightmap(), base.constantAmbientLight());
        this.def = def;
        this.base = base;
        this.sky = def == null || def.traits().sky() == null ? null : new ContentSkyRenderer(def.traits().sky());
        this.rain = def == null ? null : def.traits().rain();
        this.weather = rain == null ? null : new ContentWeatherRenderer(rain);
    }

    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        for (DimensionDef def : ContentDimensions.all()) {
            if (!def.hasEffects()) { continue; }
            DimensionSpecialEffects base = new OverworldEffects();
            if (def.surfaceWorld()) { event.register(def.key(), new ContentDimensionEffects(def, base, def.cloudHeight() >= 0 ? def.cloudHeight() : base.getCloudHeight(), SkyType.NORMAL)); }
            else { event.register(def.key(), new ContentDimensionEffects(def, base, Float.NaN, SkyType.NONE)); }
        }
    }

    @Nullable private SkyLookDef look() { return def == null ? null : def.look(); }

    public static float sun(@Nullable ClientLevel level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look == null ? 1.0F : look.sunBrightness();
    }

    public static float moon(@Nullable ClientLevel level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look == null ? 1.0F : look.moonBrightness();
    }

    public boolean drawsLayer() { return drawing != null && !Float.isNaN(drawing.height()); }

    public int layerColor() { return drawing == null ? SkyLookDef.UNSET : drawing.color(); }

    @Override @Nonnull public Vec3 getBrightnessDependentFogColor(@Nonnull Vec3 color, float brightness) {
        SkyLookDef look = look();
        Vec3 own = base.getBrightnessDependentFogColor(def == null || def.fogColor() < 0 ? color : Vec3.fromRGB24(def.fogColor()), brightness);
        return look != null && look.sampleFog() ? ContentFogSampler.color(own) : own;
    }

    @Override public float getCloudHeight() {
        SkyLookDef.CloudLayer layer = drawing;
        return layer != null && !Float.isNaN(layer.height()) ? layer.height() : super.getCloudHeight();
    }

    @Override public void adjustLightmapColors(@Nonnull ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, @Nonnull Vector3f colors) {
        SkyLookDef look = look();
        if (look == null || !look.tintsLight() || forceBrightLightmap()) { return; }
        float block = LightTexture.getBrightness(level.dimensionType(), pixelX) * blockLightRedFlicker;
        float sky = skyLight * look.skyFactor();
        float skyRed = sky * Mth.lerp(SKY_FLOOR, skyDarken, 1.0F);
        float blockGreen = block * ((block * 0.6F + 0.4F) * 0.6F + 0.4F);
        float blockBlue = block * (block * block * 0.6F + 0.4F);
        colors.set(skyRed * channel(look.lightSkyColor(), 16) + block * channel(look.lightBlockColor(), 16), skyRed * channel(look.lightSkyColor(), 8) + blockGreen * channel(look.lightBlockColor(), 8),
                sky * channel(look.lightSkyColor(), 0) + blockBlue * channel(look.lightBlockColor(), 0));
        colors.lerp(GRAY, GRAY_PULL);
        float darken = Minecraft.getInstance().gameRenderer.getDarkenWorldAmount(partialTicks);
        if (darken > 0.0F) { colors.lerp(new Vector3f(colors).mul(BOSS_SHADE), darken); }
    }

    private static float channel(int rgb, int shift) { return rgb == SkyLookDef.UNSET ? 1.0F : ((rgb >> shift) & 255) / 255.0F; }

    @Override public boolean isFoggyAt(int x, int z) { return (def != null && def.showFog()) || base.isFoggyAt(x, z); }

    @Override @Nullable public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        if (def != null && !def.sunriseColors()) { return null; }
        return base.getSunriseColor(timeOfDay, partialTicks);
    }

    @Override public boolean renderSky(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, @Nonnull Camera camera, @Nonnull Matrix4f projectionMatrix, boolean isFoggy, @Nonnull Runnable setupFog) {
        if (def != null && !def.renderSky()) { return true; }
        if (sky == null) { return false; }
        sky.render(level, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        return true;
    }

    @Override public boolean renderClouds(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, double camX, double camY, double camZ, @Nonnull Matrix4f projectionMatrix) {
        if (def == null) { return false; }
        if (!def.renderClouds()) { return true; }
        SkyLookDef look = def.look();
        if (drawing != null || look == null || !look.movesClouds()) { return false; }
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        double drift = (ticks + partialTick) * DRIFT;
        List<SkyLookDef.CloudLayer> layers = look.cloudLayers().isEmpty() ? List.of(new SkyLookDef.CloudLayer(Float.NaN, look.cloudSpeed(), SkyLookDef.UNSET)) : look.cloudLayers();
        try {
            for (SkyLookDef.CloudLayer layer : layers) {
                drawing = layer;
                renderer.renderClouds(poseStack, projectionMatrix, partialTick, camX + (layer.speed() - 1.0D) * drift, camY, camZ);
            }
        }
        finally { drawing = null; }
        return true;
    }

    @Override public boolean renderSnowAndRain(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull LightTexture lightTexture, double camX, double camY, double camZ) {
        if (def == null) { return false; }
        if (!def.renderWeather()) { return true; }
        if (weather == null) { return false; }
        weather.render(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, int ticks, @Nonnull Camera camera) {
        if (rain == null) { return false; }
        RainSplash.tick(level, rain, ticks, camera);
        return true;
    }
}
