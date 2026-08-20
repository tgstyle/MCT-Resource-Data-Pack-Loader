package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PackOptionsButton {
    private static final int ID = 74125;
    private static final int SELECTION_ID = 74126;
    private static final int FALLBACK_Y = 163;
    private static final int MORE_OPTIONS_ID = 3;
    private static final int CREATE_ID = 0;
    private static final int PLAY_ID = 1;
    private static final int CORNER_WIDTH = 100;
    private static final int CORNER_INSET = 8;
    private static final long FLASH_MILLIS = 500L;
    private static GuiButton create;
    private static GuiButton play;
    private static GuiButton corner;
    private static int moreOptionsY = FALLBACK_Y + 24;

    private PackOptionsButton() {}

    private static void flash(GuiScreen gui, int y) {
        if (Minecraft.getSystemTime() / FLASH_MILLIS % 2L != 0L) { return; }
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String message = I18n.format("rdpl.gui.packOptions.restartRequired");
        font.drawStringWithShadow(message, (float) gui.width / 2 - font.getStringWidth(message) / 2.0F, y, 0xFF5555);
    }

    public static final class Handler {
        @SubscribeEvent public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (PackOptions.files().isEmpty()) { return; }
            if (event.getGui() instanceof GuiCreateWorld) {
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
                return;
            }
            if (!(event.getGui() instanceof GuiWorldSelection)) { return; }

            play = null;
            for (GuiButton held : event.getButtonList()) {
                if (held.id == PLAY_ID) { play = held; }
            }
            corner = new GuiButton(SELECTION_ID, event.getGui().width - CORNER_WIDTH - CORNER_INSET, CORNER_INSET - 2, CORNER_WIDTH, 20, I18n.format("rdpl.gui.packOptions"));
            corner.visible = false;
            event.getButtonList().add(corner);
        }

        @SubscribeEvent public void onAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
            if (event.getButton().id != ID && event.getButton().id != SELECTION_ID) { return; }
            if (!(event.getGui() instanceof GuiCreateWorld) && !(event.getGui() instanceof GuiWorldSelection)) { return; }
            event.getGui().mc.displayGuiScreen(new GuiPackOptions(event.getGui()));
        }

        @SubscribeEvent public void beforeDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
            if (event.getGui() instanceof GuiCreateWorld) {
                if (create == null || PackOptions.applied()) { return; }
                create.enabled = false;
                return;
            }
            if (!(event.getGui() instanceof GuiWorldSelection) || corner == null) { return; }
            boolean pending = !PackOptions.applied();
            corner.visible = (play != null && play.enabled) || pending;
            corner.enabled = corner.visible;
            if (pending && play != null) { play.enabled = false; }
        }

        @SubscribeEvent public void afterDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
            if (PackOptions.applied()) { return; }
            if (event.getGui() instanceof GuiCreateWorld) {
                flash(event.getGui(), (moreOptionsY + 20 + event.getGui().height - 28) / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2);
                return;
            }
            if (!(event.getGui() instanceof GuiWorldSelection)) { return; }
            flash(event.getGui(), event.getGui().height - 62);
        }
    }
}
