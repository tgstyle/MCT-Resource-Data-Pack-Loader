package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

final class SwapEntry {
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final List<SwapEntry> ALL = new ArrayList<>();
    private final ItemSelection from = new ItemSelection("Recipe swap", "replaces");
    private final RecipeSelector scope = new RecipeSelector();
    private final Identifier to;
    private final boolean output;
    private final boolean scoped;

    private SwapEntry(Identifier to, boolean output, boolean scoped) {
        this.to = to;
        this.output = output;
        this.scoped = scoped;
    }

    static synchronized List<SwapEntry> all() {
        if (GENERATION.stale()) {
            ALL.clear();
            if (Config.recipes.swaps()) { Json.eachObject(PackManager.RECIPE_SWAPS, "recipe swap file", SwapEntry::readFile); }
        }
        return Collections.unmodifiableList(ALL);
    }

    Identifier to() { return to; }

    boolean output() { return output; }

    boolean scoped() { return scoped; }

    boolean needsTags() { return !from.tags().isEmpty(); }

    boolean inScope(Identifier id) { return !scoped || scope.matchesName(id); }

    Set<Identifier> members(@Nullable Map<Identifier, List<Identifier>> tagged) {
        Set<Identifier> ids = new LinkedHashSet<>(from.items());
        for (Identifier tag : from.tags()) {
            List<Identifier> held = tagged == null ? null : tagged.get(tag);
            if (held == null) { ContentLog.LOGGER.debug("Recipe swap tag {} is no item tag", tag); }
            else { ids.addAll(held); }
        }
        return ids;
    }

    private static void readFile(Identifier key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has("swaps")) {
            ContentLog.LOGGER.error("Recipe swap file {} has no swaps array, ignoring it", key);
            return;
        }
        for (JsonElement element : Json.getJsonArray(json, "swaps")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A swap in {} is not an object, skipping it", key);
                continue;
            }
            SwapEntry entry = read(key, element.getAsJsonObject());
            if (entry != null) { ALL.add(entry); }
        }
    }

    @Nullable private static SwapEntry read(Identifier key, JsonObject json) {
        if (!json.has("from") || !json.get("from").isJsonObject()) {
            ContentLog.LOGGER.error("A swap in {} needs a from object, skipping it", key);
            return null;
        }
        Item item = ContentStacks.find(key, Json.getString(json, "to", ""));
        if (item == null || item == Items.AIR) {
            ContentLog.LOGGER.error("A swap in {} needs a valid to item, skipping it", key);
            return null;
        }
        if (json.has("recipes") && !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("A swap in {} has a recipes value that is not an object, skipping it", key);
            return null;
        }
        boolean scoped = json.has("recipes");
        SwapEntry entry = new SwapEntry(BuiltInRegistries.ITEM.getKey(item), Json.getBoolean(json, "output", false), scoped);
        entry.from.read(json.getAsJsonObject("from"), key);
        if (entry.from.unnamed()) {
            ContentLog.LOGGER.error("The from of a swap in {} names nothing, skipping it", key);
            return null;
        }
        entry.from.unknown();
        if (entry.from.items().isEmpty() && entry.from.tags().isEmpty()) {
            ContentLog.LOGGER.error("The from of a swap in {} matches no registered item, skipping it", key);
            return null;
        }
        if (scoped) { entry.readScope(key, json.getAsJsonObject("recipes")); }
        return entry;
    }

    private void readScope(Identifier key, JsonObject recipes) {
        if (recipes.has("outputs")) { ContentLog.LOGGER.error("A swap in {} has outputs in its recipes, which a swap does not read, ignoring it", key); }
        JsonObject limits = new JsonObject();
        for (String member : new String[] {"names", "namespaces"}) {
            if (recipes.has(member)) { limits.add(member, recipes.get(member)); }
        }
        scope.read(key, limits);
    }
}
