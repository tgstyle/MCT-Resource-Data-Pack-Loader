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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ContentRetrogen {
    private static final Map<ResourceKey<Level>, Deque<Pending>> QUEUES = new HashMap<>();
    private static final List<ContentWorldgen.Entry> ARMED = new ArrayList<>();
    private static final Set<String> STAMP = new LinkedHashSet<>();
    private static int queued;
    private static int completed;

    private ContentRetrogen() {}

    public static void setup(Collection<ContentWorldgen.Entry> entries) {
        QUEUES.clear();
        ARMED.clear();
        STAMP.clear();
        queued = 0;
        completed = 0;
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
        if (STAMP.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        if (event.isNewChunk()) {
            ContentChunkTokens.put(chunk, STAMP);
            return;
        }
        Set<String> already = ContentChunkTokens.get(chunk);
        if (already.isEmpty() && adoptWanted()) {
            ContentChunkTokens.put(chunk, STAMP);
            ContentLog.LOGGER.debug("Adopted chunk {} as though this pack had generated it", chunk.getPos());
            return;
        }
        if (!wanted() || ARMED.isEmpty()) { return; }
        List<ContentWorldgen.Entry> pending = new ArrayList<>();
        for (ContentWorldgen.Entry entry : ARMED) {
            if (!already.contains(token(entry.def())) && ContentWorldgen.dimensionAllows(entry, level)) { pending.add(entry); }
        }
        if (pending.isEmpty()) { return; }
        QUEUES.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>()).add(new Pending(chunk.getPos(), pending));
        queued++;
        ContentLog.LOGGER.debug("Queued chunk {} for retrogen, {} entries behind", chunk.getPos(), pending.size());
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
        int budget = Config.worldgen.retrogenChunksPerTick();
        for (int step = 0; step < budget && !queue.isEmpty(); step++) {
            run(level, queue.poll());
            queued--;
        }
        if (queue.isEmpty() && completed > 0) {
            Summary.info("retrogen.done", "Caught up " + completed + " chunk(s) that were made before the pack");
            completed = 0;
        }
    }

    private static void run(ServerLevel level, Pending pending) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pending.pos().x, pending.pos().z);
        if (chunk == null) { return; }
        BlockPos origin = new BlockPos(pending.pos().getMinBlockX(), level.getMinBuildHeight(), pending.pos().getMinBlockZ());
        PlacementContext context = new PlacementContext(level, level.getChunkSource().getGenerator(), Optional.empty());
        Set<String> already = new LinkedHashSet<>(ContentChunkTokens.get(chunk));
        try {
            for (ContentWorldgen.Entry entry : pending.entries()) {
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

    private record Pending(ChunkPos pos, List<ContentWorldgen.Entry> entries) {}
}
