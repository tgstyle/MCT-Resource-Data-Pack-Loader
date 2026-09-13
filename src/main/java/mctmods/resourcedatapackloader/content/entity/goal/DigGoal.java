package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class DigGoal extends Goal {
    private static final int LOOK = 10;
    private static final int SWING = 6;
    private static final double SPEED = 1.0D;
    private final PathfinderMob mob;
    @Nullable private LivingEntity target;
    @Nullable private BlockPos digging;
    private int total;
    private int left;
    private int rest;

    public DigGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (--rest > 0) { return false; }
        rest = LOOK;
        LivingEntity found = mob.getTarget();
        if (unwanted(found)) { return false; }
        target = found;
        return true;
    }

    @Override public boolean canContinueToUse() {
        if (unwanted(target)) { return false; }
        return digging == null || diggable(digging);
    }

    @Override public void start() {
        mob.getNavigation().stop();
        begin(pick());
    }

    @Override public void stop() {
        if (digging != null) { mob.level().destroyBlockProgress(mob.getId(), digging, -1); }
        digging = null;
        target = null;
    }

    @Override public void tick() {
        LivingEntity aim = mob.getTarget();
        if (aim != null) { target = aim; }
        aim = target;
        if (aim == null) { return; }
        BlockPos pos = digging;
        if (pos == null) {
            advance(aim);
            return;
        }
        mob.getLookControl().setLookAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 30.0F, 30.0F);
        if ((total - left) % SWING == 0) { mob.swing(InteractionHand.MAIN_HAND); }
        left--;
        mob.level().destroyBlockProgress(mob.getId(), pos, (int) (9.0F * (total - left) / total));
        if (left > 0) { return; }
        ContentHardness.dig(mob, pos);
        if (mob.level() instanceof ServerLevel level) { mob.getMainHandItem().hurtAndBreak(1, level, mob, held -> { }); }
        digging = null;
        begin(pick());
    }

    private void advance(LivingEntity aim) {
        if (mob.tickCount % LOOK == 0) {
            BlockPos next = pick();
            if (next != null) {
                begin(next);
                return;
            }
        }
        mob.getLookControl().setLookAt(aim, 30.0F, 30.0F);
        mob.getMoveControl().setWantedPosition(aim.getX(), aim.getY(), aim.getZ(), SPEED);
    }

    private void begin(@Nullable BlockPos pos) {
        digging = pos;
        if (pos == null) { return; }
        BlockState state = mob.level().getBlockState(pos);
        ItemStack tool = mob.getMainHandItem();
        float speed = Math.max(1.0F, tool.getDestroySpeed(state));
        total = Math.max(1, Mth.ceil(state.getDestroySpeed(mob.level(), pos) * 30.0F / speed * ContentHardness.miningAt(state, mob, pos.getX(), pos.getY(), pos.getZ())));
        left = total;
        mob.getNavigation().stop();
    }

    private boolean unwanted(@Nullable LivingEntity aim) {
        if (aim == null || !aim.isAlive() || mob.getMainHandItem().isEmpty()) { return true; }
        if (!mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) { return true; }
        double vanilla = mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + aim.getBbWidth();
        return mob.distanceToSqr(aim) <= ContentEntities.attackReachSqr(mob, aim, vanilla);
    }

    @Nullable private BlockPos pick() {
        LivingEntity aim = target;
        if (aim == null) { return null; }
        BlockPos feet = mob.blockPosition();
        if (aim.getY() - mob.getY() < -1.5D) { return diggable(feet.below()) ? feet.below() : null; }
        double dx = aim.getX() - mob.getX();
        double dz = aim.getZ() - mob.getZ();
        Direction toward = Math.abs(dx) > Math.abs(dz) ? (dx > 0.0D ? Direction.EAST : Direction.WEST) : (dz > 0.0D ? Direction.SOUTH : Direction.NORTH);
        BlockPos ahead = feet.relative(toward);
        int tall = Math.max(1, Mth.ceil(mob.getBbHeight()));
        for (int up = 0; up < tall; up++) {
            BlockPos pos = ahead.above(up);
            if (diggable(pos)) { return pos; }
        }
        return null;
    }

    private boolean diggable(BlockPos pos) {
        BlockState state = mob.level().getBlockState(pos);
        if (state.isAir() || !state.getFluidState().isEmpty() || state.getDestroySpeed(mob.level(), pos) < 0.0F) { return false; }
        ItemStack tool = mob.getMainHandItem();
        return !ContentHardness.digBarred(mob, state, tool) && tool.isCorrectToolForDrops(state);
    }
}
