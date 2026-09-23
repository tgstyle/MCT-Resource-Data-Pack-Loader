package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentOverrides;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemParser.class) public abstract class MixinItemParser {
    @WrapOperation(method = "validateComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;validateComponents(Lnet/minecraft/core/component/DataComponentMap;)Lcom/mojang/serialization/DataResult;"))
    private static DataResult<Unit> rdpl$stackDamageable(DataComponentMap components, Operation<DataResult<Unit>> original, @Local(argsOnly = true) Holder<Item> item) { return original.call(ContentOverrides.validating(item.value(), components)); }
}
