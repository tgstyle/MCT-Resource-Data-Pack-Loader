package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentParserEntities;
import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.RankDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WeightedPicks;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentRanks {
    private static final List<String> KEYS = Arrays.asList("entities", "weight", "name", "health", "damage", "effects", "loot", "dimensions");
    private static final String RANK = "rdplRank";
    private static final String NAME = "RDPL rank";
    private static final UUID HEALTH = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a21");
    private static final UUID DAMAGE = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a22");
    private static final Map<String, RankDef> DEFS = new LinkedHashMap<>();
    private static final Map<Entity, List<ItemStack>> GEAR = new MapMaker().weakKeys().makeMap();

    private ContentRanks() {}

    public static boolean load() {
        DEFS.clear();
        Json.eachFile(PackManager.RANKS, "rank definition", ContentRanks::read);
        return !DEFS.isEmpty();
    }

    public static String rankOf(Entity entity) { return entity.getEntityData().getString(RANK); }

    public static String killKey(String namespace, String entry) {
        int at = entry.indexOf('@');
        return at < 0 ? entry : entry.substring(0, at) + "@" + key(namespace, entry.substring(at + 1).trim());
    }

    public static String key(String namespace, String rank) { return rank.isEmpty() || rank.indexOf(':') >= 0 ? rank : namespace + ":" + rank; }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "rank definition");
        if (json == null) { return; }
        RankDef def = parse(key, json);
        if (def != null) { DEFS.put(key.toString(), def); }
    }

    @Nullable private static RankDef parse(ResourceLocation key, JsonObject json) {
        String owner = "Rank " + key;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                ContentLog.LOGGER.error("{} gives {}, which is not a rank key, ignoring the rank", owner, entry.getKey());
                return null;
            }
        }
        List<String> entities = Collections.singletonList(MobStats.MONSTER);
        if (json.has("entities")) {
            entities = MobStats.entities(owner, "entities", json.get("entities"));
            if (entities == null || entities.size() != json.get("entities").getAsJsonArray().size()) {
                ContentLog.LOGGER.error("{} gives entities that are not all entity ids and kinds, ignoring the rank", owner);
                return null;
            }
        }
        Float weight = number(owner, json, "weight", 1.0F, true, "a whole number of 1 or more");
        Float health = number(owner, json, "health", Float.MIN_VALUE, false, "a number above 0");
        Float damage = number(owner, json, "damage", 0.0F, false, "a number of 0 or more");
        Float loot = number(owner, json, "loot", 1.0F, false, "a number of 1 or more");
        if (weight == null || health == null || damage == null || loot == null) { return null; }
        String label = "";
        if (json.has("name")) {
            JsonElement name = json.get("name");
            if (!name.isJsonPrimitive() || !name.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives name as {}, which is not text, ignoring the rank", owner, name);
                return null;
            }
            label = name.getAsString();
        }
        Map<String, Integer> effects = ContentParserEntities.effects(key, json);
        if (json.has("effects") && effects.size() != json.get("effects").getAsJsonArray().size()) {
            ContentLog.LOGGER.error("{} gives effects that are not all objects with a different potion each, ignoring the rank", owner);
            return null;
        }
        return new RankDef(key, entities, weight.intValue(), label, health, damage, effects, loot, Json.strings(json, "dimensions"));
    }

    @Nullable private static Float number(String owner, JsonObject json, String key, float low, boolean whole, String need) {
        if (!json.has(key)) { return 1.0F; }
        JsonElement value = json.get(key);
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            float number = value.getAsFloat();
            if (number >= low && number < Float.POSITIVE_INFINITY && (!whole || number == Math.floor(number))) { return number; }
        }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not {}, ignoring the rank", owner, key, value, need);
        return null;
    }

    public static void verify() {
        DEFS.values().removeIf(ContentRanks::unregistered);
        if (!DEFS.isEmpty()) { Summary.info("ranks", "Loaded " + DEFS.size() + " mob rank(s)"); }
    }

    private static boolean unregistered(RankDef def) {
        boolean missing = false;
        for (String id : def.entities) {
            if (!id.startsWith("#") && !EntityList.isRegistered(new ResourceLocation(id))) {
                ContentLog.LOGGER.error("Rank {} names entity '{}', which is not an entity, ignoring the rank", def.key, id);
                missing = true;
            }
        }
        for (String potion : def.effects.keySet()) {
            if (Registries.find(ForgeRegistries.POTIONS, new ResourceLocation(potion)) == null) {
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
        if (data.hasKey(RANK)) { return; }
        List<RankDef> matches = new ArrayList<>();
        for (RankDef def : DEFS.values()) {
            if (MobStats.targets(def.entities, mob) && (def.dimensions.isEmpty() || CardPlace.inAny(def.dimensions, mob.world.provider.getDimension()))) { matches.add(def); }
        }
        RankDef def = WeightedPicks.pick(matches, rank -> rank.weight, mob.world.rand);
        if (def == null) { return; }
        data.setString(RANK, def.key.toString());
        MobStats.scale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH, def.health, NAME);
        if (def.health != 1.0F) { mob.setHealth(mob.getHealth() * def.health); }
        MobStats.scale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE, def.damage, NAME);
        ContentEntityApply.effects(mob, def.effects, def.key);
        if (!def.label.isEmpty() && !mob.hasCustomName()) { mob.setCustomNameTag(def.label.replace("%s", mob.getName())); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} rolled for {} at {}", def.key, EntityList.getKey(mob), mob.getPosition()); }
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
            int count = (int) wanted;
            if (entity.world.rand.nextFloat() < wanted - count) { count++; }
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
