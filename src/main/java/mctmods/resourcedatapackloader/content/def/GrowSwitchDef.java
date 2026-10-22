package mctmods.resourcedatapackloader.content.def;

import java.util.Arrays;

public final class GrowSwitchDef {
    public static final int CROPS = 0;
    public static final int SAPLINGS = 1;
    public static final int ANIMALS = 2;
    public static final int BREEDING = 3;
    public static final int KINDS = 4;
    public static final GrowSwitchDef NONE = new GrowSwitchDef(filled(1.0F));
    private final float[] scales;

    public GrowSwitchDef(float[] scales) { this.scales = scales; }

    public static float[] filled(float value) {
        float[] values = new float[KINDS];
        Arrays.fill(values, value);
        return values;
    }

    public float scale(int kind) { return this.scales[kind]; }
}
