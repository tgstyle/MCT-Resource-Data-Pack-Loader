package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

final class RecipeSwapJson {
    private static final String ITEM = "item";
    private static final String TAG = "tag";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String INGREDIENTS = "ingredients";
    private static final String INGREDIENT = "ingredient";
    private static final String RESULT = "result";
    private static final String COUNT = "count";
    private final Set<ResourceLocation> from;
    private final ResourceLocation to;
    private final boolean output;
    private final boolean tagged;
    private final Map<ResourceLocation, Set<ResourceLocation>> tags;
    private final List<String> missing = new ArrayList<>();
    private int ingredients;
    private int results;
    private boolean left;
    private boolean taggedResult;
    private boolean taggedSlot;
    private boolean forced;
    @Nullable private ResourceLocation replaced;

    RecipeSwapJson(Set<ResourceLocation> from, ResourceLocation to, boolean output, boolean tagged, Map<ResourceLocation, Set<ResourceLocation>> tags) {
        this.from = from;
        this.to = to;
        this.output = output;
        this.tagged = tagged;
        this.tags = tags;
    }

    int ingredients() { return ingredients; }

    int results() { return results; }

    boolean left() { return left; }

    boolean taggedResult() { return taggedResult; }

    boolean taggedSlot() { return taggedSlot; }

    List<String> missing() { return missing; }

    static boolean resultKey(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return lower.contains(RESULT) || lower.contains("output");
    }

    private void reset() {
        ingredients = 0;
        results = 0;
        left = false;
        taggedResult = false;
        taggedSlot = false;
        replaced = null;
        missing.clear();
    }

    @Nullable ResourceLocation replaced() { return replaced; }

