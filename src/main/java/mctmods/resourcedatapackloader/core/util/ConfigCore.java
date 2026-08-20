package mctmods.resourcedatapackloader.core.util;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import java.io.File;

public final class ConfigCore {
    private static File configDir;

    private ConfigCore() {}

    public static void at(File mcDir) { configDir = new File(mcDir, "config"); }

    public static boolean read(String category, String name) {
        try {
            Configuration cfg = new Configuration(new File(directory(), ConfigLate.FILE));
            cfg.load();
            if (!cfg.hasCategory(category)) { return false; }
            Property prop = cfg.getCategory(category).get(name);
            return prop != null && prop.getBoolean();
        }
        catch (RuntimeException ex) { return false; }
    }

    public static String text(String category, String name, String fallback) {
        try {
            Configuration cfg = new Configuration(new File(directory(), ConfigLate.FILE));
            cfg.load();
            if (!cfg.hasCategory(category)) { return fallback; }
            Property prop = cfg.getCategory(category).get(name);
            return prop == null ? fallback : prop.getString();
        }
        catch (RuntimeException ex) { return fallback; }
    }

    private static File directory() {
        if (configDir != null) { return configDir; }
        try {
            File known = Loader.instance().getConfigDir();
            if (known != null) { return known; }
        }
        catch (RuntimeException tooEarly) { return new File("config"); }
        return new File("config");
    }
}
