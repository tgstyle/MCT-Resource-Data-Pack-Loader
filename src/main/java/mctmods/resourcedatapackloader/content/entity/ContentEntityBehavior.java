package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIAnvilWork;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAICharge;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIDig;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIFleeWhenHurt;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIPounce;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISleepByDay;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISniff;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIGust;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIPatrol;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISwoop;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIKamikaze;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIStrike;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIThrower;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registries;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ContentEntityBehavior {
    private static final List<String> PLAYER_ONLY = Collections.singletonList("minecraft:player");
    private static final String HOME_X = "rdplHomeX";
    private static final String HOME_Y = "rdplHomeY";
    private static final String HOME_Z = "rdplHomeZ";

    private ContentEntityBehavior() {}

    static boolean declaresAttackDamage(EntityVariantDef def) {
        for (String key : def.attributes.keySet()) {
            if (ContentAttributes.lookup(key) == SharedMonsterAttributes.ATTACK_DAMAGE) { return true; }
        }
        return false;
    }

    static void behavior(EntityLiving living, EntityVariantDef def) {
        if (ContentLog.LOGGER.debugEnabled() && (def.charges || def.pounces || def.sniffs > 0 || def.sleepsByDay || def.home > 0 || def.fleesWhenHurt > 0.0F || def.patrols || def.swoops || def.gusts)) {
            ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} takes its behaviors:{}{}{}{}{}{}{}{}{}", def.registryName, (int) living.posX, (int) living.posY, (int) living.posZ, def.charges ? " charges" : "", def.pounces ? " pounces" : "", def.sniffs > 0 ? " sniffs " + def.sniffs : "", def.fleesWhenHurt > 0.0F ? " flees under " + def.fleesWhenHurt : "", def.sleepsByDay ? " sleeps by day" : "", def.home > 0 ? " home " + def.home : "", def.patrols ? " patrols" : "", def.swoops ? " swoops" : "", def.gusts ? " gusts " + def.gustPower : "");
        }
        if (def.collectsExperience && living instanceof EntityCreature) { living.tasks.addTask(0, new EntityAIAnvilWork((EntityCreature) living)); }
        settled(living, def);
        if (def.passive) {
            clear(living.targetTasks);
            removeMelee(living.tasks);
            living.setAttackTarget(null);
            return;
        }
        if (!def.hostile) {
            if (def.charges) { ContentLog.LOGGER.error("Entity variant {} asks to charge, but is not hostile, so it never takes a target to charge at", def.registryName); }
            if (def.pounces) { ContentLog.LOGGER.error("Entity variant {} asks to pounce, but is not hostile, so it never takes a target to pounce on", def.registryName); }
            if (def.sniffs > 0) { ContentLog.LOGGER.error("Entity variant {} asks to sniff players out, but is not hostile, so it never takes a target when it finds one", def.registryName); }
            if (def.fleesWhenHurt > 0.0F) { ContentLog.LOGGER.error("Entity variant {} asks to flee when hurt, but is not hostile, so it never has a fight to flee", def.registryName); }
            if (def.patrols) { ContentLog.LOGGER.error("Entity variant {} asks to patrol, but is not hostile, so its patrol never converges on anyone", def.registryName); }
            if (def.swoops) { ContentLog.LOGGER.error("Entity variant {} asks to swoop, but is not hostile, so it never takes a target to dive on", def.registryName); }
            if (def.gusts) { ContentLog.LOGGER.error("Entity variant {} asks to gust, but is not hostile, so it never takes a target to blow away", def.registryName); }
            if (def.digs) { ContentLog.LOGGER.error("Entity variant {} asks to dig, but is not hostile, so it never has a target to dig toward", def.registryName); }
            if (def.explodes) { ContentLog.LOGGER.error("Entity variant {} asks to explode, but is not hostile, so it never takes a target to close on", def.registryName); }
            if (def.throwsItems) { ContentLog.LOGGER.error("Entity variant {} asks to throw what it holds, but is not hostile, so it never takes a target to throw at", def.registryName); }
            return;
        }
        if (!(living instanceof EntityCreature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behavior needs", def.registryName, def.base);
            return;
        }
        EntityCreature creature = (EntityCreature) living;
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(living.tasks.taskEntries)) {
            if (entry.action instanceof EntityAIAvoidEntity || entry.action instanceof EntityAIPanic || tame(entry.action)) { living.tasks.removeTask(entry.action); }
        }
        if (ownStrike(living, def)) {
            for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(living.tasks.taskEntries)) {
                if (entry.action instanceof EntityAIAttackMelee) { living.tasks.removeTask(entry.action); }
            }
            living.tasks.addTask(2, new EntityAIStrike(creature, 1.2D, false));
            ContentLog.LOGGER.debug("Entity variant {} asks for an attack damage of {}, which {} never reads when it strikes, so the blow is dealt by the pack's own reckoning instead",
                    def.registryName, living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), def.base);
        }
        else {
            boolean already = false;
            for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries) {
                if (task.action instanceof EntityAIAttackMelee) {
                    already = true;
                    break;
                }
            }
            if (!already) { living.tasks.addTask(2, new EntityAIAttackMelee(creature, 1.2D, false)); }
        }
        if (def.explodes) { living.tasks.addTask(0, new EntityAIKamikaze(creature, def.explosionPower, def.explosionFuse, def.explosionFire)); }
        if (def.throwsItems) { living.tasks.addTask(0, new EntityAIThrower(creature, carrying(def), def.explosionFuse, def.throwReload > 0 ? def.throwReload : def.explosionFuse, def.throwRetreat > 0 ? def.throwRetreat : def.explosionFuse, def.throwAmmo, def.throwPower, def.throwArc, living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), def.throwReturns)); }
        if (def.charges) { living.tasks.addTask(1, new EntityAICharge(creature, 2.0D)); }
        if (def.pounces) { living.tasks.addTask(1, new EntityAIPounce(creature)); }
        if (def.fleesWhenHurt > 0.0F) { living.tasks.addTask(0, new EntityAIFleeWhenHurt(creature, def.fleesWhenHurt, 1.4D)); }
        if (def.sniffs > 0) { living.tasks.addTask(3, new EntityAISniff(creature, def.sniffs)); }
        if (def.gusts) { living.tasks.addTask(1, new EntityAIGust(creature, def.gustPower)); }
        if (def.digs) { living.tasks.addTask(1, new EntityAIDig(creature)); }
        if (def.swoops) { living.tasks.addTask(1, new EntityAISwoop(creature)); }
        if (def.patrols) { living.tasks.addTask(4, new EntityAIPatrol(creature)); }
        living.targetTasks.addTask(1, new EntityAIHurtByTarget(creature, true));
        int priority = 2;
        List<String> targets = def.targets.isEmpty() ? PLAYER_ONLY : def.targets;
        for (String name : targets) {
            Class<? extends EntityLivingBase> type = ContentEntities.living(name, def);
            if (type == null) { continue; }
            living.targetTasks.addTask(priority++, new EntityAINearestAttackableTarget<>(creature, type, !def.digs));
        }
    }

    private static boolean ownStrike(EntityLiving living, EntityVariantDef def) { return !(living instanceof EntityMob) && declaresAttackDamage(def); }

    private static boolean tame(EntityAIBase task) {
        return task instanceof EntityAIMate || task instanceof EntityAITempt || task instanceof EntityAIFollowParent || task instanceof EntityAIFollow
                || task instanceof EntityAIFollowOwner || task instanceof EntityAIFollowOwnerFlying || task instanceof EntityAISit || task instanceof EntityAILandOnOwnersShoulder;
    }

    private static void settled(EntityLiving living, EntityVariantDef def) {
        if (!def.sleepsByDay && def.home <= 0) { return; }
        if (!(living instanceof EntityCreature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to sleep by day or keep to a home, but {} does not walk the ground the way those behaviors need", def.registryName, def.base);
            return;
        }
        EntityCreature creature = (EntityCreature) living;
        if (def.home > 0) {
            NBTTagCompound kept = creature.getEntityData();
            if (!kept.hasKey(HOME_X)) {
                kept.setInteger(HOME_X, (int) Math.floor(creature.posX));
                kept.setInteger(HOME_Y, (int) Math.floor(creature.posY));
                kept.setInteger(HOME_Z, (int) Math.floor(creature.posZ));
            }
            creature.setHomePosAndDistance(new BlockPos(kept.getInteger(HOME_X), kept.getInteger(HOME_Y), kept.getInteger(HOME_Z)), def.home);
            living.tasks.addTask(4, new EntityAIMoveTowardsRestriction(creature, 1.0D));
        }
        if (def.sleepsByDay) { living.tasks.addTask(1, new EntityAISleepByDay(creature)); }
    }

    private static ItemStack carrying(EntityVariantDef def) {
        String named = def.equipment.get("mainhand");
        if (named == null) { return ItemStack.EMPTY; }
        ResourceLocation name = new ResourceLocation(named);
        Item item = Registries.find(ForgeRegistries.ITEMS, name);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static void clear(EntityAITasks tasks) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) { tasks.removeTask(entry.action); }
    }

    private static void removeMelee(EntityAITasks tasks) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) {
            if (entry.action instanceof EntityAIAttackMelee) { tasks.removeTask(entry.action); }
        }
    }
}
