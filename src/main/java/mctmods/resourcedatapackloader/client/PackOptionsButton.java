package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PackOptionsButton {
    private static final int ID = 74125;
    private static final int FALLBACK_Y = 163;
    private static final int MORE_OPTIONS_ID = 3;
    private static final int CREATE_ID = 0;
    private static final long FLASH_MILLIS = 500L;
    private static GuiButton create;
    private static int moreOptionsY = FALLBACK_Y + 24;

    private PackOptionsButton() {}

    public static final class Handler {
        @SubscribeEvent public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || PackOptions.files().isEmpty()) { return; }

            int x = event.getGui().width / 2 - 75;
            int y = FALLBACK_Y;
            create = null;
            for (GuiButton held : event.getButtonList()) {
                if (held.id == MORE_OPTIONS_ID) {
                    x = held.x;
                    y = held.y - 24;
                }
                else if (held.id == CREATE_ID) { create = held; }
            }
            moreOptionsY = y + 24;
            event.getButtonList().add(new GuiButton(ID, x, y, 150, 20, I18n.format("rdpl.gui.packOptions")));
        }

        @SubscribeEvent public void onAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || event.getButton().id != ID) { return; }

            event.getGui().mc.displayGuiScreen(new GuiPackOptions(event.getGui()));
        }

        @SubscribeEvent public void beforeDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || create == null || PackOptions.applied()) { return; }

            create.enabled = false;
        }

        @SubscribeEvent public void afterDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
            if (!(event.getGui() instanceof GuiCreateWorld) || PackOptions.applied()) { return; }
            if (Minecraft.getSystemTime() / FLASH_MILLIS % 2L != 0L) { return; }

            GuiScreen gui = event.getGui();
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            String message = I18n.format("rdpl.gui.packOptions.restartRequired");
            int y = (moreOptionsY + 20 + gui.height - 28) / 2 - font.FONT_HEIGHT / 2;
            font.drawStringWithShadow(message, (float) gui.width / 2 - font.getStringWidth(message) / 2.0F, y, 0xFF5555);
        }
    }
}
