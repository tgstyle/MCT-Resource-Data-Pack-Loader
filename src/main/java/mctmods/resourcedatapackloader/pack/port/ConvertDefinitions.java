package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ConvertDefinitions {
    private static final Set<String> MODERN_STRUCTURES = Convert.set("ancient_cities", "buried_treasures", "ocean_ruins", "pillager_outposts", "ruined_portals", "shipwrecks", "trail_ruins", "trial_chambers", "nether_fossils");
    private static final Map<String, String[]> PROFESSIONS = new HashMap<>();

    static {
        String[][] professions = {{"farmer", "farmer", "farmer"}, {"fisherman", "farmer", "fisherman"}, {"shepherd", "farmer", "shepherd"}, {"fletcher", "farmer", "fletcher"},
                {"librarian", "librarian", "librarian"}, {"cartographer", "librarian", "cartographer"}, {"cleric", "priest", "cleric"}, {"armorer", "smith", "armor"},
                {"weaponsmith", "smith", "weapon"}, {"toolsmith", "smith", "tool"}, {"butcher", "butcher", "butcher"}, {"leatherworker", "butcher", "leather"}, {"nitwit", "nitwit", "nitwit"}};
        for (String[] profession : professions) { PROFESSIONS.put("minecraft:" + profession[0], new String[] {"minecraft:" + profession[1], profession[2]}); }
    }

    private ConvertDefinitions() {}

    static void template(JsonObject json, Ported pack, String where) {
        if (json.has("structures") && json.get("structures").isJsonObject()) {
            JsonObject structures = json.getAsJsonObject("structures");
            List<String> removed = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : new ArrayList<>(structures.entrySet())) {
                if (!MODERN_STRUCTURES.contains(entry.getKey())) { continue; }
                structures.remove(entry.getKey());
                removed.add(entry.getKey());
            }
            if (!removed.isEmpty()) { pack.note(where + " sets the structures " + String.join(", ", removed) + ", which 1.12.2 never generated, so they are left out"); }
        }
        if (!json.has("settings") || !json.get("settings").isJsonObject()) { return; }
        JsonObject settings = json.getAsJsonObject("settings");
        int shift = Convert.flatShift(settings);
        movedPoint(settings, "worldSpawn", shift, pack, where);
        movedPoint(settings, "resetSendsTo", shift, pack, where);
        if (shift != 0 && settings.has("worldMinHeight")) { settings.remove("worldMinHeight"); }
        if (shift != 0 && settings.has("generatorOptions") && settings.get("generatorOptions").isJsonPrimitive()) { settings.addProperty("generatorOptions", superflat(settings.get("generatorOptions").getAsString(), pack, where)); }
    }

    private static String superflat(String text, Ported pack, String where) {
        String[] parts = text.split(";", -1);
        if (parts.length < 2) { return text; }
        String[] layers = parts[1].split(",", -1);
        for (int i = 0; i < layers.length; i++) {
            int star = layers[i].indexOf('*');
            Convert.Ref ref = Convert.block(layers[i].substring(star + 1), Collections.emptyMap(), pack, where);
            if (ref != null) { layers[i] = layers[i].substring(0, star + 1) + ref.text(); }
        }
        parts[1] = String.join(",", layers);
        return String.join(";", parts);
    }

    static void team(JsonObject json, Ported pack, String where) {
        int shift = pack.overworldShift();
        movedPoint(json, "spawn", shift, pack, where);
        if (json.has("standIn") && json.get("standIn").isJsonObject()) { movedPoint(json.getAsJsonObject("standIn"), "at", shift, pack, where); }
        if (shift == 0 || !json.has("spawnBox") || !json.get("spawnBox").isJsonArray() || json.getAsJsonArray("spawnBox").size() != 6) { return; }
        JsonArray box = json.getAsJsonArray("spawnBox");
        for (int corner : new int[] {1, 4}) {
            if (box.get(corner).isJsonPrimitive() && box.get(corner).getAsJsonPrimitive().isNumber()) { box.set(corner, new JsonPrimitive(box.get(corner).getAsInt() + shift)); }
        }
        flatNote(where + " gives a spawnBox", box.toString(), pack);
    }

    static void movedPoint(JsonObject holder, String key, int shift, Ported pack, String where) {
        if (!holder.has(key) || !holder.get(key).isJsonPrimitive()) { return; }
        String written = holder.get(key).getAsString().trim();
        int comma = written.indexOf(',');
        int colon = comma < 0 ? -1 : written.lastIndexOf(':', comma);
        String[] parts = written.substring(colon + 1).split(",", -1);
        String dimension = colon > 0 ? written.substring(0, colon) : "";
        int moved = colon > 0 ? pack.shiftIn(dimension) : shift;
        String prefix = "";
        if (colon > 0) {
            Integer number = pack.dimensionNumber(dimension);
            prefix = (number == null ? dimension : String.valueOf(number)) + ":";
        }
        if (parts.length != 3) { return; }
        parts[1] = Convert.shiftY(parts[1], moved);
        String out = prefix + String.join(",", parts);
        if (out.equals(written)) { return; }
        holder.addProperty(key, out);
        if (moved != 0) { flatNote(where + " gives " + key + " '" + written + "'", out, pack); }
    }

    private static void flatNote(String what, String moved, Ported pack) { pack.note(what + " in a modern flat world, whose floor sits at the bottom of the world; 1.12.2 lays a flat world from y 0, so it becomes '" + moved + "'"); }

    static void dimension(JsonObject json, String id, Ported pack) {
        Integer number = pack.dimensionNumber(id);
        if (number != null && !json.has("id")) { json.addProperty("id", number); }
        int shift = pack.dimensionShift(id);
        JsonObject terrain = json.has("terrain") && json.get("terrain").isJsonObject() ? json.getAsJsonObject("terrain") : null;
        if (shift != 0 && terrain != null) { terrain.remove("minHeight"); }
        JsonObject sky = json.has("sky") && json.get("sky").isJsonObject() ? json.getAsJsonObject("sky") : null;
        if (shift == 0 || sky == null || !sky.has("groundLevel") || !sky.get("groundLevel").isJsonPrimitive() || !sky.getAsJsonPrimitive("groundLevel").isNumber()) { return; }
        int written = sky.get("groundLevel").getAsInt();
        sky.addProperty("groundLevel", written + shift);
        flatNote("Dimension " + id + " gives groundLevel " + written, String.valueOf(written + shift), pack);
    }

    static void anvil(JsonObject json, Ported pack, String where) {
        for (String key : new String[] {"item", "with"}) {
            JsonObject holder = json.has(key) && json.get(key).isJsonObject() ? json.getAsJsonObject(key) : json;
            String field = holder == json ? key : "item";
            if (!holder.has(field) || !holder.get(field).isJsonArray()) { continue; }
            JsonArray names = holder.getAsJsonArray(field);
            Set<String> files = new LinkedHashSet<>();
            for (JsonElement named : names) {
                Convert.Ref ref = Convert.item(named.getAsString(), pack, where);
                files.add(ref == null ? named.getAsString() : ref.id);
            }
            if (files.size() == 1 && names.size() > 1) { holder.addProperty(field, files.iterator().next() + ":*"); }
            else if (names.size() > 0) {
                holder.add(field, names.get(0));
                if (names.size() > 1) { pack.note(where + " lists several items as its " + key + ", and 1.12.2 reads one, so only " + names.get(0).getAsString() + " is kept"); }
            }
        }
        if (!json.has("enchantments") || !json.get("enchantments").isJsonObject()) { return; }
        JsonObject levels = json.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(levels.entrySet())) {
            String mapped = Ids.enchantment(entry.getKey());
            if (mapped.equals(entry.getKey())) { continue; }
            levels.remove(entry.getKey());
            levels.add(mapped, entry.getValue());
        }
    }

    static void fuels(JsonObject json, Ported pack, String where) {
        if (!json.has("fuels") || !json.get("fuels").isJsonArray()) { return; }
        for (JsonElement element : json.getAsJsonArray("fuels")) {
            if (!element.isJsonObject() || !element.getAsJsonObject().has("tag")) { continue; }
            JsonObject fuel = element.getAsJsonObject();
            String tag = fuel.remove("tag").getAsString();
            String ore = Ids.oreName(tag);
            if (ore == null) { pack.note(where + " burns the tag " + tag + ", which has no ore dictionary name, so the entry is left without one"); }
            else { fuel.addProperty("oreDict", ore); }
        }
    }

    static void remap(JsonObject json, Ported pack, String where) {
        String registry = Convert.text(json, "registry");
        if ("minecraft:block".equals(registry) || "minecraft:item".equals(registry)) { json.addProperty("registry", registry + "s"); }
        if (!json.has("mapping") || !json.get("mapping").isJsonObject()) { return; }
        boolean blocks = registry.startsWith("minecraft:block");
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("mapping").entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) { continue; }
            Convert.Ref ref = blocks ? Convert.block(entry.getValue().getAsString(), Collections.emptyMap(), pack, where) : Convert.item(entry.getValue().getAsString(), pack, where);
            if (ref != null) { entry.setValue(new JsonPrimitive(ref.id)); }
        }
    }

    static void trades(JsonObject json, Ported pack, String where) {
        if (!json.has("trades") || !json.get("trades").isJsonArray()) { return; }
        for (JsonElement element : json.getAsJsonArray("trades")) {
            if (!element.isJsonObject()) { continue; }
            JsonObject trade = element.getAsJsonObject();
            String profession = Ids.namespaced(Convert.text(trade, "profession"));
            if (trade.has("career") || profession.endsWith(":")) { continue; }
            String[] legacy = PROFESSIONS.get(profession);
            if (legacy != null) {
                trade.addProperty("profession", legacy[0]);
                trade.addProperty("career", legacy[1]);
            }
            else if (!Ids.vanilla(profession)) { trade.addProperty("career", profession.replace(':', '.')); }
            else { pack.note(where + " trades with the profession " + profession + ", which 1.12.2 does not have, so the trade finds no villager"); }
        }
    }

    static void exposures(JsonObject json, Ported pack, String where) {
        for (String key : new String[] {"blocks", "items"}) {
            if (!json.has(key) || !json.get(key).isJsonArray()) { continue; }
            JsonArray out = new JsonArray();
            for (JsonElement element : json.remove(key).getAsJsonArray()) {
                String text = element.isJsonPrimitive() ? element.getAsString() : "";
                int split = text.indexOf('=');
                String named = split < 0 ? text : text.substring(0, split);
                String level = split < 0 ? "" : text.substring(split);
                String mapped = "blocks".equals(key) ? Convert.blockString(named, pack, where) : Convert.itemString(named, pack, where);
                out.add(element.isJsonPrimitive() ? new JsonPrimitive(mapped + level) : element);
            }
            json.add(key, out);
        }
    }
}
