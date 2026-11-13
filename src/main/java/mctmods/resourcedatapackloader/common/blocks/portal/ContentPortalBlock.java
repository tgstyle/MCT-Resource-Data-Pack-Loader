package mctmods.resourcedatapackloader.common.blocks.portal;

import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.common.gates.ContentTeleporter;
import mctmods.resourcedatapackloader.common.gates.PortalStorage;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.portals.ContentPortals;
import mctmods.resourcedatapackloader.common.portals.PortalFit;
import mctmods.resourcedatapackloader.common.sounds.ContentSounds;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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

public final class ContentPortalBlock extends ContentBlock {
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

    public BlockState oriented(PortalFit fit) { return defaultBlockState().setValue(AXIS, fit.flat() ? Direction.Axis.Y : fit.alongX() ? Direction.Axis.X : Direction.Axis.Z); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(AXIS); }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        if (getDef().bounds() != null) { return super.getShape(state, level, pos, context); }
        if (getDef().fullCube()) { return Shapes.block(); }
        return switch (state.getValue(AXIS)) {
            case Y -> FLAT;
            case Z -> ALONG_Z;
            default -> ALONG_X;
        };
    }

    @Override @Nonnull public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return portal.walkIn() ? Shapes.empty() : getShape(state, level, pos, context); }

    @Override public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState old, boolean moving) { if (level instanceof ServerLevel server && !old.is(this)) { PortalStorage.add(server, pos, null); } }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) { if (level instanceof ServerLevel server) { PortalStorage.add(server, pos, placer instanceof Player ? placer.getUUID() : null); } }

    @Override public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState replaced, boolean moving) {
        if (level instanceof ServerLevel server && !replaced.is(this)) { PortalStorage.remove(server, pos); }
        super.onRemove(state, level, pos, replaced, moving);
    }

    @Override public boolean onDestroyedByPlayer(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, boolean willHarvest, @Nonnull FluidState fluid) {
        if (level instanceof ServerLevel server && !mayBreak(server, pos, player)) {
            player.displayClientMessage(Component.translatable("rdpl.portal.owned"), true);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override public boolean canDropFromExplosion(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Explosion explosion) { return !portal.owned(); }

    @Override public void onBlockExploded(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (level instanceof ServerLevel server && owned(server, pos)) { return; }
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override public float getExplosionResistance(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
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

    @Override @Nonnull protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
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
        ContentPortals.Binding binding = ContentPortals.forBlock(this);
        PortalFit fit = binding == null ? null : ContentPortals.fitAt(player.level(), pos, binding);
        if (!portal.mounted() && player.isPassenger()) {
            player.displayClientMessage(Component.translatable("rdpl.portal.mounted"), true);
            return;
        }
        if (portal.useRange() > 0 && !near(player, state, pos, fit, portal.useRange())) {
            player.displayClientMessage(Component.translatable("rdpl.portal.far"), true);
            return;
        }
        GateDef gate = portal.gate().isEmpty() ? null : ContentGates.find(portal.gate());
        if (gate != null && !ContentGates.admits(player, gate)) {
            ContentGates.refuse(player, gate);
            return;
        }
        if (ContentTeleporter.target(player, portal).equals(player.level().dimension().location())) { return; }
        sound(player);
        RECENT.put(player.getUUID(), player.level().getGameTime());
        ContentTeleporter.travel(player, portal, state, pos, binding == null || binding.portal().noReturn() ? null : fit);
    }

    private static boolean near(ServerPlayer player, BlockState state, BlockPos pos, @Nullable PortalFit fit, int range) {
        List<BlockPos> holes = fit == null || fit.holes().isEmpty() ? List.of(pos) : fit.holes();
        boolean flat = fit == null ? state.getValue(AXIS) == Direction.Axis.Y : fit.flat();
        IntSummaryStatistics span = holes.stream().mapToInt(BlockPos::getY).summaryStatistics();
        int low = span.getMin();
        int high = flat ? low + 1 : span.getMax();
        int feet = Mth.floor(player.getY());
        if (feet < low || feet > high) { return false; }
        for (BlockPos hole : holes) {
            double dx = hole.getX() + 0.5D - player.getX();
            double dz = hole.getZ() + 0.5D - player.getZ();
            if (dx * dx + dz * dz <= (double) range * range) { return true; }
        }
        return false;
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
        SoundEvent event = ContentSounds.find(portal.sound());
        if (event == null) { return; }
        player.level().playSound(null, player.blockPosition(), event, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
