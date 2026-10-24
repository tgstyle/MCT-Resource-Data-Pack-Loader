package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class GateDef {
    public static final int NO_DIMENSION = Integer.MIN_VALUE;
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";
    public static final String TEAM = "team";
    public static final String ALONE = "alone";
    public static final String WITH = "with";
    public static final String REFUSE = "refuse";
    public final ResourceLocation registryName;
    public final int dimension;
    public final String name;
    public final String scope;
    public final String teamless;
    public final boolean open;
    public final String craft;
    public final String consume;
    public final int consumeCount;
    public final String hold;
    public final String advancement;
    public final String killed;
    public final int killedCount;
    public final String killedDrops;
    public final String killedTally;
    public final Standing standing;
    public final String teamAndScore;
    public final List<String> portalBlocks;
    public final String blockedMessage;
    public final String unlockedMessage;
    public final boolean safeReturn;
    public final List<String> requires;

    public GateDef(ResourceLocation registryName, int dimension, String name, String scope, String teamless, boolean open, String craft, String consume, int consumeCount, String hold, String advancement, String killed, int killedCount, String killedDrops, String killedTally, Standing standing, String teamAndScore, List<String> portalBlocks, String blockedMessage, String unlockedMessage, boolean safeReturn, List<String> requires) {
        this.registryName = registryName;
        this.dimension = dimension;
        this.name = name;
        this.scope = scope;
        this.teamless = teamless;
        this.open = open;
        this.craft = craft;
        this.consume = consume;
        this.consumeCount = consumeCount;
        this.hold = hold;
        this.advancement = advancement;
        this.killed = killed;
        this.killedCount = killedCount;
        this.killedDrops = killedDrops;
        this.killedTally = killedTally;
        this.standing = standing;
        this.teamAndScore = teamAndScore;
        this.portalBlocks = portalBlocks;
        this.blockedMessage = blockedMessage;
        this.unlockedMessage = unlockedMessage;
        this.safeReturn = safeReturn;
        this.requires = requires;
    }

    public String getKey() { return registryName.toString(); }

    public boolean guardsDimension() { return dimension != NO_DIMENSION; }

    public boolean global() { return GLOBAL.equals(scope); }

    public boolean teamWide() { return TEAM.equals(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }

    public boolean refusesTeamless() { return teamWide() && REFUSE.equals(teamless); }

    public boolean tallyByTeam() { return TEAM.equals(killedTally); }

    public boolean standsAlone() { return ALONE.equals(teamAndScore); }
}
