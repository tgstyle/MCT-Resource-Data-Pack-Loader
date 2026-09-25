package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.util.ContentDisabled;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BlockDrops extends LootModifier {
    public static final String DROPS = "drops";
    public static final String REPLACE = "replace";
    public static final String ITEM = "item";
    public static final String COUNT = "count";
    public static final String CHANCE = "chance";
    public static final String FORTUNE = "fortune";
    public static final String SILK_TOUCH = "silkTouch";
    public static final String EXPERIENCE = "experience";
    public static final String ADVANCEMENT = "advancement";
    private static final String EITHER = "either";
    private static final String ONLY = "only";
    private static final String NEVER = "never";
    public static final MapCodec<BlockDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance).apply(instance, BlockDrops::new));
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ResourceDataPackLoader.MOD_ID);
    private static final Gson GSON = new Gson();
    private static final Map<Block, List<Rule>> BY_BLOCK = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    static { REGISTER.register("block_drops", () -> CODEC); }

    public BlockDrops(LootItemCondition[] conditions) { super(conditions); }

    @Override @Nonnull public MapCodec<? extends IGlobalLootModifier> codec() { return CODEC; }

    private record Rule(@Nullable List<BlockState> states, boolean replace, String advancement, List<Drop> drops) {}

    private record Drop(ItemStack item, boolean experience, AmountDef count, float chance, int fortune, String silkTouch) {}

    public static void reload() {
        BY_BLOCK.clear();
        GENERATION.stale();
        if (Config.data.blockDropsOff()) { return; }
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
        if (GsonHelper.getAsString(json, "block", "").isEmpty()) {
            ContentLog.LOGGER.error("Block drops {} names no block, ignoring it", key);
            return;
        }
        BlockMatchDef match = ContentParser.match(key, json);
        if (match == null) { return; }
        Block block = BuiltInRegistries.BLOCK.getOptional(match.block()).orElse(null);
        if (block == null) {
            ContentLog.LOGGER.error("Block drops {} names block '{}', which nothing registers, ignoring it", key, match.block());
            return;
        }
        if (!json.has(DROPS)) {
            ContentLog.LOGGER.error("Block drops {} has no drops, ignoring it", key);
            return;
        }
        List<Drop> drops = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, DROPS)) {
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
        List<BlockState> states = match.properties().isEmpty() ? null : ContentStates.matching(block, match.properties(), key);
        BY_BLOCK.computeIfAbsent(block, k -> new ArrayList<>()).add(new Rule(states, GsonHelper.getAsBoolean(json, REPLACE, false), GsonHelper.getAsString(json, ADVANCEMENT, "").trim(), drops));
        count[0]++;
    }

    @Nullable private static Drop drop(ResourceLocation key, JsonObject json) {
        boolean experience = json.has(EXPERIENCE);
        ItemStack item = experience ? ItemStack.EMPTY : ContentStacks.parse(key, GsonHelper.getAsString(json, ITEM, ""), 1);
        if (!experience && item.isEmpty()) { return null; }
        AmountDef count = amount(key, json, experience ? EXPERIENCE : COUNT);
        if (count == null) { return null; }
        String silkTouch = GsonHelper.getAsString(json, SILK_TOUCH, EITHER).trim().toLowerCase(Locale.ROOT);
        if (!EITHER.equals(silkTouch) && !ONLY.equals(silkTouch) && !NEVER.equals(silkTouch)) {
            ContentLog.LOGGER.error("Drop silkTouch '{}' in {} is not either, only or never, skipping the drop", silkTouch, key);
            return null;
        }
        return new Drop(item, experience, count, Mth.clamp(GsonHelper.getAsFloat(json, CHANCE, 1.0F), 0.0F, 1.0F), Math.max(0, GsonHelper.getAsInt(json, FORTUNE, 0)), silkTouch);
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

    @Override @Nonnull protected ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        ObjectArrayList<ItemStack> loot = rolled(generatedLoot, context);
        loot.removeIf(ContentDisabled::disabled);
        return loot;
    }

    private static ObjectArrayList<ItemStack> rolled(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (Config.data.blockDropsOff()) { return generatedLoot; }
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (state == null || origin == null) { return generatedLoot; }
        if (GENERATION.stale()) { reload(); }
        List<Rule> rules = BY_BLOCK.get(state.getBlock());
        if (rules == null) { return generatedLoot; }
        Player player = context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player held ? held : null;
        roll(rules, generatedLoot, state, context.getLevel(), BlockPos.containing(origin), player, context.getParamOrNull(LootContextParams.TOOL), context.getParamOrNull(LootContextParams.EXPLOSION_RADIUS), context.getRandom(), false);
        return generatedLoot;
    }

    public static List<ItemStack> prospected(BlockState state, ServerLevel level, BlockPos pos, Player player, ItemStack tool) {
        ObjectArrayList<ItemStack> dropped = new ObjectArrayList<>();
        if (Config.data.blockDropsOff()) { return dropped; }
        if (GENERATION.stale()) { reload(); }
        List<Rule> rules = BY_BLOCK.get(state.getBlock());
        if (rules == null) { return dropped; }
        roll(rules, dropped, state, level, pos, player, tool, null, level.getRandom(), true);
        return dropped;
    }

    private static void roll(List<Rule> rules, ObjectArrayList<ItemStack> generatedLoot, BlockState state, ServerLevel level, BlockPos pos, @Nullable Player player, @Nullable ItemStack tool, @Nullable Float radius, RandomSource random, boolean prospecting) {
        boolean silk = silk(state, level, pos, player, tool);
        boolean withheld = prospecting && !silk;
        int fortuneLevel = silk || tool == null ? 0 : level(level, tool, Enchantments.FORTUNE);
        for (Rule rule : rules) {
            if (rule.states() != null && !rule.states().contains(state)) { continue; }
            if (!rule.advancement().isEmpty() && !Advancements.has(player, rule.advancement())) { continue; }
            if (rule.replace()) { generatedLoot.clear(); }
            for (Drop drop : rule.drops()) {
                if (ONLY.equals(drop.silkTouch()) && !silk) { continue; }
                if (NEVER.equals(drop.silkTouch()) && silk) { continue; }
                if (drop.chance() < 1.0F && random.nextFloat() >= drop.chance()) { continue; }
                int count = drop.count().pick(random);
                if (drop.fortune() > 0 && fortuneLevel > 0) { count += random.nextInt(drop.fortune() * fortuneLevel + 1); }
                if (count <= 0) { continue; }
                if (drop.experience()) {
                    level.addFreshEntity(new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, count));
                    continue;
                }
                if (withheld || radius != null && random.nextFloat() > 1.0F / radius) { continue; }
                generatedLoot.add(drop.item().copyWithCount(count));
            }
        }
    }

    private static boolean silk(BlockState state, ServerLevel level, BlockPos pos, @Nullable Player player, @Nullable ItemStack tool) { return player != null && tool != null && level(level, tool, Enchantments.SILK_TOUCH) > 0 && silkHarvests(state, level, pos); }

    private static boolean silkHarvests(BlockState state, ServerLevel level, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof IronBarsBlock || block instanceof WebBlock || block instanceof EnderChestBlock) { return true; }
        return !state.hasBlockEntity() && Block.isShapeFullBlock(block.defaultBlockState().getShape(level, pos));
    }

    static int level(ServerLevel level, ItemStack tool, ResourceKey<Enchantment> enchantment) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(tool::getEnchantmentLevel).orElse(0);
    }
}
