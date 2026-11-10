package mctmods.resourcedatapackloader.common.leafdecay;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ContentLeafDecay {
    private static final String DYNAMIC_LEAVES = "com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves";
    private static final String BLOCKS = "blocks";
    private static final String TAG = "tag";
    private static final String ALL = "all";
    private static final String EXCEPT = "except";
    private static final int MOST_TICKS = 1200;
    private static final List<LeafDecayRule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static volatile boolean resolved;
    @Nullable private static volatile Class<?> dynamicLeaves;

    private ContentLeafDecay() {}

    public static boolean load() {
        if (!GENERATION.stale()) { return !RULES.isEmpty(); }
        RULES.clear();
        Json.eachObject(PackManager.LEAF_DECAY, "leaf decay rule", ContentLeafDecay::read);
        if (!RULES.isEmpty()) { Summary.info("content.leafdecay", "Loaded " + RULES.size() + " leaf decay rule(s)"); }
        return !RULES.isEmpty();
    }

    private static BlockGroup group(ResourceLocation key, JsonObject json, String part) {
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, key, part); }
        return group;
    }

    private static List<BlockMatch> tags(ResourceLocation key, JsonObject json, String part) {
        List<BlockMatch> found = new ArrayList<>();
        for (String name : Json.strings(json, TAG)) {
            BlockMatch match = BlockMatches.parse(key.toString(), name, part);
            if (match != null) { found.add(match); }
        }
        return found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        boolean all = Json.getBoolean(json, ALL, false);
        if (!json.has(BLOCKS) && !json.has(TAG) && !all) {
            ContentLog.LOGGER.error("Leaf decay {} needs blocks, tag or all, ignoring it", key);
            return;
        }
        BlockGroup blocks = group(key, json, BLOCKS);
        List<BlockMatch> tags = tags(key, json, TAG);
        if (!all && blocks.isEmpty() && tags.isEmpty()) {
            ContentLog.LOGGER.error("Leaf decay {} names no registered blocks, ignoring it", key);
            return;
        }
        JsonObject except = Json.object(json, EXCEPT);
        int min = (int) Json.ranged(json, "minTicks", 10, 1, MOST_TICKS, key);
        int max = (int) Json.ranged(json, "maxTicks", 60, 1, MOST_TICKS, key);
        if (max < min) {
            ContentLog.LOGGER.error("Leaf decay {} gives maxTicks {} below minTicks {}, ignoring it", key, max, min);
            return;
        }
        RULES.add(new LeafDecayRule(blocks, tags, all, group(key, except, EXCEPT + "." + BLOCKS), tags(key, except, EXCEPT + "." + TAG), Json.names(json, "dimensions"), min, max));
    }

    @Nullable private static Class<?> dynamicLeaves() {
        if (!resolved) {
            try { dynamicLeaves = Class.forName(DYNAMIC_LEAVES, false, ContentLeafDecay.class.getClassLoader()); }
            catch (ClassNotFoundException | LinkageError absent) { dynamicLeaves = null; }
            resolved = true;
        }
        return dynamicLeaves;
    }

    static boolean qualifies(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (!(block instanceof BlockLeaves) || !block.isLeaves(state, world, pos)) { return false; }
        Class<?> dynamic = dynamicLeaves();
        if (dynamic != null && dynamic.isInstance(block)) { return false; }
        Collection<IProperty<?>> keys = state.getPropertyKeys();
        return keys.contains(BlockLeaves.DECAYABLE) && keys.contains(BlockLeaves.CHECK_DECAY) && state.getValue(BlockLeaves.DECAYABLE);
    }

    @Nullable static LeafDecayRule ruleFor(IBlockState state, World world, BlockPos pos) {
        for (LeafDecayRule rule : RULES) {
            if (rule.covers(state, world, pos)) { return rule; }
        }
        return null;
    }

    public static int delay(IBlockState state, World world, BlockPos pos, int unruled) {
        LeafDecayRule rule = qualifies(state, world, pos) ? ruleFor(state, world, pos) : null;
        return rule == null ? unruled : rule.delay(world.rand);
    }
}
