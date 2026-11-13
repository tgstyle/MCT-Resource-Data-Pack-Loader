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
import net.minecraft.world.phys.AABB;
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
    private static final double MAX_PULL = 2.0D;
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

    public void shoot(Vec3 motion) {
        setDeltaMovement(motion);
        face();
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) { builder.define(STACK, ItemStack.EMPTY); }

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
        if (hit != null) { pull(hit); }
        if (hit != null || blocked) {
            discard();
            return;
        }
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 pull = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (pull.length() > MAX_PULL) { pull = pull.normalize().scale(MAX_PULL); }
        player.push(pull);
        player.hurtMarked = true;
        player.resetFallDistance();
    }

    @Nullable private Player struck(Vec3 from, Vec3 to) {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Player candidate : level().getEntitiesOfClass(Player.class, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D))) {
            if (candidate.isSpectator()) { continue; }
            AABB box = candidate.getBoundingBox().inflate(0.3D);
            Optional<Vec3> crossing = box.clip(from, to);
            if (crossing.isEmpty()) { continue; }
            double distance = from.distanceToSqr(crossing.get());
            if (distance < best) {
                best = distance;
                nearest = candidate;
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

    @Override public boolean hurt(@Nonnull DamageSource source, float amount) { return false; }

    @Override public boolean shouldBeSaved() { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {}
}
