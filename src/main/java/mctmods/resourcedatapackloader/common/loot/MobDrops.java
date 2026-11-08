package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobDropsDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public final class MobDrops {
    private static final String ENTITIES = "entities";
    private static final String DAMAGE = "Damage";
    private static final List<MobDropsDef> DEFS = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private MobDrops() {}

    private static void reload() {
        DEFS.clear();
        GENERATION.stale();
        if (Config.data.mobDropsOff()) { return; }
        Json.eachObject(PackManager.MOB_DROPS, "mob drop file", MobDrops::read);
        if (!DEFS.isEmpty()) { Summary.info("loot.mobDrops", "Loaded " + DEFS.size() + " mob drop file(s)"); }
    }

    private static List<ItemMatch> matches(ResourceLocation key, JsonObject json, String member) {
        List<ItemMatch> found = new ArrayList<>();
        for (String text : Json.strings(json, member)) {
            ItemMatch match = ItemMatch.parse(key, text);
            if (match != null) { found.add(match); }
        }
        return found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String owner = "Mob drop file " + key;
        List<String> entities = json.has(ENTITIES) ? MobStats.entities(owner, ENTITIES, json.get(ENTITIES)) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        List<MobDropsDef.Add> adds = new ArrayList<>();
        JsonArray listed = Json.getJsonArray(json, "adds", new JsonArray());
        for (JsonElement element : listed) {
            MobDropsDef.Add add = element.isJsonObject() ? add(key, element.getAsJsonObject()) : null;
            if (add == null) { ContentLog.LOGGER.error("{} has an adds entry that is not an object with a valid item, skipping it", owner); }
            else { adds.add(add); }
        }
        DEFS.add(new MobDropsDef(key, entities, matches(key, json, "strip"), Json.getBoolean(json, "stripTagged", false), adds));
    }

    @Nullable private static MobDropsDef.Add add(ResourceLocation key, JsonObject json) {
        Item item = ContentStacks.find(key, Json.getString(json, "item", ""));
        if (item == null) { return null; }
        int min = 1;
        int max = 1;
        if (json.has("count") && json.get("count").isJsonObject()) {
            JsonObject range = json.getAsJsonObject("count");
            min = Math.max(1, Json.getInt(range, "min", 1));
            max = Math.max(min, Json.getInt(range, "max", min));
        } else if (json.has("count")) {
            min = Math.max(1, Json.getInt(json, "count", 1));
            max = min;
        }
        List<String> types = new ArrayList<>();
        for (String type : Json.strings(json, "damageTypes")) { types.add(type.indexOf(':') < 0 ? "minecraft:" + type : type); }
        return new MobDropsDef.Add(item, min, max, Json.ranged(json, "chance", 1.0F, 0.0F, 1.0F, "Mob drop file " + key), matches(key, json, "killerHolds"), types,
                Json.getBoolean(json, "requireAll", false), matches(key, json, "unlessDropped"));
    }

    public static void onDrops(LivingDropsEvent event) {
        if (Config.data.mobDropsOff()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (DEFS.isEmpty()) { return; }
        LivingEntity mob = event.getEntity();
        List<MobDropsDef> hits = new ArrayList<>();
        for (MobDropsDef def : DEFS) {
            if (MobStats.targets(def.entities(), mob)) { hits.add(def); }
        }
        if (hits.isEmpty()) { return; }
        Collection<ItemEntity> drops = event.getDrops();
        for (MobDropsDef def : hits) { drops.removeIf(drop -> strips(def, drop.getItem())); }
        DamageSource source = event.getSource();
        ItemStack held = source.getEntity() instanceof LivingEntity killer ? killer.getMainHandItem() : ItemStack.EMPTY;
        for (MobDropsDef def : hits) {
            for (MobDropsDef.Add add : def.adds()) {
                if (!applies(add, source, held, drops, mob)) { continue; }
                int count = add.min() + (add.max() > add.min() ? mob.getRandom().nextInt(add.max() - add.min() + 1) : 0);
                ItemEntity drop = new ItemEntity(mob.level(), mob.getX(), mob.getY(), mob.getZ(), new ItemStack(add.item(), count));
                drop.setDefaultPickUpDelay();
                drops.add(drop);
            }
        }
    }

    private static boolean strips(MobDropsDef def, ItemStack stack) {
        if (!def.stripTagged() && tagged(stack)) { return false; }
        for (ItemMatch match : def.strip()) {
            if (match.matches(stack)) { return true; }
        }
        return false;
    }

    private static boolean tagged(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && !tag.getAllKeys().stream().allMatch(DAMAGE::equals);
    }

    private static boolean applies(MobDropsDef.Add add, DamageSource source, ItemStack held, Collection<ItemEntity> drops, LivingEntity mob) {
        for (ItemMatch match : add.unlessDropped()) {
            for (ItemEntity drop : drops) {
                if (match.matches(drop.getItem())) { return false; }
            }
        }
        boolean holds = false;
        for (ItemMatch match : add.killerHolds()) { holds |= match.matches(held); }
        String type = source.typeHolder().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse("");
        boolean typed = add.damageTypes().contains(type);
        boolean heldAsked = !add.killerHolds().isEmpty();
        boolean typeAsked = !add.damageTypes().isEmpty();
        boolean passes = !heldAsked && !typeAsked || (add.requireAll() ? (!heldAsked || holds) && (!typeAsked || typed) : holds || typed);
        return passes && (add.chance() >= 1.0F || mob.getRandom().nextFloat() < add.chance());
    }
}
