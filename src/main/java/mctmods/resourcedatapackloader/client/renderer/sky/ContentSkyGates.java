package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentSkyGates {
    private static Set<String> OPEN = Set.of();
    private static final Set<String> UNKNOWN = new HashSet<>();
    private static final Map<String, Boolean> MEMO = new HashMap<>();

    private ContentSkyGates() {}

    public static void receive(List<String> ids) {
        Set<String> next = Set.copyOf(ids);
        if (next.equals(OPEN)) { return; }
        OPEN = next;
        MEMO.clear();
        SkyOccluder.invalidate();
    }

    public static void clear() {
        OPEN = Set.of();
        MEMO.clear();
        SkyOccluder.invalidate();
    }

    public static boolean hides(String gate) {
        if (gate.isEmpty()) { return false; }
        return MEMO.computeIfAbsent(gate, ContentSkyGates::decide);
    }

    private static boolean decide(String gate) {
        GateDef def = ContentGates.find(gate);
        if (def == null) {
            if (UNKNOWN.add(gate)) { ContentLog.LOGGER.error("A sky body names gate '{}', which is no gate, so the body shows", gate); }
            return false;
        }
        return !OPEN.contains(def.id());
    }
}
