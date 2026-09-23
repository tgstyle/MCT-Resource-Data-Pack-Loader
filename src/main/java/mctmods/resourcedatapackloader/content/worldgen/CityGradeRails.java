package mctmods.resourcedatapackloader.content.worldgen;

final class CityGradeRails {
    private CityGradeRails() {}

    static double[] filled(int[] ground, int sea) {
        int rows = ground.length;
        double[] base = new double[rows];
        int i = 0;
        while (i < rows) {
            if (ground[i] != Integer.MIN_VALUE) {
                base[i] = ground[i];
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && ground[end + 1] == Integer.MIN_VALUE) { end++; }
            int before = i > 0 ? ground[i - 1] : Integer.MIN_VALUE;
            int after = end + 1 < rows ? ground[end + 1] : Integer.MIN_VALUE;
            for (int at = i; at <= end; at++) {
                double level;
                if (before == Integer.MIN_VALUE && after == Integer.MIN_VALUE) { level = sea; }
                else if (before == Integer.MIN_VALUE) { level = after; }
                else if (after == Integer.MIN_VALUE) { level = before; }
                else { level = before + (after - before) * (at - i + 1) / (double) (end - i + 2); }
                base[at] = Math.max(sea, level);
            }
            i = end + 1;
        }
        return base;
    }

    static int span(int climb) { return Math.max(4, 2 * climb); }

    static double[] meaned(double[] base, int span) {
        int rows = base.length;
        double[] mean = new double[rows];
        for (int at = 0; at < rows; at++) {
            double total = 0.0D;
            int count = 0;
            for (int near = Math.max(0, at - span); near <= Math.min(rows - 1, at + span); near++) {
                total += base[near];
                count++;
            }
            mean[at] = total / count;
        }
        return mean;
    }

    static double[] lowest(double[] base, int span) {
        int rows = base.length;
        double[] least = new double[rows];
        for (int at = 0; at < rows; at++) {
            double found = base[at];
            for (int near = Math.max(0, at - span); near <= Math.min(rows - 1, at + span); near++) { found = Math.min(found, base[near]); }
            least[at] = found;
        }
        return least;
    }

    static double[] under(double[] level, double step) {
        double[] lower = level.clone();
        for (int at = 1; at < lower.length; at++) { lower[at] = Math.min(lower[at], lower[at - 1] + step); }
        for (int at = lower.length - 2; at >= 0; at--) { lower[at] = Math.min(lower[at], lower[at + 1] + step); }
        return lower;
    }

    static double[] over(double[] level, double step) {
        double[] upper = level.clone();
        for (int at = 1; at < upper.length; at++) { upper[at] = Math.max(upper[at], upper[at - 1] - step); }
        for (int at = upper.length - 2; at >= 0; at--) { upper[at] = Math.max(upper[at], upper[at + 1] - step); }
        return upper;
    }

    static void rein(int[] profile, boolean[] fixed) {
        for (int at = 1; at < profile.length; at++) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at - 1] - 1, Math.min(profile[at - 1] + 1, profile[at]));
        }
        for (int at = profile.length - 2; at >= 0; at--) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at + 1] - 1, Math.min(profile[at + 1] + 1, profile[at]));
        }
    }
}
