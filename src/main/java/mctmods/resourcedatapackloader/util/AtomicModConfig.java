package mctmods.resourcedatapackloader.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class AtomicModConfig extends ModConfig {
    private static final Handler HANDLER = new Handler();

    public AtomicModConfig(Type type, IConfigSpec<?> spec, ModContainer container) { super(type, spec, container); }

    @Override public ConfigFileTypeHandler getHandler() { return HANDLER; }

    private static final class Handler extends ConfigFileTypeHandler {
        @Override public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return config -> {
                Path path = configBasePath.resolve(config.getFileName());
                CommentedFileConfig data = CommentedFileConfig.builder(path, AtomicToml.FORMAT).sync().preserveInsertionOrder().autosave().onFileNotFound((file, format) -> setup(config, file, format)).writingMode(WritingMode.REPLACE).build();
                try { data.load(); }
                catch (ParsingException unreadable) { throw new IllegalStateException("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), unreadable); }
                try { FileWatcher.defaultInstance().addWatch(path, new Watcher(config, data, Thread.currentThread().getContextClassLoader())); }
                catch (IOException unwatched) { throw new IllegalStateException("Couldn't watch config file", unwatched); }
                return data;
            };
        }

        private static boolean setup(ModConfig config, Path file, ConfigFormat<?> format) throws IOException {
            Files.createDirectories(file.getParent());
            Path shipped = FMLPaths.GAMEDIR.get().resolve(FMLConfig.getConfigValue(FMLConfig.ConfigValue.DEFAULT_CONFIG_PATH)).resolve(config.getFileName());
            if (Files.exists(shipped)) { Files.copy(shipped, file); }
            else {
                Files.createFile(file);
                format.initEmptyFile(file);
            }
            return true;
        }
    }

    private record Watcher(ModConfig config, CommentedFileConfig data, ClassLoader loader) implements Runnable {
        @Override public void run() {
            Thread.currentThread().setContextClassLoader(loader);
            if (config.getSpec().isCorrecting()) { return; }
            try {
                data.load();
                if (!config.getSpec().isCorrect(data)) {
                    ConfigFileTypeHandler.backUpConfig(data);
                    config.getSpec().correct(data);
                    data.save();
                }
            }
            catch (ParsingException unreadable) { throw new IllegalStateException("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), unreadable); }
            config.getSpec().afterReload();
            ModList.get().getModContainerById(config.getModId()).ifPresent(container -> container.dispatchConfigEvent(IConfigEvent.reloading(config)));
        }
    }

    private static final class AtomicToml implements ConfigFormat<CommentedConfig>, ConfigWriter {
        private static final AtomicToml FORMAT = new AtomicToml();

        @Override public ConfigWriter createWriter() { return this; }

        @Override public ConfigParser<CommentedConfig> createParser() { return TomlFormat.instance().createParser(); }

        @Override public CommentedConfig createConfig(Supplier<Map<String, Object>> mapCreator) { return TomlFormat.instance().createConfig(mapCreator); }

        @Override public boolean supportsComments() { return true; }

        @Override public boolean supportsType(Class<?> type) { return TomlFormat.instance().supportsType(type); }

        @Override public void write(UnmodifiableConfig config, Writer writer) { TomlFormat.instance().createWriter().write(config, writer); }

        @Override public void write(UnmodifiableConfig config, Path file, WritingMode mode, Charset charset) {
            Path target = file.toAbsolutePath();
            Path temp = null;
            try {
                temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
                try (OutputStream output = Files.newOutputStream(temp)) { write(config, output, charset); }
                try { Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
                catch (AtomicMoveNotSupportedException notAtomic) { Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING); }
            }
            catch (IOException failed) { throw new WritingException("An I/O error occured", failed); }
            finally {
                if (temp != null) {
                    try { Files.deleteIfExists(temp); }
                    catch (IOException ignored) { }
                }
            }
        }
    }
}
