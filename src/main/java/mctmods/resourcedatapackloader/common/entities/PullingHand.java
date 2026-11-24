package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import javax.annotation.Nonnull;

public final class PullingHand extends AbstractPackProjectile {
    private static final double PULL_SCALE = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double MAX_PULL = 2.0D;
    private float power;
    private int age;
    private int life;

    public PullingHand(World world) {
        super(world);
        setNoGravity(true);
    }

    public PullingHand(World world, EntityLivingBase thrower, ItemStack stack, float power) {
        this(world);
        thrownBy(thrower, stack);
        this.power = power;
        life = (int) Math.ceil(ContentAttributes.followRange(thrower) / 1.5D) + 5;
    }

    public void aim(double x, double y, double z, float velocity) {
        double length = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = x / length * velocity;
        motionY = y / length * velocity;
        motionZ = z / length * velocity;
        face(this);
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @Override public void onUpdate() {
        super.onUpdate();
        if (stepRemote()) { return; }
        if (++age > life || owner == null || !owner.isEntityAlive() || owner.world != world) {
            setDead();
            return;
        }
        Vec3d from = here();
        Vec3d to = ahead();
        RayTraceResult block = trace(from, to);
        if (block != null) { to = block.hitVec; }
        EntityPlayer hit = nearestHit(EntityPlayer.class, from, to, player -> true);
        if (hit != null) {
            pull(hit);
            setDead();
        }
        else if (block != null) { setDead(); }
        else { advance(); }
    }

    private void pull(EntityPlayer player) {
        Vec3d push = new Vec3d(owner.posX - player.posX, owner.posY - player.posY, owner.posZ - player.posZ).scale(PULL_SCALE * power);
        push = new Vec3d(push.x, push.y + LIFT, push.z);
        if (push.lengthSquared() > MAX_PULL * MAX_PULL) { push = push.normalize().scale(MAX_PULL); }
        player.addVelocity(push.x, push.y, push.z);
        player.velocityChanged = true;
        player.fallDistance = 0.0F;
    }

    @Override public boolean writeToNBTOptional(@Nonnull NBTTagCompound tag) { return false; }

    @Override public boolean writeToNBTAtomically(@Nonnull NBTTagCompound tag) { return false; }

    @Override protected void readEntityFromNBT(@Nonnull NBTTagCompound tag) {}

    @Override protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {}
}
