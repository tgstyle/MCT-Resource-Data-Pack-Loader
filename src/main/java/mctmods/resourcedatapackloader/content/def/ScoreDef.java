package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.util.Map;
import javax.annotation.Nullable;

public record ScoreDef(String name, String displayName, ObjectiveCriteria criterion, String slot, @Nullable ObjectiveCriteria.RenderType render, boolean teamTotals,
                       boolean individuals, Map<String, Integer> killPoints, int deathPoints, int endsAtScore, int endsAfterMinutes, int endsAfterRounds, boolean resultsCard,
                       String resultsTitle, String resultsIcon, String resultsImage, int resultsBackground, int resultsTicks, boolean carries, boolean endsResets,
                       int endsIntermission, String awardsTo, boolean endsLocksTeams, int ownKillPoints, String intermissionSays, String startsSays, String opensBy,
                       String opensSays, String opensLeaderSays, @Nullable Place opensLobby, boolean opensLobbyJoins, String opensJoinsSays, boolean endsLastStanding,
                       String endsOutSays, RoundResetDef reset) {
    public static final String AUTO = "auto";
    public static final String LEADER = "leader";

    public record Place(ResourceKey<Level> dimension, int x, int y, int z) {}

    public boolean ends() { return endsAtScore > 0 || endsAfterMinutes > 0 || endsAfterRounds > 0 || endsLastStanding; }

    public boolean fed() { return !killPoints.isEmpty() || deathPoints != 0; }

    public boolean shown() { return !slot.isEmpty(); }
}
