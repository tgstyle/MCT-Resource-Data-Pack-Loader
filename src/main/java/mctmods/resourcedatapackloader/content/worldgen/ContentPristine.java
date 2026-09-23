package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;
import javax.annotation.Nonnull;

public final class ContentPristine {
    public static final String FOLDER = "rdpl-pristine";
    private static final List<String> SKIPPED = Arrays.asList("session.lock", "playerdata", "stats", "advancements", "level.dat_old");

    private ContentPristine() {}

    public static void beforeWorldsLoad(MinecraftServer server) {
        if (server == null) { return; }
        Path save = save(server);
        Path kept = holdingFor(server);
        Path stamp = stampFor(server);
        if (!Files.isDirectory(kept) || !Files.isRegularFile(stamp) || !empty(save)) { return; }
        if (!stamped(stamp)) {
            ContentLog.LOGGER.info("The pristine copy of {} was made by different packs, so it is thrown away and the world is generated, then copied afresh", server.getFolderName());
            clear(kept, stamp);
            return;
        }
        long begun = System.currentTimeMillis();
        int files = copy(kept, save);
        if (files > 0) {
            ContentLog.LOGGER.info("Restored {} from the pristine copy, {} file(s) in {} ms, so it is not generated again",
                    server.getFolderName(), files, System.currentTimeMillis() - begun);
        }
    }

    private static boolean empty(Path save) {
        Path region = save.resolve("region");
        if (!Files.isDirectory(region)) { return true; }
        try (java.util.stream.Stream<Path> held = Files.list(region)) { return !held.findAny().isPresent(); }
        catch (IOException unreadable) { return false; }
    }

    private static int copy(Path from, Path to) {
        try {
            List<Path> wanted = worth(from);
            for (Path one : wanted) {
                Path landing = to.resolve(from.relativize(one).toString());
                Files.createDirectories(landing.getParent());
                Files.copy(one, landing, StandardCopyOption.REPLACE_EXISTING);
            }
            return wanted.size();
        }
        catch (IOException broken) {
            ContentLog.LOGGER.error("The pristine copy could not be restored, so the world is generated as usual", broken);
            return 0;
        }
    }

    private static Path save(MinecraftServer server) { return server.getActiveAnvilConverter().getFile(server.getFolderName(), ".").toPath().toAbsolutePath().normalize(); }

    public static Path holdingFor(MinecraftServer server) {
        Path save = save(server);
        return save.getParent().resolve(FOLDER).resolve(save.getFileName().toString());
    }

    public static boolean already(MinecraftServer server) {
        Path kept = holdingFor(server);
        if (!Files.isDirectory(kept)) { return false; }
        Path stamp = stampFor(server);
        if (stamped(stamp)) { return true; }
        ContentLog.LOGGER.info("The pristine copy in {} was made by different packs, so it is thrown away and kept again from this world", kept);
        clear(kept, stamp);
        return false;
    }

    private static boolean stamped(Path stamp) {
        try { return Files.isRegularFile(stamp) && new String(Files.readAllBytes(stamp), java.nio.charset.StandardCharsets.UTF_8).equals(fingerprint()); }
        catch (IOException unreadable) { return false; }
    }

    private static void clear(Path kept, Path stamp) {
        try {
            if (Files.isDirectory(kept)) {
                Files.walkFileTree(kept, new SimpleFileVisitor<Path>() {
                    @Override @Nonnull public FileVisitResult visitFile(@Nonnull Path at, @Nonnull BasicFileAttributes attrs) throws IOException {
                        Files.delete(at);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override @Nonnull public FileVisitResult postVisitDirectory(@Nonnull Path at, IOException broken) throws IOException {
                        Files.delete(at);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            Files.deleteIfExists(stamp);
        }
        catch (IOException broken) { ContentLog.LOGGER.warn("The stale pristine copy in {} could not be thrown away, so delete it by hand", kept, broken); }
    }

    private static Path stampFor(MinecraftServer server) {
        Path kept = holdingFor(server);
        return kept.getParent().resolve(kept.getFileName().toString() + ".stamp");
    }

    private static String fingerprint() {
        StringBuilder made = new StringBuilder();
        List<String> packs = new ArrayList<>();
        for (mctmods.resourcedatapackloader.pack.RDPLPack pack : mctmods.resourcedatapackloader.pack.PackManager.get().getPacks()) {
            packs.add(pack.getName() + ":" + pack.getFileCount());
        }
        java.util.Collections.sort(packs);
        for (String pack : packs) { made.append(pack).append('\n'); }
        return made.toString();
    }

    public static void mark(MinecraftServer server) {
        Path stamp = stampFor(server);
        try { Files.write(stamp, fingerprint().getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
        catch (IOException broken) { ContentLog.LOGGER.warn("The pristine copy could not be stamped, so it will not be trusted later", broken); }
    }

    public static int take(MinecraftServer server, IntConsumer along) {
        Path save = save(server);
        Path kept = holdingFor(server);
        try {
            List<Path> wanted = worth(save);
            if (wanted.isEmpty()) { return 0; }
            Files.createDirectories(kept);
            int done = 0;
            for (Path from : wanted) {
                Path to = kept.resolve(save.relativize(from).toString());
                Files.createDirectories(to.getParent());
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                along.accept(++done * 100 / wanted.size());
            }
            ContentLog.LOGGER.info("Kept a pristine copy of {} in {}, {} file(s)", save.getFileName(), kept, wanted.size());
            return wanted.size();
        }
        catch (IOException broken) {
            ContentLog.LOGGER.error("The pristine copy of {} could not be written to {}, so there is nothing to reset to", save.getFileName(), kept, broken);
            return 0;
        }
    }

    private static List<Path> worth(Path save) throws IOException {
        List<Path> found = new ArrayList<>();
        Files.walkFileTree(save, new SimpleFileVisitor<Path>() {
            @Override @Nonnull public FileVisitResult preVisitDirectory(@Nonnull Path at, @Nonnull BasicFileAttributes attrs) {
                return SKIPPED.contains(at.getFileName().toString()) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
            }
            @Override @Nonnull public FileVisitResult visitFile(@Nonnull Path at, @Nonnull BasicFileAttributes attrs) {
                if (!SKIPPED.contains(at.getFileName().toString())) { found.add(at); }
                return FileVisitResult.CONTINUE;
            }
        });
        return found;
    }
}
