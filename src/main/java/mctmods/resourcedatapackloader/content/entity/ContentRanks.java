package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.RankDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WeightedPicks;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentRanks {
    private static final List<String> KEYS = List.of("entities", "weight", "name", "health", "damage", "effects", "loot", "dimensions");
    private static final String RANK = "rdplRank";
    private static final ResourceLocation HEALTH = ResourceLocation.fromNamespaceAndPath("resourcedatapackloader", "rank_health");
    private static final ResourceLocation DAMAGE = ResourceLocation.fromNamespaceAndPath("resourcedatapackloader", "rank_damage");
    private static final Map<String, RankDef> DEFS = new LinkedHashMap<>();
    private static final Map<Entity, List<ItemStack>> GEAR = new MapMaker().weakKeys().makeMap();

    private ContentRanks() {}

    public static boolean load() {
        DEFS.clear();
        Json.eachFile(PackManager.RANKS, "rank definition", ContentRanks::read);
        DEFS.values().removeIf(ContentRanks::unregistered);
        if (!DEFS.isEmpty()) { Summary.info("ranks", "Loaded " + DEFS.size() + " mob rank(s)"); }
        return !DEFS.isEmpty();
    }

    public static String rankOf(Entity entity) { return entity.getPersistentData().getString(RANK); }

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
        List<String> entities = List.of(MobStats.MONSTER);
        if (json.has("entities")) {
            List<String> listed = MobStats.entities(owner, "entities", json.get("entities"));
            if (listed == null || listed.size() != json.get("entities").getAsJsonArray().size()) {
                ContentLog.LOGGER.error("{} gives entities that are not all entity ids and kinds, ignoring the rank", owner);
                return null;
            }
            entities = listed;
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
        Map<String, Integer> effects = ContentEntityParser.effects(key, json);
        if (json.has("effects") && effects.size() != json.get("effects").getAsJsonArray().size()) {
            ContentLog.LOGGER.error("{} gives effects that are not all objects with a different potion each, ignoring the rank", owner);
            return null;
        }
        List<String> dimensions = new ArrayList<>();
        for (String named : Json.strings(json, "dimensions")) { dimensions.add(CardPlace.dimensionId(named)); }
        return new RankDef(key, entities, weight.intValue(), label, health, damage, effects, loot, dimensions);
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

    private static boolean unregistered(RankDef def) {
        boolean missing = false;
        for (String id : def.entities()) {
            if (id.startsWith("#")) { continue; }
            ResourceLocation entity = ResourceLocation.tryParse(id);
            if (entity == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(entity)) {
                ContentLog.LOGGER.error("Rank {} names entity '{}', which is not an entity, ignoring the rank", def.key(), id);
                missing = true;
            }
        }
        for (String potion : def.effects().keySet()) {
            if (ContentEffects.find(potion) == null) {
                ContentLog.LOGGER.error("Rank {} wants effect '{}', which nothing registers, ignoring the rank", def.key(), potion);
                missing = true;
            }
        }
        return missing;
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || DEFS.isEmpty() || !(event.getEntity() instanceof Mob mob)) { return; }
        CompoundTag data = mob.getPersistentData();
        if (data.contains(RANK)) { return; }
        List<RankDef> matches = new ArrayList<>();
        for (RankDef each : DEFS.values()) {
            if (MobStats.targets(each.entities(), mob) && (each.dimensions().isEmpty() || CardPlace.inAny(each.dimensions(), mob.level()))) { matches.add(each); }
        }
        RankDef def = WeightedPicks.pick(matches, RankDef::weight, mob.getRandom());
        if (def == null) { return; }
        data.putString(RANK, def.key().toString());
        if (MobStats.scale(mob, Attributes.MAX_HEALTH, HEALTH, def.health())) { mob.setHealth(mob.getHealth() * def.health()); }
        MobStats.scale(mob, Attributes.ATTACK_DAMAGE, DAMAGE, def.damage());
        ContentEntityApply.effects(mob, def.effects(), def.key());
        if (!def.label().isEmpty() && !mob.hasCustomName()) { mob.setCustomName(named(def.label(), mob)); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} rolled for {} at {}", def.key(), BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mob.blockPosition()); }
    }

    private static Component named(String label, Mob mob) {
        MutableComponent name = Component.empty();
        String[] parts = label.split("%s", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) { name.append(mob.getType().getDescription()); }
            name.append(Component.literal(parts[i]));
        }
        return name;
    }

    @Nullable private static RankDef multiplying(Entity entity) {
        if (!(entity instanceof Mob) || entity instanceof InventoryCarrier || entity instanceof AbstractChestedHorse) { return null; }
        RankDef def = DEFS.get(entity.getPersistentData().getString(RANK));
        return def == null || def.loot() == 1.0F ? null : def;
    }

    public static void onDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (multiplying(entity) == null) { return; }
        List<ItemStack> held = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = ((Mob) entity).getItemBySlot(slot);
            if (!stack.isEmpty()) { held.add(stack.copy()); }
        }
        GEAR.put(entity, held);
    }

    public static void onDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        List<ItemStack> held = GEAR.remove(entity);
        RankDef def = multiplying(entity);
        if (def == null) { return; }
        List<ItemEntity> more = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.getMaxStackSize() <= 1 || held != null && carried(held, stack)) { continue; }
            float wanted = stack.getCount() * def.loot();
            int count = (int) wanted;
            if (entity.level().random.nextFloat() < wanted - count) { count++; }
            int most = stack.getMaxStackSize();
            ItemStack first = stack.copy();
            first.setCount(Math.min(count, most));
            drop.setItem(first);
            for (int left = count - most; left > 0; left -= most) {
                ItemStack rest = stack.copy();
                rest.setCount(Math.min(left, most));
                ItemEntity item = new ItemEntity(entity.level(), drop.getX(), drop.getY(), drop.getZ(), rest);
                item.setDeltaMovement(drop.getDeltaMovement());
                item.setDefaultPickUpDelay();
                more.add(item);
            }
        }
        event.getDrops().addAll(more);
    }

    private static boolean carried(List<ItemStack> held, ItemStack stack) {
        for (Iterator<ItemStack> each = held.iterator(); each.hasNext(); ) {
            if (ItemStack.matches(each.next(), stack)) {
                each.remove();
                return true;
            }
        }
        return false;
    }
}
