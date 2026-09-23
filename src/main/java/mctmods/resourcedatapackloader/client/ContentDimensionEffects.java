package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.joml.Matrix4f;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDimensionEffects extends DimensionSpecialEffects {
    @Nullable private final DimensionDef def;
    private final DimensionSpecialEffects base;

    private ContentDimensionEffects(@Nullable DimensionDef def, DimensionSpecialEffects base, float cloudHeight, SkyType skyType) {
        super(cloudHeight, base.hasGround(), skyType, base.forceBrightLightmap(), base.constantAmbientLight());
        this.def = def;
        this.base = base;
    }

    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        for (DimensionDef def : ContentDimensions.all()) {
            if (!def.hasEffects()) { continue; }
            DimensionSpecialEffects base = new OverworldEffects();
            if (def.surfaceWorld()) { event.register(def.key(), new ContentDimensionEffects(def, base, def.cloudHeight() >= 0 ? def.cloudHeight() : base.getCloudHeight(), SkyType.NORMAL)); }
            else { event.register(def.key(), new ContentDimensionEffects(def, base, Float.NaN, SkyType.NONE)); }
        }
    }

    @Override @Nonnull public Vec3 getBrightnessDependentFogColor(@Nonnull Vec3 color, float brightness) {
        if (def == null || def.fogColor() < 0) { return base.getBrightnessDependentFogColor(color, brightness); }
        int rgb = def.fogColor();
        return new Vec3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D);
    }

    @Override public boolean isFoggyAt(int x, int z) { return (def != null && def.showFog()) || base.isFoggyAt(x, z); }

    @Override @Nullable public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        if (def != null && !def.sunriseColors()) { return null; }
        return base.getSunriseColor(timeOfDay, partialTicks);
    }

    @Override public boolean renderSky(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, @Nonnull Camera camera, @Nonnull Matrix4f projectionMatrix, boolean isFoggy, @Nonnull Runnable setupFog) {
        return def != null && !def.renderSky();
    }

    @Override public boolean renderClouds(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, double camX, double camY, double camZ, @Nonnull Matrix4f projectionMatrix) {
        return def != null && !def.renderClouds();
    }

    @Override public boolean renderSnowAndRain(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull LightTexture lightTexture, double camX, double camY, double camZ) {
        return def != null && !def.renderWeather();
    }
}
