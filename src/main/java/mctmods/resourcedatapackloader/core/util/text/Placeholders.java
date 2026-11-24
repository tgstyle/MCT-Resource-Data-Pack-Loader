package mctmods.resourcedatapackloader.core.util.text;

import javax.annotation.Nullable;

public final class Placeholders {
    private Placeholders() {}

    public static String orDash(@Nullable Object value) { return value == null ? "-" : String.valueOf(value); }
}
