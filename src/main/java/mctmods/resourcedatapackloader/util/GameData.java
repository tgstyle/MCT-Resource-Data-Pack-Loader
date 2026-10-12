package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class GameData {
    @Nullable private static VanillaPackResources vanilla;

    private GameData() {}

    @Nullable public static JsonObject json(Identifier at) {
        JsonObject found = read(at);
        if (found == null) { ContentLog.LOGGER.error("The game's or a mod's data file {} could not be read, so what a pack builds on it is left out", at); }
        return found;
    }

    @Nullable public static JsonObject noiseSettings(String name) { return LineCompat.worldgenIn("worldgen/noise_settings/" + name + PackManager.JSON_SUFFIX); }

    public static boolean has(Identifier at) { return read(at) != null; }

    public static Set<String> modFiles(String folder) {
        Set<String> found = new LinkedHashSet<>();
        for (IModFileInfo info : ModList.get().getModFiles()) {
            for (IModInfo mod : info.getMods()) {
                String space = mod.getModId();
                if (Identifier.DEFAULT_NAMESPACE.equals(space)) { continue; }
                Path root = ModFiles.find(info.getFile(), "data", space, folder);
                if (!Files.isDirectory(root)) { continue; }
                try (Stream<Path> walk = Files.walk(root)) {
                    walk.filter(Files::isRegularFile).forEach(file -> {
                        String name = root.relativize(file).toString().replace('\\', '/');
                        if (name.endsWith(PackManager.JSON_SUFFIX)) { found.add(space + ":" + name.substring(0, name.length() - PackManager.JSON_SUFFIX.length())); }
                    });
                }
                catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.debug("Could not list the {} folder of {}", folder, space, unreadable); }
            }
        }
        return found;
    }

    @Nullable private static JsonObject read(Identifier at) { return Identifier.DEFAULT_NAMESPACE.equals(at.getNamespace()) ? fromVanilla(at) : fromMod(at); }

    public static void release() { vanilla = null; }

    @Nullable private static JsonObject fromVanilla(Identifier at) {
        if (vanilla == null) { vanilla = ServerPacksSource.createVanillaPackSource(); }
        IoSupplier<InputStream> supplier = LineCompat.vanillaResource(vanilla, PackType.SERVER_DATA, at);
        if (supplier == null) { return null; }
        return read(() -> new InputStreamReader(supplier.get(), StandardCharsets.UTF_8));
    }

    @Nullable private static JsonObject fromMod(Identifier at) {
        IModFileInfo info = ModList.get().getModFileById(at.getNamespace());
        if (info == null) { return null; }
        Path path = ModFiles.find(info.getFile(), "data", at.getNamespace(), at.getPath());
        if (!Files.isRegularFile(path)) { return null; }
        return read(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

    @Nullable private static JsonObject read(IoSupplier<Reader> opener) {
        try (Reader reader = opener.get()) { return Json.GSON.fromJson(reader, JsonObject.class); }
        catch (IOException | JsonParseException failed) { return null; }
    }
}
