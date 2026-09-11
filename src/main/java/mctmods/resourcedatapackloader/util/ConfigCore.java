package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.neoforged.fml.loading.FMLPaths;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ConfigCore {
    public static final String FILE = ResourceDataPackLoader.MOD_ID + "-common.toml";
    private static final long RESTAT_MILLIS = 1000L;
    @Nullable private static Map<String, Object> cached;
    @Nullable private static FileTime cachedStamp;
    private static long cachedSize = -1L;
    private static long checkedAt = Long.MIN_VALUE;
    @Nullable private static Path located;

    private ConfigCore() {}

    public static synchronized Path file() {
        if (located == null) { located = FMLPaths.CONFIGDIR.get().resolve(FILE); }
        return located;
    }

    public static String text(String path, String fallback) {
        Object held = read(path);
        return held instanceof String ? (String) held : fallback;
    }

    public static boolean flag(String path, boolean fallback) {
        Object held = read(path);
        return held instanceof Boolean ? (Boolean) held : fallback;
    }

    @Nullable private static synchronized Object read(String path) {
        long now = System.currentTimeMillis();
        if (cached != null && now - checkedAt < RESTAT_MILLIS) { return cached.get(path); }
        Path file = file();
        if (!Files.isRegularFile(file)) { return null; }
        try {
            checkedAt = now;
            FileTime stamp = Files.getLastModifiedTime(file);
            long size = Files.size(file);
            if (cached == null || !stamp.equals(cachedStamp) || size != cachedSize) {
                Map<String, Object> flat = new HashMap<>();
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    flatten(TomlFormat.instance().createParser().parse(reader), "", flat);
                }
                cached = flat;
                cachedStamp = stamp;
                cachedSize = size;
            }
            return cached.get(path);
        }
        catch (IOException | RuntimeException unreadable) { return null; }
    }

    private static void flatten(UnmodifiableConfig config, String prefix, Map<String, Object> into) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof UnmodifiableConfig nested) { flatten(nested, key, into); }
            else { into.put(key, value); }
        }
    }
}
