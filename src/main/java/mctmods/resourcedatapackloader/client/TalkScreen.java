package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.network.MessageTalk;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class TalkScreen extends Screen {
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
    private static final int BUTTON = 12;
    private static final int MOST_PAGES = 32;
    private static final int BAND = 0x40;
    private static final float DIM = 0.4F;
    private static final long PICK_GUARD_MS = 200L;
    private static final int BASE = 0x30FFFFFF;
    private static final int TRACK = 0x50000000;
    private static final int THUMB = 0x80FFFFFF;
    private static final int COLUMN_MIN = PAD * 2 + 40;
    private final List<Page> pages = new ArrayList<>();
    private List<FormattedCharSequence> title = new ArrayList<>();
    private List<FormattedCharSequence> lines = new ArrayList<>();
    private List<FormattedCharSequence> chosen = new ArrayList<>();
    private final List<List<FormattedCharSequence>> rows = new ArrayList<>();
    private int viewing;
    private int scroll;
    private int maxScroll;
    private int contentHeight;
    private long shownAt;
    private boolean closeAsked;
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private boolean column;
    private boolean ended;
    private boolean waiting;
    private boolean dragging;
    private int grab;
    private String pending;

    private record Page(MessageTalk talk, String picked) {}

    private TalkScreen(MessageTalk talk) {
        super(Component.literal("Talk"));
        pages.add(new Page(talk, null));
        shownAt = Util.getMillis();
    }

    private MessageTalk talk() { return pages.get(viewing).talk(); }

    private MessageTalk newest() { return pages.get(pages.size() - 1).talk(); }

    private boolean live() { return viewing == pages.size() - 1; }

    public static void receive(MessageTalk message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof TalkScreen screen) {
            if (message.end()) { screen.finish(); }
            else { screen.show(message); }
        }
        else if (!message.end()) { mc.setScreen(new TalkScreen(message)); }
    }

    private boolean guarded() { return Util.getMillis() - shownAt < PICK_GUARD_MS; }

    private void show(MessageTalk next) {
        shownAt = Util.getMillis();
        int last = pages.size() - 1;
        pages.set(last, new Page(pages.get(last).talk(), pending));
        pages.add(new Page(next, null));
        if (pages.size() > MOST_PAGES) { pages.remove(0); }
        pending = null;
        waiting = false;
        viewing = pages.size() - 1;
        scroll = 0;
        if (closeAsked) {
            onClose();
            return;
        }
        if (width > 0) { layout(); }
    }

    private void pick(int number) {
        if (waiting || guarded()) { return; }
        waiting = true;
        pending = newest().replies().get(number - 1);
        RDPLNetwork.sendTalkPick(number);
    }

    private void page(int to) {
        viewing = to;
        scroll = 0;
        layout();
    }

    private void finish() {
        ended = true;
        onClose();
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override protected void init() {
        width = Crisp.width();
        height = Crisp.height();
        layout();
    }

    private int textWidth() { return panelWidth - STRIPE - PAD * 2 - (talk().icon().isEmpty() ? 0 : ICON + PAD) - (column ? BUTTON + GAP : 0); }

    private List<FormattedCharSequence> wrap(String said, int less) { return font.split(MarkText.text(CardFont.of(talk().font()), said), Math.max(1, textWidth() - less)); }

    private int contentLeft() { return left + STRIPE + PAD + (talk().icon().isEmpty() ? 0 : ICON + PAD); }

    private int pagerWidth() { return paging() ? BUTTON * 2 + 4 + GAP : 0; }

    private int titleHeight() { return title.isEmpty() ? (paging() ? CardOverlay.LINE + ROW_PAD : 0) : title.size() * CardOverlay.LINE + ROW_PAD; }

    private int columnLeft() { return left + panelWidth - PAD - BUTTON; }

    private int edge() { return column ? columnLeft() - GAP : left + panelWidth; }

    private int contentTop() { return top + PAD - scroll; }

    private static int tone(int set, int fallback) { return set < 0 ? fallback : set; }

    private boolean paging() { return newest().history(); }

    private void layout() {
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        left = (width - panelWidth) / 2;
        column = false;
        int total = build();
        int most = Math.max(COLUMN_MIN, height - MARGIN * 2 - LIFT);
        if (total > most) {
            column = true;
            total = Math.max(build(), COLUMN_MIN);
        }
        contentHeight = total;
        panelHeight = Math.min(total, most);
        maxScroll = Math.max(0, total - panelHeight);
        scroll = Math.max(0, Math.min(scroll, maxScroll));
        top = Math.max(MARGIN, height - LIFT - panelHeight);
    }

    private int build() {
        Page page = pages.get(viewing);
        MessageTalk talk = page.talk();
        title = talk.title().isEmpty() ? new ArrayList<>() : wrap(talk.title(), pagerWidth());
        lines = new ArrayList<>();
        for (String line : talk.lines()) { lines.addAll(wrap(line, 0)); }
        rows.clear();
        chosen = page.picked() == null || page.picked().isEmpty() ? new ArrayList<>() : wrap("> " + page.picked(), 0);
        int total = PAD * 2 + titleHeight() + (lines.size() + chosen.size()) * CardOverlay.LINE;
        if (live()) {
            for (int i = 0; i < talk.replies().size(); i++) {
                List<FormattedCharSequence> row = wrap((i + 1) + ". " + talk.replies().get(i), 0);
                rows.add(row);
                total += rowHeight(row);
            }
        }
        if (!rows.isEmpty()) { total += GAP; }
        return total;
    }

    private static int rowHeight(List<FormattedCharSequence> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int firstRowTop() { return contentTop() + titleHeight() + (lines.size() + chosen.size()) * CardOverlay.LINE + GAP; }

    private boolean onControl(double mouseX, double mouseY) {
        if (paging() && (inside(mouseX, mouseY, contentLeft(), pagerTop()) || inside(mouseX, mouseY, contentLeft() + BUTTON + 2, pagerTop()))) { return true; }
        return column && mouseX >= columnLeft() && mouseX < columnLeft() + BUTTON && mouseY >= pagerTop() && mouseY < downTop() + BUTTON;
    }

    private int rowAt(double mouseX, double mouseY) {
        if (!live() || onControl(mouseX, mouseY) ||mouseX < left || mouseX >= edge() || mouseY < top + PAD || mouseY >= top + panelHeight - PAD) { return -1; }
        int at = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            int next = at + rowHeight(rows.get(i));
            if (mouseY >= at && mouseY < next) { return i; }
            at = next;
        }
        return -1;
    }

    @Override public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Entity speaker = mc.level == null ? null : mc.level.getEntity(newest().npc());
        if (speaker == null || !speaker.isAlive() || mc.player == null || speaker.distanceTo(mc.player) > REACH) {
            if (waiting) { closeAsked = true; }
            else { onClose(); }
        }
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        MessageTalk talk = talk();
        Crisp.raise(graphics);
        RenderSystem.enableBlend();
        if (talk.panel()) { CardOverlay.panel(graphics, left, top, panelWidth, panelHeight, talk.background(), 1.0F); }
        if (!talk.image().isEmpty()) {
            ResourceLocation image = ContentParser.location(talk.image());
            if (image != null) { graphics.blit(image, left, top, 0, 0.0F, 0.0F, panelWidth, panelHeight, panelWidth, panelHeight); }
        }
        if (talk.panel()) { graphics.fill(left, top, left + STRIPE, top + panelHeight, CardOverlay.withAlpha(TEXT, 1.0F)); }
        int x = left + STRIPE + PAD;
        if (!talk.icon().isEmpty()) {
            graphics.renderItem(talk.icon(), x, top + PAD);
            x += ICON + PAD;
        }
        graphics.enableScissor(Crisp.real(left), Crisp.real(top + PAD), Crisp.real(edge()), Crisp.real(top + panelHeight - PAD));
        int y = contentTop();
        for (FormattedCharSequence line : title) {
            graphics.drawString(font, line, x + pagerWidth(), y, CardOverlay.withAlpha(tone(talk.titleColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        y = contentTop() + titleHeight();
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y, CardOverlay.withAlpha(tone(talk.textColor(), CardOverlay.LINE_COLOR), 1.0F), true);
            y += CardOverlay.LINE;
        }
        for (FormattedCharSequence line : chosen) {
            graphics.drawString(font, line, x, y, CardOverlay.withAlpha(tone(talk.replyColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        drawReplies(graphics, talk, x,Crisp.fit((double) mouseX), Crisp.fit((double) mouseY));
        graphics.disableScissor();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1.0F);
        drawButtons(graphics, talk, Crisp.fit((double) mouseX), Crisp.fit((double) mouseY));
        graphics.pose().popPose();
        RenderSystem.disableBlend();
        Crisp.lower(graphics);
    }

    private void drawReplies(GuiGraphics graphics, MessageTalk talk, int x, double mouseX, double mouseY) {
        int hovered = rowAt(mouseX, mouseY);
        int y = firstRowTop();
        int replyColor = tone(talk.replyColor(), TEXT);
        for (int i = 0; i < rows.size(); i++) {
            List<FormattedCharSequence> row = rows.get(i);
            int rowHeight = rowHeight(row);
            if (i == hovered) { graphics.fill(left + STRIPE, y, edge(), y + rowHeight, band(talk)); }
            int lineY = y + ROW_PAD;
            for (FormattedCharSequence line : row) {
                graphics.drawString(font, line, x, lineY, CardOverlay.withAlpha(i == hovered ? tone(talk.hoverColor(), HOVER_TEXT) : replyColor, 1.0F), true);
                lineY += CardOverlay.LINE;
            }
            y += rowHeight;
        }
    }

    private static int band(MessageTalk talk) { return talk.hoverBackground() < 0 ? HOVER : BAND << 24 | talk.hoverBackground(); }

    private int pagerTop() { return top + PAD; }

    private int downTop() { return top + panelHeight - PAD - BUTTON; }

    private int trackTop() { return top + PAD + BUTTON; }

    private int trackHeight() { return downTop() - trackTop(); }

    private int thumbHeight() { return Math.min(trackHeight(), Math.max(8, trackHeight() * panelHeight / Math.max(1, contentHeight))); }

    private int thumbTop() { return trackTop() + (maxScroll == 0 ? 0 : (trackHeight() - thumbHeight()) * scroll / maxScroll); }

    private static boolean inside(double mouseX, double mouseY, int x, int y) { return mouseX >= x && mouseX < x + BUTTON && mouseY >= y && mouseY < y + BUTTON; }

    private void drawButtons(GuiGraphics graphics, MessageTalk talk, double mouseX, double mouseY) {
        if (column) {
            int x = columnLeft();
            graphics.fill(x, trackTop(), x + BUTTON, downTop(), TRACK);
            graphics.fill(x + 2, thumbTop(), x + BUTTON - 2, thumbTop() + thumbHeight(), dragging ? band(talk) : THUMB);
        }
        if (paging()) {
            drawLabel(graphics, talk, contentLeft(), viewing > 0, mouseX, mouseY, "<");
            drawLabel(graphics, talk, contentLeft() + BUTTON + 2, !live(), mouseX, mouseY, ">");
        }
        if (column) {
            int x = columnLeft();
            drawArrow(graphics, x, drawFrame(graphics, talk, x, pagerTop(), scroll > 0, mouseX, mouseY), pagerTop(), true);
            drawArrow(graphics, x, drawFrame(graphics, talk, x, downTop(), scroll < maxScroll, mouseX, mouseY), downTop(), false);
        }
    }

    private int drawFrame(GuiGraphics graphics, MessageTalk talk, int x, int y, boolean enabled, double mouseX, double mouseY) {
        if (talk.panel()) { graphics.fill(x, y, x + BUTTON, y + BUTTON, CardOverlay.withAlpha(talk.background(), 1.0F)); }
        graphics.fill(x, y, x + BUTTON, y + BUTTON, enabled && inside(mouseX, mouseY, x, y) ? band(talk) : BASE);
        return CardOverlay.withAlpha(tone(talk.replyColor(), TEXT), enabled ? 1.0F : DIM);
    }

    private void drawLabel(GuiGraphics graphics, MessageTalk talk, int x, boolean enabled, double mouseX, double mouseY, String label) {
        int color = drawFrame(graphics, talk, x, pagerTop(), enabled, mouseX, mouseY);
        Component text = MarkText.literal(CardFont.of(talk.font()), label);
        graphics.drawString(font, text, x + (BUTTON - font.width(text)) / 2, pagerTop() + 2, color, true);
    }

    private static void drawArrow(GuiGraphics graphics, int x, int color, int y, boolean up) {
        for (int i = 0; i < 4; i++) {
            int row = up ? y + 3 + i : y + 8 - i;
            graphics.fill(x + 5 - i, row, x + 7 + i, row + 1, color);
        }
    }

    private void scrollBy(int amount) { scroll = Math.max(0, Math.min(scroll + amount, maxScroll)); }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) { return false; }
        double x = Crisp.fit(mouseX);
        double y = Crisp.fit(mouseY);
        if (guarded()) { return true; }
        if (paging()) {
            if (inside(x, y, contentLeft(), pagerTop())) {
                if (viewing > 0) { page(viewing - 1); }
                return true;
            }
            if (inside(x, y, contentLeft() + BUTTON + 2, pagerTop())) {
                if (!live()) { page(viewing + 1); }
                return true;
            }
        }
        if (column) {
            if (inside(x, y, columnLeft(), pagerTop())) {
                scrollBy(-CardOverlay.LINE * 2);
                return true;
            }
            if (inside(x, y, columnLeft(), downTop())) {
                scrollBy(CardOverlay.LINE * 2);
                return true;
            }
            if (x >= columnLeft() && x < columnLeft() + BUTTON && y >= trackTop() && y < downTop()) {
                if (y < thumbTop()) { scrollBy(-(panelHeight - PAD * 2)); }
                else if (y >= thumbTop() + thumbHeight()) { scrollBy(panelHeight - PAD * 2); }
                else {
                    dragging = true;
                    grab = (int) y - thumbTop();
                }
                return true;
            }
        }
        if (newest().replies().isEmpty()) {
            onClose();
            return true;
        }
        int row = rowAt(x, y);
        if (row < 0) { return false; }
        pick(row + 1);
        return true;
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging) { return super.mouseDragged(mouseX, mouseY, button, dragX, dragY); }
        int travel = trackHeight() - thumbHeight();
        if (travel > 0) { scroll = Math.max(0, Math.min((int) Math.round((Crisp.fit(mouseY) - grab - trackTop()) * maxScroll / travel), maxScroll)); }
        return true;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        double x = Crisp.fit(mouseX);
        double y = Crisp.fit(mouseY);
        if (x < left || x >= left + panelWidth || y < top || y >= top + panelHeight) { return super.mouseScrolled(mouseX, mouseY, delta); }
        scrollBy((int) -Math.signum(delta) * CardOverlay.LINE);
        return true;
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE || minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            if (waiting) { closeAsked = true; }
            else { onClose(); }
            return true;
        }
        if (paging() && keyCode == InputConstants.KEY_LEFT && viewing > 0) {
            page(viewing - 1);
            return true;
        }
        if (paging() && keyCode == InputConstants.KEY_RIGHT && !live()) {
            page(viewing + 1);
            return true;
        }
        int number = keyCode - InputConstants.KEY_1 + 1;
        if (!live() || number < 1 || number > MOST_KEYS || number > rows.size()) { return super.keyPressed(keyCode, scanCode, modifiers); }
        pick(number);
        return true;
    }

    @Override public void removed() { if (!ended && !newest().replies().isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }
}
