package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.interfaces.IDayLength;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentRuleSwitch {
    private static final List<String> KEYS = List.of("dayLength", "weather", "weatherSpeed", "difficulty", "localDifficulty", "sleep", "wakeAt");
    private static final List<String> WHOLE = List.of("dayLength", "weather", "weatherSpeed", "difficulty", "wakeAt");
    private static final List<String> WEATHERS = List.of("clear", "rain", "thunder");
    private static final float HARDEST = 6.75F;

    private ContentRuleSwitch() {}

    @Nullable static RuleSwitchDef parse(String owner, @Nullable JsonElement element, boolean local, boolean precipitation) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rules that is not an object, ignoring it", owner);
            return null;
        }
        long dayLength = RuleSwitchDef.NONE.dayLength();
        int weather = RuleSwitchDef.NONE.weather();
        float weatherSpeed = RuleSwitchDef.NONE.weatherSpeed();
        Difficulty difficulty = RuleSwitchDef.NONE.difficulty();
        float localDifficulty = RuleSwitchDef.NONE.localDifficulty();
        boolean sleep = RuleSwitchDef.NONE.sleep();
        int wakeAt = RuleSwitchDef.NONE.wakeAt();
        boolean given = false;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!KEYS.contains(key)) {
                ContentLog.LOGGER.error("{} gives rules.{}, which is not a rules key, ignoring it", owner, key);
                continue;
            }
            if (local && WHOLE.contains(key)) {
                ContentLog.LOGGER.error("{} gives rules.{}, which covers a whole dimension, but the environment covers only some biomes; ignoring it", owner, key);
                continue;
            }
            switch (key) {
                case "dayLength" -> {
                    long held = whole(owner, key, value, 1L, Integer.MAX_VALUE);
                    if (held >= 0L) {
                        dayLength = held;
                        given = true;
                    }
                }
                case "wakeAt" -> {
                    long held = whole(owner, key, value, 0L, DimensionTraitsDef.VANILLA_DAY - 1L);
                    if (held >= 0L) {
                        wakeAt = (int) held;
                        given = true;
                    }
                }
                case "weather" -> {
                    int held = weatherKind(owner, value, precipitation);
                    if (held != RuleSwitchDef.UNSET) {
                        weather = held;
                        given = true;
                    }
                }
                case "weatherSpeed" -> {
                    float amount = ContentSpawnSwitch.amount(owner, "rules." + key, value);
                    if (amount > ContentGrowSwitch.MOST) { ContentLog.LOGGER.error("{} gives rules.{} as {}, which is above {}, ignoring it", owner, key, amount, ContentGrowSwitch.MOST); }
                    else if (amount >= 0.0F) {
                        weatherSpeed = amount;
                        given = true;
                    }
                }
                case "difficulty" -> {
                    Difficulty held = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? ContentServer.difficultyFrom(value.getAsString()) : null;
                    if (held == null) { ContentLog.LOGGER.error("{} gives rules.{} as {}, which is not one of peaceful, easy, normal or hard, ignoring it", owner, key, value); }
                    else {
                        difficulty = held;
                        given = true;
                    }
                }
                case "localDifficulty" -> {
                    float amount = ContentSpawnSwitch.amount(owner, "rules." + key, value);
                    if (amount > HARDEST) { ContentLog.LOGGER.error("{} gives rules.{} as {}, which is above {}, ignoring it", owner, key, amount, HARDEST); }
                    else if (amount >= 0.0F) {
                        localDifficulty = amount;
                        given = true;
                    }
                }
                default -> {
                    if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                        sleep = value.getAsBoolean();
                        given = true;
                    }
                    else { ContentLog.LOGGER.error("{} gives rules.{} as {}, which is not true or false, ignoring it", owner, key, value); }
                }
            }
        }
        return given ? new RuleSwitchDef(dayLength, weather, weatherSpeed, difficulty, localDifficulty, sleep, wakeAt) : null;
    }

    private static long whole(String owner, String key, JsonElement value, long low, long high) {
        float amount = ContentSpawnSwitch.amount(owner, "rules." + key, value);
        if (amount < 0.0F) { return -1L; }
        if (amount != Math.floor(amount) || amount < low || amount > high) {
            ContentLog.LOGGER.error("{} gives rules.{} as {}, which is not a whole number from {} to {}, ignoring it", owner, key, value, low, high);
            return -1L;
        }
        return (long) amount;
    }

    private static int weatherKind(String owner, JsonElement value, boolean precipitation) {
        int kind = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? WEATHERS.indexOf(Settings.lower(value.getAsString())) : -1;
        if (kind < 0) {
            ContentLog.LOGGER.error("{} gives rules.weather as {}, which is not clear, rain or thunder, ignoring it", owner, value);
            return RuleSwitchDef.UNSET;
        }
        if (kind != RuleSwitchDef.CLEAR && !precipitation) {
            ContentLog.LOGGER.error("{} gives rules.weather as {}, but the dimension has no precipitation, ignoring it", owner, value);
            return RuleSwitchDef.UNSET;
        }
        return kind;
    }

    private static RuleSwitchDef wide(Level level) { return ContentEnvironments.wide(level.dimension().location()); }

    public static long dayLength(Level level) {
        long ruled = wide(level).dayLength();
        return ruled > 0L && level.dimensionType().fixedTime().isEmpty() ? ruled : ContentWeather.traits(level).dayLength();
    }

    public static int weather(Level level) { return wide(level).weather(); }

    public static int speed(ServerLevel level) { return ContentGrowSwitch.roll(wide(level).weatherSpeed(), level.getRandom()); }

    @Nullable public static Difficulty difficulty(Level level) { return wide(level).difficulty(); }

    public static DifficultyInstance local(ServerLevel level, BlockPos pos, DifficultyInstance found) {
        float ruled = ContentEnvironments.rules(level, pos).localDifficulty();
        if (ruled < 0.0F || found.getDifficulty() == Difficulty.PEACEFUL) { return found; }
        DifficultyInstance made = new DifficultyInstance(found.getDifficulty(), 0L, 0L, 0.0F);
        made.effectiveDifficulty = ruled;
        return made;
    }

    static long woken(ServerLevel level, long vanilla) {
        RuleSwitchDef rules = wide(level);
        if (rules.dayLength() <= 0L && rules.wakeAt() < 0) { return vanilla; }
        long length = dayLength(level);
        long time = level.getDayTime();
        long target = time - Math.floorMod(time, length) + Math.max(rules.wakeAt(), 0) * length / DimensionTraitsDef.VANILLA_DAY;
        return target > time ? target : target + length;
    }

    public static void settle(Level level) { ((IDayLength) (Object) level.dimensionType()).rdpl$dayLength(dayLength(level)); }

    public static void onCanSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() == null && event.getLevel() instanceof ServerLevel level && !ContentEnvironments.rules(level, event.getPos()).sleep()) { event.setProblem(Player.BedSleepingProblem.NOT_POSSIBLE_NOW); }
    }

    public static void onContinueSleep(CanContinueSleepingEvent event) {
        if (!(event.getEntity() instanceof Player sleeper)) { return; }
        if (sleeper.level() instanceof ServerLevel level && !ContentEnvironments.rules(level, sleeper.getSleepingPos().orElse(sleeper.blockPosition())).sleep()) { event.setContinueSleeping(false); }
    }

    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) { event.setTimeAddition(woken(level, event.getNewTime())); }
    }
}
