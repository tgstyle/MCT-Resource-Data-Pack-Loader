package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.util.Map;
import javax.annotation.Nullable;

public record ScoreDef(String name, String displayName, ObjectiveCriteria criterion, String slot, @Nullable ObjectiveCriteria.RenderType render, boolean teamTotals,
                       boolean individuals, Map<String, Integer> killPoints, int deathPoints, int endsAtScore, int endsAfterMinutes, int endsAfterRounds, boolean resultsCard,
                       String resultsTitle, String resultsIcon, String resultsImage, int resultsBackground, int resultsTicks, boolean carries, boolean endsResets,
                       int endsIntermission, String awardsTo, boolean endsLocksTeams, int ownKillPoints, String intermissionSays, String startsSays) {
    public boolean ends() { return endsAtScore > 0 || endsAfterMinutes > 0 || endsAfterRounds > 0; }

    public boolean fed() { return !killPoints.isEmpty() || deathPoints != 0; }

    public boolean shown() { return !slot.isEmpty(); }
}
