package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.MessageCard;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public final class CardOverlay {
    private static final int MOST = 5;
    private static final int SLIDE = 8;
    private static final int FADE = 20;
    private static final float ABOVE_ITEMS = 300.0F;
    private static final int MARGIN = 6;
    private static final int GAP = 4;
    private static final int PAD = 5;
    private static final int STRIPE = 3;
    private static final int ICON = 16;
    private static final int LINE = 10;
    private static final int LEAST_WIDTH = 96;
    private static final int TEXT = 0xE8E8E8;
    private static final List<Card> CARDS = new ArrayList<>();

    private CardOverlay() {}

    private static final class Card {
        final String title;
        final List<String> lines;
        final ItemStack icon;
        @Nullable final ResourceLocation image;
        final int background;
        final int text;
        final int life;
        int age;

        Card(MessageCard message) {
            this.title = message.title();
            this.lines = message.lines();
            this.icon = message.icon();
            this.image = message.image().isEmpty() ? null : ResourceLocation.tryParse(message.image());
            this.background = message.background();
            this.text = message.text();
            this.life = Math.max(SLIDE + FADE, message.ticks());
        }

        int height() { return PAD * 2 + (title.isEmpty() ? 0 : LINE) + lines.size() * LINE; }

        int width(Font font) {
            int widest = title.isEmpty() ? 0 : font.width(title);
            for (String line : lines) { widest = Math.max(widest, font.width(line)); }
            return Math.max(LEAST_WIDTH, STRIPE + PAD + (icon.isEmpty() ? 0 : ICON + PAD) + widest + PAD);
        }

        float alpha() {
            int left = life - age;
            return left < FADE ? Math.max(0.05F, left / (float) FADE) : 1.0F;
        }

        float slide() { return age < SLIDE ? 1.0F - age / (float) SLIDE : 0.0F; }
    }

    public static void show(MessageCard message) {
        while (CARDS.size() >= MOST) { CARDS.removeFirst(); }
        CARDS.add(new Card(message));
    }

    @SuppressWarnings("unused") public static void onClientTick(ClientTickEvent.Post event) {
        if (CARDS.isEmpty()) { return; }
        for (Iterator<Card> each = CARDS.iterator(); each.hasNext();) {
            Card card = each.next();
            card.age++;
            if (card.age >= card.life) { each.remove(); }
        }
    }

    public static void onLayer(GuiGraphics graphics, DeltaTracker ignoredDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (CARDS.isEmpty() || mc.screen != null) { return; }
        draw(graphics, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    public static void onScreen(ScreenEvent.Render.Post event) {
        if (CARDS.isEmpty()) { return; }
        draw(event.getGuiGraphics(), event.getScreen().width, event.getScreen().height);
    }

    private static void draw(GuiGraphics graphics, int screenWidth, int screenHeight) {
        Font font = Minecraft.getInstance().font;
        int bottom = screenHeight - MARGIN;
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, ABOVE_ITEMS);
        RenderSystem.enableBlend();
        for (int i = CARDS.size() - 1; i >= 0; i--) {
            Card card = CARDS.get(i);
            int width = card.width(font);
            int height = card.height();
            int top = bottom - height;
            if (top < MARGIN) { break; }
            int left = screenWidth - MARGIN - width + Math.round(card.slide() * (width + MARGIN));
            float alpha = card.alpha();
            graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, withAlpha(darker(card.background), alpha));
            graphics.fill(left, top, left + width, top + height, withAlpha(card.background, 0.85F * alpha));
            if (card.image != null) {
                graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
                graphics.blit(card.image, left, top, 0.0F, 0.0F, width, height, width, height);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            graphics.fill(left, top, left + STRIPE, top + height, withAlpha(card.text, alpha));
            int x = left + STRIPE + PAD;
            int y = top + PAD;
            if (!card.icon.isEmpty()) {
                graphics.renderItem(card.icon, x, top + (height - ICON) / 2);
                x += ICON + PAD;
            }
            if (!card.title.isEmpty()) {
                graphics.drawString(font, card.title, x, y, withAlpha(card.text, alpha), true);
                y += LINE;
            }
            for (String line : card.lines) {
                graphics.drawString(font, line, x, y, withAlpha(TEXT, alpha), true);
                y += LINE;
            }
            bottom = top - GAP;
        }
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }

    private static int withAlpha(int rgb, float alpha) { return (Math.round(Mth.clamp(alpha, 0.05F, 1.0F) * 255.0F) << 24) | (rgb & 0xFFFFFF); }

    private static int darker(int rgb) { return ((rgb >> 16 & 0xFF) / 2 << 16) | ((rgb >> 8 & 0xFF) / 2 << 8) | ((rgb & 0xFF) / 2); }
}
