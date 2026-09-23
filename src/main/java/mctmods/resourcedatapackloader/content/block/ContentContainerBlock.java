package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.ContentDrops;
import mctmods.resourcedatapackloader.content.def.AmountDef;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentContainerBlock extends BaseEntityBlock implements IContentContainer {
    private final ContainerDef container;
    private final BlockDef def;
    @Nullable private final VoxelShape shape;
    private final AmountDef expDrop;

    public ContentContainerBlock(BlockDef def, Properties properties) {
        super(properties);
        this.container = def.container() == null ? ContentContainers.FALLBACK : def.container();
        this.def = def;
        this.shape = ContentBlock.shape(def);
        this.expDrop = ContentBlock.expRange(def);
        registerDefaultState(stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
    }

    @Override @Nonnull protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }

    @Override public ContainerDef container() { return container; }

    public BlockDef getDef() { return def; }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(HorizontalDirectionalBlock.FACING); }

    @Override @Nonnull public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override @Nonnull protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override @Nonnull protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(HorizontalDirectionalBlock.FACING, mirror.mirror(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override @Nonnull protected RenderShape getRenderShape(@Nonnull BlockState state) { return container.chestModel() ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL; }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return shape == null ? super.getShape(state, level, pos, context) : shape; }

    @Override @Nonnull public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new ContentContainerBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        if (!level.isClientSide() || !container.chestModel()) { return null; }
        return createTickerHelper(type, ContentContainers.type(), ContentContainerBlockEntity::lidTick);
    }

    @Override @Nonnull protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        if (!(level.getBlockEntity(pos) instanceof ContentContainerBlockEntity held) || !(player instanceof ServerPlayer server)) { return InteractionResult.PASS; }
        server.openMenu(held, extra -> ContentContainerMenu.write(extra, held.shape()));
        return InteractionResult.CONSUME;
    }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof ContentContainerBlockEntity held) { held.placed(); }
    }

    @Override protected void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        if (state.is(replaced.getBlock())) { return; }
        if (level.getBlockEntity(pos) instanceof ContentContainerBlockEntity held) {
            Containers.dropContents(level, pos, held);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        ContentDrops.removed(level, pos, state, replaced);
        super.onRemove(state, level, pos, replaced, moving);
    }

    @Override public int getExpDrop(@Nonnull BlockState state, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, @Nonnull ItemStack tool) { return ContentBlock.experience(def, expDrop, level, breaker); }

    @Override @Nonnull public TriState canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos soilPosition, @Nonnull Direction facing, @Nonnull BlockState plant) { return ContentBlock.sustains(def, facing, plant); }

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
