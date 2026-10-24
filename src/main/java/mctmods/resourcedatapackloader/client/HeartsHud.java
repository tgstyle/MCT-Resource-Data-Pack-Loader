package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentHearts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.EventHooks;
import java.util.List;

public final class HeartsHud {
    private static final ResourceLocation FULL = ResourceDataPackLoader.id("textures/gui/hearts/full.png");
    private static final ResourceLocation HALF = ResourceDataPackLoader.id("textures/gui/hearts/half.png");
    private static final ResourceLocation HARDCORE_FULL = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_full.png");
    private static final ResourceLocation HARDCORE_HALF = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_half.png");
    private static final RandomSource RAND = RandomSource.create();
    private static final int PER_ROW = 10;
    private static final int SIZE = 9;
    private static volatile int[] colors = ContentHearts.DEFAULT_COLORS;
    private static int lastHealth;
    private static int displayHealth;
    private static long lastHealthTime;
    private static long healthBlinkTime;

    private HeartsHud() {}

    public static void show(List<Integer> shown) { colors = shown.isEmpty() ? ContentHearts.DEFAULT_COLORS : shown.stream().mapToInt(Integer::intValue).toArray(); }

    public static void onLayer(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) { return; }
        Minecraft mc = Minecraft.getInstance();
        Entity view = mc.getCameraEntity();
        if (!(view instanceof Player player)) { return; }
        float healthMax = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
        if (Mth.ceil(healthMax / 2.0F) <= PER_ROW) { return; }
        event.setCanceled(true);
        draw(mc, event.getGuiGraphics(), player, healthMax);
    }

    private static Gui.HeartType kind(Player player) {
        Gui.HeartType type = Gui.HeartType.NORMAL;
        if (player.hasEffect(MobEffects.POISON)) { type = Gui.HeartType.POISIONED; }
        else if (player.hasEffect(MobEffects.WITHER)) { type = Gui.HeartType.WITHERED; }
        else if (player.isFullyFrozen()) { type = Gui.HeartType.FROZEN; }
        return EventHooks.firePlayerHeartTypeEvent(player, type);
    }

    private static void draw(Minecraft mc, GuiGraphics graphics, Player player, float healthMax) {
        Gui gui = mc.gui;
        mc.getProfiler().push("health");
        int ticks = gui.getGuiTicks();
        int health = Mth.ceil(player.getHealth());
        boolean highlight = healthBlinkTime > ticks && (healthBlinkTime - ticks) / 3L % 2L == 1L;
        long now = Util.getMillis();
        if (health < lastHealth && player.invulnerableTime > 0) {
            lastHealthTime = now;
            healthBlinkTime = ticks + 20;
        }
        else if (health > lastHealth && player.invulnerableTime > 0) {
            lastHealthTime = now;
            healthBlinkTime = ticks + 10;
        }
        if (now - lastHealthTime > 1000L) {
            lastHealth = health;
            displayHealth = health;
            lastHealthTime = now;
        }
        lastHealth = health;
        int healthLast = displayHealth;
        int absorb = Mth.ceil(player.getAbsorptionAmount());
        int hearts = Mth.ceil(healthMax / 2.0F);
        int base = Math.min(hearts, PER_ROW);
        int total = base + Mth.ceil(absorb / 2.0F);
        int rows = Mth.ceil(total / (float) PER_ROW);
        int rowHeight = Math.max(10 - (rows - 2), 3);
        RAND.setSeed(ticks * 312871L);
        int left = graphics.guiWidth() / 2 - 91;
        int top = graphics.guiHeight() - gui.leftHeight;
        gui.leftHeight += (rows - 1) * rowHeight + 10;
        int regen = player.hasEffect(MobEffects.REGENERATION) ? ticks % 25 : -1;
        boolean hardcore = player.level().getLevelData().isHardcore();
        Gui.HeartType type = kind(player);
        boolean status = type != Gui.HeartType.NORMAL;
        for (int i = total - 1; i >= 0; i--) {
            int x = left + i % PER_ROW * 8;
            int y = top - i / PER_ROW * rowHeight;
            if (health + absorb <= 4) { y += RAND.nextInt(2); }
            if (i == regen) { y -= 2; }
            sprite(graphics, Gui.HeartType.CONTAINER, x, y, hardcore, highlight, false);
            if (i >= base) {
                int index = (i - base) * 2;
                if (index < absorb) { sprite(graphics, type == Gui.HeartType.WITHERED ? type : Gui.HeartType.ABSORBING, x, y, hardcore, false, index + 1 == absorb); }
                continue;
            }
            for (int heart = i; heart < hearts; heart += PER_ROW) {
                int value = heart * 2;
                if (highlight && value < healthLast) { sprite(graphics, type, x, y, hardcore, true, value + 1 == healthLast); }
                if (value >= health) { continue; }
                boolean half = value + 1 == health;
                if (heart < PER_ROW || status) { sprite(graphics, type, x, y, hardcore, false, half); }
                else { tinted(graphics, x, y, heart / PER_ROW, half, hardcore); }
            }
        }
        mc.getProfiler().pop();
    }

    private static void sprite(GuiGraphics graphics, Gui.HeartType type, int x, int y, boolean hardcore, boolean blink, boolean half) {
        RenderSystem.enableBlend();
        graphics.blitSprite(type.getSprite(hardcore, blink, half), x, y, SIZE, SIZE);
        RenderSystem.disableBlend();
    }

    private static void tinted(GuiGraphics graphics, int x, int y, int layer, boolean half, boolean hardcore) {
        int[] palette = colors;
        int rgb = palette[Math.min(layer - 1, palette.length - 1)];
        ResourceLocation texture = hardcore ? half ? HARDCORE_HALF : HARDCORE_FULL : half ? HALF : FULL;
        RenderSystem.enableBlend();
        graphics.setColor((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
        graphics.blit(texture, x, y, 0, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
