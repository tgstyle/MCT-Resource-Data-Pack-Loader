package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;

final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private static final float THUNDER_STEP = 0.01F;
    private boolean raining;
    private boolean thundering;
    private int left;
    private int thunderLeft;
    private int shift;
    private float target;

    static void clear(World world) {
        world.getWorldInfo().setRainTime(0);
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setThunderTime(0);
        world.getWorldInfo().setThundering(false);
        world.prevRainingStrength = 0.0F;
        world.rainingStrength = 0.0F;
        world.prevThunderingStrength = 0.0F;
        world.thunderingStrength = 0.0F;
    }

    void tick(World world, DimensionTraitsDef.Cycle cycle) {
        if (world.isRemote) { return; }
        Random random = world.rand;
        if (--left <= 0) {
            raining = !raining;
            left = raining ? between(random, cycle.rainMin, cycle.rainMax) : between(random, cycle.clearMin, cycle.clearMax);
        }
        if (cycle.thunderMax <= 0) { thundering = false; }
        else if (--thunderLeft <= 0) {
            thundering = !thundering;
            thunderLeft = thundering ? between(random, cycle.thunderMin, cycle.thunderMax) : between(random, cycle.calmMin, cycle.calmMax);
        }
        if (--shift <= 0) {
            target = cycle.strength * (0.25F + random.nextFloat() * 0.75F);
            shift = random.nextInt(200) + 100;
        }
        boolean storming = raining && thundering;
        float strength = world.rainingStrength;
        world.prevRainingStrength = strength;
        float aim = storming ? 1.0F : raining ? target : 0.0F;
        world.rainingStrength = MathHelper.clamp(strength + MathHelper.clamp(aim - strength, -STEP, STEP), 0.0F, 1.0F);
        float thunder = world.thunderingStrength;
        world.prevThunderingStrength = thunder;
        world.thunderingStrength = MathHelper.clamp(thunder + (storming ? THUNDER_STEP : -THUNDER_STEP), 0.0F, cycle.thunderStrength);
    }

    private static int between(Random random, int min, int max) { return max > min ? min + random.nextInt(max - min + 1) : min; }
}
