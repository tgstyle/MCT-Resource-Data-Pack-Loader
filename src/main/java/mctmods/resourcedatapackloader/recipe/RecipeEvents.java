package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID)
public final class RecipeEvents {

    private RecipeEvents() {}

    @SubscribeEvent public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) { RecipeOverrides.registerAdditions(event.getRegistry()); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onRemoveRecipes(RegistryEvent.Register<IRecipe> event) {
        RecipeRemovals.apply(event.getRegistry());
        RecipeBlocking.apply(event.getRegistry());
    }
}
