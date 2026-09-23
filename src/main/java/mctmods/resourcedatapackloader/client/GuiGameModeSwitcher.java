package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.interfaces.IPreviousGameType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

@SideOnly(Side.CLIENT) public class GuiGameModeSwitcher extends GuiScreen {
    private static final GameType[] MODES = {GameType.CREATIVE, GameType.SURVIVAL, GameType.ADVENTURE, GameType.SPECTATOR};
    private static final int SLOT = 26;
    private static final int SLOT_STEP = 31;
    private static final int ICON_INSET = 5;
    private static final int ALL_SLOTS_WIDTH = MODES.length * SLOT_STEP - ICON_INSET;
    private static final int PANEL_WIDTH = 125;
    private static final int PANEL_HEIGHT = 75;
    private static final int PANEL_FILL = 0x90000000;
    private static final int PANEL_EDGE = 0xFF5A5A5A;
    private static final int SLOT_FILL = 0xC0303030;
    private static final int SLOT_EDGE = 0xFF8B8B8B;
    private static final int SELECTED_EDGE = 0xFFFFFFFF;
    private static final int WHITE = 0xFFFFFF;
    private final ItemStack[] icons = {new ItemStack(Blocks.GRASS), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.MAP), new ItemStack(Items.ENDER_EYE)};
    private int hovered;
    private int firstMouseX;
    private int firstMouseY;
    private boolean mousePlaced;

    public GuiGameModeSwitcher() { hovered = slotOf(startingMode(Minecraft.getMinecraft().playerController)); }

    private static GameType startingMode(PlayerControllerMP controller) {
        GameType previous = ((IPreviousGameType) controller).rdpl$previousGameType();
        if (previous != null) { return previous; }
        return controller.getCurrentGameType() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
    }

    private static int slotOf(GameType mode) {
        for (int i = 0; i < MODES.length; i++) {
            if (MODES[i] == mode) { return i; }
        }
        return 0;
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Keyboard.isKeyDown(Keyboard.KEY_F3)) { drawSwitcher(mouseX, mouseY, partialTicks); }
        else {
            switchToHovered();
            mc.displayGuiScreen(null);
        }
    }

    private void drawSwitcher(int mouseX, int mouseY, float partialTicks) {
        int centerX = width / 2;
        int centerY = height / 2;
        int panelLeft = centerX - 62;
        int panelTop = centerY - SLOT_STEP - 27;
        drawFrame(panelLeft, panelTop, PANEL_WIDTH, PANEL_HEIGHT, PANEL_FILL, PANEL_EDGE);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, I18n.format("gameMode." + MODES[hovered].getName()), centerX, centerY - SLOT_STEP - 20, WHITE);
        drawCenteredString(fontRenderer, I18n.format("rdpl.debug.gamemodes.select_next", TextFormatting.AQUA + I18n.format("rdpl.debug.gamemodes.press_f4") + TextFormatting.RESET), centerX, centerY + ICON_INSET, WHITE);
        if (!mousePlaced) {
            firstMouseX = mouseX;
            firstMouseY = mouseY;
            mousePlaced = true;
        }
        boolean mouseStill = firstMouseX == mouseX && firstMouseY == mouseY;
        int slotTop = centerY - SLOT_STEP;
        for (int i = 0; i < MODES.length; i++) {
            int slotLeft = centerX - ALL_SLOTS_WIDTH / 2 + i * SLOT_STEP;
            drawFrame(slotLeft, slotTop, SLOT, SLOT, SLOT_FILL, i == hovered ? SELECTED_EDGE : SLOT_EDGE);
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(icons[i], slotLeft + ICON_INSET, slotTop + ICON_INSET);
            RenderHelper.disableStandardItemLighting();
            if (!mouseStill && mouseX >= slotLeft && mouseX < slotLeft + SLOT && mouseY >= slotTop && mouseY < slotTop + SLOT) { hovered = i; }
        }
    }

    private void drawFrame(int left, int top, int frameWidth, int frameHeight, int fill, int edge) {
        drawRect(left, top, left + frameWidth, top + frameHeight, fill);
        drawHorizontalLine(left, left + frameWidth - 1, top, edge);
        drawHorizontalLine(left, left + frameWidth - 1, top + frameHeight - 1, edge);
        drawVerticalLine(left, top, top + frameHeight - 1, edge);
        drawVerticalLine(left + frameWidth - 1, top, top + frameHeight - 1, edge);
    }

    private void switchToHovered() {
        if (mc.player == null || mc.playerController == null) { return; }
        GameType chosen = MODES[hovered];
        if (mc.player.canUseCommand(2, "") && chosen != mc.playerController.getCurrentGameType()) { mc.player.sendChatMessage("/gamemode " + chosen.getName()); }
    }

    @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_F4) {
            mousePlaced = false;
            hovered = (hovered + 1) % MODES.length;
        }
        else { super.keyTyped(typedChar, keyCode); }
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}
