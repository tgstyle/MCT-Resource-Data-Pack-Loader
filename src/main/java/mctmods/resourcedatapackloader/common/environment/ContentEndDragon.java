package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;
import mctmods.resourcedatapackloader.core.config.Config;

import javax.annotation.Nullable;
import net.minecraft.world.World;

public final class ContentEndDragon {
    private ContentEndDragon() {}

    public static boolean wanted(@Nullable World world) {
        if (world == null) { return true; }
        boolean unasked = !ContentVoidWorld.appliesTo(world) && Config.worldgen.dragonFight;
        return ContentControl.flag(ContentControl.STRUCTURES, "dragonFight", unasked);
    }
}
