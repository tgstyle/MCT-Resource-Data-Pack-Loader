package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private static final float THUNDER_STEP = 0.01F;
    private boolean raining;
    private boolean thundering;
    private int left;
    private int thunderLeft;
    private int shift;
    private float target;

    public static void clear(Level level) {
        level.oRainLevel = 0.0F;
        level.rainLevel = 0.0F;
        level.oThunderLevel = 0.0F;
        level.thunderLevel = 0.0F;
    }

    public void tick(Level level, DimensionTraitsDef.Cycle cycle) {
        if (level.isClientSide()) { return; }
        RandomSource random = level.getRandom();
        if (--left <= 0) {
            raining = !raining;
            left = raining ? between(random, cycle.rainMin(), cycle.rainMax()) : between(random, cycle.clearMin(), cycle.clearMax());
        }
        if (cycle.thunderMax() <= 0) { thundering = false; }
        else if (--thunderLeft <= 0) {
            thundering = !thundering;
            thunderLeft = thundering ? between(random, cycle.thunderMin(), cycle.thunderMax()) : between(random, cycle.calmMin(), cycle.calmMax());
        }
        if (--shift <= 0) {
            target = cycle.strength() * (0.25F + random.nextFloat() * 0.75F);
            shift = random.nextInt(200) + 100;
        }
        boolean storming = raining && thundering;
        float strength = level.rainLevel;
        level.oRainLevel = strength;
        float aim = storming ? 1.0F : raining ? target : 0.0F;
        level.rainLevel = Mth.clamp(strength + Mth.clamp(aim - strength, -STEP, STEP), 0.0F, 1.0F);
        float thunder = level.thunderLevel;
        level.oThunderLevel = thunder;
        level.thunderLevel = Mth.clamp(thunder + (storming ? THUNDER_STEP : -THUNDER_STEP), 0.0F, cycle.thunderStrength());
    }

    private static int between(RandomSource random, int min, int max) { return max > min ? min + random.nextInt(max - min + 1) : min; }
}
