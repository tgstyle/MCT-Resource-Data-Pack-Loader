package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public final class ContentCaneBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private final BlockDef def;
    private final GrowthDef growth;
    private Set<Block> soil = Collections.emptySet();

    public ContentCaneBlock(BlockDef def, GrowthDef growth, Properties properties) {
        super(properties);
        this.def = def;
        this.growth = growth;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    public BlockDef getDef() { return def; }

    public void resolveSoil() {
        Set<Block> resolved = new HashSet<>();
        for (String name : growth.soil()) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Block block = key == null ? null : ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) { resolved.add(block); }
        }
        soil = resolved;
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(AGE); }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override @Nonnull public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return growth.damage() ? SHAPE : Shapes.empty(); }

    @Override public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!level.isEmptyBlock(pos.above())) { return; }
        int height = 1;
        while (level.getBlockState(pos.below(height)).is(this)) { height++; }
        if (height >= growth.maxHeight()) { return; }
        int age = state.getValue(AGE);
        if (!ForgeHooks.onCropsGrowPre(level, pos, state, true)) { return; }
        if (age >= growth.stages() - 1) {
            level.setBlockAndUpdate(pos.above(), defaultBlockState());
            level.setBlock(pos, state.setValue(AGE, 0), 4);
        }
        else { level.setBlock(pos, state.setValue(AGE, age + 1), 4); }
        ForgeHooks.onCropsGrowPost(level, pos, state);
    }

    @Override @Nonnull public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbor, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) { level.scheduleTick(pos, this, 1); }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!state.canSurvive(level, pos)) { level.destroyBlock(pos, true); }
    }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        BlockPos below = pos.below();
        BlockState under = level.getBlockState(below);
        if (under.is(this)) { return true; }
        if (!soil.isEmpty() && !soil.contains(under.getBlock())) { return false; }
        if (growth.needsSky() && !level.canSeeSky(pos)) { return false; }
        if (growth.breaksNeighbors() && crowded(level, pos)) { return false; }
        if (!growth.needsWater()) { return true; }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int step = 1; step <= growth.waterRange(); step++) {
                BlockPos side = below.relative(facing, step);
                BlockState held = level.getBlockState(side);
                if (level.getFluidState(side).is(Fluids.WATER) || level.getFluidState(side).is(Fluids.FLOWING_WATER)) { return true; }
                if (held.isAir()) { continue; }
                break;
            }
        }
        return false;
    }

    private boolean crowded(LevelReader level, BlockPos pos) {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockState side = level.getBlockState(pos.relative(facing));
            if (side.isSolid() || side.is(Blocks.LAVA)) { return true; }
        }
        return false;
    }

    @Override public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (growth.damage()) { entity.hurt(level.damageSources().cactus(), growth.damageAmount()); }
    }
}
