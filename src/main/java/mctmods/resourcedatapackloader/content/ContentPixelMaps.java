package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.util.TintFactory;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public final class ContentPixelMaps {
    public static final String SUFFIX = ".json";
    public static final String PNG = ".png";
    public static final String CACHE_DIRECTORY = "pixelmap-cache";
    private static final String EXTENDS = "extends";
    private static final String PALETTE = "palette";
    private static final String ROWS = "rows";
    private static final String SIZE = "size";
    private static final String NOTES = "notes";
    private static final String TINT = "tint";
    private static final int MAX_SIDE = 4096;
    private static final int HASH_LENGTH = 16;
    private static final char PLAIN = 'n';
    private static final char OVERRIDING = 'o';
    private static final Map<String, byte[]> MADE = new ConcurrentHashMap<>();
    private static final Set<String> FAILED = ConcurrentHashMap.newKeySet();

    private ContentPixelMaps() {}

    public static void forget() {
        MADE.clear();
        FAILED.clear();
    }

    public static boolean couldBeDrawn(String path) { return path.endsWith(PNG); }

    public static boolean exists(String namespace, String path, boolean overriding) { return PackManager.get().existsRaw(namespace, path + SUFFIX, overriding); }

    @Nullable public static byte[] made(String namespace, String path, boolean overriding) {
        String key = namespace + ":" + path + (overriding ? "!" : "");
        byte[] held = MADE.get(key);
        if (held != null) { return held; }
        if (FAILED.contains(key)) { return null; }
        byte[] drawn = build(namespace, path, overriding);
        if (drawn == null) {
            FAILED.add(key);
            return null;
        }
        MADE.put(key, drawn);
        return drawn;
    }

    @Nullable private static byte[] build(String namespace, String path, boolean overriding) {
        Resolved resolved = resolve(namespace, path, overriding);
        if (resolved == null) { return null; }
        String stamp = hash(resolved.sources);
        byte[] cached = fromCache(namespace, path, overriding, stamp);
        if (cached != null) { return cached; }
        byte[] drawn = paint(namespace, path, resolved);
        if (drawn != null) { toCache(namespace, path, overriding, stamp, drawn); }
        return drawn;
    }

    @Nullable public static Resolved resolve(String namespace, String path, boolean overriding) {
        Map<String, String> palette = new LinkedHashMap<>();
        Map<String, String> notes = new LinkedHashMap<>();
        Map<String, String> from = new LinkedHashMap<>();
        List<String> chain = new ArrayList<>();
        List<String> rows = null;
        String rowsFrom = null;
        TintFactory tint = null;
        int[] size = null;
        int[] base = null;
        String baseFrom = null;
        StringBuilder sources = new StringBuilder();
        String where = namespace;
        String at = path + SUFFIX;
        Set<String> seen = new LinkedHashSet<>();
        while (at != null) {
            if (!seen.add(where + ":" + at)) {
                ContentLog.LOGGER.error("Pixel map {}:{} reaches itself again through {}:{}, so nothing is drawn", namespace, path, where, at);
                return null;
            }
            String contents = read(where, at, overriding);
            if (contents == null) {
                ContentLog.LOGGER.error("Pixel map {}:{} needs {}:{}, which no pack provides, so nothing is drawn", namespace, path, where, at);
                return null;
            }
            sources.append(where).append(':').append(at).append('\n').append(contents).append('\n');
            chain.add(where + ":" + at);
            JsonObject json;
            try { json = new Gson().fromJson(contents, JsonObject.class); }
            catch (JsonParseException ex) {
                ContentLog.LOGGER.error("Pixel map {}:{} could not be read, so nothing is drawn", where, at, ex);
                return null;
            }
            if (json == null) {
                ContentLog.LOGGER.error("Pixel map {}:{} is empty, so nothing is drawn", where, at);
                return null;
            }
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, PALETTE, new JsonObject()).entrySet()) {
                if (palette.containsKey(entry.getKey())) { continue; }
                palette.put(entry.getKey(), entry.getValue().getAsString());
                from.put(entry.getKey(), where + ":" + at);
            }
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, NOTES, new JsonObject()).entrySet()) {
                if (!notes.containsKey(entry.getKey())) { notes.put(entry.getKey(), entry.getValue().getAsString()); }
            }
            if (rows == null && json.has(ROWS)) {
                rowsFrom = where + ":" + at;
                JsonArray listed = JsonUtils.getJsonArray(json, ROWS);
                rows = new ArrayList<>(listed.size());
                for (JsonElement row : listed) { rows.add(row.getAsString()); }
            }
            if (tint == null && json.has(TINT)) {
                JsonObject asked = JsonUtils.getJsonObject(json, TINT);
                tint = TintFactory.of(asked);
                if (tint == null) {
                    ContentLog.LOGGER.error("Pixel map {}:{} tints from '{}' to '{}', and both must be #RRGGBB or #AARRGGBB, so nothing is drawn", where, at, JsonUtils.getString(asked, TintFactory.FROM, ""), JsonUtils.getString(asked, TintFactory.TO, ""));
                    return null;
                }
            }
            if (size == null && json.has(SIZE)) {
                size = size(JsonUtils.getString(json, SIZE, ""));
                if (size == null) {
                    ContentLog.LOGGER.error("Pixel map {}:{} has the size '{}', which is not written as widthxheight such as 16x16 or 16x32, so nothing is drawn", where, at, JsonUtils.getString(json, SIZE, ""));
                    return null;
                }
            }
            String next = JsonUtils.getString(json, EXTENDS, "");
            if (next.isEmpty()) { break; }
            int colon = next.indexOf(':');
            String nextWhere = colon < 0 ? where : next.substring(0, colon);
            String nextPath = colon < 0 ? next : next.substring(colon + 1);
            if (PackManager.get().existsRaw(nextWhere, nextPath + SUFFIX, overriding)) {
                where = nextWhere;
                at = nextPath + SUFFIX;
                continue;
            }
            int[][] image = image(nextWhere, nextPath);
            if (image == null) {
                ContentLog.LOGGER.error("Pixel map {}:{} builds on {}:{}, which is neither a pixel map nor an image any pack or the game provides, so nothing is drawn", namespace, path, nextWhere, nextPath);
                return null;
            }
            if (size == null) { size = image[0]; }
            base = image[1];
            baseFrom = nextWhere + ":" + nextPath;
            chain.add(baseFrom);
            sources.append(baseFrom).append('\n').append(hash(base)).append('\n');
            at = null;
        }
        if (size == null) {
            ContentLog.LOGGER.error("Pixel map {}:{} names no size and inherits none, so nothing is drawn. Give it a size such as \"16x16\"", namespace, path);
            return null;
        }
        if (base == null && (rows == null || rows.isEmpty())) {
            ContentLog.LOGGER.error("Pixel map {}:{} has no rows and inherits none, so nothing is drawn", namespace, path);
            return null;
        }
        if (size[0] < 1 || size[1] < 1 || size[0] > MAX_SIDE || size[1] > MAX_SIDE) {
            ContentLog.LOGGER.error("Pixel map {}:{} asks for {} by {}, and each side must be between 1 and {}, so nothing is drawn", namespace, path, size[0], size[1], MAX_SIDE);
            return null;
        }
        if (base != null) {
            if (base.length != size[0] * size[1]) {
                ContentLog.LOGGER.error("Pixel map {}:{} builds on {}, which is not {} by {}, so nothing is drawn", namespace, path, baseFrom, size[0], size[1]);
                return null;
            }
            return new Resolved(size, rows, palette, notes, from, chain, rowsFrom, tint, base, baseFrom, sources.toString());
        }
        if (rows.size() != size[1]) {
            ContentLog.LOGGER.error("Pixel map {}:{} is {} tall but has {} row(s). Give it one row per line of pixels, from the top down", namespace, path, size[1], rows.size());
            return null;
        }
        for (int y = 0; y < rows.size(); y++) {
            if (rows.get(y).length() != size[0]) {
                ContentLog.LOGGER.error("Pixel map {}:{} is {} wide but row {} holds {} character(s). Give it one character per pixel across", namespace, path, size[0], y + 1, rows.get(y).length());
                return null;
            }
        }
        return new Resolved(size, rows, palette, notes, from, chain, rowsFrom, tint, null, null, sources.toString());
    }

    public static final class Resolved {
        public final int[] size;
        public final List<String> rows;
        public final Map<String, String> palette;
        public final Map<String, String> notes;
        public final Map<String, String> from;
        public final List<String> chain;
        public final String rowsFrom;
        @Nullable public final TintFactory tint;
        @Nullable public final int[] base;
        @Nullable public final String baseFrom;
        final String sources;

        Resolved(int[] size, List<String> rows, Map<String, String> palette, Map<String, String> notes, Map<String, String> from, List<String> chain, String rowsFrom, @Nullable TintFactory tint, @Nullable int[] base, @Nullable String baseFrom, String sources) {
            this.size = size;
            this.rows = rows;
            this.palette = palette;
            this.notes = notes;
            this.from = from;
            this.chain = chain;
            this.rowsFrom = rowsFrom;
            this.tint = tint;
            this.base = base;
            this.baseFrom = baseFrom;
            this.sources = sources;
        }

        public int used(String character) {
            if (rows == null || character.length() != 1) { return 0; }
            char wanted = character.charAt(0);
            int count = 0;
            for (String row : rows) {
                for (int i = 0; i < row.length(); i++) { if (row.charAt(i) == wanted) { count++; } }
            }
            return count;
        }
    }

    @Nullable private static String read(String namespace, String path, boolean overriding) {
        try (InputStream stream = PackManager.get().openRaw(namespace, path, overriding)) {
            return stream == null ? null : IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Pixel map {}:{} could not be opened", namespace, path, ex);
            return null;
        }
    }

    @Nullable private static int[] size(String written) {
        int split = written.indexOf('x');
        if (split < 1 || split == written.length() - 1) { return null; }
        try { return new int[] { Integer.parseInt(written.substring(0, split).trim()), Integer.parseInt(written.substring(split + 1).trim()) }; }
        catch (NumberFormatException ex) { return null; }
    }

    @Nullable private static byte[] paint(String namespace, String path, Resolved resolved) {
        int[] size = resolved.size;
        Map<String, String> palette = resolved.palette;
        if (resolved.base != null) { return repaint(namespace, path, resolved, resolved.base); }
        List<String> rows = resolved.rows;
        Map<Character, Integer> colors = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : palette.entrySet()) {
            if (entry.getKey().length() != 1) {
                ContentLog.LOGGER.error("Pixel map {}:{} has the palette key '{}', which is not a single character, ignoring it", namespace, path, entry.getKey());
                continue;
            }
            Integer color = color(entry.getValue());
            if (color == null) {
                ContentLog.LOGGER.error("Pixel map {}:{} gives '{}' the color '{}', which is not #RRGGBB or #AARRGGBB, ignoring it", namespace, path, entry.getKey(), entry.getValue());
                continue;
            }
            colors.put(entry.getKey().charAt(0), resolved.tint == null ? color : resolved.tint.shade(color));
        }
        BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < size[1]; y++) {
            String row = rows.get(y);
            for (int x = 0; x < size[0]; x++) {
                Integer color = colors.get(row.charAt(x));
                image.setRGB(x, y, color == null ? 0 : color);
            }
        }
        return written(namespace, path, image);
    }

    @Nullable private static byte[] written(String namespace, String path, BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Pixel map {}:{} could not be written out, so nothing is drawn", namespace, path, ex);
            return null;
        }
    }

    @Nullable private static Path cacheFolder() {
        Path root = PackManager.get().getRoot();
        return root == null ? null : root.resolve(CACHE_DIRECTORY);
    }

    private static String stampOf(boolean overriding, String hash) { return (overriding ? OVERRIDING : PLAIN) + hash; }

    @Nullable private static Path cacheFile(String namespace, String path, boolean overriding, String hash) {
        Path folder = cacheFolder();
        return folder == null ? null : folder.resolve(namespace).resolve(path + "-" + stampOf(overriding, hash) + PNG);
    }

    @Nullable private static byte[] fromCache(String namespace, String path, boolean overriding, String hash) {
        Path file = cacheFile(namespace, path, overriding, hash);
        if (file == null || !Files.isRegularFile(file)) { return null; }
        try { return Files.readAllBytes(file); }
        catch (IOException ex) {
            ContentLog.LOGGER.warn("Cached pixel map {} could not be read, drawing it again", file, ex);
            return null;
        }
    }

    private static void toCache(String namespace, String path, boolean overriding, String hash, byte[] drawn) {
        Path file = cacheFile(namespace, path, overriding, hash);
        if (file == null) { return; }
        try {
            Files.createDirectories(file.getParent());
            sweep(file.getParent(), name(path), overriding, file.getFileName().toString());
            Files.write(file, drawn);
        }
        catch (IOException ex) { ContentLog.LOGGER.warn("Pixel map {}:{} could not be cached, it will be drawn again next time", namespace, path, ex); }
    }

    private static String name(String path) {
        int slash = path.lastIndexOf('/');
        return slash < 0 ? path : path.substring(slash + 1);
    }

    private static void sweep(Path folder, String name, boolean overriding, String keep) throws IOException {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder, name + "-" + (overriding ? OVERRIDING : PLAIN) + "*" + PNG)) {
            for (Path entry : entries) {
                if (entry.getFileName().toString().equals(keep)) { continue; }
                try { Files.deleteIfExists(entry); }
                catch (IOException ex) { ContentLog.LOGGER.warn("Stale cached pixel map {} could not be removed", entry, ex); }
            }
        }
    }

    public static void tidy() {
        Path folder = cacheFolder();
        if (folder == null || !Files.isDirectory(folder)) { return; }
        List<Path> dropped = new ArrayList<>();
        try (Stream<Path> found = Files.walk(folder)) {
            found.filter(Files::isRegularFile).forEach(file -> {
                if (!orphaned(folder, file)) { return; }
                dropped.add(file);
            });
        }
        catch (IOException ex) {
            ContentLog.LOGGER.warn("The pixel map cache at {} could not be looked over", folder, ex);
            return;
        }
        int removed = 0;
        for (Path file : dropped) {
            try {
                Files.deleteIfExists(file);
                removed++;
            }
            catch (IOException ex) { ContentLog.LOGGER.warn("Cached pixel map {} is no longer wanted but could not be removed", file, ex); }
        }
        if (removed > 0) { ContentLog.LOGGER.debug("Cleared {} cached pixel map(s) whose map no packs provide any more", removed); }
        prune(folder);
    }

    private static boolean orphaned(Path folder, Path file) {
        Path relative = folder.relativize(file);
        if (relative.getNameCount() < 2) { return true; }
        String namespace = relative.getName(0).toString();
        StringBuilder path = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) { path.append(i > 1 ? "/" : "").append(relative.getName(i)); }
        String held = path.toString();
        int dash = held.lastIndexOf('-');
        if (!held.endsWith(PNG) || dash < 0) { return true; }
        String stamp = held.substring(dash + 1, held.length() - PNG.length());
        if (stamp.isEmpty()) { return true; }
        boolean overriding = stamp.charAt(0) == OVERRIDING;
        if (!overriding && stamp.charAt(0) != PLAIN) { return true; }
        return !PackManager.get().existsRaw(namespace, held.substring(0, dash) + SUFFIX, overriding);
    }

    private static void prune(Path folder) {
        try (Stream<Path> found = Files.walk(folder)) {
            List<Path> folders = new ArrayList<>();
            found.filter(Files::isDirectory).forEach(folders::add);
            for (int i = folders.size() - 1; i > 0; i--) {
                Path entry = folders.get(i);
                try (DirectoryStream<Path> inside = Files.newDirectoryStream(entry)) {
                    if (!inside.iterator().hasNext()) { Files.deleteIfExists(entry); }
                }
                catch (IOException ignored) { }
            }
        }
        catch (IOException ignored) { }
    }

    private static String hash(String sources) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] made = digest.digest(sources.getBytes(StandardCharsets.UTF_8));
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < made.length && text.length() < HASH_LENGTH; i++) { text.append(Character.forDigit((made[i] >> 4) & 0xF, 16)).append(Character.forDigit(made[i] & 0xF, 16)); }
            return text.substring(0, HASH_LENGTH);
        }
        catch (NoSuchAlgorithmException ex) { return Integer.toHexString(sources.hashCode()); }
    }

    @Nullable private static byte[] repaint(String namespace, String path, Resolved resolved, int[] base) {
        Map<Integer, Integer> swaps = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : resolved.palette.entrySet()) {
            Integer was = color(entry.getKey());
            Integer becomes = color(entry.getValue());
            if (was == null || becomes == null) {
                ContentLog.LOGGER.error("Pixel map {}:{} builds on an image, so its palette must go from one color to another, and '{}' to '{}' does not, ignoring it", namespace, path, entry.getKey(), entry.getValue());
                continue;
            }
            swaps.put(was, becomes);
        }
        int[] size = resolved.size;
        BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < size[1]; y++) {
            for (int x = 0; x < size[0]; x++) {
                int was = base[y * size[0] + x];
                Integer becomes = swaps.get(was);
                int drawn = becomes == null ? was : becomes;
                image.setRGB(x, y, resolved.tint == null ? drawn : resolved.tint.shade(drawn));
            }
        }
        return written(namespace, path, image);
    }

    @Nullable private static int[][] image(String namespace, String path) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) { return null; }
        if (!ContentPixelImages.exists(namespace, path)) { return null; }
        return ContentPixelImages.read(namespace, path);
    }

    private static String hash(int[] pixels) {
        StringBuilder text = new StringBuilder();
        for (int pixel : pixels) { text.append(Integer.toHexString(pixel)); }
        return hash(text.toString());
    }

    @Nullable private static Integer color(String written) { return TintFactory.color(written); }
}
