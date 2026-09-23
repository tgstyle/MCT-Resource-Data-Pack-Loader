package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentBells;
import mctmods.resourcedatapackloader.content.def.BellDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;
import mctmods.resourcedatapackloader.util.Registered;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentBellBlock extends BellBlock implements IContentBell {
    private static final double HIGHEST_HIT = 0.8124F;
    private static final float RING_VOLUME = 2.0F;
    private static final float RESONATE_VOLUME = 1.0F;
    private final BellDef bell;

    public ContentBellBlock(BlockDef def, Properties properties) {
        super(properties);
        this.bell = def.bell() == null ? BellDef.PLAIN : def.bell();
    }

    @Override @Nonnull public MapCodec<BellBlock> codec() { return MapCodec.unit(this); }

    @Override public boolean swings() { return bell.swing(); }

    @Override @Nonnull public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new ContentBellBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return createTickerHelper(type, ContentBells.registeredType(), ContentBellBlockEntity::tick);
    }

    @Override @Nonnull protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
        return hit(level, state, hit, player) ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
    }

    @Override protected void onProjectileHit(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockHitResult hit, @Nonnull Projectile projectile) {
        Entity owner = projectile.getOwner();
        hit(level, state, hit, owner instanceof Player player ? player : null);
    }

    @Override protected void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block from, @Nonnull BlockPos fromPos, boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) { return; }
        if (powered) { ring(level, pos, null); }
        level.setBlock(pos, state.setValue(POWERED, powered), 3);
    }

    private boolean hit(Level level, BlockState state, BlockHitResult at, @Nullable Player player) {
        BlockPos pos = at.getBlockPos();
        if (properHit(state, at.getDirection(), at.getLocation().y - pos.getY())) {
            ring(level, pos, at.getDirection());
            if (player != null) { player.awardStat(Stats.BELL_RING); }
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return true;
        }
        return false;
    }

    private static boolean properHit(BlockState state, Direction side, double hitY) {
        if (side.getAxis() == Direction.Axis.Y || hitY > HIGHEST_HIT) { return false; }
        Direction.Axis along = state.getValue(FACING).getAxis();
        return switch (state.getValue(ATTACHMENT)) {
            case FLOOR -> along == side.getAxis();
            case SINGLE_WALL, DOUBLE_WALL -> along != side.getAxis();
            default -> true;
        };
    }

    public void ring(Level level, BlockPos pos, @Nullable Direction side) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof ContentBellBlockEntity held)) { return; }
        held.hit(side == null ? level.getBlockState(pos).getValue(FACING) : side);
        play(level, pos, bell.sound(), RING_VOLUME);
    }

    public void resonate(Level level, BlockPos pos) { play(level, pos, bell.resonateSound(), RESONATE_VOLUME); }

    private static void play(Level level, BlockPos pos, String name, float volume) {
        if (name.isEmpty()) { return; }
        SoundEvent sound = Registered.find(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryParse(name));
        if (sound != null) { level.playSound(null, pos, sound, SoundSource.BLOCKS, volume, 1.0F); }
    }
}
