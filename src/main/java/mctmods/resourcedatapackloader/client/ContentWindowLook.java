package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentTimeWindows;
import mctmods.resourcedatapackloader.util.Rgb;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

final class ContentWindowLook {
    private static final float EASE = 0.1F;
    private static final float[] NO_WEIGHTS = new float[0];
    @Nullable private static DimensionDef held;
    private static float[] weights = NO_WEIGHTS;

    private ContentWindowLook() {}

    static void tick(@Nullable ClientLevel level) {
        DimensionDef def = ContentDimensions.def(level);
        if (level == null || def == null || def.windows().isEmpty()) {
            held = null;
            weights = NO_WEIGHTS;
            return;
        }
        TimeWindowDef active = ContentTimeWindows.active(level, def);
        boolean arrived = def != held;
        if (arrived) {
            held = def;
            weights = new float[def.windows().size()];
        }
        for (int index = 0; index < weights.length; index++) {
            float aim = def.windows().get(index) == active ? 1.0F : 0.0F;
            weights[index] = arrived ? aim : weights[index] + (aim - weights[index]) * EASE;
        }
    }

    static boolean samples(DimensionDef def) { return def.windows().stream().anyMatch(TimeWindowDef::sampleFog); }

    @Nullable private static DimensionDef current(@Nullable DimensionDef def) { return held != null && def == held ? held : null; }

    static float fogDensity(@Nullable DimensionDef def, float base) {
        DimensionDef own = current(def);
        if (own == null) { return base; }
        float density = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = own.windows().get(index);
            if (window.gives(TimeWindowDef.FOG_DENSITY)) { density += (window.fogDensity() - density) * weights[index]; }
        }
        return density;
    }

    static Vec3 fogColor(@Nullable DimensionDef def, Vec3 base, float brightness) {
        DimensionDef own = current(def);
        if (own == null) { return base; }
        Vec3 color = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = own.windows().get(index);
            if (weights[index] <= 0.0F || !window.gives(TimeWindowDef.FOG_COLOR)) { continue; }
            Vec3 aim = window.sampleFog() ? ContentFogSampler.color(base) : ContentDimensionEffects.litFog(window.fogColor(), brightness);
            color = color.add(aim.subtract(color).scale(weights[index]));
        }
        return color;
    }

    @Nullable static float[] sunTint(@Nullable DimensionDef def) {
        SkyLookDef look = def == null ? null : def.look();
        boolean windowed = def != null && def.windows().stream().anyMatch(window -> window.gives(TimeWindowDef.SUN_COLOR));
        if ((look == null || look.sunColor() == SkyLookDef.UNSET) && !windowed) { return null; }
        float[] tint = look == null || look.sunColor() == SkyLookDef.UNSET ? new float[] {1.0F, 1.0F, 1.0F, 1.0F} : rgba(look.sunColor(), 1.0F);
        DimensionDef own = current(def);
        if (own == null) { return tint; }
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = own.windows().get(index);
            if (window.gives(TimeWindowDef.SUN_COLOR)) { blend(tint, rgba(window.sunColor(), 1.0F), weights[index]); }
        }
        return tint;
    }

    @Nullable static float[] aura(@Nullable DimensionDef def) {
        SkyLookDef look = def == null ? null : def.look();
        boolean based = look != null && look.sunAuraColor() != SkyLookDef.UNSET;
        TimeWindowDef first = def == null ? null : def.windows().stream().filter(window -> window.gives(TimeWindowDef.SUN_AURA)).findFirst().orElse(null);
        if (!based && first == null) { return null; }
        float[] aura = based ? rgba(look.sunAuraColor(), 1.0F) : rgba(first.sunAuraColor(), 0.0F);
        DimensionDef own = current(def);
        if (own == null) { return aura; }
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = own.windows().get(index);
            if (window.gives(TimeWindowDef.SUN_AURA)) { blend(aura, rgba(window.sunAuraColor(), 1.0F), weights[index]); }
        }
        return aura;
    }

    private static float[] rgba(int rgb, float alpha) { return new float[] {Rgb.red(rgb), Rgb.green(rgb), Rgb.blue(rgb), alpha}; }

    private static void blend(float[] color, float[] aim, float weight) {
        for (int channel = 0; channel < color.length; channel++) { color[channel] += (aim[channel] - color[channel]) * weight; }
    }
}
