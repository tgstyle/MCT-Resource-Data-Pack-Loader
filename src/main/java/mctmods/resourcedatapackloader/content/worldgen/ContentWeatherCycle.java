package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;

public final class ContentWeatherCycle {
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

    public static void clear(Level level) {
        level.oRainLevel = 0.0F;
        level.rainLevel = 0.0F;
        level.oThunderLevel = 0.0F;
        level.thunderLevel = 0.0F;
    }

    public static boolean pour(Level level, ServerLevelData data, boolean skyLit) {
        if (skyLit && !(data instanceof DerivedLevelData)) {
            data.setClearWeatherTime(0);
            data.setRaining(true);
            if (data.getRainTime() < 2) { data.setRainTime(RAIN_HOLD); }
            return true;
        }
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Mth.clamp(level.rainLevel + RAIN_STEP, 0.0F, 1.0F);
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Mth.clamp(level.thunderLevel + (level.getLevelData().isThundering() ? THUNDER_STEP : -THUNDER_STEP), 0.0F, 1.0F);
        return false;
    }

    public static void dry(Level level) {
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Math.max(0.0F, level.rainLevel - RAIN_STEP);
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Math.max(0.0F, level.thunderLevel - THUNDER_STEP);
    }

    public static void force(Level level, int kind) {
        if (kind == RuleSwitchDef.CLEAR) {
            dry(level);
            return;
        }
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Mth.clamp(level.rainLevel + RAIN_STEP, 0.0F, 1.0F);
        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Mth.clamp(level.thunderLevel + (kind == RuleSwitchDef.THUNDER ? THUNDER_STEP : -THUNDER_STEP), 0.0F, 1.0F);
    }

    public static void hurry(ServerLevel level, ServerLevelData data, int steps) {
        if (steps == 1 || level.dimension() != Level.OVERWORLD || !data.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) { return; }
        data.setClearWeatherTime(hasten(data.getClearWeatherTime(), steps));
        data.setRainTime(hasten(data.getRainTime(), steps));
        data.setThunderTime(hasten(data.getThunderTime(), steps));
    }

    private static int hasten(int time, int steps) {
        if (time <= 0) { return time; }
        return steps == 0 ? time + 1 : Math.max(1, time - (steps - 1));
    }

    public void tick(Level level, DimensionTraitsDef.Cycle cycle, int steps) {
        if (level.isClientSide()) { return; }
        RandomSource random = level.random;
        if (!started) {
            started = true;
            left = Mth.randomBetweenInclusive(random, cycle.clearMin(), cycle.clearMax());
        }
        else if ((left -= steps) <= 0) {
            raining = !raining;
            left = raining ? Mth.randomBetweenInclusive(random, cycle.rainMin(), cycle.rainMax()) : Mth.randomBetweenInclusive(random, cycle.clearMin(), cycle.clearMax());
        }
        if (cycle.thunderMax() <= 0) { thundering = false; }
        else if ((thunderLeft -= steps) <= 0) {
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
