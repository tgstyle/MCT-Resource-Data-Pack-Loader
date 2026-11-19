package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.core.network.SyncDataMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class SyncMirror {
    private static final Map<String, SyncValue> WORLD = new HashMap<>();
    private static final Map<String, SyncValue> PLAYER = new HashMap<>();
    private static final DataReader READER = new DataReader() {
        @Nullable @Override public SyncValue get(String name) {
            SyncValue own = PLAYER.get(name);
            return own != null ? own : WORLD.get(name);
        }

        @Override public boolean known() { return known; }
    };
    private static boolean known;

    private SyncMirror() {}

    public static DataReader reader() { return READER; }

    public static void apply(boolean full, List<SyncDataMessage.Entry> entries) {
        if (full) {
            WORLD.clear();
            PLAYER.clear();
        }
        known = true;
        for (SyncDataMessage.Entry entry : entries) {
            Map<String, SyncValue> scope = entry.player ? PLAYER : WORLD;
            if (entry.value == null) { scope.remove(entry.name); }
            else { scope.put(entry.name, entry.value.forClient()); }
        }
    }

    public static void reset() {
        known = false;
        WORLD.clear();
        PLAYER.clear();
    }
}
