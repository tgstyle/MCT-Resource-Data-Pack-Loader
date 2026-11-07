package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

final class RecipeSwapJson {
    private static final String KEY = "key";
    private static final String INGREDIENTS = "ingredients";
    private static final String INGREDIENT = "ingredient";
    private static final String RESULT = "result";
    private static final String ID = "id";

    private RecipeSwapJson() {}

    static final class Outcome {
        int slots;
        boolean typed;
        @Nullable Identifier made;

        boolean changed() { return slots > 0 || made != null; }
    }

    static Outcome rewrite(JsonObject recipe, Set<Identifier> from, Identifier to, boolean output, Map<Identifier, List<Identifier>> tags) {
        Outcome outcome = new Outcome();
        if (recipe.has(KEY) && recipe.get(KEY).isJsonObject()) {
            JsonObject key = recipe.getAsJsonObject(KEY);
            for (String symbol : new ArrayList<>(key.keySet())) {
                JsonElement next = slot(key.get(symbol), from, to, tags, outcome);
                if (next != null) { key.add(symbol, next); }
            }
        }
        if (recipe.has(INGREDIENTS) && recipe.get(INGREDIENTS).isJsonArray()) {
            JsonArray array = recipe.getAsJsonArray(INGREDIENTS);
            for (int i = 0; i < array.size(); i++) {
                JsonElement next = slot(array.get(i), from, to, tags, outcome);
                if (next != null) { array.set(i, next); }
            }
        }
        if (recipe.has(INGREDIENT)) {
            JsonElement next = slot(recipe.get(INGREDIENT), from, to, tags, outcome);
            if (next != null) { recipe.add(INGREDIENT, next); }
        }
        if (output) { result(recipe, from, to, outcome); }
        return outcome;
    }

    static Set<Identifier> inputs(JsonObject recipe, Map<Identifier, List<Identifier>> tags) {
        Set<Identifier> ids = new LinkedHashSet<>();
        if (recipe.has(INGREDIENT) && unresolved(recipe.get(INGREDIENT), ids, tags)) { ids.clear(); }
        return ids;
    }

    @Nullable private static JsonElement slot(JsonElement element, Set<Identifier> from, Identifier to, Map<Identifier, List<Identifier>> tags, Outcome outcome) {
        if (typed(element)) {
            if (names(element, from, tags)) { outcome.typed = true; }
            return null;
        }
        Set<Identifier> all = new LinkedHashSet<>();
        if (unresolved(element, all, tags)) { return null; }
        Set<Identifier> kept = new LinkedHashSet<>();
        boolean hit = false;
        for (Identifier member : all) {
            if (from.contains(member)) { hit = true; }
            else { kept.add(member); }
        }
        if (!hit) { return null; }
        outcome.slots++;
        if (kept.isEmpty()) { return new JsonPrimitive(to.toString()); }
        kept.add(to);
        JsonArray array = new JsonArray();
        for (Identifier each : kept) { array.add(each.toString()); }
        return array;
    }

    private static void result(JsonObject recipe, Set<Identifier> from, Identifier to, Outcome outcome) {
        if (!recipe.has(RESULT)) { return; }
        JsonElement held = recipe.get(RESULT);
        if (isText(held)) {
            Identifier id = Identifier.tryParse(held.getAsString());
            if (id == null || !from.contains(id)) { return; }
            recipe.addProperty(RESULT, to.toString());
            outcome.made = id;
        }
        else if (held.isJsonObject() && isText(held.getAsJsonObject().get(ID))) {
            JsonObject object = held.getAsJsonObject();
            Identifier id = Identifier.tryParse(object.get(ID).getAsString());
            if (id == null || !from.contains(id)) { return; }
            object.addProperty(ID, to.toString());
            object.remove("components");
            outcome.made = id;
        }
    }

    private static boolean typed(JsonElement element) {
        if (element.isJsonObject()) { return true; }
        if (!element.isJsonArray()) { return false; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (!child.isJsonPrimitive()) { return true; }
        }
        return false;
    }

    private static boolean unresolved(JsonElement element, Set<Identifier> into, Map<Identifier, List<Identifier>> tags) {
        if (isText(element)) { return unresolved(element.getAsString(), into, tags); }
        if (!element.isJsonArray()) { return true; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (!isText(child) || unresolved(child.getAsString(), into, tags)) { return true; }
        }
        return false;
    }

    private static boolean unresolved(String text, Set<Identifier> into, Map<Identifier, List<Identifier>> tags) {
        if (text.startsWith("#")) {
            Identifier tag = Identifier.tryParse(text.substring(1));
            List<Identifier> held = tag == null ? null : tags.get(tag);
            if (held == null) {
                ContentLog.LOGGER.debug("Recipe swap found the ingredient tag {} missing from the built item tags, leaving its slot alone", text);
                return true;
            }
            into.addAll(held);
            return false;
        }
        Identifier id = Identifier.tryParse(text);
        if (id == null) { return true; }
        into.add(id);
        return false;
    }

    private static boolean names(JsonElement element, Set<Identifier> from, Map<Identifier, List<Identifier>> tags) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (names(child, from, tags)) { return true; }
            }
            return false;
        }
        if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (names(child, from, tags)) { return true; }
            }
            return false;
        }
        if (!isText(element)) { return false; }
        Set<Identifier> found = new LinkedHashSet<>();
        if (unresolved(element.getAsString(), found, tags)) { return false; }
        for (Identifier id : found) {
            if (from.contains(id)) { return true; }
        }
        return false;
    }

    private static boolean isText(@Nullable JsonElement element) { return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString(); }
}
