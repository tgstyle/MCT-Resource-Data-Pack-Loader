package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.PhaseDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Goals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

final class ContentEntityPhases {
    private static final String MODIFIER = "rdpl:phase";
    private static final Map<UUID, State> STATES = new HashMap<>();

    private static final class State {
        int current = -1;
        List<WrappedGoal> goalsAdded = List.of();
        List<WrappedGoal> goalsDropped = List.of();
        List<WrappedGoal> targetsAdded = List.of();
        List<WrappedGoal> targetsDropped = List.of();
    }

    private ContentEntityPhases() {}

    static void join(Entity entity) { STATES.put(entity.getUUID(), new State()); }

    static void drop(Entity entity) { STATES.remove(entity.getUUID()); }

    @Nullable static BossBarDef bar(LivingEntity living, EntityVariantDef def) {
        State state = STATES.get(living.getUUID());
        if (state == null || state.current < 0) { return def.bossBar(); }
        BossBarDef phased = def.phases().get(state.current).bossBar();
        return phased == null ? def.bossBar() : phased;
    }

    static void tick(Mob mob, EntityVariantDef def) {
        State state = STATES.get(mob.getUUID());
        if (state == null) { return; }
        if (!mob.isAlive()) {
            drop(mob);
            return;
        }
        float fraction = mob.getHealth() / mob.getMaxHealth();
        int target = -1;
        for (int i = 0; i < def.phases().size(); i++) {
            if (fraction < def.phases().get(i).below()) { target = i; }
        }
        int was = state.current;
        if (target == was) { return; }
        BossBarDef shown = bar(mob, def);
        if (was >= 0) { leave(mob, def, def.phases().get(was), state); }
        if (target >= 0) { enter(mob, def, def.phases().get(target), state); }
        state.current = target;
        if (bar(mob, def) != shown) { ContentEntityBars.drop(mob); }
        report(mob, def, target, was);
    }

    private static UUID modifierId(Attribute attribute) { return UUID.nameUUIDFromBytes((MODIFIER + " " + attribute.getDescriptionId()).getBytes(StandardCharsets.UTF_8)); }

    private static void enter(Mob mob, EntityVariantDef def, PhaseDef phase, State state) {
        for (Map.Entry<String, Double> entry : phase.attributes().entrySet()) {
            AttributeInstance instance = ContentAttributes.instance(mob, entry.getKey(), def.key());
            if (instance == null) {
                if (ContentAttributes.lookup(entry.getKey()) != null) { ContentLog.LOGGER.error("Unknown attribute '{}' in a phase of {}, which {} does not carry either, skipping that modifier", entry.getKey(), def.key(), def.base()); }
                continue;
            }
            UUID id = modifierId(instance.getAttribute());
            instance.removeModifier(id);
            instance.addTransientModifier(new AttributeModifier(id, MODIFIER, entry.getValue() - instance.getBaseValue(), AttributeModifier.Operation.ADDITION));
        }
        List<WrappedGoal> goalsBefore = Goals.entries(mob.goalSelector);
        List<WrappedGoal> targetsBefore = Goals.entries(mob.targetSelector);
        ContentTasks.apply(mob, def, phase.tasks());
        if (mob instanceof PathfinderMob creature) { ContentEntityBehavior.special(creature, phase.charges() && !def.combat().charges(), phase.pounces() && !def.combat().pounces(), phase.gusts() && !def.combat().gusts(), phase.gustPower()); }
        List<WrappedGoal> goalsAfter = Goals.entries(mob.goalSelector);
        List<WrappedGoal> targetsAfter = Goals.entries(mob.targetSelector);
        state.goalsAdded = Goals.missing(goalsAfter, goalsBefore);
        state.goalsDropped = Goals.missing(goalsBefore, goalsAfter);
        state.targetsAdded = Goals.missing(targetsAfter, targetsBefore);
        state.targetsDropped = Goals.missing(targetsBefore, targetsAfter);
    }

    private static void leave(Mob mob, EntityVariantDef def, PhaseDef phase, State state) {
        for (String name : phase.attributes().keySet()) {
            AttributeInstance instance = ContentAttributes.instance(mob, name, def.key());
            if (instance != null) { instance.removeModifier(modifierId(instance.getAttribute())); }
        }
        Goals.undo(mob.goalSelector, state.goalsAdded, state.goalsDropped);
        Goals.undo(mob.targetSelector, state.targetsAdded, state.targetsDropped);
        state.goalsAdded = List.of();
        state.goalsDropped = List.of();
        state.targetsAdded = List.of();
        state.targetsDropped = List.of();
    }

    private static void report(Mob mob, EntityVariantDef def, int target, int was) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        StringBuilder values = new StringBuilder();
        for (String name : def.phases().get(Math.max(target, was)).attributes().keySet()) {
            AttributeInstance instance = ContentAttributes.instance(mob, name, def.key());
            if (instance != null) { values.append(' ').append(name).append('=').append(instance.getValue()); }
        }
        ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} {}:{}, {} tasks, {} targets", def.key(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ(),
                target >= 0 ? "enters phase " + (target + 1) + " of " + def.phases().size() : "returns to its base", values, mob.goalSelector.getAvailableGoals().size(), mob.targetSelector.getAvailableGoals().size());
    }
}
