package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyPoseDef;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import java.io.IOException;
import java.io.InputStream;

public final class SkyOccluder {
    private static final double RADIUS = SkyMesh.SIZE;
    private static final int THRESHOLD = 8;
    private static final double MARGIN = 0.055D;
    private static final double REACH = Math.sqrt(0.5D);
    private static int generation;
    private final float angle;
    private final float tilt;
    private final double cosRoll;
    private final double sinRoll;
    private final boolean spins;
    private final float size;
    private final boolean moon;
    private final boolean fixed;
    private final String gate;
    private final ResourceLocation[] textures;
    private final int columns;
    private final int rows;
    private float turn;
    private boolean[] mask;
    private boolean[] radial;
    private int loaded;
    private int width;
    private int height;

    SkyOccluder(float angle, float tilt, float size, SkyPoseDef pose, boolean moon, boolean fixed, ResourceLocation[] textures, int columns, int rows, String gate) {
        this.angle = angle;
        this.tilt = (float) Math.toRadians(tilt);
        cosRoll = Math.cos(Math.toRadians(pose.roll()));
        sinRoll = Math.sin(Math.toRadians(pose.roll()));
        spins = pose.spins();
        this.size = size;
        this.moon = moon;
        this.fixed = fixed;
        this.gate = gate;
        this.textures = textures;
        this.columns = columns;
        this.rows = rows;
    }

    public static void invalidate() { ++generation; }

    static int current() { return generation; }

    boolean fixed() { return fixed; }

    void turn(float turn) { this.turn = turn; }

    boolean sweeps(double x, double reach) {
        if (ContentSkyGates.hides(gate)) { return false; }
        double gap = Math.abs(Math.asin(Mth.clamp(x, -1.0D, 1.0D)) - Math.asin(-Math.sin(tilt)));
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
        double flatX = x2 * RADIUS / (y2 * size);
        double flatZ = z1 * RADIUS / (y2 * size);
        double r = (fixed ? reach + MARGIN * (RADIUS / y2) * (RADIUS / y2) : reach) / (2.0D * size);
        if (spins) { return spun(Math.hypot(flatX, flatZ) / 2.0D, r); }
        double px = flatX * cosRoll - flatZ * sinRoll;
        double pz = flatX * sinRoll + flatZ * cosRoll;
        double u = moon ? (1.0D - px) / 2.0D : (px + 1.0D) / 2.0D;
        double v = moon ? (1.0D - pz) / 2.0D : (pz + 1.0D) / 2.0D;
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

    private boolean spun(double rho, double r) {
        if (rho - r > REACH) { return false; }
        if (radial == null || loaded != generation) { load(); }
        int bins = radial.length;
        int from = clamp((int) Math.floor(Math.max(0.0D, rho - r) / REACH * bins), bins);
        int to = clamp((int) Math.floor((rho + r) / REACH * bins), bins);
        for (int bin = from; bin <= to; ++bin) {
            if (radial[bin]) { return true; }
        }
        return false;
    }

    private static int clamp(int value, int limit) { return Mth.clamp(value, 0, limit - 1); }

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

    private void load() {
        mask = null;
        loaded = generation;
        for (ResourceLocation texture : textures) {
            try (InputStream stream = Minecraft.getInstance().getResourceManager().open(texture); NativeImage image = NativeImage.read(stream)) {
                merge(image);
            }
            catch (IOException e) {
                clear();
                return;
            }
        }
        if (mask == null) { clear(); }
        else if (spins) { bakeRadial(); }
    }

    private void clear() {
        width = 1;
        height = 1;
        mask = new boolean[] {true};
        radial = new boolean[] {true};
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
                        int pixel = image.getPixelRGBA(cellColumn * cellWidth + x * cellWidth / width, cellRow * cellHeight + y * cellHeight / height);
                        int alpha = pixel >>> 24;
                        int bright = Math.max(pixel >> 16 & 255, Math.max(pixel >> 8 & 255, pixel & 255));
                        if (alpha * bright > THRESHOLD * 255) { mask[y * width + x] = true; }
                    }
                }
            }
        }
    }
}
