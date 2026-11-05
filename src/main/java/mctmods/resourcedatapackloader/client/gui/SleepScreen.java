package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.core.network.BedStateMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;

public final class SleepScreen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ABOVE_BOTTOM = 64;
    private static byte state = BedStateMessage.HIDDEN;
    private static boolean pressed;
    @Nullable private static Button button;

    private SleepScreen() {}

    public static void receive(byte received) { state = received; }

    public static void clear() {
        state = BedStateMessage.HIDDEN;
        pressed = false;
        button = null;
    }

    public static void onInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InBedChatScreen)) { return; }
        pressed = false;
        button = Button.builder(Component.translatable("rdpl.sleep.setSpawn"), held -> press()).bounds(screen.width / 2 - BUTTON_WIDTH / 2, screen.height - ABOVE_BOTTOM, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        button.visible = shown();
        event.addListener(button);
    }

    public static void onRenderPost(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InBedChatScreen)) { return; }
        if (button != null) { button.visible = shown(); }
        ChatFormatting color;
        String key;
        switch (state) {
            case BedStateMessage.NONE -> {
                color = ChatFormatting.RED;
                key = "rdpl.sleep.noPreviousBed";
            }
            case BedStateMessage.CURRENT -> {
                color = ChatFormatting.BLUE;
                key = "rdpl.sleep.currentSpawnBed";
            }
            case BedStateMessage.UPDATED -> {
                color = ChatFormatting.GREEN;
                key = "rdpl.sleep.spawnUpdated";
            }
            default -> { return; }
        }
        Minecraft minecraft = Minecraft.getInstance();
        event.getGuiGraphics().drawCenteredString(minecraft.font, Component.translatable(key).withStyle(color), screen.width / 2, screen.height - ABOVE_BOTTOM + (BUTTON_HEIGHT - minecraft.font.lineHeight) / 2, 0xFFFFFF);
    }

    private static boolean shown() { return state == BedStateMessage.OTHER && !pressed; }

    private static void press() {
        pressed = true;
        if (button != null) { button.visible = false; }
        RDPLNetwork.setSpawnHere();
    }
}
