package mctmods.resourcedatapackloader.common.pack.io;

import net.minecraft.server.packs.PackType;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class GeneratedResources {
    private static final Map<PackType, Map<String, Map<String, Supplier<byte[]>>>> HELD = new EnumMap<>(PackType.class);

    static {
        for (PackType type : PackType.values()) { HELD.put(type, new ConcurrentHashMap<>()); }
    }

    private GeneratedResources() {}

    public static void put(PackType type, String namespace, String path, String contents) { HELD.get(type).computeIfAbsent(namespace, k -> new ConcurrentHashMap<>()).put(path, fixed(contents.getBytes(StandardCharsets.UTF_8))); }

    public static void put(PackType type, String namespace, String path, byte[] contents) { HELD.get(type).computeIfAbsent(namespace, k -> new ConcurrentHashMap<>()).put(path, fixed(contents.clone())); }

    public static void putLazy(PackType type, String namespace, String path, Supplier<byte[]> contents) {
        AtomicReference<byte[]> cache = new AtomicReference<>();
        HELD.get(type).computeIfAbsent(namespace, k -> new ConcurrentHashMap<>()).put(path, () -> {
            byte[] bytes = cache.get();
            if (bytes == null) {
                bytes = contents.get();
                cache.set(bytes);
            }
            return bytes;
        });
    }

    private static Supplier<byte[]> fixed(byte[] contents) { return () -> contents; }

    @Nullable public static byte[] get(PackType type, String namespace, String path) {
        Map<String, Supplier<byte[]>> paths = HELD.get(type).get(namespace);
        Supplier<byte[]> held = paths == null ? null : paths.get(path);
        return held == null ? null : held.get();
    }

    public static boolean has(PackType type, String namespace, String path) { return get(type, namespace, path) != null; }

    public static Set<String> namespaces(PackType type) { return Collections.unmodifiableSet(new LinkedHashSet<>(HELD.get(type).keySet())); }

    public static String head(String prefix) { return prefix.isEmpty() || prefix.endsWith("/") ? prefix : prefix + "/"; }

    public static void list(PackType type, String namespace, String prefix, Consumer<String> out) {
        Map<String, Supplier<byte[]>> paths = HELD.get(type).get(namespace);
        if (paths == null) { return; }
        String head = head(prefix);
        for (String path : paths.keySet()) {
            if (path.startsWith(head)) { out.accept(path); }
        }
    }

    public static void remove(PackType type, String prefix) { for (Map<String, Supplier<byte[]>> paths : HELD.get(type).values()) { paths.keySet().removeIf(path -> path.startsWith(prefix)); } }

    public static boolean holds() {
        for (Map<String, Map<String, Supplier<byte[]>>> held : HELD.values()) { if (!held.isEmpty()) { return true; } }
        return false;
    }

    public static int count() {
        int total = 0;
        for (Map<String, Map<String, Supplier<byte[]>>> held : HELD.values()) {
            for (Map<String, Supplier<byte[]>> paths : held.values()) { total += paths.size(); }
        }
        return total;
    }

    public static void clear() { for (Map<String, Map<String, Supplier<byte[]>>> held : HELD.values()) { held.clear(); } }
}
