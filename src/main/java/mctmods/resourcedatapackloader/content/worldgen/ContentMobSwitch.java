package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.MobSwitchDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLiving;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentMobSwitch {
    private static final List<String> KEYS = Arrays.asList("entities", "health", "damage", "speed", "equipment");
    private static final List<String> SLOTS = Arrays.asList("mainhand", "offhand", "head", "chest", "legs", "feet");
    private static final List<String> KINDS = Arrays.asList("#monster", "#creature", "#ambient", "#water_creature");
    private static final String MONSTER = "#monster";
    private static final String RECORD = "rdplMobSwitch";
    private static final String SET = "set";
    private static final String GEAR = "gear";
    private static final String ITEM = "item";
    private static final String CHANCE = "chance";
    private static final String NAME = "RDPL environment";
    private static final float VANILLA_CHANCE = 0.085F;
    private static final UUID HEALTH = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a11");
    private static final UUID DAMAGE = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a12");
    private static final UUID SPEED = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a13");
    private static boolean ready;

    private ContentMobSwitch() {}

    @Nullable static MobSwitchDef parse(String owner, String name, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs that is not an object of entities, health, damage, speed and equipment keys, ignoring it", owner);
            return null;
        }
        List<String> entities = Collections.singletonList(MONSTER);
        float health = 1.0F;
        float damage = 1.0F;
        float speed = 1.0F;
        Map<String, MobSwitchDef.Gear> equipment = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            if (!KEYS.contains(key)) {
                ContentLog.LOGGER.error("{} gives mobs.{}, which is not a mobs key, ignoring it", owner, key);
                continue;
            }
            if ("entities".equals(key)) {
                List<String> found = entities(owner, entry.getValue());
                if (found != null) { entities = found; }
            }
            else if ("equipment".equals(key)) { equip(owner, entry.getValue(), equipment); }
            else {
                Float value = ContentSpawnSwitch.number(owner, "mobs." + key, entry.getValue(), false);
                if (value == null) { continue; }
                if ("health".equals(key)) {
                    if (value > 0.0F) { health = value; }
                    else { ContentLog.LOGGER.error("{} gives mobs.health as {}, which is not above 0, ignoring it", owner, value); }
                }
                else if ("damage".equals(key)) { damage = value; }
                else { speed = value; }
            }
        }
        return new MobSwitchDef(owner, name, entities, health, damage, speed, equipment);
    }

    @Nullable private static List<String> entities(String owner, JsonElement value) {
        if (!value.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives mobs.entities that is not a list of entity ids and kinds, ignoring it", owner);
            return null;
        }
        List<String> found = new ArrayList<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives mobs.entities an entry {} that is not text, ignoring it", owner, entry);
                continue;
            }
            String id = Settings.lower(entry.getAsString());
            if (id.startsWith("#")) {
                if (KINDS.contains(id)) { found.add(id); }
                else { ContentLog.LOGGER.error("{} gives mobs.entities the kind '{}', which is not one of #monster, #creature, #ambient or #water_creature, ignoring it", owner, id); }
            }
            else { found.add(id.indexOf(':') < 0 ? "minecraft:" + id : id); }
        }
        return found;
    }

    private static void equip(String owner, JsonElement value, Map<String, MobSwitchDef.Gear> into) {
        if (!value.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs.equipment that is not an object of slot to item, ignoring it", owner);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
            String slot = entry.getKey();
            if (!SLOTS.contains(slot)) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{}, which is not one of mainhand, offhand, head, chest, legs or feet, ignoring it", owner, slot);
                continue;
            }
            JsonObject gear = entry.getValue().isJsonObject() ? entry.getValue().getAsJsonObject() : null;
            if (gear != null) {
                for (Map.Entry<String, JsonElement> field : gear.entrySet()) {
                    if (!ITEM.equals(field.getKey()) && !"dropChance".equals(field.getKey())) {
                        ContentLog.LOGGER.error("{} gives mobs.equipment.{}.{}, which is not an equipment key, ignoring it", owner, slot, field.getKey());
                    }
                }
            }
            JsonElement item = gear == null ? null : gear.get(ITEM);
            if (item == null || !item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{} without an item id, ignoring it", owner, slot);
                continue;
            }
            float chance = VANILLA_CHANCE;
            JsonElement given = gear.get("dropChance");
            if (given != null) {
                Float number = ContentSpawnSwitch.number(owner, "mobs.equipment." + slot + ".dropChance", given, false);
                if (number == null) { continue; }
                if (number > 1.0F) {
                    ContentLog.LOGGER.error("{} gives mobs.equipment.{}.dropChance as {}, which is above 1, ignoring it", owner, slot, number);
                    continue;
                }
                chance = number;
            }
            into.put(slot, new MobSwitchDef.Gear(Settings.lower(item.getAsString()), chance));
        }
    }

    private static boolean targets(MobSwitchDef def, EntityLiving mob) {
        ResourceLocation key = EntityList.getKey(mob);
        String id = key == null ? "" : key.toString();
        for (String entity : def.entities) {
            if (entity.startsWith("#")) {
                if (mob.isCreatureType(EnumCreatureType.valueOf(entity.substring(1).toUpperCase(Locale.ROOT)), false)) { return true; }
            }
            else if (entity.equals(id)) { return true; }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onJoin(EntityJoinWorldEvent event) {
        if (ready && !event.getWorld().isRemote && event.getEntity() instanceof EntityLiving) { settle((EntityLiving) event.getEntity()); }
    }

    private static void settle(EntityLiving mob) {
        if (!mob.isEntityAlive()) { return; }
        MobSwitchDef def = ContentEnvironments.mobs(mob.world, mob.getPosition());
        String wanted = def != MobSwitchDef.NONE && targets(def, mob) ? mob.world.provider.getDimension() + "/" + def.name : "";
        NBTTagCompound data = mob.getEntityData();
        String held = data.getCompoundTag(RECORD).getString(SET);
        if (held.equals(wanted)) { return; }
        if (!held.isEmpty()) { strip(mob, data); }
        if (!wanted.isEmpty()) { apply(mob, data, def, wanted); }
    }

    private static void apply(EntityLiving mob, NBTTagCompound data, MobSwitchDef def, String wanted) {
        if (def.health != 1.0F) {
            scale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH, def.health);
            mob.setHealth(mob.getHealth() * def.health);
        }
        scale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE, def.damage);
        scale(mob, SharedMonsterAttributes.MOVEMENT_SPEED, SPEED, def.speed);
        NBTTagCompound gear = new NBTTagCompound();
        for (Map.Entry<String, MobSwitchDef.Gear> entry : def.equipment.entrySet()) {
            Item item = Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(entry.getValue().item));
            EntityEquipmentSlot slot = EntityEquipmentSlot.fromString(entry.getKey());
            if (item == null || !mob.getItemStackFromSlot(slot).isEmpty()) { continue; }
            NBTTagCompound held = new NBTTagCompound();
            held.setString(ITEM, entry.getValue().item);
            held.setFloat(CHANCE, chances(mob, slot)[slot.getIndex()]);
            gear.setTag(entry.getKey(), held);
            mob.setItemStackToSlot(slot, new ItemStack(item));
            chances(mob, slot)[slot.getIndex()] = entry.getValue().chance;
        }
        NBTTagCompound record = new NBTTagCompound();
        record.setTag(GEAR, gear);
        record.setString(SET, wanted);
        data.setTag(RECORD, record);
    }

    private static void strip(EntityLiving mob, NBTTagCompound data) {
        IAttributeInstance health = attribute(mob, SharedMonsterAttributes.MAX_HEALTH);
        AttributeModifier held = health == null ? null : health.getModifier(HEALTH);
        float current = mob.getHealth();
        unscale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH);
        unscale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE);
        unscale(mob, SharedMonsterAttributes.MOVEMENT_SPEED, SPEED);
        if (held != null) { mob.setHealth((float) (current / (1.0D + held.getAmount()))); }
        NBTTagCompound gear = data.getCompoundTag(RECORD).getCompoundTag(GEAR);
        for (String name : gear.getKeySet()) {
            NBTTagCompound recorded = gear.getCompoundTag(name);
            Item item = Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(recorded.getString(ITEM)));
            EntityEquipmentSlot slot = EntityEquipmentSlot.fromString(name);
            if (item == null || mob.getItemStackFromSlot(slot).getItem() != item) { continue; }
            mob.setItemStackToSlot(slot, ItemStack.EMPTY);
            chances(mob, slot)[slot.getIndex()] = recorded.getFloat(CHANCE);
        }
        data.removeTag(RECORD);
    }

    private static void scale(EntityLiving mob, IAttribute attribute, UUID id, float multiplier) {
        IAttributeInstance instance = attribute(mob, attribute);
        if (instance == null || multiplier == 1.0F) { return; }
        if (instance.getModifier(id) != null) { instance.removeModifier(id); }
        instance.applyModifier(new AttributeModifier(id, NAME, multiplier - 1.0D, 2));
    }

    private static void unscale(EntityLiving mob, IAttribute attribute, UUID id) {
        IAttributeInstance instance = attribute(mob, attribute);
        if (instance != null) { instance.removeModifier(id); }
    }

    private static IAttributeInstance attribute(EntityLiving mob, IAttribute attribute) { return mob.getAttributeMap().getAttributeInstanceByName(attribute.getName()); }

    private static float[] chances(EntityLiving mob, EntityEquipmentSlot slot) {
        IEntityLiving access = (IEntityLiving) mob;
        return slot.getSlotType() == EntityEquipmentSlot.Type.HAND ? access.rdpl$handDropChances() : access.rdpl$armorDropChances();
    }

    static void settleAll(World world) {
        for (Object entity : new ArrayList<>(world.loadedEntityList)) {
            if (entity instanceof EntityLiving) { settle((EntityLiving) entity); }
        }
    }

    public static void serverStarted(MinecraftServer server) {
        for (MobSwitchDef def : ContentEnvironments.allMobs()) {
            for (String id : def.entities) {
                if (!id.startsWith("#") && !EntityList.isRegistered(new ResourceLocation(id))) { ContentLog.LOGGER.error("{} gives mobs.entities for '{}', which is not an entity, so it changes nothing", def.owner, id); }
            }
            for (MobSwitchDef.Gear gear : def.equipment.values()) {
                if (Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(gear.item)) == null) { ContentLog.LOGGER.error("{} gives mobs.equipment for '{}', which is not an item, so it gives nothing", def.owner, gear.item); }
            }
        }
        ready = true;
        for (WorldServer world : server.worlds) { settleAll(world); }
    }

    public static void serverStopped() { ready = false; }
}
