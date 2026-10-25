package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentSpawners {
    private static final float UNBREAKABLE = -1.0F;
    private static final float BEDROCK_RESISTANCE = 18000000.0F;

    private ContentSpawners() {}

    public static void apply() {
        if (!Config.tweaks.unbreakableSpawners()) { return; }
        Block spawner = Blocks.SPAWNER;
        spawner.explosionResistance = BEDROCK_RESISTANCE;
        for (BlockState state : spawner.getStateDefinition().getPossibleStates()) { state.destroySpeed = UNBREAKABLE; }
        Summary.info("spawners.unbreakable", "Mob spawners cannot be mined or blown up, which is what the config asks for");
    }
}
