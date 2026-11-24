package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientCompat;
import mctmods.resourcedatapackloader.client.util.Crisp;
import mctmods.resourcedatapackloader.core.network.CardMessage;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class CenterCard {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final int PAD = 8;
    private static final int TITLE_SCALE = 2;
    private static final int ABOVE_MIDDLE = 24;
    private static final Deque<CardMessage> WAITING = new ArrayDeque<>();
    @Nullable private static CardOverlay.Card shown;
    private static int widest;

    private CenterCard() {}

    public static void show(CardMessage message) {
        if (shown == null) { start(message); }
        else { WAITING.add(message); }
    }

    private static void start(CardMessage message) {
        shown = new CardOverlay.Card(message, FADE_IN + FADE_OUT);
        widest = -1;
    }

    public static void reset() {
        shown = null;
        WAITING.clear();
    }

    public static void onLevelUnload(LevelEvent.Unload event) { if (event.getLevel().isClientSide()) { WAITING.clear(); } }

    @SuppressWarnings("unused") public static void onClientTick(ClientTickEvent.Post event) {
        if (shown == null) { return; }
        if (++shown.age < shown.life) { return; }
        shown = null;
        CardMessage next = WAITING.poll();
        if (next != null) { start(next); }
    }

    public static void onLayer(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        CardOverlay.Card card = shown;
        Minecraft mc = Minecraft.getInstance();
        if (card == null || ClientCompat.screen(mc) != null) { return; }
        Font font = mc.font;
        int screenWidth = Crisp.width();
        if (widest < 0) {
            widest = font.width(MarkText.text(card.face, card.title)) * TITLE_SCALE;
            for (String line : card.lines) { widest = Math.max(widest, font.width(MarkText.text(card.face, line))); }
        }
        int titleHeight = card.title.isEmpty() ? 0 : CardOverlay.LINE * TITLE_SCALE + PAD / 2;
        int width = widest + PAD * 2;
        int height = titleHeight + card.lines.size() * CardOverlay.LINE + PAD * 2;
        int left = (screenWidth - width) / 2;
        int top = Math.max(PAD, Crisp.height() / 2 - ABOVE_MIDDLE - height);
        float alpha = alpha(card, delta.getGameTimeDeltaPartialTick(false));
        Crisp.raise(graphics);
        if (card.panel) { CardOverlay.panel(graphics, left, top, width, height, card.background, alpha); }
        if (card.image != null) { graphics.blit(RenderPipelines.GUI_TEXTURED, card.image, left, top, 0.0F, 0.0F, width, height, width, height, ARGB.white(alpha)); }
        int y = top + PAD;
        if (!card.title.isEmpty()) {
            graphics.pose().pushMatrix();
            graphics.pose().scale(TITLE_SCALE, TITLE_SCALE);
            int titleLeft = (screenWidth - font.width(MarkText.text(card.face, card.title)) * TITLE_SCALE) / 2;
            graphics.text(font, MarkText.text(card.face, card.title), titleLeft / TITLE_SCALE, y / TITLE_SCALE, CardOverlay.withAlpha(card.text, alpha), true);
            graphics.pose().popMatrix();
            y += titleHeight;
        }
        for (String line : card.lines) {
            graphics.text(font, MarkText.text(card.face, line), (screenWidth - font.width(MarkText.text(card.face, line))) / 2, y, CardOverlay.withAlpha(CardOverlay.LINE_COLOR, alpha), true);
            y += CardOverlay.LINE;
        }
        Crisp.lower(graphics);
    }

    private static float alpha(CardOverlay.Card card, float partialTicks) {
        float at = card.age + partialTicks;
        if (at < FADE_IN) { return at / FADE_IN; }
        return MathUtil.ramp(card.life - at, FADE_OUT);
    }
}
