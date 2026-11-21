package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;

public final class ContentColumns {
    private static final String BLOCKS = "blocks";
    private static final String ALTERNATE = "alternate";
    private static final List<String> COLUMN_KEYS = List.of("requires", BLOCKS, ALTERNATE, "limit", "includeSelf", "unlessSneaking");
    private static final List<String> ALTERNATE_KEYS = List.of("item", "chance");
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static boolean clearing;

    private ContentColumns() {}

    private record Rule(BlockGroup group, int limit, boolean includeSelf, boolean unlessSneaking, ItemStack alternate, float chance) {}

    public static boolean clearing() { return clearing; }

    private static void reload() {
        RULES.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.COLUMNS, "falling column", ContentColumns::read);
        if (!RULES.isEmpty()) { Summary.info("content.columns", "Loaded " + RULES.size() + " falling column group(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", COLUMN_KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, "Falling column " + key, BLOCKS); }
        if (group.isEmpty()) {
            ContentLog.LOGGER.error("Falling column {} names no registered blocks, ignoring it", key);
            return;
        }
        ItemStack alternate = ItemStack.EMPTY;
        float chance = 0.0F;
        if (json.has(ALTERNATE)) {
            JsonObject extra = Json.object(json, ALTERNATE);
            Json.unknownKeys(extra, ALTERNATE, ALTERNATE_KEYS);
            alternate = ContentStacks.parse(key, Json.text(extra, "item"), 1);
            chance = Json.bounded(extra, "chance", 0.0F, 1.0F, key + " " + ALTERNATE);
            if (alternate.isEmpty() || Float.isNaN(chance)) {
                ContentLog.LOGGER.error("Falling column {} has a bad {}, which needs a registered item and a chance from 0 to 1, ignoring that key", key, ALTERNATE);
                alternate = ItemStack.EMPTY;
                chance = 0.0F;
            }
        }
        RULES.add(new Rule(group, Mth.clamp(Json.getInt(json, "limit", 256), 1, 256), Json.getBoolean(json, "includeSelf", false), Json.getBoolean(json, "unlessSneaking", false), alternate, chance));
    }

    @Nullable private static Rule ruleFor(BlockState above) {
        for (Rule rule : RULES) {
            if (rule.group().contains(above)) { return rule; }
        }
        return null;
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (clearing || ContentCarry.breaking() || ContentCarry.probing() || !(event.getLevel() instanceof ServerLevel level) || !(event.getPlayer() instanceof ServerPlayer player) || player instanceof FakePlayer || player.isSpectator()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (RULES.isEmpty()) { return; }
        BlockPos origin = event.getPos();
        Rule rule = ruleFor(level.getBlockState(origin.above()));
        if (rule == null || rule.unlessSneaking() && player.isShiftKeyDown() || !rule.includeSelf() && rule.group().contains(event.getState())) { return; }
        clearing = true;
        try { clear(level, origin, rule, player); }
        finally { clearing = false; }
    }

    private static void clear(ServerLevel level, BlockPos origin, Rule rule, ServerPlayer player) {
        for (int step = 1; step <= rule.limit(); step++) {
            BlockPos pos = origin.above(step);
            if (level.isOutsideBuildHeight(pos)) { return; }
            BlockState state = level.getBlockState(pos);
            if (!rule.group().contains(state)) { return; }
            BlockEvent.BreakEvent broken = new BlockEvent.BreakEvent(level, pos, state, player);
            if (MinecraftForge.EVENT_BUS.post(broken)) { return; }
            if (!player.isCreative()) { drop(level, pos, state, rule, player); }
            level.destroyBlock(pos, false, player);
        }
    }

    private static void drop(ServerLevel level, BlockPos pos, BlockState state, Rule rule, ServerPlayer player) {
        if (rule.alternate().isEmpty()) { Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, ItemStack.EMPTY); }
        else if (level.random.nextFloat() < rule.chance()) { Block.popResource(level, pos, rule.alternate().copy()); }
        else { Block.popResource(level, pos, new ItemStack(state.getBlock())); }
    }
}
