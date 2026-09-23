package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.References;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class Ids {
    public static final int LEGACY = 1343;
    private static final int FLATTENED = 1451;
    private static final String MINECRAFT = "minecraft";
    private static final String ENTITY_TABLES = "entities/";
    private static final String SPAWNER = "minecraft:mob_spawner";
    private static final String SPAWN_DATA = "SpawnData";
    private static final String SPAWN_POTENTIALS = "SpawnPotentials";
    private static final String ENTITY = "entity";
    private static final int METAS = 16;
    private static final Map<String, String> LOOT_TABLES = Map.of("chests/village_blacksmith", "chests/village/village_weaponsmith", "entities/sheep/silver", "entities/sheep/light_gray");
    private static final Pattern LEGACY_ATTRIBUTE = Pattern.compile("^(generic|zombie|horse)\\.[A-Za-z]+$");
    private static final Map<String, Integer> BLOCK_IDS = new HashMap<>();
    private static final int FIRST_LATE_BLOCK = 198;
    private static final String[] LATE_BLOCKS = {"end_rod", "chorus_plant", "chorus_flower", "purpur_block", "purpur_pillar", "purpur_stairs", "purpur_double_slab", "purpur_slab", "end_bricks", "beetroots", "grass_path", "end_gateway", "repeating_command_block", "chain_command_block", "frosted_ice", "magma", "nether_wart_block", "red_nether_brick", "bone_block", "structure_void", "observer", "white_shulker_box", "orange_shulker_box", "magenta_shulker_box", "light_blue_shulker_box", "yellow_shulker_box", "lime_shulker_box", "pink_shulker_box", "gray_shulker_box", "silver_shulker_box", "cyan_shulker_box", "purple_shulker_box", "blue_shulker_box", "brown_shulker_box", "green_shulker_box", "red_shulker_box", "black_shulker_box", "white_glazed_terracotta", "orange_glazed_terracotta", "magenta_glazed_terracotta", "light_blue_glazed_terracotta", "yellow_glazed_terracotta", "lime_glazed_terracotta", "pink_glazed_terracotta", "gray_glazed_terracotta", "silver_glazed_terracotta", "cyan_glazed_terracotta", "purple_glazed_terracotta", "blue_glazed_terracotta", "brown_glazed_terracotta", "green_glazed_terracotta", "red_glazed_terracotta", "black_glazed_terracotta", "concrete", "concrete_powder", "", "", "structure_block"};
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

    public static boolean isModded(String name) { return !name.startsWith(MINECRAFT + ":") && name.indexOf(':') >= 0; }

    private static String namespaced(String name) { return name.indexOf(':') < 0 ? MINECRAFT + ":" + name : name; }

    public static String item(String name, int meta) {
        String asked = namespaced(name.trim());
        if (isModded(asked)) { return asked; }
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
        if (isModded(asked)) { return new Block(asked, Map.of()); }
        String key = asked + "@" + meta;
        Block held = BLOCKS.get(key);
        if (held != null) { return held; }
        Block found = new Block(asked, Map.of());
        try {
            Integer id = blockIds().get(asked);
            if (id != null) { found = flattened(BlockStateData.getTag(id << 4 | (meta & 15)), asked); }
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

    private static Block flattened(Dynamic<?> tag, String asked) {
        Dynamic<?> fixed = DataFixers.getDataFixer().update(References.BLOCK_STATE, tag, FLATTENED, current());
        Map<String, String> properties = new TreeMap<>();
        fixed.get("Properties").asMapOpt(k -> k.asString(""), v -> v.asString("")).result().ifPresent(properties::putAll);
        return new Block(fixed.get("Name").asString(asked), properties.isEmpty() ? Map.of() : Collections.unmodifiableMap(properties));
    }

    public static Block numberedBlock(int id, int meta) { return flattened(BlockStateData.getTag((id & 255) << 4 | (meta & 15)), "minecraft:air"); }

    public static String numberedItem(int id) { return ItemIdFix.getItem(id); }

    @Nullable public static Block state(String name, Map<String, String> properties) {
        String asked = namespaced(name.trim());
        try {
            CompoundTag legacy = new CompoundTag();
            legacy.putString("Name", asked);
            if (!properties.isEmpty()) {
                CompoundTag held = new CompoundTag();
                properties.forEach(held::putString);
                legacy.put("Properties", held);
            }
            Dynamic<?> given = new Dynamic<>(NbtOps.INSTANCE, legacy);
            Dynamic<?> upgraded = BlockStateData.upgradeBlockStateTag(given);
            return upgraded == given ? null : flattened(upgraded, asked);
        }
        catch (RuntimeException failed) { return null; }
    }

    @Nullable public static String stack(String name, int meta, @Nullable String nbt) {
        String asked = namespaced(name.trim());
        try {
            CompoundTag stack = new CompoundTag();
            stack.putString("id", asked);
            stack.putByte("Count", (byte) 1);
            stack.putShort("Damage", (short) meta);
            if (nbt != null) { stack.put("tag", TagParser.parseTag(nbt)); }
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, stack), LEGACY, current());
            String id = fixed.get("id").asString(asked);
            Tag tag = fixed.get("tag").result().map(Dynamic::getValue).orElse(null);
            if (!(tag instanceof CompoundTag held)) { return id; }
            if (held.contains("Damage") && held.getInt("Damage") == 0) { held.remove("Damage"); }
            return held.isEmpty() ? id : id + held;
        }
        catch (CommandSyntaxException | RuntimeException failed) { return null; }
    }

    @Nullable public static String entityData(String id, String nbt) {
        try {
            CompoundTag data = TagParser.parseTag(nbt);
            data.putString("id", namespaced(id.trim()));
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.ENTITY_TREE, new Dynamic<>(NbtOps.INSTANCE, data), LEGACY, current());
            return fixed.getValue() instanceof CompoundTag held ? withoutPlace(held) : null;
        }
        catch (CommandSyntaxException | RuntimeException failed) { return null; }
    }

    @Nullable public static String blockEntityData(String block, String id, String nbt) {
        try {
            CompoundTag data = TagParser.parseTag(nbt);
            data.putString("id", namespaced(id.trim()));
            CompoundTag placed = new CompoundTag();
            ListTag position = new ListTag();
            for (int i = 0; i < 3; i++) { position.add(IntTag.valueOf(0)); }
            placed.put("pos", position);
            placed.putInt("state", 0);
            placed.put("nbt", data);
            ListTag blocks = new ListTag();
            blocks.add(placed);
            CompoundTag state = new CompoundTag();
            state.putString("Name", namespaced(block.trim()));
            ListTag palette = new ListTag();
            palette.add(state);
            CompoundTag structure = new CompoundTag();
            structure.put("size", position.copy());
            structure.put("palette", palette);
            structure.put("blocks", blocks);
            structure.put("entities", new ListTag());
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.STRUCTURE, new Dynamic<>(NbtOps.INSTANCE, structure), LEGACY, current());
            Tag found = fixed.get("blocks").asStream().findFirst().flatMap(held -> held.get("nbt").result()).map(Dynamic::getValue).orElse(null);
            if (!(found instanceof CompoundTag held)) { return null; }
            if (SPAWNER.equals(namespaced(id.trim()))) { spawnerEntities(held); }
            return withoutPlace(held);
        }
        catch (CommandSyntaxException | RuntimeException failed) { return null; }
    }

    private static void spawnerEntities(CompoundTag spawner) {
        if (spawner.contains(SPAWN_DATA, Tag.TAG_COMPOUND) && !spawner.getCompound(SPAWN_DATA).isEmpty() && !spawner.getCompound(SPAWN_DATA).contains(ENTITY)) {
            CompoundTag data = new CompoundTag();
            data.put(ENTITY, spawner.getCompound(SPAWN_DATA));
            spawner.put(SPAWN_DATA, data);
        }
        if (!spawner.contains(SPAWN_POTENTIALS, Tag.TAG_LIST)) { return; }
        ListTag potentials = spawner.getList(SPAWN_POTENTIALS, Tag.TAG_COMPOUND);
        for (int i = 0; i < potentials.size(); i++) {
            CompoundTag potential = potentials.getCompound(i);
            if (!potential.contains("Entity", Tag.TAG_COMPOUND)) { continue; }
            CompoundTag data = new CompoundTag();
            data.put(ENTITY, potential.getCompound("Entity"));
            CompoundTag entry = new CompoundTag();
            entry.putInt("weight", potential.contains("Weight") ? potential.getInt("Weight") : 1);
            entry.put("data", data);
            potentials.set(i, entry);
        }
    }

    private static String withoutPlace(CompoundTag held) {
        for (String key : new String[] {"id", "x", "y", "z"}) { held.remove(key); }
        return held.toString();
    }

    @Nullable public static String nbt(String nbt) {
        try { return TagParser.parseTag(nbt).toString(); }
        catch (CommandSyntaxException failed) { return null; }
    }

    @Nullable public static String enchantment(String named) {
        CompoundTag stack = enchantedBook(named.trim().toLowerCase(Locale.ROOT));
        try {
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, stack), LEGACY, current());
            String found = fixed.get("tag").get("Enchantments").asStream().findFirst().map(held -> held.get("id").asString("")).orElse("");
            return found.isEmpty() || "null".equals(found) ? null : found;
        }
        catch (RuntimeException failed) { return null; }
    }

    private static CompoundTag enchantedBook(String enchantmentName) {
        CompoundTag enchantment = new CompoundTag();
        String key;
        try {
            enchantment.putShort("id", Short.parseShort(enchantmentName));
            key = "ench";
        }
        catch (NumberFormatException byName) {
            enchantment.putString("id", namespaced(enchantmentName));
            key = "Enchantments";
        }
        enchantment.putShort("lvl", (short) 1);
        ListTag enchantments = new ListTag();
        enchantments.add(enchantment);
        CompoundTag tag = new CompoundTag();
        tag.put(key, enchantments);
        CompoundTag stack = new CompoundTag();
        stack.putString("id", "minecraft:enchanted_book");
        stack.putByte("Count", (byte) 1);
        stack.put("tag", tag);
        return stack;
    }

    public static String entity(String name) {
        String asked = namespaced(name.trim());
        if (isModded(asked)) { return asked; }
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
        if (isModded(asked)) { return asked; }
        return BIOMES.computeIfAbsent(asked, held -> {
            try { return DataFixers.getDataFixer().update(References.BIOME, new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(held)), LEGACY, current()).asString(held); }
            catch (RuntimeException failed) {
                warn("biome", held, failed);
                return held;
            }
        });
    }

    public static String dimension(String named) { return ContentFormats.dimensionId(named); }

    public static String criterion(String named) {
        if (!named.startsWith("stat.")) { return named; }
        JsonObject stats = new JsonObject();
        stats.addProperty(named, 0);
        try {
            JsonElement fixed = DataFixers.getDataFixer().update(References.STATS, new Dynamic<>(JsonOps.INSTANCE, stats), LEGACY, current()).getValue();
            JsonElement byType = fixed.isJsonObject() ? fixed.getAsJsonObject().get("stats") : null;
            if (byType != null && byType.isJsonObject()) {
                for (Map.Entry<String, JsonElement> type : byType.getAsJsonObject().entrySet()) {
                    if (!type.getValue().isJsonObject()) { continue; }
                    for (String value : type.getValue().getAsJsonObject().keySet()) { return type.getKey().replace(':', '.') + ":" + value.replace(':', '.'); }
                }
            }
        }
        catch (RuntimeException failed) { warn("statistic", named, failed); }
        return named;
    }

    public static List<String> items(String name) {
        Set<String> found = new LinkedHashSet<>();
        for (int meta = 0; meta < METAS; meta++) { found.add(item(name, meta)); }
        return List.copyOf(found);
    }

    public static List<String> blocks(String name) {
        Set<String> found = new LinkedHashSet<>();
        for (int meta = 0; meta < METAS; meta++) { found.add(block(name, meta).name()); }
        return List.copyOf(found);
    }

    @Nullable public static JsonObject nbtIngredient(String name, int meta, String nbt) {
        String asked = namespaced(name.trim());
        try {
            CompoundTag stack = new CompoundTag();
            stack.putString("id", asked);
            stack.putByte("Count", (byte) 1);
            stack.putShort("Damage", (short) meta);
            stack.put("tag", TagParser.parseTag(nbt));
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, stack), LEGACY, current());
            JsonObject out = new JsonObject();
            out.addProperty("type", "forge:nbt");
            out.addProperty("item", fixed.get("id").asString(asked));
            out.addProperty("nbt", fixed.get("tag").result().map(tag -> tag.getValue().toString()).orElse("{}"));
            return out;
        }
        catch (CommandSyntaxException | RuntimeException failed) {
            warn("item with nbt", asked, failed);
            return null;
        }
    }

    public static String lootTable(String name) {
        String asked = namespaced(name.trim().toLowerCase(Locale.ROOT));
        if (isModded(asked)) { return asked; }
        String path = asked.substring(MINECRAFT.length() + 1);
        String renamed = LOOT_TABLES.get(path);
        if (renamed != null) { return MINECRAFT + ":" + renamed; }
        if (!path.startsWith(ENTITY_TABLES)) { return asked; }
        String rest = path.substring(ENTITY_TABLES.length());
        int slash = rest.indexOf('/');
        String entity = entity(slash < 0 ? rest : rest.substring(0, slash));
        return MINECRAFT + ":" + ENTITY_TABLES + entity.substring(entity.indexOf(':') + 1) + (slash < 0 ? "" : rest.substring(slash));
    }

    public static String attribute(String name) {
        String trimmed = name.trim();
        return LEGACY_ATTRIBUTE.matcher(trimmed).matches() ? snake(trimmed) : trimmed;
    }

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
                BLOCK_IDS.putIfAbsent(ItemIdFix.getItem(id), id);
            }
            for (int i = 0; i < LATE_BLOCKS.length; i++) {
                if (!LATE_BLOCKS[i].isEmpty()) { BLOCK_IDS.putIfAbsent("minecraft:" + LATE_BLOCKS[i], FIRST_LATE_BLOCK + i); }
            }
        }
        return BLOCK_IDS;
    }

    private static void warn(String kind, String name, Exception failed) {
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
