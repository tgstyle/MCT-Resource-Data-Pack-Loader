package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class HideRecipeBook {
    private HideRecipeBook() {}

    public static void onInitPre(ScreenEvent.Init.Pre event) {
        if (untouched(event.getScreen())) { return; }
        LocalPlayer player = event.getScreen().getMinecraft().player;
        if (player != null) { player.getRecipeBook().setOpen(RecipeBookType.CRAFTING, false); }
    }

    public static void onInitPost(ScreenEvent.Init.Post event) {
        if (untouched(event.getScreen())) { return; }
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof ImageButton button && button.sprites == RecipeBookComponent.RECIPE_BUTTON_SPRITES) {
                event.removeListener(listener);
                return;
            }
        }
    }

    private static boolean untouched(Screen screen) { return !Config.tweaks.hideRecipeBookButton() || !(screen instanceof InventoryScreen || screen instanceof CraftingScreen); }
}
