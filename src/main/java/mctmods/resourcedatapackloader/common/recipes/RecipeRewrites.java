package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public final class RecipeRewrites {
    private static final String TEMPLATE = "template";
    private static final String EDIT = "edit";
    private static final String RECIPES = "recipes";
    private static final String CONDITIONS = "neoforge:conditions";
    private static final Set<String> OPS = Set.of("set", "multiply", "replace");
    private static final Set<String> ROUNDS = Set.of("nearest", "down", "up");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z0-9_]+(\\.[A-Za-z0-9_]+)*)}");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final List<Rewrite> REWRITES = new ArrayList<>();
    private static final Set<String> REPORTED = new HashSet<>();
    private static int edited;
    private static int templated;

    private RecipeRewrites() {}

    public static void reload() {
        REPORTED.clear();
        edited = 0;
        templated = 0;
        if (!GENERATION.stale()) { return; }
        REWRITES.clear();
        if (!Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe rewrite file", RecipeRewrites::read);
    }

    public static void scriptedNote() {
        if (REWRITES.isEmpty()) { return; }
        ContentLog.LOGGER.info("Recipe rewrites ran on the recipe files only, recipes made by a script are not rewritten");
    }

    public static void apply(Map<ResourceLocation, JsonElement> recipes, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse, boolean beforeSwaps) {
        List<Rewrite> stage = new ArrayList<>();
        for (Rewrite file : REWRITES) {
            if (file.beforeSwaps() == beforeSwaps) { stage.add(file); }
        }
        if (!stage.isEmpty()) {
            for (Map.Entry<ResourceLocation, JsonElement> each : recipes.entrySet()) {
                ResourceLocation id = each.getKey();
                if (id.getPath().startsWith("_") || !each.getValue().isJsonObject()) { continue; }
                JsonObject current = each.getValue().getAsJsonObject();
                for (Rewrite file : stage) {
                    if (!file.matches(id, RecipeSwaps.type(current))) { continue; }
                    JsonObject next = rewrite(file, id, current, parse);
                    if (next != null) { current = next; }
                }
                each.setValue(current);
            }
        }
        if (!beforeSwaps && (edited > 0 || templated > 0)) { Summary.info("recipes.rewritten", "Edited " + edited + " recipe(s) and rewrote " + templated + " recipe(s) from a template"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        ScriptRef script = script(key, json);
        if (!json.has(TEMPLATE) && !json.has(EDIT) && script == null) { return; }
        RecipeTypeFilter types = new RecipeTypeFilter();
        types.read(key, json);
        RecipeSelector scope = new RecipeSelector();
        if (json.has(RECIPES) && json.get(RECIPES).isJsonObject()) {
            JsonObject recipes = json.getAsJsonObject(RECIPES);
            for (String member : new String[] {"outputs", "inputs", "types"}) {
                if (recipes.has(member)) {
                    ContentLog.LOGGER.error("Recipe rewrite file {} has {} in its recipes, which a rewrite does not read, ignoring it", key, member);
                    return;
                }
            }
            scope.read(key, recipes);
            if (scope.invalid() > 0) {
                ContentLog.LOGGER.error("Recipe rewrite file {} has a regex that is not valid, ignoring it", key);
                return;
            }
            if (scope.isEmpty() && types.isEmpty()) {
                ContentLog.LOGGER.error("Recipe rewrite file {} selects no recipes, ignoring it", key);
                return;
            }
        }
        else if (types.isEmpty()) {
            ContentLog.LOGGER.error("Recipe rewrite file {} needs a recipes object or a types list saying which recipes it covers, ignoring it", key);
            return;
        }
        JsonObject template = null;
        if (json.has(TEMPLATE)) {
            if (json.get(TEMPLATE).isJsonObject()) { template = json.getAsJsonObject(TEMPLATE); }
            else { ContentLog.LOGGER.error("The template in {} is not an object, ignoring it", key); }
        }
        List<Edit> edits = new ArrayList<>();
        if (json.has(EDIT)) {
            for (JsonElement element : Json.getJsonArray(json, EDIT)) {
                Edit edit = edit(key, element);
                if (edit != null) { edits.add(edit); }
            }
        }
        if (template == null && edits.isEmpty() && script == null) { return; }
        REWRITES.add(new Rewrite(key, scope, types, Json.getBoolean(json, "before_swaps", false), edits, template, script));
    }

    @Nullable private static ScriptRef script(ResourceLocation key, JsonObject json) {
        boolean inline = !Json.getString(json, "script", "").trim().isEmpty();
        boolean file = !Json.getString(json, "scriptFile", "").trim().isEmpty();
        if (!inline && !file) { return null; }
        if (inline && file) {
            ContentLog.LOGGER.error("Recipe rewrite file {} has more than one of script and scriptFile, ignoring both", key);
            return null;
        }
        ScriptEngine.prepare();
        return ScriptRef.read(key, json);
    }

    @Nullable private static Edit edit(ResourceLocation key, JsonElement element) {
        JsonObject json = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        String op = Json.getString(json, "op", "");
        String path = Json.getString(json, "path", "");
        if (!OPS.contains(op) || path.isEmpty()) {
            ContentLog.LOGGER.error("An edit in {} needs an op of set, multiply or replace and a path, skipping it", key);
            return null;
        }
        if (JsonTree.badPath(path)) {
            ContentLog.LOGGER.error("The path '{}' in {} has an empty part, skipping it", path, key);
            return null;
        }
        if (!"multiply".equals(op)) {
            if (json.has("value")) { return new Edit(op, path, json.get("value"), 0, "nearest"); }
            ContentLog.LOGGER.error("An edit in {} needs a value, skipping it", key);
            return null;
        }
        JsonElement by = json.get("by");
        if (by == null || !by.isJsonPrimitive() || !by.getAsJsonPrimitive().isNumber()) {
            ContentLog.LOGGER.error("An edit in {} needs a number to multiply by, skipping it", key);
            return null;
        }
        String round = Json.getString(json, "round", "nearest");
        if (!ROUNDS.contains(round)) {
            ContentLog.LOGGER.error("The round '{}' of an edit in {} is not nearest, down or up, using nearest", round, key);
            round = "nearest";
        }
        return new Edit(op, path, JsonNull.INSTANCE, by.getAsDouble(), round);
    }

    @Nullable private static JsonObject rewrite(Rewrite file, ResourceLocation id, JsonObject current, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        JsonObject working = current.deepCopy();
        if (blocked(file, id, working)) { return null; }
        JsonObject made = working;
        if (file.template() != null) {
            made = new Filler(working, id, file.key()).run(file.template());
            if (made == null) { return null; }
            if (working.has(CONDITIONS) && !made.has(CONDITIONS)) { made.add(CONDITIONS, working.get(CONDITIONS).deepCopy()); }
        }
        if (RecipeSwaps.parsed(id, made, parse, "rewrite") == null) {
            if (RecipeSwaps.parsed(id, current, parse, "rewrite") != null) { ContentLog.LOGGER.error("Left {} alone, its rewritten form from {} does not parse", id, file.key()); }
            return null;
        }
        if (file.script() != null) { made = scripted(file, id, made, parse); }
        if (!file.edits().isEmpty()) { edited++; }
        if (file.template() != null) { templated++; }
        return made;
    }

    private static JsonObject scripted(Rewrite file, ResourceLocation id, JsonObject before, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        ScriptEngine.Called called = ScriptEngine.callWith(file.script(), before.toString());
        if (called.failure() != null) { return kept(file, id, before, "failed", "failed: " + called.failure()); }
        if (called.text() == null) { return kept(file, id, before, "null", "returned null"); }
        JsonElement result;
        try { result = JsonParser.parseString(called.text()); }
        catch (JsonParseException e) { return kept(file, id, before, "text", "returned text that is not JSON"); }
        if (!result.isJsonObject()) { return kept(file, id, before, "shape", "returned JSON that is not an object"); }
        if (RecipeSwaps.parsed(id, result.getAsJsonObject(), parse, "rewrite") == null) { return kept(file, id, before, "recipe", "returned a recipe that does not parse"); }
        return result.getAsJsonObject();
    }

    private static JsonObject kept(Rewrite file, ResourceLocation id, JsonObject before, String kind, String why) {
        if (REPORTED.add(file.key() + "/" + kind)) { ContentLog.LOGGER.error("The script of recipe rewrite file {} {} (first seen on {}), keeping each such recipe as it was before the script", file.key(), why, id); }
        return before;
    }

    private static boolean blocked(Rewrite file, ResourceLocation id, JsonObject recipe) {
        for (Edit edit : file.edits()) {
            JsonElement held = JsonTree.get(recipe, edit.path());
            JsonElement parent = JsonTree.parent(recipe, edit.path());
            if (parent == null || held == null && !"set".equals(edit.op())) {
                ContentLog.LOGGER.info("Left {} alone, it has no {} for the edit in {}", id, edit.path(), file.key());
                return true;
            }
            JsonElement value = edit.value().deepCopy();
            if ("multiply".equals(edit.op())) {
                if (!held.isJsonPrimitive() || !held.getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Left {} alone, the {} that the edit in {} multiplies is not a number", id, edit.path(), file.key());
                    return true;
                }
                value = multiplied(held.getAsJsonPrimitive(), edit.by(), edit.round());
            }
            JsonTree.put(parent, JsonTree.last(edit.path()), value);
        }
        return false;
    }

    static JsonPrimitive multiplied(JsonPrimitive value, double by, String round) {
        double was = value.getAsDouble();
        double product = was * by;
        String text = value.getAsString();
        if (text.indexOf('.') >= 0 || text.indexOf('e') >= 0 || text.indexOf('E') >= 0) { return new JsonPrimitive(product); }
        long rounded = switch (round) {
            case "down" -> (long) Math.floor(product);
            case "up" -> (long) Math.ceil(product);
            default -> Math.round(product);
        };
        if (was > 0 && by > 0 && rounded == 0) { rounded = 1; }
        return new JsonPrimitive(rounded);
    }

    private static final class Filler {
        private final JsonObject recipe;
        private final ResourceLocation id;
        private final ResourceLocation key;
        private boolean failed;

        private Filler(JsonObject recipe, ResourceLocation id, ResourceLocation key) {
            this.recipe = recipe;
            this.id = id;
            this.key = key;
        }

        @Nullable private JsonObject run(JsonObject template) {
            JsonObject filled = JsonTree.map(template, (name, value) -> fill(value));
            return failed ? null : filled;
        }

        private JsonElement fill(JsonElement template) {
            if (failed) { return template; }
            if (template.isJsonObject()) { return JsonTree.map(template.getAsJsonObject(), (name, value) -> fill(value)); }
            if (template.isJsonArray()) { return JsonTree.map(template.getAsJsonArray(), this::fill); }
            if (!template.isJsonPrimitive() || !template.getAsJsonPrimitive().isString()) { return template.deepCopy(); }
            return text(template.getAsString());
        }

        private JsonElement text(String text) {
            Matcher matcher = PLACEHOLDER.matcher(text);
            if (matcher.matches()) {
                JsonElement found = found(matcher.group(1));
                return found == null ? new JsonPrimitive(text) : found.deepCopy();
            }
            StringBuilder out = new StringBuilder();
            while (matcher.find()) {
                JsonElement found = found(matcher.group(1));
                if (found == null) { break; }
                if (!found.isJsonPrimitive()) {
                    ContentLog.LOGGER.error("Left {} alone, its {} is not a single value the template in {} can put in text", id, matcher.group(1), key);
                    failed = true;
                    break;
                }
                matcher.appendReplacement(out, Matcher.quoteReplacement(found.getAsString()));
            }
            matcher.appendTail(out);
            return new JsonPrimitive(out.toString());
        }

        @Nullable private JsonElement found(String path) {
            JsonElement found = JsonTree.get(recipe, path);
            if (found != null && !found.isJsonNull()) { return found; }
            ContentLog.LOGGER.info("Left {} alone, it has no {} for the template in {}", id, path, key);
            failed = true;
            return null;
        }
    }

    private record Rewrite(ResourceLocation key, RecipeSelector scope, RecipeTypeFilter types, boolean beforeSwaps, List<Edit> edits, @Nullable JsonObject template, @Nullable ScriptRef script) {
        boolean matches(ResourceLocation id, String type) { return (scope.isEmpty() || scope.matchesName(id)) && (types.isEmpty() || types.matches(type)); }
    }

    private record Edit(String op, String path, JsonElement value, double by, String round) {}
}
