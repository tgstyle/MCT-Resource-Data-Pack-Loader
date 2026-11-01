package mctmods.resourcedatapackloader.common.pack.io;

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
        byte[] loaded = bytes;
        if (loaded == null) {
            try (InputStream in = FallbackIcon.class.getResourceAsStream(PATH)) { loaded = in == null ? new byte[0] : in.readAllBytes(); }
            catch (IOException ex) { loaded = new byte[0]; }
            bytes = loaded;
        }
        return loaded.length == 0 ? null : loaded;
    }
}
