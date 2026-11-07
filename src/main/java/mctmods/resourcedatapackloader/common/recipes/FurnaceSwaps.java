package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;

public final class FurnaceSwaps {
    private static final Set<String> SKIPPED = new HashSet<>();
    private static int ingredients;
    private static int results;
    private static int recipes;

    private FurnaceSwaps() {}

    public static void apply() {
        List<SwapEntry> entries = SwapEntry.all();
        if (entries.isEmpty()) { return; }
        for (SwapEntry entry : entries) {
            if (!entry.scoped()) { swap(entry); }
        }
        RecipeSwaps.summarize(ingredients, results, recipes);
    }

    private static void swap(SwapEntry entry) {
        net.minecraft.item.crafting.FurnaceRecipes furnace = net.minecraft.item.crafting.FurnaceRecipes.instance();
        Map<ItemStack, ItemStack> smelting = furnace.getSmeltingList();
        Map<ItemStack, Float> experience = furnace.experienceList;
        for (ItemStack input : new ArrayList<>(smelting.keySet())) {
            ItemStack result = smelting.get(input);
            if (result == null) { continue; }
            boolean inputMatch = entry.matches(input) && !smelts(furnace, entry, input);
            boolean resultMatch = entry.output() && entry.matches(result);
            if (!inputMatch && !resultMatch) { continue; }
            ItemStack newInput = inputMatch ? entry.result(1) : input;
            ItemStack newResult = resultMatch ? entry.result(result.getCount()) : result;
            if (inputMatch) {
                smelting.remove(input);
                ingredients++;
            }
            smelting.put(newInput, newResult);
            if (resultMatch) {
                Float xp = experience.remove(result);
                if (xp != null) { experience.put(newResult, xp); }
                FurnaceBlocking.trustOutput(newResult);
                results++;
            }
            recipes++;
        }
    }

    private static boolean smelts(net.minecraft.item.crafting.FurnaceRecipes furnace, SwapEntry entry, ItemStack input) {
        if (furnace.getSmeltingResult(entry.result(1)).isEmpty()) { return false; }
        if (SKIPPED.add(input.toString() + entry.result(1))) { ContentLog.LOGGER.info("Did not swap the furnace input {}: {} already smelts", input, entry.result(1)); }
        return true;
    }
}
