package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentDisabled {
    public static final Identifier TAG = ResourceDataPackLoader.id("disabled");
    public static final TagKey<Item> ITEMS = TagKey.create(Registries.ITEM, TAG);
    public static final TagKey<Block> BLOCKS = TagKey.create(Registries.BLOCK, TAG);
    public static final TagKey<Item> HIDDEN = TagKey.create(Registries.ITEM, ResourceDataPackLoader.id("hidden"));
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String BLOCK_TAGS = Registries.tagsDirPath(Registries.BLOCK);
    private static final String FLUID_TAGS = Registries.tagsDirPath(Registries.FLUID);
    private static final ItemSelection SELECTION = new ItemSelection("Disabled", "disables");
    private static final ItemSelection HIDE = new ItemSelection("Hidden", "hides");
    private static final ItemSelection UNTAG_ITEMS = new ItemSelection("Untagged", "untags");
    private static final ItemSelection UNTAG_FLUIDS = new ItemSelection("Untagged fluid", "untags");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String EMPTY_TAG = "Empty Tag: ";
    private static final Set<Identifier> EMPTIED = ConcurrentHashMap.newKeySet();
    private static final Set<String> CATEGORIES = ConcurrentHashMap.newKeySet();
    private static volatile boolean active;

    private ContentDisabled() {}

    public static boolean disabled(ItemStack stack) { return !stack.isEmpty() && stack.is(ITEMS); }

    public static boolean disabled(BlockState state) { return state.is(BLOCKS); }

    public static boolean hidden(ItemStack stack) { return !stack.isEmpty() && stack.is(HIDDEN); }

    public static boolean none() { return !active; }

    public static Set<String> hiddenCategories() {
        reload();
        return Set.copyOf(CATEGORIES);
    }

    public static boolean emptiedTag(ItemStack stack) {
        if (!stack.is(Items.BARRIER)) { return false; }
        String name = stack.getHoverName().getString();
        if (!name.startsWith(EMPTY_TAG)) { return false; }
        Identifier tag = Identifier.tryParse(name.substring(EMPTY_TAG.length()));
        return tag != null && EMPTIED.contains(tag);
    }

    public static <T> void strip(String directory, Map<Identifier, List<T>> built, Function<Identifier, Optional<? extends T>> lookup) {
        boolean items = ITEM_TAGS.equals(directory);
        boolean fluids = FLUID_TAGS.equals(directory);
        if (!items && !fluids && !BLOCK_TAGS.equals(directory)) { return; }
        if (items) {
            EMPTIED.clear();
            active = false;
        }
        reload();
        if (fluids) {
            untag(built, UNTAG_FLUIDS, UNTAG_FLUIDS.fluids(), "fluid");
            return;
        }
        if (items) {
            untag(built, UNTAG_ITEMS, UNTAG_ITEMS.items(), "item");
            hide(built, lookup);
        }
        if (SELECTION.unnamed()) { return; }
        Set<Identifier> ids = items ? SELECTION.items() : SELECTION.blocks();
        SELECTION.tagged(built, ids, tag -> ContentLog.LOGGER.debug("Disabled tag {} is no {} tag", tag, items ? "item" : "block"));
        if (items) { SELECTION.unknown(); }
        List<T> values = new ArrayList<>();
        for (Identifier id : ids) { lookup.apply(id).ifPresent(values::add); }
        rebuild(built, ids, items);
        built.put(TAG, ImmutableSet.copyOf(values).asList());
        if (items) { active = true; }
        Summary.info(items ? "disabled.items" : "disabled.blocks", "Disabled " + values.size() + (items ? " item(s)" : " block(s)"));
    }

    private static <T> void untag(Map<Identifier, List<T>> built, ItemSelection selection, Set<Identifier> ids, String kind) {
        if (selection.unnamed()) { return; }
        selection.tagged(built, ids, tag -> ContentLog.LOGGER.debug("Untagged tag {} is no {} tag", tag, kind));
        int removed = 0;
        for (Map.Entry<Identifier, List<T>> entry : built.entrySet()) {
            List<T> kept = entry.getValue().stream().filter(value -> !ids.contains(ItemSelection.id(value))).toList();
            removed += entry.getValue().size() - kept.size();
            entry.setValue(kept);
        }
        Summary.info("untagged." + kind + "s", "Removed " + removed + " tag entry/entries for untagged " + kind + "s");
    }

    private static <T> void hide(Map<Identifier, List<T>> built, Function<Identifier, Optional<? extends T>> lookup) {
        if (HIDE.unnamed()) { return; }
        Set<Identifier> ids = HIDE.items();
        HIDE.tagged(built, ids, tag -> ContentLog.LOGGER.debug("Hidden tag {} is no item tag", tag));
        HIDE.unknown();
        List<T> values = new ArrayList<>();
        for (Identifier id : ids) { lookup.apply(id).ifPresent(values::add); }
        built.put(HIDDEN.location(), ImmutableSet.copyOf(values).asList());
        Summary.info("hidden", "Hid " + values.size() + " item(s) in JEI");
    }

    private static <T> void rebuild(Map<Identifier, List<T>> built, Set<Identifier> ids, boolean items) {
        for (Map.Entry<Identifier, List<T>> entry : built.entrySet()) {
            ImmutableSet.Builder<T> kept = ImmutableSet.builder();
            if (!SELECTION.tags().contains(entry.getKey())) {
                for (T value : entry.getValue()) {
                    if (!ids.contains(ItemSelection.id(value))) { kept.add(value); }
                }
            }
            ImmutableSet<T> left = kept.build();
            if (items && left.isEmpty() && !entry.getValue().isEmpty()) { EMPTIED.add(entry.getKey()); }
            entry.setValue(left.asList());
        }
    }

    private static synchronized void reload() {
        if (!GENERATION.stale()) { return; }
        SELECTION.clear();
        HIDE.clear();
        UNTAG_ITEMS.clear();
        UNTAG_FLUIDS.clear();
        CATEGORIES.clear();
        if (!Config.content.disabled()) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
    }

    private static void read(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "disabled file");
        if (json == null) { return; }
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        (Json.getBoolean(json, "hideOnly", false) ? HIDE : SELECTION).read(json, key);
        UNTAG_ITEMS.readNames(Json.strings(json, "untag"), key);
        UNTAG_FLUIDS.readNames(Json.strings(json, "fluids"), key);
        CATEGORIES.addAll(Json.strings(json, "hideCategories"));
    }
}
