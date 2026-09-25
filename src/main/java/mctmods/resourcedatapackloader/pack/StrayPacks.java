package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.port.Port;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class StrayPacks {
    private static final String RESOURCEPACKS = "resourcepacks";
    private static final String ZIP = ".zip";
    private static final String MOVING = ".moving";
    private static final Set<String> DEFINITIONS = new HashSet<>(Arrays.asList("blocks", "items", "fluids", "materials", "tabs", "biomes", "worldgen", "dimensions", "worldtemplates", "gates", "gamerules", "entities", "potions", "potion_types", "villagers", "trades", "villages", "structuremaps", "citymaps", "caveregions", "hardness", "anvils", "exposures", "overrides", "teams", "scoring", "raids", "worldintro", "portalframes", "blastplaster", "pathintersects", "player_loot", "registry_remap", "oredict", "block_drops", "brewing", "fuels", "furnace", "recipe_removals", "loot_injections"));

    private StrayPacks() {}

    public static void collect(Path gameDir, Path root) { gather(gameDir.resolve(RESOURCEPACKS), root); }

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
        if (RDPLPack.ASSETS.equals(parts[at])) { return DEFINITIONS.contains(folder) && !PackManager.ITEMS.equals(folder); }
        if (!Port.DATA.equals(parts[at])) { return false; }
        if (PackManager.WORLDGEN.equals(folder)) { return parts.length == at + 4 && parts[at + 3].endsWith("." + PackManager.JSON); }
        return DEFINITIONS.contains(folder) || PackManager.SOUNDS.equals(folder);
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
