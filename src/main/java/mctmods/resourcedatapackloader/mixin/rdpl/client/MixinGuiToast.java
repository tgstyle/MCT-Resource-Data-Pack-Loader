package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.TutorialToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiToast.class) public abstract class MixinGuiToast {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true) private void rdpl$noToast(IToast toastIn, CallbackInfo ci) {
        if (Toasts.hides(rdpl$kind(toastIn))) { ci.cancel(); }
    }

    @Unique private static int rdpl$kind(IToast toast) {
        if (toast instanceof AdvancementToast) { return Toasts.ADVANCEMENTS; }
        if (toast instanceof RecipeToast) { return Toasts.RECIPES; }
        if (toast instanceof TutorialToast) { return Toasts.TUTORIAL; }
        if (toast instanceof SystemToast) { return Toasts.SYSTEM; }
        return Toasts.OTHER;
    }
}
