package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonSizeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserDungeons {
    private static final List<String> SHAPES = Collections.unmodifiableList(Arrays.asList("box", "pillared", "ring", "pit", "cross", "cave"));
    private static final List<String> SHAFTS = Collections.unmodifiableList(Arrays.asList("ladder", "spiral", "stairs"));
    private static final List<String> ENTRANCES = Collections.unmodifiableList(Arrays.asList("simple", "spiral", "toproom"));
    private static final Set<String> DUNGEON_KEYS = keys("name", "themes", "dimensions", "dimensionsAreBlacklist", "chance", "minDistance", "group", "minDistanceFromSpawn", "noOceans", "biomes", "biomeTypes", "biomesAreBlacklist", "at", "locateAs", "sizes", "shrink", "surfaceBuffer", "minY", "levelHeight", "roomSize", "corridor", "loops", "rolls", "roomKinds", "entrance", "boss", "shafts", "difficulty", "support", "decay", "levelRules", "respawn");
    private static final Set<String> SIZE_KEYS = keys("name", "weight", "footprint", "levels", "rooms");
    private static final Set<String> KIND_KEYS = keys("kind", "weight", "minLevel", "maxLevel", "maxPerDungeon", "maxPerLevel", "minSize", "height", "shapes", "templates", "yOffset", "turns", "decor", "mobs", "loot", "spawners", "chests", "rolls", "decay", "node");
    private static final Set<String> ENTRANCE_KEYS = keys("types", "topRoomTemplates", "extra");
    private static final Set<String> BOSS_KEYS = keys("templates", "size", "height", "mob", "loot", "count");
    private static final Set<String> RULE_KEYS = keys("fromDepth", "theme", "rolls", "difficulty", "decay", "lootBonus", "roomKinds");
    private static final Set<String> DIFFICULTY_KEYS = keys("preset", "spawners", "promote", "maxTier", "nodeTier", "bossTier", "blocksPerSpawner", "entranceMobs");
    private static final Set<String> DECAY_KEYS = keys("base", "perDepth");
    private static final int TOP_TIER = 4;

    private ContentParserDungeons() {}

    @Nullable public static DungeonDef dungeon(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "dungeon");
        if (json == null) { return null; }
        String owner = "Dungeon " + key;
        unknown(owner, json, DUNGEON_KEYS);
        Set<Integer> dimensions = new LinkedHashSet<>();
        if (json.has("dimensions")) { dimensions.addAll(Json.ints(Json.getJsonArray(json, "dimensions", new JsonArray()), "dimensions")); }
        else { dimensions.add(0); }
        int[] levelHeight = pair(owner, json, "levelHeight", DungeonDef.DEFAULT_LEVEL_HEIGHT, 4, 64);
        DungeonDef.Corridor corridor = corridor(owner, json, levelHeight[0]);
        int[] roomSize = pair(owner, json, "roomSize", DungeonDef.DEFAULT_ROOM_SIZE, 3, 64);
        if (roomSize[0] < corridor.width + 2) {
            ContentLog.LOGGER.error("{} gives a roomSize minimum of {}, too small for a corridor {} wide, so it is raised to {}", owner, roomSize[0], corridor.width, corridor.width + 2);
            roomSize = new int[] { corridor.width + 2, Math.max(roomSize[1], corridor.width + 2) };
        }
        List<PickDef> shafts = picks(owner, json, "shafts", SHAFTS);
        if (shafts.isEmpty()) { shafts = Collections.singletonList(new PickDef("ladder", 1)); }
        return new DungeonDef(key, Json.getString(json, "name", key.getPath()), picks(owner, json, "themes", null), dimensions, Json.getBoolean(json, "dimensionsAreBlacklist", false),
                number(owner, json, "chance", DungeonDef.DEFAULT_CHANCE, 1, 100), number(owner, json, "minDistance", DungeonDef.DEFAULT_MIN_DISTANCE, 0, 100000),
                Json.getString(json, "group", key.toString()), number(owner, json, "minDistanceFromSpawn", 0, 0, 100000), Json.getBoolean(json, "noOceans", true),
                Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), Json.getBoolean(json, "biomesAreBlacklist", false),
                at(owner, json), json.has("locateAs") ? Json.getString(json, "locateAs", "") : null, sizes(owner, json), number(owner, json, "shrink", 0, 0, 100),
                number(owner, json, "surfaceBuffer", DungeonDef.DEFAULT_SURFACE_BUFFER, 0, 64), json.has("minY") ? Json.getInt(json, "minY", 0) : null,
                levelHeight, roomSize, corridor, number(owner, json, "loops", DungeonDef.DEFAULT_LOOPS, 0, 100), rolls(owner, json), roomKinds(owner, json, true),
                entrance(owner, json), boss(owner, json), shafts, json.has("difficulty") ? difficulty(owner, json.get("difficulty")) : null,
                support(owner, json), json.has("decay") ? decay(owner, json.get("decay")) : null,
                levelRules(owner, json), ContentParserRespawn.parse(key, json));
    }

    public static DungeonDef.Difficulty difficulty(String owner, @Nullable JsonElement held) {
        if (held == null) { return new DungeonDef.Difficulty(DungeonDef.DEFAULT_DIFFICULTY, null, null, null, null, null, null, null); }
        if (held.isJsonPrimitive()) { return new DungeonDef.Difficulty(preset(owner, held.getAsString()), null, null, null, null, null, null, null); }
        if (!held.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives a difficulty that is neither a preset name nor an object, so '{}' is used", owner, DungeonDef.DEFAULT_DIFFICULTY);
            return difficulty(owner, null);
        }
        JsonObject json = held.getAsJsonObject();
        unknown(owner + " difficulty", json, DIFFICULTY_KEYS);
        String where = owner + " difficulty";
        return new DungeonDef.Difficulty(json.has("preset") ? preset(owner, Json.getString(json, "preset", "")) : DungeonDef.DEFAULT_DIFFICULTY,
                optional(where, json, "spawners", 0, 100), optional(where, json, "promote", 0, 100), optional(where, json, "maxTier", 0, TOP_TIER), optional(where, json, "nodeTier", 0, TOP_TIER),
                optional(where, json, "bossTier", 0, TOP_TIER), optional(where, json, "blocksPerSpawner", 1, 100000), json.has("entranceMobs") ? Json.getBoolean(json, "entranceMobs", false) : null);
    }

    public static DungeonDef.Decay decay(String owner, JsonElement held) {
        DungeonDef.Decay fallback = new DungeonDef.Decay(DungeonDef.Decay.DEFAULT_BASE, DungeonDef.Decay.DEFAULT_PER_DEPTH);
        if (!held.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives a decay that is not an object of base and perDepth, so the default is used", owner);
            return fallback;
        }
        JsonObject json = held.getAsJsonObject();
        unknown(owner + " decay", json, DECAY_KEYS);
        return new DungeonDef.Decay(number(owner + " decay", json, "base", fallback.base, 0, 100), number(owner + " decay", json, "perDepth", fallback.perDepth, 0, 100));
    }

    public static Map<String, DungeonDef.Degree> rolls(String owner, JsonObject json) {
        if (!json.has("rolls")) { return Collections.emptyMap(); }
        if (!json.get("rolls").isJsonObject()) {
            ContentLog.LOGGER.error("{} gives rolls that are not an object of element to degree, so they are ignored", owner);
            return Collections.emptyMap();
        }
        Map<String, DungeonDef.Degree> rolls = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("rolls").entrySet()) {
            if (!DungeonDef.ROLLS.contains(entry.getKey())) {
                ContentLog.LOGGER.error("{} names the roll '{}', which is not one of {}, so it is ignored", owner, entry.getKey(), DungeonDef.ROLLS);
                continue;
            }
            DungeonDef.Degree degree = degree(owner + " roll '" + entry.getKey() + "'", entry.getValue());
            if (degree != null) { rolls.put(entry.getKey(), degree); }
        }
        return Collections.unmodifiableMap(rolls);
    }

    @Nullable static DungeonDef.Degree degree(String where, JsonElement held) {
        if (held.isJsonPrimitive()) {
            int index = DungeonDef.Degree.indexOf(held.getAsString().trim().toLowerCase(Locale.ROOT));
            if (index >= 0) { return DungeonDef.Degree.named(index); }
            ContentLog.LOGGER.error("{} is '{}', which is not one of {}, so it is ignored", where, held.getAsString(), Arrays.toString(DungeonDef.Degree.NAMES));
            return null;
        }
        if (!held.isJsonObject()) {
            ContentLog.LOGGER.error("{} is neither a degree name nor an object of degree weights, so it is ignored", where);
            return null;
        }
        int[] weights = new int[DungeonDef.Degree.NAMES.length];
        for (Map.Entry<String, JsonElement> entry : held.getAsJsonObject().entrySet()) {
            int index = DungeonDef.Degree.indexOf(entry.getKey());
            if (index < 0) {
                ContentLog.LOGGER.error("{} names the degree '{}', which is not one of {}, so that weight is ignored", where, entry.getKey(), Arrays.toString(DungeonDef.Degree.NAMES));
                continue;
            }
            weights[index] = Math.max(0, Json.asInt(entry.getValue(), where, 0));
        }
        DungeonDef.Degree degree = new DungeonDef.Degree(weights);
        if (degree.valid()) { return degree; }
        ContentLog.LOGGER.error("{} has every degree weight at 0, so it is ignored", where);
        return null;
    }

    public static List<PickDef> picks(String owner, JsonObject json, String key, @Nullable Collection<String> valid) {
        if (!json.has(key)) { return Collections.emptyList(); }
        JsonElement held = json.get(key);
        List<String> entries = new ArrayList<>();
        if (held.isJsonArray()) { entries.addAll(Json.texts(held.getAsJsonArray(), key)); }
        else if (held.isJsonPrimitive()) { entries.add(held.getAsString()); }
        else { ContentLog.LOGGER.error("{} gives '{}' that is not a list of id=weight entries, so it is ignored", owner, key); }
        List<PickDef> picks = new ArrayList<>();
        for (String entry : entries) {
            Settings.Weighted split = Settings.weighted(entry, 1);
            String name = split.name.toLowerCase(Locale.ROOT);
            if (split.unreadable || name.isEmpty()) { ContentLog.LOGGER.error("{} lists '{}' in '{}', which is not an id or id=weight, so it is left out", owner, entry, key); }
            else if (valid != null && !valid.contains(name)) { ContentLog.LOGGER.error("{} lists '{}' in '{}', which is not one of {}, so it is left out", owner, name, key, valid); }
            else { picks.add(new PickDef(name, split.weight)); }
        }
        return Collections.unmodifiableList(picks);
    }

    private static Set<String> keys(String... names) { return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(names))); }

    private static void unknown(String owner, JsonObject json, Set<String> known) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!known.contains(entry.getKey())) { ContentLog.LOGGER.warn("{} sets '{}', which is not a key anything reads, so it does nothing. Check the spelling", owner, entry.getKey()); }
        }
    }

    private static int number(String owner, JsonObject json, String key, int fallback, int low, int high) {
        if (!json.has(key)) { return fallback; }
        int value = Json.getInt(json, key, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets '{}' to {}, which is outside {} to {}, so {} is used instead", owner, key, value, low, high, fallback);
        return fallback;
    }

    @Nullable private static Integer optional(String owner, JsonObject json, String key, int low, int high) {
        if (!json.has(key)) { return null; }
        int value = Json.getInt(json, key, low - 1);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets '{}' to {}, which is outside {} to {}, so the preset's value is used", owner, key, value, low, high);
        return null;
    }

    private static int[] pair(String owner, JsonObject json, String key, int[] fallback, int low, int high) {
        if (!json.has(key)) { return fallback; }
        JsonElement held = json.get(key);
        if (held.isJsonArray() && held.getAsJsonArray().size() == 2) {
            int least = Json.asInt(held.getAsJsonArray().get(0), key, low - 1);
            int most = Json.asInt(held.getAsJsonArray().get(1), key, low - 1);
            if (least >= low && most <= high && least <= most) { return new int[] { least, most }; }
        }
        ContentLog.LOGGER.error("{} sets '{}' to something other than [min, max] with {} <= min <= max <= {}, so {} instead", owner, key, low, high, fallback == null ? "it is ignored" : Arrays.toString(fallback) + " is used");
        return fallback;
    }

    @Nullable private static int[] optionalPair(String owner, JsonObject json, String key, int low) { return json.has(key) ? pair(owner, json, key, null, low, 64) : null; }

    private static String preset(String owner, String named) {
        String name = named.trim().toLowerCase(Locale.ROOT);
        if (DungeonDef.Difficulty.PRESETS.containsKey(name)) { return name; }
        ContentLog.LOGGER.error("{} names the difficulty preset '{}', which is not one of {}, so '{}' is used", owner, named, DungeonDef.Difficulty.PRESETS.keySet(), DungeonDef.DEFAULT_DIFFICULTY);
        return DungeonDef.DEFAULT_DIFFICULTY;
    }

    @Nullable private static int[][] at(String owner, JsonObject json) {
        if (!json.has("at")) { return null; }
        JsonArray held = Json.getJsonArray(json, "at", new JsonArray());
        List<int[]> spots = new ArrayList<>();
        if (held.size() == 2 && held.get(0).isJsonPrimitive() && held.get(1).isJsonPrimitive()) { spots.add(new int[] { Json.asInt(held.get(0), "at", 0), Json.asInt(held.get(1), "at", 0) }); }
        else {
            for (JsonElement entry : held) {
                if (entry.isJsonArray() && entry.getAsJsonArray().size() == 2) { spots.add(new int[] { Json.asInt(entry.getAsJsonArray().get(0), "at", 0), Json.asInt(entry.getAsJsonArray().get(1), "at", 0) }); }
                else { ContentLog.LOGGER.error("{} pins 'at' with an entry that is not [x, z], so it is ignored", owner); }
            }
        }
        return spots.isEmpty() ? null : spots.toArray(new int[0][]);
    }

    private static List<DungeonSizeDef> sizes(String owner, JsonObject json) {
        if (!json.has("sizes")) { return DungeonSizeDef.BUILT_IN; }
        List<DungeonSizeDef> sizes = new ArrayList<>();
        for (JsonElement held : Json.getJsonArray(json, "sizes", new JsonArray())) {
            if (!held.isJsonObject()) {
                ContentLog.LOGGER.error("{} lists a size that is not an object, so it is left out", owner);
                continue;
            }
            JsonObject size = held.getAsJsonObject();
            String where = owner + " size " + sizes.size();
            unknown(where, size, SIZE_KEYS);
            sizes.add(new DungeonSizeDef(Json.getString(size, "name", "size" + sizes.size()), number(where, size, "weight", DungeonSizeDef.DEFAULT_WEIGHT, 1, 1000000),
                    number(where, size, "footprint", DungeonSizeDef.DEFAULT_FOOTPRINT, DungeonSizeDef.MIN_FOOTPRINT, DungeonSizeDef.MAX_FOOTPRINT),
                    pair(where, size, "levels", DungeonSizeDef.DEFAULT_LEVELS, 1, 64), pair(where, size, "rooms", DungeonSizeDef.DEFAULT_ROOMS, 1, 400)));
        }
        if (sizes.isEmpty()) {
            ContentLog.LOGGER.error("{} gives no usable size, so the built in sizes are used", owner);
            return DungeonSizeDef.BUILT_IN;
        }
        return Collections.unmodifiableList(sizes);
    }

    private static DungeonDef.Corridor corridor(String owner, JsonObject json, int levelHeightLeast) {
        JsonObject held = json.has("corridor") && json.get("corridor").isJsonObject() ? json.getAsJsonObject("corridor") : new JsonObject();
        if (json.has("corridor") && !json.get("corridor").isJsonObject()) { ContentLog.LOGGER.error("{} gives a corridor that is not an object of width and height, so the default is used", owner); }
        unknown(owner + " corridor", held, keys("width", "height"));
        return new DungeonDef.Corridor(number(owner + " corridor", held, "width", DungeonDef.Corridor.DEFAULT_WIDTH, DungeonDef.Corridor.MIN_WIDTH, DungeonDef.Corridor.MAX_WIDTH),
                number(owner + " corridor", held, "height", DungeonDef.Corridor.DEFAULT_HEIGHT, DungeonDef.Corridor.MIN_HEIGHT, Math.max(DungeonDef.Corridor.MIN_HEIGHT, levelHeightLeast - 2)));
    }

    private static int support(String owner, JsonObject json) {
        if (!json.has("support")) { return DungeonDef.DEFAULT_SUPPORT_DEPTH; }
        JsonElement held = json.get("support");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? DungeonDef.DEFAULT_SUPPORT_DEPTH : 0; }
        if (held.isJsonObject()) {
            unknown(owner + " support", held.getAsJsonObject(), keys("depth"));
            return number(owner + " support", held.getAsJsonObject(), "depth", DungeonDef.DEFAULT_SUPPORT_DEPTH, 0, 64);
        }
        ContentLog.LOGGER.error("{} gives a support that is neither false nor an object with a depth, so the default is used", owner);
        return DungeonDef.DEFAULT_SUPPORT_DEPTH;
    }

    private static DungeonDef.Entrance entrance(String owner, JsonObject json) {
        if (!json.has("entrance")) { return DungeonDef.Entrance.simple(); }
        if (!json.get("entrance").isJsonObject()) {
            ContentLog.LOGGER.error("{} gives an entrance that is not an object, so a simple entrance is used", owner);
            return DungeonDef.Entrance.simple();
        }
        JsonObject held = json.getAsJsonObject("entrance");
        String where = owner + " entrance";
        unknown(where, held, ENTRANCE_KEYS);
        List<PickDef> types = picks(where, held, "types", ENTRANCES);
        return new DungeonDef.Entrance(types.isEmpty() ? DungeonDef.Entrance.simple().types : types, picks(where, held, "topRoomTemplates", null),
                held.has("extra") ? degree(where + " extra", held.get("extra")) : null);
    }

    @Nullable private static DungeonDef.Boss boss(String owner, JsonObject json) {
        if (!json.has("boss")) { return null; }
        if (!json.get("boss").isJsonObject()) {
            ContentLog.LOGGER.error("{} gives a boss that is not an object, so there is no boss room", owner);
            return null;
        }
        JsonObject held = json.getAsJsonObject("boss");
        String where = owner + " boss";
        unknown(where, held, BOSS_KEYS);
        return new DungeonDef.Boss(picks(where, held, "templates", null), optionalPair(where, held, "size", 3), optionalPair(where, held, "height", 3),
                Json.getString(held, "mob", DungeonDef.Boss.DEFAULT_MOB), Json.getString(held, "loot", DungeonDef.Boss.DEFAULT_LOOT), number(where, held, "count", DungeonDef.Boss.DEFAULT_COUNT, 1, 16));
    }

    private static List<DungeonDef.LevelRule> levelRules(String owner, JsonObject json) {
        if (!json.has("levelRules")) { return Collections.emptyList(); }
        List<DungeonDef.LevelRule> rules = new ArrayList<>();
        for (JsonElement held : Json.getJsonArray(json, "levelRules", new JsonArray())) {
            String where = owner + " levelRule " + rules.size();
            if (!held.isJsonObject() || !held.getAsJsonObject().has("fromDepth")) {
                ContentLog.LOGGER.error("{} is not an object with a fromDepth, so it is left out", where);
                continue;
            }
            JsonObject rule = held.getAsJsonObject();
            unknown(where, rule, RULE_KEYS);
            double from = Json.asDouble(rule.get("fromDepth"), "fromDepth", -1.0D);
            if (from < 0.0D || from > 1.0D) {
                ContentLog.LOGGER.error("{} sets fromDepth to {}, which is outside 0.0 to 1.0, so it is left out", where, from);
                continue;
            }
            rules.add(new DungeonDef.LevelRule(from, rule.has("theme") ? new ResourceLocation(Json.getString(rule, "theme", "")) : null, rule.has("rolls") ? rolls(where, rule) : null,
                    rule.has("difficulty") ? difficulty(where, rule.get("difficulty")) : null, rule.has("decay") ? decay(where, rule.get("decay")) : null,
                    rule.has("lootBonus") ? Json.getInt(rule, "lootBonus", 0) : null, rule.has("roomKinds") ? roomKinds(where, rule, false) : null));
        }
        rules.sort(Comparator.comparingDouble(rule -> rule.fromDepth));
        return Collections.unmodifiableList(rules);
    }

    private static List<RoomKindDef> roomKinds(String owner, JsonObject json, boolean hallDefault) {
        List<RoomKindDef> kinds = new ArrayList<>();
        for (JsonElement held : Json.getJsonArray(json, "roomKinds", new JsonArray())) {
            String where = owner + " roomKind " + kinds.size();
            if (!held.isJsonObject() || Json.text(held.getAsJsonObject(), "kind", "").isEmpty()) {
                ContentLog.LOGGER.error("{} is not an object with a kind, so it is left out", where);
                continue;
            }
            kinds.add(roomKind(where, held.getAsJsonObject()));
        }
        if (kinds.isEmpty() && hallDefault) { kinds.add(RoomKindDef.hall()); }
        return Collections.unmodifiableList(kinds);
    }

    private static RoomKindDef roomKind(String where, JsonObject json) {
        unknown(where, json, KIND_KEYS);
        int decay = 0;
        boolean decayTemplate = false;
        if (json.has("decay") && json.get("decay").isJsonPrimitive() && json.get("decay").getAsJsonPrimitive().isBoolean()) { decayTemplate = json.get("decay").getAsBoolean(); }
        else { decay = number(where, json, "decay", 0, 0, 100); }
        List<PickDef> shapes = picks(where, json, "shapes", SHAPES);
        return new RoomKindDef(Json.text(json, "kind", "").toLowerCase(Locale.ROOT), number(where, json, "weight", RoomKindDef.DEFAULT_WEIGHT, 1, 1000000),
                number(where, json, "minLevel", RoomKindDef.ANY, 0, 64), number(where, json, "maxLevel", RoomKindDef.ANY, 0, 64),
                number(where, json, "maxPerDungeon", RoomKindDef.NO_CAP, 0, 100000), number(where, json, "maxPerLevel", RoomKindDef.NO_CAP, 0, 100000),
                optionalPair(where, json, "minSize", 1), optionalPair(where, json, "height", 3), shapes.isEmpty() ? Collections.singletonList(new PickDef("box", 1)) : shapes,
                picks(where, json, "templates", null), number(where, json, "yOffset", 0, -64, 64), picks(where, json, "turns", null), Json.strings(json, "decor"),
                Json.getString(json, "mobs", RoomKindDef.DEFAULT_MOBS), Json.getString(json, "loot", RoomKindDef.DEFAULT_LOOT), amount(where, json, "spawners"), amount(where, json, "chests"),
                rolls(where, json), decay, decayTemplate, Json.getBoolean(json, "node", false));
    }

    @Nullable private static RoomKindDef.Amount amount(String where, JsonObject json, String key) {
        if (!json.has(key)) { return null; }
        JsonElement held = json.get(key);
        if (held.isJsonPrimitive()) {
            int percent = Json.asInt(held, key, -1);
            if (percent >= 0 && percent <= 100) { return new RoomKindDef.Amount(true, percent, percent); }
        }
        else if (held.isJsonArray() && held.getAsJsonArray().size() == 2) {
            int least = Json.asInt(held.getAsJsonArray().get(0), key, -1);
            int most = Json.asInt(held.getAsJsonArray().get(1), key, -1);
            if (least >= 0 && least <= most) { return new RoomKindDef.Amount(false, least, most); }
        }
        ContentLog.LOGGER.error("{} sets '{}' to something other than a percent of 0 to 100 or a [min, max] count, so it is ignored", where, key);
        return null;
    }
}
