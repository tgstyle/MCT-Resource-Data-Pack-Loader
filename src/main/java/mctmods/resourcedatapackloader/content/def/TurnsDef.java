package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record TurnsDef(String order, int seconds, int gapSeconds, String held, int endsAtScore, int cycles, List<String> mobTags, List<String> mobTypes) {
    public static final String FIXED = "fixed";
    public static final String RANDOM = "random";
    public static final String LOWEST_FIRST = "lowestFirst";
    public static final String LAST_WINNER = "lastWinner";
    public static final List<String> ORDERS = List.of(FIXED, RANDOM, LOWEST_FIRST, LAST_WINNER);
    public static final String FROZEN = "frozen";
    public static final List<String> HOLDS = List.of(FROZEN, "spectator", "adventure");

    public boolean ends() { return endsAtScore > 0 || cycles > 0; }
}
