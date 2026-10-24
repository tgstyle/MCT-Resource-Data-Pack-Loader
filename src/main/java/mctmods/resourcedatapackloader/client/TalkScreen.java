package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.network.MessageTalk;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
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
    private static final int BUTTON_GAP = 2;
    private static final int MIN_COLUMN = PAD * 2 + 40;
    private static final int MIN_THUMB = 8;
    private static final int TRACK_BACK = 0x50000000;
    private static final int THUMB_BACK = 0x80FFFFFF;
    private static final int NAV = BUTTON * 2 + 4;
    private static final int MOST_PAGES = 32;
    private static final int HOVER_ALPHA = 0x40;
    private static final int BUTTON_BACK = 0x30FFFFFF;
    private static final long PICK_GUARD_MS = 200L;
    private static final float DIM = 0.4F;
    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private MessageTalk talk;
    private final List<Page> pages = new ArrayList<>();
    private int viewing;
    private String pending = "";
    private boolean waiting;
    private List<FormattedCharSequence> title = new ArrayList<>();
    private List<FormattedCharSequence> lines = new ArrayList<>();
    private List<FormattedCharSequence> picked = new ArrayList<>();
    private final List<List<FormattedCharSequence>> rows = new ArrayList<>();
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private int scroll;
    private int maxScroll;
    private boolean column;
    private boolean dragging;
    private double dragOffset;
    private boolean ended;
    private boolean closeAfter;
    private long shownAt;

    private record Page(MessageTalk talk, String picked) {}

    private TalkScreen(MessageTalk talk) {
        super(Component.literal("Talk"));
        this.talk = talk;
        pages.add(new Page(talk, ""));
        shownAt = System.currentTimeMillis();
    }

    public static void receive(MessageTalk message) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientCompat.screen(mc) instanceof TalkScreen screen) {
            if (message.end()) { screen.finish(); }
            else { screen.show(message); }
        }
        else if (!message.end()) { ClientCompat.setScreen(mc, new TalkScreen(message)); }
    }

    private void show(MessageTalk next) {
        int last = pages.size() - 1;
        pages.set(last, new Page(pages.get(last).talk(), pending));
        pages.add(new Page(next, ""));
        if (pages.size() > MOST_PAGES) { pages.removeFirst(); }
        pending = "";
        waiting = false;
        talk = next;
        shownAt = System.currentTimeMillis();
        if (closeAfter) {
            onClose();
            return;
        }
        viewing = pages.size() - 1;
        scroll = 0;
        if (width > 0) { layout(); }
    }

    private boolean guarded() { return System.currentTimeMillis() - shownAt < PICK_GUARD_MS; }

    private boolean live() { return viewing == pages.size() - 1; }

    private void pick(int number) {
        if (waiting || guarded() || !live() || number > talk.replies().size()) { return; }
        waiting = true;
        pending = talk.replies().get(number - 1);
        RDPLNetwork.sendTalkPick(number);
    }

    private void page(int step) {
        int to = Mth.clamp(viewing + step, 0, pages.size() - 1);
        if (!talk.history() || to == viewing) { return; }
        viewing = to;
        scroll = 0;
        layout();
    }

    private int view() { return panelHeight - PAD * 2; }

    private int trackTop() { return top + PAD + BUTTON; }

    private int trackHeight() { return view() - BUTTON * 2; }

    private int thumbHeight() { return Math.clamp((long) trackHeight() * panelHeight / Math.max(1, panelHeight + maxScroll), MIN_THUMB, Math.max(MIN_THUMB, trackHeight())); }

    private int thumbTop() { return maxScroll == 0 ? trackTop() : trackTop() + (trackHeight() - thumbHeight()) * scroll / maxScroll; }

    private boolean onTrack(double mouseX, double mouseY) { return column && mouseX >= columnLeft() && mouseX < columnLeft() + BUTTON && mouseY >= trackTop() && mouseY < trackTop() + trackHeight(); }

    private void dragTo(double mouseY) {
        int span = trackHeight() - thumbHeight();
        if (span > 0) { scroll = Mth.clamp((int) Math.round((mouseY - dragOffset - trackTop()) * maxScroll / span), 0, maxScroll); }
    }

    private void scrollBy(int by) { scroll = Mth.clamp(scroll + by, 0, maxScroll); }

    private static int shade(int set, int fallback) { return set < 0 ? fallback : set; }

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

    private int textWidth() { return panelWidth - STRIPE - PAD * 2 - (talk.icon().isEmpty() ? 0 : ICON + PAD) - (column ? BUTTON + GAP : 0); }

    private int navShift() { return talk.history() ? NAV + GAP : 0; }

    private String pickedLine() { return live() ? "" : pages.get(viewing).picked(); }

    private int contentLeft() { return left + STRIPE + PAD + (talk.icon().isEmpty() ? 0 : ICON + PAD); }

    private int headerHeight() { return title.isEmpty() ? (talk.history() ? CardOverlay.LINE + ROW_PAD : 0) : title.size() * CardOverlay.LINE + ROW_PAD; }

    private int buttonX(int button) {
        return switch (button) {
            case PREVIOUS -> contentLeft();
            case NEXT -> contentLeft() + BUTTON + BUTTON_GAP;
            default -> columnLeft();
        };
    }

    private int columnLeft() { return left + panelWidth - PAD - BUTTON; }

    private int contentRight() { return column ? columnLeft() - GAP : left + panelWidth; }

    private int contentTop() { return top + PAD - scroll; }

    private List<FormattedCharSequence> wrap(String said) { return wrap(said, textWidth()); }

    private List<FormattedCharSequence> wrap(String said, int wide) { return font.split(MarkText.text(CardFont.of(talk.font()), said), Math.max(1, wide)); }

    private void layout() {
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        left = (width - panelWidth) / 2;
        column = false;
        int total = build();
        int most = Math.max(MIN_COLUMN, height - MARGIN * 2 - LIFT);
        if (total > most) {
            column = true;
            total = Math.max(build(), MIN_COLUMN);
        }
        panelHeight = Math.min(total, most);
        top = Math.max(MARGIN, height - LIFT - panelHeight);
        maxScroll = Math.max(0, total - panelHeight);
        scroll = Mth.clamp(scroll, 0, maxScroll);
    }

    private int build() {
        Page page = pages.get(viewing);
        title = page.talk().title().isEmpty() ? new ArrayList<>() : wrap(page.talk().title(), textWidth() - navShift());
        lines = new ArrayList<>();
        for (String line : page.talk().lines()) { lines.addAll(wrap(line)); }
        picked = pickedLine().isEmpty() ? new ArrayList<>() : wrap("> " + pickedLine());
        rows.clear();
        int total = PAD * 2 + headerHeight() + (lines.size() + picked.size()) * CardOverlay.LINE;
        if (live()) {
            if (!page.talk().replies().isEmpty()) { total += GAP; }
            for (int i = 0; i < page.talk().replies().size(); i++) {
                List<FormattedCharSequence> row = wrap((i + 1) + ". " + page.talk().replies().get(i));
                rows.add(row);
                total += rowHeight(row);
            }
        }
        return total;
    }

    private static int rowHeight(List<FormattedCharSequence> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int firstRowTop() { return contentTop() + headerHeight() + (lines.size() + picked.size()) * CardOverlay.LINE + GAP; }

    private int buttonY(int button) {
        return switch (button) {
            case PREVIOUS, NEXT, UP -> top + PAD;
            default -> top + panelHeight - PAD - BUTTON;
        };
    }

    private boolean shown(int button) { return button >= UP ? column : talk.history(); }

    private boolean enabled(int button) {
        return switch (button) {
            case PREVIOUS -> viewing > 0;
            case NEXT -> viewing < pages.size() - 1;
            case UP -> scroll > 0;
            default -> scroll < maxScroll;
        };
    }

    private int buttonAt(double mouseX, double mouseY) {
        for (int i = PREVIOUS; i <= DOWN; i++) {
            if (shown(i) && mouseX >= buttonX(i) && mouseX < buttonX(i) + BUTTON && mouseY >= buttonY(i) && mouseY < buttonY(i) + BUTTON) { return i; }
        }
        return -1;
    }

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < left || mouseX >= contentRight() || mouseY < top + PAD || mouseY >= top + panelHeight - PAD || buttonAt(mouseX, mouseY) >= 0 || onTrack(mouseX, mouseY)) { return -1; }
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
        Entity speaker = mc.level == null ? null : mc.level.getEntity(talk.npc());
        if (speaker == null || !speaker.isAlive() || mc.player == null || speaker.distanceTo(mc.player) > REACH) {
            if (waiting) { closeAfter = true; }
            else { onClose(); }
        }
    }

    @Override public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    @Override public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Crisp.raise(graphics);
        if (talk.panel()) { CardOverlay.panel(graphics, left, top, panelWidth, panelHeight, talk.background(), 1.0F); }
        if (!talk.image().isEmpty()) {
            Identifier image = ContentParser.location(talk.image());
            if (image != null) { graphics.blit(RenderPipelines.GUI_TEXTURED, image, left, top, 0.0F, 0.0F, panelWidth, panelHeight, panelWidth, panelHeight, ARGB.white(1.0F)); }
        }
        if (talk.panel()) { graphics.fill(left, top, left + STRIPE, top + panelHeight, CardOverlay.withAlpha(TEXT, 1.0F)); }
        int x = left + STRIPE + PAD;
        if (!talk.icon().isEmpty()) {
            graphics.item(talk.icon(), x, top + PAD);
            x += ICON + PAD;
        }
        double fitX = Crisp.fit((double) mouseX);
        double fitY = Crisp.fit((double) mouseY);
        graphics.enableScissor(left, top + PAD, contentRight(), top + panelHeight - PAD);
        int y = contentTop();
        for (FormattedCharSequence line : title) {
            graphics.text(font, line, x + navShift(), y, CardOverlay.withAlpha(shade(talk.titleColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        y = contentTop() + headerHeight();
        for (FormattedCharSequence line : lines) {
            graphics.text(font, line, x, y, CardOverlay.withAlpha(shade(talk.textColor(), CardOverlay.LINE_COLOR), 1.0F), true);
            y += CardOverlay.LINE;
        }
        for (FormattedCharSequence line : picked) {
            graphics.text(font, line, x, y, CardOverlay.withAlpha(shade(talk.replyColor(), TEXT), 1.0F), true);
            y += CardOverlay.LINE;
        }
        drawReplies(graphics, x, fitX, fitY);
        graphics.disableScissor();
        drawButtons(graphics, fitX, fitY);
        Crisp.lower(graphics);
    }

    private int band() { return talk.hoverBackground() < 0 ? HOVER : (HOVER_ALPHA << 24) | talk.hoverBackground(); }

    private void drawReplies(GuiGraphicsExtractor graphics, int x, double mouseX, double mouseY) {
        int hovered = rowAt(mouseX, mouseY);
        int y = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            List<FormattedCharSequence> row = rows.get(i);
            int rowHeight = rowHeight(row);
            if (i == hovered) { graphics.fill(left + STRIPE, y, contentRight(), y + rowHeight, band()); }
            int lineY = y + ROW_PAD;
            int color = i == hovered ? shade(talk.hoverColor(), HOVER_TEXT) : shade(talk.replyColor(), TEXT);
            for (FormattedCharSequence line : row) {
                graphics.text(font, line, x, lineY, CardOverlay.withAlpha(color, 1.0F), true);
                lineY += CardOverlay.LINE;
            }
            y += rowHeight;
        }
    }

    private void drawButtons(GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        int hovered = buttonAt(mouseX, mouseY);
        if (column) {
            graphics.fill(columnLeft(), trackTop(), columnLeft() + BUTTON, trackTop() + trackHeight(), TRACK_BACK);
            graphics.fill(columnLeft() + 2, thumbTop(), columnLeft() + BUTTON - 2, thumbTop() + thumbHeight(), dragging ? band() : THUMB_BACK);
        }
        for (int i = PREVIOUS; i <= DOWN; i++) {
            if (!shown(i)) { continue; }
            int bx = buttonX(i);
            int by = buttonY(i);
            boolean on = enabled(i);
            if (talk.panel()) { graphics.fill(bx, by, bx + BUTTON, by + BUTTON, CardOverlay.withAlpha(talk.background(), 1.0F)); }
            graphics.fill(bx, by, bx + BUTTON, by + BUTTON, i == hovered && on ? band() : BUTTON_BACK);
            int color = CardOverlay.withAlpha(shade(talk.replyColor(), TEXT), on ? 1.0F : DIM);
            if (i <= NEXT) {
                Component label = MarkText.text(CardFont.of(talk.font()), i == PREVIOUS ? "<" : ">");
                graphics.text(font, label, bx + (BUTTON - font.width(label)) / 2, by + 2, color, true);
            }
            else { arrow(graphics, bx, by, i == UP, color); }
        }
    }

    private static void arrow(GuiGraphicsExtractor graphics, int bx, int by, boolean up, int color) {
        for (int i = 0; i < 4; i++) {
            int row = up ? by + 3 + i : by + 8 - i;
            graphics.fill(bx + 5 - i, row, bx + 7 + i, row + 1, color);
        }
    }

    @Override public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) { return false; }
        double fitX = Crisp.fit(event.x());
        double fitY = Crisp.fit(event.y());
        if (guarded()) { return true; }
        int button = buttonAt(fitX, fitY);
        if (button >= 0) {
            if (!enabled(button)) { return true; }
            switch (button) {
                case PREVIOUS -> page(-1);
                case NEXT -> page(1);
                case UP -> scrollBy(-CardOverlay.LINE * 2);
                default -> scrollBy(CardOverlay.LINE * 2);
            }
            return true;
        }
        if (onTrack(fitX, fitY)) {
            if (fitY < thumbTop()) { scrollBy(-view()); }
            else if (fitY >= thumbTop() + thumbHeight()) { scrollBy(view()); }
            else {
                dragging = true;
                dragOffset = fitY - thumbTop();
            }
            return true;
        }
        if (talk.replies().isEmpty()) {
            onClose();
            return true;
        }
        int row = rowAt(fitX, fitY);
        if (row < 0) { return false; }
        pick(row + 1);
        return true;
    }

    @Override public boolean mouseDragged(@Nonnull MouseButtonEvent event, double dx, double dy) {
        if (!dragging) { return super.mouseDragged(event, dx, dy); }
        dragTo(Crisp.fit(event.y()));
        return true;
    }

    @Override public boolean mouseReleased(@Nonnull MouseButtonEvent event) {
        dragging = false;
        return super.mouseReleased(event);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double fitX = Crisp.fit(mouseX);
        double fitY = Crisp.fit(mouseY);
        if (fitX < left || fitX >= left + panelWidth || fitY < top || fitY >= top + panelHeight) { return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY); }
        scrollBy((int) -Math.signum(scrollY) * CardOverlay.LINE);
        return true;
    }

    @Override public boolean keyPressed(@Nonnull KeyEvent event) {
        if (event.isEscape() || minecraft.options.keyInventory.matches(event)) {
            if (waiting) { closeAfter = true; }
            else { onClose(); }
            return true;
        }
        if (event.key() == InputConstants.KEY_LEFT) {
            page(-1);
            return true;
        }
        if (event.key() == InputConstants.KEY_RIGHT) {
            page(1);
            return true;
        }
        int number = event.key() - InputConstants.KEY_1 + 1;
        if (number < 1 || number > MOST_KEYS || number > rows.size()) { return super.keyPressed(event); }
        pick(number);
        return true;
    }

    @Override public void removed() { if (!ended && !talk.replies().isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }
}
