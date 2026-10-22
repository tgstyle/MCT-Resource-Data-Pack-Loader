package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SideOnly(Side.CLIENT) public final class SkyOccluder {
    private static final double RADIUS = 100.0D;
    private static final int THRESHOLD = 8;
    private static int generation;
    private final float angle;
    private final float tilt;
    private final float size;
    private final boolean moon;
    private final ResourceLocation[] textures;
    private final int columns;
    private final int rows;
    private boolean[] mask;
    private int loaded;
    private int width;
    private int height;

    SkyOccluder(float angle, float tilt, float size, boolean moon, ResourceLocation[] textures, int columns, int rows) {
        this.angle = angle;
        this.tilt = tilt;
        this.size = size;
        this.moon = moon;
        this.textures = textures;
        this.columns = columns;
        this.rows = rows;
    }

    static void invalidate() { ++generation; }

    static int current() { return generation; }

    boolean covers(double x, double y, double z, double reach) {
        double y1 = y * Math.cos(angle) + z * Math.sin(angle);
        double z1 = z * Math.cos(angle) - y * Math.sin(angle);
        double x2 = x * Math.cos(tilt) + y1 * Math.sin(tilt);
        double y2 = y1 * Math.cos(tilt) - x * Math.sin(tilt);
        if (y2 <= 0.0D) { return false; }
        double px = x2 * RADIUS / (y2 * size);
        double pz = z1 * RADIUS / (y2 * size);
        double u = moon ? (1.0D - px) / 2.0D : (px + 1.0D) / 2.0D;
        double v = moon ? (1.0D - pz) / 2.0D : (pz + 1.0D) / 2.0D;
        double r = reach / (2.0D * size);
        if (u + r < 0.0D || u - r > 1.0D || v + r < 0.0D || v - r > 1.0D) { return false; }
        if (mask == null || loaded != generation) { load(); }
        int fromX = clamp((int) Math.floor((u - r) * width), width);
        int toX = clamp((int) Math.floor((u + r) * width), width);
        int fromY = clamp((int) Math.floor((v - r) * height), height);
        int toY = clamp((int) Math.floor((v + r) * height), height);
        for (int row = fromY; row <= toY; ++row) {
            for (int column = fromX; column <= toX; ++column) {
                if (mask[row * width + column]) { return true; }
            }
        }
        return false;
    }

    private static int clamp(int value, int limit) { return MathHelper.clamp(value, 0, limit - 1); }

    private void load() {
        mask = null;
        loaded = generation;
        for (ResourceLocation texture : textures) {
            try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(texture)) {
                merge(TextureUtil.readBufferedImage(resource.getInputStream()));
            }
            catch (IOException e) {
                width = 1;
                height = 1;
                mask = new boolean[] {true};
                return;
            }
        }
        if (mask == null) {
            width = 1;
            height = 1;
            mask = new boolean[] {true};
        }
    }

    private void merge(BufferedImage image) {
        int cellWidth = image.getWidth() / columns;
        int cellHeight = image.getHeight() / rows;
        if (mask == null) {
            width = cellWidth;
            height = cellHeight;
            mask = new boolean[width * height];
        }
        for (int cellRow = 0; cellRow < rows; ++cellRow) {
            for (int cellColumn = 0; cellColumn < columns; ++cellColumn) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int pixel = image.getRGB(cellColumn * cellWidth + x * cellWidth / width, cellRow * cellHeight + y * cellHeight / height);
                        int alpha = pixel >>> 24;
                        int bright = Math.max(pixel >> 16 & 255, Math.max(pixel >> 8 & 255, pixel & 255));
                        if (alpha * bright > THRESHOLD * 255) { mask[y * width + x] = true; }
                    }
                }
            }
        }
    }
}
