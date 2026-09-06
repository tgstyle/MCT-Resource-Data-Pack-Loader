package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.TaskDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentTasks {
    private static final Map<String, Kind> KINDS = new LinkedHashMap<>();
    private static final String WALKER = "a walking creature";
    private static final String TAME = "a tameable base, like a wolf, cat or parrot";
    private static final String ANY_NAME = "any living entity";

    private ContentTasks() {}

    @FunctionalInterface private interface Maker { @Nullable Goal make(Mob mob, TaskDef task, EntityVariantDef def); }

    private record Kind(String name, Class<?> needs, String needsName, boolean targeting, @Nullable Class<? extends Goal> type, Maker maker) {}

    private static void task(String name, Class<?> needs, String needsName, Class<? extends Goal> type, Maker maker) { KINDS.put(name.toLowerCase(Locale.ROOT), new Kind(name, needs, needsName, false, type, maker)); }

    private static void target(String name, Class<?> needs, String needsName, Class<? extends Goal> type, Maker maker) { KINDS.put(name.toLowerCase(Locale.ROOT), new Kind(name, needs, needsName, true, type, maker)); }

    private static void gone(String name, String why) {
        KINDS.put(name.toLowerCase(Locale.ROOT), new Kind(name, Mob.class, ANY_NAME, false, null, (m, t, d) -> {
            ContentLog.LOGGER.error("Entity variant {} asks for the task {}, which this line does not have: {}", d.key(), name, why);
            return null;
        }));
    }

    static {
        task("attackMelee", PathfinderMob.class, WALKER, MeleeAttackGoal.class, (m, t, d) -> new MeleeAttackGoal((PathfinderMob) m, t.speed(1.0D), t.memory()));
        task("attackRanged", RangedAttackMob.class, "a base that shoots, like a skeleton, witch, snow golem or blaze", RangedAttackGoal.class, (m, t, d) -> new RangedAttackGoal((RangedAttackMob) m, t.speed(1.0D), t.cooldown(20), t.distance(15.0F)));
        task("attackRangedBow", Monster.class, "a monster that shoots, like a skeleton", RangedBowAttackGoal.class, ContentTasks::bow);
        task("avoidEntity", PathfinderMob.class, WALKER, AvoidEntityGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> avoided = entity(t, d);
            return avoided == null ? null : new AvoidEntityGoal<>((PathfinderMob) m, avoided, t.distance(6.0F), t.speed(1.0D), t.nearSpeed(1.2D));
        });
        task("beg", Wolf.class, "a wolf", BegGoal.class, (m, t, d) -> new BegGoal((Wolf) m, t.distance(8.0F)));
        task("breakDoor", Mob.class, ANY_NAME, BreakDoorGoal.class, (m, t, d) -> new BreakDoorGoal(m, difficulty -> difficulty == Difficulty.HARD));
        task("creeperSwell", Creeper.class, "a creeper", SwellGoal.class, (m, t, d) -> new SwellGoal((Creeper) m));
        target("defendVillage", IronGolem.class, "an iron golem", DefendVillageTargetGoal.class, (m, t, d) -> new DefendVillageTargetGoal((IronGolem) m));
        task("eatGrass", Mob.class, ANY_NAME, EatBlockGoal.class, (m, t, d) -> new EatBlockGoal(m));
        target("findEntityNearest", Mob.class, ANY_NAME, NearestAttackableTargetGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> sought = entity(t, d);
            return sought == null ? null : new NearestAttackableTargetGoal<>(m, sought, true);
        });
        target("findEntityNearestPlayer", Mob.class, ANY_NAME, NearestAttackableTargetGoal.class, (m, t, d) -> new NearestAttackableTargetGoal<>(m, Player.class, true));
        task("fleeSun", PathfinderMob.class, WALKER, FleeSunGoal.class, (m, t, d) -> new FleeSunGoal((PathfinderMob) m, t.speed(1.0D)));
        task("follow", Mob.class, ANY_NAME, FollowMobGoal.class, (m, t, d) -> new FollowMobGoal(m, t.speed(1.0D), t.near(3.0F), t.distance(7.0F)));
        task("followOwner", TamableAnimal.class, TAME, FollowOwnerGoal.class, (m, t, d) -> new FollowOwnerGoal((TamableAnimal) m, t.speed(1.0D), t.near(10.0F), t.distance(2.0F), false));
        task("followOwnerFlying", TamableAnimal.class, TAME, FollowOwnerGoal.class, (m, t, d) -> new FollowOwnerGoal((TamableAnimal) m, t.speed(1.0D), t.near(5.0F), t.distance(1.0F), true));
        task("followParent", Animal.class, "an animal", FollowParentGoal.class, (m, t, d) -> new FollowParentGoal((Animal) m, t.speed(1.1D)));
        target("hurtByTarget", PathfinderMob.class, WALKER, HurtByTargetGoal.class, (m, t, d) -> {
            HurtByTargetGoal goal = new HurtByTargetGoal((PathfinderMob) m);
            return t.help() ? goal.setAlertOthers() : goal;
        });
        task("landOnOwnersShoulder", ShoulderRidingEntity.class, "a parrot", LandOnOwnersShoulderGoal.class, (m, t, d) -> new LandOnOwnersShoulderGoal((ShoulderRidingEntity) m));
        task("leapAtTarget", Mob.class, ANY_NAME, LeapAtTargetGoal.class, (m, t, d) -> new LeapAtTargetGoal(m, t.leap(0.4F)));
        task("llamaFollowCaravan", Llama.class, "a llama", LlamaFollowCaravanGoal.class, (m, t, d) -> new LlamaFollowCaravanGoal((Llama) m, t.speed(2.1D)));
        task("lookAtTradePlayer", AbstractVillager.class, "a villager", LookAtTradingPlayerGoal.class, (m, t, d) -> new LookAtTradingPlayerGoal((AbstractVillager) m));
        task("lookAtVillager", Mob.class, ANY_NAME, LookAtPlayerGoal.class, (m, t, d) -> new LookAtPlayerGoal(m, Villager.class, t.distance(6.0F)));
        task("lookIdle", Mob.class, ANY_NAME, RandomLookAroundGoal.class, (m, t, d) -> new RandomLookAroundGoal(m));
        task("mate", Animal.class, "an animal", BreedGoal.class, (m, t, d) -> {
            if (t.entity().isEmpty()) { return new BreedGoal((Animal) m, t.speed(1.0D)); }
            Class<? extends LivingEntity> partner = entity(t, d);
            if (partner == null) { return null; }
            if (!Animal.class.isAssignableFrom(partner)) {
                ContentLog.LOGGER.error("Entity variant {} asks to mate with '{}', which is not an animal", d.key(), t.entity());
                return null;
            }
            return new BreedGoal((Animal) m, t.speed(1.0D), partner.asSubclass(Animal.class));
        });
        task("moveThroughVillage", PathfinderMob.class, WALKER, MoveThroughVillageGoal.class, (m, t, d) -> new MoveThroughVillageGoal((PathfinderMob) m, t.speed(1.0D), t.nocturnal(), 4, () -> false));
        task("moveTowardsRestriction", PathfinderMob.class, WALKER, MoveTowardsRestrictionGoal.class, (m, t, d) -> new MoveTowardsRestrictionGoal((PathfinderMob) m, t.speed(1.0D)));
        task("moveTowardsTarget", PathfinderMob.class, WALKER, MoveTowardsTargetGoal.class, (m, t, d) -> new MoveTowardsTargetGoal((PathfinderMob) m, t.speed(0.9D), t.distance(32.0F)));
        target("nearestAttackableTarget", Mob.class, ANY_NAME, NearestAttackableTargetGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> sought = entity(t, d);
            return sought == null ? null : new NearestAttackableTargetGoal<>(m, sought, t.sight(), t.nearby());
        });
        task("ocelotAttack", Mob.class, ANY_NAME, OcelotAttackGoal.class, (m, t, d) -> new OcelotAttackGoal(m));
        task("openDoor", Mob.class, ANY_NAME, OpenDoorGoal.class, (m, t, d) -> new OpenDoorGoal(m, t.close()));
        target("ownerHurtByTarget", TamableAnimal.class, TAME, OwnerHurtByTargetGoal.class, (m, t, d) -> new OwnerHurtByTargetGoal((TamableAnimal) m));
        target("ownerHurtTarget", TamableAnimal.class, TAME, OwnerHurtTargetGoal.class, (m, t, d) -> new OwnerHurtTargetGoal((TamableAnimal) m));
        task("panic", PathfinderMob.class, WALKER, PanicGoal.class, (m, t, d) -> new PanicGoal((PathfinderMob) m, t.speed(1.4D)));
        task("restrictSun", PathfinderMob.class, WALKER, RestrictSunGoal.class, (m, t, d) -> new RestrictSunGoal((PathfinderMob) m));
        task("runAroundLikeCrazy", AbstractHorse.class, "a horse, donkey, mule or llama", RunAroundLikeCrazyGoal.class, (m, t, d) -> new RunAroundLikeCrazyGoal((AbstractHorse) m, t.speed(1.2D)));
        task("sit", TamableAnimal.class, TAME, SitWhenOrderedToGoal.class, (m, t, d) -> new SitWhenOrderedToGoal((TamableAnimal) m));
        task("skeletonRiders", SkeletonHorse.class, "a skeleton horse", SkeletonTrapGoal.class, (m, t, d) -> new SkeletonTrapGoal((SkeletonHorse) m));
        task("swimming", Mob.class, ANY_NAME, FloatGoal.class, (m, t, d) -> new FloatGoal(m));
        target("targetNonTamed", TamableAnimal.class, TAME, NonTameRandomTargetGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> sought = entity(t, d);
            return sought == null ? null : new NonTameRandomTargetGoal<>((TamableAnimal) m, sought, t.sight(), null);
        });
        task("tempt", PathfinderMob.class, WALKER, TemptGoal.class, (m, t, d) -> {
            Set<Item> wanted = items(t, d);
            return wanted == null ? null : new TemptGoal((PathfinderMob) m, t.speed(1.2D), Ingredient.of(wanted.toArray(Item[]::new)), t.scared());
        });
        task("tradePlayer", AbstractVillager.class, "a villager", TradeWithPlayerGoal.class, (m, t, d) -> new TradeWithPlayerGoal((AbstractVillager) m));
        task("wander", PathfinderMob.class, WALKER, RandomStrollGoal.class, (m, t, d) -> t.chance() == null ? new RandomStrollGoal((PathfinderMob) m, t.speed(1.0D)) : new RandomStrollGoal((PathfinderMob) m, t.speed(1.0D), Math.max(1, Math.round(t.chance()))));
        task("wanderAvoidWater", PathfinderMob.class, WALKER, WaterAvoidingRandomStrollGoal.class, (m, t, d) -> t.chance() == null ? new WaterAvoidingRandomStrollGoal((PathfinderMob) m, t.speed(1.0D)) : new WaterAvoidingRandomStrollGoal((PathfinderMob) m, t.speed(1.0D), t.chance()));
        task("wanderAvoidWaterFlying", PathfinderMob.class, WALKER, WaterAvoidingRandomFlyingGoal.class, (m, t, d) -> new WaterAvoidingRandomFlyingGoal((PathfinderMob) m, t.speed(1.0D)));
        task("watchClosest", Mob.class, ANY_NAME, LookAtPlayerGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> watched = t.entity().isEmpty() ? Player.class : entity(t, d);
            if (watched == null) { return null; }
            return t.chance() == null ? new LookAtPlayerGoal(m, watched, t.distance(8.0F)) : new LookAtPlayerGoal(m, watched, t.distance(8.0F), t.chance());
        });
        task("watchClosest2", Mob.class, ANY_NAME, LookAtPlayerGoal.class, (m, t, d) -> {
            Class<? extends LivingEntity> watched = t.entity().isEmpty() ? Player.class : entity(t, d);
            return watched == null ? null : new LookAtPlayerGoal(m, watched, t.distance(8.0F), t.chance() == null ? 0.02F : t.chance());
        });
        task("zombieAttack", Zombie.class, "a zombie", ZombieAttackGoal.class, (m, t, d) -> new ZombieAttackGoal((Zombie) m, t.speed(1.0D), t.memory()));
        String brains = "villagers think with brains now, not task lists";
        gone("followGolem", brains);
        gone("harvestFarmland", brains);
        gone("moveIndoors", brains);
        gone("play", brains);
        gone("restrictOpenDoor", brains);
        gone("villagerInteract", brains);
        gone("villagerMate", brains);
        gone("ocelotSit", "cats sit through their own behavior and ocelots no longer sit");
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) @Nullable private static Goal bow(Mob mob, TaskDef task, EntityVariantDef def) {
        if (!(mob instanceof Monster && mob instanceof RangedAttackMob)) {
            ContentLog.LOGGER.error("Entity variant {} asks for the task attackRangedBow, which needs a monster that shoots, like a skeleton; {} does not", def.key(), def.base());
            return null;
        }
        return new RangedBowAttackGoal((Monster) mob, task.speed(1.0D), task.cooldown(20), task.distance(15.0F));
    }

    public static String names() {
        List<String> names = new ArrayList<>();
        for (Kind kind : KINDS.values()) { if (kind.type() != null) { names.add(kind.name()); } }
        return String.join(", ", names);
    }

    public static List<TaskDef> parse(ResourceLocation key, JsonObject json) {
        if (!json.has("tasks")) { return List.of(); }
        List<TaskDef> tasks = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "tasks")) {
            if (element.isJsonPrimitive()) {
                String text = element.getAsString().trim();
                if (!text.startsWith("-")) {
                    ContentLog.LOGGER.error("Entity variant {} lists the task '{}' as a bare name; a task to add is an object with \"task\" and \"priority\", a task to drop is its name after a '-'", key, text);
                    continue;
                }
                Kind kind = known(key, text.substring(1));
                if (kind != null) { tasks.add(TaskDef.removal(kind.name())); }
                continue;
            }
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("Entity variant {} has a tasks entry that is neither an object nor a name, ignoring it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            Kind kind = known(key, GsonHelper.getAsString(entry, "task", ""));
            if (kind == null) { continue; }
            if (!entry.has("priority")) {
                ContentLog.LOGGER.error("Entity variant {} adds the task {} without a priority, so it is dropped; vanilla runs 0 first and its own tasks sit between 1 and 8", key, kind.name());
                continue;
            }
            tasks.add(new TaskDef(kind.name(), false, Math.max(0, GsonHelper.getAsInt(entry, "priority")),
                    number(entry, "speed"), number(entry, "nearSpeed"),
                    decimal(entry, "distance"), decimal(entry, "near"), decimal(entry, "chance"), decimal(entry, "leap"),
                    entry.has("cooldown") ? Math.max(1, GsonHelper.getAsInt(entry, "cooldown")) : null,
                    GsonHelper.getAsString(entry, "entity", "").trim(), Json.strings(entry, "items"),
                    GsonHelper.getAsBoolean(entry, "sight", true), GsonHelper.getAsBoolean(entry, "nearby", false), GsonHelper.getAsBoolean(entry, "help", false),
                    GsonHelper.getAsBoolean(entry, "memory", false), GsonHelper.getAsBoolean(entry, "close", false), GsonHelper.getAsBoolean(entry, "nocturnal", false),
                    GsonHelper.getAsBoolean(entry, "scared", false)));
        }
        return tasks;
    }

    @Nullable private static Kind known(ResourceLocation key, String name) {
        Kind kind = KINDS.get(name.trim().toLowerCase(Locale.ROOT));
        if (kind == null) { ContentLog.LOGGER.error("Entity variant {} names the task '{}', which is not one of {}", key, name, names()); }
        return kind;
    }

    @Nullable private static Double number(JsonObject json, String name) { return json.has(name) ? (double) GsonHelper.getAsFloat(json, name) : null; }

    @Nullable private static Float decimal(JsonObject json, String name) { return json.has(name) ? GsonHelper.getAsFloat(json, name) : null; }

    public static void apply(Mob mob, EntityVariantDef def) {
        if (def.tasks().isEmpty()) { return; }
        StringBuilder taken = new StringBuilder();
        for (TaskDef task : def.tasks()) {
            Kind kind = KINDS.get(task.name().toLowerCase(Locale.ROOT));
            if (kind == null) { continue; }
            if (task.remove()) {
                if (kind.type() != null) {
                    drop(mob.goalSelector, kind.type());
                    drop(mob.targetSelector, kind.type());
                }
                taken.append(" -").append(kind.name());
                continue;
            }
            if (!kind.needs().isInstance(mob)) {
                ContentLog.LOGGER.error("Entity variant {} asks for the task {}, which needs {}; {} is not one", def.key(), kind.name(), kind.needsName(), def.base());
                continue;
            }
            Goal made = kind.maker().make(mob, task, def);
            if (made == null) { continue; }
            (kind.targeting() ? mob.targetSelector : mob.goalSelector).addGoal(task.priority(), made);
            taken.append(' ').append(kind.name()).append('@').append(task.priority());
        }
        ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} takes its tasks:{}", def.key(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ(), taken);
    }

    public static void drop(GoalSelector selector, Class<? extends Goal> type) {
        for (WrappedGoal wrapped : new ArrayList<>(selector.getAvailableGoals())) {
            if (wrapped.getGoal().getClass() == type) { selector.removeGoal(wrapped.getGoal()); }
        }
    }

    @Nullable private static Class<? extends LivingEntity> entity(TaskDef task, EntityVariantDef def) {
        if (task.entity().isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} asks for the task {} without saying which entity, add \"entity\"", def.key(), task.name());
            return null;
        }
        return living(task.entity(), def.key());
    }

    @Nullable public static Class<? extends LivingEntity> living(String name, ResourceLocation owner) {
        ResourceLocation location = ResourceLocation.tryParse(name);
        if (location != null && "minecraft".equals(location.getNamespace()) && "player".equals(location.getPath())) { return Player.class; }
        EntityType<?> type = location == null ? null : EntityType.byString(location.toString()).orElse(null);
        Class<? extends Entity> found = type == null ? null : type.getBaseClass();
        if (found == null || !LivingEntity.class.isAssignableFrom(found)) {
            ContentLog.LOGGER.error("Entity variant {} names '{}', which is not a living entity that is registered", owner, name);
            return null;
        }
        return found.asSubclass(LivingEntity.class);
    }

    @Nullable private static Set<Item> items(TaskDef task, EntityVariantDef def) {
        if (task.items().isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} asks to be tempted without saying by what, add \"items\"", def.key());
            return null;
        }
        Set<Item> wanted = new HashSet<>();
        for (String name : task.items()) {
            Item item = ContentStacks.item(ResourceLocation.tryParse(name));
            if (item == null) { ContentLog.LOGGER.error("Entity variant {} would be tempted by '{}', which is not a registered item", def.key(), name); }
            else { wanted.add(item); }
        }
        return wanted.isEmpty() ? null : wanted;
    }
}
