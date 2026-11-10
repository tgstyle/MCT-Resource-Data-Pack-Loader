package mctmods.resourcedatapackloader.common.blocks.sensor;

import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SensorSignal extends ContentBlock {
    protected SensorSignal(BlockDef def, Properties properties) { super(def, properties); }

    protected abstract int output(BlockState state);

    @Override protected int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return output(state); }
}
