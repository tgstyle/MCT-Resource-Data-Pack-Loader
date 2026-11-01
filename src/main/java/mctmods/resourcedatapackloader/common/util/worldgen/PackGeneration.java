package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;

public final class PackGeneration {
    private int seen = -1;

    public boolean stale() {
        int now = PackManager.get().getGeneration();
        if (now == seen) { return false; }
        seen = now;
        return true;
    }
}
