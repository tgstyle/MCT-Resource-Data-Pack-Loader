package mctmods.resourcedatapackloader.common.pack.loading;

import net.minecraft.util.ResourceLocation;

public final class Registered<T, D> {
    private final ResourceLocation key;
    private final T value;
    public final D def;

    Registered(ResourceLocation key, T value, D def) {
        this.key = key;
        this.value = value;
        this.def = def;
    }

    public ResourceLocation getKey() { return key; }

    public T getValue() { return value; }
}
