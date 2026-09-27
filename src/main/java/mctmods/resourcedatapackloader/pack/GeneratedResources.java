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
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class GeneratedResources {
    private static final Map<PackType, Map<String, Map<String, byte[]>>> HELD = new EnumMap<>(PackType.class);
    private static final Map<PackType, Map<String, Map<String, Supplier<byte[]>>>> LATER = new EnumMap<>(PackType.class);

    static {
        for (PackType type : PackType.values()) {
            HELD.put(type, new ConcurrentHashMap<>());
            LATER.put(type, new ConcurrentHashMap<>());
        }
    }

    private GeneratedResources() {}

    public static void put(PackType type, String namespace, String path, String contents) {
        HELD.get(type).computeIfAbsent(namespace, _ -> new ConcurrentHashMap<>()).put(path, contents.getBytes(StandardCharsets.UTF_8));
    }

    public static void put(PackType type, String namespace, String path, byte[] contents) {
        HELD.get(type).computeIfAbsent(namespace, _ -> new ConcurrentHashMap<>()).put(path, contents.clone());
    }

    public static void putLater(PackType type, String namespace, String path, Supplier<byte[]> contents) {
        LATER.get(type).computeIfAbsent(namespace, _ -> new ConcurrentHashMap<>()).put(path, contents);
    }

    @Nullable public static byte[] get(PackType type, String namespace, String path) {
        Map<String, byte[]> paths = HELD.get(type).get(namespace);
        byte[] held = paths == null ? null : paths.get(path);
        if (held != null) { return held; }
        Map<String, Supplier<byte[]>> later = LATER.get(type).get(namespace);
        Supplier<byte[]> maker = later == null ? null : later.get(path);
        if (maker == null) { return null; }
        byte[] made = maker.get();
        if (made == null) { return null; }
        put(type, namespace, path, made);
        later.remove(path);
        return made;
    }

    public static boolean has(PackType type, String namespace, String path) { return get(type, namespace, path) != null; }

    public static Set<String> namespaces(PackType type) {
        Set<String> namespaces = new LinkedHashSet<>(HELD.get(type).keySet());
        namespaces.addAll(LATER.get(type).keySet());
        return Collections.unmodifiableSet(namespaces);
    }

    public static void list(PackType type, String namespace, String prefix, Consumer<String> out) {
        Set<String> paths = new LinkedHashSet<>();
        Map<String, byte[]> held = HELD.get(type).get(namespace);
        if (held != null) { paths.addAll(held.keySet()); }
        Map<String, Supplier<byte[]>> later = LATER.get(type).get(namespace);
        if (later != null) { paths.addAll(later.keySet()); }
        String head = prefix.isEmpty() || prefix.endsWith("/") ? prefix : prefix + "/";
        for (String path : paths) {
            if (path.startsWith(head)) { out.accept(path); }
        }
    }

    public static void remove(PackType type, String prefix) {
        for (Map<String, byte[]> paths : HELD.get(type).values()) { paths.keySet().removeIf(path -> path.startsWith(prefix)); }
        for (Map<String, Supplier<byte[]>> paths : LATER.get(type).values()) { paths.keySet().removeIf(path -> path.startsWith(prefix)); }
    }

    public static boolean isEmpty() { return count() == 0; }

    public static int count() {
        int total = 0;
        for (Map<String, Map<String, byte[]>> held : HELD.values()) {
            for (Map<String, byte[]> paths : held.values()) { total += paths.size(); }
        }
        for (Map<String, Map<String, Supplier<byte[]>>> later : LATER.values()) {
            for (Map<String, Supplier<byte[]>> paths : later.values()) { total += paths.size(); }
        }
        return total;
    }

    public static void clear() {
        for (Map<String, Map<String, byte[]>> held : HELD.values()) { held.clear(); }
        for (Map<String, Map<String, Supplier<byte[]>>> later : LATER.values()) { later.clear(); }
    }
}
