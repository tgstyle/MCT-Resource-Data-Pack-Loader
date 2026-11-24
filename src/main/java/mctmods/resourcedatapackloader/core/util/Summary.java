package mctmods.resourcedatapackloader.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Summary {
    private static final Map<String, String> LAST = new ConcurrentHashMap<>();

    private Summary() {}

    public static void loaded(String key, int count, String noun) {
        if (count > 0) { info(key, "Loaded " + count + " " + noun); }
    }

    public static void info(String key, String message) {
        if (message.equals(LAST.put(key, message))) { return; }
        ContentLog.LOGGER.info(message);
    }

    public static void packAsks(String key, String done) { info(key, done + ", which is what a pack asks for"); }
}
