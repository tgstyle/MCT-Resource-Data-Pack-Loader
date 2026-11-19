package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;

public final class RecipeTransfer {
    private static final String ENCHANTMENTS = "enchantments";
    private static final String NAME = "name";
    private static final String DAMAGE = "damage";
    private static final String NBT = "nbt";
    private static final Set<String> PARTS = Set.of(ENCHANTMENTS, NAME, DAMAGE, NBT);
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Set<Identifier> GIVEN = new HashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeTransfer() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        ENTRIES.clear();
        if (!Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe transfer file", RecipeTransfer::read);
    }

    public static void begin() { GIVEN.clear(); }

    public static RecipeHolder<?> wrap(RecipeHolder<?> holder, ItemStack result) {
        Identifier id = holder.id().identifier();
        if (holder.value() instanceof TransferShaped || holder.value() instanceof TransferShapeless) {
            GIVEN.add(id);
            return holder;
        }
        if (ENTRIES.isEmpty() || !(holder.value() instanceof CraftingRecipe recipe)) { return holder; }
        List<Transfer> wanted = new ArrayList<>();
        for (Entry entry : ENTRIES) {
            if (entry.scope.matches(id, recipe, result)) { wanted.add(entry.transfer); }
        }
        if (wanted.isEmpty()) { return holder; }
        RecipeHolder<?> made = transferred(holder.id(), recipe, wanted);
        if (made == null) {
            ContentLog.LOGGER.error("Recipe {} is not a plain shaped or shapeless crafting recipe, so it was given no transfer", id);
            return holder;
        }
        GIVEN.add(id);
        return made;
    }

    @Nullable private static RecipeHolder<?> transferred(ResourceKey<Recipe<?>> key, CraftingRecipe recipe, List<Transfer> wanted) {
        if (recipe.getClass() == ShapedRecipe.class) { return new RecipeHolder<>(key, new TransferShaped((ShapedRecipe) recipe, wanted)); }
        if (recipe.getClass() == ShapelessRecipe.class) { return new RecipeHolder<>(key, new TransferShapeless((ShapelessRecipe) recipe, wanted)); }
        return null;
    }

    public static void report() {
        if (!GIVEN.isEmpty()) { Summary.info("recipes.transfer", "Gave " + GIVEN.size() + " crafting recipe(s) a transfer"); }
    }

    private static void read(Identifier key, JsonObject json) {
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

    @Nullable private static Transfer transfer(Identifier key, JsonObject json) {
        Predicate<ItemStack> from = matcher(key, Json.getString(json, "from", ""));
        Predicate<ItemStack> source = matcher(key, Json.getString(json, "source", ""));
        Set<String> take = new HashSet<>(Json.strings(json, "take"));
        if (from == null || source == null || take.isEmpty() || !PARTS.containsAll(take)) { return null; }
        return new Transfer(from, source, take);
    }

    @Nullable private static Predicate<ItemStack> matcher(Identifier key, String text) {
        return switch (text) {
            case "" -> null;
            case "armor" -> stack -> stack.get(DataComponents.EQUIPPABLE) instanceof Equippable equippable && equippable.slot().isArmor();
            case "tool" -> stack -> stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.AXES) || stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES) || stack.is(Items.SHEARS);
            case "weapon" -> stack -> stack.is(ItemTags.SWORDS) || stack.is(Items.BOW);
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
            ItemStack added = input.getItem(extra);
            result.applyComponents(input.getItem(base).getComponentsPatch());
            if (take.contains(NBT)) { mergeData(result, added); }
            if (take.contains(DAMAGE) && result.isDamageableItem() && added.isDamageableItem()) { result.setDamageValue(added.getDamageValue()); }
            Component name = added.get(DataComponents.CUSTOM_NAME);
            if (take.contains(NAME) && name != null) { result.set(DataComponents.CUSTOM_NAME, name); }
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
            if (data != null) { CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.merge(data.copyTag())); }
        }

        private static DataComponentType<ItemEnchantments> held(ItemStack stack) { return stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS; }

        private static void mergeEnchantments(ItemStack result, ItemStack added) {
            boolean book = result.is(Items.ENCHANTED_BOOK);
            DataComponentType<ItemEnchantments> type = held(result);
            ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(result.getOrDefault(type, ItemEnchantments.EMPTY));
            for (Object2IntMap.Entry<Holder<Enchantment>> wanted : added.getOrDefault(held(added), ItemEnchantments.EMPTY).entrySet()) {
                Holder<Enchantment> enchantment = wanted.getKey();
                if (!book && !result.supportsEnchantment(enchantment) || clashes(merged, enchantment)) { continue; }
                merged.upgrade(enchantment, wanted.getIntValue());
            }
            result.set(type, merged.toImmutable());
        }

        private static boolean clashes(ItemEnchantments.Mutable present, Holder<Enchantment> enchantment) {
            for (Holder<Enchantment> other : present.keySet()) {
                if (!other.equals(enchantment) && !Enchantment.areCompatible(enchantment, other)) { return true; }
            }
            return false;
        }
    }
}
