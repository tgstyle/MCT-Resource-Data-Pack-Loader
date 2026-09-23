package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Locale;

public record TabDef(ResourceLocation key, String label, String icon, List<String> requires) {
    public ResourceLocation id() {
        ResourceLocation id = ResourceLocation.tryBuild(key.getNamespace(), label.toLowerCase(Locale.ROOT));
        return id == null ? key : id;
    }
}
