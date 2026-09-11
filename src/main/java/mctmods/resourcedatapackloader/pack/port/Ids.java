package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.References;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class Ids {
    public static final int LEGACY = 1343;
    private static final int FLATTENED = 1451;
    private static final String MINECRAFT = "minecraft";
    private static final Map<String, Integer> BLOCK_IDS = new HashMap<>();
    private static final Map<String, String> ITEMS = new ConcurrentHashMap<>();
    private static final Map<String, Block> BLOCKS = new ConcurrentHashMap<>();
    private static final Map<String, String> ENTITIES = new ConcurrentHashMap<>();
    private static final Map<String, String> BIOMES = new ConcurrentHashMap<>();
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private static final Map<String, String> ORE_PREFIXES = Map.ofEntries(
            Map.entry("ore", "ores"), Map.entry("ingot", "ingots"), Map.entry("gem", "gems"), Map.entry("dust", "dusts"), Map.entry("nugget", "nuggets"),
            Map.entry("block", "storage_blocks"), Map.entry("plate", "plates"), Map.entry("rod", "rods"), Map.entry("gear", "gears"), Map.entry("dye", "dyes"),
            Map.entry("seed", "seeds"), Map.entry("crop", "crops"), Map.entry("food", "foods"), Map.entry("slab", "slabs"), Map.entry("stair", "stairs"),
            Map.entry("fence", "fences"), Map.entry("chest", "chests"), Map.entry("wire", "wires"), Map.entry("coin", "coins"), Map.entry("shard", "shards"),
            Map.entry("raw", "raw_materials"), Map.entry("cluster", "clusters"), Map.entry("clump", "clumps"), Map.entry("crystal", "crystals"));
    private static final Map<String, String> ORE_WHOLE = Map.ofEntries(
            Map.entry("logWood", "minecraft:logs"), Map.entry("plankWood", "minecraft:planks"), Map.entry("stickWood", CONVENTION() + ":rods/wooden"), Map.entry("slabWood", "minecraft:wooden_slabs"),
            Map.entry("stairWood", "minecraft:wooden_stairs"), Map.entry("fenceWood", "minecraft:wooden_fences"), Map.entry("fenceGateWood", "minecraft:fence_gates"), Map.entry("doorWood", "minecraft:wooden_doors"),
            Map.entry("trapdoorWood", "minecraft:wooden_trapdoors"), Map.entry("treeSapling", "minecraft:saplings"), Map.entry("treeLeaves", "minecraft:leaves"), Map.entry("stone", CONVENTION() + ":stone"),
            Map.entry("cobblestone", CONVENTION() + ":cobblestone"), Map.entry("sand", "minecraft:sand"), Map.entry("sandstone", CONVENTION() + ":sandstone"), Map.entry("blockGlass", CONVENTION() + ":glass"),
            Map.entry("paneGlass", CONVENTION() + ":glass_panes"), Map.entry("wool", "minecraft:wool"), Map.entry("string", CONVENTION() + ":string"), Map.entry("feather", CONVENTION() + ":feathers"),
            Map.entry("leather", CONVENTION() + ":leather"), Map.entry("egg", CONVENTION() + ":eggs"), Map.entry("bone", CONVENTION() + ":bones"), Map.entry("slimeball", CONVENTION() + ":slimeballs"),
            Map.entry("enderpearl", CONVENTION() + ":ender_pearls"), Map.entry("gunpowder", CONVENTION() + ":gunpowder"), Map.entry("netherStar", CONVENTION() + ":nether_stars"), Map.entry("obsidian", CONVENTION() + ":obsidian"),
            Map.entry("record", "minecraft:music_discs"), Map.entry("bookshelf", CONVENTION() + ":bookshelves"), Map.entry("chest", CONVENTION() + ":chests"), Map.entry("workbench", CONVENTION() + ":workbenches"),
            Map.entry("torch", CONVENTION() + ":torches"), Map.entry("glowstone", CONVENTION() + ":glowstone"), Map.entry("netherrack", CONVENTION() + ":netherrack"), Map.entry("endstone", CONVENTION() + ":end_stones"),
            Map.entry("gravel", CONVENTION() + ":gravel"), Map.entry("dirt", "minecraft:dirt"), Map.entry("grass", CONVENTION() + ":grass"), Map.entry("ice", CONVENTION() + ":ice"),
            Map.entry("blockCoal", CONVENTION() + ":storage_blocks/coal"), Map.entry("blockRedstone", CONVENTION() + ":storage_blocks/redstone"));

    private Ids() {}

    private static String CONVENTION() { return ContentFormats.CONVENTION; }

    public record Block(String name, Map<String, String> properties) {}

    private static int current() { return SharedConstants.getCurrentVersion().getDataVersion().getVersion(); }

    public static boolean isVanilla(String name) { return name.startsWith(MINECRAFT + ":") || name.indexOf(':') < 0; }

    private static String namespaced(String name) { return name.indexOf(':') < 0 ? MINECRAFT + ":" + name : name; }

    public static String item(String name, int meta) {
        String asked = namespaced(name.trim());
        if (!isVanilla(asked)) { return asked; }
        String key = asked + "@" + meta;
        String held = ITEMS.get(key);
        if (held != null) { return held; }
        String found = asked;
        try {
            JsonObject stack = new JsonObject();
            stack.addProperty("id", asked);
            stack.addProperty("Count", 1);
            stack.addProperty("Damage", meta);
            Dynamic<JsonElement> fixed = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(JsonOps.INSTANCE, stack), LEGACY, current());
            found = fixed.get("id").asString(asked);
        }
        catch (RuntimeException failed) { warn("item", asked, failed); }
        ITEMS.put(key, found);
        return found;
    }

    public static Block block(String name, int meta) {
        String asked = namespaced(name.trim());
        if (!isVanilla(asked)) { return new Block(asked, Map.of()); }
        String key = asked + "@" + meta;
        Block held = BLOCKS.get(key);
        if (held != null) { return held; }
        Block found = new Block(asked, Map.of());
        try {
            Integer id = blockIds().get(asked);
            if (id != null) {
                Dynamic<?> tag = BlockStateData.getTag(id << 4 | (meta & 15));
                Dynamic<?> fixed = DataFixers.getDataFixer().update(References.BLOCK_STATE, tag, FLATTENED, current());
                String flat = fixed.get("Name").asString(asked);
                Map<String, String> properties = new LinkedHashMap<>();
                fixed.get("Properties").asMapOpt(k -> k.asString(""), v -> v.asString("")).result().ifPresent(properties::putAll);
                found = new Block(flat, properties.isEmpty() ? Map.of() : Map.copyOf(properties));
            }
            else {
                String flat = BlockStateData.upgradeBlock(asked);
                Dynamic<JsonElement> fixed = DataFixers.getDataFixer().update(References.BLOCK_NAME, new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(flat)), FLATTENED, current());
                found = new Block(fixed.asString(flat), Map.of());
            }
        }
        catch (RuntimeException failed) { warn("block", asked, failed); }
        BLOCKS.put(key, found);
        return found;
    }

    public static String entity(String name) {
        String asked = namespaced(name.trim());
        if (!isVanilla(asked)) { return asked; }
        return ENTITIES.computeIfAbsent(asked, held -> {
            try { return DataFixers.getDataFixer().update(References.ENTITY_NAME, new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(held)), LEGACY, current()).asString(held); }
            catch (RuntimeException failed) {
                warn("entity", held, failed);
                return held;
            }
        });
    }

    public static String biome(String name) {
        String asked = namespaced(name.trim());
        if (!isVanilla(asked)) { return asked; }
        return BIOMES.computeIfAbsent(asked, held -> {
            try { return DataFixers.getDataFixer().update(References.BIOME, new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(held)), LEGACY, current()).asString(held); }
            catch (RuntimeException failed) {
                warn("biome", held, failed);
                return held;
            }
        });
    }

    public static String dimension(String named) { return ContentFormats.dimensionId(named); }

    public static String oreDictTag(String ore) {
        String whole = ORE_WHOLE.get(ore);
        if (whole != null) { return whole; }
        int split = 0;
        while (split < ore.length() && !Character.isUpperCase(ore.charAt(split))) { split++; }
        String prefix = ore.substring(0, split);
        String rest = ore.substring(split);
        String folder = ORE_PREFIXES.get(prefix);
        if (folder == null || rest.isEmpty()) { return CONVENTION() + ":" + snake(ore); }
        return CONVENTION() + ":" + folder + "/" + snake(rest);
    }

    private static String snake(String camel) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Character.isUpperCase(c) && i > 0) { out.append('_'); }
            out.append(Character.toLowerCase(c));
        }
        return out.toString().toLowerCase(Locale.ROOT);
    }

    private static synchronized Map<String, Integer> blockIds() {
        if (BLOCK_IDS.isEmpty()) {
            for (int id = 0; id < 256; id++) {
                String name = ItemIdFix.getItem(id);
                if (name != null) { BLOCK_IDS.putIfAbsent(name, id); }
            }
        }
        return BLOCK_IDS;
    }

    private static void warn(String kind, String name, RuntimeException failed) {
        if (WARNED.add(kind + " " + name)) { ContentLog.LOGGER.warn("The forward port could not resolve the 1.12.2 {} '{}' through the game's data fixers, so it is left as written: {}", kind, name, failed.toString()); }
    }

    @Nullable public static String[] splitMeta(String named) {
        String[] parts = named.trim().split(":");
        if (parts.length != 3) { return null; }
        try {
            Integer.parseInt(parts[2].trim());
            return new String[] {parts[0] + ":" + parts[1], parts[2].trim()};
        }
        catch (NumberFormatException notMeta) { return null; }
    }
}
