package mctmods.resourcedatapackloader.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import javax.annotation.Nullable;

public final class Travel {
    private Travel() {}

    public static boolean to(Entity entity, int dimension, double x, double y, double z, float yaw, float pitch) { return move(entity, dimension, x, y, z, yaw, pitch) != null; }

    @Nullable public static Entity move(Entity entity, int dimension, double x, double y, double z, float yaw, float pitch) {
        Entity moved = entity;
        if (entity.dimension != dimension) {
            Entity arrived = entity.changeDimension(dimension, new Spot(x, y, z, yaw, pitch));
            if (arrived != null) { moved = arrived; }
            if (moved.dimension != dimension) { return null; }
        }
        if (moved instanceof EntityPlayerMP) { ((EntityPlayerMP) moved).connection.setPlayerLocation(x, y, z, yaw, pitch); }
        else { moved.setLocationAndAngles(x, y, z, yaw, pitch); }
        if (moved instanceof EntityLivingBase) {
            moved.setRotationYawHead(yaw);
            moved.setRenderYawOffset(yaw);
        }
        moved.fallDistance = 0.0F;
        moved.motionX = 0.0D;
        moved.motionY = 0.0D;
        moved.motionZ = 0.0D;
        moved.velocityChanged = true;
        return moved;
    }

    private static final class Spot implements ITeleporter {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        private Spot(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override public void placeEntity(World world, Entity entity, float ignored) {
            entity.setLocationAndAngles(x, y, z, yaw, pitch);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
        }
    }
}
