package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.IntroPageDef;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class WorldIntroScreen extends Screen {
    private static final int TEXT_WIDTH = 274;
    private static final int LINE_HEIGHT = 12;
    private static final int MARGIN = 40;
    private static final int FOOTER = 36;
    private static final float DERIVED_SPEED = 0.25F;
    private final List<IntroPageDef> pages;
    private final List<FormattedCharSequence> lines = new ArrayList<>();
    @Nullable private final SoundInstance music;
    private final boolean landBeingMade;
    private int page;
    private boolean sounding;
    private int wrapWidth = TEXT_WIDTH;
    private float totalScrollLength;
    private float ticks;

    private WorldIntroScreen(List<IntroPageDef> pages, @Nullable SoundInstance music, boolean landBeingMade) {
        super(Component.literal("World intro"));
        this.pages = pages;
        this.music = music;
        this.landBeingMade = landBeingMade;
    }

    public static void open(boolean landBeingMade) {
        List<IntroPageDef> pages = ContentWorldIntro.pages();
        if (pages.isEmpty()) { return; }
        Minecraft.getInstance().setScreen(new WorldIntroScreen(pages, track(), landBeingMade));
    }

    @Override public boolean isPauseScreen() { return !landBeingMade; }

    @Nullable private static SoundInstance track() {
        ResourceLocation key = ContentWorldIntro.music();
        if (key == null) { return null; }
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getOptional(key).orElse(null);
        if (event == null) {
            ContentLog.LOGGER.error("World intro names music {}, which nothing registers, so it plays silently", key);
            return null;
        }
        return SimpleSoundInstance.forMusic(event);
    }

    @Override protected void init() {
        clearWidgets();
        if (page >= pages.size() - 1) { addRenderableWidget(Button.builder(Component.translatable("rdpl.intro.continue"), button -> advance()).bounds(width / 2 - 100, height - 28, 200, 20).build()); }
        else {
            addRenderableWidget(Button.builder(Component.translatable("rdpl.intro.next"), button -> advance()).bounds(width / 2 - 154, height - 28, 150, 20).build());
            addRenderableWidget(Button.builder(Component.translatable("rdpl.intro.skip"), button -> finish()).bounds(width / 2 + 4, height - 28, 150, 20).build());
        }
        loadPage();
        if (music != null && !sounding) {
            sounding = true;
            Minecraft.getInstance().getSoundManager().play(music);
        }
    }

    @Override public void tick() {
        ticks += 1.0F;
        IntroPageDef def = pages.get(page);
        if (def.still() || page >= pages.size() - 1) { return; }
        if (ticks >= duration()) { advance(); }
    }

    @Override public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        drawPageBackground(graphics, partialTick);
        IntroPageDef def = pages.get(page);
        float scale = Crisp.scale(def.textScale());
        float step = LINE_HEIGHT * scale;
        float y = offset(partialTick);
        graphics.enableScissor(0, 0, width, height - FOOTER);
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0F);
        for (FormattedCharSequence line : lines) {
            if (y > -step && y < height) {
                float x = def.still() ? (width - font.width(line) * scale) / 2.0F : (width - wrapWidth * scale) / 2.0F;
                graphics.drawString(font, line, Crisp.snap(x) / scale, Crisp.snap(y) / scale, 0xFFFFFF, true);
            }
            y += step;
        }
        graphics.pose().popPose();
        graphics.disableScissor();
        for (Renderable widget : renderables) { widget.render(graphics, mouseX, mouseY, partialTick); }
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            finish();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }

    private void advance() {
        if (page >= pages.size() - 1) {
            finish();
            return;
        }
        page++;
        ticks = 0.0F;
        init();
    }

    @Override public void removed() {
        if (music != null) { Minecraft.getInstance().getSoundManager().stop(music); }
    }

    private void finish() {
        RDPLNetwork.introDone();
        Minecraft.getInstance().setScreen(null);
    }

    private float duration() {
        IntroPageDef def = pages.get(page);
        if (def.time() > IntroPageDef.DERIVE) { return def.time() * 20.0F; }
        return Math.abs(endOffset() - startOffset()) / DERIVED_SPEED;
    }

    private float startOffset() { return pages.get(page).up() ? height : -totalScrollLength; }

    private float endOffset() {
        IntroPageDef def = pages.get(page);
        if (def.settle()) {
            float step = LINE_HEIGHT * def.textScale();
            return (height - step) / 2.0F - Math.max(lines.size() - 1, 0) * step;
        }
        return def.up() ? -totalScrollLength - 24.0F : height + 24.0F;
    }

    private float offset(float partialTick) {
        IntroPageDef def = pages.get(page);
        if (def.still()) { return (height - totalScrollLength) / 2.0F; }
        float start = startOffset();
        float span = duration();
        if (span <= 0.0F) { return endOffset(); }
        return start + (endOffset() - start) * Math.min((ticks + partialTick) / span, 1.0F);
    }

    private void drawPageBackground(GuiGraphics graphics, float partialTick) {
        IntroPageDef def = pages.get(page);
        if (def.backgrounds().isEmpty()) {
            graphics.fill(0, 0, width, height, 0xFF000000);
            return;
        }
        int index = def.cycles() ? (int) ((ticks + partialTick) / (def.interval() * 20.0F)) % def.backgrounds().size() : 0;
        graphics.blit(def.backgrounds().get(index), 0, 0, 0.0F, 0.0F, width, height, width, height);
    }

    private void loadPage() {
        lines.clear();
        totalScrollLength = 0.0F;
        IntroPageDef def = pages.get(page);
        wrapWidth = (int) Mth.clamp((width - MARGIN) / def.textScale(), 1.0F, TEXT_WIDTH);
        if (def.text() == null) { return; }
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(def.text());
        if (resource.isEmpty()) {
            ContentLog.LOGGER.error("Could not find intro text {}, showing the page without it", def.text());
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8))) {
            String name = Minecraft.getInstance().getUser().getName();
            String line;
            while ((line = reader.readLine()) != null) {
                String text = line.replace("PLAYERNAME", name);
                if (text.isEmpty()) { lines.add(FormattedCharSequence.EMPTY); }
                else { lines.addAll(font.split(Component.literal(text), wrapWidth)); }
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not read intro text {}, showing the page without it: {}", def.text(), ex.getMessage()); }
        totalScrollLength = lines.size() * LINE_HEIGHT * def.textScale();
    }
}
