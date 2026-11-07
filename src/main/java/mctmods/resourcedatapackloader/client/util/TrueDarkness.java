package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class TrueDarkness {
    private static final float NIGHT_FLOOR = 0.2F;
    private static final float DAY_SPAN = 0.8F;
    private static final float VISION_SCALE = 0.9F;
    private static final float VISION_LIFT = 0.1F;
    private static final float GRAY_LIFT = 0.1F;
    private static final int END = 1;
    private static final int NOT_END = 0;
    private static final int ROW = 16;
    private static final float[] BARE = new float[ROW];
    @Nullable private static float[] source;
    private static float sourceFloor;

    private TrueDarkness() {}

    @Nullable public static DarknessDef of(@Nullable World world) {
        DimensionDef def = ContentDimensions.of(world);
        return def == null ? null : def.darkness;
    }

    public static float lockedGamma(@Nullable World world) {
        DarknessDef dark = of(world);
        return dark == null ? Float.NaN : dark.gamma;
    }

    public static float sun(@Nullable DarknessDef dark, World world, float own) {
        if (dark == null || DarknessDef.BLOCK.equals(dark.mode)) { return own; }
        float day = MathHelper.clamp((own - NIGHT_FLOOR) / DAY_SPAN, 0.0F, 1.0F);
        if (DarknessDef.FULL.equals(dark.mode)) { return day; }
        float moon = dark.moonLight(world.getCurrentMoonPhaseFactor());
        return day * (1.0F - moon) + moon;
    }

    public static float scale(@Nullable DarknessDef dark, float own) { return dark == null ? own : 1.0F; }

    public static float floor(@Nullable DarknessDef dark, float own) { return dark == null ? own : 0.0F; }

    public static int endLight(@Nullable DarknessDef dark, int own) { return dark != null && !dark.endGlow && own == END ? NOT_END : own; }

    public static float gamma(@Nullable DarknessDef dark, float own) { return dark == null || Float.isNaN(dark.gamma) ? own : dark.gamma; }

    public static float[] table(@Nullable DarknessDef dark, float[] own) {
        if (dark == null || !dark.bareTable || own.length != ROW || own[0] <= 0.0F || own[0] >= 1.0F) { return own; }
        if (own != source || own[0] != sourceFloor) {
            source = own;
            sourceFloor = own[0];
            for (int level = 0; level < ROW; level++) { BARE[level] = (own[level] - sourceFloor) / (1.0F - sourceFloor); }
        }
        return BARE;
    }

    public static void nightVision(@Nullable DarknessDef dark, float[] colors) {
        if (dark == null || unaided()) { return; }
        for (int channel = 0; channel < colors.length; channel++) { colors[channel] = MathHelper.clamp(colors[channel], 0.0F, 1.0F) * VISION_SCALE + VISION_LIFT; }
    }

    public static void gray(@Nullable DarknessDef dark, int[] lightmap) {
        if (dark == null || keepsColor(dark)) { return; }
        float lift = DarknessDef.BLOCK.equals(dark.mode) ? GRAY_LIFT : 0.0F;
        for (int entry = ROW; entry < lightmap.length; entry++) {
            int level = (int) (Math.min(1.0F, (lightmap[entry] >> 16 & 0xFF) / 255.0F + lift) * 255.0F + 0.5F);
            lightmap[entry] = 0xFF000000 | level << 16 | level << 8 | level;
        }
    }

    private static boolean keepsColor(DarknessDef dark) { return DarknessDef.FULL.equals(dark.mode) ? unaided() : !dark.grayNight; }

    private static boolean unaided() { return !Minecraft.getMinecraft().player.isPotionActive(MobEffects.NIGHT_VISION); }
}
