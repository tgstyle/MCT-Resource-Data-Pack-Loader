package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
    private static final String ITEM = "item";
    private static final String TAG = "tag";
    private static final String TYPE = "type";
    private static final String GROUP = "group";
    private final Set<Identifier> from;
    private final Identifier to;
    private final boolean output;
    private final boolean tagged;
    private final Map<Identifier, List<Identifier>> tags;
    private Outcome outcome = new Outcome();
    private boolean forced;

    RecipeSwapJson(Set<Identifier> from, Identifier to, boolean output, boolean tagged, Map<Identifier, List<Identifier>> tags) {
        this.from = from;
        this.to = to;
        this.output = output;
        this.tagged = tagged;
        this.tags = tags;
    }

    static final class Outcome {
        final List<String> missing = new ArrayList<>();
        int slots;
        int results;
        boolean typed;
        boolean taggedResult;
        boolean taggedSlot;
        @Nullable Identifier made;

        boolean changed() { return slots > 0 || results > 0; }
    }

    static boolean resultKey(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return lower.contains(RESULT) || lower.contains("output");
    }

    Outcome rewrite(JsonObject recipe) {
        outcome = new Outcome();
        if (recipe.has(KEY) && recipe.get(KEY).isJsonObject()) {
            JsonObject key = recipe.getAsJsonObject(KEY);
            for (String symbol : new ArrayList<>(key.keySet())) {
                JsonElement next = slot(key.get(symbol));
                if (next != null) { key.add(symbol, next); }
            }
        }
        if (recipe.has(INGREDIENTS) && recipe.get(INGREDIENTS).isJsonArray()) {
            JsonArray array = recipe.getAsJsonArray(INGREDIENTS);
            for (int i = 0; i < array.size(); i++) {
                JsonElement next = slot(array.get(i));
                if (next != null) { array.set(i, next); }
            }
        }
        if (recipe.has(INGREDIENT)) {
            JsonElement next = slot(recipe.get(INGREDIENT));
            if (next != null) { recipe.add(INGREDIENT, next); }
        }
        if (output) { result(recipe); }
        return outcome;
    }

    Outcome rewriteForeign(JsonObject recipe, List<String> paths) {
        outcome = new Outcome();
        if (paths.isEmpty()) {
            for (Map.Entry<String, JsonElement> member : new ArrayList<>(recipe.entrySet())) {
                if (TYPE.equals(member.getKey()) || GROUP.equals(member.getKey())) { continue; }
                JsonElement next = deep(member.getValue(), resultKey(member.getKey()), true);
                if (next != member.getValue()) { recipe.add(member.getKey(), next); }
            }
            return outcome;
        }
        for (String path : paths) {
            JsonElement parent = JsonTree.parent(recipe, path);
            JsonElement held = JsonTree.get(recipe, path);
            if (parent == null || held == null) {
                outcome.missing.add(path);
                continue;
            }
            forced = resultPath(path);
            JsonElement next = held.isJsonPrimitive() ? primitive(held.getAsJsonPrimitive()) : deep(held, forced, true);
            forced = false;
            if (next != held) { JsonTree.put(parent, JsonTree.last(path), next); }
        }
        return outcome;
    }

    static Set<Identifier> inputs(JsonObject recipe, Map<Identifier, List<Identifier>> tags) {
        Set<Identifier> ids = new LinkedHashSet<>();
        if (recipe.has(INGREDIENT) && unresolved(recipe.get(INGREDIENT), ids, tags)) { ids.clear(); }
        return ids;
    }

    private static boolean resultPath(String path) {
        for (String part : path.split("\\.", -1)) {
            if (resultKey(part)) { return true; }
        }
        return false;
    }

    private JsonElement primitive(JsonPrimitive value) {
        if (!value.isString()) { return value; }
        if (!forced) {
            JsonElement next = slot(value);
            return next == null ? value : next;
        }
        return resultText(value);
    }

    private JsonElement resultText(JsonPrimitive value) {
        Identifier id = Identifier.tryParse(value.getAsString());
        if (id == null || !from.contains(id)) { return value; }
        if (tagged) {
            outcome.taggedResult = true;
            return value;
        }
        outcome.made = id;
        outcome.results++;
        return new JsonPrimitive(to.toString());
    }

    private JsonElement deep(JsonElement element, boolean result, boolean bare) {
        if (element.isJsonArray()) { return deepArray(element.getAsJsonArray(), result, bare); }
        if (element.isJsonPrimitive()) { return bare && element.getAsJsonPrimitive().isString() ? text(element.getAsJsonPrimitive(), result) : element; }
        if (!element.isJsonObject()) { return element; }
        JsonObject object = element.getAsJsonObject();
        boolean typedObject = object.has(TYPE);
        if (!typedObject) {
            String key = itemKey(object);
            if (key != null) { return item(object, key, result); }
            Identifier tag = string(object, TAG);
            if (tag != null) { return tagSlot(object, tag); }
        }
        else if (names(object)) { outcome.typed = true; }
        JsonObject next = null;
        for (Map.Entry<String, JsonElement> member : object.entrySet()) {
            if (TYPE.equals(member.getKey()) || GROUP.equals(member.getKey())) { continue; }
            JsonElement value = deep(member.getValue(), result || resultKey(member.getKey()), !typedObject);
            if (value != member.getValue()) {
                if (next == null) { next = object.deepCopy(); }
                next.add(member.getKey(), value);
            }
        }
        return next == null ? object : next;
    }

    private JsonElement text(JsonPrimitive value, boolean result) {
        if (result) {
            if (!output && !forced) { return value; }
            return resultText(value);
        }
        JsonElement next = slot(value);
        return next == null ? value : next;
    }

    private JsonElement deepArray(JsonArray array, boolean result, boolean bare) {
        JsonArray next = new JsonArray();
        boolean changed = false;
        for (JsonElement each : array) {
            JsonElement value = deep(each, result, bare);
            if (value == each) { next.add(each); }
            else {
                changed = true;
                if (!tagged || !next.contains(value)) { next.add(value); }
            }
        }
        return changed ? next : array;
    }

    private JsonElement item(JsonObject slot, String key, boolean result) {
        Identifier id = string(slot, key);
        if (id == null || !from.contains(id) || result && !output && !forced) { return slot; }
        if (result && tagged) {
            outcome.taggedResult = true;
            return slot;
        }
        if (tagged && ID.equals(key)) {
            outcome.typed = true;
            return slot;
        }
        JsonObject copy = slot.deepCopy();
        if (result) {
            outcome.made = id;
            outcome.results++;
        }
        else { outcome.slots++; }
        if (tagged) {
            copy.remove(key);
            copy.addProperty(TAG, to.toString());
        }
        else { copy.addProperty(key, to.toString()); }
        return copy;
    }

    private JsonElement tagSlot(JsonObject slot, Identifier tag) {
        if (tagged) {
            outcome.taggedSlot = true;
            return slot;
        }
        if (!tagHits(tag)) { return slot; }
        outcome.typed = true;
        return slot;
    }

    private boolean tagHits(Identifier tag) {
        List<Identifier> held = tags.get(tag);
        if (held == null) { return false; }
        for (Identifier id : held) {
            if (from.contains(id)) { return true; }
        }
        return false;
    }

    @Nullable private static String itemKey(JsonObject object) {
        if (string(object, ITEM) != null) { return ITEM; }
        return string(object, ID) == null ? null : ID;
    }

    @Nullable private static Identifier string(JsonObject object, String member) {
        JsonElement value = object.get(member);
        return isText(value) ? Identifier.tryParse(value.getAsString()) : null;
    }

    @Nullable private JsonElement slot(JsonElement element) {
        if (typed(element)) {
            if (names(element)) { outcome.typed = true; }
            return null;
        }
        if (tagged && writtenAsTag(element)) {
            outcome.taggedSlot = true;
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
        if (kept.isEmpty()) {
            outcome.slots++;
            return new JsonPrimitive(tagged ? "#" + to : to.toString());
        }
        if (tagged) {
            outcome.typed = true;
            return null;
        }
        outcome.slots++;
        kept.add(to);
        JsonArray array = new JsonArray();
        for (Identifier each : kept) { array.add(each.toString()); }
        return array;
    }

    private static boolean writtenAsTag(JsonElement element) {
        if (isText(element)) { return element.getAsString().startsWith("#"); }
        if (!element.isJsonArray()) { return false; }
        for (JsonElement each : element.getAsJsonArray()) {
            if (writtenAsTag(each)) { return true; }
        }
        return false;
    }

    private void result(JsonObject recipe) {
        if (!recipe.has(RESULT)) { return; }
        JsonElement held = recipe.get(RESULT);
        if (isText(held)) {
            Identifier id = Identifier.tryParse(held.getAsString());
            if (id == null || !from.contains(id)) { return; }
            if (tagged) {
                outcome.taggedResult = true;
                return;
            }
            recipe.addProperty(RESULT, to.toString());
            outcome.made = id;
            outcome.results++;
        }
        else if (held.isJsonObject() && isText(held.getAsJsonObject().get(ID))) {
            JsonObject object = held.getAsJsonObject();
            Identifier id = Identifier.tryParse(object.get(ID).getAsString());
            if (id == null || !from.contains(id)) { return; }
            if (tagged) {
                outcome.taggedResult = true;
                return;
            }
            object.addProperty(ID, to.toString());
            object.remove("components");
            outcome.made = id;
            outcome.results++;
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

    private boolean names(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (names(child)) { return true; }
            }
            return false;
        }
        if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (names(child)) { return true; }
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
