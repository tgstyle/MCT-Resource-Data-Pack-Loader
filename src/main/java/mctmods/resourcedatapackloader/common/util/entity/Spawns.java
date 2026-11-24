package mctmods.resourcedatapackloader.common.util.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class Spawns {
    private Spawns() {}

    @Nullable public static Entity placed(World world, ResourceLocation id, double x, double y, double z, float yaw, float pitch, boolean initialise) {
        Entity made = EntityList.createEntityByIDFromName(id, world);
        if (made == null) { return null; }
        made.setLocationAndAngles(x, y, z, yaw, pitch);
        if (initialise) { initialise(world, made); }
        return made;
    }

    public static void initialise(World world, Entity entity) { initialise(world, entity, new BlockPos(entity)); }

    public static void initialise(World world, Entity entity, BlockPos at) {
        if (entity instanceof EntityLiving) { ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(at), null); }
    }
}
