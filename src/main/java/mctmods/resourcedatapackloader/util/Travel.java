package mctmods.resourcedatapackloader.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Objects;
import java.util.Set;

public final class Travel {
    private Travel() {}

    public static void to(Entity entity, ServerLevel level, double x, double y, double z, float yaw, float pitch) {
        boolean crossing = entity.level() != level;
        entity.teleportTo(level, x, y, z, Set.of(), yaw, pitch);
        Entity moved = entity;
        if (crossing) {
            moved = Objects.requireNonNullElse(level.getEntity(entity.getUUID()), entity);
            moved.setDeltaMovement(Vec3.ZERO);
        }
        moved.setYHeadRot(yaw);
        if (moved instanceof LivingEntity living) { living.yBodyRot = yaw; }
    }
}
