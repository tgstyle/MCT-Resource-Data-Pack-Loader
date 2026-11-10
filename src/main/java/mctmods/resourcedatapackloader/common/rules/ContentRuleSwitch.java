package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RuleSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SnowRuleDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.util.dimension.IDayLength;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

public final class ContentRuleSwitch {
    private static final List<String> KEYS = List.of("dayLength", "weather", "weatherSpeed", "difficulty", "localDifficulty", "sleep", "wakeAt", "snow");
    private static final List<String> WHOLE = List.of("dayLength", "weather", "weatherSpeed", "difficulty", "wakeAt");
    private static final List<String> WEATHERS = List.of("clear", "rain", "thunder");
    private static final float HARDEST = 6.75F;

    private ContentRuleSwitch() {}

    @Nullable public static RuleSwitchDef parse(String owner, @Nullable JsonElement element, boolean local, boolean precipitation) {
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
        SnowRuleDef snow = RuleSwitchDef.NONE.snow();
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
                case "snow" -> {
                    SnowRuleDef held = snow(owner, value);
                    if (held != null) {
                        snow = held;
                        given = true;
                    }
                }
                default -> {
                    Boolean held = flag(owner, "rules." + key, value);
                    if (held != null) {
                        sleep = held;
                        given = true;
                    }
                }
            }
        }
        return given ? new RuleSwitchDef(dayLength, weather, weatherSpeed, difficulty, localDifficulty, sleep, wakeAt, snow) : null;
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

    @Nullable private static Boolean flag(String owner, String key, JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not true or false, ignoring it", owner, key, value);
        return null;
    }

    @Nullable private static SnowRuleDef snow(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rules.snow that is not an object of place, freeze, whenDry, melt, meltChance and meltBlocks keys, ignoring it", owner);
            return null;
        }
        int place = SnowRuleDef.NONE.place();
        int freeze = SnowRuleDef.NONE.freeze();
        boolean whenDry = SnowRuleDef.NONE.whenDry();
        boolean melt = SnowRuleDef.NONE.melt();
        float meltChance = SnowRuleDef.NONE.meltChance();
        Set<ResourceLocation> meltBlocks = SnowRuleDef.NONE.meltBlocks();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            JsonElement value = entry.getValue();
            switch (entry.getKey()) {
                case "place" -> place = snowMode(owner, "rules.snow.place", value);
                case "freeze" -> freeze = snowMode(owner, "rules.snow.freeze", value);
                case "whenDry" -> {
                    Boolean held = flag(owner, "rules.snow.whenDry", value);
                    if (held != null) { whenDry = held; }
                }
                case "melt" -> melt = melt(owner, value);
                case "meltChance" -> meltChance = meltChance(owner, value);
                case "meltBlocks" -> meltBlocks = meltBlocks(owner, value);
                default -> ContentLog.LOGGER.error("{} gives rules.snow.{}, which is not a snow key, ignoring it", owner, entry.getKey());
            }
        }
        return new SnowRuleDef(place, freeze, whenDry, melt, meltChance, meltBlocks);
    }

    private static int snowMode(String owner, String key, JsonElement value) {
        int mode = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? SnowRuleDef.MODES.indexOf(Settings.lower(value.getAsString())) : -1;
        if (mode >= 0) { return mode; }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not one of default, biome, any or never, ignoring it", owner, key, value);
        return SnowRuleDef.DEFAULT;
    }

    private static boolean melt(String owner, JsonElement value) {
        String name = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? Settings.lower(value.getAsString()) : "";
        if (name.equals("always")) { return true; }
        if (!name.equals("never")) { ContentLog.LOGGER.error("{} gives rules.snow.melt as {}, which is not never or always, ignoring it", owner, value); }
        return false;
    }

    private static float meltChance(String owner, JsonElement value) {
        float amount = ContentSpawnSwitch.amount(owner, "rules.snow.meltChance", value);
        if (amount < 0.0F) { return SnowRuleDef.MELT_CHANCE; }
        if (amount == 0.0F || amount > 1.0F) {
            ContentLog.LOGGER.error("{} gives rules.snow.meltChance as {}, which is not above 0 and at most 1, ignoring it", owner, value);
            return SnowRuleDef.MELT_CHANCE;
        }
        return amount;
    }

    private static Set<ResourceLocation> meltBlocks(String owner, JsonElement value) {
        if (!value.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives rules.snow.meltBlocks that is not a list of block ids, ignoring it", owner);
            return SnowRuleDef.MELT_BLOCKS;
        }
        Set<ResourceLocation> blocks = new HashSet<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            ResourceLocation id = entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() ? ResourceLocation.tryParse(Settings.lower(entry.getAsString().trim())) : null;
            if (id == null) {
                ContentLog.LOGGER.error("{} gives rules.snow.meltBlocks an entry {} that is not a block id, ignoring it", owner, entry);
                continue;
            }
            blocks.add(id);
        }
        return Set.copyOf(blocks);
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
