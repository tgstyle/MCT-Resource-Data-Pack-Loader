package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.Arrays;

public final class CityGrade {
    private static final int TUNNEL_LEAST = 12;

    private CityGrade() {}

    public static void flatRuns(int[] profile, int start, int run) {
        if (run <= 1) { return; }
        int at = 0;
        while (at < profile.length) {
            int grid = (start + at) - Math.floorMod(start + at, run);
            int last = Math.min(profile.length - 1, grid + run - 1 - start);
            int first = Math.max(0, grid - start);
            int[] taken = Arrays.copyOfRange(profile, first, last + 1);
            Arrays.sort(taken);
            int level = taken[taken.length / 2];
            for (int i = first; i <= last; i++) { profile[i] = level; }
            at = last + 1;
        }
    }

    public static void pin(int[] profile, boolean[] held, int start, int from, int to, int level) {
        for (int row = Math.max(from, start); row <= Math.min(to, start + profile.length - 1); row++) {
            profile[row - start] = level;
            held[row - start] = true;
        }
    }

    public static void reconcile(int[] profile, boolean[] held) {
        int rows = profile.length;
        int[][] edges = bounds(profile, held);
        int[] low = edges[0];
        int[] high = edges[1];
        for (int i = 0; i < rows; i++) {
            if (held[i]) { continue; }
            int least = low[i] == Integer.MIN_VALUE ? profile[i] : low[i];
            int most = high[i] == Integer.MAX_VALUE ? profile[i] : high[i];
            if (least > most) {
                profile[i] = (least + most) / 2;
                continue;
            }
            profile[i] = Math.max(least, Math.min(most, profile[i]));
        }
    }

    public static void smooth(int[] profile, boolean[] held) {
        for (int i = 1; i < profile.length; i++) {
            if (held[i]) { continue; }
            if (profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; }
            else if (profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; }
        }
        for (int i = profile.length - 2; i >= 0; i--) {
            if (held[i]) { continue; }
            if (profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; }
            else if (profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; }
        }
    }

    public static boolean[] buriedRuns(int[] profile, int[] ground, boolean[] held, boolean[] bridged, int depth) {
        boolean[] bored = new boolean[profile.length];
        if (depth <= 0) { return bored; }
        int from = 0;
        while (from < profile.length) {
            if (held[from] || bridged[from] || ground[from] - profile[from] < depth) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < profile.length && !held[to + 1] && !bridged[to + 1] && ground[to + 1] - profile[to + 1] >= depth) { to++; }
            if (to - from + 1 >= TUNNEL_LEAST) {
                for (int at = from; at <= to; at++) { bored[at] = true; }
            }
            from = to + 1;
        }
        return bored;
    }

    public static boolean[] bore(int[] profile, int[] ground, boolean[] held, boolean[] bridged, int depth) {
        int rows = profile.length;
        boolean[] bored = new boolean[rows];
        if (depth <= 0) { return bored; }
        int at = 0;
        while (at < rows) {
            if (held[at] || bridged[at]) {
                at++;
                continue;
            }
            int end = at;
            while (end + 1 < rows && !held[end + 1] && !bridged[end + 1]) { end++; }
            int[] cut = cutLine(profile, at, end);
            int high = -1;
            for (int row = at; row <= end + 1; row++) {
                boolean raised = row <= end && cut[row - at] < profile[row];
                if (raised && high < 0) { high = row; }
                if (raised || high < 0) { continue; }
                int longest = buried(ground, cut, at, high, row, depth);
                if (longest >= TUNNEL_LEAST) {
                    for (int inside = high; inside < row; inside++) {
                        profile[inside] = cut[inside - at];
                        bored[inside] = true;
                    }
                }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A street rises over rows {} to {}, its cut line held at y {} with the ground standing at most {} over it and buried {} row(s) at {} block(s) or more, wanting {}", high, row - 1, cut[high - at], deepest(ground, cut, at, high, row), longest, depth, TUNNEL_LEAST); }
                high = -1;
            }
            at = end + 1;
        }
        return bored;
    }

    private static int[] cutLine(int[] profile, int from, int to) {
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
        for (int at = 0; at < span; at++) { cut[at] = Math.max(leftFloor[at], rightFloor[at]); }
        return cut;
    }

    private static int deepest(int[] ground, int[] cut, int from, int low, int high) {
        int most = 0;
        for (int at = low; at < high; at++) { most = Math.max(most, ground[at] - cut[at - from]); }
        return most;
    }

    private static int buried(int[] ground, int[] cut, int from, int low, int high, int depth) {
        int longest = 0;
        int run = 0;
        for (int at = low; at < high; at++) {
            run = ground[at] - cut[at - from] >= depth ? run + 1 : 0;
            longest = Math.max(longest, run);
        }
        return longest;
    }

    public static void climb(int[] profile, boolean[] held, int rows) {
        if (rows <= 1) {
            smooth(profile, held);
            return;
        }
        int[] wanted = profile.clone();
        int[] sorted = profile.clone();
        Arrays.sort(sorted);
        int middle = sorted[sorted.length / 2];
        int anchor = 0;
        for (int at = 1; at < profile.length; at++) {
            if (Math.abs(profile[at] - middle) < Math.abs(profile[anchor] - middle)) { anchor = at; }
        }
        for (int at = 0; at < profile.length; at++) {
            if (!held[at]) { continue; }
            anchor = at;
            break;
        }
        reach(profile, wanted, held, rows, anchor, 1);
        reach(profile, wanted, held, rows, anchor, -1);
    }

    private static void reach(int[] profile, int[] wanted, boolean[] held, int rows, int anchor, int step) {
        int since = rows;
        for (int at = anchor + step; at >= 0 && at < profile.length; at += step) {
            if (held[at]) {
                since = rows;
                continue;
            }
            int prev = profile[at - step];
            if (wanted[at] == prev) {
                profile[at] = prev;
                since++;
                continue;
            }
            if (since >= rows) {
                profile[at] = prev + (wanted[at] > prev ? 1 : -1);
                since = 1;
            }
            else {
                profile[at] = prev;
                since++;
            }
        }
    }

    private static int[][] bounds(int[] profile, boolean[] held) {
        int rows = profile.length;
        int[] low = new int[rows];
        int[] high = new int[rows];
        Arrays.fill(low, Integer.MIN_VALUE);
        Arrays.fill(high, Integer.MAX_VALUE);
        for (int i = 0; i < rows; i++) {
            if (!held[i]) { continue; }
            for (int at = 0; at < rows; at++) {
                int reach = Math.abs(at - i);
                low[at] = Math.max(low[at], profile[i] - reach);
                high[at] = Math.min(high[at], profile[i] + reach);
            }
        }
        return new int[][] { low, high };
    }
}
