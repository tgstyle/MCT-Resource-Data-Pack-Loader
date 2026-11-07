package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import net.minecraft.util.Mth;

public final class DarknessDef {
    public static final String FULL = "full";
    public static final String BLOCK = "block";
    public static final String MOON = "moon";
    public static final int PHASES = 5;
    private static final float NIGHT_FACTOR = 0.24F;
    private static final float DAY_SPAN = 0.76F;
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

    public float moonLight(float moonFactor) { return moonLight[Mth.clamp(Math.round(moonFactor * (PHASES - 1)), 0, PHASES - 1)]; }

    public float sky(float own, float moonFactor) {
        float day = Mth.clamp((own - NIGHT_FACTOR) / DAY_SPAN, 0.0F, 1.0F);
        if (FULL.equals(mode)) { return day; }
        float moon = moonLight(moonFactor);
        return day * (1.0F - moon) + moon;
    }
}
