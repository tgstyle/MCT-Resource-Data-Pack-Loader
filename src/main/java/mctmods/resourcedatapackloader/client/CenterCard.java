package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.network.MessageCard;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;

public final class CenterCard {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final int PAD = 8;
    private static final int LINE = 10;
    private static final int TITLE_SCALE = 2;
    private static final int ABOVE_MIDDLE = 24;
    private static final int TEXT = 0xE8E8E8;
    private static final Deque<MessageCard> WAITING = new ArrayDeque<>();
    @Nullable private static MessageCard shown;
    @Nullable private static Identifier image;
    private static CardFont.Face face = new CardFont.Face(FontDescription.DEFAULT.id(), null);
    private static int life;
    private static int age;
    private static int widest;

    private CenterCard() {}

    public static void show(MessageCard message) {
        if (shown == null) { start(message); }
        else { WAITING.add(message); }
    }

    private static void start(MessageCard message) {
        shown = message;
        image = message.image().isEmpty() ? null : ContentParser.location(message.image());
        face = CardFont.of(message.font());
        life = Math.max(FADE_IN + FADE_OUT, message.ticks());
        age = 0;
        widest = -1;
    }

    public static void reset() {
        shown = null;
        WAITING.clear();
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) { WAITING.clear(); }
    }

    @SuppressWarnings("unused") public static void onClientTick(ClientTickEvent.Post event) {
        if (shown == null) { return; }
        if (++age < life) { return; }
        shown = null;
        MessageCard next = WAITING.poll();
        if (next != null) { start(next); }
    }

    public static void onLayer(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        MessageCard card = shown;
        Minecraft mc = Minecraft.getInstance();
        if (card == null || ClientCompat.screen(mc) != null) { return; }
        Font font = mc.font;
        int screenWidth = Crisp.fit(mc.getWindow().getGuiScaledWidth());
        if (widest < 0) {
            widest = font.width(MarkText.text(face, card.title())) * TITLE_SCALE;
            for (String line : card.lines()) { widest = Math.max(widest, font.width(MarkText.text(face, line))); }
        }
        int titleHeight = card.title().isEmpty() ? 0 : LINE * TITLE_SCALE + PAD / 2;
        int width = widest + PAD * 2;
        int height = titleHeight + card.lines().size() * LINE + PAD * 2;
        int left = (screenWidth - width) / 2;
        int top = Math.max(PAD, Crisp.fit(mc.getWindow().getGuiScaledHeight()) / 2 - ABOVE_MIDDLE - height);
        float alpha = alpha(delta.getGameTimeDeltaPartialTick(false));
        Crisp.raise(graphics);
        if (card.panel()) {
            graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, CardOverlay.withAlpha(CardOverlay.darker(card.background()), alpha));
            graphics.fill(left, top, left + width, top + height, CardOverlay.withAlpha(card.background(), 0.85F * alpha));
        }
        if (image != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, image, left, top, 0.0F, 0.0F, width, height, width, height, ARGB.white(alpha));
        }
        int y = top + PAD;
        if (!card.title().isEmpty()) {
            graphics.pose().pushMatrix();
            graphics.pose().scale(TITLE_SCALE, TITLE_SCALE);
            int titleLeft = (screenWidth - font.width(MarkText.text(face, card.title())) * TITLE_SCALE) / 2;
            graphics.text(font, MarkText.text(face, card.title()), titleLeft / TITLE_SCALE, y / TITLE_SCALE, CardOverlay.withAlpha(card.text(), alpha), true);
            graphics.pose().popMatrix();
            y += titleHeight;
        }
        for (String line : card.lines()) {
            graphics.text(font, MarkText.text(face, line), (screenWidth - font.width(MarkText.text(face, line))) / 2, y, CardOverlay.withAlpha(TEXT, alpha), true);
            y += LINE;
        }
        Crisp.lower(graphics);
    }

    private static float alpha(float partialTicks) {
        float at = age + partialTicks;
        if (at < FADE_IN) { return at / FADE_IN; }
        float left = life - at;
        return left < FADE_OUT ? left / FADE_OUT : 1.0F;
    }
}
