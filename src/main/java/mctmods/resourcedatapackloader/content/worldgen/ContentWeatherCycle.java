package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import java.util.Random;

final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private static final float THUNDER_STEP = 0.01F;
    private static final float RAIN_STEP = 0.01F;
    private static final int RAIN_HOLD = 12000;
    private boolean started;
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

    static void pour(World world) {
        if (world.isRemote) { return; }
        world.getWorldInfo().setRaining(true);
        if (world.getWorldInfo().getRainTime() < 2) { world.getWorldInfo().setRainTime(RAIN_HOLD); }
        if (!world.provider.hasSkyLight()) { world.prevRainingStrength = world.rainingStrength; }
        world.rainingStrength = Math.max(world.rainingStrength, MathHelper.clamp(world.prevRainingStrength + RAIN_STEP, 0.0F, 1.0F));
    }

    static void dry(World world) {
        if (world.isRemote) { return; }
        world.prevRainingStrength = world.rainingStrength;
        world.rainingStrength = Math.max(0.0F, world.rainingStrength - RAIN_STEP);
        world.prevThunderingStrength = world.thunderingStrength;
        world.thunderingStrength = Math.max(0.0F, world.thunderingStrength - THUNDER_STEP);
    }

    static void force(World world, int kind) {
        if (world.isRemote) { return; }
        if (kind == RuleSwitchDef.CLEAR) {
            dry(world);
            return;
        }
        world.prevRainingStrength = world.rainingStrength;
        world.rainingStrength = MathHelper.clamp(world.rainingStrength + RAIN_STEP, 0.0F, 1.0F);
        world.prevThunderingStrength = world.thunderingStrength;
        world.thunderingStrength = MathHelper.clamp(world.thunderingStrength + (kind == RuleSwitchDef.THUNDER ? THUNDER_STEP : -THUNDER_STEP), 0.0F, 1.0F);
    }

    static void hurry(World world, int steps) {
        if (world.isRemote || steps == 1 || world.provider.getDimension() != 0 || !world.getGameRules().getBoolean("doWeatherCycle")) { return; }
        WorldInfo info = world.getWorldInfo();
        info.setCleanWeatherTime(shifted(info.getCleanWeatherTime(), steps));
        info.setRainTime(shifted(info.getRainTime(), steps));
        info.setThunderTime(shifted(info.getThunderTime(), steps));
    }

    private static int shifted(int time, int steps) {
        if (time <= 0) { return time; }
        return steps == 0 ? time + 1 : Math.max(1, time - (steps - 1));
    }

    void tick(World world, DimensionTraitsDef.Cycle cycle, int steps) {
        if (world.isRemote) { return; }
        Random random = world.rand;
        if (!started) {
            started = true;
            left = MathHelper.getInt(random,cycle.clearMin, cycle.clearMax);
        }
        else if ((left -= steps) <= 0) {
            raining = !raining;
            left = raining ? MathHelper.getInt(random,cycle.rainMin, cycle.rainMax) : MathHelper.getInt(random,cycle.clearMin, cycle.clearMax);
        }
        if (cycle.thunderMax <= 0) { thundering = false; }
        else if ((thunderLeft -= steps) <= 0) {
            thundering = !thundering;
            thunderLeft = thundering ? MathHelper.getInt(random,cycle.thunderMin, cycle.thunderMax) : MathHelper.getInt(random,cycle.calmMin, cycle.calmMax);
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
}
