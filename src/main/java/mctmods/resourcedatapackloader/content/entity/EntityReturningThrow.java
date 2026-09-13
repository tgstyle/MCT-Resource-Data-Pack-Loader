package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityReturningThrow extends Entity {
    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntityReturningThrow.class, DataSerializers.ITEM_STACK);
    private static final int LOYALTY = 3;
    private static final int STUCK_TICKS = 4;
    private static final int LIFE = 1200;
    private static final double CATCH_DISTANCE_SQ = 2.25D;
    private static final double DRAG = 0.99D;
    private UUID ownerId;
    private EntityLivingBase owner;
    private float damage;
    private boolean returning;
    private int stuck;
    private int age;

    public EntityReturningThrow(World world) {
        super(world);
        setSize(0.5F, 0.5F);
    }

    public EntityReturningThrow(World world, EntityLivingBase thrower, ItemStack stack, float damage) {
        this(world);
        setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - 0.1D, thrower.posZ);
        owner = thrower;
        ownerId = thrower.getUniqueID();
        this.damage = damage;
        dataManager.set(STACK, stack.copy());
    }

    @Override protected void entityInit() { dataManager.register(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return dataManager.get(STACK); }

    @Override public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            setPosition(posX + motionX, posY + motionY, posZ + motionZ);
            face();
            return;
        }
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
        Vec3d from = new Vec3d(posX, posY, posZ);
        Vec3d to = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult block = world.rayTraceBlocks(from, to, false, true, false);
        if (block != null) { to = block.hitVec; }
        EntityLivingBase hit = struck(from, to, thrower);
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
        face();
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
        motionX = motionX * 0.95D + pull.x;
        motionY = motionY * 0.95D + pull.y;
        motionZ = motionZ * 0.95D + pull.z;
        setPosition(posX + motionX, posY + toward.y * 0.015D * LOYALTY + motionY, posZ + motionZ);
        face();
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

    @Nullable private EntityLivingBase struck(Vec3d from, Vec3d to, @Nullable EntityLivingBase thrower) {
        EntityLivingBase nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D))) {
            if (!(candidate instanceof EntityLivingBase) || candidate == thrower || !candidate.canBeCollidedWith()) { continue; }
            AxisAlignedBB box = candidate.getEntityBoundingBox().grow(0.3D);
            RayTraceResult crossing = box.calculateIntercept(from, to);
            if (crossing == null) { continue; }
            double distance = from.squareDistanceTo(crossing.hitVec);
            if (distance < best) {
                best = distance;
                nearest = (EntityLivingBase) candidate;
            }
        }
        return nearest;
    }

    @Nullable private EntityLivingBase owner() {
        if (owner == null && ownerId != null && world instanceof WorldServer) {
            Entity found = ((WorldServer) world).getEntityFromUuid(ownerId);
            if (found instanceof EntityLivingBase) { owner = (EntityLivingBase) found; }
        }
        return owner;
    }

    private void face() {
        float flat = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180.0D / Math.PI));
        rotationPitch = (float) (MathHelper.atan2(motionY, flat) * (180.0D / Math.PI));
    }

    @Override public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) { return false; }

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
