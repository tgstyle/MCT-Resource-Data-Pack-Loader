package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class VillagerDef {
    public final ResourceLocation registryName;
    public final String texture;
    public final String zombieTexture;
    public final List<String> careers;
    public final List<String> requires;

    public VillagerDef(ResourceLocation registryName, String texture, String zombieTexture, List<String> careers, List<String> requires) {
        this.registryName = registryName;
        this.texture = texture;
        this.zombieTexture = zombieTexture;
        this.careers = careers;
        this.requires = requires;
    }
}
