package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentOverrides;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class) public abstract class MixinItemStack {
    @WrapOperation(method = "validateStrict", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;validateComponents(Lnet/minecraft/core/component/DataComponentMap;)Lcom/mojang/serialization/DataResult;"))
    private static DataResult<Unit> rdpl$stackDamageable(DataComponentMap components, Operation<DataResult<Unit>> original, @Local(argsOnly = true) ItemStack stack) { return original.call(ContentOverrides.validating(stack.getItem(), components)); }
}
