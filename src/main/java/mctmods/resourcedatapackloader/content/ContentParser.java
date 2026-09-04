package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.portal.PortalShapes;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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

public final class ContentParser {
    private static final Set<String> KNOWN_SPREADS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED)));
    private static final Set<String> KNOWN_TERRAIN = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END)));
    private static final Set<String> KNOWN_SHAPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES,
            ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD)));
    public static final String PLACEHOLDER = "open";
    public static final String DEFAULT_STILL = "minecraft:blocks/water_still";
    public static final String DEFAULT_FLOW = "minecraft:blocks/water_flow";
    private static final Gson GSON = new GsonBuilder().create();
    private static final int[] NO_CHANCE = new int[0];

    private ContentParser() {}

    @Nullable public static BlockDef block(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Block definition {} is empty, ignoring it", key);
            return null;
        }
        Material material = ContentTypes.material(JsonUtils.getString(json, "material", "rock"), key.toString());
        MapColor mapColor = ContentTypes.mapColor(JsonUtils.getString(json, "mapColor", ""), material.getMaterialMapColor(), key.toString());
        SoundType soundType = ContentTypes.soundType(JsonUtils.getString(json, "soundType", ""), null, key.toString());
        JsonObject exp = JsonUtils.getJsonObject(json, "expDrop", new JsonObject());
        int expMin = JsonUtils.getInt(exp, "min", 0);
        int expMax = JsonUtils.getInt(exp, "max", 0);
        String type = JsonUtils.getString(json, "type", ContentBlockTypes.DEFAULT);
        int maxVariants = ContentBlockTypes.get(type, key).maxVariants();
        boolean opaque = JsonUtils.getBoolean(json, "opaque", true);
        JsonObject variants = JsonUtils.getJsonObject(json, "variants", new JsonObject());
        BlockVariant[] byMeta = new BlockVariant[maxVariants];
        List<BlockVariant> visible = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Block variant '{}' in {} is not an object, skipping it", name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            int meta = JsonUtils.getInt(variant, "meta", -1);
            if (meta < 0 || meta >= maxVariants) {
                ContentLog.LOGGER.error("Block variant '{}' in {} has meta {}, which is outside 0-{}. A '{}' block cannot hold more than {} variants, so this one is skipped. Split it into another block file", name, key, meta, maxVariants - 1, type, maxVariants);
                continue;
            }
            if (byMeta[meta] != null) {
                ContentLog.LOGGER.error("Block variants '{}' and '{}' in {} both claim meta {}, skipping '{}'", byMeta[meta].name, name, key, meta, name);
                continue;
            }
            BlockVariant parsed = blockVariant(key, name, meta, variant);
            byMeta[meta] = parsed;
            visible.add(parsed);
        }
        for (int meta = 0; meta < byMeta.length; meta++) {
            if (byMeta[meta] == null) { byMeta[meta] = BlockVariant.placeholder(placeholderName(meta, 2), meta); }
        }
        return new BlockDef(key, type, material, mapColor, soundType,
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getString(json, "harvestTool", "pickaxe"),
                JsonUtils.getInt(json, "harvestToolLevel", 0),
                JsonUtils.getBoolean(json, "silkHarvest", true),
                expMin, expMax,
                JsonUtils.getFloat(json, "explosionResistanceDivisor", 1.0F),
                byMeta, Collections.unmodifiableList(visible), strings(json, "requires"),
                renderLayer(JsonUtils.getString(json, "renderLayer", ""), key.toString()),
                opaque,
                JsonUtils.getBoolean(json, "fullCube", opaque),
                JsonUtils.getInt(json, "lightOpacity", opaque ? 255 : 0),
                JsonUtils.getFloat(json, "slipperiness", 0.6F),
                bounds(key, json),
                JsonUtils.getInt(json, "flammability", 0),
                JsonUtils.getInt(json, "fireSpread", 0),
                JsonUtils.getString(json, "modelBlock", "minecraft:stone"),
                JsonUtils.getInt(json, "modelMeta", 0),
                "item".equals(JsonUtils.getString(json, "itemModel", "state")),
                JsonUtils.getString(json, "particle", BlockDef.PARTICLE_FLAME).toLowerCase(Locale.ROOT),
                JsonUtils.getBoolean(json, "smoke", true),
                ContentTypes.color(JsonUtils.getString(json, "particleColor", "FFFFFF"), key.toString()),
                JsonUtils.getString(json, "seed", ""),
                JsonUtils.getString(json, "produce", ""),
                Math.max(1, Math.min(7, JsonUtils.getInt(json, "maxAge", 7))),
                sapling(json),
                portal(json),
                growth(json),
                strings(json, "plantTypes"),
                behaviors(key, json),
                JsonUtils.getString(json, "tint", ""),
                JsonUtils.getString(json, "leafSapling", ""),
                Math.max(0, Math.min(100, JsonUtils.getInt(json, "leafSaplingChance", 5))),
                opensWith(json),
                JsonUtils.getString(json, "openSound", "").trim());
    }

    @Nullable private static ResourceLocation opensWith(JsonObject json) {
        String named = JsonUtils.getString(json, "opensWith", "").trim();
        return named.isEmpty() ? null : new ResourceLocation(named);
    }

    @Nullable private static PortalDef portal(JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = JsonUtils.getJsonObject(json, "portal");
        return new PortalDef(JsonUtils.getInt(entry, "dimension"),
                JsonUtils.getInt(entry, "returnDimension", 0),
                JsonUtils.getString(entry, "gate", ""),
                Math.max(0, JsonUtils.getInt(entry, "cooldown", 60)),
                JsonUtils.getBoolean(entry, "platform", true),
                JsonUtils.getString(entry, "platformBlock", ""),
                JsonUtils.getString(entry, "sound", ""),
                JsonUtils.getBoolean(entry, "owned", true),
                JsonUtils.getBoolean(entry, "walkIn", false));
    }

    @Nullable public static PortalFrameDef portalFrame(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        Map<Character, IBlockState> legend = new LinkedHashMap<>();
        if (json.has("legend")) {
            JsonObject entry = JsonUtils.getJsonObject(json, "legend");
            for (Map.Entry<String, JsonElement> mark : entry.entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || symbol.charAt(0) == PortalFrameDef.HOLE || symbol.charAt(0) == PortalFrameDef.REPEAT) {
                    ContentLog.LOGGER.error("Portal frame {} legend symbol '{}' must be a single character and neither {} nor {}", key, mark.getKey(), PortalFrameDef.HOLE, PortalFrameDef.REPEAT);
                    continue;
                }
                IBlockState state = ContentStates.parse(mark.getValue().getAsString(), key + " legend " + symbol);
                if (state != null) { legend.put(symbol.charAt(0), state); }
            }
        }
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

    @Nullable public static PathIntersectDef pathIntersect(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        Map<Character, IBlockState> legend = new LinkedHashMap<>();
        if (json.has("legend")) {
            JsonObject entry = JsonUtils.getJsonObject(json, "legend");
            for (Map.Entry<String, JsonElement> mark : entry.entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || "rlsc.".indexOf(symbol.charAt(0)) >= 0) {
                    ContentLog.LOGGER.error("Path intersect {} legend symbol '{}' must be a single character and not one of the role letters r, l, s, c or .", key, mark.getKey());
                    continue;
                }
                IBlockState state = ContentStates.parse(mark.getValue().getAsString(), key + " legend " + symbol);
                if (state != null) { legend.put(symbol.charAt(0), state); }
            }
        }
        List<String> mouth = strings(json, "mouth");
        List<String> corner = strings(json, "corner");
        for (String row : mouth) {
            for (char held : row.toCharArray()) {
                if ("rlsc.".indexOf(held) < 0 && !legend.containsKey(held)) { ContentLog.LOGGER.error("Path intersect {} mouth uses '{}', which is neither a role letter nor in the legend", key, held); }
            }
        }
        for (String row : corner) {
            for (char held : row.toCharArray()) {
                if ("rlsc.".indexOf(held) < 0 && !legend.containsKey(held)) { ContentLog.LOGGER.error("Path intersect {} corner uses '{}', which is neither a role letter nor in the legend", key, held); }
            }
        }
        return new PathIntersectDef(JsonUtils.getString(json, "name", key.getPath()), Math.max(1, JsonUtils.getInt(json, "weight", 1)), legend, mouth.toArray(new String[0]), corner.toArray(new String[0]));
    }

    @Nullable public static StructureMapDef structureMap(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
        int ground = Math.max(0, Math.min(layers.length - 1, JsonUtils.getInt(json, "ground", 0)));
        int cell = Math.max(1, Math.min(48, JsonUtils.getInt(json, "cell", 32)));
        int spacing = Math.max(0, JsonUtils.getInt(json, "spacing", 0));
        int chance = Math.max(1, Math.min(100, JsonUtils.getInt(json, "chance", 100)));
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
        StructureMapDef def = new StructureMapDef(key, JsonUtils.getString(json, "name", key.getPath()), cell, ground, spacing, chance, pinned, dimensions, layers);
        if (spacing > 0 && spacing * 16 < def.widest) {
            ContentLog.LOGGER.error("Structure map {} asks for a spacing of {} chunk(s) but spans {} block(s), so it is dropped rather than overlapping itself", key, spacing, def.widest);
            return null;
        }
        return def;
    }

    @Nullable public static CityMapDef cityMap(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
                if (mark.getValue().isJsonArray()) {
                    for (JsonElement choice : mark.getValue().getAsJsonArray()) { names.add(choice.getAsString().trim()); }
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
                        default: break;
                    }
                }
                if (kind == CityMapDef.Kind.PLOT) {
                    for (String name : names) { picks.add(weighted(name)); }
                }
                palette.put(symbol.charAt(0), new CityMapDef.Cell(kind, picks));
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
        int cell = Math.max(8, Math.min(128, JsonUtils.getInt(json, "cell", 48)));
        return new CityMapDef(key, JsonUtils.getString(json, "name", key.getPath()), cell, palette, rows.toArray(new String[0]));
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
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
        return new WorldIntroDef(key,
                JsonUtils.getBoolean(json, "once", false),
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
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
                Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(sky, "ambientLight", 0.0F))),
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

    private static List<String> behaviors(ResourceLocation key, JsonObject json) {
        List<String> found = new ArrayList<>();
        for (String raw : strings(json, "behavesAs")) {
            String name = ContentSpawning.normalize(raw);
            if (!ContentSpawning.known(name)) {
                ContentLog.LOGGER.error("Block {} says it behaves as '{}', which is not one of {}, ignoring it", key, raw, ContentSpawning.describe());
                continue;
            }
            found.add(name);
        }
        return Collections.unmodifiableList(found);
    }

    private static Map<String, Boolean> structures(ResourceLocation key, JsonObject json) {
        Map<String, Boolean> settings = new LinkedHashMap<>();
        if (!json.has("structures")) { return Collections.unmodifiableMap(settings); }
        JsonObject entry = JsonUtils.getJsonObject(json, "structures");
        for (Map.Entry<String, JsonElement> value : entry.entrySet()) {
            String name = ContentStructures.normalize(value.getKey());
            if (!ContentStructures.known(name)) {
                ContentLog.LOGGER.error("World template {} names structure '{}', which is not one of {}, ignoring it", key, value.getKey(), ContentStructures.describe());
                continue;
            }
            settings.put(name, value.getValue().getAsBoolean());
        }
        return Collections.unmodifiableMap(settings);
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }
        JsonObject entry = JsonUtils.getJsonObject(json, "growth");
        return new GrowthDef(Math.max(1, JsonUtils.getInt(entry, "maxHeight", 3)),
                Math.max(1, Math.min(16, JsonUtils.getInt(entry, "stages", 16))),
                strings(entry, "soil"),
                JsonUtils.getBoolean(entry, "needsWater", false),
                Math.max(1, JsonUtils.getInt(entry, "waterRange", 1)),
                JsonUtils.getBoolean(entry, "needsSky", false),
                JsonUtils.getBoolean(entry, "damage", false),
                JsonUtils.getFloat(entry, "damageAmount", 1.0F),
                JsonUtils.getBoolean(entry, "breaksNeighbors", false),
                Math.max(0, JsonUtils.getInt(entry, "spread", 0)),
                JsonUtils.getString(entry, "drop", ""),
                Math.max(1, JsonUtils.getInt(entry, "dropCount", 1)));
    }

    @Nullable private static SaplingDef sapling(JsonObject json) {
        if (!json.has("sapling")) { return null; }
        JsonObject entry = JsonUtils.getJsonObject(json, "sapling");
        return new SaplingDef(
                strings(entry, "soil"),
                Math.max(1, JsonUtils.getInt(entry, "stages", 2)),
                Math.max(1, JsonUtils.getInt(entry, "chance", 7)),
                Math.max(0, JsonUtils.getInt(entry, "light", 9)),
                JsonUtils.getString(entry, "structure", ""),
                JsonUtils.getString(entry, "log", "minecraft:log"),
                JsonUtils.getString(entry, "leaves", "minecraft:leaves"),
                Math.max(1, JsonUtils.getInt(entry, "height", 4)),
                JsonUtils.getBoolean(entry, "vines", false));
    }

    private static BlockVariant blockVariant(ResourceLocation key, String name, int meta, JsonObject json) {
        List<DropDef> drops = new ArrayList<>();
        if (json.has("drops")) {
            JsonArray array = JsonUtils.getJsonArray(json, "drops");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) { continue; }
                DropDef drop = drop(key, name, element.getAsJsonObject());
                if (drop != null) { drops.add(drop); }
            }
        }
        return new BlockVariant(name, meta,
                ContentTypes.rarity(JsonUtils.getString(json, "rarity", "COMMON"), key + " " + name),
                JsonUtils.getInt(json, "maxSize", 64),
                strings(json, "oreDict"),
                JsonUtils.getFloat(json, "hardness", 1.0F),
                JsonUtils.getFloat(json, "resistance", 5.0F),
                JsonUtils.getInt(json, "harvestLevel", 0),
                JsonUtils.getInt(json, "light", 0),
                portal(json),
                Collections.unmodifiableList(drops), false);
    }

    @Nullable private static DropDef drop(ResourceLocation key, String name, JsonObject json) {
        String block = JsonUtils.getString(json, "block", "");
        String entity = JsonUtils.getString(json, "entity", "");
        if (block.isEmpty() && entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names neither a block nor an entity, skipping it", name, key);
            return null;
        }
        if (!block.isEmpty() && !entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names both block {} and entity {}, using the entity", name, key, block, entity);
        }
        int[] chances = NO_CHANCE;
        if (json.has("bonusChance")) {
            JsonArray array = JsonUtils.getJsonArray(json, "bonusChance");
            chances = new int[array.size()];
            for (int i = 0; i < array.size(); i++) { chances[i] = array.get(i).getAsInt(); }
        }
        boolean guaranteed = JsonUtils.getBoolean(json, "guaranteed", true);
        return new DropDef(block.isEmpty() ? null : new ResourceLocation(block),
                entity.isEmpty() ? null : new ResourceLocation(entity),
                JsonUtils.getInt(json, "meta", 0),
                amount(json, "amount", 1, 0),
                guaranteed,
                Math.max(0, Math.min(100, JsonUtils.getInt(json, "chance", guaranteed ? 100 : 0))),
                Math.max(0, JsonUtils.getInt(json, "weight", 0)),
                chances);
    }

    @Nullable public static ItemDef item(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Item definition {} is empty, ignoring it", key);
            return null;
        }
        JsonObject variants = JsonUtils.getJsonObject(json, "variants", new JsonObject());
        Map<Integer, ItemVariant> byMeta = new LinkedHashMap<>();
        List<ItemVariant> visible = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Item variant '{}' in {} is not an object, skipping it", name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            int meta = JsonUtils.getInt(variant, "meta", -1);
            if (meta < 0 || meta > Short.MAX_VALUE) {
                ContentLog.LOGGER.error("Item variant '{}' in {} has meta {}, which is outside 0-{}, skipping it", name, key, meta, (int) Short.MAX_VALUE);
                continue;
            }
            if (byMeta.containsKey(meta)) {
                ContentLog.LOGGER.error("Item variants '{}' and '{}' in {} both claim meta {}, skipping '{}'", byMeta.get(meta).name, name, key, meta, name);
                continue;
            }
            ItemVariant parsed = new ItemVariant(name, meta,
                    ContentTypes.rarity(JsonUtils.getString(variant, "rarity", "COMMON"), key + " " + name),
                    JsonUtils.getInt(variant, "maxSize", 64),
                    strings(variant, "oreDict"),
                    JsonUtils.getInt(variant, "healAmount", 0),
                    JsonUtils.getFloat(variant, "saturation", 0.0F),
                    potion(variant));
            byMeta.put(meta, parsed);
            visible.add(parsed);
        }
        String type = JsonUtils.getString(json, "type", ContentItemTypes.DEFAULT);
        ContentItemTypes.get(type, key);
        return new ItemDef(key, type,
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getBoolean(json, "alwaysEdible", false),
                Collections.unmodifiableMap(byMeta), Collections.unmodifiableList(visible), strings(json, "requires"),
                Math.max(1, JsonUtils.getInt(json, "useDuration", 32)),
                JsonUtils.getBoolean(json, "eat", false),
                JsonUtils.getString(json, "container", ""),
                JsonUtils.getString(json, "material", ""),
                JsonUtils.getString(json, "toolClass", ""),
                JsonUtils.getString(json, "slot", ""),
                JsonUtils.getString(json, "crop", ""),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                strings(json, "potionTypes"),
                JsonUtils.getFloat(json, "attackSpeed", Float.NaN),
                Math.max(0, JsonUtils.getInt(json, "cooldown", 0)));
    }

    @Nullable public static FluidDef fluid(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Fluid definition {} is empty, ignoring it", key);
            return null;
        }
        String name = JsonUtils.getString(json, "name", key.getPath());
        JsonObject block = JsonUtils.getJsonObject(json, "block", new JsonObject());
        boolean createBlock = json.has("block");
        return new FluidDef(key, name,
                ContentTypes.color(JsonUtils.getString(json, "color", ""), key.toString()),
                new ResourceLocation(JsonUtils.getString(json, "still", DEFAULT_STILL)),
                new ResourceLocation(JsonUtils.getString(json, "flow", DEFAULT_FLOW)),
                JsonUtils.getInt(json, "temperature", 300),
                JsonUtils.getInt(json, "density", 1000),
                JsonUtils.getInt(json, "viscosity", 1000),
                JsonUtils.getInt(json, "luminosity", 0),
                JsonUtils.getBoolean(json, "gaseous", false),
                JsonUtils.getBoolean(json, "bucket", true),
                createBlock,
                ContentTypes.material(JsonUtils.getString(block, "material", "water"), key.toString()),
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getInt(block, "flammability", 0),
                JsonUtils.getInt(block, "fireSpread", 0),
                JsonUtils.getInt(block, "quantaPerBlock", 0),
                strings(block, "potions"), strings(json, "requires"));
    }

    @Nullable public static ExposureDef exposure(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Exposure definition {} is empty, ignoring it", key);
            return null;
        }
        List<ExposureLevelDef> levels = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "levels", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A level in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject level = element.getAsJsonObject();
            String effect = JsonUtils.getString(level, "effect", "");
            if (effect.isEmpty()) {
                ContentLog.LOGGER.error("A level in {} names no effect, skipping it", key);
                continue;
            }
            List<PotionEffectDef> extras = new ArrayList<>();
            for (JsonElement extraElement : JsonUtils.getJsonArray(level, "effects", new JsonArray())) {
                if (!extraElement.isJsonObject()) { continue; }
                JsonObject extra = extraElement.getAsJsonObject();
                String potion = JsonUtils.getString(extra, "potion", "");
                if (potion.isEmpty()) {
                    ContentLog.LOGGER.error("An extra effect in {} names no potion, skipping it", key);
                    continue;
                }
                extras.add(new PotionEffectDef(potion,
                        Math.max(0, JsonUtils.getInt(extra, "duration", 0)),
                        Math.max(0, JsonUtils.getInt(extra, "amplifier", 0)),
                        JsonUtils.getBoolean(extra, "ambient", false),
                        JsonUtils.getBoolean(extra, "showParticles", false)));
            }
            levels.add(new ExposureLevelDef(effect,
                    Math.max(0.0F, JsonUtils.getFloat(level, "damage", 0.0F)),
                    Math.max(0, JsonUtils.getInt(level, "damageInterval", 160)),
                    Collections.unmodifiableList(extras)));
        }
        if (levels.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} has no usable levels, ignoring it", key);
            return null;
        }
        Map<ResourceLocation, Integer> blocks = leveledNames(key, json, "blocks");
        Map<ResourceLocation, Integer> items = leveledNames(key, json, "items");
        if (blocks.isEmpty() && items.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} names no blocks and no items, ignoring it", key);
            return null;
        }
        return new ExposureDef(key,
                Math.max(1, JsonUtils.getInt(json, "scanInterval", 20)),
                Math.max(0, JsonUtils.getInt(json, "range", 10)),
                JsonUtils.getBoolean(json, "skipsCreative", true),
                Math.max(0, JsonUtils.getInt(json, "sourcesForNextLevel", 0)),
                JsonUtils.getString(json, "immunity", "").trim(),
                blocks, items, Collections.unmodifiableList(levels));
    }

    private static Map<ResourceLocation, Integer> leveledNames(ResourceLocation key, JsonObject json, String member) {
        Map<ResourceLocation, Integer> out = new LinkedHashMap<>();
        for (String entry : strings(json, member)) {
            String named = entry.trim();
            int level = 1;
            int split = named.indexOf('=');
            if (split >= 0) {
                try {
                    level = Math.max(1, Integer.parseInt(named.substring(split + 1).trim()));
                } catch (NumberFormatException bad) {
                    ContentLog.LOGGER.error("The {} entry '{}' in {} has a level that is not a number, so it counts as level 1", member, entry, key);
                }
                named = named.substring(0, split).trim();
            }
            if (named.isEmpty()) { continue; }
            out.put(new ResourceLocation(named), level);
        }
        return out;
    }

    @Nullable public static WorldgenDef worldgen(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Worldgen definition {} is empty, ignoring it", key);
            return null;
        }
        String block = JsonUtils.getString(json, "block", "");
        if (block.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen definition {} has no block, ignoring it", key);
            return null;
        }
        int minHeight = JsonUtils.getInt(json, "minHeight", 0);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 64);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Worldgen definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        minHeight = Math.max(-Config.worldgen.rubicHeightLimit, minHeight);
        maxHeight = Math.max(minHeight, maxHeight);
        WorldgenDef made = new WorldgenDef(key, new ResourceLocation(block),
                JsonUtils.getInt(json, "meta", 0),
                weights(json),
                amount(json, "size", 8, 1),
                amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                replaces(key, json),
                adjacent(key, json),
                JsonUtils.getBoolean(json, "sparse", false),
                integers(json),
                JsonUtils.getBoolean(json, "dimensionsAreBlacklist", false),
                strings(json, "biomes"), strings(json, "biomeTypes"),
                JsonUtils.getBoolean(json, "biomesAreBlacklist", false),
                strings(json, "requires"),
                JsonUtils.getBoolean(json, "retrogen", false),
                JsonUtils.getString(json, "retrogenKey", ""),
                Math.max(0, JsonUtils.getInt(json, "minDistanceFromSpawn", 0)),
                spread(key, json, minHeight, maxHeight),
                shape(key, json),
                JsonUtils.getFloat(json, "minTemperature", -100.0F),
                JsonUtils.getFloat(json, "maxTemperature", 100.0F),
                JsonUtils.getFloat(json, "minRainfall", -100.0F),
                JsonUtils.getFloat(json, "maxRainfall", 100.0F),
                json.has("replace"));
        List<ResourceLocation> regions = new ArrayList<>();
        for (String name : strings(json, "caveRegions")) {
            regions.add(name.indexOf(':') >= 0 ? new ResourceLocation(name) : new ResourceLocation(key.getNamespace(), name));
        }
        if (!regions.isEmpty()) { made.caveRegions = regions; }
        String snap = JsonUtils.getString(json, "snap", "").trim().toLowerCase(Locale.ROOT);
        if (!snap.isEmpty() && !"floor".equals(snap) && !"ceiling".equals(snap)) {
            ContentLog.LOGGER.error("Worldgen {} asks for snap '{}', which is not floor or ceiling, so it does not snap", key, snap);
            snap = "";
        }
        made.snap = snap;
        int depth = JsonUtils.getInt(json, "snapDepth", 0);
        if (depth < 0) {
            ContentLog.LOGGER.error("Worldgen {} asks for snapDepth {}, which is below zero, so it stays at the surface", key, depth);
            depth = 0;
        }
        made.snapDepth = snap.isEmpty() ? 0 : depth;
        return made;
    }

    @Nullable public static CaveRegionDef caveRegion(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Cave region definition {} is empty, ignoring it", key);
            return null;
        }
        int minHeight = JsonUtils.getInt(json, "minHeight", -Config.worldgen.rubicHeightLimit);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 48);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Cave region definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        int waterLevel = CaveRegionDef.NO_WATER;
        if (json.has("waterLevel")) { waterLevel = JsonUtils.getInt(json, "waterLevel", CaveRegionDef.NO_WATER); }
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                String entity = JsonUtils.getString(entry, "entity", "");
                if (entity.isEmpty()) {
                    ContentLog.LOGGER.error("A spawn entry in {} names no entity, skipping it", key);
                    continue;
                }
                int min = Math.max(1, JsonUtils.getInt(entry, "min", 1));
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "type", "creature"), entity,
                        Math.max(1, JsonUtils.getInt(entry, "weight", 8)),
                        min, Math.max(min, JsonUtils.getInt(entry, "max", 4))));
            }
        }
        return new CaveRegionDef(key,
                Math.max(0, JsonUtils.getInt(json, "weight", 1)),
                minHeight, maxHeight,
                integers(json),
                JsonUtils.getString(json, "floorCover", ""),
                clamp01(JsonUtils.getFloat(json, "floorChance", 1.0F)),
                JsonUtils.getString(json, "ceilingCover", ""),
                clamp01(JsonUtils.getFloat(json, "ceilingChance", 1.0F)),
                strings(json, "coverReplace"),
                waterLevel,
                spawns,
                JsonUtils.getBoolean(json, "keepDefaultSpawns", false),
                picks(json, "structures", "structure"),
                clamp01(JsonUtils.getFloat(json, "structureChance", 1.0F)),
                JsonUtils.getString(json, "structureLoot", ""),
                JsonUtils.getString(json, "biome", ""),
                JsonUtils.getString(json, "skyStone", ""),
                Json.bounded(json, "skyIslands", -1.0F, 1.0F, key),
                Json.bounded(json, "skyThickness", 0.0F, 8.0F, key));
    }

    private static float clamp01(float value) { return value < 0.0F ? 0.0F : Math.min(value, 1.0F); }

    private static SpreadDef spread(ResourceLocation key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = JsonUtils.getJsonObject(json, "spread");
        String type = JsonUtils.getString(entry, "type", SpreadDef.EVEN).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SPREADS.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for spread type '{}', which is not one of {}, using {}", key, type, KNOWN_SPREADS, SpreadDef.EVEN);
            type = SpreadDef.EVEN;
        }
        int center = JsonUtils.getInt(entry, "center", (minHeight + maxHeight) / 2);
        int offsetMin = JsonUtils.getInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                center,
                Math.max(1, JsonUtils.getInt(entry, "range", Math.max(2, (maxHeight - minHeight) / 2))),
                Math.max(1, Math.min(8, JsonUtils.getInt(entry, "smoothness", 2))),
                Math.max(1, JsonUtils.getInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, JsonUtils.getInt(entry, "veinDiameter", 12)),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "verticalDensity", 16))),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "horizontalDensity", 32))),
                offsetMin,
                Math.max(offsetMin, JsonUtils.getInt(entry, "offsetMax", offsetMin)),
                JsonUtils.getBoolean(entry, "ceiling", false));
    }

    private static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }
        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, element.getAsInt())); }
        JsonObject range = element.getAsJsonObject();
        int least = Math.max(floor, JsonUtils.getInt(range, "min", fallback));
        return new AmountDef(least, Math.max(least, JsonUtils.getInt(range, "max", least)));
    }

    @Nullable public static GateDef gate(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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

    @Nullable public static EntityVariantDef entityVariant(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Entity file {} is empty, ignoring it", key);
            return null;
        }
        String base = JsonUtils.getString(json, "entity", "");
        if (base.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }
        Map<String, Double> attributes = new LinkedHashMap<>();
        if (json.has("attributes")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "attributes").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets attribute '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                attributes.put(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        List<String> tintParts = new ArrayList<>();
        for (String part : strings(json, "tintParts")) { tintParts.add(part.trim().toLowerCase(Locale.ROOT)); }
        if (tintParts.isEmpty()) { tintParts.add(EntityVariantDef.BODY); }
        Map<String, String> equipment = new LinkedHashMap<>();
        if (json.has("equipment")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "equipment").entrySet()) {
                if (!entry.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not an item name, ignoring it", key, entry.getKey());
                    continue;
                }
                equipment.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "creatureType", "creature"), "",
                        Math.max(1, JsonUtils.getInt(entry, "weight", 8)),
                        Math.max(1, JsonUtils.getInt(entry, "min", 1)),
                        Math.max(1, JsonUtils.getInt(entry, "max", 4))));
            }
        }
        JsonObject sounds = JsonUtils.getJsonObject(json, "sounds", new JsonObject());
        Map<String, Integer> effects = new LinkedHashMap<>();
        if (json.has("effects")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "effects")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject effect = element.getAsJsonObject();
                effects.put(JsonUtils.getString(effect, "potion", ""), Math.max(0, JsonUtils.getInt(effect, "amplifier", 0)));
            }
        }
        Map<String, Float> priorities = new LinkedHashMap<>();
        if (json.has("pathPriorities")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "pathPriorities").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets path priority '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                priorities.put(entry.getKey(), entry.getValue().getAsFloat());
            }
        }
        JsonObject egg = json.has("egg") && json.get("egg").isJsonObject() ? JsonUtils.getJsonObject(json, "egg") : null;
        boolean wantsEgg = !json.has("egg") || egg != null || JsonUtils.getBoolean(json, "egg", true);
        return new EntityVariantDef(key, new ResourceLocation(base),
                JsonUtils.getString(json, "name", ""),
                JsonUtils.getString(json, "texture", ""),
                JsonUtils.getString(json, "lootTable", ""),
                JsonUtils.getString(json, "profession", ""),
                Math.max(0, JsonUtils.getInt(json, "career", 0)),
                baby(json),
                picks(json, "becomes", "variant"),
                JsonUtils.getString(sounds, "ambient", ""),
                JsonUtils.getString(sounds, "hurt", ""),
                JsonUtils.getString(sounds, "death", ""),
                strings(json, "immuneTo"),
                Math.max(0.1F, JsonUtils.getFloat(json, "jumpMultiplier", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "fallDamage", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "soundVolume", 1.0F)),
                Math.max(0.1F, JsonUtils.getFloat(json, "soundPitch", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "waterSlowdown", 0.8F)),
                Math.max(-1, JsonUtils.getInt(json, "experience", -1)),
                Math.max(-1, JsonUtils.getInt(json, "maxFallHeight", -1)),
                Math.max(0.0F, JsonUtils.getFloat(json, "absorption", 0.0F)),
                JsonUtils.getString(json, "creatureAttribute", ""),
                JsonUtils.getBoolean(json, "breathesUnderwater", false),
                JsonUtils.getBoolean(json, "swims", false),
                JsonUtils.getBoolean(json, "amphibious", false),
                JsonUtils.getBoolean(json, "despawns", true),
                Math.max(0, JsonUtils.getInt(json, "despawnAfter", 0)) * 20,
                JsonUtils.getBoolean(json, "noAI", false),
                JsonUtils.getBoolean(json, "leftHanded", false),
                JsonUtils.getBoolean(json, "fireproof", false),
                JsonUtils.getBoolean(json, "invulnerable", false),
                JsonUtils.getBoolean(json, "glowing", false),
                JsonUtils.getBoolean(json, "invisible", false),
                Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(json, "dropChance", 0.0F))),
                Math.max(0.05F, JsonUtils.getFloat(json, "scale", 1.0F)),
                Math.max(0.05F, JsonUtils.getFloat(json, "angryScale", JsonUtils.getFloat(json, "scale", 1.0F))),
                JsonUtils.getBoolean(json, "leashable", false),
                JsonUtils.getBoolean(json, "steerable", false),
                Math.max(0.0F, JsonUtils.getFloat(json, "width", 0.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "height", 0.0F)),
                effects, priorities,
                wantsEgg,
                egg == null || !egg.has("primary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "primary", ""), key + " egg primary"),
                egg == null || !egg.has("secondary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "secondary", ""), key + " egg secondary"),
                Math.max(1, JsonUtils.getInt(json, "trackingRange", 80)),
                Math.max(1, JsonUtils.getInt(json, "trackingFrequency", 3)),
                JsonUtils.getBoolean(json, "trackVelocity", true),
                attributes,
                JsonUtils.getBoolean(json, "hostile", false),
                JsonUtils.getBoolean(json, "passive", false),
                JsonUtils.getBoolean(json, "ignoresSpawnRules", false),
                strings(json, "targets"),
                JsonUtils.getBoolean(json, "persistent", false),
                JsonUtils.getBoolean(json, "silent", false),
                JsonUtils.getBoolean(json, "picksUpLoot", false),
                JsonUtils.getBoolean(json, "hideArmor", false),
                JsonUtils.getBoolean(json, "hideHeld", false),
                json.has("tint") ? ContentTypes.color(JsonUtils.getString(json, "tint", ""), key + " tint") : 0,
                tintParts,
                JsonUtils.getBoolean(json, "showName", false),
                JsonUtils.getBoolean(json, "explodes", false),
                JsonUtils.getBoolean(json, "throws", false),
                Math.max(0, JsonUtils.getInt(json, "throwReload", 0)) * 20,
                Math.max(0, JsonUtils.getInt(json, "throwRetreat", 0)) * 20,
                Math.max(0, JsonUtils.getInt(json, "throwAmmo", 0)),
                Math.max(0.0F, JsonUtils.getFloat(json, "throwPower", 1.0F)),
                JsonUtils.getFloat(json, "throwArc", 0.35F),
                Math.max(0.0F, JsonUtils.getFloat(json, "explosionPower", 3.0F)),
                Math.max(1, JsonUtils.getInt(json, "explosionFuse", 30)),
                JsonUtils.getBoolean(json, "explosionFire", false),
                equipment, spawns,
                strings(json, "biomes"), strings(json, "biomeTypes"), strings(json, "requires"),
                JsonUtils.getBoolean(json, "charges", false),
                JsonUtils.getBoolean(json, "pounces", false),
                Math.max(0, JsonUtils.getInt(json, "sniffs", 0)),
                JsonUtils.getBoolean(json, "sleepsByDay", false),
                Math.max(0, JsonUtils.getInt(json, "home", 0)),
                Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(json, "fleesWhenHurt", 0.0F))),
                JsonUtils.getBoolean(json, "patrols", false),
                JsonUtils.getBoolean(json, "swoops", false),
                JsonUtils.getBoolean(json, "gusts", false),
                Math.max(0.1F, JsonUtils.getFloat(json, "gustPower", 1.5F)),
                Math.max(0, JsonUtils.getInt(json, "threatLeast", 0)),
                Math.max(0, JsonUtils.getInt(json, "threatHostile", 0)));
    }

    @Nullable public static VillageDef village(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Village file {} is empty, ignoring it", key);
            return null;
        }
        String type = JsonUtils.getString(json, "type", VillageDef.FARM).trim().toLowerCase(Locale.ROOT);
        if (!VillageDef.FARM.equals(type) && !VillageDef.TEMPLATE.equals(type)) {
            ContentLog.LOGGER.error("Village plot {} asks for type '{}', which is not {} or {}, using {}", key, type, VillageDef.FARM, VillageDef.TEMPLATE, VillageDef.FARM);
            type = VillageDef.FARM;
        }
        String structure = JsonUtils.getString(json, "structure", "");
        if (VillageDef.TEMPLATE.equals(type) && structure.isEmpty()) {
            ContentLog.LOGGER.error("Village plot {} is a template but names no structure, ignoring it", key);
            return null;
        }
        return new VillageDef(key, type,
                Math.max(1, JsonUtils.getInt(json, "weight", 3)),
                Math.max(0, JsonUtils.getInt(json, "leastCount", 1)),
                Math.max(0, JsonUtils.getInt(json, "mostCount", 4)),
                Math.max(3, JsonUtils.getInt(json, "width", 7)),
                Math.max(1, JsonUtils.getInt(json, "height", 4)),
                Math.max(3, JsonUtils.getInt(json, "depth", 9)),
                strings(json, "crops"),
                JsonUtils.getString(json, "edge", "minecraft:log"),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                JsonUtils.getBoolean(json, "water", true),
                Math.max(1, JsonUtils.getInt(json, "rowWidth", 2)),
                structure,
                JsonUtils.getString(json, "ground", "minecraft:dirt"),
                Math.max(1, Math.min(100, JsonUtils.getInt(json, "integrity", 100))),
                Math.max(0, JsonUtils.getInt(json, "villagers", 0)),
                JsonUtils.getString(json, "villagerEntity", ""),
                JsonUtils.getInt(json, "villagerX", 1),
                JsonUtils.getInt(json, "villagerY", 1),
                JsonUtils.getInt(json, "villagerZ", 1),
                strings(json, "requires"),
                JsonUtils.getString(json, "lootTable", ""));
    }

    private static ShapeDef shape(ResourceLocation key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = JsonUtils.getJsonObject(json, "shape");
        String type = JsonUtils.getString(entry, "type", ShapeDef.CLUSTER).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SHAPES.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for shape '{}', which is not one of {}, using {}", key, type, KNOWN_SHAPES, ShapeDef.CLUSTER);
            type = ShapeDef.CLUSTER;
        }
        String plane = JsonUtils.getString(entry, "plane", ShapeDef.CIRCLE).trim().toLowerCase(Locale.ROOT);
        if (!ShapeDef.CIRCLE.equals(plane) && !ShapeDef.SQUARE.equals(plane)) {
            ContentLog.LOGGER.error("Worldgen {} asks for plane '{}', which is not {} or {}, using {}", key, plane, ShapeDef.CIRCLE, ShapeDef.SQUARE, ShapeDef.CIRCLE);
            plane = ShapeDef.CIRCLE;
        }
        ShapeDef made = new ShapeDef(type,
                amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                amount(entry, "width", 12, 3),
                plane,
                JsonUtils.getBoolean(entry, "slim", false),
                JsonUtils.getString(entry, "outline", ""),
                JsonUtils.getString(entry, "fill", ""),
                surface(entry),
                amount(entry, "stackHeight", 1, 1),
                JsonUtils.getBoolean(entry, "seeSky", true),
                JsonUtils.getBoolean(entry, "checkStay", true),
                JsonUtils.getInt(entry, "scatterX", 8),
                JsonUtils.getInt(entry, "scatterY", 4),
                JsonUtils.getInt(entry, "scatterZ", 8),
                JsonUtils.getString(entry, "log", ""),
                JsonUtils.getString(entry, "leaves", ""),
                JsonUtils.getBoolean(entry, "vines", false),
                JsonUtils.getBoolean(entry, "hanging", false),
                JsonUtils.getString(entry, "structure", ""),
                picks(entry, "structures", "structure"),
                picks(entry, "turns", "turn"),
                picks(entry, "mirrors", "mirror"),
                taper(key, entry),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "integrity", 100))),
                Math.max(0, JsonUtils.getInt(entry, "rarity", 0)),
                JsonUtils.getBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(JsonUtils.getJsonObject(entry, "field", new JsonObject())) : null,
                JsonUtils.getFloat(entry, "threshold", 0.5F),
                JsonUtils.getString(entry, "lootTable", ""));
        made.locateAs = JsonUtils.getString(entry, "locateAs", "");
        made.fade = Math.max(0, JsonUtils.getInt(entry, "fade", 0));
        if (entry.has("at")) {
            JsonArray pinned = JsonUtils.getJsonArray(entry, "at");
            if (pinned.size() == 2) { made.at = new int[] { pinned.get(0).getAsInt(), pinned.get(1).getAsInt() }; }
            else { ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key); }
        }
        return made;
    }

    private static float baby(JsonObject json) {
        if (!json.has("baby")) { return 0.0F; }
        JsonElement held = json.get("baby");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? 1.0F : 0.0F; }
        return Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(json, "baby", 0.0F)));
    }

    private static List<PickDef> picks(JsonObject entry, String listKey, String nameKey) {
        if (!entry.has(listKey)) { return Collections.emptyList(); }
        List<PickDef> picked = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(entry, listKey)) {
            if (element.isJsonPrimitive()) { picked.add(new PickDef(element.getAsString().trim().toLowerCase(Locale.ROOT), 1)); }
            else {
                JsonObject held = element.getAsJsonObject();
                picked.add(new PickDef(JsonUtils.getString(held, nameKey, "").trim().toLowerCase(Locale.ROOT), JsonUtils.getInt(held, "weight", 1)));
            }
        }
        return Collections.unmodifiableList(picked);
    }

    private static String taper(ResourceLocation key, JsonObject entry) {
        String named = JsonUtils.getString(entry, "taper", ShapeDef.STRAIGHT).trim().toLowerCase(Locale.ROOT);
        if (ShapeDef.STRAIGHT.equals(named) || ShapeDef.BELL.equals(named) || ShapeDef.NEEDLE.equals(named)) { return named; }
        ContentLog.LOGGER.error("Worldgen {} asks for taper '{}', which is not {}, {} or {}, using {}", key, named, ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE, ShapeDef.STRAIGHT);
        return ShapeDef.STRAIGHT;
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return Collections.emptyList(); }
        JsonElement element = entry.get("surface");
        if (!element.isJsonArray()) { return Collections.singletonList(element.getAsString()); }
        return strings(entry, "surface");
    }

    private static List<BlockMatchDef> replaces(ResourceLocation key, JsonObject json) {
        if (!json.has("replace")) { return Collections.singletonList(match(key, "minecraft:stone")); }
        JsonElement element = json.get("replace");
        if (!element.isJsonArray()) { return Collections.singletonList(match(key, element)); }
        List<BlockMatchDef> values = new ArrayList<>();
        for (JsonElement name : element.getAsJsonArray()) { values.add(match(key, name)); }
        return values.isEmpty() ? Collections.singletonList(match(key, "minecraft:stone")) : Collections.unmodifiableList(values);
    }

    private static List<BlockMatchDef> adjacent(ResourceLocation key, JsonObject json) {
        if (!json.has("adjacent")) { return Collections.emptyList(); }
        JsonElement element = json.get("adjacent");
        if (!element.isJsonArray()) { return Collections.singletonList(match(key, element)); }
        List<BlockMatchDef> values = new ArrayList<>();
        for (JsonElement name : element.getAsJsonArray()) { values.add(match(key, name)); }
        return Collections.unmodifiableList(values);
    }

    public static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }
        JsonObject entry = element.getAsJsonObject();
        return new BlockMatchDef(new ResourceLocation(JsonUtils.getString(entry, "block", "minecraft:stone")), JsonUtils.getInt(entry, "meta", -1), Json.map(entry, "properties"));
    }

    private static BlockMatchDef match(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length < 3) { return new BlockMatchDef(new ResourceLocation(name), -1, Collections.emptyMap()); }
        ResourceLocation block = new ResourceLocation(parts[0] + ":" + parts[1]);
        try { return new BlockMatchDef(block, Integer.parseInt(parts[2]), Collections.emptyMap()); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Block metadata '{}' in {} is not a number, using 0", parts[2], key);
            return new BlockMatchDef(block, 0, Collections.emptyMap());
        }
    }

    private static List<BlockWeightDef> weights(JsonObject json) {
        if (!json.has("blocks")) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, "blocks");
        List<BlockWeightDef> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject entry = element.getAsJsonObject();
            String name = JsonUtils.getString(entry, "block", "");
            if (name.isEmpty()) { continue; }
            values.add(new BlockWeightDef(new ResourceLocation(name),
                    JsonUtils.getInt(entry, "meta", 0),
                    Math.max(1, JsonUtils.getInt(entry, "weight", 1)),
                    Json.map(entry, "properties")));
        }
        return Collections.unmodifiableList(values);
    }

    private static List<Integer> integers(JsonObject json) {
        if (!json.has("dimensions")) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, "dimensions");
        List<Integer> values = new ArrayList<>(array.size());
        for (JsonElement element : array) { values.add(element.getAsInt()); }
        return Collections.unmodifiableList(values);
    }

    private static BlockRenderLayer renderLayer(String value, String context) {
        if (value.isEmpty()) { return BlockRenderLayer.SOLID; }
        try { return BlockRenderLayer.valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("Unknown renderLayer '{}' in {}, using solid", value, context);
            return BlockRenderLayer.SOLID;
        }
    }

    @Nullable private static AxisAlignedBB bounds(ResourceLocation key, JsonObject json) {
        if (!json.has("bounds")) { return null; }
        JsonArray array = JsonUtils.getJsonArray(json, "bounds");
        if (array.size() != 6) {
            ContentLog.LOGGER.error("Bounds in {} need six numbers, minX minY minZ maxX maxY maxZ, ignoring them", key);
            return null;
        }
        double[] v = new double[6];
        for (int i = 0; i < 6; i++) { v[i] = array.get(i).getAsDouble(); }
        return new AxisAlignedBB(v[0], v[1], v[2], v[3], v[4], v[5]);
    }

    @Nullable private static String potion(JsonObject json) {
        if (!json.has("potion") || json.get("potion").isJsonNull()) { return null; }
        String value = JsonUtils.getString(json, "potion");
        return value.trim().isEmpty() ? null : value;
    }

    public static String placeholderName(int meta, int digits) {
        StringBuilder builder = new StringBuilder(PLACEHOLDER);
        String number = Integer.toString(meta);
        for (int i = number.length(); i < digits; i++) { builder.append('0'); }
        return builder.append(number).toString();
    }
}
