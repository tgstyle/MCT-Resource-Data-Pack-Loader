package mctmods.resourcedatapackloader.common.leafdecay;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public final class DecayWatch implements IWorldEventListener {
    private static final int RADIUS = 4;
    private static final int BUDGET = 64;
    private static final int CAP = 8192;
    private static final int LOADED = 6;
    private final Set<BlockPos> queue = new LinkedHashSet<>();

    @Override public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        if (world.isRemote || queue.size() >= CAP) { return; }
        if (oldState.getBlock().canSustainLeaves(oldState, world, pos) && !newState.getBlock().canSustainLeaves(newState, world, pos)) { queue.add(pos.toImmutable()); }
    }

    void work(World world) {
        if (world.isRemote || queue.isEmpty()) { return; }
        int taken = 0;
        Iterator<BlockPos> each = queue.iterator();
        while (each.hasNext() && taken < BUDGET) {
            BlockPos origin = each.next();
            each.remove();
            taken++;
            if (world.isBlockLoaded(origin)) { scan(world, origin); }
        }
    }

    private static void scan(World world, BlockPos origin) {
        for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-RADIUS, -RADIUS, -RADIUS), origin.add(RADIUS, RADIUS, RADIUS))) {
            if (!world.isBlockLoaded(pos)) { continue; }
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.AIR || !ContentLeafDecay.qualifies(state, world, pos)) { continue; }
            LeafDecayRule rule = ContentLeafDecay.ruleFor(state, world, pos);
            if (rule != null && world.isAreaLoaded(pos, LOADED)) { begin(world, pos.toImmutable(), state, rule); }
        }
    }

    private static void begin(World world, BlockPos pos, IBlockState state, LeafDecayRule rule) {
        Block block = state.getBlock();
        block.beginLeavesDecay(state, world, pos);
        world.scheduleUpdate(pos, block, rule.delay(world.rand));
    }

    @Override public void notifyLightSet(BlockPos pos) {}

    @Override public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {}

    @Override public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

    @Override public void playRecord(SoundEvent sound, BlockPos pos) {}

    @Override public void spawnParticle(int id, boolean ignoreRange, double x, double y, double z, double speedX, double speedY, double speedZ, int... parameters) {}

    @Override public void spawnParticle(int id, boolean ignoreRange, boolean minimal, double x, double y, double z, double speedX, double speedY, double speedZ, int... parameters) {}

    @Override public void onEntityAdded(Entity entity) {}

    @Override public void onEntityRemoved(Entity entity) {}

    @Override public void broadcastSound(int sound, BlockPos pos, int data) {}

    @Override public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {}

    @Override public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}
