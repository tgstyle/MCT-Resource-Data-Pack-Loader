package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

final class ContentForeignDimensions {
    private static final String FOLDER = "dimension";
    private static final String NOISE = "minecraft:noise";
    private static final String FLAT = "minecraft:flat";
    private static final Set<String> GAME = Set.of(ContentBiomes.OVERWORLD, "minecraft:the_nether", "minecraft:the_end");

    private ContentForeignDimensions() {}

    static void generate() {
        boolean flatAsked = ContentBedrock.bedrockAsked();
        boolean voidAsked = ContentVoidWorld.voidAsked();
        if (!flatAsked && !voidAsked) { return; }
        Set<String> files = GameData.modFiles(FOLDER);
        if (flatAsked && !ContentBedrock.dimensionsAreBlacklist()) { warnUnreached("flatBedrockDimensions", ContentBedrock.bedrockDimensions(), files, "its bedrock is flattened chunk by chunk as it generates"); }
        if (voidAsked && !ContentVoidWorld.voidBlacklist()) { warnUnreached("voidWorldDimensions", ContentVoidWorld.voidDimensions(), files, "its features, structures and mobs are left out as it generates, and its terrain too when its generator is noise based; any other generator keeps its own terrain"); }
        List<String> applied = new ArrayList<>();
        for (String file : files) {
            ResourceLocation id = ResourceLocation.tryParse(file);
            if (id == null || ownedByDefinition(id)) { continue; }
            boolean flatBedrock = ContentBedrock.bedrockApplies(file);
            boolean isVoid = ContentVoidWorld.voidApplies(file);
            if ((flatBedrock || isVoid) && override(id, flatBedrock, isVoid)) { applied.add(file); }
        }
        if (!applied.isEmpty()) { Summary.info("dimensions.foreign", "Applied flat bedrock or void to " + applied.size() + " dimension file(s) of other mods: " + String.join(", ", applied)); }
    }

    private static boolean ownedByDefinition(ResourceLocation id) {
        DimensionDef def = ContentDimensions.def(id);
        return def != null && ContentDimensions.shapes(def) && ContentRegistry.available(def.requires(), def.key());
    }

    private static void warnUnreached(String key, List<String> listed, Set<String> files, String outcome) {
        for (String entry : listed) {
            String dimension = ContentFormats.dimensionId(entry);
            ResourceLocation id = ResourceLocation.tryParse(dimension);
            if (id == null || GAME.contains(dimension) || files.contains(dimension) || ContentDimensions.def(id) != null) { continue; }
            ContentLog.LOGGER.warn("{} names dimension {}, which no mod defines in a dimension file under data/{}/{}, so it is made in code and {}", key, dimension, id.getNamespace(), FOLDER, outcome);
        }
    }

    private static boolean override(ResourceLocation id, boolean flatBedrock, boolean isVoid) {
        JsonObject stem = GameData.json(file(FOLDER, id.getNamespace(), id.getPath()));
        if (stem == null) { return false; }
        if (!GsonHelper.isObjectNode(stem, "generator")) {
            ContentLog.LOGGER.warn("Dimension {} has no generator object in its dimension file, so it keeps the game's own terrain", id);
            return false;
        }
        JsonObject generator = stem.getAsJsonObject("generator");
        String kind = Json.text(generator, "type");
        boolean changed;
        if (NOISE.equals(kind)) { changed = noise(id, generator, isVoid); }
        else if (FLAT.equals(kind) && isVoid) {
            empty(generator);
            changed = true;
        }
        else {
            if (isVoid) { ContentLog.LOGGER.warn("Dimension {} has a '{}' generator, which cannot be made void, so it keeps the game's own terrain", id, kind); }
            else if (flatBedrock) { ContentLog.LOGGER.debug("Dimension {} has a '{}' generator, so its bedrock is flattened chunk by chunk instead of in its noise settings", id, kind); }
            return false;
        }
        if (changed) { GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), FOLDER + "/" + id.getPath() + PackManager.JSON_SUFFIX, stem.toString()); }
        return changed;
    }

    private static boolean noise(ResourceLocation id, JsonObject generator, boolean isVoid) {
        JsonObject settings = settingsOf(generator, id);
        if (settings == null) { return false; }
        try {
            if (isVoid) { ContentWorldShape.empty(settings); }
            else { ContentBedrock.flattenBedrock(settings, id.toString()); }
        }
        catch (JsonParseException unreadable) {
            ContentLog.LOGGER.warn("The noise settings of dimension {} are not laid out as the game's own, so it keeps its terrain", id, unreadable);
            return false;
        }
        ResourceLocation made = ResourceDataPackLoader.id("foreign/" + id.getNamespace() + "/" + id.getPath() + (isVoid ? "_void" : "_noise"));
        GeneratedResources.put(PackType.SERVER_DATA, made.getNamespace(), "worldgen/noise_settings/" + made.getPath() + PackManager.JSON_SUFFIX, settings.toString());
        generator.addProperty("settings", made.toString());
        return true;
    }

    @Nullable private static JsonObject settingsOf(JsonObject generator, ResourceLocation dimension) {
        JsonElement named = generator.get("settings");
        if (named != null && named.isJsonObject()) { return named.getAsJsonObject().deepCopy(); }
        ResourceLocation settings = named != null && named.isJsonPrimitive() ? ResourceLocation.tryParse(named.getAsString()) : null;
        if (settings == null) {
            ContentLog.LOGGER.warn("Dimension {} does not name its noise settings, so it keeps the game's own terrain", dimension);
            return null;
        }
        return GameData.json(file("worldgen/noise_settings", settings.getNamespace(), settings.getPath()));
    }

    private static void empty(JsonObject generator) {
        JsonObject settings = Json.object(generator, "settings");
        settings.add("layers", new JsonArray());
        settings.addProperty("features", false);
        settings.addProperty("lakes", false);
        settings.add("structure_overrides", new JsonArray());
        generator.add("settings", settings);
    }

    private static ResourceLocation file(String folder, String namespace, String path) { return ResourceLocation.fromNamespaceAndPath(namespace, folder + "/" + path + PackManager.JSON_SUFFIX); }
}
