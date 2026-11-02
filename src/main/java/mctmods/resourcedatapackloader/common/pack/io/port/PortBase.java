package mctmods.resourcedatapackloader.common.pack.io.port;

import mctmods.resourcedatapackloader.common.pack.io.port.commands.FunctionLines;
import static mctmods.resourcedatapackloader.common.pack.loading.RDPLPack.key;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.PackPaths;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackType;

public abstract class PortBase<S extends PortBase.IServed> implements IPackPort {
    public interface IServed {
        Path real();

        Port.Kind kind();
    }

    public final Map<PackType, Map<String, Map<String, S>>> exposed = new EnumMap<>(PackType.class);
    public final String name;
    public final Path root;
    protected final PackPaths places;
    public final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    final Set<String> notes = new LinkedHashSet<>();
    public boolean reported;
    public int rewritten;

    public PortBase(String name, Path root, PackPaths places) {
        this.name = name;
        this.root = root;
        this.places = places;
    }

    public final void note(String said) { if (notes.add(said) && reported) { ContentLog.LOGGER.info("Pack '{}': {}", name, said); } }

    public final void listNotes() {
        List<String> said = new ArrayList<>(notes);
        for (int i = 0; i < Math.min(said.size(), 40); i++) { ContentLog.LOGGER.info("  {}", said.get(i)); }
        if (said.size() > 40) { ContentLog.LOGGER.info("  ... and {} more", said.size() - 40); }
    }

    public static JsonObject packOf(@Nullable JsonObject old) { return old != null && old.has("pack") && old.get("pack").isJsonObject() ? old.getAsJsonObject("pack") : new JsonObject(); }

    public final void writeMeta(ZipOutputStream out, String prefix, JsonObject pack) throws IOException {
        if (!pack.has("description")) { pack.addProperty("description", name); }
        JsonObject meta = new JsonObject();
        meta.add("pack", pack);
        out.putNextEntry(new ZipEntry(prefix + PackManager.PACK_META));
        out.write(Ported.GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
    }

    public final FunctionLines.Result function(String contents, String path, String target, UnaryOperator<String> command) {
        FunctionLines.Result result = FunctionLines.convert(contents, path, command, this::note);
        if (result.changed() > 0 || result.kept() > 0) { note("'" + path + "' is a " + origin() + " function: " + result.changed() + " command(s) rewritten for " + target + ", " + result.kept() + " turned into comments"); }
        return result;
    }

    public abstract byte[] convert(S source, String namespace, String path) throws IOException;

    @Override @Nullable public final InputStream open(PackType type, String namespace, String path) throws IOException {
        S source = exposed.getOrDefault(type, Map.of()).getOrDefault(namespace, Map.of()).get(path);
        if (source == null) { return null; }
        if (source.kind() == Port.Kind.RAW) { return Files.newInputStream(source.real()); }
        String key = key(type, namespace, path);
        byte[] held = cache.get(key);
        if (held == null) {
            held = convert(source, namespace, path);
            cache.put(key, held);
        }
        return new ByteArrayInputStream(held);
    }

    public final void expose(PackType type, String namespace, String path, S source) { exposed.computeIfAbsent(type, _ -> new LinkedHashMap<>()).computeIfAbsent(namespace, _ -> new LinkedHashMap<>()).putIfAbsent(path, source); }

    @Override public final Map<PackType, Map<String, Set<String>>> exposed() {
        Map<PackType, Map<String, Set<String>>> out = new EnumMap<>(PackType.class);
        exposed.forEach((type, namespaces) -> {
            Map<String, Set<String>> paths = new LinkedHashMap<>();
            namespaces.forEach((namespace, held) -> paths.put(namespace, new LinkedHashSet<>(held.keySet())));
            out.put(type, paths);
        });
        return out;
    }

    @Nullable public static JsonObject readJson(Path file) {
        try {
            JsonElement held = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException failed) { return null; }
    }
}
