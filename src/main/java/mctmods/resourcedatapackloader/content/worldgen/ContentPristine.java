package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ContentPristine {
    public static final String FOLDER = "rdpl-pristine";
    private static final List<String> SKIPPED = List.of("session.lock", "playerdata", "stats", "advancements", "level.dat_old");

    private ContentPristine() {}

    private static Path save(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize(); }

    public static Path holdingFor(MinecraftServer server) {
        Path save = save(server);
        return save.getParent().resolve(FOLDER).resolve(save.getFileName().toString());
    }

    private static Path stampFor(MinecraftServer server) {
        Path kept = holdingFor(server);
        return kept.getParent().resolve(kept.getFileName().toString() + ".stamp");
    }

    public static boolean already(MinecraftServer server) { return Files.isDirectory(holdingFor(server)); }

    public static void beforeWorldsLoad(MinecraftServer server) {
        Path save = save(server);
        Path kept = holdingFor(server);
        Path stamp = stampFor(server);
        if (!Files.isDirectory(kept) || !Files.isRegularFile(stamp) || !bare(save)) { return; }
        try {
            String held = Files.readString(stamp, StandardCharsets.UTF_8);
            if (!held.equals(fingerprint())) {
                ContentLog.LOGGER.info("The pristine copy of {} was made by different packs, so it is not restored and the world is generated", save.getFileName());
                return;
            }
        }
        catch (IOException unreadable) { return; }
        long begun = System.currentTimeMillis();
        int files = give(server, along -> {});
        if (files > 0) { ContentLog.LOGGER.info("Restored {} from the pristine copy, {} file(s) in {} ms, so it is not generated again", save.getFileName(), files, System.currentTimeMillis() - begun); }
    }

    private static boolean bare(Path save) {
        Path region = save.resolve("region");
        if (!Files.isDirectory(region)) { return true; }
        try (Stream<Path> held = Files.list(region)) { return held.findAny().isEmpty(); }
        catch (IOException unreadable) { return false; }
    }

    private static String fingerprint() {
        List<String> packs = new ArrayList<>();
        for (RDPLPack pack : PackManager.get().getPacks()) { packs.add(pack.getName() + ":" + pack.getFileCount()); }
        Collections.sort(packs);
        StringBuilder made = new StringBuilder();
        for (String pack : packs) { made.append(pack).append('\n'); }
        return made.toString();
    }

    public static void mark(MinecraftServer server) {
        try { Files.writeString(stampFor(server), fingerprint(), StandardCharsets.UTF_8); }
        catch (IOException broken) { ContentLog.LOGGER.warn("The pristine copy could not be stamped, so it will not be trusted later", broken); }
    }

    public static int give(MinecraftServer server, IntConsumer along) { return move(holdingFor(server), save(server), along, "restored from the pristine copy, instead of generating it", "The pristine copy of {} could not be restored, so the world is generated as usual"); }

    public static int take(MinecraftServer server, IntConsumer along) { return move(save(server), holdingFor(server), along, "kept as a pristine copy", "The pristine copy of {} could not be written, so there is nothing to reset to"); }

    private static int move(Path from, Path to, IntConsumer along, String did, String failed) {
        try {
            List<Path> wanted = worth(from);
            if (wanted.isEmpty()) { return 0; }
            Files.createDirectories(to);
            int done = 0;
            for (Path one : wanted) {
                Path landing = to.resolve(from.relativize(one).toString());
                Files.createDirectories(landing.getParent());
                Files.copy(one, landing, StandardCopyOption.REPLACE_EXISTING);
                along.accept(++done * 100 / wanted.size());
            }
            ContentLog.LOGGER.info("{} {}: {} file(s) between {} and {}", from.getFileName(), did, wanted.size(), from, to);
            return wanted.size();
        }
        catch (IOException broken) {
            ContentLog.LOGGER.error(failed, from.getFileName(), broken);
            return 0;
        }
    }

    private static List<Path> worth(Path save) throws IOException {
        List<Path> found = new ArrayList<>();
        if (!Files.isDirectory(save)) { return found; }
        Files.walkFileTree(save, new SimpleFileVisitor<>() {
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
