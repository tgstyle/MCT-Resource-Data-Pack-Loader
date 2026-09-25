package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.MessageCard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class CenterCard {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final int PAD = 8;
    private static final int LINE = 10;
    private static final int TITLE_SCALE = 2;
    private static final int ABOVE_MIDDLE = 24;
    @Nullable private static MessageCard shown;
    @Nullable private static ResourceLocation image;
    private static List<MarkText.Piece> title = new ArrayList<>();
    private static final List<List<MarkText.Piece>> LINES = new ArrayList<>();
    private static final Deque<MessageCard> WAITING = new ArrayDeque<>();
    private static int life;
    private static int age;

    private CenterCard() {}

    public static void show(MessageCard message) {
        if (shown == null) { start(message); }
        else { WAITING.add(message); }
    }

    private static void start(MessageCard message) {
        shown = message;
        image = message.image.isEmpty() ? null : new ResourceLocation(message.image);
        title = MarkText.pieces(message.font, message.title);
        LINES.clear();
        for (String line : message.lines) { LINES.add(MarkText.pieces(message.font, line)); }
        life = Math.max(FADE_IN + FADE_OUT, message.ticks);
        age = 0;
    }

    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || shown == null) { return; }
        if (++age < life) { return; }
        shown = null;
        MessageCard next = WAITING.poll();
        if (next != null) { start(next); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        shown = null;
        WAITING.clear();
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) { WAITING.clear(); }
    }

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        MessageCard card = shown;
        if (card == null || event.getType() != RenderGameOverlayEvent.ElementType.ALL) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) { return; }
        ScaledResolution resolution = event.getResolution();
        int screenWidth = Crisp.fit(resolution.getScaledWidth());
        int screenHeight = Crisp.fit(resolution.getScaledHeight());
        int widest = MarkText.width(title) * TITLE_SCALE;
        for (List<MarkText.Piece> line : LINES) { widest = Math.max(widest, MarkText.width(line)); }
        int titleHeight = card.title.isEmpty() ? 0 : LINE * TITLE_SCALE + PAD / 2;
        int width = widest + PAD * 2;
        int height = titleHeight + card.lines.size() * LINE + PAD * 2;
        int left = (screenWidth - width) / 2;
        int top = Math.max(PAD, screenHeight / 2 - ABOVE_MIDDLE - height);
        float alpha = alpha(event.getPartialTicks());
        Crisp.raise();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (card.panel) {
            Gui.drawRect(left - 1, top - 1, left + width + 1, top + height + 1, CardOverlay.withAlpha(CardOverlay.darker(card.background), alpha));
            Gui.drawRect(left, top, left + width, top + height, CardOverlay.withAlpha(card.background, 0.85F * alpha));
        }
        if (image != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            mc.getTextureManager().bindTexture(image);
            Gui.drawModalRectWithCustomSizedTexture(left, top, 0.0F, 0.0F, width, height, width, height);
        }
        int y = top + PAD;
        if (!card.title.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(TITLE_SCALE, TITLE_SCALE, 1.0F);
            int titleLeft = (screenWidth - MarkText.width(title) * TITLE_SCALE) / 2;
            MarkText.draw(title, titleLeft / (float) TITLE_SCALE, y / (float) TITLE_SCALE, CardOverlay.withAlpha(card.text, alpha));
            GlStateManager.popMatrix();
            y += titleHeight;
        }
        for (List<MarkText.Piece> line : LINES) {
            MarkText.draw(line, (screenWidth - MarkText.width(line)) / 2.0F, y, CardOverlay.withAlpha(0xE8E8E8, alpha));
            y += LINE;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        Crisp.lower();
    }

    private static float alpha(float partialTicks) {
        float at = age + partialTicks;
        if (at < FADE_IN) { return at / FADE_IN; }
        float left = life - at;
        return left < FADE_OUT ? left / FADE_OUT : 1.0F;
    }
}
