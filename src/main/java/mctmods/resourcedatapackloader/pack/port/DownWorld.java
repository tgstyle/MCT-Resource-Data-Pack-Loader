package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;

final class DownWorld {
    private static final String VISUAL = "minecraft:visual/";
    private static final String AUDIO = "minecraft:audio/";
    private static final String GAMEPLAY = "minecraft:gameplay/";
    private static final String ATTRIBUTES = "attributes";
    private static final String EFFECTS = "effects";
    private static final String CONFIG = "config";
    private static final String BELOW_TRUNK = "below_trunk_provider";
    private static final int FULL_SKY_LIGHT = 15;
    private static final int NOON = 6000;
    private static final int MIDNIGHT = 18000;
    private static final List<String> NEW_TYPE_FIELDS = List.of("skybox", "cardinal_light", "default_clock", "timelines", "has_fixed_time", "has_ender_dragon_fight");
    private static final Set<String> TYPE_MAPPED = Set.of(GAMEPLAY + "water_evaporates", GAMEPLAY + "fast_lava", GAMEPLAY + "nether_portal_spawns_piglin", GAMEPLAY + "respawn_anchor_works",
            GAMEPLAY + "piglins_zombify", GAMEPLAY + "can_start_raid", GAMEPLAY + "bed_rule", GAMEPLAY + "sky_light_level", VISUAL + "sun_angle", VISUAL + "default_dripstone_particle");
    private static final Map<String, Integer> BIOME_COLORS = Map.of("fog_color", 12638463, "water_color", 4159204, "water_fog_color", 329011);
    private static final Map<String, String> SOUNDS = Map.of("loop", "ambient_sound", "mood", "mood_sound", "additions", "additions_sound");

    private DownWorld() {}

    static void dimensionType(JsonObject json, String file, Consumer<String> note) {
        JsonElement held = json.remove(ATTRIBUTES);
        boolean modern = held != null || NEW_TYPE_FIELDS.stream().anyMatch(json::has);
        if (!modern) { return; }
        JsonObject attributes = held != null && held.isJsonObject() ? held.getAsJsonObject() : new JsonObject();
        String skybox = DownFixes.string(json, "skybox");
        boolean nether = "nether".equals(DownFixes.string(json, "cardinal_light")) || "none".equals(skybox) && flag(json, "has_ceiling");
        String effects = "end".equals(skybox) ? "minecraft:the_end" : nether ? "minecraft:the_nether" : "minecraft:overworld";
        boolean fixed = flag(json, "has_fixed_time");
        NEW_TYPE_FIELDS.forEach(json::remove);
        json.addProperty(EFFECTS, effects);
        json.addProperty("ultrawarm", flag(attributes, GAMEPLAY + "water_evaporates"));
        json.addProperty("natural", flag(attributes, GAMEPLAY + "nether_portal_spawns_piglin"));
        json.addProperty("respawn_anchor_works", flag(attributes, GAMEPLAY + "respawn_anchor_works"));
        json.addProperty("piglin_safe", attributes.has(GAMEPLAY + "piglins_zombify") && !flag(attributes, GAMEPLAY + "piglins_zombify"));
        json.addProperty("has_raids", !attributes.has(GAMEPLAY + "can_start_raid") || flag(attributes, GAMEPLAY + "can_start_raid"));
        JsonElement bed = attributes.get(GAMEPLAY + "bed_rule");
        json.addProperty("bed_works", bed == null || !bed.isJsonObject() || !flag(bed.getAsJsonObject(), "explodes"));
        if (fixed) {
            JsonElement level = attributes.get(GAMEPLAY + "sky_light_level");
            JsonElement sun = attributes.get(VISUAL + "sun_angle");
            int light = level != null && level.isJsonPrimitive() ? Math.round(level.getAsFloat()) : FULL_SKY_LIGHT;
            boolean angled = sun != null && sun.isJsonPrimitive();
            int time = angled ? timeAt(sun.getAsDouble()) : fixedTime(light);
            json.addProperty("fixed_time", time);
            String kept = angled ? "its sun at " + sun.getAsString() + " degrees" : "sky light " + light;
            note.accept("'" + file + "' has no day cycle and " + kept + ", which " + Port.Line.running().title() + " writes as the fixed time " + time);
        }
        List<String> lost = new ArrayList<>();
        for (String key : attributes.keySet()) {
            if (!TYPE_MAPPED.contains(key)) { lost.add(key); }
        }
        if (!lost.isEmpty()) { note.accept("'" + file + "' sets the dimension attribute(s) " + String.join(", ", lost) + ", which " + Port.Line.running().title() + " does not have, so they are left out and it takes the " + effects + " look"); }
    }

