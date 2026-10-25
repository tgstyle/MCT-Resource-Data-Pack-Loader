package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraftforge.client.event.ToastAddEvent;

public final class ToastFilter {
    private ToastFilter() {}

    public static void onToastAdd(ToastAddEvent event) { if (Toasts.hides(kind(event.getToast()))) { event.setCanceled(true); } }

    private static int kind(Toast toast) {
        if (toast instanceof AdvancementToast) { return Toasts.ADVANCEMENTS; }
        if (toast instanceof RecipeToast) { return Toasts.RECIPES; }
        if (toast instanceof TutorialToast) { return Toasts.TUTORIAL; }
        if (toast instanceof SystemToast) { return Toasts.SYSTEM; }
        return Toasts.OTHER;
    }
}
