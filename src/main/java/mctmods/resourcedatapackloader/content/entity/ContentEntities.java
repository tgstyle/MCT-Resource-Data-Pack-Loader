package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import mctmods.resourcedatapackloader.mixin.AccessorEntity;
import mctmods.resourcedatapackloader.mixin.AccessorEntityLiving;
import mctmods.resourcedatapackloader.mixin.AccessorEntityVillager;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Loader;
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
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentEntities {
    private static final Map<ResourceLocation, EntityVariantDef> DEFS = new LinkedHashMap<>();
    private static final Map<Class<?>, EntityVariantDef> BY_CLASS = new LinkedHashMap<>();
    private static final List<String> PLAYER_ONLY = Collections.singletonList("minecraft:player");
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> NAMES = new LinkedHashMap<>();
    private static final Map<Class<?>, float[]> SIZES = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentEntities() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.entities) { return false; }

        PackManager.get().forEach(PackManager.ENTITIES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                EntityVariantDef def = ContentParser.entityVariant(key, contents);
                if (def == null) { return; }
                if (!present(def)) {
                    ContentLog.LOGGER.info("Entity variant {} needs {}, which is not here, so it is left out", key, def.requires);
                    return;
                }
                DEFS.put(key, def);
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in entity file {}, ignoring it", key, ex); }
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
            EntityEntry base = ForgeRegistries.ENTITIES.containsKey(def.base) ? ForgeRegistries.ENTITIES.getValue(def.base) : null;
            if (base == null) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which nothing registers, leaving it out", entry.getKey(), def.base);
                continue;
            }

            Class<? extends Entity> made$class = EntityClassMaker.make(base.getEntityClass(), entry.getKey().getNamespace() + "_" + entry.getKey().getPath());
            if (made$class == null) { continue; }

            EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create();
            builder.entity(made$class).id(entry.getKey(), network++)
                    .name(entry.getKey().getNamespace() + "." + entry.getKey().getPath())
                    .tracker(def.trackingRange, def.trackingFrequency, def.trackVelocity);
            if (def.egg) { builder.egg(eggColour(def, true), eggColour(def, false)); }

            registry.register(builder.build());
            BY_CLASS.put(made$class, def);
            addSpawns(made$class, def);
            made++;
        }

        if (made > 0) { Summary.info("entities.registered", "Registered " + made + " entity variant(s) from packs"); }
    }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.texture.isEmpty()) { return null; }

        return TEXTURES.computeIfAbsent(def.texture, ResourceLocation::new);
    }

    @Nullable public static ResourceLocation lootTable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.lootTable.isEmpty()) { return null; }

        return NAMES.computeIfAbsent(def.lootTable, ResourceLocation::new);
    }

    @Nullable public static SoundEvent soundEvent(Entity entity, int which) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null) { return null; }

        String name = which == 0 ? def.ambientSound : which == 1 ? def.hurtSound : def.deathSound;
        if (name.isEmpty()) { return null; }

        ResourceLocation key = new ResourceLocation(name);
        SoundEvent event = ForgeRegistries.SOUND_EVENTS.containsKey(key) ? ForgeRegistries.SOUND_EVENTS.getValue(key) : null;
        if (event == null) { ContentLog.LOGGER.error("Entity variant {} names sound {}, which nothing registers", def.registryName, key); }

        return event;
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

    public static boolean leashable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.leashable;
    }

    public static boolean steerable(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.steerable;
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityLiving)) { return; }

        EntityVariantDef def = BY_CLASS.get(living.getClass());
        if (def == null || (def.scale == def.angryScale && !def.baby)) { return; }

        if (living.world.isRemote) {
            resize(living, living.isSprinting() ? def.angryScale : def.scale);
            return;
        }

        if (def.baby && living instanceof EntityAgeable && ((EntityAgeable) living).getGrowingAge() >= 0) { ((EntityAgeable) living).setGrowingAge(-24000); }

        boolean angry = ((EntityLiving) living).getAttackTarget() != null;
        if (angry != living.isSprinting()) { living.setSprinting(angry); }

        resize(living, angry ? def.angryScale : def.scale);
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
        return def != null && def.breathesUnderwater;
    }

    public static boolean despawns(Entity entity, boolean original) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def == null ? original : def.despawns && original;
    }

    @Nullable public static EnumCreatureAttribute creatureAttribute(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        if (def == null || def.creatureAttribute.isEmpty()) { return null; }

        for (EnumCreatureAttribute value : EnumCreatureAttribute.values()) {
            if (value.name().equalsIgnoreCase(def.creatureAttribute)) { return value; }
        }
        ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod or illager", def.registryName, def.creatureAttribute);
        return null;
    }

    public static boolean hidesArmor(Entity entity) {
        EntityVariantDef def = BY_CLASS.get(entity.getClass());
        return def != null && def.hideArmor;
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent event) {
        EntityVariantDef def = BY_CLASS.get(event.getEntity().getClass());
        if (def == null) { return; }

        remember(event.getEntity(), def);
        if (event.getWorld().isRemote) {
            resize(event.getEntity(), event.getEntity().isSprinting() ? def.angryScale : def.scale);
            return;
        }

        apply(event.getEntity(), def);
    }

    private static void apply(Entity entity, EntityVariantDef def) {
        if (!def.name.isEmpty() && !entity.hasCustomName()) {
            entity.setCustomNameTag(def.name);
            entity.setAlwaysRenderNameTag(def.showName);
        }
        if (def.silent) { entity.setSilent(true); }
        if (def.glowing) { entity.setGlowing(true); }
        if (def.invisible) { entity.setInvisible(true); }
        if (def.fireproof) { ((AccessorEntity) entity).rdpl$setImmuneToFire(true); }
        if (def.invulnerable) { entity.setEntityInvulnerable(true); }
        resize(entity, def.scale);
        if (!(entity instanceof EntityLivingBase)) { return; }

        EntityLivingBase alive = (EntityLivingBase) entity;
        attributes(alive, def);
        if (def.absorption > 0.0F) { alive.setAbsorptionAmount(def.absorption); }
        effects(alive, def);
        if (!(entity instanceof EntityLiving)) { return; }

        EntityLiving living = (EntityLiving) entity;
        ResourceLocation table = lootTable(living);
        if (table != null) { ((AccessorEntityLiving) living).rdpl$setDeathLootTable(table); }
        if (def.baby) { child(living); }
        if (living instanceof EntityVillager && !def.profession.isEmpty()) { profession((EntityVillager) living, def); }
        if (def.persistent) { living.enablePersistence(); }
        if (def.noAI) { living.setNoAI(true); }
        if (def.leftHanded) { living.setLeftHanded(true); }
        living.setCanPickUpLoot(def.picksUpLoot);
        priorities(living, def);
        gear(living, def);
        behaviour(living, def);
    }

    private static void attributes(EntityLivingBase living, EntityVariantDef def) {
        for (Map.Entry<String, Double> entry : def.attributes.entrySet()) {
            IAttribute attribute = attribute(entry.getKey());
            if (attribute == null) {
                ContentLog.LOGGER.error("Entity variant {} names attribute '{}', which is not one of maxHealth, movementSpeed, attackDamage, knockbackResistance, followRange or armor", def.registryName, entry.getKey());
                continue;
            }

            AbstractAttributeMap map = living.getAttributeMap();
            IAttributeInstance instance = map.getAttributeInstanceByName(attribute.getName());
            if (instance == null) { instance = map.registerAttribute(attribute); }
            instance.setBaseValue(entry.getValue());
            if (attribute == SharedMonsterAttributes.MAX_HEALTH) { living.setHealth((float) (double) entry.getValue()); }
        }
    }

    private static void behaviour(EntityLiving living, EntityVariantDef def) {
        if (def.passive) {
            clear(living.targetTasks);
            removeMelee(living.tasks);
            living.setAttackTarget(null);
            return;
        }
        if (!def.hostile) { return; }
        if (!(living instanceof EntityCreature)) {
            ContentLog.LOGGER.error("Entity variant {} asks to be hostile, but {} does not walk the ground the way the attack behaviour needs", def.registryName, def.base);
            return;
        }

        EntityCreature creature = (EntityCreature) living;
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(living.tasks.taskEntries)) {
            if (entry.action instanceof EntityAIAvoidEntity || entry.action instanceof EntityAIPanic) { living.tasks.removeTask(entry.action); }
        }

        boolean already = false;
        for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries) {
            if (task.action instanceof EntityAIAttackMelee) {
                already = true;
                break;
            }
        }
        if (!already) { living.tasks.addTask(2, new EntityAIAttackMelee(creature, 1.2D, false)); }

        living.targetTasks.addTask(1, new EntityAIHurtByTarget(creature, true));
        int priority = 2;
        List<String> targets = def.targets.isEmpty() ? PLAYER_ONLY : def.targets;
        for (String name : targets) {
            Class<? extends EntityLivingBase> type = living(name, def);
            if (type == null) { continue; }

            living.targetTasks.addTask(priority++, new EntityAINearestAttackableTarget<>(creature, type, true));
        }
    }

    private static void child(EntityLiving living) {
        if (living instanceof EntityZombie) { ((EntityZombie) living).setChild(true); }
        else if (living instanceof EntityAgeable) { ((EntityAgeable) living).setGrowingAge(-24000); }
    }

    private static void profession(EntityVillager villager, EntityVariantDef def) {
        ResourceLocation key = new ResourceLocation(def.profession);
        VillagerRegistry.VillagerProfession found = ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(key) ? ForgeRegistries.VILLAGER_PROFESSIONS.getValue(key) : null;
        if (found == null) {
            ContentLog.LOGGER.error("Entity variant {} names profession {}, which nothing registers", def.registryName, key);
            return;
        }

        villager.setProfession(found);
        if (def.career > 0) { ((AccessorEntityVillager) villager).rdpl$setCareer(def.career); }
    }

    private static void remember(Entity entity, EntityVariantDef def) {
        if (def.width > 0.0F && def.height > 0.0F) {
            SIZES.put(entity.getClass(), new float[] { def.width, def.height });
            return;
        }
        SIZES.computeIfAbsent(entity.getClass(), k -> new float[] { entity.width, entity.height });
    }

    private static void resize(Entity entity, float scale) {
        float[] base = SIZES.get(entity.getClass());
        if (base == null) { return; }

        boolean young = entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild()
                || entity instanceof EntityZombie && ((EntityZombie) entity).isChild();
        float factor = young ? scale / 2.0F : scale;
        float width = base[0] * factor;
        float height = base[1] * factor;
        if (entity.width == width && entity.height == height) { return; }

        AxisAlignedBB before = entity.getEntityBoundingBox();
        double centreX = (before.minX + before.maxX) / 2.0D;
        double centreZ = (before.minZ + before.maxZ) / 2.0D;
        double half = width / 2.0D;
        entity.width = width;
        entity.height = height;
        entity.setEntityBoundingBox(new AxisAlignedBB(centreX - half, before.minY, centreZ - half, centreX + half, before.minY + height, centreZ + half));
    }

    private static void effects(EntityLivingBase living, EntityVariantDef def) {
        for (Map.Entry<String, Integer> entry : def.effects.entrySet()) {
            ResourceLocation name = new ResourceLocation(entry.getKey());
            Potion potion = ForgeRegistries.POTIONS.containsKey(name) ? ForgeRegistries.POTIONS.getValue(name) : null;
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

    private static void gear(EntityLiving living, EntityVariantDef def) {
        for (Map.Entry<String, String> entry : def.equipment.entrySet()) {
            EntityEquipmentSlot slot = slot(entry.getKey());
            if (slot == null) {
                ContentLog.LOGGER.error("Entity variant {} names equipment slot '{}', which is not one of mainhand, offhand, head, chest, legs or feet", def.registryName, entry.getKey());
                continue;
            }

            ResourceLocation name = new ResourceLocation(entry.getValue());
            Item item = ForgeRegistries.ITEMS.containsKey(name) ? ForgeRegistries.ITEMS.getValue(name) : null;
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
            if (wanted.equalsIgnoreCase(biome.getBiomeName())) { return true; }
        }
        for (String wanted : def.biomeTypes) {
            for (net.minecraftforge.common.BiomeDictionary.Type type : net.minecraftforge.common.BiomeDictionary.getTypes(biome)) {
                if (type.getName().equalsIgnoreCase(wanted)) { return true; }
            }
        }
        return false;
    }

    private static int eggColour(EntityVariantDef def, boolean primary) {
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

    @Nullable private static IAttribute attribute(String name) {
        switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "maxhealth": return SharedMonsterAttributes.MAX_HEALTH;
            case "movementspeed": return SharedMonsterAttributes.MOVEMENT_SPEED;
            case "attackdamage": return SharedMonsterAttributes.ATTACK_DAMAGE;
            case "knockbackresistance": return SharedMonsterAttributes.KNOCKBACK_RESISTANCE;
            case "followrange": return SharedMonsterAttributes.FOLLOW_RANGE;
            case "armor": return SharedMonsterAttributes.ARMOR;
            default: return null;
        }
    }

    @Nullable private static EntityEquipmentSlot slot(String name) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getName().equalsIgnoreCase(name.trim())) { return slot; }
        }
        return null;
    }

    @Nullable private static net.minecraft.entity.EnumCreatureType creatureType(String name) {
        for (net.minecraft.entity.EnumCreatureType type : net.minecraft.entity.EnumCreatureType.values()) {
            if (type.name().equalsIgnoreCase(name.trim())) { return type; }
        }
        return null;
    }

    @Nullable private static Class<? extends EntityLivingBase> living(String name, EntityVariantDef def) {
        ResourceLocation location = new ResourceLocation(name);
        if ("minecraft".equals(location.getNamespace()) && "player".equals(location.getPath())) { return EntityPlayer.class; }

        EntityEntry entry = ForgeRegistries.ENTITIES.containsKey(location) ? ForgeRegistries.ENTITIES.getValue(location) : null;
        if (entry == null || !EntityLivingBase.class.isAssignableFrom(entry.getEntityClass())) {
            ContentLog.LOGGER.error("Entity variant {} wants to attack '{}', which is not a living entity that is registered", def.registryName, name);
            return null;
        }
        return entry.getEntityClass().asSubclass(EntityLivingBase.class);
    }

    private static boolean present(EntityVariantDef def) {
        for (String name : def.requires) {
            if (!Loader.isModLoaded(name) && !PackManager.get().provides(name)) { return false; }
        }
        return true;
    }
}
