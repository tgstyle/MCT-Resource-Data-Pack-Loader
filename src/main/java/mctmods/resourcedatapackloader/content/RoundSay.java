package mctmods.resourcedatapackloader.content;

public final class RoundSay {
    public final String key;
    public final boolean good;
    public final Object[] args;

    private RoundSay(String key, boolean good, Object[] args) {
        this.key = key;
        this.good = good;
        this.args = args;
    }

    public static RoundSay good(String key, Object... args) { return new RoundSay(key, true, args); }

    public static RoundSay bad(String key, Object... args) { return new RoundSay(key, false, args); }
}
