package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockBehaviour;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockStateBase;
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
        ((IBlockBehaviour) spawner).rdpl$setExplosionResistance(BEDROCK_RESISTANCE);
        for (BlockState state : spawner.getStateDefinition().getPossibleStates()) { ((IBlockStateBase) state).rdpl$setDestroySpeed(UNBREAKABLE); }
        Summary.info("spawners.unbreakable", "Mob spawners cannot be mined or blown up, which is what the config asks for");
    }
}
