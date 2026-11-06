package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraftforge.client.event.ScreenEvent;

public final class HideRecipeBook {
    private HideRecipeBook() {}

    private static boolean untouched(Screen screen) {
        return !Config.tweaks.hideRecipeBookButton() || !(screen instanceof InventoryScreen || screen instanceof CraftingScreen);
    }

    public static void onInitPre(ScreenEvent.Init.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || untouched(event.getScreen())) { return; }
        player.getRecipeBook().setOpen(RecipeBookType.CRAFTING, false);
    }

    public static void onInitPost(ScreenEvent.Init.Post event) {
        if (untouched(event.getScreen())) { return; }
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof ImageButton) {
                event.removeListener(listener);
                return;
            }
        }
    }
}
