package mctmods.resourcedatapackloader.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.Logger;

public final class SplashDark {
    private static final String FILE = "config/splash.properties";
    private static final String FORGE_LOGO = "fml:textures/gui/forge.png";
    private static final String OUR_LOGO = "resourcedatapackloader:textures/gui/hold.png";
    private static final String LOGO_PATH = "assets/resourcedatapackloader/textures/gui/hold.png";
    private static final Map<String, Integer> DARK = new LinkedHashMap<>();
    private static final Map<String, Integer> FORGE = new LinkedHashMap<>();

    static {
        DARK.put("background", 0x1E2630);
        DARK.put("font", 0xE0E6EC);
        DARK.put("barBackground", 0x2A3440);
        DARK.put("barBorder", 0x5A6470);
        FORGE.put("background", 0xFFFFFF);
        FORGE.put("font", 0x000000);
        FORGE.put("barBackground", 0xFFFFFF);
        FORGE.put("barBorder", 0xC0C0C0);
    }

    private SplashDark() {}

    public static void apply(File mcDir, boolean dark, Logger log) {
        File file = new File(mcDir, FILE);
        Properties held = new Properties();
        if (file.isFile()) {
            try (InputStream in = Files.newInputStream(file.toPath())) { held.load(in); }
            catch (IOException unreadable) {
                log.warn("Could not read {}, so the loading screen keeps whatever it has", FILE, unreadable);
                return;
            }
        }
        boolean changed = false;
        if (dark ? holds(held, FORGE) : holds(held, DARK)) {
            for (Map.Entry<String, Integer> color : (dark ? DARK : FORGE).entrySet()) { held.setProperty(color.getKey(), "0x" + Integer.toHexString(color.getValue()).toUpperCase()); }
            changed = true;
        }
        String logo = held.getProperty("forgeTexture", FORGE_LOGO).trim();
        if (dark && FORGE_LOGO.equals(logo) && placed(mcDir, held, log)) {
            held.setProperty("forgeTexture", OUR_LOGO);
            changed = true;
        }
        else if (dark && OUR_LOGO.equals(logo)) { placed(mcDir, held, log); }
        else if (!dark && OUR_LOGO.equals(logo)) {
            held.setProperty("forgeTexture", FORGE_LOGO);
            changed = true;
        }
        if (!changed) { return; }
        try {
            Files.createDirectories(file.toPath().getParent());
            try (OutputStream out = Files.newOutputStream(file.toPath())) { held.store(out, "Splash screen properties"); }
            log.info("The loading screen is {} now, written to {}", dark ? "dark, with the pack loader's logo" : "back to Forge's own colors and logo", FILE);
        }
        catch (IOException unwritable) { log.warn("Could not write {}, so the loading screen keeps whatever it has", FILE, unwritable); }
    }

    private static boolean placed(File mcDir, Properties held, Logger log) {
        File to = new File(new File(mcDir, held.getProperty("resourcePackPath", "resources").trim()), LOGO_PATH);
        try (InputStream in = SplashDark.class.getResourceAsStream("/" + LOGO_PATH)) {
            if (in == null) {
                log.warn("The pack loader's logo is not in its own jar, so the loading screen keeps Forge's");
                return false;
            }
            byte[] logo = readAll(in);
            if (to.isFile() && Arrays.equals(Files.readAllBytes(to.toPath()), logo)) { return true; }
            Files.createDirectories(to.toPath().getParent());
            Files.write(to.toPath(), logo);
            return true;
        }
        catch (IOException broken) {
            log.warn("Could not put the pack loader's logo at {}, so the loading screen keeps Forge's", to, broken);
            return false;
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int read;
        while ((read = in.read(chunk)) >= 0) { out.write(chunk, 0, read); }
        return out.toByteArray();
    }

    private static boolean holds(Properties held, Map<String, Integer> palette) {
        for (Map.Entry<String, Integer> color : palette.entrySet()) {
            String found = held.getProperty(color.getKey());
            int value;
            if (found == null) { value = FORGE.get(color.getKey()); }
            else {
                try { value = Integer.decode(found.trim()); }
                catch (NumberFormatException odd) { return false; }
            }
            if (value != color.getValue()) { return false; }
        }
        return true;
    }
}
