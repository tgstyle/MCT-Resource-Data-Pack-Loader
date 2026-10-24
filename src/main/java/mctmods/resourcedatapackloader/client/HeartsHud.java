package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentHearts;
import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageHeartColors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;

@SideOnly(Side.CLIENT) public final class HeartsHud {
    private static final ResourceLocation FULL = ResourceDataPackLoader.id("textures/gui/hearts/full.png");
    private static final ResourceLocation HALF = ResourceDataPackLoader.id("textures/gui/hearts/half.png");
    private static final ResourceLocation HARDCORE_FULL = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_full.png");
    private static final ResourceLocation HARDCORE_HALF = ResourceDataPackLoader.id("textures/gui/hearts/hardcore_half.png");
    private static final Gui GUI = new Pen();
    private static final Random RAND = new Random();
    private static final int PER_ROW = 10;
    private static final float DEPTH = -90.0F;
    private static volatile int[] colors = ContentHearts.DEFAULT_COLORS;
    private static int playerHealth;
    private static int lastPlayerHealth;
    private static long lastSystemTime;
    private static long healthUpdateCounter;

    private HeartsHud() {}

    static void show(int[] shown) { colors = shown.length == 0 ? ContentHearts.DEFAULT_COLORS : shown.clone(); }

    @SubscribeEvent public static void onHealth(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        Entity view = mc.getRenderViewEntity();
        if (!(view instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) view;
        float healthMax = (float) player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
        if (MathHelper.ceil(healthMax / 2.0F) <= PER_ROW) { return; }
        event.setCanceled(true);
        draw(mc, player, healthMax, event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
    }

    private static void draw(Minecraft mc, EntityPlayer player, float healthMax, int width, int height) {
        mc.getTextureManager().bindTexture(Gui.ICONS);
        mc.profiler.startSection("health");
        GlStateManager.enableBlend();
        int updateCounter = mc.ingameGUI.getUpdateCounter();
        int health = MathHelper.ceil(player.getHealth());
        boolean highlight = healthUpdateCounter > updateCounter && (healthUpdateCounter - updateCounter) / 3L % 2L == 1L;
        if (health < playerHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = updateCounter + 20;
        }
        else if (health > playerHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = updateCounter + 10;
        }
        if (Minecraft.getSystemTime() - lastSystemTime > 1000L) {
            playerHealth = health;
            lastPlayerHealth = health;
            lastSystemTime = Minecraft.getSystemTime();
        }
        playerHealth = health;
        int healthLast = lastPlayerHealth;
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());
        int hearts = MathHelper.ceil(healthMax / 2.0F);
        int base = Math.min(hearts, PER_ROW);
        int total = base + MathHelper.ceil(absorb / 2.0F);
        int rows = MathHelper.ceil(total / (float) PER_ROW);
        int rowHeight = Math.max(10 - (rows - 2), 3);
        RAND.setSeed(updateCounter * 312871L);
        int left = width / 2 - 91;
        int top = height - GuiIngameForge.left_height;
        GuiIngameForge.left_height += rows * rowHeight;
        if (rowHeight != 10) { GuiIngameForge.left_height += 10 - rowHeight; }
        int regen = player.isPotionActive(MobEffects.REGENERATION) ? updateCounter % 25 : -1;
        boolean hardcore = mc.world.getWorldInfo().isHardcoreModeEnabled();
        int rowV = 9 * (hardcore ? 5 : 0);
        int margin = 16;
        boolean status = player.isPotionActive(MobEffects.POISON) || player.isPotionActive(MobEffects.WITHER);
        if (player.isPotionActive(MobEffects.POISON)) { margin += 36; }
        else if (player.isPotionActive(MobEffects.WITHER)) { margin += 72; }
        float absorbRemaining = absorb;
        for (int i = total - 1; i >= 0; i--) {
            int x = left + i % PER_ROW * 8;
            int y = top - i / PER_ROW * rowHeight;
            if (health <= 4) { y += RAND.nextInt(2); }
            if (i == regen) { y -= 2; }
            GUI.drawTexturedModalRect(x, y, highlight ? 25 : 16, rowV, 9, 9);
            if (i >= base) {
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        GUI.drawTexturedModalRect(x, y, margin + 153, rowV, 9, 9);
                        absorbRemaining--;
                    }
                    else {
                        GUI.drawTexturedModalRect(x, y, margin + 144, rowV, 9, 9);
                        absorbRemaining -= 2.0F;
                    }
                }
                continue;
            }
            for (int heart = i; heart < hearts; heart += PER_ROW) {
                if (highlight) {
                    if (heart * 2 + 1 < healthLast) { GUI.drawTexturedModalRect(x, y, margin + 54, rowV, 9, 9); }
                    else if (heart * 2 + 1 == healthLast) { GUI.drawTexturedModalRect(x, y, margin + 63, rowV, 9, 9); }
                }
                boolean full = heart * 2 + 1 < health;
                if (!full && heart * 2 + 1 != health) { continue; }
                if (heart < PER_ROW || status) { GUI.drawTexturedModalRect(x, y, margin + (full ? 36 : 45), rowV, 9, 9); }
                else { tinted(mc, x, y, heart / PER_ROW, full, hardcore); }
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        mc.profiler.endSection();
    }

    private static void tinted(Minecraft mc, int x, int y, int layer, boolean full, boolean hardcore) {
        int[] palette = colors;
        int rgb = palette[Math.min(layer - 1, palette.length - 1)];
        mc.getTextureManager().bindTexture(hardcore ? full ? HARDCORE_FULL : HARDCORE_HALF : full ? FULL : HALF);
        GlStateManager.color((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + 9, DEPTH).tex(0.0D, 1.0D).endVertex();
        buffer.pos(x + 9, y + 9, DEPTH).tex(1.0D, 1.0D).endVertex();
        buffer.pos(x + 9, y, DEPTH).tex(1.0D, 0.0D).endVertex();
        buffer.pos(x, y, DEPTH).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    private static final class Pen extends Gui {
        Pen() { zLevel = DEPTH; }
    }

    public static class Handler extends AbstractClientMessageHandler<MessageHeartColors> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageHeartColors message, MessageContext ctx) { show(message.colors); }
    }
}
