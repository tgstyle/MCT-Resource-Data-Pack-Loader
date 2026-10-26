package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.MathHelper;

public final class ContentSpawnChunks {
    private static final int VANILLA = 128;
    private static final int MOST_BLOCKS = 1024;
    private static final Map<Integer, Integer> BY_DIMENSION = new HashMap<>();
    private static int everywhere = VANILLA;
    private static boolean loaded;

    private ContentSpawnChunks() {}

    public static int radius(int dimension) {
        load();
        Integer own = BY_DIMENSION.get(dimension);
        return own == null ? everywhere : own;
    }

    public static void reload() {
        loaded = false;
        BY_DIMENSION.clear();
        everywhere = VANILLA;
    }

    public static int chunks(int dimension) { return (radius(dimension) + 8) / 16; }

    public static boolean overridden(int dimension) {
        load();
        return everywhere != VANILLA || BY_DIMENSION.containsKey(dimension);
    }

    public static int bootReach() { return radius(0) > 0 ? chunks(0) + 4 : -1; }

    public static int bootPrepared(int reach) {
        int across = 2 * reach + 1;
        return reach < 0 ? 0 : across * across;
    }

    public static void logBoot(int reach) {
        if (reach < 0) { ContentLog.LOGGER.info("Prepared no chunks around the spawn point as the world started, as spawnChunkRadius 0 asks"); }
        else { ContentLog.LOGGER.info("Prepared {} chunk(s) around the spawn point as the world started, {} each way from the spawn chunk", bootPrepared(reach), reach); }
    }

    private static void load() {
        if (loaded) { return; }
        loaded = true;
        if (ContentControl.off(ContentControl.CHUNKS)) {
            everywhere = VANILLA;
            return;
        }
        everywhere = clamp(ContentControl.number(ContentControl.CHUNKS, "spawnChunkRadius", Config.chunks.spawnChunkRadius));
        for (String entry : ContentControl.list(ContentControl.CHUNKS, "spawnChunkRadii", Config.chunks.spawnChunkRadii)) {
            String[] parts = Settings.pair(entry, "spawnChunkRadii", "dimension=blocks");
            if (parts == null) { continue; }
            Integer dimension = Settings.clamped(parts[0], Integer.MIN_VALUE, Integer.MAX_VALUE);
            Integer blocks = Settings.clamped(parts[1], 0, MOST_BLOCKS);
            if (dimension == null || blocks == null) { ContentLog.LOGGER.error("spawnChunkRadii entry '{}' is not two numbers written as dimension=blocks, ignoring it", entry); }
            else { BY_DIMENSION.put(dimension, blocks); }
        }
        if (everywhere == VANILLA && BY_DIMENSION.isEmpty()) { return; }
        Summary.info("chunks.spawn", "Holding " + describe(everywhere) + " around the spawn point"
                + (BY_DIMENSION.isEmpty() ? "" : ", and " + BY_DIMENSION + " for the dimension(s) named"));
    }

    private static String describe(int radius) { return radius <= 0 ? "no chunks" : radius + " block(s) of chunks"; }

    private static int clamp(int wanted) { return MathHelper.clamp(wanted, 0, MOST_BLOCKS); }
}
