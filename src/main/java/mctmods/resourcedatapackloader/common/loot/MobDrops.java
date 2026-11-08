package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.FilterDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobDropsDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MobDrops {
    private static final List<MobDropsDef> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private MobDrops() {}

    public static void load() {
        RULES.clear();
        GENERATION.stale();
        if (!Config.data.mobDrops) { return; }
        Json.eachObject(PackManager.MOB_DROPS, "mob drop file", MobDrops::read);
        if (!RULES.isEmpty()) { Summary.info("loot.mobdrops", "Loaded " + RULES.size() + " mob drop file(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        List<String> entities = json.has("entities") ? MobStats.entities("Mob drop file " + key, "entities", json.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("Mob drop file {} names no entities, ignoring it", key);
            return;
        }
        List<MobDropsDef.Add> adds = new ArrayList<>();
        if (json.has("adds")) {
            for (JsonElement element : Json.getJsonArray(json, "adds")) {
                MobDropsDef.Add add = element.isJsonObject() ? add(key, element.getAsJsonObject()) : null;
                if (add == null) { ContentLog.LOGGER.error("An add in mob drop file {} is not an object with a known item, skipping it", key); }
                else { adds.add(add); }
            }
        }
        List<FilterDef.Entry> strip = matchers(key, json, "strip", false);
        if (strip.isEmpty() && adds.isEmpty()) {
            ContentLog.LOGGER.error("Mob drop file {} strips and adds nothing, ignoring it", key);
            return;
        }
        RULES.add(new MobDropsDef(entities, strip, Json.getBoolean(json, "stripTagged", false), adds));
    }

    private static MobDropsDef.Add add(ResourceLocation key, JsonObject json) {
        ItemStack item = ContentStacks.parse(key, Json.getString(json, "item", ""), 1);
        if (item.isEmpty()) { return null; }
        AmountDef count = ContentParser.amount(json, "count", 1, 1);
        float chance = MathHelper.clamp(Json.getFloat(json, "chance", 1.0F), 0.0F, 1.0F);
        return new MobDropsDef.Add(item, count, chance, matchers(key, json, "killerHolds", false), Json.strings(json, "damageTypes"), Json.getBoolean(json, "requireAll", false), matchers(key, json, "unlessDropped", true));
    }

    private static List<FilterDef.Entry> matchers(ResourceLocation key, JsonObject json, String member, boolean anyMeta) {
        List<FilterDef.Entry> found = new ArrayList<>();
        for (String value : Json.strings(json, member)) {
            if (value.startsWith("#")) { found.add(new FilterDef.Entry(key, "", value.substring(1), "", 0)); }
            else { found.add(new FilterDef.Entry(key, anyMeta && value.split(":").length < 3 ? value + ":*" : value, "", "", 0)); }
        }
        return found;
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onDrops(LivingDropsEvent event) {
        if (!Config.data.mobDrops || event.getEntityLiving().world.isRemote) { return; }
        if (GENERATION.stale()) { load(); }
        if (RULES.isEmpty()) { return; }
        EntityLivingBase mob = event.getEntityLiving();
        List<EntityItem> drops = event.getDrops();
        for (MobDropsDef rule : RULES) {
            if (!MobStats.targets(rule.entities, mob)) { continue; }
            if (!rule.strip.isEmpty()) { strip(rule, drops); }
            for (MobDropsDef.Add add : rule.adds) {
                if (!applies(add, event.getSource(), drops, mob)) { continue; }
                ItemStack stack = add.item.copy();
                stack.setCount(add.count.pick(mob.getRNG()));
                EntityItem drop = new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, stack);
                drop.setDefaultPickupDelay();
                drops.add(drop);
            }
        }
    }

    private static void strip(MobDropsDef rule, List<EntityItem> drops) {
        for (Iterator<EntityItem> each = drops.iterator(); each.hasNext(); ) {
            ItemStack stack = each.next().getItem();
            if ((rule.stripTagged || !tagged(stack)) && any(rule.strip, stack)) { each.remove(); }
        }
    }

    private static boolean tagged(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && (tag.getSize() > 1 || tag.getSize() == 1 && !tag.hasKey("Damage"));
    }

    private static boolean applies(MobDropsDef.Add add, DamageSource source, List<EntityItem> drops, EntityLivingBase mob) {
        for (EntityItem drop : drops) {
            if (any(add.unlessDropped, drop.getItem())) { return false; }
        }
        boolean holds = add.killerHolds.isEmpty() || any(add.killerHolds, killerHand(source));
        boolean typed = add.damageTypes.isEmpty() || add.damageTypes.contains(source.getDamageType());
        boolean met = add.killerHolds.isEmpty() || add.damageTypes.isEmpty() ? holds && typed : add.requireAll ? holds && typed : holds || typed;
        return met && (add.chance >= 1.0F || mob.getRNG().nextFloat() < add.chance);
    }

    private static ItemStack killerHand(DamageSource source) {
        Entity by = source.getTrueSource();
        return by instanceof EntityLivingBase ? ((EntityLivingBase) by).getHeldItemMainhand() : ItemStack.EMPTY;
    }

    private static boolean any(List<FilterDef.Entry> entries, ItemStack stack) {
        for (FilterDef.Entry entry : entries) {
            if (entry.matches(stack)) { return true; }
        }
        return false;
    }
}
