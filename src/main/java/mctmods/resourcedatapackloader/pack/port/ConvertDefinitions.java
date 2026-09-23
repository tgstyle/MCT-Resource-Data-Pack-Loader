package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class ConvertDefinitions {
    private static final List<String> VANILLA_PIECES = List.of("house1", "house2", "house3", "house4garden", "woodhut", "hall", "church", "field1", "field2");
    private static final String PIECES = "villagePieces";
    private static final String PIECES_BLACKLIST = "villagePiecesAreBlacklist";
    private static final List<String> ANVIL_INPUTS = List.of("item", "with");
    private static final List<String> VANILLA_DIMENSIONS = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");
    private static final Map<String, List<String>> LEGACY_STRUCTURES = Map.of(
            "minecraft:overworld", List.of("villages", "mineshafts", "strongholds", "temples", "monuments", "mansions"),
            "minecraft:the_nether", List.of("netherbridges"), "minecraft:the_end", List.of("endcities"));
    private static final Map<String, List<String>> MODERN_STRUCTURES = Map.of(
            "minecraft:overworld", List.of("ancient_cities", "buried_treasures", "ocean_ruins", "pillager_outposts", "ruined_portals", "shipwrecks", "trail_ruins"),
            "minecraft:the_nether", List.of("nether_fossils"), "minecraft:the_end", List.of());

    private ConvertDefinitions() {}

    static void villagePieces(JsonObject settings, Ported pack) {
        if (!settings.has(PIECES) || !settings.get(PIECES).isJsonArray()) { return; }
        if (settings.has(PIECES_BLACKLIST) && settings.get(PIECES_BLACKLIST).isJsonPrimitive() && !settings.get(PIECES_BLACKLIST).getAsBoolean()) { return; }
        Set<String> named = new HashSet<>();
        for (JsonElement element : settings.getAsJsonArray(PIECES)) {
            if (element.isJsonPrimitive()) { named.add(element.getAsString().trim().toLowerCase(Locale.ROOT)); }
        }
        if (!named.containsAll(VANILLA_PIECES)) { return; }
        List<String> kept = new ArrayList<>();
        for (String plot : pack.plots()) {
            String id = plot.toLowerCase(Locale.ROOT);
            if (!named.contains(id) && !named.contains(id.substring(id.indexOf(':') + 1))) { kept.add(plot); }
        }
        if (kept.isEmpty()) { return; }
        JsonArray whitelist = new JsonArray();
        kept.forEach(whitelist::add);
        settings.add(PIECES, whitelist);
        settings.addProperty(PIECES_BLACKLIST, false);
        pack.rewrote();
        pack.note("villagePieces blocks all nine 1.12.2 village pieces, so it becomes a whitelist of the pack's own " + kept.size() + " plot(s): " + String.join(", ", kept));
    }

    static JsonArray exposureNames(JsonArray names, boolean blocks, Ported pack) {
        JsonArray out = new JsonArray();
        for (JsonElement element : names) {
            String text = element.isJsonPrimitive() ? element.getAsString() : "";
            int split = text.indexOf('=');
            String name = (split < 0 ? text : text.substring(0, split)).trim();
            if (name.indexOf(':') < 0 || Ids.splitMeta(name) != null) {
                out.add(element);
                continue;
            }
            String level = split < 0 ? "" : text.substring(split);
            List<String> mapped = pack.owns(name) ? blocks ? pack.ownBlocks(name) : pack.ownItems(name) : Ids.isModded(name) ? List.of(name) : blocks ? Ids.blocks(name) : Ids.items(name);
            if (mapped.size() != 1 || !mapped.get(0).equals(name)) { pack.rewrote(); }
            for (String one : mapped) { out.add(one + level); }
        }
        return out;
    }

    static JsonArray furnaceRemovals(JsonArray removals, Ported pack) {
        JsonArray out = new JsonArray();
        for (JsonElement element : removals) {
            if (element.isJsonPrimitive()) {
                for (String name : Convert.itemNames(element.getAsString(), pack)) { out.add(name); }
                continue;
            }
            if (!element.isJsonObject()) {
                out.add(element);
                continue;
            }
            JsonObject removal = element.getAsJsonObject();
            List<String> inputs = removal.has("input") && removal.get("input").isJsonPrimitive() ? Convert.itemNames(removal.get("input").getAsString(), pack) : List.of("");
            List<String> results = removal.has("result") && removal.get("result").isJsonPrimitive() ? Convert.itemNames(removal.get("result").getAsString(), pack) : List.of("");
            for (String input : inputs) {
                for (String result : results) {
                    JsonObject one = new JsonObject();
                    if (!input.isEmpty()) { one.addProperty("input", input); }
                    if (!result.isEmpty()) { one.addProperty("result", result); }
                    out.add(one);
                }
            }
        }
        return out;
    }

    static void modernStructuresOff(JsonObject template, Ported pack) {
        JsonObject structures = template.getAsJsonObject("structures");
        List<String> dimensions = new ArrayList<>();
        if (template.has("dimensions") && template.get("dimensions").isJsonArray()) {
            for (JsonElement named : template.getAsJsonArray("dimensions")) { dimensions.add(ContentFormats.dimensionId(named.getAsString())); }
        }
        if (dimensions.isEmpty()) { dimensions.addAll(VANILLA_DIMENSIONS); }
        List<String> added = new ArrayList<>();
        for (String dimension : dimensions) {
            List<String> legacy = LEGACY_STRUCTURES.get(dimension);
            if (legacy == null || !allOff(structures, legacy)) { continue; }
            for (String set : MODERN_STRUCTURES.get(dimension)) {
                if (structures.has(set)) { continue; }
                structures.addProperty(set, false);
                added.add(set);
            }
        }
        if (added.isEmpty()) { return; }
        pack.rewrote();
        pack.note("A world template turns off every structure 1.12.2 had, so the structures only this version has are turned off with them: " + String.join(", ", added));
    }

    private static String shiftedPoint(String written, int shift, Ported pack) {
        String trimmed = written.trim();
        int comma = trimmed.indexOf(',');
        int colon = comma < 0 ? -1 : trimmed.lastIndexOf(':', comma);
        String[] parts = trimmed.substring(colon + 1).split(",", -1);
        int moved = colon > 0 ? pack.shiftIn(trimmed.substring(0, colon)) : shift;
        if (parts.length != 3 || moved == 0) { return written; }
        parts[1] = Convert.shiftY(parts[1], moved);
        return trimmed.substring(0, colon + 1) + String.join(",", parts);
    }

    static void movedPoint(JsonObject holder, String key, int shift, Ported pack, String owner) {
        if (!holder.has(key) || !holder.get(key).isJsonPrimitive()) { return; }
        String written = holder.get(key).getAsString();
        String moved = shiftedPoint(written, shift, pack);
        if (moved.equals(written)) { return; }
        holder.addProperty(key, moved);
        flatNote(owner + " gives " + key + " '" + written + "'", moved, pack);
    }

    private static void flatNote(String what, String moved, Ported pack) {
        pack.rewrote();
        pack.note(what + " in a 1.12.2 flat world, whose floor sat at y 0; this version lays a flat world from its bottom, so it becomes '" + moved + "'");
    }

    static void flatLayers(JsonObject settings, Ported pack) {
        if (Convert.flat(settings) && settings.has("generatorOptions") && settings.get("generatorOptions").isJsonPrimitive()) { settings.addProperty("generatorOptions", superflat(settings.get("generatorOptions").getAsString(), pack)); }
    }

    private static String superflat(String text, Ported pack) {
        String[] parts = text.split(";", -1);
        if (parts.length < 2) { return text; }
        String[] layers = parts[1].split(",", -1);
        for (int i = 0; i < layers.length; i++) {
            int star = layers[i].indexOf('*');
            layers[i] = layers[i].substring(0, star + 1) + Convert.blockName(layers[i].substring(star + 1), pack);
        }
        parts[1] = String.join(",", layers);
        return String.join(";", parts);
    }

    static void templatePositions(JsonObject settings, String template, Ported pack) {
        int shift = Convert.flatShift(settings);
        movedPoint(settings, "worldSpawn", shift, pack, "World template " + template);
        movedPoint(settings, "resetSendsTo", shift, pack, "World template " + template);
    }

    static void teamPositions(JsonObject json, String team, Ported pack) {
        int shift = pack.overworldShift();
        movedPoint(json, "spawn", shift, pack, "Team file " + team);
        if (json.has("standIn") && json.get("standIn").isJsonObject()) { movedPoint(json.getAsJsonObject("standIn"), "at", shift, pack, "Team file " + team + " standIn"); }
        if (shift == 0 || !json.has("spawnBox") || !json.get("spawnBox").isJsonArray() || json.getAsJsonArray("spawnBox").size() != 6) { return; }
        JsonArray box = json.getAsJsonArray("spawnBox");
        for (int corner : new int[] {1, 4}) {
            if (box.get(corner).isJsonPrimitive() && box.get(corner).getAsJsonPrimitive().isNumber()) { box.set(corner, new JsonPrimitive(box.get(corner).getAsInt() + shift)); }
        }
        flatNote("Team file " + team + " gives a spawnBox", box.toString(), pack);
    }

    static void groundLevel(JsonObject json, String dimension, Ported pack) {
        int shift = pack.shiftIn(dimension);
        JsonObject sky = json.has("sky") && json.get("sky").isJsonObject() ? json.getAsJsonObject("sky") : null;
        if (shift == 0 || sky == null || !sky.has("groundLevel") || !sky.get("groundLevel").isJsonPrimitive() || !sky.getAsJsonPrimitive("groundLevel").isNumber()) { return; }
        int written = sky.get("groundLevel").getAsInt();
        sky.addProperty("groundLevel", written + shift);
        flatNote("Dimension " + dimension + " gives groundLevel " + written, String.valueOf(written + shift), pack);
    }

    private static boolean allOff(JsonObject structures, List<String> names) {
        for (String name : names) {
            if (!structures.has(name) || !structures.get(name).isJsonPrimitive() || structures.get(name).getAsBoolean()) { return false; }
        }
        return true;
    }

    static void anvil(JsonObject json, Ported pack) {
        for (String key : ANVIL_INPUTS) {
            JsonObject holder = json.has(key) && json.get(key).isJsonObject() ? json.getAsJsonObject(key) : json;
            String field = holder == json ? key : "item";
            if (!holder.has(field) || !holder.get(field).isJsonPrimitive() || !holder.get(field).getAsString().trim().endsWith(Convert.WILDCARD)) { continue; }
            JsonArray names = new JsonArray();
            for (String name : Convert.itemNames(holder.get(field).getAsString(), pack)) { names.add(name); }
            holder.add(field, names);
        }
        if (!json.has("enchantments") || !json.get("enchantments").isJsonObject()) { return; }
        JsonObject levels = json.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(levels.entrySet())) {
            String mapped = Ids.enchantment(entry.getKey());
            if (mapped == null || mapped.equals(entry.getKey())) { continue; }
            levels.remove(entry.getKey());
            levels.add(mapped, entry.getValue());
            pack.rewrote();
        }
    }
}
