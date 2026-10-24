package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.portal.PortalShapes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Standing;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.JsonUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentParserWorlds {
    private static final Set<String> KNOWN_TERRAIN = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END)));
    private static final List<String> AXES = Collections.unmodifiableList(Arrays.asList(PortalFrameDef.VERTICAL, PortalFrameDef.HORIZONTAL, PortalFrameDef.BOTH));
    private static final List<String> RETURNS = Collections.unmodifiableList(Arrays.asList(DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE));
    private static final List<String> DIRECTIONS = Collections.unmodifiableList(Arrays.asList(IntroPageDef.UP, IntroPageDef.DOWN));
    private static final List<String> SOURCES = Collections.unmodifiableList(Arrays.asList(DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.LIST));
    private static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.GLOBAL, GateDef.TEAM));
    private static final List<String> TEAMLESS = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.REFUSE));
    private static final List<String> TALLIES = Collections.unmodifiableList(Arrays.asList(GateDef.PLAYER, GateDef.TEAM));
    private static final List<String> STANDINGS = Collections.unmodifiableList(Arrays.asList(GateDef.ALONE, GateDef.WITH));
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
                Math.max(PortalFrameDef.LEAST_WIDE, Json.getInt(json, "maxWidth", 21)),
                Math.max(PortalFrameDef.LEAST_TALL, Json.getInt(json, "maxHeight", 21)));
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
        return new DimensionPortalDef(frames, Json.getString(entry, "ignitedBy", "minecraft:flint_and_steel"), color, back, travel);
    }

    private static Map<Character, IBlockState> legend(ResourceLocation key, JsonObject json, String forbidden, String what) {
        Map<Character, IBlockState> legend = new LinkedHashMap<>();
        if (!json.has("legend")) { return legend; }
        JsonObject entry = Json.getJsonObject(json, "legend");
        for (Map.Entry<String, JsonElement> mark : entry.entrySet()) {
            String symbol = mark.getKey().trim();
            if (symbol.length() != 1 || forbidden.indexOf(symbol.charAt(0)) >= 0) {
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
        return new PathIntersectDef(Math.max(1, Json.getInt(json, "weight", 1)), legend, mouth.toArray(new String[0]), corner.toArray(new String[0]));
    }

    @Nullable public static StructureMapDef structureMap(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(Json.GSON, contents, JsonObject.class);
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
            Map<Character, List<PickDef>> palette = new LinkedHashMap<>();
            if (entry.has("palette")) {
                for (Map.Entry<String, JsonElement> mark : Json.getJsonObject(entry, "palette").entrySet()) {
                    String symbol = mark.getKey().trim();
                    if (badSymbol("Structure map " + key + " layer " + at, symbol, mark.getKey())) { return null; }
                    List<PickDef> picks = new ArrayList<>();
                    if (mark.getValue().isJsonArray()) {
                        for (String choice : Json.texts(mark.getValue().getAsJsonArray(), mark.getKey())) { picks.add(weighted(choice)); }
                    }
                    else { picks.add(weighted(mark.getValue().getAsString())); }
                    palette.put(symbol.charAt(0), Collections.unmodifiableList(picks));
                }
            }
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
        int spacing = Math.max(0, Json.getInt(json, "spacing", 0));
        int chance = MathHelper.clamp(Json.getInt(json, "chance", 100), 1, 100);
        int[] pinned = null;
        if (json.has("at")) {
            JsonArray spot = Json.getJsonArray(json, "at");
            if (spot.size() == 2) { pinned = new int[] { Json.asInt(spot.get(0), "at", 0), Json.asInt(spot.get(1), "at", 0) }; }
            else { ContentLog.LOGGER.error("Structure map {} pins 'at' with {} number(s) instead of x and z, so the pin is ignored", key, spot.size()); }
        }
        Set<Integer> dimensions = new LinkedHashSet<>();
        if (json.has("dimensions")) {
            dimensions.addAll(Json.ints(Json.getJsonArray(json, "dimensions"), "dimensions"));
        }
        StructureMapDef def = new StructureMapDef(key, cell, ground, spacing, chance, pinned, dimensions, layers, mapDepth(key, json), mapSpawners(key, json), mapLoot(key, json));
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
        Map<Character, CityMapDef.Cell> palette = new LinkedHashMap<>();
        if (json.has("palette")) {
            for (Map.Entry<String, JsonElement> mark : Json.getJsonObject(json, "palette").entrySet()) {
                String symbol = mark.getKey().trim();
                if (badSymbol("City map " + key, symbol, mark.getKey())) { return null; }
                List<String> names = new ArrayList<>();
                int height = CityMapDef.LIFT;
                JsonObject roadKeys = null;
                if (mark.getValue().isJsonArray()) {
                    for (String choice : Json.texts(mark.getValue().getAsJsonArray(), mark.getKey())) { names.add(choice.trim()); }
                }
                else if (mark.getValue().isJsonObject()) {
                    JsonObject described = mark.getValue().getAsJsonObject();
                    names.add(Json.getString(described, "kind", "").trim());
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
                palette.put(symbol.charAt(0), new CityMapDef.Cell(kind, picks, height, roadKeys));
            }
        }
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

    private static boolean badSymbol(String what, String symbol, String written) {
        if (symbol.length() == 1 && symbol.charAt(0) != '.') { return false; }
        ContentLog.LOGGER.error("{} palette symbol '{}' must be a single character other than '.', so the map is dropped", what, written);
        return true;
    }

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
        List<Integer> dimensions = new ArrayList<>();
        if (json.has("dimensions")) {
            dimensions.addAll(Json.ints(Json.getJsonArray(json, "dimensions"), "dimensions"));
        }
        return new WorldTemplateDef(key,
                Json.getString(json, "name", key.getPath()),
                Json.getString(json, "default", WorldTemplateDef.VOID),
                Collections.unmodifiableMap(roles),
                structures(key, json),
                json.has("settings") ? Json.getJsonObject(json, "settings") : null,
                Collections.unmodifiableList(dimensions),
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
        String music = Json.getString(json, "music", "").trim();
        return new WorldIntroDef(Json.getBoolean(json, "once", false),
                music.isEmpty() ? null : new ResourceLocation(music),
                Collections.unmodifiableList(pages),
                strings(json, "requires"));
    }

    @Nullable private static IntroPageDef introPage(ResourceLocation key, JsonObject json) {
        List<ResourceLocation> backgrounds = new ArrayList<>();
        String single = Json.getString(json, "background", "").trim();
        if (!single.isEmpty()) { backgrounds.add(new ResourceLocation(single)); }
        for (String name : strings(json, "backgrounds")) { backgrounds.add(new ResourceLocation(name)); }
        String mode = Settings.lower(Json.getString(json, "mode", IntroPageDef.SCROLL));
        if (!IntroPageDef.SCROLL.equals(mode) && !IntroPageDef.STATIC.equals(mode)) {
            ContentLog.LOGGER.error("World intro {} has a page with mode '{}', which is neither '{}' nor '{}', ignoring the page", key, mode, IntroPageDef.SCROLL, IntroPageDef.STATIC);
            return null;
        }
        String direction = Json.choice(json, "direction", IntroPageDef.UP, "World intro " + key + " page", DIRECTIONS);
        String text = Json.getString(json, "text", "").trim();
        return new IntroPageDef(Collections.unmodifiableList(backgrounds),
                Json.getFloat(json, "interval", 5.0F),
                text.isEmpty() ? null : new ResourceLocation(text),
                mode,
                Json.getFloat(json, "time", IntroPageDef.DERIVE),
                direction,
                Json.getFloat(json, "textScale", 1.0F),
                Json.getBoolean(json, "settle", false));
    }

    public static Map<Integer, Map<String, String>> gameRuleFile(ResourceLocation key, String contents) {
        Map<Integer, Map<String, String>> found = new LinkedHashMap<>();
        JsonObject json = JsonUtils.gsonDeserialize(Json.GSON, contents, JsonObject.class);
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
        Map<String, String> found = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> rule : object.entrySet()) {
            if (!rule.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("{} sets game rule '{}' to something that is not a value, ignoring it", key, rule.getKey());
                continue;
            }
            found.put(rule.getKey(), rule.getValue().getAsString());
        }
        return found;
    }

    public static DimensionDef dimension(ResourceLocation key, JsonObject json) {
        JsonObject terrain = Json.getJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = Json.getJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = Json.getJsonObject(json, "sky", new JsonObject());
        String skyColor = Json.getString(sky, "skyColor", "").trim();
        String cloudColor = Json.getString(sky, "cloudColor", "").trim();
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
        int clouds = cloudColor.isEmpty() ? -1 : ContentTypes.color(cloudColor, key.toString());
        DimensionTraitsDef traits = ContentParserDimensionTraits.traits(key, json, belt);
        return new DimensionDef(key,
                Json.getInt(json, "id"),
                Json.getString(json, "suffix", "DIM_" + key.getPath()),
                Json.getBoolean(json, "keepLoaded", false),
                type,
                Json.getString(terrain, "generatorOptions", ""),
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
                fog.isEmpty() ? -1 : ContentTypes.color(fog, key.toString()),
                skyColor.isEmpty() ? -1 : ContentTypes.color(skyColor, key.toString()),
                Json.getInt(sky, "fixedTime", -1),
                Json.getBoolean(sky, "sunriseColors", !belt),
                Json.getBoolean(sky, "nether", false),
                Json.getBoolean(sky, "beds", true),
                Json.getBoolean(sky, "waterVaporizes", false),
                Json.getBoolean(sky, "showFog", false),
                MathHelper.clamp(Json.getFloat(sky, "ambientLight", 0.0F), 0.0F, 1.0F),
                Json.getFloat(sky, "starBrightness", -1.0F),
                clouds,
                Json.getInt(sky, "respawnDimension", Integer.MIN_VALUE),
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
                Json.paths(json));
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
        JsonObject unlock = Json.getJsonObject(json, "unlock", new JsonObject());
        return new GateDef(key,
                Json.getInt(json, "dimension"),
                Json.getString(json, "name", key.getPath()),
                scope,
                Json.choice(json, "teamless", GateDef.PLAYER, owner, TEAMLESS),
                Json.getBoolean(json, "open", false),
                Json.getString(unlock, "craft", ""),
                Json.getString(unlock, "consume", ""),
                Math.max(1, Json.getInt(unlock, "consumeCount", 1)),
                Json.getString(unlock, "hold", ""),
                Json.getString(unlock, "advancement", ""),
                Json.getString(unlock, "killed", ""),
                Math.max(1, Json.getInt(unlock, "killedCount", 1)),
                Json.getString(unlock, "killedDrops", ""),
                Json.choice(unlock, "killedTally", GateDef.PLAYER, owner, TALLIES),
                Standing.parse(unlock),
                Json.choice(unlock, "teamAndScore", GateDef.ALONE, owner, STANDINGS),
                strings(json, "portalBlocks"),
                Json.getString(json, "blockedMessage", "You need %item% to enter %dim%"),
                Json.getString(json, "unlockedMessage", "%dim% is now open"),
                Json.getBoolean(json, "safeReturn", false),
                strings(json, "requires"));
    }
}
