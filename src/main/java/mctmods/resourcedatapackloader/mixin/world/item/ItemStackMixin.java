package mctmods.resourcedatapackloader.mixin.world.item;

import mctmods.resourcedatapackloader.common.blocks.ContentOverrides;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class) public abstract class ItemStackMixin {
    @WrapOperation(method = "validateStrict(Lnet/minecraft/world/item/ItemStack;)Lcom/mojang/serialization/DataResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;validateComponents(Lnet/minecraft/core/component/DataComponentMap;)Lcom/mojang/serialization/DataResult;")) private static DataResult<?> rdpl$stackDamageable(DataComponentMap components, Operation<DataResult<?>> original, @Local(argsOnly = true, name = "itemStack") ItemStack itemStack) { return original.call(ContentOverrides.validating(itemStack.getItem(), components)); }
}
