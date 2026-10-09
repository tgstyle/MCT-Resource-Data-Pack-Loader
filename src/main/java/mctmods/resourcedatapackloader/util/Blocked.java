package mctmods.resourcedatapackloader.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Blocked {
    private final Map<String, Integer> counts = new LinkedHashMap<>();

    public synchronized void count(String owner) { counts.merge(owner, 1, Integer::sum); }

    public synchronized void clear() { counts.clear(); }

    public synchronized int total() {
        int total = 0;
        for (int value : counts.values()) { total += value; }
        return total;
    }

    public synchronized Map<String, Integer> map() { return Map.copyOf(counts); }

    public synchronized void report(String what) { for (Map.Entry<String, Integer> entry : counts.entrySet()) { ContentLog.LOGGER.debug("  blocked {} {} from {}", entry.getValue(), what, entry.getKey()); } }
}
