package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record GateDef(Identifier key, Identifier dimension, String name, boolean global, boolean open, String craft, String consume, int consumeCount, String hold, String advancement,
                      String killed, int killedCount, String killedDrops, List<String> portalBlocks, String blockedMessage, String unlockedMessage, boolean safeReturn, List<String> requires) {
    public static final String GLOBAL = "global";
    public static final String PLAYER = "player";

    public String id() { return key.toString(); }
}
