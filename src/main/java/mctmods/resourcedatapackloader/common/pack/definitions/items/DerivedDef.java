package mctmods.resourcedatapackloader.common.pack.definitions.items;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class DerivedDef {
    public final ResourceLocation base;
    public final JsonObject set;
    public final JsonObject returns;
    @Nullable public final String addTo;
    public final String name;

    public DerivedDef(ResourceLocation base, JsonObject set, JsonObject returns, @Nullable String addTo, String name) {
        this.base = base;
        this.set = set;
        this.returns = returns;
        this.addTo = addTo;
        this.name = name;
    }
}
