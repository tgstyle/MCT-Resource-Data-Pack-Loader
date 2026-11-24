package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.dimension.ContentWorldTemplates;
import mctmods.resourcedatapackloader.common.gates.GateLoot;
import mctmods.resourcedatapackloader.common.gates.GateStay;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.DimensionPortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalFrameDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.GalacticraftDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveTerrainDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CityMapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.IntroPageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.PathIntersectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldIntroDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldTemplateDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.portals.PortalShapes;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.rules.ContentGrowWater;
import mctmods.resourcedatapackloader.common.rules.ContentLootSwitch;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.entity.WornCheck;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ContentParserWorlds {
    private static final Set<String> KNOWN_TERRAIN = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END, DimensionDef.CAVE)));
    private static final List<String> AXES = Collections.unmodifiableList(Arrays.asList(PortalFrameDef.VERTICAL, PortalFrameDef.HORIZONTAL, PortalFrameDef.BOTH));
    private static final List<String> RETURNS = Collections.unmodifiableList(Arrays.asList(DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE));
    private static final List<String> DIRECTIONS = Collections.unmodifiableList(Arrays.asList(IntroPageDef.UP, IntroPageDef.DOWN));
    private static final List<String> SOURCES = Collections.unmodifiableList(Arrays.asList(DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.LIST));
    static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.GLOBAL, GateDef.TEAM));
    private static final List<String> TEAMLESS = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.REFUSE));
    private static final List<String> TALLIES = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.TEAM));
    private static final List<String> STANDINGS = Collections.unmodifiableList(Arrays.asList(GateDef.ALONE, GateDef.WITH));
    private static final List<String> GATE_KEYS = Collections.unmodifiableList(Arrays.asList("scope", "dimension", "name", "teamless", "open", "unlock", "portalBlocks", "blockedMessage", "unlockedMessage", "safeReturn", "requires", "stayMinutes", "stayReset", "stayWarn", "stayMessage", "onExpire", "dropOnLeave"));
    private static final List<String> UNLOCK_KEYS = Collections.unmodifiableList(Arrays.asList("craft", "consume", "consumeCount", "hold", "advancement", "killed", "killedCount", "killedDrops", "killedTally", "killedRank", "scoreAtLeast", "team", "objective", "dietAtLeast", "dietCount", "teamAndScore", "worn", "each", "data"));
    private static final List<String> DIMENSION_KEYS = Collections.unmodifiableList(Arrays.asList("id", "suffix", "keepLoaded", "requires", "terrain", "biomes", "sky", "gameRules", "portal", "galacticraft", "physics", "time", "weather", "ambience", "darkDamage", "environments", "seasons", "inherits", "variants"));
    private static final List<String> TERRAIN_KEYS = Collections.unmodifiableList(Arrays.asList("type", "generatorOptions", "structures", "seaLevel", "fillBlock", "seaFluid"));
    private static final List<String> BIOMES_KEYS = Collections.unmodifiableList(Arrays.asList("biome", "source", "biomeList"));
    private static final List<String> PORTAL_KEYS = Collections.unmodifiableList(Arrays.asList("frames", "color", "return", "ignitedByFluid", "ignitedBy", "returnDimension", "gate", "cooldown", "platform", "platformBlock", "sound", "owned", "walkIn", "landing", "torches", "useRange", "mounted", "returnTo"));
    private static final String ROLES = "rlsc.";

    private ContentParserWorlds() {}

    @Nullable public static PortalFrameDef portalFrame(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "portal frame file");
        if (json == null) { return null; }
        Map<Character, IBlockState> legend = legend(key, json, "" + PortalFrameDef.HOLE + PortalFrameDef.REPEAT, "Portal frame");
        List<String> rows = strings(json, "rows");
        if (rows.isEmpty()) {
            ContentLog.LOGGER.error("Portal frame {} draws no rows, so there is no frame to find", key);
            return null;
        }
        boolean holed = false;
        for (String row : rows) {
            for (char held : row.toCharArray()) {
                if (held == PortalFrameDef.HOLE) { holed = true; }
                if (held == PortalFrameDef.HOLE || held == PortalFrameDef.SKIP || held == PortalFrameDef.REPEAT || legend.containsKey(held)) { continue; }
                ContentLog.LOGGER.error("Portal frame {} uses '{}', which is neither a hole, a gap, a repeat nor in the legend", key, held);
                return null;
            }
        }
        boolean repeating = false;
        for (String row : rows) { repeating |= row.indexOf(PortalFrameDef.REPEAT) >= 0; }
        if (!repeating) {
            for (String row : rows) {
                if (row.length() == rows.get(0).length()) { continue; }
                ContentLog.LOGGER.error("Portal frame {} has rows of different lengths and no repeat mark, so it is skipped", key);
                return null;
            }
        }
        if (!holed) {
            ContentLog.LOGGER.error("Portal frame {} has no '{}' in it, so nothing would ever stand inside it", key, PortalFrameDef.HOLE);
            return null;
        }
        String axis = Json.choice(json, "axis", PortalFrameDef.VERTICAL, "Portal frame " + key, AXES);
        PortalFrameDef frame = new PortalFrameDef(key, Json.getString(json, "name", key.getPath()), axis, legend, rows,
                Json.atLeast(json, "maxWidth", 21, PortalFrameDef.LEAST_WIDE),
                Json.atLeast(json, "maxHeight", 21, PortalFrameDef.LEAST_TALL));
        if (PortalShapes.spread(frame).isEmpty()) {
            ContentLog.LOGGER.error("Portal frame {} never leaves room for a player, who needs a hole {} across and {} up, so nothing could walk through it", key, PortalFrameDef.LEAST_WIDE, frame.leastTall());
            return null;
        }
        return frame;
    }

    @Nullable private static DimensionPortalDef dimensionPortal(ResourceLocation key, JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "portal");
        List<String> frames = strings(entry, "frames");
        if (frames.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has a portal section naming no frames, so nothing could ever open it", key);
            return null;
        }
        int color = ContentTypes.color(Json.getString(entry, "color", "#FFFFFF"), key + " portal color");
        String back = Json.choice(entry, "return", DimensionPortalDef.BUILT, "Dimension " + key + " portal", RETURNS);
        PortalDef travel = ContentParser.portal(entry, Json.getInt(json, "id"), false, true);
        String fluid = Json.trimmed(entry, "ignitedByFluid", "");
        String item = entry.has("ignitedBy") ? Json.trimmed(entry, "ignitedBy", "") : fluid.isEmpty() ? "minecraft:flint_and_steel" : "";
        return new DimensionPortalDef(frames, item, fluid, color, back, travel);
    }

    private static Map<Character, IBlockState> legend(ResourceLocation key, JsonObject json, String forbidden, String what) {
        Map<Character, IBlockState> legend = new LinkedHashMap<>();
        if (!json.has("legend")) { return legend; }
        JsonObject entry = Json.getJsonObject(json, "legend");
        for (Map.Entry<String, JsonElement> mark : entry.entrySet()) {
            String symbol = mark.getKey().trim();
            if (!symbolAllowed(symbol, forbidden)) {
                ContentLog.LOGGER.error("{} {} legend symbol '{}' must be a single character and not one of {}", what, key, mark.getKey(), forbidden);
                continue;
            }
            IBlockState state = ContentStates.parse(mark.getValue().getAsString(), key + " legend " + symbol);
            if (state != null) { legend.put(symbol.charAt(0), state); }
        }
        return legend;
    }

    private static void roles(ResourceLocation key, Map<Character, IBlockState> legend, List<String> rows, String part) {
        for (String row : rows) {
            for (char held : row.toCharArray()) {
                if (ROLES.indexOf(held) < 0 && !legend.containsKey(held)) { ContentLog.LOGGER.error("Path intersect {} {} uses '{}', which is neither a role letter nor in the legend", key, part, held); }
            }
        }
    }

    @Nullable public static PathIntersectDef pathIntersect(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "path intersect design");
        if (json == null) { return null; }
        Map<Character, IBlockState> legend = legend(key, json, ROLES, "Path intersect");
        List<String> mouth = strings(json, "mouth");
        List<String> corner = strings(json, "corner");
        roles(key, legend, mouth, "mouth");
        roles(key, legend, corner, "corner");
        return new PathIntersectDef(Json.atLeast(json, "weight", 1, 1), legend, mouth.toArray(new String[0]), corner.toArray(new String[0]));
    }

    @Nullable public static StructureMapDef structureMap(ResourceLocation key, String contents) {
        JsonObject json = Json.strictObject(contents);
        if (json == null) { return null; }
        if (!json.has("layers")) {
            ContentLog.LOGGER.error("Structure map {} has no layers, so it is dropped", key);
            return null;
        }
        JsonArray held = Json.getJsonArray(json, "layers");
        if (held.size() < 1 || held.size() > StructureMapDef.LIMIT) {
            ContentLog.LOGGER.error("Structure map {} has {} layer(s), the most being {}, so it is dropped", key, held.size(), StructureMapDef.LIMIT);
            return null;
        }
        StructureMapDef.Layer[] layers = new StructureMapDef.Layer[held.size()];
        for (int at = 0; at < held.size(); at++) {
            if (!held.get(at).isJsonObject()) {
                ContentLog.LOGGER.error("Structure map {} layer {} is not an object, so the map is dropped", key, at);
                return null;
            }
            JsonObject entry = held.get(at).getAsJsonObject();
            Map<Character, List<PickDef>> palette = palette(entry, "Structure map " + key + " layer " + at, (symbol, mark) -> picks(mark));
            if (palette == null) { return null; }
            List<String> rows = strings(entry, "map");
            if (rows.isEmpty() || rows.size() > StructureMapDef.LIMIT) {
                ContentLog.LOGGER.error("Structure map {} layer {} holds {} map row(s), the most being {}, so the map is dropped", key, at, rows.size(), StructureMapDef.LIMIT);
                return null;
            }
            if (badRows("Structure map " + key + " layer " + at, rows, StructureMapDef.LIMIT, palette.keySet())) { return null; }
            layers[at] = new StructureMapDef.Layer(palette, rows.toArray(new String[0]));
        }
        int ground = MathHelper.clamp(Json.getInt(json, "ground", 0), 0, layers.length - 1);
        int cell = MathHelper.clamp(Json.getInt(json, "cell", 32), 1, 48);
        int spacing = Json.atLeast(json, "spacing", 0, 0);
        int chance = MathHelper.clamp(Json.getInt(json, "chance", 100), 1, 100);
        int[] pinned = null;
        if (json.has("at")) {
            JsonArray spot = Json.getJsonArray(json, "at");
            if (spot.size() == 2) { pinned = new int[] { Json.asInt(spot.get(0), "at", 0), Json.asInt(spot.get(1), "at", 0) }; }
            else { ContentLog.LOGGER.error("Structure map {} pins 'at' with {} number(s) instead of x and z, so the pin is ignored", key, spot.size()); }
        }
        Set<Integer> dimensions = new LinkedHashSet<>(Json.dimensions(json));
        StructureMapDef def = new StructureMapDef(key, cell, ground, spacing, chance, pinned, dimensions, layers, mapDepth(key, json), mapSpawners(key, json), mapLoot(key, json), ContentParserRespawn.parse(key, json));
        if (spacing > 0 && spacing * 16 < def.widest) {
            ContentLog.LOGGER.error("Structure map {} asks for a spacing of {} chunk(s) but spans {} block(s), so it is dropped rather than overlapping itself", key, spacing, def.widest);
            return null;
        }
        return def;
    }

    @Nullable private static int[] mapDepth(ResourceLocation key, JsonObject json) {
        if (!json.has("depth")) { return null; }
        JsonElement held = json.get("depth");
        List<Integer> numbers = new ArrayList<>();
        if (held.isJsonArray()) { for (JsonElement entry : held.getAsJsonArray()) { numbers.add(Json.asInt(entry, "depth", -1)); } }
        else { numbers.add(Json.asInt(held, "depth", -1)); }
        boolean fine = numbers.size() == 1 || numbers.size() == 2;
        for (int number : numbers) { fine &= number >= 0; }
        if (!fine) {
            ContentLog.LOGGER.error("Structure map {} gives a depth that is not one or two numbers of 0 or more, so the depth is ignored", key);
            return null;
        }
        int least = numbers.get(0);
        int most = numbers.get(numbers.size() - 1);
        return new int[] { Math.min(least, most), Math.max(least, most) };
    }

    private static List<PickDef> mapSpawners(ResourceLocation key, JsonObject json) {
        List<PickDef> picks = new ArrayList<>();
        JsonElement held = json.get("spawners");
        if (held == null) { return picks; }
        if (held.isJsonArray()) {
            for (String choice : Json.texts(held.getAsJsonArray(), "spawners")) { picks.add(weighted(choice)); }
        }
        else if (held.isJsonPrimitive()) { picks.add(weighted(held.getAsString())); }
        else { ContentLog.LOGGER.error("Structure map {} gives spawners that are not a mob id or a list of them, so they are ignored", key); }
        return Collections.unmodifiableList(picks);
    }

    @Nullable private static ResourceLocation mapLoot(ResourceLocation key, JsonObject json) {
        if (!json.has("loot")) { return null; }
        String text = Json.getString(json, "loot", "");
        ResourceLocation loot = ContentLootSwitch.id(text);
        if (loot == null) { ContentLog.LOGGER.error("Structure map {} gives loot '{}', which is not a loot table id, so the loot is ignored", key, text); }
        return loot;
    }

    @Nullable public static CityMapDef cityMap(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "city map");
        if (json == null) { return null; }
        List<String> rows = strings(json, "map");
        Map<Character, CityMapDef.Cell> palette = palette(json, "City map " + key, (symbol, mark) -> cell(key, symbol, mark));
        if (palette == null) { return null; }
        if (badRows("City map " + key, rows, CityMapDef.LIMIT, palette.keySet())) { return null; }
        int cell = MathHelper.clamp(Json.getInt(json, "cell", 48), 8, 128);
        JsonObject settings = null;
        if (json.has("settings")) {
            if (json.get("settings").isJsonObject()) {
                settings = json.getAsJsonObject("settings");
                ContentControl.warnUnknown("City map " + key, settings);
            }
            else { ContentLog.LOGGER.error("City map {} has settings that are not an object of setting names, so it keeps the world template's", key); }
        }
        return new CityMapDef(key, cell, palette, rows.toArray(new String[0]), settings);
    }

    @Nullable private static <V> Map<Character, V> palette(JsonObject json, String where, BiFunction<String, Map.Entry<String, JsonElement>, V> mark) {
        Map<Character, V> palette = new LinkedHashMap<>();
        if (!json.has("palette")) { return palette; }
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "palette").entrySet()) {
            String symbol = entry.getKey().trim();
            if (badSymbol(where, symbol, entry.getKey())) { return null; }
            palette.put(symbol.charAt(0), mark.apply(symbol, entry));
        }
        return palette;
    }

    private static List<PickDef> picks(Map.Entry<String, JsonElement> mark) {
        List<PickDef> picks = new ArrayList<>();
        if (mark.getValue().isJsonArray()) {
            for (String choice : Json.texts(mark.getValue().getAsJsonArray(), mark.getKey())) { picks.add(weighted(choice)); }
        }
        else { picks.add(weighted(mark.getValue().getAsString())); }
        return Collections.unmodifiableList(picks);
    }

    private static CityMapDef.Cell cell(ResourceLocation key, String symbol, Map.Entry<String, JsonElement> mark) {
        List<String> names = new ArrayList<>();
        int height = CityMapDef.LIFT;
        JsonObject roadKeys = null;
        if (mark.getValue().isJsonArray()) {
            for (String choice : Json.texts(mark.getValue().getAsJsonArray(), mark.getKey())) { names.add(choice.trim()); }
        }
        else if (mark.getValue().isJsonObject()) {
            JsonObject described = mark.getValue().getAsJsonObject();
            names.add(Json.trimmed(described, "kind", ""));
            height = MathHelper.clamp(Json.getInt(described, "height", CityMapDef.LIFT), 2, 64);
            if (described.has("settings") && described.get("settings").isJsonObject()) {
                roadKeys = described.getAsJsonObject("settings");
                ContentControl.warnUnknown("City map " + key + " mark '" + symbol + "'", roadKeys);
            }
        }
        else { names.add(mark.getValue().getAsString().trim()); }
        CityMapDef.Kind kind = CityMapDef.Kind.PLOT;
        List<PickDef> picks = new ArrayList<>();
        if (names.size() == 1) {
            switch (names.get(0).toLowerCase(Locale.ROOT)) {
                case "street": kind = CityMapDef.Kind.STREET; break;
                case "plaza": kind = CityMapDef.Kind.PLAZA; break;
                case "alley": kind = CityMapDef.Kind.ALLEY; break;
                case "open": kind = CityMapDef.Kind.OPEN; break;
                case "grow": kind = CityMapDef.Kind.GROW; break;
                case "junction": kind = CityMapDef.Kind.JUNCTION; break;
                case "bulb": kind = CityMapDef.Kind.BULB; break;
                case "elevated": kind = CityMapDef.Kind.ELEVATED; break;
                default: break;
            }
        }
        if (kind == CityMapDef.Kind.PLOT) {
            for (String name : names) { picks.add(weighted(name)); }
        }
        return new CityMapDef.Cell(kind, picks, height, roadKeys);
    }

    private static boolean badSymbol(String what, String symbol, String written) {
        if (symbolAllowed(symbol, ".")) { return false; }
        ContentLog.LOGGER.error("{} palette symbol '{}' must be a single character other than '.', so the map is dropped", what, written);
        return true;
    }

    private static boolean symbolAllowed(String symbol, String forbidden) { return symbol.length() == 1 && forbidden.indexOf(symbol.charAt(0)) < 0; }

    private static boolean badRows(String what, List<String> rows, int limit, Set<Character> palette) {
        for (String row : rows) {
            if (row.length() > limit) {
                ContentLog.LOGGER.error("{} row '{}' is {} cell(s) long, the most being {}, so the map is dropped", what, row, row.length(), limit);
                return true;
            }
            for (char cell : row.toCharArray()) {
                if (cell != '.' && !palette.contains(cell)) {
                    ContentLog.LOGGER.error("{} uses '{}', which is not in its palette, so the map is dropped", what, cell);
                    return true;
                }
            }
        }
        return false;
    }

    private static PickDef weighted(String entry) {
        Settings.Weighted split = Settings.weighted(entry, 1);
        return new PickDef(split.name.toLowerCase(Locale.ROOT), split.weight);
    }

    @Nullable public static WorldTemplateDef worldTemplate(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "world template");
        if (json == null) { return null; }
        Map<String, String> roles = new LinkedHashMap<>();
        if (json.has("roles")) {
            JsonObject entry = Json.getJsonObject(json, "roles");
            for (Map.Entry<String, JsonElement> role : entry.entrySet()) {
                String name = Settings.lower(role.getKey());
                if (!ContentWorldTemplates.knownRoles().containsKey(name)) {
                    ContentLog.LOGGER.error("World template {} names role '{}', which is not one of {}, ignoring it", key, role.getKey(), ContentWorldTemplates.describeRoles());
                    continue;
                }
                if (!role.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("World template {} sets role '{}' to something that is not a biome name, ignoring it", key, role.getKey());
                    continue;
                }
                roles.put(name, role.getValue().getAsString());
            }
        }
        return new WorldTemplateDef(key,
                Json.getString(json, "name", key.getPath()),
                Json.getString(json, "default", WorldTemplateDef.VOID),
                Collections.unmodifiableMap(roles),
                structures(key, json),
                json.has("settings") ? Json.getJsonObject(json, "settings") : null,
                Json.dimensions(json),
                strings(json, "requires"));
    }

    @Nullable public static WorldIntroDef worldIntro(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "world intro file");
        if (json == null) { return null; }
        List<IntroPageDef> pages = new ArrayList<>();
        if (json.has("pages")) {
            for (JsonElement element : Json.getJsonArray(json, "pages")) {
                IntroPageDef page = introPage(key, element.getAsJsonObject());
                if (page != null) { pages.add(page); }
            }
        }
        if (pages.isEmpty()) {
            ContentLog.LOGGER.error("World intro {} names no pages, ignoring it", key);
            return null;
        }
        return new WorldIntroDef(Json.getBoolean(json, "once", false),
                Json.location(json, "music"),
                Collections.unmodifiableList(pages),
                strings(json, "requires"));
    }

    @Nullable private static IntroPageDef introPage(ResourceLocation key, JsonObject json) {
        List<ResourceLocation> backgrounds = new ArrayList<>();
        ResourceLocation single = Json.location(json, "background");
        if (single != null) { backgrounds.add(single); }
        for (String name : strings(json, "backgrounds")) { backgrounds.add(new ResourceLocation(name)); }
        String mode = Settings.lower(Json.getString(json, "mode", IntroPageDef.SCROLL));
        if (!IntroPageDef.SCROLL.equals(mode) && !IntroPageDef.STATIC.equals(mode)) {
            ContentLog.LOGGER.error("World intro {} has a page with mode '{}', which is neither '{}' nor '{}', ignoring the page", key, mode, IntroPageDef.SCROLL, IntroPageDef.STATIC);
            return null;
        }
        String direction = Json.choice(json, "direction", IntroPageDef.UP, "World intro " + key + " page", DIRECTIONS);
        return new IntroPageDef(Collections.unmodifiableList(backgrounds),
                Json.getFloat(json, "interval", 5.0F),
                Json.location(json, "text"),
                mode,
                Json.getFloat(json, "time", IntroPageDef.DERIVE),
                direction,
                Json.getFloat(json, "textScale", 1.0F),
                Json.getBoolean(json, "settle", false));
    }

    public static Map<Integer, Map<String, String>> gameRuleFile(ResourceLocation key, String contents) {
        Map<Integer, Map<String, String>> found = new LinkedHashMap<>();
        JsonObject json = Json.strictObject(contents);
        if (json == null) { return found; }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            int dimension;
            try { dimension = Integer.parseInt(entry.getKey().trim()); }
            catch (NumberFormatException broken) {
                ContentLog.LOGGER.error("Game rule file {} has key '{}', which is not a dimension id, ignoring it", key, entry.getKey());
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Game rule file {} maps dimension {} to something that is not a set of rules, ignoring it", key, dimension);
                continue;
            }
            found.put(dimension, rules(key, entry.getValue().getAsJsonObject()));
        }
        return found;
    }

    private static Map<String, String> gameRules(ResourceLocation key, JsonObject json) {
        if (!json.has("gameRules")) { return Collections.emptyMap(); }
        return Collections.unmodifiableMap(rules(key, Json.getJsonObject(json, "gameRules")));
    }

    private static Map<String, String> rules(ResourceLocation key, JsonObject object) {
        return Json.primitives(object, rule -> ContentLog.LOGGER.error("{} sets game rule '{}' to something that is not a value, ignoring it", key, rule));
    }

    public static void dimensionKeys(JsonObject json) {
        Json.unknownKeys(json, "the top level", DIMENSION_KEYS);
        level(json, "terrain", TERRAIN_KEYS);
        level(json, "biomes", BIOMES_KEYS);
        level(json, "portal", PORTAL_KEYS);
        level(json, "sky", ContentParserSkyLook.SKY_KEYS);
    }

    private static void level(JsonObject json, String member, List<String> read) {
        if (json.has(member) && json.get(member).isJsonObject()) { Json.unknownKeys(json.getAsJsonObject(member), member, read); }
    }

    public static DimensionDef dimension(ResourceLocation key, JsonObject json) {
        JsonObject terrain = Json.getJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = Json.getJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = Json.getJsonObject(json, "sky", new JsonObject());
        String skyColor = Json.trimmed(sky, "skyColor", "");
        String cloudColor = Json.trimmed(sky, "cloudColor", "");
        String owner = "Dimension " + key;
        String type = Json.choice(terrain, "type", DimensionDef.OVERWORLD, owner + " terrain", KNOWN_TERRAIN);
        List<String> biomeList = strings(biomes, "biomeList");
        String chosen = Json.choice(biomes, "source", DimensionDef.INHERIT, owner + " biomes", SOURCES);
        boolean empty = DimensionDef.LIST.equals(chosen) && biomeList.isEmpty();
        if (empty) { ContentLog.LOGGER.error("{} biomes asks for source '{}' but gives no biomeList, using {}", owner, chosen, DimensionDef.INHERIT); }
        String source = empty ? DimensionDef.INHERIT : chosen;
        GalacticraftDef galacticraft = ContentParserCelestial.galacticraft(key, json);
        boolean belt = galacticraft != null && CelestialDef.ASTEROIDS.equals(galacticraft.body.kind);
        String fog = ContentParserSkyLook.samples(sky) ? "" : Json.getString(sky, "fogColor", belt ? "000000" : "");
        int cloudHeight = Json.getInt(sky, "cloudHeight", 128);
        int clouds = ContentTypes.optionalColor(cloudColor, key.toString());
        DimensionTraitsDef traits = ContentParserDimensionTraits.traits(key, json, belt);
        return new DimensionDef(key,
                Json.getInt(json, "id"),
                Json.getString(json, "suffix", "DIM_" + key.getPath()),
                Json.getBoolean(json, "keepLoaded", false),
                type,
                Json.getString(terrain, "generatorOptions", ""),
                DimensionDef.CAVE.equals(type) ? cave(key, terrain) : null,
                Json.getBoolean(terrain, "structures", true),
                source,
                Json.getString(biomes, "biome", "minecraft:plains"),
                biomeList,
                Json.getBoolean(sky, "hasSkyLight", true),
                Json.getBoolean(sky, "surfaceWorld", true),
                Json.getBoolean(sky, "respawn", true),
                Json.getBoolean(sky, "spawning", true),
                cloudHeight,
                Json.getInt(sky, "groundLevel", 63),
                Json.getFloat(sky, "movementFactor", 1.0F),
                ContentTypes.optionalColor(fog, key.toString()),
                ContentTypes.optionalColor(skyColor, key.toString()),
                Json.getInt(sky, "fixedTime", -1),
                Json.getBoolean(sky, "sunriseColors", !belt),
                Json.getBoolean(sky, "nether", false),
                Json.getBoolean(sky, "beds", true),
                Json.getBoolean(sky, "waterVaporizes", false),
                Json.getBoolean(sky, "showFog", false),
                Json.chance(sky, "ambientLight", 0.0F),
                Json.getFloat(sky, "starBrightness", -1.0F),
                clouds,
                Json.getInt(sky, "respawnDimension", DimensionDef.NO_RESPAWN_DIMENSION),
                Json.getBoolean(sky, "renderSky", true),
                Json.getBoolean(sky, "renderClouds", !belt),
                Json.getBoolean(sky, "renderWeather", true),
                gameRules(key, json),
                strings(json, "requires"),
                dimensionPortal(key, json),
                galacticraft,
                traits,
                ContentParserSkyLook.look(key, sky, cloudHeight, clouds),
                ContentParserTimeWindows.windows(key, json, traits.precipitation),
                ContentParserDarkness.darkness(key, sky),
                Json.paths(json));
    }

    private static CaveTerrainDef cave(ResourceLocation key, JsonObject terrain) {
        if (terrain.has("generatorOptions")) { ContentLog.LOGGER.error("Dimension {} terrain is '{}', which does not read generatorOptions, ignoring it", key, DimensionDef.CAVE); }
        int asked = Json.getInt(terrain, "seaLevel", 32);
        int level = MathHelper.clamp(asked, 0, 255);
        if (level != asked) { ContentLog.LOGGER.error("Dimension {} terrain sets seaLevel to {}, which is outside 0 to 255, using {}", key, asked, level); }
        return new CaveTerrainDef(Json.trimmed(terrain, "fillBlock", ContentParser.STONE), level, Json.trimmed(terrain, "seaFluid", ContentGrowWater.WATER));
    }

    private static Map<String, Boolean> structures(ResourceLocation key, JsonObject json) {
        Map<String, Boolean> settings = new LinkedHashMap<>();
        if (!json.has("structures")) { return Collections.unmodifiableMap(settings); }
        JsonObject entry = Json.getJsonObject(json, "structures");
        for (Map.Entry<String, JsonElement> value : entry.entrySet()) {
            String name = ContentStructures.normalize(value.getKey());
            if (ContentStructures.unknown(name)) {
                ContentLog.LOGGER.error("World template {} names structure '{}', which is not one of {}, ignoring it", key, value.getKey(), ContentStructures.describe());
                continue;
            }
            settings.put(name, value.getValue().getAsBoolean());
        }
        return Collections.unmodifiableMap(settings);
    }

    @Nullable public static GateDef gate(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "gate file");
        if (json == null) { return null; }
        String owner = "Gate " + key;
        String scope = Json.choice(json, "scope", GateDef.PLAYER, owner, SCOPES);
        Json.unknownKeys(json, "the top level", GATE_KEYS);
        JsonObject unlock = Json.getJsonObject(json, "unlock", new JsonObject());
        Json.unknownKeys(unlock, "unlock", UNLOCK_KEYS);
        int dimension = Json.getInt(json, "dimension", GateDef.NO_DIMENSION);
        return new GateDef(key,
                dimension,
                Json.getString(json, "name", key.getPath()),
                scope,
                Json.choice(json, "teamless", GateDef.PLAYER, owner, TEAMLESS),
                Json.getBoolean(json, "open", false),
                Json.getString(unlock, "craft", ""),
                Json.getString(unlock, "consume", ""),
                Json.atLeast(unlock, "consumeCount", 1, 1),
                Json.getString(unlock, "hold", ""),
                Json.getString(unlock, "advancement", ""),
                Json.getString(unlock, "killed", ""),
                Json.atLeast(unlock, "killedCount", 1, 1),
                Json.getString(unlock, "killedDrops", ""),
                Json.choice(unlock, "killedTally", GateDef.PLAYER, owner, TALLIES),
                IdNames.namespaced(Json.trimmed(unlock, "killedRank", ""), key.getNamespace()),
                Standing.parse(unlock),
                Json.choice(unlock, "teamAndScore", GateDef.ALONE, owner, STANDINGS),
                strings(json, "portalBlocks"),
                Json.getString(json, "blockedMessage", "You need %item% to enter %dim%"),
                Json.getString(json, "unlockedMessage", "%dim% is now open"),
                Json.getBoolean(json, "safeReturn", false),
                strings(json, "requires"),
                WornCheck.parse(unlock, key, "Gate"),
                GateStay.parse(json, key, dimension),
                GateLoot.parse(json, key, dimension),
                unlock.has("data") ? DataCheck.parse(unlock.get("data"), "Gate", "unlock", key) : Collections.emptyList());
    }
}
