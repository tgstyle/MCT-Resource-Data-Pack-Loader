package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

final class CrossLoot {
    private static final String FUNCTION = "function";
    private static final String CONDITION = "condition";
    private static final String PREDICATE = "predicate";
    private static final String PREDICATES = "predicates";
    private static final String TAG = "tag";
    private static final String ITEMS = "items";
    private static final String ENCHANTMENT = "enchantment";
    private static final String ENCHANTMENTS = "enchantments";
    private static final String OPTIONS = "options";
    private static final String LOOTING = "minecraft:looting";
    private static final Set<String> ITEM_KEYS = Set.of("item", "items", "mainhand", "offhand", "head", "chest", "legs", "feet", "body", "rod");
    private static final Map<String, String> OPERATIONS = Map.of("addition", "add_value", "multiply_base", "add_multiplied_base", "multiply_total", "add_multiplied_total");
    private static final Map<String, String> PREDICATE_KEYS = Map.of("nbt", "minecraft:custom_data", ENCHANTMENTS, "minecraft:enchantments", "stored_enchantments", "minecraft:stored_enchantments", "potion", "minecraft:potion_contents");
    private static final Pattern ID_CHARACTERS = Pattern.compile("[^a-z0-9_./-]");

    private CrossLoot() {}

    static void loot(JsonObject json, String file, Crossed pack) {
        node(json, "", file, pack);
        CrossJson.replace(json, CrossJson.walk(json, "", null, file, pack).getAsJsonObject());
    }

