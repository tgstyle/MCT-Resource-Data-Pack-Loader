package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import javax.annotation.Nullable;

public enum SkyFlip {
    NONE(false, false),
    HORIZONTAL(true, false),
    VERTICAL(false, true),
    BOTH(true, true);

    private final boolean horizontal;
    private final boolean vertical;

    SkyFlip(boolean horizontal, boolean vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    @Nullable public static SkyFlip parse(String name) {
        for (SkyFlip flip : values()) {
            if (flip != NONE && flip.name().equalsIgnoreCase(name)) { return flip; }
        }
        return null;
    }

    public boolean horizontal() { return horizontal; }

    public boolean vertical() { return vertical; }

    public float u(float u) { return horizontal ? 1.0F - u : u; }

    public float v(float v) { return vertical ? 1.0F - v : v; }

    public double u(double u) { return horizontal ? 1.0D - u : u; }

    public double v(double v) { return vertical ? 1.0D - v : v; }
}
