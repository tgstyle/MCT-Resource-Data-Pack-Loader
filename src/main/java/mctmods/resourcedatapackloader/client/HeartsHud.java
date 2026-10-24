package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.compat.LineClientCompat;
import mctmods.resourcedatapackloader.content.ContentHearts;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import java.util.Random;
import javax.annotation.Nullable;

public final class HeartsHud {
    private static final Identifier TINT_FULL = ResourceDataPackLoader.id("hud/heart/tint_full");
    private static final Identifier TINT_HALF = ResourceDataPackLoader.id("hud/heart/tint_half");
    private static final Identifier TINT_HARDCORE_FULL = ResourceDataPackLoader.id("hud/heart/tint_hardcore_full");
    private static final Identifier TINT_HARDCORE_HALF = ResourceDataPackLoader.id("hud/heart/tint_hardcore_half");
    private static final Random RAND = new Random();
    private static final int PER_ROW = 10;
    private static volatile int[] colors = ContentHearts.DEFAULT_COLORS;
    private static int lastHealth;
    private static int displayHealth;
    private static long lastHealthTime;
    private static long blinkTime;

    private HeartsHud() {}

    public static void show(int[] shown) { colors = shown.length == 0 ? ContentHearts.DEFAULT_COLORS : shown.clone(); }

    @Nullable private static Player viewer(Minecraft mc) { return mc.getCameraEntity() instanceof Player player ? player : null; }

    public static void onPre(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) { return; }
        Player player = viewer(Minecraft.getInstance());
        if (player != null && heartCount(player) > PER_ROW) { event.setCanceled(true); }
    }

    private static int heartCount(Player player) { return Mth.ceil((float) player.getAttributeValue(Attributes.MAX_HEALTH) / 2.0F); }

    public static void onLayer(GuiGraphicsExtractor graphics, DeltaTracker ignoredDelta) {
        Minecraft mc = Minecraft.getInstance();
        Player player = viewer(mc);
        if (player == null || ClientCompat.hudHidden(mc) || mc.gameMode == null || !mc.gameMode.canHurtPlayer() || heartCount(player) <= PER_ROW) { return; }
        draw(graphics, mc, player);
    }

    private static Identifier container(boolean hardcore, boolean blink) { return Identifier.withDefaultNamespace("hud/heart/container" + (hardcore ? "_hardcore" : "") + (blink ? "_blinking" : "")); }

    private static Identifier sprite(String type, boolean hardcore, boolean half, boolean blink) { return Identifier.withDefaultNamespace("hud/heart/" + type + (hardcore ? "hardcore_" : "") + (half ? "half" : "full") + (blink ? "_blinking" : "")); }

    private static void beat(Player player, int tick, int health) {
        long now = Util.getMillis();
        int cooldown = LineClientCompat.hurtCooldown(player);
        if (health < lastHealth && cooldown > 0) {
            lastHealthTime = now;
            blinkTime = tick + 20L;
        }
        else if (health > lastHealth && cooldown > 0) {
            lastHealthTime = now;
            blinkTime = tick + 10L;
        }
        if (now - lastHealthTime > 1000L) {
            displayHealth = health;
            lastHealthTime = now;
        }
        lastHealth = health;
    }

    private static void draw(GuiGraphicsExtractor graphics, Minecraft mc, Player player) {
        int tick = player.tickCount;
        int health = Mth.ceil(player.getHealth());
        boolean blink = blinkTime > tick && (blinkTime - tick) / 3L % 2L == 1L;
        beat(player, tick, health);
        int oldHealth = displayHealth;
        int absorb = Mth.ceil(player.getAbsorptionAmount());
        int hearts = heartCount(player);
        int base = Math.min(hearts, PER_ROW);
        int total = base + Mth.ceil(absorb / 2.0F);
        int rows = Mth.ceil(total / (float) PER_ROW);
        int rowHeight = Math.max(10 - (rows - 2), 3);
        RAND.setSeed(tick * 312871L);
        int left = graphics.guiWidth() / 2 - 91;
        int top = graphics.guiHeight() - ClientCompat.leftHeight(mc);
        ClientCompat.addLeftHeight(mc, (rows - 1) * rowHeight + 10);
        int regen = player.hasEffect(MobEffects.REGENERATION) ? tick % 25 : -1;
        boolean hardcore = player.level().getLevelData().isHardcore();
        boolean poisoned = player.hasEffect(MobEffects.POISON);
        boolean withered = !poisoned && player.hasEffect(MobEffects.WITHER);
        boolean status = poisoned || withered;
        String type = poisoned ? "poisoned_" : withered ? "withered_" : player.isFullyFrozen() ? "frozen_" : "";
        for (int i = total - 1; i >= 0; i--) {
            int x = left + i % PER_ROW * 8;
            int y = top - i / PER_ROW * rowHeight;
            if (health + absorb <= 4) { y += RAND.nextInt(2); }
            if (i == regen) { y -= 2; }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, container(hardcore, blink), x, y, 9, 9);
            if (i >= base) {
                int halves = (i - base) * 2;
                if (halves < absorb) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite(withered ? type : "absorbing_", hardcore, halves + 1 == absorb, false), x, y, 9, 9); }
                continue;
            }
            for (int heart = i; heart < hearts; heart += PER_ROW) {
                int halves = heart * 2;
                if (blink && halves < oldHealth) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite(type, hardcore, halves + 1 == oldHealth, true), x, y, 9, 9); }
                if (halves >= health) { continue; }
                boolean half = halves + 1 == health;
                if (heart < PER_ROW || status) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite(type, hardcore, half, false), x, y, 9, 9); }
                else { tinted(graphics, x, y, heart / PER_ROW, half, hardcore); }
            }
        }
    }

    private static void tinted(GuiGraphicsExtractor graphics, int x, int y, int layer, boolean half, boolean hardcore) {
        int[] palette = colors;
        int rgb = palette[Math.min(layer - 1, palette.length - 1)];
        Identifier tint = hardcore ? half ? TINT_HARDCORE_HALF : TINT_HARDCORE_FULL : half ? TINT_HALF : TINT_FULL;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, tint, x, y, 9, 9, ARGB.opaque(rgb));
    }
}
