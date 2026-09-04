package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class Lang {
    private static final String DEFAULT = "en_us";
    private static final String EXT = ".json";
    private static final Map<String, Map<String, String>> TABLES = new HashMap<>();

    private Lang() {}

    public static void load() {
        TABLES.clear();
        Path home = null;
        try {
            home = ModList.get().getModFileById(ResourceDataPackLoader.MOD_ID).getFile().findResource("assets", ResourceDataPackLoader.MOD_ID, "lang");
            try (Stream<Path> files = Files.list(home)) {
                for (Path file : (Iterable<Path>) files::iterator) {
                    String name = file.getFileName().toString();
                    if (name.endsWith(EXT) && Files.isRegularFile(file)) { table(name.substring(0, name.length() - EXT.length()).toLowerCase(Locale.ROOT), file); }
                }
            }
        }
        catch (IOException | RuntimeException ex) { ContentLog.LOGGER.error("Could not look through the jar for language files under {}", home, ex); }
        if (!TABLES.containsKey(DEFAULT)) {
            ContentLog.LOGGER.error("The language files are missing from the jar, so players will see raw language keys");
            return;
        }
        ContentLog.LOGGER.info("Speaking {} language(s) to players", TABLES.size());
    }

    public static String vanilla(String key) { return Language.getInstance().getOrDefault(key); }

    public static String tr(String key, Object... args) { return line(TABLES.get(DEFAULT), key, args); }

    public static String tr(CommandSourceStack source, String key, Object... args) {
        ServerPlayer player = source.getPlayer();
        return player == null ? tr(key, args) : tr(player, key, args);
    }

    public static String tr(ServerPlayer player, String key, Object... args) {
        Map<String, String> table = TABLES.get(player.getLanguage().toLowerCase(Locale.ROOT));
        if (table == null || !table.containsKey(key)) { table = TABLES.get(DEFAULT); }
        return line(table, key, args);
    }

    private static void table(String locale, Path file) {
        Map<String, String> lines = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) { lines.put(entry.getKey(), entry.getValue().getAsString()); }
            }
        }
        catch (IOException | RuntimeException ex) {
            ContentLog.LOGGER.error("Could not read the language file {}", file.getFileName(), ex);
            return;
        }
        TABLES.put(locale, lines);
    }

    private static String line(@Nullable Map<String, String> table, String key, Object... args) {
        String held = table == null ? null : table.get(key);
        if (held == null) { return key; }
        if (args.length == 0) { return held; }
        try { return String.format(held, args); }
        catch (RuntimeException ex) { return held; }
    }
}
