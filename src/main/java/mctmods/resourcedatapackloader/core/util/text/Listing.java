package mctmods.resourcedatapackloader.core.util.text;

public final class Listing {
    private Listing() {}

    public static boolean allows(boolean listed, boolean blacklist) { return listed != blacklist; }

    public static boolean blocks(boolean listed, boolean blacklist) { return listed == blacklist; }

    public static boolean allows(int[] listed, int dimension, boolean blacklist, boolean emptyMeansAll) {
        if (emptyMeansAll && listed.length == 0) { return true; }
        for (int held : listed) {
            if (held == dimension) { return !blacklist; }
        }
        return blacklist;
    }
}
