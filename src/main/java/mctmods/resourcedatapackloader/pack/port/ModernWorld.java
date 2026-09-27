package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

final class ModernWorld {
    private static final String VISUAL = "minecraft:visual/";
    private static final String AUDIO = "minecraft:audio/";
    private static final String GAMEPLAY = "minecraft:gameplay/";
    private static final String ATTRIBUTES = "attributes";
    private static final String EFFECTS = "effects";
    private static final String FIXED_TIME = "fixed_time";
    private static final String DRAGON_FIGHT = "has_ender_dragon_fight";
    private static final String END_TYPE = "data/minecraft/dimension_type/the_end.json";
    private static final String CONFIG = "config";
    private static final String BELOW_TRUNK = "below_trunk_provider";
    private static final List<String> MUSHROOMS = List.of("minecraft:huge_red_mushroom", "minecraft:huge_brown_mushroom");
    private static final int FULL_SKY_LIGHT = 15;
    private static final List<String> OLD_TYPE_FIELDS = List.of(EFFECTS, "ultrawarm", "natural", "bed_works", "respawn_anchor_works", "piglin_safe", "has_raids", FIXED_TIME);
    private static final List<String> TEMPLATE_FIELDS = List.of("skybox", "cardinal_light");
    private static final List<String> BIOME_COLORS = List.of("fog_color", "sky_color", "water_fog_color");
    private static final List<String> PATCHES = List.of("minecraft:random_patch", "minecraft:flower", "minecraft:no_bonemeal_flower");

    private ModernWorld() {}

