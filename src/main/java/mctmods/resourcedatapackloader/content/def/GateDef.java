package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record GateDef(ResourceLocation key, ResourceLocation dimension, String name, String scope, boolean open, String craft, String consume, int consumeCount, String hold, String advancement,
                      String killed, int killedCount, String killedDrops, Standing standing, List<String> portalBlocks, String blockedMessage, String unlockedMessage, boolean safeReturn, List<String> requires) {
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";
    public static final String TEAM = "team";

    public String id() { return key.toString(); }

    public boolean global() { return GLOBAL.equals(scope); }

    public boolean teamWide() { return TEAM.equals(scope); }

    public boolean perPlayer() { return PLAYER.equals(scope); }
}
