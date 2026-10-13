package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public final class BlockDrops {
    private static final String BLOCK = "block";
    private static final String META = "meta";
    private static final String REPLACE = "replace";
    private static final String DROPS = "drops";
    private static final String ITEM = "item";
    private static final String COUNT = "count";
    private static final String CHANCE = "chance";
    private static final String FORTUNE = "fortune";
    private static final String SILK_TOUCH = "silkTouch";
    private static final String EXPERIENCE = "experience";
    private static final String ADVANCEMENT = "advancement";
    private static final String EITHER = "either";
    private static final String ONLY = "only";
    private static final String NEVER = "never";
    private static final Map<Block, List<Rule>> BY_BLOCK = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private BlockDrops() {}

    private static final class Rule {
        final int meta;
        final boolean replace;
        final String advancement;
        final List<Drop> drops;

        Rule(int meta, boolean replace, String advancement, List<Drop> drops) {
            this.meta = meta;
            this.replace = replace;
            this.advancement = advancement;
            this.drops = drops;
        }
    }

    private static final class Drop {
        final ItemStack item;
        final boolean experience;
        final AmountDef count;
        final float chance;
        final int fortune;
        final String silkTouch;

        Drop(ItemStack item, boolean experience, AmountDef count, float chance, int fortune, String silkTouch) {
            this.item = item;
            this.experience = experience;
            this.count = count;
            this.chance = chance;
            this.fortune = fortune;
            this.silkTouch = silkTouch;
        }
    }

    private static void reload() {
        BY_BLOCK.clear();
        GENERATION.stale();
        if (!Config.data.blockDrops) { return; }
        int[] count = new int[1];
        Json.eachObject(PackManager.BLOCK_DROPS, "block drops", (key, json) -> read(key, json, count));
        if (count[0] > 0) { Summary.info("loot.blockdrops", "Loaded " + count[0] + " block drop rule(s) across " + BY_BLOCK.size() + " block(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        String name = Json.getString(json, BLOCK, "");
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} names no block, ignoring it", key);
            return;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (block == null) {
            ContentLog.LOGGER.error("Block drops {} names block '{}', which nothing registers, ignoring it", key, name);
            return;
        }
        if (!json.has(DROPS)) {
            ContentLog.LOGGER.error("Block drops {} has no drops, ignoring it", key);
            return;
        }
        List<Drop> drops = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, DROPS)) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A drop in {} is not an object, skipping it", key);
                continue;
            }
            Drop drop = drop(key, element.getAsJsonObject());
            if (drop != null) { drops.add(drop); }
        }
        if (drops.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} has no usable drop, ignoring it", key);
            return;
        }
        BY_BLOCK.computeIfAbsent(block, k -> new ArrayList<>()).add(new Rule(Json.getInt(json, META, -1), Json.getBoolean(json, REPLACE, false), Json.getString(json, ADVANCEMENT, "").trim(), drops));
        count[0]++;
    }

    private static Drop drop(ResourceLocation key, JsonObject json) {
        boolean experience = json.has(EXPERIENCE);
        ItemStack item = experience ? ItemStack.EMPTY : ContentStacks.parse(key, Json.getString(json, ITEM, ""), 1);
        if (!experience && item.isEmpty()) { return null; }
        AmountDef count = amount(key, json, experience ? EXPERIENCE : COUNT);
        if (count == null) { return null; }
        String silkTouch = Settings.lower(Json.getString(json, SILK_TOUCH, EITHER));
        if (!EITHER.equals(silkTouch) && !ONLY.equals(silkTouch) && !NEVER.equals(silkTouch)) {
            ContentLog.LOGGER.error("Drop silkTouch '{}' in {} is not either, only or never, skipping the drop", silkTouch, key);
            return null;
        }
        return new Drop(item, experience, count, MathHelper.clamp(Json.getFloat(json, CHANCE, 1.0F), 0.0F, 1.0F), Math.max(0, Json.getInt(json, FORTUNE, 0)), silkTouch);
    }

    @Nullable private static AmountDef amount(ResourceLocation key, JsonObject json, String name) {
        if (!json.has(name)) { return new AmountDef(1, 1); }
        String count = json.get(name).getAsString().trim();
        String[] parts = count.split("-", 2);
        int least;
        int most;
        try {
            least = Integer.parseInt(parts[0].trim());
            most = parts.length == 2 ? Integer.parseInt(parts[1].trim()) : least;
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Drop {} '{}' in {} is not a number or a low-high range, skipping the drop", name, count, key);
            return null;
        }
        if (least < 0 || most < least) {
            ContentLog.LOGGER.error("Drop {} '{}' in {} must run from a low to a high number, skipping the drop", name, count, key);
            return null;
        }
        return new AmountDef(least, most);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (!Config.data.blockDrops) { return; }
        if (GENERATION.stale()) { reload(); }
        Block block = event.getState().getBlock();
        List<Rule> rules = BY_BLOCK.get(block);
        if (rules == null) { return; }
        int meta = block.getMetaFromState(event.getState());
        Random random = event.getWorld().rand;
        for (Rule rule : rules) {
            if (rule.meta >= 0 && rule.meta != meta) { continue; }
            if (!rule.advancement.isEmpty() && (event.getHarvester() == null || !Advancements.has(event.getHarvester(), rule.advancement))) { continue; }
            if (rule.replace) { event.getDrops().clear(); }
            for (Drop drop : rule.drops) {
                if (ONLY.equals(drop.silkTouch) && !event.isSilkTouching()) { continue; }
                if (NEVER.equals(drop.silkTouch) && event.isSilkTouching()) { continue; }
                if (drop.chance < 1.0F && random.nextFloat() >= drop.chance) { continue; }
                int count = drop.count.pick(random);
                if (drop.fortune > 0 && event.getFortuneLevel() > 0) { count += random.nextInt(drop.fortune * event.getFortuneLevel() + 1); }
                if (count <= 0) { continue; }
                if (drop.experience) {
                    if (!event.getWorld().isRemote && event.getDropChance() > 0.0F) { event.getWorld().spawnEntity(new EntityXPOrb(event.getWorld(), event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D, event.getPos().getZ() + 0.5D, count)); }
                    continue;
                }
                ItemStack stack = drop.item.copy();
                stack.setCount(count);
                event.getDrops().add(stack);
            }
        }
    }
}
