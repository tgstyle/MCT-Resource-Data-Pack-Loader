package mctmods.resourcedatapackloader.client.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClientDiet {
    private static Set<String> eaten = Collections.emptySet();
    private static boolean known;

    private ClientDiet() {}

    public static void receive(List<String> kinds) {
        known = true;
        eaten = new HashSet<>(kinds);
    }

    public static boolean waiting() { return !known; }

    public static boolean ate(String kind) { return eaten.contains(kind); }

    public static void clear() {
        known = false;
        eaten = Collections.emptySet();
    }
}
