package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemChorusFruit.class) public abstract class MixinItemChorusFruit {
    @ModifyConstant(method = "onItemUseFinish", constant = @Constant(doubleValue = 0.0)) private double rdpl$getMinHeight(double orig, ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight();
    }

    @Redirect(method = "onItemUseFinish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getActualHeight()I"))
    private int rdpl$getMaxHeight(World world) { return ((IRubicWorld)world).rdpl$isRubicWorld() ? world.getHeight() : world.getActualHeight(); }
}
