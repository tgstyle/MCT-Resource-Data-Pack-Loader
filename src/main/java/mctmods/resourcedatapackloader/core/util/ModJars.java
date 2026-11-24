package mctmods.resourcedatapackloader.core.util;

import mctmods.resourcedatapackloader.core.MCTMixin;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ModJars {
    private ModJars() {}

    public static boolean present(String classPath) { return Launch.classLoader.getResource(classPath) != null; }

    public static List<File> list() {
        try {
            File home = (File) FMLInjectionData.data()[6];
            File mods = new File(home, "mods");
            List<File> found = new ArrayList<>();
            for (File root : new File[] {mods, new File(mods, "1.12.2")}) {
                File[] jars = root.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".jar"));
                if (jars != null) { Collections.addAll(found, jars); }
            }
            found.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            return found;
        }
        catch (Exception unavailable) { return Collections.emptyList(); }
    }

    public static boolean anyEntry(ZipFile zip, Predicate<ZipEntry> test) {
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            if (test.test(entries.nextElement())) { return true; }
        }
        return false;
    }

    public static boolean hasEntry(String entry, String what) {
        try {
            for (File jar : list()) {
                try (ZipFile zip = new ZipFile(jar)) {
                    if (zip.getEntry(entry) != null) { return true; }
                }
                catch (Exception unreadable) { MCTMixin.LOGGER.debug("Could not open {} while looking for {}", jar.getName(), what); }
            }
            return false;
        }
        catch (Exception failed) {
            MCTMixin.LOGGER.warn("Could not scan the mods folder for {}, assuming it is absent", what, failed);
            return false;
        }
    }
}
