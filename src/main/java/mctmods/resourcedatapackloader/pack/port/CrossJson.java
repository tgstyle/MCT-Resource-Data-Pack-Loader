package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class CrossJson {
    static final String ITEM = "item";
    static final String ID = "id";
    static final String NBT = "nbt";
    static final String COMPONENTS = "components";
    static final String TYPE = "type";
    private static final String RESULT = "result";
    private static final String COUNT = "count";
    private static final String CONDITIONS = "conditions";
    private static final String NEO_CONDITIONS = "neoforge:conditions";
    private static final String NEO_COMPONENTS = "neoforge:components";
    private static final Set<String> TAG_KEYS = Set.of("tag", "tags");
    private static final Set<String> STRING_RESULTS = Set.of("minecraft:smelting", "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking", "minecraft:stonecutting");
    private static final String STONECUTTING = "minecraft:stonecutting";

    private CrossJson() {}

    static String tag(String id, @Nullable String registry, String file, Crossed pack) {
        String converted = CrossIds.tag(registry, id, pack.to());
        if (!converted.contains(CrossIds.BOTH)) { return converted; }
        String kept = CrossIds.COMMON + id.substring(id.indexOf(':'));
        pack.note("'" + file + "' names the tag " + id + ", which is " + converted.replace(CrossIds.BOTH, " and ") + " together on " + pack.to().title() + "; it is read as " + kept + ", so name the tags by hand");
        return kept;
    }

    static String id(String id, String file, Crossed pack) {
        String converted = CrossIds.id(id, pack.to());
        if (CrossIds.missing(converted, pack.to())) { pack.note("'" + file + "' names " + converted + ", which " + pack.to().title() + " does not have; it is kept as written, so replace it by hand"); }
        return converted;
    }

    static JsonElement walk(JsonElement element, String key, @Nullable String registry, String file, Crossed pack) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement inner : element.getAsJsonArray()) { out.add(walk(inner, key, registry, file, pack)); }
            return out;
        }
        if (element.isJsonObject()) {
            JsonObject out = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                String named = entry.getKey().indexOf(':') > 0 && !entry.getKey().startsWith("#") ? id(entry.getKey(), file, pack) : entry.getKey();
                out.add(named, walk(entry.getValue(), entry.getKey(), registry, file, pack));
            }
            return out;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { return element; }
        String text = element.getAsString();
        if (text.startsWith("#") && text.indexOf(':') > 1) { return new JsonPrimitive("#" + tag(text.substring(1), registry, file, pack)); }
        if (text.indexOf(':') <= 0 || text.indexOf(' ') >= 0 || text.indexOf('{') >= 0) { return element; }
        if (TAG_KEYS.contains(key)) { return new JsonPrimitive(tag(text, registry, file, pack)); }
        return new JsonPrimitive(id(text, file, pack));
    }

    static void replace(JsonObject held, JsonObject walked) {
        for (String key : new ArrayList<>(held.keySet())) { held.remove(key); }
        walked.entrySet().forEach(entry -> held.add(entry.getKey(), entry.getValue()));
    }

    static void definition(JsonObject json, @Nullable String registry, String file, Crossed pack) {
        if (pack.to() == Port.Line.V1_20) { structures(json, file, pack); }
        replace(json, walk(json, "", registry, file, pack).getAsJsonObject());
    }

    private static void structures(JsonObject json, String file, Crossed pack) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement value = entry.getValue();
            if ("structures".equals(entry.getKey()) && value.isJsonObject()) {
                for (String named : new ArrayList<>(value.getAsJsonObject().keySet())) {
                    if (!CrossIds.missingStructure(named, pack.to())) { continue; }
                    value.getAsJsonObject().remove(named);
                    pack.note("'" + file + "' names the structure '" + named + "', which " + pack.to().title() + " does not have, so it is left out");
                }
            }
            else if (entry.getKey().startsWith("structure") && value.isJsonArray()) {
                JsonArray kept = new JsonArray();
                for (JsonElement one : value.getAsJsonArray()) {
                    String named = one.isJsonPrimitive() ? one.getAsString().split("[=,\\s]", 2)[0].trim() : "";
                    if (CrossIds.missingStructure(named, pack.to())) { pack.note("'" + file + "' names the structure '" + named + "' in " + entry.getKey() + ", which " + pack.to().title() + " does not have, so that entry is left out"); }
                    else { kept.add(one); }
                }
                entry.setValue(kept);
            }
            else if (value.isJsonObject()) { structures(value.getAsJsonObject(), file, pack); }
        }
    }

    static void recipe(JsonObject json, String file, Crossed pack) {
        boolean up = pack.to() == Port.Line.V1_21;
        rename(json, up ? CONDITIONS : NEO_CONDITIONS, up ? NEO_CONDITIONS : CONDITIONS);
        String type = json.has(TYPE) && json.get(TYPE).isJsonPrimitive() ? json.get(TYPE).getAsString() : "";
        if (json.has(RESULT)) { result(json, type, file, pack); }
        ingredients(json, file, pack);
        replace(json, walk(json, "", CrossIds.ITEM_REGISTRY, file, pack).getAsJsonObject());
    }

    private static void rename(JsonObject json, String from, String to) {
        if (json.has(from) && !json.has(to)) { json.add(to, json.remove(from)); }
    }

    private static void result(JsonObject json, String type, String file, Crossed pack) {
        JsonElement result = json.get(RESULT);
        if (pack.to() == Port.Line.V1_21) {
            JsonObject stack = result.isJsonPrimitive() ? new JsonObject() : result.getAsJsonObject();
            if (result.isJsonPrimitive()) { stack.add(ID, result); }
            if (STONECUTTING.equals(type) && json.has(COUNT)) { stack.add(COUNT, json.remove(COUNT)); }
            stackUp(stack);
            json.add(RESULT, stack);
            return;
        }
        if (!result.isJsonObject()) { return; }
        JsonObject stack = result.getAsJsonObject();
        stackDown(stack, file, pack);
        if (!STRING_RESULTS.contains(type) || stack.has(NBT)) { return; }
        if (stack.has(COUNT) && STONECUTTING.equals(type)) { json.add(COUNT, stack.get(COUNT)); }
        json.add(RESULT, stack.get(ITEM));
    }

    static void stackUp(JsonObject stack) {
        if (stack.has(ITEM) && !stack.has(ID)) { stack.add(ID, stack.remove(ITEM)); }
        if (!stack.has(NBT)) { return; }
        JsonElement nbt = stack.remove(NBT);
        String named = stack.has(ID) ? stack.get(ID).getAsString() : "minecraft:stone";
        JsonObject components = CrossStacks.upJson(named, nbt);
        if (!components.keySet().isEmpty()) { stack.add(COMPONENTS, components); }
    }

    static void stackDown(JsonObject stack, String file, Crossed pack) {
        if (stack.has(ID) && !stack.has(ITEM)) { stack.add(ITEM, stack.remove(ID)); }
        if (!stack.has(COMPONENTS) || !stack.get(COMPONENTS).isJsonObject()) { return; }
        String nbt = downNbt(stack.remove(COMPONENTS).getAsJsonObject(), file, pack);
        if (nbt != null) { stack.addProperty(NBT, nbt); }
    }

    @Nullable static String downNbt(JsonObject components, String file, Crossed pack) {
        List<String> lost = new ArrayList<>();
        String nbt = CrossStacks.down(CrossStacks.compound(components), lost).toString();
        if (!lost.isEmpty()) { pack.note("'" + file + "' sets the item component(s) " + String.join(", ", lost) + ", which have no " + pack.to().title() + " NBT form and are left out"); }
        return "{}".equals(nbt) ? null : nbt;
    }

    private static void ingredients(JsonElement element, String file, Crossed pack) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(inner -> ingredients(inner, file, pack));
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        String type = held.has(TYPE) && held.get(TYPE).isJsonPrimitive() ? held.get(TYPE).getAsString() : "";
        if (pack.to() == Port.Line.V1_21 && ("forge:nbt".equals(type) || "forge:partial_nbt".equals(type))) {
            String item = held.has(ITEM) ? held.remove(ITEM).getAsString() : "minecraft:stone";
            held.addProperty(TYPE, NEO_COMPONENTS);
            held.addProperty("items", item);
            held.add(COMPONENTS, CrossStacks.upJson(item, held.has(NBT) ? held.remove(NBT) : null));
            if ("forge:partial_nbt".equals(type)) { held.addProperty("strict", false); }
            return;
        }
        if (pack.to() == Port.Line.V1_20 && NEO_COMPONENTS.equals(type)) {
            boolean strict = !held.has("strict") || held.remove("strict").getAsBoolean();
            held.addProperty(TYPE, strict ? "forge:nbt" : "forge:partial_nbt");
            JsonElement items = held.remove("items");
            if (items != null && items.isJsonPrimitive()) { held.add(ITEM, items); }
            else { pack.note("'" + file + "' tests components on a list or tag of items, which a 1.20.1 NBT ingredient cannot, so fix its item by hand"); }
            String nbt = held.has(COMPONENTS) && held.get(COMPONENTS).isJsonObject() ? downNbt(held.remove(COMPONENTS).getAsJsonObject(), file, pack) : null;
            held.addProperty(NBT, nbt == null ? "{}" : nbt);
            return;
        }
        held.entrySet().forEach(entry -> ingredients(entry.getValue(), file, pack));
    }

    static void advancement(JsonObject json, String file, Crossed pack) {
        JsonObject display = json.has("display") && json.get("display").isJsonObject() ? json.getAsJsonObject("display") : null;
        if (display != null && display.has("icon") && display.get("icon").isJsonObject()) {
            if (pack.to() == Port.Line.V1_21) { stackUp(display.getAsJsonObject("icon")); }
            else { stackDown(display.getAsJsonObject("icon"), file, pack); }
        }
        CrossLoot.loot(json, file, pack);
    }
}
