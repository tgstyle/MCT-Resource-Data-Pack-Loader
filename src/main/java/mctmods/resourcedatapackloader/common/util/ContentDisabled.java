package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ContentDisabled {
    private static final List<String> KEYS = Collections.unmodifiableList(Arrays.asList("requires", "hideOnly", "untag", "hideCategories", "fluids", "names", "namespaces", "oreDict"));
    private static final ItemSelection SELECTION = new ItemSelection("Disabled", "disables");
    private static final ItemSelection HIDDEN = new ItemSelection("Hidden", "hides");
    private static final ItemSelection UNTAGGED = new ItemSelection("Untagged", "untags");
    private static final Set<String> HIDDEN_CATEGORIES = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentDisabled() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.disabled) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
        resolveHidden();
        resolveUntagged();
        if (SELECTION.unnamed()) { return; }
        SELECTION.resolve();
        if (!any()) { return; }
        Summary.info("disabled", "Disabled " + SELECTION.itemCount() + " item(s), " + SELECTION.variantCount() + " item variant(s) and " + SELECTION.blockCount() + " block(s)");
    }

    private static void resolveHidden() {
        if (HIDDEN.unnamed()) { return; }
        HIDDEN.resolve();
        Summary.info("hidden", "Hid " + HIDDEN.itemCount() + " item(s) and " + HIDDEN.variantCount() + " item variant(s) in JEI");
    }

    private static void resolveUntagged() {
        if (UNTAGGED.unnamed()) { return; }
        UNTAGGED.resolve();
    }

    public static Set<String> hiddenCategories() { return Collections.unmodifiableSet(HIDDEN_CATEGORIES); }

    public static boolean any() { return SELECTION.any(); }

    public static boolean anyHidden() { return HIDDEN.any(); }

    public static boolean anyUntagged() { return UNTAGGED.any(); }

    public static boolean untagged(ItemStack stack) { return UNTAGGED.matches(stack); }

    public static void eachHidden(Consumer<ItemStack> sink) { HIDDEN.eachStack(sink); }

    public static Set<String> oreNames() { return SELECTION.oreNames(); }

    public static boolean disabled(ItemStack stack) { return SELECTION.matches(stack); }

    public static boolean disabled(Block block) { return SELECTION.matches(block); }

    public static void eachStack(Consumer<ItemStack> sink) { SELECTION.eachStack(sink); }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "disabled file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        (Json.getBoolean(json, "hideOnly", false) ? HIDDEN : SELECTION).read(json, key);
        UNTAGGED.readNames(Json.strings(json, "untag"), key);
        HIDDEN_CATEGORIES.addAll(Json.strings(json, "hideCategories"));
        if (json.has("fluids")) { ContentLog.LOGGER.warn("Disabled file {} lists 'fluids', but 1.12.2 has no fluid tags, so it is ignored", key); }
    }
}
