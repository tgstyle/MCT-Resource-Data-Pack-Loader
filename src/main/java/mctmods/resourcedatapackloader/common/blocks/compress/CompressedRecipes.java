package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class CompressedRecipes {
    private static final int GRID = 9;
    private static final String ROW = "AAA";
    private static Set<Identifier> ids = Set.of();

    private CompressedRecipes() {}

    public static void register(Map<Identifier, JsonElement> recipes) {
        Set<Identifier> added = new HashSet<>();
        for (CompressedSet set : ContentCompressables.sets()) {
            if (!set.def.recipes()) { continue; }
            for (int layer = 1; layer <= set.layers; layer++) {
                Item lower = set.item(layer - 1);
                Item upper = set.item(layer);
                if (lower == Items.AIR || upper == Items.AIR) {
                    ContentLog.LOGGER.error("The compressable {} has no registered item for layer {} or the layer below it, so its recipes stop there", set.def.key(), layer);
                    break;
                }
                String namespace = set.def.key().getNamespace();
                Identifier packId = Identifier.fromNamespaceAndPath(namespace, set.name + "_compress_" + layer);
                Identifier unpackId = Identifier.fromNamespaceAndPath(namespace, set.name + "_decompress_" + layer);
                if (recipes.containsKey(packId) || recipes.containsKey(unpackId)) {
                    ContentLog.LOGGER.error("A recipe named {} or {} already exists, so the compressable {} gets no recipes for layer {}", packId, unpackId, set.def.key(), layer);
                    continue;
                }
                recipes.put(packId, shaped(lower, upper));
                recipes.put(unpackId, shapeless(lower, upper));
                added.add(packId);
                added.add(unpackId);
            }
        }
        ids = Set.copyOf(added);
        if (!added.isEmpty()) { Summary.info("recipes.compressed", "Registered " + added.size() + " compressed block recipe(s)"); }
    }

    public static Set<Identifier> ids() { return ids; }

    private static JsonObject shaped(Item lower, Item upper) {
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

    private static JsonObject shapeless(Item lower, Item upper) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients = new JsonArray();
        ingredients.add(item(upper));
        json.add("ingredients", ingredients);
        json.add("result", result(lower, GRID));
        return json;
    }

    private static JsonPrimitive item(Item item) {
        return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    private static JsonObject result(Item item, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("id", BuiltInRegistries.ITEM.getKey(item).toString());
        json.addProperty("count", count);
        return json;
    }
}
