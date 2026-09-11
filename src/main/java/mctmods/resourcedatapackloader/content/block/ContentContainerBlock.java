package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentContainerBlock extends BaseEntityBlock implements IContentContainer {
    private final BlockDef def;
    private final ContainerDef container;

    public ContentContainerBlock(BlockDef def, ContainerDef container, Properties properties) {
        super(properties);
        this.def = def;
        this.container = container;
        registerDefaultState(stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Override @Nonnull protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }

    @Override public ContainerDef container() { return container; }

    public BlockDef getDef() { return def; }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(HorizontalDirectionalBlock.FACING); }

    @Override @Nullable public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override @Nonnull protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override @Nonnull protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(HorizontalDirectionalBlock.FACING, mirror.mirror(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override @Nonnull protected RenderShape getRenderShape(@Nonnull BlockState state) { return container.chestModel() ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL; }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new ContentContainerBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        if (!level.isClientSide() || !container.chestModel()) { return null; }
        return createTickerHelper(type, ContentContainers.type(), ContentContainerBlockEntity::lidTick);
    }

    @Override @Nonnull protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity held) || !(player instanceof ServerPlayer server)) { return InteractionResult.PASS; }
        held.stock();
        server.openMenu(held, extra -> ContentContainerMenu.write(extra, container));
        return InteractionResult.CONSUME;
    }

    @Override protected void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        if (state.is(replaced.getBlock())) { return; }
        if (level.getBlockEntity(pos) instanceof ContentContainerBlockEntity held) {
            Containers.dropContents(level, pos, held);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replaced, moving);
    }

    @Override protected boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return true; }

    @Override protected int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        return ContentContainerBlockEntity.comparatorOutput(level.getBlockEntity(pos));
    }

    @Override protected boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int value) {
        super.triggerEvent(state, level, pos, id, value);
        BlockEntity held = level.getBlockEntity(pos);
        return held != null && held.triggerEvent(id, value);
    }
}
