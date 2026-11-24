package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.CardFont;
import mctmods.resourcedatapackloader.client.util.ClientCompat;
import mctmods.resourcedatapackloader.client.util.Crisp;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.core.network.CardMessage;
import mctmods.resourcedatapackloader.core.util.math.Colors;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class CardOverlay {
    private static final int MOST = 5;
    private static final int SLIDE = 8;
    private static final int FADE = 20;
    private static final float MIN_ALPHA = 0.05F;
    private static final int MARGIN = 6;
    private static final int GAP = 4;
    private static final int PAD = 5;
    private static final int STRIPE = 3;
    private static final int ICON = 16;
    public static final int LINE = 10;
    private static final int LEAST_WIDTH = 96;
    public static final int LINE_COLOR = 0xE8E8E8;
    private static final List<Card> CARDS = new ArrayList<>();

    private CardOverlay() {}

    static final class Card {
        final String title;
        final List<String> lines;
        final ItemStack icon;
        @Nullable final Identifier image;
        final int background;
        final boolean panel;
        final CardFont.Face face;
        final int text;
        final int life;
        int age;
        int measured;

        Card(CardMessage message, int leastLife) {
            this.title = message.title();
            this.lines = message.lines();
            this.icon = message.icon();
            this.image = message.image().isEmpty() ? null : ContentParser.location(message.image());
            this.background = message.background();
            this.panel = message.panel();
            this.face = CardFont.of(message.font());
            this.text = message.text();
            this.life = Math.max(leastLife, message.ticks());
        }

        int height() { return PAD * 2 + (title.isEmpty() ? 0 : LINE) + lines.size() * LINE; }

        int width(Font font) {
            if (measured == 0) {
                int widest = title.isEmpty() ? 0 : font.width(MarkText.text(face, title));
                for (String line : lines) { widest = Math.max(widest, font.width(MarkText.text(face, line))); }
                measured = Math.max(LEAST_WIDTH, STRIPE + PAD + (icon.isEmpty() ? 0 : ICON + PAD) + widest + PAD);
            }
            return measured;
        }

        float alpha() { return Math.max(MIN_ALPHA, MathUtil.ramp(life - age, FADE)); }

        float slide() { return MathUtil.drain(age, SLIDE); }
    }

    public static void show(CardMessage message) {
        if (message.center()) {
            CenterCard.show(message);
            return;
        }
        while (CARDS.size() >= MOST) { CARDS.removeFirst(); }
        CARDS.add(new Card(message, SLIDE + FADE));
    }

    @SuppressWarnings("unused") public static void onClientTick(ClientTickEvent.Post event) {
        if (CARDS.isEmpty()) { return; }
        for (Iterator<Card> each = CARDS.iterator(); each.hasNext();) {
            Card card = each.next();
            card.age++;
            if (card.age >= card.life) { each.remove(); }
        }
    }

    public static void onLayer(GuiGraphicsExtractor graphics, DeltaTracker ignoredDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (CARDS.isEmpty() || ClientCompat.screen(mc) != null) { return; }
        draw(graphics);
    }

    public static void onScreen(ScreenEvent.Render.Post event) {
        if (CARDS.isEmpty()) { return; }
        draw(event.getGuiGraphics());
    }

    private static void draw(GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int screenWidth = Crisp.width();
        int bottom = Crisp.height() - MARGIN;
        Crisp.raise(graphics);
        for (int i = CARDS.size() - 1; i >= 0; i--) {
            Card card = CARDS.get(i);
            int width = card.width(font);
            int height = card.height();
            int top = bottom - height;
            if (top < MARGIN) { break; }
            int left = screenWidth - MARGIN - width + Math.round(card.slide() * (width + MARGIN));
            float alpha = card.alpha();
            if (card.panel) { panel(graphics, left, top, width, height, card.background, alpha); }
            if (card.image != null) { graphics.blit(RenderPipelines.GUI_TEXTURED, card.image, left, top, 0.0F, 0.0F, width, height, width, height, ARGB.white(alpha)); }
            if (card.panel) { graphics.fill(left, top, left + STRIPE, top + height, withAlpha(card.text, alpha)); }
            int x = left + STRIPE + PAD;
            int y = top + PAD;
            if (!card.icon.isEmpty()) {
                graphics.item(card.icon, x, top + (height - ICON) / 2);
                x += ICON + PAD;
            }
            if (!card.title.isEmpty()) {
                graphics.text(font, MarkText.text(card.face, card.title), x, y, withAlpha(card.text, alpha), true);
                y += LINE;
            }
            for (String line : card.lines) {
                graphics.text(font, MarkText.text(card.face, line), x, y, withAlpha(LINE_COLOR, alpha), true);
                y += LINE;
            }
            bottom = top - GAP;
        }
        Crisp.lower(graphics);
    }

    public static void panel(GuiGraphicsExtractor graphics, int left, int top, int width, int height, int background, float alpha) {
        graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, withAlpha(darker(background), alpha));
        graphics.fill(left, top, left + width, top + height, withAlpha(background, 0.85F * alpha));
    }

    public static int withAlpha(int rgb, float alpha) { return Colors.withAlpha(rgb, Mth.clamp(alpha, MIN_ALPHA, 1.0F)); }

    static int darker(int rgb) { return ARGB.scaleRGB(rgb, 0.5F); }
}
