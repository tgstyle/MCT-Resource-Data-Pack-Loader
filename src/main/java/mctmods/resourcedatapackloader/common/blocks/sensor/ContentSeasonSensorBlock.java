package mctmods.resourcedatapackloader.common.blocks.sensor;

import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SeasonSensorDef;

import com.mojang.serialization.MapCodec;
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

public final class ContentSeasonSensorBlock extends Block implements IContentBlock {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    private static final int POLL = 100;
    private final BlockDef def;
    private final SeasonSensorDef sensor;

    public ContentSeasonSensorBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.sensor = def.sensor() == null ? SeasonSensorDef.PLAIN : def.sensor();
        registerDefaultState(stateDefinition.any().setValue(POWER, 0));
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nonnull protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(POWER); }

    @Override protected boolean isSignalSource(@Nonnull BlockState state) { return true; }

    @Override protected int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return state.getValue(POWER); }

    @Override protected boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return true; }

    @Override protected int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) { return state.getValue(POWER); }

    @Override protected void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean moving) { refresh(level, pos, state); }

    @Override protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { refresh(level, pos, state); }

    private void refresh(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) { return; }
        level.scheduleTick(pos, this, POLL);
        int power = power(level);
        if (power == state.getValue(POWER)) { return; }
        level.setBlock(pos, state.setValue(POWER, power), Block.UPDATE_ALL);
        level.updateNeighborsAt(pos, this);
    }

    private int power(Level level) {
        SeasonState season = ContentSeasons.state(sensor.dimension(), level.dimension().location());
        return season == null ? 0 : sensor.power(season.name(), season.index(), season.count());
    }
}
