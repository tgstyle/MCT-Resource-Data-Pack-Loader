package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ContentRetrogen {
    private static final Map<ResourceKey<Level>, Deque<Pending>> QUEUES = new HashMap<>();
    private static final List<ContentWorldgen.Entry> ARMED = new ArrayList<>();
    private static final Set<String> STAMP = new LinkedHashSet<>();
    private static final Map<ResourceKey<Level>, Spent> SPENT = new HashMap<>();
    private static int queued;
    private static int completed;
    private static int flattened;

    private ContentRetrogen() {}

    public static boolean canCatchUp(ServerLevel level, ChunkPos pos) {
        Set<ChunkPos> spent = spent(level);
        return spent.contains(pos) || spent.size() < Config.worldgen.retrogenChunksPerTick();
    }

    public static void caughtUp(ServerLevel level, ChunkPos pos) { spent(level).add(pos); }

    private static Set<ChunkPos> spent(ServerLevel level) {
        Spent spent = SPENT.computeIfAbsent(level.dimension(), dimension -> new Spent());
        if (spent.tick != level.getGameTime()) {
            spent.tick = level.getGameTime();
            spent.chunks.clear();
        }
        return spent.chunks;
    }

    public static void setup(Collection<ContentWorldgen.Entry> entries) {
        QUEUES.clear();
        ARMED.clear();
        STAMP.clear();
        queued = 0;
        completed = 0;
        flattened = 0;
        for (ContentWorldgen.Entry entry : entries) {
            STAMP.add(token(entry.def()));
            if (entry.def().retrogen()) { ARMED.add(entry); }
        }
        if (ARMED.isEmpty()) { return; }
        if (wanted()) { Summary.info("retrogen", "Retrogen is on: " + ARMED.size() + " worldgen entries catch up in chunks older than the pack"); }
        else { ContentLog.LOGGER.info("{} worldgen entries ask for retrogen, which is off, so chunks that already exist are left alone", ARMED.size()); }
    }

    public static String token(WorldgenDef def) { return def.key() + "#" + Config.worldgen.retrogenKey().trim() + def.retrogenKey().trim(); }

    public static boolean wanted() { return ContentControl.flag(ContentControl.CHUNKS, "retrogen", Config.worldgen.retrogen()); }

    private static boolean adoptWanted() { return ContentControl.flag(ContentControl.CHUNKS, "adoptExistingChunks", Config.worldgen.adoptExistingChunks()); }

    public static void onChunkLoad(ChunkEvent.Load event) {
        boolean bedrock = ContentBedrock.bedrockAsked();
        if ((STAMP.isEmpty() && !bedrock) || !(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        Set<String> already = ContentChunkTokens.get(chunk);
        if (event.isNewChunk()) {
            stamp(chunk, already, bedrock);
            return;
        }
        if (adoptWanted() && !STAMP.isEmpty() && adoptable(already)) {
            already = stamp(chunk, already, false);
            ContentLog.LOGGER.debug("Adopted chunk {} as though this pack had generated it", chunk.getPos());
        }
        if (!wanted()) { return; }
        List<ContentWorldgen.Entry> pending = new ArrayList<>();
        for (ContentWorldgen.Entry entry : ARMED) {
            if (!already.contains(token(entry.def())) && ContentWorldgen.dimensionAllows(entry, level)) { pending.add(entry); }
        }
        boolean flatten = ContentBedrock.bedrockRetrogen() && !already.contains(ContentBedrock.bedrockToken()) && ContentBedrock.flattens(level);
        if (pending.isEmpty() && !flatten) { return; }
        QUEUES.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>()).add(new Pending(chunk.getPos(), pending, flatten));
        queued++;
        ContentLog.LOGGER.debug("Queued chunk {} for retrogen, {} entries behind{}", chunk.getPos(), pending.size(), flatten ? ", bedrock to flatten" : "");
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Deque<Pending> queue = QUEUES.remove(level.dimension());
        if (queue != null) { queued -= queue.size(); }
        if (QUEUES.isEmpty()) { queued = 0; }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || queued == 0 || !(event.level instanceof ServerLevel level)) { return; }
        Deque<Pending> queue = QUEUES.get(level.dimension());
        if (queue == null || queue.isEmpty()) { return; }
        while (!queue.isEmpty() && canCatchUp(level, queue.peekFirst().pos())) {
            Pending pending = queue.removeFirst();
            run(level, pending);
            queued--;
            caughtUp(level, pending.pos());
        }
        if (queue.isEmpty() && completed > 0) {
            Summary.info("retrogen.done", "Caught up " + completed + " chunk(s) that were made before the pack" + (flattened > 0 ? ", flattening bedrock in " + flattened + " of them" : ""));
            completed = 0;
            flattened = 0;
        }
    }

    private static void run(ServerLevel level, Pending pending) {
        LevelChunk chunk = level.getChunk(pending.pos().x, pending.pos().z);
        BlockPos origin = new BlockPos(pending.pos().getMinBlockX(), level.getMinBuildHeight(), pending.pos().getMinBlockZ());
        PlacementContext context = new PlacementContext(level, level.getChunkSource().getGenerator(), Optional.empty());
        Set<String> already = new LinkedHashSet<>(ContentChunkTokens.get(chunk));
        try {
            if (pending.flatten() && !already.contains(ContentBedrock.bedrockToken())) {
                ContentBedrock.flattenChunk(level, pending.pos());
                already.add(ContentBedrock.bedrockToken());
                flattened++;
            }
            for (ContentWorldgen.Entry entry : pending.entries()) {
                if (already.contains(token(entry.def()))) { continue; }
                RandomSource random = ContentSpread.regionRandom(level, pending.pos());
                ContentPlacer placer = new ContentPlacer(level, entry.palette(), pending.pos(), ContentPlacer.CHUNK_ONLY);
                for (BlockPos at : ContentSpreadPlacement.positions(entry, context, random, origin).toList()) { ContentShapeFeature.run(entry, placer, random, pending.pos(), at); }
                already.add(token(entry.def()));
            }
        }
        catch (RuntimeException failed) {
            ContentLog.LOGGER.error("Retrogen failed for chunk {}, leaving it as it was", pending.pos(), failed);
            return;
        }
        ContentChunkTokens.put(chunk, already);
        completed++;
    }

    private static Set<String> stamp(LevelChunk chunk, Set<String> already, boolean bedrock) {
        Set<String> stamped = new LinkedHashSet<>(already);
        stamped.addAll(STAMP);
        if (bedrock) { stamped.add(ContentBedrock.bedrockToken()); }
        ContentChunkTokens.put(chunk, stamped);
        return stamped;
    }

    private static boolean adoptable(Set<String> already) {
        for (String token : already) {
            if (!token.startsWith("bedrock:") && !token.startsWith("replace:") && !token.startsWith("swap:")) { return false; }
        }
        return true;
    }

    private record Pending(ChunkPos pos, List<ContentWorldgen.Entry> entries, boolean flatten) {}

    private static final class Spent {
        private final Set<ChunkPos> chunks = new HashSet<>();
        private long tick = -1L;
    }
}
