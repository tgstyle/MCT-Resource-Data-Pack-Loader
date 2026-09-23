package mctmods.resourcedatapackloader.content.worldgen;

final class CityGradeBore {
    private CityGradeBore() {}

    static int[] cutLine(int[] profile, int from, int to, boolean openLow, boolean openHigh) {
        int span = to - from + 1;
        int[] leftFloor = new int[span];
        int running = from > 0 ? profile[from - 1] : Integer.MAX_VALUE;
        for (int at = from; at <= to; at++) {
            running = Math.min(running, profile[at]);
            leftFloor[at - from] = running;
        }
        int[] rightFloor = new int[span];
        running = to + 1 < profile.length ? profile[to + 1] : Integer.MAX_VALUE;
        for (int at = to; at >= from; at--) {
            running = Math.min(running, profile[at]);
            rightFloor[at - from] = running;
        }
        int[] cut = new int[span];
        for (int at = 0; at < span; at++) {
            if (openHigh && !openLow) { cut[at] = leftFloor[at]; }
            else if (openLow && !openHigh) { cut[at] = rightFloor[at]; }
            else { cut[at] = Math.max(leftFloor[at], rightFloor[at]); }
        }
        return cut;
    }

    static int deepest(int[] ground, int[] cut, int from, int low, int high) {
        int most = 0;
        for (int at = low; at < high; at++) { most = Math.max(most, ground[at] - cut[at - from]); }
        return most;
    }

    static int buried(int[] ground, int[] cut, int from, int low, int high, int depth) {
        int longest = 0;
        int run = 0;
        for (int at = low; at < high; at++) {
            run = ground[at] - cut[at - from] >= depth ? run + 1 : 0;
            longest = Math.max(longest, run);
        }
        return longest;
    }

    static boolean roofedAt(int[] profile, int[] ground, boolean[] bridged, int at, int depth) {
        if (!buriedAt(profile, ground, bridged, at, 1)) { return false; }
        if (buriedAt(profile, ground, bridged, at, depth)) { return true; }
        return buriedWithin(profile, ground, bridged, at, -1, depth) && buriedWithin(profile, ground, bridged, at, 1, depth);
    }

    static boolean buriedWithin(int[] profile, int[] ground, boolean[] bridged, int at, int step, int depth) {
        for (int near = at + step, seen = 1; near >= 0 && near < profile.length && seen <= CityGrade.TUNNEL_LEAST; near += step, seen++) {
            if (!buriedAt(profile, ground, bridged, near, 1)) { return false; }
            if (buriedAt(profile, ground, bridged, near, depth)) { return true; }
        }
        return false;
    }

    static int[] openEnds(int[] ground, int start, CityPlan.Line line) {
        int[] held = ground.clone();
        for (int at = 0; at < held.length; at++) {
            int row = start + at;
            if (row < line.from() && line.endsLow() != CityPlan.End.MET || row > line.to() && line.endsHigh() != CityPlan.End.MET) { held[at] = Integer.MIN_VALUE; }
        }
        return held;
    }

    static boolean buriedAt(int[] profile, int[] ground, boolean[] bridged, int at, int depth) { return !bridged[at] && ground[at] != Integer.MIN_VALUE && ground[at] - profile[at] >= depth; }
}
