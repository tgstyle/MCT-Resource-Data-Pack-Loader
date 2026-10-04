package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.compat.LineClientCompat;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
    private static boolean sampled;

    private ContentFogSampler() {}

    @Nullable public static SkyLookDef look(@Nullable Level level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
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

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        SkyLookDef look = look(level);
        Entity camera = mc.getCameraEntity();
        if (level == null || look == null || !look.sampleFog() || camera == null) {
            sampled = false;
            target = null;
            current = null;
            return;
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
                red += (map.col >> 16 & 255) / 255.0D;
                green += (map.col >> 8 & 255) / 255.0D;
                blue += (map.col & 255) / 255.0D;
                count++;
            }
        }
        if (count == 0) { return null; }
        double light = (FULL_LIGHT - level.getSkyDarken()) / FULL_LIGHT;
        return new Vec3(red / count * light, green / count * light, blue / count * light);
    }

    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        SkyLookDef look = look(Minecraft.getInstance().level);
        if (look == null || !look.sampleFog() || !sampled || camera.getFluidInCamera() != FogType.NONE) { return; }
        Vec3 sky = LineClientCompat.skyColor(camera, (float) event.getPartialTick());
        Vec3 color = current == null ? sky : sky.scale(1.0D - look.fogGroundWeight()).add(current.scale(look.fogGroundWeight()));
        event.setRed((float) color.x);
        event.setGreen((float) color.y);
        event.setBlue((float) color.z);
    }

    public static void onFog(ViewportEvent.RenderFog event) {
        SkyLookDef look = look(Minecraft.getInstance().level);
        if (look == null || look.fogDensity() <= 0.0F || event.getType() != FogType.ATMOSPHERIC) { return; }
        if (event.getCamera().entity() instanceof LivingEntity living && (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS))) { return; }
        FogData fog = event.getFogData();
        float far = Math.min(fog.environmentalEnd, fog.renderDistanceEnd);
        float end = far + (NEAR - far) * look.fogDensity();
        event.setNearPlaneDistance(end * START);
        event.setFarPlaneDistance(end);
        fog.skyEnd = Math.min(fog.skyEnd, end);
        fog.cloudEnd = Math.min(fog.cloudEnd, end);
    }
}
