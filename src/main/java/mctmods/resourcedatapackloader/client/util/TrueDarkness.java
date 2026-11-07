package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class TrueDarkness {
    private static final float GRAY_LIFT = 0.1F;
    private static final float NO_GRAY = -1.0F;
    private static final Vector3fc BLACK = new Vector3f();

    private TrueDarkness() {}

    @Nullable public static DarknessDef of(@Nullable Level level) {
        DimensionDef def = ContentDimensions.def(level);
        return def == null ? null : def.darkness();
    }

    public static float lockedGamma(@Nullable Level level) {
        DarknessDef dark = of(level);
        return dark == null ? Float.NaN : dark.gamma;
    }

    public static float gamma(@Nullable DarknessDef dark, float own) { return dark == null || Float.isNaN(dark.gamma) ? own : dark.gamma; }

    public static void ambient(LightmapRenderState state, ClientLevel level) {
        DarknessDef dark = of(level);
        if (dark != null && darkensAmbient(dark, level)) { state.ambientColor = BLACK; }
    }

    public static float grayLift() {
        Minecraft mc = Minecraft.getInstance();
        DarknessDef dark = of(mc.level);
        if (dark == null || mc.player == null || keepsColor(dark, mc.player)) { return NO_GRAY; }
        return DarknessDef.BLOCK.equals(dark.mode) ? GRAY_LIFT : 0.0F;
    }

    public static float ambientBlend() {
        DimensionDef def = ContentDimensions.def(Minecraft.getInstance().level);
        return def == null || !def.gives("sky.ambientLight") ? 0.0F : def.ambientLight();
    }

    private static boolean darkensAmbient(DarknessDef dark, ClientLevel level) { return level.dimensionTypeRegistration().is(BuiltinDimensionTypes.END) ? !dark.endGlow && dark.bareTable : dark.bareTable; }

    private static boolean keepsColor(DarknessDef dark, LocalPlayer player) { return DarknessDef.FULL.equals(dark.mode) ? !aided(player) : !dark.grayNight; }

    private static boolean aided(LocalPlayer player) { return player.hasEffect(MobEffects.NIGHT_VISION) || player.getWaterVision() > 0.0F && player.hasEffect(MobEffects.CONDUIT_POWER); }
}
