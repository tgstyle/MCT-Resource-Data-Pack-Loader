package mctmods.resourcedatapackloader.content.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import java.util.Map;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public abstract class ContentRisingFluid extends BaseFlowingFluid {
    private static final int FAR = 1000;
    private static final int SIDES_FROM_SOURCES = 3;

    protected ContentRisingFluid(Properties properties) { super(properties); }

    @Override protected void spread(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidState state) {
        if (state.isEmpty()) { return; }
        BlockState here = level.getBlockState(pos);
        BlockPos up = pos.above();
        BlockState upState = level.getBlockState(up);
        FluidState rising = getNewLiquid(level, up, upState);
        if (canSpreadTo(level, pos, here, Direction.UP, up, upState, level.getFluidState(up), rising.getType())) {
            spreadTo(level, up, upState, Direction.UP, rising);
            if (sourceNeighbors(level, pos) >= SIDES_FROM_SOURCES) { spreadToSides(level, pos, state, here); }
        }
        else if (state.isSource() || !hole(level, pos, here, up, upState, rising.getType())) { spreadToSides(level, pos, state, here); }
    }

    private void spreadToSides(Level level, BlockPos pos, FluidState state, BlockState here) {
        int amount = state.getValue(FALLING) ? 7 : state.getAmount() - getDropOff(level);
        if (amount <= 0) { return; }
        for (Map.Entry<Direction, FluidState> entry : getSpread(level, pos, here).entrySet()) {
            BlockPos side = pos.relative(entry.getKey());
            BlockState sideState = level.getBlockState(side);
            if (canSpreadTo(level, pos, here, entry.getKey(), side, sideState, level.getFluidState(side), entry.getValue().getType())) { spreadTo(level, side, sideState, entry.getKey(), entry.getValue()); }
        }
    }

    @Override @Nonnull protected FluidState getNewLiquid(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int most = 0;
        int sources = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(direction);
            BlockState sideState = level.getBlockState(side);
            FluidState fluid = sideState.getFluidState();
            if (fluid.getType().isSame(this) && through(level, pos, state, direction, side, sideState, this)) {
                if (fluid.isSource() && EventHooks.canCreateFluidSource(level, side, sideState)) { sources++; }
                most = Math.max(most, fluid.getAmount());
            }
        }
        if (sources >= 2) {
            BlockState above = level.getBlockState(pos.above());
            if (above.isSolid() || sourceOfThis(above.getFluidState())) { return getSource(false); }
        }
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        FluidState belowFluid = belowState.getFluidState();
        if (!belowFluid.isEmpty() && belowFluid.getType().isSame(this) && through(level, pos, state, Direction.DOWN, below, belowState, this)) { return getFlowing(8, true); }
        int amount = most - getDropOff(level);
        return amount <= 0 ? Fluids.EMPTY.defaultFluidState() : getFlowing(amount, false);
    }

    @Override @Nonnull protected Map<Direction, FluidState> getSpread(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int best = FAR;
        Map<Direction, FluidState> spread = Maps.newEnumMap(Direction.class);
        Short2ObjectMap<Pair<BlockState, FluidState>> states = new Short2ObjectOpenHashMap<>();
        Short2BooleanMap holes = new Short2BooleanOpenHashMap();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(direction);
            short key = key(pos, side);
            Pair<BlockState, FluidState> held = states.computeIfAbsent(key, ignored -> read(level, side));
            FluidState next = getNewLiquid(level, side, held.getFirst());
            if (blocked(level, next.getType(), pos, state, direction, side, held)) { continue; }
            int distance = holes.computeIfAbsent(key, ignored -> holeAbove(level, side, held.getFirst())) ? 0 : getSlopeDistance(level, side, 1, direction.getOpposite(), held.getFirst(), pos, states, holes);
            if (distance < best) { spread.clear(); }
            if (distance <= best) {
                spread.put(direction, next);
                best = distance;
            }
        }
        return spread;
    }

    @Override protected int getSlopeDistance(@Nonnull LevelReader level, @Nonnull BlockPos pos, int distance, @Nonnull Direction from, @Nonnull BlockState state, @Nonnull BlockPos origin, @Nonnull Short2ObjectMap<Pair<BlockState, FluidState>> states, @Nonnull Short2BooleanMap holes) {
        int best = FAR;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == from) { continue; }
            BlockPos side = pos.relative(direction);
            short key = key(origin, side);
            Pair<BlockState, FluidState> held = states.computeIfAbsent(key, ignored -> read(level, side));
            if (blocked(level, getFlowing(), pos, state, direction, side, held)) { continue; }
            if (holes.computeIfAbsent(key, ignored -> holeAbove(level, side, held.getFirst()))) { return distance; }
            if (distance < getSlopeFindDistance(level)) { best = Math.min(best, getSlopeDistance(level, side, distance + 1, direction.getOpposite(), held.getFirst(), origin, states, holes)); }
        }
        return best;
    }

    private boolean holeAbove(BlockGetter level, BlockPos pos, BlockState state) { return hole(level, pos, state, pos.above(), level.getBlockState(pos.above()), getFlowing()); }

    private boolean hole(BlockGetter level, BlockPos pos, BlockState state, BlockPos up, BlockState upState, Fluid fluid) { return through(level, pos, state, Direction.UP, up, upState, upState.getFluidState().getType().isSame(this) ? this : fluid); }

    private boolean blocked(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, Direction direction, BlockPos side, Pair<BlockState, FluidState> held) { return sourceOfThis(held.getSecond()) || !through(level, pos, state, direction, side, held.getFirst(), fluid); }

    private boolean through(BlockGetter level, BlockPos pos, BlockState state, Direction direction, BlockPos side, BlockState sideState, Fluid fluid) { return canSpreadTo(level, pos, state, direction, side, sideState, Fluids.EMPTY.defaultFluidState(), fluid); }

    private boolean sourceOfThis(FluidState state) { return state.getType().isSame(this) && state.isSource(); }

    private int sourceNeighbors(LevelReader level, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (sourceOfThis(level.getFluidState(pos.relative(direction)))) { count++; }
        }
        return count;
    }

    private static Pair<BlockState, FluidState> read(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return Pair.of(state, state.getFluidState());
    }

    private static short key(BlockPos origin, BlockPos pos) { return (short) ((pos.getX() - origin.getX() + 128 & 0xFF) << 8 | pos.getZ() - origin.getZ() + 128 & 0xFF); }

    public static final class Source extends ContentRisingFluid {
        public Source(Properties properties) { super(properties); }

        @Override public int getAmount(@Nonnull FluidState state) { return 8; }

        @Override public boolean isSource(@Nonnull FluidState state) { return true; }
    }

    public static final class Flowing extends ContentRisingFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override protected void createFluidStateDefinition(@Nonnull StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override public int getAmount(@Nonnull FluidState state) { return state.getValue(LEVEL); }

        @Override public boolean isSource(@Nonnull FluidState state) { return false; }
    }
}
