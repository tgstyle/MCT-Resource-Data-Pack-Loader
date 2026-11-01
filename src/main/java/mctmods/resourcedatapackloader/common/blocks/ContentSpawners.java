package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.Summary;

import net.minecraft.init.Blocks;

public final class ContentSpawners {
    private static final float UNBREAKABLE = -1.0F;
    private static final float BEDROCK_RESISTANCE = 18000000.0F;

    private ContentSpawners() {}

    public static void apply() {
        if (!Config.tweaks.unbreakableSpawners) { return; }
        Blocks.MOB_SPAWNER.blockHardness = UNBREAKABLE;
        Blocks.MOB_SPAWNER.blockResistance = BEDROCK_RESISTANCE;
        Summary.info("spawners.unbreakable", "Mob spawners cannot be mined or blown up, which is what the config asks for");
    }
}
