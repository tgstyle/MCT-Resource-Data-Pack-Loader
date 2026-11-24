package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.core.util.math.Colors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public final class Draw {
    private Draw() {}

    public static void tint(int color) { GlStateManager.color(Colors.redF(color), Colors.greenF(color), Colors.blueF(color), 1.0F); }

    public static void white() { GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); }

    public static BufferBuilder begin(int mode, VertexFormat format) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(mode, format);
        return buffer;
    }

    public static void blit(ResourceLocation texture, int x, int y, int width, int height, float alpha) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        quad(x, y, width, height);
    }

    public static void blitTinted(ResourceLocation texture, int x, int y, int width, int height, float alpha) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        quad(x, y, width, height);
    }

    private static void quad(int x, int y, int width, int height) {
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
    }
}
