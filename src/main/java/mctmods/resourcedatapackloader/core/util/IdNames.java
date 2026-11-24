package mctmods.resourcedatapackloader.core.util;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class IdNames {
    public static final String VANILLA = "minecraft";
    public static final String MINECRAFT = VANILLA + ":";
    public static final String PNG = ".png";
    public static final String PLAYER = MINECRAFT + "player";

    private IdNames() {}

    public static String path(String id) { return id.substring(id.indexOf(':') + 1); }

    public static String namespace(String id) { return id.indexOf(':') < 0 ? VANILLA : id.substring(0, id.indexOf(':')); }

    public static String namespaced(String id) { return id.indexOf(':') < 0 ? MINECRAFT + id : id; }

    public static String namespaced(String id, String namespace) { return id.isEmpty() || id.indexOf(':') >= 0 ? id : namespace + ":" + id; }

    @Nullable public static <T> T byName(Map<String, T> defs, String asked) {
        T exact = defs.get(asked);
        if (exact != null) { return exact; }
        for (Map.Entry<String, T> entry : defs.entrySet()) {
            if (path(entry.getKey()).equals(asked)) { return entry.getValue(); }
        }
        return null;
    }

    @Nullable public static ResourceLocation optional(String id) { return id.isEmpty() ? null : new ResourceLocation(id); }

    @Nullable public static ResourceLocation location(String text) {
        try { return new ResourceLocation(text); }
        catch (RuntimeException notAName) {
            ContentLog.LOGGER.debug("{} is not an item name", text);
            return null;
        }
    }

    public static boolean isTag(String id) { return id.startsWith("#"); }

    public static String tagPath(String id) { return id.substring(1); }

    @Nullable public static String tag(String text) {
        String held = text.trim();
        return held.startsWith("#") ? held.substring(1).trim() : null;
    }

    public static String javaSafe(String text) { return text.replaceAll("[^A-Za-z0-9_]", "_"); }

    public static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
