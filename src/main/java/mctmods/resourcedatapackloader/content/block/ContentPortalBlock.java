package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.ContentTeleporter;
import mctmods.resourcedatapackloader.content.gate.PortalStorage;
import mctmods.resourcedatapackloader.content.portal.ContentPortals;
import mctmods.resourcedatapackloader.content.portal.PortalFit;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public final class ContentPortalBlock extends ContentBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final VoxelShape ALONG_X = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    private static final VoxelShape ALONG_Z = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    private static final VoxelShape FLAT = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    private static final Map<UUID, Long> RECENT = new HashMap<>();
    private final PortalDef portal;

    public ContentPortalBlock(BlockDef def, PortalDef portal, Properties properties) {
        super(def, properties);
        this.portal = portal;
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    public PortalDef portal() { return portal; }

    public BlockState oriented(PortalFit fit) { return defaultBlockState().setValue(AXIS, fit.flat() ? Direction.Axis.Y : fit.alongX() ? Direction.Axis.X : Direction.Axis.Z); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(AXIS); }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case Y -> FLAT;
            case Z -> ALONG_Z;
            default -> ALONG_X;
        };
    }

    @Override @Nonnull public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return portal.walkIn() ? Shapes.empty() : getShape(state, level, pos, context);
    }

    @Override public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState old, boolean moving) {
        if (level instanceof ServerLevel server && !old.is(this)) { PortalStorage.add(server, pos, null); }
    }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        if (level instanceof ServerLevel server) { PortalStorage.add(server, pos, placer instanceof Player ? placer.getUUID() : null); }
    }

    @Override public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        if (level instanceof ServerLevel server && !replaced.is(this)) { PortalStorage.remove(server, pos); }
        super.onRemove(state, level, pos, replaced, moving);
    }

    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level instanceof ServerLevel server && !mayBreak(server, pos, player)) {
            player.displayClientMessage(Component.translatable("rdpl.portal.owned"), true);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) { return !portal.owned(); }

    @Override public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (level instanceof ServerLevel server && owned(server, pos)) { return; }
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        if (level instanceof ServerLevel server && owned(server, pos)) { return Float.MAX_VALUE; }
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    private boolean owned(ServerLevel level, BlockPos pos) { return portal.owned() && PortalStorage.owner(level, pos) != null; }

    private boolean mayBreak(ServerLevel level, BlockPos pos, Player player) {
        if (!portal.owned() || player.isCreative()) { return true; }
        UUID owner = PortalStorage.owner(level, pos);
        return owner == null || owner.equals(player.getUUID());
    }

    @Override public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (!portal.walkIn() || !(entity instanceof ServerPlayer player)) { return; }
        travel(player, state, pos);
    }

    @Override @Nonnull public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (player instanceof ServerPlayer server) { travel(server, state, pos); }
        return InteractionResult.SUCCESS;
    }

    @Override public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos from, boolean moving) {
        super.neighborChanged(state, level, pos, block, from, moving);
        if (level.isClientSide()) { return; }
        ContentPortals.Binding binding = ContentPortals.forBlock(this);
        if (binding == null || ContentPortals.standing(level, pos, binding)) { return; }
        ContentLog.LOGGER.debug("The frame around the portal at {} no longer holds, so it goes out", pos);
        level.removeBlock(pos, false);
    }

    private void travel(ServerPlayer player, BlockState state, BlockPos pos) {
        if (recently(player)) { return; }
        GateDef gate = portal.gate().isEmpty() ? null : ContentGates.find(portal.gate());
        if (gate != null && !ContentGates.unlocked(player, gate)) {
            ContentGates.refuse(player, gate);
            return;
        }
        ResourceLocation here = player.level().dimension().location();
        ResourceLocation target = here.equals(portal.dimension()) ? portal.returnDimension() : portal.dimension();
        if (target.equals(here)) { return; }
        sound(player);
        RECENT.put(player.getUUID(), player.level().getGameTime());
        ContentTeleporter.travel(player, portal, state, pos, returning(player.level(), pos));
    }

    @Nullable private PortalFit returning(Level level, BlockPos pos) {
        ContentPortals.Binding binding = ContentPortals.forBlock(this);
        if (binding == null || !binding.portal().buildsReturn()) { return null; }
        return ContentPortals.fitAt(level, pos, binding);
    }

    public static void forget(UUID player) { RECENT.remove(player); }

    private boolean recently(ServerPlayer player) {
        Long last = RECENT.get(player.getUUID());
        if (last == null) { return false; }
        long since = player.level().getGameTime() - last;
        return since >= 0L && since < portal.cooldown();
    }

    private void sound(ServerPlayer player) {
        if (portal.sound().isEmpty()) { return; }
        ResourceLocation key = ResourceLocation.tryParse(portal.sound());
        SoundEvent event = key == null ? null : BuiltInRegistries.SOUND_EVENT.getOptional(key).orElse(null);
        if (event == null) { return; }
        player.level().playSound(null, player.blockPosition(), event, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
