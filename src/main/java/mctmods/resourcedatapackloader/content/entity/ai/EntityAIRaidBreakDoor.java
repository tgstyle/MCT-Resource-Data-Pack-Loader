package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.raid.ActiveRaid;
import mctmods.resourcedatapackloader.content.raid.RaidStorage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import javax.annotation.Nullable;

public final class EntityAIRaidBreakDoor extends EntityAIBase {
    private static final int BREAK_TICKS = 240;
    private static final double REACH_SQ = 2.25D;
    private static final double STAY_SQ = 4.0D;
    private final EntityCreature mob;
    private boolean granted;
    private BlockPos door;
    private int breaking;
    private int shown = -1;

    public EntityAIRaidBreakDoor(EntityCreature mob) { this.mob = mob; }

    @Override public boolean shouldExecute() {
        boolean raiding = raiding();
        grant(raiding);
        if (!raiding || !mob.collidedHorizontally) { return false; }
        door = doorAhead();
        return door != null && breakable();
    }

    @Override public boolean shouldContinueExecuting() { return breaking <= BREAK_TICKS && closedDoor(door) && mob.getDistanceSq(door) < STAY_SQ && hardEnough() && raiding(); }

    @Override public void startExecuting() {
        breaking = 0;
        shown = -1;
    }

    @Override public void resetTask() { mob.world.sendBlockBreakProgress(mob.getEntityId(), door, -1); }

    @Override public void updateTask() {
        mob.getLookHelper().setLookPosition(door.getX() + 0.5D, door.getY() + 0.5D, door.getZ() + 0.5D, 30.0F, 30.0F);
        if (mob.getRNG().nextInt(20) == 0) {
            mob.world.playEvent(1019, door, 0);
            mob.swingArm(EnumHand.MAIN_HAND);
        }
        breaking++;
        int progress = (int) ((float) breaking / BREAK_TICKS * 10.0F);
        if (progress != shown) {
            mob.world.sendBlockBreakProgress(mob.getEntityId(), door, progress);
            shown = progress;
        }
        if (breaking == BREAK_TICKS) {
            IBlockState state = mob.world.getBlockState(door);
            mob.world.setBlockToAir(door);
            mob.world.playEvent(1021, door, 0);
            mob.world.playEvent(2001, door, Block.getStateId(state));
        }
    }

    private boolean raiding() { return mob.world instanceof WorldServer && mob.getEntityData().hasKey(ActiveRaid.RAIDER) && RaidStorage.get((WorldServer) mob.world).underway(new BlockPos(mob)); }

    private void grant(boolean raiding) {
        if (!(mob.getNavigator() instanceof PathNavigateGround) || raiding == granted) { return; }
        PathNavigateGround navigator = (PathNavigateGround) mob.getNavigator();
        navigator.setEnterDoors(true);
        navigator.setBreakDoors(raiding);
        granted = raiding;
    }

    @Nullable private BlockPos doorAhead() {
        Path path = mob.getNavigator().getPath();
        if (path == null || path.isFinished()) { return null; }
        for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); i++) {
            PathPoint point = path.getPathPointFromIndex(i);
            BlockPos at = new BlockPos(point.x, point.y + 1, point.z);
            if (mob.getDistanceSq(at.getX(), mob.posY, at.getZ()) <= REACH_SQ && closedDoor(at)) { return at; }
        }
        BlockPos at = new BlockPos(mob).up();
        return closedDoor(at) ? at : null;
    }

    private boolean closedDoor(BlockPos at) {
        IBlockState state = mob.world.getBlockState(at);
        return state.getBlock() instanceof BlockDoor && state.getMaterial() == Material.WOOD && !BlockDoor.isOpen(mob.world, at);
    }

    private boolean breakable() {
        IBlockState state = mob.world.getBlockState(door);
        return hardEnough() && ForgeEventFactory.getMobGriefingEvent(mob.world, mob) && state.getBlock().canEntityDestroy(state, mob.world, door, mob) && ForgeEventFactory.onEntityDestroyBlock(mob, door, state);
    }

    private boolean hardEnough() { return mob.world.getDifficulty().getId() >= EnumDifficulty.NORMAL.getId(); }
}
