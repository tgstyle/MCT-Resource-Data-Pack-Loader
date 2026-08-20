package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.world.World;
import javax.annotation.Nullable;

public final class ContentEndDragon {
    private ContentEndDragon() {}

    public static boolean wanted(@Nullable World world) {
        if (world == null) { return true; }
        boolean unasked = ContentVoidWorld.appliesTo(world) ? false : Config.worldgen.dragonFight;
        return ContentControl.flag(ContentControl.STRUCTURES, "dragonFight", unasked);
    }
}
