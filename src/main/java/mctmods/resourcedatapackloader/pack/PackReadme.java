package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;import javax.annotation.Nullable;

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
        File options = new File(ConfigCore.gameDir(), "options.txt");
        if (!options.isFile()) { return ""; }
        try {
            for (String line : Files.readAllLines(options.toPath(), StandardCharsets.UTF_8)) {
                if (line.startsWith("lang:")) { return Settings.lower(line.substring(5)); }
            }
        }
        catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.warn("Could not read the chosen language from {}, writing the readme in English", options); }
        return "";
    }

    @Nullable static String readmeText() {
        String source = readmeSource();
        try (InputStream stream = PackManager.class.getResourceAsStream(source)) {
            if (stream == null) { return null; }
            return new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {} out of the jar", source, ex);
            return null;
        }
    }
}
