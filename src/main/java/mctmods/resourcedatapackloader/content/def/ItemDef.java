package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record ItemDef(ResourceLocation key, String type, String creativeTab, boolean alwaysEdible, List<ItemVariant> variants, List<String> requires, int useDuration, boolean eat, String container,
                      String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown, @Nullable ContainerDef holds, String rolls, String rollStore, boolean passesTurn, String locates,
                      String curioSlot, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, @Nullable PortwayItemDef portway) {
    public boolean slotted() { return !curioSlot.isEmpty(); }

    public boolean worn() { return slotted() && (!wornEffects.isEmpty() || !wornAttributes.isEmpty()); }
}
