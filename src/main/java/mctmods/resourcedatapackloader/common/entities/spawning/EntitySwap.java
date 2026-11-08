package mctmods.resourcedatapackloader.common.entities.spawning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class EntitySwap {
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private EntitySwap() {}

    public static boolean active() { return SWAPPING.get() == Boolean.TRUE; }

    public static boolean spawn(World world, Entity was, ResourceLocation wanted, boolean needsRoom) {
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = EntityList.createEntityByIDFromName(wanted, world);
            if (becomes == null) { return false; }
            becomes.setLocationAndAngles(was.posX, was.posY, was.posZ, was.rotationYaw, was.rotationPitch);
            if (needsRoom && !world.getCollisionBoxes(becomes, becomes.getEntityBoundingBox()).isEmpty()) { return false; }
            if (becomes instanceof EntityLiving) { ((EntityLiving) becomes).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(becomes)), null); }
            world.spawnEntity(becomes);
            return true;
        }
        finally { SWAPPING.set(Boolean.FALSE); }
    }
}
