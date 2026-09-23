package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ReturningThrow;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class ThrowerGoal extends Goal {
    private static final String LEFT = "rdplThrowsLeft";
    private static final double REACH = 3.0D;
    private static final double NEAR = 0.4D;
    private static final double FURTHER = 0.03D;
    private static final float TRIDENT_DAMAGE = 8.0F;
    private static final float TRIDENT_SPEED = 1.6F;
    private final PathfinderMob mob;
    private final int fuse;
    private final int reload;
    private final int retreat;
    private final int carried;
    private final float power;
    private final float arc;
    private final double range;
    private final ItemStack pack;
    private final boolean returns;
    @Nullable private LivingEntity target;
    private int reloading;
    private int retreating;

    public ThrowerGoal(PathfinderMob mob, ItemStack pack, int fuse, int reload, int retreat, int carried, float power, float arc, double range, boolean returns) {
        this.returns = returns;
        this.mob = mob;
        this.fuse = fuse;
        this.reload = reload;
        this.retreat = retreat;
        this.carried = carried;
        this.power = power;
        this.arc = arc;
        this.range = range;
        this.pack = pack;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (retreating > 0 || reloading > 0) { return true; }
        LivingEntity found = mob.getTarget();
        if (found == null || held().isEmpty()) { return false; }
        double away = mob.distanceToSqr(found);
        return away < range * range && away > REACH * REACH && mob.getSensing().hasLineOfSight(found);
    }

    @Override public boolean canContinueToUse() { return retreating > 0 || reloading > 0 || canUse(); }

    @Override public void start() { target = mob.getTarget(); }

    @Override public void stop() { target = null; }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        LivingEntity found = mob.getTarget();
        if (found != null) { target = found; }
        if (reloading > 0 && --reloading == 0) { restock(); }
        if (retreating > 0) {
            retreating--;
            backAway();
            return;
        }
        if (target == null || !target.isAlive()) { return; }
        if (spent()) {
            if (!held().isEmpty()) { mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY); }
            return;
        }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        mob.getNavigation().stop();
        ItemStack thrown = held();
        if (thrown.isEmpty() || mob.level().isClientSide) { return; }
        Vec3 at = new Vec3(target.getX() - mob.getX(), target.getEyeY() - mob.getEyeY(), target.getZ() - mob.getZ());
        double far = Math.max(1.0D, at.length());
        Vec3 push = at.normalize().scale((NEAR + far * FURTHER) * power).add(0.0D, arc, 0.0D);
        if (isTnt(thrown)) { lit(push); }
        else if (returns) {
            flung(thrown, target);
            mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            return;
        }
        else { tossed(thrown, push); }
        spend();
    }

    private void spend() {
        mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        retreating = retreat;
        if (carried > 0) {
            int left = mob.getPersistentData().contains(LEFT) ? mob.getPersistentData().getInt(LEFT) : carried;
            left--;
            mob.getPersistentData().putInt(LEFT, left);
            if (left <= 0) { return; }
        }
        reloading = Math.max(1, reload);
    }

    private boolean spent() { return carried > 0 && mob.getPersistentData().contains(LEFT) && mob.getPersistentData().getInt(LEFT) <= 0; }

    private void restock() { mob.setItemSlot(EquipmentSlot.MAINHAND, pack.copy()); }

    private void lit(Vec3 push) {
        PrimedTnt primed = new PrimedTnt(mob.level(), mob.getX(), mob.getEyeY(), mob.getZ(), mob);
        primed.setFuse(fuse);
        primed.setDeltaMovement(push);
        mob.level().addFreshEntity(primed);
        thrownWith(SoundEvents.TNT_PRIMED);
    }

    private void flung(ItemStack thrown, LivingEntity aimed) {
        AttributeInstance strength = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        ReturningThrow flying = new ReturningThrow(mob.level(), mob, thrown, strength == null ? TRIDENT_DAMAGE : (float) strength.getValue());
        double dx = aimed.getX() - mob.getX();
        double dy = aimed.getBoundingBox().minY + aimed.getBbHeight() / 3.0F - flying.getY();
        double dz = aimed.getZ() - mob.getZ();
        flying.shoot(dx, dy + Math.sqrt(dx * dx + dz * dz) * 0.2D, dz, TRIDENT_SPEED * power, 14 - mob.level().getDifficulty().getId() * 4);
        mob.level().addFreshEntity(flying);
        thrownWith(SoundEvents.SNOWBALL_THROW);
    }

    private void tossed(ItemStack thrown, Vec3 push) {
        ItemEntity flying = new ItemEntity(mob.level(), mob.getX(), mob.getEyeY(), mob.getZ(), thrown.copy());
        flying.setPickUpDelay(40);
        flying.setDeltaMovement(push);
        mob.level().addFreshEntity(flying);
        thrownWith(SoundEvents.SNOWBALL_THROW);
    }

    private void thrownWith(SoundEvent fallback) {
        SoundEvent own = ContentEntities.sound(mob, ContentEntities.THROW);
        mob.playSound(own != null ? own : fallback, 1.0F, 1.0F);
    }

    private void backAway() {
        if (target == null || !mob.getNavigation().isDone()) { return; }
        Vec3 away = DefaultRandomPos.getPosAway(mob, 12, 5, target.position());
        if (away != null) { mob.getNavigation().moveTo(away.x, away.y, away.z, 1.4D); }
    }

    private ItemStack held() { return mob.getMainHandItem(); }

    private static boolean isTnt(ItemStack stack) { return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof TntBlock; }
}
