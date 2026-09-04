package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.interfaces.IPackConsumer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class RDPLPack {
    public static final String ASSETS = PackType.CLIENT_RESOURCES.getDirectory();
    public static final String DATA = PackType.SERVER_DATA.getDirectory();
    private final String name;
    private final int priority;
    private final boolean overriding;
    private final Path root;
    @Nullable private final FileSystem owned;
    @Nullable private final Set<String> ownedNamespaces;
    private final Map<PackType, Map<String, Set<String>>> index = new EnumMap<>(PackType.class);
    private int fileCount;

    RDPLPack(String name, int priority, boolean overriding, Path root, @Nullable FileSystem owned) {
        this(name, priority, overriding, root, owned, null);
    }

    RDPLPack(String name, int priority, boolean overriding, Path root, @Nullable FileSystem owned, @Nullable Set<String> ownedNamespaces) {
        this.name = name;
        this.priority = priority;
        this.overriding = overriding;
        this.root = root;
        this.owned = owned;
        this.ownedNamespaces = ownedNamespaces;
        buildIndex();
    }

    public static boolean lacksContent(Path root) { return !Files.isDirectory(root.resolve(ASSETS)) && !Files.isDirectory(root.resolve(DATA)); }

    public boolean isFromMod() { return ownedNamespaces != null; }

    public String getName() { return name; }

    public int getPriority() { return priority; }

    public boolean isOverriding() { return overriding; }

    public boolean isEmpty() { return index.isEmpty(); }

    public Set<String> getNamespaces(PackType type) { return Collections.unmodifiableSet(index.getOrDefault(type, Collections.emptyMap()).keySet()); }

    Set<String> getPaths(PackType type, String namespace) { return index.getOrDefault(type, Collections.emptyMap()).getOrDefault(namespace, Collections.emptySet()); }

    public int getFileCount() { return fileCount; }

    public int getFileCount(PackType type) {
        int total = 0;
        for (Set<String> paths : index.getOrDefault(type, Collections.emptyMap()).values()) { total += paths.size(); }
        return total;
    }

    private void buildIndex() {
        for (PackType type : PackType.values()) {
            Path home = root.resolve(type.getDirectory());
            if (!Files.isDirectory(home)) { continue; }
            try (Stream<Path> stream = Files.list(home)) { stream.filter(Files::isDirectory).filter(this::ownsNamespace).forEach(dir -> indexNamespace(type, dir)); }
            catch (IOException | UncheckedIOException ex) {
                ContentLog.LOGGER.error("Pack '{}': could not list namespaces under {}", name, type.getDirectory(), ex);
            }
        }
    }

    private boolean ownsNamespace(Path dir) {
        if (ownedNamespaces == null) { return true; }
        String namespace = trimSeparator(dir.getFileName().toString());
        if (ownedNamespaces.contains(namespace)) { return true; }
        ContentLog.LOGGER.warn("Mod pack '{}' ships files under the namespace '{}', which it does not declare in its mods.toml, so they are ignored. A mod may only supply content for its own namespace; anything else belongs in a pack under the pack folder", name, namespace);
        return false;
    }

    private void indexNamespace(PackType type, Path dir) {
        String namespace = trimSeparator(dir.getFileName().toString());
        Set<String> paths = new LinkedHashSet<>();
        int nested = 0;
        try (Stream<Path> stream = Files.walk(dir)) {
            for (String path : (Iterable<String>) stream.filter(Files::isRegularFile).map(p -> relative(dir, p))::iterator) {
                if (path.startsWith(PackManager.ROOT_DIRECTORY + "/")) { nested++; }
                else { paths.add(path); }
            }
        }
        catch (IOException | UncheckedIOException ex) {
            ContentLog.LOGGER.error("Pack '{}': could not index {}/{}", name, type.getDirectory(), namespace, ex);
            return;
        }
        if (nested > 0) { ContentLog.LOGGER.warn("Pack '{}': {} file(s) under '{}/{}/{}/' are ignored. Nothing reads a '{}' folder inside a namespace; content folders sit directly under the namespace", name, nested, type.getDirectory(), namespace, PackManager.ROOT_DIRECTORY, PackManager.ROOT_DIRECTORY); }
        if (paths.isEmpty()) { return; }
        index.computeIfAbsent(type, k -> new HashMap<>()).put(namespace, paths);
        fileCount += paths.size();
    }

    private static String relative(Path dir, Path file) {
        String base = dir.toString().replace('\\', '/');
        if (!base.endsWith("/")) { base = base + "/"; }
        String path = file.toString().replace('\\', '/');
        if (path.startsWith(base)) { return path.substring(base.length()); }
        return trimLeadingSeparator(dir.relativize(file).toString().replace('\\', '/'));
    }

    private static String trimLeadingSeparator(String path) {
        int start = 0;
        while (start < path.length() && path.charAt(start) == '/') { start++; }
        return start == 0 ? path : path.substring(start);
    }

    private static String trimSeparator(String raw) {
        if (raw.endsWith("/") || raw.endsWith("\\")) { return raw.substring(0, raw.length() - 1); }
        return raw;
    }

    private Path locate(PackType type, String namespace, String path) { return root.resolve(type.getDirectory()).resolve(namespace).resolve(path); }

    public InputStream open(PackType type, String namespace, String path) throws IOException { return Files.newInputStream(locate(type, namespace, path)); }

    private String read(PackType type, String namespace, String path) throws IOException { return Files.readString(locate(type, namespace, path)); }

    public void forEach(PackType type, String folder, String ext, IPackConsumer consumer) {
        String prefix = folder + "/";
        String suffix = "." + ext;
        for (Map.Entry<String, Set<String>> entry : index.getOrDefault(type, Collections.emptyMap()).entrySet()) {
            String namespace = entry.getKey();
            for (String path : entry.getValue()) {
                if (!path.startsWith(prefix) || !path.endsWith(suffix)) { continue; }
                String id = path.substring(prefix.length(), path.length() - suffix.length());
                try { consumer.accept(namespace, id, read(type, namespace, path)); }
                catch (IOException ex) { ContentLog.LOGGER.error("Pack '{}': could not read {}/{}/{}", name, type.getDirectory(), namespace, path, ex); }
            }
        }
    }

    public List<String> packFiles(String folder, String ext) {
        List<String> out = new ArrayList<>();
        Path home = root.resolve(folder);
        if (!Files.isDirectory(home)) { return out; }
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(home)) {
            for (Path entry : entries) {
                String fileName = trimSeparator(entry.getFileName().toString());
                if (Files.isRegularFile(entry) && fileName.endsWith("." + ext)) { out.add(fileName); }
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Pack '{}': could not list {}", name, folder, ex); }
        return out;
    }

    @Nullable public Path packFile(String fileName) {
        Path file = root.resolve(fileName);
        return Files.isRegularFile(file) ? file : null;
    }

    @Nullable public String readPackFile(String fileName) throws IOException {
        Path file = packFile(fileName);
        return file == null ? null : Files.readString(file);
    }

    public void close() throws IOException {
        if (owned != null) { owned.close(); }
    }
}
