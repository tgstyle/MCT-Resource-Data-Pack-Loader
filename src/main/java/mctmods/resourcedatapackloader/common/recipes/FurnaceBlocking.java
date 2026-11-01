package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.config.Config;

import java.util.Collections;
import net.minecraft.world.item.ItemStack;

public final class FurnaceBlocking {
    private static final ModBlocking BLOCKING = new ModBlocking("furnaceWhitelist", "blockedFurnaceMods", "blockFurnaceRecipes", "furnace.blocked", "furnace recipe(s)");

    private FurnaceBlocking() {}

    public static void reload() { BLOCKING.reload(Config.recipes.furnaceWhitelist(), Config.recipes.blockedFurnaceMods(), Config.recipes.blockFurnaceRecipes()); }

    public static boolean blocks(ItemStack result) {
        String owner = RecipeBlocking.owner(result);
        return owner != null && BLOCKING.blocks(() -> Collections.singleton(owner));
    }

    public static void report() { BLOCKING.report(); }
}
