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
    private MessageTalk talk;
    private List<FormattedCharSequence> title = new ArrayList<>();
    private List<FormattedCharSequence> lines = new ArrayList<>();
    private final List<List<FormattedCharSequence>> rows = new ArrayList<>();
    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;
    private boolean ended;

    private TalkScreen(MessageTalk talk) {
        super(Component.literal("Talk"));
        this.talk = talk;
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
        talk = next;
        if (width > 0) { layout(); }
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

    private int textWidth() { return panelWidth - STRIPE - PAD * 2 - (talk.icon().isEmpty() ? 0 : ICON + PAD); }

    private List<FormattedCharSequence> wrap(String said) { return font.split(MarkText.text(CardFont.of(talk.font()), said), textWidth()); }

    private void layout() {
        panelWidth = Math.min(WIDTH, width - MARGIN * 2);
        title = talk.title().isEmpty() ? new ArrayList<>() : wrap(talk.title());
        lines = new ArrayList<>();
        for (String line : talk.lines()) { lines.addAll(wrap(line)); }
        rows.clear();
        int total = PAD * 2 + (title.isEmpty() ? 0 : CardOverlay.LINE + ROW_PAD) + lines.size() * CardOverlay.LINE;
        if (!talk.replies().isEmpty()) { total += GAP; }
        for (int i = 0; i < talk.replies().size(); i++) {
            List<FormattedCharSequence> row = wrap((i + 1) + ". " + talk.replies().get(i));
            rows.add(row);
            total += rowHeight(row);
        }
        panelHeight = total;
        left = (width - panelWidth) / 2;
        top = Math.max(MARGIN, height - LIFT - panelHeight);
    }

    private static int rowHeight(List<FormattedCharSequence> row) { return row.size() * CardOverlay.LINE + ROW_PAD * 2; }

    private int firstRowTop() { return top + PAD + (title.isEmpty() ? 0 : CardOverlay.LINE + ROW_PAD) + lines.size() * CardOverlay.LINE + GAP; }

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < left || mouseX >= left + panelWidth) { return -1; }
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
        if (speaker == null || !speaker.isAlive() || mc.player == null || speaker.distanceTo(mc.player) > REACH) { onClose(); }
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
        int y = top + PAD;
        for (FormattedCharSequence line : title) {
            graphics.text(font, line, x, y, CardOverlay.withAlpha(TEXT, 1.0F), true);
            y += CardOverlay.LINE;
        }
        if (!title.isEmpty()) { y += ROW_PAD; }
        for (FormattedCharSequence line : lines) {
            graphics.text(font, line, x, y, CardOverlay.withAlpha(CardOverlay.LINE_COLOR, 1.0F), true);
            y += CardOverlay.LINE;
        }
        drawReplies(graphics, x, Crisp.fit((double) mouseX), Crisp.fit((double) mouseY));
        Crisp.lower(graphics);
    }

    private void drawReplies(GuiGraphicsExtractor graphics, int x, double mouseX, double mouseY) {
        int hovered = rowAt(mouseX, mouseY);
        int y = firstRowTop();
        for (int i = 0; i < rows.size(); i++) {
            List<FormattedCharSequence> row = rows.get(i);
            int rowHeight = rowHeight(row);
            if (i == hovered) { graphics.fill(left + STRIPE, y, left + panelWidth, y + rowHeight, HOVER); }
            int lineY = y + ROW_PAD;
            for (FormattedCharSequence line : row) {
                graphics.text(font, line, x, lineY, CardOverlay.withAlpha(i == hovered ? HOVER_TEXT : TEXT, 1.0F), true);
                lineY += CardOverlay.LINE;
            }
            y += rowHeight;
        }
    }

    @Override public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) { return false; }
        if (rows.isEmpty()) {
            onClose();
            return true;
        }
        int row = rowAt(Crisp.fit(event.x()), Crisp.fit(event.y()));
        if (row < 0) { return false; }
        RDPLNetwork.sendTalkPick(row + 1);
        return true;
    }

    @Override public boolean keyPressed(@Nonnull KeyEvent event) {
        if (event.isEscape() || minecraft.options.keyInventory.matches(event)) {
            onClose();
            return true;
        }
        int number = event.key() - InputConstants.KEY_1 + 1;
        if (number < 1 || number > MOST_KEYS || number > rows.size()) { return super.keyPressed(event); }
        RDPLNetwork.sendTalkPick(number);
        return true;
    }

    @Override public void removed() { if (!ended && !rows.isEmpty()) { RDPLNetwork.sendTalkPick(-1); } }
}
