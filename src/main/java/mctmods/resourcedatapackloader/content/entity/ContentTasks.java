package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.TaskDef;
import mctmods.resourcedatapackloader.util.AiTasks;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.common.base.Predicates;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILlamaFollowCaravan;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityShoulderRiding;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentTasks {
    private static final Map<String, Kind> KINDS = new LinkedHashMap<>();
    private static final Class<?> ANY = EntityLiving.class;
    private static final String ANY_NAME = "any living entity";
    private static final String WALKER = "a walking creature";
    private static final String VILLAGER = "a villager";
    private static final String TAMEABLE = "a tameable base, like a wolf, cat or parrot";
    private static final String GOLEM = "an iron golem";
    private static final String ANIMAL = "an animal";

    private ContentTasks() {}

    @FunctionalInterface private interface IMaker { @Nullable EntityAIBase make(EntityLiving living, TaskDef task, EntityVariantDef def); }

    private static final class Kind {
        final String name;
        final Class<?> needs;
        final String needsName;
        final boolean targeting;
        final Class<? extends EntityAIBase> type;
        final IMaker maker;

        Kind(String name, Class<?> needs, String needsName, boolean targeting, Class<? extends EntityAIBase> type, IMaker maker) {
            this.name = name;
            this.needs = needs;
            this.needsName = needsName;
            this.targeting = targeting;
            this.type = type;
            this.maker = maker;
        }
    }

    private static void register(boolean targeting, String name, Class<?> needs, String needsName, Class<? extends EntityAIBase> type, IMaker maker) { KINDS.put(name.toLowerCase(Locale.ROOT), new Kind(name, needs, needsName, targeting, type, maker)); }

    private static void task(String name, Class<?> needs, String needsName, Class<? extends EntityAIBase> type, IMaker maker) { register(false, name, needs, needsName, type, maker); }

    private static void target(String name, Class<?> needs, String needsName, Class<? extends EntityAIBase> type, IMaker maker) { register(true, name, needs, needsName, type, maker); }

    static {
        task("attackMelee", EntityCreature.class, WALKER, EntityAIAttackMelee.class, (l, t, d) -> new EntityAIAttackMelee((EntityCreature) l, t.speed(1.0D), t.memory));
        task("attackRanged", IRangedAttackMob.class, "a base that shoots, like a skeleton, witch, snow golem or blaze", EntityAIAttackRanged.class, (l, t, d) -> new EntityAIAttackRanged((IRangedAttackMob) l, t.speed(1.0D), t.cooldown(20), t.distance(15.0F)));
        task("attackRangedBow", EntityMob.class, "a monster that shoots, like a skeleton", EntityAIAttackRangedBow.class, ContentTasks::bow);
        task("avoidEntity", EntityCreature.class, WALKER, EntityAIAvoidEntity.class, (l, t, d) -> {
            Class<? extends Entity> avoided = entity(t, d, Entity.class);
            return avoided == null ? null : new EntityAIAvoidEntity<>((EntityCreature) l, avoided, t.distance(6.0F), t.speed(1.0D), t.nearSpeed(1.2D));
        });
        task("beg", EntityWolf.class, "a wolf", EntityAIBeg.class, (l, t, d) -> new EntityAIBeg((EntityWolf) l, t.distance(8.0F)));
        task("breakDoor", ANY, ANY_NAME, EntityAIBreakDoor.class, (l, t, d) -> new EntityAIBreakDoor(l));
        task("creeperSwell", EntityCreeper.class, "a creeper", EntityAICreeperSwell.class, (l, t, d) -> new EntityAICreeperSwell((EntityCreeper) l));
        target("defendVillage", EntityIronGolem.class, GOLEM, EntityAIDefendVillage.class, (l, t, d) -> new EntityAIDefendVillage((EntityIronGolem) l));
        task("eatGrass", ANY, ANY_NAME, EntityAIEatGrass.class, (l, t, d) -> new EntityAIEatGrass(l));
        target("findEntityNearest", ANY, ANY_NAME, EntityAIFindEntityNearest.class, (l, t, d) -> {
            Class<? extends EntityLivingBase> sought = entity(t, d, EntityLivingBase.class);
            return sought == null ? null : new EntityAIFindEntityNearest(l, sought);
        });
        target("findEntityNearestPlayer", ANY, ANY_NAME, EntityAIFindEntityNearestPlayer.class, (l, t, d) -> new EntityAIFindEntityNearestPlayer(l));
        task("fleeSun", EntityCreature.class, WALKER, EntityAIFleeSun.class, (l, t, d) -> new EntityAIFleeSun((EntityCreature) l, t.speed(1.0D)));
        task("follow", ANY, ANY_NAME, EntityAIFollow.class, (l, t, d) -> new EntityAIFollow(l, t.speed(1.0D), t.near(3.0F), t.distance(7.0F)));
        task("followGolem", EntityVillager.class, VILLAGER, EntityAIFollowGolem.class, (l, t, d) -> new EntityAIFollowGolem((EntityVillager) l));
        task("followOwner", EntityTameable.class, TAMEABLE, EntityAIFollowOwner.class, (l, t, d) -> new EntityAIFollowOwner((EntityTameable) l, t.speed(1.0D), t.near(10.0F), t.distance(2.0F)));
        task("followOwnerFlying", EntityTameable.class, TAMEABLE, EntityAIFollowOwnerFlying.class, (l, t, d) -> new EntityAIFollowOwnerFlying((EntityTameable) l, t.speed(1.0D), t.near(5.0F), t.distance(1.0F)));
        task("followParent", EntityAnimal.class, ANIMAL, EntityAIFollowParent.class, (l, t, d) -> new EntityAIFollowParent((EntityAnimal) l, t.speed(1.1D)));
        task("harvestFarmland", EntityVillager.class, VILLAGER, EntityAIHarvestFarmland.class, (l, t, d) -> new EntityAIHarvestFarmland((EntityVillager) l, t.speed(0.6D)));
        target("hurtByTarget", EntityCreature.class, WALKER, EntityAIHurtByTarget.class, (l, t, d) -> new EntityAIHurtByTarget((EntityCreature) l, t.help));
        task("landOnOwnersShoulder", EntityShoulderRiding.class, "a parrot", EntityAILandOnOwnersShoulder.class, (l, t, d) -> new EntityAILandOnOwnersShoulder((EntityShoulderRiding) l));
        task("leapAtTarget", ANY, ANY_NAME, EntityAILeapAtTarget.class, (l, t, d) -> new EntityAILeapAtTarget(l, t.leap(0.4F)));
        task("llamaFollowCaravan", EntityLlama.class, "a llama", EntityAILlamaFollowCaravan.class, (l, t, d) -> new EntityAILlamaFollowCaravan((EntityLlama) l, t.speed(2.1D)));
        task("lookAtTradePlayer", EntityVillager.class, VILLAGER, EntityAILookAtTradePlayer.class, (l, t, d) -> new EntityAILookAtTradePlayer((EntityVillager) l));
        task("lookAtVillager", EntityIronGolem.class, GOLEM, EntityAILookAtVillager.class, (l, t, d) -> new EntityAILookAtVillager((EntityIronGolem) l));
        task("lookIdle", ANY, ANY_NAME, EntityAILookIdle.class, (l, t, d) -> new EntityAILookIdle(l));
        task("mate", EntityAnimal.class, ANIMAL, EntityAIMate.class, (l, t, d) -> {
            if (t.entity.isEmpty()) { return new EntityAIMate((EntityAnimal) l, t.speed(1.0D)); }
            Class<? extends EntityAnimal> partner = entity(t, d, EntityAnimal.class);
            return partner == null ? null : new EntityAIMate((EntityAnimal) l, t.speed(1.0D), partner);
        });
        task("moveIndoors", EntityCreature.class, WALKER, EntityAIMoveIndoors.class, (l, t, d) -> new EntityAIMoveIndoors((EntityCreature) l));
        task("moveThroughVillage", EntityCreature.class, WALKER, EntityAIMoveThroughVillage.class, (l, t, d) -> new EntityAIMoveThroughVillage((EntityCreature) l, t.speed(1.0D), t.nocturnal));
        task("moveTowardsRestriction", EntityCreature.class, WALKER, EntityAIMoveTowardsRestriction.class, (l, t, d) -> new EntityAIMoveTowardsRestriction((EntityCreature) l, t.speed(1.0D)));
        task("moveTowardsTarget", EntityCreature.class, WALKER, EntityAIMoveTowardsTarget.class, (l, t, d) -> new EntityAIMoveTowardsTarget((EntityCreature) l, t.speed(0.9D), t.distance(32.0F)));
        target("nearestAttackableTarget", EntityCreature.class, WALKER, EntityAINearestAttackableTarget.class, (l, t, d) -> {
            Class<? extends EntityLivingBase> sought = entity(t, d, EntityLivingBase.class);
            return sought == null ? null : new EntityAINearestAttackableTarget<>((EntityCreature) l, sought, t.sight, t.nearby);
        });
        task("ocelotAttack", ANY, ANY_NAME, EntityAIOcelotAttack.class, (l, t, d) -> new EntityAIOcelotAttack(l));
        task("ocelotSit", EntityOcelot.class, "an ocelot", EntityAIOcelotSit.class, (l, t, d) -> new EntityAIOcelotSit((EntityOcelot) l, t.speed(0.8D)));
        task("openDoor", ANY, ANY_NAME, EntityAIOpenDoor.class, (l, t, d) -> new EntityAIOpenDoor(l, t.close));
        target("ownerHurtByTarget", EntityTameable.class, TAMEABLE, EntityAIOwnerHurtByTarget.class, (l, t, d) -> new EntityAIOwnerHurtByTarget((EntityTameable) l));
        target("ownerHurtTarget", EntityTameable.class, TAMEABLE, EntityAIOwnerHurtTarget.class, (l, t, d) -> new EntityAIOwnerHurtTarget((EntityTameable) l));
        task("panic", EntityCreature.class, WALKER, EntityAIPanic.class, (l, t, d) -> new EntityAIPanic((EntityCreature) l, t.speed(1.4D)));
        task("play", EntityVillager.class, VILLAGER, EntityAIPlay.class, (l, t, d) -> new EntityAIPlay((EntityVillager) l, t.speed(0.32D)));
        task("restrictOpenDoor", EntityCreature.class, WALKER, EntityAIRestrictOpenDoor.class, (l, t, d) -> new EntityAIRestrictOpenDoor((EntityCreature) l));
        task("restrictSun", EntityCreature.class, WALKER, EntityAIRestrictSun.class, (l, t, d) -> new EntityAIRestrictSun((EntityCreature) l));
        task("runAroundLikeCrazy", AbstractHorse.class, "a horse, donkey, mule or llama", EntityAIRunAroundLikeCrazy.class, (l, t, d) -> new EntityAIRunAroundLikeCrazy((AbstractHorse) l, t.speed(1.2D)));
        task("sit", EntityTameable.class, TAMEABLE, EntityAISit.class, (l, t, d) -> new EntityAISit((EntityTameable) l));
        task("skeletonRiders", EntitySkeletonHorse.class, "a skeleton horse", EntityAISkeletonRiders.class, (l, t, d) -> new EntityAISkeletonRiders((EntitySkeletonHorse) l));
        task("swimming", ANY, ANY_NAME, EntityAISwimming.class, (l, t, d) -> new EntityAISwimming(l));
        target("targetNonTamed", EntityTameable.class, TAMEABLE, EntityAITargetNonTamed.class, (l, t, d) -> {
            Class<? extends EntityLivingBase> sought = entity(t, d, EntityLivingBase.class);
            return sought == null ? null : new EntityAITargetNonTamed<>((EntityTameable) l, sought, t.sight, Predicates.alwaysTrue());
        });
        task("tempt", EntityCreature.class, WALKER, EntityAITempt.class, (l, t, d) -> {
            Set<Item> wanted = items(t, d);
            return wanted == null ? null : new EntityAITempt((EntityCreature) l, t.speed(1.2D), t.scared, wanted);
        });
        task("tradePlayer", EntityVillager.class, VILLAGER, EntityAITradePlayer.class, (l, t, d) -> new EntityAITradePlayer((EntityVillager) l));
        task("villagerInteract", EntityVillager.class, VILLAGER, EntityAIVillagerInteract.class, (l, t, d) -> new EntityAIVillagerInteract((EntityVillager) l));
        task("villagerMate", EntityVillager.class, VILLAGER, EntityAIVillagerMate.class, (l, t, d) -> new EntityAIVillagerMate((EntityVillager) l));
        task("wander", EntityCreature.class, WALKER, EntityAIWander.class, (l, t, d) -> t.chance == null ? new EntityAIWander((EntityCreature) l, t.speed(1.0D)) : new EntityAIWander((EntityCreature) l, t.speed(1.0D), Math.max(1, Math.round(t.chance))));
        task("wanderAvoidWater", EntityCreature.class, WALKER, EntityAIWanderAvoidWater.class, (l, t, d) -> t.chance == null ? new EntityAIWanderAvoidWater((EntityCreature) l, t.speed(1.0D)) : new EntityAIWanderAvoidWater((EntityCreature) l, t.speed(1.0D), t.chance));
        task("wanderAvoidWaterFlying", EntityCreature.class, WALKER, EntityAIWanderAvoidWaterFlying.class, (l, t, d) -> new EntityAIWanderAvoidWaterFlying((EntityCreature) l, t.speed(1.0D)));
        task("watchClosest", ANY, ANY_NAME, EntityAIWatchClosest.class, (l, t, d) -> {
            Class<? extends Entity> watched = t.entity.isEmpty() ? EntityPlayer.class : entity(t, d, Entity.class);
            if (watched == null) { return null; }
            return t.chance == null ? new EntityAIWatchClosest(l, watched, t.distance(8.0F)) : new EntityAIWatchClosest(l, watched, t.distance(8.0F), t.chance);
        });
        task("watchClosest2", ANY, ANY_NAME, EntityAIWatchClosest2.class, (l, t, d) -> {
            Class<? extends Entity> watched = t.entity.isEmpty() ? EntityPlayer.class : entity(t, d, Entity.class);
            return watched == null ? null : new EntityAIWatchClosest2(l, watched, t.distance(8.0F), t.chance == null ? 0.02F : t.chance);
        });
        task("zombieAttack", EntityZombie.class, "a zombie", EntityAIZombieAttack.class, (l, t, d) -> new EntityAIZombieAttack((EntityZombie) l, t.speed(1.0D), t.memory));
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) @Nullable private static EntityAIBase bow(EntityLiving living, TaskDef task, EntityVariantDef def) {
        if (!(living instanceof IRangedAttackMob)) {
            ContentLog.LOGGER.error("Entity variant {} asks for the task attackRangedBow, which needs a base that shoots, like a skeleton; {} does not", def.registryName, def.base);
            return null;
        }
        return new EntityAIAttackRangedBow((EntityMob & IRangedAttackMob) living, task.speed(1.0D), task.cooldown(20), task.distance(15.0F));
    }

    public static String names() { return String.join(", ", KINDS.values().stream().map(kind -> kind.name).toArray(String[]::new)); }

    public static List<TaskDef> parse(ResourceLocation key, JsonObject json) {
        if (!json.has("tasks")) { return Collections.emptyList(); }
        List<TaskDef> tasks = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "tasks")) {
            if (element.isJsonPrimitive()) {
                String text = element.getAsString().trim();
                if (!text.startsWith("-")) {
                    ContentLog.LOGGER.error("Entity variant {} lists the task '{}' as a bare name; a task to add is an object with \"task\" and \"priority\", a task to drop is its name after a '-'", key, text);
                    continue;
                }
                Kind kind = known(key, text.substring(1));
                if (kind != null) { tasks.add(new TaskDef(kind.name, true, 0, null, null, null, null, null, null, null, "", Collections.emptyList(), true, false, false, false, false, false, false)); }
                continue;
            }
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("Entity variant {} has a tasks entry that is neither an object nor a name, ignoring it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            Kind kind = known(key, Json.getString(entry, "task", ""));
            if (kind == null) { continue; }
            if (!entry.has("priority")) {
                ContentLog.LOGGER.error("Entity variant {} adds the task {} without a priority, so it is dropped; vanilla runs 0 first and its own tasks sit between 1 and 8", key, kind.name);
                continue;
            }
            tasks.add(new TaskDef(kind.name, false, Math.max(0, Json.getInt(entry, "priority")),
                    number(entry, "speed"), number(entry, "nearSpeed"),
                    decimal(entry, "distance"), decimal(entry, "near"), decimal(entry, "chance"), decimal(entry, "leap"),
                    entry.has("cooldown") ? Math.max(1, Json.getInt(entry, "cooldown")) : null,
                    Json.getString(entry, "entity", ""), Json.strings(entry, "items"),
                    Json.getBoolean(entry, "sight", true), Json.getBoolean(entry, "nearby", false), Json.getBoolean(entry, "help", false),
                    Json.getBoolean(entry, "memory", false), Json.getBoolean(entry, "close", false), Json.getBoolean(entry, "nocturnal", false),
                    Json.getBoolean(entry, "scared", false)));
        }
        return tasks;
    }

    @Nullable private static Kind known(ResourceLocation key, String name) {
        Kind kind = KINDS.get(Settings.lower(name));
        if (kind == null) { ContentLog.LOGGER.error("Entity variant {} names the task '{}', which is not one of {}", key, name, names()); }
        return kind;
    }

    @Nullable private static Double number(JsonObject json, String name) { return json.has(name) ? (Double) (double) Json.getFloat(json, name) : null; }

    @Nullable private static Float decimal(JsonObject json, String name) { return json.has(name) ? (Float) Json.getFloat(json, name) : null; }

    public static void apply(EntityLiving living, EntityVariantDef def) { apply(living, def, def.tasks); }

    static void apply(EntityLiving living, EntityVariantDef def, List<TaskDef> asked) {
        if (asked.isEmpty()) { return; }
        StringBuilder taken = ContentLog.LOGGER.debugEnabled() ? new StringBuilder() : null;
        for (TaskDef task : asked) {
            Kind kind = KINDS.get(task.name.toLowerCase(Locale.ROOT));
            if (kind == null) { continue; }
            if (task.remove) {
                drop(living.tasks, kind.type);
                drop(living.targetTasks, kind.type);
                if (taken != null) { taken.append(" -").append(kind.name); }
                continue;
            }
            if (!kind.needs.isInstance(living)) {
                ContentLog.LOGGER.error("Entity variant {} asks for the task {}, which needs {}; {} is not one", def.registryName, kind.name, kind.needsName, def.base);
                continue;
            }
            EntityAIBase made = kind.maker.make(living, task, def);
            if (made == null) { continue; }
            (kind.targeting ? living.targetTasks : living.tasks).addTask(task.priority, made);
            if (taken != null) { taken.append(' ').append(kind.name).append('@').append(task.priority); }
        }
        if (taken != null) { ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} takes its tasks:{}", def.registryName, (int) living.posX, (int) living.posY, (int) living.posZ, taken); }
    }

    private static void drop(EntityAITasks tasks, Class<? extends EntityAIBase> type) { AiTasks.remove(tasks, action -> action.getClass() == type); }

    @Nullable private static <T extends Entity> Class<? extends T> entity(TaskDef task, EntityVariantDef def, Class<T> least) {
        if (task.entity.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} asks for the task {} without saying which entity, add \"entity\"", def.registryName, task.name);
            return null;
        }
        Class<? extends EntityLivingBase> found = ContentEntities.living(task.entity, def);
        if (found == null) { return null; }
        if (!least.isAssignableFrom(found)) {
            ContentLog.LOGGER.error("Entity variant {} asks for the task {} on '{}', which is not {}", def.registryName, task.name, task.entity, least.getSimpleName());
            return null;
        }
        return found.asSubclass(least);
    }

    @Nullable private static Set<Item> items(TaskDef task, EntityVariantDef def) {
        if (task.items.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} asks to be tempted without saying by what, add \"items\"", def.registryName);
            return null;
        }
        Set<Item> wanted = new HashSet<>();
        for (String name : task.items) {
            Item item = Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(name));
            if (item == null) { ContentLog.LOGGER.error("Entity variant {} would be tempted by '{}', which is not a registered item", def.registryName, name); }
            else { wanted.add(item); }
        }
        return wanted.isEmpty() ? null : wanted;
    }
}
