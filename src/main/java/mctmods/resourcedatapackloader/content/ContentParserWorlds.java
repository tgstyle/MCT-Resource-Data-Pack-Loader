package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.portal.PortalShapes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.ContentLog;
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
    private static final String ROLES = "rlsc.";

    private ContentParserWorlds() {}

    @Nullable public static PortalFrameDef portalFrame(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
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
        if (!holed) {
            ContentLog.LOGGER.error("Portal frame {} has no '{}' in it, so nothing would ever stand inside it", key, PortalFrameDef.HOLE);
            return null;
        }
        String axis = JsonUtils.getString(json, "axis", PortalFrameDef.VERTICAL).trim().toLowerCase(Locale.ROOT);
        if (!PortalFrameDef.VERTICAL.equals(axis) && !PortalFrameDef.HORIZONTAL.equals(axis) && !PortalFrameDef.BOTH.equals(axis)) {
            ContentLog.LOGGER.error("Portal frame {} asks for axis '{}', which is none of {}, {} or {}, standing it up instead", key, axis, PortalFrameDef.VERTICAL, PortalFrameDef.HORIZONTAL, PortalFrameDef.BOTH);
            axis = PortalFrameDef.VERTICAL;
        }
        PortalFrameDef frame = new PortalFrameDef(key, JsonUtils.getString(json, "name", key.getPath()), axis, legend, rows,
                Math.max(PortalFrameDef.LEAST_WIDE, JsonUtils.getInt(json, "maxWidth", 21)),
                Math.max(PortalFrameDef.LEAST_TALL, JsonUtils.getInt(json, "maxHeight", 21)));
        if (PortalShapes.spread(frame).isEmpty()) {
            ContentLog.LOGGER.error("Portal frame {} never leaves room for a player, who needs a hole {} across and {} up, so nothing could walk through it", key, PortalFrameDef.LEAST_WIDE, frame.leastTall());
            return null;
        }
        return frame;
    }

    @Nullable private static DimensionPortalDef dimensionPortal(ResourceLocation key, JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = JsonUtils.getJsonObject(json, "portal");
        List<String> frames = strings(entry, "frames");
        if (frames.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has a portal section naming no frames, so nothing could ever open it", key);
            return null;
        }
        int color = ContentTypes.color(JsonUtils.getString(entry, "color", "#FFFFFF"), key + " portal color");
        String back = JsonUtils.getString(entry, "return", DimensionPortalDef.BUILT).trim().toLowerCase(Locale.ROOT);
        if (!DimensionPortalDef.BUILT.equals(back) && !DimensionPortalDef.PLAYER.equals(back) && !DimensionPortalDef.NONE.equals(back)) {
            ContentLog.LOGGER.error("Dimension {} asks for a return of '{}', which is none of {}, {} or {}, building one instead", key, back, DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE);
            back = DimensionPortalDef.BUILT;
        }
        PortalDef travel = new PortalDef(JsonUtils.getInt(json, "id"),
                JsonUtils.getInt(entry, "returnDimension", 0),
                JsonUtils.getString(entry, "gate", ""),
                Math.max(0, JsonUtils.getInt(entry, "cooldown", 60)),
                JsonUtils.getBoolean(entry, "platform", true),
                JsonUtils.getString(entry, "platformBlock", ""),
                JsonUtils.getString(entry, "sound", ""),
                JsonUtils.getBoolean(entry, "owned", false),
                JsonUtils.getBoolean(entry, "walkIn", true));
        return new DimensionPortalDef(frames, JsonUtils.getString(entry, "ignitedBy", "minecraft:flint_and_steel"), color, back, travel);
    }

    private static Map<Character, IBlockState> legend(ResourceLocation key, JsonObject json, String forbidden, String what) {
        Map<Character, IBlockState> legend = new LinkedHashMap<>();
        if (!json.has("legend")) { return legend; }
        JsonObject entry = JsonUtils.getJsonObject(json, "legend");
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
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        Map<Character, IBlockState> legend = legend(key, json, ROLES, "Path intersect");
        List<String> mouth = strings(json, "mouth");
        List<String> corner = strings(json, "corner");
        roles(key, legend, mouth, "mouth");
        roles(key, legend, corner, "corner");
        return new PathIntersectDef(Math.max(1, JsonUtils.getInt(json, "weight", 1)), legend, mouth.toArray(new String[0]), corner.toArray(new String[0]));
    }

    @Nullable public static StructureMapDef structureMap(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        if (!json.has("layers")) {
            ContentLog.LOGGER.error("Structure map {} has no layers, so it is dropped", key);
            return null;
        }
        JsonArray held = JsonUtils.getJsonArray(json, "layers");
        if (held.size() < 1 || held.size() > StructureMapDef.LIMIT) {
            ContentLog.LOGGER.error("Structure map {} has {} layer(s), the most being {}, so it is dropped", key, held.size(), StructureMapDef.LIMIT);
            return null;
        }
        StructureMapDef.Layer[] layers = new StructureMapDef.Layer[held.size()];
        for (int at = 0; at < held.size(); at++) {
            JsonObject entry = held.get(at).getAsJsonObject();
            Map<Character, List<PickDef>> palette = new LinkedHashMap<>();
            if (entry.has("palette")) {
                for (Map.Entry<String, JsonElement> mark : JsonUtils.getJsonObject(entry, "palette").entrySet()) {
                    String symbol = mark.getKey().trim();
                    if (symbol.length() != 1 || symbol.charAt(0) == '.') {
                        ContentLog.LOGGER.error("Structure map {} layer {} palette symbol '{}' must be a single character other than '.', so the map is dropped", key, at, mark.getKey());
                        return null;
                    }
                    List<PickDef> picks = new ArrayList<>();
                    if (mark.getValue().isJsonArray()) {
                        for (JsonElement choice : mark.getValue().getAsJsonArray()) { picks.add(weighted(choice.getAsString())); }
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
            for (String row : rows) {
                if (row.length() > StructureMapDef.LIMIT) {
                    ContentLog.LOGGER.error("Structure map {} layer {} row '{}' is {} cell(s) long, the most being {}, so the map is dropped", key, at, row, row.length(), StructureMapDef.LIMIT);
                    return null;
                }
                for (char cell : row.toCharArray()) {
                    if (cell != '.' && !palette.containsKey(cell)) {
                        ContentLog.LOGGER.error("Structure map {} layer {} uses '{}', which is not in that layer's palette, so the map is dropped", key, at, cell);
                        return null;
                    }
                }
            }
            layers[at] = new StructureMapDef.Layer(palette, rows.toArray(new String[0]));
        }
        int ground = MathHelper.clamp(JsonUtils.getInt(json, "ground", 0), 0, layers.length - 1);
        int cell = MathHelper.clamp(JsonUtils.getInt(json, "cell", 32), 1, 48);
        int spacing = Math.max(0, JsonUtils.getInt(json, "spacing", 0));
        int chance = MathHelper.clamp(JsonUtils.getInt(json, "chance", 100), 1, 100);
        int[] pinned = null;
        if (json.has("at")) {
            JsonArray spot = JsonUtils.getJsonArray(json, "at");
            if (spot.size() == 2) { pinned = new int[] { spot.get(0).getAsInt(), spot.get(1).getAsInt() }; }
            else { ContentLog.LOGGER.error("Structure map {} pins 'at' with {} number(s) instead of x and z, so the pin is ignored", key, spot.size()); }
        }
        Set<Integer> dimensions = new LinkedHashSet<>();
        if (json.has("dimensions")) {
            for (JsonElement dim : JsonUtils.getJsonArray(json, "dimensions")) { dimensions.add(dim.getAsInt()); }
        }
        StructureMapDef def = new StructureMapDef(key, cell, ground, spacing, chance, pinned, dimensions, layers);
        if (spacing > 0 && spacing * 16 < def.widest) {
            ContentLog.LOGGER.error("Structure map {} asks for a spacing of {} chunk(s) but spans {} ContentParser.block(s), so it is dropped rather than overlapping itself", key, spacing, def.widest);
            return null;
        }
        return def;
    }

    @Nullable public static CityMapDef cityMap(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        List<String> rows = strings(json, "map");
        if (rows.isEmpty() || rows.size() > CityMapDef.LIMIT) {
            ContentLog.LOGGER.error("City map {} holds {} map row(s), the most being {}, so it is dropped", key, rows.size(), CityMapDef.LIMIT);
            return null;
        }
        Map<Character, CityMapDef.Cell> palette = new LinkedHashMap<>();
        if (json.has("palette")) {
            for (Map.Entry<String, JsonElement> mark : JsonUtils.getJsonObject(json, "palette").entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || symbol.charAt(0) == '.') {
                    ContentLog.LOGGER.error("City map {} palette symbol '{}' must be a single character other than '.', so the map is dropped", key, mark.getKey());
                    return null;
                }
                List<String> names = new ArrayList<>();
                int height = CityMapDef.LIFT;
                JsonObject roadKeys = null;
                if (mark.getValue().isJsonArray()) {
                    for (JsonElement choice : mark.getValue().getAsJsonArray()) { names.add(choice.getAsString().trim()); }
                }
                else if (mark.getValue().isJsonObject()) {
                    JsonObject described = mark.getValue().getAsJsonObject();
                    names.add(JsonUtils.getString(described, "kind", "").trim());
                    height = MathHelper.clamp(JsonUtils.getInt(described, "height", CityMapDef.LIFT), 2, 64);
                    if (described.has("settings") && described.get("settings").isJsonObject()) {
                        roadKeys = described.getAsJsonObject("settings");
                        for (Map.Entry<String, JsonElement> entry : roadKeys.entrySet()) {
                            if (ContentControl.ignores(entry.getKey())) { ContentLog.LOGGER.error("City map {} mark '{}' sets '{}', which is not a setting anything reads, so it does nothing", key, symbol, entry.getKey()); }
                        }
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
        for (String row : rows) {
            if (row.length() > CityMapDef.LIMIT) {
                ContentLog.LOGGER.error("City map {} row '{}' is {} cell(s) long, the most being {}, so the map is dropped", key, row, row.length(), CityMapDef.LIMIT);
                return null;
            }
            for (char cell : row.toCharArray()) {
                if (cell != '.' && !palette.containsKey(cell)) {
                    ContentLog.LOGGER.error("City map {} uses '{}', which is not in its palette, so the map is dropped", key, cell);
                    return null;
                }
            }
        }
        int cell = MathHelper.clamp(JsonUtils.getInt(json, "cell", 48), 8, 128);
        JsonObject settings = null;
        if (json.has("settings")) {
            if (json.get("settings").isJsonObject()) {
                settings = json.getAsJsonObject("settings");
                for (Map.Entry<String, JsonElement> entry : settings.entrySet()) {
                    if (ContentControl.ignores(entry.getKey())) { ContentLog.LOGGER.error("City map {} sets '{}', which is not a setting anything reads, so it does nothing", key, entry.getKey()); }
                }
            }
            else { ContentLog.LOGGER.error("City map {} has settings that are not an object of setting names, so it keeps the world template's", key); }
        }
        return new CityMapDef(key, cell, palette, rows.toArray(new String[0]), settings);
    }

    private static PickDef weighted(String entry) {
        int split = entry.lastIndexOf('=');
        if (split < 0) { return new PickDef(entry.trim().toLowerCase(Locale.ROOT), 1); }
        int weight;
        try { weight = Integer.parseInt(entry.substring(split + 1).trim()); }
        catch (NumberFormatException held) { weight = 1; }
        return new PickDef(entry.substring(0, split).trim().toLowerCase(Locale.ROOT), weight);
    }

    @Nullable public static WorldTemplateDef worldTemplate(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        Map<String, String> roles = new LinkedHashMap<>();
        if (json.has("roles")) {
            JsonObject entry = JsonUtils.getJsonObject(json, "roles");
            for (Map.Entry<String, JsonElement> role : entry.entrySet()) {
                String name = role.getKey().trim().toLowerCase(Locale.ROOT);
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
            for (JsonElement element : JsonUtils.getJsonArray(json, "dimensions")) { dimensions.add(element.getAsInt()); }
        }
        return new WorldTemplateDef(key,
                JsonUtils.getString(json, "name", key.getPath()),
                JsonUtils.getString(json, "default", WorldTemplateDef.VOID),
                Collections.unmodifiableMap(roles),
                structures(key, json),
                json.has("settings") ? JsonUtils.getJsonObject(json, "settings") : null,
                Collections.unmodifiableList(dimensions),
                strings(json, "requires"));
    }

    @Nullable public static WorldIntroDef worldIntro(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        List<IntroPageDef> pages = new ArrayList<>();
        if (json.has("pages")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "pages")) {
                IntroPageDef page = introPage(key, element.getAsJsonObject());
                if (page != null) { pages.add(page); }
            }
        }
        if (pages.isEmpty()) {
            ContentLog.LOGGER.error("World intro {} names no pages, ignoring it", key);
            return null;
        }
        String music = JsonUtils.getString(json, "music", "").trim();
        return new WorldIntroDef(JsonUtils.getBoolean(json, "once", false),
                music.isEmpty() ? null : new ResourceLocation(music),
                Collections.unmodifiableList(pages),
                strings(json, "requires"));
    }

    @Nullable private static IntroPageDef introPage(ResourceLocation key, JsonObject json) {
        List<ResourceLocation> backgrounds = new ArrayList<>();
        String single = JsonUtils.getString(json, "background", "").trim();
        if (!single.isEmpty()) { backgrounds.add(new ResourceLocation(single)); }
        for (String name : strings(json, "backgrounds")) { backgrounds.add(new ResourceLocation(name)); }
        String mode = JsonUtils.getString(json, "mode", IntroPageDef.SCROLL).trim().toLowerCase(Locale.ROOT);
        if (!IntroPageDef.SCROLL.equals(mode) && !IntroPageDef.STATIC.equals(mode)) {
            ContentLog.LOGGER.error("World intro {} has a page with mode '{}', which is neither '{}' nor '{}', ignoring the page", key, mode, IntroPageDef.SCROLL, IntroPageDef.STATIC);
            return null;
        }
        String direction = JsonUtils.getString(json, "direction", IntroPageDef.UP).trim().toLowerCase(Locale.ROOT);
        if (!IntroPageDef.UP.equals(direction) && !IntroPageDef.DOWN.equals(direction)) {
            ContentLog.LOGGER.error("World intro {} has a page with direction '{}', which is neither '{}' nor '{}', taking '{}'", key, direction, IntroPageDef.UP, IntroPageDef.DOWN, IntroPageDef.UP);
            direction = IntroPageDef.UP;
        }
        String text = JsonUtils.getString(json, "text", "").trim();
        return new IntroPageDef(Collections.unmodifiableList(backgrounds),
                JsonUtils.getFloat(json, "interval", 5.0F),
                text.isEmpty() ? null : new ResourceLocation(text),
                mode,
                JsonUtils.getFloat(json, "time", IntroPageDef.DERIVE),
                direction,
                JsonUtils.getFloat(json, "textScale", 1.0F),
                JsonUtils.getBoolean(json, "settle", false));
    }

    public static Map<Integer, Map<String, String>> gameRuleFile(ResourceLocation key, String contents) {
        Map<Integer, Map<String, String>> found = new LinkedHashMap<>();
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
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
            Map<String, String> rules = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> rule : entry.getValue().getAsJsonObject().entrySet()) {
                if (!rule.getValue().isJsonPrimitive()) { continue; }
                rules.put(rule.getKey(), rule.getValue().getAsString());
            }
            found.put(dimension, rules);
        }
        return found;
    }

    private static Map<String, String> gameRules(ResourceLocation key, JsonObject json) {
        Map<String, String> found = new LinkedHashMap<>();
        if (!json.has("gameRules")) { return Collections.unmodifiableMap(found); }
        JsonObject entry = JsonUtils.getJsonObject(json, "gameRules");
        for (Map.Entry<String, JsonElement> rule : entry.entrySet()) {
            if (!rule.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Dimension {} sets game rule '{}' to something that is not a value, ignoring it", key, rule.getKey());
                continue;
            }
            found.put(rule.getKey(), rule.getValue().getAsString());
        }
        return Collections.unmodifiableMap(found);
    }

    @Nullable public static DimensionDef dimension(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        JsonObject terrain = JsonUtils.getJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = JsonUtils.getJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = JsonUtils.getJsonObject(json, "sky", new JsonObject());
        String skyColor = JsonUtils.getString(sky, "skyColor", "").trim();
        String cloudColor = JsonUtils.getString(sky, "cloudColor", "").trim();
        String type = JsonUtils.getString(terrain, "type", DimensionDef.OVERWORLD).trim().toLowerCase(Locale.ROOT);
        String source = JsonUtils.getString(biomes, "source", DimensionDef.INHERIT).trim().toLowerCase(Locale.ROOT);
        if (!DimensionDef.SINGLE.equals(source) && !DimensionDef.INHERIT.equals(source)) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}', which is not {} or {}, using {}", key, source, DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        if (!KNOWN_TERRAIN.contains(type)) {
            ContentLog.LOGGER.error("Dimension {} asks for terrain '{}', which is not one of {}, using {}", key, type, KNOWN_TERRAIN, DimensionDef.OVERWORLD);
            type = DimensionDef.OVERWORLD;
        }
        String fog = JsonUtils.getString(sky, "fogColor", "");
        return new DimensionDef(key,
                JsonUtils.getInt(json, "id"),
                JsonUtils.getString(json, "suffix", "DIM_" + key.getPath()),
                JsonUtils.getBoolean(json, "keepLoaded", false),
                type,
                JsonUtils.getString(terrain, "generatorOptions", ""),
                JsonUtils.getBoolean(terrain, "structures", true),
                source,
                JsonUtils.getString(biomes, "biome", "minecraft:plains"),
                JsonUtils.getBoolean(sky, "hasSkyLight", true),
                JsonUtils.getBoolean(sky, "surfaceWorld", true),
                JsonUtils.getBoolean(sky, "respawn", true),
                JsonUtils.getBoolean(sky, "spawning", true),
                JsonUtils.getInt(sky, "cloudHeight", 128),
                JsonUtils.getInt(sky, "groundLevel", 63),
                JsonUtils.getFloat(sky, "movementFactor", 1.0F),
                fog.isEmpty() ? -1 : ContentTypes.color(fog, key.toString()),
                skyColor.isEmpty() ? -1 : ContentTypes.color(skyColor, key.toString()),
                JsonUtils.getInt(sky, "fixedTime", -1),
                JsonUtils.getBoolean(sky, "sunriseColors", true),
                JsonUtils.getBoolean(sky, "nether", false),
                JsonUtils.getBoolean(sky, "beds", true),
                JsonUtils.getBoolean(sky, "waterVaporizes", false),
                JsonUtils.getBoolean(sky, "showFog", false),
                MathHelper.clamp(JsonUtils.getFloat(sky, "ambientLight", 0.0F), 0.0F, 1.0F),
                JsonUtils.getFloat(sky, "starBrightness", -1.0F),
                cloudColor.isEmpty() ? -1 : ContentTypes.color(cloudColor, key.toString()),
                JsonUtils.getInt(sky, "respawnDimension", Integer.MIN_VALUE),
                JsonUtils.getBoolean(sky, "renderSky", true),
                JsonUtils.getBoolean(sky, "renderClouds", true),
                JsonUtils.getBoolean(sky, "renderWeather", true),
                gameRules(key, json),
                strings(json, "requires"),
                dimensionPortal(key, json));
    }

    private static Map<String, Boolean> structures(ResourceLocation key, JsonObject json) {
        Map<String, Boolean> settings = new LinkedHashMap<>();
        if (!json.has("structures")) { return Collections.unmodifiableMap(settings); }
        JsonObject entry = JsonUtils.getJsonObject(json, "structures");
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
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        String scope = JsonUtils.getString(json, "scope", GateDef.PLAYER).trim().toLowerCase(Locale.ROOT);
        if (!GateDef.PLAYER.equals(scope) && !GateDef.GLOBAL.equals(scope)) {
            ContentLog.LOGGER.error("Gate {} asks for scope '{}', which is not {} or {}, using {}", key, scope, GateDef.PLAYER, GateDef.GLOBAL, GateDef.PLAYER);
            scope = GateDef.PLAYER;
        }
        JsonObject unlock = JsonUtils.getJsonObject(json, "unlock", new JsonObject());
        return new GateDef(key,
                JsonUtils.getInt(json, "dimension"),
                JsonUtils.getString(json, "name", key.getPath()),
                GateDef.GLOBAL.equals(scope),
                JsonUtils.getBoolean(json, "open", false),
                JsonUtils.getString(unlock, "craft", ""),
                JsonUtils.getString(unlock, "consume", ""),
                Math.max(1, JsonUtils.getInt(unlock, "consumeCount", 1)),
                JsonUtils.getString(unlock, "hold", ""),
                JsonUtils.getString(unlock, "advancement", ""),
                JsonUtils.getString(unlock, "killed", ""),
                Math.max(1, JsonUtils.getInt(unlock, "killedCount", 1)),
                JsonUtils.getString(unlock, "killedDrops", ""),
                strings(json, "portalBlocks"),
                JsonUtils.getString(json, "blockedMessage", "You need %item% to enter %dim%"),
                JsonUtils.getString(json, "unlockedMessage", "%dim% is now open"),
                JsonUtils.getBoolean(json, "safeReturn", false),
                strings(json, "requires"));
    }
}
