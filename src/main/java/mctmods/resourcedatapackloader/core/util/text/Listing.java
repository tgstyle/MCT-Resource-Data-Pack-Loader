package mctmods.resourcedatapackloader.core.util.text;

public final class Listing {
    private Listing() {}

    public static boolean allows(boolean listed, boolean blacklist) { return listed != blacklist; }

    public static boolean blocks(boolean listed, boolean blacklist) { return listed == blacklist; }
}
