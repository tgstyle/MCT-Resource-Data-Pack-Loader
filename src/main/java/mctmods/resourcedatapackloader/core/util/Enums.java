package mctmods.resourcedatapackloader.core.util;

import java.util.Arrays;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class Enums {
    private Enums() {}

    @Nullable public static <E extends Enum<E>> E byName(Class<E> type, String name) {
        return byName(Arrays.asList(type.getEnumConstants()), Enum::name, name);
    }

    @Nullable public static <T> T byName(Iterable<T> values, Function<T, String> name, String wanted) {
        String trimmed = wanted.trim();
        for (T value : values) {
            if (name.apply(value).equalsIgnoreCase(trimmed)) { return value; }
        }
        return null;
    }
}
