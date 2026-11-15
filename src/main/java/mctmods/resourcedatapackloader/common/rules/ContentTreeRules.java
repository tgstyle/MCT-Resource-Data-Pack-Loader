package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SaplingDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeRuleDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserTrees;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.plant.DynamicTrees;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public final class ContentTreeRules {
    public static final String FOLDER = "tree_growth";
    private static final String PART = "saplings";
    private static final Map<String, Entry> RULES = new LinkedHashMap<>();
    private static final Map<Block, List<Entry>> BY_BLOCK = new ConcurrentHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static volatile boolean present;

    private ContentTreeRules() {}

    private static final class Entry {
        private final TreeRuleDef def;
        private final BlockGroup blocks = new BlockGroup();
        private final List<BlockMatch> tags = new ArrayList<>();

        private Entry(TreeRuleDef def) { this.def = def; }

        private boolean tagged(Block block) {
            for (BlockMatch tag : tags) {
                if (tag.matches(block, null, BlockPos.ORIGIN)) { return true; }
            }
            return false;
        }

        private boolean covers(IBlockState state) { return blocks.contains(unstaged(state)) || tagged(state.getBlock()); }

        private static IBlockState unstaged(IBlockState state) {
            return state.getPropertyKeys().contains(BlockSapling.STAGE) ? state.withProperty(BlockSapling.STAGE, 0) : state;
        }

        private boolean mayCover(Block block) {
            if (tagged(block)) { return true; }
            for (IBlockState state : block.getBlockState().getValidStates()) {
                if (blocks.contains(state)) { return true; }
            }
            return false;
        }
    }

    private static void load() {
        if (!GENERATION.stale()) { return; }
        RULES.clear();
        BY_BLOCK.clear();
        Json.eachObject(FOLDER, "tree growth rule", ContentTreeRules::read);
        present = !RULES.isEmpty();
        if (present) { Summary.info("content.treegrowth", "Loaded " + RULES.size() + " tree growth rule(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        TreeRuleDef def = ContentParserTrees.rule(key, json);
        if (def == null) { return; }
        Entry entry = new Entry(def);
        for (BlockMatchDef match : def.targets) {
            Block block = ContentStates.block(match.block.toString(), key + " " + PART);
            if (block != null && takes(def, block)) { entry.blocks.add(match, key, PART); }
        }
        for (String name : def.tagTargets) {
            BlockMatch tag = BlockMatches.parse(def.id, "#" + name, PART);
            if (tag != null) { entry.tags.add(tag); }
        }
        RULES.put(def.id, entry);
    }

    private static boolean takes(TreeRuleDef def, Block block) {
        if (block instanceof ContentSaplingBlock) {
            ContentLog.LOGGER.warn("Tree growth rule {} targets {}, an RDPL sapling whose own JSON is its rule, skipping it", def.id, block.getRegistryName());
            return false;
        }
        if (!(block instanceof IGrowable) && !block.getTickRandomly()) {
            ContentLog.LOGGER.warn("Tree growth rule {} targets {}, which neither grows by bone meal nor ticks randomly, skipping it", def.id, block.getRegistryName());
            return false;
        }
        return !owned(def, block);
    }

    private static boolean owned(TreeRuleDef def, Block block) {
        if (!DynamicTrees.loaded() || !DynamicTrees.owns(block)) { return false; }
        ContentLog.LOGGER.info("Tree growth rule {} targets {}, which Dynamic Trees owns, skipping it", def.id, block.getRegistryName());
        return true;
    }

    private static List<Entry> candidates(Block block) {
        return BY_BLOCK.computeIfAbsent(block, found -> {
            if (found instanceof ContentSaplingBlock) { return Collections.emptyList(); }
            List<Entry> list = new ArrayList<>();
            for (Entry entry : RULES.values()) {
                if (entry.mayCover(found) && !(entry.tagged(found) && owned(entry.def, found))) { list.add(entry); }
            }
            if (list.size() > 1) {
                ContentLog.LOGGER.warn("Tree growth rules {} all take {}; where they overlap, {} wins as it loaded first", list.stream().map(rule -> rule.def.id).collect(Collectors.toList()), found.getRegistryName(), list.get(0).def.id);
            }
            return list.isEmpty() ? Collections.emptyList() : list;
        });
    }

    @Nullable private static SaplingDef own(Block block) {
        return block instanceof ContentSaplingBlock ? ((ContentSaplingBlock) block).getDef().sapling : null;
    }

    public static boolean any() {
        load();
        return present;
    }

    @Nullable public static TreeRuleDef rule(IBlockState state) {
        load();
        if (!present) { return null; }
        for (Entry entry : candidates(state.getBlock())) {
            if (entry.covers(state)) { return entry.def; }
        }
        return null;
    }

    @Nullable public static TreeGrowthDef growth(IBlockState state) {
        SaplingDef sapling = own(state.getBlock());
        if (sapling != null) { return sapling.growth.growing() ? sapling.growth : null; }
        TreeRuleDef rule = rule(state);
        return rule == null ? null : rule.growth;
    }

    @Nullable public static String ruleId(IBlockState state) {
        SaplingDef sapling = own(state.getBlock());
        if (sapling != null) { return sapling.growth.growing() ? String.valueOf(state.getBlock().getRegistryName()) : null; }
        TreeRuleDef rule = rule(state);
        return rule == null ? null : rule.id;
    }

    @Nullable public static TreeGrowthDef byId(String id) {
        load();
        Entry entry = RULES.get(id);
        if (entry != null) { return entry.def.growth; }
        Block block = Block.getBlockFromName(id);
        SaplingDef sapling = block == null ? null : own(block);
        return sapling != null && sapling.growth.growing() ? sapling.growth : null;
    }
}
