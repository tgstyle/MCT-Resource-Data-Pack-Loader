package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentTimeWindows;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public final class ContentWindowLook {
    private static final float EASE = 0.1F;
    private static final float AURA_ALPHA = 0.5F;
    private static final int WHITE = 0xFFFFFF;
    private static final float[] NO_WEIGHTS = new float[0];
    @Nullable private static DimensionDef held;
    private static float[] weights = NO_WEIGHTS;

    private ContentWindowLook() {}

    static void tick(@Nullable Level level) {
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

    @Nullable private static DimensionDef current(@Nullable Level level) { return held != null && ContentDimensions.def(level) == held ? held : null; }

    static float fogDensity(@Nullable Level level, float base) {
        DimensionDef def = current(level);
        if (def == null) { return base; }
        float density = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows().get(index);
            if (window.gives(TimeWindowDef.FOG_DENSITY)) { density += (window.fogDensity() - density) * weights[index]; }
        }
        return density;
    }

    static boolean colorsFog(@Nullable Level level) {
        DimensionDef def = current(level);
        if (def == null) { return false; }
        for (int index = 0; index < weights.length; index++) {
            if (weights[index] > 0.0F && def.windows().get(index).gives(TimeWindowDef.FOG_COLOR)) { return true; }
        }
        return false;
    }

    static Vec3 fogColor(@Nullable Level level, Vec3 base, Vec3 sampled, float sunAngle) {
        DimensionDef def = current(level);
        if (def == null) { return base; }
        float daylight = Mth.clamp(Mth.cos(sunAngle * Mth.DEG_TO_RAD) * 2.0F + 0.5F, 0.0F, 1.0F);
        Vec3 color = base;
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows().get(index);
            if (weights[index] <= 0.0F || !window.gives(TimeWindowDef.FOG_COLOR)) { continue; }
            Vec3 aim = window.sampleFog() ? sampled : lit(window.fogColor(), daylight);
            color = color.add(aim.subtract(color).scale(weights[index]));
        }
        return color;
    }

    private static Vec3 lit(int rgb, float daylight) { return new Vec3(ARGB.redFloat(rgb) * (daylight * 0.94F + 0.06F), ARGB.greenFloat(rgb) * (daylight * 0.94F + 0.06F), ARGB.blueFloat(rgb) * (daylight * 0.91F + 0.09F)); }

    @Nullable public static float[] sunTint(@Nullable Level level) {
        SkyLookDef look = ContentFogSampler.look(level);
        boolean based = look != null && look.sunColor() != SkyLookDef.UNSET;
        if (!based && first(level, TimeWindowDef.SUN_COLOR) == null) { return null; }
        float[] tint = rgba(based ? look.sunColor() : WHITE, 1.0F);
        blendWindows(level, tint, TimeWindowDef.SUN_COLOR, TimeWindowDef::sunColor);
        return tint;
    }

    @Nullable static float[] aura(@Nullable Level level, float alpha) {
        SkyLookDef look = ContentFogSampler.look(level);
        TimeWindowDef first = first(level, TimeWindowDef.SUN_AURA);
        boolean based = look != null && look.sunAuraColor() != SkyLookDef.UNSET;
        if (!based && first == null) { return null; }
        float[] aura = based ? rgba(look.sunAuraColor(), 1.0F) : rgba(first.sunAuraColor(), 0.0F);
        blendWindows(level, aura, TimeWindowDef.SUN_AURA, TimeWindowDef::sunAuraColor);
        aura[3] *= alpha * AURA_ALPHA;
        return aura;
    }

    @Nullable private static TimeWindowDef first(@Nullable Level level, String key) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null ? null : def.windows().stream().filter(window -> window.gives(key)).findFirst().orElse(null);
    }

    private static void blendWindows(@Nullable Level level, float[] color, String key, ToIntFunction<TimeWindowDef> read) {
        DimensionDef def = current(level);
        if (def == null) { return; }
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows().get(index);
            if (window.gives(key)) { blend(color, rgba(read.applyAsInt(window), 1.0F), weights[index]); }
        }
    }

    private static float[] rgba(int rgb, float alpha) { return new float[] {ARGB.redFloat(rgb), ARGB.greenFloat(rgb), ARGB.blueFloat(rgb), alpha}; }

    private static void blend(float[] color, float[] aim, float weight) {
        for (int channel = 0; channel < color.length; channel++) { color[channel] += (aim[channel] - color[channel]) * weight; }
    }
}
