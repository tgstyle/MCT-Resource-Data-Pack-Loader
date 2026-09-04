package mctmods.resourcedatapackloader.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import javax.annotation.Nullable;

public final class Registered {
    private Registered() {}

    @Nullable public static <T> T find(IForgeRegistry<T> registry, @Nullable ResourceLocation key) { return key != null && registry.containsKey(key) ? registry.getValue(key) : null; }
}
