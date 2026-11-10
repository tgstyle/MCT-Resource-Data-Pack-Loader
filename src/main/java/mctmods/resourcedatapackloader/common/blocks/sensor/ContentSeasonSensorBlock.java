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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

@SuppressWarnings("deprecation") public final class ContentSeasonSensorBlock extends Block {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    private static final int POLL = 100;
    private final SeasonSensorDef sensor;

    public ContentSeasonSensorBlock(BlockDef def, Properties properties) {
        super(properties);
        this.sensor = def.sensor() == null ? SeasonSensorDef.PLAIN : def.sensor();
        registerDefaultState(stateDefinition.any().setValue(POWER, 0));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(POWER); }

    @Override public boolean isSignalSource(@Nonnull BlockState state) { return true; }

    @Override public int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction side) { return state.getValue(POWER); }

    @Override public boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return true; }

    @Override public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) { return state.getValue(POWER); }

    @Override public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState old, boolean moving) {
        if (level instanceof ServerLevel server) { refresh(server, pos, state); }
    }

    @Override public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { refresh(level, pos, state); }

    private void refresh(ServerLevel level, BlockPos pos, BlockState state) {
        level.scheduleTick(pos, this, POLL);
        int power = power(level);
        if (power != state.getValue(POWER)) { level.setBlock(pos, state.setValue(POWER, power), 3); }
    }

    private int power(ServerLevel level) {
        SeasonState season = ContentSeasons.state(sensor.dimension(), level.dimension().location());
        return season == null ? 0 : sensor.power(season.name(), season.index(), season.count());
    }
}
