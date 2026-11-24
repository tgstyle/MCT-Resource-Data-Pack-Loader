package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.ai.StealContainerGoal;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankRandomDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankScalingDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserRanks;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentRanks {
    private static final String RANK = "rdplRank";
    private static final String STEPS = "rdplRankSteps";
    private static final String GEAR_PENDING = "rdplRankGear";
    private static final String NAME = "RDPL rank";
    private static final UUID HEALTH = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a21");
    private static final UUID DAMAGE = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a22");
    private static final Map<String, RankDef> DEFS = new LinkedHashMap<>();
    private static final Map<Entity, List<ItemStack>> GEAR = new MapMaker().weakKeys().makeMap();

    private ContentRanks() {}

    public static boolean load() {
        DEFS.clear();
        Json.eachObject(PackManager.RANKS, "rank definition", ContentRanks::read);
        return !DEFS.isEmpty();
    }

    public static String rankOf(Entity entity) { return entity.getEntityData().getString(RANK); }

    public static String killKey(String namespace, String entry) {
        int at = entry.indexOf('@');
        return at < 0 ? entry : entry.substring(0, at) + "@" + IdNames.namespaced(entry.substring(at + 1).trim(), namespace);
    }

    private static void read(ResourceLocation key, JsonObject json) {
        RankDef def = ContentParserRanks.parse(key, json);
        if (def != null) { DEFS.put(key.toString(), def); }
    }

    public static void verify() {
        MobGearPool.clear();
        DEFS.values().removeIf(ContentRanks::unregistered);
        Summary.loaded("ranks", DEFS.size(), "mob rank(s)");
    }

    public static boolean stealing() {
        for (RankDef def : DEFS.values()) {
            if (def.steals) { return true; }
        }
        return false;
    }

    private static boolean unregistered(RankDef def) {
        boolean missing = false;
        List<String> items = new ArrayList<>(def.equipment.values());
        if (def.digs && !def.digItem.isEmpty()) { items.add(def.digItem); }
        for (String exclude : def.random.exclude) {
            if (exclude.indexOf(':') >= 0) { items.add(exclude); }
        }
        for (String item : items) {
            if (Registries.itemNamed(item) == null) {
                ContentLog.LOGGER.error("Rank {} names item '{}', which nothing registers, ignoring the rank", def.key, item);
                missing = true;
            }
        }
        for (String id : def.entities) {
            if (MobStats.unknownEntity(id)) {
                ContentLog.LOGGER.error("Rank {} names entity '{}', which is not an entity, ignoring the rank", def.key, id);
                missing = true;
            }
        }
        for (String potion : def.effects.keySet()) {
            if (Registries.find(ForgeRegistries.POTIONS, potion) == null) {
                ContentLog.LOGGER.error("Rank {} wants effect '{}', which nothing registers, ignoring the rank", def.key, potion);
                missing = true;
            }
        }
        return missing;
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || DEFS.isEmpty() || !(event.getEntity() instanceof EntityLiving)) { return; }
        EntityLiving mob = (EntityLiving) event.getEntity();
        NBTTagCompound data = mob.getEntityData();
        if (data.hasKey(RANK)) {
            RankDef known = DEFS.get(data.getString(RANK));
            if (known != null) { arm(mob, known); }
            return;
        }
        List<RankDef> matches = new ArrayList<>();
        for (RankDef def : DEFS.values()) {
            if (MobStats.targets(def.entities, mob) && (def.dimensions.isEmpty() || CardPlace.inAny(def.dimensions, mob.world.provider.getDimension()))) { matches.add(def); }
        }
        RankDef def = WeightedPicks.pick(matches, rank -> rank.weight, mob.world.rand);
        if (def == null) { return; }
        data.setString(RANK, def.key.toString());
        float steps = RankScaling.steps(mob, def.scaling);
        if (def.scaling != null) { data.setFloat(STEPS, steps); }
        RankScalingDef scaling = RankScaling.of(def);
        float health = Math.max(0.1F, RankScaling.add(def.health, scaling.health, steps));
        MobStats.scale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH, health, NAME);
        if (health != 1.0F) { mob.setHealth(mob.getHealth() * health); }
        MobStats.scale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE, Math.max(0.0F, RankScaling.add(def.damage, scaling.damage, steps)), NAME);
        ContentEntityApply.effects(mob, def.effects, def.key);
        if (!def.label.isEmpty() && !mob.hasCustomName()) { mob.setCustomNameTag(def.label.replace("%s", mob.getName())); }
        if (givesGear(def)) { data.setBoolean(GEAR_PENDING, true); }
        arm(mob, def);
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} rolled for {} at {}", def.key, EntityList.getKey(mob), mob.getPosition()); }
    }

    private static boolean givesGear(RankDef def) { return !def.equipment.isEmpty() || def.random != RankRandomDef.NONE || scalesGear(def.scaling) || !def.digItem.isEmpty() || def.picksUpLoot != null; }

    private static boolean scalesGear(@Nullable RankScalingDef scaling) { return scaling != null && (scaling.armor > 0.0F || scaling.weapon > 0.0F || scaling.enchant > 0.0F); }

    private static void arm(EntityLiving mob, RankDef def) {
        if (def.digs) {
            if (mob instanceof EntityCreature) { MobAbilities.dig((EntityCreature) mob, true); }
            else if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} digs, but {} is not a walking creature, skipping it", def.key, EntityList.getKey(mob)); }
        }
        if (def.breaksDoors) {
            EntityAIBase task = MobAbilities.breakDoor(mob);
            if (task != null) { mob.tasks.addTask(1, task); }
        }
        if (def.steals && mob instanceof EntityCreature) { mob.tasks.addTask(5, new StealContainerGoal((EntityCreature) mob)); }
    }

    @SubscribeEvent public static void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityLiving) || event.getEntity().world.isRemote) { return; }
        EntityLiving mob = (EntityLiving) event.getEntity();
        NBTTagCompound data = mob.getEntityData();
        if (!data.hasKey(GEAR_PENDING)) { return; }
        data.removeTag(GEAR_PENDING);
        RankDef def = DEFS.get(data.getString(RANK));
        if (def != null) { gear(mob, def, data.getFloat(STEPS)); }
    }

    private static void gear(EntityLiving mob, RankDef def, float steps) {
        RankScalingDef scaling = RankScaling.of(def);
        RankRandomDef random = def.random;
        MobGear.fixed(mob, def.equipment, def.dropChance, "Mob rank " + def.key);
        MobGear.random(mob, RankScaling.chance(random.armor, scaling.armor, steps), RankScaling.chance(random.armorPiece, scaling.armor, steps), RankScaling.chance(random.weapon, scaling.weapon, steps), RankScaling.chance(random.offhand, scaling.weapon, steps), RankScaling.chance(random.enchant, scaling.enchant, steps), def.digs, random.exclude, def.dropChance);
        if (def.picksUpLoot != null) { mob.setCanPickUpLoot(def.picksUpLoot); }
        if (def.digs) { MobAbilities.armPick(mob, def.digItem, def.dropChance, "Mob rank " + def.key); }
    }

    @Nullable private static RankDef multiplying(Entity entity) {
        if (!(entity instanceof EntityLiving) || entity instanceof AbstractChestHorse) { return null; }
        RankDef def = DEFS.get(entity.getEntityData().getString(RANK));
        return def == null || def.loot == 1.0F ? null : def;
    }

    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (multiplying(entity) == null) { return; }
        List<ItemStack> held = new ArrayList<>();
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = ((EntityLiving) entity).getItemStackFromSlot(slot);
            if (!stack.isEmpty()) { held.add(stack.copy()); }
        }
        GEAR.put(entity, held);
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        List<ItemStack> held = GEAR.remove(entity);
        RankDef def = multiplying(entity);
        if (def == null) { return; }
        List<EntityItem> more = new ArrayList<>();
        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.getMaxStackSize() <= 1 || held != null && carried(held, stack)) { continue; }
            float wanted = stack.getCount() * def.loot;
            int count = MathUtil.roundRandom(wanted, entity.world.rand);
            int most = stack.getMaxStackSize();
            ItemStack first = stack.copy();
            first.setCount(Math.min(count, most));
            drop.setItem(first);
            for (int left = count - most; left > 0; left -= most) {
                ItemStack rest = stack.copy();
                rest.setCount(Math.min(left, most));
                EntityItem item = new EntityItem(entity.world, drop.posX, drop.posY, drop.posZ, rest);
                item.motionX = drop.motionX;
                item.motionY = drop.motionY;
                item.motionZ = drop.motionZ;
                item.setDefaultPickupDelay();
                more.add(item);
            }
        }
        event.getDrops().addAll(more);
    }

    private static boolean carried(List<ItemStack> held, ItemStack stack) {
        for (Iterator<ItemStack> each = held.iterator(); each.hasNext(); ) {
            if (ItemStack.areItemStacksEqual(each.next(), stack)) {
                each.remove();
                return true;
            }
        }
        return false;
    }
}
