package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.ContainerCreative.class) public abstract class MixinContainerCreative {
    @Shadow public NonNullList<ItemStack> itemList;

    @Inject(method = "scrollTo", at = @At("HEAD")) private void rdpl$hideDisabled(float pos, CallbackInfo ci) {
        if (ContentDisabled.any()) { itemList.removeIf(ContentDisabled::disabled); }
    }
}
