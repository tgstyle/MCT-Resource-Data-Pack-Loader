package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.content.raid.ActiveRaid;
import mctmods.resourcedatapackloader.content.raid.RaidStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class RaidBreakDoorGoal extends Goal {
    private static final int BREAK_TICKS = 240;
    private static final double REACH_SQ = 2.25D;
    private static final double STAY_SQ = 4.0D;
    private final PathfinderMob mob;
    private boolean granted;
    @Nullable private BlockPos door;
    private int breaking;
    private int shown = -1;

    public RaidBreakDoorGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        boolean raiding = raiding();
        grant(raiding);
        if (!raiding || !mob.horizontalCollision) { return false; }
        door = doorAhead();
        return door != null && breakable(door);
    }

    @Override public boolean canContinueToUse() { return door != null && breaking <= BREAK_TICKS && closedDoor(door) && door.distToLowCornerSqr(mob.getX(), mob.getY(), mob.getZ()) < STAY_SQ && hardEnough() && raiding(); }

    @Override public void start() {
        breaking = 0;
        shown = -1;
    }

    @Override public void stop() {
        if (door != null) { mob.level().destroyBlockProgress(mob.getId(), door, -1); }
    }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        if (door == null) { return; }
        Level level = mob.level();
        mob.getLookControl().setLookAt(door.getX() + 0.5D, door.getY() + 0.5D, door.getZ() + 0.5D, 30.0F, 30.0F);
        if (mob.getRandom().nextInt(20) == 0) {
            level.levelEvent(LevelEvent.SOUND_ZOMBIE_WOODEN_DOOR, door, 0);
            mob.swing(InteractionHand.MAIN_HAND);
        }
        breaking++;
        int progress = (int) ((float) breaking / BREAK_TICKS * 10.0F);
        if (progress != shown) {
            level.destroyBlockProgress(mob.getId(), door, progress);
            shown = progress;
        }
        if (breaking == BREAK_TICKS) {
            BlockState state = level.getBlockState(door);
            level.removeBlock(door, false);
            level.levelEvent(LevelEvent.SOUND_ZOMBIE_DOOR_CRASH, door, 0);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, door, Block.getId(state));
        }
    }

    private boolean raiding() { return mob.level() instanceof ServerLevel level && mob.getPersistentData().contains(ActiveRaid.RAIDER) && RaidStorage.get(level).underway(mob.blockPosition()); }

    private void grant(boolean raiding) {
        if (!(mob.getNavigation() instanceof GroundPathNavigation navigation) || raiding == granted) { return; }
        navigation.setCanPassDoors(true);
        navigation.setCanOpenDoors(raiding);
        granted = raiding;
    }

    @Nullable private BlockPos doorAhead() {
        Path path = mob.getNavigation().getPath();
        if (path == null || path.isDone()) { return null; }
        for (int i = 0; i < Math.min(path.getNextNodeIndex() + 2, path.getNodeCount()); i++) {
            Node point = path.getNode(i);
            BlockPos at = new BlockPos(point.x, point.y + 1, point.z);
            if (mob.distanceToSqr(at.getX(), mob.getY(), at.getZ()) <= REACH_SQ && closedDoor(at)) { return at; }
        }
        BlockPos at = mob.blockPosition().above();
        return closedDoor(at) ? at : null;
    }

    private boolean closedDoor(BlockPos at) {
        BlockState state = mob.level().getBlockState(at);
        return DoorBlock.isWoodenDoor(state) && !state.getValue(DoorBlock.OPEN);
    }

    private boolean breakable(BlockPos at) {
        Level level = mob.level();
        BlockState state = level.getBlockState(at);
        return hardEnough() && ForgeEventFactory.getMobGriefingEvent(level, mob) && state.canEntityDestroy(level, at, mob) && ForgeEventFactory.onEntityDestroyBlock(mob, at, state);
    }

    private boolean hardEnough() { return mob.level().getDifficulty().getId() >= Difficulty.NORMAL.getId(); }
}
