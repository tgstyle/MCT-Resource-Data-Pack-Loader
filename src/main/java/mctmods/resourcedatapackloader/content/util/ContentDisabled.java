package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class ContentDisabled {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Set<String> NAMES = new LinkedHashSet<>();
    private static final Set<String> PREFIXES = new LinkedHashSet<>();
    private static final Map<String, Set<Integer>> NAMED_METAS = new LinkedHashMap<>();
    private static final Set<String> ORE_NAMES = new LinkedHashSet<>();
    private static final Set<Item> ITEMS = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Map<Item, Set<Integer>> METAS = new IdentityHashMap<>();
    private static final Set<Block> BLOCKS = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean loaded;

    private ContentDisabled() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.disabled) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
        if (NAMES.isEmpty() && PREFIXES.isEmpty() && NAMED_METAS.isEmpty() && ORE_NAMES.isEmpty()) { return; }
        resolve();
        if (!any()) { return; }
        int variants = 0;
        for (Set<Integer> metas : METAS.values()) { variants += metas.size(); }
        Summary.info("disabled", "Disabled " + ITEMS.size() + " item(s), " + variants + " item variant(s) and " + BLOCKS.size() + " block(s)");
    }

    public static boolean any() { return !ITEMS.isEmpty() || !METAS.isEmpty() || !BLOCKS.isEmpty(); }

    public static Set<String> oreNames() { return Collections.unmodifiableSet(ORE_NAMES); }

    public static boolean disabled(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        Item item = stack.getItem();
        if (ITEMS.contains(item)) { return true; }
        Set<Integer> metas = METAS.get(item);
        return metas != null && metas.contains(stack.getMetadata());
    }

    public static boolean disabled(Block block) { return BLOCKS.contains(block); }

    public static void eachStack(Consumer<ItemStack> sink) {
        for (Item item : ITEMS) { sink.accept(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE)); }
        for (Map.Entry<Item, Set<Integer>> entry : METAS.entrySet()) {
            for (int meta : entry.getValue()) { sink.accept(new ItemStack(entry.getKey(), 1, meta)); }
        }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Disabled file {} is empty, ignoring it", key);
            return;
        }
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        for (String name : Json.strings(json, "names")) { name(key, name.trim().toLowerCase(Locale.ROOT)); }
        for (String namespace : Json.strings(json, "namespaces")) { PREFIXES.add(namespace.trim().toLowerCase(Locale.ROOT) + ":"); }
        ORE_NAMES.addAll(Json.strings(json, "oreDict"));
    }

    private static void name(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length == 3) {
            String id = parts[0] + ":" + parts[1];
            if ("*".equals(parts[2])) {
                NAMES.add(id);
                return;
            }
            try { NAMED_METAS.computeIfAbsent(id, k -> new LinkedHashSet<>()).add(Integer.parseInt(parts[2])); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("Metadata '{}' of {} in {} is not a number, skipping it", parts[2], id, key); }
            return;
        }
        if (parts.length != 2) {
            ContentLog.LOGGER.error("Name '{}' in {} needs a namespace, such as minecraft:iron_ingot, skipping it", name, key);
            return;
        }
        if (name.endsWith("*")) { PREFIXES.add(name.substring(0, name.length() - 1)); }
        else { NAMES.add(name); }
    }

    private static void resolve() {
        Set<String> found = new HashSet<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = item.getRegistryName();
            if (id == null || item == Items.AIR) { continue; }
            String name = id.toString();
            if (named(name, found)) { item(item); }
            Set<Integer> metas = NAMED_METAS.get(name);
            if (metas == null) { continue; }
            found.add(name);
            METAS.computeIfAbsent(item, k -> new LinkedHashSet<>()).addAll(metas);
        }
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = block.getRegistryName();
            if (id == null || block == Blocks.AIR || !named(id.toString(), found)) { continue; }
            BLOCKS.add(block);
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) { ITEMS.add(item); }
        }
        for (String ore : ORE_NAMES) {
            if (!OreDictionary.doesOreNameExist(ore)) {
                ContentLog.LOGGER.error("Disabled ore dictionary name '{}' does not exist, so it disables nothing", ore);
                continue;
            }
            for (ItemStack stack : OreDictionary.getOres(ore, false)) {
                if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) { item(stack.getItem()); }
                else { METAS.computeIfAbsent(stack.getItem(), k -> new LinkedHashSet<>()).add(stack.getMetadata()); }
            }
        }
        METAS.keySet().removeAll(ITEMS);
        for (String name : NAMES) {
            if (!found.contains(name)) { ContentLog.LOGGER.error("Disabled name {} is no registered block or item, so it disables nothing", name); }
        }
        for (String name : NAMED_METAS.keySet()) {
            if (!found.contains(name)) { ContentLog.LOGGER.error("Disabled name {} is no registered item, so it disables nothing", name); }
        }
    }

    private static boolean named(String name, Set<String> found) {
        if (NAMES.contains(name)) {
            found.add(name);
            return true;
        }
        for (String prefix : PREFIXES) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }

    private static void item(Item item) {
        ITEMS.add(item);
        if (item instanceof ItemBlock) { BLOCKS.add(((ItemBlock) item).getBlock()); }
    }
}
