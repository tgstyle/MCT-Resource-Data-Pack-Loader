package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class PackLootTables<T> {
    @FunctionalInterface public interface Loader<T> { @Nullable T load(ResourceLocation location, String contents); }

    private final Map<ResourceLocation, T> cache = new HashMap<>();

    @Nullable public T serve(ResourceLocation location, String path, Loader<T> loader, String fallback) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return null; }
        T cached = cache.get(location);
        if (cached != null) { return cached; }
        String contents = manager.read(location.getNamespace(), path, PackManager.LOOT_TABLES, PackManager.JSON);
        if (contents == null) { return null; }
        try {
            T table = loader.load(location, contents);
            if (table != null) { cache.put(location, table); }
            return table;
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            ContentLog.LOGGER.error("Parsing error in loot table {}, falling back to {}", location, fallback, ex);
            return null;
        }
    }

    public void clear() { cache.clear(); }

    public static String tablePath(ResourceLocation location) {
        String path = location.getPath();
        return path.startsWith(PackManager.LOOT_TABLES + "/") ? path.substring(PackManager.LOOT_TABLES.length() + 1) : path;
    }
}
