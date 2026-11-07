package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import net.minecraft.util.math.MathHelper;

public final class DarknessDef {
    public static final String FULL = "full";
    public static final String BLOCK = "block";
    public static final String MOON = "moon";
    public static final int PHASES = 5;
    public final String mode;
    public final boolean grayNight;
    public final boolean endGlow;
    public final boolean bareTable;
    public final float gamma;
    private final float[] moonLight;

    public DarknessDef(String mode, float[] moonLight, boolean grayNight, boolean endGlow, boolean bareTable, float gamma) {
        this.mode = mode;
        this.moonLight = moonLight;
        this.grayNight = grayNight;
        this.endGlow = endGlow;
        this.bareTable = bareTable;
        this.gamma = gamma;
    }

    public float moonLight(float moonFactor) { return moonLight[MathHelper.clamp(Math.round(moonFactor * (PHASES - 1)), 0, PHASES - 1)]; }
}
