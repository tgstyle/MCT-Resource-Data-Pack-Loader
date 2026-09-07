package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionPortalDef;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentDimensionParser {
    private static final Gson GSON = new Gson();
    private static final Set<String> KNOWN_TERRAIN = Set.of(DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END);
    private static final List<String> FLAT_DEFAULT = List.of("minecraft:bedrock", "59*minecraft:stone", "3*minecraft:dirt", "minecraft:grass_block");

    private ContentDimensionParser() {}

    @Nullable public static DimensionDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Dimension {} is empty, ignoring it", key);
            return null;
        }
        JsonObject terrain = GsonHelper.getAsJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = GsonHelper.getAsJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = GsonHelper.getAsJsonObject(json, "sky", new JsonObject());
        if (json.has("id")) { ContentLog.LOGGER.debug("Dimension {} names an id, which this line does not use, the dimension being {} everywhere", key, key); }
        if (json.has("keepLoaded")) { ContentLog.LOGGER.debug("Dimension {} asks to be kept loaded, which this line leaves to forceload", key); }
        String type = GsonHelper.getAsString(terrain, "type", DimensionDef.OVERWORLD).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_TERRAIN.contains(type)) {
            ContentLog.LOGGER.error("Dimension {} asks for terrain '{}', which is not one of {}, using {}", key, type, KNOWN_TERRAIN, DimensionDef.OVERWORLD);
            type = DimensionDef.OVERWORLD;
        }
        String source = GsonHelper.getAsString(biomes, "source", DimensionDef.INHERIT).trim().toLowerCase(Locale.ROOT);
        if (!DimensionDef.SINGLE.equals(source) && !DimensionDef.INHERIT.equals(source)) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}', which is not {} or {}, using {}", key, source, DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        int minHeight = terrain.has("minHeight") ? GsonHelper.getAsInt(terrain, "minHeight") : DimensionDef.UNSET;
        int maxHeight = terrain.has("maxHeight") ? GsonHelper.getAsInt(terrain, "maxHeight") : DimensionDef.UNSET;
        if ((minHeight == DimensionDef.UNSET) != (maxHeight == DimensionDef.UNSET)) {
            ContentLog.LOGGER.error("Dimension {} names only one of minHeight and maxHeight, so both are left to the terrain type", key);
            minHeight = DimensionDef.UNSET;
            maxHeight = DimensionDef.UNSET;
        }
        else if (minHeight != DimensionDef.UNSET) {
            minHeight = Mth.floor(minHeight / 16.0D) * 16;
            maxHeight = Mth.ceil(maxHeight / 16.0D) * 16;
            if (maxHeight - minHeight < 16 || maxHeight - minHeight > 4064 || minHeight < -2032 || maxHeight > 2032) {
                ContentLog.LOGGER.error("Dimension {} asks for heights {} to {}, which are outside what a dimension can hold (-2032 to 2032, at least 16 apart, at most 4064), so they are left to the terrain type", key, minHeight, maxHeight);
                minHeight = DimensionDef.UNSET;
                maxHeight = DimensionDef.UNSET;
            }
        }
        JsonObject options = terrain.has("generatorOptions") && terrain.get("generatorOptions").isJsonObject() ? terrain.getAsJsonObject("generatorOptions") : new JsonObject();
        String fog = GsonHelper.getAsString(sky, "fogColor", "").trim();
        String skyColor = GsonHelper.getAsString(sky, "skyColor", "").trim();
        String cloudColor = GsonHelper.getAsString(sky, "cloudColor", "").trim();
        String respawn = GsonHelper.getAsString(sky, "respawnDimension", "").trim();
        ResourceLocation respawnDimension = respawn.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(respawn));
        if (!respawn.isEmpty() && respawnDimension == null) { ContentLog.LOGGER.error("Dimension {} names respawnDimension '{}', which is not a dimension id, so respawns stay where the game puts them", key, respawn); }
        return new DimensionDef(key, type, flatLayers(key, terrain), GsonHelper.getAsBoolean(terrain, "structures", true), source,
                GsonHelper.getAsString(biomes, "biome", "minecraft:plains").trim(), minHeight, maxHeight,
                options.has("seaLevel") ? GsonHelper.getAsInt(options, "seaLevel") : -1, GsonHelper.getAsBoolean(options, "useLavaOceans", false),
                GsonHelper.getAsBoolean(sky, "hasSkyLight", true), GsonHelper.getAsBoolean(sky, "surfaceWorld", true), GsonHelper.getAsBoolean(sky, "spawning", true),
                sky.has("cloudHeight") ? GsonHelper.getAsInt(sky, "cloudHeight") : -1, Math.max(1.0E-5D, GsonHelper.getAsDouble(sky, "movementFactor", 1.0D)),
                fog.isEmpty() ? -1 : ContentParser.color(fog, key + " fogColor") & 0xFFFFFF, skyColor.isEmpty() ? -1 : ContentParser.color(skyColor, key + " skyColor") & 0xFFFFFF,
                cloudColor.isEmpty() ? -1 : ContentParser.color(cloudColor, key + " cloudColor") & 0xFFFFFF, GsonHelper.getAsLong(sky, "fixedTime", -1L),
                GsonHelper.getAsBoolean(sky, "sunriseColors", true), GsonHelper.getAsBoolean(sky, "nether", false), GsonHelper.getAsBoolean(sky, "beds", true),
                GsonHelper.getAsBoolean(sky, "waterVaporizes", false), GsonHelper.getAsBoolean(sky, "showFog", false), Mth.clamp(GsonHelper.getAsFloat(sky, "ambientLight", 0.0F), 0.0F, 1.0F),
                GsonHelper.getAsFloat(sky, "starBrightness", -1.0F), GsonHelper.getAsBoolean(sky, "renderSky", true), GsonHelper.getAsBoolean(sky, "renderClouds", true),
                GsonHelper.getAsBoolean(sky, "renderWeather", true), respawnDimension, GsonHelper.getAsBoolean(sky, "respawn", true),
                sky.has("groundLevel") ? GsonHelper.getAsInt(sky, "groundLevel") : DimensionDef.UNSET, gameRules(key, json), Json.strings(json, "requires"), portal(key, json));
    }

    @Nullable private static DimensionPortalDef portal(ResourceLocation key, JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "portal");
        List<String> frames = Json.strings(entry, "frames");
        if (frames.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has a portal section naming no frames, so nothing could ever open it", key);
            return null;
        }
        int color = ContentParser.color(GsonHelper.getAsString(entry, "color", "#FFFFFF"), key + " portal color");
        String back = GsonHelper.getAsString(entry, "return", DimensionPortalDef.BUILT).trim().toLowerCase(Locale.ROOT);
        if (!DimensionPortalDef.BUILT.equals(back) && !DimensionPortalDef.PLAYER.equals(back) && !DimensionPortalDef.NONE.equals(back)) {
            ContentLog.LOGGER.error("Dimension {} asks for a return of '{}', which is none of {}, {} or {}, building one instead", key, back, DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE);
            back = DimensionPortalDef.BUILT;
        }
        PortalDef travel = ContentParser.portal(key, json, key, false, true);
        return travel == null ? null : new DimensionPortalDef(frames, GsonHelper.getAsString(entry, "ignitedBy", "minecraft:flint_and_steel").trim(), color, back, travel);
    }

    private static List<String> flatLayers(ResourceLocation key, JsonObject terrain) {
        if (!terrain.has("generatorOptions")) { return FLAT_DEFAULT; }
        JsonElement options = terrain.get("generatorOptions");
        if (options.isJsonArray()) { return Json.strings(terrain, "generatorOptions"); }
        if (!options.isJsonPrimitive()) { return FLAT_DEFAULT; }
        String written = options.getAsString().trim();
        if (written.isEmpty()) { return FLAT_DEFAULT; }
        String[] parts = written.split(";");
        String layers = parts.length > 1 ? parts[1] : parts[0];
        List<String> found = new ArrayList<>();
        for (String layer : layers.split(",")) {
            if (!layer.trim().isEmpty()) { found.add(layer.trim()); }
        }
        if (found.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has flat generator options '{}' naming no layers, using the default ground", key, written);
            return FLAT_DEFAULT;
        }
        return found;
    }

    public static Map<String, String> gameRules(ResourceLocation key, JsonObject json) {
        Map<String, String> found = new LinkedHashMap<>();
        if (!json.has("gameRules")) { return Map.of(); }
        for (Map.Entry<String, JsonElement> rule : GsonHelper.getAsJsonObject(json, "gameRules").entrySet()) {
            if (!rule.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Dimension {} sets game rule '{}' to something that is not a value, ignoring it", key, rule.getKey());
                continue;
            }
            found.put(rule.getKey(), rule.getValue().getAsString());
        }
        return Map.copyOf(found);
    }
}
