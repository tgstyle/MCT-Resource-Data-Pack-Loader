package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.LineCompat;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class BlockDrops extends LineCompat.Modifier {
    private static final String DROPS = "drops";
    private static final String REPLACE = "replace";
    private static final String ITEM = "item";
    private static final String COUNT = "count";
    private static final String CHANCE = "chance";
    private static final String FORTUNE = "fortune";
    private static final String SILK_TOUCH = "silkTouch";
    private static final String EXPERIENCE = "experience";
    private static final String ADVANCEMENT = "advancement";
    private static final String ONCE = "once";
    private static final String PER = "per";
    private static final String GRANT = "grant";
    private static final String RUNS = "runs";
    private static final String PLAYER = "player";
    private static final String EITHER = "either";
    private static final String ONLY = "only";
    private static final String NEVER = "never";
    public static final MapCodec<BlockDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> LineCompat.Modifier.start(instance).apply(instance, BlockDrops::new));
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ResourceDataPackLoader.MOD_ID);
    private static final Map<Block, List<Rule>> BY_BLOCK = new HashMap<>();
    private static final List<TagRule> BY_TAG = new ArrayList<>();
    private static final Set<Identifier> MISSING = new HashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    static { REGISTER.register("block_drops", () -> CODEC); }

    private BlockDrops(LineCompat.ModifierStart start) { super(start); }

    @Override @Nonnull public MapCodec<? extends IGlobalLootModifier> codec() { return CODEC; }

    private record Rule(Identifier key, @Nullable List<BlockState> states, boolean replace, String advancement, String grant, String runs, List<Drop> drops) {}

    private record TagRule(BlockMatch match, Map<String, String> properties, Rule rule) {}

    private record Drop(ItemStack item, boolean experience, AmountDef count, float chance, int fortune, String silkTouch) {}

    private static void reload() {
        BY_BLOCK.clear();
        BY_TAG.clear();
        MISSING.clear();
        GENERATION.stale();
        if (Config.data.blockDropsOff()) { return; }
        int[] count = new int[1];
        Json.eachObject(PackManager.BLOCK_DROPS, "block drops", (key, json) -> read(key, json, count));
        if (count[0] > 0) { Summary.info("loot.blockdrops", "Loaded " + count[0] + " block drop rule(s) across " + BY_BLOCK.size() + " block(s)" + (BY_TAG.isEmpty() ? "" : " and " + BY_TAG.size() + " tag rule(s)")); }
    }

    private static void read(Identifier key, JsonObject json, int[] count) {
        String name = Json.getString(json, "block", "");
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} names no block, ignoring it", key);
            return;
        }
        boolean tag = name.startsWith("#");
        Identifier tagId = tag ? Identifier.tryParse(Settings.lower(name.substring(1).trim())) : null;
        BlockMatchDef match = tag ? null : ContentParser.match(key, json);
        Block block = match == null ? null : Registered.find(BuiltInRegistries.BLOCK, match.block());
        if (tag ? tagId == null : match == null) {
            if (tag) { ContentLog.LOGGER.error("Block drops {} names tag '{}', which is not a valid tag id, ignoring it", key, name); }
            return;
        }
        if (!tag && block == null) {
            ContentLog.LOGGER.error("Block drops {} names block '{}', which nothing registers, ignoring it", key, match.block());
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
        String grant = grant(key, json);
        if (grant == null) { return; }
        List<BlockState> states = tag || match.properties().isEmpty() ? null : ContentStates.matching(block, match.properties(), key);
        Rule rule = new Rule(key, states, Json.getBoolean(json, REPLACE, false), Json.getString(json, ADVANCEMENT, "").trim(), grant, Json.getString(json, RUNS, "").trim(), drops);
        if (tag) { BY_TAG.add(new TagRule(BlockMatch.group(tagId), Json.map(json, "properties"), rule)); }
        else { BY_BLOCK.computeIfAbsent(block, _ -> new ArrayList<>()).add(rule); }
        count[0]++;
    }

    @Nullable private static String grant(Identifier key, JsonObject json) {
        if (!json.has(ONCE)) { return ""; }
        if (!json.get(ONCE).isJsonObject()) {
            ContentLog.LOGGER.error("Block drops {} has a 'once' that is not an object, ignoring it", key);
            return null;
        }
        JsonObject once = json.getAsJsonObject(ONCE);
        String grant = Json.getString(once, GRANT, "").trim();
        if (!PLAYER.equals(Settings.lower(Json.getString(once, PER, ""))) || grant.isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} 'once' needs per \"player\" and a grant, ignoring it", key);
            return null;
        }
        return grant;
    }

    @Nullable private static Drop drop(Identifier key, JsonObject json) {
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
        return new Drop(item, experience, count, Mth.clamp(Json.getFloat(json, CHANCE, 1.0F), 0.0F, 1.0F), Math.max(0, Json.getInt(json, FORTUNE, 0)), silkTouch);
    }

    @Nullable private static AmountDef amount(Identifier key, JsonObject json, String name) {
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

    @Override @Nonnull protected ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        ObjectArrayList<ItemStack> loot = rolled(generatedLoot, context);
        loot.removeIf(ContentDisabled::disabled);
        return loot;
    }

    private static ObjectArrayList<ItemStack> rolled(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (Config.data.blockDropsOff()) { return generatedLoot; }
        BlockState state = LineCompat.param(context, LootContextParams.BLOCK_STATE);
        Vec3 origin = LineCompat.param(context, LootContextParams.ORIGIN);
        if (state == null || origin == null) { return generatedLoot; }
        if (GENERATION.stale()) { reload(); }
        List<Rule> rules = rulesFor(state);
        if (rules == null) { return generatedLoot; }
        Player player = LineCompat.param(context, LootContextParams.THIS_ENTITY) instanceof Player held ? held : null;
        roll(rules, generatedLoot, state, context.getLevel(), BlockPos.containing(origin), player, LineCompat.param(context, LootContextParams.TOOL), LineCompat.param(context, LootContextParams.EXPLOSION_RADIUS), context.getRandom(), false);
        return generatedLoot;
    }

    public static List<ItemStack> prospected(BlockState state, ServerLevel level, BlockPos pos, Player player, ItemStack tool) {
        ObjectArrayList<ItemStack> dropped = new ObjectArrayList<>();
        if (Config.data.blockDropsOff()) { return dropped; }
        if (GENERATION.stale()) { reload(); }
        List<Rule> rules = rulesFor(state);
        if (rules == null) { return dropped; }
        roll(rules, dropped, state, level, pos, player, tool, null, level.getRandom(), true);
        return dropped;
    }

    @Nullable private static List<Rule> rulesFor(BlockState state) {
        List<Rule> direct = BY_BLOCK.get(state.getBlock());
        if (BY_TAG.isEmpty()) { return direct; }
        List<Rule> found = null;
        for (TagRule tag : BY_TAG) {
            if (!tag.match().matches(state.getBlock()) || !fits(state, tag.properties())) { continue; }
            if (found == null) { found = direct == null ? new ArrayList<>() : new ArrayList<>(direct); }
            found.add(tag.rule());
        }
        return found == null ? direct : found;
    }

    private static boolean fits(BlockState state, Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Property<?> property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (property == null || !property.getValue(entry.getValue()).map(value -> value.equals(state.getValue(property))).orElse(false)) { return false; }
        }
        return true;
    }

    private static boolean open(Rule rule, ServerLevel level, @Nullable ServerPlayer earner) {
        if (earner == null) { return false; }
        if (!Advancements.defined(level.getServer(), rule.grant())) {
            if (MISSING.add(rule.key())) { ContentLog.LOGGER.error("Block drops {} grants the advancement {}, which no pack provides, so the rule is skipped", rule.key(), rule.grant()); }
            return false;
        }
        return !Advancements.has(earner, rule.grant());
    }

    private static void run(Rule rule, ServerLevel level, BlockPos pos, @Nullable Player player) {
        if (rule.runs().isEmpty()) { return; }
        if (player instanceof ServerPlayer held) { Functions.runAs(held, rule.runs(), "Block drops"); }
        else { Functions.runAt(level, pos, rule.runs(), "Block drops"); }
    }

    private static void roll(List<Rule> rules, ObjectArrayList<ItemStack> generatedLoot, BlockState state, ServerLevel level, BlockPos pos, @Nullable Player player, @Nullable ItemInstance tool, @Nullable Float radius, RandomSource random, boolean prospecting) {
        boolean silk = silk(state, level, pos, player, tool);
        int fortuneLevel = silk || tool == null ? 0 : level(level, tool, Enchantments.FORTUNE);
        ServerPlayer earner = player instanceof ServerPlayer held && !(held instanceof FakePlayer) ? held : null;
        Set<String> grants = null;
        for (Rule rule : rules) {
            if (rule.states() != null && !rule.states().contains(state)) { continue; }
            if (!rule.advancement().isEmpty() && !Advancements.has(player, rule.advancement())) { continue; }
            if (!rule.grant().isEmpty() && !open(rule, level, earner)) { continue; }
            if (rule.replace()) { generatedLoot.clear(); }
            for (Drop drop : rule.drops()) {
                if (ONLY.equals(drop.silkTouch()) && !silk) { continue; }
                if (NEVER.equals(drop.silkTouch()) && silk) { continue; }
                if (drop.chance() < 1.0F && random.nextFloat() >= drop.chance()) { continue; }
                int count = drop.count().pick(random);
                if (drop.fortune() > 0 && fortuneLevel > 0) { count += random.nextInt(drop.fortune() * fortuneLevel + 1); }
                if (count <= 0) { continue; }
                if (drop.experience()) {
                    if (!prospecting) { level.addFreshEntity(new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, count)); }
                    continue;
                }
                if (prospecting || radius != null && random.nextFloat() > 1.0F / radius) { continue; }
                generatedLoot.add(drop.item().copyWithCount(count));
            }
            run(rule, level, pos, player);
            if (rule.grant().isEmpty()) { continue; }
            if (grants == null) { grants = new LinkedHashSet<>(); }
            grants.add(rule.grant());
        }
        if (grants == null) { return; }
        for (String grant : grants) { Advancements.grant(earner, grant, "Block drops"); }
    }

    private static boolean silk(BlockState state, ServerLevel level, BlockPos pos, @Nullable Player player, @Nullable ItemInstance tool) { return player != null && tool != null && level(level, tool, Enchantments.SILK_TOUCH) > 0 && silkHarvests(state, level, pos); }

    private static boolean silkHarvests(BlockState state, ServerLevel level, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof IronBarsBlock || block instanceof WebBlock || block instanceof EnderChestBlock) { return true; }
        return !state.hasBlockEntity() && Block.isShapeFullBlock(block.defaultBlockState().getShape(level, pos));
    }

    static int level(ServerLevel level, ItemInstance tool, ResourceKey<Enchantment> enchantment) { return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment).map(tool::getEnchantmentLevel).orElse(0); }
}
