package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.itemlocks.ContentItemLocks;
import mctmods.resourcedatapackloader.common.blocks.compress.CompressedRecipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class RecipeEvents {
    private RecipeEvents() {}

    @SubscribeEvent public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        RecipeOverrides.registerAdditions(event.getRegistry());
        RecipeSpared.snapshot(event.getRegistry());
        CompressedRecipes.register(event.getRegistry());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onRemoveRecipes(RegistryEvent.Register<IRecipe> event) {
        RecipeSpared.settle(event.getRegistry());
        RecipeRemovals.apply(event.getRegistry());
        RecipeLocks.reload();
        ContentItemLocks.load();
        RecipeBlocking.apply(event.getRegistry());
    }
}
