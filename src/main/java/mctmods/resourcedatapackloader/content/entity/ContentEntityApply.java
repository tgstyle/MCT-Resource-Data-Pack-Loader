package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ICreeper;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IGhast;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMob;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.PathType;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

final class ContentEntityApply {
    static final String DRESSED = "rdplDressed";
    private static final String ROLLED = "rdplBabyRolled";
    static final String YOUNG = "rdplBabyYoung";
    private static final Map<String, String> CACTUS_PATHS = Map.of("danger_cactus", "DANGER_OTHER", "damage_cactus", "DAMAGE_OTHER");

    private ContentEntityApply() {}

    static void dress(Entity entity, EntityVariantDef def) {
        CompoundTag data = entity.getPersistentData();
        boolean first = !data.getBoolean(DRESSED);
        data.putBoolean(DRESSED, true);
        if (first && !def.name().isEmpty() && !entity.hasCustomName()) {
            entity.setCustomName(Component.literal(def.name()));
            entity.setCustomNameVisible(def.showName());
        }
        if (def.flags().silent()) { entity.setSilent(true); }
        if (def.flags().glowing()) { entity.setGlowingTag(true); }
        if (def.flags().invisible()) { entity.setInvisible(true); }
        if (def.flags().invulnerable()) { entity.setInvulnerable(true); }
        if (!(entity instanceof LivingEntity living)) { return; }
        if (first) {
            if (def.absorption() > 0.0F) { living.setAbsorptionAmount(def.absorption()); }
            effects(living, def);
        }
        attributes(living, def, first);
        AttributeInstance stride = living.getAttribute(Attributes.STEP_HEIGHT);
        if (stride != null && def.physics().stepHeight() >= 0.0F) { stride.setBaseValue(def.physics().stepHeight()); }
        stands(living, def);
        AttributeInstance size = living.getAttribute(Attributes.SCALE);
        if (size != null && def.scale() != 1.0F) { size.setBaseValue(def.scale()); }
        if (!(entity instanceof Mob mob)) { return; }
        if (first) {
            gear(mob, def);
            if (def.baby() > 0.0F && rolledYoung(mob, def)) { child(mob); }
            if (mob instanceof Villager villager && !def.profession().isEmpty()) { profession(villager, def); }
        }
        if (def.flags().persistent()) { mob.setPersistenceRequired(); }
        if (def.flags().noAI()) { mob.setNoAi(true); }
        if (def.flags().leftHanded()) { mob.setLeftHanded(true); }
        mob.setCanPickUpLoot(def.flags().picksUpLoot());
        if (def.physics().climbs() != null) { climber(mob, def.physics().climbs()); }
        if (def.combat().ownBlast()) { blast(mob, def.combat()); }
        navigation(mob, def);
        priorities(mob, def);
        ContentEntityBehavior.behavior(mob, def);
        ContentTasks.apply(mob, def);
    }

