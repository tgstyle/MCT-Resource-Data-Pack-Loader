package mctmods.resourcedatapackloader.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ReturningThrow extends AbstractPackProjectile {
    private static final int LOYALTY = 3;
    private static final int STUCK_TICKS = 4;
    private static final int LIFE = 1200;
    private static final double CATCH_DISTANCE_SQ = 2.25D;
    private static final double DRAG = 0.99D;
    private static final double RETURN_DRAG = 0.95D;
    private static final double SPREAD = 0.0075D;
    private UUID ownerId;
    private float damage;
    private boolean returning;
    private int stuck;
    private int age;

    public ReturningThrow(World world) { super(world); }

    public ReturningThrow(World world, EntityLivingBase thrower, ItemStack stack, float damage) {
        this(world);
        thrownBy(thrower, stack);
        ownerId = thrower.getUniqueID();
        this.damage = damage;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = (x / length + rand.nextGaussian() * SPREAD * inaccuracy) * velocity;
        motionY = (y / length + rand.nextGaussian() * SPREAD * inaccuracy) * velocity;
        motionZ = (z / length + rand.nextGaussian() * SPREAD * inaccuracy) * velocity;
        face(this);
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @Override public void onUpdate() {
        super.onUpdate();
        if (stepRemote()) { return; }
        age++;
        EntityLivingBase thrower = owner();
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
        Vec3d from = here();
        Vec3d to = ahead();
        RayTraceResult block = trace(from, to);
        if (block != null) { to = block.hitVec; }
        EntityLivingBase hit = nearestHit(EntityLivingBase.class, from, to, candidate -> candidate != thrower);
        if (hit != null) {
            hit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower == null ? this : thrower), damage);
            playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
            motionX *= -0.01D;
            motionY *= -0.1D;
            motionZ *= -0.01D;
            returning = true;
        }
        else if (block != null) {
            setPosition(block.hitVec.x, block.hitVec.y, block.hitVec.z);
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            stuck = 1;
            playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.0F);
            return;
        }
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        motionX *= DRAG;
        motionY *= DRAG;
        motionZ *= DRAG;
        motionY -= 0.05D;
        face(this);
    }

    private void comeBack(@Nullable EntityLivingBase thrower) {
        if (thrower == null || !thrower.isEntityAlive() || thrower.world != world) {
            dropAndDie();
            return;
        }
        Vec3d toward = new Vec3d(thrower.posX - posX, thrower.posY + thrower.getEyeHeight() - posY, thrower.posZ - posZ);
        if (toward.lengthSquared() < CATCH_DISTANCE_SQ) {
            handBack(thrower);
            return;
        }
        Vec3d pull = toward.normalize().scale(0.05D * LOYALTY);
        motionX = motionX * RETURN_DRAG + pull.x;
        motionY = motionY * RETURN_DRAG + pull.y;
        motionZ = motionZ * RETURN_DRAG + pull.z;
        setPosition(posX + motionX, posY + toward.y * 0.015D * LOYALTY + motionY, posZ + motionZ);
        face(this);
    }

    private void handBack(EntityLivingBase thrower) {
        ItemStack carried = stack();
        if (thrower instanceof EntityLiving && thrower.getHeldItemMainhand().isEmpty()) { thrower.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, carried.copy()); }
        else if (!carried.isEmpty()) { world.spawnEntity(new EntityItem(world, thrower.posX, thrower.posY, thrower.posZ, carried.copy())); }
        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.4F, 1.0F);
        setDead();
    }

    private void dropAndDie() {
        if (!stack().isEmpty()) { world.spawnEntity(new EntityItem(world, posX, posY, posZ, stack().copy())); }
        setDead();
    }

    @Nullable private EntityLivingBase owner() {
        if (owner == null && ownerId != null && world instanceof WorldServer) {
            Entity found = ((WorldServer) world).getEntityFromUuid(ownerId);
            if (found instanceof EntityLivingBase) { owner = (EntityLivingBase) found; }
        }
        return owner;
    }

    @Override protected void readEntityFromNBT(@Nonnull NBTTagCompound tag) {
        if (tag.hasUniqueId("Owner")) { ownerId = tag.getUniqueId("Owner"); }
        dataManager.set(STACK, new ItemStack(tag.getCompoundTag("Item")));
        damage = tag.getFloat("Damage");
        returning = tag.getBoolean("Returning");
        stuck = tag.getInteger("Stuck");
        age = tag.getInteger("Age");
    }

    @Override protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        if (ownerId != null) { tag.setUniqueId("Owner", ownerId); }
        tag.setTag("Item", stack().writeToNBT(new NBTTagCompound()));
        tag.setFloat("Damage", damage);
        tag.setBoolean("Returning", returning);
        tag.setInteger("Stuck", stuck);
        tag.setInteger("Age", age);
    }
}
