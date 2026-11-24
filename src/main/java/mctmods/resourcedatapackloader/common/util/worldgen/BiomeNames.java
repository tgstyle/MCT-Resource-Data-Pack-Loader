package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomeControl;
import mctmods.resourcedatapackloader.core.util.IdNames;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class BiomeNames {
    private BiomeNames() {}

    public static boolean matches(List<String> wanted, Biome biome) {
        if (wanted.isEmpty()) { return true; }
        ResourceLocation name = biome.getRegistryName();
        for (String one : wanted) {
            if (IdNames.isTag(one)) {
                if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.getType(IdNames.tagPath(one).toUpperCase(Locale.ROOT)))) { return true; }
            }
            else if (names(one, name)) { return true; }
        }
        return false;
    }

    public static boolean known(String name) {
        for (ResourceLocation held : ForgeRegistries.BIOMES.getKeys()) {
            if (names(name, held)) { return true; }
        }
        return false;
    }

    private static boolean names(String one, ResourceLocation name) {
        return name != null && (one.equals(name.toString()) || one.equals(name.getPath()));
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
