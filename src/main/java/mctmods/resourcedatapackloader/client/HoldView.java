package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.network.MessageHold;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class HoldView {
    private static final float DENSITY = 0.3F;
    private static final int SHOW = 60;
    private static final int FADE = 60;
    private static final ResourceLocation LOGO = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "textures/gui/hold.png");
    private static final int LOGO_WIDTH = 329;
    private static final int LOGO_HEIGHT = 150;
    private static final int MARGIN = 12;
    private static final int SUBTITLE_TOP = 10;
    private static final String KEY = "pregenLogo";
    private static String warnedAbout = "";
    private static boolean held;
    private static int showing;

    private HoldView() {}

    public static void set(boolean holding) {
        if (holding == held) { return; }
        held = holding;
        showing = holding ? 0 : SHOW + FADE;
    }

    public static boolean showing() { return held || showing > 0; }

    private static float strength(float partialTicks) {
        if (held || showing > FADE) { return 1.0F; }
        if (showing <= 0) { return 0.0F; }
        return Math.max(0.0F, (showing - partialTicks) / FADE);
    }

    @SubscribeEvent public static void onFog(EntityViewRenderEvent.FogDensity event) {
        float strength = strength((float) event.getRenderPartialTicks());
        if (strength <= 0.0F) { return; }
        GlStateManager.setFog(GlStateManager.FogMode.EXP2);
        event.setDensity(DENSITY * strength);
        event.setCanceled(true);
    }

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || held) { return; }
        float strength = strength(event.getPartialTicks());
        if (strength <= 0.0F) { return; }
        ScaledResolution resolution = event.getResolution();
        int width = Math.min(LOGO_WIDTH, resolution.getScaledWidth() / 4);
        int height = LOGO_HEIGHT * width / LOGO_WIDTH;
        int left = leftFor(resolution.getScaledWidth(), width);
        int top = Math.max(MARGIN, resolution.getScaledHeight() / 2 + SUBTITLE_TOP - MARGIN - height);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, strength);
        mc.getTextureManager().bindTexture(LOGO);
        Gui.drawModalRectWithCustomSizedTexture(left, top, 0.0F, 0.0F, width, height, width, height);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static int leftFor(int screenWidth, int width) {
        String asked = ContentControl.text(ContentControl.CHUNKS, KEY, Config.chunks.pregenLogo).trim().toLowerCase(java.util.Locale.ROOT);
        if ("left".equals(asked)) { return MARGIN; }
        if ("right".equals(asked)) { return screenWidth - MARGIN - width; }
        if (!"center".equals(asked) && !asked.isEmpty() && !asked.equals(warnedAbout)) {
            warnedAbout = asked;
            ContentLog.LOGGER.error("{} '{}' is not left, center or right, so the logo stands in the center", KEY, asked);
        }
        return (screenWidth - width) / 2;
    }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || held || showing <= 0) { return; }
        showing--;
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        held = false;
        showing = 0;
    }

    public static class Handler implements IMessageHandler<MessageHold, IMessage> {
        @Override public IMessage onMessage(MessageHold message, MessageContext ctx) {
            boolean holding = message.held;
            Minecraft.getMinecraft().addScheduledTask(() -> set(holding));
            return null;
        }
    }
}
