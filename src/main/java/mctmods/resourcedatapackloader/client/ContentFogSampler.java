package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
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
    @Nullable private static Vec3 target;
    @Nullable private static Vec3 current;

    private ContentFogSampler() {}

    @Nullable public static SkyLookDef look(@Nullable Level level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        return def == null ? null : def.look();
    }

    public static Vec3 color(Vec3 fallback) { return current == null ? fallback : current; }

    public static int waterFog(int biome) {
        SkyLookDef look = look(Minecraft.getInstance().level);
        return look == null || look.waterFogColor() == SkyLookDef.UNSET ? biome : look.waterFogColor();
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
        SkyLookDef look = look(level);
        Entity camera = mc.getCameraEntity();
        if (level == null || look == null || !look.sampleFog() || camera == null) {
            target = null;
            current = null;
            return;
        }
        if (target == null || level.getGameTime() % INTERVAL == 0L) { target = sample(level, camera, look.fogGroundWeight()); }
        current = current == null ? target : current.add(target.subtract(current).scale(EASE));
    }

    private static Vec3 sample(ClientLevel level, Entity camera, float weight) {
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
        Vec3 sky = level.getSkyColor(camera.position(), 1.0F);
        if (count == 0) { return sky; }
        double light = level.getSkyDarken(1.0F);
        Vec3 ground = new Vec3(red / count * light, green / count * light, blue / count * light);
        return sky.scale(1.0D - weight).add(ground.scale(weight));
    }

    public static void onFog(ViewportEvent.RenderFog event) {
        SkyLookDef look = look(Minecraft.getInstance().level);
        if (look == null || look.fogDensity() <= 0.0F || event.getType() != FogType.NONE) { return; }
        if (event.getCamera().getEntity() instanceof LivingEntity living && (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS))) { return; }
        float far = event.getFarPlaneDistance();
        float end = far + (NEAR - far) * look.fogDensity();
        event.setNearPlaneDistance(event.getMode() == FogRenderer.FogMode.FOG_SKY ? 0.0F : end * START);
        event.setFarPlaneDistance(end);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }
}
