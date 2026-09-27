package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.pack.port.IPackPort;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.FileMoves;

import net.minecraft.SharedConstants;
import net.neoforged.fml.ModList;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

final class PackVersions {
    static final String FOLDER = "versions";
    private static final String STAMP = "port.stamp";

    private PackVersions() {}

    static String prefix() { return FOLDER + "/" + SharedConstants.getCurrentVersion().name() + "/"; }

    @Nullable static Path home(Path root) {
        Path home = root.resolve(FOLDER).resolve(SharedConstants.getCurrentVersion().name());
        return Files.isDirectory(home) && !stale(home.resolve(STAMP)) ? home : null;
    }

    private static boolean stale(Path stamp) {
        if (!Files.isRegularFile(stamp)) { return false; }
        try { return !Files.readString(stamp, StandardCharsets.UTF_8).trim().equals(modVersion()); }
        catch (IOException unreadable) { return false; }
    }

    private static String modVersion() { return ModList.get().getModContainerById(ResourceDataPackLoader.MOD_ID).map(held -> held.getModInfo().getVersion().toString()).orElse(""); }

    @Nullable static Path write(Path zip, IPackPort ported) {
        Path written = null;
        String prefix = prefix();
        try {
            written = Files.createTempFile(zip.getParent(), zip.getFileName().toString(), ".converting");
            try (ZipFile original = new ZipFile(zip.toFile()); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(written)))) {
                for (ZipEntry held : Collections.list(original.entries())) {
                    if (held.getName().startsWith(prefix)) {
                        if (!held.isDirectory() && !held.getName().equals(prefix + STAMP)) { ContentLog.LOGGER.info("Pack '{}': the port replaces {}, which another version of RDPL wrote", zip.getFileName(), held.getName()); }
                        continue;
                    }
                    ZipEntry copy = new ZipEntry(held.getName());
                    copy.setTime(held.getTime());
                    out.putNextEntry(copy);
                    try (InputStream in = original.getInputStream(held)) { in.transferTo(out); }
                    out.closeEntry();
                }
                ported.writeVersion(out, prefix);
                out.putNextEntry(new ZipEntry(prefix + STAMP));
                out.write(modVersion().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
            return written;
        }
        catch (IOException | RuntimeException ex) {
            ContentLog.LOGGER.error("Pack '{}': the port could not be written into the zip, so the pack is read through the port in memory this time and the zip is left as it was", zip.getFileName(), ex);
            FileMoves.deleteQuietly(written);
            return null;
        }
    }

    static boolean swap(Path written, Path zip) {
        try {
            FileMoves.replace(written, zip);
            return true;
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Pack '{}': the ported zip could not replace the original, which is left as it was", zip.getFileName(), ex);
            FileMoves.deleteQuietly(written);
            return false;
        }
    }
}
