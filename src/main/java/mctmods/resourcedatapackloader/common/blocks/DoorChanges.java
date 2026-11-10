package mctmods.resourcedatapackloader.common.blocks;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public final class DoorChanges implements IWorldEventListener {
    private final Set<BlockPos> flips = new HashSet<>();

    @Override public void notifyBlockUpdate(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState, int flags) {
        if (world.isRemote || oldState.getBlock() != newState.getBlock() || ContentPairedDoors.idle() || !DoorPairs.isDoor(newState)) { return; }
        if (!DoorPairs.isLower(newState)) {
            if (!ContentPairedDoors.following() && DoorPairs.isPowered(oldState) != DoorPairs.isPowered(newState)) { flips.add(pos.down()); }
            return;
        }
        boolean open = DoorPairs.isOpen(newState);
        if (DoorPairs.isOpen(oldState) == open) { return; }
        boolean redstone = flips.remove(pos);
        if (!ContentPairedDoors.following()) { ContentPairedDoors.changed(world, pos, open, redstone); }
    }

    @Override public void notifyLightSet(@Nonnull BlockPos pos) {}

    @Override public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {}

    @Override public void playSoundToAllNearExcept(@Nullable EntityPlayer player, @Nonnull SoundEvent sound, @Nonnull SoundCategory category, double x, double y, double z, float volume, float pitch) {}

    @Override public void playRecord(@Nonnull SoundEvent sound, @Nonnull BlockPos pos) {}

    @Override public void spawnParticle(int id, boolean ignoreRange, double x, double y, double z, double speedX, double speedY, double speedZ, @Nonnull int... parameters) {}

    @Override public void spawnParticle(int id, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double speedX, double speedY, double speedZ, @Nonnull int... parameters) {}

    @Override public void onEntityAdded(@Nonnull Entity entity) {}

    @Override public void onEntityRemoved(@Nonnull Entity entity) {}

    @Override public void broadcastSound(int soundId, @Nonnull BlockPos pos, int data) {}

    @Override public void playEvent(@Nonnull EntityPlayer player, int type, @Nonnull BlockPos pos, int data) {}

    @Override public void sendBlockBreakProgress(int breakerId, @Nonnull BlockPos pos, int progress) {}
}
