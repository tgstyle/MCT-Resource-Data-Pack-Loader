package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;

final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private boolean raining;
    private int left;
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
        if (--shift <= 0) {
            target = cycle.strength * (0.25F + random.nextFloat() * 0.75F);
            shift = random.nextInt(200) + 100;
        }
        float strength = world.rainingStrength;
        world.prevRainingStrength = strength;
        float aim = raining ? target : 0.0F;
        world.rainingStrength = MathHelper.clamp(strength + MathHelper.clamp(aim - strength, -STEP, STEP), 0.0F, cycle.strength);
        world.prevThunderingStrength = world.thunderingStrength;
        world.thunderingStrength = 0.0F;
    }

    private static int between(Random random, int min, int max) { return max > min ? min + random.nextInt(max - min + 1) : min; }
}
