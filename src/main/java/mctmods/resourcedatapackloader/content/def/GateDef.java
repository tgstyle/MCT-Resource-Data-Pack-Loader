package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record GateDef(ResourceLocation key, @Nullable ResourceLocation dimension, String name, String scope, String teamless, boolean open, String craft, String consume, int consumeCount, String hold, String advancement,
                      String killed, int killedCount, String killedDrops, String killedTally, String killedRank, Standing standing, String teamAndScore, List<String> portalBlocks, String blockedMessage, String unlockedMessage,
                      boolean safeReturn, List<String> requires) {
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";
    public static final String TEAM = "team";
    public static final String ALONE = "alone";
    public static final String WITH = "with";
    public static final String REFUSE = "refuse";
    public static final List<String> SCOPES = List.of(PLAYER, GLOBAL, TEAM);

    public String id() { return key.toString(); }

    public boolean global() { return GLOBAL.equals(scope); }

    public boolean teamWide() { return TEAM.equals(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }

    public boolean refusesTeamless() { return teamWide() && REFUSE.equals(teamless); }

    public boolean tallyByTeam() { return TEAM.equals(killedTally); }

    public boolean standsAlone() { return ALONE.equals(teamAndScore); }
}
