package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;
import mctmods.resourcedatapackloader.core.util.LineCompat;

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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ContentPlantPartBlock extends LineCompat.PartBase implements IContentBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final ThreadLocal<Boolean> PENDING = new ThreadLocal<>();
    private final BlockDef def;
    private final boolean crop;
    private final int part;

    public static ContentPlantPartBlock crop(BlockDef def, int part, Properties properties) { return build(def, true, part, properties); }

    public static ContentPlantPartBlock flower(BlockDef def, int part, Properties properties) { return build(def, false, part, properties); }

    private static ContentPlantPartBlock build(BlockDef def, boolean crop, int part, Properties properties) {
        PENDING.set(crop);
        try { return new ContentPlantPartBlock(def, crop, part, properties); }
        finally { PENDING.remove(); }
    }

    private ContentPlantPartBlock(BlockDef def, boolean crop, int part, Properties properties) {
        super(properties.noOcclusion());
        this.def = def;
        this.crop = crop;
        this.part = part;
        registerDefaultState(crop ? stateDefinition.any().setValue(AGE, 0) : stateDefinition.any());
    }

    @Override public BlockDef getDef() { return def; }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { if (Boolean.TRUE.equals(PENDING.get())) { builder.add(AGE); } }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override @Nonnull protected VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return Shapes.empty(); }

    @Override protected boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        Block base = TallPlants.baseBlockOf(this);
        if (base == null) { return false; }
        Block below = level.getBlockState(pos.below()).getBlock();
        return below == (part == 1 ? base : TallPlants.partBlock(base, part - 1));
    }

    @Override @Nonnull protected BlockState updateShape(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull ScheduledTickAccess ticks, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull BlockPos neighborPos, @Nonnull BlockState neighbor, @Nonnull RandomSource random) {
        if (!state.canSurvive(level, pos)) { ticks.scheduleTick(pos, this, 1); }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
    }

    @Override protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { if (!state.canSurvive(level, pos)) { level.destroyBlock(pos, true); } }

    @Override @SuppressWarnings("deprecation") @Nonnull protected ItemStack getCloneItemStack(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean includeData) {
        Block base = TallPlants.baseBlockOf(this);
        if (base == null) { return ItemStack.EMPTY; }
        if (crop) { return base instanceof ContentCropBlock cropBlock ? new ItemStack(cropBlock.seed()) : ItemStack.EMPTY; }
        return new ItemStack(base);
    }

    @Override @Nonnull public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        BlockPos base = TallPlants.base(level, pos);
        TallDef tall = def.tall();
        if (!level.isClientSide() && base != null && tall != null) {
            if (!crop || !tall.regrow()) {
                if (player.isCreative()) { TallPlants.removeAbove(level, base, false); }
                level.destroyBlock(base, !player.isCreative());
            }
            else {
                BlockState baseState = level.getBlockState(base);
                level.setBlock(base, baseState.setValue(AGE, TallPlants.regrowAge(tall, part)), Block.UPDATE_ALL);
                TallPlants.sync(level, base);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override @Nullable protected BlockPos growBase(@Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (!crop || !(level instanceof Level world)) { return null; }
        BlockPos base = TallPlants.base(world, pos);
        return base != null && world.getBlockState(base).getBlock() instanceof BonemealableBlock ? base : null;
    }
}
