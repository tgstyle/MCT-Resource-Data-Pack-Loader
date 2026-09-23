package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.server.level.ServerLevel;

public final class ContentEndDragon {
    private ContentEndDragon() {}

    public static boolean unwanted(ServerLevel level) {
        boolean unasked = !ContentVoidWorld.voidApplies(level) && Config.worldgen.dragonFight();
        return !ContentControl.flag(ContentControl.STRUCTURES, "dragonFight", unasked);
    }
}
