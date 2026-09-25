package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.util.Mth;

final class CityGradeDecks {
    private CityGradeDecks() {}

    static void walk(int[] profile, int[] target, boolean[] fixed, boolean[] claimed, int from, int dir, int start, int climb) {
        int level = start;
        int last = from - dir * climb;
        for (int row = from; row >= 0 && row < profile.length && !fixed[row] && !claimed[row]; row += dir) {
            boolean spaced = (row - last) * dir >= climb;
            if (target[row] == level && spaced) { return; }
            if (spaced && target[row] != level) {
                level += target[row] > level ? 1 : -1;
                last = row;
            }
            profile[row] = level;
            claimed[row] = true;
        }
    }

    static void layDeck(int[] profile, int[] before, boolean[] keep, int first, int last, int deck, int run) {
        for (int k = first; k <= last; k++) { profile[k] = deck; }
        if (run > 1) {
            boolean[] claimed = new boolean[profile.length];
            walk(profile, before, keep, claimed, first - 1, -1, deck, run);
            walk(profile, before, keep, claimed, last + 1, 1, deck, run);
            return;
        }
        for (int k = first - 1, away = 1; k >= 0 && !keep[k]; k--, away++) {
            int want = Mth.clamp(profile[k], deck - away, deck + away);
            if (profile[k] == want) { break; }
            profile[k] = want;
        }
        for (int k = last + 1, away = 1; k < profile.length && !keep[k]; k++, away++) {
            int want = Mth.clamp(profile[k], deck - away, deck + away);
            if (profile[k] == want) { break; }
            profile[k] = want;
        }
    }

    static boolean stepped(int[] profile, int[] before, int run) {
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
            if (Math.abs(profile[k] - profile[k - 1]) > 1) { return true; }
            if (profile[k] == profile[k - 1]) { continue; }
            if (last != Integer.MIN_VALUE && k - last < run) { return true; }
            last = k;
        }
        return false;
    }
}