    private static void stands(LivingEntity living, EntityVariantDef def) {
        EntityVariantDef.Physics physics = def.physics();
        EntityVariantDef.Combat combat = def.combat();
        if (ContentLog.LOGGER.debugEnabled() && (physics.hurtResistance() >= 0 || physics.stepHeight() >= 0.0F || physics.climbs() != null || combat.attackReach() > 0.0F || combat.knockback() >= 0.0F || physics.teleports())) {
            ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} stands with step height {}, hurt resistance {}, attack reach {}, knockback {}, climbs {}, teleports {}", def.key(), living.getBlockX(), living.getBlockY(), living.getBlockZ(), living.maxUpStep(), ContentEntities.hurtResistance(living, 20), combat.attackReach(), combat.knockback(), physics.climbs(), physics.teleports());
        }
    }

    private static void attributes(LivingEntity mob, EntityVariantDef def, boolean first) {
        for (Map.Entry<String, Double> entry : def.attributes().entrySet()) {
            Holder<Attribute> attribute = ContentAttributes.find(entry.getKey(), def.key());
            if (attribute == null) { continue; }
            AttributeInstance instance = mob.getAttribute(attribute);
            if (instance == null) {
                ContentLog.LOGGER.error("Entity variant {} sets {}, which {} does not carry", def.key(), entry.getKey(), def.base());
                continue;
            }
            instance.setBaseValue(entry.getValue());
            if (first && attribute.equals(Attributes.MAX_HEALTH)) { mob.setHealth((float) (double) entry.getValue()); }
        }
    }

    private static void effects(LivingEntity mob, EntityVariantDef def) {
        for (Map.Entry<String, Integer> entry : def.effects().entrySet()) {
            ResourceLocation name = ResourceLocation.tryParse(entry.getKey());
            Holder<MobEffect> effect = Registered.holder(BuiltInRegistries.MOB_EFFECT, name);
            if (effect == null) {
                ContentLog.LOGGER.error("Entity variant {} wants effect {}, which nothing registers", def.key(), entry.getKey());
                continue;
            }
            mob.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, entry.getValue(), false, false));
        }
    }

    private static void gear(Mob mob, EntityVariantDef def) {
        for (Map.Entry<String, String> entry : def.equipment().entrySet()) {
            EquipmentSlot slot = slot(entry.getKey());
            if (slot == null) {
                ContentLog.LOGGER.error("Entity variant {} names equipment slot '{}', which is not one of mainhand, offhand, head, chest, legs or feet", def.key(), entry.getKey());
                continue;
            }
            Item item = ContentStacks.item(ResourceLocation.tryParse(entry.getValue()));
            if (item == null) {
                ContentLog.LOGGER.error("Entity variant {} gives {}, which nothing registers", def.key(), entry.getValue());
                continue;
            }
            mob.setItemSlot(slot, new ItemStack(item));
            mob.setDropChance(slot, def.dropChance());
        }
    }

    @Nullable private static EquipmentSlot slot(String name) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getName().equalsIgnoreCase(name) || slot.name().equalsIgnoreCase(name)) { return slot; }
        }
        return null;
    }

    private static boolean rolledYoung(Mob mob, EntityVariantDef def) {
        CompoundTag held = mob.getPersistentData();
        if (!held.contains(ROLLED)) {
            held.putBoolean(ROLLED, true);
            held.putBoolean(YOUNG, mob.getRandom().nextFloat() < def.baby());
        }
        return held.getBoolean(YOUNG);
    }

    private static void child(Mob mob) {
        if (mob instanceof Zombie zombie) { zombie.setBaby(true); }
        else if (mob instanceof AgeableMob ageable) { ageable.setAge(-24000); }
    }

    private static void profession(Villager villager, EntityVariantDef def) {
        ResourceLocation key = ResourceLocation.tryParse(def.profession());
        VillagerProfession found = Registered.find(BuiltInRegistries.VILLAGER_PROFESSION, key);
        if (found == null) {
            ContentLog.LOGGER.error("Entity variant {} names profession {}, which nothing registers", def.key(), def.profession());
            return;
        }
        villager.setVillagerData(villager.getVillagerData().setProfession(found));
        ContentVillagers.keepsJob(villager);
        if (def.career() > 0) { ContentLog.LOGGER.debug("Entity variant {} names career {}, which this line has no use for", def.key(), def.career()); }
    }

    private static void priorities(Mob mob, EntityVariantDef def) {
        for (Map.Entry<String, Float> entry : def.pathPriorities().entrySet()) {
            String named = CACTUS_PATHS.getOrDefault(entry.getKey().toLowerCase(Locale.ROOT), entry.getKey());
            PathType type = null;
            for (PathType value : PathType.values()) {
                if (value.name().equalsIgnoreCase(named)) { type = value; }
            }
            if (type == null) {
                ContentLog.LOGGER.error("Entity variant {} names path type '{}', which is not one of {}", def.key(), entry.getKey(), java.util.Arrays.toString(PathType.values()));
                continue;
            }
            mob.setPathfindingMalus(type, entry.getValue());
        }
    }

    private static void blast(Mob mob, EntityVariantDef.Combat combat) {
        if (mob instanceof Creeper creeper) {
            ((ICreeper) creeper).rdpl$setMaxSwell(combat.explosionFuse());
            ((ICreeper) creeper).rdpl$setExplosionRadius((int) combat.explosionPower());
        }
        else if (mob instanceof Ghast ghast) { ((IGhast) ghast).rdpl$setExplosionPower((int) combat.explosionPower()); }
    }

    private static void climber(Mob mob, boolean climbs) {
        IMob inner = (IMob) mob;
        if (climbs && !(mob.getNavigation() instanceof WallClimberNavigation)) { inner.rdpl$setNavigation(new WallClimberNavigation(mob, mob.level())); }
        else if (!climbs && mob.getNavigation() instanceof WallClimberNavigation) { inner.rdpl$setNavigation(new GroundPathNavigation(mob, mob.level())); }
    }

    private static void walker(Mob mob, EntityVariantDef def) {
        if (!(mob instanceof Rabbit)) {
            ContentLog.LOGGER.error("Entity variant {} asks to walk, but {} is not a rabbit, and only a rabbit moves in hops", def.key(), def.base());
            return;
        }
        ((IMob) mob).rdpl$setMoveControl(new MoveControl(mob));
    }

    private static void navigation(Mob mob, EntityVariantDef def) {
        IMob inner = (IMob) mob;
        if (def.physics().swims()) {
            swimmer(mob);
            mob.setPathfindingMalus(PathType.WATER, 0.0F);
        }
        else if (def.physics().amphibious() && mob.getNavigation() instanceof GroundPathNavigation ground) { ground.setCanFloat(true); }
        if (def.combat().swoops()) {
            inner.rdpl$setNavigation(new FlyingPathNavigation(mob, mob.level()));
            inner.rdpl$setMoveControl(new FlyingMoveControl(mob, 20, true));
        }
        if (def.physics().walks()) { walker(mob, def); }
        if (def.physics().breathesUnderwater() || def.physics().swims()) { ContentTasks.drop(mob.goalSelector, FloatGoal.class); }
    }

    private static void swimmer(Mob mob) {
        ((IMob) mob).rdpl$setNavigation(new WaterBoundPathNavigation(mob, mob.level()));
        ((IMob) mob).rdpl$setMoveControl(new SwimmingMoveControl(mob));
    }

    static void amphibious(Mob mob) {
        boolean wet = mob.isInWater();
        if (wet == mob.getNavigation() instanceof WaterBoundPathNavigation) { return; }
        if (wet) {
            swimmer(mob);
            return;
        }
        GroundPathNavigation ground = new GroundPathNavigation(mob, mob.level());
        ground.setCanFloat(true);
        ((IMob) mob).rdpl$setNavigation(ground);
        ((IMob) mob).rdpl$setMoveControl(new MoveControl(mob));
    }
}
