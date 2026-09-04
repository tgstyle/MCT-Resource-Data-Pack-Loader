package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

public final class ConfigCore {
    public static final String FILE = ResourceDataPackLoader.MOD_ID + "-common.toml";

    private ConfigCore() {}

    public static Path file() { return FMLPaths.CONFIGDIR.get().resolve(FILE); }

    public static String text(String path, String fallback) {
        Object held = read(path);
        return held instanceof String ? (String) held : fallback;
    }

    public static boolean flag(String path, boolean fallback) {
        Object held = read(path);
        return held instanceof Boolean ? (Boolean) held : fallback;
    }

    @Nullable private static Object read(String path) {
        Path file = file();
        if (!Files.isRegularFile(file)) { return null; }
        try (CommentedFileConfig config = CommentedFileConfig.of(file)) {
            config.load();
            return config.get(path);
        }
        catch (RuntimeException unreadable) { return null; }
    }
}
