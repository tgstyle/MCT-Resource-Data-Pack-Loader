package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MobSwitchDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMob;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentMobSwitch {
    private static final List<String> KEYS = List.of("entities", "health", "damage", "speed", "equipment");
    private static final List<String> SLOTS = List.of("mainhand", "offhand", "head", "chest", "legs", "feet");
    private static final List<String> KINDS = List.of("#monster", "#creature", "#ambient", "#water_creature");
    private static final String MONSTER = "#monster";
    private static final String ITEM = "item";
    private static final String DROP_CHANCE = "dropChance";
    private static final String RECORD = "rdplMobSwitch";
    private static final String SET = "set";
    private static final String GEAR = "gear";
    private static final String CHANCE = "chance";
    private static final float VANILLA_DROP = 0.085F;
    private static final String NAME = "RDPL environment";
    private static final UUID HEALTH = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a11");
    private static final UUID DAMAGE = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a12");
    private static final UUID SPEED = UUID.fromString("6f1a3c52-9b0e-4d7a-8a41-2e5c0d9b7a13");
    private static boolean ready;

    private ContentMobSwitch() {}

    @Nullable static MobSwitchDef parse(String owner, String name, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs that is not an object, ignoring it", owner);
            return null;
        }
        List<String> entities = List.of(MONSTER);
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
                List<String> listed = entities(owner, entry.getValue());
                if (listed != null) { entities = listed; }
            }
            else if ("equipment".equals(key)) { equipment(owner, entry.getValue(), equipment); }
            else {
                float amount = ContentSpawnSwitch.amount(owner, "mobs." + key, entry.getValue());
                if (amount == 0.0F && "health".equals(key)) { ContentLog.LOGGER.error("{} gives mobs.health as 0, which is not above 0, ignoring it", owner); }
                else if (amount >= 0.0F) {
                    if ("health".equals(key)) { health = amount; }
                    else if ("damage".equals(key)) { damage = amount; }
                    else { speed = amount; }
                }
            }
        }
        return new MobSwitchDef(owner, name, List.copyOf(entities), health, damage, speed, equipment);
    }

    @Nullable private static List<String> entities(String owner, JsonElement element) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives mobs.entities that is not a list of entity ids and kinds, ignoring it", owner);
            return null;
        }
        List<String> listed = new ArrayList<>();
        JsonArray array = element.getAsJsonArray();
        for (JsonElement item : array) {
            if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives a mobs.entities entry that is not text, ignoring it", owner);
                continue;
            }
            String id = Settings.lower(item.getAsString());
            if (id.startsWith("#")) {
                if (KINDS.contains(id)) { listed.add(id); }
                else { ContentLog.LOGGER.error("{} gives mobs.entities '{}', which is not a kind, ignoring it", owner, id); }
            }
            else { listed.add(id.indexOf(':') < 0 ? "minecraft:" + id : id); }
        }
        return listed;
    }

    private static void equipment(String owner, JsonElement element, Map<String, MobSwitchDef.Gear> into) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives mobs.equipment that is not an object of slots, ignoring it", owner);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String slot = entry.getKey();
            String where = "mobs.equipment." + entry.getKey();
            if (!SLOTS.contains(slot)) {
                ContentLog.LOGGER.error("{} gives {}, which is not a slot, ignoring it", owner, where);
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("{} gives {} that is not an object, ignoring it", owner, where);
                continue;
            }
            MobSwitchDef.Gear gear = gear(owner, where, entry.getValue().getAsJsonObject());
            if (gear != null) { into.put(slot, gear); }
        }
    }

    @Nullable private static MobSwitchDef.Gear gear(String owner, String where, JsonObject json) {
        String item = null;
        float chance = VANILLA_DROP;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (ITEM.equals(entry.getKey())) {
                JsonElement value = entry.getValue();
                String id = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? Settings.lower(value.getAsString()) : "";
                if (id.isEmpty()) { ContentLog.LOGGER.error("{} gives {}.item that is not an item id, ignoring it", owner, where); }
                else { item = id.indexOf(':') < 0 ? "minecraft:" + id : id; }
            }
            else if (DROP_CHANCE.equals(entry.getKey())) {
                float amount = ContentSpawnSwitch.amount(owner, where + "." + DROP_CHANCE, entry.getValue());
                if (amount > 1.0F) {
                    ContentLog.LOGGER.error("{} gives {}.{} as {}, which is above 1, ignoring it", owner, where, DROP_CHANCE, amount);
                    return null;
                }
                else if (amount >= 0.0F) { chance = amount; }
                else { return null; }
            }
            else { ContentLog.LOGGER.error("{} gives {}.{}, which is not an equipment key, ignoring it", owner, where, entry.getKey()); }
        }
        if (item == null) {
            ContentLog.LOGGER.error("{} gives {} with no item, ignoring it", owner, where);
            return null;
        }
        return new MobSwitchDef.Gear(item, chance);
    }

    private static boolean targets(MobSwitchDef def, Mob mob) {
        if (def.entities().isEmpty()) { return false; }
        String id = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()));
        String kind = "#" + mob.getType().getCategory().getName();
        return def.entities().contains(id) || def.entities().contains(kind);
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (ready && event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof Mob mob) { settle(mob, level); }
    }

    private static void settle(Mob mob, ServerLevel level) {
        if (!mob.isAlive()) { return; }
        MobSwitchDef def = ContentEnvironments.mobs(level, mob.blockPosition());
        String wanted = targets(def, mob) ? level.dimension().location() + "/" + def.name() : "";
        CompoundTag record = mob.getPersistentData().getCompound(RECORD);
        String held = record.getString(SET);
        if (held.equals(wanted)) { return; }
        if (!held.isEmpty()) { strip(mob, record); }
        if (!wanted.isEmpty()) { apply(mob, def, wanted); }
    }

    private static void apply(Mob mob, MobSwitchDef def, String wanted) {
        CompoundTag record = new CompoundTag();
        record.putString(SET, wanted);
        if (scale(mob, Attributes.MAX_HEALTH, HEALTH, def.health())) { mob.setHealth(mob.getHealth() * def.health()); }
        scale(mob, Attributes.ATTACK_DAMAGE, DAMAGE, def.damage());
        scale(mob, Attributes.MOVEMENT_SPEED, SPEED, def.speed());
        CompoundTag gear = new CompoundTag();
        for (Map.Entry<String, MobSwitchDef.Gear> entry : def.equipment().entrySet()) {
            EquipmentSlot slot = Enums.byName(EquipmentSlot.class, entry.getKey());
            Item item = ContentStacks.item(ResourceLocation.tryParse(entry.getValue().item()));
            if (slot == null || item == null || !mob.getItemBySlot(slot).isEmpty()) { continue; }
            CompoundTag given = new CompoundTag();
            given.putString(ITEM, entry.getValue().item());
            given.putFloat(CHANCE, ((IMob) mob).rdpl$dropChance(slot));
            gear.put(entry.getKey(), given);
            mob.setItemSlot(slot, new ItemStack(item));
            mob.setDropChance(slot, entry.getValue().chance());
        }
        record.put(GEAR, gear);
        mob.getPersistentData().put(RECORD, record);
    }

    private static boolean scale(Mob mob, Attribute attribute, UUID id, float multiplier) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance == null || multiplier == 1.0F) { return false; }
        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, NAME, multiplier - 1.0F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return true;
    }

    private static void strip(Mob mob, CompoundTag record) {
        AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
        AttributeModifier scaled = health == null ? null : health.getModifier(HEALTH);
        float before = mob.getHealth();
        if (health != null) { health.removeModifier(HEALTH); }
        AttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) { damage.removeModifier(DAMAGE); }
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) { speed.removeModifier(SPEED); }
        if (scaled != null) { mob.setHealth((float) (before / (1.0D + scaled.getAmount()))); }
        CompoundTag gear = record.getCompound(GEAR);
        for (String key : gear.getAllKeys()) {
            CompoundTag given = gear.getCompound(key);
            EquipmentSlot slot = Enums.byName(EquipmentSlot.class, key);
            Item item = ContentStacks.item(ResourceLocation.tryParse(given.getString(ITEM)));
            if (slot == null || item == null || !mob.getItemBySlot(slot).is(item)) { continue; }
            mob.setItemSlot(slot, ItemStack.EMPTY);
            mob.setDropChance(slot, given.getFloat(CHANCE));
        }
        mob.getPersistentData().remove(RECORD);
    }

    public static void settleAll(ServerLevel level) {
        if (!ready) { return; }
        List<Entity> loaded = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) { loaded.add(entity); }
        for (Entity entity : loaded) {
            if (entity instanceof Mob mob) { settle(mob, level); }
        }
    }

    public static void start(MinecraftServer server, Collection<MobSwitchDef> defs) {
        for (MobSwitchDef def : defs) {
            for (String id : def.entities()) {
                if (id.startsWith("#")) { continue; }
                ResourceLocation entity = ResourceLocation.tryParse(id);
                if (entity == null || !server.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).containsKey(entity)) { ContentLog.LOGGER.error("{} gives mobs.entities for '{}', which is not an entity id, so it does nothing", def.owner(), id); }
            }
            for (MobSwitchDef.Gear gear : def.equipment().values()) {
                if (ContentStacks.item(ResourceLocation.tryParse(gear.item())) == null) { ContentLog.LOGGER.error("{} gives mobs.equipment for '{}', which is not an item id, so it does nothing", def.owner(), gear.item()); }
            }
        }
        ready = true;
        for (ServerLevel level : server.getAllLevels()) { settleAll(level); }
    }

    public static void forget() { ready = false; }
}
