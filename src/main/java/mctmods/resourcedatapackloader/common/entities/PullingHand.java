package mctmods.resourcedatapackloader.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PullingHand extends Entity {
    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(PullingHand.class, DataSerializers.ITEM_STACK);
    private static final double PULL_SCALE = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double MAX_PULL = 2.0D;
    private EntityLivingBase owner;
    private float power;
    private int age;
    private int life;

    public PullingHand(World world) {
        super(world);
        setSize(0.5F, 0.5F);
        setNoGravity(true);
    }

    public PullingHand(World world, EntityLivingBase thrower, ItemStack stack, float power) {
        this(world);
        setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - 0.1D, thrower.posZ);
        owner = thrower;
        this.power = power;
        life = (int) Math.ceil(thrower.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue() / 1.5D) + 5;
        dataManager.set(STACK, stack.copy());
    }

    public void aim(double x, double y, double z, float velocity) {
        double length = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = x / length * velocity;
        motionY = y / length * velocity;
        motionZ = z / length * velocity;
        ReturningThrow.face(this);
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @Override protected void entityInit() { dataManager.register(STACK, ItemStack.EMPTY); }

    public ItemStack stack() { return dataManager.get(STACK); }

    @Override public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            setPosition(posX + motionX, posY + motionY, posZ + motionZ);
            ReturningThrow.face(this);
            return;
        }
        if (++age > life || owner == null || !owner.isEntityAlive() || owner.world != world) {
            setDead();
            return;
        }
        Vec3d from = new Vec3d(posX, posY, posZ);
        Vec3d to = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult block = world.rayTraceBlocks(from, to, false, true, false);
        if (block != null) { to = block.hitVec; }
        EntityPlayer hit = struck(from, to);
        if (hit != null) {
            pull(hit);
            setDead();
        }
        else if (block != null) { setDead(); }
        else {
            setPosition(posX + motionX, posY + motionY, posZ + motionZ);
            ReturningThrow.face(this);
        }
    }

    private void pull(EntityPlayer player) {
        Vec3d push = new Vec3d(owner.posX - player.posX, owner.posY - player.posY, owner.posZ - player.posZ).scale(PULL_SCALE * power);
        push = new Vec3d(push.x, push.y + LIFT, push.z);
        if (push.lengthSquared() > MAX_PULL * MAX_PULL) { push = push.normalize().scale(MAX_PULL); }
        player.addVelocity(push.x, push.y, push.z);
        player.velocityChanged = true;
        player.fallDistance = 0.0F;
    }

    @Nullable private EntityPlayer struck(Vec3d from, Vec3d to) {
        EntityPlayer nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D))) {
            if (!(candidate instanceof EntityPlayer) || !candidate.canBeCollidedWith()) { continue; }
            AxisAlignedBB box = candidate.getEntityBoundingBox().grow(0.3D);
            RayTraceResult crossing = box.calculateIntercept(from, to);
            if (crossing == null) { continue; }
            double distance = from.squareDistanceTo(crossing.hitVec);
            if (distance < best) {
                best = distance;
                nearest = (EntityPlayer) candidate;
            }
        }
        return nearest;
    }

    @Override public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) { return false; }

    @Override public boolean writeToNBTOptional(@Nonnull NBTTagCompound tag) { return false; }

    @Override public boolean writeToNBTAtomically(@Nonnull NBTTagCompound tag) { return false; }

    @Override protected void readEntityFromNBT(@Nonnull NBTTagCompound tag) {}

    @Override protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {}
}
