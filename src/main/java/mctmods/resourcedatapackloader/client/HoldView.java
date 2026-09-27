package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import java.util.Locale;

public final class HoldView {
    private static final float FOG_REACH = 2.0F;
    private static final float VANILLA_START = 0.25F;
    private static final int SHOW = 60;
    private static final int FADE = 60;
    private static final Identifier LOGO = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "textures/gui/hold.png");
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
    private static final float PULSE_LEAST = 0.11F;
    private static String warning = "";
    private static String note = "";
    private static CardFont.Face face = new CardFont.Face(FontDescription.DEFAULT.id(), null);
    private static boolean fogging;
    private static boolean shaded = true;
    private static final int NOTE_COLOR = 0x55FF55;
    private static final float NOTE_SCALE = 2.0F;
    private static final int BACKDROP_ALPHA = 0x99;
    private static boolean held;
    private static int showing;

    private HoldView() {}

    public static void set(boolean holding, String said, boolean fog, boolean shade, String font) {
        warning = said;
        shaded = shade;
        face = CardFont.of(font);
        if (holding) {
            note = "";
            fogging = fog;
        }
        if (holding == held) { return; }
        held = holding;
        showing = holding ? 0 : SHOW + FADE;
    }

    public static void note(String said, boolean shade, String font) {
        note = said == null ? "" : said;
        shaded = shade;
        face = CardFont.of(font);
        if (note.isEmpty()) { return; }
        if (!held) { showing = SHOW + FADE; }
    }

    public static boolean showing() { return held || showing > 0; }

    private static float strength(float partialTicks) {
        if (held || showing > FADE) { return 1.0F; }
        if (showing <= 0) { return 0.0F; }
        return Math.max(0.0F, (showing - partialTicks) / FADE);
    }

    public static void onFog(ViewportEvent.RenderFog event) {
        if (!held && !fogging) { return; }
        float strength = strength((float) event.getPartialTick());
        if (strength <= 0.0F) { return; }
        float eased = strength * strength * (3.0F - 2.0F * strength);
        FogData fog = event.getFogData();
        float reach = Math.min(fog.environmentalEnd, fog.renderDistanceEnd);
        float near = reach * VANILLA_START * (1.0F - eased);
        float far = Math.min(reach, FOG_REACH * eased + reach * (1.0F - eased));
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        fog.skyEnd = Math.min(fog.skyEnd, far);
        fog.cloudEnd = Math.min(fog.cloudEnd, far);
    }

    public static void onHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientCompat.screen(mc) != null) { return; }
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        Crisp.raise(graphics);
        if (held) { warn(graphics); }
        else { welcome(graphics, strength(event.getPartialTick().getGameTimeDeltaPartialTick(false))); }
        Crisp.lower(graphics);
    }

    private static void welcome(GuiGraphicsExtractor graphics, float strength) {
        if (strength <= 0.0F) { return; }
        double gui = Crisp.factor();
        int screenWidth = (int) Math.round(Crisp.fit(graphics.guiWidth()) * gui);
        int times = Math.max(1, screenWidth / 4 / LOGO_WIDTH);
        int width = LOGO_WIDTH * times;
        int height = LOGO_HEIGHT * times;
        int margin = (int) Math.round(MARGIN * gui);
        int left = leftFor(screenWidth, width, margin);
        int top = Math.max(margin, (int) Math.round((Crisp.fit(graphics.guiHeight()) / 2.0D + SUBTITLE_TOP - MARGIN) * gui) - height);
        graphics.pose().pushMatrix();
        graphics.pose().scale((float) (1.0D / gui), (float) (1.0D / gui));
        graphics.blit(RenderPipelines.GUI_TEXTURED, LOGO, left, top, 0.0F, 0.0F, width, height, width, height, ARGB.white(strength));
        graphics.pose().popMatrix();
        greet(graphics, strength);
    }

    private static float pulse() {
        long at = System.currentTimeMillis() % PULSE_CYCLE;
        if (at < PULSE_HELD) { return 1.0F; }
        if (at < PULSE_HELD + PULSE_FADE) { return 1.0F - (at - PULSE_HELD) / (float) PULSE_FADE; }
        return 0.0F;
    }

    private static void greet(GuiGraphicsExtractor graphics, float strength) {
        if (note.isEmpty() || strength < PULSE_LEAST) { return; }
        Minecraft mc = Minecraft.getInstance();
        float scale = Crisp.scale(NOTE_SCALE);
        Component text = MarkText.text(face, note);
        int width = mc.font.width(text);
        float x = Crisp.snap((Crisp.fit(graphics.guiWidth()) - width * scale) / 2.0F);
        float y = Crisp.snap(Crisp.fit(graphics.guiHeight()) / 2.0F + SUBTITLE_TOP);
        int pad = 4;
        if (shaded) { graphics.fill(Math.round(x) - pad, Math.round(y) - pad, Math.round(x + width * scale) + pad, Math.round(y + mc.font.lineHeight * scale) + pad, Math.round(strength * BACKDROP_ALPHA) << 24); }
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.pose().translate(x / scale, y / scale);
        graphics.text(mc.font, text.getVisualOrderText(), 0, 0, Math.round(strength * 0xFF) << 24 | NOTE_COLOR, true);
        graphics.pose().popMatrix();
    }

    private static void warn(GuiGraphicsExtractor graphics) {
        if (warning.isEmpty()) { return; }
        float pulse = pulse();
        if (pulse < PULSE_LEAST) { return; }
        Minecraft mc = Minecraft.getInstance();
        float scale = Crisp.scale(TEXT_SCALE);
        Component text = MarkText.text(face, warning);
        int width = mc.font.width(text);
        float x = Crisp.snap((Crisp.fit(graphics.guiWidth()) - width * scale) / 2.0F);
        float y = Crisp.snap(Crisp.fit(graphics.guiHeight()) / 2.0F - TEXT_ABOVE_MIDDLE);
        int pad = 4;
        if (shaded) { graphics.fill(Math.round(x) - pad, Math.round(y) - pad, Math.round(x + width * scale) + pad, Math.round(y + mc.font.lineHeight * scale) + pad, Math.round(pulse * 0x99) << 24); }
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.pose().translate(x / scale, y / scale);
        graphics.text(mc.font, text.getVisualOrderText(), 0, 0, Math.round(pulse * 0xFF) << 24 | 0xFF5555, true);
        graphics.pose().popMatrix();
    }

    private static int leftFor(int screenWidth, int width, int margin) {
        String asked = ContentControl.text(ContentControl.CHUNKS, KEY, Config.chunks.pregenLogo()).trim().toLowerCase(Locale.ROOT);
        if ("left".equals(asked)) { return margin; }
        if ("right".equals(asked)) { return screenWidth - margin - width; }
        if (!"center".equals(asked) && !asked.isEmpty() && !asked.equals(warnedAbout)) {
            warnedAbout = asked;
            ContentLog.LOGGER.error("{} '{}' is not left, center or right, so the logo stands in the center", KEY, asked);
        }
        return (screenWidth - width) / 2;
    }

    public static void tick() {
        if (held || showing <= 0) { return; }
        showing--;
        if (showing <= 0) {
            note = "";
            fogging = false;
        }
    }

    public static void reset() {
        held = false;
        showing = 0;
        fogging = false;
        warning = "";
        note = "";
    }
}
