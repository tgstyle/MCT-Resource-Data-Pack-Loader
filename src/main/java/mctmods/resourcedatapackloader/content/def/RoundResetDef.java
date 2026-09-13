package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record RoundResetDef(String lead, boolean playersVote, List<String> teams, int passPercent, int voteSeconds, int cooldownSeconds, String leadSays, String voteSays,
                            String tallySays, String passSays, String failSays) {
    public static final String NONE = "none";
    public static final String NOW = "now";
    public static final String VOTE = "vote";

    public boolean offered() { return playersVote || !NONE.equals(lead); }

    public boolean callableFrom(@Nullable String side) { return playersVote && (teams.isEmpty() || side != null && teams.contains(side)); }
}
