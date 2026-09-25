package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class) public interface ISmithingTrimRecipe {
    @Accessor("template") Ingredient rdpl$getTemplate();

    @Accessor("base") Ingredient rdpl$getBase();

    @Accessor("addition") Ingredient rdpl$getAddition();
}
