package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.ai.AnvilWorkGoal;
import mctmods.resourcedatapackloader.common.entities.ai.ChargeGoal;
import mctmods.resourcedatapackloader.common.entities.ai.FleeLightGoal;
import mctmods.resourcedatapackloader.common.entities.ai.FleeWhenHurtGoal;
import mctmods.resourcedatapackloader.common.entities.ai.FollowPlayerGoal;
import mctmods.resourcedatapackloader.common.entities.ai.GustGoal;
import mctmods.resourcedatapackloader.common.entities.ai.KamikazeGoal;
import mctmods.resourcedatapackloader.common.entities.ai.PatrolGoal;
import mctmods.resourcedatapackloader.common.entities.ai.PounceGoal;
import mctmods.resourcedatapackloader.common.entities.ai.PullGoal;
import mctmods.resourcedatapackloader.common.entities.ai.SleepByDayGoal;
import mctmods.resourcedatapackloader.common.entities.ai.SniffGoal;
import mctmods.resourcedatapackloader.common.entities.ai.StrikeGoal;
import mctmods.resourcedatapackloader.common.entities.ai.SwoopGoal;
import mctmods.resourcedatapackloader.common.entities.ai.ThrowerGoal;
import mctmods.resourcedatapackloader.common.entities.ai.WorkOrderGoal;
import mctmods.resourcedatapackloader.common.entities.orders.ContentOrders;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import mctmods.resourcedatapackloader.common.util.entity.AiTasks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class ContentEntityBehavior {
    private static final List<String> PLAYER_ONLY = Collections.singletonList(IdNames.PLAYER);
    private static final String HOME_X = "rdplHomeX";
    private static final String HOME_Y = "rdplHomeY";
    private static final String HOME_Z = "rdplHomeZ";
    private static final String[][] PEACEFUL_ASKS = {
            { "charge", "it never takes a target to charge at" },
            { "pounce", "it never takes a target to pounce on" },
            { "sniff players out", "it never takes a target when it finds one" },
            { "flee when hurt", "it never has a fight to flee" },
            { "patrol", "its patrol never converges on anyone" },
            { "swoop", "it never takes a target to dive on" },
            { "gust", "it never takes a target to blow away" },
            { "dig", "it never has a target to dig toward" },
            { "explode", "it never takes a target to close on" },
            { "throw what it holds", "it never takes a target to throw at" }
    };

    private ContentEntityBehavior() {}

    public static boolean declaresAttackDamage(EntityVariantDef def) {
        for (String key : def.combat.attributes.keySet()) {
            if (ContentAttributes.lookup(key) == SharedMonsterAttributes.ATTACK_DAMAGE) { return true; }
        }
        return false;
    }

    static void behavior(EntityLiving living, EntityVariantDef def) {
        if (ContentLog.LOGGER.debugEnabled() && (def.combat.charges || def.combat.pounces || def.sniffs > 0 || def.sleepsByDay || def.home > 0 || def.fleesWhenHurt > 0.0F || def.patrols || def.swoops || def.combat.gusts || def.fleesLight > 0 || def.followsPlayers > 0 || def.teleportsWhenHit > 0 || def.pulls)) { ContentLog.LOGGER.debug("Entity variant {} at {} takes its behaviors:{}{}{}{}{}{}{}{}{}{}{}{}{}", def.registryName, ContentLog.at(living), def.combat.charges ? " charges" : "", def.combat.pounces ? " pounces" : "", def.sniffs > 0 ? " sniffs " + def.sniffs : "", def.fleesWhenHurt > 0.0F ? " flees under " + def.fleesWhenHurt : "", def.sleepsByDay ? " sleeps by day" : "", def.home > 0 ? " home " + def.home : "", def.patrols ? " patrols" : "", def.swoops ? " swoops" : "", def.combat.gusts ? " gusts " + def.combat.gustPower : "", def.fleesLight > 0 ? " flees light " + def.fleesLight : "", def.followsPlayers > 0 ? " follows " + def.followsPlayers : "", def.teleportsWhenHit > 0 ? " teleports when hit " + def.teleportsWhenHit : "", def.pulls ? " pulls" : ""); }
        if (def.collectsExperience && living instanceof EntityCreature) { living.tasks.addTask(0, new AnvilWorkGoal((EntityCreature) living)); }
        if (living instanceof EntityCreature && ContentOrders.wants(def)) { living.tasks.addTask(3, new WorkOrderGoal((EntityCreature) living)); }
        settled(living, def);
        stalks(living, def);
        if (def.passive) {
            AiTasks.clear(living.targetTasks);
            AiTasks.remove(living.tasks, EntityAIAttackMelee.class::isInstance);
            living.setAttackTarget(null);
            return;
        }
        if (!def.hostile) {
            boolean[] asked = { def.combat.charges, def.combat.pounces, def.sniffs > 0, def.fleesWhenHurt > 0.0F, def.patrols, def.swoops, def.combat.gusts, def.digs, def.explodes, def.throwsItems };
            for (int i = 0; i < asked.length; i++) {
                if (asked[i]) { ContentLog.LOGGER.error("Entity variant {} asks to {}, but is not hostile, so {}", def.registryName, PEACEFUL_ASKS[i][0], PEACEFUL_ASKS[i][1]); }
            }
            return;
        }
        EntityCreature creature = creature(living, def, "asks to be hostile", "the attack behavior needs");
        if (creature == null) { return; }
        AiTasks.remove(living.tasks, action -> action instanceof EntityAIAvoidEntity || action instanceof EntityAIPanic || tame(action));
        if (ownStrike(living, def)) {
            AiTasks.remove(living.tasks, EntityAIAttackMelee.class::isInstance);
            living.tasks.addTask(2, new StrikeGoal(creature, 1.2D, false));
            ContentLog.LOGGER.debug("Entity variant {} asks for an attack damage of {}, which {} never reads when it strikes, so the blow is dealt by the pack's own reckoning instead",
                    def.registryName, living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), def.base);
        }
        else {
            if (AiTasks.first(living.tasks, EntityAIAttackMelee.class) == null) { living.tasks.addTask(2, new EntityAIAttackMelee(creature, 1.2D, false)); }
        }
        if (def.explodes) { living.tasks.addTask(0, new KamikazeGoal(creature, def.explosionPower, def.explosionFuse, def.explosionFire)); }
        if (def.throwsItems) { living.tasks.addTask(0, new ThrowerGoal(creature, carrying(def), def.explosionFuse, def.throwReload > 0 ? def.throwReload : def.explosionFuse, def.throwRetreat > 0 ? def.throwRetreat : def.explosionFuse, def.throwAmmo, def.throwPower, def.throwArc, living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), def.throwReturns)); }
        special(creature, def.combat.charges, def.combat.pounces, def.combat.gusts, def.combat.gustPower);
        if (def.fleesWhenHurt > 0.0F) { living.tasks.addTask(0, new FleeWhenHurtGoal(creature, def.fleesWhenHurt, 1.4D)); }
        if (def.sniffs > 0) { living.tasks.addTask(3, new SniffGoal(creature, def.sniffs)); }
        if (def.digs) { MobAbilities.dig(creature, false); }
        if (def.swoops) { living.tasks.addTask(1, new SwoopGoal(creature)); }
        if (def.patrols) { living.tasks.addTask(4, new PatrolGoal(creature)); }
        living.targetTasks.addTask(1, new EntityAIHurtByTarget(creature, true));
        int priority = 2;
        List<String> targets = def.targets.isEmpty() ? PLAYER_ONLY : def.targets;
        for (String name : targets) {
            Class<? extends EntityLivingBase> type = ContentEntities.living(name, def);
            if (type == null) { continue; }
            MobAbilities.target(creature, type, priority++, !def.digs);
        }
    }

    static void special(EntityCreature creature, boolean charges, boolean pounces, boolean gusts, float gustPower) {
        if (charges) { creature.tasks.addTask(1, new ChargeGoal(creature, 2.0D)); }
        if (pounces) { creature.tasks.addTask(1, new PounceGoal(creature)); }
        if (gusts) { creature.tasks.addTask(1, new GustGoal(creature, gustPower)); }
    }

    private static boolean ownStrike(EntityLiving living, EntityVariantDef def) { return !(living instanceof EntityMob) && declaresAttackDamage(def); }

    private static boolean tame(EntityAIBase task) {
        return task instanceof EntityAIMate || task instanceof EntityAITempt || task instanceof EntityAIFollowParent || task instanceof EntityAIFollow
                || task instanceof EntityAIFollowOwner || task instanceof EntityAIFollowOwnerFlying || task instanceof EntityAISit || task instanceof EntityAILandOnOwnersShoulder;
    }

    @Nullable
    private static EntityCreature creature(EntityLiving living, EntityVariantDef def, String asks, String needs) {
        if (living instanceof EntityCreature) { return (EntityCreature) living; }
        ContentLog.LOGGER.error("Entity variant {} {}, but {} does not walk the ground the way {}", def.registryName, asks, def.base, needs);
        return null;
    }

    private static void settled(EntityLiving living, EntityVariantDef def) {
        if (!def.sleepsByDay && def.home <= 0) { return; }
        EntityCreature creature = creature(living, def, "asks to sleep by day or keep to a home", "those behaviors need");
        if (creature == null) { return; }
        if (def.home > 0) {
            NBTTagCompound kept = creature.getEntityData();
            if (!kept.hasKey(HOME_X)) {
                kept.setInteger(HOME_X, MathHelper.floor(creature.posX));
                kept.setInteger(HOME_Y, MathHelper.floor(creature.posY));
                kept.setInteger(HOME_Z, MathHelper.floor(creature.posZ));
            }
            creature.setHomePosAndDistance(new BlockPos(kept.getInteger(HOME_X), kept.getInteger(HOME_Y), kept.getInteger(HOME_Z)), def.home);
            living.tasks.addTask(4, new EntityAIMoveTowardsRestriction(creature, 1.0D));
        }
        if (def.sleepsByDay) { living.tasks.addTask(1, new SleepByDayGoal(creature)); }
    }

    private static void stalks(EntityLiving living, EntityVariantDef def) {
        if (def.fleesLight <= 0 && def.followsPlayers <= 0 && !def.pulls) { return; }
        EntityCreature creature = creature(living, def, "asks to flee light, follow players or pull", "those behaviors need");
        if (creature == null) { return; }
        if (def.fleesLight > 0) { living.tasks.addTask(1, new FleeLightGoal(creature, def.fleesLight, 1.2D)); }
        if (def.followsPlayers > 0) { living.tasks.addTask(3, new FollowPlayerGoal(creature, def.followsPlayers, def.fleesLight, 1.0D)); }
        if (!def.pulls) { return; }
        if (!def.hostile && def.followsPlayers <= 0) {
            ContentLog.LOGGER.error("Entity variant {} asks to pull, but is neither hostile nor follows players, so it never has anyone to pull", def.registryName);
            return;
        }
        living.tasks.addTask(1, new PullGoal(creature, pullStack(def), def.pullReload, def.pullPower, def.followsPlayers > 0));
    }

    private static ItemStack carrying(EntityVariantDef def) { return stackOf(def.equipment.get(EntityEquipmentSlot.MAINHAND.getName())); }

    private static ItemStack pullStack(EntityVariantDef def) {
        ItemStack named = stackOf(def.pullItem);
        if (!named.isEmpty()) { return named; }
        ContentLog.LOGGER.error("Entity variant {} names pull item {}, which nothing registers, so the hand is drawn as an ender pearl", def.registryName, def.pullItem);
        return new ItemStack(Items.ENDER_PEARL);
    }

    private static ItemStack stackOf(@Nullable String named) {
        if (named == null) { return ItemStack.EMPTY; }
        Item item = Registries.itemNamed(named);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
