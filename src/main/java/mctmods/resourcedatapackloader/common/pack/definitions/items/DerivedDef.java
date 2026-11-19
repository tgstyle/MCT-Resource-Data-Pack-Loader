package mctmods.resourcedatapackloader.common.pack.definitions.items;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record DerivedDef(Identifier base, JsonObject set, JsonObject returns, JsonObject components, @Nullable String addTo, String name) {}
