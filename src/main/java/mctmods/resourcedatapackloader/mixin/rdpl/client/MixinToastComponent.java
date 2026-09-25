package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class) public abstract class MixinToastComponent {
    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true) private void rdpl$noToast(Toast toast, CallbackInfo ci) {
        if (Toasts.hides(rdpl$kind(toast))) { ci.cancel(); }
    }

    @Unique private static int rdpl$kind(Toast toast) {
        if (toast instanceof AdvancementToast) { return Toasts.ADVANCEMENTS; }
        if (toast instanceof RecipeToast) { return Toasts.RECIPES; }
        if (toast instanceof TutorialToast) { return Toasts.TUTORIAL; }
        if (toast instanceof SystemToast) { return Toasts.SYSTEM; }
        return Toasts.OTHER;
    }
}
