package mctmods.resourcedatapackloader.core.util;

import java.util.Map;

public final class Maps {
    private Maps() {}

    public static void bound(Map<?, ?> map, int limit) {
        if (map.size() > limit) { map.clear(); }
    }
}
