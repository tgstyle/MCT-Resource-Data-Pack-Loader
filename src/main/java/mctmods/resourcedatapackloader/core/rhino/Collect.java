package mctmods.resourcedatapackloader.core.rhino;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Collect {
    private Collect() {}

    @SafeVarargs
    public static <E> List<E> listOf(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        for (E element : elements) {
            list.add(Objects.requireNonNull(element));
        }
        return Collections.unmodifiableList(list);
    }

    public static <K, V> Map<K, V> mapOf() {
        return Collections.emptyMap();
    }

    public static <K, V> Map<K, V> mapOf(K key, V value) {
        return Collections.singletonMap(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    public static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) {
        HashMap<K, V> copy = new HashMap<>();
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            copy.put(Objects.requireNonNull(e.getKey()), Objects.requireNonNull(e.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(
                Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
        HashMap<K, V> map = new HashMap<>();
        for (Map.Entry<? extends K, ? extends V> e : entries) {
            if (map.put(e.getKey(), e.getValue()) != null) {
                throw new IllegalArgumentException("duplicate key: " + e.getKey());
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