    static Set<ResourceLocation> inputs(JsonObject recipe, Map<ResourceLocation, Set<ResourceLocation>> tags) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        JsonElement single = recipe.get(INGREDIENT);
        if (single != null) { readable(single, tags, ids); }
        return ids;
    }

    @Nullable JsonObject walk(JsonObject recipe) {
        reset();
        Map<String, JsonElement> keys = new LinkedHashMap<>();
        JsonElement keyed = recipe.get(KEY);
        if (keyed != null && keyed.isJsonObject()) {
            for (Map.Entry<String, JsonElement> slot : keyed.getAsJsonObject().entrySet()) {
                JsonElement swapped = slot(slot.getValue());
                if (swapped != slot.getValue()) { keys.put(slot.getKey(), swapped); }
            }
        }
        JsonArray list = listed(recipe.get(INGREDIENTS));
        JsonElement single = recipe.get(INGREDIENT);
        JsonElement swappedSingle = single == null ? null : slot(single);
        JsonElement made = output ? result(recipe.get(RESULT)) : null;
        if (keys.isEmpty() && list == null && swappedSingle == single && made == null) { return null; }
        JsonObject copy = recipe.deepCopy();
        if (!keys.isEmpty()) { keys.forEach(copy.getAsJsonObject(KEY)::add); }
        if (list != null) { copy.add(INGREDIENTS, list); }
        if (swappedSingle != single) { copy.add(INGREDIENT, swappedSingle); }
        if (made != null) { copy.add(RESULT, made); }
        return copy;
    }

    @Nullable JsonObject walkForeign(JsonObject recipe, List<String> paths) {
        reset();
        JsonObject copy = null;
        if (paths.isEmpty()) {
            for (Map.Entry<String, JsonElement> member : recipe.entrySet()) {
                JsonElement next = deep(member.getValue(), resultKey(member.getKey()));
                if (next == member.getValue()) { continue; }
                if (copy == null) { copy = recipe.deepCopy(); }
                copy.add(member.getKey(), next);
            }
            return copy;
        }
        for (String path : paths) {
            JsonObject source = copy == null ? recipe : copy;
            JsonElement held = JsonTree.get(source, path);
            if (JsonTree.parent(source, path) == null || held == null) {
                missing.add(path);
                continue;
            }
            forced = resultPath(path);
            JsonElement next = held.isJsonPrimitive() ? primitive(held.getAsJsonPrimitive(), forced) : deep(held, forced);
            forced = false;
            if (next == held) { continue; }
            if (copy == null) { copy = recipe.deepCopy(); }
            JsonElement target = JsonTree.parent(copy, path);
            if (target != null) { JsonTree.put(target, JsonTree.last(path), next.deepCopy()); }
        }
        return copy;
    }

    private static boolean resultPath(String path) {
        for (String part : path.split("\\.", -1)) {
            if (resultKey(part)) { return true; }
        }
        return false;
    }

    private JsonElement primitive(JsonPrimitive value, boolean result) {
        ResourceLocation id = value.isString() ? ResourceLocation.tryParse(value.getAsString()) : null;
        if (id == null || !from.contains(id)) { return value; }
        if (tagged) {
            if (result) { taggedResult = true; }
            else { left = true; }
            return value;
        }
        if (result) {
            replaced = id;
            results++;
        }
        else { ingredients++; }
        return new JsonPrimitive(to.toString());
    }

    private JsonElement deep(JsonElement element, boolean result) {
        if (element.isJsonArray()) { return deepArray(element.getAsJsonArray(), result); }
        if (!element.isJsonObject()) { return element; }
        JsonObject object = element.getAsJsonObject();
        if (!object.has(TYPE)) {
            ResourceLocation item = itemId(object);
            if (item != null) { return item(object, item, result); }
            ResourceLocation tag = string(object, TAG);
            if (tag != null) { return tagSlot(object, tag, result); }
        }
        else if (mentions(object)) { left = true; }
        JsonObject next = null;
        for (Map.Entry<String, JsonElement> member : object.entrySet()) {
            JsonElement value = deep(member.getValue(), result || resultKey(member.getKey()));
            if (value != member.getValue()) {
                if (next == null) { next = object.deepCopy(); }
                next.add(member.getKey(), value);
            }
        }
        return next == null ? object : next;
    }

    private JsonElement deepArray(JsonArray array, boolean result) {
        JsonArray next = new JsonArray();
        boolean changed = false;
        for (JsonElement each : array) {
            JsonElement value = deep(each, result);
            if (value == each) { next.add(each); }
            else {
                changed = true;
                if (!tagged || !next.contains(value)) { next.add(value); }
            }
        }
        return changed ? next : array;
    }

    private JsonElement item(JsonObject slot, ResourceLocation id, boolean result) {
        if (!from.contains(id) || result && !output && !forced) { return slot; }
        if (result && tagged) {
            taggedResult = true;
            return slot;
        }
        JsonObject copy = slot.deepCopy();
        if (result) {
            replaced = id;
            results++;
        }
        else { ingredients++; }
        if (tagged) { copy.remove(ITEM); }
        copy.addProperty(tagged ? TAG : ITEM, to.toString());
        return copy;
    }

    private JsonElement tagSlot(JsonObject slot, ResourceLocation tag, boolean result) {
        if (tagged || !tagHits(tag)) { return slot; }
        if (result || slot.size() != 1) {
            left = true;
            return slot;
        }
        return slot(slot);
    }

    private boolean mentions(JsonObject object) {
        ResourceLocation id = itemId(object);
        ResourceLocation tag = string(object, TAG);
        return id != null && from.contains(id) || tag != null && tagHits(tag);
    }

    @Nullable private JsonArray listed(@Nullable JsonElement element) {
        if (element == null || !element.isJsonArray()) { return null; }
        JsonArray next = new JsonArray();
        boolean changed = false;
        for (JsonElement each : element.getAsJsonArray()) {
            JsonElement swapped = slot(each);
            changed |= swapped != each;
            next.add(swapped);
        }
        return changed ? next : null;
    }

    private JsonElement slot(JsonElement slot) {
        if (tagged && writtenAsTag(slot)) { taggedSlot = true; return slot; }
        Set<ResourceLocation> members = new LinkedHashSet<>();
        if (!readable(slot, tags, members)) {
            if (names(slot)) { left = true; }
            return slot;
        }
        Set<ResourceLocation> others = new LinkedHashSet<>();
        boolean hit = false;
        for (ResourceLocation member : members) {
            if (from.contains(member)) { hit = true; }
            else { others.add(member); }
        }
        if (!hit) { return slot; }
        ingredients++;
        if (others.isEmpty()) { return ingredientEntry(); }
        JsonArray array = new JsonArray();
        for (ResourceLocation id : others) { array.add(entry(id)); }
        if (tagged || !others.contains(to)) { array.add(ingredientEntry()); }
        return array;
    }

    private boolean writtenAsTag(JsonElement slot) {
        if (slot.isJsonArray()) {
            for (JsonElement each : slot.getAsJsonArray()) {
                if (writtenAsTag(each)) { return true; }
            }
            return false;
        }
        return slot.isJsonObject() && slot.getAsJsonObject().has(TAG);
    }

    private JsonObject ingredientEntry() {
        JsonObject entry = new JsonObject();
        entry.addProperty(tagged ? TAG : ITEM, to.toString());
        return entry;
    }

    @Nullable private JsonElement result(@Nullable JsonElement element) {
        if (element == null) { return null; }
        if (element.isJsonPrimitive()) {
            ResourceLocation id = element.getAsJsonPrimitive().isString() ? ResourceLocation.tryParse(element.getAsString()) : null;
            if (id == null || !from.contains(id)) { return null; }
            if (tagged) {
                taggedResult = true;
                return null;
            }
            replaced = id;
            results++;
            return new JsonPrimitive(to.toString());
        }
        if (!element.isJsonObject()) { return null; }
        JsonObject stack = element.getAsJsonObject();
        ResourceLocation id = itemId(stack);
        if (id == null || !from.contains(id)) { return null; }
        if (tagged) {
            taggedResult = true;
            return null;
        }
        JsonObject made = entry(to);
        if (stack.has(COUNT)) { made.add(COUNT, stack.get(COUNT)); }
        replaced = id;
        results++;
        return made;
    }

    private boolean names(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (names(child)) { return true; }
            }
            return false;
        }
        if (!element.isJsonObject()) { return false; }
        for (Map.Entry<String, JsonElement> member : element.getAsJsonObject().entrySet()) {
            JsonElement value = member.getValue();
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                ResourceLocation id = ResourceLocation.tryParse(value.getAsString());
                if (ITEM.equals(member.getKey()) && id != null && from.contains(id)) { return true; }
                if (TAG.equals(member.getKey()) && id != null && tagHits(id)) { return true; }
            }
            else if (names(value)) { return true; }
        }
        return false;
    }

    private boolean tagHits(ResourceLocation tag) {
        Set<ResourceLocation> held = tags.get(tag);
        if (held == null) { return false; }
        for (ResourceLocation id : held) {
            if (from.contains(id)) { return true; }
        }
        return false;
    }

    private static boolean readable(JsonElement element, Map<ResourceLocation, Set<ResourceLocation>> tags, Set<ResourceLocation> into) {
        if (element.isJsonArray()) {
            boolean readable = true;
            for (JsonElement each : element.getAsJsonArray()) { readable &= readable(each, tags, into); }
            return readable;
        }
        if (!element.isJsonObject() || element.getAsJsonObject().has(TYPE)) { return false; }
        JsonObject object = element.getAsJsonObject();
        ResourceLocation id = itemId(object);
        if (id != null) {
            into.add(id);
            return true;
        }
        ResourceLocation tag = string(object, TAG);
        if (tag == null) { return false; }
        Set<ResourceLocation> held = tags.get(tag);
        if (held == null) {
            ContentLog.LOGGER.debug("The item tag {} is not built, so a recipe slot written as that tag is left alone", tag);
            return false;
        }
        into.addAll(held);
        return true;
    }

    @Nullable private static ResourceLocation itemId(JsonObject object) { return string(object, ITEM); }

    @Nullable private static ResourceLocation string(JsonObject object, String member) {
        JsonElement value = object.get(member);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? ResourceLocation.tryParse(value.getAsString()) : null;
    }

    private static JsonObject entry(ResourceLocation id) {
        JsonObject entry = new JsonObject();
        entry.addProperty(ITEM, id.toString());
        return entry;
    }
}
