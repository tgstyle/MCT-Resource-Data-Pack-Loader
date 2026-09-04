package mctmods.resourcedatapackloader.pack;

import net.minecraft.server.packs.PackType;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class GeneratedResources {
    private static final Map<PackType, Map<String, Map<String, byte[]>>> HELD = new EnumMap<>(PackType.class);

    static {
        for (PackType type : PackType.values()) { HELD.put(type, new ConcurrentHashMap<>()); }
    }

    private GeneratedResources() {}

    public static void put(PackType type, String namespace, String path, String contents) {
        HELD.get(type).computeIfAbsent(namespace, k -> new ConcurrentHashMap<>()).put(path, contents.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable public static byte[] get(PackType type, String namespace, String path) {
        Map<String, byte[]> paths = HELD.get(type).get(namespace);
        return paths == null ? null : paths.get(path);
    }

    public static boolean has(PackType type, String namespace, String path) { return get(type, namespace, path) != null; }

    public static Set<String> namespaces(PackType type) { return Collections.unmodifiableSet(new LinkedHashSet<>(HELD.get(type).keySet())); }

    public static void list(PackType type, String namespace, String prefix, Consumer<String> out) {
        Map<String, byte[]> paths = HELD.get(type).get(namespace);
        if (paths == null) { return; }
        String head = prefix.isEmpty() || prefix.endsWith("/") ? prefix : prefix + "/";
        for (String path : paths.keySet()) {
            if (path.startsWith(head)) { out.accept(path); }
        }
    }

    public static void remove(PackType type, String prefix) {
        for (Map<String, byte[]> paths : HELD.get(type).values()) { paths.keySet().removeIf(path -> path.startsWith(prefix)); }
    }

    public static boolean isEmpty() {
        for (Map<String, Map<String, byte[]>> held : HELD.values()) {
            if (!held.isEmpty()) { return false; }
        }
        return true;
    }

    public static int count() {
        int total = 0;
        for (Map<String, Map<String, byte[]>> held : HELD.values()) {
            for (Map<String, byte[]> paths : held.values()) { total += paths.size(); }
        }
        return total;
    }

    public static void clear() {
        for (Map<String, Map<String, byte[]>> held : HELD.values()) { held.clear(); }
    }
}
