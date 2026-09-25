package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.ContentLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class CrossIds {
    static final int DATA_1_20 = 3465;
    static final String MINECRAFT = "minecraft:";
    static final String FORGE = "forge";
    static final String NEOFORGE = "neoforge";
    static final String COMMON = "c";
    static final String BOTH = "+";
    static final String ITEM_REGISTRY = "item";
    private static final String TABLE = "/assets/resourcedatapackloader/port/forge_tags.txt";
    private static final List<String> REGISTRIES = List.of("worldgen/biome", "entity_type", "block", "item", "fluid");
    private static final Map<String, String> RENAMED = Map.ofEntries(
            Map.entry("minecraft:grass", "minecraft:short_grass"), Map.entry("minecraft:scute", "minecraft:turtle_scute"), Map.entry("minecraft:sweeping", "minecraft:sweeping_edge"),
            Map.entry("minecraft:horse.jump_strength", "minecraft:generic.jump_strength"), Map.entry("forge:block_reach", "minecraft:player.block_interaction_range"),
            Map.entry("forge:entity_reach", "minecraft:player.entity_interaction_range"), Map.entry("forge:entity_gravity", "minecraft:generic.gravity"),
            Map.entry("forge:swim_speed", "neoforge:swim_speed"), Map.entry("forge:nametag_distance", "neoforge:nametag_distance"));
    private static final Map<String, String> RENAMED_BACK = back();
    private static final Set<String> LOADER = Set.of("add_features", "remove_features", "add_spawns", "remove_spawns",
            "any", "and", "or", "not", "mod_loaded", "item_exists", "tag_empty", "true", "false", "compound", "difference", "intersection");
    private static final Set<String> ONLY_1_21 = Set.of(
            "chiseled_copper", "chiseled_tuff", "chiseled_tuff_bricks", "copper_bulb", "copper_door", "copper_grate", "copper_trapdoor", "crafter", "exposed_chiseled_copper", "exposed_copper_bulb",
            "exposed_copper_door", "exposed_copper_grate", "exposed_copper_trapdoor", "heavy_core", "oxidized_chiseled_copper", "oxidized_copper_bulb", "oxidized_copper_door", "oxidized_copper_grate",
            "oxidized_copper_trapdoor", "polished_tuff", "polished_tuff_slab", "polished_tuff_stairs", "polished_tuff_wall", "trial_spawner", "tuff_bricks", "tuff_brick_slab", "tuff_brick_stairs",
            "tuff_brick_wall", "tuff_slab", "tuff_stairs", "tuff_wall", "vault", "waxed_chiseled_copper", "waxed_copper_bulb", "waxed_copper_door", "waxed_copper_grate", "waxed_copper_trapdoor",
            "waxed_exposed_chiseled_copper", "waxed_exposed_copper_bulb", "waxed_exposed_copper_door", "waxed_exposed_copper_grate", "waxed_exposed_copper_trapdoor", "waxed_oxidized_chiseled_copper",
            "waxed_oxidized_copper_bulb", "waxed_oxidized_copper_door", "waxed_oxidized_copper_grate", "waxed_oxidized_copper_trapdoor", "waxed_weathered_chiseled_copper", "waxed_weathered_copper_bulb",
            "waxed_weathered_copper_door", "waxed_weathered_copper_grate", "waxed_weathered_copper_trapdoor", "weathered_chiseled_copper", "weathered_copper_bulb", "weathered_copper_door",
            "weathered_copper_grate", "weathered_copper_trapdoor", "armadillo_scute", "armadillo_spawn_egg", "bogged_spawn_egg", "bolt_armor_trim_smithing_template", "breeze_rod", "breeze_spawn_egg",
            "flow_armor_trim_smithing_template", "flow_banner_pattern", "flow_pottery_sherd", "guster_banner_pattern", "guster_pottery_sherd", "mace", "music_disc_creator", "music_disc_creator_music_box",
            "music_disc_precipice", "ominous_bottle", "ominous_trial_key", "scrape_pottery_sherd", "trial_key", "wind_charge", "wolf_armor", "armadillo", "bogged", "breeze", "breeze_wind_charge",
            "ominous_item_spawner", "infested", "oozing", "raid_omen", "trial_omen", "weaving", "wind_charged", "breach", "density", "wind_burst", "trial_chambers");
    private static final Map<String, String> UP = new HashMap<>();
    private static final Map<String, String> DOWN = new HashMap<>();
    private static final Map<String, Integer> DOWN_RANK = new HashMap<>();
    private static boolean loaded;

    private CrossIds() {}

    private static Map<String, String> back() {
        Map<String, String> out = new LinkedHashMap<>();
        RENAMED.forEach((key, value) -> out.put(value, key));
        return out;
    }

    static String id(String id, Port.Line to) {
        if (to == Port.Line.V1_21) {
            String renamed = RENAMED.get(id);
            return renamed != null ? renamed : loader(id, FORGE, NEOFORGE);
        }
        String renamed = RENAMED_BACK.get(id);
        return renamed != null ? renamed : loader(id, NEOFORGE, FORGE);
    }

    private static String loader(String id, String from, String to) {
        int colon = id.indexOf(':');
        if (colon < 0 || !from.equals(id.substring(0, colon)) || !LOADER.contains(id.substring(colon + 1))) { return id; }
        return to + id.substring(colon);
    }

    static boolean missing(String id, Port.Line to) { return to == Port.Line.V1_20 && id.startsWith(MINECRAFT) && ONLY_1_21.contains(id.substring(MINECRAFT.length())); }

    static boolean missingStructure(String name, Port.Line to) {
        String bare = name.startsWith(MINECRAFT) ? name.substring(MINECRAFT.length()) : name;
        return to == Port.Line.V1_20 && "trial_chambers".equals(bare);
    }

    @Nullable static String registry(String singularTagPath) {
        if (!singularTagPath.startsWith("tags/")) { return null; }
        String rest = singularTagPath.substring("tags/".length());
        for (String registry : REGISTRIES) {
            if (rest.startsWith(registry + "/")) { return registry; }
        }
        return null;
    }

    static String tag(@Nullable String registry, String id, Port.Line to) {
        load();
        int colon = id.indexOf(':');
        if (colon < 0) { return id; }
        String namespace = id.substring(0, colon);
        String path = id.substring(colon + 1);
        if (to == Port.Line.V1_21) {
            if (!FORGE.equals(namespace)) { return id; }
            String found = registry == null ? null : UP.get(registry + "|" + id);
            if (found == null) { found = UP.get("|" + id); }
            return found != null ? found : COMMON + ":" + path;
        }
        String found = registry == null ? null : DOWN.get(registry + "|" + id);
        if (found == null) { found = DOWN.get("|" + id); }
        if (found != null) { return found; }
        return COMMON.equals(namespace) || NEOFORGE.equals(namespace) ? FORGE + ":" + path : id;
    }

    private static synchronized void load() {
        if (loaded) { return; }
        loaded = true;
        try (InputStream stream = CrossIds.class.getResourceAsStream(TABLE)) {
            if (stream == null) {
                ContentLog.LOGGER.error("The tag convention table {} is missing from the jar, so forge and c tags are only renamed by namespace", TABLE);
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] parts = line.trim().split(" ");
                if (parts.length == 4) { row(parts[0], FORGE + ":" + parts[1], parts[2], "1".equals(parts[3])); }
            }
        }
        catch (IOException failed) { ContentLog.LOGGER.error("Could not read the tag convention table {}", TABLE, failed); }
    }

    private static void row(String registry, String forge, String target, boolean known) {
        UP.putIfAbsent(registry + "|" + forge, target);
        UP.putIfAbsent("|" + forge, target);
        if (target.contains(BOTH)) { return; }
        int rank = (known ? 2 : 0) + (target.substring(target.indexOf(':') + 1).equals(forge.substring(forge.indexOf(':') + 1)) ? 1 : 0);
        for (String key : new String[] {registry + "|" + target, "|" + target}) {
            if (DOWN_RANK.getOrDefault(key, -1) >= rank) { continue; }
            DOWN.put(key, forge);
            DOWN_RANK.put(key, rank);
        }
    }
}
