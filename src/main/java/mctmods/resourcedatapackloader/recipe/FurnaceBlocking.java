package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Stacks;

import net.minecraft.item.ItemStack;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class FurnaceBlocking {
    private static final ModBlocking BLOCKING = new ModBlocking("furnaceWhitelist", () -> Config.recipes.furnaceWhitelist, "blockedFurnaceMods", () -> Config.recipes.blockedFurnaceMods, "blockFurnaceRecipes", () -> Config.recipes.blockFurnaceRecipes, "furnace.blocked", "furnace recipe(s)");
    private static final ThreadLocal<Boolean> TRUSTED = new ThreadLocal<>();
    private static final Set<String> SOURCES = new HashSet<>();
    private static final Set<ItemStack> TRUSTED_OUTPUTS = Collections.newSetFromMap(new IdentityHashMap<>());

    private FurnaceBlocking() {}

    public static void beginTrusted(String source) {
        if (SOURCES.add(source)) { ContentLog.LOGGER.info("Furnace recipes added by {} are trusted and will not be blocked", source); }
        TRUSTED.set(Boolean.TRUE);
    }

    public static void endTrusted() { TRUSTED.remove(); }

    public static void trustOutput(ItemStack output) { TRUSTED_OUTPUTS.add(output); }

    public static boolean rejects(ItemStack result) {
        if (BLOCKING.disabled()) { return false; }
        if (Boolean.TRUE.equals(TRUSTED.get())) {
            TRUSTED_OUTPUTS.add(result);
            return false;
        }
        String owner = Stacks.namespace(result);
        return owner != null && BLOCKING.blocks(Collections.singleton(owner));
    }

    public static void apply() {
        if (BLOCKING.disabled()) { return; }
        Map<ItemStack, ItemStack> smelting = net.minecraft.item.crafting.FurnaceRecipes.instance().getSmeltingList();
        Map<ItemStack, Float> experience = net.minecraft.item.crafting.FurnaceRecipes.instance().experienceList;
        Iterator<Map.Entry<ItemStack, ItemStack>> iterator = smelting.entrySet().iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            Map.Entry<ItemStack, ItemStack> entry = iterator.next();
            if (TRUSTED_OUTPUTS.contains(entry.getValue())) { continue; }
            if (!rejects(entry.getValue())) { continue; }
            experience.remove(entry.getValue());
            iterator.remove();
            removed++;
        }
        BLOCKING.report(", " + removed + " of them already registered before the block list could be read");
    }
}
