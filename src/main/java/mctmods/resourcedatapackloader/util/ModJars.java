package mctmods.resourcedatapackloader.util;

import net.minecraftforge.fml.relauncher.FMLInjectionData;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public final class ModJars {
    private ModJars() {}

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
}
