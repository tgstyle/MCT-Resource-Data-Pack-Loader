package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class HideRecipeBook {
    private HideRecipeBook() {}

    private static boolean skips(Screen screen) { return !Config.tweaks.hideRecipeBookButton() || !(screen instanceof InventoryScreen || screen instanceof CraftingScreen); }

    public static void onInitPre(ScreenEvent.Init.Pre event) {
        Screen screen = event.getScreen();
        if (skips(screen)) { return; }
        LocalPlayer player = screen.getMinecraft().player;
        if (player != null) { player.getRecipeBook().setOpen(RecipeBookType.CRAFTING, false); }
    }

    public static void onInitPost(ScreenEvent.Init.Post event) {
        if (skips(event.getScreen())) { return; }
        for (GuiEventListener child : event.getListenersList()) {
            if (child instanceof ImageButton) {
                event.removeListener(child);
                return;
            }
        }
    }
}
