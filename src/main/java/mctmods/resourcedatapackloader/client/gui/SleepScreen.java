package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.common.sleeping.ContentSleep;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class SleepScreen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ABOVE_BOTTOM = 64;
    private static final int WHITE = 0xFFFFFFFF;
    private static int state = ContentSleep.HIDDEN;
    private static boolean pressed;
    @Nullable private static Button setSpawn;

    private SleepScreen() {}

    public static void receive(int received) {
        state = received;
        if (received != ContentSleep.OTHER) { pressed = false; }
        show();
    }

    public static void onInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InBedChatScreen)) { return; }
        pressed = false;
        setSpawn = Button.builder(Component.translatable("rdpl.sleep.setSpawn"), _ -> press()).bounds(screen.width / 2 - BUTTON_WIDTH / 2, screen.height - ABOVE_BOTTOM, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        show();
        event.addListener(setSpawn);
    }

    public static void onRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InBedChatScreen)) { return; }
        show();
        Component line = switch (state) {
            case ContentSleep.NONE -> Component.translatable("rdpl.sleep.noPreviousBed").withStyle(ChatFormatting.RED);
            case ContentSleep.CURRENT -> Component.translatable("rdpl.sleep.currentSpawnBed").withStyle(ChatFormatting.BLUE);
            case ContentSleep.UPDATED -> Component.translatable("rdpl.sleep.spawnUpdated").withStyle(ChatFormatting.GREEN);
            default -> null;
        };
        if (line == null) { return; }
        Minecraft minecraft = Minecraft.getInstance();
        event.getGuiGraphics().centeredText(minecraft.font, line, screen.width / 2, screen.height - ABOVE_BOTTOM + (BUTTON_HEIGHT - minecraft.font.lineHeight) / 2, WHITE);
    }

    private static void press() {
        pressed = true;
        show();
        RDPLNetwork.setSpawnHere();
    }

    private static void show() {
        if (setSpawn == null) { return; }
        setSpawn.visible = state == ContentSleep.OTHER && !pressed;
        setSpawn.active = setSpawn.visible;
    }
}
