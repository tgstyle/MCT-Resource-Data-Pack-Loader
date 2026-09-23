package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class Ids {
    static final String MINECRAFT = "minecraft";
    private static final String TABLE = "/assets/resourcedatapackloader/port/flattening.json";
    private static final String ENTITY_TABLES = "entities/";
    private static final Map<String, List<Legacy>> BLOCKS = new HashMap<>();
    private static final Map<String, List<Legacy>> ITEMS = new HashMap<>();
    private static final Map<String, String> SPAWN_EGGS = new HashMap<>();
    private static final Map<String, String> ENTITIES = new HashMap<>();
    private static final Map<String, String> BIOMES = new HashMap<>();
    private static final Set<String> VARIED_BLOCKS = new HashSet<>();
    private static final Set<String> VARIED_ITEMS = new HashSet<>();
    private static final Set<String> LEGACY_BLOCKS = new HashSet<>();
    private static final Set<String> LEGACY_ITEMS = new HashSet<>();
    private static final Map<String, String> LOOT_TABLES = new HashMap<>();
    private static final Map<String, String> ORE_WHOLE = new HashMap<>();
    private static final Map<String, String> ORE_PREFIXES = new HashMap<>();
    private static final Map<String, String> PARTICLES = new LinkedHashMap<>();
    private static final Map<String, String> SOUNDS = new HashMap<>();
    private static final String[][] SOUND_PREFIXES = {
            {"block.wool.", "block.cloth."}, {"block.ender_chest.", "block.enderchest."}, {"block.metal_pressure_plate.", "block.metal_pressureplate."},
            {"block.note_block.", "block.note."}, {"block.slime_block.", "block.slime."}, {"block.stone_pressure_plate.", "block.stone_pressureplate."},
            {"block.lily_pad.", "block.waterlily."}, {"block.wooden_button.", "block.wood_button."}, {"block.wooden_pressure_plate.", "block.wood_pressureplate."},
            {"entity.armor_stand.", "entity.armorstand."}, {"entity.fishing_bobber.", "entity.bobber."}, {"entity.dragon_fireball.", "entity.enderdragon_fireball."},
            {"entity.ender_dragon.", "entity.enderdragon."}, {"entity.ender_eye.", "entity.endereye."}, {"entity.enderman.", "entity.endermen."},
            {"entity.ender_pearl.", "entity.enderpearl."}, {"entity.evoker_fangs.", "entity.evocation_fangs."}, {"entity.evoker.", "entity.evocation_illager."},
            {"entity.firework_rocket.", "entity.firework."}, {"entity.illusioner.", "entity.illusion_illager."}, {"entity.iron_golem.", "entity.irongolem."},
            {"entity.item_frame.", "entity.itemframe."}, {"entity.leash_knot.", "entity.leashknot."}, {"entity.lightning_bolt.", "entity.lightning."},
            {"entity.lingering_potion.", "entity.lingeringpotion."}, {"entity.snow_golem.", "entity.snowman."},
            {"entity.vindicator.", "entity.vindication_illager."}, {"entity.zombified_piglin.", "entity.zombie_pig."}, {"music_disc.", "record."},
            {"entity.parrot.imitate.ender_dragon", "entity.parrot.imitate.enderdragon"}, {"entity.parrot.imitate.evoker", "entity.parrot.imitate.evocation_illager"},
            {"entity.parrot.imitate.illusioner", "entity.parrot.imitate.illusion_illager"}, {"entity.parrot.imitate.magma_cube", "entity.parrot.imitate.magmacube"},
            {"entity.parrot.imitate.vindicator", "entity.parrot.imitate.vindication_illager"}};
    private static boolean loaded;

    static {
        LOOT_TABLES.put("chests/village/village_weaponsmith", "chests/village_blacksmith");
        LOOT_TABLES.put("entities/sheep/light_gray", "entities/sheep/silver");
        String[][] whole = {{"minecraft:logs", "logWood"}, {"minecraft:planks", "plankWood"}, {"rods/wooden", "stickWood"}, {"minecraft:wooden_slabs", "slabWood"},
                {"minecraft:wooden_stairs", "stairWood"}, {"minecraft:wooden_fences", "fenceWood"}, {"minecraft:fence_gates", "fenceGateWood"}, {"minecraft:wooden_doors", "doorWood"},
                {"minecraft:wooden_trapdoors", "trapdoorWood"}, {"minecraft:saplings", "treeSapling"}, {"minecraft:leaves", "treeLeaves"}, {"stone", "stone"},
                {"cobblestone", "cobblestone"}, {"minecraft:sand", "sand"}, {"sandstone", "sandstone"}, {"glass", "blockGlass"}, {"glass_panes", "paneGlass"},
                {"minecraft:wool", "wool"}, {"string", "string"}, {"feathers", "feather"}, {"leather", "leather"}, {"eggs", "egg"}, {"bones", "bone"},
                {"slimeballs", "slimeball"}, {"ender_pearls", "enderpearl"}, {"gunpowder", "gunpowder"}, {"nether_stars", "netherStar"}, {"obsidian", "obsidian"},
                {"minecraft:music_discs", "record"}, {"bookshelves", "bookshelf"}, {"chests", "chest"}, {"workbenches", "workbench"}, {"torches", "torch"},
                {"glowstone", "glowstone"}, {"netherrack", "netherrack"}, {"end_stones", "endstone"}, {"gravel", "gravel"}, {"minecraft:dirt", "dirt"},
                {"grass", "grass"}, {"ice", "ice"}, {"storage_blocks/coal", "blockCoal"}, {"storage_blocks/redstone", "blockRedstone"}};
        for (String[] pair : whole) { ORE_WHOLE.put(pair[0], pair[1]); }
        String[][] prefixes = {{"ores", "ore"}, {"ingots", "ingot"}, {"gems", "gem"}, {"dusts", "dust"}, {"nuggets", "nugget"}, {"storage_blocks", "block"},
                {"plates", "plate"}, {"rods", "rod"}, {"gears", "gear"}, {"dyes", "dye"}, {"seeds", "seed"}, {"crops", "crop"}, {"foods", "food"},
                {"slabs", "slab"}, {"stairs", "stair"}, {"fences", "fence"}, {"wires", "wire"}, {"coins", "coin"}, {"shards", "shard"},
                {"raw_materials", "raw"}, {"clusters", "cluster"}, {"clumps", "clump"}, {"crystals", "crystal"}};
        for (String[] pair : prefixes) { ORE_PREFIXES.put(pair[0], pair[1]); }
        String[][] particles = {{"poof", "explode"}, {"explosion", "largeexplode"}, {"explosion_emitter", "hugeexplosion"}, {"firework", "fireworksSpark"},
                {"bubble", "bubble"}, {"splash", "splash"}, {"fishing", "wake"}, {"underwater", "suspended"}, {"crit", "crit"}, {"enchanted_hit", "magicCrit"},
                {"smoke", "smoke"}, {"large_smoke", "largesmoke"}, {"effect", "spell"}, {"instant_effect", "instantSpell"}, {"witch", "witchMagic"},
                {"dripping_water", "dripWater"}, {"dripping_lava", "dripLava"}, {"angry_villager", "angryVillager"}, {"happy_villager", "happyVillager"},
                {"mycelium", "townaura"}, {"note", "note"}, {"portal", "portal"}, {"enchant", "enchantmenttable"}, {"flame", "flame"}, {"lava", "lava"},
                {"cloud", "cloud"}, {"item_snowball", "snowballpoof"}, {"item_slime", "slime"}, {"heart", "heart"}, {"rain", "droplet"},
                {"elder_guardian", "mobappearance"}, {"dragon_breath", "dragonbreath"}, {"end_rod", "endRod"}, {"damage_indicator", "damageIndicator"},
                {"sweep_attack", "sweepAttack"}, {"totem_of_undying", "totem"}, {"spit", "spit"}, {"entity_effect", "mobSpell"}, {"ambient_entity_effect", "mobSpellAmbient"},
                {"dust", "reddust"}, {"item", "iconcrack"}, {"block", "blockcrack"}, {"falling_dust", "fallingdust"}};
        for (String[] pair : particles) { PARTICLES.put(pair[0], pair[1]); }
        SOUNDS.put("entity.polar_bear.ambient_baby", "entity.polar_bear.baby_ambient");
        SOUNDS.put("entity.villager.trade", "entity.villager.trading");
        SOUNDS.put("entity.zombie.attack_wooden_door", "entity.zombie.attack_door_wood");
        SOUNDS.put("entity.zombie.break_wooden_door", "entity.zombie.break_door_wood");
        SOUNDS.put("music.nether.nether_wastes", "music.nether");
    }

    private Ids() {}

    static final class Legacy {
        final String id;
        final int meta;
        final Map<String, String> properties;
        final Map<String, String> legacyProperties;
        final boolean lossy;

        Legacy(String id, int meta, Map<String, String> properties, Map<String, String> legacyProperties, boolean lossy) {
            this.id = id;
            this.meta = meta;
            this.properties = properties;
            this.legacyProperties = legacyProperties;
            this.lossy = lossy;
        }

        Legacy lossy() { return new Legacy(id, meta, properties, legacyProperties, true); }
    }

    static boolean vanilla(String name) { return name.startsWith(MINECRAFT + ":") || name.indexOf(':') < 0; }

    static String namespaced(String name) {
        String trimmed = name.trim();
        return trimmed.indexOf(':') < 0 ? MINECRAFT + ":" + trimmed : trimmed;
    }

    private static synchronized void load() {
        if (loaded) { return; }
        loaded = true;
        try (InputStream stream = Ids.class.getResourceAsStream(TABLE)) {
            if (stream == null) {
                ContentLog.LOGGER.error("The flattening table {} is missing from the jar, so vanilla ids in modern packs are left as written", TABLE);
                return;
            }
            JsonObject table = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            candidates(table, "blocks", BLOCKS);
            candidates(table, "items", ITEMS);
            names(table, "spawnEggs", SPAWN_EGGS);
            names(table, "entities", ENTITIES);
            names(table, "biomes", BIOMES);
            list(table, "legacyBlocks", LEGACY_BLOCKS);
            list(table, "legacyItems", LEGACY_ITEMS);
            varied(BLOCKS, VARIED_BLOCKS);
            varied(ITEMS, VARIED_ITEMS);
        }
        catch (Exception failed) { ContentLog.LOGGER.error("Could not read the flattening table {}, so vanilla ids in modern packs are left as written", TABLE, failed); }
    }

    private static void varied(Map<String, List<Legacy>> table, Set<String> into) {
        Map<String, Set<String>> names = new HashMap<>();
        for (Map.Entry<String, List<Legacy>> entry : table.entrySet()) {
            for (Legacy one : entry.getValue()) { names.computeIfAbsent(one.id, k -> new HashSet<>()).add(entry.getKey()); }
        }
        for (Map.Entry<String, Set<String>> entry : names.entrySet()) {
            Set<String> held = entry.getValue();
            held.add(entry.getKey());
            if (held.size() > 1) { into.add(entry.getKey()); }
        }
    }

    private static void candidates(JsonObject table, String section, Map<String, List<Legacy>> into) {
        if (!table.has(section) || !table.get(section).isJsonObject()) { return; }
        for (Map.Entry<String, JsonElement> entry : table.getAsJsonObject(section).entrySet()) {
            List<Legacy> list = new ArrayList<>();
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                JsonObject held = element.getAsJsonObject();
                list.add(new Legacy(held.get("legacy").getAsString(), held.has("meta") ? held.get("meta").getAsInt() : 0, strings(held, "properties"), strings(held, "legacyProperties"), false));
            }
            if (!list.isEmpty()) { into.put(entry.getKey(), list); }
        }
    }

    private static Map<String, String> strings(JsonObject held, String key) {
        if (!held.has(key) || !held.get(key).isJsonObject()) { return Collections.emptyMap(); }
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : held.getAsJsonObject(key).entrySet()) { out.put(entry.getKey(), entry.getValue().getAsString()); }
        return out;
    }

    private static void names(JsonObject table, String section, Map<String, String> into) {
        if (!table.has(section) || !table.get(section).isJsonObject()) { return; }
        for (Map.Entry<String, JsonElement> entry : table.getAsJsonObject(section).entrySet()) { into.put(entry.getKey(), entry.getValue().getAsString()); }
    }

    private static void list(JsonObject table, String section, Set<String> into) {
        if (!table.has(section) || !table.get(section).isJsonArray()) { return; }
        for (JsonElement element : table.getAsJsonArray(section)) { into.add(element.getAsString()); }
    }

    static boolean unknownBlock(String name) {
        load();
        return !LEGACY_BLOCKS.contains(namespaced(name));
    }

    static boolean legacyItem(String name) {
        load();
        return LEGACY_ITEMS.contains(namespaced(name)) || LEGACY_BLOCKS.contains(namespaced(name));
    }

    @Nullable static Legacy block(String name, Map<String, String> properties) {
        load();
        List<Legacy> list = BLOCKS.get(namespaced(name));
        return list == null ? null : pick(list, properties);
    }

    @Nullable static Legacy item(String name) {
        load();
        List<Legacy> list = ITEMS.get(namespaced(name));
        if (list != null) { return list.get(0); }
        if (legacyItem(name)) { return null; }
        list = BLOCKS.get(namespaced(name));
        return list == null ? null : list.get(0);
    }

    static boolean variedBlock(String legacyId) {
        load();
        return VARIED_BLOCKS.contains(legacyId);
    }

    static boolean variedItem(String legacyId) {
        load();
        return VARIED_ITEMS.contains(legacyId);
    }

    @Nullable static String spawnEgg(String name) {
        load();
        return SPAWN_EGGS.get(namespaced(name));
    }

    private static Legacy pick(List<Legacy> list, Map<String, String> properties) {
        if (properties.isEmpty()) { return list.get(0); }
        Legacy best = null;
        int bestScore = -1;
        for (Legacy one : list) {
            int score = 0;
            boolean clash = false;
            for (Map.Entry<String, String> wanted : properties.entrySet()) {
                String held = one.properties.get(wanted.getKey());
                if (held == null) { continue; }
                if (held.equals(wanted.getValue())) { score++; }
                else { clash = true; }
            }
            if (!clash && score > bestScore) {
                best = one;
                bestScore = score;
            }
        }
        return best == null ? list.get(0).lossy() : best;
    }

    static String entity(String name) {
        load();
        String id = namespaced(name.toLowerCase(Locale.ROOT));
        String found = ENTITIES.get(id);
        return found == null ? id : found;
    }

    static String biome(String name) {
        load();
        String id = namespaced(name.toLowerCase(Locale.ROOT));
        String found = BIOMES.get(id);
        return found == null ? id : found;
    }

    static String lootTable(String name) {
        String asked = namespaced(name.trim().toLowerCase(Locale.ROOT));
        if (!asked.startsWith(MINECRAFT + ":")) { return asked; }
        String path = asked.substring(MINECRAFT.length() + 1);
        String renamed = LOOT_TABLES.get(path);
        if (renamed != null) { return MINECRAFT + ":" + renamed; }
        if (!path.startsWith(ENTITY_TABLES)) { return asked; }
        String rest = path.substring(ENTITY_TABLES.length());
        int slash = rest.indexOf('/');
        String entity = entity(slash < 0 ? rest : rest.substring(0, slash));
        return MINECRAFT + ":" + ENTITY_TABLES + entity.substring(entity.indexOf(':') + 1) + (slash < 0 ? "" : rest.substring(slash));
    }

    static String attribute(String name) {
        String trimmed = name.trim();
        if (trimmed.startsWith(MINECRAFT + ":")) { trimmed = trimmed.substring(MINECRAFT.length() + 1); }
        int dot = trimmed.indexOf('.');
        if (dot < 0) { return trimmed; }
        return trimmed.substring(0, dot + 1) + camel(trimmed.substring(dot + 1), false);
    }

    static String enchantment(String name) {
        String id = namespaced(name.toLowerCase(Locale.ROOT));
        return "minecraft:sweeping_edge".equals(id) ? "minecraft:sweeping" : id;
    }

    static String sound(String given) {
        String name = given.trim();
        String namespace = "";
        if (name.startsWith(MINECRAFT + ":")) {
            namespace = MINECRAFT + ":";
            name = name.substring(namespace.length());
        }
        else if (name.indexOf(':') >= 0) { return given; }
        String exact = SOUNDS.get(name);
        if (exact != null) { return namespace + exact; }
        if (name.endsWith("_small") && (name.startsWith("entity.magma_cube.") || name.startsWith("entity.slime."))) {
            String bare = name.substring(0, name.length() - "_small".length());
            return namespace + (bare.startsWith("entity.slime.") ? "entity.small_slime." + bare.substring("entity.slime.".length()) : "entity.small_magmacube." + bare.substring("entity.magma_cube.".length()));
        }
        if (name.startsWith("entity.magma_cube.")) { return namespace + "entity.magmacube." + name.substring("entity.magma_cube.".length()); }
        for (String[] prefix : SOUND_PREFIXES) {
            if (name.startsWith(prefix[0])) { return namespace + prefix[1] + name.substring(prefix[0].length()); }
        }
        return given;
    }

    @Nullable static String particle(String given) {
        String name = given.trim();
        if (name.startsWith(MINECRAFT + ":")) { name = name.substring(MINECRAFT.length() + 1); }
        return PARTICLES.get(name);
    }

    @Nullable static String oreName(String tag) {
        String id = namespaced(tag.trim().startsWith("#") ? tag.trim().substring(1) : tag.trim());
        String namespace = id.substring(0, id.indexOf(':'));
        String path = id.substring(id.indexOf(':') + 1);
        String whole = ORE_WHOLE.get(id);
        if (whole != null) { return whole; }
        boolean convention = "forge".equals(namespace) || "c".equals(namespace);
        if (!convention) { return null; }
        whole = ORE_WHOLE.get(path);
        if (whole != null) { return whole; }
        int slash = path.indexOf('/');
        if (slash > 0) {
            String prefix = ORE_PREFIXES.get(path.substring(0, slash));
            String rest = path.substring(slash + 1).replace('/', '_');
            return prefix == null ? camel(path.replace('/', '_'), false) : prefix + camel(rest, true);
        }
        return camel(path, false);
    }

    static String camel(String snake, boolean capitalFirst) {
        StringBuilder out = new StringBuilder();
        boolean upper = capitalFirst;
        for (int i = 0; i < snake.length(); i++) {
            char c = snake.charAt(i);
            if (c == '_') {
                upper = true;
                continue;
            }
            out.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return out.toString();
    }
}
