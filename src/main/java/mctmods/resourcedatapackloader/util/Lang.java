package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.AccessorPlayerLanguage;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.translation.LanguageMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Lang {
    private static final String HOME = "assets/resourcedatapackloader/lang/";
    private static final Map<String, Map<String, String>> TABLES = new HashMap<>();

    private Lang() {}

    public static void load() {
        try {
            URL source = Lang.class.getResource("/" + HOME);
            if (source != null && "file".equals(source.getProtocol())) {
                File[] found = new File(source.toURI()).listFiles((folder, name) -> name.endsWith(".lang"));
                if (found != null) {
                    for (File file : found) { table(file.getName(), Files.newInputStream(file.toPath())); }
                }
            }
            else if (source != null) {
                URLConnection reached = source.openConnection();
                if (reached instanceof JarURLConnection) {
                    reached.setUseCaches(false);
                    try (JarFile jar = ((JarURLConnection) reached).getJarFile()) {
                        for (JarEntry entry : Collections.list(jar.entries())) {
                            if (entry.getName().startsWith(HOME) && entry.getName().endsWith(".lang")) { table(entry.getName().substring(HOME.length()), jar.getInputStream(entry)); }
                        }
                    }
                }
            }
        }
        catch (Exception ex) { ContentLog.LOGGER.error("Could not look through the jar for language files", ex); }
        if (!TABLES.containsKey("en_us")) {
            InputStream fallback = Lang.class.getResourceAsStream("/" + HOME + "en_us.lang");
            if (fallback != null) { table("en_us.lang", fallback); }
        }
        Map<String, String> english = TABLES.get("en_us");
        if (english == null) {
            ContentLog.LOGGER.error("The language files are missing from the jar, so players will see raw language keys");
            return;
        }
        InputStream vanillaSide = Lang.class.getResourceAsStream("/" + HOME + "en_us.lang");
        if (vanillaSide != null) { LanguageMap.inject(vanillaSide); }
        ContentLog.LOGGER.info("Speaking {} language(s) to players", TABLES.size());
    }

    @SuppressWarnings("deprecation") public static String vanilla(String key) { return net.minecraft.util.text.translation.I18n.translateToLocal(key); }

    public static String tr(String key, Object... args) { return line(TABLES.get("en_us"), key, args); }

    public static String tr(ICommandSender sender, String key, Object... args) {
        if (sender instanceof EntityPlayer) { return tr((EntityPlayer) sender, key, args); }

        return tr(key, args);
    }

    public static String tr(EntityPlayer player, String key, Object... args) {
        if (!(player instanceof EntityPlayerMP)) { return tr(key, args); }

        String locale = ((AccessorPlayerLanguage) player).rdpl$language();
        Map<String, String> table = locale == null ? null : TABLES.get(locale.toLowerCase(Locale.ROOT));
        if (table == null || !table.containsKey(key)) { table = TABLES.get("en_us"); }
        return line(table, key, args);
    }

    private static void table(String fileName, InputStream entries) {
        String locale = fileName.substring(0, fileName.length() - ".lang".length()).toLowerCase(Locale.ROOT);
        Map<String, String> lines = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entries, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) { continue; }

                int split = line.indexOf('=');
                if (split < 1) { continue; }

                lines.put(line.substring(0, split), line.substring(split + 1));
            }
        }
        catch (Exception ex) { ContentLog.LOGGER.error("Could not read the language file {}", fileName, ex); }
        TABLES.put(locale, lines);
    }

    private static String line(Map<String, String> table, String key, Object... args) {
        String held = table == null ? null : table.get(key);
        if (held == null) { return key; }
        if (args.length == 0) { return held; }

        try { return String.format(held, args); }
        catch (Exception ex) { return held; }
    }
}
