package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.interfaces.IPackConsumer;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class RDPLPack {
    public static final String ASSETS = "assets";
    private final String name;
    private final int priority;
    private final boolean overriding;
    private final Path root;
    @Nullable private final FileSystem owned;
    @Nullable private final Set<String> ownedNamespaces;
    private final Map<String, Set<String>> index = new HashMap<>();
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

    public boolean isFromMod() { return ownedNamespaces != null; }

    public String getName() { return name; }

    public int getPriority() { return priority; }

    public boolean isOverriding() { return overriding; }

    public Set<String> getNamespaces() { return Collections.unmodifiableSet(index.keySet()); }

    Set<String> getPaths(String namespace) { return index.getOrDefault(namespace, Collections.emptySet()); }

    public int getFileCount() { return fileCount; }

    private void buildIndex() {
        Path assets = root.resolve(ASSETS);
        if (!Files.isDirectory(assets)) { return; }
        try (Stream<Path> stream = Files.list(assets)) { stream.filter(Files::isDirectory).filter(this::ownsNamespace).forEach(this::indexNamespace); }
        catch (IOException | UncheckedIOException ex) {
            ContentLog.LOGGER.error("Pack '{}': could not list namespaces", name, ex);
        }
    }

    private boolean ownsNamespace(Path dir) {
        if (ownedNamespaces == null) { return true; }
        String namespace = trimSeparator(dir.getFileName().toString());
        if (ownedNamespaces.contains(namespace)) { return true; }
        ContentLog.LOGGER.warn("Mod pack '{}' ships files under the namespace '{}', which it does not declare in its mcmod.info, so they are ignored. A mod may only supply content for its own namespace; anything else belongs in a pack under the pack folder", name, namespace);
        return false;
    }

    private void indexNamespace(Path dir) {
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
            ContentLog.LOGGER.error("Pack '{}': could not index namespace {}", name, namespace, ex);
            return;
        }
        if (nested > 0) { ContentLog.LOGGER.warn("Pack '{}': {} file(s) under '{}/{}/' are ignored. Nothing reads a '{}' folder inside a namespace; content folders sit directly under the namespace", name, nested, namespace, PackManager.ROOT_DIRECTORY, PackManager.ROOT_DIRECTORY); }
        if (paths.isEmpty()) { return; }
        index.put(namespace, paths);
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

    private Path locate(String namespace, String path) { return root.resolve(ASSETS).resolve(namespace).resolve(path); }

    public InputStream open(String namespace, String path) throws IOException { return Files.newInputStream(locate(namespace, path)); }

    public String read(String namespace, String path) throws IOException { return new String(Files.readAllBytes(locate(namespace, path)), StandardCharsets.UTF_8); }

    public java.util.List<String> packFiles(String folder, String ext) {
        java.util.List<String> out = new java.util.ArrayList<>();
        Path home = root.resolve(folder);
        if (!Files.isDirectory(home)) { return out; }
        try (java.nio.file.DirectoryStream<Path> entries = Files.newDirectoryStream(home)) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (name.endsWith("/")) { name = name.substring(0, name.length() - 1); }
                if (Files.isRegularFile(entry) && name.endsWith("." + ext)) { out.add(name); }
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Pack '{}': could not list {}", this.name, folder, ex); }
        return out;
    }

    @Nullable public String readPackFile(String fileName) throws IOException {
        Path file = root.resolve(fileName);
        if (!Files.isRegularFile(file)) { return null; }
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    @Nullable public InputStream openPackFile(String fileName) throws IOException {
        Path file = root.resolve(fileName);
        if (!Files.isRegularFile(file)) { return null; }
        return Files.newInputStream(file);
    }

    public void forEach(String type, String ext, IPackConsumer consumer) {
        String prefix = type + "/";
        String suffix = "." + ext;
        for (Map.Entry<String, Set<String>> entry : index.entrySet()) {
            String namespace = entry.getKey();
            for (String path : entry.getValue()) {
                if (!path.startsWith(prefix) || !path.endsWith(suffix)) { continue; }
                String id = path.substring(prefix.length(), path.length() - suffix.length());
                try { consumer.accept(namespace, id, read(namespace, path)); }
                catch (IOException ex) { ContentLog.LOGGER.error("Pack '{}': could not read {}:{}", name, namespace, path, ex); }
            }
        }
    }

    public int count(String type, String ext) {
        String prefix = type + "/";
        String suffix = "." + ext;
        int total = 0;
        for (Set<String> paths : index.values()) {
            for (String path : paths) {
                if (path.startsWith(prefix) && path.endsWith(suffix)) { total++; }
            }
        }
        return total;
    }

    public void close() throws IOException {
        if (owned != null) { owned.close(); }
    }
}
