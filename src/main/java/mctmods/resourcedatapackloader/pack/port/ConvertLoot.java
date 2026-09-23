package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ConvertLoot {
    private static final Set<String> LOOT_FUNCTIONS = Convert.set("set_count", "set_data", "set_damage", "set_nbt", "enchant_randomly", "enchant_with_levels", "looting_enchant", "furnace_smelt", "set_attributes", "exploration_map");
    private static final Set<String> LOOT_CONDITIONS = Convert.set("random_chance", "random_chance_with_looting", "entity_properties", "entity_scores", "killed_by_player");
    private static final Map<String, String> OPERATIONS = new HashMap<>();

    static {
        OPERATIONS.put("add_value", "addition");
        OPERATIONS.put("add_multiplied_base", "multiply_base");
        OPERATIONS.put("add_multiplied_total", "multiply_total");
    }

    private ConvertLoot() {}

    static String loot(JsonObject json, String from, Ported pack) {
        if (json.has("target") && json.get("target").isJsonPrimitive()) { json.addProperty("target", Ids.lootTable(json.get("target").getAsString())); }
        json.remove("type");
        json.remove("random_sequence");
        if (json.has("functions")) { json.add("functions", lootFunctions(json.get("functions").getAsJsonArray(), from, pack)); }
        if (!json.has("pools") || !json.get("pools").isJsonArray()) { return Ported.GSON.toJson(json); }
        int index = 0;
        for (JsonElement element : json.getAsJsonArray("pools")) {
            if (!element.isJsonObject()) { continue; }
            JsonObject pool = element.getAsJsonObject();
            if (!pool.has("name")) { pool.addProperty("name", "pool" + index); }
            index++;
            numberAt(pool, "rolls", from, pack);
            numberAt(pool, "bonus_rolls", from, pack);
            if (pool.has("conditions")) { pool.add("conditions", lootConditions(pool.getAsJsonArray("conditions"), from, pack)); }
            if (pool.has("functions")) {
                pack.note("'" + from + "' gives a pool functions, which 1.12.2 reads only on entries, so they are left out");
                pool.remove("functions");
            }
            JsonArray entries = new JsonArray();
            if (pool.has("entries")) { flattenEntries(pool.getAsJsonArray("entries"), entries, from, pack); }
            Set<String> names = new HashSet<>();
            for (JsonElement entry : entries) {
                JsonObject held = entry.getAsJsonObject();
                String named = Convert.text(held, "name");
                if (!named.isEmpty() && !names.add(named) && !held.has("entryName")) { held.addProperty("entryName", named + "#" + names.size()); }
            }
            pool.add("entries", entries);
        }
        return Ported.GSON.toJson(json);
    }

    private static void flattenEntries(JsonArray entries, JsonArray out, String from, Ported pack) {
        for (JsonElement element : entries) {
            if (!element.isJsonObject()) { continue; }
            JsonObject entry = element.getAsJsonObject();
            String type = Convert.text(entry, "type");
            String bare = type.startsWith(Ids.MINECRAFT + ":") ? type.substring(Ids.MINECRAFT.length() + 1) : type;
            if ("alternatives".equals(bare) || "group".equals(bare) || "sequence".equals(bare)) {
                pack.note("'" + from + "' has a " + bare + " entry, which 1.12.2 does not have, so its children are listed in the pool on their own");
                if (entry.has("children")) { flattenEntries(entry.getAsJsonArray("children"), out, from, pack); }
                continue;
            }
            if (!"item".equals(bare) && !"loot_table".equals(bare) && !"empty".equals(bare)) {
                pack.note("'" + from + "' has a " + type + " entry, which has no twin on 1.12.2, so it is left out");
                continue;
            }
            entry.addProperty("type", bare);
            if (entry.has("value") && !entry.has("name")) { entry.add("name", entry.remove("value")); }
            JsonArray functions = entry.has("functions") ? lootFunctions(entry.getAsJsonArray("functions"), from, pack) : new JsonArray();
            if ("loot_table".equals(bare) && entry.has("name")) { entry.addProperty("name", Ids.lootTable(entry.get("name").getAsString())); }
            if ("item".equals(bare) && entry.has("name")) {
                Convert.Ref ref = Convert.item(entry.get("name").getAsString(), pack, "'" + from + "'");
                if (ref != null) {
                    entry.addProperty("name", ref.id);
                    if (ref.meta > 0) {
                        JsonObject data = new JsonObject();
                        data.addProperty("function", "set_data");
                        data.addProperty("data", ref.meta);
                        functions.add(data);
                    }
                }
            }
            if (functions.size() > 0) { entry.add("functions", functions); }
            else { entry.remove("functions"); }
            if (entry.has("conditions")) {
                JsonArray conditions = lootConditions(entry.getAsJsonArray("conditions"), from, pack);
                if (conditions.size() > 0) { entry.add("conditions", conditions); }
                else { entry.remove("conditions"); }
            }
            out.add(entry);
        }
    }

    private static JsonArray lootFunctions(JsonArray functions, String from, Ported pack) {
        JsonArray out = new JsonArray();
        for (JsonElement element : functions) {
            if (!element.isJsonObject()) { continue; }
            JsonObject function = element.getAsJsonObject();
            String name = Convert.text(function, "function");
            String bare = name.startsWith(Ids.MINECRAFT + ":") ? name.substring(Ids.MINECRAFT.length() + 1) : name;
            if ("enchanted_count_increase".equals(bare)) { bare = "looting_enchant"; }
            if (!LOOT_FUNCTIONS.contains(bare)) {
                pack.note("'" + from + "' uses the loot function " + name + ", which has no twin on 1.12.2, so it is left out");
                continue;
            }
            function.addProperty("function", bare);
            numberAt(function, "count", from, pack);
            numberAt(function, "damage", from, pack);
            numberAt(function, "levels", from, pack);
            if ("set_nbt".equals(bare) && function.has("tag") && function.get("tag").isJsonObject()) { function.addProperty("tag", function.get("tag").toString()); }
            if ("set_attributes".equals(bare) && function.has("modifiers") && function.get("modifiers").isJsonArray()) {
                for (JsonElement modifier : function.getAsJsonArray("modifiers")) {
                    if (!modifier.isJsonObject()) { continue; }
                    JsonObject held = modifier.getAsJsonObject();
                    if (held.has("attribute")) { held.addProperty("attribute", Ids.attribute(held.get("attribute").getAsString())); }
                    String operation = Convert.text(held, "operation");
                    if (OPERATIONS.containsKey(operation)) { held.addProperty("operation", OPERATIONS.get(operation)); }
                    if (held.has("id") && !held.has("name")) { held.add("name", held.remove("id")); }
                    numberAt(held, "amount", from, pack);
                }
            }
            if (function.has("conditions")) { function.add("conditions", lootConditions(function.getAsJsonArray("conditions"), from, pack)); }
            out.add(function);
        }
        return out;
    }

    private static JsonArray lootConditions(JsonArray conditions, String from, Ported pack) {
        JsonArray out = new JsonArray();
        for (JsonElement element : conditions) {
            if (!element.isJsonObject()) { continue; }
            JsonObject condition = element.getAsJsonObject();
            String name = Convert.text(condition, "condition");
            String bare = name.startsWith(Ids.MINECRAFT + ":") ? name.substring(Ids.MINECRAFT.length() + 1) : name;
            if ("inverted".equals(bare) && condition.has("term") && condition.get("term").isJsonObject() && Convert.text(condition.getAsJsonObject("term"), "condition").endsWith("killed_by_player")) {
                JsonObject inverse = new JsonObject();
                inverse.addProperty("condition", "killed_by_player");
                inverse.addProperty("inverse", true);
                out.add(inverse);
                continue;
            }
            if (!LOOT_CONDITIONS.contains(bare)) {
                pack.note("'" + from + "' uses the loot condition " + name + ", which has no twin on 1.12.2, so it is left out and what it guarded is no longer guarded");
                continue;
            }
            condition.addProperty("condition", bare);
            if ("entity_properties".equals(bare) && condition.has("predicate") && condition.get("predicate").isJsonObject()) {
                JsonObject predicate = condition.remove("predicate").getAsJsonObject();
                JsonObject properties = new JsonObject();
                JsonObject flags = predicate.has("flags") && predicate.get("flags").isJsonObject() ? predicate.getAsJsonObject("flags") : new JsonObject();
                if (flags.has("is_on_fire")) { properties.add("on_fire", flags.get("is_on_fire")); }
                if (predicate.size() > (flags.size() > 0 ? 1 : 0) || flags.size() > (flags.has("is_on_fire") ? 1 : 0)) { pack.note("'" + from + "' asks an entity_properties condition for more than being on fire, which is all 1.12.2 can ask, so the rest is left out"); }
                condition.add("properties", properties);
            }
            out.add(condition);
        }
        return out;
    }

    private static void numberAt(JsonObject holder, String key, String from, Ported pack) {
        if (!holder.has(key) || !holder.get(key).isJsonObject()) { return; }
        JsonObject provider = holder.getAsJsonObject(key);
        String type = Convert.text(provider, "type");
        String bare = type.startsWith(Ids.MINECRAFT + ":") ? type.substring(Ids.MINECRAFT.length() + 1) : type;
        if ("constant".equals(bare) && provider.has("value")) {
            holder.add(key, provider.get("value"));
            return;
        }
        if ("binomial".equals(bare)) {
            JsonObject range = new JsonObject();
            range.addProperty("min", 0);
            range.add("max", provider.has("n") && provider.get("n").isJsonPrimitive() ? provider.get("n") : new JsonPrimitive(1));
            holder.add(key, range);
            pack.note("'" + from + "' rolls a binomial " + key + ", and 1.12.2 rolls only uniform ranges, so it becomes " + range);
            return;
        }
        if (!bare.isEmpty() && !"uniform".equals(bare)) {
            pack.note("'" + from + "' gives " + key + " as a " + type + " number, which has no twin on 1.12.2");
            return;
        }
        provider.remove("type");
    }
}
