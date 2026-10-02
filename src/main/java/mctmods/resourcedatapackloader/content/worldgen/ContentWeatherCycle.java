package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private boolean raining;
    private int left;
    private int shift;
    private float target;

    public static void clear(Level level) {
        level.oRainLevel = 0.0F;
        level.rainLevel = 0.0F;
        level.oThunderLevel = 0.0F;
        level.thunderLevel = 0.0F;
    }

    public void tick(Level level, DimensionTraitsDef.Cycle cycle) {
        if (level.isClientSide) { return; }
        RandomSource random = level.random;
        if (--left <= 0) {
            raining = !raining;
            left = raining ? between(random, cycle.rainMin(), cycle.rainMax()) : between(random, cycle.clearMin(), cycle.clearMax());
        }
        if (--shift <= 0) {
            target = cycle.strength() * (0.25F + random.nextFloat() * 0.75F);
            shift = random.nextInt(200) + 100;
        }
        float strength = level.rainLevel;
        level.oRainLevel = strength;
        float aim = raining ? target : 0.0F;
        level.rainLevel = Mth.clamp(strength + Mth.clamp(aim - strength, -STEP, STEP), 0.0F, cycle.strength());
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = 0.0F;
    }

    private static int between(RandomSource random, int min, int max) { return max > min ? min + random.nextInt(max - min + 1) : min; }
}
