package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.entities.behavior.MobGear;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobSwitchDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentMobSwitch {
    private static final String ENTITIES_KEY = "entities";
    private static final String HEALTH_KEY = "health";
    private static final String DAMAGE_KEY = "damage";
    private static final String SPEED_KEY = "speed";
    private static final String EQUIPMENT_KEY = "equipment";
    private static final List<String> KEYS = Arrays.asList(ENTITIES_KEY, HEALTH_KEY, DAMAGE_KEY, SPEED_KEY, EQUIPMENT_KEY);
    private static final List<String> SLOTS = Arrays.asList("mainhand", "offhand", "head", "chest", "legs", "feet");
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

    @Nullable public static MobSwitchDef parse(String owner, String name, @Nullable JsonElement element) {
        if (element == null) { return null; }
        JsonObject json = Json.object(owner, "mobs", element, "an object of entities, health, damage, speed and equipment keys");
        if (json == null) { return null; }
        List<String> entities = Collections.singletonList(MobStats.MONSTER);
        float health = 1.0F;
        float damage = 1.0F;
        float speed = 1.0F;
        Map<EntityEquipmentSlot, MobSwitchDef.Gear> equipment = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            if (!KEYS.contains(key)) {
                ContentLog.LOGGER.error("{} gives mobs.{}, which is not a mobs key, ignoring it", owner, key);
                continue;
            }
            if (ENTITIES_KEY.equals(key)) {
                List<String> found = MobStats.entities(owner, "mobs.entities", entry.getValue());
                if (found != null) { entities = found; }
            }
            else if (EQUIPMENT_KEY.equals(key)) { equip(owner, entry.getValue(), equipment); }
            else {
                Float value = Json.number(owner, "mobs." + key, entry.getValue(), false);
                if (value == null) { continue; }
                if (HEALTH_KEY.equals(key)) {
                    if (value > 0.0F) { health = value; }
                    else { ContentLog.LOGGER.error("{} gives mobs.health as {}, which is not above 0, ignoring it", owner, value); }
                }
                else if (DAMAGE_KEY.equals(key)) { damage = value; }
                else { speed = value; }
            }
        }
        return new MobSwitchDef(owner, name, entities, health, damage, speed, equipment);
    }

    private static void equip(String owner, JsonElement value, Map<EntityEquipmentSlot, MobSwitchDef.Gear> into) {
        JsonObject json = Json.object(owner, "mobs.equipment", value, "an object of slot to item");
        if (json == null) { return; }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String slot = entry.getKey();
            if (!SLOTS.contains(slot)) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{}, which is not one of mainhand, offhand, head, chest, legs or feet, ignoring it", owner, slot);
                continue;
            }
            EntityEquipmentSlot resolved = EntityEquipmentSlot.fromString(slot);
            JsonObject gear = entry.getValue().isJsonObject() ? entry.getValue().getAsJsonObject() : null;
            if (gear != null) {
                for (Map.Entry<String, JsonElement> field : gear.entrySet()) {
                    if (!ITEM.equals(field.getKey()) && !"dropChance".equals(field.getKey())) { ContentLog.LOGGER.error("{} gives mobs.equipment.{}.{}, which is not an equipment key, ignoring it", owner, slot, field.getKey()); }
                }
            }
            String item = gear == null || !gear.has(ITEM) ? null : Json.text(gear.get(ITEM));
            if (item == null) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{} without an item id, ignoring it", owner, slot);
                continue;
            }
            float chance = VANILLA_CHANCE;
            JsonElement given = gear.get("dropChance");
            if (given != null) {
                Float number = Json.number(owner, "mobs.equipment." + slot + ".dropChance", given, false);
                if (number == null) { continue; }
                if (number > 1.0F) {
                    ContentLog.LOGGER.error("{} gives mobs.equipment.{}.dropChance as {}, which is above 1, ignoring it", owner, slot, number);
                    continue;
                }
                chance = number;
            }
            into.put(resolved, new MobSwitchDef.Gear(Settings.lower(item), chance));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onJoin(EntityJoinWorldEvent event) {
        if (ready && !event.getWorld().isRemote && event.getEntity() instanceof EntityLiving) { settle((EntityLiving) event.getEntity()); }
    }

    private static void settle(EntityLiving mob) {
        if (!mob.isEntityAlive()) { return; }
        MobSwitchDef def = ContentEnvironments.mobs(mob.world, mob.getPosition());
        String wanted = def != MobSwitchDef.NONE && MobStats.targets(def.entities, mob) ? mob.world.provider.getDimension() + "/" + def.name : "";
        NBTTagCompound data = mob.getEntityData();
        String held = data.getCompoundTag(RECORD).getString(SET);
        if (held.equals(wanted)) { return; }
        if (!held.isEmpty()) { strip(mob, data); }
        if (!wanted.isEmpty()) { apply(mob, data, def, wanted); }
    }

    private static void apply(EntityLiving mob, NBTTagCompound data, MobSwitchDef def, String wanted) {
        if (def.health != 1.0F) {
            MobStats.scale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH, def.health, NAME);
            mob.setHealth(mob.getHealth() * def.health);
        }
        MobStats.scale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE, def.damage, NAME);
        MobStats.scale(mob, SharedMonsterAttributes.MOVEMENT_SPEED, SPEED, def.speed, NAME);
        NBTTagCompound gear = new NBTTagCompound();
        for (Map.Entry<EntityEquipmentSlot, MobSwitchDef.Gear> entry : def.equipment.entrySet()) {
            Item item = Registries.itemNamed(entry.getValue().item);
            EntityEquipmentSlot slot = entry.getKey();
            if (item == null || !mob.getItemStackFromSlot(slot).isEmpty()) { continue; }
            NBTTagCompound held = new NBTTagCompound();
            held.setString(ITEM, entry.getValue().item);
            held.setFloat(CHANCE, MobGear.chances(mob, slot)[slot.getIndex()]);
            gear.setTag(slot.getName(), held);
            mob.setItemStackToSlot(slot, new ItemStack(item));
            MobGear.chances(mob, slot)[slot.getIndex()] = entry.getValue().chance;
        }
        NBTTagCompound record = new NBTTagCompound();
        record.setTag(GEAR, gear);
        record.setString(SET, wanted);
        data.setTag(RECORD, record);
    }

    private static void strip(EntityLiving mob, NBTTagCompound data) {
        IAttributeInstance health = MobStats.attribute(mob, SharedMonsterAttributes.MAX_HEALTH);
        AttributeModifier held = health == null ? null : health.getModifier(HEALTH);
        float current = mob.getHealth();
        unscale(mob, SharedMonsterAttributes.MAX_HEALTH, HEALTH);
        unscale(mob, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE);
        unscale(mob, SharedMonsterAttributes.MOVEMENT_SPEED, SPEED);
        if (held != null) { mob.setHealth((float) (current / (1.0D + held.getAmount()))); }
        NBTTagCompound gear = data.getCompoundTag(RECORD).getCompoundTag(GEAR);
        for (String name : gear.getKeySet()) {
            NBTTagCompound recorded = gear.getCompoundTag(name);
            Item item = Registries.itemNamed(recorded.getString(ITEM));
            EntityEquipmentSlot slot = EntityEquipmentSlot.fromString(name);
            if (item == null || mob.getItemStackFromSlot(slot).getItem() != item) { continue; }
            mob.setItemStackToSlot(slot, ItemStack.EMPTY);
            MobGear.chances(mob, slot)[slot.getIndex()] = recorded.getFloat(CHANCE);
        }
        data.removeTag(RECORD);
    }

    private static void unscale(EntityLiving mob, IAttribute attribute, UUID id) {
        IAttributeInstance instance = MobStats.attribute(mob, attribute);
        if (instance != null) { instance.removeModifier(id); }
    }

    public static void settleAll(World world) {
        for (Object entity : new ArrayList<>(world.loadedEntityList)) {
            if (entity instanceof EntityLiving) { settle((EntityLiving) entity); }
        }
    }

    public static void serverStarted(MinecraftServer server) {
        for (MobSwitchDef def : ContentEnvironments.allMobs()) {
            for (String id : def.entities) {
                if (MobStats.unknownEntity(id)) { ContentLog.LOGGER.error("{} gives mobs.entities for '{}', which is not an entity, so it changes nothing", def.owner, id); }
            }
            for (MobSwitchDef.Gear gear : def.equipment.values()) {
                if (Registries.itemNamed(gear.item) == null) { ContentLog.LOGGER.error("{} gives mobs.equipment for '{}', which is not an item, so it gives nothing", def.owner, gear.item); }
            }
        }
        ready = true;
        for (WorldServer world : server.worlds) { settleAll(world); }
    }

    public static void serverStopped() { ready = false; }
}
