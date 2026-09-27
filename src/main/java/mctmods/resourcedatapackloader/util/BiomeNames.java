package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;

import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class BiomeNames {
    private BiomeNames() {}

    public static String shownName(Identifier biome) {
        BiomeDef def = ContentBiomes.def(biome);
        return def != null ? def.name() : Lang.vanilla("biome." + biome.getNamespace() + "." + biome.getPath());
    }

    public static boolean named(Identifier biome, Collection<String> names) {
        if (names.isEmpty()) { return false; }
        Set<String> wanted = Settings.lower(names);
        return wanted.contains(biome.toString().toLowerCase(Locale.ROOT)) || wanted.contains(shownName(biome).toLowerCase(Locale.ROOT));
    }

    public static List<String> ids(Collection<String> names) {
        Set<String> out = new LinkedHashSet<>();
        Set<String> wanted = Settings.lower(names);
        for (String name : wanted) {
            if (name.indexOf(':') >= 0) { out.add(name); }
        }
        for (Identifier biome : ContentBiomes.known()) {
            if (wanted.contains(shownName(biome).toLowerCase(Locale.ROOT))) { out.add(biome.toString()); }
        }
        return new ArrayList<>(out);
    }
}
