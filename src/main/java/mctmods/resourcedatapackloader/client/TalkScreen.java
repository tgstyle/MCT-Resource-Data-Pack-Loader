package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.client.render.Blends;
import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageTalk;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT) public class TalkScreen extends GuiScreen {
    private static final int WIDTH = 320;
    private static final int PAD = 6;
    private static final int STRIPE = 3;
    private static final int ICON = 16;
    private static final int MARGIN = 8;
    private static final int LIFT = 52;
    private static final int GAP = 4;
    private static final int ROW_PAD = 2;
    private static final int MOST_KEYS = 8;
    private static final double REACH = 8.0D;
    private static final int HOVER = 0x40FFFFFF;
    private static final int HOVER_TEXT = 0xFFFF55;
    private static final int TEXT = 0xFFFFFF;
    private MessageTalk talk;
    private List<MarkText.Piece> title = new ArrayList<>();
    private List<List<MarkText.Piece>> lines = new ArrayList<>();
    private final List<List<List<MarkText.Piece>>> rows = new ArrayList<>();
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private boolean ended;

    private TalkScreen(MessageTalk talk) { this.talk = talk; }

    private void show(MessageTalk next) {
        talk = next;
        if (width > 0) { layout(); }
    }

    private void finish() {
        ended = true;
        mc.displayGuiScreen(null);
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    @Override public void initGui() { layout(); }

    @Override public void setWorldAndResolution(@Nonnull Minecraft mc, int width, int height) { super.setWorldAndResolution(mc, Crisp.fit(width), Crisp.fit(height)); }

    private int textWidth() { return panelWidth - STRIPE - PAD * 2 - (talk.icon.isEmpty() ? 0 : ICON + PAD); }

    private void layout() {
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        title = talk.title.isEmpty() ? new ArrayList<>() : MarkText.pieces(talk.font, talk.title);
        lines = new ArrayList<>();
        for (String line : talk.lines) { lines.addAll(MarkText.wrap(MarkText.pieces(talk.font, line), textWidth())); }
        rows.clear();
        int height = PAD * 2 + (title.isEmpty() ? 0 : CardOverlay.LINE + ROW_PAD) + lines.size() * CardOverlay.LINE;
        if (!talk.replies.isEmpty()) { height += GAP; }
        for (int i = 0; i < talk.replies.size(); i++) {
            List<List<MarkText.Piece>> row = MarkText.wrap(MarkText.pieces(talk.font, (i + 1) + ". " + talk.replies.get(i)), textWidth());
            rows.add(row);
            height += rowHeight(row);
        }
        panelHeight = height;
        left = (this.width - panelWidth) / 2;
        top = Math.max(MARGIN, this.height - LIFT - panelHeight);
    }

    private static int rowHeight(List<List<MarkText.Piece>> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int firstRowTop() { return top + PAD + (title.isEmpty() ? 0 : CardOverlay.LINE + ROW_PAD) + lines.size() * CardOverlay.LINE + GAP; }

    private int rowAt(int mouseX, int mouseY) {
        if (mouseX < left || mouseX >= left + panelWidth) { return -1; }
        int at = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            int next = at + rowHeight(rows.get(i));
            if (mouseY >= at && mouseY < next) { return i; }
            at = next;
        }
        return -1;
    }

    @Override public void updateScreen() {
        Entity speaker = mc.world == null ? null : mc.world.getEntityByID(talk.npc);
        if (speaker == null || speaker.isDead || speaker.getDistance(mc.player) > REACH) { mc.displayGuiScreen(null); }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Crisp.raise();
        GlStateManager.enableBlend();
        Blends.translucent();
        if (talk.panel) { CardOverlay.panel(left, top, panelWidth, panelHeight, talk.background, 1.0F); }
        if (!talk.image.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(new ResourceLocation(talk.image));
            Gui.drawModalRectWithCustomSizedTexture(left, top, 0.0F, 0.0F, panelWidth, panelHeight, panelWidth, panelHeight);
        }
        if (talk.panel) { Gui.drawRect(left, top, left + STRIPE, top + panelHeight, CardOverlay.withAlpha(TEXT, 1.0F)); }
        int x = left + STRIPE + PAD;
        if (!talk.icon.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(talk.icon, x, top + PAD);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            x += ICON + PAD;
        }
        int y = top + PAD;
        if (!title.isEmpty()) {
            MarkText.draw(title, x, y, CardOverlay.withAlpha(TEXT, 1.0F));
            y += CardOverlay.LINE + ROW_PAD;
        }
        for (List<MarkText.Piece> line : lines) {
            MarkText.draw(line, x, y, CardOverlay.withAlpha(CardOverlay.LINE_COLOR, 1.0F));
            y += CardOverlay.LINE;
        }
        drawReplies(x, Crisp.fit(mouseX), Crisp.fit(mouseY));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        Crisp.lower();
    }

    private void drawReplies(int x, int mouseX, int mouseY) {
        int hovered = rowAt(mouseX, mouseY);
        int y = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            List<List<MarkText.Piece>> row = rows.get(i);
            int height = rowHeight(row);
            if (i == hovered) { Gui.drawRect(left + STRIPE, y, left + panelWidth, y + height, HOVER); }
            int lineY = y + ROW_PAD;
            for (List<MarkText.Piece> line : row) {
                MarkText.draw(line, x, lineY, CardOverlay.withAlpha(i == hovered ? HOVER_TEXT : TEXT, 1.0F));
                lineY += CardOverlay.LINE;
            }
            y += height;
        }
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) { return; }
        if (rows.isEmpty()) {
            mc.displayGuiScreen(null);
            return;
        }
        int row = rowAt(Crisp.fit(mouseX), Crisp.fit(mouseY));
        if (row >= 0) { RDPLNetwork.sendTalkPick(row + 1); }
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            mc.displayGuiScreen(null);
            return;
        }
        int number = keyCode - Keyboard.KEY_1 + 1;
        if (number >= 1 && number <= MOST_KEYS && number <= rows.size()) { RDPLNetwork.sendTalkPick(number); }
    }

    @Override public void onGuiClosed() { if (!ended && !rows.isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }

    public static class Handler extends AbstractClientMessageHandler<MessageTalk> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageTalk message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            TalkScreen current = mc.currentScreen instanceof TalkScreen ? (TalkScreen) mc.currentScreen : null;
            if (message.end) {
                if (current != null) { current.finish(); }
            }
            else if (current != null) { current.show(message); }
            else { mc.displayGuiScreen(new TalkScreen(message)); }
        }
    }
}
