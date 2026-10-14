package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.WeatherData;

public final class ContentWeatherCycle {
    private static final float STEP = 0.004F;
    private static final float THUNDER_STEP = 0.01F;
    private static final int RAIN_HOLD = 12000;
    private boolean started;
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

    public static boolean pour(ServerLevel level, boolean skyLit) {
        if (skyLit && level.dimension() == Level.OVERWORLD) {
            WeatherData data = level.getWeatherData();
            data.setClearWeatherTime(0);
            data.setRaining(true);
            if (data.getRainTime() < 2) { data.setRainTime(RAIN_HOLD); }
            return true;
        }
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Mth.clamp(level.rainLevel + THUNDER_STEP, 0.0F, 1.0F);
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Mth.clamp(level.thunderLevel + (level.getWeatherData().isThundering() ? THUNDER_STEP : -THUNDER_STEP), 0.0F, 1.0F);
        return false;
    }

    public static void dry(Level level) {
        if (level.isClientSide()) { return; }
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Math.max(0.0F, level.rainLevel - THUNDER_STEP);
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Math.max(0.0F, level.thunderLevel - THUNDER_STEP);
    }

    public void tick(Level level, DimensionTraitsDef.Cycle cycle) {
        if (level.isClientSide()) { return; }
        RandomSource random = level.getRandom();
        if (!started) {
            started = true;
            left = Mth.randomBetweenInclusive(random, cycle.clearMin(), cycle.clearMax());
        }
        else if (--left <= 0) {
            raining = !raining;
            left = raining ? Mth.randomBetweenInclusive(random, cycle.rainMin(), cycle.rainMax()) : Mth.randomBetweenInclusive(random, cycle.clearMin(), cycle.clearMax());
        }
        if (cycle.thunderMax() <= 0) { thundering = false; }
        else if (--thunderLeft <= 0) {
            thundering = !thundering;
            thunderLeft = thundering ? Mth.randomBetweenInclusive(random, cycle.thunderMin(), cycle.thunderMax()) : Mth.randomBetweenInclusive(random, cycle.calmMin(), cycle.calmMax());
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
}
