package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraftforge.fml.loading.FMLPaths;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.annotation.Nullable;

final class PackReadme {
    static final String README = "readme.txt";
    private static final String README_BASE = "/assets/resourcedatapackloader/readme";
    private static final String README_FALLBACK = README_BASE + "_en_us.txt";

    private PackReadme() {}

    private static String readmeSource() {
        String language = readmeLanguage();
        if (!language.isEmpty()) {
            String scoped = README_BASE + "_" + language + ".txt";
            if (PackManager.class.getResource(scoped) != null) { return scoped; }
        }
        return README_FALLBACK;
    }

    private static String readmeLanguage() {
        Path options = FMLPaths.GAMEDIR.get().resolve("options.txt");
        if (!Files.isRegularFile(options)) { return ""; }
        try {
            for (String line : Files.readAllLines(options, StandardCharsets.UTF_8)) {
                if (line.startsWith("lang:")) { return line.substring(5).trim().toLowerCase(Locale.ROOT); }
            }
        }
        catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.warn("Could not read the chosen language from {}, writing the readme in English", options); }
        return "";
    }

    @Nullable static String readmeText() {
        String source = readmeSource();
        try (InputStream stream = PackManager.class.getResourceAsStream(source)) {
            if (stream == null) { return null; }
            ByteArrayOutputStream held = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            for (int read = stream.read(buffer); read > 0; read = stream.read(buffer)) { held.write(buffer, 0, read); }
            return held.toString(StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {} out of the jar", source, ex);
            return null;
        }
    }
}
