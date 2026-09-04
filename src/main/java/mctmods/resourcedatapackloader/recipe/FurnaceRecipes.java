package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class FurnaceRecipes {
    private static final String REMOVE = "remove";
    private static final String ADD = "add";
    private static final String INPUT = "input";
    private static final String RESULT = "result";
    private static final String OUTPUT = "output";
    private static final String COUNT = "count";
    private static final String EXPERIENCE = "experience";
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<Removal> REMOVALS = new ArrayList<>();
    private static final List<Addition> ADDITIONS = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private FurnaceRecipes() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        REMOVALS.clear();
        ADDITIONS.clear();
        if (!Config.recipes.furnace()) { return; }
        Json.eachFile(PackManager.FURNACE, "furnace file", FurnaceRecipes::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Furnace file {} is empty, ignoring it", key);
            return;
        }
        if (json.has(REMOVE)) {
            for (JsonElement element : GsonHelper.getAsJsonArray(json, REMOVE)) { removal(key, element); }
        }
        if (json.has(ADD)) {
            int index = 0;
            for (JsonElement element : GsonHelper.getAsJsonArray(json, ADD)) { addition(key, element, index++); }
        }
    }

    private static void removal(ResourceLocation key, JsonElement element) {
        JsonObject json = element.isJsonObject() ? element.getAsJsonObject() : null;
        String result = json == null ? element.getAsString() : GsonHelper.getAsString(json, RESULT, "");
        String input = json == null ? "" : GsonHelper.getAsString(json, INPUT, "");
        Item resultItem = result.isEmpty() ? null : ContentStacks.find(key, result);
        Item inputItem = input.isEmpty() ? null : ContentStacks.find(key, input);
        if (resultItem == null && inputItem == null) {
            ContentLog.LOGGER.error("A removal in {} names neither an input nor a result, skipping it", key);
            return;
        }
        REMOVALS.add(new Removal(key, result.isEmpty() ? input : result, inputItem, resultItem));
    }

    private static void addition(ResourceLocation key, JsonElement element, int index) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("An addition in {} is not an object, skipping it", key);
            return;
        }
        JsonObject json = element.getAsJsonObject();
        String inputName = GsonHelper.getAsString(json, INPUT, "");
        String outputName = GsonHelper.getAsString(json, OUTPUT, "");
        if (inputName.isEmpty() || outputName.isEmpty()) {
            ContentLog.LOGGER.error("An addition in {} needs both an input and an output, skipping it", key);
            return;
        }
        Item input = ContentStacks.find(key, inputName);
        ItemStack output = ContentStacks.parse(key, outputName, GsonHelper.getAsInt(json, COUNT, 1));
        if (input == null || output.isEmpty()) { return; }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), PackManager.FURNACE + "/" + key.getPath() + "/" + index);
        ADDITIONS.add(new Addition(id, input, output, GsonHelper.getAsFloat(json, EXPERIENCE, 0.0F)));
    }

    public static boolean removes(List<Ingredient> ingredients, ItemStack result, boolean withInputs) {
        boolean matched = false;
        for (Removal removal : REMOVALS) {
            if (removal.input != null && !withInputs) { continue; }
            if (removal.result != null && (result.isEmpty() || result.getItem() != removal.result)) { continue; }
            if (removal.input != null && !accepts(ingredients, removal.input)) { continue; }
            removal.matched++;
            matched = true;
        }
        return matched;
    }

    private static boolean accepts(List<Ingredient> ingredients, Item input) {
        ItemStack probe = new ItemStack(input);
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(probe)) { return true; }
        }
        return false;
    }

    public static List<Addition> additions() { return Collections.unmodifiableList(ADDITIONS); }

    public static boolean resolvedAtLoad() {
        for (Removal removal : REMOVALS) {
            if (removal.input != null) { return false; }
        }
        return true;
    }

    public static void report(int removed, int added) {
        Map<String, int[]> perPack = new LinkedHashMap<>();
        for (Removal removal : REMOVALS) {
            if (removal.matched == 0) { ContentLog.LOGGER.debug("No furnace recipe matched the removal {} in {}", removal.asked, removal.key); }
            perPack.computeIfAbsent(removal.key.getNamespace(), k -> new int[2])[0] += removal.matched;
            removal.matched = 0;
        }
        for (Addition addition : ADDITIONS) { perPack.computeIfAbsent(addition.id().getNamespace(), k -> new int[2])[1]++; }
        if (removed == 0 && added == 0) { return; }
        Summary.info("furnace", "Removed " + removed + " and added " + added + " furnace recipe(s)");
        if (perPack.size() < 2) { return; }
        for (Map.Entry<String, int[]> entry : perPack.entrySet()) {
            ContentLog.LOGGER.debug("  {} removed {} and added {}", entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
    }

    public record Addition(ResourceLocation id, Item input, ItemStack output, float experience) {}

    private static final class Removal {
        final ResourceLocation key;
        final String asked;
        @Nullable final Item input;
        @Nullable final Item result;
        int matched;

        Removal(ResourceLocation key, String asked, @Nullable Item input, @Nullable Item result) {
            this.key = key;
            this.asked = asked;
            this.input = input;
            this.result = result;
        }
    }
}
