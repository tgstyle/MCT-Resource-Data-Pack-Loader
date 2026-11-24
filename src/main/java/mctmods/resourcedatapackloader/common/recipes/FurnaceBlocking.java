package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.item.ItemStack;

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
        int removed = FurnaceRecipes.removeWhere((in, out) -> !TRUSTED_OUTPUTS.contains(out) && rejects(out));
        BLOCKING.report(", " + removed + " of them already registered before the block list could be read");
    }
}
