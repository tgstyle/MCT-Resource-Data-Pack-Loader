package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record PotionTypeDef(ResourceLocation key, String baseName, List<PotionEffectDef> effects, List<String> requires) {}
