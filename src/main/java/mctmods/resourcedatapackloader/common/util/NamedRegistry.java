package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class NamedRegistry<T> {
    private final String kind;
    private final String fallback;
    private final Map<String, T> entries = new LinkedHashMap<>();

    public NamedRegistry(String kind, String fallback) {
        this.kind = kind;
        this.fallback = fallback;
    }

    public void register(String name, T value) {
        T previous = entries.put(name.toLowerCase(Locale.ROOT), value);
        if (previous != null) { ContentLog.LOGGER.warn("{} type '{}' was registered twice, the later one wins", kind, name); }
    }

    public static String key(@Nullable String name) { return name == null ? "" : name.toLowerCase(Locale.ROOT); }

    @Nullable public T find(String name) { return entries.get(key(name)); }

    public T get(String name, Object context) {
        T value = find(name);
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Unknown {} type '{}' in {}, treating it as '{}'. Known types are {}", kind.toLowerCase(Locale.ROOT), name, context, fallback, names());
        return entries.get(fallback);
    }

    @Nullable public static <V> V skipping(Map<String, V> map, String kind, String plural, String name, Object context) {
        V value = map.get(name);
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Unknown {} '{}' in {}, the item is skipped. Known {} are {}", kind, name, context, plural, map.keySet());
        return null;
    }

    @Nullable public static <V> V skippingIgnoreCase(Map<String, V> map, String kind, String plural, @Nullable String name, Object context) {
        V value = map.get(key(name));
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Unknown {} '{}' in {}, the item is skipped. Known {} are {}", kind, name, context, plural, map.keySet());
        return null;
    }

    public Set<String> names() { return Collections.unmodifiableSet(entries.keySet()); }
}
