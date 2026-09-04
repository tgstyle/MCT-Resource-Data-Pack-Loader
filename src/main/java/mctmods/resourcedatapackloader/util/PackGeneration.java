package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

public final class PackGeneration {
    private int seen = -1;

    public boolean stale() {
        int now = PackManager.get().getGeneration();
        if (now == seen) { return false; }
        seen = now;
        return true;
    }
}
