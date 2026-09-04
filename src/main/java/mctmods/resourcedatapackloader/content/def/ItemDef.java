package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record ItemDef(ResourceLocation key, String type, String creativeTab, boolean alwaysEdible, List<ItemVariant> variants, List<String> requires, int useDuration, boolean eat, String container,
                      String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown) {}
