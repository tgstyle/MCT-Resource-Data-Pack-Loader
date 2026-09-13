package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIAnvilWork;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAICharge;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIDig;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIFleeWhenHurt;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIPounce;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISleepByDay;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISniff;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIGust;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIPatrol;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAISwoop;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIKamikaze;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIStrike;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIThrower;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntity;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityCreeper;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityGhast;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLiving;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLivingNavigator;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityVillager;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.world.WorldServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentEntities {
    private static final Map<ResourceLocation, EntityVariantDef> DEFS = new LinkedHashMap<>();
    private static final Map<Class<?>, EntityVariantDef> BY_CLASS = new LinkedHashMap<>();
    private static final List<String> PLAYER_ONLY = Collections.singletonList("minecraft:player");
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> NAMES = new LinkedHashMap<>();
    private static final Map<String, SoundEvent> SOUNDS = new LinkedHashMap<>();
    private static final Map<Class<?>, float[]> SIZES = new LinkedHashMap<>();
    private static boolean loaded;
    @Nullable private static EntityLivingBase struck;
    private static long struckAt;
    private static boolean struckEffects;
    private static boolean struckFire;

    private ContentEntities() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.entities) { return false; }
        Json.eachFile(PackManager.ENTITIES, "entity file", (key, contents) -> {
            EntityVariantDef def = ContentParser.entityVariant(key, contents);
            if (def == null) { return; }
            if (missing(def)) {
                ContentLog.LOGGER.debug("Entity variant {} needs {}, which is not here, so it is left out", key, def.requires);
                return;
            }
            DEFS.put(key, def);
        });
        if (!DEFS.isEmpty()) { Summary.info("entities", "Loaded " + DEFS.size() + " entity variant(s) from packs"); }
        return !DEFS.isEmpty();
    }

    public static void register(IForgeRegistry<EntityEntry> registry) {
        if (!load()) { return; }
        int made = 0;
        int network = 0;
        for (Map.Entry<ResourceLocation, EntityVariantDef> entry : DEFS.entrySet()) {
            EntityVariantDef def = entry.getValue();
            EntityEntry base = Registries.find(ForgeRegistries.ENTITIES, def.base);
            if (base == null) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which nothing registers, leaving it out", entry.getKey(), def.base);
                continue;
            }
            Class<? extends Entity> made$class = EntityClassMaker.make(base.getEntityClass(), entry.getKey().getNamespace() + "_" + entry.getKey().getPath(), def.ignoresSpawnRules, def.hostile);
            if (made$class == null) { continue; }
            EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create();
            builder.entity(made$class).id(entry.getKey(), network++)
                    .name(entry.getKey().getNamespace() + "." + entry.getKey().getPath())
                    .tracker(def.trackingRange, def.trackingFrequency, def.trackVelocity);
            if (def.egg) { builder.egg(eggColor(def, true), eggColor(def, false)); }
            registry.register(builder.build());
            BY_CLASS.put(made$class, def);
            addSpawns(made$class, def);
            made++;
            ContentLog.LOGGER.debug("Entity variant {} read from the pack with attributes {} and equipment {}", entry.getKey(), def.attributes, def.equipment);
        }
        if (throwsReturning()) { registry.register(EntityEntryBuilder.create().entity(EntityReturningThrow.class).id(new ResourceLocation(ResourceDataPackLoader.MOD_ID, "returning_throw"), network).name(ResourceDataPackLoader.MOD_ID + ".returning_throw").tracker(64, 1, true).build()); }
        if (made > 0) { Summary.info("entities.registered", "Registered " + made + " entity variant(s) from packs"); }
    }

    private static boolean throwsReturning() {
        for (EntityVariantDef def : DEFS.values()) {
            if (def.throwsItems && def.throwReturns) { return true; }
        }
        return false;
    }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.texture.isEmpty()) { return null; }
        return TEXTURES.computeIfAbsent(def.texture, ResourceLocation::new);
    }

    public static float baseBabyChance(Entity entity, float chance) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null || def.keepsBaseBaby ? chance : 0.0F;
    }

    @Nullable public static ResourceLocation lootTable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.lootTable.isEmpty()) { return null; }
        return NAMES.computeIfAbsent(def.lootTable, ResourceLocation::new);
    }

    @Nullable public static SoundEvent soundEvent(Entity entity, int which) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null) { return null; }
        String name = which == 0 ? def.ambientSound : which == 1 ? def.hurtSound : which == 2 ? def.deathSound : which == 3 ? def.targetSound : def.explodeSound;
        if (name.isEmpty()) { return null; }
        if (SOUNDS.containsKey(name)) { return SOUNDS.get(name); }
        ResourceLocation key = new ResourceLocation(name);
        SoundEvent event = Registries.find(ForgeRegistries.SOUND_EVENTS, key);
        if (event == null) { ContentLog.LOGGER.error("Entity variant {} names sound {}, which nothing registers", def.registryName, key); }
        SOUNDS.put(name, event);
        return event;
    }

    public static int attackInterval(EntityLivingBase attacker) {
        IAttributeInstance speed = attacker.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_SPEED.getName());
        if (speed == null || speed.getAttributeValue() <= 0.0D) { return 20; }
        return Math.max(1, (int) Math.round(20.0D / speed.getAttributeValue()));
    }

    public static boolean immune(Entity entity, String damageType) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.immuneTo.isEmpty()) { return false; }
        for (String wanted : def.immuneTo) {
            if (wanted.equalsIgnoreCase(damageType)) { return true; }
        }
        return false;
    }

    public static float scale(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null) { return 1.0F; }
        return entity.isSprinting() ? def.angryScale : def.scale;
    }

    public static int threatLeast(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? 0 : def.threatLeast;
    }

    public static int threatHostile(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? 0 : def.threatHostile;
    }

    public static boolean leashable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.leashable;
    }

    public static boolean steerable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.steerable;
    }

    private static final java.util.Map<Integer, Long> WAS_AT = new java.util.HashMap<>();
    private static final java.util.Map<Integer, Float> FACED = new java.util.HashMap<>();
    private static final java.util.Map<Integer, Float> HEALTHS = new java.util.HashMap<>();
    private static final int ENGAGE_EVERY = 100;
    private static int engageWatch;

    @SubscribeEvent public static void onEngagement(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BY_CLASS.isEmpty() || !ContentLog.LOGGER.debugEnabled()) { return; }
        if (++engageWatch % ENGAGE_EVERY != 0) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        int mobs = 0;
        int aimed = 0;
        int pathless = 0;
        int reaching = 0;
        int walked = 0;
        int spun = 0;
        int hurt = 0;
        java.util.List<EntityLiving> here = new java.util.ArrayList<>();
        long away = 0L;
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (WorldServer world : server.worlds) {
            if (world == null) { continue; }
            for (Entity entity : world.loadedEntityList) {
                if (!(entity instanceof EntityLiving) || !BY_CLASS.containsKey(entity.getClass())) { continue; }
                EntityLiving mob = (EntityLiving) entity;
                mobs++;
                seen.add(mob.getEntityId());
                here.add(mob);
                Float hp = HEALTHS.put(mob.getEntityId(), mob.getHealth());
                if (hp != null && hp > mob.getHealth()) { hurt++; }
                long at = mob.getPosition().toLong();
                Long was = WAS_AT.put(mob.getEntityId(), at);
                Float faced = FACED.put(mob.getEntityId(), mob.rotationYaw);
                if (was != null && was != at) { walked++; }
                if (was != null && was == at && faced != null && Math.abs(net.minecraft.util.math.MathHelper.wrapDegrees(mob.rotationYaw - faced)) >= 90.0F) { spun++; }
                EntityLivingBase aim = mob.getAttackTarget();
                if (aim == null) { continue; }
                aimed++;
                double gap = Math.sqrt(mob.getDistanceSq(aim));
                away += (long) gap;
                if (mob.getNavigator().noPath()) { pathless++; }
                if (gap <= mob.width * 2.0F + aim.width) { reaching++; }
            }
        }
        int gone = 0;
        for (java.util.Iterator<Integer> held = WAS_AT.keySet().iterator(); held.hasNext();) {
            Integer id = held.next();
            if (seen.contains(id)) { continue; }
            held.remove();
            FACED.remove(id);
            HEALTHS.remove(id);
            gone++;
        }
        int thickest = 0;
        double spread = 0.0D;
        if (!here.isEmpty()) {
            double middleX = 0.0D;
            double middleZ = 0.0D;
            for (EntityLiving one : here) { middleX += one.posX; middleZ += one.posZ; }
            middleX /= here.size();
            middleZ /= here.size();
            for (EntityLiving one : here) { spread += Math.sqrt((one.posX - middleX) * (one.posX - middleX) + (one.posZ - middleZ) * (one.posZ - middleZ)); }
            spread /= here.size();
            for (EntityLiving one : here) {
                int near = 0;
                for (EntityLiving other : here) { if (one.getDistanceSq(other) <= 64.0D) { near++; } }
                thickest = Math.max(thickest, near);
            }
        }
        if (!here.isEmpty()) {
            double leastX = Double.MAX_VALUE;
            double mostX = -Double.MAX_VALUE;
            double leastZ = Double.MAX_VALUE;
            double mostZ = -Double.MAX_VALUE;
            for (EntityLiving one : here) {
                leastX = Math.min(leastX, one.posX);
                mostX = Math.max(mostX, one.posX);
                leastZ = Math.min(leastZ, one.posZ);
                mostZ = Math.max(mostZ, one.posZ);
            }
            int[][] cells = new int[11][11];
            double wideX = Math.max(1.0D, mostX - leastX);
            double wideZ = Math.max(1.0D, mostZ - leastZ);
            for (EntityLiving one : here) {
                int col = Math.min(10, (int) ((one.posX - leastX) / wideX * 11.0D));
                int row = Math.min(10, (int) ((one.posZ - leastZ) / wideZ * 11.0D));
                cells[row][col]++;
            }
            StringBuilder drawn = new StringBuilder();
            for (int[] row : cells) {
                if (drawn.length() > 0) { drawn.append('/'); }
                for (int count : row) { drawn.append(count == 0 ? "." : count > 9 ? "+" : Character.forDigit(count, 10)); }
            }
            ContentLog.LOGGER.debug("Where they stand, {} mob(s) over x {} to {} and z {} to {}, eleven cells each way: {}",
                    here.size(), (int) leastX, (int) mostX, (int) leastZ, (int) mostZ, drawn);
        }
        if (!FELL_TO.isEmpty()) {
            ContentLog.LOGGER.debug("What has killed pack mobs so far: {}", FELL_TO);
        }
        if (mobs == 0) { return; }
        ContentLog.LOGGER.debug("Of {} pack mob(s), {} hold a target, {} of those have no path to walk to it, {} stand close enough to strike, and a target is {} block(s) off on average; {} moved since the last look, {} are gone since the last look and {} turned 90 degrees or more without leaving their block, {} lost health since the last look, the thickest crowd holds {} within 8 blocks and a mob stands {} block(s) from the middle of them all on average",
                mobs, aimed, pathless, reaching, aimed == 0 ? 0L : away / aimed, walked, gone, spun, hurt, thickest, String.format(java.util.Locale.ROOT, "%.1f", spread));
    }

    private static final java.util.Map<String, Integer> FELL_TO = new java.util.LinkedHashMap<>();

    @SubscribeEvent public static void onDeathWatch(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (!ContentLog.LOGGER.debugEnabled() || BY_CLASS.get(event.getEntityLiving().getClass()) == null) { return; }
        FELL_TO.merge(event.getSource().damageType, 1, Integer::sum);
    }

    @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityLiving)) { return; }
        EntityVariantDef def = BY_CLASS.get(living.getClass());
        if (def == null) { return; }
        if (def.collectsExperience && !living.world.isRemote && living.isEntityAlive()) {
            ContentMobExperience.collect((EntityLiving) living);
            ContentAnvils.gather((EntityLiving) living);
        }
        if (def.scale == def.angryScale && def.scale == 1.0F && def.baby <= 0.0F && !def.amphibious && def.despawnTicks <= 0 && def.targetSound.isEmpty()) { return; }
        if (living.world.isRemote) {
            if (def.scale != 1.0F || def.scale != def.angryScale) { resize(living, living.isSprinting() ? def.angryScale : def.scale); }
            return;
        }
        if (def.despawnTicks > 0 && timeIsUp(living, def)) {
            living.setDead();
            return;
        }
        if (def.amphibious) { amphibious((EntityLiving) living); }
        if (def.baby > 0.0F && living.getEntityData().getBoolean(YOUNG) && living instanceof EntityAgeable && ((EntityAgeable) living).getGrowingAge() >= 0) { ((EntityAgeable) living).setGrowingAge(-24000); }
        boolean angry = stillRoused((EntityLiving) living);
        if (angry && !living.isSprinting()) { cry(living); }
        if (angry != living.isSprinting()) { living.setSprinting(angry); }
        resize(living, angry ? def.angryScale : def.scale);
    }

    private static void cry(EntityLivingBase living) {
        SoundEvent cry = soundEvent(living, 3);
        if (cry == null) { return; }
        float carries = (float) Math.max(1.0D, living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue() / 16.0D);
        float varies = BY_CLASS.get(living.getClass()).targetVaries;
        float pitch = varies <= 0.0F ? 1.0F : (float) Math.pow(2.0D, (living.getRNG().nextFloat() * 2.0F - 1.0F) * varies / 12.0D);
        living.world.playSound(null, living.posX, living.posY, living.posZ, cry, living.getSoundCategory(), carries, pitch);
    }

    @Nullable public static SoundEvent explodeSound(@Nullable Entity exploder) {
        if (exploder instanceof EntityTNTPrimed) { exploder = ((EntityTNTPrimed) exploder).getTntPlacedBy(); }
        return exploder == null ? null : soundEvent(exploder, 4);
    }

    public static void fromSpawner(boolean adding) { SPAWNER.set(adding); }

    private static List<PickDef> kin(EntityVariantDef def) {
        List<PickDef> kept = new ArrayList<>();
        List<TeamDef> sides = ContentTeams.claiming(def.registryName.toString());
        for (PickDef choice : def.becomes) {
            EntityVariantDef other = DEFS.get(new ResourceLocation(choice.name));
            if (other == null || !other.base.equals(def.base)) { continue; }
            List<TeamDef> theirs = ContentTeams.claiming(other.registryName.toString());
            if (sides.isEmpty() ? theirs.isEmpty() : sides.stream().anyMatch(theirs::contains)) { kept.add(choice); }
        }
        return kept;
    }

    public static boolean collectsExperience(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.collectsExperience;
    }

    public static boolean bright(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.bright;
    }

    public static float jumpMultiplier(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? 1.0F : def.jumpMultiplier;
    }

    public static float fallDamage(Entity entity, float original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? original : original * def.fallDamage;
    }

    public static float sound(Entity entity, float original, boolean pitch) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null) { return original; }
        return original * (pitch ? def.soundPitch : def.soundVolume);
    }

    public static float waterSlowdown(Entity entity, float original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? original : def.waterSlowdown;
    }

    public static int experience(Entity entity, int original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null || def.experience < 0 ? original : def.experience;
    }

    public static int maxFallHeight(Entity entity, int original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null || def.maxFallHeight < 0 ? original : def.maxFallHeight;
    }

    public static boolean breathesUnderwater(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && (def.breathesUnderwater || def.swims || def.amphibious);
    }

    public static boolean sinks(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.breathesUnderwater && !def.swims;
    }

    public static boolean despawns(Entity entity, boolean original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? original : def.despawns && original;
    }

    @Nullable public static EnumCreatureAttribute creatureAttribute(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.creatureAttribute.isEmpty()) { return null; }
        EnumCreatureAttribute value = Enums.byName(EnumCreatureAttribute.class, def.creatureAttribute);
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod or illager", def.registryName, def.creatureAttribute);
        return null;
    }

    public static boolean fireproof(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.fireproof;
    }

    public static boolean hidesArmor(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.hideArmor;
    }

    public static boolean hidesHeld(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.hideHeld;
    }

    public static float channel(int tint, int shift) { return (tint >> shift & 255) / 255.0F; }

    public static int tint(Entity entity, String part) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.tint == 0 || !def.tintParts.contains(part)) { return 0; }
        return def.tint;
    }

    @SubscribeEvent public static void onStruck(LivingAttackEvent event) {
        Entity by = event.getSource().getTrueSource();
        if (by == null || event.getEntityLiving().world.isRemote) { return; }
        EntityVariantDef def = BY_CLASS.get(by.getClass());
        if (def == null || (def.hitEffects && def.hitFire)) { return; }
        struck = event.getEntityLiving();
        struckAt = struck.world.getTotalWorldTime();
        struckEffects = !def.hitEffects;
        struckFire = !def.hitFire;
    }

    public static boolean struckFireless(Entity entity) { return struckFire && entity == struck && entity.world.getTotalWorldTime() == struckAt; }

    @SubscribeEvent public static void onShot(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityArrow)) { return; }
        EntityArrow arrow = (EntityArrow) event.getEntity();
        Entity shooter = arrow.shootingEntity;
        if (!(shooter instanceof EntityLivingBase)) { return; }
        EntityVariantDef def = BY_CLASS.get(shooter.getClass());
        if (def == null || !declaresAttackDamage(def)) { return; }
        IAttributeInstance damage = ((EntityLivingBase) shooter).getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        if (damage == null) { return; }
        arrow.setDamage(arrow.getDamage() - 2.0D + damage.getAttributeValue());
    }

    public static double attackReachSqr(EntityLivingBase attacker, EntityLivingBase target, double vanilla) {
        EntityVariantDef def = BY_CLASS.get(attacker.getClass());
        if (def == null || def.attackReach <= 0.0F) { return vanilla; }
        return (double) def.attackReach * def.attackReach + target.width;
    }

    public static float knockback(@Nullable Entity by, float strength) {
        EntityVariantDef def = by == null ? null : BY_CLASS.get(by.getClass());
        return def == null || def.knockback < 0.0F ? strength : def.knockback;
    }

    public static boolean lyingAsleep(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.sleepsByDay && entity.isSneaking();
    }

    public static boolean walks(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.walks;
    }

    @Nullable public static Boolean climbs(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? null : def.climbs;
    }

    public static boolean teleports(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null || def.teleports;
    }

    @SubscribeEvent public static void onEffect(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (struckEffects && living == struck && living.world.getTotalWorldTime() == struckAt) {
            event.setResult(Event.Result.DENY);
            return;
        }
        EntityVariantDef def = BY_CLASS.get(living.getClass());
        if (def == null || def.ignoresEffects.isEmpty()) { return; }
        ResourceLocation potion = event.getPotionEffect().getPotion().getRegistryName();
        String id = potion == null ? "" : potion.toString();
        if (def.effects.containsKey(id)) { return; }
        for (String ignored : def.ignoresEffects) {
            if (ignored.equalsIgnoreCase("all") || ignored.equalsIgnoreCase(id)) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        EntityVariantDef def = BY_CLASS.get(event.getEntity().getClass());
        if (def == null) { return; }
        if (!event.getWorld().isRemote && swapped(event, def)) { return; }
        remember(event.getEntity(), def);
        if (event.getWorld().isRemote) {
            resize(event.getEntity(), event.getEntity().isSprinting() ? def.angryScale : def.scale);
            return;
        }
        apply(event.getEntity(), def);
    }

    private static boolean swapped(EntityJoinWorldEvent event, EntityVariantDef def) {
        if (def.becomes.isEmpty() || SWAPPING.get() == Boolean.TRUE) { return false; }
        List<PickDef> choices = SPAWNER.get() == Boolean.TRUE ? kin(def) : def.becomes;
        String chosen = PickDef.pick(choices, event.getWorld().rand, null);
        if (chosen == null || chosen.equals(def.registryName.toString())) { return false; }
        ResourceLocation wanted = new ResourceLocation(chosen);
        if (!EntityList.isRegistered(wanted)) {
            ContentLog.LOGGER.error("Entity variant {} can become {}, which nothing registers, so it stays as it is", def.registryName, chosen);
            return false;
        }
        Entity was = event.getEntity();
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = EntityList.createEntityByIDFromName(wanted, event.getWorld());
            if (becomes == null) { return false; }
            becomes.setLocationAndAngles(was.posX, was.posY, was.posZ, was.rotationYaw, was.rotationPitch);
            if (becomes instanceof EntityLiving) { ((EntityLiving) becomes).onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(becomes)), null); }
            event.getWorld().spawnEntity(becomes);
        }
        finally { SWAPPING.set(Boolean.FALSE); }
        event.setCanceled(true);
        return true;
    }


    private static void apply(Entity entity, EntityVariantDef def) {
        if (!def.name.isEmpty() && !entity.hasCustomName()) {
            entity.setCustomNameTag(def.name);
            entity.setAlwaysRenderNameTag(def.showName);
        }
        if (def.silent) { entity.setSilent(true); }
        if (def.glowing) { entity.setGlowing(true); }
        if (def.invisible) { entity.setInvisible(true); }
        if (def.fireproof) { ((IEntity) entity).rdpl$setImmuneToFire(true); }
        if (def.invulnerable) { entity.setEntityInvulnerable(true); }
        resize(entity, def.scale);
        if (!(entity instanceof EntityLivingBase)) { return; }
        EntityLivingBase alive = (EntityLivingBase) entity;
        attributes(alive, def);
        body(alive, def);
        if (def.absorption > 0.0F) { alive.setAbsorptionAmount(def.absorption); }
        effects(alive, def);
        if (!(entity instanceof EntityLiving)) { return; }
        EntityLiving living = (EntityLiving) entity;
        if (def.swims) { swimmer(living); }
        if (def.swoops) { flyer(living); }
        if (def.walks) { walker(living, def); }
        if (def.climbs != null) { climber(living, def.climbs); }
        if (def.amphibious && living.getNavigator() instanceof PathNavigateGround) { ((PathNavigateGround) living.getNavigator()).setCanSwim(true); }
        if (def.breathesUnderwater || def.swims) {
            for (EntityAITasks.EntityAITaskEntry task : new ArrayList<>(living.tasks.taskEntries)) {
                if (task.action instanceof EntityAISwimming) { living.tasks.removeTask(task.action); }
            }
        }
        ResourceLocation table = lootTable(living);
        if (table != null) { ((IEntityLiving) living).rdpl$setDeathLootTable(table); }
        if (def.baby > 0.0F && rolledYoung(living, def)) { child(living); }
        if (living instanceof EntityVillager && !def.profession.isEmpty()) { profession((EntityVillager) living, def); }
        if (def.persistent) { living.enablePersistence(); }
        if (def.noAI) { living.setNoAI(true); }
        if (def.leftHanded) { living.setLeftHanded(true); }
        living.setCanPickUpLoot(def.picksUpLoot);
        priorities(living, def);
        gear(living, def);
        behavior(living, def);
        ContentTasks.apply(living, def);
    }

    private static void attributes(EntityLivingBase living, EntityVariantDef def) {
        AbstractAttributeMap map = living.getAttributeMap();
        for (Map.Entry<String, Double> entry : def.attributes.entrySet()) {
            IAttribute attribute = ContentAttributes.lookup(entry.getKey());
            IAttributeInstance instance = map.getAttributeInstanceByName(attribute == null ? entry.getKey() : attribute.getName());
            if (instance == null && attribute != null) { instance = map.registerAttribute(attribute); }
            if (instance == null) {
                ContentLog.LOGGER.error("Unknown attribute '{}' in {}, which {} does not carry either, skipping that modifier", entry.getKey(), def.registryName, def.base);
                continue;
            }
            instance.setBaseValue(entry.getValue());
            if (attribute == SharedMonsterAttributes.MAX_HEALTH) { living.setHealth((float) (double) entry.getValue()); }
        }
    }

    private static void body(EntityLivingBase alive, EntityVariantDef def) {
        if (def.hurtResistance >= 0) { alive.maxHurtResistantTime = def.hurtResistance; }
        if (def.stepHeight >= 0.0F) { alive.stepHeight = def.stepHeight; }
        if (ContentLog.LOGGER.debugEnabled() && (def.hurtResistance >= 0 || def.stepHeight >= 0.0F || def.climbs != null || def.attackReach > 0.0F || def.knockback >= 0.0F || def.teleports)) {
            ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} stands with step height {}, hurt resistance {}, attack reach {}, knockback {}, climbs {}, teleports {}", def.registryName, (int) alive.posX, (int) alive.posY, (int) alive.posZ, alive.stepHeight, alive.maxHurtResistantTime, def.attackReach, def.knockback, def.climbs, def.teleports);
        }
        if (!def.ownBlast) { return; }
        if (alive instanceof EntityCreeper) {
            ((IEntityCreeper) alive).rdpl$setFuseTime(def.explosionFuse);
            ((IEntityCreeper) alive).rdpl$setExplosionRadius((int) def.explosionPower);
        }
        else if (alive instanceof EntityGhast) { ((IEntityGhast) alive).rdpl$setExplosionStrength((int) def.explosionPower); }
    }

    private static void climber(EntityLiving living, boolean climbs) {
        if (climbs && !(living.getNavigator() instanceof PathNavigateClimber)) { ((IEntityLivingNavigator) living).rdpl$setNavigator(new PathNavigateClimber(living, living.world)); }
        else if (!climbs && living.getNavigator() instanceof PathNavigateClimber) { ((IEntityLivingNavigator) living).rdpl$setNavigator(new PathNavigateGround(living, living.world)); }
    }

    private static boolean declaresAttackDamage(EntityVariantDef def) {
        for (String key : def.attributes.keySet()) {
            if (ContentAttributes.lookup(key) == SharedMonsterAttributes.ATTACK_DAMAGE) { return true; }
        }
        return false;
    }

    private static void behavior(EntityLiving living, EntityVariantDef def) {
        if (ContentLog.LOGGER.debugEnabled() && (def.charges || def.pounces || def.sniffs > 0 || def.sleepsByDay || def.home > 0 || def.fleesWhenHurt > 0.0F || def.patrols || def.swoops || def.gusts)) {
            ContentLog.LOGGER.debug("Entity variant {} at {}, {}, {} takes its behaviors:{}{}{}{}{}{}{}{}{}", def.registryName, (int) living.posX, (int) living.posY, (int) living.posZ, def.charges ? " charges" : "", def.pounces ? " pounces" : "", def.sniffs > 0 ? " sniffs " + def.sniffs : "", def.fleesWhenHurt > 0.0F ? " flees under " + def.fleesWhenHurt : "", def.sleepsByDay ? " sleeps by day" : "", def.home > 0 ? " home " + def.home : "", def.patrols ? " patrols" : "", def.swoops ? " swoops" : "", def.gusts ? " gusts " + def.gustPower : "");
        }
        if (def.collectsExperience && living instanceof EntityCreature) { living.tasks.addTask(0, new EntityAIAnvilWork((EntityCreature) living)); }
        settled(living, def);
        if (def.passive) {
            clear(living.targetTasks);
            removeMelee(living.tasks);
            living.setAttackTarget(null);
            return;
        }
        if (!def.hostile) {
            if (def.charges) { ContentLog.LOGGER.error("Entity variant {} asks to charge, but is not hostile, so it never takes a target to charge at", def.registryName); }
            if (def.pounces) { ContentLog.LOGGER.error("Entity variant {} asks to pounce, but is not hostile, so it never takes a target to pounce on", def.registryName); }
            if (def.sniffs > 0) { ContentLog.LOGGER.error("Entity variant {} asks to sniff players out, but is not hostile, so it never takes a target when it finds one", def.registryName); }
            if (def.fleesWhenHurt > 0.0F) { ContentLog.LOGGER.error("Entity variant {} asks to flee when hurt, but is not hostile, so it never has a fight to flee", def.registryName); }
            if (def.patrols) { ContentLog.LOGGER.error("Entity variant {} asks to patrol, but is not hostile, so its patrol never converges on anyone", def.registryName); }
            if (def.swoops) { ContentLog.LOGGER.error("Entity variant {} asks to swoop, but is not hostile, so it never takes a target to dive on", def.registryName); }
            if (def.gusts) { ContentLog.LOGGER.error("Entity variant {} asks to gust, but is not hostile, so it never takes a target to blow away", def.registryName); }
            if (def.digs) { ContentLog.LOGGER.error("Entity variant {} asks to dig, but is not hostile, so it never has a target to dig toward", def.registryName); }
            if (def.explodes) { ContentLog.LOGGER.error("Entity variant {} asks to explode, but is not hostile, so it never takes a target to close on", def.registryName); }
            if (def.throwsItems) { ContentLog.LOGGER.error("Entity variant {} asks to throw what it holds, but is not hostile, so it never takes a target to throw at", def.registryName); }
            return;
        }
        if (!(living instanceof EntityCreature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behavior needs", def.registryName, def.base);
            return;
        }
        EntityCreature creature = (EntityCreature) living;
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(living.tasks.taskEntries)) {
            if (entry.action instanceof EntityAIAvoidEntity || entry.action instanceof EntityAIPanic || tame(entry.action)) { living.tasks.removeTask(entry.action); }
        }
        if (ownStrike(living, def)) {
            for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(living.tasks.taskEntries)) {
                if (entry.action instanceof EntityAIAttackMelee) { living.tasks.removeTask(entry.action); }
            }
            living.tasks.addTask(2, new EntityAIStrike(creature, 1.2D, false));
            ContentLog.LOGGER.debug("Entity variant {} asks for an attack damage of {}, which {} never reads when it strikes, so the blow is dealt by the pack's own reckoning instead",
                    def.registryName, living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), def.base);
        }
        else {
            boolean already = false;
            for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries) {
                if (task.action instanceof EntityAIAttackMelee) {
                    already = true;
                    break;
                }
            }
            if (!already) { living.tasks.addTask(2, new EntityAIAttackMelee(creature, 1.2D, false)); }
        }
        if (def.explodes) { living.tasks.addTask(0, new EntityAIKamikaze(creature, def.explosionPower, def.explosionFuse, def.explosionFire)); }
        if (def.throwsItems) { living.tasks.addTask(0, new EntityAIThrower(creature, carrying(def), def.explosionFuse, def.throwReload > 0 ? def.throwReload : def.explosionFuse, def.throwRetreat > 0 ? def.throwRetreat : def.explosionFuse, def.throwAmmo, def.throwPower, def.throwArc, living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), def.throwReturns)); }
        if (def.charges) { living.tasks.addTask(1, new EntityAICharge(creature, 2.0D)); }
        if (def.pounces) { living.tasks.addTask(1, new EntityAIPounce(creature)); }
        if (def.fleesWhenHurt > 0.0F) { living.tasks.addTask(0, new EntityAIFleeWhenHurt(creature, def.fleesWhenHurt, 1.4D)); }
        if (def.sniffs > 0) { living.tasks.addTask(3, new EntityAISniff(creature, def.sniffs)); }
        if (def.gusts) { living.tasks.addTask(1, new EntityAIGust(creature, def.gustPower)); }
        if (def.digs) { living.tasks.addTask(1, new EntityAIDig(creature)); }
        if (def.swoops) { living.tasks.addTask(1, new EntityAISwoop(creature)); }
        if (def.patrols) { living.tasks.addTask(4, new EntityAIPatrol(creature)); }
        living.targetTasks.addTask(1, new EntityAIHurtByTarget(creature, true));
        int priority = 2;
        List<String> targets = def.targets.isEmpty() ? PLAYER_ONLY : def.targets;
        for (String name : targets) {
            Class<? extends EntityLivingBase> type = living(name, def);
            if (type == null) { continue; }
            living.targetTasks.addTask(priority++, new EntityAINearestAttackableTarget<>(creature, type, !def.digs));
        }
    }

    private static boolean ownStrike(EntityLiving living, EntityVariantDef def) { return !(living instanceof EntityMob) && declaresAttackDamage(def); }

    private static boolean tame(EntityAIBase task) {
        return task instanceof EntityAIMate || task instanceof EntityAITempt || task instanceof EntityAIFollowParent || task instanceof EntityAIFollow
                || task instanceof EntityAIFollowOwner || task instanceof EntityAIFollowOwnerFlying || task instanceof EntityAISit || task instanceof EntityAILandOnOwnersShoulder;
    }

    private static void settled(EntityLiving living, EntityVariantDef def) {
        if (!def.sleepsByDay && def.home <= 0) { return; }
        if (!(living instanceof EntityCreature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to sleep by day or keep to a home, but {} does not walk the ground the way those behaviors need", def.registryName, def.base);
            return;
        }
        EntityCreature creature = (EntityCreature) living;
        if (def.home > 0) {
            NBTTagCompound kept = creature.getEntityData();
            if (!kept.hasKey(HOME_X)) {
                kept.setInteger(HOME_X, (int) Math.floor(creature.posX));
                kept.setInteger(HOME_Y, (int) Math.floor(creature.posY));
                kept.setInteger(HOME_Z, (int) Math.floor(creature.posZ));
            }
            creature.setHomePosAndDistance(new BlockPos(kept.getInteger(HOME_X), kept.getInteger(HOME_Y), kept.getInteger(HOME_Z)), def.home);
            living.tasks.addTask(4, new EntityAIMoveTowardsRestriction(creature, 1.0D));
        }
        if (def.sleepsByDay) { living.tasks.addTask(1, new EntityAISleepByDay(creature)); }
    }

    private static final String HOME_X = "rdplHomeX";
    private static final String HOME_Y = "rdplHomeY";
    private static final String HOME_Z = "rdplHomeZ";
    private static final int ROUSED = 60;
    private static final String BORN = "rdplBorn";
    private static final String CALM = "rdplCalmAt";
    private static final long CALM_STEP = 10L;
    private static final String ROLLED = "rdplBabyRolled";
    private static final String YOUNG = "rdplBabyYoung";
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final ThreadLocal<Boolean> SPAWNER = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private static boolean stillRoused(EntityLiving living) {
        long now = living.world.getTotalWorldTime();
        if (living.getAttackTarget() != null) {
            NBTTagCompound held = living.getEntityData();
            if (held.getLong(CALM) + CALM_STEP < now + ROUSED) { held.setLong(CALM, now + ROUSED); }
            return true;
        }
        return living.getEntityData().getLong(CALM) > now;
    }

    private static boolean timeIsUp(EntityLivingBase living, EntityVariantDef def) {
        NBTTagCompound held = living.getEntityData();
        long now = living.world.getTotalWorldTime();
        if (!held.hasKey(BORN)) {
            held.setLong(BORN, now);
            return false;
        }
        return now - held.getLong(BORN) >= def.despawnTicks;
    }

    private static boolean rolledYoung(EntityLiving living, EntityVariantDef def) {
        NBTTagCompound held = living.getEntityData();
        if (!held.hasKey(ROLLED)) {
            held.setBoolean(ROLLED, true);
            held.setBoolean(YOUNG, living.world.rand.nextFloat() < def.baby);
        }
        return held.getBoolean(YOUNG);
    }

    private static void child(EntityLiving living) {
        if (living instanceof EntityZombie) { ((EntityZombie) living).setChild(true); }
        else if (living instanceof EntityAgeable) { ((EntityAgeable) living).setGrowingAge(-24000); }
    }

    private static void profession(EntityVillager villager, EntityVariantDef def) {
        ResourceLocation key = new ResourceLocation(def.profession);
        VillagerRegistry.VillagerProfession found = Registries.find(ForgeRegistries.VILLAGER_PROFESSIONS, key);
        if (found == null) {
            ContentLog.LOGGER.error("Entity variant {} names profession {}, which nothing registers", def.registryName, key);
            return;
        }
        villager.setProfession(found);
        if (def.career > 0) { ((IEntityVillager) villager).rdpl$setCareer(def.career); }
    }

    private static void remember(Entity entity, EntityVariantDef def) {
        if (def.width > 0.0F && def.height > 0.0F) {
            SIZES.put(entity.getClass(), new float[] { def.width, def.height });
            return;
        }
        if (entity instanceof EntitySlime) { return; }
        SIZES.computeIfAbsent(entity.getClass(), k -> new float[] { entity.width, entity.height });
    }

    private static void resize(Entity entity, float scale) {
        float[] base = entity instanceof EntitySlime
                ? new float[] { 0.51000005F * ((EntitySlime) entity).getSlimeSize(), 0.51000005F * ((EntitySlime) entity).getSlimeSize() }
                : SIZES.get(entity.getClass());
        if (base == null) { return; }
        boolean young = entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild()
                || entity instanceof EntityZombie && ((EntityZombie) entity).isChild();
        float factor = young ? scale / 2.0F : scale;
        float width = base[0] * factor;
        float height = base[1] * factor;
        if (entity.width == width && entity.height == height) { return; }
        AxisAlignedBB before = entity.getEntityBoundingBox();
        double centerX = (before.minX + before.maxX) / 2.0D;
        double centerZ = (before.minZ + before.maxZ) / 2.0D;
        double half = width / 2.0D;
        entity.width = width;
        entity.height = height;
        entity.setEntityBoundingBox(new AxisAlignedBB(centerX - half, before.minY, centerZ - half, centerX + half, before.minY + height, centerZ + half));
    }

    private static void flyer(EntityLiving living) {
        ((IEntityLivingNavigator) living).rdpl$setNavigator(new PathNavigateFlying(living, living.world));
        ((IEntityLivingNavigator) living).rdpl$setMoveHelper(new EntityFlyHelper(living));
    }

    private static void walker(EntityLiving living, EntityVariantDef def) {
        if (!(living instanceof EntityRabbit)) {
            ContentLog.LOGGER.error("Entity variant {} asks to walk, but {} is not a rabbit, and only a rabbit moves in hops", def.registryName, def.base);
            return;
        }
        ((IEntityLivingNavigator) living).rdpl$setMoveHelper(new EntityMoveHelper(living));
    }

    private static void swimmer(EntityLiving living) {
        ((IEntityLivingNavigator) living).rdpl$setNavigator(new PathNavigateSwimmer(living, living.world));
        ((IEntityLivingNavigator) living).rdpl$setMoveHelper(new SwimmingMoveHelper(living));
    }

    private static void amphibious(EntityLiving living) {
        boolean wet = living.isInWater();
        boolean swimming = living.getNavigator() instanceof PathNavigateSwimmer;
        if (wet == swimming) { return; }
        if (wet) {
            swimmer(living);
            return;
        }
        PathNavigateGround ground = new PathNavigateGround(living, living.world);
        ground.setCanSwim(true);
        ((IEntityLivingNavigator) living).rdpl$setNavigator(ground);
        ((IEntityLivingNavigator) living).rdpl$setMoveHelper(new EntityMoveHelper(living));
    }

    private static void effects(EntityLivingBase living, EntityVariantDef def) {
        for (Map.Entry<String, Integer> entry : def.effects.entrySet()) {
            ResourceLocation name = new ResourceLocation(entry.getKey());
            Potion potion = Registries.find(ForgeRegistries.POTIONS, name);
            if (potion == null) {
                ContentLog.LOGGER.error("Entity variant {} wants effect {}, which nothing registers", def.registryName, name);
                continue;
            }
            living.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, entry.getValue(), false, false));
        }
    }

    private static void priorities(EntityLiving living, EntityVariantDef def) {
        for (Map.Entry<String, Float> entry : def.pathPriorities.entrySet()) {
            PathNodeType type = null;
            for (PathNodeType value : PathNodeType.values()) {
                if (value.name().equalsIgnoreCase(entry.getKey())) { type = value; }
            }
            if (type == null) {
                ContentLog.LOGGER.error("Entity variant {} names path type '{}', which is not one of {}", def.registryName, entry.getKey(), Arrays.toString(PathNodeType.values()));
                continue;
            }
            living.setPathPriority(type, entry.getValue());
        }
    }

    private static ItemStack carrying(EntityVariantDef def) {
        String named = def.equipment.get("mainhand");
        if (named == null) { return ItemStack.EMPTY; }
        ResourceLocation name = new ResourceLocation(named);
        Item item = Registries.find(ForgeRegistries.ITEMS, name);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static void gear(EntityLiving living, EntityVariantDef def) {
        for (Map.Entry<String, String> entry : def.equipment.entrySet()) {
            EntityEquipmentSlot slot = slot(entry.getKey());
            if (slot == null) {
                ContentLog.LOGGER.error("Entity variant {} names equipment slot '{}', which is not one of mainhand, offhand, head, chest, legs or feet", def.registryName, entry.getKey());
                continue;
            }
            ResourceLocation name = new ResourceLocation(entry.getValue());
            Item item = Registries.find(ForgeRegistries.ITEMS, name);
            if (item == null) {
                ContentLog.LOGGER.error("Entity variant {} gives {}, which nothing registers", def.registryName, name);
                continue;
            }
            living.setItemStackToSlot(slot, new ItemStack(item));
            living.setDropChance(slot, def.dropChance);
        }
    }

    private static void addSpawns(Class<? extends Entity> type, EntityVariantDef def) {
        if (def.spawns.isEmpty() || !EntityLiving.class.isAssignableFrom(type)) { return; }
        List<Biome> biomes = biomes(def);
        if (biomes.isEmpty()) { return; }
        for (SpawnEntryDef entry : def.spawns) {
            net.minecraft.entity.EnumCreatureType creature = creatureType(entry.creatureType);
            if (creature == null) {
                ContentLog.LOGGER.error("Entity variant {} has spawn type '{}', which is not one of monster, creature, ambient or water", def.registryName, entry.creatureType);
                continue;
            }
            EntityRegistry.addSpawn(type.asSubclass(EntityLiving.class), entry.weight, entry.min, entry.max, creature, biomes.toArray(new Biome[0]));
        }
    }

    private static List<Biome> biomes(EntityVariantDef def) {
        List<Biome> found = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (matches(biome, def)) { found.add(biome); }
        }
        if (found.isEmpty()) { ContentLog.LOGGER.error("Entity variant {} names biomes nothing matches, so it will not spawn on its own", def.registryName); }
        return found;
    }

    private static boolean matches(Biome biome, EntityVariantDef def) {
        if (def.biomes.isEmpty() && def.biomeTypes.isEmpty()) { return true; }
        ResourceLocation name = biome.getRegistryName();
        for (String wanted : def.biomes) {
            if (name != null && wanted.equalsIgnoreCase(name.toString())) { return true; }
            if (wanted.equalsIgnoreCase(ContentBiomeControl.shownName(biome))) { return true; }
        }
        for (String wanted : def.biomeTypes) {
            for (net.minecraftforge.common.BiomeDictionary.Type type : net.minecraftforge.common.BiomeDictionary.getTypes(biome)) {
                if (type.getName().equalsIgnoreCase(wanted)) { return true; }
            }
        }
        return false;
    }

    private static int eggColor(EntityVariantDef def, boolean primary) {
        int wanted = primary ? def.eggPrimary : def.eggSecondary;
        if (wanted >= 0) { return wanted; }
        EntityList.EntityEggInfo info = EntityList.ENTITY_EGGS.get(def.base);
        if (info == null) { return primary ? 0xFFFFFF : 0x808080; }
        return primary ? info.primaryColor : info.secondaryColor;
    }

    private static void clear(EntityAITasks tasks) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) { tasks.removeTask(entry.action); }
    }

    private static void removeMelee(EntityAITasks tasks) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) {
            if (entry.action instanceof EntityAIAttackMelee) { tasks.removeTask(entry.action); }
        }
    }


    @Nullable private static EntityEquipmentSlot slot(String name) { return Enums.byName(EntityEquipmentSlot.class, name); }

    @Nullable private static net.minecraft.entity.EnumCreatureType creatureType(String name) { return Enums.byName(net.minecraft.entity.EnumCreatureType.class, name); }

    @Nullable static Class<? extends EntityLivingBase> living(String name, EntityVariantDef def) {
        ResourceLocation location = new ResourceLocation(name);
        if ("minecraft".equals(location.getNamespace()) && "player".equals(location.getPath())) { return EntityPlayer.class; }
        EntityEntry entry = Registries.find(ForgeRegistries.ENTITIES, location);
        if (entry == null || !EntityLivingBase.class.isAssignableFrom(entry.getEntityClass())) {
            ContentLog.LOGGER.error("Entity variant {} wants to attack '{}', which is not a living entity that is registered", def.registryName, name);
            return null;
        }
        return entry.getEntityClass().asSubclass(EntityLivingBase.class);
    }

    private static boolean missing(EntityVariantDef def) { return !ContentRegistry.available(def.requires, def.registryName); }
}
