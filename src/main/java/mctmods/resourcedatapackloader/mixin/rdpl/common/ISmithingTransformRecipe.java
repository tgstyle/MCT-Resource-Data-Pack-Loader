package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class) public interface ISmithingTransformRecipe {
    @Accessor("template") Ingredient rdpl$getTemplate();

    @Accessor("base") Ingredient rdpl$getBase();

    @Accessor("addition") Ingredient rdpl$getAddition();
}
