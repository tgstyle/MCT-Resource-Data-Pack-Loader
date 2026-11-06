package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.behavior.OpenedContainers;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class StealContainerGoal extends EntityAIMoveToBlock {
    private static final double SPEED = 1.0D;
    private static final int SEARCH = 9;
    private static final double REACH = 5.0D;
    private static final int TRIES = 10;
    private static final int COOLDOWN = 150;
    private static final int COOLDOWN_SPREAD = 45;
    private final EntityCreature mob;
    private long readyAt;

    public StealContainerGoal(EntityCreature mob) {
        super(mob, SPEED, SEARCH);
        this.mob = mob;
    }

    @Override public boolean shouldExecute() { return MobGriefing.allowed(mob) && mob.getAttackTarget() == null && ready() && super.shouldExecute(); }

    @Override public boolean shouldContinueExecuting() { return ready() && mob.getAttackTarget() == null && MobGriefing.allowed(mob) && super.shouldContinueExecuting(); }

    @Override public void updateTask() {
        super.updateTask();
        if (!ready() || mob.getDistanceSqToCenter(destinationBlock) >= REACH || !reaches(destinationBlock)) { return; }
        TileEntity tile = mob.world.getTileEntity(destinationBlock);
        if (tile instanceof IInventory) { steal((IInventory) tile); }
    }

    @Override protected boolean shouldMoveTo(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!OpenedContainers.has(world, pos) || !world.isBlockLoaded(pos)) { return false; }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IInventory) { return !((IInventory) tile).isEmpty(); }
        OpenedContainers.forget(world, pos);
        return false;
    }

    private boolean ready() { return mob.world.getTotalWorldTime() >= readyAt; }

    private boolean reaches(BlockPos pos) {
        RayTraceResult hit = mob.world.rayTraceBlocks(mob.getPositionEyes(1.0F), new Vec3d(pos).add(0.5D, 0.5D, 0.5D), false, true, false);
        return hit == null || pos.equals(hit.getBlockPos());
    }

    private void steal(IInventory inventory) {
        int size = inventory.getSizeInventory();
        if (size <= 0) { return; }
        for (int tries = 0; tries < TRIES; tries++) {
            ItemStack taken = inventory.decrStackSize(mob.getRNG().nextInt(size), 1);
            if (!taken.isEmpty()) {
                inventory.markDirty();
                mob.entityDropItem(taken, 0.0F);
                mob.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.3F, 1.0F);
                mob.swingArm(EnumHand.MAIN_HAND);
                readyAt = mob.world.getTotalWorldTime() + COOLDOWN + mob.getRNG().nextInt(COOLDOWN_SPREAD);
                return;
            }
        }
    }
}
