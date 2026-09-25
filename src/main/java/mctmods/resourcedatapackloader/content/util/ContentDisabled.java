package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentDisabled {
    public static final ResourceLocation TAG = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "disabled");
    public static final TagKey<Item> ITEMS = TagKey.create(Registries.ITEM, TAG);
    public static final TagKey<Block> BLOCKS = TagKey.create(Registries.BLOCK, TAG);
    private static final String ITEM_TAGS = TagManager.getTagDir(Registries.ITEM);
    private static final String BLOCK_TAGS = TagManager.getTagDir(Registries.BLOCK);
    private static final Gson GSON = new GsonBuilder().create();
    private static final Set<String> NAMES = new LinkedHashSet<>();
    private static final Set<String> PREFIXES = new LinkedHashSet<>();
    private static final Set<ResourceLocation> TAGS = new LinkedHashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String EMPTY_TAG = "Empty Tag: ";
    private static final Set<ResourceLocation> EMPTIED = ConcurrentHashMap.newKeySet();
    private static volatile boolean active;

    private ContentDisabled() {}

    public static boolean disabled(ItemStack stack) { return !stack.isEmpty() && stack.is(ITEMS); }

    public static boolean disabled(BlockState state) { return state.is(BLOCKS); }

    public static boolean any() { return active; }

    public static boolean emptiedTag(ItemStack stack) {
        if (!stack.is(Items.BARRIER)) { return false; }
        String name = stack.getHoverName().getString();
        if (!name.startsWith(EMPTY_TAG)) { return false; }
        ResourceLocation tag = ResourceLocation.tryParse(name.substring(EMPTY_TAG.length()));
        return tag != null && EMPTIED.contains(tag);
    }

    public static <T> void strip(String directory, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        if (!items && !BLOCK_TAGS.equals(directory)) { return; }
        if (items) {
            EMPTIED.clear();
            active = false;
        }
        reload();
        if (NAMES.isEmpty() && PREFIXES.isEmpty() && TAGS.isEmpty()) { return; }
        Set<ResourceLocation> ids = items ? items() : blocks();
        for (ResourceLocation tag : TAGS) {
            Collection<T> held = built.get(tag);
            if (held == null) {
                ContentLog.LOGGER.debug("Disabled tag {} is no {} tag", tag, items ? "item" : "block");
                continue;
            }
            for (T value : held) {
                ResourceLocation id = id(value);
                if (id != null) { ids.add(id); }
            }
        }
        if (items) { unknown(); }
        List<T> values = new ArrayList<>();
        for (ResourceLocation id : ids) { lookup.apply(id).ifPresent(values::add); }
        rebuild(built, ids, items);
        built.put(TAG, ImmutableSet.copyOf(values));
        if (items) { active = true; }
        Summary.info(items ? "disabled.items" : "disabled.blocks", "Disabled " + values.size() + (items ? " item(s)" : " block(s)"));
    }

    private static <T> void rebuild(Map<ResourceLocation, Collection<T>> built, Set<ResourceLocation> ids, boolean items) {
        for (Map.Entry<ResourceLocation, Collection<T>> entry : built.entrySet()) {
            ImmutableSet.Builder<T> kept = ImmutableSet.builder();
            if (!TAGS.contains(entry.getKey())) {
                for (T value : entry.getValue()) {
                    if (!ids.contains(id(value))) { kept.add(value); }
                }
            }
            ImmutableSet<T> left = kept.build();
            if (items && left.isEmpty() && !entry.getValue().isEmpty()) { EMPTIED.add(entry.getKey()); }
            entry.setValue(left);
        }
    }

    private static void unknown() {
        for (String name : NAMES) {
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (id == null || !ForgeRegistries.ITEMS.containsKey(id) && !ForgeRegistries.BLOCKS.containsKey(id)) { ContentLog.LOGGER.error("Disabled name {} is no registered block or item, so it disables nothing", name); }
        }
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        NAMES.clear();
        PREFIXES.clear();
        TAGS.clear();
        if (!Config.content.disabled()) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Disabled file {} is empty, ignoring it", key);
            return;
        }
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        for (String name : Json.strings(json, "names")) {
            String trimmed = name.trim().toLowerCase(Locale.ROOT);
            if (trimmed.indexOf(':') < 0) { ContentLog.LOGGER.error("Name '{}' in {} needs a namespace, such as minecraft:iron_ingot, skipping it", name, key); }
            else if (trimmed.endsWith("*")) { PREFIXES.add(trimmed.substring(0, trimmed.length() - 1)); }
            else { NAMES.add(trimmed); }
        }
        for (String namespace : Json.strings(json, "namespaces")) { PREFIXES.add(namespace.trim().toLowerCase(Locale.ROOT) + ":"); }
        for (String tag : Json.strings(json, "tags")) {
            ResourceLocation location = ResourceLocation.tryParse(tag.startsWith("#") ? tag.substring(1) : tag);
            if (location == null) { ContentLog.LOGGER.error("Tag '{}' in {} is not a valid name, skipping it", tag, key); }
            else { TAGS.add(location); }
        }
    }

    private static Set<ResourceLocation> items() {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (item == Items.AIR || id == null) { continue; }
            if (named(id) || item instanceof BlockItem block && named(ForgeRegistries.BLOCKS.getKey(block.getBlock()))) { ids.add(id); }
        }
        return ids;
    }

    private static Set<ResourceLocation> blocks() {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (block == Blocks.AIR || id == null) { continue; }
            Item item = block.asItem();
            if (named(id) || item != Items.AIR && named(ForgeRegistries.ITEMS.getKey(item))) { ids.add(id); }
        }
        return ids;
    }

    private static boolean named(@Nullable ResourceLocation id) {
        if (id == null) { return false; }
        String name = id.toString();
        if (NAMES.contains(name)) { return true; }
        for (String prefix : PREFIXES) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }

    @Nullable private static ResourceLocation id(Object value) { return value instanceof Holder<?> holder ? holder.unwrapKey().map(ResourceKey::location).orElse(null) : null; }
}
