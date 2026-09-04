package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageCard;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class CardOverlay {
    private static final int MOST = 5;
    private static final int SLIDE = 8;
    private static final int FADE = 20;
    private static final int MARGIN = 6;
    private static final int GAP = 4;
    private static final int PAD = 5;
    private static final int STRIPE = 3;
    private static final int ICON = 16;
    private static final int LINE = 10;
    private static final int LEAST_WIDTH = 96;
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
            this.title = message.title;
            this.lines = message.lines;
            this.icon = message.icon;
            this.image = message.image.isEmpty() ? null : new ResourceLocation(message.image);
            this.background = message.background;
            this.text = message.text;
            this.life = Math.max(SLIDE + FADE, message.ticks);
        }

        int height() { return PAD * 2 + (title.isEmpty() ? 0 : LINE) + lines.size() * LINE; }

        int width(FontRenderer font) {
            int widest = title.isEmpty() ? 0 : font.getStringWidth(title);
            for (String line : lines) { widest = Math.max(widest, font.getStringWidth(line)); }
            return Math.max(LEAST_WIDTH, STRIPE + PAD + (icon.isEmpty() ? 0 : ICON + PAD) + widest + PAD);
        }

        float alpha() {
            int left = life - age;
            if (left < FADE) { return Math.max(0.05F, left / (float) FADE); }
            return 1.0F;
        }

        float slide() { return age < SLIDE ? 1.0F - age / (float) SLIDE : 0.0F; }
    }

    public static void show(MessageCard message) {
        while (CARDS.size() >= MOST) { CARDS.remove(0); }
        CARDS.add(new Card(message));
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
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || CARDS.isEmpty()) { return; }
        if (Minecraft.getMinecraft().currentScreen != null) { return; }
        ScaledResolution resolution = event.getResolution();
        draw(resolution.getScaledWidth(), resolution.getScaledHeight());
    }

    @SubscribeEvent public static void onGui(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (CARDS.isEmpty()) { return; }
        draw(event.getGui().width, event.getGui().height);
    }

    private static void draw(int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        int bottom = screenHeight - MARGIN;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        for (int i = CARDS.size() - 1; i >= 0; i--) {
            Card card = CARDS.get(i);
            int width = card.width(font);
            int height = card.height();
            int top = bottom - height;
            if (top < MARGIN) { break; }
            int left = screenWidth - MARGIN - width + Math.round(card.slide() * (width + MARGIN));
            float alpha = card.alpha();
            int panel = withAlpha(card.background, 0.85F * alpha);
            int edge = withAlpha(darker(card.background), alpha);
            int stripe = withAlpha(card.text, alpha);
            Gui.drawRect(left - 1, top - 1, left + width + 1, top + height + 1, edge);
            Gui.drawRect(left, top, left + width, top + height, panel);
            if (card.image != null) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                mc.getTextureManager().bindTexture(card.image);
                Gui.drawModalRectWithCustomSizedTexture(left, top, 0.0F, 0.0F, width, height, width, height);
            }
            Gui.drawRect(left, top, left + STRIPE, top + height, stripe);
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
                font.drawStringWithShadow(card.title, x, y, withAlpha(card.text, alpha));
                y += LINE;
            }
            for (String line : card.lines) {
                font.drawStringWithShadow(line, x, y, withAlpha(0xE8E8E8, alpha));
                y += LINE;
            }
            bottom = top - GAP;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static int withAlpha(int rgb, float alpha) { return (Math.round(MathHelper.clamp(alpha, 0.05F, 1.0F) * 255.0F) << 24) | (rgb & 0xFFFFFF); }

    private static int darker(int rgb) { return ((rgb >> 16 & 0xFF) / 2 << 16) | ((rgb >> 8 & 0xFF) / 2 << 8) | ((rgb & 0xFF) / 2); }

    public static class Handler extends AbstractClientMessageHandler<MessageCard> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageCard message, MessageContext ctx) { show(message); }
    }
}
