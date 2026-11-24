package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.PullingHand;
import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import mctmods.resourcedatapackloader.common.util.entity.Looking;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import javax.annotation.Nullable;

public final class PullGoal extends EntityAIBase {
    private static final double MIN_DISTANCE = 4.0D;
    private static final float SPEED = 1.5F;
    private final EntityCreature creature;
    private final ItemStack stack;
    private final int reload;
    private final float power;
    private final boolean follows;
    private long next;
    private EntityPlayer target;

    public PullGoal(EntityCreature creature, ItemStack stack, int reload, float power, boolean follows) {
        this.creature = creature;
        this.stack = stack;
        this.reload = reload;
        this.power = power;
        this.follows = follows;
        setMutexBits(0);
    }

    @Nullable private EntityPlayer pick() {
        EntityLivingBase aimed = creature.getAttackTarget();
        double range = ContentAttributes.followRange(creature);
        EntityPlayer found = aimed instanceof EntityPlayer ? (EntityPlayer) aimed : null;
        if (found != null && (found.isCreative() || found.isSpectator())) { found = null; }
        if (found == null && follows) { found = creature.world.getNearestAttackablePlayer(creature, range, range); }
        if (found == null) { return null; }
        double distance = creature.getDistance(found);
        return distance >= MIN_DISTANCE && distance <= range && creature.getEntitySenses().canSee(found) ? found : null;
    }

    @Override public boolean shouldExecute() {
        if (next == 0L) { next = creature.world.getTotalWorldTime() + reload; }
        if (creature.world.getTotalWorldTime() < next) { return false; }
        target = pick();
        return target != null;
    }

    @Override public boolean shouldContinueExecuting() { return false; }

    @Override public void startExecuting() {
        next = creature.world.getTotalWorldTime() + reload;
        Looking.at(creature, target);
        PullingHand hand = new PullingHand(creature.world, creature, stack, power);
        hand.aim(target.posX - hand.posX, target.posY + target.getEyeHeight() - hand.posY, target.posZ - hand.posZ, SPEED);
        creature.world.spawnEntity(hand);
        SoundEvent own = ContentEntities.soundEvent(creature, ContentEntities.THROW);
        if (own != null) { creature.playSound(own, 1.0F, 1.0F); }
        creature.swingArm(EnumHand.MAIN_HAND);
        target = null;
    }
}
