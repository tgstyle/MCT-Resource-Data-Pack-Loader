package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.PackOptions;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.client.event.ScreenEvent;
import javax.annotation.Nullable;

public final class PackOptionsButton {
    private static final int CORNER_WIDTH = 100;
    private static final int CORNER_HEIGHT = 20;
    private static final int CORNER_INSET = 8;
    private static final int FLASH_ABOVE_FOOTER = 40;
    private static final int FLASH_ABOVE_LIST_FOOTER = 62;
    private static final long FLASH_MILLIS = 500L;
    private static final int WARNING = 0xFF5555;
    private static final String PLAY_KEY = "selectWorld.select";
    private static final String CREATE_KEY = "selectWorld.create";
    @Nullable private static Button play;
    @Nullable private static Button create;
    @Nullable private static Button corner;

    private PackOptionsButton() {}

    public static void onInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean selecting = screen instanceof SelectWorldScreen;
        if (!selecting && !(screen instanceof CreateWorldScreen)) { return; }
        play = null;
        create = null;
        corner = null;
        if (PackOptions.files().isEmpty()) { return; }
        for (GuiEventListener listener : event.getListenersList()) {
            if (!(listener instanceof Button button)) { continue; }
            String key = keyOf(button.getMessage());
            if (selecting && PLAY_KEY.equals(key)) { play = button; }
            if (!selecting && CREATE_KEY.equals(key)) { create = button; }
        }
        int y = selecting ? CORNER_INSET - 2 : screen.height - CORNER_INSET - CORNER_HEIGHT;
        Button made = Button.builder(Component.translatable("rdpl.gui.packOptions"), button -> Minecraft.getInstance().setScreen(new PackOptionsScreen(screen)))
                .bounds(screen.width - CORNER_WIDTH - CORNER_INSET, y, CORNER_WIDTH, CORNER_HEIGHT).build();
        made.visible = !selecting;
        corner = made;
        event.addListener(made);
    }

    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        Screen screen = event.getScreen();
        boolean pending = !PackOptions.applied();
        if (screen instanceof CreateWorldScreen) {
            if (create != null && pending) { create.active = false; }
            return;
        }
        if (!(screen instanceof SelectWorldScreen) || corner == null) { return; }
        corner.visible = (play != null && play.active) || pending;
        corner.active = corner.visible;
        if (pending && play != null) { play.active = false; }
    }

    public static void onRenderPost(ScreenEvent.Render.Post event) {
        if (PackOptions.applied()) { return; }
        Screen screen = event.getScreen();
        if (screen instanceof CreateWorldScreen) { flash(event.getGuiGraphics(), screen, screen.height - FLASH_ABOVE_FOOTER); }
        else if (screen instanceof SelectWorldScreen) { flash(event.getGuiGraphics(), screen, screen.height - FLASH_ABOVE_LIST_FOOTER); }
    }

    private static void flash(GuiGraphics graphics, Screen screen, int y) {
        if (Util.getMillis() / FLASH_MILLIS % 2L != 0L) { return; }
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("rdpl.gui.packOptions.restartRequired"), screen.width / 2, y, WARNING);
    }

    @Nullable private static String keyOf(Component message) { return message.getContents() instanceof TranslatableContents contents ? contents.getKey() : null; }
}
