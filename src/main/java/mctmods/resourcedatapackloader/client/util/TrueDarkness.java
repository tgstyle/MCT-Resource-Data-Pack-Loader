package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class TrueDarkness {
    private static final float NIGHT_FLOOR = 0.2F;
    private static final float DAY_SPAN = 0.8F;
    private static final float VISION_SCALE = 0.9F;
    private static final float VISION_LIFT = 0.1F;
    private static final float GRAY_LIFT = 0.1F;
    private static final int ROW = 16;

    private TrueDarkness() {}

    @Nullable public static DarknessDef of(@Nullable Level level) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null ? null : def.darkness();
    }

    public static float lockedGamma(@Nullable Level level) {
        DarknessDef dark = of(level);
        return dark == null ? Float.NaN : dark.gamma;
    }

    public static float sun(@Nullable DarknessDef dark, Level level, float own) {
        if (dark == null || DarknessDef.BLOCK.equals(dark.mode)) { return own; }
        float day = Mth.clamp((own - NIGHT_FLOOR) / DAY_SPAN, 0.0F, 1.0F);
        if (DarknessDef.FULL.equals(dark.mode)) { return day; }
        float moon = dark.moonLight(level.getMoonBrightness());
        return day * (1.0F - moon) + moon;
    }

    public static float scale(@Nullable DarknessDef dark, float own) { return dark == null ? own : 1.0F; }

    public static float floor(@Nullable DarknessDef dark, float own) { return dark == null ? own : 0.0F; }

    public static boolean endLight(@Nullable DarknessDef dark, boolean own) { return own && (dark == null || dark.endGlow); }

    public static float gamma(@Nullable DarknessDef dark, float own) { return dark == null || Float.isNaN(dark.gamma) ? own : dark.gamma; }

    public static float table(@Nullable DarknessDef dark, float ambient, float own) { return dark == null || !dark.bareTable || ambient <= 0.0F || ambient >= 1.0F ? own : (own - ambient) / (1.0F - ambient); }

    public static void nightVision(@Nullable DarknessDef dark, Vector3f colors) {
        if (dark == null || unaided()) { return; }
        colors.set(lift(colors.x()), lift(colors.y()), lift(colors.z()));
    }

    public static void gray(@Nullable DarknessDef dark, NativeImage lightmap) {
        if (dark == null || keepsColor(dark)) { return; }
        float lift = DarknessDef.BLOCK.equals(dark.mode) ? GRAY_LIFT : 0.0F;
        for (int sky = 1; sky < ROW; sky++) {
            for (int block = 0; block < ROW; block++) {
                int level = (int) (Math.min(1.0F, (lightmap.getPixelRGBA(block, sky) & 0xFF) / 255.0F + lift) * 255.0F + 0.5F);
                lightmap.setPixelRGBA(block, sky, 0xFF000000 | level << 16 | level << 8 | level);
            }
        }
    }

    private static float lift(float channel) { return Mth.clamp(channel, 0.0F, 1.0F) * VISION_SCALE + VISION_LIFT; }

    private static boolean keepsColor(DarknessDef dark) { return DarknessDef.FULL.equals(dark.mode) ? unaided() : !dark.grayNight; }

    private static boolean unaided() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null || !player.hasEffect(MobEffects.NIGHT_VISION) && !(player.getWaterVision() > 0.0F && player.hasEffect(MobEffects.CONDUIT_POWER));
    }
}
