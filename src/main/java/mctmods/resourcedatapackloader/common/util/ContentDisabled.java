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

public final class ContentDisabled {
    public static final ResourceLocation TAG = ResourceDataPackLoader.id("disabled");
    public static final TagKey<Item> ITEMS = TagKey.create(Registries.ITEM, TAG);
    public static final TagKey<Block> BLOCKS = TagKey.create(Registries.BLOCK, TAG);
    private static final String ITEM_TAGS = TagManager.getTagDir(Registries.ITEM);
    private static final String BLOCK_TAGS = TagManager.getTagDir(Registries.BLOCK);
    private static final ItemSelection SELECTION = new ItemSelection("Disabled", "disables");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String EMPTY_TAG = "Empty Tag: ";
    private static final Set<ResourceLocation> EMPTIED = ConcurrentHashMap.newKeySet();
    private static volatile boolean active;

    private ContentDisabled() {}

    public static boolean disabled(ItemStack stack) { return !stack.isEmpty() && stack.is(ITEMS); }

    public static boolean disabled(BlockState state) { return state.is(BLOCKS); }

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
        if (!items && !BLOCK_TAGS.equals(directory)) { return; }
        if (items) {
            EMPTIED.clear();
            active = false;
        }
        reload();
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
        if (!Config.content.disabled()) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "disabled file");
        if (json == null) { return; }
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        SELECTION.read(json, key);
    }
}
