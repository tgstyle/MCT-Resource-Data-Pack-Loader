package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraft.server.MinecraftServer;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.item.PrimedTnt;
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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private static final String CRIED = "rdplCried";
    private static final String ROLLED = "rdplBabyRolled";
    private static final String YOUNG = "rdplBabyYoung";
    private static final int ROUSED = 60;
    private static final long CALM_STEP = 10L;
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final ThreadLocal<Boolean> SPAWNER = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final Set<ResourceLocation> WARNED = new HashSet<>();

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
        EntityType<?> base = def == null ? null : Registered.find(ForgeRegistries.ENTITY_TYPES, def.base());
        return base == null ? "monster" : base.getCategory().getName();
    }

    public static Map<ResourceLocation, EntityType<Mob>> types() { return Collections.unmodifiableMap(TYPES); }

    @Nullable public static EntityVariantDef def(Entity entity) { return BY_TYPE.get(entity.getType()); }

    @Nullable public static EntityVariantDef def(EntityType<?> type) { return BY_TYPE.get(type); }

    @Nullable public static EntityType<?> base(EntityType<?> variant) { return BASES.get(variant); }

    public static void registerTypes(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        int made = 0;
        for (EntityVariantDef def : DEFS.values()) {
            EntityType<?> base = Registered.find(ForgeRegistries.ENTITY_TYPES, def.base());
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
            float width = def.width() > 0.0F ? def.width() : dims.width;
            float height = def.height() > 0.0F ? def.height() : dims.height;
            EntityType.Builder<Mob> builder = EntityType.Builder.of(factory, def.hostile() ? MobCategory.MONSTER : base.getCategory())
                    .sized(width * def.scale(), height * def.scale())
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
            EntityType<?> base = Registered.find(ForgeRegistries.ENTITY_TYPES, def.base());
            if (base == null) { continue; }
            SpawnEggItem baseEgg = ForgeSpawnEggItem.fromEntityType(base);
            int primary = def.egg().primary() >= 0 ? def.egg().primary() : baseEgg == null ? 0xFFFFFF : baseEgg.getColor(0);
            int secondary = def.egg().secondary() >= 0 ? def.egg().secondary() : baseEgg == null ? 0x808080 : baseEgg.getColor(1);
            ResourceLocation key = def.key();
            helper.register(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_spawn_egg"), new ForgeSpawnEggItem(() -> TYPES.get(key), primary, secondary, new Item.Properties()));
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
                Attribute attribute = ContentAttributes.find(name, def.key());
                if (attribute != null && !event.has(type, attribute)) { event.add(type, attribute); }
            }
        }
    }

    public static void placements(SpawnPlacementRegisterEvent event) {
        for (EntityType<Mob> type : TYPES.values()) {
            EntityVariantDef def = BY_TYPE.get(type);
            EntityType<?> base = BASES.get(type);
            boolean free = def.flags().ignoresSpawnRules();
            event.register(type, SpawnPlacements.getPlacementType(base), SpawnPlacements.getHeightmapType(base), (kind, level, reason, pos, random) -> free || rules(base, level, reason, pos, random), SpawnPlacementRegisterEvent.Operation.REPLACE);
        }
    }

    @SuppressWarnings("unchecked") private static <T extends Entity> Entity make(EntityType.EntityFactory<T> factory, EntityType<?> type, Level level) { return factory.create((EntityType<T>) type, level); }

    private static <T extends Entity> boolean rules(EntityType<T> base, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) { return SpawnPlacements.checkSpawnRules(base, level, reason, pos, random); }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.texture().isEmpty()) { return null; }
        return TEXTURES.computeIfAbsent(def.texture(), ResourceLocation::tryParse);
    }

    private static final Map<String, Integer> FELL_TO = new LinkedHashMap<>();

    public static void onDeathWatch(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (!ContentLog.LOGGER.debugEnabled() || BY_TYPE.get(event.getEntity().getType()) == null) { return; }
        String by = event.getSource().getMsgId();
        FELL_TO.merge(by, 1, Integer::sum);
    }

    @Nullable private static LivingEntity struck;
    private static long struckAt;
    @Nullable private static EntityVariantDef struckBy;

    public static boolean bright(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.flags().bright();
    }

    public static void fromSpawner(boolean adding) { SPAWNER.set(adding); }

    private static List<PickDef> kin(EntityVariantDef def) {
        List<PickDef> kept = new ArrayList<>();
        List<TeamDef> sides = ContentTeams.claiming(def.key().toString());
        for (PickDef choice : def.becomes()) {
            ResourceLocation id = ResourceLocation.tryParse(choice.name());
            EntityVariantDef other = id == null ? null : DEFS.get(id);
            if (other == null || !other.base().equals(def.base())) { continue; }
            List<TeamDef> theirs = ContentTeams.claiming(other.key().toString());
            if (sides.isEmpty() ? theirs.isEmpty() : sides.stream().anyMatch(theirs::contains)) { kept.add(choice); }
        }
        return kept;
    }

    public static boolean collectsExperience(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.flags().collectsExperience();
    }

    public static boolean teleports(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def == null || def.physics().teleports();
    }

    public static float baseBabyChance(Entity entity, float chance) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def == null || def.flags().keepsBaseBaby() ? chance : 0.0F;
    }

    public static int hurtResistance(Entity entity, int vanilla) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def == null || def.physics().hurtResistance() < 0 ? vanilla : def.physics().hurtResistance();
    }

    public static boolean struckFireless(Entity entity) {
        return struckBy != null && !struckBy.combat().hitFire() && entity == struck && entity.level().getGameTime() == struckAt;
    }

    public static void onStruck(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        Entity by = event.getSource().getEntity();
        if (by == null || event.getEntity().level().isClientSide()) { return; }
        EntityVariantDef def = BY_TYPE.get(by.getType());
        if (def == null) { return; }
        struck = event.getEntity();
        struckAt = struck.level().getGameTime();
        struckBy = def;
    }

    public static void onKnockBack(net.minecraftforge.event.entity.living.LivingKnockBackEvent event) {
        if (struckBy == null || event.getEntity() != struck || event.getEntity().level().getGameTime() != struckAt) { return; }
        if (struckBy.combat().knockback() >= 0.0F) { event.setStrength(struckBy.combat().knockback()); }
    }

    public static void onEffect(MobEffectEvent.Applicable event) {
        if (struckBy != null && !struckBy.combat().hitEffects() && event.getEntity() == struck && event.getEntity().level().getGameTime() == struckAt) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            return;
        }
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def == null || def.ignoresEffects().isEmpty()) { return; }
        ResourceLocation named = ForgeRegistries.MOB_EFFECTS.getKey(event.getEffectInstance().getEffect());
        String id = named == null ? "" : named.toString();
        if (def.effects().containsKey(id)) { return; }
        for (String ignored : def.ignoresEffects()) {
            if (ignored.equalsIgnoreCase("all") || ignored.equalsIgnoreCase(id)) {
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                return;
            }
        }
    }

    public static boolean fairGame(LivingEntity target) {
        return !target.isInvulnerableTo(target.level().damageSources().generic());
    }

    public static double attackReachSqr(LivingEntity attacker, LivingEntity target, double vanilla) {
        EntityVariantDef def = BY_TYPE.get(attacker.getType());
        if (def == null || def.combat().attackReach() <= 0.0F) { return vanilla; }
        return (double) def.combat().attackReach() * def.combat().attackReach() + target.getBbWidth();
    }

    private static final Set<ResourceLocation> PACED = new LinkedHashSet<>();

    public static int attackInterval(LivingEntity attacker) {
        AttributeInstance speed = attacker.getAttribute(Attributes.ATTACK_SPEED);
        if (speed == null || speed.getValue() <= 0.0D) { return 20; }
        int every = Math.max(1, (int) Math.round(20.0D / speed.getValue()));
        EntityVariantDef def = BY_TYPE.get(attacker.getType());
        if (def != null && PACED.add(def.key())) { ContentLog.LOGGER.debug("Entity variant {} strikes {} time(s) a second, so its blows are paced every {} tick(s) in place of the game's 20", def.key(), speed.getValue(), every); }
        return every;
    }

    public static float scale(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null) { return 1.0F; }
        return entity.isSprinting() ? def.angryScale() : def.scale();
    }

    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        EntityVariantDef def = def(event.getEntity());
        if (def == null || !def.flags().ignoresSpawnRules() || event.getResult() != Event.Result.DEFAULT) { return; }
        event.setResult(event.getEntity().checkSpawnObstruction(event.getLevel()) ? Event.Result.ALLOW : Event.Result.DENY);
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
        List<PickDef> choices = SPAWNER.get() == Boolean.TRUE ? kin(def) : def.becomes();
        String chosen = PickDef.pick(choices, level.getRandom());
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
            if (becomes instanceof Mob mob) { ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(becomes.blockPosition()), MobSpawnType.EVENT, null, null); }
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
        if (def.physics().stepHeight() >= 0.0F) { mob.setMaxUpStep(def.physics().stepHeight()); }
        if (def.physics().climbs() != null) { climber(mob, def.physics().climbs()); }

        navigation(mob, def);
        behavior(mob, def);
        ContentTasks.apply(mob, def);
    }

    private static void attributes(Mob mob, EntityVariantDef def, boolean first) {
        for (Map.Entry<String, Double> entry : def.attributes().entrySet()) {
            Attribute attribute = ContentAttributes.find(entry.getKey(), def.key());
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
            MobEffect effect = Registered.find(ForgeRegistries.MOB_EFFECTS, name);
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
        VillagerProfession found = Registered.find(ForgeRegistries.VILLAGER_PROFESSIONS, key);
        if (found == null) {
            ContentLog.LOGGER.error("Entity variant {} names profession {}, which nothing registers", def.key(), def.profession());
            return;
        }
        villager.setVillagerData(villager.getVillagerData().setProfession(found));
        if (def.career() > 0) { ContentLog.LOGGER.debug("Entity variant {} names career {}, which this line has no use for", def.key(), def.career()); }
    }

    private static void priorities(Mob mob, EntityVariantDef def) {
        for (Map.Entry<String, Float> entry : def.pathPriorities().entrySet()) {
            BlockPathTypes type = null;
            for (BlockPathTypes value : BlockPathTypes.values()) {
                if (value.name().equalsIgnoreCase(entry.getKey())) { type = value; }
            }
            if (type == null) {
                ContentLog.LOGGER.error("Entity variant {} names path type '{}', which is not one of {}", def.key(), entry.getKey(), java.util.Arrays.toString(BlockPathTypes.values()));
                continue;
            }
            mob.setPathfindingMalus(type, entry.getValue());
        }
    }

    private static void climber(Mob mob, boolean climbs) {
        IMob inner = (IMob) mob;
        if (climbs && !(mob.getNavigation() instanceof WallClimberNavigation)) { inner.rdpl$setNavigation(new WallClimberNavigation(mob, mob.level())); }
        else if (!climbs && mob.getNavigation() instanceof WallClimberNavigation) { inner.rdpl$setNavigation(new GroundPathNavigation(mob, mob.level())); }
    }

    private static void navigation(Mob mob, EntityVariantDef def) {
        IMob inner = (IMob) mob;
        if (def.physics().swims()) {
            inner.rdpl$setNavigation(new WaterBoundPathNavigation(mob, mob.level()));
            inner.rdpl$setMoveControl(new SmoothSwimmingMoveControl(mob, 85, 10, 0.1F, 0.5F, false));
            mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }
        else if (def.physics().amphibious()) {
            inner.rdpl$setNavigation(new AmphibiousPathNavigation(mob, mob.level()));
            mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
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
            if (def.combat().any() || def.combat().explodes() || def.combat().digs()) { ContentLog.LOGGER.error("Entity variant {} asks for a fighting behavior, but is not hostile, so it never takes a target to use it on", def.key()); }
            return;
        }
        if (!(mob instanceof PathfinderMob creature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behavior needs", def.key(), def.base());
            return;
        }
        for (WrappedGoal wrapped : new ArrayList<>(mob.goalSelector.getAvailableGoals())) {
            if (wrapped.getGoal() instanceof AvoidEntityGoal || wrapped.getGoal() instanceof PanicGoal || tame(wrapped)) { mob.goalSelector.removeGoal(wrapped.getGoal()); }
        }
        if (ownStrike(mob, def)) {
            ContentTasks.drop(mob.goalSelector, MeleeAttackGoal.class);
            mob.goalSelector.addGoal(2, new StrikeGoal(creature, 1.2D, false));
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
        EntityVariantDef.Combat combat = def.combat();
        if (combat.explodes()) { mob.goalSelector.addGoal(0, new KamikazeGoal(creature, combat.explosionPower(), combat.explosionFuse(), combat.explosionFire())); }
        if (combat.throwsItems()) { mob.goalSelector.addGoal(0, new ThrowerGoal(creature, carrying(def), combat.explosionFuse(), combat.throwReload() > 0 ? combat.throwReload() : combat.explosionFuse(), combat.throwRetreat() > 0 ? combat.throwRetreat() : combat.explosionFuse(), combat.throwAmmo(), combat.throwPower(), combat.throwArc(), mob.getAttributeValue(Attributes.FOLLOW_RANGE))); }
        if (combat.charges()) { mob.goalSelector.addGoal(1, new ChargeGoal(creature, 2.0D)); }
        if (combat.pounces()) { mob.goalSelector.addGoal(1, new PounceGoal(creature)); }
        if (combat.fleesWhenHurt() > 0.0F) { mob.goalSelector.addGoal(0, new FleeWhenHurtGoal(creature, combat.fleesWhenHurt(), 1.4D)); }
        if (combat.sniffs() > 0) { mob.goalSelector.addGoal(3, new SniffGoal(creature, combat.sniffs())); }
        if (combat.gusts()) { mob.goalSelector.addGoal(1, new GustGoal(creature, combat.gustPower())); }
        if (combat.swoops()) { mob.goalSelector.addGoal(1, new SwoopGoal(creature)); }
        if (combat.digs()) { mob.goalSelector.addGoal(1, new DigGoal(creature)); }
        if (combat.patrols()) { mob.goalSelector.addGoal(4, new PatrolGoal(creature)); }
        mob.targetSelector.addGoal(1, new HurtByTargetGoal(creature).setAlertOthers());
        int priority = 2;
        for (String name : def.targets().isEmpty() ? PLAYER_ONLY : def.targets()) {
            Class<? extends LivingEntity> type = ContentTasks.living(name, def.key(), mob.level());
            if (type == null) { continue; }
            EntityType<?> variant = EntityType.byString(name).filter(BY_TYPE::containsKey).orElse(null);
            mob.targetSelector.addGoal(priority++, new NearestAttackableTargetGoal<>(creature, type, 10, true, false, variant == null ? ContentEntities::fairGame : found -> found.getType() == variant && fairGame(found)));
        }
    }

    private static boolean ownStrike(Mob mob, EntityVariantDef def) {
        if (mob instanceof Monster) { return false; }
        for (String name : def.attributes().keySet()) {
            if (ContentAttributes.find(name, def.key()) == Attributes.ATTACK_DAMAGE) { return true; }
        }
        return false;
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
        if (def.flags().collectsExperience() && mob.isAlive()) { ContentMobExperience.collect(mob); }
        if (def.despawnTicks() > 0 && timeIsUp(mob, def)) {
            mob.discard();
            return;
        }
        if (def.hostile() && !(mob instanceof Monster) && mob.level().getDifficulty() == Difficulty.PEACEFUL) {
            mob.discard();
            return;
        }
        if (def.baby() > 0.0F && mob.getPersistentData().getBoolean(YOUNG) && mob instanceof AgeableMob ageable && ageable.getAge() >= 0) { ageable.setAge(-24000); }
        boolean angry = stillRoused(mob);
        CompoundTag heard = mob.getPersistentData();
        if (angry != heard.getBoolean(CRIED)) {
            if (angry) { cry(mob); }
            heard.putBoolean(CRIED, angry);
        }
        if (def.keepsSize()) { return; }
        if (angry != mob.isSprinting()) {
            mob.setSprinting(angry);
            mob.refreshDimensions();
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
    public static final int TARGET = 3;
    public static final int EXPLODE = 4;
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
        String name = switch (which) {
            case HURT -> def.sounds().hurt();
            case DEATH -> def.sounds().death();
            case TARGET -> def.sounds().target();
            case EXPLODE -> def.sounds().explode();
            default -> def.sounds().ambient();
        };
        if (name.isEmpty()) { return null; }
        if (SOUNDS.containsKey(name)) { return SOUNDS.get(name); }
        SoundEvent event = Registered.find(ForgeRegistries.SOUND_EVENTS, ResourceLocation.tryParse(name));
        if (event == null) { ContentLog.LOGGER.error("Entity variant {} names sound {}, which nothing registers", def.key(), name); }
        SOUNDS.put(name, event);
        return event;
    }

    private static void cry(Mob mob) {
        SoundEvent cry = sound(mob, TARGET);
        if (cry == null) { return; }
        EntityVariantDef def = BY_TYPE.get(mob.getType());
        float carries = (float) Math.max(1.0D, mob.getAttributeValue(Attributes.FOLLOW_RANGE) / 16.0D);
        float varies = def == null ? 0.0F : def.sounds().targetVaries();
        float pitch = varies <= 0.0F ? 1.0F : (float) Math.pow(2.0D, (mob.getRandom().nextFloat() * 2.0F - 1.0F) * varies / 12.0D);
        mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(), cry, mob.getSoundSource(), carries, pitch);
    }

    @Nullable public static SoundEvent explodeSound(@Nullable Entity exploder) {
        if (exploder instanceof PrimedTnt tnt) { exploder = tnt.getOwner(); }
        return exploder == null ? null : sound(exploder, EXPLODE);
    }

    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide()) { return; }
        SoundEvent own = explodeSound(event.getExplosion().getDirectSourceEntity());
        if (own == null) { return; }
        event.getLevel().playSound(null, event.getExplosion().getPosition().x, event.getExplosion().getPosition().y, event.getExplosion().getPosition().z, own, SoundSource.BLOCKS, 4.0F, 1.0F);
    }

    public static int tint(Entity entity, String part) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.tint() == 0 || !def.tintParts().contains(part)) { return 0; }
        return def.tint();
    }


    private static final Long2LongOpenHashMap WAS_AT = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap TICKED = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap GAPS = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap FACED = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap HEALTHS = new Long2LongOpenHashMap();
    private static final int ENGAGE_EVERY = 100;
    private static int engageWatch;

    public static void onEngagement(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BY_TYPE.isEmpty() || !ContentLog.LOGGER.debugEnabled()) { return; }
        if (++engageWatch % ENGAGE_EVERY != 0) { return; }
        tally(event.getServer());
    }

    private static void tally(@Nullable MinecraftServer server) {
        if (server == null) { return; }
        for (ServerLevel level : server.getAllLevels()) {
            StringBuilder where = new StringBuilder();
            for (net.minecraft.server.level.ServerPlayer player : level.players()) { where.append(' ').append(player.getGameProfile().getName()).append('@').append(player.chunkPosition()); }
            ContentLog.LOGGER.debug("{} holds {} chunk(s) at entity-ticking status, spawn chunk {}, players:{}", level.dimension().location(), level.getChunkSource().getTickingGenerated(), new ChunkPos(level.getSharedSpawnPos()), where.isEmpty() ? " none" : where);
        }
        int mobs = 0;
        int aimed = 0;
        int pathless = 0;
        int reaching = 0;
        int slowed = 0;
        int still = 0;
        int walked = 0;
        int ticking = 0;
        int closing = 0;
        int milling = 0;
        int inRange = 0;
        int inTickingChunk = 0;
        int spun = 0;
        int hurt = 0;
        java.util.List<Mob> here = new java.util.ArrayList<>();
        it.unimi.dsi.fastutil.longs.LongOpenHashSet seen = new it.unimi.dsi.fastutil.longs.LongOpenHashSet();
        long away = 0L;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Mob mob) || !BY_TYPE.containsKey(mob.getType())) { continue; }
                mobs++;
                seen.add(mob.getId());
                here.add(mob);
                long hp = HEALTHS.put(mob.getId(), (long) (mob.getHealth() * 100.0F));
                if (hp != HEALTHS.defaultReturnValue() && hp > (long) (mob.getHealth() * 100.0F)) { hurt++; }
                long was = WAS_AT.put(mob.getId(), mob.blockPosition().asLong());
                if (was != WAS_AT.defaultReturnValue() && was != mob.blockPosition().asLong()) { walked++; }
                long faced = FACED.put(mob.getId(), (long) mob.getYRot());
                if (was != WAS_AT.defaultReturnValue() && was == mob.blockPosition().asLong() && faced != FACED.defaultReturnValue() && Math.abs(net.minecraft.util.Mth.wrapDegrees(mob.getYRot() - faced)) >= 90.0F) { spun++; }
                long ticked = TICKED.put(mob.getId(), mob.tickCount);
                if (ticked != TICKED.defaultReturnValue() && ticked != mob.tickCount) { ticking++; }
                if (level.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(mob.chunkPosition().toLong())) { inRange++; }
                LevelChunk stood = level.getChunkSource().getChunkNow(mob.chunkPosition().x, mob.chunkPosition().z);
                if (stood != null && stood.getFullStatus().isOrAfter(FullChunkStatus.ENTITY_TICKING)) { inTickingChunk++; }
                LivingEntity aim = mob.getTarget();
                if (aim == null) { continue; }
                aimed++;
                double gap = Math.sqrt(mob.distanceToSqr(aim));
                away += (long) gap;
                long before = GAPS.put(mob.getId(), (long) (gap * 100.0D));
                if (before != GAPS.defaultReturnValue()) {
                    if (before - (long) (gap * 100.0D) > 100L) { closing++; }
                    else if (Math.abs(before - (long) (gap * 100.0D)) <= 100L) { milling++; }
                }
                if (mob.getNavigation().isDone()) { pathless++; }
                if (ContentEntityTicks.thinksSlower(mob)) { slowed++; }
                if (mob.isNoAi()) { still++; }
                if (gap <= mob.getBbWidth() * 2.0F + aim.getBbWidth()) { reaching++; }
            }
        }
        int gone = 0;
        for (it.unimi.dsi.fastutil.longs.LongIterator held = WAS_AT.keySet().iterator(); held.hasNext();) {
            long id = held.nextLong();
            if (seen.contains(id)) { continue; }
            held.remove();
            FACED.remove(id);
            HEALTHS.remove(id);
            TICKED.remove(id);
            GAPS.remove(id);
            gone++;
        }
        int thickest = 0;
        double spread = 0.0D;
        if (!here.isEmpty()) {
            double middleX = 0.0D;
            double middleZ = 0.0D;
            for (Mob one : here) { middleX += one.getX(); middleZ += one.getZ(); }
            middleX /= here.size();
            middleZ /= here.size();
            for (Mob one : here) { spread += Math.sqrt((one.getX() - middleX) * (one.getX() - middleX) + (one.getZ() - middleZ) * (one.getZ() - middleZ)); }
            spread /= here.size();
            for (Mob one : here) {
                int near = 0;
                for (Mob other : here) { if (one.distanceToSqr(other) <= 64.0D) { near++; } }
                thickest = Math.max(thickest, near);
            }
        }
        if (!here.isEmpty()) {
            double leastX = Double.MAX_VALUE;
            double mostX = -Double.MAX_VALUE;
            double leastZ = Double.MAX_VALUE;
            double mostZ = -Double.MAX_VALUE;
            for (Mob one : here) {
                leastX = Math.min(leastX, one.getX());
                mostX = Math.max(mostX, one.getX());
                leastZ = Math.min(leastZ, one.getZ());
                mostZ = Math.max(mostZ, one.getZ());
            }
            int[][] cells = new int[11][11];
            double wideX = Math.max(1.0D, mostX - leastX);
            double wideZ = Math.max(1.0D, mostZ - leastZ);
            for (Mob one : here) {
                int col = Math.min(10, (int) ((one.getX() - leastX) / wideX * 11.0D));
                int row = Math.min(10, (int) ((one.getZ() - leastZ) / wideZ * 11.0D));
                cells[row][col]++;
            }
            StringBuilder drawn = new StringBuilder();
            for (int[] row : cells) {
                if (!drawn.isEmpty()) { drawn.append('/'); }
                for (int count : row) { drawn.append(count == 0 ? "." : count > 9 ? "+" : Character.forDigit(count, 10)); }
            }
            ContentLog.LOGGER.debug("Where they stand, {} mob(s) over x {} to {} and z {} to {}, eleven cells each way: {}",
                    here.size(), (int) leastX, (int) mostX, (int) leastZ, (int) mostZ, drawn);
        }
        if (!FELL_TO.isEmpty()) {
            ContentLog.LOGGER.debug("What has killed pack mobs so far: {}", FELL_TO);
        }
        if (mobs == 0) { return; }
        ContentLog.LOGGER.debug("Of {} pack mob(s), {} hold a target, {} of those have no path to walk to it, {} stand close enough to strike, and a target is {} block(s) off on average; {} of {} still ticking moved since the last look, {} closed on their target and {} held the same distance, {} stand in entity-ticking range and {} in a chunk at entity-ticking status, {} are gone since the last look and {} turned 90 degrees or more without leaving their block, {} lost health since the last look, the thickest crowd holds {} within 8 blocks and a mob stands {} block(s) from the middle of them all on average, {} were given a slower pace this tick and {} have no AI at all",
                mobs, aimed, pathless, reaching, aimed == 0 ? 0L : away / aimed, walked, ticking, closing, milling, inRange, inTickingChunk, gone, spun, hurt, thickest, String.format(java.util.Locale.ROOT, "%.1f", spread), slowed, still);
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

    public static float angryFactor(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.keepsSize() || !entity.isSprinting()) { return 1.0F; }
        return def.angryScale() / def.scale();
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

    public static void onHurt(LivingAttackEvent event) {
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

    @Nullable public static MobType mobType(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.creatureAttribute().isEmpty()) { return null; }
        return switch (def.creatureAttribute()) {
            case "undefined" -> MobType.UNDEFINED;
            case "undead" -> MobType.UNDEAD;
            case "arthropod" -> MobType.ARTHROPOD;
            case "illager" -> MobType.ILLAGER;
            case "water" -> MobType.WATER;
            default -> {
                if (WARNED.add(def.key())) { ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod, illager or water", def.key(), def.creatureAttribute()); }
                yield null;
            }
        };
    }

    public static void generate() {
        MODIFIERS = 0;
        if (Config.contentOff()) { return; }
        int eggs = 0;
        for (EntityVariantDef def : DEFS.values()) {
            if (!def.spawns().isEmpty()) { spawnModifiers(def); }
            if (eggModel(def)) { eggs++; }
        }
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
