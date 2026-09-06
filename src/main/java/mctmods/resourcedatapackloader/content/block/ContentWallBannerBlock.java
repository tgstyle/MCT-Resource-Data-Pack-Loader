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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentWallBannerBlock extends Block implements EntityBlock, IContentBanner {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    private final BlockDef def;
    private final ResourceLocation texture;

    static {
        SHAPES.put(Direction.NORTH, Block.box(0.0D, 0.0D, 14.0D, 16.0D, 12.5D, 16.0D));
        SHAPES.put(Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.5D, 2.0D));
        SHAPES.put(Direction.WEST, Block.box(14.0D, 0.0D, 0.0D, 16.0D, 12.5D, 16.0D));
        SHAPES.put(Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 12.5D, 16.0D));
    }

    public ContentWallBannerBlock(BlockDef def, ResourceLocation id, Properties properties) {
        super(properties);
        this.def = def;
        this.texture = IContentBanner.textureOf(id);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockDef getDef() { return def; }

    @Override public ResourceLocation texture() { return texture; }

    @Override @Nonnull public String getDescriptionId() { return asItem().getDescriptionId(); }

    @Override @Nonnull public RenderShape getRenderShape(@Nonnull BlockState state) { return RenderShape.INVISIBLE; }

    @Override public boolean isPossibleToRespawnInThis(@Nonnull BlockState state) { return true; }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new ContentBannerBlockEntity(pos, state); }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos behind = pos.relative(facing.getOpposite());
        return level.getBlockState(behind).isFaceSturdy(level, behind, facing);
    }

    @Override @Nonnull public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        return facing == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPES.get(state.getValue(FACING)); }

    @Override @Nullable public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : context.getNearestLookingDirections()) {
            if (!direction.getAxis().isHorizontal()) { continue; }
            state = state.setValue(FACING, direction.getOpposite());
            if (state.canSurvive(level, pos)) { return state; }
        }
        return null;
    }

    @Override @Nonnull public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }

    @Override @Nonnull public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) { return state.setValue(FACING, mirror.getRotation(state.getValue(FACING)).rotate(state.getValue(FACING))); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
}
