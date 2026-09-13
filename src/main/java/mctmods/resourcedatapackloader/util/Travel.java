package mctmods.resourcedatapackloader.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class Travel {
    private Travel() {}

    public static void to(Entity entity, ServerLevel level, double x, double y, double z, float yaw, float pitch) {
        entity.teleportTo(level, x, y, z, Set.of(), yaw, pitch);
        entity.setYHeadRot(yaw);
        if (entity instanceof LivingEntity living) { living.yBodyRot = yaw; }
    }
}
