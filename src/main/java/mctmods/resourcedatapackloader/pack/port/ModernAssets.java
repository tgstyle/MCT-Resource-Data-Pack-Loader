package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentGeneratedModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class ModernAssets {
    private static final String ITEM_MODELS = "models/item/";
    private static final String JSON = ".json";
    private static final String MCMETA = ".mcmeta";
    private static final String TYPE = "type";
    private static final String MODEL = "model";
    private static final int LARGEST = 1 << 20;
    private static final Pattern ARMOR = Pattern.compile("textures/models/armor/(.+)_layer_([12])(_overlay)?\\.png");
    private static final Pattern TRIM = Pattern.compile("textures/trims/models/armor/(.+?)(_leggings)?\\.png");
    private static final Pattern HORSE = Pattern.compile("textures/entity/horse/armor/horse_armor_(.+)\\.png");
    private static final Map<String, String> MOVED = Map.of("textures/entity/elytra.png", "textures/entity/equipment/wings/elytra.png", "textures/entity/wolf/wolf_armor.png", "textures/entity/equipment/wolf_body/armadillo_scute.png",
            "textures/entity/wolf/wolf_armor_overlay.png", "textures/entity/equipment/wolf_body/armadillo_scute_overlay.png");
    private static final Map<String, String> RANGES = Map.of("custom_model_data", "minecraft:custom_model_data", "damage", "minecraft:damage", "pull", "minecraft:use_duration");
    private static final Map<String, String> CONDITIONS = Map.of("damaged", "minecraft:damaged", "pulling", "minecraft:using_item", "blocking", "minecraft:using_item", "throwing", "minecraft:using_item",
            "tooting", "minecraft:using_item", "broken", "minecraft:broken", "cast", "minecraft:fishing_rod/cast");
    private static final Set<String> TRANSLUCENT = Set.of("translucent", "minecraft:translucent");

    private ModernAssets() {}

    static void model(JsonObject json) {
        JsonElement layer = json.remove("render_type");
        if (layer != null && layer.isJsonPrimitive() && TRANSLUCENT.contains(layer.getAsString()) && json.has("textures") && json.get("textures").isJsonObject()) { ContentGeneratedModels.forceTranslucent(json.getAsJsonObject("textures")); }
    }

    @Nullable static String definitionPath(String path) {
        if (!path.startsWith(ITEM_MODELS) || !path.endsWith(JSON)) { return null; }
        String rest = path.substring(ITEM_MODELS.length(), path.length() - JSON.length());
        return rest.isEmpty() || rest.indexOf('/') >= 0 ? null : "items/" + rest + JSON;
    }

    @Nullable static JsonObject definition(String namespace, String path, @Nullable JsonObject model, String file, Consumer<String> note) {
        if (model == null || !model.has("overrides") || !model.get("overrides").isJsonArray() || model.getAsJsonArray("overrides").isEmpty()) { return null; }
        JsonObject base = plain(namespace + ":item/" + path.substring(ITEM_MODELS.length(), path.length() - JSON.length()));
        JsonObject current = base;
        for (JsonElement override : model.getAsJsonArray("overrides")) {
            if (!override.isJsonObject() || !override.getAsJsonObject().has(MODEL) || !override.getAsJsonObject().has("predicate") || !override.getAsJsonObject().get("predicate").isJsonObject()) { continue; }
            List<Map.Entry<String, JsonElement>> tests = new ArrayList<>(override.getAsJsonObject().getAsJsonObject("predicate").entrySet());
            JsonObject inner = plain(qualified(override.getAsJsonObject().get(MODEL).getAsString()));
            boolean kept = true;
            for (int i = tests.size() - 1; i >= 0 && kept; i--) {
                JsonObject tested = test(tests.get(i).getKey(), tests.get(i).getValue().getAsFloat(), inner, current);
                if (tested == null) {
                    kept = false;
                    note.accept("'" + file + "' switches its model on '" + tests.get(i).getKey() + "', which item definitions have no plain twin for, so that override is left out");
                }
                else { inner = tested; }
            }
            if (kept) { current = inner; }
        }
        JsonObject definition = new JsonObject();
        definition.add(MODEL, current);
        if (Ported.GSON.toJson(definition).length() > LARGEST) {
            note.accept("'" + file + "' has too many combined overrides to write as one item definition, so items/" + path.substring(ITEM_MODELS.length()) + " shows the plain model");
            definition.add(MODEL, base);
        }
        return definition;
    }

    private static String qualified(String model) { return model.indexOf(':') < 0 ? ModernFixes.MINECRAFT + model : model; }

    private static JsonObject plain(String model) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, "minecraft:model");
        out.addProperty(MODEL, model);
        return out;
    }

    @Nullable private static JsonObject test(String predicate, float value, JsonObject on, JsonObject off) {
        String bare = predicate.startsWith(ModernFixes.MINECRAFT) ? predicate.substring(ModernFixes.MINECRAFT.length()) : predicate;
        if (value <= 0.0F && !RANGES.containsKey(bare)) { return on; }
        JsonObject out = new JsonObject();
        if (RANGES.containsKey(bare)) {
            out.addProperty(TYPE, "minecraft:range_dispatch");
            out.addProperty("property", RANGES.get(bare));
            if ("pull".equals(bare)) { out.addProperty("scale", 0.05F); }
            JsonObject entry = new JsonObject();
            entry.addProperty("threshold", value);
            entry.add(MODEL, on);
            JsonArray entries = new JsonArray();
            entries.add(entry);
            out.add("entries", entries);
            out.add("fallback", off);
            return out;
        }
        if (CONDITIONS.containsKey(bare)) {
            out.addProperty(TYPE, "minecraft:condition");
            out.addProperty("property", CONDITIONS.get(bare));
            out.add("on_true", on);
            out.add("on_false", off);
            return out;
        }
        if (!"lefthanded".equals(bare)) { return null; }
        JsonObject left = new JsonObject();
        left.addProperty("when", "left");
        left.add(MODEL, on);
        JsonArray cases = new JsonArray();
        cases.add(left);
        out.addProperty(TYPE, "minecraft:select");
        out.addProperty("property", "minecraft:main_hand");
        out.add("cases", cases);
        out.add("fallback", off);
        return out;
    }

    @Nullable static String moved(String path) {
        boolean meta = path.endsWith(MCMETA);
        String image = meta ? path.substring(0, path.length() - MCMETA.length()) : path;
        String out = MOVED.get(image);
        Matcher armor = ARMOR.matcher(image);
        Matcher trim = TRIM.matcher(image);
        Matcher horse = HORSE.matcher(image);
        if (armor.matches()) { out = "textures/entity/equipment/" + ("1".equals(armor.group(2)) ? "humanoid/" : "humanoid_leggings/") + armor.group(1) + (armor.group(3) == null ? "" : armor.group(3)) + ".png"; }
        else if (trim.matches()) { out = "textures/trims/entity/" + (trim.group(2) == null ? "humanoid/" : "humanoid_leggings/") + trim.group(1) + ".png"; }
        else if (horse.matches()) { out = "textures/entity/equipment/horse_body/" + horse.group(1) + ".png"; }
        return out == null ? null : meta ? out + MCMETA : out;
    }
}
