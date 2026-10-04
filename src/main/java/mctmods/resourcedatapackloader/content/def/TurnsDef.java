package mctmods.resourcedatapackloader.content.def;

import java.util.Arrays;
import java.util.List;

public final class TurnsDef {
    public static final String FIXED = "fixed";
    public static final String RANDOM = "random";
    public static final String LOWEST_FIRST = "lowestFirst";
    public static final String LAST_WINNER = "lastWinner";
    public static final List<String> ORDERS = Arrays.asList(FIXED, RANDOM, LOWEST_FIRST, LAST_WINNER);
    public static final String FROZEN = "frozen";
    public static final List<String> HOLDS = Arrays.asList(FROZEN, "spectator", "adventure");
    public final String order;
    public final int seconds;
    public final int gapSeconds;
    public final String held;
    public final int endsAtScore;
    public final int cycles;
    public final List<String> mobTags;
    public final List<String> mobTypes;

    public TurnsDef(String order, int seconds, int gapSeconds, String held, int endsAtScore, int cycles, List<String> mobTags, List<String> mobTypes) {
        this.order = order;
        this.seconds = seconds;
        this.gapSeconds = gapSeconds;
        this.held = held;
        this.endsAtScore = endsAtScore;
        this.cycles = cycles;
        this.mobTags = mobTags;
        this.mobTypes = mobTypes;
    }

    public boolean ends() { return endsAtScore > 0 || cycles > 0; }
}
