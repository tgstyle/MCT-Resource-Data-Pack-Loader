package mctmods.resourcedatapackloader.content.def;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class WorldTemplateDef {
    public static final String VOID = "void";
    public final ResourceLocation registryName;
    public final String name;
    public final String fallback;
    public final Map<String, String> roles;
    public final Map<String, Boolean> structures;
    @Nullable public final JsonObject settings;
    public final List<Integer> dimensions;
    public final List<String> requires;

    public WorldTemplateDef(ResourceLocation registryName, String name, String fallback, Map<String, String> roles, Map<String, Boolean> structures, @Nullable JsonObject settings, List<Integer> dimensions, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.fallback = fallback;
        this.roles = roles;
        this.structures = structures;
        this.settings = settings;
        this.dimensions = dimensions;
        this.requires = requires;
    }

    public String getKey() { return registryName.toString(); }
}
