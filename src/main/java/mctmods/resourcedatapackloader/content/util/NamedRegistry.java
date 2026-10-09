package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

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

    @Nullable public T find(String name) { return entries.get(name == null ? "" : name.toLowerCase(Locale.ROOT)); }

    public T get(String name, Object context) {
        T value = find(name);
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Unknown {} type '{}' in {}, treating it as '{}'. Known types are {}", kind.toLowerCase(Locale.ROOT), name, context, fallback, names());
        return entries.get(fallback);
    }

    public Set<String> names() { return Collections.unmodifiableSet(entries.keySet()); }
}
