package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentReplacements {
    private static final int FLAGS = 2 | 16;
    private static final int PER_TICK = 4;
    private static final Map<BlockState, BlockState> EXACT = new HashMap<>();
    private static final Map<Block, BlockState> WHOLE = new LinkedHashMap<>();
    private static final Map<BlockState, String> KEYS = new IdentityHashMap<>();
    private static final Map<String, Long> REPLACED = new LinkedHashMap<>();
    private static final Set<String> REPORTED = new HashSet<>();
    private static final Map<ResourceKey<Level>, Deque<ChunkPos>> QUEUES = new HashMap<>();
    private static Set<String> dimensions;
    private static boolean blacklist;
    private static boolean logging;
    private static boolean wanted;
    private static String token = "";
    private static int minHeight;
    private static int maxHeight;
    private static long chunks;

    private ContentReplacements() {}

    public static boolean wanted() {
        if (dimensions == null) { load(); }
        return wanted;
    }

    public static boolean appliesTo(Level level) {
        if (dimensions == null) { load(); }
        if (dimensions.isEmpty()) { return true; }
        return dimensions.contains(level.dimension().location().toString()) != blacklist;
    }

    public static void reload() {
        dimensions = null;
        EXACT.clear();
        WHOLE.clear();
        KEYS.clear();
        REPORTED.clear();
        REPLACED.clear();
        chunks = 0L;
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        if (!wanted() || !appliesTo(level)) { return; }
        if (ContentChunkTokens.get(chunk).contains(token)) { return; }
        QUEUES.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>()).add(chunk.getPos());
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { QUEUES.remove(level.dimension()); }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Deque<ChunkPos> queue = QUEUES.get(level.dimension());
        if (queue == null || queue.isEmpty()) { return; }
        int budget = PER_TICK;
        while (budget > 0 && !queue.isEmpty()) {
            ChunkPos pos = queue.pollFirst();
            if (!level.hasChunk(pos.x, pos.z)) { continue; }
            LevelChunk chunk = level.getChunk(pos.x, pos.z);
            Set<String> already = ContentChunkTokens.get(chunk);
            if (already.contains(token)) { continue; }
            replace(level, chunk);
            Set<String> tokens = new HashSet<>(already);
            tokens.add(token);
            ContentChunkTokens.put(chunk, tokens);
            budget--;
        }
    }

    private static void replace(ServerLevel level, LevelChunk chunk) {
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        int replaced = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        LevelChunkSection[] sections = chunk.getSections();
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section == null || section.hasOnlyAir()) { continue; }
            int bottom = chunk.getSectionYFromSectionIndex(index) << 4;
            if (bottom > maxHeight || bottom + 15 < minHeight) { continue; }
            for (int y = 0; y < 16; y++) {
                int worldY = bottom + y;
                if (worldY < minHeight || worldY > maxHeight) { continue; }
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState found = section.getBlockState(x, y, z);
                        BlockState swap = replacementFor(found);
                        if (swap == null) { continue; }
                        pos.set(baseX + x, worldY, baseZ + z);
                        level.setBlock(pos, swap, FLAGS);
                        count(found, swap);
                        replaced++;
                    }
                }
            }
        }
        if (replaced > 0) {
            chunks++;
            ContentLog.LOGGER.debug("Replaced {} block(s) in chunk {} of {}", replaced, chunk.getPos(), level.dimension().location());
        }
    }

    public static void report() {
        long total = 0L;
        for (long held : REPLACED.values()) { total += held; }
        if (total == 0L) { return; }
        Summary.info("replacements", "Replaced " + total + " block(s) in " + chunks + " chunk(s) that already existed");
        if (logging) {
            for (Map.Entry<String, Long> entry : REPLACED.entrySet()) { ContentLog.LOGGER.debug("  replaced {} {}", entry.getValue(), entry.getKey()); }
        }
    }

    @Nullable private static BlockState replacementFor(BlockState found) {
        BlockState swap = WHOLE.get(found.getBlock());
        if (swap == null && !EXACT.isEmpty()) { swap = EXACT.get(found); }
        if (swap == null || swap == found) { return null; }
        return swap;
    }

    private static void count(BlockState found, BlockState swap) {
        String key = KEYS.computeIfAbsent(found, held -> name(held) + " to " + name(swap));
        REPLACED.merge(key, 1L, Long::sum);
        if (logging && REPORTED.add(key)) { ContentLog.LOGGER.info("Replacing {} in chunks that already exist", key); }
    }

    private static String name(BlockState state) { return BuiltInRegistries.BLOCK.getKey(state.getBlock()) + (state.getValues().isEmpty() ? "" : state.getValues().toString()); }

    private static void load() {
        dimensions = new HashSet<>();
        if (ContentControl.off(ContentControl.REPLACEMENTS)) {
            token = "replace:off";
            wanted = false;
            return;
        }
        for (String entry : ContentControl.list(ContentControl.REPLACEMENTS, "blockReplacementDimensions", Config.worldgen.blockReplacementDimensions())) { dimensions.add(ContentFormats.dimensionId(entry)); }
        minHeight = ContentControl.number(ContentControl.REPLACEMENTS, "blockReplacementMinHeight", Config.worldgen.blockReplacementMinHeight());
        maxHeight = Math.max(minHeight, ContentControl.number(ContentControl.REPLACEMENTS, "blockReplacementMaxHeight", Config.worldgen.blockReplacementMaxHeight()));
        EXACT.clear();
        WHOLE.clear();
        for (String entry : ContentControl.list(ContentControl.REPLACEMENTS, "blockReplacements", Config.worldgen.blockReplacements())) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                ContentLog.LOGGER.error("blockReplacements entry '{}' needs the form block=block, ignoring it", entry);
                continue;
            }
            List<BlockState> from = states(parts[0].trim(), entry);
            List<BlockState> to = states(parts[1].trim(), entry);
            if (from.isEmpty() || to.isEmpty()) { continue; }
            BlockState swap = to.getFirst();
            if (!parts[0].contains("[")) { WHOLE.put(from.getFirst().getBlock(), swap); }
            else {
                for (BlockState state : from) { EXACT.put(state, swap); }
            }
        }
        blacklist = ContentControl.flag(ContentControl.REPLACEMENTS, "blockReplacementDimensionsAreBlacklist", Config.worldgen.blockReplacementDimensionsAreBlacklist());
        logging = ContentControl.flag(ContentControl.REPLACEMENTS, "logBlockReplacements", Config.worldgen.logBlockReplacements());
        wanted = !EXACT.isEmpty() || !WHOLE.isEmpty();
        token = "replace:" + Integer.toHexString(EXACT.keySet().hashCode() * 31 + WHOLE.keySet().hashCode()) + "#" + ContentControl.text(ContentControl.REPLACEMENTS, "blockReplacementKey", Config.worldgen.blockReplacementKey()).trim();
        if (wanted) { Summary.info("replacements.setup", "Replacing " + (EXACT.size() + WHOLE.size()) + " kind(s) of block in chunks that already exist, between y" + minHeight + " and y" + maxHeight); }
    }

    private static List<BlockState> states(String written, String entry) {
        String name = written;
        Map<String, String> properties = new LinkedHashMap<>();
        int open = written.indexOf('[');
        if (open >= 0) {
            name = written.substring(0, open).trim();
            String inside = written.substring(open + 1, written.endsWith("]") ? written.length() - 1 : written.length());
            for (String pair : inside.split(",")) {
                String[] halves = pair.split("=", 2);
                if (halves.length == 2) { properties.put(halves[0].trim(), halves[1].trim()); }
            }
        }
        ResourceLocation id = ResourceLocation.tryParse(name);
        Block block = Registered.find(BuiltInRegistries.BLOCK, id);
        if (block == null) {
            ContentLog.LOGGER.error("blockReplacements entry '{}' names '{}', which is not a registered block, ignoring it", entry, name);
            return List.of();
        }
        if (properties.isEmpty()) { return List.of(block.defaultBlockState()); }
        List<BlockState> found = ContentStates.matching(block, properties, "blockReplacements entry '" + entry + "'");
        if (found.isEmpty()) { ContentLog.LOGGER.error("blockReplacements entry '{}' names a state of {} that does not exist, ignoring it", entry, name); }
        return found;
    }
}
