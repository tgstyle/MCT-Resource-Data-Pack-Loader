package mctmods.resourcedatapackloader.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class Lists {
    private Lists() {}

    @Nullable public static <T> T first(Iterable<T> items, Predicate<T> test) {
        for (T item : items) {
            if (test.test(item)) { return item; }
        }
        return null;
    }

    public static <T, R> List<R> collect(Iterable<T> items, Predicate<T> keep, Function<T, R> map) {
        List<R> found = null;
        for (T item : items) {
            if (!keep.test(item)) { continue; }
            if (found == null) { found = new ArrayList<>(); }
            found.add(map.apply(item));
        }
        return found == null ? Collections.emptyList() : found;
    }
}
