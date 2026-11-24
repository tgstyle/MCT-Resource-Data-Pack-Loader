package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.util.entity.Spawns;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public final class EntitySwap {
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private EntitySwap() {}

    public static boolean active() { return SWAPPING.get() == Boolean.TRUE; }

    public static boolean spawn(World world, Entity was, ResourceLocation wanted, boolean needsRoom) {
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = Spawns.placed(world, wanted, was.posX, was.posY, was.posZ, was.rotationYaw, was.rotationPitch, false);
            if (becomes == null) { return false; }
            if (needsRoom && !world.getCollisionBoxes(becomes, becomes.getEntityBoundingBox()).isEmpty()) { return false; }
            Spawns.initialise(world, becomes);
            world.spawnEntity(becomes);
            return true;
        }
        finally { SWAPPING.set(Boolean.FALSE); }
    }
}
