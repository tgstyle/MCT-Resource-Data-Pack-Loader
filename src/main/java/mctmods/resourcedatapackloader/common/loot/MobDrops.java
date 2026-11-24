package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobDropsDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

    public static void load() {
        GENERATION.load(!Config.data.mobDropsOff(), DEFS::clear, PackManager.MOB_DROPS, "mob drop file", MobDrops::read, () -> {
            if (!DEFS.isEmpty()) { Summary.info("loot.mobdrops", "Loaded " + DEFS.size() + " mob drop file(s)"); }
        });
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        String owner = "Mob drop file " + key;
        List<Identifier> ids = new ArrayList<>();
        List<String> kinds = new ArrayList<>();
        if (!json.has("entities") || !MobStats.entities(owner, "entities", json.get("entities"), ids, kinds) || ids.isEmpty() && kinds.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        ItemSelection strip = new ItemSelection("Mob drop file", "strips");
        strip.readNames(Json.strings(json, "strip"), key);
        List<MobDropsDef.Add> adds = new ArrayList<>();
        if (json.has("adds")) {
            for (JsonElement element : Json.getJsonArray(json, "adds")) {
                if (!element.isJsonObject()) { ContentLog.LOGGER.error("{} has an adds entry that is not an object, skipping it", owner); }
                else { add(key, owner, element.getAsJsonObject(), adds); }
            }
        }
        DEFS.add(new MobDropsDef(ids, kinds, strip, Json.getBoolean(json, "stripTagged", false), List.copyOf(adds)));
    }

    private static void add(Identifier key, String owner, JsonObject json, List<MobDropsDef.Add> adds) {
        Json.unknownKeys(json, "adds", ADD_KEYS);
        String item = Json.getString(json, "item", "");
        if (item.isEmpty()) {
            ContentLog.LOGGER.error("{} has an adds entry with no item, skipping it", owner);
            return;
        }
        ItemSelection holds = new ItemSelection("Mob drop file", "matches");
        holds.readNames(Json.strings(json, "killerHolds"), key);
        ItemSelection unless = new ItemSelection("Mob drop file", "matches");
        unless.readNames(Json.strings(json, "unlessDropped"), key);
        List<Identifier> types = new ArrayList<>();
        for (String name : Json.strings(json, "damageTypes")) {
            Identifier type = Identifier.tryParse(Settings.lower(name));
            if (type == null) { ContentLog.LOGGER.error("{} names the damage type '{}', which is not a valid id, skipping it", owner, name); }
            else { types.add(type); }
        }
        AmountDef count = ContentParser.amount(json, "count", 1, 1);
        adds.add(new MobDropsDef.Add(key, item, count, Json.ranged(json, "chance", 1.0F, 0.0F, 1.0F, owner), holds, List.copyOf(types), Json.getBoolean(json, "requireAll", false), unless));
    }

    public static void onDrops(LivingDropsEvent event) {
        if (Config.data.mobDropsOff()) { return; }
        if (GENERATION.stale()) { load(); }
        if (DEFS.isEmpty() || !(event.getEntity().level() instanceof ServerLevel level)) { return; }
        LivingEntity mob = event.getEntity();
        List<MobDropsDef> matching = new ArrayList<>();
        for (MobDropsDef def : DEFS) {
            if (MobStats.targets(def.ids(), def.kinds(), mob)) { matching.add(def); }
        }
        if (matching.isEmpty()) { return; }
        Collection<ItemEntity> drops = event.getDrops();
        for (MobDropsDef def : matching) {
            if (def.strip().unnamed()) { continue; }
            drops.removeIf(drop -> def.strip().matches(drop.getItem()) && (def.stripTagged() || plain(drop.getItem())));
        }
        DamageSource source = event.getSource();
        ItemStack held = source.getEntity() instanceof LivingEntity killer ? killer.getMainHandItem() : ItemStack.EMPTY;
        Identifier type = source.typeHolder().unwrapKey().map(ResourceKey::identifier).orElse(null);
        for (MobDropsDef def : matching) {
            for (MobDropsDef.Add add : def.adds()) {
                if (add.holds(held, type) && !dropped(drops, add) && level.getRandom().nextFloat() < add.chance()) { give(level, mob, drops, add); }
            }
        }
    }

    private static boolean plain(ItemStack stack) {
        return stack.getComponentsPatch().forget(type -> type == DataComponents.DAMAGE).isEmpty();
    }

    private static boolean dropped(Collection<ItemEntity> drops, MobDropsDef.Add add) {
        if (add.unlessDropped().unnamed()) { return false; }
        for (ItemEntity drop : drops) {
            if (add.unlessDropped().matches(drop.getItem())) { return true; }
        }
        return false;
    }

    private static void give(ServerLevel level, LivingEntity mob, Collection<ItemEntity> drops, MobDropsDef.Add add) {
        Item item = add.item();
        if (item == null) { return; }
        ItemEntity drop = new ItemEntity(level, mob.getX(), mob.getY(), mob.getZ(), new ItemStack(item, add.count().pick(level.getRandom())));
        drop.setDefaultPickUpDelay();
        drops.add(drop);
    }
}
