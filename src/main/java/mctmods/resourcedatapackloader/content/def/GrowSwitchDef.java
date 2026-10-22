package mctmods.resourcedatapackloader.content.def;

public final class GrowSwitchDef {
    public static final int CROPS = 0;
    public static final int SAPLINGS = 1;
    public static final int ANIMALS = 2;
    public static final int BREEDING = 3;
    public static final GrowSwitchDef NONE = new GrowSwitchDef();
    public final float[] scales = {1.0F, 1.0F, 1.0F, 1.0F};

    public float scale(int kind) { return scales[kind]; }
}
