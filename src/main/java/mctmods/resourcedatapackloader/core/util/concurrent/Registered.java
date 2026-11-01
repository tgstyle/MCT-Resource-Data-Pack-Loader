package mctmods.resourcedatapackloader.core.util.concurrent;

import mctmods.resourcedatapackloader.core.config.Settings;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public final class Registered {
    private Registered() {}

    @Nullable public static <T> T find(IForgeRegistry<T> registry, @Nullable ResourceLocation key) { return key != null && registry.containsKey(key) ? registry.getValue(key) : null; }

    @Nullable public static Block block(@Nullable String name) { return name == null ? null : find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(Settings.lower(name))); }

    @Nullable public static ResourceLocation tagId(String named) { return ResourceLocation.tryParse(named.startsWith("#") ? named.substring(1) : named); }
}
