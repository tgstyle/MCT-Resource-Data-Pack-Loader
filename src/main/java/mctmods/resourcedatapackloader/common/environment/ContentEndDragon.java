package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;
import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.server.level.ServerLevel;

public final class ContentEndDragon {
    private ContentEndDragon() {}

    public static boolean unwanted(ServerLevel level) {
        boolean unasked = !ContentVoidWorld.voidApplies(level) && Config.worldgen.dragonFight();
        return !ContentControl.flag(ContentControl.STRUCTURES, "dragonFight", unasked);
    }
}
