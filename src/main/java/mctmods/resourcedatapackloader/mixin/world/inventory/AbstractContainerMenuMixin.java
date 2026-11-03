package mctmods.resourcedatapackloader.mixin.world.inventory;

import mctmods.resourcedatapackloader.common.weights.WeightEnforce;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class) public abstract class AbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD")) private void rdpl$noteClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) { WeightEnforce.beginClick(player, clickType, button); }

    @Inject(method = "clicked", at = @At("RETURN")) private void rdpl$endClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) { WeightEnforce.endClick(player); }
}
