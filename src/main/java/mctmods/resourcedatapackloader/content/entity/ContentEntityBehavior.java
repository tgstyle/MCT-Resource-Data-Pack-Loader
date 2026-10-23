package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.goal.AnvilWorkGoal;
import mctmods.resourcedatapackloader.content.entity.goal.ChargeGoal;
import mctmods.resourcedatapackloader.content.entity.goal.DigGoal;
import mctmods.resourcedatapackloader.content.entity.goal.FleeWhenHurtGoal;
import mctmods.resourcedatapackloader.content.entity.goal.GustGoal;
import mctmods.resourcedatapackloader.content.entity.goal.KamikazeGoal;
import mctmods.resourcedatapackloader.content.entity.goal.PatrolGoal;
import mctmods.resourcedatapackloader.content.entity.goal.PounceGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SleepByDayGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SniffGoal;
import mctmods.resourcedatapackloader.content.entity.goal.StrikeGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SwoopGoal;
import mctmods.resourcedatapackloader.content.entity.goal.ThrowerGoal;
import mctmods.resourcedatapackloader.content.entity.goal.WorkOrderGoal;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Goals;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.List;

final class ContentEntityBehavior {
    private static final List<String> PLAYER_ONLY = List.of("minecraft:player");
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

    private static ItemStack carrying(EntityVariantDef def) {
        String named = def.equipment().get("mainhand");
        Item item = named == null ? null : ContentStacks.item(Identifier.tryParse(named));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    static void behavior(Mob mob, EntityVariantDef def) {
        EntityVariantDef.Combat combat = def.combat();
        if (ContentLog.LOGGER.debugEnabled() && (combat.charges() || combat.pounces() || combat.sniffs() > 0 || combat.sleepsByDay() || combat.home() > 0 || combat.fleesWhenHurt() > 0.0F || combat.patrols() || combat.swoops() || combat.gusts())) {
            ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} takes its behaviors:{}{}{}{}{}{}{}{}{}", def.key(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ(), combat.charges() ? " charges" : "", combat.pounces() ? " pounces" : "", combat.sniffs() > 0 ? " sniffs " + combat.sniffs() : "", combat.fleesWhenHurt() > 0.0F ? " flees under " + combat.fleesWhenHurt() : "", combat.sleepsByDay() ? " sleeps by day" : "", combat.home() > 0 ? " home " + combat.home() : "", combat.patrols() ? " patrols" : "", combat.swoops() ? " swoops" : "", combat.gusts() ? " gusts " + combat.gustPower() : "");
        }
        if (def.flags().collectsExperience() && mob instanceof PathfinderMob creature) { mob.goalSelector.addGoal(0, new AnvilWorkGoal(creature)); }
        if (mob instanceof PathfinderMob creature && ContentOrders.wants(def)) { mob.goalSelector.addGoal(3, new WorkOrderGoal(creature)); }
        settled(mob, def);
        if (def.passive()) {
            Goals.remove(mob.targetSelector, _ -> true);
            removeMelee(mob.goalSelector);
            mob.setTarget(null);
            return;
        }
        if (!def.hostile()) {
            boolean[] asked = { combat.charges(), combat.pounces(), combat.sniffs() > 0, combat.fleesWhenHurt() > 0.0F, combat.patrols(), combat.swoops(), combat.gusts(), combat.digs(), combat.explodes(), combat.throwsItems() };
            for (int i = 0; i < asked.length; i++) {
                if (asked[i]) { ContentLog.LOGGER.error("Entity variant {} asks to {}, but is not hostile, so {}", def.key(), PEACEFUL_ASKS[i][0], PEACEFUL_ASKS[i][1]); }
            }
            return;
        }
        if (!(mob instanceof PathfinderMob creature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behavior needs", def.key(), def.base());
            return;
        }
        Goals.remove(mob.goalSelector, goal -> goal instanceof AvoidEntityGoal || goal instanceof PanicGoal || tame(goal));
        if (ownStrike(mob, def)) {
            removeMelee(mob.goalSelector);
            mob.goalSelector.addGoal(2, new StrikeGoal(creature, 1.2D, false));
            ContentLog.LOGGER.debug("Entity variant {} asks for an attack damage of {}, which {} never reads when it strikes, so the blow is dealt by the pack's own reckoning instead", def.key(), mob.getAttributeValue(Attributes.ATTACK_DAMAGE), def.base());
        }
        else {
            boolean already = false;
            for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
                if (wrapped.getGoal() instanceof MeleeAttackGoal) {
                    already = true;
                    break;
                }
            }
            if (!already) { mob.goalSelector.addGoal(2, new MeleeAttackGoal(creature, 1.2D, false)); }
        }
        if (combat.explodes()) { mob.goalSelector.addGoal(0, new KamikazeGoal(creature, combat.explosionPower(), combat.explosionFuse(), combat.explosionFire())); }
        if (combat.throwsItems()) { mob.goalSelector.addGoal(0, new ThrowerGoal(creature, carrying(def), combat.explosionFuse(), combat.throwReload() > 0 ? combat.throwReload() : combat.explosionFuse(), combat.throwRetreat() > 0 ? combat.throwRetreat() : combat.explosionFuse(), combat.throwAmmo(), combat.throwPower(), combat.throwArc(), mob.getAttributeValue(Attributes.FOLLOW_RANGE), combat.throwReturns())); }
        special(creature, combat.charges(), combat.pounces(), combat.gusts(), combat.gustPower());
        if (combat.fleesWhenHurt() > 0.0F) { mob.goalSelector.addGoal(0, new FleeWhenHurtGoal(creature, combat.fleesWhenHurt(), 1.4D)); }
        if (combat.sniffs() > 0) { mob.goalSelector.addGoal(3, new SniffGoal(creature, combat.sniffs())); }
        if (combat.swoops()) { mob.goalSelector.addGoal(1, new SwoopGoal(creature)); }
        if (combat.digs()) { mob.goalSelector.addGoal(1, new DigGoal(creature)); }
        if (combat.patrols()) { mob.goalSelector.addGoal(4, new PatrolGoal(creature)); }
        mob.targetSelector.addGoal(1, new HurtByTargetGoal(creature).setAlertOthers());
        int priority = 2;
        for (String name : def.targets().isEmpty() ? PLAYER_ONLY : def.targets()) {
            Class<? extends LivingEntity> type = ContentTasks.living(name, def.key(), mob.level());
            if (type == null) { continue; }
            mob.targetSelector.addGoal(priority++, new NearestAttackableTargetGoal<>(creature, type, 10, !def.combat().digs(), false, ContentTasks.fairGame(name)));
        }
    }