    private static boolean flag(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsBoolean(); }

    private static int fixedTime(int light) {
        if (light >= FULL_SKY_LIGHT) { return NOON; }
        if (light <= skyLight(MIDNIGHT)) { return MIDNIGHT; }
        int first = MIDNIGHT;
        int last = MIDNIGHT;
        for (int time = NOON; time < MIDNIGHT; time++) {
            int level = skyLight(time);
            if (level <= light && first == MIDNIGHT) { first = time; }
            if (level < light) {
                last = time;
                break;
            }
        }
        return (first + last) / 2;
    }

    private static int timeAt(double angle) {
        double wanted = angle / 360.0 - Math.floor(angle / 360.0);
        double low = 0.0;
        double high = 1.0;
        for (int i = 0; i < 60; i++) {
            double middle = (low + high) / 2.0;
            if (timeOfDay(middle) < wanted) { low = middle; }
            else { high = middle; }
        }
        return (int) (Math.round((low + 0.25) * 24000.0) % 24000L);
    }

    private static double timeOfDay(double turn) { return (turn * 2.0 + (0.5 - Math.cos(turn * Math.PI) / 2.0)) / 3.0; }

    private static int skyLight(long time) {
        double day = timeOfDay((time / 24000.0 - 0.25) - Math.floor(time / 24000.0 - 0.25));
        double dark = 1.0 - Mth.clamp(1.0 - (Math.cos(day * Math.PI * 2.0) * 2.0 + 0.5), 0.0, 1.0);
        return FULL_SKY_LIGHT - (int) ((1.0 - dark) * 11.0);
    }

