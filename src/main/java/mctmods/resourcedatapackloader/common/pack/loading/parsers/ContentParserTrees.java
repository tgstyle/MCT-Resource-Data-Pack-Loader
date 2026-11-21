package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreePickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeRuleDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ContentParserTrees {
    private static final String TREES = "trees";
    private static final String STRUCTURE = "structure";
    private static final String VANILLA = "vanilla";
    private static final String MINECRAFT = "minecraft:";
    private static final String SAPLINGS = "saplings";
    private static final int MIN_STEPS = 4;
    private static final int MAX_STEPS = 64;
    public static final int SAPLING_CHANCE = 7;
    private static final List<String> BLOCKED = Arrays.asList("wait", "reset", "drop");
    private static final List<String> GROW_BY = Arrays.asList("chance", "time");
    private static final List<String> CATCH_UP = Arrays.asList("world", "pause");
    private static final List<String> BONEMEAL = Arrays.asList("step", "time", "off");
    private static final List<String> RULE_IGNORED = Arrays.asList("stages", "soil", "log", "leaves", "height", STRUCTURE, "structures");
    private static final List<String> RULE_KEYS = Collections.unmodifiableList(Arrays.asList("requires", "saplings", "chance", "steps", "growBy", "stepTicks", "blocked", "catchUp", "catchUpLimit", "bonemeal", "bonemealTicks", "bonemealChecks", "biomes", "needsSky", "needsWater", "waterRange", "light", TREES, "stages", "soil", "log", "leaves", "height", STRUCTURE, "structures"));
    private static final List<String> TREE_KEYS = Collections.unmodifiableList(Arrays.asList("own", STRUCTURE, VANILLA, "turns", "turn", "mirrors", "mirror", "weight", "inSteps"));
    private static final List<PickDef> NO_TURN = Collections.singletonList(new PickDef("none", 1));

    private ContentParserTrees() {}

    static TreeGrowthDef growth(JsonObject sapling) {
        if (!sapling.has(TREES)) { return TreeGrowthDef.NONE; }
        String owner = "Sapling growth";
        if (sapling.has(STRUCTURE) || sapling.has("structures")) { ContentLog.LOGGER.warn("{} gives trees, so its structure and structures are ignored", owner); }
        return read(sapling, owner, 1.0 / Math.max(1, Json.getInt(sapling, "chance", SAPLING_CHANCE)));
    }

    @Nullable public static TreeRuleDef rule(ResourceLocation key, JsonObject json) {
        String owner = "Tree growth rule " + key;
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        if (!json.has(SAPLINGS)) {
            ContentLog.LOGGER.error("{} names no saplings, ignoring it", owner);
            return null;
        }
        if (json.size() == 1) {
            ContentLog.LOGGER.warn("{} has no key besides saplings, so it changes nothing", owner);
            return null;
        }
        List<String> ignored = new ArrayList<>();
        for (String name : RULE_IGNORED) {
            if (json.has(name)) { ignored.add(name); }
        }
        if (!ignored.isEmpty()) { ContentLog.LOGGER.warn("{} gives {}, which a rule does not read", owner, ignored); }
        List<BlockMatchDef> targets = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        JsonElement held = json.get(SAPLINGS);
        if (held.isJsonArray()) {
            for (JsonElement element : held.getAsJsonArray()) { target(key, element, targets, tags); }
        }
        else { target(key, held, targets, tags); }
        if (targets.isEmpty() && tags.isEmpty()) {
            ContentLog.LOGGER.error("{} has no usable saplings, ignoring it", owner);
            return null;
        }
        return new TreeRuleDef(key.toString(), Collections.unmodifiableList(targets), Collections.unmodifiableList(tags), read(json, owner, json.has("chance") ? MathHelper.clamp(Json.getFloat(json, "chance", 0.0F), 0.0F, 1.0F) : -1.0));
    }

    private static void target(ResourceLocation key, JsonElement element, List<BlockMatchDef> targets, List<String> tags) {
        if (!element.isJsonObject() && !element.isJsonPrimitive()) {
            ContentLog.LOGGER.error("Tree growth rule {} has a sapling target that is neither a name nor an object, skipping it", key);
            return;
        }
        if (element.isJsonPrimitive() && element.getAsString().startsWith("#")) { tags.add(element.getAsString().substring(1)); }
        else { targets.add(ContentParser.match(key, element)); }
    }

    private static TreeGrowthDef read(JsonObject json, String owner, double chance) {
        int steps = Json.getInt(json, "steps", 0);
        if (steps != 0 && (steps < MIN_STEPS || steps > MAX_STEPS)) {
            ContentLog.LOGGER.warn("{} sets steps to {}, outside {} to {}, so it is clamped", owner, steps, MIN_STEPS, MAX_STEPS);
            steps = MathHelper.clamp(steps, MIN_STEPS, MAX_STEPS);
        }
        TreeGrowthDef.GrowBy growBy = choose(json, "growBy", TreeGrowthDef.GrowBy.class, TreeGrowthDef.GrowBy.CHANCE, owner, GROW_BY);
        int stepTicks = Json.getInt(json, "stepTicks", 0);
        if (growBy == TreeGrowthDef.GrowBy.TIME && stepTicks < 1) {
            ContentLog.LOGGER.error("{} grows by time but gives no stepTicks of 1 or more, so it grows by chance instead", owner);
            growBy = TreeGrowthDef.GrowBy.CHANCE;
        }
        return new TreeGrowthDef(trees(json, owner),
                steps,
                choose(json, "blocked", TreeGrowthDef.Blocked.class, TreeGrowthDef.Blocked.WAIT, owner, BLOCKED),
                growBy,
                Math.max(0, stepTicks),
                choose(json, "catchUp", TreeGrowthDef.CatchUp.class, TreeGrowthDef.CatchUp.WORLD, owner, CATCH_UP),
                Math.max(0, Json.getInt(json, "catchUpLimit", 0)),
                choose(json, "bonemeal", TreeGrowthDef.Bonemeal.class, TreeGrowthDef.Bonemeal.UNSET, owner, BONEMEAL),
                Math.max(0, Json.getInt(json, "bonemealTicks", 0)),
                Json.getBoolean(json, "bonemealChecks", false),
                Json.strings(json, "biomes"),
                Json.getBoolean(json, "needsSky", false),
                Json.getBoolean(json, "needsWater", false),
                Math.max(1, Json.getInt(json, "waterRange", 1)),
                chance,
                json.has("light") ? MathHelper.clamp(Json.getInt(json, "light", 0), 0, 15) : -1);
    }

    private static <E extends Enum<E>> E choose(JsonObject json, String member, Class<E> type, E fallback, String owner, List<String> options) {
        String picked = Json.choice(json, member, null, owner, options);
        return picked == null ? fallback : Enum.valueOf(type, picked.toUpperCase(Locale.ROOT));
    }

    private static List<TreePickDef> trees(JsonObject json, String owner) {
        if (!json.has(TREES)) { return Collections.emptyList(); }
        List<TreePickDef> picked = new ArrayList<>();
        int index = 0;
        for (JsonElement element : Json.getJsonArray(json, TREES)) {
            index++;
            String where = owner + " trees entry " + index;
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, dropping it", where);
                continue;
            }
            TreePickDef tree = tree(element.getAsJsonObject(), where);
            if (tree != null) { picked.add(tree); }
        }
        return Collections.unmodifiableList(picked);
    }

    @Nullable private static TreePickDef tree(JsonObject entry, String where) {
        Json.unknownKeys(entry, "trees", TREE_KEYS);
        boolean own = Json.getBoolean(entry, "own", false);
        int named = (entry.has(STRUCTURE) ? 1 : 0) + (entry.has(VANILLA) ? 1 : 0) + (own ? 1 : 0);
        if (named != 1) {
            ContentLog.LOGGER.error("{} must name exactly one of structure, vanilla and own, dropping it", where);
            return null;
        }
        TreePickDef.Kind kind = own ? TreePickDef.Kind.OWN : entry.has(VANILLA) ? TreePickDef.Kind.VANILLA : TreePickDef.Kind.STRUCTURE;
        String source = kind == TreePickDef.Kind.OWN ? "" : Settings.lower(Json.getString(entry, kind == TreePickDef.Kind.VANILLA ? VANILLA : STRUCTURE, "").trim());
        if (kind != TreePickDef.Kind.OWN && source.isEmpty()) {
            ContentLog.LOGGER.error("{} names an empty {}, dropping it", where, kind.name().toLowerCase(Locale.ROOT));
            return null;
        }
        if (kind == TreePickDef.Kind.VANILLA && source.contains(":") && !source.startsWith(MINECRAFT)) {
            ContentLog.LOGGER.error("{} names vanilla '{}', which is not a minecraft id, dropping it", where, source);
            return null;
        }
        List<PickDef> turns = NO_TURN;
        List<PickDef> mirrors = NO_TURN;
        if (kind == TreePickDef.Kind.STRUCTURE) {
            turns = orNone(ContentParser.picks(entry, "turns", "turn"));
            mirrors = orNone(ContentParser.picks(entry, "mirrors", "mirror"));
        }
        else if (entry.has("turns") || entry.has("mirrors")) { ContentLog.LOGGER.warn("{} gives turns or mirrors, which only a structure entry reads, so they are ignored", where); }
        return new TreePickDef(kind, source, Json.getInt(entry, "weight", 1), turns, mirrors, entry.has("inSteps") ? Json.getBoolean(entry, "inSteps", true) : null);
    }

    private static List<PickDef> orNone(List<PickDef> picks) { return picks.isEmpty() ? NO_TURN : picks; }
}
