package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.compat.LineClientCompat;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.content.worldgen.ContentParserSkyLook;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;
import javax.annotation.Nullable;

public final class ContentFogSampler {
    private static final int REACH = 16;
    private static final int STEP = 4;
    private static final int INTERVAL = 20;
    private static final double EASE = 0.1D;
    private static final float NEAR = 8.0F;
    private static final float START = 0.75F;
    private static final float FULL_LIGHT = 15.0F;
    @Nullable private static Vec3 target;
    @Nullable private static Vec3 current;
    @Nullable private static ResourceKey<Level> sampledIn;
    private static boolean sampled;

    private ContentFogSampler() {}

    @Nullable public static SkyLookDef look(@Nullable Level level) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null ? null : def.look();
    }

    public static float sun(@Nullable Level level) {
        SkyLookDef look = look(level);
        return look == null ? 1.0F : look.sunBrightness();
    }

    public static float moon(@Nullable Level level) {
        SkyLookDef look = look(level);
        return look == null ? 1.0F : look.moonBrightness();
    }

    public static int lavaFog() {
        SkyLookDef look = look(Minecraft.getInstance().level);
        return look == null ? SkyLookDef.UNSET : look.lavaFogColor();
    }

    public static int lightning() {
        SkyLookDef look = look(Minecraft.getInstance().level);
        return look == null ? SkyLookDef.UNSET : look.lightningColor();
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Entity camera = mc.getCameraEntity();
        if (level != null && camera != null) { ContentEnvironments.view(level.getBiome(camera.blockPosition())); }
        ContentWindowLook.tick(level);
        DimensionDef def = ContentDimensions.def(level);
        SkyLookDef look = def == null ? null : def.look();
        if (level == null || def == null || camera == null || !(look != null && look.sampleFog() || ContentWindowLook.samples(def))) {
            sampledIn = null;
            sampled = false;
            target = null;
            current = null;
            return;
        }
        if (!level.dimension().equals(sampledIn)) {
            sampledIn = level.dimension();
            sampled = false;
            target = null;
            current = null;
        }
        if (!sampled || level.getGameTime() % INTERVAL == 0L) {
            target = ground(level, camera);
            sampled = true;
        }
        if (target == null) { current = null; }
        else { current = current == null ? target : current.add(target.subtract(current).scale(EASE)); }
    }

    @Nullable private static Vec3 ground(ClientLevel level, Entity camera) {
        int x = Mth.floor(camera.getX());
        int z = Mth.floor(camera.getZ());
        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        int count = 0;
        for (int dx = -REACH; dx <= REACH; dx += STEP) {
            for (int dz = -REACH; dz <= REACH; dz += STEP) {
                BlockPos top = new BlockPos(x + dx, level.getHeight(Heightmap.Types.WORLD_SURFACE, x + dx, z + dz) - 1, z + dz);
                MapColor map = level.getBlockState(top).getMapColor(level, top);
                if (map == MapColor.NONE) { continue; }
                red += ARGB.redFloat(map.col);
                green += ARGB.greenFloat(map.col);
                blue += ARGB.blueFloat(map.col);
                count++;
            }
        }
        if (count == 0) { return null; }
        double light = (FULL_LIGHT - level.getSkyDarken()) / FULL_LIGHT;
        return new Vec3(red / count * light, green / count * light, blue / count * light);
    }

    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = look(level);
        boolean samples = look != null && look.sampleFog() && sampled;
        if (!samples && !ContentWindowLook.colorsFog(level) || camera.getFluidInCamera() != FogType.NONE) { return; }
        float partialTick = (float) event.getPartialTick();
        Vec3 sky = LineClientCompat.skyColor(camera, partialTick);
        double weight = look == null ? ContentParserSkyLook.GROUND_WEIGHT : look.fogGroundWeight();
        Vec3 ground = current == null ? sky : sky.scale(1.0D - weight).add(current.scale(weight));
        Vec3 base = samples ? ground : new Vec3(event.getRed(), event.getGreen(), event.getBlue());
        Vec3 color = ContentWindowLook.fogColor(level, base, ground, camera.attributeProbe().getValue(EnvironmentAttributes.SUN_ANGLE, partialTick));
        event.setRed((float) color.x);
        event.setGreen((float) color.y);
        event.setBlue((float) color.z);
    }

    public static void onFog(ViewportEvent.RenderFog event) {
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = look(level);
        float density = ContentWindowLook.fogDensity(level, look == null ? 0.0F : look.fogDensity());
        if (density <= 0.0F || event.getType() != FogType.ATMOSPHERIC) { return; }
        if (event.getCamera().entity() instanceof LivingEntity living && (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS))) { return; }
        FogData fog = event.getFogData();
        float far = Math.min(fog.environmentalEnd, fog.renderDistanceEnd);
        float end = far + (NEAR - far) * density;
        event.setNearPlaneDistance(end * START);
        event.setFarPlaneDistance(end);
        fog.skyEnd = Math.min(fog.skyEnd, end);
        fog.cloudEnd = Math.min(fog.cloudEnd, end);
    }
}
