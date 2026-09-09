package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import java.util.Locale;

public final class HoldView {
    private static final float FOG_REACH = 24.0F;
    private static final int SHOW = 60;
    private static final int FADE = 60;
    private static final ResourceLocation LOGO = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "textures/gui/hold.png");
    private static final int LOGO_WIDTH = 329;
    private static final int LOGO_HEIGHT = 150;
    private static final int MARGIN = 12;
    private static final int SUBTITLE_TOP = 10;
    private static final String KEY = "pregenLogo";
    private static String warnedAbout = "";
    private static final int TEXT_ABOVE_MIDDLE = 36;
    private static final float TEXT_SCALE = 1.5F;
    private static final int PULSE_CYCLE = 1500;
    private static final int PULSE_HELD = 750;
    private static final int PULSE_FADE = 500;
    private static final float PULSE_LEAST = 0.05F;
    private static String warning = "";
    private static boolean held;
    private static int showing;

    private HoldView() {}

    public static void set(boolean holding, String said) {
        warning = said;
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

    public static void onFog(ViewportEvent.RenderFog event) {
        float strength = strength((float) event.getPartialTick());
        if (strength <= 0.0F) { return; }
        float eased = strength * strength * (3.0F - 2.0F * strength);
        float near = event.getNearPlaneDistance() * (1.0F - eased);
        float far = Math.min(event.getFarPlaneDistance(), FOG_REACH * eased + event.getFarPlaneDistance() * (1.0F - eased));
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        event.setCanceled(true);
    }

    public static void onHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) { return; }
        if (held) {
            warn(event.getGuiGraphics());
            return;
        }
        float strength = strength(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        if (strength <= 0.0F) { return; }
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int width = Math.min(LOGO_WIDTH, screenWidth / 4);
        int height = LOGO_HEIGHT * width / LOGO_WIDTH;
        int left = leftFor(screenWidth, width);
        int top = Math.max(MARGIN, graphics.guiHeight() / 2 + SUBTITLE_TOP - MARGIN - height);
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, strength);
        graphics.blit(LOGO, left, top, 0.0F, 0.0F, width, height, width, height);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private static float pulse() {
        long at = System.currentTimeMillis() % PULSE_CYCLE;
        if (at < PULSE_HELD) { return 1.0F; }
        if (at < PULSE_HELD + PULSE_FADE) { return 1.0F - (at - PULSE_HELD) / (float) PULSE_FADE; }
        return 0.0F;
    }

    private static void warn(GuiGraphics graphics) {
        if (warning.isEmpty()) { return; }
        float pulse = pulse();
        if (pulse < PULSE_LEAST) { return; }
        Minecraft mc = Minecraft.getInstance();
        float scale = Crisp.scale(TEXT_SCALE);
        int width = mc.font.width(warning);
        float x = Crisp.snap((graphics.guiWidth() - width * scale) / 2.0F);
        float y = Crisp.snap(graphics.guiHeight() / 2.0F - TEXT_ABOVE_MIDDLE);
        int pad = 4;
        graphics.fill(Math.round(x) - pad, Math.round(y) - pad, Math.round(x + width * scale) + pad, Math.round(y + mc.font.lineHeight * scale) + pad, Math.round(pulse * 0x99) << 24);
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(mc.font, warning, x / scale, y / scale, Math.round(pulse * 0xFF) << 24 | 0xFF5555, true);
        graphics.pose().popPose();
    }

    private static int leftFor(int screenWidth, int width) {
        String asked = ContentControl.text(ContentControl.CHUNKS, KEY, Config.chunks.pregenLogo()).trim().toLowerCase(Locale.ROOT);
        if ("left".equals(asked)) { return MARGIN; }
        if ("right".equals(asked)) { return screenWidth - MARGIN - width; }
        if (!"center".equals(asked) && !asked.isEmpty() && !asked.equals(warnedAbout)) {
            warnedAbout = asked;
            ContentLog.LOGGER.error("{} '{}' is not left, center or right, so the logo stands in the center", KEY, asked);
        }
        return (screenWidth - width) / 2;
    }

    public static void tick() {
        if (held || showing <= 0) { return; }
        showing--;
    }

    public static void reset() {
        held = false;
        showing = 0;
    }
}
