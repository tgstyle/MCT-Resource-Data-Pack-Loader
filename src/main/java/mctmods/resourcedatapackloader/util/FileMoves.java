package mctmods.resourcedatapackloader.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.annotation.Nullable;

public final class FileMoves {
    private FileMoves() {}

    public static void replace(Path from, Path to) throws IOException {
        try { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException notAtomic) { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING); }
    }

    public static void deleteQuietly(@Nullable Path file) {
        if (file == null) { return; }
        try { Files.deleteIfExists(file); }
        catch (IOException ignored) { }
    }
}
