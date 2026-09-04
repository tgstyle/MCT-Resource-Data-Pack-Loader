package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class BlockDrops {
    public static final String BLOCK = "block";
    public static final String META = "meta";
    public static final String REPLACE = "replace";
    public static final String DROPS = "drops";
    public static final String ITEM = "item";
    public static final String COUNT = "count";
    public static final String CHANCE = "chance";
    public static final String FORTUNE = "fortune";
    public static final String SILK_TOUCH = "silkTouch";
    private static final String EITHER = "either";
    private static final String ONLY = "only";
    private static final String NEVER = "never";
    private static final Gson GSON = new Gson();
    private static final Map<Block, List<Rule>> BY_BLOCK = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private BlockDrops() {}

    private static final class Rule {
        final int meta;
        final boolean replace;
        final List<Drop> drops;

        Rule(int meta, boolean replace, List<Drop> drops) {
            this.meta = meta;
            this.replace = replace;
            this.drops = drops;
        }
    }

    private static final class Drop {
        final ItemStack item;
        final AmountDef count;
        final float chance;
        final int fortune;
        final String silkTouch;

        Drop(ItemStack item, AmountDef count, float chance, int fortune, String silkTouch) {
            this.item = item;
            this.count = count;
            this.chance = chance;
            this.fortune = fortune;
            this.silkTouch = silkTouch;
        }
    }

    public static void reload() {
        BY_BLOCK.clear();
        GENERATION.stale();
        if (!Config.data.blockDrops) { return; }
        int[] count = new int[1];
        Json.eachFile(PackManager.BLOCK_DROPS, "block drops", (key, contents) -> read(key, contents, count));
        if (count[0] > 0) { Summary.info("loot.blockdrops", "Loaded " + count[0] + " block drop rule(s) across " + BY_BLOCK.size() + " block(s)"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Block drops {} is empty, ignoring it", key);
            return;
        }
        String name = JsonUtils.getString(json, BLOCK, "");
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
        for (JsonElement element : JsonUtils.getJsonArray(json, DROPS)) {
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
        BY_BLOCK.computeIfAbsent(block, k -> new ArrayList<>()).add(new Rule(JsonUtils.getInt(json, META, -1), JsonUtils.getBoolean(json, REPLACE, false), drops));
        count[0]++;
    }

    private static Drop drop(ResourceLocation key, JsonObject json) {
        ItemStack item = ContentStacks.parse(key, JsonUtils.getString(json, ITEM, ""), 1);
        if (item.isEmpty()) { return null; }
        int least = 1;
        int most = 1;
        if (json.has(COUNT)) {
            String count = json.get(COUNT).getAsString().trim();
            String[] parts = count.split("-", 2);
            try {
                least = Integer.parseInt(parts[0].trim());
                most = parts.length == 2 ? Integer.parseInt(parts[1].trim()) : least;
            }
            catch (NumberFormatException ex) {
                ContentLog.LOGGER.error("Drop count '{}' in {} is not a number or a low-high range, skipping the drop", count, key);
                return null;
            }
            if (least < 0 || most < least) {
                ContentLog.LOGGER.error("Drop count '{}' in {} must run from a low to a high number, skipping the drop", count, key);
                return null;
            }
        }
        String silkTouch = JsonUtils.getString(json, SILK_TOUCH, EITHER).trim().toLowerCase(Locale.ROOT);
        if (!EITHER.equals(silkTouch) && !ONLY.equals(silkTouch) && !NEVER.equals(silkTouch)) {
            ContentLog.LOGGER.error("Drop silkTouch '{}' in {} is not either, only or never, skipping the drop", silkTouch, key);
            return null;
        }
        return new Drop(item, new AmountDef(least, most), MathHelper.clamp(JsonUtils.getFloat(json, CHANCE, 1.0F), 0.0F, 1.0F), Math.max(0, JsonUtils.getInt(json, FORTUNE, 0)), silkTouch);
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
            if (rule.replace) { event.getDrops().clear(); }
            for (Drop drop : rule.drops) {
                if (ONLY.equals(drop.silkTouch) && !event.isSilkTouching()) { continue; }
                if (NEVER.equals(drop.silkTouch) && event.isSilkTouching()) { continue; }
                if (drop.chance < 1.0F && random.nextFloat() >= drop.chance) { continue; }
                int count = drop.count.pick(random);
                if (drop.fortune > 0 && event.getFortuneLevel() > 0) { count += random.nextInt(drop.fortune * event.getFortuneLevel() + 1); }
                if (count <= 0) { continue; }
                ItemStack stack = drop.item.copy();
                stack.setCount(count);
                event.getDrops().add(stack);
            }
        }
    }
}
