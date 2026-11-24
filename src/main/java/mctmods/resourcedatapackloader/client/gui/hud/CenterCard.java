package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.renderer.Blends;
import mctmods.resourcedatapackloader.client.util.Crisp;
import mctmods.resourcedatapackloader.client.util.Draw;
import mctmods.resourcedatapackloader.core.network.CardMessage;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CenterCard {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final int PAD = 8;
    private static final int TITLE_SCALE = 2;
    private static final int ABOVE_MIDDLE = 24;
    @Nullable private static CardOverlay.Card shown;
    private static final Deque<CardMessage> WAITING = new ArrayDeque<>();

    private CenterCard() {}

    public static void show(CardMessage message) {
        if (shown == null) { start(message); }
        else { WAITING.add(message); }
    }

    private static void start(CardMessage message) { shown = new CardOverlay.Card(message, FADE_IN + FADE_OUT); }

    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || shown == null) { return; }
        if (++shown.age < shown.life) { return; }
        shown = null;
        CardMessage next = WAITING.poll();
        if (next != null) { start(next); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        shown = null;
        WAITING.clear();
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) { if (event.getWorld().isRemote) { WAITING.clear(); } }

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        CardOverlay.Card card = shown;
        if (card == null || Crisp.hudBlocked(event)) { return; }
        ScaledResolution resolution = event.getResolution();
        int screenWidth = Crisp.fit(resolution.getScaledWidth());
        int screenHeight = Crisp.fit(resolution.getScaledHeight());
        int widest = MarkText.width(card.title) * TITLE_SCALE;
        for (List<MarkText.Piece> line : card.lines) { widest = Math.max(widest, MarkText.width(line)); }
        int titleHeight = card.title.isEmpty() ? 0 : CardOverlay.LINE * TITLE_SCALE + PAD / 2;
        int width = widest + PAD * 2;
        int height = titleHeight + card.lines.size() * CardOverlay.LINE + PAD * 2;
        int left = (screenWidth - width) / 2;
        int top = Math.max(PAD, screenHeight / 2 - ABOVE_MIDDLE - height);
        float alpha = alpha(card, event.getPartialTicks());
        Crisp.raise();
        Blends.on();
        if (card.panel) { CardOverlay.panel(left, top, width, height, card.background, alpha); }
        if (card.image != null) { Draw.blitTinted(card.image, left, top, width, height, alpha); }
        int y = top + PAD;
        if (!card.title.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(TITLE_SCALE, TITLE_SCALE, 1.0F);
            int titleLeft = (screenWidth - MarkText.width(card.title) * TITLE_SCALE) / 2;
            MarkText.draw(card.title, titleLeft / (float) TITLE_SCALE, y / (float) TITLE_SCALE, CardOverlay.withAlpha(card.text, alpha));
            GlStateManager.popMatrix();
            y += titleHeight;
        }
        for (List<MarkText.Piece> line : card.lines) {
            MarkText.draw(line, (screenWidth - MarkText.width(line)) / 2.0F, y, CardOverlay.withAlpha(CardOverlay.LINE_COLOR, alpha));
            y += CardOverlay.LINE;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        Crisp.lower();
    }

    private static float alpha(CardOverlay.Card card, float partialTicks) {
        float at = card.age + partialTicks;
        if (at < FADE_IN) { return at / FADE_IN; }
        return MathUtil.ramp(card.life - at, FADE_OUT);
    }
}
