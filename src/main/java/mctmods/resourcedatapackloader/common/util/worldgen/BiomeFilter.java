package mctmods.resourcedatapackloader.common.util.worldgen;

import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class BiomeFilter {
    private BiomeFilter() {}

    public static boolean allowsDimension(Collection<Identifier> dimensions, boolean dimensionsAreBlacklist, Identifier dimension) {
        if (dimensions.isEmpty()) { return true; }
        return dimensions.contains(dimension) != dimensionsAreBlacklist;
    }

    public static boolean blocked(Collection<Identifier> named, Collection<TagKey<Biome>> tags, boolean biomesAreBlacklist, Holder<Biome> biome) { return matches(named, tags, biome) == biomesAreBlacklist; }

    public static boolean matches(Collection<Identifier> named, Collection<TagKey<Biome>> tags, Holder<Biome> biome) {
        for (Identifier id : named) {
            if (biome.is(id)) { return true; }
        }
        for (TagKey<Biome> tag : tags) {
            if (biome.is(tag)) { return true; }
        }
        return false;
    }
}
