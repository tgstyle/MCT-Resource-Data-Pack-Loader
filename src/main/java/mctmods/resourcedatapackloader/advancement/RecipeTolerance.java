package mctmods.resourcedatapackloader.advancement;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.core.MCTMixin;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RecipeTolerance {
    private static final Set<ResourceLocation> REPORTED = Collections.synchronizedSet(new HashSet<>());

    private RecipeTolerance() {}

    public static boolean disabled() { return !Config.settings.tolerateMissingRecipes; }

    public static IRecipe resolve(ResourceLocation name) {
        IRecipe recipe = CraftingManager.getRecipe(name);
        if (recipe != null) { return recipe; }
        report(name);
        return new MissingRecipe(name);
    }

    public static ResourceLocation[] existing(ResourceLocation[] names) {
        List<ResourceLocation> kept = new ArrayList<>(names.length);
        for (ResourceLocation name : names) {
            if (CraftingManager.getRecipe(name) != null) { kept.add(name); }
            else { report(name); }
        }
        if (kept.size() == names.length) { return names; }
        return kept.toArray(new ResourceLocation[0]);
    }

    private static void report(ResourceLocation name) {
        if (!REPORTED.add(name)) { return; }
        MCTMixin.LOGGER.warn("Recipe {} no longer exists, advancements referring to it will load but never unlock it", name);
    }
}
