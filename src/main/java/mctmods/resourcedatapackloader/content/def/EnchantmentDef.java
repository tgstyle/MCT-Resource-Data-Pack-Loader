package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record EnchantmentDef(ResourceLocation key, String name, int maxLevel, String rarity, List<String> slots, String category, List<String> items, int minBase, int minPerLevel, int maxBase, int maxPerLevel, boolean treasure, List<EnchantmentEffectDef> effects, List<String> requires) {}
