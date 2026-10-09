package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.util.IdNames;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.GsonHelper;
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
    private static final List<String> VANILLA_DIMENSIONS = List.of(ContentBiomes.OVERWORLD, "minecraft:the_nether", "minecraft:the_end");
    private static final Map<String, List<String>> LEGACY_STRUCTURES = Map.of(
            ContentBiomes.OVERWORLD, List.of("villages", "mineshafts", "strongholds", "temples", "monuments", "mansions"),
            "minecraft:the_nether", List.of("netherbridges"), "minecraft:the_end", List.of("endcities"));
    private static final Map<String, List<String>> MODERN_STRUCTURES = Map.of(
            ContentBiomes.OVERWORLD, List.of("ancient_cities", "buried_treasures", "ocean_ruins", "pillager_outposts", "ruined_portals", "shipwrecks", "trail_ruins", "trial_chambers"),
            "minecraft:the_nether", List.of("nether_fossils"), "minecraft:the_end", List.of());

    private ConvertDefinitions() {}

    static void villagePieces(JsonObject settings, Ported pack) {
        if (!GsonHelper.isArrayNode(settings, PIECES)) { return; }
        if (GsonHelper.isValidPrimitive(settings, PIECES_BLACKLIST) && !settings.get(PIECES_BLACKLIST).getAsBoolean()) { return; }
        Set<String> named = new HashSet<>();
        for (JsonElement element : settings.getAsJsonArray(PIECES)) {
            if (element.isJsonPrimitive()) { named.add(Settings.lower(element.getAsString())); }
        }
        if (!named.containsAll(VANILLA_PIECES)) { return; }
        List<String> kept = new ArrayList<>();
        for (String plot : pack.plots()) {
            String id = plot.toLowerCase(Locale.ROOT);
            if (!named.contains(id) && !named.contains(IdNames.path(id))) { kept.add(plot); }
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
            List<String> mapped = pack.variants(name, blocks);
            if (mapped.size() != 1 || !mapped.getFirst().equals(name)) { pack.rewrote(); }
            for (String one : mapped) { out.add(one + level); }
        }
        return out;
    }

    static JsonArray exposureDimensions(JsonArray names, Ported pack) {
        JsonArray out = new JsonArray();
        for (JsonElement element : names) {
            if (!element.isJsonPrimitive()) {
                out.add(element);
                continue;
            }
            String text = element.getAsString();
            int split = text.indexOf('=');
            String name = (split < 0 ? text : text.substring(0, split)).trim();
            String mapped = pack.dimension(name);
            if (!mapped.equals(name) || element.getAsJsonPrimitive().isNumber()) { pack.rewrote(); }
            out.add(mapped + (split < 0 ? "" : text.substring(split)));
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
            List<String> inputs = GsonHelper.isValidPrimitive(removal, "input") ? Convert.itemNames(removal.get("input").getAsString(), pack) : List.of("");
            List<String> results = GsonHelper.isValidPrimitive(removal, "result") ? Convert.itemNames(removal.get("result").getAsString(), pack) : List.of("");
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
        if (GsonHelper.isArrayNode(template, "dimensions")) {
            for (JsonElement named : template.getAsJsonArray("dimensions")) { dimensions.add(ContentFormats.dimensionId(named.getAsString())); }
        }
        if (dimensions.isEmpty()) { dimensions.addAll(VANILLA_DIMENSIONS); }
        List<String> added = new ArrayList<>();
        for (String dimension : dimensions) {
            List<String> legacy = LEGACY_STRUCTURES.get(dimension);
            if (legacy == null || anyOn(structures, legacy)) { continue; }
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
        if (!GsonHelper.isValidPrimitive(holder, key)) { return; }
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

    static void flatLayers(JsonObject settings, Ported pack) { if (Convert.flat(settings) && GsonHelper.isValidPrimitive(settings, "generatorOptions")) { settings.addProperty("generatorOptions", superflat(settings.get("generatorOptions").getAsString(), pack)); } }

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
        if (GsonHelper.isObjectNode(json, "standIn")) { movedPoint(json.getAsJsonObject("standIn"), "at", shift, pack, "Team file " + team + " standIn"); }
        if (shift == 0 || !GsonHelper.isArrayNode(json, "spawnBox") || json.getAsJsonArray("spawnBox").size() != 6) { return; }
        JsonArray box = json.getAsJsonArray("spawnBox");
        boolean changed = false;
        for (int corner : new int[] {1, 4}) {
            if (box.get(corner).isJsonPrimitive() && box.get(corner).getAsJsonPrimitive().isNumber()) {
                box.set(corner, new JsonPrimitive(box.get(corner).getAsInt() + shift));
                changed = true;
            }
        }
        if (changed) { flatNote("Team file " + team + " gives a spawnBox", box.toString(), pack); }
    }

    static void groundLevel(JsonObject json, String dimension, Ported pack) {
        int shift = pack.shiftIn(dimension);
        JsonObject sky = GsonHelper.isObjectNode(json, "sky") ? json.getAsJsonObject("sky") : new JsonObject();
        if (shift == 0 || !GsonHelper.isNumberValue(sky, "groundLevel")) { return; }
        int written = sky.get("groundLevel").getAsInt();
        sky.addProperty("groundLevel", written + shift);
        flatNote("Dimension " + dimension + " gives groundLevel " + written, String.valueOf(written + shift), pack);
    }

    private static boolean anyOn(JsonObject structures, List<String> names) {
        for (String name : names) { if (!GsonHelper.isValidPrimitive(structures, name) || structures.get(name).getAsBoolean()) { return true; } }
        return false;
    }

    static void anvil(JsonObject json, Ported pack) {
        for (String key : ANVIL_INPUTS) {
            JsonObject holder = GsonHelper.isObjectNode(json, key) ? json.getAsJsonObject(key) : json;
            String field = holder == json ? key : "item";
            if (!GsonHelper.isValidPrimitive(holder, field) || !holder.get(field).getAsString().trim().endsWith(Convert.WILDCARD)) { continue; }
            JsonArray names = new JsonArray();
            for (String name : Convert.itemNames(holder.get(field).getAsString(), pack)) { names.add(name); }
            holder.add(field, names);
        }
        if (!GsonHelper.isObjectNode(json, "enchantments")) { return; }
        JsonObject levels = json.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(levels.entrySet())) {
            String mapped = Ids.enchantment(entry.getKey());
            if (mapped == null || mapped.equals(entry.getKey())) { continue; }
            levels.remove(entry.getKey());
            levels.add(mapped, entry.getValue());
            pack.rewrote();
        }
    }

    static void oreDictTags(JsonObject json, String list, Ported pack) {
        if (!GsonHelper.isArrayNode(json, list)) { return; }
        for (JsonElement element : json.getAsJsonArray(list)) {
            if (!element.isJsonObject()) { continue; }
            JsonObject entry = element.getAsJsonObject();
            if (!entry.has("oreDict") || entry.has("tag")) { continue; }
            entry.addProperty("tag", Ids.oreDictTag(entry.remove("oreDict").getAsString()));
            pack.rewrote();
        }
    }

    static void orderTags(JsonObject json, Ported pack) {
        if (!GsonHelper.isArrayNode(json, "orders")) { return; }
        for (JsonElement element : json.getAsJsonArray("orders")) {
            if (!element.isJsonObject()) { continue; }
            JsonObject entry = element.getAsJsonObject();
            if (!GsonHelper.isValidPrimitive(entry, "blocks")) { continue; }
            String blocks = entry.get("blocks").getAsString().trim();
            if (blocks.isEmpty() || blocks.contains(":")) { continue; }
            entry.addProperty("blocks", Ids.oreDictTag(blocks));
            pack.rewrote();
        }
    }
}
