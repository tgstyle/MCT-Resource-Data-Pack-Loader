package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.pack.port.Ported;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.FileMoves;

import net.minecraftforge.common.ForgeVersion;
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
import org.apache.commons.io.IOUtils;

final class PackVersions {
    static final String FOLDER = "versions";
    static final String PREFIX = FOLDER + "/" + ForgeVersion.mcVersion + "/";

    private PackVersions() {}

    @Nullable static Path home(Path root) {
        Path home = root.resolve(FOLDER).resolve(ForgeVersion.mcVersion);
        return Files.isDirectory(home) ? home : null;
    }

    @Nullable static Path write(Path zip, Ported ported) {
        Path written = null;
        try {
            written = Files.createTempFile(zip.getParent(), zip.getFileName().toString(), ".converting");
            try (ZipFile original = new ZipFile(zip.toFile()); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(written)))) {
                for (ZipEntry held : Collections.list(original.entries())) {
                    ZipEntry copy = new ZipEntry(held.getName());
                    copy.setTime(held.getTime());
                    out.putNextEntry(copy);
                    try (InputStream in = original.getInputStream(held)) { IOUtils.copy(in, out); }
                    out.closeEntry();
                }
                ported.writeVersion(out, PREFIX);
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
