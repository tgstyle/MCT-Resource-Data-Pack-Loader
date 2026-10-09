package mctmods.resourcedatapackloader.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import javax.annotation.Nullable;

public final class Registered {
    private Registered() {}

    @Nullable public static <T> T find(Registry<T> registry, @Nullable ResourceLocation key) { return key != null && registry.containsKey(key) ? registry.get(key) : null; }

    @Nullable public static Block block(@Nullable String name) { return name == null ? null : find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(Settings.lower(name))); }

    @Nullable public static ResourceLocation tagId(String named) { return ResourceLocation.tryParse(named.startsWith("#") ? named.substring(1) : named); }

    @Nullable public static <T> Holder<T> holder(Registry<T> registry, @Nullable ResourceLocation key) { return key == null ? null : registry.getHolder(key).orElse(null); }
}
