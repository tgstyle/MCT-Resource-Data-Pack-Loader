package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record ItemDef(Identifier key, String type, String creativeTab, boolean alwaysEdible, List<ItemVariant> variants, List<String> requires, int useDuration, boolean eat, String container,
                      String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown, @Nullable ContainerDef holds, String rolls, boolean passesTurn) {}
