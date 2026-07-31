package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class PotionTypeDef {
    public final ResourceLocation registryName;
    public final String baseName;
    public final List<PotionEffectDef> effects;
    public final List<String> requires;

    public PotionTypeDef(ResourceLocation registryName, String baseName, List<PotionEffectDef> effects, List<String> requires) {
        this.registryName = registryName;
        this.baseName = baseName;
        this.effects = effects;
        this.requires = requires;
    }
}
