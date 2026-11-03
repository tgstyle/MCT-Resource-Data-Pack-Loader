package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ItemSelection {
    private final String kind;
    private final String verb;
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Map<String, Set<Integer>> namedMetas = new LinkedHashMap<>();
    private final Set<String> oreNames = new LinkedHashSet<>();
    private final Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Item, Set<Integer>> metas = new IdentityHashMap<>();
    private final Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());

    public ItemSelection(String kind, String verb) {
        this.kind = kind;
        this.verb = verb;
    }

    public void read(JsonObject json, ResourceLocation key) {
        for (String name : Json.strings(json, "names")) { name(key, Settings.lower(name)); }
        for (String namespace : Json.strings(json, "namespaces")) { prefixes.add(Settings.lower(namespace) + ":"); }
        oreNames.addAll(Json.strings(json, "oreDict"));
    }

    public boolean unnamed() { return names.isEmpty() && prefixes.isEmpty() && namedMetas.isEmpty() && oreNames.isEmpty(); }

    public boolean any() { return !items.isEmpty() || !metas.isEmpty() || !blocks.isEmpty(); }

    public Set<String> oreNames() { return Collections.unmodifiableSet(oreNames); }

    public int itemCount() { return items.size(); }

    public int blockCount() { return blocks.size(); }

    public int variantCount() {
        int variants = 0;
        for (Set<Integer> held : metas.values()) { variants += held.size(); }
        return variants;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        Item item = stack.getItem();
        if (items.contains(item)) { return true; }
        Set<Integer> held = metas.get(item);
        return held != null && held.contains(stack.getMetadata());
    }

    public boolean matches(Block block) { return blocks.contains(block); }

    public void eachStack(Consumer<ItemStack> sink) {
        for (Item item : items) { sink.accept(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE)); }
        for (Map.Entry<Item, Set<Integer>> entry : metas.entrySet()) {
            for (int meta : entry.getValue()) { sink.accept(new ItemStack(entry.getKey(), 1, meta)); }
        }
    }

    public void resolve() {
        Set<String> found = new HashSet<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = item.getRegistryName();
            if (id == null || item == Items.AIR) { continue; }
            String name = id.toString();
            if (named(name, found)) { item(item); }
            Set<Integer> named = namedMetas.get(name);
            if (named == null) { continue; }
            found.add(name);
            metas.computeIfAbsent(item, k -> new LinkedHashSet<>()).addAll(named);
        }
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = block.getRegistryName();
            if (id == null || block == Blocks.AIR || !named(id.toString(), found)) { continue; }
            blocks.add(block);
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) { items.add(item); }
        }
        for (String ore : oreNames) {
            if (!OreDictionary.doesOreNameExist(ore)) {
                ContentLog.LOGGER.error("{} ore dictionary name '{}' does not exist, so it {} nothing", kind, ore, verb);
                continue;
            }
            for (ItemStack stack : OreDictionary.getOres(ore, false)) {
                if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) { item(stack.getItem()); }
                else { metas.computeIfAbsent(stack.getItem(), k -> new LinkedHashSet<>()).add(stack.getMetadata()); }
            }
        }
        metas.keySet().removeAll(items);
        for (String name : names) {
            if (!found.contains(name)) { ContentLog.LOGGER.error("{} name {} is no registered block or item, so it {} nothing", kind, name, verb); }
        }
        for (String name : namedMetas.keySet()) {
            if (!found.contains(name)) { ContentLog.LOGGER.error("{} name {} is no registered item, so it {} nothing", kind, name, verb); }
        }
    }

    private void name(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length == 3) {
            String id = parts[0] + ":" + parts[1];
            if ("*".equals(parts[2])) {
                names.add(id);
                return;
            }
            try { namedMetas.computeIfAbsent(id, k -> new LinkedHashSet<>()).add(Integer.parseInt(parts[2])); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("Metadata '{}' of {} in {} is not a number, skipping it", parts[2], id, key); }
            return;
        }
        if (parts.length != 2) {
            ContentLog.LOGGER.error("Name '{}' in {} needs a namespace, such as minecraft:iron_ingot, skipping it", name, key);
            return;
        }
        if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
        else { names.add(name); }
    }

    private boolean named(String name, Set<String> found) {
        if (names.contains(name)) {
            found.add(name);
            return true;
        }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }

    private void item(Item item) {
        items.add(item);
        if (item instanceof ItemBlock) { blocks.add(((ItemBlock) item).getBlock()); }
    }
}
