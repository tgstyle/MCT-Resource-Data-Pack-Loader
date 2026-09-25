package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.port.PackPort;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.FileMoves;

import net.minecraft.SharedConstants;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

final class PackVersions {
    static final String FOLDER = "versions";

    private PackVersions() {}

    static String prefix() { return FOLDER + "/" + SharedConstants.getCurrentVersion().getName() + "/"; }

    @Nullable static Path home(Path root) {
        Path home = root.resolve(FOLDER).resolve(SharedConstants.getCurrentVersion().getName());
        return Files.isDirectory(home) ? home : null;
    }

    @Nullable static Path write(Path zip, PackPort ported) {
        Path written = null;
        try {
            written = Files.createTempFile(zip.getParent(), zip.getFileName().toString(), ".converting");
            try (ZipFile original = new ZipFile(zip.toFile()); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(written)))) {
                for (ZipEntry held : Collections.list(original.entries())) {
                    ZipEntry copy = new ZipEntry(held.getName());
                    copy.setTime(held.getTime());
                    out.putNextEntry(copy);
                    try (InputStream in = original.getInputStream(held)) { in.transferTo(out); }
                    out.closeEntry();
                }
                ported.writeVersion(out, prefix());
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
