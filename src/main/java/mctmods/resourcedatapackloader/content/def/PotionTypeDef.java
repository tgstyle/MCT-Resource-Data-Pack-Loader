package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record PotionTypeDef(Identifier key, String baseName, List<PotionEffectDef> effects, List<String> requires) {}