    static void biome(JsonObject json, String file, Consumer<String> note) {
        if (json.has("carvers") && !json.get("carvers").isJsonObject()) {
            JsonObject steps = new JsonObject();
            steps.add("air", json.remove("carvers"));
            json.add("carvers", steps);
        }
        JsonElement held = json.remove(ATTRIBUTES);
        JsonObject attributes = held != null && held.isJsonObject() ? held.getAsJsonObject() : new JsonObject();
        JsonObject effects = json.has(EFFECTS) && json.get(EFFECTS).isJsonObject() ? json.getAsJsonObject(EFFECTS) : new JsonObject();
        for (String named : List.of("fog_color", "sky_color", "water_fog_color")) {
            JsonElement color = attributes.remove(VISUAL + named);
            if (color != null) { effects.add(named, color); }
        }
        JsonElement particles = attributes.remove(VISUAL + "ambient_particles");
        if (particles != null && particles.isJsonArray() && !particles.getAsJsonArray().isEmpty() && particles.getAsJsonArray().get(0).isJsonObject()) {
            JsonObject first = particles.getAsJsonArray().get(0).getAsJsonObject();
            JsonObject particle = new JsonObject();
            particle.add("options", first.get("particle"));
            particle.add("probability", first.get("probability"));
            effects.add("particle", particle);
        }
        JsonElement sounds = attributes.remove(AUDIO + "ambient_sounds");
        if (sounds != null && sounds.isJsonObject()) { SOUNDS.forEach((from, to) -> { if (sounds.getAsJsonObject().has(from)) { effects.add(to, sounds.getAsJsonObject().get(from)); } }); }
        JsonElement music = attributes.remove(AUDIO + "background_music");
        if (music != null && music.isJsonObject() && music.getAsJsonObject().has("default") && music.getAsJsonObject().get("default").isJsonObject()) {
            JsonObject track = music.getAsJsonObject().getAsJsonObject("default").deepCopy();
            if (!track.has("replace_current_music")) { track.addProperty("replace_current_music", false); }
            effects.add("music", track);
        }
        List<String> lost = new ArrayList<>(attributes.keySet());
        if (effects.remove("music_volume") != null) { lost.add("music_volume"); }
        for (Map.Entry<String, Integer> color : BIOME_COLORS.entrySet()) { effects.addProperty(color.getKey(), DownFixes.color(effects.get(color.getKey()), color.getValue())); }
        float temperature = json.has("temperature") && json.get("temperature").isJsonPrimitive() ? json.get("temperature").getAsFloat() : 0.5F;
        effects.addProperty("sky_color", DownFixes.color(effects.get("sky_color"), skyColor(temperature)));
        for (String tint : List.of("foliage_color", "grass_color")) {
            if (effects.has(tint)) { effects.addProperty(tint, DownFixes.color(effects.get(tint), 0)); }
        }
        json.add(EFFECTS, effects);
        if (!lost.isEmpty()) { note.accept("'" + file + "' sets the biome attribute(s) " + String.join(", ", lost) + ", which " + Port.Line.running().title() + " does not have, so they are left out"); }
    }

    private static int skyColor(float temperature) {
        float scaled = Mth.clamp(temperature / 3.0F, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - scaled * 0.05F, 0.5F + scaled * 0.1F, 1.0F);
    }

    static void feature(JsonObject json, String file, Consumer<String> note) {
        String type = DownFixes.namespaced(DownFixes.string(json, "type"));
        JsonObject config = json.has(CONFIG) && json.get(CONFIG).isJsonObject() ? json.getAsJsonObject(CONFIG) : null;
        if (config == null) { return; }
        switch (type) {
            case "minecraft:spike" -> {
                String block = config.has("state") && config.get("state").isJsonObject() ? DownFixes.string(config.getAsJsonObject("state"), "Name") : "";
                if (!block.isEmpty() && !"minecraft:packed_ice".equals(block)) { note.accept("'" + file + "' is a spike of " + block + ", and " + Port.Line.running().title() + " builds spikes of packed ice only, so it is an ice spike"); }
                json.addProperty("type", "minecraft:ice_spike");
                json.add(CONFIG, new JsonObject());
            }
            case "minecraft:block_blob" -> {
                JsonObject rock = new JsonObject();
                if (config.has("state")) { rock.add("state", config.get("state")); }
                json.addProperty("type", "minecraft:forest_rock");
                json.add(CONFIG, rock);
            }
            case "minecraft:huge_red_mushroom", "minecraft:huge_brown_mushroom" -> config.remove("can_place_on");
            case "minecraft:tree" -> tree(config, file, note);
            default -> { }
        }
    }

    private static void tree(JsonObject config, String file, Consumer<String> note) {
        JsonElement below = config.remove(BELOW_TRUNK);
        if (config.has("dirt_provider")) { return; }
        JsonElement guarded = below != null && below.isJsonObject() ? guarded(below.getAsJsonObject()) : null;
        if (below == null || guarded != null) {
            config.add("dirt_provider", guarded != null ? guarded : simpleState());
            config.addProperty("force_dirt", false);
            return;
        }
        boolean rules = "minecraft:rule_based_state_provider".equals(DownFixes.namespaced(DownFixes.string(below.getAsJsonObject(), "type")));
        if (rules) { note.accept("'" + file + "' places blocks under its trunk by rules, which " + Port.Line.running().title() + " cannot hold, so it places dirt"); }
        config.add("dirt_provider", rules ? simpleState() : below);
        config.addProperty("force_dirt", !rules);
    }

