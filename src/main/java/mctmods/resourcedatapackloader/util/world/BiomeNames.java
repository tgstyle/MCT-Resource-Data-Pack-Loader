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

    public static String title(Biome biome) {
        ResourceLocation name = biome.getRegistryName();
        if (name == null) { return ""; }
        StringBuilder title = new StringBuilder();
        for (String word : name.getPath().split("_")) {
            if (word.isEmpty()) { continue; }
            if (title.length() > 0) { title.append(' '); }
            title.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return title.toString();
    }

    public static boolean named(Biome biome, Set<String> names) {
        if (names.contains(ContentBiomeControl.shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        ResourceLocation name = biome.getRegistryName();
        return name != null && names.contains(name.toString().toLowerCase(Locale.ROOT));
    }
}
