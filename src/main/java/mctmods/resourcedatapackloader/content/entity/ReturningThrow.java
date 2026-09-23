package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ReturningThrow extends Entity {
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ReturningThrow.class, EntityDataSerializers.ITEM_STACK);
    private static final int LOYALTY = 3;
    private static final int STUCK_TICKS = 4;
    private static final int LIFE = 1200;
    private static final double CATCH_DISTANCE_SQ = 2.25D;
    private static final double DRAG = 0.99D;
    private static final double SPREAD = 0.0075D;
    @Nullable private UUID ownerId;
    @Nullable private LivingEntity owner;
    private float damage;
    private boolean returning;
    private int stuck;
    private int age;

    public ReturningThrow(EntityType<? extends ReturningThrow> type, Level level) { super(type, level); }

    public ReturningThrow(Level level, LivingEntity thrower, ItemStack stack, float damage) {
        this(ContentEntities.returningThrow(), level);
        setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        owner = thrower;
        ownerId = thrower.getUUID();
        this.damage = damage;
        entityData.set(STACK, stack.copy());
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        setDeltaMovement((x / length + random.nextGaussian() * SPREAD * inaccuracy) * velocity, (y / length + random.nextGaussian() * SPREAD * inaccuracy) * velocity, (z / length + random.nextGaussian() * SPREAD * inaccuracy) * velocity);
        face();
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) { builder.define(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return entityData.get(STACK); }

    @Override public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide) {
            setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
            face();
            return;
        }
        age++;
        LivingEntity thrower = owner();
        if (age > LIFE) {
            dropAndDie();
            return;
        }
        if (returning) {
            comeBack(thrower);
            return;
        }
        if (stuck > 0) {
            if (++stuck > STUCK_TICKS) { returning = true; }
            return;
        }
        Vec3 from = position();
        Vec3 to = from.add(motion);
        BlockHitResult block = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        boolean blocked = block.getType() != HitResult.Type.MISS;
        if (blocked) { to = block.getLocation(); }
        LivingEntity hit = struck(from, to, thrower);
        if (hit != null) {
            hit.hurt(damageSources().thrown(this, thrower == null ? this : thrower), damage);
            playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
            motion = new Vec3(motion.x * -0.01D, motion.y * -0.1D, motion.z * -0.01D);
            returning = true;
        }
        else if (blocked) {
            setPos(block.getLocation());
            setDeltaMovement(Vec3.ZERO);
            stuck = 1;
            playSound(SoundEvents.ARROW_HIT, 1.0F, 1.0F);
            return;
        }
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        setDeltaMovement(motion.x * DRAG, motion.y * DRAG - 0.05D, motion.z * DRAG);
        face();
    }

    private void comeBack(@Nullable LivingEntity thrower) {
        if (thrower == null || !thrower.isAlive() || thrower.level() != level()) {
            dropAndDie();
            return;
        }
        Vec3 toward = new Vec3(thrower.getX() - getX(), thrower.getEyeY() - getY(), thrower.getZ() - getZ());
        if (toward.lengthSqr() < CATCH_DISTANCE_SQ) {
            handBack(thrower);
            return;
        }
        Vec3 pull = toward.normalize().scale(0.05D * LOYALTY);
        Vec3 motion = getDeltaMovement().scale(0.95D).add(pull);
        setDeltaMovement(motion);
        setPos(getX() + motion.x, getY() + toward.y * 0.015D * LOYALTY + motion.y, getZ() + motion.z);
        face();
    }

    private void handBack(LivingEntity thrower) {
        ItemStack carried = stack();
        if (thrower instanceof Mob && thrower.getMainHandItem().isEmpty()) { thrower.setItemSlot(EquipmentSlot.MAINHAND, carried.copy()); }
        else if (!carried.isEmpty()) { level().addFreshEntity(new ItemEntity(level(), thrower.getX(), thrower.getY(), thrower.getZ(), carried.copy())); }
        playSound(SoundEvents.ITEM_PICKUP, 0.4F, 1.0F);
        discard();
    }

    private void dropAndDie() {
        if (!stack().isEmpty()) { level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), stack().copy())); }
        discard();
    }

    @Nullable private LivingEntity struck(Vec3 from, Vec3 to, @Nullable LivingEntity thrower) {
        LivingEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D))) {
            if (!(candidate instanceof LivingEntity living) || candidate == thrower || !candidate.isPickable()) { continue; }
            AABB box = candidate.getBoundingBox().inflate(0.3D);
            Optional<Vec3> crossing = box.clip(from, to);
            if (crossing.isEmpty()) { continue; }
            double distance = from.distanceToSqr(crossing.get());
            if (distance < best) {
                best = distance;
                nearest = living;
            }
        }
        return nearest;
    }

    @Nullable private LivingEntity owner() {
        if (owner == null && ownerId != null && level() instanceof ServerLevel server && server.getEntity(ownerId) instanceof LivingEntity found) { owner = found; }
        return owner;
    }

    private void face() {
        Vec3 motion = getDeltaMovement();
        double flat = motion.horizontalDistance();
        yRotO = getYRot();
        xRotO = getXRot();
        double sideways = motion.x;
        double forward = motion.z;
        setYRot((float) (Mth.atan2(sideways, forward) * (180.0D / Math.PI)));
        setXRot((float) (Mth.atan2(motion.y, flat) * (180.0D / Math.PI)));
    }

    @Override public boolean hurt(@Nonnull DamageSource source, float amount) { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        if (tag.hasUUID("Owner")) { ownerId = tag.getUUID("Owner"); }
        entityData.set(STACK, ItemStack.parseOptional(registryAccess(), tag.getCompound("Item")));
        damage = tag.getFloat("Damage");
        returning = tag.getBoolean("Returning");
        stuck = tag.getInt("Stuck");
        age = tag.getInt("Age");
    }

    @Override protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        if (ownerId != null) { tag.putUUID("Owner", ownerId); }
        tag.put("Item", stack().saveOptional(registryAccess()));
        tag.putFloat("Damage", damage);
        tag.putBoolean("Returning", returning);
        tag.putInt("Stuck", stuck);
        tag.putInt("Age", age);
    }
}
