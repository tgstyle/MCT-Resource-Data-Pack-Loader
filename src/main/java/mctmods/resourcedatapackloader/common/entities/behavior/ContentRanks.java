package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.ai.DigGoal;
import mctmods.resourcedatapackloader.common.entities.ai.StealContainerGoal;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankRandomDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankScalingDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ContentEffects;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

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
        MobGearPool.clear();
        Json.eachFile(PackManager.RANKS, "rank definition", ContentRanks::read);
        DEFS.values().removeIf(ContentRanks::unregistered);
        if (!DEFS.isEmpty()) { Summary.info("ranks", "Loaded " + DEFS.size() + " mob rank(s)"); }
        return !DEFS.isEmpty();
    }

    public static boolean stealing() {
        for (RankDef def : DEFS.values()) {
            if (def.steals()) { return true; }
        }
        return false;
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
        RankDef def = ContentRankParser.parse(key, json);
        if (def != null) { DEFS.put(key.toString(), def); }
    }

    private static boolean unregistered(RankDef def) {
        boolean missing = false;
        List<String> items = new ArrayList<>(def.equipment().values());
        if (def.digs() && !def.digItem().isEmpty()) { items.add(def.digItem()); }
        for (String exclude : def.random().exclude()) {
            if (exclude.indexOf(':') >= 0) { items.add(exclude); }
        }
        for (String item : items) {
            if (ContentStacks.item(ResourceLocation.tryParse(item)) == null) {
                ContentLog.LOGGER.error("Rank {} names item '{}', which nothing registers, ignoring the rank", def.key(), item);
                missing = true;
            }
        }
        for (String id : def.entities()) {
            if (id.startsWith("#")) { continue; }
            ResourceLocation entity = ResourceLocation.tryParse(id);
            if (entity == null || !ForgeRegistries.ENTITY_TYPES.containsKey(entity)) {
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
        if (data.contains(RANK)) {
            RankDef known = DEFS.get(data.getString(RANK));
            if (known != null) { arm(mob, known); }
            return;
        }
        List<RankDef> matches = new ArrayList<>();
        for (RankDef each : DEFS.values()) {
            if (MobStats.targets(each.entities(), mob) && (each.dimensions().isEmpty() || CardPlace.inAny(each.dimensions(), mob.level()))) { matches.add(each); }
        }
        RankDef def = WeightedPicks.pick(matches, RankDef::weight, mob.getRandom());
        if (def == null) { return; }
        data.putString(RANK, def.key().toString());
        float steps = RankScaling.steps(mob, def.scaling());
        if (def.scaling() != null) { data.putFloat(STEPS, steps); }
        RankScalingDef scaling = RankScaling.of(def);
        float health = Math.max(0.1F, RankScaling.add(def.health(), scaling.health(), steps));
        if (MobStats.scale(mob, Attributes.MAX_HEALTH, HEALTH, health, NAME)) { mob.setHealth(mob.getHealth() * health); }
        MobStats.scale(mob, Attributes.ATTACK_DAMAGE, DAMAGE, Math.max(0.0F, RankScaling.add(def.damage(), scaling.damage(), steps)), NAME);
        ContentEntityApply.effects(mob, def.effects(), def.key());
        if (!def.label().isEmpty() && !mob.hasCustomName()) { mob.setCustomName(named(def.label(), mob)); }
        if (givesGear(def)) { data.putBoolean(GEAR_PENDING, true); }
        arm(mob, def);
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} rolled for {} at {}", def.key(), ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()), mob.blockPosition()); }
    }

    private static boolean givesGear(RankDef def) { return !def.equipment().isEmpty() || def.random() != RankRandomDef.NONE || scalesGear(def.scaling()) || !def.digItem().isEmpty() || def.picksUpLoot() != null; }

    private static boolean scalesGear(@Nullable RankScalingDef scaling) { return scaling != null && (scaling.armor() > 0.0F || scaling.weapon() > 0.0F || scaling.enchant() > 0.0F); }

    private static void arm(Mob mob, RankDef def) {
        if (def.digs()) {
            if (mob instanceof PathfinderMob creature) {
                creature.goalSelector.addGoal(1, new DigGoal(creature));
                MobAbilities.sightlessTarget(creature, Player.class, 2);
            }
            else if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Rank {} digs, but {} is not a walking creature, skipping it", def.key(), ForgeRegistries.ENTITY_TYPES.getKey(mob.getType())); }
        }
        if (def.breaksDoors()) {
            Goal task = MobAbilities.breakDoor(mob);
            if (task != null) { mob.goalSelector.addGoal(1, task); }
        }
        if (def.steals() && mob instanceof PathfinderMob creature) { mob.goalSelector.addGoal(5, new StealContainerGoal(creature)); }
    }

    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide()) { return; }
        CompoundTag data = mob.getPersistentData();
        if (!data.contains(GEAR_PENDING)) { return; }
        data.remove(GEAR_PENDING);
        RankDef def = DEFS.get(data.getString(RANK));
        if (def != null) { gear(mob, def, data.getFloat(STEPS)); }
    }

    private static void gear(Mob mob, RankDef def, float steps) {
        RankScalingDef scaling = RankScaling.of(def);
        RankRandomDef random = def.random();
        MobGear.fixed(mob, def.equipment(), def.dropChance(), def.key().toString());
        MobGear.random(mob, RankScaling.chance(random.armor(), scaling.armor(), steps), RankScaling.chance(random.armorPiece(), scaling.armor(), steps), RankScaling.chance(random.weapon(), scaling.weapon(), steps), RankScaling.chance(random.offhand(), scaling.weapon(), steps), RankScaling.chance(random.enchant(), scaling.enchant(), steps), def.digs(), random.exclude(), def.dropChance());
        if (def.picksUpLoot() != null) { mob.setCanPickUpLoot(def.picksUpLoot()); }
        if (def.digs()) { MobAbilities.armPick(mob, def.digItem(), def.dropChance(), def.key().toString()); }
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
