package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentHearts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public final class HeartsHud {
    private static final ResourceLocation ICONS = ResourceLocation.parse("textures/gui/icons.png");
    private static final ResourceLocation FULL = ResourceDataPackLoader.id("textures/gui/hearts/full.png");
    private static final ResourceLocation HALF = ResourceDataPackLoader.id("textures/gui/hearts/half.png");
    private static final ResourceLocation HARDCORE_FULL = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_full.png");
    private static final ResourceLocation HARDCORE_HALF = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_half.png");
    private static final RandomSource RAND = RandomSource.create();
    private static final int PER_ROW = 10;
    private static final int SIZE = 9;
    private static volatile int[] colors = ContentHearts.DEFAULT_COLORS;
    private static int playerHealth;
    private static int lastPlayerHealth;
    private static long lastSystemTime;
    private static long healthUpdateCounter;

    private HeartsHud() {}

    public static void show(int[] shown) { colors = shown.length == 0 ? ContentHearts.DEFAULT_COLORS : shown.clone(); }

    public static void onHealth(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.PLAYER_HEALTH.type()) { return; }
        Minecraft mc = Minecraft.getInstance();
        Entity view = mc.getCameraEntity();
        if (!(view instanceof Player player) || !(mc.gui instanceof ForgeGui gui)) { return; }
        float healthMax = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
        if (Mth.ceil(healthMax / 2.0F) <= PER_ROW) { return; }
        event.setCanceled(true);
        draw(mc, gui, event.getGuiGraphics(), player, healthMax, event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
    }

    private static void draw(Minecraft mc, ForgeGui gui, GuiGraphics graphics, Player player, float healthMax, int width, int height) {
        mc.getProfiler().push("health");
        RenderSystem.enableBlend();
        int updateCounter = gui.getGuiTicks();
        int health = Mth.ceil(player.getHealth());
        boolean highlight = healthUpdateCounter > updateCounter && (healthUpdateCounter - updateCounter) / 3L % 2L == 1L;
        if (health < playerHealth && player.invulnerableTime > 0) {
            lastSystemTime = Util.getMillis();
            healthUpdateCounter = updateCounter + 20;
        }
        else if (health > playerHealth && player.invulnerableTime > 0) {
            lastSystemTime = Util.getMillis();
            healthUpdateCounter = updateCounter + 10;
        }
        if (Util.getMillis() - lastSystemTime > 1000L) {
            playerHealth = health;
            lastPlayerHealth = health;
            lastSystemTime = Util.getMillis();
        }
        playerHealth = health;
        int healthLast = lastPlayerHealth;
        float absorb = Mth.ceil(player.getAbsorptionAmount());
        int hearts = Mth.ceil(healthMax / 2.0F);
        int base = Math.min(hearts, PER_ROW);
        int total = base + Mth.ceil(absorb / 2.0F);
        int rows = Mth.ceil(total / (float) PER_ROW);
        int rowHeight = Math.max(10 - (rows - 2), 3);
        RAND.setSeed(updateCounter * 312871L);
        int left = width / 2 - 91;
        int top = height - gui.leftHeight;
        gui.leftHeight += rows * rowHeight;
        if (rowHeight != 10) { gui.leftHeight += 10 - rowHeight; }
        int regen = player.hasEffect(MobEffects.REGENERATION) ? updateCounter % 25 : -1;
        boolean hardcore = player.level().getLevelData().isHardcore();
        int rowV = 9 * (hardcore ? 5 : 0);
        int margin = 16;
        boolean status = player.hasEffect(MobEffects.POISON) || player.hasEffect(MobEffects.WITHER);
        if (player.hasEffect(MobEffects.POISON)) { margin += 36; }
        else if (player.hasEffect(MobEffects.WITHER)) { margin += 72; }
        float absorbRemaining = absorb;
        for (int i = total - 1; i >= 0; i--) {
            int x = left + i % PER_ROW * 8;
            int y = top - i / PER_ROW * rowHeight;
            if (health <= 4) { y += RAND.nextInt(2); }
            if (i == regen) { y -= 2; }
            graphics.blit(ICONS, x, y, highlight ? 25 : 16, rowV, SIZE, SIZE);
            if (i >= base) {
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        graphics.blit(ICONS, x, y, margin + 153, rowV, SIZE, SIZE);
                        absorbRemaining--;
                    }
                    else {
                        graphics.blit(ICONS, x, y, margin + 144, rowV, SIZE, SIZE);
                        absorbRemaining -= 2.0F;
                    }
                }
                continue;
            }
            for (int heart = i; heart < hearts; heart += PER_ROW) {
                if (highlight) {
                    if (heart * 2 + 1 < healthLast) { graphics.blit(ICONS, x, y, margin + 54, rowV, SIZE, SIZE); }
                    else if (heart * 2 + 1 == healthLast) { graphics.blit(ICONS, x, y, margin + 63, rowV, SIZE, SIZE); }
                }
                boolean full = heart * 2 + 1 < health;
                if (!full && heart * 2 + 1 != health) { continue; }
                if (heart < PER_ROW || status) { graphics.blit(ICONS, x, y, margin + (full ? 36 : 45), rowV, SIZE, SIZE); }
                else { tinted(graphics, x, y, heart / PER_ROW, full, hardcore); }
            }
        }
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }

    private static void tinted(GuiGraphics graphics, int x, int y, int layer, boolean full, boolean hardcore) {
        int[] palette = colors;
        int rgb = palette[Math.min(layer - 1, palette.length - 1)];
        ResourceLocation texture = hardcore ? full ? HARDCORE_FULL : HARDCORE_HALF : full ? FULL : HALF;
        graphics.setColor((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
        graphics.blit(texture, x, y, SIZE, SIZE, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
