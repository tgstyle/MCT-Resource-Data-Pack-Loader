package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntity;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityCreeper;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityGhast;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLiving;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLivingNavigator;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityVillager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Registries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.pathfinding.PathNavigateFlying;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

final class ContentEntityApply {
    private static final Map<Class<?>, float[]> SIZES = new LinkedHashMap<>();
    private static final String ROLLED = "rdplBabyRolled";
    static final String YOUNG = "rdplBabyYoung";

    private ContentEntityApply() {}

    static void apply(Entity entity, EntityVariantDef def) {
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
        ResourceLocation table = ContentEntities.lootTable(living);
        if (table != null) { ((IEntityLiving) living).rdpl$setDeathLootTable(table); }
        if (def.baby > 0.0F && rolledYoung(living, def)) { child(living); }
        if (living instanceof EntityVillager && !def.profession.isEmpty()) { profession((EntityVillager) living, def); }
        if (def.persistent) { living.enablePersistence(); }
        if (def.noAI) { living.setNoAI(true); }
        if (def.leftHanded) { living.setLeftHanded(true); }
        living.setCanPickUpLoot(def.picksUpLoot);
        priorities(living, def);
        gear(living, def);
        ContentEntityBehavior.behavior(living, def);
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

    static void remember(Entity entity, EntityVariantDef def) {
        if (def.width > 0.0F && def.height > 0.0F) {
            SIZES.put(entity.getClass(), new float[] { def.width, def.height });
            return;
        }
        if (entity instanceof EntitySlime) { return; }
        SIZES.computeIfAbsent(entity.getClass(), k -> new float[] { entity.width, entity.height });
    }

    static void resize(Entity entity, float scale) {
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

    static void amphibious(EntityLiving living) {
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

    @Nullable private static EntityEquipmentSlot slot(String name) { return Enums.byName(EntityEquipmentSlot.class, name); }
}
