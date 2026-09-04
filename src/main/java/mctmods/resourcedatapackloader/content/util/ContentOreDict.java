package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ContentOreDict {
    private static final Gson GSON = new GsonBuilder().create();
    private static boolean applied;

    private ContentOreDict() {}

    public static void apply() {
        if (applied) { return; }
        applied = true;
        if (!Config.content.oreDictionary) { return; }
        int[] count = new int[2];
        PackManager.get().forEach(PackManager.OREDICT, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents, count); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in ore dictionary file {}, ignoring it", key, ex); }
        });
        if (count[0] > 0) { Summary.info("oredict_extra", "Added " + count[0] + " extra ore dictionary entry/entries"); }
        if (count[1] > 0) { Summary.info("oredict_removed", "Removed " + count[1] + " ore dictionary entry/entries"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Ore dictionary file {} is empty, ignoring it", key);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String name = entry.getKey();
            if (name.startsWith("_")) { continue; }
            if (!entry.getValue().isJsonArray()) {
                ContentLog.LOGGER.error("Ore dictionary name '{}' in {} is not an array, skipping it", name, key);
                continue;
            }
            if (name.startsWith("-")) {
                remove(key, name.substring(1), entry.getValue().getAsJsonArray(), count);
                continue;
            }
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                ItemStack stack = ContentStacks.parse(key, element.getAsString(), 1);
                if (stack.isEmpty()) { continue; }
                OreDictionary.registerOre(name, stack);
                count[0]++;
            }
        }
    }

    private static void remove(ResourceLocation key, String name, JsonArray items, int[] count) {
        if (!OreDictionary.doesOreNameExist(name)) {
            ContentLog.LOGGER.error("Ore dictionary name '{}' in {} does not exist, so there is nothing to remove from it", name, key);
            return;
        }
        int id = OreDictionary.getOreID(name);
        List<ItemStack> registered;
        Map<Integer, List<Integer>> stackToId;
        try {
            registered = idToStack().get(id);
            stackToId = stackToId();
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            ContentLog.LOGGER.error("The ore dictionary's tables could not be reached, so nothing is removed from '{}'", name, ex);
            return;
        }
        for (JsonElement element : items) {
            String wanted = element.getAsString().trim();
            if ("*".equals(wanted)) {
                for (ItemStack held : registered) { unlink(stackToId, held, id); }
                count[1] += registered.size();
                registered.clear();
                continue;
            }
            ItemStack stack = ContentStacks.parse(key, wanted, 1);
            if (stack.isEmpty()) { continue; }
            boolean found = false;
            for (Iterator<ItemStack> each = registered.iterator(); each.hasNext();) {
                ItemStack held = each.next();
                if (held.getItem() != stack.getItem()) { continue; }
                boolean wildcard = held.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE;
                if (!wildcard && held.getItemDamage() != stack.getItemDamage()) { continue; }
                if (held.getItemDamage() == OreDictionary.WILDCARD_VALUE && stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) { ContentLog.LOGGER.info("Ore dictionary name '{}' carries {} for every metadata, so {} in {} removes the whole entry", name, held.getItem().getRegistryName(), wanted, key); }
                unlink(stackToId, held, id);
                each.remove();
                count[1]++;
                found = true;
            }
            if (!found) { ContentLog.LOGGER.error("Ore dictionary name '{}' in {} does not carry {}, so there is nothing to remove", name, key, wanted); }
        }
    }

    private static void unlink(Map<Integer, List<Integer>> stackToId, ItemStack held, int id) {
        int hash = Item.REGISTRY.getIDForObject(held.getItem());
        if (held.getItemDamage() != OreDictionary.WILDCARD_VALUE) { hash |= ((held.getItemDamage() + 1) << 16); }
        List<Integer> ids = stackToId.get(hash);
        if (ids != null) { ids.remove(Integer.valueOf(id)); }
    }

    @SuppressWarnings("unchecked") private static List<List<ItemStack>> idToStack() throws ReflectiveOperationException {
        Field field = OreDictionary.class.getDeclaredField("idToStack");
        field.setAccessible(true);
        return (List<List<ItemStack>>) field.get(null);
    }

    @SuppressWarnings("unchecked") private static Map<Integer, List<Integer>> stackToId() throws ReflectiveOperationException {
        Field field = OreDictionary.class.getDeclaredField("stackToId");
        field.setAccessible(true);
        return (Map<Integer, List<Integer>>) field.get(null);
    }
}
