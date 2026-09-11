package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
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
    private static boolean off;

    private FurnaceBlocking() {}

    public static void reload() {
        BLOCKED.clear();
        off = ContentControl.off(ContentControl.RECIPES);
        whitelist = Settings.lower(ContentControl.list(ContentControl.RECIPES, "furnaceWhitelist", Config.recipes.furnaceWhitelist()));
        blocked = Settings.lower(ContentControl.list(ContentControl.RECIPES, "blockedFurnaceMods", Config.recipes.blockedFurnaceMods()));
        blockAll = ContentControl.flag(ContentControl.RECIPES, "blockFurnaceRecipes", Config.recipes.blockFurnaceRecipes());
    }

    public static boolean disabled() { return off || (!blockAll && blocked.isEmpty()); }

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
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes())) { BLOCKED.report("furnace recipe(s)"); }
    }
}
