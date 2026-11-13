package mctmods.resourcedatapackloader.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PullingHand extends Entity {
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(PullingHand.class, EntityDataSerializers.ITEM_STACK);
    private static final double SPEED = 1.5D;
    private static final double PULL = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double CAP = 2.0D;
    @Nullable private LivingEntity owner;
    private float power;
    private int life;
    private int age;

    public PullingHand(EntityType<? extends PullingHand> type, Level level) { super(type, level); }

    public PullingHand(Level level, LivingEntity thrower, ItemStack stack, float power) {
        this(ContentEntities.pullingHand(), level);
        setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        owner = thrower;
        this.power = power;
        life = (int) Math.ceil(thrower.getAttributeValue(Attributes.FOLLOW_RANGE) / SPEED) + 5;
        entityData.set(STACK, stack.copy());
    }

    public void aim(Vec3 at) {
        setDeltaMovement(at.subtract(position()).normalize().scale(SPEED));
        face();
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override protected void defineSynchedData() { entityData.define(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return entityData.get(STACK); }

    @Override public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide()) {
            setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
            face();
            return;
        }
        if (++age > life || owner == null || !owner.isAlive() || owner.level() != level()) {
            discard();
            return;
        }
        Vec3 from = position();
        Vec3 to = from.add(motion);
        BlockHitResult block = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        boolean blocked = block.getType() != HitResult.Type.MISS;
        if (blocked) { to = block.getLocation(); }
        Player hit = struck(from, to);
        if (hit != null) {
            pull(hit);
            discard();
            return;
        }
        if (blocked) {
            discard();
            return;
        }
        setPos(to.x, to.y, to.z);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 pull = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (pull.length() > CAP) { pull = pull.normalize().scale(CAP); }
        player.push(pull.x, pull.y, pull.z);
        player.hurtMarked = true;
        player.resetFallDistance();
    }

    @Nullable private Player struck(Vec3 from, Vec3 to) {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D))) {
            if (!(candidate instanceof Player player) || player.isSpectator() || !player.isAlive()) { continue; }
            Optional<Vec3> crossing = player.getBoundingBox().inflate(0.3D).clip(from, to);
            if (crossing.isEmpty()) { continue; }
            double distance = from.distanceToSqr(crossing.get());
            if (distance < best) {
                best = distance;
                nearest = player;
            }
        }
        return nearest;
    }

    private void face() {
        Vec3 motion = getDeltaMovement();
        yRotO = getYRot();
        xRotO = getXRot();
        double sideways = motion.x;
        double forward = motion.z;
        setYRot((float) (Mth.atan2(sideways, forward) * (180.0D / Math.PI)));
        setXRot((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (180.0D / Math.PI)));
    }

    @Override public boolean shouldBeSaved() { return false; }

    @Override public boolean hurt(@Nonnull DamageSource source, float amount) { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {}
}
