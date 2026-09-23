package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

public final class CityGrade {
    static final int TUNNEL_LEAST = 12;
    private static final double UNHELD = 1.0e6D;
    static final int CAP = 2;
    private static final int CAP_PASSES = 4;
    private static final int CUT_REACH = 2;
    private static final int SPAN_MOST = 12;
    private static final int DECK_LEAST = 4;
    private static final int ROAD_GAP = 12;

    private CityGrade() {}

    public static void flatRuns(int[] profile, int start, IntUnaryOperator flat) {
        for (int at = 0; at < profile.length; at++) {
            int level = flat.applyAsInt(start + at);
            if (level != Integer.MIN_VALUE) { profile[at] = level; }
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

    public static void smoothRoad(int[] profile) {
        int rows = profile.length;
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; } }
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; } }
        for (int i = 1; i < rows - 1; i++) {
            if (!joined(profile, i) || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; }
        }
        int gap = 0;
        while (gap < rows) {
            if (profile[gap] != Integer.MIN_VALUE) {
                gap++;
                continue;
            }
            int gapEnd = gap;
            while (gapEnd < rows && profile[gapEnd] == Integer.MIN_VALUE) { gapEnd++; }
            if (gap > 0 && gapEnd < rows && gapEnd - gap <= ROAD_GAP) {
                int fromY = profile[gap - 1];
                int toY = profile[gapEnd];
                for (int row = gap; row < gapEnd; row++) { profile[row] = fromY + (toY - fromY) * (row - gap + 1) / (gapEnd - gap + 1); }
            }
            gap = gapEnd + 1;
        }
    }

    private static boolean joined(int[] profile, int i) { return profile[i] != Integer.MIN_VALUE && profile[i - 1] != Integer.MIN_VALUE; }

    public static int carried(int[] profile, int at) {
        int before = Integer.MIN_VALUE;
        for (int back = at - 1; back >= 0; back--) {
            if (profile[back] == Integer.MIN_VALUE) { continue; }
            before = profile[back];
            break;
        }
        int after = Integer.MIN_VALUE;
        for (int on = at + 1; on < profile.length; on++) {
            if (profile[on] == Integer.MIN_VALUE) { continue; }
            after = profile[on];
            break;
        }
        if (before == Integer.MIN_VALUE) { return after; }
        if (after == Integer.MIN_VALUE) { return before; }
        return Math.max(before, after);
    }

    public static void trimPeaks(int[] profile, boolean[] held, boolean[] bridged) {
        for (int at = 1; at < profile.length - 1; at++) {
            if (held[at] || bridged[at]) { continue; }
            int crest = Math.max(profile[at - 1], profile[at + 1]);
            if (profile[at] > crest) { profile[at] = crest; }
        }
    }

    public static int capEmbankment(int[] profile, int[] underfoot, boolean[] held, boolean[] open) {
        int capped = 0;
        for (int pass = 0; pass < CAP_PASSES; pass++) {
            int clamped = 0;
            for (int at = 0; at < profile.length; at++) {
                if (held[at] || underfoot[at] == Integer.MIN_VALUE || profile[at] <= underfoot[at] + CAP) { continue; }
                profile[at] = underfoot[at] + CAP;
                held[at] = true;
                clamped++;
            }
            if (clamped == 0) { break; }
            capped += clamped;
            settle(profile, held, open);
        }
        return capped;
    }

    public static boolean[] openRows(boolean[] under, boolean[] valued) {
        int rows = under.length;
        boolean[] open = new boolean[rows];
        int at = 0;
        while (at < rows) {
            if (!under[at]) {
                at++;
                continue;
            }
            int end = at;
            while (end + 1 < rows && under[end + 1]) { end++; }
            boolean spanned = at > 0 && end < rows - 1 && end - at < SPAN_MOST;
            for (int row = at; row <= end; row++) { open[row] = !spanned && !valued[row]; }
            at = end + 1;
        }
        return open;
    }

    public static void settle(int[] profile, boolean[] held, boolean[] open) {
        int rows = profile.length;
        flattenBumps(profile, held, open);
        int i = 0;
        while (i < rows) {
            if (open[i]) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && !open[end + 1]) { end++; }
            evenOut(profile, held, i, end, 1);
            evenOut(profile, held, i, end, -1);
            i = end + 1;
        }
        rein(profile, held, open, 1);
        rein(profile, held, open, -1);
        flattenBumps(profile, held, open);
    }

    private static void evenOut(int[] profile, boolean[] held, int from, int to, int sign) {
        int[] leftMost = new int[to - from + 1];
        int running = Integer.MIN_VALUE;
        for (int k = from; k <= to; k++) {
            running = Math.max(running, sign * profile[k]);
            leftMost[k - from] = running;
        }
        int[] rightMost = new int[to - from + 1];
        running = Integer.MIN_VALUE;
        for (int k = to; k >= from; k--) {
            running = Math.max(running, sign * profile[k]);
            rightMost[k - from] = running;
        }
        int low = -1;
        for (int k = from; k <= to + 1; k++) {
            boolean sunken = k <= to && Math.min(leftMost[k - from], rightMost[k - from]) > sign * profile[k];
            if (sunken && low < 0) { low = k; }
            if (sunken || low < 0) { continue; }
            if (k - low <= SPAN_MOST) {
                for (int at = low; at < k; at++) {
                    int level = sign * Math.min(leftMost[at - from], rightMost[at - from]);
                    if (!held[at] && sign * level > sign * profile[at]) { profile[at] = level; }
                }
            }
            low = -1;
        }
    }

    private static void rein(int[] profile, boolean[] held, boolean[] open, int sign) {
        for (int i = 1; i < profile.length; i++) { if (!held[i] && linked(open, i) && sign * (profile[i] - profile[i - 1]) > 1) { profile[i] = profile[i - 1] + sign; } }
        for (int i = profile.length - 2; i >= 0; i--) { if (!held[i] && linked(open, i + 1) && sign * (profile[i] - profile[i + 1]) > 1) { profile[i] = profile[i + 1] + sign; } }
    }

    private static void flattenBumps(int[] profile, boolean[] held, boolean[] open) {
        for (int i = 1; i < profile.length - 1; i++) { if (!held[i] && linked(open, i) && !open[i + 1] && profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; } }
    }

    private static boolean linked(boolean[] open, int i) { return !open[i] && !open[i - 1]; }

    public static int deckDrops(int[] profile, int[] underfoot, boolean[] bridged, boolean[] keep, int drop) {
        if (drop <= 0) { return 0; }
        int rows = profile.length;
        boolean[] own = new boolean[rows];
        for (int at = 0; at < rows; at++) {
            if (keep[at] || bridged[at] || underfoot[at] == Integer.MIN_VALUE || profile[at] <= underfoot[at] + drop) { continue; }
            bridged[at] = true;
            own[at] = true;
        }
        int decked = 0;
        int at = 0;
        while (at < rows) {
            if (!bridged[at]) {
                at++;
                continue;
            }
            int end = at;
            while (end + 1 < rows && bridged[end + 1]) { end++; }
            int mine = 0;
            for (int row = at; row <= end; row++) { mine += own[row] ? 1 : 0; }
            if (mine > 0 && end - at + 1 < DECK_LEAST) {
                for (int row = at; row <= end; row++) { bridged[row] &= !own[row]; }
            }
            else { decked += mine; }
            at = end + 1;
        }
        return decked;
    }

    public static int limitCut(int[] profile, int[] underfoot, boolean[] held) {
        int limited = 0;
        for (int at = 0; at < profile.length; at++) {
            if (held[at] || underfoot[at] == Integer.MIN_VALUE) { continue; }
            int floor = underfoot[at];
            for (int near = Math.max(0, at - CUT_REACH); near <= Math.min(profile.length - 1, at + CUT_REACH); near++) {
                if (underfoot[near] != Integer.MIN_VALUE) { floor = Math.min(floor, underfoot[near]); }
            }
            if (profile[at] >= floor - CAP) { continue; }
            profile[at] = floor - CAP;
            limited++;
        }
        return limited;
    }

    public static void easeCaps(int[] profile, boolean[] capped, boolean[] held, boolean[] bridged) {
        for (int round = 0; round < CAP_PASSES; round++) {
            for (int at = 1; at < profile.length; at++) {
                if (!capped[at] && !bridged[at - 1] && profile[at] > profile[at - 1] + 1) { profile[at] = profile[at - 1] + 1; }
            }
            for (int at = profile.length - 2; at >= 0; at--) {
                if (!capped[at] && !bridged[at + 1] && profile[at] > profile[at + 1] + 1) { profile[at] = profile[at + 1] + 1; }
            }
            for (int at = 1; at < profile.length; at++) {
                if (!held[at] && !bridged[at - 1] && profile[at] < profile[at - 1] - 1) { profile[at] = profile[at - 1] - 1; }
            }
            for (int at = profile.length - 2; at >= 0; at--) {
                if (!held[at] && !bridged[at + 1] && profile[at] < profile[at + 1] - 1) { profile[at] = profile[at + 1] - 1; }
            }
        }
    }

    public static int fillDips(int[] profile, boolean[] keep) {
        int rows = profile.length;
        int[] leftMax = new int[rows];
        int running = Integer.MIN_VALUE;
        for (int at = 0; at < rows; at++) {
            running = keep[at] ? profile[at] : Math.max(running, profile[at]);
            leftMax[at] = running;
        }
        int[] rightMax = new int[rows];
        running = Integer.MIN_VALUE;
        for (int at = rows - 1; at >= 0; at--) {
            running = keep[at] ? profile[at] : Math.max(running, profile[at]);
            rightMax[at] = running;
        }
        int lifted = 0;
        for (int at = 0; at < rows; at++) {
            if (keep[at]) { continue; }
            int fill = Math.min(leftMax[at], rightMax[at]);
            if (fill <= profile[at]) { continue; }
            profile[at] = fill;
            lifted++;
        }
        return lifted;
    }

    public static void rampSteps(int[] profile, boolean[] held, boolean[] keep) {
        for (int round = 0; round < 4 && stepsOverOne(profile); round++) {
            if (!letGo(profile, held, keep)) { return; }
            smooth(profile, held);
        }
    }

    private static boolean stepsOverOne(int[] profile) {
        for (int at = 1; at < profile.length; at++) {
            if (Math.abs(profile[at] - profile[at - 1]) > 1) { return true; }
        }
        return false;
    }

    private static boolean letGo(int[] profile, boolean[] held, boolean[] keep) {
        int rows = profile.length;
        boolean freed = false;
        for (int i = 1; i < rows; i++) {
            if (Math.abs(profile[i] - profile[i - 1]) <= 1) { continue; }
            int lo = i;
            int hi = i - 1;
            boolean leftTurn = true;
            while (Math.abs(profile[hi + 1] - profile[lo - 1]) > hi - lo + 2) {
                boolean leftOpen = lo - 2 >= 0 && !keep[lo - 1];
                boolean rightOpen = hi + 2 < rows && !keep[hi + 1];
                if (!leftOpen && !rightOpen) { break; }
                if ((leftTurn && leftOpen) || !rightOpen) { lo--; }
                else { hi++; }
                leftTurn = !leftTurn;
            }
            int a = profile[lo - 1];
            int b = profile[hi + 1];
            if (Math.abs(b - a) > hi - lo + 2) { continue; }
            int step = b > a ? 1 : -1;
            for (int k = lo; k <= hi; k++) {
                profile[k] = a + step * Math.min(k - lo + 1, Math.abs(b - a));
                held[k] = false;
                freed = true;
            }
            i = hi + 1;
        }
        return freed;
    }

    public static boolean[] bore(int[] profile, int[] ground, boolean[] held, boolean[] bridged, int depth, boolean openLow, boolean openHigh) {
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
            int[] cut = CityGradeBore.cutLine(profile, at, end, openLow && at == 0, openHigh && end == rows - 1);
            int high = -1;
            for (int row = at; row <= end + 1; row++) {
                boolean raised = row <= end && cut[row - at] < profile[row];
                if (raised && high < 0) { high = row; }
                if (raised || high < 0) { continue; }
                int longest = CityGradeBore.buried(ground, cut, at, high, row, depth);
                if (longest >= TUNNEL_LEAST) {
                    for (int inside = high; inside < row; inside++) {
                        profile[inside] = cut[inside - at];
                        bored[inside] = true;
                    }
                }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A street rises over rows {} to {}, its cut line held at y {} with the ground standing at most {} over it and buried {} row(s) at {} block(s) or more, wanting {}", high, row - 1, cut[high - at], CityGradeBore.deepest(ground, cut, at, high, row), longest, depth, TUNNEL_LEAST); }
                high = -1;
            }
            at = end + 1;
        }
        return bored;
    }

    public static int[] railLine(int[] ground, int sea, int climb) { return smoothed(CityGradeRails.filled(ground, sea), climb); }

    public static double[] railBase(int[] ground, int sea, boolean[] lowered, int depth, int floorLeast) {
        double[] base = CityGradeRails.filled(ground, sea);
        for (int at = 0; at < base.length; at++) {
            if (lowered[at]) { base[at] = Math.max(floorLeast, base[at] - depth); }
        }
        return base;
    }

    public static int[] smoothed(double[] base, int climb) {
        int rows = base.length;
        double step = 1.0D / climb;
        double[] mean = CityGradeRails.meaned(base, CityGradeRails.span(climb));
        double[] lower = CityGradeRails.under(mean, step);
        double[] upper = CityGradeRails.over(mean, step);
        double[] want = new double[rows];
        for (int at = 0; at < rows; at++) { want[at] = (lower[at] + upper[at]) / 2.0D; }
        return stepped(want, climb);
    }

    public static double[] sinking(double[] base, int climb) { return CityGradeRails.under(CityGradeRails.lowest(base, CityGradeRails.span(climb)), 1.0D / climb); }

    public static int[] sunken(double[] base, int climb) { return stepped(sinking(base, climb), climb); }

    public static int[] stepped(double[] want, int climb) {
        int rows = want.length;
        int[] profile = new int[rows];
        int level = (int) Math.round(want[0]);
        int lastStep = -climb;
        for (int at = 0; at < rows; at++) {
            if (want[at] >= level + 1 && at - lastStep >= climb) {
                level++;
                lastStep = at;
            }
            else if (want[at] <= level - 1 && at - lastStep >= climb) {
                level--;
                lastStep = at;
            }
            profile[at] = level;
        }
        return profile;
    }

    public static int eased(int[] profile, double[] want, boolean[] fixed, int start, int center, int climb) {
        int rows = profile.length;
        double step = 1.0D / climb;
        double[] high = new double[rows];
        double[] low = new double[rows];
        for (int at = 0; at < rows; at++) {
            high[at] = fixed[at] ? profile[at] : UNHELD;
            low[at] = fixed[at] ? profile[at] : -UNHELD;
        }
        high = CityGradeRails.under(high, step);
        low = CityGradeRails.over(low, step);
        double[] cone = new double[rows];
        for (int at = 0; at < rows; at++) {
            double lifted = Math.max(low[at], want[at]);
            cone[at] = Math.min(high[at], lifted);
        }
        int[] made = stepped(cone, climb);
        int moved = 0;
        int strained = 0;
        int firstStrain = 0;
        int deepestStrain = 0;
        for (int at = 0; at < rows; at++) {
            if (fixed[at]) {
                if (made[at] == profile[at]) { continue; }
                if (strained == 0) { firstStrain = start + at; }
                strained++;
                deepestStrain = Math.max(deepestStrain, Math.abs(made[at] - profile[at]));
                continue;
            }
            if (made[at] == profile[at]) { continue; }
            profile[at] = made[at];
            moved++;
        }
        if (strained > 0) { ContentLog.LOGGER.warn("Subway line at {} holds {} row(s) at levels no grade of one block every {} row(s) can join, the first at row {} and the worst {} block(s) out, so its grade steps there", center, strained, climb, firstStrain, deepestStrain); }
        return moved;
    }

    public static void spaceSteps(int[] profile, boolean[] fixed, boolean[] settled, int climb) {
        CityGradeRails.rein(profile, fixed);
        int rows = profile.length;
        int since = -climb;
        for (int at = 1; at < rows; at++) {
            if (profile[at] == profile[at - 1]) { continue; }
            if (at - since >= climb) {
                since = at;
                continue;
            }
            int back = at;
            while (back < rows && !settled[back] && profile[back] != profile[at - 1]) {
                profile[back] = profile[at - 1];
                back++;
            }
            if (back == at) { since = at; }
        }
        CityGradeRails.rein(profile, fixed);
    }

    public static void levelDecks(int[] profile, boolean[] bridged, boolean[] keep, int run) {
        int rows = profile.length;
        int i = 0;
        while (i < rows) {
            if (!bridged[i] || keep[i]) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && bridged[end + 1] && !keep[end + 1]) { end++; }
            int most = Integer.MIN_VALUE;
            int least = Integer.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                most = Math.max(most, profile[k]);
                least = Math.min(least, profile[k]);
            }
            int[] before = profile.clone();
            for (int deck = most; deck >= least; deck--) {
                CityGradeDecks.layDeck(profile, before, keep, i, end, deck, run);
                if (!CityGradeDecks.stepped(profile, before, run)) { break; }
                System.arraycopy(before, 0, profile, 0, rows);
            }
            i = end + 1;
        }
    }

    public static int groundDeckEnds(boolean[] bridged, boolean[] landed) {
        int rows = bridged.length;
        int trimmed = 0;
        int i = 0;
        while (i < rows) {
            if (!bridged[i]) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && bridged[end + 1]) { end++; }
            int first = i;
            for (; first <= end && landed[first]; first++) {
                bridged[first] = false;
                trimmed++;
            }
            for (int last = end; last >= first && landed[last]; last--) {
                bridged[last] = false;
                trimmed++;
            }
            i = end + 1;
        }
        return trimmed;
    }

    public static void approach(int[] profile, boolean[] fixed, boolean[] band, int climb) {
        int rows = profile.length;
        int[] target = profile.clone();
        boolean[] claimed = new boolean[rows];
        for (int at = 0; at < rows; at++) {
            if (!band[at]) { continue; }
            if (at > 0 && !band[at - 1]) { CityGradeDecks.walk(profile, target, fixed, claimed, at - 1, -1, profile[at], climb); }
            if (at + 1 < rows && !band[at + 1]) { CityGradeDecks.walk(profile, target, fixed, claimed, at + 1, 1, profile[at], climb); }
        }
    }

    public static boolean[] roofed(int[] profile, int[] ground, boolean[] bridged, int depth) {
        boolean[] roof = new boolean[profile.length];
        if (depth <= 0) { return roof; }
        for (int at = 0; at < roof.length; at++) { roof[at] = CityGradeBore.roofedAt(profile, ground, bridged, at, depth); }
        dropShortRuns(roof);
        return roof;
    }

    public static void dropShortRuns(boolean[] tunnels) {
        int from = 0;
        while (from < tunnels.length) {
            if (!tunnels[from]) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < tunnels.length && tunnels[to + 1]) { to++; }
            if (to - from + 1 < TUNNEL_LEAST) { Arrays.fill(tunnels, from, to + 1, false); }
            from = to + 1;
        }
    }

    public static boolean[] frameRows(boolean[] decking, int least, int run) {
        int rows = decking.length;
        boolean[] frames = new boolean[rows];
        for (int i = 0; i < rows; i++) {
            if (!decking[i]) { continue; }
            int end = i;
            while (end + 1 < rows && decking[end + 1]) { end++; }
            int span = end - i + 1;
            if (span >= least) {
                int count = Math.max(1, span / run);
                int spread = (count - 1) * run;
                int first = i + (span - 1 - spread) / 2;
                for (int at = 0; at < count; at++) {
                    int mark = first + at * run;
                    if (mark >= i && mark <= end) { frames[mark] = true; }
                }
            }
            i = end;
        }
        return frames;
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
