package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public final class ContentSkyShapes {
    public static final int TEXTURED = 5;
    public static final int COLORED = 7;
    private static final float SIZE = 100.0F;
    private static final float[][][] FACES = {
            {{-1, 1, 1}, {1, 1, 1}, {1, 1, -1}, {-1, 1, -1}},
            {{-1, -1, -1}, {1, -1, -1}, {1, -1, 1}, {-1, -1, 1}},
            {{-1, 1, -1}, {1, 1, -1}, {1, -1, -1}, {-1, -1, -1}},
            {{1, 1, -1}, {1, 1, 1}, {1, -1, 1}, {1, -1, -1}},
            {{1, 1, 1}, {-1, 1, 1}, {-1, -1, 1}, {1, -1, 1}},
            {{-1, 1, 1}, {-1, 1, -1}, {-1, -1, -1}, {-1, -1, 1}}};
    private static final float[][] CORNER_UV = {{0.0F, 0.0F}, {1.0F, 0.0F}, {1.0F, 1.0F}, {0.0F, 1.0F}};
    private static final int LONGITUDES = 32;
    private static final int LATITUDES = 16;
    private static final int AURORA_STEPS = 48;
    private static final float[] BOW_RADII = {40.0F, 40.6F, 41.0F, 41.4F, 41.8F, 42.2F, 42.6F, 43.2F};
    private static final int[] BOW_COLORS = {0x8B00FF, 0x8B00FF, 0x0040FF, 0x00C000, 0xFFFF00, 0xFF8000, 0xFF0000, 0xFF0000};
    private static final int BOW_STEPS = 64;
    public static final int FACE_COUNT = FACES.length;
    public static final int PANORAMA_VERTICES = LATITUDES * LONGITUDES * 4;
    public static final int AURORA_VERTICES = 2 * AURORA_STEPS * 4;
    public static final int BOW_VERTICES = (BOW_RADII.length - 1) * BOW_STEPS * 4;
    private static final long RAINBOW_TICKS = 2400L;
    private static final float RAINBOW_FADE = 600.0F;
    private static int wetWorld;
    private static long wetAt = Long.MIN_VALUE;

    private ContentSkyShapes() {}

    @Nullable public static SkyLookDef.Skybox skybox(@Nullable Level level) {
        SkyLookDef look = ContentFogSampler.look(level);
        return look == null ? null : look.skybox();
    }

    public static AbstractTexture[] images(SkyLookDef.Skybox skybox) {
        TextureManager textures = Minecraft.getInstance().getTextureManager();
        if (skybox.panorama() != null) { return new AbstractTexture[] {textures.getTexture(skybox.panorama())}; }
        AbstractTexture[] images = new AbstractTexture[FACE_COUNT];
        for (int face = 0; face < FACE_COUNT; ++face) { images[face] = textures.getTexture(skybox.faces().get(face)); }
        return images;
    }

    public static float partialTick() { return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false); }

    public static float aurora(@Nullable Level level, float partialTick, float sunAngle) {
        SkyLookDef look = ContentFogSampler.look(level);
        if (level == null || look == null || look.aurora() == null) { return 0.0F; }
        float dark = Mth.clamp(1.0F - (Mth.cos(sunAngle) * 2.0F + 0.25F), 0.0F, 1.0F);
        return dark * dark * (1.0F - level.getRainLevel(partialTick));
    }

    public static float rainbow(@Nullable Level level, float partialTick, float sunAngle) {
        SkyLookDef look = ContentFogSampler.look(level);
        if (level == null || look == null || !look.rainbow()) { return 0.0F; }
        return afterRain(level, partialTick) * Mth.clamp(Mth.cos(sunAngle) * 4.0F, 0.0F, 1.0F);
    }

    private static float afterRain(Level level, float partialTick) {
        int id = System.identityHashCode(level);
        if (id != wetWorld) {
            wetWorld = id;
            wetAt = Long.MIN_VALUE;
        }
        float now = level.getRainLevel(1.0F);
        float before = level.getRainLevel(0.0F);
        long tick = level.getGameTime();
        if (now > before) {
            if (now > 0.2F) { wetAt = tick; }
            return 0.0F;
        }
        if (now >= 0.2F && now == before) { wetAt = tick; }
        if (wetAt == Long.MIN_VALUE || tick - wetAt > RAINBOW_TICKS) { return 0.0F; }
        return Math.min(1.0F, (RAINBOW_TICKS - (tick - wetAt)) / RAINBOW_FADE) * (1.0F - level.getRainLevel(partialTick));
    }

    public static float[] box() {
        float[] out = new float[FACES.length * 4 * TEXTURED];
        int at = 0;
        for (float[][] face : FACES) {
            for (int corner = 0; corner < 4; ++corner) { at = put(out, at, face[corner][0] * SIZE, face[corner][1] * SIZE, face[corner][2] * SIZE, CORNER_UV[corner][0], CORNER_UV[corner][1]); }
        }
        return out;
    }

    public static float[] panorama() {
        float[] out = new float[PANORAMA_VERTICES * TEXTURED];
        int at = 0;
        for (int row = 0; row < LATITUDES; ++row) {
            float top = (float) row / LATITUDES;
            float bottom = (float) (row + 1) / LATITUDES;
            for (int column = 0; column < LONGITUDES; ++column) {
                float left = (float) column / LONGITUDES;
                float right = (float) (column + 1) / LONGITUDES;
                at = sphere(out, at, left, top);
                at = sphere(out, at, right, top);
                at = sphere(out, at, right, bottom);
                at = sphere(out, at, left, bottom);
            }
        }
        return out;
    }

    private static int sphere(float[] out, int at, float u, float v) {
        float around = u * Mth.TWO_PI;
        float down = v * Mth.PI;
        float ring = Mth.sin(down) * SIZE;
        return put(out, at, Mth.sin(around) * ring, Mth.cos(down) * SIZE, -Mth.cos(around) * ring, u, v);
    }

    public static float[] aurora(SkyLookDef.Aurora aurora, float strength, float time) {
        float[] out = new float[AURORA_VERTICES * COLORED];
        int at = 0;
        for (int band = 0; band < 2; ++band) {
            for (int step = 0; step < AURORA_STEPS; ++step) {
                at = curtain(out, at, aurora, strength, time, band, step, false);
                at = curtain(out, at, aurora, strength, time, band, step + 1, false);
                at = curtain(out, at, aurora, strength, time, band, step + 1, true);
                at = curtain(out, at, aurora, strength, time, band, step, true);
            }
        }
        return out;
    }

    private static int curtain(float[] out, int at, SkyLookDef.Aurora aurora, float strength, float time, int band, int step, boolean top) {
        float along = (float) step / AURORA_STEPS;
        float around = (float) Math.toRadians(-70.0F + 140.0F * along + 4.0F * Mth.sin(time * 0.05F + step * 0.35F + band * 2.0F));
        float base = 18.0F + band * 6.0F + 3.0F * Mth.sin(time * 0.03F + step * 0.2F + band);
        float height = 18.0F + 10.0F * (0.5F + 0.5F * Mth.sin(time * 0.07F + step * 0.5F + band * 1.3F));
        float up = (float) Math.toRadians(top ? base + height : base);
        float flat = Mth.cos(up) * SIZE;
        float alpha = top ? 0.0F : strength * 0.8F * Mth.sin(along * Mth.PI) * (0.6F + 0.4F * Mth.sin(time * 0.11F + step * 0.9F + band * 0.7F));
        return colored(out, at, Mth.sin(around) * flat, Mth.sin(up) * SIZE, -Mth.cos(around) * flat, top ? aurora.topColor() : aurora.color(), alpha);
    }

    public static float[] bow(float sunAngle, float strength) {
        float[] away = {Mth.sin(sunAngle), -Mth.cos(sunAngle)};
        float[] side = {-Mth.cos(sunAngle), -Mth.sin(sunAngle)};
        float[] out = new float[BOW_VERTICES * COLORED];
        int at = 0;
        for (int ring = 0; ring < BOW_RADII.length - 1; ++ring) {
            for (int step = 0; step < BOW_STEPS; ++step) {
                at = arc(out, at, away, side, ring, step, strength);
                at = arc(out, at, away, side, ring, step + 1, strength);
                at = arc(out, at, away, side, ring + 1, step + 1, strength);
                at = arc(out, at, away, side, ring + 1, step, strength);
            }
        }
        return out;
    }

    private static int arc(float[] out, int at, float[] away, float[] side, int ring, int step, float strength) {
        float radius = (float) Math.toRadians(BOW_RADII[ring]);
        float turn = step * Mth.TWO_PI / BOW_STEPS;
        float reach = Mth.sin(radius);
        float in = Mth.cos(radius);
        float along = Mth.sin(turn) * reach;
        float alpha = ring == 0 || ring == BOW_RADII.length - 1 ? 0.0F : strength * 0.35F;
        return colored(out, at, (away[0] * in + side[0] * along) * SIZE, (away[1] * in + side[1] * along) * SIZE, Mth.cos(turn) * reach * SIZE, BOW_COLORS[ring], alpha);
    }

    private static int put(float[] out, int at, float x, float y, float z, float u, float v) {
        out[at] = x;
        out[at + 1] = y;
        out[at + 2] = z;
        out[at + 3] = u;
        out[at + 4] = v;
        return at + TEXTURED;
    }

    private static int colored(float[] out, int at, float x, float y, float z, int color, float alpha) {
        out[at] = x;
        out[at + 1] = y;
        out[at + 2] = z;
        out[at + 3] = (color >> 16 & 255) / 255.0F;
        out[at + 4] = (color >> 8 & 255) / 255.0F;
        out[at + 5] = (color & 255) / 255.0F;
        out[at + 6] = alpha;
        return at + COLORED;
    }
}
