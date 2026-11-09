package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation") public final class ContentPlantPartBlock extends Block implements BonemealableBlock {
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final ThreadLocal<Boolean> FLOWER = new ThreadLocal<>();
    private final BlockDef def;
    private final int part;
    private final boolean flower;

    public ContentPlantPartBlock(BlockDef def, int part, Properties properties) {
        super(properties.noOcclusion());
        this.def = def;
        this.part = part;
        this.flower = Boolean.TRUE.equals(FLOWER.get());
        registerDefaultState(flower ? stateDefinition.any() : stateDefinition.any().setValue(BlockStateProperties.AGE_7, 0));
    }

    public static ContentPlantPartBlock flower(BlockDef def, int part, Properties properties) {
        FLOWER.set(Boolean.TRUE);
        try { return new ContentPlantPartBlock(def, part, properties); }
        finally { FLOWER.remove(); }
    }

    public BlockDef def() { return def; }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { if (!Boolean.TRUE.equals(FLOWER.get())) { builder.add(BlockStateProperties.AGE_7); } }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override @Nonnull public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return Shapes.empty(); }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        Block base = TallPlants.baseBlockOf(this);
        if (base == null) { return false; }
        return level.getBlockState(pos.below()).getBlock() == (part == 1 ? base : TallPlants.partBlock(base, part - 1));
    }

    @Override @Nonnull public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbor, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) { level.scheduleTick(pos, this, 1); }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { if (!state.canSurvive(level, pos)) { level.destroyBlock(pos, true); } }

    @Override @Nonnull public ItemStack getCloneItemStack(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        Block base = TallPlants.baseBlockOf(this);
        if (base == null) { return ItemStack.EMPTY; }
        if (!flower && base instanceof ContentCropBlock crop) { return new ItemStack(crop.seed()); }
        return new ItemStack(base);
    }

    @Override public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        BlockPos base = TallPlants.base(level, pos);
        TallDef tall = def.tall();
        if (base != null && tall != null && !level.isClientSide) {
            if (flower || !tall.regrow()) {
                if (player.isCreative()) { TallPlants.removeAbove(level, base, false); }
                level.destroyBlock(base, !player.isCreative());
            }
            else {
                BlockState baseState = level.getBlockState(base);
                level.setBlock(base, baseState.setValue(BlockStateProperties.AGE_7, TallPlants.regrowAge(tall, part)), Block.UPDATE_ALL);
                TallPlants.sync(level, base);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable private BonemealableBlock growable(LevelReader level, BlockPos pos) {
        Block base = TallPlants.baseBlockOf(this);
        if (flower || base == null || level.getBlockState(pos.below(part)).getBlock() != base) { return null; }
        return base instanceof BonemealableBlock growable ? growable : null;
    }

    @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
        BonemealableBlock growable = growable(level, pos);
        return growable != null && growable.isValidBonemealTarget(level, pos.below(part), level.getBlockState(pos.below(part)), isClient);
    }

    @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BonemealableBlock growable = growable(level, pos);
        return growable != null && growable.isBonemealSuccess(level, random, pos.below(part), level.getBlockState(pos.below(part)));
    }

    @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BonemealableBlock growable = growable(level, pos);
        if (growable != null) { growable.performBonemeal(level, random, pos.below(part), level.getBlockState(pos.below(part))); }
    }
}
