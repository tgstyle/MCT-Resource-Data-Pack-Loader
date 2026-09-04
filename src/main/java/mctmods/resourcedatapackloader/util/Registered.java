package mctmods.resourcedatapackloader.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

public final class Registered {
    private Registered() {}

    @Nullable public static <T> T find(Registry<T> registry, @Nullable ResourceLocation key) { return key != null && registry.containsKey(key) ? registry.get(key) : null; }

    @Nullable public static <T> Holder<T> holder(Registry<T> registry, @Nullable ResourceLocation key) { return key == null ? null : registry.getHolder(key).orElse(null); }
}
