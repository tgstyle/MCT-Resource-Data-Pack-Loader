package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import java.util.Locale;
import java.util.Set;

public final class BiomeNames {
    private BiomeNames() {}

    public static boolean named(Biome biome, Set<String> names) {
        if (names.contains(ContentBiomeControl.shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        ResourceLocation name = biome.getRegistryName();
        return name != null && names.contains(name.toString().toLowerCase(Locale.ROOT));
    }
}
