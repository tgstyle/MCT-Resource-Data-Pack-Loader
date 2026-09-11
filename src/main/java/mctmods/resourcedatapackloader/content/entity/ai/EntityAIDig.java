package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public final class EntityAIDig extends EntityAIBase {
    private static final int LOOK = 10;
    private static final int SWING = 6;
    private static final double SPEED = 1.0D;
    private final EntityCreature mob;
    @Nullable private EntityLivingBase target;
    @Nullable private BlockPos digging;
    private int total;
    private int left;
    private int rest;

    public EntityAIDig(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (--rest > 0) { return false; }
        rest = LOOK;
        EntityLivingBase found = mob.getAttackTarget();
        if (unwanted(found)) { return false; }
        target = found;
        return true;
    }

    @Override public boolean shouldContinueExecuting() {
        if (unwanted(target)) { return false; }
        return digging == null || diggable(digging);
    }

    @Override public void startExecuting() {
        mob.getNavigator().clearPath();
        begin(pick());
    }

    @Override public void resetTask() {
        if (digging != null) { mob.world.sendBlockBreakProgress(mob.getEntityId(), digging, -1); }
        digging = null;
        target = null;
    }

    @Override public void updateTask() {
        EntityLivingBase aim = target;
        if (aim == null) { return; }
        BlockPos pos = digging;
        if (pos == null) {
            advance(aim);
            return;
        }
        mob.getLookHelper().setLookPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 30.0F, 30.0F);
        if ((total - left) % SWING == 0) { mob.swingArm(EnumHand.MAIN_HAND); }
        left--;
        mob.world.sendBlockBreakProgress(mob.getEntityId(), pos, (int) (9.0F * (total - left) / total));
        if (left > 0) { return; }
        ContentHardness.dig(mob.world, pos);
        mob.getHeldItemMainhand().damageItem(1, mob);
        digging = null;
        begin(pick());
    }

    private void advance(EntityLivingBase aim) {
        if (mob.ticksExisted % LOOK == 0) {
            BlockPos next = pick();
            if (next != null) {
                begin(next);
                return;
            }
        }
        mob.getLookHelper().setLookPositionWithEntity(aim, 30.0F, 30.0F);
        mob.getMoveHelper().setMoveTo(aim.posX, aim.posY, aim.posZ, SPEED);
    }

    private void begin(@Nullable BlockPos pos) {
        digging = pos;
        if (pos == null) { return; }
        IBlockState state = mob.world.getBlockState(pos);
        ItemStack tool = mob.getHeldItemMainhand();
        float speed = Math.max(1.0F, tool.getItem().getDestroySpeed(tool, state));
        total = Math.max(1, MathHelper.ceil(state.getBlockHardness(mob.world, pos) * 30.0F / speed * ContentHardness.miningAt(state, mob, pos.getX(), pos.getY(), pos.getZ())));
        left = total;
        mob.getNavigator().clearPath();
    }

    private boolean unwanted(@Nullable EntityLivingBase aim) {
        if (aim == null || !aim.isEntityAlive() || mob.getHeldItemMainhand().isEmpty()) { return true; }
        if (!mob.world.getGameRules().getBoolean("mobGriefing")) { return true; }
        double vanilla = (double) mob.width * 2.0F * mob.width * 2.0F + aim.width;
        return mob.getDistanceSq(aim) <= ContentEntities.attackReachSqr(mob, aim, vanilla);
    }

    @Nullable private BlockPos pick() {
        EntityLivingBase aim = target;
        if (aim == null) { return null; }
        BlockPos feet = new BlockPos(mob);
        if (aim.posY - mob.posY < -1.5D) { return diggable(feet.down()) ? feet.down() : null; }
        double dx = aim.posX - mob.posX;
        double dz = aim.posZ - mob.posZ;
        EnumFacing toward = Math.abs(dx) > Math.abs(dz) ? (dx > 0.0D ? EnumFacing.EAST : EnumFacing.WEST) : (dz > 0.0D ? EnumFacing.SOUTH : EnumFacing.NORTH);
        BlockPos ahead = feet.offset(toward);
        int tall = Math.max(1, MathHelper.ceil(mob.height));
        for (int up = 0; up < tall; up++) {
            BlockPos pos = ahead.up(up);
            if (diggable(pos)) { return pos; }
        }
        return null;
    }

    private boolean diggable(BlockPos pos) {
        IBlockState state = mob.world.getBlockState(pos);
        if (state.getMaterial() == Material.AIR || state.getMaterial().isLiquid() || state.getBlockHardness(mob.world, pos) < 0.0F) { return false; }
        ItemStack tool = mob.getHeldItemMainhand();
        if (!ContentHardness.mayDig(mob, state, tool)) { return false; }
        String needed = state.getBlock().getHarvestTool(state);
        if (needed == null || !tool.getItem().getToolClasses(tool).contains(needed)) { return false; }
        return tool.getItem().getHarvestLevel(tool, needed, null, state) >= state.getBlock().getHarvestLevel(state);
    }
}
