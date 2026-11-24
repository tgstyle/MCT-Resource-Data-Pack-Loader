package mctmods.resourcedatapackloader.common.util;

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BoundedLru<K, V> extends LinkedHashMap<K, V> {
    @Serial private static final long serialVersionUID = 1L;
    private final int capacity;

    public BoundedLru(int capacity) {
        super(16, 0.75F, true);
        this.capacity = capacity;
    }

    @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > capacity; }
}
