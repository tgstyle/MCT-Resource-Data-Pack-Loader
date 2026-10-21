package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class GateDef {
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";
    public static final String TEAM = "team";
    public final ResourceLocation registryName;
    public final int dimension;
    public final String name;
    public final String scope;
    public final boolean open;
    public final String craft;
    public final String consume;
    public final int consumeCount;
    public final String hold;
    public final String advancement;
    public final String killed;
    public final int killedCount;
    public final String killedDrops;
    public final Standing standing;
    public final List<String> portalBlocks;
    public final String blockedMessage;
    public final String unlockedMessage;
    public final boolean safeReturn;
    public final List<String> requires;

    public GateDef(ResourceLocation registryName, int dimension, String name, String scope, boolean open, String craft, String consume, int consumeCount, String hold, String advancement, String killed, int killedCount, String killedDrops, Standing standing, List<String> portalBlocks, String blockedMessage, String unlockedMessage, boolean safeReturn, List<String> requires) {
        this.registryName = registryName;
        this.dimension = dimension;
        this.name = name;
        this.scope = scope;
        this.open = open;
        this.craft = craft;
        this.consume = consume;
        this.consumeCount = consumeCount;
        this.hold = hold;
        this.advancement = advancement;
        this.killed = killed;
        this.killedCount = killedCount;
        this.killedDrops = killedDrops;
        this.standing = standing;
        this.portalBlocks = portalBlocks;
        this.blockedMessage = blockedMessage;
        this.unlockedMessage = unlockedMessage;
        this.safeReturn = safeReturn;
        this.requires = requires;
    }

    public String getKey() { return registryName.toString(); }

    public boolean global() { return GLOBAL.equals(scope); }

    public boolean teamWide() { return TEAM.equals(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }
}
