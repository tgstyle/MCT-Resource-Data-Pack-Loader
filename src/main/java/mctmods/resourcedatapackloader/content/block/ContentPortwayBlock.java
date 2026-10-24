package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public final class ContentPortwayBlock extends ContentBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final ThreadLocal<PortwayDef> PENDING = new ThreadLocal<>();

    public static ContentPortwayBlock create(BlockDef def, Properties properties) {
        PENDING.set(def.portway() == null ? PortwayDef.PLAIN : def.portway());
        try { return new ContentPortwayBlock(def, properties); }
        finally { PENDING.remove(); }
    }

    private ContentPortwayBlock(BlockDef def, Properties properties) {
        super(def, properties);
        BlockState base = stateDefinition.any();
        if (settings().facing()) { base = base.setValue(FACING, Direction.NORTH); }
        if (settings().tall()) { base = base.setValue(HALF, DoubleBlockHalf.LOWER); }
        registerDefaultState(base);
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        PortwayDef shape = PENDING.get();
        if (shape.facing()) { builder.add(FACING); }
        if (shape.tall()) { builder.add(HALF); }
    }

    public PortwayDef settings() { return getDef().portway() == null ? PortwayDef.PLAIN : getDef().portway(); }

    public boolean upper(BlockState state) { return settings().tall() && state.getValue(HALF) == DoubleBlockHalf.UPPER; }

    private String id() { return String.valueOf(ForgeRegistries.BLOCKS.getKey(this)); }

    @Override @Nullable public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (settings().tall() && (pos.getY() >= context.getLevel().getMaxBuildHeight() - 1 || !context.getLevel().getBlockState(pos.above()).canBeReplaced(context))) { return null; }
        BlockState state = defaultBlockState();
        return settings().facing() ? state.setValue(FACING, context.getHorizontalDirection().getOpposite()) : state;
    }

    @Override @Nonnull public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) { return settings().facing() ? state.setValue(FACING, rotation.rotate(state.getValue(FACING))) : state; }

    @Override @Nonnull public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) { return settings().facing() ? state.setValue(FACING, mirror.mirror(state.getValue(FACING))) : state; }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return upper(state) ? null : new ContentPortwayBlockEntity(pos, state); }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (settings().tall()) { level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3); }
        if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof ContentPortwayBlockEntity held) { held.rename(stack.getHoverName().getString()); }
    }

    @Override @Nonnull public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbor, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (settings().tall() && direction.getAxis() == Direction.Axis.Y && (state.getValue(HALF) == DoubleBlockHalf.LOWER) == (direction == Direction.UP)) {
            return neighbor.is(this) && neighbor.getValue(HALF) != state.getValue(HALF) ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if (!level.isClientSide() && upper(state)) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(this)) {
                if (!player.isCreative()) { Block.dropResources(lower, level, below, level.getBlockEntity(below), player, player.getMainHandItem()); }
                level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override @Nonnull public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) {
        if (upper(state)) { return List.of(); }
        List<ItemStack> drops = super.getDrops(state, builder);
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(this);
        if (entry != null && entry.variant().drops().isEmpty() && builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof ContentPortwayBlockEntity held && !held.stored().isEmpty()) {
            for (ItemStack drop : drops) {
                if (drop.is(asItem())) { drop.setHoverName(Component.literal(held.stored())); }
            }
        }
        return drops;
    }

    @Override @Nonnull public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer opener)) { return InteractionResult.SUCCESS; }
        BlockPos base = upper(state) ? pos.below() : pos;
        PortwayDef settings = settings();
        if (PortwayTravel.admit(opener, server, settings, id())) {
            Portways.touch(opener, server, base, settings);
            NetworkHooks.openScreen(opener, new SimpleMenuProvider((id, inventory, ignored) -> new ContentPortwayMenu(id, inventory, server, base), getName()));
        }
        return InteractionResult.CONSUME;
    }

    @Override public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        if (!state.is(replaced.getBlock()) && !upper(state) && level instanceof ServerLevel server) { Portways.withdraw(server, pos); }
        super.onRemove(state, level, pos, replaced, moving);
    }
}
