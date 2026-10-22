package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class BiomeNames {
    private BiomeNames() {}

    public static boolean matches(List<String> wanted, Biome biome) {
        if (wanted.isEmpty()) { return true; }
        ResourceLocation name = biome.getRegistryName();
        for (String one : wanted) {
            if (one.startsWith("#")) {
                if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.getType(one.substring(1).toUpperCase(Locale.ROOT)))) { return true; }
            }
            else if (name != null && (one.equals(name.toString()) || one.equals(name.getPath()))) { return true; }
        }
        return false;
    }

    public static boolean named(Biome biome, Set<String> names) {
        if (names.contains(ContentBiomeControl.shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        ResourceLocation name = biome.getRegistryName();
        return name != null && names.contains(name.toString().toLowerCase(Locale.ROOT));
    }
}
