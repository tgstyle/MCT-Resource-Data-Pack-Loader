package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.core.util.Registries;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class Biomes {
    private Biomes() {}

    @Nullable public static Biome byName(String name) {
        if (name == null || name.trim().isEmpty()) { return null; }
        ResourceLocation key = new ResourceLocation(name.trim());
        return Registries.find(ForgeRegistries.BIOMES, key);
    }

    @Nullable public static Biome byName(String name, String namespace) {
        if (name == null || name.trim().isEmpty()) { return null; }
        String trimmed = name.trim();
        if (trimmed.indexOf(':') < 0) {
            Biome own = byName(namespace + ":" + trimmed);
            if (own != null) { return own; }
        }
        return byName(trimmed);
    }
}
