package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.Registries;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import javax.annotation.Nullable;

public final class Biomes {
    private Biomes() {}

    @Nullable public static Biome byName(String name) {
        if (name == null || name.trim().isEmpty()) { return null; }
        ResourceLocation key = new ResourceLocation(name.trim());
        return Registries.find(ForgeRegistries.BIOMES, key);
    }
}
