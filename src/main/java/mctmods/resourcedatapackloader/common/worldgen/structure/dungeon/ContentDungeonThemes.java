package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserDungeons;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPopulation;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Fallbacks;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class ContentDungeonThemes {
    private static final Map<String, DungeonThemeDef> THEMES = new LinkedHashMap<>();
    private static final Set<String> WARNED = new HashSet<>();
    private static final List<String> FAMILIES = Arrays.asList("walls", "floors", "ceilings", "pillars", "fencing", "liquid", "cave", "air", "support", "stairs", "slabs", "decay");
    private static final List<String> TIERS = DungeonPopulation.TIERS;
    private static final List<String> DECORATOR_TYPES = Arrays.asList("wallLine", "floorPatch", "scatter", "pillarsRow", "bannerLine", "torchLine", "fill");
    private static final List<String> SURFACES = Arrays.asList("floor", "ceiling", "wall");
    private static final List<String> THEME_KEYS = Collections.unmodifiableList(Arrays.asList("name", "weight", "dimensions", "dimensionsAreBlacklist", "biomes", "biomeTypes", "notBiomes", "notBiomeTypes", "minY", "maxY", "blocks", "mobs", "spawnGroups", "loot", "lootLevels", "lootBonus", "decorators", "rolls", "difficulty", "decay", "append"));
    private static final List<String> SPAWN_GROUP_KEYS = Collections.unmodifiableList(Arrays.asList("name", "tier", "minDepth", "maxDepth", "kinds", "mobs", "weight"));
    private static final List<String> LOOT_KEYS = Collections.unmodifiableList(Arrays.asList("table", "weight", "minLevel", "maxLevel"));
    private static final List<String> DECORATOR_NUMBERS = Collections.unmodifiableList(Arrays.asList("height", "gap", "percent", "per100"));
    private static final List<String> DECORATOR_KEYS = Collections.unmodifiableList(Arrays.asList("type", "block", "on", "loot", "height", "gap", "percent", "per100"));
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        List<DungeonThemeDef> appends = new ArrayList<>();
        Json.eachObject(PackManager.DUNGEONTHEMES, "dungeon theme", (key, json) -> {
            DungeonThemeDef def = new Reader(key, json).theme();
            if (def == null) { return; }
            if (def.appends()) { appends.add(def); }
            else { THEMES.put(key.toString(), def); }
        });
        appends.sort(Comparator.comparing(append -> append.key));
        for (DungeonThemeDef append : appends) { apply(append); }
        for (Map.Entry<String, DungeonThemeDef> held : THEMES.entrySet()) { held.setValue(withKnownLoot(held.getValue())); }
        if (!THEMES.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} dungeon theme(s): {}", THEMES.size(), THEMES.keySet()); }
    }

    @Nullable public static DungeonThemeDef byName(String id) { return THEMES.get(new ResourceLocation(id).toString()); }

    public static List<PickDef> usable(List<PickDef> themes, int dimension, Biome biome, int lowestY, int highestY) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
        List<PickDef> found = new ArrayList<>();
        for (PickDef pick : themes) {
            DungeonThemeDef theme = byName(pick.name);
            if (theme == null) {
                ContentLog.LOGGER.errorOnce(WARNED, "missing:" + pick.name, "Dungeon theme '{}' is named but no pack defines it, leaving it out", pick.name);
                continue;
            }
            if (!theme.allowsDimension(dimension) || !fitsY(theme, lowestY, highestY)) { continue; }
            int share = biomeShare(theme, biome, types);
            if (share > 0) { found.add(new PickDef(theme.key.toString(), pick.weight * theme.weightOrDefault() * share)); }
        }
        return Collections.unmodifiableList(found);
    }

    private static boolean fitsY(DungeonThemeDef theme, int lowestY, int highestY) { return (theme.minY == null || theme.minY <= highestY) && (theme.maxY == null || theme.maxY >= lowestY); }

    private static int biomeShare(DungeonThemeDef theme, Biome biome, Set<BiomeDictionary.Type> types) {
        if (Math.max(named(theme.notBiomes, biome), typed(theme.notBiomeTypes, types)) > 0) { return 0; }
        if (theme.biomes.isEmpty() && theme.biomeTypes.isEmpty()) { return 1; }
        return Math.max(named(theme.biomes, biome), typed(theme.biomeTypes, types));
    }

    private static int named(List<PickDef> picks, Biome biome) {
        ResourceLocation name = biome.getRegistryName();
        int share = 0;
        for (PickDef pick : picks) {
            if (name != null && (pick.name.equals(name.toString()) || pick.name.equals(name.getPath()))) { share = Math.max(share, pick.weight); }
        }
        return share;
    }

    private static int typed(List<PickDef> picks, Set<BiomeDictionary.Type> types) {
        int share = 0;
        for (PickDef pick : picks) {
            for (BiomeDictionary.Type type : types) {
                if (type.getName().equalsIgnoreCase(pick.name)) { share = Math.max(share, pick.weight); }
            }
        }
        return share;
    }

    private static void apply(DungeonThemeDef append) {
        for (ResourceLocation target : append.append) {
            DungeonThemeDef base = THEMES.get(target.toString());
            if (base == null) {
                ContentLog.LOGGER.errorOnce(WARNED, "target:" + append.key + ">" + target, "Dungeon theme append {} names theme {}, which no pack defines, skipping it", append.key, target);
                continue;
            }
            DungeonThemeDef merged = merged(base, append);
            if (merged.blocks.getOrDefault("walls", Collections.emptyList()).isEmpty()) { ContentLog.LOGGER.error("Dungeon theme append {} would leave theme {} with no walls, so it is not applied", append.key, target); }
            else { THEMES.put(target.toString(), merged); }
        }
    }

    private static DungeonThemeDef withKnownLoot(DungeonThemeDef theme) {
        Map<String, DungeonThemeDef.Decorator> kept = new LinkedHashMap<>();
        for (Map.Entry<String, DungeonThemeDef.Decorator> held : theme.decorators.entrySet()) {
            DungeonThemeDef.Decorator decorator = held.getValue();
            if (decorator.loot != null && !theme.loot.containsKey(decorator.loot)) { ContentLog.LOGGER.error("Dungeon theme {} decorator '{}' fills with loot set '{}', which the theme does not have, leaving the decorator out", theme.key, held.getKey(), decorator.loot); }
            else { kept.put(held.getKey(), decorator); }
        }
        if (kept.size() == theme.decorators.size()) { return theme; }
        return new DungeonThemeDef(theme.key, theme.name, theme.weight, theme.dimensions, theme.dimensionsAreBlacklist, theme.biomes, theme.biomeTypes, theme.notBiomes, theme.notBiomeTypes, theme.minY, theme.maxY, theme.blocks, theme.mobs, theme.spawnGroups, theme.loot, Collections.unmodifiableMap(kept), theme.rolls, theme.difficulty, theme.decay, theme.lootLevels, theme.lootBonus, theme.append, theme.removals);
    }

    private static DungeonThemeDef merged(DungeonThemeDef base, DungeonThemeDef add) {
        return new DungeonThemeDef(base.key, base.name, Fallbacks.orDefault(add.weight, base.weight), base.dimensions, base.dimensionsAreBlacklist,
                joined(base.biomes, add.biomes, removed(add, "biomes"), pick -> pick.name),
                joined(base.biomeTypes, add.biomeTypes, removed(add, "biomeTypes"), pick -> pick.name),
                joined(base.notBiomes, add.notBiomes, removed(add, "notBiomes"), pick -> pick.name),
                joined(base.notBiomeTypes, add.notBiomeTypes, removed(add, "notBiomeTypes"), pick -> pick.name),
                Fallbacks.orDefault(add.minY, base.minY), Fallbacks.orDefault(add.maxY, base.maxY),
                sections("blocks", base.blocks, add.blocks, add, block -> block.block.toString()),
                sections("mobs", base.mobs, add.mobs, add, mob -> mob.id),
                joined(base.spawnGroups, add.spawnGroups, removed(add, "spawnGroups"), group -> Settings.lower(group.name)),
                sections("loot", base.loot, add.loot, add, entry -> entry.table.toString()),
                decorators(base.decorators, add),
                Fallbacks.orDefault(add.rolls, base.rolls), Fallbacks.orDefault(add.difficulty, base.difficulty), Fallbacks.orDefault(add.decay, base.decay),
                Fallbacks.orDefault(add.lootLevels, base.lootLevels), Fallbacks.orDefault(add.lootBonus, base.lootBonus),
                Collections.emptyList(), Collections.emptyMap());
    }

    private static Set<String> removed(DungeonThemeDef from, String... keys) {
        Set<String> names = new HashSet<>();
        for (String key : keys) {
            for (String name : from.removals.getOrDefault(key, Collections.emptyList())) {
                names.add(name);
                if (name.indexOf(':') < 0) { names.add(new ResourceLocation(name).toString()); }
            }
        }
        return names;
    }

    private static <T> List<T> joined(List<T> base, List<T> add, Set<String> removed, Function<T, String> identity) {
        List<T> result = new ArrayList<>(base.size() + add.size());
        for (T held : base) {
            if (!removed.contains(identity.apply(held))) { result.add(held); }
        }
        result.addAll(add);
        return Collections.unmodifiableList(result);
    }

    private static <T> Map<String, List<T>> sections(String section, Map<String, List<T>> base, Map<String, List<T>> add, DungeonThemeDef from, Function<T, String> identity) {
        Set<String> names = new LinkedHashSet<>(base.keySet());
        names.addAll(add.keySet());
        Map<String, List<T>> result = new LinkedHashMap<>();
        for (String name : names) {
            result.put(name, joined(base.getOrDefault(name, Collections.emptyList()), add.getOrDefault(name, Collections.emptyList()), removed(from, name, section + "." + name), identity));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, DungeonThemeDef.Decorator> decorators(Map<String, DungeonThemeDef.Decorator> base, DungeonThemeDef add) {
        Set<String> removed = removed(add, "decorators");
        Map<String, DungeonThemeDef.Decorator> result = new LinkedHashMap<>();
        for (Map.Entry<String, DungeonThemeDef.Decorator> held : base.entrySet()) {
            if (!removed.contains(Settings.lower(held.getKey()))) { result.put(held.getKey(), held.getValue()); }
        }
        result.putAll(add.decorators);
        return Collections.unmodifiableMap(result);
    }

    private static final class Reader {
        private final ResourceLocation key;
        private final JsonObject json;
        private final String owner;

        private Reader(ResourceLocation key, JsonObject json) {
            this.key = key;
            this.owner = "Dungeon theme " + key;
            this.json = json;
        }

        @Nullable private DungeonThemeDef theme() {
            JsonObject written = new JsonObject();
            for (Map.Entry<String, JsonElement> held : json.entrySet()) {
                if (!held.getKey().startsWith(DungeonThemeDef.REMOVE_PREFIX)) { written.add(held.getKey(), held.getValue()); }
            }
            Json.unknownKeys(written, "the top level", THEME_KEYS);
            List<ResourceLocation> append = targets();
            Map<String, List<BlockWeightDef>> blocks = blocks();
            Map<String, List<String>> removals = removals();
            if (append.isEmpty() && blocks.getOrDefault("walls", Collections.emptyList()).isEmpty()) {
                ContentLog.LOGGER.error("Dungeon theme {} has no blocks.walls, ignoring it", key);
                return null;
            }
            if (append.isEmpty() && !removals.isEmpty()) { ContentLog.LOGGER.error("Dungeon theme {} uses remove: keys but has no append, ignoring them", key); }
            return new DungeonThemeDef(key, Json.text(json, "name", key.getPath()), integer("weight", 1, Integer.MAX_VALUE), dimensions(), Json.flag(json, "dimensionsAreBlacklist"),
                    picks("biomes"), picks("biomeTypes"), picks("notBiomes"), picks("notBiomeTypes"), integer("minY", Integer.MIN_VALUE, Integer.MAX_VALUE), integer("maxY", Integer.MIN_VALUE, Integer.MAX_VALUE),
                    blocks, mobs(), spawnGroups(), loot(), decorators(), json.has("rolls") ? ContentParserDungeons.rolls(owner, json) : null,
                    json.has("difficulty") ? ContentParserDungeons.difficulty(owner, json.get("difficulty")) : null, json.has("decay") ? ContentParserDungeons.decay(owner, json.get("decay")) : null,
                    integer("lootLevels", 1, DungeonThemeDef.MAX_LOOT_LEVELS), integer("lootBonus", Integer.MIN_VALUE, Integer.MAX_VALUE), append, removals);
        }

        private List<ResourceLocation> targets() {
            if (!json.has("append")) { return Collections.emptyList(); }
            List<String> names = json.get("append").isJsonArray() ? Json.names(json, "append") : Collections.singletonList(Json.text(json, "append"));
            List<ResourceLocation> targets = new ArrayList<>();
            for (String name : names) {
                if (!name.isEmpty()) { targets.add(new ResourceLocation(name)); }
            }
            if (targets.isEmpty()) { ContentLog.LOGGER.error("Dungeon theme {} has an empty append, ignoring the file", key); }
            return targets;
        }

        @Nullable private Integer integer(String member, int low, int high) {
            if (!json.has(member)) { return null; }
            int value = Json.getInt(json, member, low);
            if (value >= low && value <= high) { return value; }
            int clamped = Math.max(low, Math.min(high, value));
            ContentLog.LOGGER.error("Dungeon theme {} sets {} to {}, which is outside {} to {}, using {}", key, member, value, low, high, clamped);
            return clamped;
        }

        private List<PickDef> picks(String member) { return ContentParserDungeons.picks(owner, json, member, null); }

        private Set<Integer> dimensions() { return Collections.unmodifiableSet(new LinkedHashSet<>(Json.ints(Json.getJsonArray(json, "dimensions", new JsonArray()), "dimensions"))); }

        private int number(String text, String member, String entry) {
            try { return Integer.parseInt(text.trim()); }
            catch (NumberFormatException bad) {
                ContentLog.LOGGER.error("Dungeon theme {} key '{}' has entry '{}' with a weight that is not a number, using 1", key, member, entry);
                return 1;
            }
        }

        private Map<String, List<BlockWeightDef>> blocks() {
            JsonObject object = Json.object(json, "blocks");
            Map<String, List<BlockWeightDef>> blocks = new LinkedHashMap<>();
            Json.unknownKeys(object, "blocks", FAMILIES);
            for (String family : JsonTree.keys(object)) {
                if (FAMILIES.contains(family)) { blocks.put(family, ContentParserWorldgen.weights(object, family)); }
            }
            return Collections.unmodifiableMap(blocks);
        }

        private Map<String, List<DungeonThemeDef.Mob>> mobs() {
            JsonObject object = Json.object(json, "mobs");
            Map<String, List<DungeonThemeDef.Mob>> mobs = new LinkedHashMap<>();
            Json.unknownKeys(object, "mobs", TIERS);
            for (String tier : JsonTree.keys(object)) {
                if (TIERS.contains(tier)) { mobs.put(tier, mobList(object, tier)); }
            }
            return Collections.unmodifiableMap(mobs);
        }

        private List<DungeonThemeDef.Mob> mobList(JsonObject object, String member) {
            List<DungeonThemeDef.Mob> mobs = new ArrayList<>();
            for (String text : Json.strings(object, member)) {
                int brace = text.indexOf('{');
                String head = brace < 0 ? text : text.substring(0, brace);
                int split = head.indexOf('=');
                int weight = split < 0 ? 1 : number(head.substring(split + 1), member, text);
                String id = Settings.lower(split < 0 ? head : head.substring(0, split));
                if (id.isEmpty()) { ContentLog.LOGGER.error("Dungeon theme {} key '{}' has an entry '{}' with no entity id, leaving it out", key, member, text); }
                else { mobs.add(new DungeonThemeDef.Mob(id, Math.max(1, weight), brace < 0 ? null : text.substring(brace).trim())); }
            }
            return Collections.unmodifiableList(mobs);
        }

        private List<DungeonThemeDef.SpawnGroup> spawnGroups() {
            List<DungeonThemeDef.SpawnGroup> groups = new ArrayList<>();
            for (JsonElement element : Json.getJsonArray(json, "spawnGroups", new JsonArray())) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("Dungeon theme {} has a spawnGroups entry that is not an object, leaving it out", key);
                    continue;
                }
                JsonObject group = element.getAsJsonObject();
                Json.unknownKeys(group, "spawnGroups", SPAWN_GROUP_KEYS);
                String name = Json.text(group, "name", "");
                String tier = Settings.lower(Json.text(group, "tier", ""));
                if (name.isEmpty()) {
                    ContentLog.LOGGER.error("Dungeon theme {} has a spawn group with no name, leaving it out", key);
                    continue;
                }
                if (!tier.isEmpty() && !TIERS.contains(tier)) {
                    ContentLog.LOGGER.error("Dungeon theme {} spawn group '{}' has tier '{}', which is not one of {}, using none", key, name, tier, TIERS);
                    tier = "";
                }
                double minDepth = depth(group, "minDepth", 0.0D, name);
                double maxDepth = depth(group, "maxDepth", 1.0D, name);
                if (maxDepth < minDepth) {
                    ContentLog.LOGGER.error("Dungeon theme {} spawn group '{}' has maxDepth below minDepth, swapping them", key, name);
                    double swap = minDepth;
                    minDepth = maxDepth;
                    maxDepth = swap;
                }
                List<String> kinds = new ArrayList<>();
                for (String kind : Json.strings(group, "kinds")) { kinds.add(Settings.lower(kind)); }
                groups.add(new DungeonThemeDef.SpawnGroup(name, mobList(group, "mobs"), Math.max(1, Json.number(group, "weight", 1)), minDepth, maxDepth, Collections.unmodifiableList(kinds), tier.isEmpty() ? null : tier));
            }
            return Collections.unmodifiableList(groups);
        }

        private double depth(JsonObject group, String member, double fallback, String name) {
            if (!group.has(member)) { return fallback; }
            double value = Json.asDouble(group.get(member), member, fallback);
            if (value >= 0.0D && value <= 1.0D) { return value; }
            ContentLog.LOGGER.error("Dungeon theme {} spawn group '{}' sets {} to {}, which is outside 0 to 1, using {}", key, name, member, value, fallback);
            return fallback;
        }

        private Map<String, List<DungeonThemeDef.LootEntry>> loot() {
            JsonObject object = Json.object(json, "loot");
            Map<String, List<DungeonThemeDef.LootEntry>> loot = new LinkedHashMap<>();
            for (String set : JsonTree.keys(object)) {
                List<DungeonThemeDef.LootEntry> entries = new ArrayList<>();
                for (JsonElement element : Json.getJsonArray(object, set, new JsonArray())) {
                    DungeonThemeDef.LootEntry entry = element.isJsonObject() ? lootEntry(set, element.getAsJsonObject()) : null;
                    if (entry != null) { entries.add(entry); }
                    else if (!element.isJsonObject()) { ContentLog.LOGGER.error("Dungeon theme {} loot set '{}' has an entry that is not an object, leaving it out", key, set); }
                }
                loot.put(set, Collections.unmodifiableList(entries));
            }
            return Collections.unmodifiableMap(loot);
        }

        @Nullable private DungeonThemeDef.LootEntry lootEntry(String set, JsonObject entry) {
            Json.unknownKeys(entry, "loot", LOOT_KEYS);
            String table = Json.text(entry, "table", "");
            int minLevel = Json.number(entry, "minLevel", 0);
            int maxLevel = Json.number(entry, "maxLevel", DungeonThemeDef.LootEntry.ANY_LEVEL);
            if (table.isEmpty()) {
                ContentLog.LOGGER.error("Dungeon theme {} loot set '{}' has an entry with no table, leaving it out", key, set);
                return null;
            }
            if (minLevel < 0 || maxLevel < minLevel) {
                ContentLog.LOGGER.error("Dungeon theme {} loot set '{}' entry {} has minLevel {} and maxLevel {}, which is not a range from 0 up, leaving it out", key, set, table, minLevel, maxLevel);
                return null;
            }
            return new DungeonThemeDef.LootEntry(new ResourceLocation(table), Math.max(1, Json.number(entry, "weight", 1)), minLevel, maxLevel);
        }

        private Map<String, DungeonThemeDef.Decorator> decorators() {
            JsonObject object = Json.object(json, "decorators");
            Map<String, DungeonThemeDef.Decorator> decorators = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> held : object.entrySet()) {
                DungeonThemeDef.Decorator decorator = held.getValue().isJsonObject() ? decorator(held.getKey(), held.getValue().getAsJsonObject()) : null;
                if (decorator != null) { decorators.put(held.getKey(), decorator); }
                else if (!held.getValue().isJsonObject()) { ContentLog.LOGGER.error("Dungeon theme {} decorator '{}' is not an object, leaving it out", key, held.getKey()); }
            }
            return Collections.unmodifiableMap(decorators);
        }

        @Nullable private DungeonThemeDef.Decorator decorator(String name, JsonObject object) {
            Json.unknownKeys(object, "decorators", DECORATOR_KEYS);
            String type = Json.text(object, "type", "");
            if (!DECORATOR_TYPES.contains(type)) {
                ContentLog.LOGGER.error("Dungeon theme {} decorator '{}' has type '{}', which is not one of {}, leaving it out", key, name, type, DECORATOR_TYPES);
                return null;
            }
            String on = Settings.lower(Json.text(object, "on", ""));
            if (!on.isEmpty() && !SURFACES.contains(on)) {
                ContentLog.LOGGER.error("Dungeon theme {} decorator '{}' has on '{}', which is not one of {}, using none", key, name, on, SURFACES);
                on = "";
            }
            List<String> blocks = object.has("block") && object.get("block").isJsonPrimitive() ? Collections.singletonList(object.get("block").getAsString()) : Json.strings(object, "block");
            Map<String, Integer> numbers = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> held : object.entrySet()) {
                String member = held.getKey();
                if (!DECORATOR_NUMBERS.contains(member)) { continue; }
                if (held.getValue().isJsonPrimitive() && held.getValue().getAsJsonPrimitive().isNumber()) { numbers.put(member, held.getValue().getAsInt()); }
                else { ContentLog.LOGGER.warnOnce(WARNED, "key:decorators." + member, "Dungeon theme {} decorator '{}' has key '{}' that is not a number, ignoring it (shown once)", key, name, member); }
            }
            String loot = Json.text(object, "loot", "");
            if ("fill".equals(type) && loot.isEmpty()) {
                ContentLog.LOGGER.error("Dungeon theme {} decorator '{}' is a fill with no loot set, leaving it out", key, name);
                return null;
            }
            return new DungeonThemeDef.Decorator(type, blocks, Collections.unmodifiableMap(numbers), on.isEmpty() ? null : on, loot.isEmpty() ? null : loot);
        }

        private Map<String, List<String>> removals() {
            Map<String, List<String>> removals = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> held : json.entrySet()) {
                if (!held.getKey().startsWith(DungeonThemeDef.REMOVE_PREFIX)) { continue; }
                String target = held.getKey().substring(DungeonThemeDef.REMOVE_PREFIX.length());
                if (!held.getValue().isJsonArray()) {
                    ContentLog.LOGGER.error("Dungeon theme {} key '{}' is not a list, ignoring it", key, held.getKey());
                    continue;
                }
                List<String> names = new ArrayList<>();
                for (JsonElement element : held.getValue().getAsJsonArray()) {
                    String name = identity(element);
                    if (name.isEmpty()) { ContentLog.LOGGER.error("Dungeon theme {} key '{}' has an entry that names nothing, leaving it out", key, held.getKey()); }
                    else { names.add(name); }
                }
                removals.put(Settings.lower(target), Collections.unmodifiableList(names));
            }
            return Collections.unmodifiableMap(removals);
        }

        private String identity(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                for (String member : Arrays.asList("block", "table", "id", "name")) {
                    if (object.has(member)) { return identity(object.get(member)); }
                }
                return "";
            }
            if (!element.isJsonPrimitive()) { return ""; }
            String text = element.getAsString();
            int cut = text.length();
            for (char mark : new char[] {'=', '{'}) {
                int at = text.indexOf(mark);
                if (at >= 0) { cut = Math.min(cut, at); }
            }
            return Settings.lower(text.substring(0, cut));
        }
    }
}
