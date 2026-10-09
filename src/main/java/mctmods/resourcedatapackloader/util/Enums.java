package mctmods.resourcedatapackloader.util;

import javax.annotation.Nullable;

public final class Enums {
    private Enums() {}

    @Nullable public static <E extends Enum<E>> E byName(Class<E> type, String name) {
        String wanted = name.trim();
        for (E value : type.getEnumConstants()) {
            if (value.name().equalsIgnoreCase(wanted)) { return value; }
        }
        return null;
    }
}
