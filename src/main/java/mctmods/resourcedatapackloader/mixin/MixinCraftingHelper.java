package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.recipe.RecipeOverrides;

import net.minecraftforge.common.crafting.CraftingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.nio.file.Path;
import java.util.function.BiFunction;

@Mixin(value = CraftingHelper.class, remap = false)
public abstract class MixinCraftingHelper {

    @ModifyArg(
            method = "loadRecipes(Lnet/minecraftforge/fml/common/ModContainer;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/crafting/CraftingHelper;findFiles(Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/BiFunction;ZZ)Z"),
            index = 3,
            remap = false
    )
    private static BiFunction<Path, Path, Boolean> rdpl$wrapProcessor(BiFunction<Path, Path, Boolean> processor) {
        return RecipeOverrides.wrap(processor);
    }
}
