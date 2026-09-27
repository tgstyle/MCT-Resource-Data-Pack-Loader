package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforgespi.locating.IModFile;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ModFiles {
    private static final Map<Path, Path> JARS = new ConcurrentHashMap<>();

    private ModFiles() {}

    public static Path find(IModFile file, String... parts) {
        String relative = String.join("/", parts);
        Path first = null;
        for (Path root : file.getContents().getContentRoots()) {
            Path base = Files.isDirectory(root) ? root : jar(root);
            if (base == null) { continue; }
            Path found = base.resolve(relative);
            if (Files.exists(found)) { return found; }
            if (first == null) { first = found; }
        }
        return first != null ? first : file.getFilePath().resolve(relative);
    }

    @Nullable private static Path jar(Path archive) { return JARS.computeIfAbsent(archive, ModFiles::open); }

    @Nullable private static Path open(Path archive) {
        try { return FileSystems.newFileSystem(archive).getPath("/"); }
        catch (IOException | RuntimeException unreadable) { return null; }
    }
}
