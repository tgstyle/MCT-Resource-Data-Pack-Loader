package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobDropsDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public final class MobDrops {
    private static final List<String> FILE_KEYS = List.of("entities", "adds", "strip", "stripTagged");
    private static final List<String> ADD_KEYS = List.of("item", "count", "chance", "killerHolds", "damageTypes", "requireAll", "unlessDropped");
    private static final List<MobDropsDef> DEFS = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private MobDrops() {}

    private static synchronized void ensure() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        if (Config.data.mobDropsOff()) { return; }
        Json.eachObject(PackManager.MOB_DROPS, "mob drops file", MobDrops::read);
        if (!DEFS.isEmpty()) { Summary.info("loot.mobdrops", "Loaded " + DEFS.size() + " mob drop file(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        String owner = "Mob drops file " + key;
        List<String> entities = json.has("entities") ? MobStats.entities(owner, "entities", json.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        List<MobDropsDef.Add> adds = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, "adds")) {
            for (JsonElement element : json.getAsJsonArray("adds")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("{} has an add that is not an object, ignoring it", owner);
                    continue;
                }
                MobDropsDef.Add add = add(key, element.getAsJsonObject());
                if (add != null) { adds.add(add); }
            }
        }
        List<ItemMatch> strip = matches(key, json, "strip");
        if (strip.isEmpty() && adds.isEmpty()) {
            ContentLog.LOGGER.error("{} strips and adds nothing, ignoring it", owner);
            return;
        }
        DEFS.add(new MobDropsDef(entities, strip, Json.flag(json, "stripTagged"), adds));
    }

    private static MobDropsDef.Add add(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "adds", ADD_KEYS);
        Item item = ContentStacks.find(key, Json.text(json, "item", ""));
        if (item == null) { return null; }
        int min = 1;
        int max = 1;
        if (json.has("count")) {
            JsonElement count = json.get("count");
            if (count.isJsonObject()) {
                min = Math.max(1, Json.getInt(count.getAsJsonObject(), "min", 1));
                max = Math.max(min, Json.getInt(count.getAsJsonObject(), "max", min));
            }
            else {
                min = Math.max(1, Json.asInt(count, "count", 1));
                max = min;
            }
        }
        List<String> types = new ArrayList<>();
        for (String type : Json.names(json, "damageTypes")) { types.add(IdNames.namespaced(Settings.lower(type))); }
        return new MobDropsDef.Add(item, min, max, Mth.clamp(Json.getFloat(json, "chance", 1.0F), 0.0F, 1.0F), matches(key, json, "killerHolds"), types, Json.flag(json, "requireAll"), matches(key, json, "unlessDropped"));
    }

    private static List<ItemMatch> matches(ResourceLocation key, JsonObject json, String member) {
        List<ItemMatch> found = new ArrayList<>();
        for (String name : Json.names(json, member)) {
            ItemMatch match = ItemMatch.parse(key, name);
            if (match != null) { found.add(match); }
        }
        return found;
    }

    public static void onDrops(LivingDropsEvent event) {
        if (Config.data.mobDropsOff() || !(event.getEntity().level() instanceof ServerLevel level)) { return; }
        ensure();
        if (DEFS.isEmpty()) { return; }
        LivingEntity mob = event.getEntity();
        List<MobDropsDef> applying = new ArrayList<>();
        for (MobDropsDef def : DEFS) {
            if (MobStats.targets(def.entities(), mob)) { applying.add(def); }
        }
        if (applying.isEmpty()) { return; }
        Collection<ItemEntity> drops = event.getDrops();
        for (MobDropsDef def : applying) {
            for (ItemMatch strip : def.strip()) { drops.removeIf(drop -> strip.matches(drop.getItem()) && (def.stripTagged() || !tagged(drop.getItem()))); }
        }
        DamageSource source = event.getSource();
        ItemStack held = source.getEntity() instanceof LivingEntity killer ? killer.getMainHandItem() : ItemStack.EMPTY;
        String type = source.typeHolder().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse("");
        for (MobDropsDef def : applying) {
            for (MobDropsDef.Add add : def.adds()) {
                if (!applies(add, held, type) || dropped(drops, add.unlessDropped()) || add.chance() < 1.0F && mob.getRandom().nextFloat() >= add.chance()) { continue; }
                int count = add.min() >= add.max() ? add.min() : add.min() + mob.getRandom().nextInt(add.max() - add.min() + 1);
                ItemEntity drop = new ItemEntity(level, mob.getX(), mob.getY(), mob.getZ(), new ItemStack(add.item(), Math.min(count, add.item().getDefaultMaxStackSize())));
                drop.setDefaultPickUpDelay();
                drops.add(drop);
            }
        }
    }

    private static boolean applies(MobDropsDef.Add add, ItemStack held, String type) {
        if (!add.conditional()) { return true; }
        boolean holdOk = add.killerHolds().stream().anyMatch(match -> match.matches(held));
        boolean typeOk = add.damageTypes().contains(type);
        if (add.requireAll()) { return (add.killerHolds().isEmpty() || holdOk) && (add.damageTypes().isEmpty() || typeOk); }
        return holdOk || typeOk;
    }

    private static boolean dropped(Collection<ItemEntity> drops, List<ItemMatch> unless) {
        for (ItemEntity drop : drops) {
            for (ItemMatch match : unless) {
                if (match.matches(drop.getItem())) { return true; }
            }
        }
        return false;
    }

    private static boolean tagged(ItemStack stack) {
        for (Map.Entry<DataComponentType<?>, ?> entry : stack.getComponentsPatch().entrySet()) {
            if (entry.getKey() != DataComponents.DAMAGE) { return true; }
        }
        return false;
    }
}
