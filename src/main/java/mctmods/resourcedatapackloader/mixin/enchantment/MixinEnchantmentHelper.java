package mctmods.resourcedatapackloader.mixin.enchantment;

import mctmods.resourcedatapackloader.common.enchantments.ContentEnchanting;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class) public abstract class MixinEnchantmentHelper {
    @Inject(method = "calcItemStackEnchantability", at = @At("HEAD"), cancellable = true) private static void rdpl$tableLevel(Random rand, int enchantNum, int power, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        Integer level = ContentEnchanting.level(rand, enchantNum, power, stack);
        if (level != null) { cir.setReturnValue(level); }
    }
}
