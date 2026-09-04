package mctmods.resourcedatapackloader.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import javax.annotation.Nullable;

public final class Registries {
    private Registries() {}

    @Nullable public static <T extends IForgeRegistryEntry<T>> T find(IForgeRegistry<T> registry, ResourceLocation key) { return registry.containsKey(key) ? registry.getValue(key) : null; }
}
