package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class SkyOccluder {
    private static final double RADIUS = 100.0D;
    private static final int THRESHOLD = 8;
    private static final double REACH = Math.sqrt(0.5D);
    private static final double MARGIN = 0.055D;
    private static int generation;
    private final float angle;
    private final float tilt;
    private final float size;
    private final boolean moon;
    private final boolean fixed;
    private final ResourceLocation[] textures;
    private final int columns;
    private final int rows;
    private final double rollCos;
    private final double rollSin;
    private final boolean spins;
    private float turn;
    private boolean[] mask;
    private boolean[] rings;
    private int loaded;
    private int width;
    private int height;
    private String gate = "";

    SkyOccluder(float angle, float tilt, float size, SkyPoseDef pose, boolean moon, boolean fixed, ResourceLocation[] textures, int columns, int rows) {
        rollCos = Math.cos(Math.toRadians(pose.roll));
        rollSin = Math.sin(Math.toRadians(pose.roll));
        spins = pose.spins();
        this.angle = angle;
        this.tilt = tilt;
        this.size = size;
        this.moon = moon;
        this.fixed = fixed;
        this.textures = textures;
        this.columns = columns;
        this.rows = rows;
    }

    static void invalidate() { ++generation; }

    static int current() { return generation; }

    boolean fixed() { return fixed; }

    SkyOccluder gated(String gate) {
        this.gate = gate;
        return this;
    }

    void turn(float turn) { this.turn = turn; }

    boolean sweeps(double x, double reach) {
        if (ContentSkyGates.hides(gate)) { return false; }
        double gap = Math.abs(Math.asin(MathHelper.clamp(x, -1.0D, 1.0D)) - Math.asin(-Math.sin(tilt)));
        return gap <= Math.atan((size + reach + MARGIN) * Math.sqrt(2.0D) / RADIUS);
    }

    boolean covers(double x, double y, double z, double reach) {
        if (ContentSkyGates.hides(gate)) { return false; }
        double a = fixed ? angle - turn : angle;
        double y1 = y * Math.cos(a) + z * Math.sin(a);
        double z1 = z * Math.cos(a) - y * Math.sin(a);
        double x2 = x * Math.cos(tilt) + y1 * Math.sin(tilt);
        double y2 = y1 * Math.cos(tilt) - x * Math.sin(tilt);
        if (y2 <= 0.0D) { return false; }
        double sx = x2 * RADIUS / (y2 * size);
        double sz = z1 * RADIUS / (y2 * size);
        double r = (fixed ? reach + MARGIN * (RADIUS / y2) * (RADIUS / y2) : reach) / (2.0D * size);
        if (mask == null || loaded != generation) { load(); }
        if (spins) { return spunCovers(Math.hypot(sx, sz) / 2.0D, r); }
        double px = sx * rollCos - sz * rollSin;
        double pz = sx * rollSin + sz * rollCos;
        double u = moon ? (1.0D - px) / 2.0D : (px + 1.0D) / 2.0D;
        double v = moon ? (1.0D - pz) / 2.0D : (pz + 1.0D) / 2.0D;
        if (u + r < 0.0D || u - r > 1.0D || v + r < 0.0D || v - r > 1.0D) { return false; }
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

    private boolean spunCovers(double rho, double r) {
        if (rho - r > REACH) { return false; }
        double step = REACH / rings.length;
        int from = clamp((int) (Math.max(rho - r, 0.0D) / step), rings.length);
        int to = clamp((int) ((rho + r) / step), rings.length);
        for (int ring = from; ring <= to; ++ring) {
            if (rings[ring]) { return true; }
        }
        return false;
    }

    private static int clamp(int value, int limit) { return MathHelper.clamp(value, 0, limit - 1); }

    private void load() {
        read();
        if (!spins) { return; }
        rings = new boolean[Math.max(width, height)];
        double step = REACH / rings.length;
        double halfX = 0.5D / width;
        double halfY = 0.5D / height;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (!mask[y * width + x]) { continue; }
                double across = Math.abs((x + 0.5D) / width - 0.5D);
                double down = Math.abs((y + 0.5D) / height - 0.5D);
                double near = Math.hypot(Math.max(across - halfX, 0.0D), Math.max(down - halfY, 0.0D));
                double far = Math.hypot(across + halfX, down + halfY);
                int to = clamp((int) (far / step), rings.length);
                for (int ring = clamp((int) (near / step), rings.length); ring <= to; ++ring) { rings[ring] = true; }
            }
        }
    }

    private void read() {
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
