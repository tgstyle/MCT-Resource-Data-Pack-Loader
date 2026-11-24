package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ai.DigGoal;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.pathfinding.PathNavigateGround;

public final class MobAbilities {
    private MobAbilities() {}

    static void target(EntityCreature mob, Class<? extends EntityLivingBase> type, int priority, boolean sight) { mob.targetTasks.addTask(priority, new EntityAINearestAttackableTarget<>(mob, type, sight)); }

    public static void dig(EntityCreature mob, boolean sightlessPlayer) {
        mob.tasks.addTask(1, new DigGoal(mob));
        if (sightlessPlayer) { target(mob, EntityPlayer.class, 2, false); }
    }

    @Nullable public static EntityAIBase breakDoor(EntityLiving mob) {
        if (!(mob.getNavigator() instanceof PathNavigateGround)) { return null; }
        ((PathNavigateGround) mob.getNavigator()).setBreakDoors(true);
        return new EntityAIBreakDoor(mob);
    }

    public static void armPick(EntityLiving mob, String digItem, float dropChance, String owner) {
        if (mob.getHeldItemMainhand().isEmpty() && !digItem.isEmpty()) { MobGear.fixed(mob, Collections.singletonMap(EntityEquipmentSlot.MAINHAND.getName(), digItem), dropChance, owner); }
    }
}
