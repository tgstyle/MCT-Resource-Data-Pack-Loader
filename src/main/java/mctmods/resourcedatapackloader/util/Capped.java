package mctmods.resourcedatapackloader.util;

import java.util.Map;
import java.util.function.Supplier;

public final class Capped {
    private Capped() {}

    public static <K, V> V held(Map<K, V> map, int most, String what, K key, Supplier<V> make) {
        V known = map.get(key);
        if (known != null) { return known; }
        return kept(map, most, what, key, make.get());
    }

    public static <K, V> V kept(Map<K, V> map, int most, String what, K key, V made) {
        room(map, most, what);
        V raced = map.putIfAbsent(key, made);
        return raced == null ? made : raced;
    }

    public static void room(Map<?, ?> map, int most, String what) {
        if (map.size() < most) { return; }
        map.clear();
        ContentLog.LOGGER.debug("The {} cache reached {} entries, so it is emptied and each entry is made again from the seed when it is next asked for", what, most);
    }
}
