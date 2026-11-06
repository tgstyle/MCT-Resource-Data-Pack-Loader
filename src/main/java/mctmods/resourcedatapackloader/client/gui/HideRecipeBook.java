package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class HideRecipeBook {
    private static final int BUTTON_ID = 10;

    private HideRecipeBook() {}

    private static boolean hidden(GuiScreen gui) {
        return Config.client.hideRecipeBookButton && (gui instanceof GuiInventory || gui instanceof GuiCrafting);
    }

    @SubscribeEvent public static void closeBook(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (hidden(event.getGui())) { Minecraft.getMinecraft().player.getRecipeBook().setGuiOpen(false); }
    }

    @SubscribeEvent public static void removeButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if (hidden(event.getGui())) { event.getButtonList().removeIf(button -> button.id == BUTTON_ID); }
    }
}
