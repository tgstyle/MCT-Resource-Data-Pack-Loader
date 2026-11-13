package mctmods.resourcedatapackloader.common.util.entity;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public final class Stalking {
    private Stalking() {}

    public static double range(Mob mob) { return mob.getAttributeValue(Attributes.FOLLOW_RANGE); }

    @Nullable public static Player nearest(Mob mob) {
        return mob.level() instanceof ServerLevel server ? server.getNearestPlayer(TargetingConditions.forCombat().range(range(mob)), mob) : null;
    }

    public static int light(Entity entity) { return entity.level().getMaxLocalRawBrightness(entity.blockPosition()); }
}
