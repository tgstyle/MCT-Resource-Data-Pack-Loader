package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.IntroPageDef;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public class GuiWorldIntro extends GuiScreen {
    private static final int TEXT_WIDTH = 274;
    private static final int LINE_HEIGHT = 12;
    private static final int MARGIN = 40;
    private static final float DERIVED_SPEED = 0.25F;
    private static final int NEXT = 0;
    private static final int SKIP = 1;
    private final List<IntroPageDef> pages;
    private final List<String> lines = new ArrayList<>();
    @Nullable private final ISound music;
    private final boolean landBeingMade;
    private int page;
    private boolean sounding;
    private int wrapWidth = TEXT_WIDTH;
    private float totalScrollLength;
    private float ticks;

    private GuiWorldIntro(List<IntroPageDef> pages, @Nullable ISound music, boolean landBeingMade) {
        this.pages = pages;
        this.music = music;
        this.landBeingMade = landBeingMade;
    }

    public static void open(boolean landBeingMade) {
        List<IntroPageDef> pages = ContentWorldIntro.pages();
        if (pages.isEmpty()) { return; }
        Minecraft.getMinecraft().displayGuiScreen(new GuiWorldIntro(pages, track(), landBeingMade));
    }

    @Override public boolean doesGuiPauseGame() { return !landBeingMade; }

    @Nullable private static ISound track() {
        ResourceLocation key = ContentWorldIntro.music();
        if (key == null) { return null; }
        SoundEvent event = SoundEvent.REGISTRY.getObject(key);
        if (event == null) {
            ContentLog.LOGGER.error("World intro names music {}, which nothing registers, so it plays silently", key);
            return null;
        }
        return PositionedSoundRecord.getMusicRecord(event);
    }

    @Override public void initGui() {
        buttonList.clear();
        if (page >= pages.size() - 1) { buttonList.add(new GuiButton(NEXT, width / 2 - 100, height - 28, 200, 20, I18n.format("rdpl.intro.continue"))); }
        else {
            buttonList.add(new GuiButton(NEXT, width / 2 - 154, height - 28, 150, 20, I18n.format("rdpl.intro.next")));
            buttonList.add(new GuiButton(SKIP, width / 2 + 4, height - 28, 150, 20, I18n.format("rdpl.intro.skip")));
        }
        loadPage();
        if (music != null && !sounding) {
            sounding = true;
            mc.getSoundHandler().playSound(music);
        }
    }

    @Override public void updateScreen() {
        ticks += 1.0F;
        IntroPageDef def = pages.get(page);
        if (def.still() || page >= pages.size() - 1) { return; }
        if (ticks >= duration()) { advance(); }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawPageBackground(partialTicks);
        IntroPageDef def = pages.get(page);
        float scale = def.textScale;
        float step = LINE_HEIGHT * scale;
        float y = offset(partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        for (String line : lines) {
            if (y > -step && y < height) {
                float x = def.still() ? (width - fontRenderer.getStringWidth(line) * scale) / 2.0F : (width - wrapWidth * scale) / 2.0F;
                fontRenderer.drawString(line, x / scale, y / scale, 0xFFFFFF, true);
            }
            y += step;
        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id == SKIP) { finish(); }
        else if (button.id == NEXT) { advance(); }
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) { finish(); }
    }

    private void advance() {
        if (page >= pages.size() - 1) {
            finish();
            return;
        }
        page++;
        ticks = 0.0F;
        initGui();
    }

    @Override public void onGuiClosed() {
        if (music != null) { mc.getSoundHandler().stopSound(music); }
    }

    private void finish() {
        RDPLNetwork.introDone();
        mc.displayGuiScreen(null);
    }

    private float duration() {
        IntroPageDef def = pages.get(page);
        if (def.time > IntroPageDef.DERIVE) { return def.time * 20.0F; }
        return Math.abs(endOffset() - startOffset()) / DERIVED_SPEED;
    }

    private float startOffset() { return pages.get(page).up() ? height : -totalScrollLength; }

    private float endOffset() {
        IntroPageDef def = pages.get(page);
        if (def.settle) {
            float step = LINE_HEIGHT * def.textScale;
            return (height - step) / 2.0F - Math.max(lines.size() - 1, 0) * step;
        }
        return def.up() ? -totalScrollLength - 24.0F : height + 24.0F;
    }

    private float offset(float partialTicks) {
        IntroPageDef def = pages.get(page);
        if (def.still()) { return (height - totalScrollLength) / 2.0F; }
        float start = startOffset();
        float span = duration();
        if (span <= 0.0F) { return endOffset(); }
        return start + (endOffset() - start) * Math.min((ticks + partialTicks) / span, 1.0F);
    }

    private void drawPageBackground(float partialTicks) {
        IntroPageDef def = pages.get(page);
        if (def.backgrounds.isEmpty()) {
            drawBackground(0);
            return;
        }
        int index = def.cycles() ? (int) ((ticks + partialTicks) / (def.interval * 20.0F)) % def.backgrounds.size() : 0;
        mc.getTextureManager().bindTexture(def.backgrounds.get(index));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, width, height, width, height);
    }

    private void loadPage() {
        lines.clear();
        totalScrollLength = 0.0F;
        IntroPageDef def = pages.get(page);
        wrapWidth = (int) Math.min(TEXT_WIDTH, Math.max(1.0F, (width - MARGIN) / def.textScale));
        if (def.text == null) { return; }
        InputStream stream = null;
        try {
            stream = mc.getResourceManager().getResource(def.text).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String name = mc.getSession().getUsername();
            String line;
            while ((line = reader.readLine()) != null) {
                String text = line.replaceAll("PLAYERNAME", name);
                if (text.isEmpty()) { lines.add(""); }
                else { lines.addAll(fontRenderer.listFormattedStringToWidth(text, wrapWidth)); }
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not read intro text {}, showing the page without it: {}", def.text, ex.getMessage()); }
        finally { IOUtils.closeQuietly(stream); }
        totalScrollLength = lines.size() * LINE_HEIGHT * def.textScale;
    }
}
