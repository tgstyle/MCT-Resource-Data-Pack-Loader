package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.util.Colors;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) final class ContentWindowLook {
    private static final float EASE = 0.1F;
    private static final float[] NO_WEIGHTS = new float[0];
    @Nullable private static DimensionDef held;
    private static float[] weights = NO_WEIGHTS;

    private ContentWindowLook() {}

    static void tick(@Nullable World world) {
        DimensionDef def = ContentDimensions.of(world);
        if (world == null || def == null || def.windows.isEmpty()) {
            held = null;
            weights = NO_WEIGHTS;
            return;
        }
        TimeWindowDef active = ContentTimeWindows.active(world, def);
        boolean arrived = def != held;
        if (arrived) {
            held = def;
            weights = new float[def.windows.size()];
        }
        for (int index = 0; index < weights.length; index++) {
            float aim = def.windows.get(index) == active ? 1.0F : 0.0F;
            weights[index] = arrived ? aim : weights[index] + (aim - weights[index]) * EASE;
        }
    }

    static boolean samples(DimensionDef def) { return def.windows.stream().anyMatch(window -> window.sampleFog); }

    @Nullable private static DimensionDef current(@Nullable World world) { return held != null && ContentDimensions.of(world) == held ? held : null; }

    static float fogDensity(@Nullable World world, float base) {
        DimensionDef def = current(world);
        if (def == null) { return base; }
        float density = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (window.gives(TimeWindowDef.FOG_DENSITY)) { density += (window.fogDensity - density) * weights[index]; }
        }
        return density;
    }

    static Vec3d fogColor(@Nullable World world, Vec3d base, float celestialAngle) {
        DimensionDef def = current(world);
        if (def == null) { return base; }
        Vec3d color = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (weights[index] <= 0.0F || !window.gives(TimeWindowDef.FOG_COLOR)) { continue; }
            Vec3d aim = window.sampleFog ? ContentFogSampler.color(base) : ContentDimensionRules.fogColor(window.fogColor, celestialAngle);
            color = color.add(aim.subtract(color).scale(weights[index]));
        }
        return color;
    }

    static float colorFog(@Nullable World world) {
        DimensionDef def = current(world);
        if (def == null) { return 0.0F; }
        float lit = 0.0F;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (window.gives(TimeWindowDef.FOG_COLOR) && !window.sampleFog) { lit += (1.0F - lit) * weights[index]; }
        }
        return lit;
    }

    @Nullable static float[] sunTint(@Nullable World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        TimeWindowDef first = first(world, TimeWindowDef.SUN_COLOR);
        if ((look == null || look.sunColor == SkyLookDef.UNSET) && first == null) { return null; }
        float[] tint = look == null || look.sunColor == SkyLookDef.UNSET ? new float[] {1.0F, 1.0F, 1.0F, 1.0F} : rgba(look.sunColor, 1.0F);
        DimensionDef def = current(world);
        if (def == null) { return tint; }
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (window.gives(TimeWindowDef.SUN_COLOR)) { blend(tint, rgba(window.sunColor, 1.0F), weights[index]); }
        }
        return tint;
    }

    @Nullable static float[] aura(@Nullable World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        TimeWindowDef first = first(world, TimeWindowDef.SUN_AURA);
        if ((look == null || look.sunAuraColor == SkyLookDef.UNSET) && first == null) { return null; }
        float[] aura = look == null || look.sunAuraColor == SkyLookDef.UNSET ? rgba(first.sunAuraColor, 0.0F) : rgba(look.sunAuraColor, 1.0F);
        DimensionDef def = current(world);
        if (def == null) { return aura; }
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (window.gives(TimeWindowDef.SUN_AURA)) { blend(aura, rgba(window.sunAuraColor, 1.0F), weights[index]); }
        }
        return aura;
    }

    @Nullable private static TimeWindowDef first(@Nullable World world, String key) {
        DimensionDef def = ContentDimensions.of(world);
        if (def == null) { return null; }
        return def.windows.stream().filter(window -> window.gives(key)).findFirst().orElse(null);
    }

    private static float[] rgba(int rgb, float alpha) { return new float[] {Colors.red(rgb) / 255.0F, Colors.green(rgb) / 255.0F, Colors.blue(rgb) / 255.0F, alpha}; }

    private static void blend(float[] color, float[] aim, float weight) {
        for (int channel = 0; channel < color.length; channel++) { color[channel] += (aim[channel] - color[channel]) * weight; }
    }
}
