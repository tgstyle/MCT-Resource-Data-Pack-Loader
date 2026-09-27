package mctmods.resourcedatapackloader.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public final class Registered {
    private Registered() {}

    @Nullable public static <T> T find(Registry<T> registry, @Nullable Identifier key) { return key != null && registry.containsKey(key) ? registry.getValue(key) : null; }

    @Nullable public static <T> Holder<T> holder(Registry<T> registry, @Nullable Identifier key) { return key == null ? null : registry.get(key).orElse(null); }
}
