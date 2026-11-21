package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentDisabled {
    public static final ResourceLocation TAG = ResourceDataPackLoader.id("disabled");
    public static final TagKey<Item> ITEMS = TagKey.create(Registries.ITEM, TAG);
    public static final TagKey<Block> BLOCKS = TagKey.create(Registries.BLOCK, TAG);
    public static final ResourceLocation HIDDEN_TAG = ResourceDataPackLoader.id("hidden");
    public static final TagKey<Item> HIDDEN_ITEMS = TagKey.create(Registries.ITEM, HIDDEN_TAG);
    private static final String ITEM_TAGS = TagManager.getTagDir(Registries.ITEM);
    private static final String BLOCK_TAGS = TagManager.getTagDir(Registries.BLOCK);
    private static final String FLUID_TAGS = TagManager.getTagDir(Registries.FLUID);
    private static final List<String> KEYS = List.of("requires", "hideOnly", "untag", "hideCategories", "fluids", "names", "namespaces", "tags");
    private static final ItemSelection SELECTION = new ItemSelection("Disabled", "disables");
    private static final ItemSelection HIDDEN = new ItemSelection("Hidden", "hides");
    private static final ItemSelection UNTAGGED = new ItemSelection("Untagged", "untags");
    private static final Set<ResourceLocation> FLUIDS = ConcurrentHashMap.newKeySet();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String EMPTY_TAG = "Empty Tag: ";
    private static final Set<ResourceLocation> EMPTIED = ConcurrentHashMap.newKeySet();
    private static final Set<String> HIDDEN_CATEGORIES = ConcurrentHashMap.newKeySet();
    private static volatile boolean active;

    private ContentDisabled() {}

    public static Set<String> hiddenCategories() {
        reload();
        return Set.copyOf(HIDDEN_CATEGORIES);
    }

    public static boolean disabled(ItemStack stack) { return !stack.isEmpty() && stack.is(ITEMS); }

    public static boolean disabled(BlockState state) { return state.is(BLOCKS); }

    public static boolean hidden(ItemStack stack) { return !stack.isEmpty() && stack.is(HIDDEN_ITEMS); }

    public static boolean none() { return !active; }

    public static boolean emptiedTag(ItemStack stack) {
        if (!stack.is(Items.BARRIER)) { return false; }
        String name = stack.getHoverName().getString();
        if (!name.startsWith(EMPTY_TAG)) { return false; }
        ResourceLocation tag = ResourceLocation.tryParse(name.substring(EMPTY_TAG.length()));
        return tag != null && EMPTIED.contains(tag);
    }

    public static <T> void strip(String directory, Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        boolean fluids = FLUID_TAGS.equals(directory);
        if (!items && !fluids && !BLOCK_TAGS.equals(directory)) { return; }
        if (items) {
            EMPTIED.clear();
            active = false;
        }
        reload();
        if (fluids) {
            untagFluids(built);
            return;
        }
        if (items) {
            untag(built);
            hide(built, lookup);
        }
        if (SELECTION.unnamed()) { return; }
        Set<ResourceLocation> ids = items ? SELECTION.items() : SELECTION.blocks();
        SELECTION.tagged(built, ids, tag -> ContentLog.LOGGER.debug("Disabled tag {} is no {} tag", tag, items ? "item" : "block"));
        if (items) { SELECTION.unknown(); }
        List<T> values = new ArrayList<>();
        for (ResourceLocation id : ids) { lookup.apply(id).ifPresent(values::add); }
        rebuild(built, ids, items);
        built.put(TAG, ImmutableSet.copyOf(values));
        if (items) { active = true; }
        Summary.info(items ? "disabled.items" : "disabled.blocks", "Disabled " + values.size() + (items ? " item(s)" : " block(s)"));
    }

    private static <T> void hide(Map<ResourceLocation, Collection<T>> built, Function<ResourceLocation, Optional<? extends T>> lookup) {
        if (HIDDEN.unnamed()) { return; }
        Set<ResourceLocation> ids = HIDDEN.items();
        HIDDEN.tagged(built, ids, tag -> ContentLog.LOGGER.debug("Hidden tag {} is no item tag", tag));
        HIDDEN.unknown();
        List<T> values = new ArrayList<>();
        for (ResourceLocation id : ids) { lookup.apply(id).ifPresent(values::add); }
        built.put(HIDDEN_TAG, ImmutableSet.copyOf(values));
        Summary.info("hidden", "Hid " + values.size() + " item(s) in JEI");
    }

    private static <T> void untag(Map<ResourceLocation, Collection<T>> built) {
        if (UNTAGGED.unnamed()) { return; }
        UNTAGGED.unknown();
        Summary.info("untagged.items", "Removed " + remove(built, UNTAGGED.items()) + " tag entry/entries for untagged items");
    }

    private static <T> void untagFluids(Map<ResourceLocation, Collection<T>> built) {
        if (FLUIDS.isEmpty()) { return; }
        for (ResourceLocation fluid : FLUIDS) {
            if (!ForgeRegistries.FLUIDS.containsKey(fluid)) { ContentLog.LOGGER.error("Fluid {} in a disabled file is no registered fluid, so it untags nothing", fluid); }
        }
        Summary.info("untagged.fluids", "Removed " + remove(built, FLUIDS) + " tag entry/entries for untagged fluids");
    }

    private static <T> int remove(Map<ResourceLocation, Collection<T>> built, Set<ResourceLocation> ids) {
        int removed = 0;
        for (Map.Entry<ResourceLocation, Collection<T>> entry : built.entrySet()) {
            List<T> kept = new ArrayList<>();
            for (T value : entry.getValue()) {
                ResourceLocation id = ItemSelection.id(value);
                if (id == null || !ids.contains(id)) { kept.add(value); }
            }
            int gone = entry.getValue().size() - kept.size();
            if (gone == 0) { continue; }
            removed += gone;
            entry.setValue(ImmutableSet.copyOf(kept));
        }
        return removed;
    }

    private static <T> void rebuild(Map<ResourceLocation, Collection<T>> built, Set<ResourceLocation> ids, boolean items) {
        for (Map.Entry<ResourceLocation, Collection<T>> entry : built.entrySet()) {
            ImmutableSet.Builder<T> kept = ImmutableSet.builder();
            if (!SELECTION.tags().contains(entry.getKey())) {
                for (T value : entry.getValue()) {
                    if (!ids.contains(ItemSelection.id(value))) { kept.add(value); }
                }
            }
            ImmutableSet<T> left = kept.build();
            if (items && left.isEmpty() && !entry.getValue().isEmpty()) { EMPTIED.add(entry.getKey()); }
            entry.setValue(left);
        }
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        SELECTION.clear();
        HIDDEN.clear();
        UNTAGGED.clear();
        FLUIDS.clear();
        HIDDEN_CATEGORIES.clear();
        if (!Config.content.disabled()) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "disabled file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        (Json.getBoolean(json, "hideOnly", false) ? HIDDEN : SELECTION).read(json, key);
        UNTAGGED.readNames(Json.strings(json, "untag"), key);
        HIDDEN_CATEGORIES.addAll(Json.strings(json, "hideCategories"));
        for (String fluid : Json.strings(json, "fluids")) {
            ResourceLocation id = ResourceLocation.tryParse(Settings.lower(fluid));
            if (id == null) { ContentLog.LOGGER.error("Fluid '{}' in {} is not a valid name, skipping it", fluid, key); }
            else { FLUIDS.add(id); }
        }
    }
}
