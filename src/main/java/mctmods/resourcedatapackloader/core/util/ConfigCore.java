package mctmods.resourcedatapackloader.core.util;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import java.io.File;
import javax.annotation.Nullable;

public final class ConfigCore {
    public static final String CONFIG_DIR = "config";
    private static File configDir;

    private ConfigCore() {}

    public static void at(File mcDir) { configDir = new File(mcDir, CONFIG_DIR); }

    public static File gameDir() { return configDir != null ? configDir.getParentFile() : new File("."); }

    public static boolean read(String category, String name) {
        Property prop = property(category, name);
        return prop != null && prop.getBoolean();
    }

    public static String text(String category, String name, String fallback) {
        Property prop = property(category, name);
        return prop == null ? fallback : prop.getString();
    }

    @Nullable
    private static Property property(String category, String name) {
        try {
            Configuration cfg = load();
            if (!cfg.hasCategory(category)) { return null; }
            return cfg.getCategory(category).get(name);
        }
        catch (RuntimeException ex) { return null; }
    }

    private static Configuration load() {
        Configuration cfg = new Configuration(new File(directory(), ConfigLate.FILE));
        cfg.load();
        return cfg;
    }

    private static File directory() {
        if (configDir != null) { return configDir; }
        try {
            File known = Loader.instance().getConfigDir();
            if (known != null) { return known; }
        }
        catch (RuntimeException tooEarly) { return new File(CONFIG_DIR); }
        return new File(CONFIG_DIR);
    }
}
