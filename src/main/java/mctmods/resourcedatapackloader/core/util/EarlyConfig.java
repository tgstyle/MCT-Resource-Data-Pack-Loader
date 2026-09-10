package mctmods.resourcedatapackloader.core.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class EarlyConfig {
    private EarlyConfig() {}

    public static boolean flag(File mcDir, String file, String prefix, boolean fallback) {
        try {
            File held = new File(mcDir, "config/" + file);
            if (!held.isFile()) { return fallback; }
            for (String line : Files.readAllLines(held.toPath(), StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.startsWith(prefix)) { return trimmed.endsWith("true"); }
            }
        }
        catch (Exception ignored) {}
        return fallback;
    }
}
