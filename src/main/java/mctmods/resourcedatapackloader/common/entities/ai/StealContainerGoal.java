package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.behavior.OpenedContainers;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public final class StealContainerGoal extends MoveToBlockGoal {
    private static final double SPEED = 1.0D;
    private static final int SEARCH = 9;
    private static final double REACH = 5.0D;
    private static final int TRIES = 10;
    private static final int COOLDOWN = 150;
    private static final int COOLDOWN_SPREAD = 45;
    private long readyAt;

    public StealContainerGoal(PathfinderMob mob) { super(mob, SPEED, SEARCH); }

    @Override public boolean canUse() { return mob.level() instanceof ServerLevel server && EventHooks.canEntityGrief(server, mob) && mob.getTarget() == null && ready(server) && super.canUse(); }

    @Override public void tick() {
        super.tick();
        if (!(mob.level() instanceof ServerLevel server) || !ready(server) || mob.distanceToSqr(Vec3.atCenterOf(blockPos)) >= REACH || !reaches(server, blockPos)) { return; }
        BlockEntity entity = server.getBlockEntity(blockPos);
        if (entity instanceof Container container) { steal(server, container); }
    }

    @Override protected boolean isValidTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (!(mob.level() instanceof ServerLevel server) || !OpenedContainers.has(server, pos) || !server.isLoaded(pos)) { return false; }
        if (level.getBlockEntity(pos) instanceof Container container) { return !container.isEmpty(); }
        OpenedContainers.forget(server, pos);
        return false;
    }

    private boolean ready(ServerLevel server) { return server.getGameTime() >= readyAt; }

    private boolean reaches(ServerLevel server, BlockPos pos) {
        BlockHitResult hit = server.clip(new ClipContext(mob.getEyePosition(), Vec3.atCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(pos);
    }

    private void steal(ServerLevel server, Container container) {
        int size = container.getContainerSize();
        if (size <= 0) { return; }
        for (int tries = 0; tries < TRIES; tries++) {
            ItemStack taken = container.removeItem(mob.getRandom().nextInt(size), 1);
            if (!taken.isEmpty()) {
                container.setChanged();
                mob.spawnAtLocation(server, taken, 0.0F);
                mob.playSound(SoundEvents.CHEST_OPEN, 0.3F, 1.0F);
                LineCompat.swing(mob);
                readyAt = server.getGameTime() + COOLDOWN + mob.getRandom().nextInt(COOLDOWN_SPREAD);
                return;
            }
        }
    }
}
