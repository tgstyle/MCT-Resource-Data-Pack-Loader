package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
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
        Path save = server.getDataDirectory().toPath().resolve(server.getFolderName());
        Path kept = server.getDataDirectory().toPath().resolve(FOLDER).resolve(server.getFolderName());
        Path stamp = kept.getParent().resolve(kept.getFileName().toString() + ".stamp");
        if (!Files.isDirectory(kept) || !Files.isRegularFile(stamp) || !empty(save)) { return; }
        try {
            String held = new String(Files.readAllBytes(stamp), java.nio.charset.StandardCharsets.UTF_8);
            if (!held.equals(fingerprint())) {
                ContentLog.LOGGER.info("The pristine copy of {} was made by different packs, so it is not restored and the world is generated", server.getFolderName());
                return;
            }
        }
        catch (IOException unreadable) { return; }
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

    public static Path holdingFor(MinecraftServer server, WorldServer world) {
        Path save = world.getSaveHandler().getWorldDirectory().toPath();
        Path root = server.getDataDirectory().toPath().resolve(FOLDER);
        return root.resolve(save.getFileName().toString());
    }

    public static boolean already(MinecraftServer server, WorldServer world) {
        return Files.isDirectory(holdingFor(server, world));
    }

    private static Path stampFor(MinecraftServer server, WorldServer world) {
        Path kept = holdingFor(server, world);
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

    public static void mark(MinecraftServer server, WorldServer world) {
        Path stamp = stampFor(server, world);
        try { Files.write(stamp, fingerprint().getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
        catch (IOException broken) { ContentLog.LOGGER.warn("The pristine copy could not be stamped, so it will not be trusted later", broken); }
    }

    public static boolean bare(WorldServer world) {
        Path region = world.getSaveHandler().getWorldDirectory().toPath().resolve("region");
        if (!Files.isDirectory(region)) { return true; }
        try (java.util.stream.Stream<Path> held = Files.list(region)) { return !held.findAny().isPresent(); }
        catch (IOException unreadable) { return false; }
    }

    public static int give(MinecraftServer server, WorldServer world, IntConsumer along) {
        Path kept = holdingFor(server, world);
        Path save = world.getSaveHandler().getWorldDirectory().toPath();
        try {
            List<Path> wanted = worth(kept);
            if (wanted.isEmpty()) { return 0; }
            int done = 0;
            for (Path from : wanted) {
                Path to = save.resolve(kept.relativize(from).toString());
                Files.createDirectories(to.getParent());
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                along.accept(++done * 100 / wanted.size());
            }
            ContentLog.LOGGER.info("Restored {} from the pristine copy, {} file(s), instead of generating it", save.getFileName(), wanted.size());
            return wanted.size();
        }
        catch (IOException broken) {
            ContentLog.LOGGER.error("The pristine copy of {} could not be restored, so the world is generated as usual", save.getFileName(), broken);
            return 0;
        }
    }

    public static int take(MinecraftServer server, WorldServer world, IntConsumer along) {
        Path save = world.getSaveHandler().getWorldDirectory().toPath();
        Path kept = holdingFor(server, world);
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