    static void special(PathfinderMob creature, boolean charges, boolean pounces, boolean gusts, float gustPower) {
        if (charges) { creature.goalSelector.addGoal(1, new ChargeGoal(creature, 2.0D)); }
        if (pounces) { creature.goalSelector.addGoal(1, new PounceGoal(creature)); }
        if (gusts) { creature.goalSelector.addGoal(1, new GustGoal(creature, gustPower)); }
    }

    private static boolean ownStrike(Mob mob, EntityVariantDef def) { return !(mob instanceof Monster) && declaresAttackDamage(def); }

    static boolean declaresAttackDamage(EntityVariantDef def) {
        for (String name : def.attributes().keySet()) {
            if (ContentAttributes.find(name, def.key()) == Attributes.ATTACK_DAMAGE) { return true; }
        }
        return false;
    }

    private static boolean tame(Goal goal) {
        return goal instanceof BreedGoal || goal instanceof TemptGoal || goal instanceof FollowParentGoal || goal instanceof FollowMobGoal
                || goal instanceof FollowOwnerGoal || goal instanceof SitWhenOrderedToGoal || goal instanceof LandOnOwnersShoulderGoal;
    }

    private static void settled(Mob mob, EntityVariantDef def) {
        EntityVariantDef.Combat combat = def.combat();
        if (!combat.sleepsByDay() && combat.home() <= 0) { return; }
        if (!(mob instanceof PathfinderMob creature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to sleep by day or keep to a home, but {} does not walk the ground the way those behaviors need", def.key(), def.base());
            return;
        }
        if (combat.home() > 0) {
            CompoundTag kept = creature.getPersistentData();
            if (!kept.contains(HOME_X)) {
                kept.putInt(HOME_X, creature.getBlockX());
                kept.putInt(HOME_Y, creature.getBlockY());
                kept.putInt(HOME_Z, creature.getBlockZ());
            }
            creature.setHomeTo(new BlockPos(kept.getIntOr(HOME_X, 0), kept.getIntOr(HOME_Y, 0), kept.getIntOr(HOME_Z, 0)), combat.home());
            mob.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(creature, 1.0D));
        }
        if (combat.sleepsByDay()) { mob.goalSelector.addGoal(1, new SleepByDayGoal(creature)); }
    }

    private static void removeMelee(GoalSelector selector) { Goals.remove(selector, goal -> goal instanceof MeleeAttackGoal); }
}
