package mctmods.resourcedatapackloader.core.util;

import net.minecraftforge.fml.client.SplashProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Field;

public final class SplashLogo {
    private static final Logger LOGGER = LogManager.getLogger("RDPL");
    private static final int BAR_WIDTH = 400;
    private static boolean read;
    private static int offset;
    private static boolean rotate;
    private static int loggedWidth = -1;
    private static int loggedHeight = -1;

    private SplashLogo() {}

    public static int fit(int side, int width, int height, int windowWidth, int windowHeight) { return (int) (side * scale(width, height, windowWidth, windowHeight)); }

    private static float scale(int width, int height, int windowWidth, int windowHeight) {
        if (!read) { readSettings(); }
        float boxWidth = rotate ? Math.max(width, height) : width;
        float boxHeight = rotate ? Math.max(width, height) : height;
        float scale = 1F;
        if (boxWidth > 0) { scale = Math.min(scale, ((windowWidth - BAR_WIDTH) / 2F - offset) / boxWidth); }
        if (boxHeight > 0) { scale = Math.min(scale, (windowHeight - offset) / boxHeight); }
        scale = Math.max(0F, scale);
        if (windowWidth != loggedWidth || windowHeight != loggedHeight) {
            loggedWidth = windowWidth;
            loggedHeight = windowHeight;
            LOGGER.info("Loading screen logo {}x{} drawn at {}x{} in a {}x{} window", width, height, (int) (width * scale), (int) (height * scale), windowWidth, windowHeight);
        }
        return scale;
    }

    private static void readSettings() {
        read = true;
        try {
            offset = field("logoOffset").getInt(null);
            rotate = field("rotate").getBoolean(null);
        }
        catch (ReflectiveOperationException | RuntimeException unreadable) { LOGGER.warn("Could not read the loading screen's logo settings, so the logo is fitted as if it had no offset and no spin", unreadable); }
    }

    private static Field field(String name) throws NoSuchFieldException {
        Field field = SplashProgress.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
