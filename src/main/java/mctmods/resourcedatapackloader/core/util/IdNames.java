package mctmods.resourcedatapackloader.core.util;

import net.minecraft.resources.Identifier;

import java.util.Map;
import javax.annotation.Nullable;

public final class IdNames {
    public static final String NAMESPACE = "minecraft";
    public static final String MINECRAFT = NAMESPACE + ":";
    public static final String PNG = ".png";

    private IdNames() {}

    public static String path(String id) { return id.substring(id.indexOf(':') + 1); }

    public static String namespace(String id) { int colon = id.indexOf(':'); return colon < 0 ? NAMESPACE : id.substring(0, colon); }

    public static String namespaced(String id) { return id.indexOf(':') < 0 ? MINECRAFT + id : id; }

    @Nullable public static <T> T byName(Map<String, T> defs, String asked) {
        T exact = defs.get(asked);
        if (exact != null) { return exact; }
        for (Map.Entry<String, T> entry : defs.entrySet()) {
            if (path(entry.getKey()).equals(asked)) { return entry.getValue(); }
        }
        return null;
    }

    @Nullable public static Identifier location(String text) {
        Identifier parsed = Identifier.tryParse(text);
        if (parsed == null) { ContentLog.LOGGER.debug("{} is not an item name", text); }
        return parsed;
    }

    public static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
