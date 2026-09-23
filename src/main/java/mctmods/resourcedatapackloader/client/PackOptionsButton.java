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
    private static final int CREATE_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CORNER_INSET = 8;
    private static final int FOOTER = 36;
    private static final int FOOTER_GAP = 10;
    private static final int WARNING_ROOM = 16;
    private static final int FLASH_ABOVE_LIST_FOOTER = 62;
    private static final long FLASH_MILLIS = 500L;
    private static final int WARNING = 0xFF5555;
    private static final String PLAY_KEY = "selectWorld.select";
    private static final String CREATE_KEY = "selectWorld.create";
    @Nullable private static Screen owner;
    @Nullable private static Button opener;

    private PackOptionsButton() {}

    public static void onInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean selecting = screen instanceof SelectWorldScreen;
        if (!selecting && !(screen instanceof CreateWorldScreen)) { return; }
        owner = null;
        opener = null;
        if (PackOptions.files().isEmpty()) { return; }
        Button.Builder builder = Button.builder(Component.translatable("rdpl.gui.packOptions"), button -> Minecraft.getInstance().setScreen(new PackOptionsScreen(screen)));
        Button made = selecting ? builder.bounds(screen.width - CORNER_WIDTH - CORNER_INSET, CORNER_INSET - 2, CORNER_WIDTH, BUTTON_HEIGHT).build() : builder.bounds(0, 0, CREATE_WIDTH, BUTTON_HEIGHT).build();
        if (selecting) { made.visible = false; }
        else { seat(made, screen); }
        owner = screen;
        opener = made;
        event.addListener(made);
    }

    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        Screen screen = event.getScreen();
        boolean pending = !PackOptions.applied();
        if (screen instanceof CreateWorldScreen) {
            if (opener != null && owner == screen) { seat(opener, screen); }
            Button create = button(screen, CREATE_KEY);
            if (create != null && pending) { create.active = false; }
            return;
        }
        if (!(screen instanceof SelectWorldScreen) || opener == null || owner != screen) { return; }
        Button play = button(screen, PLAY_KEY);
        opener.visible = (play != null && play.active) || pending;
        opener.active = opener.visible;
        if (pending && play != null) { play.active = false; }
    }

    public static void onRenderPost(ScreenEvent.Render.Post event) {
        if (PackOptions.applied()) { return; }
        Screen screen = event.getScreen();
        if (screen instanceof CreateWorldScreen) {
            int footer = footer(screen);
            flash(event.getGuiGraphics(), screen, (footer - WARNING_ROOM + footer) / 2 - Minecraft.getInstance().font.lineHeight / 2);
        }
        else if (screen instanceof SelectWorldScreen) { flash(event.getGuiGraphics(), screen, screen.height - FLASH_ABOVE_LIST_FOOTER); }
    }

    private static void seat(Button made, Screen screen) {
        made.setX(screen.width / 2 - CREATE_WIDTH / 2);
        made.setY(footer(screen) - WARNING_ROOM - BUTTON_HEIGHT);
    }

    private static int footer(Screen screen) {
        Button create = button(screen, CREATE_KEY);
        return create == null ? screen.height - FOOTER : create.getY() - FOOTER_GAP;
    }

    @Nullable private static Button button(Screen screen, String key) {
        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof Button held && key.equals(keyOf(held.getMessage()))) { return held; }
        }
        return null;
    }

    private static void flash(GuiGraphics graphics, Screen screen, int y) {
        if (Util.getMillis() / FLASH_MILLIS % 2L != 0L) { return; }
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("rdpl.gui.packOptions.restartRequired"), screen.width / 2, y, WARNING);
    }

    @Nullable private static String keyOf(Component message) { return message.getContents() instanceof TranslatableContents contents ? contents.getKey() : null; }
}
