package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public final class RoundResetDef {
    public final String lead;
    public final boolean playersVote;
    public final List<String> teams;
    public final int passPercent;
    public final int voteSeconds;
    public final int cooldownSeconds;
    public final String leadSays;
    public final String voteSays;
    public final String tallySays;
    public final String passSays;
    public final String failSays;

    public RoundResetDef(String lead, boolean playersVote, List<String> teams, int passPercent, int voteSeconds, int cooldownSeconds,
                         String leadSays, String voteSays, String tallySays, String passSays, String failSays) {
        this.lead = lead;
        this.playersVote = playersVote;
        this.teams = teams;
        this.passPercent = passPercent;
        this.voteSeconds = voteSeconds;
        this.cooldownSeconds = cooldownSeconds;
        this.leadSays = leadSays;
        this.voteSays = voteSays;
        this.tallySays = tallySays;
        this.passSays = passSays;
        this.failSays = failSays;
    }

    public boolean offered() { return playersVote || !"none".equals(lead); }

    public boolean callableFrom(@Nullable String side) { return playersVote && (teams.isEmpty() || side != null && teams.contains(side)); }
}
