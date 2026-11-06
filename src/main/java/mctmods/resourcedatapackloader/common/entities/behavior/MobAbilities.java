package mctmods.resourcedatapackloader.common.entities.behavior;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.pathfinding.PathNavigateGround;

public final class MobAbilities {
    private MobAbilities() {}

    public static void sightlessTarget(EntityCreature mob, Class<? extends EntityLivingBase> type, int priority) { mob.targetTasks.addTask(priority, new EntityAINearestAttackableTarget<>(mob, type, false)); }

    @Nullable public static EntityAIBase breakDoor(EntityLiving mob) {
        if (!(mob.getNavigator() instanceof PathNavigateGround)) { return null; }
        ((PathNavigateGround) mob.getNavigator()).setBreakDoors(true);
        return new EntityAIBreakDoor(mob);
    }

    public static void armPick(EntityLiving mob, String digItem, float dropChance, String owner) {
        if (mob.getHeldItemMainhand().isEmpty() && !digItem.isEmpty()) { MobGear.fixed(mob, Collections.singletonMap("mainhand", digItem), dropChance, owner); }
    }
}
