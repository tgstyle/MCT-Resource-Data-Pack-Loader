package mctmods.resourcedatapackloader.common.leafdecay;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentLeafDecay {
    private static final String BLOCKS = "blocks";
    private static final String TAG = "tag";
    private static final String ALL = "all";
    private static final String EXCEPT = "except";
    private static final List<String> DECAY_KEYS = List.of(BLOCKS, TAG, ALL, EXCEPT, "minTicks", "maxTicks", "dimensions", "requires");
    private static final List<String> EXCEPT_KEYS = List.of(BLOCKS, TAG);
    private static final int MOST_TICKS = 1200;
    private static final List<LeafDecayRule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentLeafDecay() {}

    public static boolean wanted() {
        int[] found = {0};
        Json.eachFile(PackManager.LEAF_DECAY, "leaf decay rule", (key, contents) -> found[0]++);
        return found[0] > 0;
    }

    static boolean load() {
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

    private static List<TagKey<Block>> tags(ResourceLocation key, JsonObject json, String part) {
        List<TagKey<Block>> found = new ArrayList<>();
        for (String name : Json.names(json, TAG)) {
            ResourceLocation id = Registered.tagId(name);
            if (id == null) { ContentLog.LOGGER.error("Leaf decay {} names tag '{}' in {}, which is not a valid tag id, skipping it", key, name, part); }
            else { found.add(TagKey.create(Registries.BLOCK, id)); }
        }
        return found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        Json.unknownKeys(json, "the top level", DECAY_KEYS);
        boolean all = Json.getBoolean(json, ALL, false);
        if (!json.has(BLOCKS) && !json.has(TAG) && !all) {
            ContentLog.LOGGER.error("Leaf decay {} needs blocks, tag or all, ignoring it", key);
            return;
        }
        BlockGroup blocks = group(key, json, BLOCKS);
        List<TagKey<Block>> tags = tags(key, json, TAG);
        if (!all && blocks.isEmpty() && tags.isEmpty()) {
            ContentLog.LOGGER.error("Leaf decay {} names no registered blocks, ignoring it", key);
            return;
        }
        JsonObject except = Json.object(json, EXCEPT);
        Json.unknownKeys(except, EXCEPT, EXCEPT_KEYS);
        int min = (int) Json.ranged(json, "minTicks", 10, 1, MOST_TICKS, key);
        int max = (int) Json.ranged(json, "maxTicks", 60, 1, MOST_TICKS, key);
        if (max < min) {
            ContentLog.LOGGER.error("Leaf decay {} gives maxTicks {} below minTicks {}, ignoring it", key, max, min);
            return;
        }
        RULES.add(new LeafDecayRule(blocks, tags, all, group(key, except, EXCEPT + "." + BLOCKS), tags(key, except, EXCEPT + "." + TAG), Json.names(json, "dimensions"), min, max));
    }

    @Nullable static LeafDecayRule ruleFor(BlockState state, Level level) {
        for (LeafDecayRule rule : RULES) {
            if (rule.covers(state, level)) { return rule; }
        }
        return null;
    }

    public static boolean covered(BlockState state, Level level) { return LeafDecays.qualifies(state) && load() && ruleFor(state, level) != null; }
}
