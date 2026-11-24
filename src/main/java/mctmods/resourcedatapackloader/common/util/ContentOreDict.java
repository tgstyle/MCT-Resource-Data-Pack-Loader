package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ContentOreDict {
    private static final List<String> COPY_KEYS = Collections.unmodifiableList(Arrays.asList("tag", "matches", "from"));
    private static final List<Copy> COPIES = new ArrayList<>();
    private static boolean applied;
    private static boolean copied;

    private ContentOreDict() {}

    public static void apply() {
        if (applied) { return; }
        applied = true;
        if (!Config.content.oreDictionary) { return; }
        int[] count = new int[2];
        Json.eachFile(PackManager.OREDICT, "ore dictionary file", (key, contents) -> read(key, contents, count));
        if (count[0] > 0) { Summary.info("oredict_extra", "Added " + count[0] + " extra ore dictionary entry/entries"); }
        if (count[1] > 0) { Summary.info("oredict_removed", "Removed " + count[1] + " ore dictionary entry/entries"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = Json.object(key, contents, "ore dictionary file");
        if (json == null) { return; }
        if (JsonUtils.isJsonPrimitive(json, "tag")) {
            Json.unknownKeys(json, "the top level", COPY_KEYS);
            copy(key, json);
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
                count[0] += register(name, stack);
            }
        }
    }

    public static int register(String name, ItemStack stack) {
        OreDictionary.registerOre(name, stack);
        return 1;
    }

    private static void copy(ResourceLocation key, JsonObject json) {
        String tag = oreOf(json.get("tag").getAsString());
        if (tag == null) {
            ContentLog.LOGGER.error("Ore dictionary copy in {} names tag '{}', which has no ore dictionary name on this version, ignoring it", key, json.get("tag").getAsString());
            return;
        }
        String matches = Json.text(json, "matches");
        if (matches.isEmpty()) {
            ContentLog.LOGGER.error("Ore dictionary copy in {} has no matches pattern, ignoring it", key);
            return;
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(matches);
        }
        catch (PatternSyntaxException ex) {
            ContentLog.LOGGER.error("Ore dictionary copy in {} has the pattern '{}', which is not a regular expression ({}), ignoring it", key, matches, ex.getDescription());
            return;
        }
        String from = null;
        if (JsonUtils.isJsonPrimitive(json, "from")) {
            String text = json.get("from").getAsString();
            from = oreOf(text);
            if (from == null) {
                ContentLog.LOGGER.error("Ore dictionary copy in {} names tag '{}', which has no ore dictionary name on this version, ignoring it", key, text);
                return;
            }
        }
        COPIES.add(new Copy(key, tag, pattern, from));
    }

    @Nullable private static String oreOf(String text) {
        return text.indexOf(':') < 0 ? text.trim() : Ids.oreName(text);
    }

    public static void copyMatching() {
        if (copied) { return; }
        copied = true;
        int added = 0;
        Set<String> tags = new HashSet<>();
        for (Copy copy : COPIES) {
            int before = added;
            boolean matched = false;
            if (copy.from == null) {
                for (Item item : ForgeRegistries.ITEMS) {
                    if (item == Items.AIR || copy.rejects(item)) { continue; }
                    matched = true;
                    added += join(copy.tag, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
                }
            }
            else if (!OreDictionary.doesOreNameExist(copy.from) || OreDictionary.getOres(copy.from, false).isEmpty()) {
                ContentLog.LOGGER.error("Ore dictionary copy in {} takes from '{}', which has no members, so nothing is copied", copy.key, copy.from);
                continue;
            }
            else {
                for (ItemStack stack : new ArrayList<>(OreDictionary.getOres(copy.from, false))) {
                    if (copy.rejects(stack.getItem())) { continue; }
                    matched = true;
                    added += join(copy.tag, stack);
                }
            }
            if (added > before) { tags.add(copy.tag); }
            if (!matched) { ContentLog.LOGGER.warn("Ore dictionary copy in {} matched no item for '{}'", copy.key, copy.pattern.pattern()); }
        }
        if (added > 0) { Summary.info("oredict_copied", "Copied " + added + " item(s) into " + tags.size() + " ore dictionary name(s) by pattern"); }
    }

    private static int join(String tag, ItemStack stack) {
        int before = OreDictionary.getOres(tag, false).size();
        OreDictionary.registerOre(tag, stack);
        return OreDictionary.getOres(tag, false).size() - before;
    }

    private static final class Copy {
        final ResourceLocation key;
        final String tag;
        final Pattern pattern;
        @Nullable final String from;

        Copy(ResourceLocation key, String tag, Pattern pattern, @Nullable String from) {
            this.key = key;
            this.tag = tag;
            this.pattern = pattern;
            this.from = from;
        }

        boolean rejects(Item item) {
            ResourceLocation id = item.getRegistryName();
            return id == null || !pattern.matcher(id.toString()).find();
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
                if (!Stacks.matches(stack, held)) { continue; }
                if (held.getItemDamage() == OreDictionary.WILDCARD_VALUE && stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) { ContentLog.LOGGER.info("Ore dictionary name '{}' carries {} for every metadata, so {} in {} removes the whole entry", name, held.getItem().getRegistryName(), wanted, key); }
                unlink(stackToId, held, id);
                each.remove();
                count[1]++;
                found = true;
            }
            if (!found) { ContentLog.LOGGER.error("Ore dictionary name '{}' in {} does not carry {}, so there is nothing to remove", name, key, wanted); }
        }
    }

    public static int strip(Predicate<ItemStack> doomed, Set<String> emptied) {
        List<List<ItemStack>> table;
        Map<Integer, List<Integer>> stackToId;
        try {
            table = idToStack();
            stackToId = stackToId();
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            ContentLog.LOGGER.error("The ore dictionary's tables could not be reached, so disabled items keep their ore dictionary names", ex);
            return 0;
        }
        int count = 0;
        for (String name : OreDictionary.getOreNames()) {
            int id = OreDictionary.getOreID(name);
            boolean all = emptied.contains(name);
            for (Iterator<ItemStack> each = table.get(id).iterator(); each.hasNext();) {
                ItemStack held = each.next();
                if (!all && !doomed.test(held)) { continue; }
                unlink(stackToId, held, id);
                each.remove();
                count++;
            }
        }
        return count;
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
