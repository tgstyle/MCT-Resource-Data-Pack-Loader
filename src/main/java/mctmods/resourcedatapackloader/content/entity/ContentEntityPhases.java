package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.PhaseDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.AiTasks;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.server.MinecraftServer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

final class ContentEntityPhases {
    private static final String MODIFIER = "rdpl phase";
    private static final Map<UUID, State> STATES = new HashMap<>();

    private static final class State {
        int current = -1;
        List<EntityAITasks.EntityAITaskEntry> tasksAdded = Collections.emptyList();
        List<EntityAITasks.EntityAITaskEntry> tasksDropped = Collections.emptyList();
        List<EntityAITasks.EntityAITaskEntry> targetsAdded = Collections.emptyList();
        List<EntityAITasks.EntityAITaskEntry> targetsDropped = Collections.emptyList();
    }

    private ContentEntityPhases() {}

    static void join(Entity entity) {
        STATES.put(entity.getUniqueID(), new State());
    }

    @Nullable static BossBarDef bar(EntityLivingBase living, EntityVariantDef def) {
        State state = STATES.get(living.getUniqueID());
        if (state == null || state.current < 0) { return def.bossBar; }
        BossBarDef phased = def.phases.get(state.current).bossBar;
        return phased == null ? def.bossBar : phased;
    }

    static void tick(EntityLiving living, EntityVariantDef def) {
        UUID id = living.getUniqueID();
        State state = STATES.get(id);
        if (state == null) { return; }
        if (!living.isEntityAlive()) {
            STATES.remove(id);
            return;
        }
        float fraction = living.getHealth() / living.getMaxHealth();
        int target = -1;
        for (int i = 0; i < def.phases.size(); i++) {
            if (fraction < def.phases.get(i).below) { target = i; }
        }
        int was = state.current;
        if (target == was) { return; }
        BossBarDef shown = bar(living, def);
        if (was >= 0) { leave(living, def.phases.get(was), state); }
        if (target >= 0) { enter(living, def, def.phases.get(target), state); }
        state.current = target;
        if (bar(living, def) != shown) { ContentEntityBars.drop(id); }
        if (ContentLog.LOGGER.debugEnabled()) { report(living, def, target, was); }
    }

    static void sweep(MinecraftServer server) {
        STATES.keySet().removeIf(id -> {
            Entity entity = server.getEntityFromUuid(id);
            return entity == null || entity.isDead;
        });
    }

    private static UUID modifierId(IAttributeInstance instance) { return UUID.nameUUIDFromBytes((MODIFIER + " " + instance.getAttribute().getName()).getBytes(StandardCharsets.UTF_8)); }

    private static void enter(EntityLiving living, EntityVariantDef def, PhaseDef phase, State state) {
        for (Map.Entry<String, Double> entry : phase.attributes.entrySet()) {
            IAttributeInstance instance = ContentAttributes.instance(living.getAttributeMap(), entry.getKey());
            if (instance == null) {
                ContentLog.LOGGER.error("Unknown attribute '{}' in a phase of {}, which {} does not carry either, skipping that modifier", entry.getKey(), def.registryName, def.base);
                continue;
            }
            UUID id = modifierId(instance);
            instance.removeModifier(id);
            instance.applyModifier(new AttributeModifier(id, MODIFIER, entry.getValue() - instance.getBaseValue(), 0).setSaved(false));
        }
        List<EntityAITasks.EntityAITaskEntry> tasksBefore = AiTasks.entries(living.tasks);
        List<EntityAITasks.EntityAITaskEntry> targetsBefore = AiTasks.entries(living.targetTasks);
        ContentTasks.apply(living, def, phase.tasks);
        if (living instanceof EntityCreature) { ContentEntityBehavior.special((EntityCreature) living, phase.charges && !def.charges, phase.pounces && !def.pounces, phase.gusts && !def.gusts, phase.gustPower); }
        List<EntityAITasks.EntityAITaskEntry> tasksAfter = AiTasks.entries(living.tasks);
        List<EntityAITasks.EntityAITaskEntry> targetsAfter = AiTasks.entries(living.targetTasks);
        state.tasksAdded = AiTasks.missing(tasksAfter, tasksBefore);
        state.tasksDropped = AiTasks.missing(tasksBefore, tasksAfter);
        state.targetsAdded = AiTasks.missing(targetsAfter, targetsBefore);
        state.targetsDropped = AiTasks.missing(targetsBefore, targetsAfter);
    }

    private static void leave(EntityLiving living, PhaseDef phase, State state) {
        for (String name : phase.attributes.keySet()) {
            IAttributeInstance instance = ContentAttributes.instance(living.getAttributeMap(), name);
            if (instance != null) { instance.removeModifier(modifierId(instance)); }
        }
        AiTasks.undo(living.tasks, state.tasksAdded, state.tasksDropped);
        AiTasks.undo(living.targetTasks, state.targetsAdded, state.targetsDropped);
        state.tasksAdded = Collections.emptyList();
        state.tasksDropped = Collections.emptyList();
        state.targetsAdded = Collections.emptyList();
        state.targetsDropped = Collections.emptyList();
    }

    private static void report(EntityLiving living, EntityVariantDef def, int target, int was) {
        StringBuilder values = new StringBuilder();
        for (String name : def.phases.get(Math.max(target, was)).attributes.keySet()) {
            IAttributeInstance instance = ContentAttributes.instance(living.getAttributeMap(), name);
            if (instance != null) { values.append(' ').append(name).append('=').append(instance.getAttributeValue()); }
        }
        ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} {}:{}, {} tasks, {} targets", def.registryName, (int) living.posX, (int) living.posY, (int) living.posZ,
                target >= 0 ? "enters phase " + (target + 1) + " of " + def.phases.size() : "returns to its base", values, living.tasks.taskEntries.size(), living.targetTasks.taskEntries.size());
    }
}
