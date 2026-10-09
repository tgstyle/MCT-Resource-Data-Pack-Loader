package mctmods.resourcedatapackloader.util;

import java.util.Map;
import java.util.function.Supplier;

public final class Raced {
    private Raced() {}

    public static <K, V> V held(Map<K, V> map, K key, Supplier<V> make) {
        V known = map.get(key);
        if (known != null) { return known; }
        V made = make.get();
        V raced = map.putIfAbsent(key, made);
        return raced == null ? made : raced;
    }
}
