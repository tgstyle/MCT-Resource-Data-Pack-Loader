package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.world.World;
import java.util.Arrays;
import javax.annotation.Nullable;

public final class BeardGrade {
    public static final int CAP = 2;
    public static final int TUNNEL_LEAST = 12;
    public static final int TUNNEL_REACH = 98;

    private BeardGrade() {}

    @Nullable public static int[] noiseProfile(World world, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost) {
        if (BeardSurface.unreadable(world)) { return null; }
        int[] profile = new int[rowMost - rowLeast + 1];
        int[] across = new int[acrossMost - acrossLeast + 1];
        for (int i = 0; i < profile.length; i++) {
            int count = 0;
            for (int at = acrossLeast; at <= acrossMost; at++) {
                int sampled = BeardSurface.surfaceAt(world, alongX ? rowLeast + i : at, alongX ? at : rowLeast + i);
                if (sampled < world.getSeaLevel() - 1) { continue; }
                across[count++] = sampled;
            }
            if (count == 0) {
                profile[i] = Integer.MIN_VALUE;
                continue;
            }
            Arrays.sort(across, 0, count);
            profile[i] = across[count / 2];
        }
        return profile;
    }
    public static void flatRuns(World world, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile) {
        int run = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathFlatRun", Config.worldgen.villagePathFlatRun));
        if (run <= 1) { return; }
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE) { continue; }
            int grid = (start + i) - Math.floorMod(start + i, run);
            int center = (acrossLeast + acrossMost) / 2;
            int[] taken = new int[3];
            int count = 0;
            for (int across = center - 1; across <= center + 1; across++) {
                int found = BeardSurface.surfaceAt(world, alongX ? grid : across, alongX ? across : grid);
                if (found < world.getSeaLevel() - 1) { continue; }
                taken[count++] = found;
            }
            if (count > 0) {
                Arrays.sort(taken, 0, count);
                profile[i] = taken[count / 2];
            }
        }
    }
    public static boolean[] smooth(int[] profile) {
        int rows = profile.length;
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; } }
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; } }
        for (int i = 1; i < rows - 1; i++) {
            if (!joined(profile, i) || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; }
        }
        boolean[] bridged = new boolean[rows];
        for (int i = 0; i < rows; i++) {
            if (profile[i] != Integer.MIN_VALUE) { continue; }
            int gapEnd = i;
            while (gapEnd < rows && profile[gapEnd] == Integer.MIN_VALUE) { gapEnd++; }
            if (i > 0 && gapEnd < rows && gapEnd - i <= 12) {
                int fromY = profile[i - 1];
                int toY = profile[gapEnd];
                for (int held = i; held < gapEnd; held++) {
                    profile[held] = fromY + (toY - fromY) * (held - i + 1) / (gapEnd - i + 1);
                    bridged[held] = true;
                }
            }
            i = gapEnd;
        }
        return bridged;
    }
    private static boolean joined(int[] profile, int i) { return profile[i] != Integer.MIN_VALUE && profile[i - 1] != Integer.MIN_VALUE; }

    public static int capEmbankment(int[] profile, int[] ground, boolean[] bridged, boolean[] held) {
        int capped = 0;
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE || ground[i] == Integer.MIN_VALUE || bridged[i] || held[i]) { continue; }
            if (profile[i] <= ground[i] + CAP) { continue; }
            profile[i] = ground[i] + CAP;
            held[i] = true;
            capped++;
        }
        return capped;
    }
    public static void sag(int[] profile, boolean[] bridged, int seaLevel) {
        int rows = profile.length;
        for (int i = 0; i < rows; i++) {
            if (profile[i] != Integer.MIN_VALUE) { continue; }
            int gapEnd = i;
            while (gapEnd < rows && profile[gapEnd] == Integer.MIN_VALUE) { gapEnd++; }
            int before = i > 0 ? profile[i - 1] : Integer.MIN_VALUE;
            int after = gapEnd < rows ? profile[gapEnd] : Integer.MIN_VALUE;
            boolean touches = before == Integer.MIN_VALUE || after == Integer.MIN_VALUE || (before - seaLevel) + (after - seaLevel) <= gapEnd - i + 1;
            for (int held = i; held < gapEnd; held++) {
                if (touches) {
                    int down = before == Integer.MIN_VALUE ? seaLevel : Math.max(seaLevel, before - (held - i + 1));
                    int up = after == Integer.MIN_VALUE ? seaLevel : Math.max(seaLevel, after - (gapEnd - held));
                    profile[held] = Math.max(down, up);
                }
                else { profile[held] = before + (after - before) * (held - i + 1) / (gapEnd - i + 1); }
                bridged[held] = true;
            }
            i = gapEnd;
        }
    }
    public static int holdCauseway(int[] profile, boolean[] bridged, int seaLevel) {
        int lifted = 0;
        int rows = profile.length;
        for (int i = 0; i < rows; i++) {
            if (bridged[i] || profile[i] == Integer.MIN_VALUE) { continue; }
            int end = i;
            while (end + 1 < rows && !bridged[end + 1] && profile[end + 1] != Integer.MIN_VALUE) { end++; }
            if (i > 0 && end + 1 < rows && bridged[i - 1] && bridged[end + 1]) {
                int deck = Math.min(Math.max(profile[i - 1], seaLevel), Math.max(profile[end + 1], seaLevel));
                for (int at = i; at <= end; at++) {
                    if (profile[at] < deck) {
                        profile[at] = deck;
                        lifted++;
                    }
                }
            }
            i = end;
        }
        return lifted;
    }
    public static int ramp(int[] profile, boolean[] pinned, boolean[] keep) {
        int rows = profile.length;
        int freed = 0;
        for (int i = 1; i < rows; i++) {
            if (!pinned[i] || !pinned[i - 1] || !joined(profile, i) || Math.abs(profile[i] - profile[i - 1]) <= 1) { continue; }
            int lo = i;
            int hi = i - 1;
            boolean leftTurn = true;
            while (true) {
                int a = profile[lo - 1];
                int b = profile[hi + 1];
                if (Math.abs(b - a) <= hi - lo + 2) { break; }
                boolean leftOpen = lo - 2 >= 0 && !keep[lo - 1] && profile[lo - 2] != Integer.MIN_VALUE;
                boolean rightOpen = hi + 2 < rows && !keep[hi + 1] && profile[hi + 2] != Integer.MIN_VALUE;
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
                pinned[k] = false;
                freed++;
            }
            i = hi + 1;
        }
        return freed;
    }

    public static int rampSteps(int[] profile, boolean[] held, boolean[] keep) {
        int rows = profile.length;
        int freed = 0;
        for (int i = 1; i < rows; i++) {
            if (!joined(profile, i) || Math.abs(profile[i] - profile[i - 1]) <= 1) { continue; }
            int lo = i;
            int hi = i - 1;
            boolean leftTurn = true;
            while (true) {
                int a = profile[lo - 1];
                int b = profile[hi + 1];
                if (Math.abs(b - a) <= hi - lo + 2) { break; }
                boolean leftOpen = lo - 2 >= 0 && !keep[lo - 1] && profile[lo - 2] != Integer.MIN_VALUE;
                boolean rightOpen = hi + 2 < rows && !keep[hi + 1] && profile[hi + 2] != Integer.MIN_VALUE;
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
                freed++;
            }
            i = hi + 1;
        }
        return freed;
    }

    public static int levelDecks(int[] profile, boolean[] bridged, boolean[] keep, int run) {
        int rows = profile.length;
        int levelled = 0;
        int i = 0;
        while (i < rows) {
            if (!bridged[i] || keep[i] || profile[i] == Integer.MIN_VALUE) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && bridged[end + 1] && !keep[end + 1] && profile[end + 1] != Integer.MIN_VALUE) { end++; }
            int most = Integer.MIN_VALUE;
            int least = Integer.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                most = Math.max(most, profile[k]);
                least = Math.min(least, profile[k]);
            }
            int[] before = profile.clone();
            boolean laidOut = false;
            for (int spacing = run; spacing >= 1 && !laidOut; spacing = spacing == 1 ? 0 : 1) {
                for (int deck = most; deck >= least; deck--) {
                    int laid = layDeck(profile, keep, i, end, deck, run);
                    if (!stepped(profile, before, spacing, run)) {
                        levelled += laid;
                        laidOut = true;
                        break;
                    }
                    System.arraycopy(before, 0, profile, 0, rows);
                }
            }
            i = end + 1;
        }
        return levelled;
    }

    private static int layDeck(int[] profile, boolean[] keep, int first, int last, int deck, int run) {
        int laid = 0;
        for (int k = first; k <= last; k++) {
            if (profile[k] == deck) { continue; }
            profile[k] = deck;
            laid++;
        }
        for (int k = first - 1, away = 1; k >= 0 && !keep[k] && profile[k] != Integer.MIN_VALUE; k--, away++) {
            int want = deck - (away + run - 1) / run;
            if (profile[k] >= want) { break; }
            profile[k] = want;
        }
        for (int k = last + 1, away = 1; k < profile.length && !keep[k] && profile[k] != Integer.MIN_VALUE; k++, away++) {
            int want = deck - (away + run - 1) / run;
            if (profile[k] >= want) { break; }
            profile[k] = want;
        }
        return laid;
    }

    private static boolean stepped(int[] profile, int[] before, int spacing, int run) {
        int lo = -1;
        int hi = -1;
        for (int k = 0; k < profile.length; k++) {
            if (profile[k] == before[k]) { continue; }
            if (lo < 0) { lo = k; }
            hi = k;
        }
        if (lo < 0) { return false; }
        int from = Math.max(1, lo - run);
        int to = Math.min(profile.length - 1, hi + run);
        int last = Integer.MIN_VALUE;
        for (int k = from; k <= to; k++) {
            if (profile[k] == Integer.MIN_VALUE || profile[k - 1] == Integer.MIN_VALUE) { continue; }
            if (Math.abs(profile[k] - profile[k - 1]) > 1) { return true; }
            if (profile[k] == profile[k - 1]) { continue; }
            if (last != Integer.MIN_VALUE && k - last < spacing) { return true; }
            last = k;
        }
        return false;
    }

    public static int deckDrops(int[] profile, int[] ground, boolean[] bridged, boolean[] keep, int drop) {
        if (drop <= 0) { return 0; }
        int decked = 0;
        for (int i = 0; i < profile.length; i++) {
            if (keep[i] || bridged[i] || profile[i] == Integer.MIN_VALUE || ground[i] == Integer.MIN_VALUE) { continue; }
            if (profile[i] <= ground[i] + drop) { continue; }
            bridged[i] = true;
            decked++;
        }
        return decked;
    }

    public static int fillDips(int[] profile, boolean[] keep) {
        int rows = profile.length;
        int lifted = 0;
        int i = 0;
        while (i < rows) {
            if (profile[i] == Integer.MIN_VALUE) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && profile[end + 1] != Integer.MIN_VALUE) { end++; }
            int[] leftMax = new int[end - i + 1];
            int running = Integer.MIN_VALUE;
            for (int k = i; k <= end; k++) {
                running = keep[k] ? profile[k] : Math.max(running, profile[k]);
                leftMax[k - i] = running;
            }
            int[] rightMax = new int[end - i + 1];
            running = Integer.MIN_VALUE;
            for (int k = end; k >= i; k--) {
                running = keep[k] ? profile[k] : Math.max(running, profile[k]);
                rightMax[k - i] = running;
            }
            for (int k = i; k <= end; k++) {
                if (keep[k]) { continue; }
                int fill = Math.min(leftMax[k - i], rightMax[k - i]);
                if (fill > profile[k]) {
                    profile[k] = fill;
                    lifted++;
                }
            }
            i = end + 1;
        }
        return lifted;
    }

    public static void settle(int[] profile, boolean[] held) {
        int rows = profile.length;
        for (int i = 1; i < rows - 1; i++) {
            if (held[i] || !joined(profile, i) || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; }
        }
        int i = 0;
        while (i < rows) {
            if (profile[i] == Integer.MIN_VALUE) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && profile[end + 1] != Integer.MIN_VALUE) { end++; }
            int[] leftMax = new int[end - i + 1];
            int running = Integer.MIN_VALUE;
            for (int k = i; k <= end; k++) {
                running = Math.max(running, profile[k]);
                leftMax[k - i] = running;
            }
            running = Integer.MIN_VALUE;
            int[] rightMax = new int[end - i + 1];
            for (int k = end; k >= i; k--) {
                running = Math.max(running, profile[k]);
                rightMax[k - i] = running;
            }
            int low = -1;
            for (int k = i; k <= end + 1; k++) {
                boolean sunken = k <= end && Math.min(leftMax[k - i], rightMax[k - i]) > profile[k];
                if (sunken && low < 0) { low = k; }
                if (!sunken && low >= 0) {
                    if (k - low <= 12) {
                        for (int fillAt = low; fillAt < k; fillAt++) {
                            int fill = Math.min(leftMax[fillAt - i], rightMax[fillAt - i]);
                            if (!held[fillAt] && fill > profile[fillAt]) { profile[fillAt] = fill; }
                        }
                    }
                    low = -1;
                }
            }
            int[] leftFloor = new int[end - i + 1];
            running = Integer.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                running = Math.min(running, profile[k]);
                leftFloor[k - i] = running;
            }
            running = Integer.MAX_VALUE;
            int[] rightFloor = new int[end - i + 1];
            for (int k = end; k >= i; k--) {
                running = Math.min(running, profile[k]);
                rightFloor[k - i] = running;
            }
            int high = -1;
            for (int k = i; k <= end + 1; k++) {
                boolean raised = k <= end && Math.max(leftFloor[k - i], rightFloor[k - i]) < profile[k];
                if (raised && high < 0) { high = k; }
                if (!raised && high >= 0) {
                    if (k - high <= 12) {
                        for (int cutAt = high; cutAt < k; cutAt++) {
                            int cut = Math.max(leftFloor[cutAt - i], rightFloor[cutAt - i]);
                            if (!held[cutAt] && cut < profile[cutAt]) { profile[cutAt] = cut; }
                        }
                    }
                    high = -1;
                }
            }
            i = end + 1;
        }
        for (int i2 = 1; i2 < rows; i2++) { if (!held[i2] && joined(profile, i2) && profile[i2] > profile[i2 - 1] + 1) { profile[i2] = profile[i2 - 1] + 1; } }
        for (int i2 = rows - 2; i2 >= 0; i2--) { if (!held[i2] && joined(profile, i2 + 1) && profile[i2] > profile[i2 + 1] + 1) { profile[i2] = profile[i2 + 1] + 1; } }
        for (int i2 = 1; i2 < rows; i2++) { if (!held[i2] && joined(profile, i2) && profile[i2] < profile[i2 - 1] - 1) { profile[i2] = profile[i2 - 1] - 1; } }
        for (int i2 = rows - 2; i2 >= 0; i2--) { if (!held[i2] && joined(profile, i2 + 1) && profile[i2] < profile[i2 + 1] - 1) { profile[i2] = profile[i2 + 1] - 1; } }
        for (int i2 = 1; i2 < rows - 1; i2++) {
            if (held[i2] || !joined(profile, i2) || profile[i2 + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i2 - 1] == profile[i2 + 1] && Math.abs(profile[i2] - profile[i2 - 1]) == 1) { profile[i2] = profile[i2 - 1]; }
        }
    }
    public static boolean[] bore(int[] profile, int[] ground, boolean[] held, boolean[] bridged, int depth, boolean openLow, boolean openHigh) {
        int rows = profile.length;
        boolean[] bored = new boolean[rows];
        if (depth <= 0) { return bored; }
        int i = 0;
        while (i < rows) {
            if (!free(profile, held, bridged, i)) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && free(profile, held, bridged, end + 1)) { end++; }
            int[] cut = cutLine(profile, i, end, openLow && i == 0, openHigh && end == rows - 1);
            int high = -1;
            for (int k = i; k <= end + 1; k++) {
                boolean raised = k <= end && cut[k - i] < profile[k];
                if (raised && high < 0) { high = k; }
                if (!raised && high >= 0) {
                    if (buried(ground, cut, i, high, k, depth) >= TUNNEL_LEAST) {
                        for (int at = high; at < k; at++) {
                            profile[at] = cut[at - i];
                            bored[at] = true;
                        }
                    }
                    high = -1;
                }
            }
            i = end + 1;
        }
        return bored;
    }

    private static int[] cutLine(int[] profile, int from, int to, boolean openLow, boolean openHigh) {
        int[] leftFloor = new int[to - from + 1];
        int running = from > 0 && profile[from - 1] != Integer.MIN_VALUE ? profile[from - 1] : Integer.MAX_VALUE;
        for (int k = from; k <= to; k++) {
            running = Math.min(running, profile[k]);
            leftFloor[k - from] = running;
        }
        int[] rightFloor = new int[to - from + 1];
        running = to + 1 < profile.length && profile[to + 1] != Integer.MIN_VALUE ? profile[to + 1] : Integer.MAX_VALUE;
        for (int k = to; k >= from; k--) {
            running = Math.min(running, profile[k]);
            rightFloor[k - from] = running;
        }
        int[] cut = new int[to - from + 1];
        for (int k = 0; k < cut.length; k++) {
            if (openHigh && !openLow) { cut[k] = leftFloor[k]; }
            else if (openLow && !openHigh) { cut[k] = rightFloor[k]; }
            else { cut[k] = Math.max(leftFloor[k], rightFloor[k]); }
        }
        return cut;
    }

    private static boolean free(int[] profile, boolean[] held, boolean[] bridged, int i) { return profile[i] != Integer.MIN_VALUE && !held[i] && !bridged[i]; }

    private static int buried(int[] ground, int[] cut, int from, int lo, int hi, int depth) {
        int longest = 0;
        int run = 0;
        for (int at = lo; at < hi; at++) {
            boolean deep = ground[at] != Integer.MIN_VALUE && ground[at] - cut[at - from] >= depth;
            run = deep ? run + 1 : 0;
            longest = Math.max(longest, run);
        }
        return longest;
    }

    public static boolean walkable(int[] profile, boolean[] bridged) {
        int held = Integer.MIN_VALUE;
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE || bridged[i]) { continue; }
            if (held != Integer.MIN_VALUE && Math.abs(profile[i] - held) > 1) { return false; }
            held = profile[i];
        }
        return true;
    }
}
