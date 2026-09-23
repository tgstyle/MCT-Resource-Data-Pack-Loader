package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ILivingEntity;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraft.server.TickTask;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
    static final Map<ResourceLocation, EntityVariantDef> DEFS = new LinkedHashMap<>();
    static final Map<EntityType<?>, EntityVariantDef> BY_TYPE = new IdentityHashMap<>();
    static final Map<EntityType<?>, EntityType<?>> BASES = new IdentityHashMap<>();
    static final Map<ResourceLocation, EntityType<?>> TYPES = new LinkedHashMap<>();
    @Nullable static EntityType<ReturningThrow> returningThrow;
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final String BORN = "rdplBorn";
    private static final String CALM = "rdplCalmAt";
    private static final String CRIED = "rdplCried";
    private static final String SIZED = "rdplSized";
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

    public static boolean undefined(ResourceLocation id) { return !DEFS.containsKey(id); }

    public static Map<ResourceLocation, EntityType<?>> types() { return Collections.unmodifiableMap(TYPES); }

    @Nullable public static EntityVariantDef def(Entity entity) { return BY_TYPE.get(entity.getType()); }

    @Nullable public static EntityVariantDef def(EntityType<?> type) { return BY_TYPE.get(type); }

    @Nullable public static EntityType<?> base(EntityType<?> variant) { return BASES.get(variant); }

    @Nullable public static EntityType<ReturningThrow> returningThrow() { return returningThrow; }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null || def.texture().isEmpty()) { return null; }
        return TEXTURES.computeIfAbsent(def.texture(), ResourceLocation::tryParse);
    }

    static final Map<String, Integer> FELL_TO = new LinkedHashMap<>();

    public static void onDeathWatch(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (!ContentLog.LOGGER.debugEnabled() || BY_TYPE.get(event.getEntity().getType()) == null) { return; }
        String by = event.getSource().getMsgId();
        FELL_TO.merge(by, 1, Integer::sum);
    }

    @Nullable private static LivingEntity struck;
    private static long struckAt;
    @Nullable private static EntityVariantDef struckBy;

    public static boolean lyingAsleep(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.combat().sleepsByDay() && entity.isShiftKeyDown();
    }

    public static boolean walks(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.physics().walks();
    }

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

    public static boolean staysPut(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && !def.physics().teleports();
    }

    @Nullable public static Boolean climbs(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def == null ? null : def.physics().climbs();
    }

    public static boolean sinks(Entity entity) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        return def != null && def.physics().breathesUnderwater() && !def.physics().swims();
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

    public static void onDespawnCheck(MobSpawnEvent.AllowDespawn event) {
        EntityVariantDef def = def(event.getEntity());
        if (def != null && !def.despawns()) { event.setResult(Event.Result.DENY); }
    }

    public static void onLeave(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Mob mob) || mob.getRemovalReason() == null || !mob.getRemovalReason().shouldDestroy()) { return; }
        level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> {
            release(mob.goalSelector);
            release(mob.targetSelector);
        }));
        mob.setTarget(null);
        mob.setLastHurtByMob(null);
        ((ILivingEntity) mob).rdpl$setLastHurtMob(null);
        ((ILivingEntity) mob).rdpl$setLastDamageSource(null);
        mob.getCombatTracker().recheckStatus();
    }

    private static void release(GoalSelector selector) {
        for (WrappedGoal wrapped : selector.getAvailableGoals()) {
            if (wrapped.isRunning()) { wrapped.stop(); }
        }
        selector.removeAllGoals(goal -> true);
    }

    public static void onShot(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk() || !(event.getEntity() instanceof AbstractArrow arrow) || !(arrow.getOwner() instanceof LivingEntity shooter)) { return; }
        EntityVariantDef def = BY_TYPE.get(shooter.getType());
        if (def == null || !ContentEntityBehavior.declaresAttackDamage(def)) { return; }
        AttributeInstance damage = shooter.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage == null) { return; }
        arrow.setBaseDamage(arrow.getBaseDamage() - 2.0D + damage.getValue());
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        EntityVariantDef def = BY_TYPE.get(event.getEntity().getType());
        if (def == null || !(event.getLevel() instanceof ServerLevel level)) { return; }
        if (swapped(level, event.getEntity(), def)) {
            event.setCanceled(true);
            return;
        }
        ContentEntityApply.dress(event.getEntity(), def);
    }

    private static boolean swapped(ServerLevel level, Entity was, EntityVariantDef def) {
        if (def.becomes().isEmpty() || SWAPPING.get() == Boolean.TRUE || was.getPersistentData().getBoolean(ContentEntityApply.DRESSED)) { return false; }
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

    public static void tick(LivingEntity living) {
        if (!(living instanceof Mob mob)) { return; }
        EntityVariantDef def = BY_TYPE.get(mob.getType());
        if (def == null) { return; }
        if (mob.level().isClientSide) {
            if (!def.keepsSize()) { resizeSeen(mob); }
            return;
        }
        if (def.flags().collectsExperience() && mob.isAlive()) {
            ContentMobExperience.collect(mob);
            ContentAnvils.gather(mob);
        }
        if (def.despawnTicks() > 0 && timeIsUp(mob, def)) {
            mob.discard();
            return;
        }
        if (def.physics().amphibious()) { ContentEntityApply.amphibious(mob); }
        if (def.baby() > 0.0F && mob.getPersistentData().getBoolean(ContentEntityApply.YOUNG) && mob instanceof AgeableMob ageable && ageable.getAge() >= 0) { ageable.setAge(-24000); }
        boolean angry = stillRoused(mob);
        CompoundTag heard = mob.getPersistentData();
        if (angry != heard.getBoolean(CRIED)) {
            if (angry) { cry(mob); }
            heard.putBoolean(CRIED, angry);
        }
        if (def.neverSprints()) { return; }
        if (angry != mob.isSprinting()) {
            mob.setSprinting(angry);
            mob.refreshDimensions();
        }
    }

    private static void resizeSeen(Mob mob) {
        CompoundTag seen = mob.getPersistentData();
        if (seen.getBoolean(SIZED) == mob.isSprinting()) { return; }
        seen.putBoolean(SIZED, mob.isSprinting());
        mob.refreshDimensions();
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
    public static final int THROW = 5;
    private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();
    private static final Map<String, List<String>> IMMUNITIES = Map.ofEntries(
            Map.entry("fall", List.of("minecraft:fall")), Map.entry("drown", List.of("minecraft:drown")), Map.entry("explosion", List.of("minecraft:explosion", "minecraft:bad_respawn_point")),
            Map.entry("explosion.player", List.of("minecraft:player_explosion")), Map.entry("magic", List.of("minecraft:magic")), Map.entry("indirectmagic", List.of("minecraft:indirect_magic")),
            Map.entry("cactus", List.of("minecraft:cactus")), Map.entry("lava", List.of("minecraft:lava")), Map.entry("wither", List.of("minecraft:wither")),
            Map.entry("starve", List.of("minecraft:starve")), Map.entry("anvil", List.of("minecraft:falling_anvil")), Map.entry("inwall", List.of("minecraft:in_wall", "minecraft:outside_border")),
            Map.entry("fire", List.of("minecraft:in_fire", "minecraft:on_fire")), Map.entry("infire", List.of("minecraft:in_fire")), Map.entry("onfire", List.of("minecraft:on_fire", "minecraft:unattributed_fireball")),
            Map.entry("fireball", List.of("minecraft:fireball")), Map.entry("lightning", List.of("minecraft:lightning_bolt")), Map.entry("lightningbolt", List.of("minecraft:lightning_bolt")),
            Map.entry("cramming", List.of("minecraft:cramming")), Map.entry("freeze", List.of("minecraft:freeze")), Map.entry("dryout", List.of("minecraft:dry_out")),
            Map.entry("sweetberry", List.of("minecraft:sweet_berry_bush")), Map.entry("hotfloor", List.of("minecraft:hot_floor")), Map.entry("fallingblock", List.of("minecraft:falling_block")),
            Map.entry("outofworld", List.of("minecraft:out_of_world", "minecraft:generic_kill")), Map.entry("void", List.of("minecraft:out_of_world")), Map.entry("dragonbreath", List.of("minecraft:dragon_breath")),
            Map.entry("thorns", List.of("minecraft:thorns")), Map.entry("arrow", List.of("minecraft:arrow")), Map.entry("thrown", List.of("minecraft:thrown")),
            Map.entry("flyintowall", List.of("minecraft:fly_into_wall")), Map.entry("fireworks", List.of("minecraft:fireworks")), Map.entry("mob", List.of("minecraft:mob_attack", "minecraft:mob_attack_no_aggro", "minecraft:mob_projectile", "minecraft:wither_skull")),
            Map.entry("player", List.of("minecraft:player_attack")), Map.entry("generic", List.of("minecraft:generic")));

    @Nullable public static SoundEvent sound(Entity entity, int which) {
        EntityVariantDef def = BY_TYPE.get(entity.getType());
        if (def == null) { return null; }
        String name = switch (which) {
            case HURT -> def.sounds().hurt();
            case DEATH -> def.sounds().death();
            case TARGET -> def.sounds().target();
            case EXPLODE -> def.sounds().explode();
            case THROW -> def.sounds().throwSound();
            default -> def.sounds().ambient();
        };
        if (name.isEmpty()) { return null; }
        if (SOUNDS.containsKey(name)) { return SOUNDS.get(name); }
        SoundEvent event = ContentSounds.find(name);
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

    private static final int ENGAGE_EVERY = 100;
    private static int engageWatch;

    public static void onEngagement(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BY_TYPE.isEmpty() || !ContentLog.LOGGER.debugEnabled()) { return; }
        if (++engageWatch % ENGAGE_EVERY != 0) { return; }
        ContentEntityWatch.tally(event.getServer());
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
}
