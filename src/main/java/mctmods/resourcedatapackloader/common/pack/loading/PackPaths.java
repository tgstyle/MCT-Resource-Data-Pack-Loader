package mctmods.resourcedatapackloader.common.pack.loading;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public final class PackPaths {
    private final String pack;
    private final Map<Path, Path> real = new HashMap<>();
    private final Set<String> warned = new HashSet<>();

    PackPaths(String pack) { this.pack = pack; }

    public Path real(Path asked) { return real.getOrDefault(asked, asked); }

    static Map<String, List<Path>> namespaces(Path home) throws IOException {
        Map<String, List<Path>> out = new TreeMap<>();
        addNamespaces(home, out);
        return out;
    }

    private static void addNamespaces(Path folder, Map<String, List<Path>> out) throws IOException {
        List<Path> dirs = new ArrayList<>();
        try (Stream<Path> stream = Files.list(folder)) { stream.sorted().filter(Files::isDirectory).forEach(dirs::add); }
        for (Path dir : dirs) {
            String name = name(dir);
            if (grouped(name)) { addNamespaces(dir, out); }
            else { out.computeIfAbsent(name, k -> new ArrayList<>()).add(dir); }
        }
    }

    List<String> walk(List<Path> dirs, Path home) throws IOException {
        List<String> asked = new ArrayList<>();
        for (Path dir : dirs) {
            List<String> written = new ArrayList<>();
            try (Stream<Path> files = Files.walk(dir)) { files.filter(Files::isRegularFile).forEach(file -> written.add(relative(dir, file))); }
            Collections.sort(written);
            for (String path : written) {
                String plain = plain(path);
                Path file = dir.resolve(path);
                Path held = real.putIfAbsent(home.resolve(plain), file);
                if (held == null) { asked.add(plain); }
                else { ContentLog.LOGGER.error("Pack '{}': {} and {} land on the same path, {}. The first is kept and the second is ignored", pack, held, file, home.resolve(plain)); }
            }
        }
        return asked;
    }

    private String plain(String path) {
        if (path.indexOf('(') < 0 && path.indexOf(')') < 0) { return path; }
        String[] parts = path.split("/");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            boolean folder = i < parts.length - 1;
            if (folder && grouped(parts[i])) { continue; }
            if (folder && (parts[i].indexOf('(') >= 0 || parts[i].indexOf(')') >= 0) && warned.add(parts[i])) { ContentLog.LOGGER.warn("Pack '{}': the folder '{}' is not a parenthesized folder, so it stays in the path", pack, parts[i]); }
            if (!out.isEmpty()) { out.append('/'); }
            out.append(parts[i]);
        }
        return out.toString();
    }

    static boolean grouped(String folder) {
        int last = folder.length() - 1;
        return last >= 2 && folder.charAt(0) == '(' && folder.indexOf(')') == last && folder.lastIndexOf('(') == 0;
    }

    private static String name(Path dir) {
        String raw = dir.getFileName().toString();
        return raw.endsWith("/") || raw.endsWith("\\") ? raw.substring(0, raw.length() - 1) : raw;
    }

    private static String relative(Path dir, Path file) {
        String base = dir.toString().replace('\\', '/');
        if (!base.endsWith("/")) { base = base + "/"; }
        String path = file.toString().replace('\\', '/');
        if (path.startsWith(base)) { return path.substring(base.length()); }
        String rest = dir.relativize(file).toString().replace('\\', '/');
        int start = 0;
        while (start < rest.length() && rest.charAt(start) == '/') { start++; }
        return rest.substring(start);
    }
}
