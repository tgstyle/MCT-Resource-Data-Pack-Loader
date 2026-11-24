package mctmods.resourcedatapackloader.common.entities;

import java.util.Optional;
import java.util.function.Predicate;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractPackProjectile extends Entity {
    protected static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(AbstractPackProjectile.class, EntityDataSerializers.ITEM_STACK);
    @Nullable protected LivingEntity owner;

    protected AbstractPackProjectile(EntityType<?> type, Level level) { super(type, level); }

    protected final void thrownBy(LivingEntity thrower, ItemStack stack) {
        setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        owner = thrower;
        entityData.set(STACK, stack.copy());
    }

    @Override protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) { builder.define(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return entityData.get(STACK); }

    protected final boolean stepRemote() {
        if (!level().isClientSide()) { return false; }
        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        face();
        return true;
    }

    protected final BlockHitResult trace(Vec3 from, Vec3 to) { return level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)); }

    @Nullable protected final <T extends LivingEntity> T nearestHit(Class<T> type, Vec3 from, Vec3 to, Predicate<T> wanted) {
        T nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D))) {
            if (!type.isInstance(candidate)) { continue; }
            T typed = type.cast(candidate);
            if (!wanted.test(typed)) { continue; }
            Optional<Vec3> crossing = candidate.getBoundingBox().inflate(0.3D).clip(from, to);
            if (crossing.isEmpty()) { continue; }
            double distance = from.distanceToSqr(crossing.get());
            if (distance < best) {
                best = distance;
                nearest = typed;
            }
        }
        return nearest;
    }

    protected final void face() {
        Vec3 motion = getDeltaMovement();
        yRotO = getYRot();
        xRotO = getXRot();
        double sideways = motion.x;
        double forward = motion.z;
        setYRot((float) (Mth.atan2(sideways, forward) * (180.0D / Math.PI)));
        setXRot((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (180.0D / Math.PI)));
    }

    @Override public boolean hurtServer(@Nonnull ServerLevel level, @Nonnull DamageSource source, float amount) { return false; }
}
