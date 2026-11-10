package mctmods.resourcedatapackloader.common.blocks.sensor;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SeasonSensorDef;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class ContentSeasonSensorBlock extends SensorSignal {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private static final int POLL = 100;
    private final SeasonSensorDef sensor;

    public ContentSeasonSensorBlock(BlockDef def, Properties properties) {
        super(def, properties);
        this.sensor = def.sensor() == null ? SeasonSensorDef.PLAIN : def.sensor();
        registerDefaultState(stateDefinition.any().setValue(POWER, 0));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(POWER); }

    @Override protected int output(BlockState state) { return state.getValue(POWER); }

    @Override protected boolean isSignalSource(@Nonnull BlockState state) { return true; }

    @Override protected boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return true; }

    @Override protected int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return state.getValue(POWER); }

    @Override protected void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && !oldState.is(this)) { level.scheduleTick(pos, this, 1); }
    }

    @Override protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        level.scheduleTick(pos, this, POLL);
        int power = power(level);
        if (power != state.getValue(POWER)) { level.setBlock(pos, state.setValue(POWER, power), 3); }
    }

    private int power(ServerLevel level) {
        SeasonState season = ContentSeasons.state(sensor.dimension(), level.dimension().identifier());
        return season == null ? 0 : sensor.power(season.name(), season.index(), season.count());
    }
}
