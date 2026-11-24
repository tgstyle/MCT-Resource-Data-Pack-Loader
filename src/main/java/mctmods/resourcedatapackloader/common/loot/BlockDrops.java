package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

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
    private static final String ONCE = "once";
    private static final String PER = "per";
    private static final String GRANT = "grant";
    private static final String PLAYER = "player";
    private static final List<String> RULE_KEYS = Collections.unmodifiableList(Arrays.asList(BLOCK, META, REPLACE, DROPS, ONCE, ADVANCEMENT, Json.RUNS));
    private static final List<String> DROP_KEYS = Collections.unmodifiableList(Arrays.asList(ITEM, EXPERIENCE, COUNT, CHANCE, FORTUNE, SILK_TOUCH));
    private static final List<String> ONCE_KEYS = Collections.unmodifiableList(Arrays.asList(PER, GRANT));
    private static final Map<Block, List<Rule>> BY_BLOCK = new HashMap<>();
    private static final List<TagRule> BY_TAG = new ArrayList<>();
    private static final Set<ResourceLocation> MISSING = new HashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private BlockDrops() {}

    private static final class Rule {
        final ResourceLocation key;
        final int meta;
        final boolean replace;
        final String advancement;
        final String grant;
        final String runs;
        final List<Drop> drops;

        Rule(ResourceLocation key, int meta, boolean replace, String advancement, String grant, String runs, List<Drop> drops) {
            this.key = key;
            this.meta = meta;
            this.replace = replace;
            this.advancement = advancement;
            this.grant = grant;
            this.runs = runs;
            this.drops = drops;
        }
    }

    private static final class TagRule {
        final BlockMatch match;
        final Rule rule;

        TagRule(BlockMatch match, Rule rule) {
            this.match = match;
            this.rule = rule;
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
        int[] count = new int[1];
        GENERATION.load(Config.data.blockDrops, () -> {
            BY_BLOCK.clear();
            BY_TAG.clear();
            MISSING.clear();
        }, PackManager.BLOCK_DROPS, "block drops", (key, json) -> read(key, json, count), () -> {
            if (count[0] > 0) { Summary.info("loot.blockdrops", "Loaded " + count[0] + " block drop rule(s) across " + BY_BLOCK.size() + " block(s)" + (BY_TAG.isEmpty() ? "" : " and " + BY_TAG.size() + " tag rule(s)")); }
        });
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        String name = Json.required(json, BLOCK, key, "Block drops {} names no block, ignoring it");
        if (name.isEmpty()) { return; }
        boolean tag = IdNames.isTag(name);
        String ore = tag ? tagName(key, name) : null;
        Block block = tag ? null : ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (tag ? ore == null : block == null) {
            if (!tag) { ContentLog.LOGGER.error("Block drops {} names block '{}', which nothing registers, ignoring it", key, name); }
            return;
        }
        if (Json.missing(json, DROPS, key, "Block drops {} has no drops, ignoring it")) { return; }
        List<Drop> drops = new ArrayList<>();
        for (JsonObject element : Json.objectsOrSkip(Json.getJsonArray(json, DROPS), "A drop in {} is not an object, skipping it", key)) {
            Drop drop = drop(key, element);
            if (drop != null) { drops.add(drop); }
        }
        if (drops.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} has no usable drop, ignoring it", key);
            return;
        }
        String grant = grant(key, json);
        if (grant == null) { return; }
        Rule rule = new Rule(key, Json.getInt(json, META, -1), Json.getBoolean(json, REPLACE, false), Json.getString(json, ADVANCEMENT, "").trim(), grant, Json.getString(json, Json.RUNS, "").trim(), drops);
        if (tag) { BY_TAG.add(new TagRule(BlockMatch.group(ore), rule)); }
        else { BY_BLOCK.computeIfAbsent(block, k -> new ArrayList<>()).add(rule); }
        count[0]++;
    }

    @Nullable private static String tagName(ResourceLocation key, String name) {
        String text = name.substring(1).trim();
        String ore = text.isEmpty() ? null : text.indexOf(':') < 0 ? text : Ids.oreName(name);
        if (ore == null) {
            ContentLog.LOGGER.error("Block drops {} names tag '{}', which has no ore dictionary name on this version, ignoring it", key, name);
            return null;
        }
        if (!OreDictionary.doesOreNameExist(ore)) {
            ContentLog.LOGGER.error("Block drops {} names tag '{}', whose ore dictionary name {} has no members, ignoring it", key, name, ore);
            return null;
        }
        return ore;
    }

    @Nullable private static String grant(ResourceLocation key, JsonObject json) {
        if (!json.has(ONCE)) { return ""; }
        if (!json.get(ONCE).isJsonObject()) {
            ContentLog.LOGGER.error("Block drops {} has a 'once' that is not an object, ignoring it", key);
            return null;
        }
        JsonObject once = json.getAsJsonObject(ONCE);
        Json.unknownKeys(once, ONCE, ONCE_KEYS);
        String grant = Json.getString(once, GRANT, "").trim();
        if (!PLAYER.equals(Settings.lower(Json.getString(once, PER, ""))) || grant.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} 'once' needs per \"player\" and a grant, ignoring it", key);
            return null;
        }
        return grant;
    }

    private static Drop drop(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, DROPS, DROP_KEYS);
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
        return new Drop(item, experience, count, Json.chance(json, CHANCE, 1.0F), Math.max(0, Json.getInt(json, FORTUNE, 0)), silkTouch);
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

    @Nullable private static List<Rule> rulesFor(BlockEvent.HarvestDropsEvent event, Block block) {
        List<Rule> direct = BY_BLOCK.get(block);
        if (BY_TAG.isEmpty()) { return direct; }
        List<Rule> found = null;
        for (TagRule tag : BY_TAG) {
            if (!tag.match.matches(block, event.getWorld(), event.getPos())) { continue; }
            if (found == null) { found = direct == null ? new ArrayList<>() : new ArrayList<>(direct); }
            found.add(tag.rule);
        }
        return found == null ? direct : found;
    }

    private static boolean open(BlockEvent.HarvestDropsEvent event, Rule rule) {
        EntityPlayer harvester = event.getHarvester();
        if (event.getWorld().isRemote || !(harvester instanceof EntityPlayerMP) || harvester instanceof FakePlayer) { return false; }
        MinecraftServer server = harvester.getServer();
        if (server == null) { return false; }
        if (Advancements.find(server, rule.grant) == null) {
            if (MISSING.add(rule.key)) { ContentLog.LOGGER.error("Block drops {} grants the advancement {}, which no pack provides, so the rule is skipped", rule.key, rule.grant); }
            return false;
        }
        return !Advancements.has(harvester, rule.grant);
    }

    private static void roll(BlockEvent.HarvestDropsEvent event, Rule rule, Random random) {
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

    private static void run(BlockEvent.HarvestDropsEvent event, Rule rule) {
        if (rule.runs.isEmpty() || event.getWorld().isRemote) { return; }
        if (event.getHarvester() != null) { Functions.runAs(event.getHarvester(), rule.runs, "Block drops"); }
        else { Functions.runAt(event.getWorld(), event.getPos(), rule.runs, "Block drops"); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (!Config.data.blockDrops) { return; }
        if (GENERATION.stale()) { reload(); }
        Block block = event.getState().getBlock();
        List<Rule> rules = rulesFor(event, block);
        if (rules == null) { return; }
        int meta = block.getMetaFromState(event.getState());
        Random random = event.getWorld().rand;
        Set<String> grants = null;
        for (Rule rule : rules) {
            if (rule.meta >= 0 && rule.meta != meta) { continue; }
            if (!rule.advancement.isEmpty() && (event.getHarvester() == null || !Advancements.has(event.getHarvester(), rule.advancement))) { continue; }
            if (!rule.grant.isEmpty() && !open(event, rule)) { continue; }
            roll(event, rule, random);
            run(event, rule);
            if (!rule.grant.isEmpty()) {
                if (grants == null) { grants = new LinkedHashSet<>(); }
                grants.add(rule.grant);
            }
        }
        if (grants == null) { return; }
        for (String grant : grants) { Advancements.grant((EntityPlayerMP) event.getHarvester(), grant, "Block drops"); }
    }
}
