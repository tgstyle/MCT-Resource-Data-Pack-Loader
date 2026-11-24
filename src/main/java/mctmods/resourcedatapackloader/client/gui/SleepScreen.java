package mctmods.resourcedatapackloader.client.gui;

import mctmods.resourcedatapackloader.client.util.CenteredText;
import mctmods.resourcedatapackloader.client.util.ClientResets;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.network.BedStateMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class SleepScreen {
    private static final int ID = 74127;
    private static final int UNSET = -1;
    private static final int ABOVE_BOTTOM = 64;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int RED = Says.rgb(TextFormatting.RED);
    private static final int BLUE = 0x5555FF;
    private static int state = UNSET;
    private static boolean pressed;
    @Nullable private static GuiButton button;

    private SleepScreen() {}

    @SubscribeEvent public static void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiSleepMP)) { return; }
        button = null;
        pressed = false;
        if (state == UNSET) { return; }
        GuiScreen gui = event.getGui();
        button = new GuiButton(ID, gui.width / 2 - BUTTON_WIDTH / 2, gui.height - ABOVE_BOTTOM, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("rdpl.sleep.setSpawn"));
        button.visible = false;
        event.getButtonList().add(button);
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { ClientResets.onDisconnect(() -> { state = UNSET; pressed = false; button = null; }); }

    @SubscribeEvent public static void beforeDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (button == null || !(event.getGui() instanceof GuiSleepMP)) { return; }
        button.visible = state == BedStateMessage.OTHER && !pressed;
        button.enabled = button.visible;
    }

    @SubscribeEvent public static void afterDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof GuiSleepMP)) { return; }
        if (state == BedStateMessage.NONE) { line(event.getGui(), "rdpl.sleep.noPreviousBed", RED); }
        else if (state == BedStateMessage.CURRENT) { line(event.getGui(), "rdpl.sleep.currentSpawnBed", BLUE); }
        else if (state == BedStateMessage.UPDATED) { line(event.getGui(), "rdpl.sleep.spawnUpdated", Colors.GREEN_TEXT); }
    }

    @SubscribeEvent public static void onAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getButton().id != ID || !(event.getGui() instanceof GuiSleepMP)) { return; }
        pressed = true;
        RDPLNetwork.setSpawnHere();
    }

    private static void line(GuiScreen gui, String key, int color) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String text = I18n.format(key);
        CenteredText.shadow(font, text, gui.width / 2.0F, gui.height - ABOVE_BOTTOM + (BUTTON_HEIGHT - font.FONT_HEIGHT) / 2.0F, color);
    }

    public static class Handler extends AbstractClientMessageHandler<BedStateMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, BedStateMessage message, MessageContext ctx) { state = message.state; }
    }
}
