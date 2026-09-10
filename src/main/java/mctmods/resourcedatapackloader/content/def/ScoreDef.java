package mctmods.resourcedatapackloader.content.def;

import net.minecraft.scoreboard.IScoreCriteria;
import java.util.Map;
import javax.annotation.Nullable;

public final class ScoreDef {
    public final String name;
    public final String displayName;
    public final IScoreCriteria criterion;
    public final String slot;
    @Nullable public final IScoreCriteria.EnumRenderType render;
    public final boolean teamTotals;
    public final boolean individuals;
    public final Map<String, Integer> killPoints;
    public final int deathPoints;
    public final int endsAtScore;
    public final int endsAfterMinutes;
    public final int endsAfterRounds;
    public final boolean resultsCard;
    public final String resultsTitle;
    public final String resultsIcon;
    public final String resultsImage;
    public final int resultsBackground;
    public final int resultsTicks;
    public final boolean carries;
    public final boolean endsResets;
    public final int endsIntermission;
    public final String awardsTo;
    public final boolean endsLocksTeams;
    public final int ownKillPoints;
    public final String intermissionSays;
    public final String startsSays;

    public ScoreDef(String name, String displayName, IScoreCriteria criterion, String slot,
                    @Nullable IScoreCriteria.EnumRenderType render, boolean teamTotals, boolean individuals,
                    Map<String, Integer> killPoints, int deathPoints, int endsAtScore, int endsAfterMinutes, int endsAfterRounds,
                    boolean resultsCard, String resultsTitle, String resultsIcon, String resultsImage,
                    int resultsBackground, int resultsTicks,
                    boolean carries, boolean endsResets, int endsIntermission, String awardsTo, boolean endsLocksTeams,
                    int ownKillPoints, String intermissionSays, String startsSays) {
        this.name = name;
        this.displayName = displayName;
        this.criterion = criterion;
        this.slot = slot;
        this.render = render;
        this.teamTotals = teamTotals;
        this.individuals = individuals;
        this.killPoints = killPoints;
        this.deathPoints = deathPoints;
        this.endsAtScore = endsAtScore;
        this.endsAfterMinutes = endsAfterMinutes;
        this.endsAfterRounds = endsAfterRounds;
        this.resultsCard = resultsCard;
        this.resultsTitle = resultsTitle;
        this.resultsIcon = resultsIcon;
        this.resultsImage = resultsImage;
        this.resultsBackground = resultsBackground;
        this.resultsTicks = resultsTicks;
        this.carries = carries;
        this.endsResets = endsResets;
        this.endsIntermission = endsIntermission;
        this.awardsTo = awardsTo;
        this.endsLocksTeams = endsLocksTeams;
        this.ownKillPoints = ownKillPoints;
        this.intermissionSays = intermissionSays;
        this.startsSays = startsSays;
    }

    public boolean ends() { return endsAtScore > 0 || endsAfterMinutes > 0 || endsAfterRounds > 0; }

    public boolean fed() { return !killPoints.isEmpty() || deathPoints != 0; }

    public boolean shown() { return !slot.isEmpty(); }
}
