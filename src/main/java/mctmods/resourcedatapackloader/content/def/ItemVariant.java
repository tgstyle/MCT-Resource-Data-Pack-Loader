package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record ItemVariant(ResourceLocation id, String name, String rarity, int maxSize, List<String> tags, int healAmount, float saturation, @Nullable String potion) {}
