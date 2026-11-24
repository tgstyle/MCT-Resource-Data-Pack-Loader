package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.environment.ContentTimeWindows;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.core.util.math.Colors;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentWindowLook {
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
            weights[index] = arrived ? aim : MathUtil.lerp(EASE, weights[index], aim);
        }
    }

    static boolean samples(DimensionDef def) { return def.windows.stream().anyMatch(window -> window.sampleFog); }

    @Nullable private static DimensionDef current(@Nullable World world) { return held != null && ContentDimensions.of(world) == held ? held : null; }

    static float fogDensity(@Nullable World world, float base) {
        DimensionDef def = current(world);
        if (def == null) { return base; }
        float[] density = {base};
        each(def, TimeWindowDef.FOG_DENSITY, (window, weight) -> density[0] = MathUtil.lerp(weight, density[0], window.fogDensity));
        return density[0];
    }

    public static Vec3d fogColor(@Nullable World world, Vec3d base, float celestialAngle) {
        DimensionDef def = current(world);
        if (def == null) { return base; }
        Vec3d[] color = {base};
        each(def, TimeWindowDef.FOG_COLOR, (window, weight) -> {
            if (weight <= 0.0F) { return; }
            Vec3d aim = window.sampleFog ? ContentFogSampler.color(base) : ContentDimensionRules.fogColor(window.fogColor, celestialAngle);
            color[0] = color[0].add(aim.subtract(color[0]).scale(weight));
        });
        return color[0];
    }

    public static float colorFog(@Nullable World world) {
        DimensionDef def = current(world);
        if (def == null) { return 0.0F; }
        float[] lit = {0.0F};
        each(def, TimeWindowDef.FOG_COLOR, (window, weight) -> {
            if (!window.sampleFog) { lit[0] = MathUtil.lerp(weight, lit[0], 1.0F); }
        });
        return lit[0];
    }

    @Nullable static float[] sunTint(@Nullable World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        TimeWindowDef first = first(world, TimeWindowDef.SUN_COLOR);
        if ((look == null || look.sunColor == SkyLookDef.UNSET) && first == null) { return null; }
        float[] tint = look == null || look.sunColor == SkyLookDef.UNSET ? new float[] {1.0F, 1.0F, 1.0F, 1.0F} : Colors.unit(look.sunColor, 1.0F);
        DimensionDef def = current(world);
        if (def == null) { return tint; }
        each(def, TimeWindowDef.SUN_COLOR, (window, weight) -> blend(tint, Colors.unit(window.sunColor, 1.0F), weight));
        return tint;
    }

    @Nullable static float[] aura(@Nullable World world, float height) {
        SkyLookDef look = ContentFogSampler.look(world);
        TimeWindowDef first = first(world, TimeWindowDef.SUN_AURA);
        if ((look == null || look.sunAuraColor == SkyLookDef.UNSET) && first == null) { return null; }
        float[] aura = look == null || look.sunAuraColor == SkyLookDef.UNSET ? Colors.unit(first.sunAuraColor, 0.0F) : Colors.unit(look.sunAuraColor, 1.0F);
        DimensionDef def = current(world);
        if (def != null) { each(def, TimeWindowDef.SUN_AURA, (window, weight) -> blend(aura, Colors.unit(window.sunAuraColor, 1.0F), weight)); }
        if (look != null && look.sunAuraHorizon > 0.0F) { aura[3] *= 1.0F - MathHelper.clamp(Math.abs((float) Math.toDegrees(MathUtil.asinClamped(height))) / look.sunAuraHorizon, 0.0F, 1.0F); }
        return aura;
    }

    @Nullable private static TimeWindowDef first(@Nullable World world, String key) {
        DimensionDef def = ContentDimensions.of(world);
        if (def == null) { return null; }
        return def.windows.stream().filter(window -> window.gives(key)).findFirst().orElse(null);
    }

    private static void each(DimensionDef def, String key, WindowStep step) {
        for (int index = 0; index < weights.length; index++) {
            TimeWindowDef window = def.windows.get(index);
            if (window.gives(key)) { step.apply(window, weights[index]); }
        }
    }

    private interface WindowStep { void apply(TimeWindowDef window, float weight); }

    private static void blend(float[] color, float[] aim, float weight) {
        for (int channel = 0; channel < color.length; channel++) { color[channel] = MathUtil.lerp(weight, color[channel], aim[channel]); }
    }
}
