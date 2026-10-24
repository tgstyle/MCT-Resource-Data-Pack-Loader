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
    private static final int MIN_BAR = 40;
    private static final int MIN_THUMB = 8;
    private static final int MOST_PAGES = 32;
    private static final int COLUMN_MIN = PAD * 2 + MIN_BAR;
    private static final int STEP = CardOverlay.LINE * 2;
    private static final long PICK_GUARD_MS = 200L;
    private static final int PREV = 1;
    private static final int NEXT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;
    private record Page(MessageTalk talk, String picked) {}
    private final List<Page> pages = new ArrayList<>();
    private List<FormattedCharSequence> title = new ArrayList<>();
    private List<FormattedCharSequence> lines = new ArrayList<>();
    private List<FormattedCharSequence> pickedLines = new ArrayList<>();
    private final List<List<FormattedCharSequence>> rows = new ArrayList<>();
    private int viewing;
    private int scroll;
    private int contentHeight;
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private boolean column;
    private boolean ended;
    private boolean waiting;
    private boolean dragging;
    private int grab;
    private boolean closeAfter;
    private long shownAt;
    private String pending;

    private TalkScreen(MessageTalk talk) {
        super(Component.literal("Talk"));
        pages.add(new Page(talk, null));
        shownAt = Util.getMillis();
    }

    private boolean guarded() { return Util.getMillis() - shownAt < PICK_GUARD_MS; }

    private MessageTalk talk() { return pages.get(viewing).talk(); }

    private MessageTalk newest() { return pages.getLast().talk(); }

    private boolean live() { return viewing == pages.size() - 1; }

    public static void receive(MessageTalk message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof TalkScreen screen) {
            if (message.end()) { screen.finish(); }
            else { screen.show(message); }
        }
        else if (!message.end()) { mc.setScreen(new TalkScreen(message)); }
    }

    private void show(MessageTalk next) {
        int last = pages.size() - 1;
        pages.set(last, new Page(pages.get(last).talk(), pending));
        pages.add(new Page(next, null));
        if (pages.size() > MOST_PAGES) { pages.removeFirst(); }
        pending = null;
        waiting = false;
        viewing = pages.size() - 1;
        scroll = 0;
        shownAt = Util.getMillis();
        if (width > 0) { layout(); }
        if (closeAfter) { onClose(); }
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

    private List<FormattedCharSequence> wrap(String said, int wide) { return font.split(MarkText.text(CardFont.of(talk().font()), said), wide); }

    private List<FormattedCharSequence> wrap(String said) { return wrap(said, textWidth()); }

    private int titleShift() { return talk().history() ? BUTTON * 2 + 4 + GAP : 0; }

    private int headerHeight() {
        if (!title.isEmpty()) { return title.size() * CardOverlay.LINE + ROW_PAD; }
        return talk().history() ? CardOverlay.LINE + ROW_PAD : 0;
    }

    private int maxHeight() { return Math.max(COLUMN_MIN, height - MARGIN * 2 - LIFT); }

    private boolean overflows() { return contentHeight > panelHeight; }

    private int rightEdge() { return column ? left + panelWidth - PAD - BUTTON - GAP : left + panelWidth; }

    private int columnLeft() { return left + panelWidth - PAD - BUTTON; }

    private int contentTop() { return top + PAD - scroll; }

    private static int pick(int set, int fallback) { return set < 0 ? fallback : set; }

    private void layout() {
        column = false;
        measure();
        if (overflows()) {
            column = true;
            measure();
        }
        scroll = Math.clamp(scroll, 0, maxScroll());
    }

    private void measure() {
        MessageTalk talk = talk();
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        title = talk.title().isEmpty() ? new ArrayList<>() : wrap(talk.title(), Math.max(1, textWidth() - titleShift()));
        lines = new ArrayList<>();
        for (String line : talk.lines()) { lines.addAll(wrap(line)); }
        String picked = pages.get(viewing).picked();
        pickedLines = live() || picked == null || picked.isEmpty() ? new ArrayList<>() : wrap("> " + picked);
        rows.clear();
        int total = PAD * 2 + headerHeight() + (lines.size() + pickedLines.size()) * CardOverlay.LINE;
        if (live()) {
            if (!talk.replies().isEmpty()) { total += GAP; }
            for (int i = 0; i < talk.replies().size(); i++) {
                List<FormattedCharSequence> row = wrap((i + 1) + ". " + talk.replies().get(i));
                rows.add(row);
                total += rowHeight(row);
            }
        }
        if (column) { total = Math.max(total, COLUMN_MIN); }
        contentHeight = total;
        panelHeight = Math.min(total, maxHeight());
        left = (width - panelWidth) / 2;
        top = Math.max(MARGIN, height - LIFT - panelHeight);
    }

    private static int rowHeight(List<FormattedCharSequence> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int firstRowTop() { return contentTop() + headerHeight() + (lines.size() + pickedLines.size()) * CardOverlay.LINE + GAP; }

    private int rowAt(double mouseX, double mouseY) {
        if (!live() || mouseX < left || mouseX >= rightEdge() || mouseY < top + PAD || mouseY >= top + panelHeight - PAD || buttonAt(mouseX, mouseY) != 0 || onTrack(mouseX, mouseY)) { return -1; }
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
            if (waiting) { closeAfter = true; }
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
        double fitX = Crisp.fit((double) mouseX);
        double fitY = Crisp.fit((double) mouseY);
        graphics.enableScissor(Crisp.real(left), Crisp.real(top + PAD), Crisp.real(rightEdge()), Crisp.real(top + panelHeight - PAD));
        int y = contentTop();
        for (FormattedCharSequence line : title) {
            graphics.drawString(font, line, x + titleShift(), y, CardOverlay.withAlpha(pick(talk.titleColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        y = contentTop() + headerHeight();
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y, CardOverlay.withAlpha(pick(talk.textColor(), CardOverlay.LINE_COLOR), 1.0F), true);
            y += CardOverlay.LINE;
        }
        for (FormattedCharSequence line : pickedLines) {
            graphics.drawString(font, line, x, y, CardOverlay.withAlpha(pick(talk.replyColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        drawReplies(graphics, x, fitX, fitY);
        graphics.disableScissor();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1.0F);
        drawButtons(graphics, fitX, fitY);
        graphics.pose().popPose();
        RenderSystem.disableBlend();
        Crisp.lower(graphics);
    }

    private void drawReplies(GuiGraphics graphics, int x, double mouseX, double mouseY) {
        MessageTalk talk = talk();
        int hovered = rowAt(mouseX, mouseY);
        int band = band();
        int y = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            List<FormattedCharSequence> row = rows.get(i);
            int rowHeight = rowHeight(row);
            if (i == hovered) { graphics.fill(left + STRIPE, y, rightEdge(), y + rowHeight, band); }
            int lineY = y + ROW_PAD;
            for (FormattedCharSequence line : row) {
                graphics.drawString(font, line, x, lineY, CardOverlay.withAlpha(i == hovered ? pick(talk.hoverColor(), HOVER_TEXT) : pick(talk.replyColor(), TEXT), 1.0F), true);
                lineY += CardOverlay.LINE;
            }
            y += rowHeight;
        }
    }

    private int maxScroll() { return Math.max(0, contentHeight - panelHeight); }

    private int trackTop() { return top + PAD + BUTTON; }

    private int trackHeight() { return panelHeight - PAD * 2 - BUTTON * 2; }

    private int thumbHeight() { return Math.clamp((long) trackHeight() * panelHeight / Math.max(1, contentHeight), MIN_THUMB, Math.max(MIN_THUMB, trackHeight())); }

    private int thumbY() { return maxScroll() == 0 ? trackTop() : trackTop() + (trackHeight() - thumbHeight()) * scroll / maxScroll(); }

    private int contentLeft() { return left + STRIPE + PAD + (talk().icon().isEmpty() ? 0 : ICON + PAD); }

    private int buttonLeft(int button) {
        return switch (button) {
            case PREV -> contentLeft();
            case NEXT -> contentLeft() + BUTTON + 2;
            default -> columnLeft();
        };
    }

    private int buttonTop(int button) { return button == DOWN ? top + panelHeight - PAD - BUTTON : top + PAD; }

    private boolean buttonShown(int button) { return button <= NEXT ? talk().history() : column; }

    private boolean buttonEnabled(int button) {
        return switch (button) {
            case PREV -> viewing > 0;
            case NEXT -> !live();
            case UP -> scroll > 0;
            default -> scroll < maxScroll();
        };
    }

    private int buttonAt(double mouseX, double mouseY) {
        for (int button = PREV; button <= DOWN; button++) {
            if (buttonShown(button) && mouseX >= buttonLeft(button) && mouseX < buttonLeft(button) + BUTTON && mouseY >= buttonTop(button) && mouseY < buttonTop(button) + BUTTON) { return button; }
        }
        return 0;
    }

    private boolean onTrack(double mouseX, double mouseY) { return column && mouseX >= columnLeft() && mouseX < columnLeft() + BUTTON && mouseY >= trackTop() && mouseY < trackTop() + trackHeight(); }

    private int band() { return talk().hoverBackground() < 0 ? HOVER : 0x40000000 | talk().hoverBackground(); }

    private void drawButtons(GuiGraphics graphics, double mouseX, double mouseY) {
        int hovered = buttonAt(mouseX, mouseY);
        MessageTalk talk = talk();
        if (column) {
            graphics.fill(columnLeft(), trackTop(), columnLeft() + BUTTON, trackTop() + trackHeight(), 0x50000000);
            graphics.fill(columnLeft() + 2, thumbY(), columnLeft() + BUTTON - 2, thumbY() + thumbHeight(), dragging ? band() : 0x80FFFFFF);
        }
        for (int button = PREV; button <= DOWN; button++) {
            if (!buttonShown(button)) { continue; }
            boolean enabled = buttonEnabled(button);
            int x = buttonLeft(button);
            int y = buttonTop(button);
            if (talk.panel()) { graphics.fill(x, y, x + BUTTON, y + BUTTON, CardOverlay.withAlpha(talk.background(), 1.0F)); }
            graphics.fill(x, y, x + BUTTON, y + BUTTON, enabled && button == hovered ? band() : 0x30FFFFFF);
            int color = CardOverlay.withAlpha(pick(talk.replyColor(), TEXT), enabled ? 1.0F : 0.4F);
            if (button <= NEXT) {
                Component label = MarkText.text(CardFont.of(talk.font()), button == PREV ? "<" : ">");
                graphics.drawString(font, label, x + (BUTTON - font.width(label)) / 2, y + 2, color, true);
                continue;
            }
            for (int i = 0; i < 4; i++) {
                int rowY = button == UP ? y + 3 + i : y + 8 - i;
                graphics.fill(x + 5 - i, rowY, x + 7 + i, rowY + 1, color);
            }
        }
    }

    private void page(int delta) {
        int to = Math.clamp(viewing + delta, 0, pages.size() - 1);
        if (to == viewing) { return; }
        viewing = to;
        scroll = 0;
        layout();
    }

    private void scrollBy(int delta) { scroll = Math.clamp(scroll + delta, 0, maxScroll()); }

    private void press(int button) {
        if (!buttonEnabled(button)) { return; }
        if (button == PREV) { page(-1); }
        else if (button == NEXT) { page(1); }
        else { scrollBy(button == UP ? -STEP : STEP); }
    }

    private boolean columnClick(double mouseX, double mouseY) {
        int button = buttonAt(mouseX, mouseY);
        if (button != 0) {
            press(button);
            return true;
        }
        if (!onTrack(mouseX, mouseY)) { return false; }
        if (mouseY < thumbY()) { scrollBy(-(panelHeight - PAD * 2)); }
        else if (mouseY >= thumbY() + thumbHeight()) { scrollBy(panelHeight - PAD * 2); }
        else {
            dragging = true;
            grab = (int) mouseY - thumbY();
        }
        return true;
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging) { return super.mouseDragged(mouseX, mouseY, button, dragX, dragY); }
        int room = trackHeight() - thumbHeight();
        if (room > 0) { scroll = Math.clamp(Math.round((Crisp.fit(mouseY) - grab - trackTop()) * maxScroll() / room), 0, maxScroll()); }
        return true;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void choose(int number) {
        if (waiting || guarded()) { return; }
        waiting = true;
        pending = newest().replies().get(number - 1);
        RDPLNetwork.sendTalkPick(number);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) { return false; }
        if (guarded()) { return true; }
        double fitX = Crisp.fit(mouseX);
        double fitY = Crisp.fit(mouseY);
        if (columnClick(fitX, fitY)) { return true; }
        if (newest().replies().isEmpty()) {
            onClose();
            return true;
        }
        int row = rowAt(fitX, fitY);
        if (row < 0) { return false; }
        choose(row + 1);
        return true;
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double fitX = Crisp.fit(mouseX);
        double fitY = Crisp.fit(mouseY);
        if (fitX < left || fitX >= left + panelWidth || fitY < top || fitY >= top + panelHeight) { return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY); }
        scrollBy((int) -Math.signum(scrollY) * CardOverlay.LINE);
        return true;
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE || minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            if (waiting) { closeAfter = true; }
            else { onClose(); }
            return true;
        }
        if (talk().history() && (keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT)) {
            page(keyCode == InputConstants.KEY_LEFT ? -1 : 1);
            return true;
        }
        int number = keyCode - InputConstants.KEY_1 + 1;
        if (!live() || number < 1 || number > MOST_KEYS || number > rows.size()) { return super.keyPressed(keyCode, scanCode, modifiers); }
        choose(number);
        return true;
    }

    @Override public void removed() { if (!ended && !newest().replies().isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }
}
