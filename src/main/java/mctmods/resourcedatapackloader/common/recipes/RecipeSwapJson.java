package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private final Map<ResourceLocation, Set<ResourceLocation>> tags;
    private int ingredients;
    private int results;
    private boolean left;
    @Nullable private ResourceLocation replaced;

    RecipeSwapJson(Set<ResourceLocation> from, ResourceLocation to, boolean output, Map<ResourceLocation, Set<ResourceLocation>> tags) {
        this.from = from;
        this.to = to;
        this.output = output;
        this.tags = tags;
    }

    int ingredients() { return ingredients; }

    int results() { return results; }

    boolean left() { return left; }

    @Nullable ResourceLocation replaced() { return replaced; }

    static Set<ResourceLocation> inputs(JsonObject recipe, Map<ResourceLocation, Set<ResourceLocation>> tags) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        JsonElement single = recipe.get(INGREDIENT);
        if (single != null) { readable(single, tags, ids); }
        return ids;
    }

    @Nullable JsonObject walk(JsonObject recipe) {
        ingredients = 0;
        results = 0;
        left = false;
        replaced = null;
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
        if (others.isEmpty()) { return entry(to); }
        others.add(to);
        JsonArray array = new JsonArray();
        for (ResourceLocation id : others) { array.add(entry(id)); }
        return array;
    }

    @Nullable private JsonElement result(@Nullable JsonElement element) {
        if (element == null) { return null; }
        if (element.isJsonPrimitive()) {
            ResourceLocation id = element.getAsJsonPrimitive().isString() ? ResourceLocation.tryParse(element.getAsString()) : null;
            if (id == null || !from.contains(id)) { return null; }
            replaced = id;
            results++;
            return new JsonPrimitive(to.toString());
        }
        if (!element.isJsonObject()) { return null; }
        JsonObject stack = element.getAsJsonObject();
        ResourceLocation id = itemId(stack);
        if (id == null || !from.contains(id)) { return null; }
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
