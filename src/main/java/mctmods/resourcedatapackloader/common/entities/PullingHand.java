package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PullingHand extends Entity {
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(PullingHand.class, EntityDataSerializers.ITEM_STACK);
    private static final double PULL = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double MAX_PULL = 2.0D;
    private static final int EXTRA_LIFE = 5;
    @Nullable private LivingEntity owner;
    private float power;
    private int life;
    private int age;

    public PullingHand(EntityType<? extends PullingHand> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public PullingHand(Level level, LivingEntity thrower, ItemStack stack, float power, double range) {
        this(ContentEntities.pullingHand(), level);
        setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        owner = thrower;
        this.power = power;
        life = (int) Math.ceil(range / 1.5D) + EXTRA_LIFE;
        entityData.set(STACK, stack.copy());
    }

    public void aim(double x, double y, double z, float velocity) {
        setDeltaMovement(new Vec3(x, y, z).normalize().scale(velocity));
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
        setPos(to);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 push = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (push.length() > MAX_PULL) { push = push.normalize().scale(MAX_PULL); }
        player.push(push);
        LineCompat.markVelocity(player);
        player.resetFallDistance();
    }

    @Nullable private Player struck(Vec3 from, Vec3 to) {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D))) {
            if (!(candidate instanceof Player player) || player.isSpectator()) { continue; }
            AABB box = candidate.getBoundingBox().inflate(0.3D);
            Optional<Vec3> crossing = box.clip(from, to);
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

    @Override public boolean hurtServer(@Nonnull ServerLevel level, @Nonnull DamageSource source, float amount) { return false; }

    @Override public boolean isPickable() { return false; }

    @Override public boolean shouldBeSaved() { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull ValueInput tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull ValueOutput tag) {}
}
