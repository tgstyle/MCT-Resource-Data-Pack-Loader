package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentEnchanting;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class) public abstract class MixinEnchantmentHelper {
    @Inject(method = "getEnchantmentCost", at = @At("HEAD"), cancellable = true) private static void rdpl$tableLevel(RandomSource random, int enchantNum, int power, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        Integer level = ContentEnchanting.level(random, enchantNum, power, stack);
        if (level != null) { cir.setReturnValue(level); }
    }
}
