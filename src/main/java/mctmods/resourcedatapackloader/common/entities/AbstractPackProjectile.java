package mctmods.resourcedatapackloader.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractPackProjectile extends Entity {
    protected static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(AbstractPackProjectile.class, DataSerializers.ITEM_STACK);
    protected EntityLivingBase owner;

    protected AbstractPackProjectile(World world) {
        super(world);
        setSize(0.5F, 0.5F);
    }

    protected final void thrownBy(EntityLivingBase thrower, ItemStack stack) {
        setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - 0.1D, thrower.posZ);
        owner = thrower;
        dataManager.set(STACK, stack.copy());
    }

    @Override protected void entityInit() { dataManager.register(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return dataManager.get(STACK); }

    protected final boolean stepRemote() {
        if (!world.isRemote) { return false; }
        advance();
        return true;
    }

    protected final void advance() {
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        face(this);
    }

    protected final Vec3d here() { return new Vec3d(posX, posY, posZ); }

    protected final Vec3d ahead() { return new Vec3d(posX + motionX, posY + motionY, posZ + motionZ); }

    @Nullable protected final RayTraceResult trace(Vec3d from, Vec3d to) { return world.rayTraceBlocks(from, to, false, true, false); }

    @Nullable protected final <T extends Entity> T nearestHit(Class<T> type, Vec3d from, Vec3d to, Predicate<T> wanted) {
        T nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D))) {
            if (!type.isInstance(candidate) || !candidate.canBeCollidedWith()) { continue; }
            T typed = type.cast(candidate);
            if (!wanted.test(typed)) { continue; }
            AxisAlignedBB box = candidate.getEntityBoundingBox().grow(0.3D);
            RayTraceResult crossing = box.calculateIntercept(from, to);
            if (crossing == null) { continue; }
            double distance = from.squareDistanceTo(crossing.hitVec);
            if (distance < best) {
                best = distance;
                nearest = typed;
            }
        }
        return nearest;
    }

    protected static void face(Entity entity) {
        float flat = MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
        entity.rotationYaw = (float) (MathHelper.atan2(entity.motionX, entity.motionZ) * (180.0D / Math.PI));
        entity.rotationPitch = (float) (MathHelper.atan2(entity.motionY, flat) * (180.0D / Math.PI));
    }

    @Override public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) { return false; }
}
