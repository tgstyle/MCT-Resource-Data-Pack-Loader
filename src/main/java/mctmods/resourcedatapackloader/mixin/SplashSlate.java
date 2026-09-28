package mctmods.resourcedatapackloader.mixin;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

public final class SplashSlate {
    public static final int SLATE = 0x1E2630;
    private static final Logger LOGGER = LogManager.getLogger("RDPL");
    private static final String FILE = "resourcedatapackloader-common.toml";
    private static final String HANDLER = "net.neoforged.fml.loading.ImmediateWindowHandler";
    private static final String WINDOW = "net.neoforged.fml.earlydisplay.DisplayWindow";
    private static final String COLOR = "net.neoforged.fml.earlydisplay.theme.ThemeColor";
    private static final int SLATE_TEXT = 0xE0E6EC;
    private static boolean painted;

    private SplashSlate() {}

    public static void paint() {
        if (painted || FMLEnvironment.getDist() != Dist.CLIENT || !wanted()) { return; }
        painted = true;
        try {
            Object window = field(Class.forName(HANDLER, true, SplashSlate.class.getClassLoader()), "provider").get(null);
            if (window == null || !WINDOW.equals(window.getClass().getName())) { return; }
            Field held = field(window.getClass(), "theme");
            Object theme = held.get(window);
            Object scheme = theme.getClass().getMethod("colorScheme").invoke(theme);
            Object text = scheme.getClass().getMethod("text").invoke(scheme);
            Method rgb = Class.forName(COLOR, true, window.getClass().getClassLoader()).getMethod("ofRgb", int.class);
            Object slateText = rgb.invoke(null, SLATE_TEXT);
            Object slate = with(theme, "colorScheme", with(with(scheme, "screenBackground", rgb.invoke(null, SLATE)), "text", slateText));
            held.set(window, slate);
            Future<?> renderer = (Future<?>) field(window.getClass(), "rendererFuture").get(window);
            if (renderer != null && renderer.state() == Future.State.SUCCESS) { restyle(renderer.resultNow(), slate, text, slateText); }
            LOGGER.info("The loading screen's dark palette is slate now, the pack loader's own");
        }
        catch (ReflectiveOperationException | RuntimeException | LinkageError unreachable) { LOGGER.warn("The loading screen's dark palette could not be repainted, so it stays the loader's black", unreachable); }
    }

    private static void restyle(Object renderer, Object slate, Object text, Object slateText) throws ReflectiveOperationException {
        Field held = field(renderer.getClass(), "theme");
        Object materialized = with(held.get(renderer), "theme", slate);
        held.set(renderer, materialized);
        for (Object element : (List<?>) field(renderer.getClass(), "elements").get(renderer)) {
            field(element.getClass(), "theme").set(element, materialized);
            for (Field color : element.getClass().getDeclaredFields()) {
                if (color.getType() != text.getClass()) { continue; }
                color.setAccessible(true);
                if (text.equals(color.get(element))) { color.set(element, slateText); }
            }
        }
    }

    private static Object with(Object record, String component, Object value) throws ReflectiveOperationException {
        RecordComponent[] parts = record.getClass().getRecordComponents();
        Class<?>[] types = new Class<?>[parts.length];
        Object[] values = new Object[parts.length];
        for (int i = 0; i < parts.length; i++) {
            types[i] = parts[i].getType();
            values[i] = component.equals(parts[i].getName()) ? value : parts[i].getAccessor().invoke(record);
        }
        return record.getClass().getDeclaredConstructor(types).newInstance(values);
    }

    private static Field field(Class<?> owner, String name) throws NoSuchFieldException {
        for (Class<?> type = owner; type != null; type = type.getSuperclass()) {
            for (Field found : type.getDeclaredFields()) {
                if (!found.getName().equals(name)) { continue; }
                found.setAccessible(true);
                return found;
            }
        }
        throw new NoSuchFieldException(name);
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
}
