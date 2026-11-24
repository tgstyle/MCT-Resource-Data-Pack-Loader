package mctmods.resourcedatapackloader.common.pack.definitions.portals;

import mctmods.resourcedatapackloader.common.gates.GateLoot;
import mctmods.resourcedatapackloader.common.gates.GateStay;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.entity.WornCheck;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import net.minecraft.util.ResourceLocation;

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
    public final String killedRank;
    public final Standing standing;
    public final String teamAndScore;
    public final List<String> portalBlocks;
    public final String blockedMessage;
    public final String unlockedMessage;
    public final boolean safeReturn;
    public final List<String> requires;
    public final WornCheck worn;
    public final GateStay stay;
    public final GateLoot loot;
    public final List<DataCheck> data;

    public GateDef(ResourceLocation registryName, int dimension, String name, String scope, String teamless, boolean open, String craft, String consume, int consumeCount, String hold, String advancement, String killed, int killedCount, String killedDrops, String killedTally, String killedRank, Standing standing, String teamAndScore, List<String> portalBlocks, String blockedMessage, String unlockedMessage, boolean safeReturn, List<String> requires, WornCheck worn, GateStay stay, GateLoot loot, List<DataCheck> data) {
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
        this.killedRank = killedRank;
        this.standing = standing;
        this.teamAndScore = teamAndScore;
        this.portalBlocks = portalBlocks;
        this.blockedMessage = blockedMessage;
        this.unlockedMessage = unlockedMessage;
        this.safeReturn = safeReturn;
        this.requires = requires;
        this.worn = worn;
        this.stay = stay;
        this.loot = loot;
        this.data = data;
    }

    public String getKey() { return registryName.toString(); }

    public boolean guardsDimension() { return dimension != NO_DIMENSION; }

    public static boolean dimensionMissing(int dimension, ResourceLocation key, String option, String consequence) {
        if (dimension != NO_DIMENSION) { return false; }
        ContentLog.LOGGER.error("Gate {} sets {} without dimension, so {}", key, option, consequence);
        return true;
    }

    public static boolean global(String scope) { return GLOBAL.equals(scope); }

    public static boolean teamWide(String scope) { return TEAM.equals(scope); }

    public boolean global() { return global(scope); }

    public boolean teamWide() { return teamWide(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }

    public boolean refusesTeamless() { return teamWide() && REFUSE.equals(teamless); }

    public boolean tallyByTeam() { return TEAM.equals(killedTally); }

    public boolean standsAlone() { return ALONE.equals(teamAndScore); }
}
