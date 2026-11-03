package mctmods.resourcedatapackloader.mixin.world.inventory;

import mctmods.resourcedatapackloader.common.weights.WeightEnforce;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class) public abstract class AbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD")) private void rdpl$noteClick(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) { WeightEnforce.beginClick(player, containerInput, buttonNum); }

    @Inject(method = "clicked", at = @At("RETURN")) private void rdpl$endClick(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) { WeightEnforce.endClick(player); }
}
