package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class SkyOccluder {
    private static final double RADIUS = ContentSkyShapes.SIZE;
    private static final int THRESHOLD = 8;
    private static final double MARGIN = 0.055D;
    private static final double REACH = Math.sqrt(0.5D);
    private static int generation;
    private final float tilt;
    private final float size;
    private final SkyPoseDef pose;
    private final double rollCos;
    private final double rollSin;
    private final boolean moon;
    private final boolean fixed;
    private final Identifier[] textures;
    private final int columns;
    private final int rows;
    private float angle;
    private boolean[] mask;
    private boolean[] radial;
    private int loaded;
    private int width;
    private int height;
    private String gate;

    SkyOccluder(float tilt, float size, SkyPoseDef pose, boolean moon, boolean fixed, Identifier[] textures, int columns, int rows) {
        this.tilt = tilt;
        this.size = size;
        this.pose = pose;
        this.rollCos = Math.cos(Math.toRadians(pose.roll()));
        this.rollSin = Math.sin(Math.toRadians(pose.roll()));
        this.moon = moon;
        this.fixed = fixed;
        this.textures = textures;
        this.columns = columns;
        this.rows = rows;
    }

    SkyOccluder gated(String gate) {
        this.gate = gate;
        return this;
    }

    void turn(float angle) { this.angle = angle; }

    boolean fixed() { return fixed; }

    boolean sweeps(double x, double reach) {
        if (ContentSkyGates.hides(gate)) { return false; }
        double gap = Math.abs(Math.asin(Mth.clamp(x, -1.0D, 1.0D)) - Math.asin(-Math.sin(tilt)));
        return gap <= Math.atan((size + reach + MARGIN) * Math.sqrt(2.0D) / RADIUS);
    }

    public static void invalidate() { ++generation; }

    static int current() { return generation; }

    boolean covers(double x, double y, double z, double reach) {
        if (ContentSkyGates.hides(gate)) { return false; }
        double y1 = y * Math.cos(angle) + z * Math.sin(angle);
        double z1 = z * Math.cos(angle) - y * Math.sin(angle);
        double x2 = x * Math.cos(tilt) + y1 * Math.sin(tilt);
        double y2 = y1 * Math.cos(tilt) - x * Math.sin(tilt);
        if (y2 <= 0.0D) { return false; }
        double px = x2 * RADIUS / (y2 * size);
        double pz = z1 * RADIUS / (y2 * size);
        double r = (fixed ? reach + MARGIN * (RADIUS / y2) * (RADIUS / y2) : reach) / (2.0D * size);
        if (pose.spins()) { return swept(Math.hypot(px, pz) / 2.0D, r); }
        double rx = px * rollCos - pz * rollSin;
        double rz = px * rollSin + pz * rollCos;
        double u = moon ? (1.0D - rx) / 2.0D : (rx + 1.0D) / 2.0D;
        double v = moon ? (1.0D - rz) / 2.0D : (rz + 1.0D) / 2.0D;
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

    private boolean swept(double rho, double r) {
        if (mask == null || loaded != generation) { load(); }
        if (rho - r > REACH) { return false; }
        int bins = radial.length;
        int from = clamp((int) Math.floor((rho - r) / REACH * bins), bins);
        int to = clamp((int) Math.floor((rho + r) / REACH * bins), bins);
        for (int bin = from; bin <= to; ++bin) {
            if (radial[bin]) { return true; }
        }
        return false;
    }

    private static int clamp(int value, int limit) { return Mth.clamp(value, 0, limit - 1); }

    private void load() {
        mask = null;
        loaded = generation;
        for (Identifier texture : textures) {
            try (InputStream stream = Minecraft.getInstance().getResourceManager().open(texture); NativeImage image = NativeImage.read(stream)) { merge(image); }
            catch (IOException e) {
                clear();
                return;
            }
        }
        if (mask == null) { clear(); }
        bakeRadial();
    }

    private void clear() {
        width = 1;
        height = 1;
        mask = new boolean[] {true};
        radial = new boolean[] {true};
    }

    private void bakeRadial() {
        int bins = Math.max(width, height);
        radial = new boolean[bins];
        double halfX = 0.5D / width;
        double halfY = 0.5D / height;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (!mask[y * width + x]) { continue; }
                double dx = Math.abs((x + 0.5D) / width - 0.5D);
                double dy = Math.abs((y + 0.5D) / height - 0.5D);
                double near = Math.hypot(Math.max(dx - halfX, 0.0D), Math.max(dy - halfY, 0.0D));
                double far = Math.hypot(dx + halfX, dy + halfY);
                int to = Math.min(bins - 1, (int) (far / REACH * bins));
                for (int bin = (int) (near / REACH * bins); bin <= to; ++bin) { radial[bin] = true; }
            }
        }
    }

    private void merge(NativeImage image) {
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
                        int pixel = image.getPixel(cellColumn * cellWidth + x * cellWidth / width, cellRow * cellHeight + y * cellHeight / height);
                        int alpha = pixel >>> 24;
                        int bright = Math.max(pixel >> 16 & 255, Math.max(pixel >> 8 & 255, pixel & 255));
                        if (alpha * bright > THRESHOLD * 255) { mask[y * width + x] = true; }
                    }
                }
            }
        }
    }
}
