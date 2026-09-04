package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.world.item.ItemStack;
import java.util.Collections;
import java.util.Set;

public final class FurnaceBlocking {
    private static final Blocked BLOCKED = new Blocked();
    private static Set<String> whitelist = Collections.emptySet();
    private static Set<String> blocked = Collections.emptySet();
    private static boolean blockAll;

    private FurnaceBlocking() {}

    public static void reload() {
        BLOCKED.clear();
        whitelist = Settings.lower(Config.recipes.furnaceWhitelist());
        blocked = Settings.lower(Config.recipes.blockedFurnaceMods());
        blockAll = Config.recipes.blockFurnaceRecipes();
    }

    public static boolean disabled() { return !blockAll && blocked.isEmpty(); }

    public static boolean blocks(ItemStack result) {
        if (disabled()) { return false; }
        String owner = RecipeBlocking.owner(result);
        if (owner == null) { return false; }
        if (blocked.contains(owner)) { return count(owner); }
        if (blockAll && !whitelist.contains(owner)) { return count(owner); }
        return false;
    }

    private static boolean count(String owner) {
        BLOCKED.count(owner);
        return true;
    }

    public static void report() {
        int total = BLOCKED.total();
        if (total == 0) { return; }
        Summary.info("furnace.blocked", "Blocked " + total + " furnace recipe(s)");
        if (Config.recipes.logBlockedRecipes()) { BLOCKED.report("furnace recipe(s)"); }
    }
}
