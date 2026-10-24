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
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import javax.annotation.Nonnull;
import java.io.IOException;
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
    private static final int MOST_PAGES = 32;
    private static final int BUTTON = 12;
    private static final int COLUMN_MIN = PAD * 2 + 40;
    private static final int STEP = CardOverlay.LINE * 2;
    private static final long PICK_GUARD_MS = 200L;
    private static final int TITLE_FLOOR = 18;
    private static final int PREV = 1;
    private static final int NEXT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;
    private final List<Page> pages = new ArrayList<>();
    private MessageTalk talk;
    private List<List<MarkText.Piece>> title = new ArrayList<>();
    private List<List<MarkText.Piece>> lines = new ArrayList<>();
    private List<List<MarkText.Piece>> chosen = new ArrayList<>();
    private final List<List<List<MarkText.Piece>>> rows = new ArrayList<>();
    private String pending = "";
    private int viewing;
    private int scroll;
    private int contentHeight;
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private boolean column;
    private boolean waiting;
    private boolean dragging;
    private int dragOffset;
    private boolean ended;
    private boolean closeAfter;
    private long shownAt;

    private static final class Page {
        final MessageTalk talk;
        final String picked;

        Page(MessageTalk talk, String picked) {
            this.talk = talk;
            this.picked = picked;
        }
    }

    private TalkScreen(MessageTalk talk) {
        this.talk = talk;
        pages.add(new Page(talk, ""));
        shownAt = Minecraft.getSystemTime();
    }

    private void show(MessageTalk next) {
        int last = pages.size() - 1;
        pages.set(last, new Page(pages.get(last).talk, pending));
        pages.add(new Page(next, ""));
        if (pages.size() > MOST_PAGES) { pages.remove(0); }
        pending = "";
        talk = next;
        waiting = false;
        shownAt = Minecraft.getSystemTime();
        if (closeAfter) {
            mc.displayGuiScreen(null);
            return;
        }
        viewing = pages.size() - 1;
        scroll = 0;
        if (width > 0) { layout(); }
    }

    private MessageTalk shown() { return pages.get(viewing).talk; }

    private boolean live() { return viewing == pages.size() - 1; }

    private void pick(int number) {
        if (waiting || guarded() || !live() || number < 1 || number > rows.size()) { return; }
        pending = talk.replies.get(number - 1);
        waiting = true;
        RDPLNetwork.sendTalkPick(number);
    }

    private boolean guarded() { return Minecraft.getSystemTime() - shownAt < PICK_GUARD_MS; }

    private static int tint(int set, int fallback) { return set < 0 ? fallback : set; }

    private int rightEdge() { return column ? left + panelWidth - PAD - BUTTON - GAP : left + panelWidth; }

    private void finish() {
        ended = true;
        mc.displayGuiScreen(null);
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    @Override public void initGui() { layout(); }

    @Override public void setWorldAndResolution(@Nonnull Minecraft mc, int width, int height) { super.setWorldAndResolution(mc, Crisp.fit(width), Crisp.fit(height)); }

    private int textWidth() { return panelWidth - STRIPE - PAD * 2 - (shown().icon.isEmpty() ? 0 : ICON + PAD) - (column ? BUTTON + GAP : 0); }

    private void layout() {
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        int most = Math.max(COLUMN_MIN, height - MARGIN * 2 - LIFT);
        column = false;
        int total = measure();
        if (total > most) {
            column = true;
            total = measure();
        }
        contentHeight = total;
        panelHeight = Math.min(total, most);
        scroll = MathHelper.clamp(scroll, 0, total - panelHeight);
        left = (this.width - panelWidth) / 2;
        top = Math.max(MARGIN, this.height - LIFT - panelHeight);
    }

    private int measure() {
        Page page = pages.get(viewing);
        MessageTalk now = page.talk;
        title = now.title.isEmpty() ? new ArrayList<>() : MarkText.wrap(MarkText.pieces(now.font, now.title), Math.max(TITLE_FLOOR, textWidth() - titleShift()));
        lines = new ArrayList<>();
        for (String line : now.lines) { lines.addAll(MarkText.wrap(MarkText.pieces(now.font, line), textWidth())); }
        chosen = live() || page.picked.isEmpty() ? new ArrayList<>() : MarkText.wrap(MarkText.pieces(now.font, "> " + page.picked), textWidth());
        rows.clear();
        int height = PAD * 2 + headerHeight() + (lines.size() + chosen.size()) * CardOverlay.LINE;
        if (live() && !now.replies.isEmpty()) { height += GAP; }
        for (int i = 0; live() && i < now.replies.size(); i++) {
            List<List<MarkText.Piece>> row = MarkText.wrap(MarkText.pieces(now.font, (i + 1) + ". " + now.replies.get(i)), textWidth());
            rows.add(row);
            height += rowHeight(row);
        }
        return Math.max(height, column ? COLUMN_MIN : 0);
    }

    private int headerHeight() { return title.isEmpty() ? (talk.history ? CardOverlay.LINE + ROW_PAD : 0) : title.size() * CardOverlay.LINE + ROW_PAD; }

    private int titleShift() { return talk.history ? BUTTON * 2 + 4 + GAP : 0; }

    private int contentLeft() { return left + STRIPE + PAD + (shown().icon.isEmpty() ? 0 : ICON + PAD); }

    private static int rowHeight(List<List<MarkText.Piece>> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int contentTop() { return top + PAD - scroll; }

    private int firstRowTop() { return contentTop() + headerHeight() + (lines.size() + chosen.size()) * CardOverlay.LINE + GAP; }

    private int rowAt(int mouseX, int mouseY) {
        if (!live() || mouseX < left || mouseX >= rightEdge() || mouseY < top + PAD || mouseY >= top + panelHeight - PAD || buttonAt(mouseX, mouseY) != 0 || onTrack(mouseX, mouseY)) { return -1; }
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
        if (speaker == null || speaker.isDead || speaker.getDistance(mc.player) > REACH) {
            if (waiting) { closeAfter = true; }
            else { mc.displayGuiScreen(null); }
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Crisp.raise();
        GlStateManager.enableBlend();
        Blends.translucent();
        MessageTalk now = shown();
        if (now.panel) { CardOverlay.panel(left, top, panelWidth, panelHeight, now.background, 1.0F); }
        if (!now.image.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(new ResourceLocation(now.image));
            Gui.drawModalRectWithCustomSizedTexture(left, top, 0.0F, 0.0F, panelWidth, panelHeight, panelWidth, panelHeight);
        }
        if (now.panel) { Gui.drawRect(left, top, left + STRIPE, top + panelHeight, CardOverlay.withAlpha(TEXT, 1.0F)); }
        int x = left + STRIPE + PAD;
        if (!now.icon.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(now.icon, x, top + PAD);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            x += ICON + PAD;
        }
        int fitX = Crisp.fit(mouseX);
        int fitY = Crisp.fit(mouseY);
        double gui = Crisp.gui();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) Math.round(left * gui), (int) Math.round(mc.displayHeight - (top + panelHeight - PAD) * gui), (int) Math.round((rightEdge() - left) * gui), (int) Math.round((panelHeight - PAD * 2) * gui));
        int y = contentTop();
        int titleY = y;
        for (List<MarkText.Piece> line : title) {
            MarkText.draw(line, x + titleShift(), titleY, CardOverlay.withAlpha(tint(now.titleColor, TEXT), 1.0F));
            titleY += CardOverlay.LINE;
        }
        y += headerHeight();
        for (List<MarkText.Piece> line : lines) {
            MarkText.draw(line, x, y, CardOverlay.withAlpha(tint(now.textColor, CardOverlay.LINE_COLOR), 1.0F));
            y += CardOverlay.LINE;
        }
        for (List<MarkText.Piece> line : chosen) {
            MarkText.draw(line, x, y, CardOverlay.withAlpha(tint(now.replyColor, TEXT), 1.0F));
            y += CardOverlay.LINE;
        }
        drawReplies(x, fitX, fitY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawButtons(fitX, fitY);
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
            if (i == hovered) { Gui.drawRect(left + STRIPE, y, rightEdge(), y + height, band()); }
            int lineY = y + ROW_PAD;
            for (List<MarkText.Piece> line : row) {
                MarkText.draw(line, x, lineY, CardOverlay.withAlpha(i == hovered ? tint(talk.hoverColor, HOVER_TEXT) : tint(talk.replyColor, TEXT), 1.0F));
                lineY += CardOverlay.LINE;
            }
            y += height;
        }
    }

    private int band() { return talk.hoverBackground < 0 ? HOVER : (0x40 << 24) | talk.hoverBackground; }

    private int buttonLeft(int button) {
        switch (button) {
            case PREV: return contentLeft();
            case NEXT: return contentLeft() + BUTTON + 2;
            default: return left + panelWidth - PAD - BUTTON;
        }
    }

    private int buttonTop(int button) { return button == DOWN ? top + panelHeight - PAD - BUTTON : top + PAD; }

    private int trackTop() { return top + PAD + BUTTON; }

    private int trackHeight() { return panelHeight - PAD * 2 - BUTTON * 2; }

    private int maxScroll() { return Math.max(0, contentHeight - panelHeight); }

    private int thumbHeight() { return Math.min(trackHeight(), Math.max(8, trackHeight() * panelHeight / Math.max(1, contentHeight))); }

    private int thumbTop() { return maxScroll() == 0 ? trackTop() : trackTop() + (trackHeight() - thumbHeight()) * scroll / maxScroll(); }

    private boolean onTrack(int mouseX, int mouseY) { return column && mouseX >= buttonLeft(UP) && mouseX < buttonLeft(UP) + BUTTON && mouseY >= trackTop() && mouseY < trackTop() + trackHeight(); }

    private void drag(int mouseY) {
        int room = trackHeight() - thumbHeight();
        if (room > 0) { scroll = MathHelper.clamp((mouseY - dragOffset - trackTop()) * maxScroll() / room, 0, maxScroll()); }
    }

    private void clickTrack(int mouseY) {
        int thumb = thumbTop();
        if (mouseY >= thumb && mouseY < thumb + thumbHeight()) {
            dragging = true;
            dragOffset = mouseY - thumb;
        }
        else { scrollBy(mouseY < thumb ? -(panelHeight - PAD * 2) : panelHeight - PAD * 2); }
    }

    private boolean buttonShown(int button) { return button <= NEXT ? talk.history : column; }

    private boolean buttonEnabled(int button) {
        switch (button) {
            case PREV: return viewing > 0;
            case NEXT: return !live();
            case UP: return scroll > 0;
            default: return scroll < maxScroll();
        }
    }

    private int buttonAt(int mouseX, int mouseY) {
        for (int button = PREV; button <= DOWN; button++) {
            if (buttonShown(button) && mouseX >= buttonLeft(button) && mouseX < buttonLeft(button) + BUTTON && mouseY >= buttonTop(button) && mouseY < buttonTop(button) + BUTTON) { return button; }
        }
        return 0;
    }

    private void drawButtons(int mouseX, int mouseY) {
        int hovered = buttonAt(mouseX, mouseY);
        MessageTalk now = shown();
        if (column) {
            int trackX = buttonLeft(UP);
            Gui.drawRect(trackX, trackTop(), trackX + BUTTON, trackTop() + trackHeight(), 0x50000000);
            Gui.drawRect(trackX + 2, thumbTop(), trackX + BUTTON - 2, thumbTop() + thumbHeight(), dragging ? band() : 0x80FFFFFF);
        }
        for (int button = PREV; button <= DOWN; button++) {
            if (!buttonShown(button)) { continue; }
            boolean enabled = buttonEnabled(button);
            int x = buttonLeft(button);
            int y = buttonTop(button);
            if (now.panel) { Gui.drawRect(x, y, x + BUTTON, y + BUTTON, CardOverlay.withAlpha(now.background, 1.0F)); }
            Gui.drawRect(x, y, x + BUTTON, y + BUTTON, enabled && button == hovered ? band() : 0x30FFFFFF);
            int color = CardOverlay.withAlpha(tint(now.replyColor, TEXT), enabled ? 1.0F : 0.4F);
            if (button <= NEXT) {
                String label = button == PREV ? "<" : ">";
                GlStateManager.enableBlend();
                Blends.translucent();
                MarkText.draw(now.font, label, x + (BUTTON - MarkText.width(now.font, label)) / 2.0F, y + 2, color);
                continue;
            }
            for (int i = 0; i < 4; i++) {
                int row = button == UP ? y + 3 + i : y + 8 - i;
                Gui.drawRect(x + 5 - i, row, x + 7 + i, row + 1, color);
            }
        }
    }

    private void scrollBy(int amount) {
        scroll = MathHelper.clamp(scroll + amount, 0, maxScroll());
    }

    private void page(int step) {
        int to = MathHelper.clamp(viewing + step, 0, pages.size() - 1);
        if (!talk.history || to == viewing) { return; }
        viewing = to;
        scroll = 0;
        layout();
    }

    private void press(int button) {
        if (!buttonEnabled(button)) { return; }
        if (button == PREV) { page(-1); }
        else if (button == NEXT) { page(1); }
        else { scrollBy(button == UP ? -STEP : STEP); }
    }

    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) { return; }
        int x = Mouse.getEventX() * width / mc.displayWidth;
        int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        if (x >= left && x < left + panelWidth && y >= top && y < top + panelHeight) { scrollBy(-Integer.signum(wheel) * CardOverlay.LINE); }
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0 || guarded()) { return; }
        int button = buttonAt(mouseX, mouseY);
        if (button != 0) {
            press(button);
            return;
        }
        if (onTrack(mouseX, mouseY)) {
            clickTrack(mouseY);
            return;
        }
        if (talk.replies.isEmpty()) {
            mc.displayGuiScreen(null);
            return;
        }
        int row = rowAt(mouseX, mouseY);
        if (row >= 0) { pick(row + 1); }
    }

    @Override protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) { if (dragging) { drag(mouseY); } }

    @Override protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            if (waiting) { closeAfter = true; }
            else { mc.displayGuiScreen(null); }
            return;
        }
        if (keyCode == Keyboard.KEY_LEFT) { page(-1); }
        else if (keyCode == Keyboard.KEY_RIGHT) { page(1); }
        int number = keyCode - Keyboard.KEY_1 + 1;
        if (number >= 1 && number <= MOST_KEYS) { pick(number); }
    }

    @Override public void onGuiClosed() { if (!ended && !talk.replies.isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }

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
