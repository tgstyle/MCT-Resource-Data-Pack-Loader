package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class RecipeTransfer {
    private static final String ENCHANTMENTS = "enchantments";
    private static final String NAME = "name";
    private static final String DAMAGE = "damage";
    private static final String NBT = "nbt";
    private static final Set<String> PARTS = Set.of(ENCHANTMENTS, NAME, DAMAGE, NBT);
    private static final List<Entry> ENTRIES = new ArrayList<>();
    @Nullable private static RecipeManager manager;
    @Nullable private static HolderLookup.Provider registries;

    private RecipeTransfer() {}

    public static void attach(RecipeManager recipes, HolderLookup.Provider lookup) {
        manager = recipes;
        registries = lookup;
    }

    public static void apply() {
        ENTRIES.clear();
        RecipeManager recipes = manager;
        HolderLookup.Provider lookup = registries;
        if (recipes == null || lookup == null || !Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe transfer file", RecipeTransfer::read);
        if (ENTRIES.isEmpty()) { return; }
        List<RecipeHolder<?>> all = new ArrayList<>();
        int given = 0;
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            List<Transfer> wanted = wanted(holder, lookup);
            RecipeHolder<?> made = wanted.isEmpty() ? null : wrap(holder, lookup, wanted);
            if (made != null) { given++; }
            all.add(made == null ? holder : made);
        }
        if (given == 0) { return; }
        recipes.replaceRecipes(all);
        Summary.info("recipes.transfer", "Gave " + given + " crafting recipe(s) a transfer");
    }

    private static List<Transfer> wanted(RecipeHolder<?> holder, HolderLookup.Provider lookup) {
        List<Transfer> wanted = new ArrayList<>();
        ItemStack result = holder.value().getResultItem(lookup);
        for (Entry entry : ENTRIES) {
            if (entry.scope.matches(holder.id(), holder.value(), result)) { wanted.add(entry.transfer); }
        }
        return wanted;
    }

    @Nullable private static RecipeHolder<?> wrap(RecipeHolder<?> holder, HolderLookup.Provider lookup, List<Transfer> wanted) {
        if (holder.value().getClass() == ShapedRecipe.class) { return new RecipeHolder<>(holder.id(), new TransferShaped((ShapedRecipe) holder.value(), lookup, wanted)); }
        if (holder.value().getClass() == ShapelessRecipe.class) { return new RecipeHolder<>(holder.id(), new TransferShapeless((ShapelessRecipe) holder.value(), lookup, wanted)); }
        ContentLog.LOGGER.error("Recipe {} is not a plain shaped or shapeless crafting recipe, so it was given no transfer", holder.id());
        return null;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!json.has("transfer")) { return; }
        if (!json.has("recipes") || !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("Recipe transfer file {} needs a recipes object saying which recipes it covers, ignoring it", key);
            return;
        }
        Transfer transfer = json.get("transfer").isJsonObject() ? transfer(key, json.getAsJsonObject("transfer")) : null;
        if (transfer == null) {
            ContentLog.LOGGER.error("The transfer in {} needs a from, a source and a take list of enchantments, name, damage or nbt, ignoring it", key);
            return;
        }
        RecipeSelector scope = new RecipeSelector();
        scope.read(key, json.getAsJsonObject("recipes"));
        if (scope.isEmpty()) {
            ContentLog.LOGGER.error("Recipe transfer file {} selects no recipes, ignoring it", key);
            return;
        }
        ENTRIES.add(new Entry(scope, transfer));
    }

    @Nullable private static Transfer transfer(ResourceLocation key, JsonObject json) {
        Predicate<ItemStack> from = matcher(key, Json.getString(json, "from", ""));
        Predicate<ItemStack> source = matcher(key, Json.getString(json, "source", ""));
        Set<String> take = Set.copyOf(Json.strings(json, "take"));
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
                ItemSelection selection = RecipeRemainders.selection("Recipe transfer", key, text);
                yield selection.unnamed() ? null : selection::matches;
            }
        };
    }

    private record Entry(RecipeSelector scope, Transfer transfer) {}

    record Transfer(Predicate<ItemStack> from, Predicate<ItemStack> source, Set<String> take) {
        void carry(CraftingInput input, ItemStack result) {
            int base = find(input, from, -1);
            int extra = find(input, source, base);
            if (base < 0 || extra < 0 || result.isEmpty()) { return; }
            ItemStack kept = input.getItem(base);
            ItemStack added = input.getItem(extra);
            result.applyComponents(kept.getComponentsPatch());
            if (result.isDamageableItem() && kept.isDamageableItem()) { result.setDamageValue(kept.getDamageValue()); }
            else { result.remove(DataComponents.DAMAGE); }
            if (take.contains(NBT)) { mergeData(result, added); }
            if (take.contains(DAMAGE) && result.isDamageableItem() && added.isDamageableItem()) { result.setDamageValue(added.getDamageValue()); }
            if (take.contains(NAME) && added.has(DataComponents.CUSTOM_NAME)) { result.set(DataComponents.CUSTOM_NAME, added.get(DataComponents.CUSTOM_NAME)); }
            if (take.contains(ENCHANTMENTS)) { mergeEnchantments(result, added); }
        }

        private static int find(CraftingInput input, Predicate<ItemStack> wanted, int skip) {
            for (int slot = 0; slot < input.size(); slot++) {
                ItemStack stack = input.getItem(slot);
                if (slot != skip && !stack.isEmpty() && wanted.test(stack)) { return slot; }
            }
            return -1;
        }

        private static void mergeData(ItemStack result, ItemStack added) {
            CustomData data = added.get(DataComponents.CUSTOM_DATA);
            if (data == null) { return; }
            result.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, held -> held.update(tag -> tag.merge(data.copyTag())));
        }

        private static void mergeEnchantments(ItemStack result, ItemStack added) {
            ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
            for (Map.Entry<Holder<Enchantment>, Integer> wanted : mapOf(EnchantmentHelper.getEnchantmentsForCrafting(added)).entrySet()) {
                Holder<Enchantment> enchantment = wanted.getKey();
                if (!result.is(Items.ENCHANTED_BOOK) && !result.supportsEnchantment(enchantment)) { continue; }
                if (clashes(merged, enchantment)) { continue; }
                merged.upgrade(enchantment, wanted.getValue());
            }
            EnchantmentHelper.setEnchantments(result, merged.toImmutable());
        }

        private static Map<Holder<Enchantment>, Integer> mapOf(ItemEnchantments enchantments) {
            Map<Holder<Enchantment>, Integer> found = new LinkedHashMap<>();
            for (Holder<Enchantment> each : enchantments.keySet()) { found.put(each, enchantments.getLevel(each)); }
            return found;
        }

        private static boolean clashes(ItemEnchantments.Mutable present, Holder<Enchantment> enchantment) {
            for (Holder<Enchantment> other : present.keySet()) {
                if (!other.equals(enchantment) && !Enchantment.areCompatible(enchantment, other)) { return true; }
            }
            return false;
        }
    }
}
