package mctmods.resourcedatapackloader.common.pack.definitions.items;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record DerivedDef(ResourceLocation base, JsonObject set, JsonObject returns, @Nullable String addTo, String name) {}
