package mctmods.resourcedatapackloader.pack;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

public final class FallbackIcon {
    public static final String FILE = "pack.png";
    public static final String TEXTURE = "resourcedatapackloader:textures/gui/icon.png";
    private static final String PATH = "/assets/resourcedatapackloader/textures/gui/icon.png";
    @Nullable private static byte[] bytes;

    private FallbackIcon() {}

    @Nullable public static byte[] bytes() {
        if (bytes == null) {
            try (InputStream in = FallbackIcon.class.getResourceAsStream(PATH)) { bytes = in == null ? new byte[0] : in.readAllBytes(); }
            catch (IOException ex) { bytes = new byte[0]; }
        }
        return bytes.length == 0 ? null : bytes;
    }
}
