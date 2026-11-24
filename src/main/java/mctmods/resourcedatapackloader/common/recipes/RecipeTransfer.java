package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class RecipeTransfer {
    private static final String KIND = "Recipe transfer";
    private static final Set<String> PARTS = new HashSet<>(Arrays.asList(Transfer.ENCHANTMENTS, Transfer.NAME, Transfer.DAMAGE, Transfer.NBT));
    private static final List<String> TRANSFER_KEYS = List.of("from", "source", "take");
    private static final List<Entry> ENTRIES = new ArrayList<>();
    @Nullable private static RecipeManager done;

    private RecipeTransfer() {}

    public static void apply(RecipeManager manager) {
        if (manager == done) { return; }
        done = manager;
        ENTRIES.clear();
        if (!Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe transfer file", RecipeTransfer::read);
        if (ENTRIES.isEmpty()) { return; }
        List<Recipe<?>> all = new ArrayList<>(manager.getRecipes());
        int given = 0;
        for (int i = 0; i < all.size(); i++) {
            Recipe<?> recipe = all.get(i);
            List<Transfer> wanted = wanted(recipe);
            if (wanted.isEmpty()) { continue; }
            CraftingRecipe wrapped = wrap(recipe, wanted);
            if (wrapped == null) {
                ContentLog.LOGGER.error("Recipe {} is not a plain shaped or shapeless recipe, so it cannot carry a transfer and was left alone", recipe.getId());
                continue;
            }
            all.set(i, wrapped);
            given++;
        }
        if (given == 0) { return; }
        manager.replaceRecipes(all);
        Summary.info("recipes.transfer", "Gave " + given + " crafting recipe(s) a transfer");
    }

    @Nullable private static CraftingRecipe wrap(Recipe<?> recipe, List<Transfer> transfers) {
        if (recipe.getClass() == ShapedRecipe.class) { return new TransferShaped((ShapedRecipe) recipe, transfers); }
        if (recipe.getClass() == ShapelessRecipe.class) { return new TransferShapeless((ShapelessRecipe) recipe, transfers); }
        return null;
    }

    private static List<Transfer> wanted(Recipe<?> recipe) {
        List<Transfer> wanted = new ArrayList<>();
        if (recipe.getType() != RecipeType.CRAFTING) { return wanted; }
        ItemStack result = result(recipe);
        for (Entry entry : ENTRIES) {
            if (entry.scope.matchesName(recipe.getId()) || entry.scope.matchesOutput(result) || entry.scope.matchesType(recipe) || entry.scope.matchesInput(recipe)) { wanted.add(entry.transfer); }
        }
        return wanted;
    }

    private static ItemStack result(Recipe<?> recipe) {
        try { return recipe.getResultItem(RegistryAccess.EMPTY); }
        catch (RuntimeException needsWorldRegistries) { return ItemStack.EMPTY; }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!json.has("transfer")) { return; }
        if (!json.has("swaps")) { Json.unknownKeys(json, "the top level", SwapEntry.FILE_KEYS); }
        if (RecipeSelector.lacksRecipes(KIND, key, json)) { return; }
        if (json.get("transfer").isJsonObject()) { Json.unknownKeys(json.getAsJsonObject("transfer"), "transfer", TRANSFER_KEYS); }
        Transfer transfer = json.get("transfer").isJsonObject() ? transfer(key, json.getAsJsonObject("transfer")) : null;
        if (transfer == null) {
            ContentLog.LOGGER.error("The transfer in {} needs a from, a source and a take list of enchantments, name, damage or nbt, ignoring it", key);
            return;
        }
        Json.unknownKeys(json.getAsJsonObject("recipes"), "recipes", RecipeSelector.KEYS);
        RecipeSelector scope = RecipeSelector.scopeOf(KIND, key, json.getAsJsonObject("recipes"));
        if (scope != null) { ENTRIES.add(new Entry(scope, transfer)); }
    }

    @Nullable private static Transfer transfer(ResourceLocation key, JsonObject json) {
        Predicate<ItemStack> from = matcher(key, Json.getString(json, "from", ""));
        Predicate<ItemStack> source = matcher(key, Json.getString(json, "source", ""));
        Set<String> take = new HashSet<>(Json.strings(json, "take"));
        if (from == null || source == null || take.isEmpty() || !PARTS.containsAll(take)) { return null; }
        return new Transfer(from, source, take);
    }

    @Nullable private static Predicate<ItemStack> matcher(ResourceLocation key, String text) {
        return switch (text) {
            case "" -> null;
            case "armor" -> stack -> stack.getItem() instanceof ArmorItem;
            case "tool" -> stack -> stack.getItem() instanceof DiggerItem || stack.getItem() instanceof ShearsItem;
            case "weapon" -> stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof BowItem;
            case "book" -> stack -> stack.is(Items.ENCHANTED_BOOK);
            default -> {
                ItemMatch match = ItemMatch.parse(key, text);
                yield match == null ? null : match::matches;
            }
        };
    }

    private record Entry(RecipeSelector scope, Transfer transfer) {}
}
