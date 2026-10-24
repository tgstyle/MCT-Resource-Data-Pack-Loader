package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class) public abstract class MixinPlayerEnchantCost {
    @ModifyVariable(method = "onEnchantmentPerformed", at = @At("HEAD"), name = "enchantmentCost", argsOnly = true) private int rdpl$levelsPaid(int enchantmentCost) { return AnvilCosts.paidAtTable((Player) (Object) this, enchantmentCost); }
}