    private static void node(JsonElement element, String key, String file, Crossed pack) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(inner -> node(inner, key, file, pack));
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        boolean up = pack.to() == Port.Line.V1_21;
        if (ITEM_KEYS.contains(key)) { item(held, up, file, pack); }
        else if ("block".equals(key)) { block(held, up, "blocks", "blocks"); }
        else if ("fluid".equals(key)) { block(held, up, "fluid", "fluids"); }
        location(held, up);
        String function = string(held, FUNCTION);
        if (!function.isEmpty()) { function(held, function, up, file, pack); }
        String condition = string(held, CONDITION);
        if (!condition.isEmpty()) { condition(held, condition, up, file, pack); }
        if ("minecraft:loot_table".equals(string(held, CrossJson.TYPE))) { rename(held, up ? "name" : "value", up ? "value" : "name"); }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) { node(entry.getValue(), entry.getKey(), file, pack); }
    }

    private static String string(JsonObject held, String key) { return held.has(key) && held.get(key).isJsonPrimitive() ? held.get(key).getAsString() : ""; }

    private static void rename(JsonObject held, String from, String to) {
        if (held.has(from) && !held.has(to)) { held.add(to, held.remove(from)); }
    }

    private static void item(JsonObject held, boolean up, String file, Crossed pack) {
        if (up) {
            JsonObject predicates = held.has(PREDICATES) && held.get(PREDICATES).isJsonObject() ? held.getAsJsonObject(PREDICATES) : new JsonObject();
            if (held.has(TAG)) {
                String tag = held.remove(TAG).getAsString();
                if (!held.has(ITEMS)) { held.addProperty(ITEMS, "#" + tag); }
                else { pack.note("'" + file + "' tests both items and the tag " + tag + ", and 1.21.1 takes one of them, so the tag is left out"); }
            }
            for (Map.Entry<String, String> moved : PREDICATE_KEYS.entrySet()) {
                if (held.has(moved.getKey())) { predicates.add(moved.getValue(), held.remove(moved.getKey())); }
            }
            enchantmentKeys(predicates, ENCHANTMENT, ENCHANTMENTS);
            if (held.has("durability")) {
                JsonObject damage = new JsonObject();
                damage.add("durability", held.remove("durability"));
                predicates.add("minecraft:damage", damage);
            }
            if (!predicates.keySet().isEmpty()) { held.add(PREDICATES, predicates); }
            return;
        }
        if (held.has(ITEMS) && held.get(ITEMS).isJsonPrimitive()) {
            String items = held.remove(ITEMS).getAsString();
            if (items.startsWith("#")) { held.addProperty(TAG, items.substring(1)); }
            else {
                JsonArray list = new JsonArray();
                list.add(items);
                held.add(ITEMS, list);
            }
        }
        if (held.has(CrossJson.COMPONENTS) && held.get(CrossJson.COMPONENTS).isJsonObject()) {
            String nbt = CrossJson.downNbt(held.remove(CrossJson.COMPONENTS).getAsJsonObject(), file, pack);
            if (nbt != null) { held.addProperty(CrossJson.NBT, nbt); }
        }
        if (!held.has(PREDICATES) || !held.get(PREDICATES).isJsonObject()) { return; }
        JsonObject predicates = held.remove(PREDICATES).getAsJsonObject();
        enchantmentKeys(predicates, ENCHANTMENTS, ENCHANTMENT);
        for (Map.Entry<String, String> moved : PREDICATE_KEYS.entrySet()) {
            if (predicates.has(moved.getValue())) { held.add(moved.getKey(), predicates.remove(moved.getValue())); }
        }
        if (predicates.has("minecraft:damage") && predicates.get("minecraft:damage").isJsonObject() && predicates.getAsJsonObject("minecraft:damage").has("durability")) { held.add("durability", predicates.remove("minecraft:damage").getAsJsonObject().get("durability")); }
        if (!predicates.keySet().isEmpty()) { pack.note("'" + file + "' tests the item predicate(s) " + String.join(", ", predicates.keySet()) + ", which 1.20.1 does not have, so they are left out"); }
    }

    private static void enchantmentKeys(JsonObject predicates, String from, String to) {
        for (String list : new String[] {"minecraft:enchantments", "minecraft:stored_enchantments"}) {
            if (!predicates.has(list) || !predicates.get(list).isJsonArray()) { continue; }
            for (JsonElement one : predicates.getAsJsonArray(list)) {
                if (one.isJsonObject()) { rename(one.getAsJsonObject(), from, to); }
            }
        }
    }

    private static void block(JsonObject held, boolean up, String single, String holders) {
        if (up) {
            if (!"blocks".equals(single)) { rename(held, single, holders); }
            if (held.has(TAG) && !held.has(holders)) { held.addProperty(holders, "#" + held.remove(TAG).getAsString()); }
            return;
        }
        if (!held.has(holders) || !held.get(holders).isJsonPrimitive()) { return; }
        String named = held.remove(holders).getAsString();
        if (named.startsWith("#")) { held.addProperty(TAG, named.substring(1)); }
        else if ("blocks".equals(single)) {
            JsonArray list = new JsonArray();
            list.add(named);
            held.add(holders, list);
        }
        else { held.addProperty(single, named); }
    }

    private static void location(JsonObject held, boolean up) {
        if (up) {
            if (held.has("biome") && held.get("biome").isJsonPrimitive()) { rename(held, "biome", "biomes"); }
            if (held.has("structure") && held.get("structure").isJsonPrimitive()) { rename(held, "structure", "structures"); }
            return;
        }
        if (held.has("biomes") && held.get("biomes").isJsonPrimitive() && !held.get("biomes").getAsString().startsWith("#")) { rename(held, "biomes", "biome"); }
        if (held.has("structures") && held.get("structures").isJsonPrimitive() && !held.get("structures").getAsString().startsWith("#")) { rename(held, "structures", "structure"); }
    }

    private static void function(JsonObject held, String function, boolean up, String file, Crossed pack) {
        switch (function) {
            case "minecraft:set_nbt" -> {
                if (!up) { return; }
                held.addProperty(FUNCTION, "minecraft:set_components");
                held.add(CrossJson.COMPONENTS, CrossStacks.upJson("minecraft:stick", held.remove(TAG)));
            }
            case "minecraft:set_components" -> {
                if (up || !held.has(CrossJson.COMPONENTS)) { return; }
                String nbt = CrossJson.downNbt(held.remove(CrossJson.COMPONENTS).getAsJsonObject(), file, pack);
                held.addProperty(FUNCTION, "minecraft:set_nbt");
                held.addProperty(TAG, nbt == null ? "{}" : nbt);
            }
            case "minecraft:set_custom_data" -> { if (!up) { held.addProperty(FUNCTION, "minecraft:set_nbt"); } }
            case "minecraft:copy_nbt" -> { if (up) { held.addProperty(FUNCTION, "minecraft:copy_custom_data"); } }
            case "minecraft:copy_custom_data" -> { if (!up) { held.addProperty(FUNCTION, "minecraft:copy_nbt"); } }
            case "minecraft:enchant_randomly" -> enchantRandomly(held, up, file, pack);
            case "minecraft:enchant_with_levels" -> {
                String gone = up ? "treasure" : OPTIONS;
                if (held.has(gone)) {
                    held.remove(gone);
                    pack.note("'" + file + "' sets '" + gone + "' on enchant_with_levels, which " + pack.to().title() + " does not read, so it is left out");
                }
            }
            case "minecraft:looting_enchant" -> {
                if (!up) { return; }
                held.addProperty(FUNCTION, "minecraft:enchanted_count_increase");
                held.addProperty(ENCHANTMENT, LOOTING);
            }
            case "minecraft:enchanted_count_increase" -> {
                if (up) { return; }
                if (!LOOTING.equals(string(held, ENCHANTMENT))) { pack.note("'" + file + "' raises the count by " + string(held, ENCHANTMENT) + ", and 1.20.1 only raises it by looting"); }
                held.remove(ENCHANTMENT);
                held.addProperty(FUNCTION, "minecraft:looting_enchant");
            }
            case "minecraft:set_attributes" -> attributes(held, up, file);
            case "minecraft:set_contents" -> pack.note("'" + file + "' fills a container with set_contents, whose form differs on " + pack.to().title() + "; it is kept as written, so check it by hand");
            default -> { }
        }
    }

    private static void enchantRandomly(JsonObject held, boolean up, String file, Crossed pack) {
        if (up) {
            rename(held, ENCHANTMENTS, OPTIONS);
            return;
        }
        if (held.has("only_compatible")) { held.remove("only_compatible"); }
        if (!held.has(OPTIONS)) { return; }
        JsonElement options = held.remove(OPTIONS);
        if (options.isJsonPrimitive() && options.getAsString().startsWith("#")) {
            pack.note("'" + file + "' picks enchantments from the tag " + options.getAsString() + ", and 1.20.1 takes a list, so any enchantment may be picked");
            return;
        }
        JsonArray list = options.isJsonArray() ? options.getAsJsonArray() : new JsonArray();
        if (options.isJsonPrimitive()) { list.add(options); }
        held.add(ENCHANTMENTS, list);
    }

    private static void attributes(JsonObject held, boolean up, String file) {
        if (!held.has("modifiers") || !held.get("modifiers").isJsonArray()) { return; }
        String namespace = file.split("/").length > 1 ? file.split("/")[1] : "minecraft";
        for (JsonElement one : held.getAsJsonArray("modifiers")) {
            if (!one.isJsonObject()) { continue; }
            JsonObject modifier = one.getAsJsonObject();
            String operation = string(modifier, "operation");
            if (up) {
                if (modifier.has("name")) {
                    modifier.remove("id");
                    modifier.addProperty("id", namespace + ":" + ID_CHARACTERS.matcher(modifier.remove("name").getAsString().toLowerCase(Locale.ROOT)).replaceAll("_"));
                }
                modifier.addProperty("operation", OPERATIONS.getOrDefault(operation, operation));
                continue;
            }
            if (modifier.has("id") && !modifier.has("name")) {
                String id = modifier.remove("id").getAsString();
                modifier.addProperty("name", id.substring(id.indexOf(':') + 1));
            }
            for (Map.Entry<String, String> named : OPERATIONS.entrySet()) {
                if (named.getValue().equals(operation)) { modifier.addProperty("operation", named.getKey()); }
            }
        }
    }

    private static void condition(JsonObject held, String condition, boolean up, String file, Crossed pack) {
        if ("minecraft:match_tool".equals(condition) && held.has(PREDICATE) && held.get(PREDICATE).isJsonObject()) { item(held.getAsJsonObject(PREDICATE), up, file, pack); }
        if (up && "minecraft:random_chance_with_looting".equals(condition)) {
            float chance = held.has("chance") ? held.remove("chance").getAsFloat() : 0.0F;
            float multiplier = held.has("looting_multiplier") ? held.remove("looting_multiplier").getAsFloat() : 0.0F;
            JsonObject enchanted = new JsonObject();
            enchanted.addProperty("type", "minecraft:linear");
            enchanted.addProperty("base", chance + multiplier);
            enchanted.addProperty("per_level_above_first", multiplier);
            held.addProperty(CONDITION, "minecraft:random_chance_with_enchanted_bonus");
            held.addProperty(ENCHANTMENT, LOOTING);
            held.addProperty("unenchanted_chance", chance);
            held.add("enchanted_chance", enchanted);
        }
        if (!up && "minecraft:random_chance_with_enchanted_bonus".equals(condition)) {
            JsonElement enchanted = held.get("enchanted_chance");
            if (!LOOTING.equals(string(held, ENCHANTMENT)) || enchanted == null || !enchanted.isJsonObject() || !enchanted.getAsJsonObject().has("per_level_above_first")) {
                pack.note("'" + file + "' uses random_chance_with_enchanted_bonus in a form 1.20.1 cannot express; it is kept as written, so replace it by hand");
                return;
            }
            float chance = held.has("unenchanted_chance") ? held.remove("unenchanted_chance").getAsFloat() : 0.0F;
            held.remove(ENCHANTMENT);
            held.remove("enchanted_chance");
            held.addProperty(CONDITION, "minecraft:random_chance_with_looting");
            held.addProperty("chance", chance);
            held.addProperty("looting_multiplier", enchanted.getAsJsonObject().get("per_level_above_first").getAsFloat());
        }
    }
}
