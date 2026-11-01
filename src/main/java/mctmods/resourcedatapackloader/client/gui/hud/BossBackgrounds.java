package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.core.network.BossBackgroundMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class BossBackgrounds {
    private static final int BAR_WIDTH = 182;
    private static final Map<UUID, BossBackgroundMessage> BACKGROUNDS = new HashMap<>();

    private BossBackgrounds() {}

    public static void set(BossBackgroundMessage message) {
        if (message.texture.isEmpty()) { BACKGROUNDS.remove(message.bar); }
        else { BACKGROUNDS.put(message.bar, message); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { BACKGROUNDS.clear(); }

    @SubscribeEvent public static void onBar(RenderGameOverlayEvent.BossInfo event) {
        BossBackgroundMessage background = BACKGROUNDS.get(event.getBossInfo().getUniqueId());
        if (background == null) { return; }
        int width = background.width > 0 ? background.width : BAR_WIDTH;
        int height = background.height > 0 ? background.height : event.getIncrement();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(background.texture));
        Gui.drawModalRectWithCustomSizedTexture(event.getX() + background.x, event.getY() - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + background.y, 0.0F, 0.0F, width, height, width, height);
    }

    public static class Handler extends AbstractClientMessageHandler<BossBackgroundMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, BossBackgroundMessage message, MessageContext ctx) { set(message); }
    }
}
