package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

final class SwapEntry {
    static final List<String> FILE_KEYS = List.of("requires", "swaps", "remainder", "transfer", "recipes", "template", "edit", "scriptFile", "script", "types", "before_swaps");
    private static final List<String> SWAP_KEYS = List.of("from", "to", "output", "recipes", "types", "paths");
    private static final List<String> FROM_KEYS = List.of("names", "namespaces", "tags");
    private static final List<String> SCOPE_KEYS = List.of("names", "namespaces", "outputs");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final List<SwapEntry> ALL = new ArrayList<>();
    private final ItemSelection from = new ItemSelection("Recipe swap", "replaces");
    private final RecipeSelector scope = new RecipeSelector();
    private final RecipeTypeFilter types = new RecipeTypeFilter();
    private final List<String> paths = new ArrayList<>();
    private final ResourceLocation key;
    private final ResourceLocation to;
    private final boolean output;
    private final boolean scoped;
    private final boolean tagged;
    private Set<ResourceLocation> named = Set.of();

    private SwapEntry(ResourceLocation key, ResourceLocation to, boolean output, boolean scoped, boolean tagged) {
        this.key = key;
        this.to = to;
        this.output = output;
        this.scoped = scoped;
        this.tagged = tagged;
    }

    static List<SwapEntry> all() {
        if (GENERATION.stale()) {
            ALL.clear();
            if (Config.recipes.swaps()) { Json.eachObject(PackManager.RECIPE_SWAPS, "recipe swap file", SwapEntry::readFile); }
        }
        return Collections.unmodifiableList(ALL);
    }

    Set<ResourceLocation> items() { return named; }

    Set<ResourceLocation> tags() { return from.tags(); }

    ResourceLocation to() { return to; }

    ResourceLocation key() { return key; }

    boolean tagged() { return tagged; }

    boolean foreign(String type) { return types.matches(type); }

    List<String> paths() { return paths; }

    boolean output() { return output; }

    boolean scoped() { return scoped; }

    boolean inScope(ResourceLocation id) { return !scoped || scope.matchesName(id); }

    private static void readFile(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has("swaps") && (json.has("remainder") || json.has("transfer") || json.has("template") || json.has("edit") || json.has("script") || json.has("scriptFile"))) { return; }
        if (!json.has("swaps")) {
            ContentLog.LOGGER.error("Recipe swap file {} has no swaps array, ignoring it", key);
            return;
        }
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        for (JsonObject element : Json.objectsOrSkip(Json.getJsonArray(json, "swaps"), "A swap in {} is not an object, skipping it", key)) {
            SwapEntry entry = read(key, element);
            if (entry != null) { ALL.add(entry); }
        }
    }

    @Nullable private static SwapEntry read(ResourceLocation key, JsonObject json) {
        if (!json.has("from") || !json.get("from").isJsonObject()) {
            ContentLog.LOGGER.error("A swap in {} needs a from object, skipping it", key);
            return null;
        }
        Json.unknownKeys(json, "swaps", SWAP_KEYS);
        Json.unknownKeys(json.getAsJsonObject("from"), "from", FROM_KEYS);
        String target = Json.getString(json, "to", "");
        boolean tagged = target.startsWith("#");
        ResourceLocation id = tagged ? Registered.tagId(target) : itemId(key, target);
        if (id == null) {
            if (tagged) { ContentLog.LOGGER.error("The to tag '{}' of a swap in {} is not a valid name, skipping it", target, key); }
            else { ContentLog.LOGGER.error("A swap in {} needs a valid to item, skipping it", key); }
            return null;
        }
        if (json.has("recipes") && !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("A swap in {} has a recipes value that is not an object, skipping it", key);
            return null;
        }
        boolean scoped = json.has("recipes");
        SwapEntry entry = new SwapEntry(key, id, Json.getBoolean(json, "output", false), scoped, tagged);
        entry.from.read(json.getAsJsonObject("from"), key);
        if (entry.from.unnamed()) {
            ContentLog.LOGGER.error("The from of a swap in {} names nothing, skipping it", key);
            return null;
        }
        entry.from.unknown();
        entry.named = new LinkedHashSet<>(entry.from.items());
        if (entry.named.isEmpty() && entry.from.tags().isEmpty()) {
            ContentLog.LOGGER.error("The from of a swap in {} matches no registered item, skipping it", key);
            return null;
        }
        if (!tagged && entry.named.contains(id)) {
            ContentLog.LOGGER.error("The to item {} of a swap in {} is matched by its own from, skipping it", id, key);
            return null;
        }
        if (scoped) { entry.readScope(key, json.getAsJsonObject("recipes")); }
        entry.readTypes(key, json);
        return entry;
    }

    @Nullable private static ResourceLocation itemId(ResourceLocation key, String target) {
        Item item = ContentStacks.find(key, target);
        return item == null ? null : ForgeRegistries.ITEMS.getKey(item);
    }

    private void readTypes(ResourceLocation key, JsonObject json) {
        types.read(key, json);
        List<String> listed = Json.strings(json, "paths");
        if (!listed.isEmpty() && types.isEmpty()) {
            ContentLog.LOGGER.error("A swap in {} has paths but no types, ignoring its paths", key);
            return;
        }
        for (String path : listed) {
            if (JsonTree.badPath(path)) { ContentLog.LOGGER.error("The path '{}' in {} has an empty part, skipping it", path, key); }
            else { paths.add(path); }
        }
    }

    private void readScope(ResourceLocation key, JsonObject recipes) {
        Json.unknownKeys(recipes, "recipes", SCOPE_KEYS);
        if (recipes.has("outputs")) { ContentLog.LOGGER.error("A swap in {} has outputs in its recipes, which a swap does not read, ignoring it", key); }
        JsonObject limits = new JsonObject();
        for (String member : new String[] {"names", "namespaces"}) {
            if (recipes.has(member)) { limits.add(member, recipes.get(member)); }
        }
        scope.read(key, limits);
    }
}
