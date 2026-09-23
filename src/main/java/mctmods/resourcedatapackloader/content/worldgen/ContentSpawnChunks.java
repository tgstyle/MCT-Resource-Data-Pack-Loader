package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public final class ContentSpawnChunks {
    private static final int MOST_BLOCKS = 1024;
    private static final String OVERWORLD = "minecraft:overworld";
    private static final int BOOT_MARGIN = 4;
    private static final int FARTHEST_TICKET = 33;
    private static final int BLOCK_TICKING = 1;
    private static final int ENTITY_TICKING = 2;
    private static final TicketType<Unit> BOOT = TicketType.create("rdpl_spawn_boot", (a, b) -> 0);
    private static final TicketType<Unit> HELD = TicketType.create("rdpl_spawn", (a, b) -> 0);
    @Nullable private static ChunkPos bootAt;
    private static int bootRadius;
    @Nullable private static ChunkPos heldAt;
    private static int heldRadius;

    private ContentSpawnChunks() {}

    public static boolean leftToTheGame() { return ContentControl.off(ContentControl.CHUNKS); }

    private static int blocks() { return Mth.clamp(asked(), 0, MOST_BLOCKS); }

    private static int asked() {
        int asked = ContentControl.number(ContentControl.CHUNKS, "spawnChunkRadius", Config.chunks.spawnChunkRadius());
        for (String entry : ContentControl.list(ContentControl.CHUNKS, "spawnChunkRadii", Config.chunks.spawnChunkRadii())) {
            int split = entry.indexOf('=');
            Integer blocks = split > 0 ? ContentDimensions.height(entry.substring(split + 1).trim()) : null;
            if (blocks == null) {
                if (ContentWorldShape.WARNED.add("spawnChunkRadii:" + entry)) { ContentLog.LOGGER.error("spawnChunkRadii entry '{}' is not written as dimension=blocks, ignoring it", entry); }
            }
            else if (ContentFormats.dimensionId(entry.substring(0, split)).equals(OVERWORLD)) { asked = blocks; }
        }
        return asked;
    }

    private static int heldChunks() { return (blocks() + 8) / 16; }

    public static int bootChunks() { return blocks() <= 0 ? 0 : heldChunks() + BOOT_MARGIN; }

    public static int bootCount() { return blocks() <= 0 ? 0 : square(bootChunks()); }

    private static int square(int radius) { return (2 * radius + 1) * (2 * radius + 1); }

    public static void begin() {
        bootAt = null;
        heldAt = null;
    }

    public static void boot(ServerChunkCache chunks, ChunkPos spawn) {
        if (blocks() <= 0) { return; }
        bootAt = spawn;
        bootRadius = bootChunks();
        tiles(bootAt, bootRadius, BLOCK_TICKING, (pos, distance) -> chunks.addRegionTicket(BOOT, pos, distance, Unit.INSTANCE));
    }

    public static void booted(ServerLevel level) {
        ServerChunkCache chunks = level.getChunkSource();
        int prepared = chunks.getTickingGenerated();
        hold(level, new ChunkPos(level.getSharedSpawnPos()));
        if (bootAt != null) { tiles(bootAt, bootRadius, BLOCK_TICKING, (pos, distance) -> chunks.removeRegionTicket(BOOT, pos, distance, Unit.INSTANCE)); }
        if (blocks() <= 0) { ContentLog.LOGGER.info("Preparing and holding no chunks around the spawn point, as spawnChunkRadius 0 asks"); }
        else { ContentLog.LOGGER.info("Prepared {} chunk(s) around the spawn point as the world started, {} each way, and holding {} chunk(s), {} each way", prepared, bootRadius, square(heldRadius), heldRadius); }
        bootAt = null;
    }

    public static void hold(ServerLevel level, ChunkPos spawn) {
        ServerChunkCache chunks = level.getChunkSource();
        if (heldAt != null) { tiles(heldAt, heldRadius, ENTITY_TICKING, (pos, distance) -> chunks.removeRegionTicket(HELD, pos, distance, Unit.INSTANCE)); }
        heldAt = null;
        if (blocks() <= 0) { return; }
        heldAt = spawn;
        heldRadius = heldChunks();
        tiles(heldAt, heldRadius, ENTITY_TICKING, (pos, distance) -> chunks.addRegionTicket(HELD, pos, distance, Unit.INSTANCE));
    }

    private static void tiles(ChunkPos middle, int radius, int ticking, BiConsumer<ChunkPos, Integer> ticket) {
        int tile = FARTHEST_TICKET - ticking;
        if (radius <= tile) {
            ticket.accept(middle, radius + ticking);
            return;
        }
        int reach = radius - tile;
        int steps = Mth.positiveCeilDiv(2 * reach, 2 * tile + 1);
        for (int i = 0; i <= steps; i++) {
            for (int j = 0; j <= steps; j++) { ticket.accept(new ChunkPos(middle.x - reach + 2 * reach * i / steps, middle.z - reach + 2 * reach * j / steps), FARTHEST_TICKET); }
        }
    }
}
