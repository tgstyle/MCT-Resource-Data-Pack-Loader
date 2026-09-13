package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;

public final class EntityAIAnvilWork extends EntityAIBase {
    private static final int LOOK_EVERY = 100;
    private static final int REACH = 16;
    private static final int RISE = 4;
    private static final double USE_DISTANCE_SQ = 9.0D;
    private static final int GIVE_UP = 600;
    private final EntityCreature mob;
    private BlockPos anvil;
    private AnvilDef work;
    private int walked;

    public EntityAIAnvilWork(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        if (mob.ticksExisted % LOOK_EVERY != 0) { return false; }
        work = ContentAnvils.affordable(mob, ContentMobExperience.level(mob));
        if (work == null) { return false; }
        anvil = nearestAnvil();
        return anvil != null;
    }

    @Nullable private BlockPos nearestAnvil() {
        BlockPos feet = new BlockPos(mob);
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos.MutableBlockPos at : BlockPos.getAllInBoxMutable(feet.add(-REACH, -RISE, -REACH), feet.add(REACH, RISE, REACH))) {
            if (mob.world.getBlockState(at).getBlock() != Blocks.ANVIL) { continue; }
            double distance = at.distanceSq(feet);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = at.toImmutable();
            }
        }
        return best;
    }

    @Override public boolean shouldContinueExecuting() { return anvil != null && walked < GIVE_UP && mob.world.getBlockState(anvil).getBlock() == Blocks.ANVIL; }

    @Override public void startExecuting() {
        walked = 0;
        ContentLog.LOGGER.debug("{} at {}, {}, {} can pay for {} and heads for the anvil at {}, {}, {}", mob.getName(), (int) mob.posX, (int) mob.posY, (int) mob.posZ, work.registryName, anvil.getX(), anvil.getY(), anvil.getZ());
        mob.getNavigator().tryMoveToXYZ(anvil.getX() + 0.5D, anvil.getY(), anvil.getZ() + 0.5D, 1.0D);
    }

    @Override public void resetTask() {
        anvil = null;
        work = null;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        walked++;
        mob.getLookHelper().setLookPosition(anvil.getX() + 0.5D, anvil.getY() + 0.5D, anvil.getZ() + 0.5D, 30.0F, 30.0F);
        if (mob.getDistanceSq(anvil.getX() + 0.5D, anvil.getY() + 0.5D, anvil.getZ() + 0.5D) <= USE_DISTANCE_SQ) {
            ContentAnvils.work(mob, work, anvil);
            anvil = null;
            return;
        }
        if (mob.getNavigator().noPath() && walked % 20 == 0) { mob.getNavigator().tryMoveToXYZ(anvil.getX() + 0.5D, anvil.getY(), anvil.getZ() + 0.5D, 1.0D); }
    }
}
