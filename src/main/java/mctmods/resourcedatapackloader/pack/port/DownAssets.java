package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class DownAssets {
    private static final String ITEM_MODELS = "models/item/";
    private static final String ITEMS = "items/";
    private static final String JSON = ".json";
    private static final String MCMETA = ".mcmeta";
    private static final String TYPE = "type";
    private static final String MODEL = "model";
    private static final String PROPERTY = "property";
    private static final String FALLBACK = "fallback";
    private static final Pattern ARMOR = Pattern.compile("textures/entity/equipment/(humanoid|humanoid_leggings)/(.+?)(_overlay)?\\.png");
    private static final Pattern TRIM = Pattern.compile("textures/trims/entity/(humanoid|humanoid_leggings)/(.+)\\.png");
    private static final Pattern HORSE = Pattern.compile("textures/entity/equipment/horse_body/(.+)\\.png");
    private static final Map<String, String> MOVED = Map.of("textures/entity/equipment/wings/elytra.png", "textures/entity/elytra.png", "textures/entity/equipment/wolf_body/armadillo_scute.png", "textures/entity/wolf/wolf_armor.png",
            "textures/entity/equipment/wolf_body/armadillo_scute_overlay.png", "textures/entity/wolf/wolf_armor_overlay.png");
    private static final Map<String, String> RANGES = Map.of("minecraft:custom_model_data", "custom_model_data", "minecraft:damage", "damage", "minecraft:use_duration", "pull");
    private static final Map<String, String> CONDITIONS = Map.of("minecraft:damaged", "damaged", "minecraft:broken", "broken", "minecraft:fishing_rod/cast", "cast");

    private DownAssets() {}

    private record Case(JsonObject predicate, String model) {}

    static void model(JsonObject json) {
        if (!json.has("textures") || !json.get("textures").isJsonObject()) { return; }
        boolean translucent = false;
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("textures").entrySet()) {
            if (!entry.getValue().isJsonObject() || !entry.getValue().getAsJsonObject().has("sprite")) { continue; }
            JsonObject sprite = entry.getValue().getAsJsonObject();
            translucent |= sprite.has("force_translucent") && sprite.get("force_translucent").getAsBoolean();
            entry.setValue(sprite.get("sprite"));
        }
        if (translucent && !json.has("render_type")) { json.addProperty("render_type", "minecraft:translucent"); }
    }

    @Nullable static String modelPath(String path) {
        if (!path.startsWith(ITEMS) || !path.endsWith(JSON)) { return null; }
        String rest = path.substring(ITEMS.length());
        return rest.length() == JSON.length() || rest.indexOf('/') >= 0 ? null : ITEM_MODELS + rest;
    }

    @Nullable static JsonObject made(String namespace, String modelPath, @Nullable JsonObject definition, String file, Consumer<String> note) {
        String self = namespace + ":item/" + modelPath.substring(ITEM_MODELS.length(), modelPath.length() - JSON.length());
        List<Case> cases = cases(definition, self, file, note);
        if (cases.isEmpty() || cases.getFirst().model().equals(self)) { return null; }
        JsonObject model = new JsonObject();
        model.addProperty("parent", cases.getFirst().model());
        overrides(model, cases);
        return model;
    }

    static void attach(JsonObject model, String namespace, String modelPath, @Nullable JsonObject definition, String file, Consumer<String> note) {
        if (model.has("overrides")) { return; }
        String self = namespace + ":item/" + modelPath.substring(ITEM_MODELS.length(), modelPath.length() - JSON.length());
        overrides(model, cases(definition, self, file, note));
    }

    private static void overrides(JsonObject model, List<Case> cases) {
        JsonArray overrides = new JsonArray();
        for (Case one : cases) {
            if (one.predicate().isEmpty()) { continue; }
            JsonObject override = new JsonObject();
            override.add("predicate", one.predicate());
            override.addProperty(MODEL, one.model());
            overrides.add(override);
        }
        if (!overrides.isEmpty()) { model.add("overrides", overrides); }
    }

    private static List<Case> cases(@Nullable JsonObject definition, String self, String file, Consumer<String> note) {
        List<Case> out = new ArrayList<>();
        if (definition != null && definition.has(MODEL)) { collect(definition.get(MODEL), new JsonObject(), self, out, file, note); }
        return out;
    }

    private static void collect(JsonElement node, JsonObject predicate, String self, List<Case> out, String file, Consumer<String> note) {
        if (!node.isJsonObject()) { return; }
        JsonObject held = node.getAsJsonObject();
        String type = DownFixes.namespaced(DownFixes.string(held, TYPE));
        String property = DownFixes.namespaced(DownFixes.string(held, PROPERTY));
        switch (type) {
            case "minecraft:model" -> out.add(new Case(predicate, DownFixes.string(held, MODEL)));
            case "minecraft:condition" -> {
                String named = "minecraft:using_item".equals(property) ? using(self) : CONDITIONS.get(property);
                collect(held.get("on_false"), predicate, self, out, file, note);
                if (named == null) { lost(file, property, note); }
                else { collect(held.get("on_true"), with(predicate, named, 1.0F), self, out, file, note); }
            }
            case "minecraft:range_dispatch" -> range(held, property, predicate, self, out, file, note);
            case "minecraft:select" -> {
                if (held.has(FALLBACK)) { collect(held.get(FALLBACK), predicate, self, out, file, note); }
                if (!"minecraft:main_hand".equals(property) || !held.has("cases")) {
                    lost(file, property, note);
                    return;
                }
                for (JsonElement one : held.getAsJsonArray("cases")) {
                    if (one.isJsonObject() && "left".equals(DownFixes.string(one.getAsJsonObject(), "when"))) { collect(one.getAsJsonObject().get(MODEL), with(predicate, "lefthanded", 1.0F), self, out, file, note); }
                }
            }
            default -> lost(file, type, note);
        }
    }

    private static void range(JsonObject held, String property, JsonObject predicate, String self, List<Case> out, String file, Consumer<String> note) {
        if (held.has(FALLBACK)) { collect(held.get(FALLBACK), predicate, self, out, file, note); }
        String named = RANGES.get(property);
        boolean normalized = !held.has("normalize") || held.get("normalize").getAsBoolean();
        if (named == null || "damage".equals(named) && !normalized || !held.has("entries") || !held.get("entries").isJsonArray()) {
            lost(file, property, note);
            return;
        }
        float scale = held.has("scale") ? held.get("scale").getAsFloat() : 1.0F;
        List<JsonObject> entries = new ArrayList<>();
        held.getAsJsonArray("entries").forEach(one -> { if (one.isJsonObject() && one.getAsJsonObject().has("threshold")) { entries.add(one.getAsJsonObject()); } });
        entries.sort((a, b) -> Float.compare(a.get("threshold").getAsFloat(), b.get("threshold").getAsFloat()));
        for (JsonObject entry : entries) {
            float threshold = entry.get("threshold").getAsFloat();
            float value = "pull".equals(named) ? threshold / (20.0F * scale) : threshold / scale;
            collect(entry.get(MODEL), with(predicate, named, value), self, out, file, note);
        }
    }

    private static String using(String self) {
        if (self.contains("shield")) { return "blocking"; }
        if (self.contains("trident")) { return "throwing"; }
        return self.contains("horn") ? "tooting" : "pulling";
    }

    private static JsonObject with(JsonObject predicate, String key, float value) {
        JsonObject out = predicate.deepCopy();
        out.addProperty(key, value);
        return out;
    }

    private static void lost(String file, String what, Consumer<String> note) { note.accept("'" + file + "' switches its model on '" + what + "', which item model overrides have no twin for, so that switch is left out"); }

    @Nullable static String moved(String path) {
        boolean meta = path.endsWith(MCMETA);
        String image = meta ? path.substring(0, path.length() - MCMETA.length()) : path;
        String out = MOVED.get(image);
        Matcher armor = ARMOR.matcher(image);
        Matcher trim = TRIM.matcher(image);
        Matcher horse = HORSE.matcher(image);
        if (armor.matches()) { out = "textures/models/armor/" + armor.group(2) + ("humanoid".equals(armor.group(1)) ? "_layer_1" : "_layer_2") + (armor.group(3) == null ? "" : armor.group(3)) + ".png"; }
        else if (trim.matches()) { out = "textures/trims/models/armor/" + trim.group(2) + ("humanoid".equals(trim.group(1)) ? "" : "_leggings") + ".png"; }
        else if (horse.matches()) { out = "textures/entity/horse/armor/horse_armor_" + horse.group(1) + ".png"; }
        return out == null ? null : meta ? out + MCMETA : out;
    }
}
