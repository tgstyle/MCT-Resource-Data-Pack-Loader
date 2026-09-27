package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record ItemVariant(Identifier id, String name, String rarity, int maxSize, List<String> tags, int healAmount, float saturation, @Nullable String potion) {}
