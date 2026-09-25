package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.port.Port;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class StrayPacks {
    private static final String RESOURCEPACKS = "resourcepacks";
    private static final String SAVES = "saves";
    private static final String SERVER_PROPERTIES = "server.properties";
    private static final String LEVEL_NAME = "level-name";
    private static final String DEFAULT_LEVEL = "world";
    private static final String ZIP = ".zip";
    private static final String MOVING = ".moving";

    private StrayPacks() {}

    public static void collect(Path gameDir, Path root) {
        gather(gameDir.resolve(RESOURCEPACKS), root);
        for (Path world : worlds(gameDir)) { gather(world.resolve(LevelResource.DATAPACK_DIR.getId()), root); }
    }

    private static List<Path> worlds(Path gameDir) {
        if (FMLEnvironment.dist != Dist.CLIENT) { return List.of(gameDir.resolve(levelName(gameDir))); }
        List<Path> worlds = new ArrayList<>();
        Path saves = gameDir.resolve(SAVES);
        if (!Files.isDirectory(saves)) { return worlds; }
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(saves, Files::isDirectory)) {
            for (Path world : entries) { worlds.add(world); }
        }
        catch (IOException | UncheckedIOException ex) { ContentLog.LOGGER.error("Could not list the worlds in {} while looking for RDPL packs", saves, ex); }
        return worlds;
    }

    private static String levelName(Path gameDir) {
        Path file = gameDir.resolve(SERVER_PROPERTIES);
        Properties properties = new Properties();
        if (Files.isRegularFile(file)) {
            try (InputStream in = Files.newInputStream(file)) { properties.load(in); }
            catch (IOException | IllegalArgumentException ex) { ContentLog.LOGGER.warn("Could not read {} for the world name, so '{}' is looked through for RDPL packs", file, DEFAULT_LEVEL, ex); }
        }
        return properties.getProperty(LEVEL_NAME, DEFAULT_LEVEL);
    }

    private static void gather(Path folder, Path root) {
        if (!Files.isDirectory(folder) || folder.toAbsolutePath().normalize().startsWith(root.toAbsolutePath().normalize())) { return; }
        List<Path> found = new ArrayList<>();
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
            for (Path entry : entries) { found.add(entry); }
        }
        catch (IOException | UncheckedIOException ex) {
            ContentLog.LOGGER.error("Could not look through {} for RDPL packs", folder, ex);
            return;
        }
        for (Path entry : found) {
            if (Files.isDirectory(entry)) {
                if (folderPack(entry)) { ContentLog.LOGGER.warn("'{}' is an RDPL pack in a folder, so it is left where it is. RDPL reads a pack only as a zip file: zip it up and put the zip in {}", entry, root); }
                continue;
            }
            if (entry.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(ZIP) && zipPack(entry)) { move(entry, root); }
        }
    }

    private static boolean definition(String path) {
        String[] parts = path.replace('\\', '/').split("/");
        int at = parts.length > 2 && PackVersions.FOLDER.equals(parts[0]) ? 2 : 0;
        if (parts.length < at + 4) { return false; }
        String folder = parts[at + 2];
        if (RDPLPack.ASSETS.equals(parts[at])) { return Port.DEFINITION_FOLDERS.contains(folder) && !PackManager.ITEMS.equals(folder); }
        if (!RDPLPack.DATA.equals(parts[at])) { return false; }
        if (PackManager.WORLDGEN.equals(folder)) { return parts.length == at + 4 && parts[at + 3].endsWith("." + PackManager.JSON); }
        return Port.DEFINITION_FOLDERS.contains(folder) || PackManager.SOUNDS.equals(folder);
    }

    private static boolean zipPack(Path zip) {
        try (ZipFile file = new ZipFile(zip.toFile())) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && definition(entry.getName())) { return true; }
            }
        }
        catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.warn("Could not read '{}' while looking for RDPL packs, so it is left where it is", zip, unreadable); }
        return false;
    }

    private static boolean folderPack(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) { return files.filter(Files::isRegularFile).anyMatch(file -> definition(dir.relativize(file).toString())); }
        catch (IOException | UncheckedIOException unreadable) {
            ContentLog.LOGGER.warn("Could not read the folder '{}' while looking for RDPL packs", dir, unreadable);
            return false;
        }
    }

    private static void move(Path source, Path root) {
        Path target = root.resolve(source.getFileName().toString());
        if (Files.exists(target)) {
            ContentLog.LOGGER.warn("'{}' is an RDPL pack, but {} already holds a pack of that name, so it is left where it is", source, target);
            return;
        }
        try {
            Files.createDirectories(root);
            try { Files.move(source, target, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException otherDrive) { copyAcross(source, target); }
            ContentLog.LOGGER.info("Moved the RDPL pack {} to {}", source, target);
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not move the RDPL pack {} to {}", source, target, ex); }
    }

    private static void copyAcross(Path source, Path target) throws IOException {
        Path copied = target.resolveSibling(target.getFileName() + MOVING);
        try {
            Files.copy(source, copied, StandardCopyOption.REPLACE_EXISTING);
            Files.move(copied, target, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException ex) {
            Files.deleteIfExists(copied);
            throw ex;
        }
        Files.delete(source);
    }
}
