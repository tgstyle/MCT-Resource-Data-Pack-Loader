package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;

public final class RecipeSpared {
    private static final Set<ResourceLocation> BEFORE = new HashSet<>();
    private static final Set<ItemStack> FURNACE_BEFORE = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<ResourceLocation> SPARED = new HashSet<>();
    private static boolean snapped;
    private static final Set<String> SCRIPTED = new HashSet<>(Arrays.asList("crafttweaker", "kubejs", "groovyscript"));

    private RecipeSpared() {}

    public static void snapshot(IForgeRegistry<IRecipe> registry) {
        BEFORE.clear();
        BEFORE.addAll(registry.getKeys());
        FURNACE_BEFORE.clear();
        FURNACE_BEFORE.addAll(FurnaceRecipes.instance().getSmeltingList().values());
        SPARED.clear();
        snapped = true;
    }

    public static void settle(IForgeRegistry<IRecipe> registry) {
        if (!snapped) { return; }
        for (ResourceLocation key : registry.getKeys()) {
            if (!BEFORE.contains(key)) { SPARED.add(key); }
        }
        int furnace = 0;
        for (ItemStack output : FurnaceRecipes.instance().getSmeltingList().values()) {
            if (FURNACE_BEFORE.contains(output)) { continue; }
            FurnaceBlocking.trustOutput(output);
            furnace++;
        }
        if (!SPARED.isEmpty() || furnace > 0) { ContentLog.LOGGER.info("{} crafting and {} furnace recipe(s) another mod registered after this mod's own are spared from removal and blocking", SPARED.size(), furnace); }
        BEFORE.clear();
        FURNACE_BEFORE.clear();
    }

    public static boolean spares(ResourceLocation key) { return SPARED.contains(key) || SCRIPTED.contains(key.getNamespace().toLowerCase(Locale.ROOT)); }
}
