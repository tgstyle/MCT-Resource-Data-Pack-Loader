package mctmods.resourcedatapackloader.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Blocked {
    private final Map<String, Integer> counts = new LinkedHashMap<>();

    public void count(String owner) { counts.merge(owner, 1, Integer::sum); }

    public void clear() { counts.clear(); }

    public int total() {
        int total = 0;
        for (int value : counts.values()) { total += value; }
        return total;
    }

    public Map<String, Integer> map() { return Collections.unmodifiableMap(counts); }

    public void report(String what) {
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            ContentLog.LOGGER.info("  blocked {} {} from {}", entry.getValue(), what, entry.getKey());
        }
    }
}
