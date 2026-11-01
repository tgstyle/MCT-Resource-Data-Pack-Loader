package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.entity.EntityCreature;

final class MobGriefing {
    private MobGriefing() {}

    static boolean allowed(EntityCreature mob) { return mob.world.getGameRules().getBoolean("mobGriefing"); }
}
