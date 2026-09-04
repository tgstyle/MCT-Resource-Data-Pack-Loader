package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record MaterialDef(ResourceLocation key, int harvestLevel, int durability, float efficiency, float damage, int enchantability, int[] reduction, float toughness, String equipSound, String armorTexture, String repairItem, List<String> requires) {}
