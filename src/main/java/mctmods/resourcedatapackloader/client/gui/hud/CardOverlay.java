package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.renderer.Blends;
import mctmods.resourcedatapackloader.client.util.Crisp;
import mctmods.resourcedatapackloader.client.util.Draw;
import mctmods.resourcedatapackloader.core.network.CardMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CardOverlay {
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
        final List<MarkText.Piece> title;
        final List<List<MarkText.Piece>> lines = new ArrayList<>();
        final ItemStack icon;
        @Nullable final ResourceLocation image;
        final int background;
        final boolean panel;
        final int text;
        final int life;
        int age;
        int measured;

        Card(CardMessage message, int leastLife) {
            this.title = message.title.isEmpty() ? new ArrayList<>() : MarkText.pieces(message.font, message.title);
            for (String line : message.lines) { lines.add(MarkText.pieces(message.font, line)); }
            this.icon = message.icon;
            this.image = IdNames.optional(message.image);
            this.background = message.background;
            this.panel = message.panel;
            this.text = message.text;
            this.life = Math.max(leastLife, message.ticks);
        }

        int height() { return PAD * 2 + (title.isEmpty() ? 0 : LINE) + lines.size() * LINE; }

        int width() {
            if (measured == 0) {
                int widest = MarkText.width(title);
                for (List<MarkText.Piece> line : lines) { widest = Math.max(widest, MarkText.width(line)); }
                measured = Math.max(LEAST_WIDTH, STRIPE + PAD + (icon.isEmpty() ? 0 : ICON + PAD) + widest + PAD);
            }
            return measured;
        }

        float alpha() {
            return Math.max(MIN_ALPHA, MathUtil.ramp(life - age, FADE));
        }

        float slide() { return MathUtil.drain(age, SLIDE); }
    }

    public static void show(CardMessage message) {
        while (CARDS.size() >= MOST) { CARDS.remove(0); }
        CARDS.add(new Card(message, SLIDE + FADE));
    }


    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || CARDS.isEmpty()) { return; }
        for (Iterator<Card> each = CARDS.iterator(); each.hasNext();) {
            Card card = each.next();
            card.age++;
            if (card.age >= card.life) { each.remove(); }
        }
    }

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        if (CARDS.isEmpty() || Crisp.hudBlocked(event)) { return; }
        draw(event.getResolution());
    }

    @SubscribeEvent public static void onGui(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (CARDS.isEmpty()) { return; }
        draw(new ScaledResolution(Minecraft.getMinecraft()));
    }

    private static void draw(ScaledResolution resolution) {
        Minecraft mc = Minecraft.getMinecraft();
        int screenWidth = Crisp.fit(resolution.getScaledWidth());
        int bottom = Crisp.fit(resolution.getScaledHeight()) - MARGIN;
        Crisp.raise();
        Blends.on();
        for (int i = CARDS.size() - 1; i >= 0; i--) {
            Card card = CARDS.get(i);
            int width = card.width();
            int height = card.height();
            int top = bottom - height;
            if (top < MARGIN) { break; }
            int left = screenWidth - MARGIN - width + Math.round(card.slide() * (width + MARGIN));
            float alpha = card.alpha();
            if (card.panel) { panel(left, top, width, height, card.background, alpha); }
            else { GlStateManager.enableBlend(); }
            if (card.image != null) { Draw.blitTinted(card.image, left, top, width, height, alpha); }
            if (card.panel) { Gui.drawRect(left, top, left + STRIPE, top + height, withAlpha(card.text, alpha)); }
            int x = left + STRIPE + PAD;
            int y = top + PAD;
            if (!card.icon.isEmpty()) {
                int iconY = top + (height - ICON) / 2;
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(card.icon, x, iconY);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                x += ICON + PAD;
            }
            if (!card.title.isEmpty()) {
                MarkText.draw(card.title, x, y, withAlpha(card.text, alpha));
                y += LINE;
            }
            for (List<MarkText.Piece> line : card.lines) {
                MarkText.draw(line, x, y, withAlpha(LINE_COLOR, alpha));
                y += LINE;
            }
            bottom = top - GAP;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        Crisp.lower();
    }

    public static void panel(int left, int top, int width, int height, int background, float alpha) {
        Gui.drawRect(left - 1, top - 1, left + width + 1, top + height + 1, withAlpha(darker(background), alpha));
        Gui.drawRect(left, top, left + width, top + height, withAlpha(background, 0.85F * alpha));
    }

    public static int withAlpha(int rgb, float alpha) { return Colors.withAlpha(rgb, MathHelper.clamp(alpha, MIN_ALPHA, 1.0F)); }

    static int darker(int rgb) { return Colors.rgb(Colors.red(rgb) / 2, Colors.green(rgb) / 2, Colors.blue(rgb) / 2); }

    public static class Handler extends AbstractClientMessageHandler<CardMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, CardMessage message, MessageContext ctx) {
            if (message.center) { CenterCard.show(message); }
            else { show(message); }
        }
    }
}
