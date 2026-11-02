package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;

import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class ContentFlowingFluid extends BaseFlowingFluid {
    private static final int SOURCE_AMOUNT = 8;
    private final FluidBehaviorDef behavior;
    private final int defaultReach;
    private final boolean limited;
    private final boolean intervals;

    protected ContentFlowingFluid(Properties properties, FluidDef def) {
        super(properties);
        behavior = def.behavior();
        limited = behavior.maxSpread() > 0;
        defaultReach = ContentFluids.quantaReach(def.quantaPerBlock());
        intervals = behavior.reactions().stream().anyMatch(rule -> rule.interval > 0);
    }

    private int floor(Level level) {
        FluidBehaviorDef.Row row = behavior.rowFor(level);
        int reach = row != null && row.spread() > 0 ? row.spread() : behavior.spread() > 0 ? behavior.spread() : defaultReach;
        return SOURCE_AMOUNT - Math.min(reach, SOURCE_AMOUNT - 1);
    }

    protected final FluidState limit(Level level, FluidState next) {
        if (!limited || next.isEmpty() || next.isSource() || next.getValue(FALLING) || next.getAmount() >= floor(level)) { return next; }
        return Fluids.EMPTY.defaultFluidState();
    }

    protected final Map<Direction, FluidState> filled(Map<Direction, FluidState> spread) {
        spread.values().removeIf(FluidState::isEmpty);
        return spread;
    }

    @Override public int getTickDelay(@Nonnull LevelReader level) {
        if (level instanceof Level real) {
            FluidBehaviorDef.Row row = behavior.rowFor(real);
            if (row != null && row.flowTicks() > 0) { return row.flowTicks(); }
        }
        return super.getTickDelay(level);
    }

    @Override public boolean canConvertToSource(@Nonnull FluidState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        FluidBehaviorDef.Row row = behavior.rowFor(level);
        Boolean infinite = row == null ? null : row.infinite();
        if (infinite != null) { return infinite; }
        return behavior.infinite() || super.canConvertToSource(state, level, pos);
    }

    @Override @Nonnull protected FluidState getNewLiquid(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) { return limit(level, super.getNewLiquid(level, pos, state)); }

    @Override @Nonnull protected Map<Direction, FluidState> getSpread(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) { return filled(super.getSpread(level, pos, state)); }

    @Override public void tick(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidState state) {
        if (!behavior.reactions().isEmpty() && FluidReactions.check(level, pos, level.getBlockState(pos), false)) { return; }
        super.tick(level, pos, state);
    }

    @Override protected boolean isRandomlyTicking() { return intervals; }

    @Override protected void randomTick(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull FluidState state, @Nonnull RandomSource random) { FluidReactions.check(level, pos, level.getBlockState(pos), true); }

    public static final class Source extends ContentFlowingFluid {
        public Source(Properties properties, FluidDef def) { super(properties, def); }

        @Override public int getAmount(@Nonnull FluidState state) { return SOURCE_AMOUNT; }

        @Override public boolean isSource(@Nonnull FluidState state) { return true; }
    }

    public static final class Flowing extends ContentFlowingFluid {
        public Flowing(Properties properties, FluidDef def) {
            super(properties, def);
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
