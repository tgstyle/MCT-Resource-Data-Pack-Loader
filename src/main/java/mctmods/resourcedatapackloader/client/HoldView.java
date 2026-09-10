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
    private static final float FOG_REACH = 2.0F;
    private static final float VANILLA_START = 0.25F;
    private static final int SHOW = 60;
    private static final int FADE = 60;
    private static final ResourceLocation LOGO = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "textures/gui/hold.png");
    private static final int LOGO_WIDTH = 329;
    private static final int LOGO_HEIGHT = 150;
    private static final int MARGIN = 12;
    private static final int SUBTITLE_TOP = 10;
    private static final String KEY = "pregenLogo";
    private static String warnedAbout = "";
    private static final int TEXT_ABOVE_MIDDLE = 36;
    private static final float TEXT_SCALE = 1.5F;
    private static final int BACKDROP_PAD = 4;
    private static final int PULSE_CYCLE = 1500;
    private static final int PULSE_HELD = 750;
    private static final int PULSE_FADE = 500;
    private static final float LEAST_SHOWN = 0.11F;
    private static final int WARNING_COLOR = 0xFF5555;
    private static final int BACKDROP_ALPHA = 0x99;
    private static String warning = "";
    private static final int NOTE_COLOR = 0x55FF55;
    private static final float NOTE_SCALE = 2.0F;
    private static String note = "";
    private static boolean held;
    private static boolean fogging;
    private static int showing;

    private HoldView() {}

    public static void set(boolean holding, String said, boolean fog) {
        warning = said == null ? "" : said;
        if (holding == held) { return; }
        if (holding) {
            note = "";
            fogging = fog;
        }
        held = holding;
        showing = holding ? 0 : SHOW + FADE;
    }

    public static boolean showing() { return held || showing > 0; }

    private static float strength(float partialTicks) {
        if (held || showing > FADE) { return 1.0F; }
        if (showing <= 0) { return 0.0F; }
        return Math.max(0.0F, (showing - partialTicks) / FADE);
    }

    @SubscribeEvent public static void onFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!held && !fogging) { return; }
        float strength = strength((float) event.getRenderPartialTicks());
        if (strength <= 0.0F) { return; }
        float eased = strength * strength * (3.0F - 2.0F * strength);
        float far = event.getFarPlaneDistance();
        float end = Math.min(far, FOG_REACH * eased + far * (1.0F - eased));
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(far * VANILLA_START * (1.0F - eased));
        GlStateManager.setFogEnd(end);
    }

    @SubscribeEvent public static void onHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) { return; }
        if (held) {
            warn(mc, event.getResolution());
            return;
        }
        float strength = strength(event.getPartialTicks());
        if (strength <= 0.0F) { return; }
        greet(mc, event.getResolution(), strength);
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

    private static float pulse() {
        long at = System.currentTimeMillis() % PULSE_CYCLE;
        if (at < PULSE_HELD) { return 1.0F; }
        if (at < PULSE_HELD + PULSE_FADE) { return 1.0F - (at - PULSE_HELD) / (float) PULSE_FADE; }
        return 0.0F;
    }

    public static void note(String said) {
        note = said == null ? "" : said;
        if (note.isEmpty()) { return; }
        if (!held) { showing = SHOW + FADE; }
    }

    private static void greet(Minecraft mc, ScaledResolution resolution, float strength) {
        if (note.isEmpty() || strength < LEAST_SHOWN) { return; }
        float scale = Crisp.scale(NOTE_SCALE);
        int width = mc.fontRenderer.getStringWidth(note);
        float x = Crisp.snap((resolution.getScaledWidth() - width * scale) / 2.0F);
        float y = Crisp.snap(resolution.getScaledHeight() / 2.0F + SUBTITLE_TOP);
        backdrop(Math.round(x), Math.round(y), Math.round(width * scale), Math.round(mc.fontRenderer.FONT_HEIGHT * scale), Math.round(strength * BACKDROP_ALPHA));
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        mc.fontRenderer.drawStringWithShadow(note, x / scale, y / scale, Math.round(strength * 0xFF) << 24 | NOTE_COLOR);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void backdrop(int left, int top, int width, int height, int alpha) {
        if (alpha <= 0) { return; }
        Gui.drawRect(left - BACKDROP_PAD, top - BACKDROP_PAD, left + width + BACKDROP_PAD, top + height + BACKDROP_PAD, alpha << 24);
    }

    private static void warn(Minecraft mc, ScaledResolution resolution) {
        if (warning.isEmpty()) { return; }
        float pulse = pulse();
        if (pulse < LEAST_SHOWN) { return; }
        float scale = Crisp.scale(TEXT_SCALE);
        int width = mc.fontRenderer.getStringWidth(warning);
        float x = Crisp.snap((resolution.getScaledWidth() - width * scale) / 2.0F);
        float y = Crisp.snap(resolution.getScaledHeight() / 2.0F - TEXT_ABOVE_MIDDLE);
        backdrop(Math.round(x), Math.round(y), Math.round(width * scale), Math.round(mc.fontRenderer.FONT_HEIGHT * scale), Math.round(pulse * BACKDROP_ALPHA));
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        mc.fontRenderer.drawStringWithShadow(warning, x / scale, y / scale, Math.round(pulse * 0xFF) << 24 | WARNING_COLOR);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
        if (event.phase != TickEvent.Phase.END) { return; }
        if (held || showing <= 0) { return; }
        showing--;
        if (showing <= 0) {
            note = "";
            fogging = false;
        }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        held = false;
        fogging = false;
        showing = 0;
        warning = "";
        note = "";
    }

    public static class NoteHandler implements IMessageHandler<mctmods.resourcedatapackloader.network.MessageNote, IMessage> {
        @Override public IMessage onMessage(mctmods.resourcedatapackloader.network.MessageNote message, MessageContext ctx) {
            String said = message.said;
            Minecraft.getMinecraft().addScheduledTask(() -> note(said));
            return null;
        }
    }

    public static class Handler implements IMessageHandler<MessageHold, IMessage> {
        @Override public IMessage onMessage(MessageHold message, MessageContext ctx) {
            boolean holding = message.held;
            String said = message.warning;
            Minecraft.getMinecraft().addScheduledTask(() -> set(holding, said, message.fog));
            return null;
        }
    }
}
