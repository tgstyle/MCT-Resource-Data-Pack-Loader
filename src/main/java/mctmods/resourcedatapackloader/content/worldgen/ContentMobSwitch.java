package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MobSwitchDef;
import mctmods.resourcedatapackloader.content.entity.MobStats;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentMobSwitch {
    private static final List<String> KEYS = List.of("entities", "health", "damage", "speed", "equipment");
    private static final List<String> SLOTS = List.of("mainhand", "offhand", "head", "chest", "legs", "feet");
    private static final String RECORD = "rdplMobSwitch";
    private static final String SET = "set";
    private static final String GEAR = "gear";
    private static final String ITEM = "item";
    private static final String CHANCE = "chance";
    private static final float VANILLA_CHANCE = 0.085F;
    private static final Identifier HEALTH = ResourceDataPackLoader.id("environment_health");
    private static final Identifier DAMAGE = ResourceDataPackLoader.id("environment_damage");
    private static final Identifier SPEED = ResourceDataPackLoader.id("environment_speed");
    private static boolean ready;

    private ContentMobSwitch() {}

    @Nullable static MobSwitchDef parse(String owner, String name, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs that is not an object, ignoring it", owner);
            return null;
        }
        MobSwitchDef def = new MobSwitchDef(owner, name);
        boolean named = false;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!KEYS.contains(key)) {
                ContentLog.LOGGER.error("{} gives mobs.{}, which is not a mobs key, ignoring it", owner, key);
                continue;
            }
            Double number = "entities".equals(key) || "equipment".equals(key) ? null : ContentSpawnSwitch.number(value);
            switch (key) {
                case "entities" -> named = MobStats.entities(owner, "mobs.entities", value, def.ids, def.kinds);
                case "equipment" -> equipment(owner, def, value);
                case "health" -> {
                    if (number == null || number <= 0.0D) { ContentLog.LOGGER.error("{} gives mobs.health that is not a number above 0, ignoring it", owner); }
                    else { def.health = number.floatValue(); }
                }
                case "damage" -> {
                    if (number == null || number < 0.0D) { ContentLog.LOGGER.error("{} gives mobs.damage that is not a number of 0 or more, ignoring it", owner); }
                    else { def.damage = number.floatValue(); }
                }
                default -> {
                    if (number == null || number < 0.0D) { ContentLog.LOGGER.error("{} gives mobs.speed that is not a number of 0 or more, ignoring it", owner); }
                    else { def.speed = number.floatValue(); }
                }
            }
        }
        if (!named) { def.kinds.add(MobStats.MONSTER); }
        return def;
    }

    private static void equipment(String owner, MobSwitchDef def, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs.equipment that is not an object of slot to item, ignoring it", owner);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String slot = entry.getKey();
            EquipmentSlot found = SLOTS.contains(slot) ? Enums.byName(EquipmentSlot.class, slot) : null;
            if (found == null) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{}, which is not one of mainhand, offhand, head, chest, legs or feet, ignoring it", owner, entry.getKey());
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{} that is not an object with an item, ignoring it", owner, slot);
                continue;
            }
            JsonObject gear = entry.getValue().getAsJsonObject();
            for (String key : gear.keySet()) {
                if (!ITEM.equals(key) && !"dropChance".equals(key)) { ContentLog.LOGGER.error("{} gives mobs.equipment.{}.{}, which is not an equipment key, ignoring it", owner, slot, key); }
            }
            JsonElement item = gear.get(ITEM);
            Identifier id = item == null || !item.isJsonPrimitive() ? null : Identifier.tryParse(Settings.lower(item.getAsString()));
            if (id == null) {
                ContentLog.LOGGER.error("{} gives mobs.equipment.{} without an item id, ignoring it", owner, slot);
                continue;
            }
            float chance = VANILLA_CHANCE;
            if (gear.has("dropChance")) {
                Double number = ContentSpawnSwitch.number(gear.get("dropChance"));
                if (number == null || number < 0.0D || number > 1.0D) {
                    ContentLog.LOGGER.error("{} gives mobs.equipment.{}.dropChance that is not a number from 0 to 1, ignoring it", owner, slot);
                    continue;
                }
                chance = number.floatValue();
            }
            def.equipment.put(found, new MobSwitchDef.Gear(id, chance));
        }
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (ready && event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Mob mob) { settle(mob); }
    }

    private static void settle(Mob mob) {
        if (!ready || !mob.isAlive() || !(mob.level() instanceof ServerLevel level)) { return; }
        MobSwitchDef def = ContentEnvironments.mobs(level, mob.blockPosition());
        String wanted = MobStats.targets(def.ids, def.kinds, mob) ? level.dimension().identifier() + "/" + def.name : "";
        String held = mob.getPersistentData().getCompoundOrEmpty(RECORD).getStringOr(SET, "");
        if (held.equals(wanted)) { return; }
        if (!held.isEmpty()) { strip(mob); }
        if (!wanted.isEmpty()) { apply(mob, def, wanted); }
    }

    private static void apply(Mob mob, MobSwitchDef def, String wanted) {
        float before = mob.getHealth();
        if (MobStats.scale(mob, Attributes.MAX_HEALTH, HEALTH, def.health)) { mob.setHealth(before * def.health); }
        MobStats.scale(mob, Attributes.ATTACK_DAMAGE, DAMAGE, def.damage);
        MobStats.scale(mob, Attributes.MOVEMENT_SPEED, SPEED, def.speed);
        CompoundTag gear = new CompoundTag();
        for (Map.Entry<EquipmentSlot, MobSwitchDef.Gear> entry : def.equipment.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            Item item = ContentStacks.item(entry.getValue().item());
            if (item == null || !mob.getItemBySlot(slot).isEmpty()) { continue; }
            CompoundTag given = new CompoundTag();
            given.putString(ITEM, entry.getValue().item().toString());
            given.putFloat(CHANCE, mob.getDropChances().byEquipment(slot));
            gear.put(slot.getName(), given);
            mob.setItemSlot(slot, new ItemStack(item));
            mob.setDropChance(slot, entry.getValue().chance());
        }
        CompoundTag record = new CompoundTag();
        record.putString(SET, wanted);
        record.put(GEAR, gear);
        mob.getPersistentData().put(RECORD, record);
    }

    private static void strip(Mob mob) {
        AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
        AttributeModifier held = health == null ? null : health.getModifier(HEALTH);
        float before = mob.getHealth();
        if (health != null) { health.removeModifier(HEALTH); }
        AttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) { damage.removeModifier(DAMAGE); }
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) { speed.removeModifier(SPEED); }
        if (held != null) { mob.setHealth(before / (1.0F + (float) held.amount())); }
        CompoundTag gear = mob.getPersistentData().getCompoundOrEmpty(RECORD).getCompoundOrEmpty(GEAR);
        for (String name : gear.keySet()) {
            EquipmentSlot slot = Enums.byName(EquipmentSlot.class, name);
            CompoundTag given = gear.getCompoundOrEmpty(name);
            Item item = ContentStacks.item(Identifier.tryParse(given.getStringOr(ITEM, "")));
            if (slot == null || item == null || !mob.getItemBySlot(slot).is(item)) { continue; }
            mob.setItemSlot(slot, ItemStack.EMPTY);
            mob.setDropChance(slot, given.getFloatOr(CHANCE, VANILLA_CHANCE));
        }
        mob.getPersistentData().remove(RECORD);
    }

    static void settleAll(ServerLevel level) {
        List<Entity> loaded = new ArrayList<>();
        level.getAllEntities().forEach(loaded::add);
        for (Entity entity : loaded) {
            if (entity instanceof Mob mob) { settle(mob); }
        }
    }

    static void start(MinecraftServer server, Collection<MobSwitchDef> defs) {
        for (MobSwitchDef def : defs) {
            for (Identifier id : def.ids) {
                if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) { ContentLog.LOGGER.error("{} changes the entity '{}', which does not exist", def.owner, id); }
            }
            for (MobSwitchDef.Gear gear : def.equipment.values()) {
                if (!ContentStacks.registered(gear.item())) { ContentLog.LOGGER.error("{} gives the item '{}', which does not exist", def.owner, gear.item()); }
            }
        }
        ready = true;
        for (ServerLevel level : server.getAllLevels()) { settleAll(level); }
    }

    static void forget() { ready = false; }
}
