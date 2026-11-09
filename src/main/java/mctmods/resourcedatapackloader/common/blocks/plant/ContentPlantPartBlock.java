package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ContentPlantPartBlock extends Block implements IContentBlock, BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final ThreadLocal<Boolean> PENDING = new ThreadLocal<>();
    private final BlockDef def;
    private final ResourceLocation baseId;
    private final int part;
    private final boolean crop;

    public static ContentPlantPartBlock crop(BlockDef def, ResourceLocation baseId, int part, Properties properties) { return create(def, baseId, part, properties, true); }

    public static ContentPlantPartBlock flower(BlockDef def, ResourceLocation baseId, int part, Properties properties) { return create(def, baseId, part, properties, false); }

    private static ContentPlantPartBlock create(BlockDef def, ResourceLocation baseId, int part, Properties properties, boolean crop) {
        PENDING.set(crop);
        try { return new ContentPlantPartBlock(def, baseId, part, properties, crop); }
        finally { PENDING.remove(); }
    }

    private ContentPlantPartBlock(BlockDef def, ResourceLocation baseId, int part, Properties properties, boolean crop) {
        super(properties.noOcclusion());
        this.def = def;
        this.baseId = baseId;
        this.part = part;
        this.crop = crop;
        registerDefaultState(crop ? stateDefinition.any().setValue(AGE, 0) : stateDefinition.any());
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nonnull protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { if (Boolean.TRUE.equals(PENDING.get())) { builder.add(AGE); } }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override @Nonnull protected VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return Shapes.empty(); }

    @Override protected boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        Block base = base();
        if (base == null) { return false; }
        Block below = level.getBlockState(pos.below()).getBlock();
        return below == (part == 1 ? base : TallPlants.partBlock(base, part - 1));
    }

    @Override @Nonnull protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbor, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) { level.scheduleTick(pos, this, 1); }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { if (!state.canSurvive(level, pos)) { level.destroyBlock(pos, true); } }

    @Override @Nonnull public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        Block base = base();
        if (base == null) { return ItemStack.EMPTY; }
        if (crop) { return base instanceof ContentCropBlock cropBlock ? new ItemStack(cropBlock.seed()) : ItemStack.EMPTY; }
        return new ItemStack(base);
    }

    @Override @Nonnull public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        BlockPos basePos = TallPlants.base(level, pos);
        TallDef tall = def.tall();
        if (!level.isClientSide() && basePos != null && tall != null) {
            if (!crop || !tall.regrow()) {
                if (player.isCreative()) { TallPlants.removeAbove(level, basePos, false); }
                level.destroyBlock(basePos, !player.isCreative());
            }
            else {
                BlockState baseState = level.getBlockState(basePos);
                level.setBlock(basePos, baseState.setValue(AGE, TallPlants.regrowAge(tall, part)), Block.UPDATE_ALL);
                TallPlants.sync(level, basePos);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (!crop || !(level instanceof Level world)) { return false; }
        BlockPos basePos = TallPlants.base(world, pos);
        if (basePos == null) { return false; }
        BlockState baseState = world.getBlockState(basePos);
        return baseState.getBlock() instanceof BonemealableBlock growable && growable.isValidBonemealTarget(level, basePos, baseState);
    }

    @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (!crop) { return false; }
        BlockPos basePos = TallPlants.base(level, pos);
        if (basePos == null) { return false; }
        BlockState baseState = level.getBlockState(basePos);
        return baseState.getBlock() instanceof BonemealableBlock growable && growable.isBonemealSuccess(level, random, basePos, baseState);
    }

    @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (!crop) { return; }
        BlockPos basePos = TallPlants.base(level, pos);
        if (basePos == null) { return; }
        BlockState baseState = level.getBlockState(basePos);
        if (baseState.getBlock() instanceof BonemealableBlock growable) { growable.performBonemeal(level, random, basePos, baseState); }
    }

    @Nullable private Block base() { return BuiltInRegistries.BLOCK.getOptional(baseId).orElse(null); }
}
