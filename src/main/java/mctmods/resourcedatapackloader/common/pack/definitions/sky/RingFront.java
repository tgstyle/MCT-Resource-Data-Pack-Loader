package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import javax.annotation.Nullable;

public enum RingFront {
    BOTTOM, TOP, LEFT, RIGHT;

    @Nullable public static RingFront parse(String value) {
        for (RingFront front : values()) {
            if (front.name().equalsIgnoreCase(value.trim())) { return front; }
        }
        return null;
    }

    public float[] half(boolean inFront) {
        boolean low = (this == TOP || this == LEFT) == inFront;
        float from = low ? 0.0F : 0.5F;
        float to = low ? 0.5F : 1.0F;
        return this == TOP || this == BOTTOM ? new float[] {0.0F, from, 1.0F, to} : new float[] {from, 0.0F, to, 1.0F};
    }
}
