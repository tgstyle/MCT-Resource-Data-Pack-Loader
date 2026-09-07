package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.joml.Matrix4f;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDimensionEffects extends DimensionSpecialEffects {
    private static final List<String> VANILLA = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");
    @Nullable private final DimensionDef def;
    private final DimensionSpecialEffects base;

    private ContentDimensionEffects(@Nullable DimensionDef def, DimensionSpecialEffects base, float cloudHeight) {
        super(cloudHeight, base.hasGround(), base.skyType(), base.forceBrightLightmap(), base.constantAmbientLight());
        this.def = def;
        this.base = base;
    }

    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        Map<String, Integer> clouds = ContentDimensions.cloudHeights();
        Integer everywhere = ContentDimensions.cloudHeight("");
        for (String vanilla : VANILLA) {
            Integer wanted = clouds.getOrDefault(vanilla, everywhere);
            if (wanted == null) { continue; }
            event.register(ResourceLocation.parse(vanilla), new ContentDimensionEffects(null, base(vanilla), wanted));
            ContentLog.LOGGER.debug("Clouds in {} are drawn at y {}", vanilla, wanted);
        }
        for (DimensionDef def : ContentDimensions.all()) {
            if (!def.hasEffects()) { continue; }
            DimensionSpecialEffects base = base("minecraft:" + def.base());
            Integer cloud = ContentDimensions.cloudHeight(def.key().toString());
            float height = def.cloudHeight() >= 0 ? def.cloudHeight() : cloud != null ? cloud : base.getCloudHeight();
            event.register(def.key(), new ContentDimensionEffects(def, base, height));
        }
    }

    private static DimensionSpecialEffects base(String dimension) {
        return switch (dimension) {
            case "minecraft:the_nether" -> new NetherEffects();
            case "minecraft:the_end" -> new EndEffects();
            default -> new OverworldEffects();
        };
    }

    @Override @Nonnull public Vec3 getBrightnessDependentFogColor(@Nonnull Vec3 color, float brightness) {
        if (def == null || def.fogColor() < 0) { return base.getBrightnessDependentFogColor(color, brightness); }
        int rgb = def.fogColor();
        Vec3 fog = new Vec3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D);
        return fog.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
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
