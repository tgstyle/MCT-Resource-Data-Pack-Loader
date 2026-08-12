package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.block.BlockTNT;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public final class EntityAIThrower extends EntityAIBase {
    private static final String LEFT = "rdplThrowsLeft";
    private static final double REACH = 3.0D;
    private static final double NEAR = 0.4D;
    private static final double FURTHER = 0.03D;
    private final EntityCreature mob;
    private final int fuse;
    private final int reload;
    private final int retreat;
    private final int carried;
    private final float power;
    private final float arc;
    private final double range;
    private final ItemStack pack;
    private EntityLivingBase target;
    private int reloading;
    private int retreating;

    public EntityAIThrower(EntityCreature mob, ItemStack pack, int fuse, int reload, int retreat, int carried, float power, float arc, double range) {
        this.mob = mob;
        this.fuse = fuse;
        this.reload = reload;
        this.retreat = retreat;
        this.carried = carried;
        this.power = power;
        this.arc = arc;
        this.range = range;
        this.pack = pack;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        if (retreating > 0 || reloading > 0) { return true; }

        EntityLivingBase found = mob.getAttackTarget();
        if (found == null || held().isEmpty()) { return false; }

        double away = mob.getDistanceSq(found);
        return away < range * range && away > REACH * REACH && mob.getEntitySenses().canSee(found);
    }

    @Override public boolean shouldContinueExecuting() { return retreating > 0 || reloading > 0 || shouldExecute(); }

    @Override public void startExecuting() { target = mob.getAttackTarget(); }

    @Override public void resetTask() { target = null; }

    @Override public void updateTask() {
        if (reloading > 0 && --reloading == 0) { restock(); }
        if (retreating > 0) {
            retreating--;
            backAway();
            return;
        }
        if (target == null || target.isDead) { return; }

        if (spent()) {
            if (!held().isEmpty()) { mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY); }
            return;
        }

        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        mob.getNavigator().clearPath();
        ItemStack thrown = held();
        if (thrown.isEmpty() || mob.world.isRemote) { return; }

        Vec3d at = new Vec3d(target.posX - mob.posX, target.posY + target.getEyeHeight() - (mob.posY + mob.getEyeHeight()), target.posZ - mob.posZ);
        double far = Math.max(1.0D, at.length());
        Vec3d push = at.normalize().scale((NEAR + far * FURTHER) * power).add(0.0D, arc, 0.0D);
        if (isTnt(thrown)) { lit(push); }
        else { tossed(thrown, push); }

        spend();
    }

    private void spend() {
        mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        retreating = retreat;
        if (carried > 0) {
            int left = mob.getEntityData().hasKey(LEFT) ? mob.getEntityData().getInteger(LEFT) : carried;
            left--;
            mob.getEntityData().setInteger(LEFT, left);
            if (left <= 0) { return; }
        }
        reloading = Math.max(1, reload);
    }

    private boolean spent() { return carried > 0 && mob.getEntityData().hasKey(LEFT) && mob.getEntityData().getInteger(LEFT) <= 0; }

    private void restock() { mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, pack.copy()); }

    private void lit(Vec3d push) {
        EntityTNTPrimed primed = new EntityTNTPrimed(mob.world, mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ, mob);
        primed.setFuse(fuse);
        primed.motionX = push.x;
        primed.motionY = push.y;
        primed.motionZ = push.z;
        mob.world.spawnEntity(primed);
        mob.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
    }

    private void tossed(ItemStack thrown, Vec3d push) {
        EntityItem flying = new EntityItem(mob.world, mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ, thrown.copy());
        flying.setPickupDelay(40);
        flying.motionX = push.x;
        flying.motionY = push.y;
        flying.motionZ = push.z;
        mob.world.spawnEntity(flying);
        mob.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
    }

    private void backAway() {
        if (target == null || !mob.getNavigator().noPath()) { return; }

        Vec3d away = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 12, 5, new Vec3d(target.posX, target.posY, target.posZ));
        if (away != null) { mob.getNavigator().tryMoveToXYZ(away.x, away.y, away.z, 1.4D); }
    }

    private ItemStack held() { return mob.getHeldItem(EnumHand.MAIN_HAND); }

    private static boolean isTnt(ItemStack stack) { return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockTNT; }
}
