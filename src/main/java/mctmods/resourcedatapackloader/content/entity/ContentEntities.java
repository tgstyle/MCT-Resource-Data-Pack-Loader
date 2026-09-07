package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.entity.goal.ChargeGoal;
import mctmods.resourcedatapackloader.content.entity.goal.FleeWhenHurtGoal;
import mctmods.resourcedatapackloader.content.entity.goal.GustGoal;
import mctmods.resourcedatapackloader.content.entity.goal.KamikazeGoal;
import mctmods.resourcedatapackloader.content.entity.goal.PatrolGoal;
import mctmods.resourcedatapackloader.content.entity.goal.PounceGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SleepByDayGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SniffGoal;
import mctmods.resourcedatapackloader.content.entity.goal.SwoopGoal;
import mctmods.resourcedatapackloader.content.entity.goal.ThrowerGoal;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityType;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMob;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentEntities {
    private static final Map<ResourceLocation, EntityVariantDef> DEFS = new LinkedHashMap<>();
    private static final Map<EntityType<?>, EntityVariantDef> BY_TYPE = new IdentityHashMap<>();
    private static final Map<EntityType<?>, EntityType<?>> BASES = new IdentityHashMap<>();
    private static final Map<ResourceLocation, EntityType<Mob>> TYPES = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final List<String> PLAYER_ONLY = List.of("minecraft:player");
    private static final String SPAWN_EGG_MODEL = "{\"parent\":\"minecraft:item/template_spawn_egg\"}";
    private static final String DRESSED = "rdplDressed";
    private static final String HOME_X = "rdplHomeX";
    private static final String HOME_Y = "rdplHomeY";
    private static final String HOME_Z = "rdplHomeZ";
    private static final String BORN = "rdplBorn";
    private static final String CALM = "rdplCalmAt";
    private static final String ROLLED = "rdplBabyRolled";
    private static final String YOUNG = "rdplBabyYoung";
    private static final int ROUSED = 60;
    private static final long CALM_STEP = 10L;
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private ContentEntities() {}

    public static void load() {
        DEFS.clear();
        if (!Config.content.entities()) { return; }
        Json.eachFile(PackManager.ENTITIES, "entity file", (key, contents) -> {
            EntityVariantDef def = ContentEntityParser.parse(key, contents);
            if (def == null) { return; }
            if (!ContentRegistry.available(def.requires(), key)) {
                ContentLog.LOGGER.debug("Entity variant {} needs {}, which is not here, so it is left out", key, def.requires());
                return;
            }
            DEFS.put(key, def);
        });
        if (!DEFS.isEmpty()) { Summary.info("entities", "Loaded " + DEFS.size() + " entity variant(s) from packs"); }
    }

    public static boolean defines(ResourceLocation id) { return DEFS.containsKey(id); }

    public static String categoryOf(ResourceLocation id) {
        EntityVariantDef def = DEFS.get(id);
        EntityType<?> base = def == null ? null : Registered.find(BuiltInRegistries.ENTITY_TYPE, def.base());
        return base == null ? "monster" : base.getCategory().getName();
    }

    public static Map<ResourceLocation, EntityType<Mob>> types() { return Collections.unmodifiableMap(TYPES); }

    @Nullable public static EntityVariantDef def(Entity entity) { return BY_TYPE.get(entity.getType()); }

    @Nullable public static EntityVariantDef def(EntityType<?> type) { return BY_TYPE.get(type); }

    @Nullable public static EntityType<?> base(EntityType<?> variant) { return BASES.get(variant); }

    public static void registerTypes(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        int made = 0;
        for (EntityVariantDef def : DEFS.values()) {
            EntityType<?> base = Registered.find(BuiltInRegistries.ENTITY_TYPE, def.base());
            if (base == null) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which nothing registers, leaving it out", def.key(), def.base());
                continue;
            }
            EntityType.EntityFactory<?> baseFactory = ((IEntityType) base).rdpl$factory();
            EntityType.EntityFactory<Mob> factory = (type, level) -> {
                Entity built = make(baseFactory, type, level);
                if (built instanceof Mob mob) { return mob; }
                built.discard();
                throw new IllegalStateException("Entity variant " + def.key() + " is based on " + def.base() + ", which is not a mob, so it cannot be made");
            };
            EntityDimensions dims = base.getDimensions();
            float width = def.width() > 0.0F ? def.width() : dims.width();
            float height = def.height() > 0.0F ? def.height() : dims.height();
            EntityType.Builder<Mob> builder = EntityType.Builder.of(factory, def.hostile() ? MobCategory.MONSTER : base.getCategory())
                    .sized(width, height)
                    .clientTrackingRange(def.tracking().range()).updateInterval(def.tracking().frequency()).setShouldReceiveVelocityUpdates(def.tracking().velocity());
            if (def.flags().fireproof()) { builder = builder.fireImmune(); }
            EntityType<Mob> type = builder.build(def.key().toString());
            helper.register(def.key(), type);
            BY_TYPE.put(type, def);
            BASES.put(type, base);
            TYPES.put(def.key(), type);
            made++;
            ContentLog.LOGGER.debug("Entity variant {} read from the pack with attributes {} and equipment {}", def.key(), def.attributes(), def.equipment());
        }
        if (made > 0) { Summary.info("entities.registered", "Registered " + made + " entity variant(s) from packs"); }
    }

    public static void registerEggs(RegisterEvent.RegisterHelper<Item> helper) {
        for (EntityVariantDef def : DEFS.values()) {
            if (!def.egg().wanted()) { continue; }
            EntityType<?> base = Registered.find(BuiltInRegistries.ENTITY_TYPE, def.base());
            if (base == null) { continue; }
            SpawnEggItem baseEgg = SpawnEggItem.byId(base);
            int primary = def.egg().primary() >= 0 ? def.egg().primary() : baseEgg == null ? 0xFFFFFF : baseEgg.getColor(0);
            int secondary = def.egg().secondary() >= 0 ? def.egg().secondary() : baseEgg == null ? 0x808080 : baseEgg.getColor(1);
            ResourceLocation key = def.key();
            helper.register(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_spawn_egg"), new DeferredSpawnEggItem(() -> TYPES.get(key), primary, secondary, new Item.Properties()));
        }
    }

    @SuppressWarnings("unchecked") public static void attributes(EntityAttributeCreationEvent event) {
        for (EntityType<Mob> type : TYPES.values()) {
            EntityType<?> base = BASES.get(type);
            if (base == null || !DefaultAttributes.hasSupplier(base)) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which has no attributes to copy", BY_TYPE.get(type).key(), base);
                continue;
            }
            event.put(type, DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) base));
        }
    }

    public static void extraAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<Mob> type : TYPES.values()) {
            EntityVariantDef def = BY_TYPE.get(type);
            for (String name : def.attributes().keySet()) {
                Holder<Attribute> attribute = ContentAttributes.find(name, def.key());
                if (attribute != null && !event.has(type, attribute)) { event.add(type, attribute); }
            }
        }
    }

    public static void placements(RegisterSpawnPlacementsEvent event) {
        for (EntityType<Mob> type : TYPES.values()) {
            EntityVariantDef def = BY_TYPE.get(type);
            EntityType<?> base = BASES.get(type);
            boolean free = def.flags().ignoresSpawnRules();
            event.register(type, SpawnPlacements.getPlacementType(base), SpawnPlacements.getHeightmapType(base), (kind, level, reason, pos, random) -> free || rules(base, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }

    @SuppressWarnings("unchecked") private static <T extends Entity> Entity make(EntityType.EntityFactory<T> factory, EntityType<?> type, Level level) { return factory.create((EntityType<T>) type, level); }

    private static <T extends Entity> boolean rules(EntityType<T> base, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) { return SpawnPlacements.checkSpawnRules(base, level, reason, pos, random); }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.texture().isEmpty()) { return null; }
        return TEXTURES.computeIfAbsent(def.texture(), ResourceLocation::tryParse);
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def == null || !(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Mob mob)) { return; }
        if (swapped(level, mob, def)) {
            event.setCanceled(true);
            return;
        }
        dress(mob, def);
    }

    private static boolean swapped(ServerLevel level, Mob was, EntityVariantDef def) {
        if (def.becomes().isEmpty() || SWAPPING.get() == Boolean.TRUE || was.getPersistentData().getBoolean(DRESSED)) { return false; }
        String chosen = PickDef.pick(def.becomes(), level.getRandom());
        if (chosen == null || chosen.equals(def.key().toString())) { return false; }
        EntityType<?> wanted = EntityType.byString(chosen).orElse(null);
        if (wanted == null) {
            ContentLog.LOGGER.error("Entity variant {} can become {}, which nothing registers, so it stays as it is", def.key(), chosen);
            return false;
        }
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = wanted.create(level);
            if (becomes == null) { return false; }
            becomes.moveTo(was.getX(), was.getY(), was.getZ(), was.getYRot(), was.getXRot());
            if (becomes instanceof Mob mob) { EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(becomes.blockPosition()), MobSpawnType.EVENT, null); }
            level.addFreshEntity(becomes);
        }
        finally { SWAPPING.set(Boolean.FALSE); }
        return true;
    }

    private static void dress(Mob mob, EntityVariantDef def) {
        CompoundTag data = mob.getPersistentData();
        boolean first = !data.getBoolean(DRESSED);
        data.putBoolean(DRESSED, true);
        if (first) {
            if (!def.name().isEmpty() && !mob.hasCustomName()) {
                mob.setCustomName(Component.literal(def.name()));
                mob.setCustomNameVisible(def.showName());
            }
            if (def.absorption() > 0.0F) { mob.setAbsorptionAmount(def.absorption()); }
            effects(mob, def);
            gear(mob, def);
            if (def.baby() > 0.0F && rolledYoung(mob, def)) { child(mob); }
            if (mob instanceof Villager villager && !def.profession().isEmpty()) { profession(villager, def); }
        }
        if (def.flags().silent()) { mob.setSilent(true); }
        if (def.flags().glowing()) { mob.setGlowingTag(true); }
        if (def.flags().invisible()) { mob.setInvisible(true); }
        if (def.flags().invulnerable()) { mob.setInvulnerable(true); }
        attributes(mob, def, first);
        if (def.flags().persistent() || !def.despawns()) { mob.setPersistenceRequired(); }
        if (def.flags().noAI()) { mob.setNoAi(true); }
        if (def.flags().leftHanded()) { mob.setLeftHanded(true); }
        mob.setCanPickUpLoot(def.flags().picksUpLoot());
        priorities(mob, def);
        AttributeInstance size = mob.getAttribute(Attributes.SCALE);
        if (size != null && def.scale() != 1.0F) { size.setBaseValue(def.scale()); }
        navigation(mob, def);
        behavior(mob, def);
        ContentTasks.apply(mob, def);
    }

    private static void attributes(Mob mob, EntityVariantDef def, boolean first) {
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

    private static void effects(Mob mob, EntityVariantDef def) {
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

    private static ItemStack carrying(EntityVariantDef def) {
        String named = def.equipment().get("mainhand");
        Item item = named == null ? null : ContentStacks.item(ResourceLocation.tryParse(named));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
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
        if (def.career() > 0) { ContentLog.LOGGER.debug("Entity variant {} names career {}, which this line has no use for", def.key(), def.career()); }
    }

    private static void priorities(Mob mob, EntityVariantDef def) {
        for (Map.Entry<String, Float> entry : def.pathPriorities().entrySet()) {
            PathType type = null;
            for (PathType value : PathType.values()) {
                if (value.name().equalsIgnoreCase(entry.getKey())) { type = value; }
            }
            if (type == null) {
                ContentLog.LOGGER.error("Entity variant {} names path type '{}', which is not one of {}", def.key(), entry.getKey(), java.util.Arrays.toString(PathType.values()));
                continue;
            }
            mob.setPathfindingMalus(type, entry.getValue());
        }
    }

    private static void navigation(Mob mob, EntityVariantDef def) {
        IMob inner = (IMob) mob;
        if (def.physics().swims()) {
            inner.rdpl$setNavigation(new WaterBoundPathNavigation(mob, mob.level()));
            inner.rdpl$setMoveControl(new SmoothSwimmingMoveControl(mob, 85, 10, 0.1F, 0.5F, false));
            mob.setPathfindingMalus(PathType.WATER, 0.0F);
        }
        else if (def.physics().amphibious()) {
            inner.rdpl$setNavigation(new AmphibiousPathNavigation(mob, mob.level()));
            mob.setPathfindingMalus(PathType.WATER, 0.0F);
        }
        if (def.combat().swoops()) {
            inner.rdpl$setNavigation(new FlyingPathNavigation(mob, mob.level()));
            inner.rdpl$setMoveControl(new FlyingMoveControl(mob, 20, true));
        }
        if (def.physics().breathesUnderwater() || def.physics().swims()) { ContentTasks.drop(mob.goalSelector, FloatGoal.class); }
    }

    private static void behavior(Mob mob, EntityVariantDef def) {
        settled(mob, def);
        if (def.passive()) {
            for (WrappedGoal wrapped : new ArrayList<>(mob.targetSelector.getAvailableGoals())) { mob.targetSelector.removeGoal(wrapped.getGoal()); }
            ContentTasks.drop(mob.goalSelector, MeleeAttackGoal.class);
            mob.setTarget(null);
            return;
        }
        if (!def.hostile()) {
            if (def.combat().any() || def.combat().explodes()) { ContentLog.LOGGER.error("Entity variant {} asks for a fighting behavior, but is not hostile, so it never takes a target to use it on", def.key()); }
            return;
        }
        if (!(mob instanceof PathfinderMob creature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behavior needs", def.key(), def.base());
            return;
        }
        for (WrappedGoal wrapped : new ArrayList<>(mob.goalSelector.getAvailableGoals())) {
            if (wrapped.getGoal() instanceof AvoidEntityGoal || wrapped.getGoal() instanceof PanicGoal || tame(wrapped)) { mob.goalSelector.removeGoal(wrapped.getGoal()); }
        }
        boolean already = false;
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof MeleeAttackGoal) {
                already = true;
                break;
            }
        }
        if (!already) { mob.goalSelector.addGoal(2, new MeleeAttackGoal(creature, 1.2D, false)); }
        EntityVariantDef.Combat combat = def.combat();
        if (combat.explodes()) { mob.goalSelector.addGoal(0, new KamikazeGoal(creature, combat.explosionPower(), combat.explosionFuse(), combat.explosionFire())); }
        if (combat.throwsItems()) { mob.goalSelector.addGoal(0, new ThrowerGoal(creature, carrying(def), combat.explosionFuse(), combat.throwReload() > 0 ? combat.throwReload() : combat.explosionFuse(), combat.throwRetreat() > 0 ? combat.throwRetreat() : combat.explosionFuse(), combat.throwAmmo(), combat.throwPower(), combat.throwArc(), mob.getAttributeValue(Attributes.FOLLOW_RANGE))); }
        if (combat.charges()) { mob.goalSelector.addGoal(1, new ChargeGoal(creature, 2.0D)); }
        if (combat.pounces()) { mob.goalSelector.addGoal(1, new PounceGoal(creature)); }
        if (combat.fleesWhenHurt() > 0.0F) { mob.goalSelector.addGoal(0, new FleeWhenHurtGoal(creature, combat.fleesWhenHurt(), 1.4D)); }
        if (combat.sniffs() > 0) { mob.goalSelector.addGoal(3, new SniffGoal(creature, combat.sniffs())); }
        if (combat.gusts()) { mob.goalSelector.addGoal(1, new GustGoal(creature, combat.gustPower())); }
        if (combat.swoops()) { mob.goalSelector.addGoal(1, new SwoopGoal(creature)); }
        if (combat.patrols()) { mob.goalSelector.addGoal(4, new PatrolGoal(creature)); }
        mob.targetSelector.addGoal(1, new HurtByTargetGoal(creature).setAlertOthers());
        int priority = 2;
        for (String name : def.targets().isEmpty() ? PLAYER_ONLY : def.targets()) {
            Class<? extends LivingEntity> type = ContentTasks.living(name, def.key());
            if (type != null) { mob.targetSelector.addGoal(priority++, new NearestAttackableTargetGoal<>(creature, type, true)); }
        }
    }

    private static boolean tame(WrappedGoal wrapped) {
        return wrapped.getGoal() instanceof BreedGoal || wrapped.getGoal() instanceof TemptGoal || wrapped.getGoal() instanceof FollowParentGoal || wrapped.getGoal() instanceof FollowMobGoal
                || wrapped.getGoal() instanceof FollowOwnerGoal || wrapped.getGoal() instanceof SitWhenOrderedToGoal || wrapped.getGoal() instanceof LandOnOwnersShoulderGoal;
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
            creature.restrictTo(new BlockPos(kept.getInt(HOME_X), kept.getInt(HOME_Y), kept.getInt(HOME_Z)), combat.home());
            mob.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(creature, 1.0D));
        }
        if (combat.sleepsByDay()) { mob.goalSelector.addGoal(1, new SleepByDayGoal(creature)); }
    }

    public static void tick(LivingEntity living) {
        if (!(living instanceof Mob mob) || living.level().isClientSide) { return; }
        EntityVariantDef def = BY_TYPE.get(mob.getType());
        if (def == null) { return; }
        if (def.despawnTicks() > 0 && timeIsUp(mob, def)) {
            mob.discard();
            return;
        }
        if (def.hostile() && !(mob instanceof Monster) && mob.level().getDifficulty() == Difficulty.PEACEFUL) {
            mob.discard();
            return;
        }
        if (def.baby() > 0.0F && mob.getPersistentData().getBoolean(YOUNG) && mob instanceof AgeableMob ageable && ageable.getAge() >= 0) { ageable.setAge(-24000); }
        if (def.keepsSize()) { return; }
        boolean angry = stillRoused(mob);
        if (angry != mob.isSprinting()) { mob.setSprinting(angry); }
        AttributeInstance size = mob.getAttribute(Attributes.SCALE);
        if (size != null) {
            double wanted = angry ? def.angryScale() : def.scale();
            if (size.getBaseValue() != wanted) { size.setBaseValue(wanted); }
        }
    }

    private static boolean stillRoused(Mob mob) {
        long now = mob.level().getGameTime();
        CompoundTag held = mob.getPersistentData();
        if (mob.getTarget() != null) {
            if (held.getLong(CALM) + CALM_STEP < now + ROUSED) { held.putLong(CALM, now + ROUSED); }
            return true;
        }
        return held.getLong(CALM) > now;
    }

    private static boolean timeIsUp(Mob mob, EntityVariantDef def) {
        CompoundTag held = mob.getPersistentData();
        long now = mob.level().getGameTime();
        if (!held.contains(BORN)) {
            held.putLong(BORN, now);
            return false;
        }
        return now - held.getLong(BORN) >= def.despawnTicks();
    }

    public static final int AMBIENT = 0;
    public static final int HURT = 1;
    public static final int DEATH = 2;
    private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();
    private static final Map<String, List<String>> IMMUNITIES = Map.ofEntries(
            Map.entry("fall", List.of("minecraft:fall")), Map.entry("drown", List.of("minecraft:drown")),
            Map.entry("explosion", List.of("minecraft:explosion", "minecraft:player_explosion")), Map.entry("magic", List.of("minecraft:magic", "minecraft:indirect_magic")),
            Map.entry("cactus", List.of("minecraft:cactus")), Map.entry("lava", List.of("minecraft:lava")), Map.entry("wither", List.of("minecraft:wither", "minecraft:wither_skull")),
            Map.entry("starve", List.of("minecraft:starve")), Map.entry("anvil", List.of("minecraft:falling_anvil")), Map.entry("inwall", List.of("minecraft:in_wall")),
            Map.entry("fire", List.of("minecraft:in_fire", "minecraft:on_fire")), Map.entry("infire", List.of("minecraft:in_fire")), Map.entry("onfire", List.of("minecraft:on_fire")),
            Map.entry("fireball", List.of("minecraft:fireball", "minecraft:unattributed_fireball")), Map.entry("lightning", List.of("minecraft:lightning_bolt")),
            Map.entry("lightningbolt", List.of("minecraft:lightning_bolt")), Map.entry("cramming", List.of("minecraft:cramming")), Map.entry("freeze", List.of("minecraft:freeze")),
            Map.entry("dryout", List.of("minecraft:dry_out")), Map.entry("sweetberry", List.of("minecraft:sweet_berry_bush")), Map.entry("hotfloor", List.of("minecraft:hot_floor")),
            Map.entry("fallingblock", List.of("minecraft:falling_block")), Map.entry("outofworld", List.of("minecraft:out_of_world")), Map.entry("void", List.of("minecraft:out_of_world")),
            Map.entry("dragonbreath", List.of("minecraft:dragon_breath")), Map.entry("thorns", List.of("minecraft:thorns")), Map.entry("arrow", List.of("minecraft:arrow")),
            Map.entry("generic", List.of("minecraft:generic")));

    @Nullable public static SoundEvent sound(Entity entity, int which) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null) { return null; }
        String name = which == AMBIENT ? def.sounds().ambient() : which == HURT ? def.sounds().hurt() : def.sounds().death();
        if (name.isEmpty()) { return null; }
        if (SOUNDS.containsKey(name)) { return SOUNDS.get(name); }
        SoundEvent event = Registered.find(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryParse(name));
        if (event == null) { ContentLog.LOGGER.error("Entity variant {} names sound {}, which nothing registers", def.key(), name); }
        SOUNDS.put(name, event);
        return event;
    }

    public static int tint(Entity entity, String part) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.tint() == 0 || !def.tintParts().contains(part)) { return 0; }
        return def.tint();
    }

    public static boolean steerable(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.flags().steerable();
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Mob mob) || !steerable(mob)) { return; }
        Player player = event.getEntity();
        if (!event.getItemStack().isEmpty() || player.isSecondaryUseActive() || mob.isVehicle() || player.isPassenger()) { return; }
        if (!event.getLevel().isClientSide()) { player.startRiding(mob); }
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        event.setCanceled(true);
    }

    public static boolean hidesArmor(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.flags().hideArmor();
    }

    public static boolean hidesHeld(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.flags().hideHeld();
    }

    public static void onFall(LivingFallEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def != null && def.physics().fallDamage() != 1.0F) { event.setDamageMultiplier(event.getDamageMultiplier() * def.physics().fallDamage()); }
    }

    public static void onExperience(LivingExperienceDropEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def != null && def.experience() >= 0) { event.setDroppedExperience(def.experience()); }
    }

    public static void onBreathe(LivingBreatheEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def != null && (def.physics().breathesUnderwater() || def.physics().swims() || def.physics().amphibious())) { event.setCanBreathe(true); }
    }

    public static void onHurt(LivingIncomingDamageEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def == null || def.immuneTo().isEmpty()) { return; }
        String type = event.getSource().typeHolder().unwrapKey().map(key -> key.location().toString()).orElse("");
        for (String wanted : def.immuneTo()) {
            List<String> ids = IMMUNITIES.getOrDefault(wanted.replace("_", ""), List.of(wanted.contains(":") ? wanted : "minecraft:" + wanted));
            if (ids.contains(type)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void creatureTags(EntityVariantDef def) {
        List<String> tags = switch (def.creatureAttribute()) {
            case "" , "undefined" -> List.of();
            case "undead" -> List.of("minecraft:undead", "minecraft:sensitive_to_smite", "minecraft:ignores_poison_and_regen", "minecraft:inverted_healing_and_harm", "minecraft:wither_friends");
            case "arthropod" -> List.of("minecraft:arthropod", "minecraft:sensitive_to_bane_of_arthropods");
            case "illager" -> List.of("minecraft:illager", "minecraft:illager_friends");
            case "water" -> List.of("minecraft:aquatic", "minecraft:sensitive_to_impaling", "minecraft:can_breathe_under_water");
            default -> {
                ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod, illager or water", def.key(), def.creatureAttribute());
                yield List.of();
            }
        };
        for (String tag : tags) { TAGS.computeIfAbsent(tag, k -> new java.util.LinkedHashSet<>()).add(def.key().toString()); }
    }

    private static final Map<String, java.util.Set<String>> TAGS = new LinkedHashMap<>();

    private static void writeTags() {
        for (Map.Entry<String, java.util.Set<String>> entry : TAGS.entrySet()) {
            ResourceLocation tag = ResourceLocation.tryParse(entry.getKey());
            if (tag == null) { continue; }
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            for (String id : entry.getValue()) { values.add(id); }
            json.add("values", values);
            GeneratedResources.put(PackType.SERVER_DATA, tag.getNamespace(), ContentFormats.ENTITY_TYPE_TAGS + "/" + tag.getPath() + ".json", json.toString());
        }
        TAGS.clear();
    }

    public static void generate() {
        MODIFIERS = 0;
        int eggs = 0;
        for (EntityVariantDef def : DEFS.values()) {
            if (!def.spawns().isEmpty()) { spawnModifiers(def); }
            creatureTags(def);
            if (eggModel(def)) { eggs++; }
        }
        writeTags();
        if (MODIFIERS > 0) { Summary.info("entities.spawns", "Generated " + MODIFIERS + " spawn modifier(s) for entity variants"); }
        if (eggs > 0) { Summary.info("entities.eggs", "Generated " + eggs + " spawn egg model(s) that the packs did not ship themselves"); }
    }

    private static boolean eggModel(EntityVariantDef def) {
        if (!def.egg().wanted()) { return false; }
        String namespace = def.key().getNamespace();
        String path = "models/item/" + def.key().getPath() + "_spawn_egg.json";
        if (PackManager.get().provides(PackType.CLIENT_RESOURCES, namespace, path)) { return false; }
        GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, path, SPAWN_EGG_MODEL);
        return true;
    }

    private static int MODIFIERS;

    private static void spawnModifiers(EntityVariantDef def) {
        JsonArray spawners = new JsonArray();
        for (SpawnEntryDef entry : def.spawns()) {
            JsonObject spawner = new JsonObject();
            spawner.addProperty("type", def.key().toString());
            spawner.addProperty("weight", entry.weight());
            spawner.addProperty("minCount", entry.min());
            spawner.addProperty("maxCount", entry.max());
            spawners.add(spawner);
        }
        List<JsonElement> targets = new ArrayList<>();
        if (!def.biomes().isEmpty()) {
            JsonArray biomes = new JsonArray();
            for (String biome : def.biomes()) { biomes.add(biome); }
            targets.add(biomes);
        }
        for (String type : def.biomeTypes()) {
            String tag = ContentFormats.biomeTag(type);
            if (tag == null) { ContentLog.LOGGER.error("Entity variant {} names biome type '{}', which no biome tag on this line answers to", def.key(), type); }
            else { targets.add(new JsonPrimitive("#" + tag)); }
        }
        if (targets.isEmpty()) { targets.add(new JsonPrimitive("#minecraft:is_overworld")); }
        int index = 0;
        for (JsonElement target : targets) {
            JsonObject modifier = new JsonObject();
            modifier.addProperty("type", ContentFormats.ADD_SPAWNS);
            modifier.add("biomes", target);
            modifier.add("spawners", spawners.deepCopy());
            MODIFIERS++;
            GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), ContentFormats.BIOME_MODIFIERS + "/" + def.key().getPath() + "_spawns" + (index == 0 ? "" : "_" + index) + ".json", modifier.toString());
            index++;
        }
    }
}
