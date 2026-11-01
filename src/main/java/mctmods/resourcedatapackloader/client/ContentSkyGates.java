package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentSkyGates {
    private static Set<String> open = Set.of();
    private static final Set<String> MISSING = new HashSet<>();
    private static final Map<String, Boolean> MEMO = new HashMap<>();

    private ContentSkyGates() {}

    public static void receive(List<String> ids) {
        Set<String> next = Set.copyOf(ids);
        if (next.equals(open)) { return; }
        open = next;
        MEMO.clear();
        SkyOccluder.invalidate();
    }

    public static void clear() {
        open = Set.of();
        MEMO.clear();
        SkyOccluder.invalidate();
    }

    public static boolean hides(String gate) {
        if (gate == null) { return false; }
        return MEMO.computeIfAbsent(gate, ContentSkyGates::decide);
    }

    private static boolean decide(String gate) {
        GateDef def = ContentGates.find(gate);
        if (def == null) {
            if (MISSING.add(gate)) { ContentLog.LOGGER.error("A sky body names the gate '{}' that no gate has, showing the body", gate); }
            return false;
        }
        return !open.contains(def.id());
    }
}
