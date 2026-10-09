package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.client.render.Blends;
import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageHold;
import mctmods.resourcedatapackloader.network.MessageNote;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT) public final class HoldView {
    private static final float FOG_REACH = 2.0F;
    private static final float VANILLA_START = 0.25F;
    private static final int SHOW = 60;
    private static final int FADE = 60;
    private static final ResourceLocation LOGO = ResourceDataPackLoader.id("textures/gui/hold.png");
    private static final int LOGO_WIDTH = 329;
    private static final int LOGO_HEIGHT = 150;
    private static final int MARGIN = 12;
    private static final int SUBTITLE_TOP = 10;
    private static final String KEY = "pregenLogo";
    private static final int TEXT_ABOVE_MIDDLE = 36;
    private static final float TEXT_SCALE = 1.5F;
    private static final int BACKDROP_PAD = 4;
    private static final int PULSE_CYCLE = 1500;
    private static final int PULSE_HELD = 750;
    private static final int PULSE_FADE = 500;
    private static final float LEAST_SHOWN = 0.11F;
    private static final int WARNING_COLOR = 0xFF5555;
    private static final int BACKDROP_ALPHA = 0x99;
    private static final int NOTE_COLOR = 0x55FF55;
    private static final float NOTE_SCALE = 2.0F;
    private static String warnedAbout = "";
    private static String warning = "";
    private static String note = "";
    private static String font = "";
    private static boolean held;
    private static boolean fogging;
    private static boolean shaded = true;
    private static int showing;

    private HoldView() {}

    public static void set(boolean holding, String said, boolean fog, boolean shade, String face) {
        warning = said == null ? "" : said;
        shaded = shade;
        font = face;
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

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) { return; }
        ScaledResolution resolution = event.getResolution();
        int screenWidth = Crisp.fit(resolution.getScaledWidth());
        int screenHeight = Crisp.fit(resolution.getScaledHeight());
        Crisp.raise();
        if (held) { warn(mc, screenWidth, screenHeight); }
        else { welcome(mc, screenWidth, screenHeight, strength(event.getPartialTicks())); }
        Crisp.lower();
    }

    private static void welcome(Minecraft mc, int screenWidth, int screenHeight, float strength) {
        if (strength <= 0.0F) { return; }
        greet(mc, screenWidth, screenHeight, strength);
        double gui = Crisp.gui();
        int pixelsWide = (int) Math.round(screenWidth * gui);
        int times = Math.max(1, pixelsWide / 4 / LOGO_WIDTH);
        int width = LOGO_WIDTH * times;
        int height = LOGO_HEIGHT * times;
        int margin = (int) Math.round(MARGIN * gui);
        int left = leftFor(pixelsWide, width, margin);
        int top = Math.max(margin, (int) Math.round((screenHeight / 2.0D + SUBTITLE_TOP - MARGIN) * gui) - height);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blends.translucent();
        GlStateManager.color(1.0F, 1.0F, 1.0F, strength);
        GlStateManager.scale((float) (1.0D / gui), (float) (1.0D / gui), 1.0F);
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

    public static void note(String said, boolean shade, String face) {
        note = said == null ? "" : said;
        shaded = shade;
        font = face;
        if (note.isEmpty()) { return; }
        if (!held) { showing = SHOW + FADE; }
    }

    private static void greet(Minecraft mc, int screenWidth, int screenHeight, float strength) {
        if (note.isEmpty() || strength < LEAST_SHOWN) { return; }
        line(mc, screenWidth, note, NOTE_SCALE, screenHeight / 2.0F + SUBTITLE_TOP, strength, NOTE_COLOR);
    }

    private static void line(Minecraft mc, int screenWidth, String text, float wanted, float top, float strength, int color) {
        float scale = Crisp.scale(wanted);
        int width = MarkText.width(font, text);
        float x = Crisp.snap((screenWidth - width * scale) / 2.0F);
        float y = Crisp.snap(top);
        backdrop(Math.round(x), Math.round(y), Math.round(width * scale), Math.round(mc.fontRenderer.FONT_HEIGHT * scale), Math.round(strength * BACKDROP_ALPHA));
        GlStateManager.enableBlend();
        Blends.translucent();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        MarkText.draw(font, text, x / scale, y / scale, Math.round(strength * 0xFF) << 24 | color);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void backdrop(int left, int top, int width, int height, int alpha) {
        if (alpha <= 0 || !shaded) { return; }
        Gui.drawRect(left - BACKDROP_PAD, top - BACKDROP_PAD, left + width + BACKDROP_PAD, top + height + BACKDROP_PAD, alpha << 24);
    }

    private static void warn(Minecraft mc, int screenWidth, int screenHeight) {
        if (warning.isEmpty()) { return; }
        float pulse = pulse();
        if (pulse < LEAST_SHOWN) { return; }
        line(mc, screenWidth, warning, TEXT_SCALE, screenHeight / 2.0F - TEXT_ABOVE_MIDDLE, pulse, WARNING_COLOR);
    }

    private static int leftFor(int screenWidth, int width, int margin) {
        String asked = Settings.lower(ContentControl.text(ContentControl.CHUNKS, KEY, Config.chunks.pregenLogo));
        if ("left".equals(asked)) { return margin; }
        if ("right".equals(asked)) { return screenWidth - margin - width; }
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

    public static class NoteHandler extends AbstractClientMessageHandler<MessageNote> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageNote message, MessageContext ctx) { note(message.said, message.backdrop, message.font); }
    }

    public static class Handler extends AbstractClientMessageHandler<MessageHold> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageHold message, MessageContext ctx) { set(message.held, message.warning, message.fog, message.backdrop, message.font); }
    }
}
