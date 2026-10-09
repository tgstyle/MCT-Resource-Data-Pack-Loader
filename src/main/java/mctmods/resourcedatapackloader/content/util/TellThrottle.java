package mctmods.resourcedatapackloader.content.util;

import java.util.HashMap;
import java.util.Map;

public final class TellThrottle {
    private final long every;
    private final Map<String, Long> told = new HashMap<>();

    public TellThrottle(long every) { this.every = every; }

    public boolean ready(String name, long now) {
        Long last = told.get(name);
        return last == null || now < last || now - last >= every;
    }

    public void mark(String name, long now) { told.put(name, now); }
}
