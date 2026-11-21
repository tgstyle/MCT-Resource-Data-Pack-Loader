package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public final class RecipeTransfer {
    private static final String ENCHANTMENTS = "enchantments";
    private static final String NAME = "name";
    private static final String DAMAGE = "damage";
    private static final String NBT = "nbt";
    private static final Set<String> PARTS = new HashSet<>(Arrays.asList(ENCHANTMENTS, NAME, DAMAGE, NBT));
    private static final List<String> TRANSFER_KEYS = Collections.unmodifiableList(Arrays.asList("from", "source", "take"));
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private RecipeTransfer() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        ENTRIES.clear();
        if (!Config.recipes.swaps) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe transfer file", RecipeTransfer::read);
        if (ENTRIES.isEmpty()) { return; }
        IForgeRegistryModifiable<IRecipe> modifiable = RecipeRemovals.modifiable(registry);
        if (modifiable == null) {
            ContentLog.LOGGER.error("The recipe registry cannot be modified, no recipe was given a transfer");
            return;
        }
        Map<ResourceLocation, List<Transfer>> byRecipe = new LinkedHashMap<>();
        for (ResourceLocation key : registry.getKeys()) {
            IRecipe recipe = registry.getValue(key);
            if (recipe == null) { continue; }
            for (Entry entry : ENTRIES) {
                if (entry.scope.matches(key, recipe)) { byRecipe.computeIfAbsent(key, k -> new ArrayList<>()).add(entry.transfer); }
            }
        }
        for (Map.Entry<ResourceLocation, List<Transfer>> each : byRecipe.entrySet()) {
            ResourceLocation key = each.getKey();
            IRecipe wrapped = new TransferRecipe(registry.getValue(key), each.getValue());
            modifiable.remove(key);
            ContentOwners.as(key.getNamespace(), () -> registry.register(wrapped.setRegistryName(key)));
        }
        if (!byRecipe.isEmpty()) { Summary.info("recipes.transfer", "Gave " + byRecipe.size() + " crafting recipe(s) a transfer"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!json.has("transfer")) { return; }
        if (!json.has("swaps")) { Json.unknownKeys(json, "the top level", SwapEntry.FILE_KEYS); }
        if (!json.has("recipes") || !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("Recipe transfer file {} needs a recipes object saying which recipes it covers, ignoring it", key);
            return;
        }
        if (json.get("transfer").isJsonObject()) { Json.unknownKeys(json.getAsJsonObject("transfer"), "transfer", TRANSFER_KEYS); }
        Transfer transfer = json.get("transfer").isJsonObject() ? transfer(key, json.getAsJsonObject("transfer")) : null;
        if (transfer == null) {
            ContentLog.LOGGER.error("The transfer in {} needs a from, a source and a take list of enchantments, name, damage or nbt, ignoring it", key);
            return;
        }
        RecipeSelector scope = new RecipeSelector();
        Json.unknownKeys(json.getAsJsonObject("recipes"), "recipes", RecipeSelector.KEYS);
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
        Set<String> take = new HashSet<>(Json.strings(json, "take"));
        if (from == null || source == null || take.isEmpty() || !PARTS.containsAll(take)) { return null; }
        return new Transfer(from, source, take);
    }

    @Nullable private static Predicate<ItemStack> matcher(ResourceLocation key, String text) {
        switch (text) {
            case "":
                return null;
            case "armor":
                return stack -> stack.getItem() instanceof ItemArmor;
            case "tool":
                return stack -> stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemShears;
            case "weapon":
                return stack -> stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow;
            case "book":
                return stack -> stack.getItem() == Items.ENCHANTED_BOOK;
            default:
                ItemSelection selection = RecipeRemainders.selection("Recipe transfer", key, text);
                return selection.any() ? selection::matches : null;
        }
    }

    private static final class Entry {
        final RecipeSelector scope;
        final Transfer transfer;

        Entry(RecipeSelector scope, Transfer transfer) {
            this.scope = scope;
            this.transfer = transfer;
        }
    }

    static final class Transfer {
        final Predicate<ItemStack> from;
        final Predicate<ItemStack> source;
        final Set<String> take;

        Transfer(Predicate<ItemStack> from, Predicate<ItemStack> source, Set<String> take) {
            this.from = from;
            this.source = source;
            this.take = take;
        }

        void carry(IInventory matrix, ItemStack result) {
            int base = find(matrix, from, -1);
            int extra = find(matrix, source, base);
            if (base < 0 || extra < 0 || result.isEmpty()) { return; }
            ItemStack kept = matrix.getStackInSlot(base);
            ItemStack added = matrix.getStackInSlot(extra);
            NBTTagCompound tag = kept.getTagCompound();
            result.setTagCompound(tag == null ? null : tag.copy());
            if (result.isItemStackDamageable() && kept.isItemStackDamageable()) { result.setItemDamage(kept.getItemDamage()); }
            if (take.contains(NBT)) { mergeTag(result, added); }
            if (take.contains(DAMAGE) && result.isItemStackDamageable() && added.isItemStackDamageable()) { result.setItemDamage(added.getItemDamage()); }
            if (take.contains(NAME) && added.hasDisplayName()) { result.setStackDisplayName(added.getDisplayName()); }
            if (take.contains(ENCHANTMENTS)) { mergeEnchantments(result, added); }
        }

        private static int find(IInventory matrix, Predicate<ItemStack> wanted, int skip) {
            for (int slot = 0; slot < matrix.getSizeInventory(); slot++) {
                ItemStack stack = matrix.getStackInSlot(slot);
                if (slot != skip && !stack.isEmpty() && wanted.test(stack)) { return slot; }
            }
            return -1;
        }

        private static void mergeTag(ItemStack result, ItemStack added) {
            NBTTagCompound tag = added.getTagCompound();
            if (tag == null) { return; }
            NBTTagCompound copy = tag.copy();
            copy.removeTag("ench");
            copy.removeTag("StoredEnchantments");
            NBTTagCompound target = result.getTagCompound();
            if (target == null) { result.setTagCompound(copy); }
            else { target.merge(copy); }
        }

        private static void mergeEnchantments(ItemStack result, ItemStack added) {
            Map<Enchantment, Integer> merged = EnchantmentHelper.getEnchantments(result);
            for (Map.Entry<Enchantment, Integer> wanted : EnchantmentHelper.getEnchantments(added).entrySet()) {
                Enchantment enchantment = wanted.getKey();
                if (result.getItem() != Items.ENCHANTED_BOOK && !enchantment.canApply(result)) { continue; }
                if (clashes(merged, enchantment)) { continue; }
                merged.merge(enchantment, wanted.getValue(), Math::max);
            }
            EnchantmentHelper.setEnchantments(merged, result);
        }

        private static boolean clashes(Map<Enchantment, Integer> present, Enchantment enchantment) {
            for (Enchantment other : present.keySet()) {
                if (other != enchantment && !enchantment.isCompatibleWith(other)) { return true; }
            }
            return false;
        }
    }
}
