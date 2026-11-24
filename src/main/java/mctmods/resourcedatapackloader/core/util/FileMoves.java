package mctmods.resourcedatapackloader.core.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class FileMoves {
    private FileMoves() {}

    public static void replace(Path from, Path to) throws IOException {
        try { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException notAtomic) { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING); }
    }

    public static List<Path> children(Path folder, Predicate<Path> keep) throws IOException {
        try (Stream<Path> stream = Files.list(folder)) { return stream.sorted().filter(keep).collect(Collectors.toList()); }
    }

    public static void deleteQuietly(@Nullable Path file) {
        if (file == null) { return; }
        try { Files.deleteIfExists(file); }
        catch (IOException ignored) { }
    }
}
