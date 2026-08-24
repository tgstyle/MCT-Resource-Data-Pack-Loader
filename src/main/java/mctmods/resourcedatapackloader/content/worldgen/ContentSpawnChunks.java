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
    public static final int VANILLA = 128;
    private static final Map<Integer, Integer> BY_DIMENSION = new HashMap<>();
    private static int everywhere = VANILLA;
    private static boolean loaded;

    private ContentSpawnChunks() {}

    public static int radius(int dimension) {
        load();
        Integer own = BY_DIMENSION.get(dimension);
        return own == null ? everywhere : own;
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
            try { BY_DIMENSION.put(Integer.parseInt(parts[0]), clamp(Integer.parseInt(parts[1]))); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("spawnChunkRadii entry '{}' is not two numbers written as dimension=blocks, ignoring it", entry); }
        }
        if (everywhere == VANILLA && BY_DIMENSION.isEmpty()) { return; }
        Summary.info("chunks.spawn", "Holding " + describe(everywhere) + " around the spawn point"
                + (BY_DIMENSION.isEmpty() ? "" : ", and " + BY_DIMENSION + " for the dimension(s) named"));
    }

    private static String describe(int radius) { return radius <= 0 ? "no chunks" : radius + " block(s) of chunks"; }

    private static int clamp(int wanted) { return MathHelper.clamp(wanted, 0, 1024); }
}
