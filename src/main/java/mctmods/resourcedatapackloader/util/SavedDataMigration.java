package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.card.CardStorage;
import mctmods.resourcedatapackloader.content.gate.GateStorage;
import mctmods.resourcedatapackloader.content.gate.PortalStorage;
import mctmods.resourcedatapackloader.content.raid.RaidStorage;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureCounts;
import mctmods.resourcedatapackloader.content.worldgen.PregenMemory;
import mctmods.resourcedatapackloader.content.worldgen.SeamMemory;
import mctmods.resourcedatapackloader.content.worldgen.VoidMemory;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class SavedDataMigration {
    private static final List<String> NAMES = List.of(PregenMemory.NAME, SeamMemory.NAME, VoidMemory.NAME, ContentStructureCounts.NAME, ContentLocate.NAME, CardStorage.NAME, RaidStorage.NAME, GateStorage.NAME, PortalStorage.NAME);

    private SavedDataMigration() {}

    public static void onAboutToStart(ServerAboutToStartEvent event) {
        Path root = event.getServer().getWorldPath(LevelResource.ROOT);
        migrate(root.resolve("data"), DimensionType.getStorageFolder(Level.OVERWORLD, root).resolve("data"));
        migrate(root.resolve("DIM-1").resolve("data"), DimensionType.getStorageFolder(Level.NETHER, root).resolve("data"));
        migrate(root.resolve("DIM1").resolve("data"), DimensionType.getStorageFolder(Level.END, root).resolve("data"));
        for (Path folder : dimensionDataFolders(root.resolve("dimensions"))) { migrate(folder, folder); }
    }

    private static List<Path> dimensionDataFolders(Path dimensions) {
        List<Path> found = new ArrayList<>();
        if (!Files.isDirectory(dimensions)) { return found; }
        try (Stream<Path> namespaces = Files.list(dimensions)) {
            for (Path namespace : namespaces.filter(Files::isDirectory).toList()) {
                try (Stream<Path> paths = Files.walk(namespace)) { paths.filter(path -> path.getFileName().toString().equals("data") && Files.isDirectory(path)).forEach(found::add); }
            }
        }
        catch (IOException failed) { ContentLog.LOGGER.warn("Could not list the dimension folders in {}: {}", dimensions, failed.toString()); }
        return found;
    }

    private static void migrate(Path oldFolder, Path newFolder) {
        if (!Files.isDirectory(oldFolder)) { return; }
        Path target = newFolder.resolve(ResourceDataPackLoader.MOD_ID);
        for (String name : NAMES) {
            Path from = oldFolder.resolve(name + ".dat");
            Path to = target.resolve(name + ".dat");
            if (!Files.isRegularFile(from) || Files.exists(to)) { continue; }
            try {
                Files.createDirectories(target);
                FileMoves.replace(from, to);
                ContentLog.LOGGER.info("Moved the 1.21.1 saved data {} to {}", from, to);
            }
            catch (IOException failed) { ContentLog.LOGGER.warn("Could not move the saved data {} to {}: {}", from, to, failed.toString()); }
        }
    }
}
