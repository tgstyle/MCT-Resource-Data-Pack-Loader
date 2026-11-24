package mctmods.resourcedatapackloader.mixin.advancements;

import mctmods.resourcedatapackloader.common.advancements.RecipeTolerance;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementRewards.Deserializer.class) public abstract class MixinAdvancementRewards {
    @Redirect(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/advancements/AdvancementRewards;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/CraftingManager;getRecipe(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/item/crafting/IRecipe;")) private IRecipe rdpl$tolerateMissing(ResourceLocation name) {
        return RecipeTolerance.resolve(name);
    }

    @ModifyArg(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/advancements/AdvancementRewards;", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;<init>(I[Lnet/minecraft/util/ResourceLocation;[Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/command/FunctionObject$CacheableFunction;)V"), index = 2) private ResourceLocation[] rdpl$stripMissing(ResourceLocation[] recipes) {
        return RecipeTolerance.existing(recipes);
    }
}
