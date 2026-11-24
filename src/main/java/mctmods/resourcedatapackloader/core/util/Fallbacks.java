package mctmods.resourcedatapackloader.core.util;

import javax.annotation.Nullable;

public final class Fallbacks {
    private Fallbacks() {}

    public static <T> T orDefault(@Nullable T value, T fallback) { return value == null ? fallback : value; }
}
