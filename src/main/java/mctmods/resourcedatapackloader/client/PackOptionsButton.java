package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PackOptionsButton {
    private static final int ID = 74125;

    private PackOptionsButton() {}

    public static final class Handler {
        @SubscribeEvent public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || PackOptions.files().isEmpty()) { return; }

            int x = event.getGui().width / 2 - 75;
            int y = 163;
            for (GuiButton held : event.getButtonList()) {
                if (held.id == 3) {
                    x = held.x;
                    y = held.y - 24;
                    break;
                }
            }
            event.getButtonList().add(new GuiButton(ID, x, y, 150, 20, net.minecraft.client.resources.I18n.format("rdpl.gui.packOptions")));
        }

        @SubscribeEvent public void onAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || event.getButton().id != ID) { return; }

            event.getGui().mc.displayGuiScreen(new GuiPackOptions(event.getGui()));
        }
    }
}
