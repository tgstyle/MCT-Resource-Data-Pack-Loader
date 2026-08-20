package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IFurnaceRecipes;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FurnaceRecipes {
    private static final Gson GSON = new GsonBuilder().create();
    private static int generation = -1;

    private FurnaceRecipes() {}

    public static void reload() {
        if (!Config.recipes.furnace) { return; }
        if (generation == PackManager.get().getGeneration()) { return; }
        generation = PackManager.get().getGeneration();
        int[] counts = new int[2];
        Map<String, int[]> perPack = new LinkedHashMap<>();
        PackManager.get().forEach(PackManager.FURNACE, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            int[] mine = perPack.computeIfAbsent(namespace, k -> new int[2]);
            try { apply(key, contents, mine); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in furnace file {}, ignoring it: {}", key, ex.getMessage()); }
        });
        for (int[] mine : perPack.values()) {
            counts[0] += mine[0];
            counts[1] += mine[1];
        }
        if (counts[0] == 0 && counts[1] == 0) { return; }
        Summary.info("furnace", "Removed " + counts[0] + " and added " + counts[1] + " furnace recipe(s)");
        if (perPack.size() < 2) { return; }
        for (Map.Entry<String, int[]> entry : perPack.entrySet()) {
            ContentLog.LOGGER.debug("  {} removed {} and added {}", entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
    }

    private static void apply(ResourceLocation key, String contents, int[] counts) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Furnace file {} is empty, ignoring it", key);
            return;
        }
        if (json.has("remove")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "remove")) { counts[0] += remove(key, element); }
        }
        if (json.has("add")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "add")) {
                if (add(key, element)) { counts[1]++; }
            }
        }
    }

    private static int remove(ResourceLocation key, JsonElement element) {
        JsonObject json = element.isJsonObject() ? element.getAsJsonObject() : null;
        String result = json == null ? element.getAsString() : JsonUtils.getString(json, "result", "");
        String input = json == null ? "" : JsonUtils.getString(json, "input", "");
        ItemStack resultStack = result.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(key, result, 1);
        ItemStack inputStack = input.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(key, input, 1);
        if (resultStack.isEmpty() && inputStack.isEmpty()) {
            ContentLog.LOGGER.error("A removal in {} names neither an input nor a result, skipping it", key);
            return 0;
        }
        int removed = 0;
        Map<ItemStack, ItemStack> smelting = net.minecraft.item.crafting.FurnaceRecipes.instance().getSmeltingList();
        Map<ItemStack, Float> experience = ((IFurnaceRecipes) net.minecraft.item.crafting.FurnaceRecipes.instance()).rdpl$getExperienceList();
        Iterator<Map.Entry<ItemStack, ItemStack>> iterator = smelting.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ItemStack, ItemStack> recipe = iterator.next();
            if (!resultStack.isEmpty() && differs(resultStack, recipe.getValue())) { continue; }
            if (!inputStack.isEmpty() && differs(inputStack, recipe.getKey())) { continue; }
            experience.remove(recipe.getValue());
            iterator.remove();
            removed++;
        }
        if (removed == 0) { ContentLog.LOGGER.debug("No furnace recipe matched the removal {} in {}", result.isEmpty() ? input : result, key); }
        return removed;
    }

    private static boolean add(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("An addition in {} is not an object, skipping it", key);
            return false;
        }
        JsonObject json = element.getAsJsonObject();
        String inputName = JsonUtils.getString(json, "input", "");
        String outputName = JsonUtils.getString(json, "output", "");
        if (inputName.isEmpty() || outputName.isEmpty()) {
            ContentLog.LOGGER.error("An addition in {} needs both an input and an output, skipping it", key);
            return false;
        }
        ItemStack input = ContentStacks.parse(key, inputName, 1);
        ItemStack output = ContentStacks.parse(key, outputName, JsonUtils.getInt(json, "count", 1));
        if (input.isEmpty() || output.isEmpty()) { return false; }
        net.minecraft.item.crafting.FurnaceRecipes.instance().addSmeltingRecipe(input, output, JsonUtils.getFloat(json, "experience", 0.0F));
        return true;
    }

    private static boolean differs(ItemStack wanted, ItemStack found) {
        if (found.isEmpty() || wanted.getItem() != found.getItem()) { return true; }
        return wanted.getMetadata() != OreDictionary.WILDCARD_VALUE && wanted.getMetadata() != found.getMetadata();
    }
}
