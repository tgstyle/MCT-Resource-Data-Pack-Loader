package mctmods.resourcedatapackloader.advancement;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RecipeTolerance {
    private static final Set<ResourceLocation> MISSING = ConcurrentHashMap.newKeySet();
    private static final Set<ResourceLocation> REPORTED = ConcurrentHashMap.newKeySet();

    private RecipeTolerance() {}

    public static boolean disabled() { return !Config.recipes.tolerateMissingInAdvancements; }

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

    public static void flush() {
        if (MISSING.isEmpty()) {
            REPORTED.clear();
            return;
        }
        if (MISSING.equals(REPORTED)) {
            MISSING.clear();
            return;
        }
        ContentLog.LOGGER.warn("{} recipe(s) referenced by advancements no longer exist, so those advancements load but never unlock them ({}). Turn on debug logging for the full list.", MISSING.size(), breakdown());
        REPORTED.clear();
        REPORTED.addAll(MISSING);
        MISSING.clear();
    }

    private static String breakdown() {
        Map<String, Integer> counts = new HashMap<>();
        for (ResourceLocation name : MISSING) { counts.merge(name.getNamespace(), 1, Integer::sum); }
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((left, right) -> {
            int byCount = Integer.compare(right.getValue(), left.getValue());
            if (byCount != 0) { return byCount; }
            return left.getKey().compareTo(right.getKey());
        });
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sorted) {
            if (result.length() > 0) { result.append(", "); }
            result.append(entry.getValue()).append(' ').append(entry.getKey());
        }
        return result.toString();
    }

    private static void report(ResourceLocation name) {
        if (!MISSING.add(name)) { return; }
        ContentLog.LOGGER.debug("Recipe {} no longer exists, advancements referring to it will load but never unlock it", name);
    }
}
