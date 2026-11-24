package mctmods.resourcedatapackloader.common.pack.definitions.portals;

import mctmods.resourcedatapackloader.common.gates.GateLoot;
import mctmods.resourcedatapackloader.common.gates.GateStay;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.entity.WornCheck;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record GateDef(ResourceLocation key, @Nullable ResourceLocation dimension, String name, String scope, String teamless, boolean open, String craft, String consume, int consumeCount, String hold, String advancement,
                      @Nullable ResourceLocation killed, int killedCount, String killedDrops, String killedTally, String killedRank, Standing standing, String teamAndScore, List<String> portalBlocks, String blockedMessage, String unlockedMessage,
                      boolean safeReturn, List<String> requires, WornCheck worn, GateStay stay, GateLoot loot, List<DataCheck> data) {
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";
    public static final String TEAM = "team";
    public static final String ALONE = "alone";
    public static final String WITH = "with";
    public static final String REFUSE = "refuse";
    public static final List<String> SCOPES = List.of(PLAYER, GLOBAL, TEAM);

    public static boolean dimensionMissing(@Nullable ResourceLocation dimension, ResourceLocation key, String option, String consequence) {
        if (dimension != null) { return false; }
        ContentLog.LOGGER.error("Gate {} sets {} without dimension, so {}", key, option, consequence);
        return true;
    }

    public String id() { return key.toString(); }

    public static boolean global(String scope) { return GLOBAL.equals(scope); }

    public static boolean teamWide(String scope) { return TEAM.equals(scope); }

    public boolean global() { return global(scope); }

    public boolean teamWide() { return teamWide(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }

    public boolean refusesTeamless() { return teamWide() && REFUSE.equals(teamless); }

    public boolean tallyByTeam() { return TEAM.equals(killedTally); }

    public boolean standsAlone() { return ALONE.equals(teamAndScore); }
}
