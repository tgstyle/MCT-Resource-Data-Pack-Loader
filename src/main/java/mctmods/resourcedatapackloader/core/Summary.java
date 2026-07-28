package mctmods.resourcedatapackloader.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Summary {
    private static final Map<String, String> LAST = new ConcurrentHashMap<>();

    private Summary() {}

    public static void info(String key, String message) {
        if (message.equals(LAST.put(key, message))) { return; }
        MCTMixin.LOGGER.info(message);
    }
}
