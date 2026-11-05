package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.network.BedStateMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class SleepScreen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 20;
    private static final int ABOVE_BOTTOM = 64;
    private static final int RED = 0xFF5555;
    private static final int BLUE = 0x5555FF;
    private static final int GREEN = 0x55FF55;
    private static byte state = BedStateMessage.HIDDEN;
    private static boolean pressed;
    @Nullable private static Button button;

    private SleepScreen() {}

    public static void receive(byte got) {
        state = got;
        if (button != null) { button.visible = shown(); }
    }

    public static void clear() {
        state = BedStateMessage.HIDDEN;
        pressed = false;
        button = null;
    }

    private static boolean shown() { return state == BedStateMessage.OTHER && !pressed; }

    public static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InBedChatScreen screen)) { return; }
        pressed = false;
        button = Button.builder(Component.translatable("rdpl.sleep.setSpawn"), held -> press()).bounds(screen.width / 2 - WIDTH / 2, screen.height - ABOVE_BOTTOM, WIDTH, HEIGHT).build();
        button.visible = shown();
        event.addListener(button);
    }

    private static void press() {
        pressed = true;
        if (button != null) { button.visible = false; }
        RDPLNetwork.setSpawnHere();
    }

    public static void onRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InBedChatScreen)) { return; }
        String key;
        int color;
        switch (state) {
            case BedStateMessage.NONE -> {
                key = "rdpl.sleep.noPreviousBed";
                color = RED;
            }
            case BedStateMessage.CURRENT -> {
                key = "rdpl.sleep.currentSpawnBed";
                color = BLUE;
            }
            case BedStateMessage.UPDATED -> {
                key = "rdpl.sleep.spawnUpdated";
                color = GREEN;
            }
            default -> { return; }
        }
        Font font = Minecraft.getInstance().font;
        event.getGuiGraphics().drawCenteredString(font, Component.translatable(key), screen.width / 2, screen.height - ABOVE_BOTTOM + (HEIGHT - font.lineHeight) / 2, color);
    }
}
