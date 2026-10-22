package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IDifficultyInstance;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import net.neoforged.neoforge.common.util.ClockAdjustment;
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
    private static final int LAST_TICK = 23999;

    private ContentRuleSwitch() {}

    @Nullable static RuleSwitchDef parse(String owner, JsonElement element, boolean local, boolean precipitation) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rules that is not an object, ignoring it", owner);
            return null;
        }
        long dayLength = RuleSwitchDef.NONE.dayLength();
        int weather = RuleSwitchDef.UNSET;
        float speed = RuleSwitchDef.NONE.weatherSpeed();
        Difficulty difficulty = null;
        float localDifficulty = RuleSwitchDef.NONE.localDifficulty();
        boolean sleep = true;
        int wakeAt = RuleSwitchDef.NONE.wakeAt();
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
                    Double found = number(owner, key, value, 1.0D, Integer.MAX_VALUE, true, "a whole number of 1 or more");
                    if (found != null) { dayLength = found.longValue(); }
                }
                case "weather" -> {
                    int kind = WEATHERS.indexOf(text(value));
                    if (kind < 0) { ContentLog.LOGGER.error("{} gives rules.weather that is not clear, rain or thunder, ignoring it", owner); }
                    else if (kind != RuleSwitchDef.CLEAR && !precipitation) { ContentLog.LOGGER.error("{} gives rules.weather {}, but the dimension has no precipitation, ignoring it", owner, WEATHERS.get(kind)); }
                    else { weather = kind; }
                }
                case "weatherSpeed" -> {
                    Double found = number(owner, key, value, 0.0D, ContentGrowSwitch.MOST, false, "a number from 0 to 100");
                    if (found != null) { speed = found.floatValue(); }
                }
                case "difficulty" -> {
                    Difficulty named = ContentServer.difficultyFrom(text(value));
                    if (named == null) { ContentLog.LOGGER.error("{} gives rules.difficulty that is not one of peaceful, easy, normal or hard, ignoring it", owner); }
                    else { difficulty = named; }
                }
                case "localDifficulty" -> {
                    Double found = number(owner, key, value, 0.0D, HARDEST, false, "a number from 0 to 6.75");
                    if (found != null) { localDifficulty = found.floatValue(); }
                }
                case "sleep" -> {
                    if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { sleep = value.getAsBoolean(); }
                    else { ContentLog.LOGGER.error("{} gives rules.sleep that is not true or false, ignoring it", owner); }
                }
                default -> {
                    Double found = number(owner, key, value, 0.0D, LAST_TICK, true, "a whole number from 0 to 23999");
                    if (found != null) { wakeAt = found.intValue(); }
                }
            }
        }
        return new RuleSwitchDef(dayLength, weather, speed, difficulty, localDifficulty, sleep, wakeAt);
    }

    private static String text(JsonElement value) { return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? Settings.lower(value.getAsString()) : ""; }

    @Nullable private static Double number(String owner, String key, JsonElement value, double least, double most, boolean whole, String need) {
        Double found = ContentSpawnSwitch.number(value);
        if (found == null || found < least || found > most || whole && found != Math.rint(found)) {
            ContentLog.LOGGER.error("{} gives rules.{} that is not {}, ignoring it", owner, key, need);
            return null;
        }
        return found;
    }

    private static long ruledLength(Level level, @Nullable DimensionDef def) {
        long length = ContentEnvironments.wide(level.dimension().identifier()).dayLength();
        return length > 0L && (def == null || def.fixedTime() < 0L) ? length : -1L;
    }

    public static long dayLength(Level level, @Nullable DimensionDef def) {
        long ruled = ruledLength(level, def);
        if (ruled > 0L) { return ruled; }
        return def == null ? DimensionTraitsDef.VANILLA_DAY : def.traits().dayLength();
    }

    static long stretch(Level level, long ticks, long period) {
        long length = ruledLength(level, ContentDimensions.def(level));
        if (length < 0L || length == period) { return ticks; }
        return Math.floorDiv(ticks, length) * period + Math.floorMod(ticks, length) * period / length;
    }

    static float scale(Level level, long period) {
        long length = ruledLength(level, ContentDimensions.def(level));
        return length < 0L ? 1.0F : (float) period / length;
    }

    public static ClockManager dayClock(ClockManager clocks, Holder<Timeline> timeline) {
        if (!(clocks instanceof LevelClock(Level wrappedLevel, ClockManager wrappedInner))) { return clocks; }
        boolean day = timeline.unwrapKey().map(key -> key.identifier().getPath()).filter(path -> "day".equals(path) || path.endsWith("/day")).isPresent();
        return day ? new DayClock(wrappedLevel, wrappedInner, timeline.value().periodTicks().orElse((int) DimensionTraitsDef.VANILLA_DAY)) : wrappedInner;
    }

    public static boolean forces(Level level) { return ContentEnvironments.wide(level.dimension().identifier()).weather() != RuleSwitchDef.UNSET; }

    public static int weather(Level level) { return ContentEnvironments.wide(level.dimension().identifier()).weather(); }

    public static int steps(Level level) { return ContentGrowSwitch.roll(ContentEnvironments.wide(level.dimension().identifier()).weatherSpeed(), level.getRandom()); }

    @Nullable public static Difficulty difficulty(Level level) { return ContentEnvironments.wide(level.dimension().identifier()).difficulty(); }

    public static DifficultyInstance local(ServerLevel level, BlockPos pos, DifficultyInstance found) {
        float ruled = ContentEnvironments.rules(level, pos).localDifficulty();
        if (ruled < 0.0F || found.getDifficulty() == Difficulty.PEACEFUL) { return found; }
        DifficultyInstance made = new DifficultyInstance(found.getDifficulty(), 0L, 0L, 0.0F);
        ((IDifficultyInstance) made).rdpl$effective(ruled);
        return made;
    }

    private static boolean blocksSleep(ServerLevel level, BlockPos pos) { return !ContentEnvironments.rules(level, pos).sleep(); }

    public static void onCanSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() == null && blocksSleep(event.getEntity().level(), event.getPos())) { event.setProblem(new Player.BedSleepingProblem(Component.translatable("block.minecraft.bed.no_sleep"))); }
    }

    public static void onContinueSleep(CanContinueSleepingEvent event) {
        if (!(event.getEntity() instanceof Player sleeper)) { return; }
        if (sleeper.level() instanceof ServerLevel level && blocksSleep(level, sleeper.getSleepingPos().orElse(sleeper.blockPosition()))) { event.setContinueSleeping(false); }
    }

    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        int wakeAt = ContentEnvironments.wide(level.dimension().identifier()).wakeAt();
        DimensionDef def = ContentDimensions.def(level);
        if (wakeAt < 0 && ruledLength(level, def) < 0L) { return; }
        long length = dayLength(level, def);
        long now = level.getDefaultClockTime();
        long target = now - Math.floorMod(now, length) + Math.max(wakeAt, 0) * length / DimensionTraitsDef.VANILLA_DAY;
        if (target <= now) { target += length; }
        event.setAdjustment(new ClockAdjustment.Absolute(target));
    }

    public static void settle(Level level) { level.environmentAttributes().invalidateTickCache(); }
}
