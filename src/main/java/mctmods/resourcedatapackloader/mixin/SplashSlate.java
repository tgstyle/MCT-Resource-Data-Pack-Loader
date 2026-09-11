package mctmods.resourcedatapackloader.mixin;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SplashSlate {
    private static final Logger LOGGER = LogManager.getLogger("RDPL");
    private static final String FILE = "resourcedatapackloader-common.toml";
    private static final String[] SCHEMES = {"net.minecraftforge.fml.earlydisplay.ColourScheme", "net.neoforged.fml.earlydisplay.ColourScheme"};
    private static final int SLATE = 0x1E2630;
    private static final int SLATE_TEXT = 0xE0E6EC;
    private static boolean painted;

    private SplashSlate() {}

    public static void paint() {
        if (painted || FMLEnvironment.dist != Dist.CLIENT || !wanted()) { return; }
        painted = true;
        for (String named : SCHEMES) {
            Class<?> scheme;
            try { scheme = Class.forName(named, true, SplashSlate.class.getClassLoader()); }
            catch (ClassNotFoundException absent) { continue; }
            try {
                Object black = null;
                for (Object constant : scheme.getEnumConstants()) { if ("BLACK".equals(((Enum<?>) constant).name())) { black = constant; } }
                if (black == null) { return; }
                Class<?> colour = Class.forName(named + "$Colour", true, scheme.getClassLoader());
                Constructor<?> made = colour.getConstructor(int.class, int.class, int.class);
                set(scheme, black, "background", made.newInstance(SLATE >> 16 & 0xFF, SLATE >> 8 & 0xFF, SLATE & 0xFF));
                set(scheme, black, "foreground", made.newInstance(SLATE_TEXT >> 16 & 0xFF, SLATE_TEXT >> 8 & 0xFF, SLATE_TEXT & 0xFF));
                LOGGER.info("The loading screen's dark palette is slate now, the pack loader's own");
            }
            catch (ReflectiveOperationException | RuntimeException unreachable) { LOGGER.warn("The loading screen's dark palette could not be repainted, so it stays the loader's black", unreachable); }
            return;
        }
    }

    private static boolean wanted() {
        Path file = FMLPaths.CONFIGDIR.get().resolve(FILE);
        if (!Files.isRegularFile(file)) { return true; }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            UnmodifiableConfig config = TomlFormat.instance().createParser().parse(reader);
            Object held = config.get("tweaks.darkSplash");
            return !(held instanceof Boolean flag) || flag;
        }
        catch (Exception unreadable) { return true; }
    }

    private static void set(Class<?> scheme, Object constant, String field, Object colour) throws ReflectiveOperationException {
        Field held = scheme.getDeclaredField(field);
        held.setAccessible(true);
        held.set(constant, colour);
    }
}
