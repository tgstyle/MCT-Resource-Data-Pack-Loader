package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ContentDisabled {
    private static final ItemSelection SELECTION = new ItemSelection("Disabled", "disables");
    private static boolean loaded;

    private ContentDisabled() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.disabled) { return; }
        Json.eachFile(PackManager.DISABLED, "disabled file", ContentDisabled::read);
        if (SELECTION.unnamed()) { return; }
        SELECTION.resolve();
        if (!any()) { return; }
        Summary.info("disabled", "Disabled " + SELECTION.itemCount() + " item(s), " + SELECTION.variantCount() + " item variant(s) and " + SELECTION.blockCount() + " block(s)");
    }

    public static boolean any() { return SELECTION.any(); }

    public static Set<String> oreNames() { return SELECTION.oreNames(); }

    public static boolean disabled(ItemStack stack) { return SELECTION.matches(stack); }

    public static boolean disabled(Block block) { return SELECTION.matches(block); }

    public static void eachStack(Consumer<ItemStack> sink) { SELECTION.eachStack(sink); }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "disabled file");
        if (json == null) { return; }
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        SELECTION.read(json, key);
    }
}