    @Nullable private static JsonElement guarded(JsonObject provider) {
        if (!"minecraft:rule_based_state_provider".equals(DownFixes.namespaced(DownFixes.string(provider, "type"))) || !provider.has("rules") || !provider.get("rules").isJsonArray()) { return null; }
        JsonArray rules = provider.getAsJsonArray("rules");
        if (rules.size() != 1 || !rules.get(0).isJsonObject()) { return null; }
        JsonObject rule = rules.get(0).getAsJsonObject();
        JsonElement test = rule.get("if_true");
        JsonObject inner = test != null && test.isJsonObject() && test.getAsJsonObject().has("predicate") && test.getAsJsonObject().get("predicate").isJsonObject() ? test.getAsJsonObject().getAsJsonObject("predicate") : null;
        boolean vanilla = inner != null && "minecraft:cannot_replace_below_tree_trunk".equals(DownFixes.string(inner, "tag"));
        return vanilla ? rule.get("then") : null;
    }

    private static JsonObject simpleState() {
        JsonObject state = new JsonObject();
        state.addProperty("Name", "minecraft:dirt");
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:simple_state_provider");
        out.add("state", state);
        return out;
    }

    static void trim(JsonObject json, String path, String file, Consumer<String> note) {
        boolean material = path.startsWith("trim_material/");
        if (material && json.has("override_armor_assets")) { json.add("override_armor_materials", json.remove("override_armor_assets")); }
        List<String> needed = material ? List.of("ingredient", "item_model_index") : List.of("template_item");
        if (needed.stream().allMatch(json::has)) { return; }
        JsonObject twin = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", path));
        boolean guessed = false;
        for (String key : needed) {
            if (json.has(key)) { continue; }
            if (twin != null && twin.has(key)) { json.add(key, twin.get(key).deepCopy()); }
            else {
                json.add(key, "item_model_index".equals(key) ? new JsonPrimitive(0.0F) : new JsonPrimitive("minecraft:air"));
                guessed = true;
            }
        }
        if (guessed) { note.accept("'" + file + "' names no " + needed.get(0) + ", which " + Port.Line.running().title() + " needs; it is read with minecraft:air, so no item uses it until one is named by hand"); }
    }

    static void wolf(JsonObject json, String file, Consumer<String> note) {
        if (!json.has("assets") || !json.get("assets").isJsonObject()) { return; }
        JsonObject assets = json.remove("assets").getAsJsonObject();
        for (String look : List.of("wild", "tame", "angry")) {
            if (assets.has(look)) { json.add(look + "_texture", assets.get(look)); }
        }
        JsonElement baby = json.remove("baby_assets");
        JsonElement conditions = json.remove("spawn_conditions");
        JsonArray biomes = new JsonArray();
        JsonElement single = null;
        int named = 0;
        if (conditions != null && conditions.isJsonArray()) {
            for (JsonElement selector : conditions.getAsJsonArray()) {
                JsonObject condition = selector.isJsonObject() && selector.getAsJsonObject().has("condition") && selector.getAsJsonObject().get("condition").isJsonObject() ? selector.getAsJsonObject().getAsJsonObject("condition") : null;
                if (condition == null || !"minecraft:biome".equals(DownFixes.namespaced(DownFixes.string(condition, "type"))) || !condition.has("biomes")) { continue; }
                JsonElement held = condition.get("biomes");
                named++;
                single = held;
                if (held.isJsonArray()) { held.getAsJsonArray().forEach(biomes::add); }
                else if (!held.getAsString().startsWith("#")) { biomes.add(held); }
            }
        }
        json.add("biomes", named == 1 ? single : biomes);
        if (baby != null && !baby.equals(assets)) { note.accept("'" + file + "' gives pups their own textures, which " + Port.Line.running().title() + " does not have, so its pups look like its grown wolves"); }
    }
}
