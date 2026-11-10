package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.dimension.ContentWorldProvider;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RuleSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SnowRuleDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentRuleSwitch {
    private static final List<String> KEYS = Arrays.asList("dayLength", "weather", "weatherSpeed", "difficulty", "localDifficulty", "sleep", "wakeAt", "snow");
    private static final List<String> WHOLE = Arrays.asList("dayLength", "weather", "weatherSpeed", "difficulty", "wakeAt");
    private static final float HARDEST = 6.75F;
    private static final int LAST_TICK = 23999;

    private ContentRuleSwitch() {}

    @Nullable public static RuleSwitchDef parse(String owner, @Nullable JsonElement element, boolean local, boolean precipitation) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rules that is not an object of dayLength, weather, weatherSpeed, difficulty, localDifficulty, sleep, wakeAt and snow keys, ignoring it", owner);
            return null;
        }
        long dayLength = -1L;
        int weather = RuleSwitchDef.UNSET;
        float speed = 1.0F;
        EnumDifficulty difficulty = null;
        float localDifficulty = -1.0F;
        boolean sleep = true;
        int wakeAt = -1;
        SnowRuleDef snow = SnowRuleDef.NONE;
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
                case "dayLength": dayLength = length(owner, value); break;
                case "weather": weather = weather(owner, value, precipitation); break;
                case "weatherSpeed": speed = speed(owner, value); break;
                case "difficulty": difficulty = difficulty(owner, value); break;
                case "localDifficulty": localDifficulty = hardness(owner, value); break;
                case "sleep": sleep = flag(owner, "rules.sleep", value, true); break;
                case "snow": snow = snow(owner, value); break;
                default: wakeAt = wakeAt(owner, value); break;
            }
        }
        return new RuleSwitchDef(dayLength, weather, speed, difficulty, localDifficulty, sleep, wakeAt, snow);
    }

    private static long length(String owner, JsonElement value) {
        Float number = ContentSpawnSwitch.number(owner, "rules.dayLength", value, true);
        if (number == null) { return -1L; }
        if (number < 1.0F) {
            ContentLog.LOGGER.error("{} gives rules.dayLength as {}, which is not above zero, ignoring it", owner, value);
            return -1L;
        }
        return number.longValue();
    }

    private static int weather(String owner, JsonElement value, boolean precipitation) {
        String name = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? Settings.lower(value.getAsString()) : "";
        int kind = name.equals("clear") ? RuleSwitchDef.CLEAR : name.equals("rain") ? RuleSwitchDef.RAIN : name.equals("thunder") ? RuleSwitchDef.THUNDER : RuleSwitchDef.UNSET;
        if (kind == RuleSwitchDef.UNSET) { ContentLog.LOGGER.error("{} gives rules.weather as {}, which is not one of clear, rain or thunder, ignoring it", owner, value); }
        else if (kind != RuleSwitchDef.CLEAR && !precipitation) {
            ContentLog.LOGGER.error("{} gives rules.weather as {}, but the dimension has precipitation off, ignoring it", owner, value);
            return RuleSwitchDef.UNSET;
        }
        return kind;
    }

    private static float speed(String owner, JsonElement value) {
        Float number = ContentSpawnSwitch.number(owner, "rules.weatherSpeed", value, false);
        if (number == null) { return 1.0F; }
        if (number > ContentGrowSwitch.MOST) {
            ContentLog.LOGGER.error("{} gives rules.weatherSpeed as {}, which is above {}, ignoring it", owner, value, (int) ContentGrowSwitch.MOST);
            return 1.0F;
        }
        return number;
    }

    @Nullable private static EnumDifficulty difficulty(String owner, JsonElement value) {
        EnumDifficulty found = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? ContentServer.difficultyFrom(value.getAsString()) : null;
        if (found == null) { ContentLog.LOGGER.error("{} gives rules.difficulty as {}, which is not one of peaceful, easy, normal or hard, ignoring it", owner, value); }
        return found;
    }

    private static float hardness(String owner, JsonElement value) {
        Float number = ContentSpawnSwitch.number(owner, "rules.localDifficulty", value, false);
        if (number == null) { return -1.0F; }
        if (number > HARDEST) {
            ContentLog.LOGGER.error("{} gives rules.localDifficulty as {}, which is above {}, ignoring it", owner, value, HARDEST);
            return -1.0F;
        }
        return number;
    }

    private static boolean flag(String owner, String key, JsonElement value, boolean fallback) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not true or false, ignoring it", owner, key, value);
        return fallback;
    }

    private static SnowRuleDef snow(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rules.snow that is not an object of place, freeze, whenDry, melt, meltChance and meltBlocks keys, ignoring it", owner);
            return SnowRuleDef.NONE;
        }
        int place = SnowRuleDef.DEFAULT;
        int freeze = SnowRuleDef.DEFAULT;
        boolean whenDry = false;
        boolean melt = false;
        float meltChance = SnowRuleDef.MELT_CHANCE;
        Set<Block> meltBlocks = SnowRuleDef.MELT_BLOCKS;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            JsonElement value = entry.getValue();
            switch (entry.getKey()) {
                case "place": place = snowMode(owner, "rules.snow.place", value); break;
                case "freeze": freeze = snowMode(owner, "rules.snow.freeze", value); break;
                case "whenDry": whenDry = flag(owner, "rules.snow.whenDry", value, false); break;
                case "melt": melt = melt(owner, value); break;
                case "meltChance": meltChance = meltChance(owner, value); break;
                case "meltBlocks": meltBlocks = meltBlocks(owner, value); break;
                default: ContentLog.LOGGER.error("{} gives rules.snow.{}, which is not a snow key, ignoring it", owner, entry.getKey()); break;
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
        Float number = ContentSpawnSwitch.number(owner, "rules.snow.meltChance", value, false);
        if (number == null) { return SnowRuleDef.MELT_CHANCE; }
        if (number <= 0.0F || number > 1.0F) {
            ContentLog.LOGGER.error("{} gives rules.snow.meltChance as {}, which is not above 0 and at most 1, ignoring it", owner, value);
            return SnowRuleDef.MELT_CHANCE;
        }
        return number;
    }

    private static Set<Block> meltBlocks(String owner, JsonElement value) {
        if (!value.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives rules.snow.meltBlocks that is not a list of block ids, ignoring it", owner);
            return SnowRuleDef.MELT_BLOCKS;
        }
        Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives rules.snow.meltBlocks an entry {} that is not a block id, ignoring it", owner, entry);
                continue;
            }
            Block block = ContentStates.block(entry.getAsString().trim(), owner + " rules.snow.meltBlocks");
            if (block != null) { blocks.add(block); }
        }
        return blocks;
    }

    private static int wakeAt(String owner, JsonElement value) {
        Float number = ContentSpawnSwitch.number(owner, "rules.wakeAt", value, true);
        if (number == null) { return -1; }
        if (number > LAST_TICK) {
            ContentLog.LOGGER.error("{} gives rules.wakeAt as {}, which is above {}, ignoring it", owner, value, LAST_TICK);
            return -1;
        }
        return number.intValue();
    }

    public static long dayLength(World world, @Nullable DimensionDef def) {
        long ruled = ContentEnvironments.wide(world.provider.getDimension()).dayLength;
        if (ruled > 0L && (def == null || def.fixedTime < 0)) { return ruled; }
        return def == null ? DimensionTraitsDef.VANILLA_DAY : def.traits.dayLength;
    }

    public static boolean forces(World world) { return ContentEnvironments.wide(world.provider.getDimension()).weather != RuleSwitchDef.UNSET; }

    public static int speed(World world) {
        if (world.isRemote) { return 1; }
        return ContentGrowSwitch.roll(ContentEnvironments.wide(world.provider.getDimension()).weatherSpeed, world.rand);
    }

    @Nullable public static EnumDifficulty difficulty(World world) { return ContentEnvironments.wide(world.provider.getDimension()).difficulty; }

    public static DifficultyInstance local(World world, BlockPos pos, DifficultyInstance vanilla) {
        float wanted = ContentEnvironments.rules(world, pos).localDifficulty;
        EnumDifficulty difficulty = world.getDifficulty();
        if (wanted < 0.0F || difficulty == EnumDifficulty.PEACEFUL) { return vanilla; }
        DifficultyInstance ruled = new DifficultyInstance(difficulty, 0L, 0L, 0.0F);
        ruled.additionalDifficulty = wanted;
        return ruled;
    }

    public static long woken(WorldServer world, long vanilla) {
        if (!world.areAllPlayersAsleep()) { return vanilla; }
        RuleSwitchDef rules = ContentEnvironments.wide(world.provider.getDimension());
        if (rules.dayLength <= 0L && rules.wakeAt < 0) { return vanilla; }
        DimensionDef def = world.provider instanceof ContentWorldProvider ? ContentDimensions.of(world) : ContentDimensions.overlay(world);
        long length = dayLength(world, def);
        if (length <= 0L) { return vanilla; }
        long now = world.getWorldTime();
        long target = now - Math.floorMod(now, length) + Math.max(rules.wakeAt, 0) * length / DimensionTraitsDef.VANILLA_DAY;
        return target > now ? target : target + length;
    }

    @SubscribeEvent public static void onSleepCheck(SleepingTimeCheckEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) { return; }
        BlockPos at = event.getSleepingLocation() == null ? player.getPosition() : event.getSleepingLocation();
        if (!ContentEnvironments.rules(player.world, at).sleep) { event.setResult(Event.Result.DENY); }
    }
}
