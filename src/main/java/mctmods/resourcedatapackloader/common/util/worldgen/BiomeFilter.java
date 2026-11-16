package mctmods.resourcedatapackloader.common.util.worldgen;

import java.util.Collection;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public final class BiomeFilter {
    private BiomeFilter() {}

    public static boolean allowsDimension(Collection<Integer> dimensions, boolean dimensionsAreBlacklist, int dimension) {
        if (dimensions.isEmpty()) { return true; }
        return dimensions.contains(dimension) != dimensionsAreBlacklist;
    }

    public static boolean blocked(Collection<Biome> named, Collection<BiomeDictionary.Type> types, boolean biomesAreBlacklist, Biome biome) { return matches(named, types, biome) == biomesAreBlacklist; }

    public static boolean matches(Collection<Biome> named, Collection<BiomeDictionary.Type> types, Biome biome) {
        if (named.contains(biome)) { return true; }
        for (BiomeDictionary.Type type : types) {
            if (BiomeDictionary.hasType(biome, type)) { return true; }
        }
        return false;
    }
}
