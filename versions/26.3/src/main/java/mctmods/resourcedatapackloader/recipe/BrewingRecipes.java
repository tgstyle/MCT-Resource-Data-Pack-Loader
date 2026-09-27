package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class BrewingRecipes implements ContentPotions.Brewing {
    private final List<Item> containers = new ArrayList<>();
    private final List<BrewingRecipe> mixes = new ArrayList<>();
    private final List<BrewingRecipe> recipes = new ArrayList<>();

    private BrewingRecipes() {}

    public static List<RecipeHolder<?>> build(Collection<RecipeHolder<?>> loaded) {
        BrewingRecipes brewing = new BrewingRecipes();
        ContentPotions.applyBrewing(brewing);
        List<RecipeHolder<?>> out = new ArrayList<>();
        for (BrewingRecipe recipe : brewing.recipes) { out.add(holder(out.size(), recipe)); }
        List<Item> all = new ArrayList<>(List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION));
        all.addAll(brewing.containers);
        for (BrewingRecipe mix : brewing.mixes) {
            for (Item container : all) { out.add(holder(out.size(), onto(mix, container))); }
        }
        if (brewing.containers.isEmpty()) { return out; }
        for (RecipeHolder<?> holder : loaded) {
            if (!(holder.value() instanceof BrewingRecipe recipe) || !potionMix(recipe)) { continue; }
            for (Item container : brewing.containers) { out.add(holder(out.size(), onto(recipe, container))); }
        }
        return out;
    }

    private static boolean potionMix(BrewingRecipe recipe) {
        if (recipe.getInput().potions().isEmpty() || recipe.getOutput().typeHolder().value() != Items.POTION) { return false; }
        List<Holder<Item>> items = recipe.getInput().ingredient().items().toList();
        return items.size() == 1 && items.getFirst().value() == Items.POTION;
    }

    private static BrewingRecipe onto(BrewingRecipe mix, Item container) {
        PotionIngredient input = mix.getInput().potions().map(potions -> PotionIngredient.of(container, potions)).orElseGet(() -> PotionIngredient.of(container));
        return new BrewingRecipe(input, mix.getReagent(), new ItemStackTemplate(container, mix.getOutput().components()));
    }

    private static RecipeHolder<?> holder(int index, BrewingRecipe recipe) { return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "brewing/" + index)), recipe); }

    @Override public void mix(Holder<Potion> from, Item ingredient, Holder<Potion> to) {
        DataComponentPatch output = DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(to)).build();
        mixes.add(new BrewingRecipe(PotionIngredient.of(Items.POTION, PotionsPredicate.ofPotion(from)), PotionIngredient.of(ingredient), new ItemStackTemplate(Items.POTION, output)));
    }

    @Override public void recipe(Item input, Item ingredient, ItemStack output) { recipes.add(new BrewingRecipe(PotionIngredient.of(input), PotionIngredient.of(ingredient), ItemStackTemplate.fromNonEmptyStack(output))); }

    @Override public void container(Item item) { containers.add(item); }
}
