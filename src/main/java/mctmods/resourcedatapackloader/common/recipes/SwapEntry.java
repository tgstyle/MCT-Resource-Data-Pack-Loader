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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

final class SwapEntry {
    static final List<String> FILE_KEYS = Collections.unmodifiableList(Arrays.asList("requires", "swaps", "remainder", "transfer", "recipes", "template", "edit", "scriptFile", "script"));
    private static final List<String> SWAP_KEYS = Collections.unmodifiableList(Arrays.asList("from", "to", "output", "recipes", "types", "paths"));
    private static final List<String> FROM_KEYS = Collections.unmodifiableList(Arrays.asList("names", "namespaces", "oreDict"));
    private static final List<String> SCOPE_KEYS = Collections.unmodifiableList(Arrays.asList("names", "namespaces", "outputs"));
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final List<SwapEntry> ALL = new ArrayList<>();
    private final ItemSelection from = new ItemSelection("Recipe swap", "replaces");
    private final RecipeSelector scope = new RecipeSelector();
    private final ItemStack to;
    private final boolean output;
    private final boolean scoped;

    private SwapEntry(ItemStack to, boolean output, boolean scoped) {
        this.to = to;
        this.output = output;
        this.scoped = scoped;
    }

    static List<SwapEntry> all() {
        if (GENERATION.stale()) {
            ALL.clear();
            if (Config.recipes.swaps) { Json.eachObject(PackManager.RECIPE_SWAPS, "recipe swap file", SwapEntry::readFile); }
        }
        return Collections.unmodifiableList(ALL);
    }

    boolean matches(ItemStack stack) { return from.matches(stack); }

    boolean output() { return output; }

    boolean scoped() { return scoped; }

    boolean inScope(ResourceLocation key) { return !scoped || scope.matches(key, ItemStack.EMPTY); }

    ItemStack result(int count) { return new ItemStack(to.getItem(), count, to.getMetadata()); }

    private static void readFile(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has("swaps") && (json.has("remainder") || json.has("transfer"))) { return; }
        if (!json.has("swaps")) {
            boolean template = json.has("template");
            boolean edit = json.has("edit");
            boolean scriptFile = json.has("scriptFile");
            boolean script = json.has("script");
            if (template) { ContentLog.LOGGER.error("Recipe swap file {} has a template, which 1.12.2 does not read, ignoring it", key); }
            if (edit) { ContentLog.LOGGER.error("Recipe swap file {} has an edit, which 1.12.2 does not read, ignoring it", key); }
            if (scriptFile) { ContentLog.LOGGER.error("Recipe swap file {} has a scriptFile, which 1.12.2 does not read, ignoring it", key); }
            if (script) { ContentLog.LOGGER.error("Recipe swap file {} has a script, which 1.12.2 does not read, ignoring it", key); }
            if (!template && !edit && !scriptFile && !script) { ContentLog.LOGGER.error("Recipe swap file {} has no swaps array, ignoring it", key); }
            return;
        }
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        for (JsonElement element : Json.getJsonArray(json, "swaps")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A swap in {} is not an object, skipping it", key);
                continue;
            }
            SwapEntry entry = read(key, element.getAsJsonObject());
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
        if (json.has("types")) { ContentLog.LOGGER.error("A swap in {} has types, which 1.12.2 does not read, ignoring it", key); }
        if (json.has("paths")) { ContentLog.LOGGER.error("A swap in {} has paths, which 1.12.2 does not read, ignoring it", key); }
        String target = Json.getString(json, "to", "");
        if (target.startsWith("#")) {
            ContentLog.LOGGER.error("A swap in {} swaps to the tag {}, which 1.12.2 does not do, skipping it", key, target.substring(1));
            return null;
        }
        ItemStack to = ContentStacks.parse(key, target, 1);
        if (to.isEmpty()) {
            ContentLog.LOGGER.error("A swap in {} needs a valid to item, skipping it", key);
            return null;
        }
        if (to.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            ContentLog.LOGGER.error("The to item of a swap in {} must not be a wildcard, skipping it", key);
            return null;
        }
        if (json.has("recipes") && !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("The recipes of a swap in {} is not an object, skipping it", key);
            return null;
        }
        boolean scoped = json.has("recipes");
        SwapEntry entry = new SwapEntry(to, Json.getBoolean(json, "output", false), scoped);
        entry.from.read(json.getAsJsonObject("from"), key);
        if (entry.from.unnamed()) {
            ContentLog.LOGGER.error("The from of a swap in {} names nothing, skipping it", key);
            return null;
        }
        entry.from.resolve();
        if (!entry.from.any()) { return null; }
        if (entry.from.matches(to)) {
            ContentLog.LOGGER.error("The to item {} of a swap in {} is matched by its own from, skipping it", to, key);
            return null;
        }
        if (scoped) { entry.readScope(key, json.getAsJsonObject("recipes")); }
        return entry;
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
