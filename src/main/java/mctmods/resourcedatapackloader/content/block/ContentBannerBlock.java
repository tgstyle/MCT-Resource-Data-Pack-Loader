package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBannerBlock extends Block implements EntityBlock, IContentBanner {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private final BlockDef def;
    private final ResourceLocation texture;

    public ContentBannerBlock(BlockDef def, ResourceLocation id, Properties properties) {
        super(properties);
        this.def = def;
        this.texture = IContentBanner.textureOf(id);
        registerDefaultState(stateDefinition.any().setValue(ROTATION, 0));
    }

    public BlockDef getDef() { return def; }

    @Override public ResourceLocation texture() { return texture; }

    @Override @Nonnull public RenderShape getRenderShape(@Nonnull BlockState state) { return RenderShape.INVISIBLE; }

    @Override public boolean isPossibleToRespawnInThis(@Nonnull BlockState state) { return true; }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new ContentBannerBlockEntity(pos, state); }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) { return canSupportCenter(level, pos.below(), Direction.UP); }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) { return defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation() + 180.0F)); }

    @Override @Nonnull public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        return facing == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override @Nonnull public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) { return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16)); }

    @Override @Nonnull public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) { return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16)); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(ROTATION); }
}