    static void dimensionType(JsonObject json, String file, Consumer<String> note) {
        if (!json.has(DRAGON_FIGHT)) { json.addProperty(DRAGON_FIGHT, END_TYPE.equals(file)); }
        if (json.has(ATTRIBUTES) || OLD_TYPE_FIELDS.stream().noneMatch(json::has)) { return; }
        String effects = json.has(EFFECTS) && json.get(EFFECTS).isJsonPrimitive() ? json.get(EFFECTS).getAsString() : "minecraft:overworld";
        String base = "minecraft:the_nether".equals(effects) ? "the_nether" : "minecraft:the_end".equals(effects) ? "the_end" : "overworld";
        JsonObject attributes = new JsonObject();
        JsonObject template = GameData.json(Identifier.fromNamespaceAndPath("minecraft", "dimension_type/" + base + ".json"));
        if (template != null && template.has(ATTRIBUTES) && template.get(ATTRIBUTES).isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : template.getAsJsonObject(ATTRIBUTES).entrySet()) {
                if (entry.getKey().startsWith(VISUAL) || entry.getKey().startsWith(AUDIO)) { attributes.add(entry.getKey(), entry.getValue().deepCopy()); }
            }
            attributes.remove(VISUAL + "default_dripstone_particle");
            for (String field : TEMPLATE_FIELDS) {
                if (template.has(field)) { json.add(field, template.get(field).deepCopy()); }
            }
        }
        else { note.accept("'" + file + "' uses the " + effects + " sky, whose look could not be read from the game, so it keeps the default look"); }
        boolean warm = flag(json, "ultrawarm");
        boolean natural = flag(json, "natural");
        long fixed = json.has(FIXED_TIME) && json.get(FIXED_TIME).isJsonPrimitive() ? json.get(FIXED_TIME).getAsLong() : -1L;
        attributes.addProperty(GAMEPLAY + "water_evaporates", warm);
        attributes.addProperty(GAMEPLAY + "fast_lava", warm);
        if (warm) {
            JsonObject drip = new JsonObject();
            drip.addProperty("type", "minecraft:dripping_dripstone_lava");
            attributes.add(VISUAL + "default_dripstone_particle", drip);
        }
        attributes.addProperty(GAMEPLAY + "nether_portal_spawns_piglin", natural);
        attributes.addProperty(GAMEPLAY + "respawn_anchor_works", flag(json, "respawn_anchor_works"));
        if (flag(json, "piglin_safe")) { attributes.addProperty(GAMEPLAY + "piglins_zombify", false); }
        if (json.has("has_raids") && !flag(json, "has_raids")) { attributes.addProperty(GAMEPLAY + "can_start_raid", false); }
        attributes.add(GAMEPLAY + "bed_rule", ContentDimensions.bedRule(flag(json, "bed_works"), natural, fixed >= 0));
        if (fixed >= 0) {
            int light = skyLight(fixed);
            if (light < FULL_SKY_LIGHT) { attributes.addProperty(GAMEPLAY + "sky_light_level", (float) light); }
            json.addProperty("has_fixed_time", true);
            json.addProperty("timelines", "#minecraft:universal");
            note.accept("'" + file + "' has the fixed time " + fixed + ": it became a dimension with no day cycle and sky light " + light + ", as this version writes the nether");
        }
        else {
            json.addProperty("default_clock", "minecraft:overworld");
            json.addProperty("timelines", "#minecraft:in_overworld");
        }
        OLD_TYPE_FIELDS.forEach(json::remove);
        json.add(ATTRIBUTES, attributes);
    }

    private static boolean flag(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsBoolean(); }

    private static int skyLight(long time) {
        double turn = (time / 24000.0 - 0.25) - Math.floor(time / 24000.0 - 0.25);
        double day = (turn * 2.0 + (0.5 - Math.cos(turn * Math.PI) / 2.0)) / 3.0;
        double dark = 1.0 - Math.clamp(1.0 - (Math.cos(day * Math.PI * 2.0) * 2.0 + 0.5), 0.0, 1.0);
        return FULL_SKY_LIGHT - (int) ((1.0 - dark) * 11.0);
    }

    static void biome(JsonObject json, String file, Consumer<String> note) {
        if (json.has("carvers") && json.get("carvers").isJsonObject()) { json.add("carvers", carvers(json.getAsJsonObject("carvers"), file, note)); }
        JsonObject effects = json.has(EFFECTS) && json.get(EFFECTS).isJsonObject() ? json.getAsJsonObject(EFFECTS) : null;
        if (effects == null) { return; }
        JsonObject attributes = json.has(ATTRIBUTES) && json.get(ATTRIBUTES).isJsonObject() ? json.getAsJsonObject(ATTRIBUTES) : new JsonObject();
        for (String named : BIOME_COLORS) {
            JsonElement held = effects.remove(named);
            if (held != null) { attributes.add(VISUAL + named, color(held)); }
        }
        JsonElement particle = effects.remove("particle");
        if (particle != null && particle.isJsonObject()) {
            JsonObject one = new JsonObject();
            one.add("particle", particle.getAsJsonObject().get("options"));
            one.add("probability", particle.getAsJsonObject().get("probability"));
            JsonArray particles = new JsonArray();
            particles.add(one);
            attributes.add(VISUAL + "ambient_particles", particles);
        }
        JsonObject sounds = new JsonObject();
        move(effects, "ambient_sound", sounds, "loop");
        move(effects, "mood_sound", sounds, "mood");
        move(effects, "additions_sound", sounds, "additions");
        if (!sounds.isEmpty()) { attributes.add(AUDIO + "ambient_sounds", sounds); }
        JsonElement music = effects.remove("music");
        JsonObject playing = music == null ? null : music.isJsonArray() && !music.getAsJsonArray().isEmpty() ? data(music.getAsJsonArray().get(0)) : music.isJsonObject() ? music.getAsJsonObject() : null;
        if (playing != null) {
            JsonObject track = playing.deepCopy();
            if (track.has("replace_current_music") && !track.get("replace_current_music").getAsBoolean()) { track.remove("replace_current_music"); }
            JsonObject background = new JsonObject();
            background.add("default", track);
            attributes.add(AUDIO + "background_music", background);
        }
        move(effects, "music_volume", attributes, AUDIO + "music_volume");
        for (String tint : List.of("water_color", "foliage_color", "grass_color")) {
            if (effects.has(tint)) { effects.add(tint, color(effects.get(tint))); }
        }
        if (!attributes.isEmpty()) { json.add(ATTRIBUTES, attributes); }
    }

    @Nullable private static JsonObject data(JsonElement weighted) {
        if (!weighted.isJsonObject()) { return null; }
        JsonObject held = weighted.getAsJsonObject();
        return held.has("data") && held.get("data").isJsonObject() ? held.getAsJsonObject("data") : held;
    }

    private static void move(JsonObject from, String key, JsonObject to, String named) {
        JsonElement held = from.remove(key);
        if (held != null) { to.add(named, held); }
    }

    private static JsonElement color(JsonElement held) {
        if (!held.isJsonPrimitive() || !held.getAsJsonPrimitive().isNumber()) { return held; }
        return new JsonPrimitive(String.format("#%06x", held.getAsInt() & 0xFFFFFF));
    }

    private static JsonElement carvers(JsonObject steps, String file, Consumer<String> note) {
        JsonArray all = new JsonArray();
        String tag = null;
        int filled = 0;
        for (JsonElement step : steps.asMap().values()) {
            if (step.isJsonArray()) { step.getAsJsonArray().forEach(all::add); }
            else if (step.isJsonPrimitive() && step.getAsString().startsWith("#")) {
                tag = step.getAsString();
                filled++;
            }
            else if (step.isJsonPrimitive()) { all.add(step); }
        }
        if (tag != null && filled == 1 && all.isEmpty()) { return new JsonPrimitive(tag); }
        if (tag != null) { note.accept("'" + file + "' names its carvers by a tag and by id for different carving steps, which is one list now; the tag " + tag + " is left out, so list its carvers by hand"); }
        return all.size() == 1 ? all.get(0) : all;
    }

    static void feature(JsonObject json, String file, Consumer<String> note) {
        String type = json.has("type") && json.get("type").isJsonPrimitive() ? json.get("type").getAsString() : "";
        JsonObject config = json.has(CONFIG) && json.get(CONFIG).isJsonObject() ? json.getAsJsonObject(CONFIG) : null;
        if (config == null) { return; }
        if ("minecraft:ice_spike".equals(type) || "minecraft:forest_rock".equals(type)) {
            replaced(json, config, type.substring(ModernFixes.MINECRAFT.length()), file, note);
            return;
        }
        if (MUSHROOMS.contains(type) && !config.has("can_place_on")) {
            JsonObject vanilla = vanillaFeature(type.substring(ModernFixes.MINECRAFT.length()));
            if (vanilla != null && vanilla.getAsJsonObject(CONFIG).has("can_place_on")) { config.add("can_place_on", vanilla.getAsJsonObject(CONFIG).get("can_place_on").deepCopy()); }
            else { note.accept("'" + file + "' is a " + type + ", which now names the blocks it grows on, and the game's own could not be read; add \"can_place_on\" by hand"); }
            return;
        }
        if ("minecraft:tree".equals(type)) {
            tree(config);
            return;
        }
        if (!PATCHES.contains(type) || !config.has("feature")) { return; }
        JsonElement inner = config.get("feature");
        JsonObject placed = inner.isJsonObject() ? inner.getAsJsonObject().deepCopy() : null;
        if (placed == null) {
            note.accept("'" + file + "' is a " + type + " naming its placed feature " + inner + ", which this version has no patch feature for; it is served as written");
            return;
        }
        JsonArray placement = new JsonArray();
        JsonObject count = new JsonObject();
        count.addProperty("type", "minecraft:count");
        count.add("count", config.has("tries") ? config.get("tries") : new JsonPrimitive(128));
        placement.add(count);
        JsonObject offset = new JsonObject();
        offset.addProperty("type", "minecraft:random_offset");
        offset.add("xz_spread", spread(config.has("xz_spread") ? config.get("xz_spread").getAsInt() : 7));
        offset.add("y_spread", spread(config.has("y_spread") ? config.get("y_spread").getAsInt() : 3));
        placement.add(offset);
        if (placed.has("placement") && placed.get("placement").isJsonArray()) { placed.getAsJsonArray("placement").forEach(placement::add); }
        placed.add("placement", placement);
        JsonObject selector = new JsonObject();
        selector.add("features", new JsonArray());
        selector.add("default", placed);
        json.addProperty("type", "minecraft:random_selector");
        json.add(CONFIG, selector);
    }

    private static void tree(JsonObject config) {
        JsonElement dirt = config.remove("dirt_provider");
        boolean forced = flag(config, "force_dirt");
        config.remove("force_dirt");
        if (config.has(BELOW_TRUNK)) { return; }
        JsonElement place = dirt != null && dirt.isJsonObject() ? dirt : WorldgenJson.simpleState("minecraft:dirt");
        config.add(BELOW_TRUNK, forced ? place : WorldgenJson.belowTrunk(place));
    }

    private static void replaced(JsonObject json, JsonObject config, String name, String file, Consumer<String> note) {
        JsonObject vanilla = vanillaFeature(name);
        if (vanilla == null) {
            note.accept("'" + file + "' is a minecraft:" + name + ", a feature this version builds another way, and the game's own could not be read; it is served as written");
            return;
        }
        JsonObject out = vanilla.getAsJsonObject(CONFIG).deepCopy();
        if (config.has("state")) { out.add("state", config.get("state")); }
        json.add("type", vanilla.get("type"));
        json.add(CONFIG, out);
    }

    @Nullable private static JsonObject vanillaFeature(String name) {
        JsonObject vanilla = GameData.json(Identifier.fromNamespaceAndPath("minecraft", "worldgen/configured_feature/" + name + ".json"));
        return vanilla != null && vanilla.has("type") && vanilla.has(CONFIG) && vanilla.get(CONFIG).isJsonObject() ? vanilla : null;
    }

    static void wolf(JsonObject json, String file, Consumer<String> note) {
        if (json.has("assets") || !json.has("wild_texture")) { return; }
        JsonObject assets = new JsonObject();
        for (String look : List.of("wild", "tame", "angry")) {
            JsonElement texture = json.remove(look + "_texture");
            if (texture != null) { assets.add(look, texture); }
        }
        json.add("assets", assets);
        JsonObject pale = GameData.json(Identifier.fromNamespaceAndPath("minecraft", "wolf_variant/pale.json"));
        boolean plain = pale != null && pale.has("baby_assets") && pale.get("baby_assets").isJsonObject();
        json.add("baby_assets", plain ? pale.get("baby_assets").deepCopy() : assets.deepCopy());
        note.accept("'" + file + "' is a wolf variant, and this version gives pups their own textures, which the pack has none of, so its pups look like " + (plain ? "plain wolf pups" : "its grown wolves"));
        JsonElement biomes = json.remove("biomes");
        JsonArray conditions = new JsonArray();
        if (biomes != null && !(biomes.isJsonArray() && biomes.getAsJsonArray().isEmpty())) {
            JsonObject biome = new JsonObject();
            biome.addProperty("type", "minecraft:biome");
            biome.add("biomes", biomes);
            JsonObject selector = new JsonObject();
            selector.add("condition", biome);
            selector.addProperty("priority", 1);
            conditions.add(selector);
        }
        json.add("spawn_conditions", conditions);
    }

    private static JsonObject spread(int spread) {
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "minecraft:trapezoid");
        provider.addProperty("min", -spread);
        provider.addProperty("max", spread);
        provider.addProperty("plateau", 0);
        return provider;
    }

    static void trim(JsonObject json, boolean material) {
        if (material) {
            json.remove("ingredient");
            json.remove("item_model_index");
            if (json.has("override_armor_materials")) { json.add("override_armor_assets", json.remove("override_armor_materials")); }
            return;
        }
        json.remove("template_item");
    }
}
