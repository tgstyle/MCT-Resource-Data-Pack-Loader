package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.util.RainSplash;
import mctmods.resourcedatapackloader.util.Rgb;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDimensionEffects extends DimensionSpecialEffects {
    private static final double DRIFT = 0.03D;
    private static final float SKY_FLOOR = 0.35F;
    private static final float GRAY_PULL = 0.04F;
    private static final Vector3f BOSS_SHADE = new Vector3f(0.7F, 0.6F, 0.6F);
    private static final Vector3f GRAY = new Vector3f(0.75F, 0.75F, 0.75F);
    private static final DimensionSpecialEffects DAYLIT = new OverworldEffects();
    private static final Map<DimensionTraitsDef.Sky, ContentSkyRenderer> SKIES = new IdentityHashMap<>();
    private static final Drawn NOTHING = new Drawn(null, null, null);
    private final ResourceLocation key;
    private final DimensionSpecialEffects base;
    private final Map<DimensionDef, Drawn> drawn = new IdentityHashMap<>();
    @Nullable private SkyLookDef.CloudLayer drawing;

    private ContentDimensionEffects(DimensionDef def, DimensionSpecialEffects base, float cloudHeight, SkyType skyType) {
        super(cloudHeight, base.hasGround(), skyType, base.forceBrightLightmap(), base.constantAmbientLight());
        this.key = def.key();
        this.base = base;
    }

    @Nullable private DimensionDef def() { return ContentDimensions.def(key); }

    private Drawn drawn(@Nullable DimensionDef def) { return def == null ? NOTHING : drawn.computeIfAbsent(def, Drawn::of); }

    private record Drawn(@Nullable ContentSkyRenderer sky, @Nullable RainDef rain, @Nullable ContentWeatherRenderer weather) {
        private static Drawn of(DimensionDef def) {
            RainDef rain = def.traits().rain();
            return new Drawn(def.traits().sky() == null || !drawsWholeSky(def) ? null : SKIES.computeIfAbsent(def.traits().sky(), held -> new ContentSkyRenderer(held, def.traits().moon())), rain, rain == null ? null : new ContentWeatherRenderer(rain));
        }
    }

    private static boolean drawsWholeSky(DimensionDef def) {
        if (!ContentDimensions.existing(def.key())) { return true; }
        return def.gives("sky.bodies") || def.gives("sky.stars") || def.gives("sky.sun.size") || def.gives("sky.moon.size");
    }

    public static DimensionSpecialEffects existing(DimensionSpecialEffects base, @Nullable DimensionDef def) {
        if (def == null || !ContentEnvironments.hasEffects(def.key(), def) || !ContentDimensions.existing(def.key())) { return base; }
        if (def.gives("sky.surfaceWorld") && !def.surfaceWorld()) { return new ContentDimensionEffects(def, base, Float.NaN, SkyType.NONE); }
        SkyType skyType = def.gives("sky.surfaceWorld") && base.skyType() == SkyType.NONE ? SkyType.NORMAL : base.skyType();
        return new ContentDimensionEffects(def, base, def.gives("sky.cloudHeight") ? def.cloudHeight() : base.getCloudHeight(), skyType);
    }

    public static ResourceLocation sunTexture(@Nullable ClientLevel level, ResourceLocation vanilla) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null || def.traits().sky() == null ? vanilla : def.traits().sky().sunTexture();
    }

    public static ResourceLocation moonTexture(@Nullable ClientLevel level, ResourceLocation vanilla) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null || def.traits().moon() == null ? vanilla : def.traits().moon().texture();
    }

    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        for (DimensionDef def : ContentDimensions.all()) {
            if (!ContentEnvironments.hasEffects(def.key(), def) || ContentDimensions.existing(def.key())) { continue; }
            DimensionSpecialEffects base = new OverworldEffects();
            if (def.surfaceWorld()) { event.register(def.key(), new ContentDimensionEffects(def, base, def.cloudHeight() >= 0 ? def.cloudHeight() : base.getCloudHeight(), SkyType.NORMAL)); }
            else { event.register(def.key(), new ContentDimensionEffects(def, base, Float.NaN, SkyType.NONE)); }
        }
    }

    @Nullable private SkyLookDef look() {
        DimensionDef def = def();
        return def == null ? null : def.look();
    }

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
        DimensionDef def = def();
        SkyLookDef look = def == null ? null : def.look();
        Vec3 own = def == null || def.fogColor() < 0 ? base.getBrightnessDependentFogColor(color, brightness) : litFog(def.fogColor(), brightness);
        return ContentWindowLook.fogColor(def, look != null && look.sampleFog() ? ContentFogSampler.color(own) : own, brightness);
    }

    static Vec3 litFog(int rgb, float brightness) { return DAYLIT.getBrightnessDependentFogColor(Vec3.fromRGB24(rgb), brightness); }

    @Override public float getCloudHeight() {
        SkyLookDef.CloudLayer layer = drawing;
        if (layer != null && !Float.isNaN(layer.height())) { return layer.height(); }
        float own = super.getCloudHeight();
        DimensionDef def = def();
        if (Float.isNaN(own) || def == null) { return own; }
        return ContentDimensions.existing(key) && !def.gives("sky.cloudHeight") ? base.getCloudHeight() : def.cloudHeight();
    }

    @Override public void adjustLightmapColors(@Nonnull ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, @Nonnull Vector3f colors) {
        base.adjustLightmapColors(level, partialTicks, skyDarken, blockLightRedFlicker, skyLight, pixelX, pixelY, colors);
        SkyLookDef look = look();
        if (look != null && look.tintsLight() && !forceBrightLightmap()) { tintLight(level, look, partialTicks, skyDarken, blockLightRedFlicker, skyLight, pixelX, colors); }
    }

    private static void tintLight(ClientLevel level, SkyLookDef look, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, Vector3f colors) {
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

    private static float channel(int rgb, int shift) { return rgb == SkyLookDef.UNSET ? 1.0F : Rgb.channel(rgb, shift); }

    @Override public boolean isFoggyAt(int x, int z) {
        DimensionDef def = def();
        return (def == null || (ContentDimensions.existing(key) && !def.gives("sky.showFog"))) && base.isFoggyAt(x, z);
    }

    @Override @Nullable public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        DimensionDef def = def();
        if (def != null && !def.sunriseColors()) { return null; }
        return base.getSunriseColor(timeOfDay, partialTicks);
    }

    @Override public boolean renderSky(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull Matrix4f modelViewMatrix, @Nonnull Camera camera, @Nonnull Matrix4f projectionMatrix, boolean isFoggy, @Nonnull Runnable setupFog) {
        DimensionDef def = def();
        if (def != null && (!def.renderSky() || (def.showFog() && skyType() == SkyType.END))) { return true; }
        ContentSkyRenderer sky = drawn(def).sky();
        if (sky == null) { return base.renderSky(level, ticks, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog); }
        sky.render(level, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
        return true;
    }

    @Override public boolean renderClouds(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, double camX, double camY, double camZ, @Nonnull Matrix4f modelViewMatrix, @Nonnull Matrix4f projectionMatrix) {
        DimensionDef def = def();
        if (def != null && !def.renderClouds()) { return true; }
        SkyLookDef look = def == null ? null : def.look();
        if (drawing != null || look == null || !look.movesClouds()) { return base.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix); }
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        double drift = (ticks + partialTick) * DRIFT;
        List<SkyLookDef.CloudLayer> layers = look.cloudLayers().isEmpty() ? List.of(new SkyLookDef.CloudLayer(Float.NaN, look.cloudSpeed(), SkyLookDef.UNSET)) : look.cloudLayers();
        try {
            for (SkyLookDef.CloudLayer layer : layers) {
                drawing = layer;
                renderer.renderClouds(poseStack, modelViewMatrix, projectionMatrix, partialTick, camX + (layer.speed() - 1.0D) * drift, camY, camZ);
            }
        }
        finally { drawing = null; }
        return true;
    }

    @Override public boolean renderSnowAndRain(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull LightTexture lightTexture, double camX, double camY, double camZ) {
        DimensionDef def = def();
        if (def != null && !def.renderWeather()) { return true; }
        ContentWeatherRenderer weather = drawn(def).weather();
        if (weather == null) { return base.renderSnowAndRain(level, ticks, partialTick, lightTexture, camX, camY, camZ); }
        weather.render(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, int ticks, @Nonnull Camera camera) {
        RainDef rain = drawn(def()).rain();
        if (rain == null) { return base.tickRain(level, ticks, camera); }
        RainSplash.tick(level, rain, ticks, camera);
        return true;
    }
}
