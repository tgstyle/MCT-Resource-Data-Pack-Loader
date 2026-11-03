package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class CompressedRecipes {
    private static final int GRID = 9;
    private static final String ROW = "AAA";

    private CompressedRecipes() {}

    public static void register(Map<ResourceLocation, JsonElement> recipes) {
        int added = 0;
        for (CompressedSet set : ContentCompressables.sets()) {
            if (!set.def.recipes()) { continue; }
            for (int layer = 1; layer <= set.layers; layer++) {
                ItemStack lower = set.stack(layer - 1);
                ItemStack upper = set.stack(layer);
                if (lower.isEmpty() || upper.isEmpty()) {
                    ContentLog.LOGGER.error("The compressable {} has no registered item for layer {} or the layer below it, so its recipes stop there", set.def.key(), layer);
                    break;
                }
                String namespace = set.def.key().getNamespace();
                ResourceLocation packId = ResourceLocation.fromNamespaceAndPath(namespace, set.name + "_compress_" + layer);
                ResourceLocation unpackId = ResourceLocation.fromNamespaceAndPath(namespace, set.name + "_decompress_" + layer);
                if (recipes.containsKey(packId) || recipes.containsKey(unpackId)) {
                    ContentLog.LOGGER.error("A recipe named {} or {} already exists, so the compressable {} gets no recipes for layer {}", packId, unpackId, set.def.key(), layer);
                    continue;
                }
                recipes.put(packId, shaped(lower, upper));
                recipes.put(unpackId, shapeless(lower, upper));
                added += 2;
            }
        }
        if (added > 0) { Summary.info("recipes.compressed", "Registered " + added + " compressed block recipe(s)"); }
    }

    private static JsonObject shaped(ItemStack lower, ItemStack upper) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        JsonArray pattern = new JsonArray();
        for (int row = 0; row < 3; row++) { pattern.add(ROW); }
        json.add("pattern", pattern);
        JsonObject key = new JsonObject();
        key.add("A", item(lower));
        json.add("key", key);
        json.add("result", result(upper, 1));
        return json;
    }

    private static JsonObject shapeless(ItemStack lower, ItemStack upper) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients = new JsonArray();
        ingredients.add(item(upper));
        json.add("ingredients", ingredients);
        json.add("result", result(lower, GRID));
        return json;
    }

    private static JsonObject item(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        return json;
    }

    private static JsonObject result(ItemStack stack, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        json.addProperty("count", count);
        return json;
    }
}
