package mctmods.resourcedatapackloader.common.pack.definitions.items;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayItemDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record ItemDef(Identifier key, String type, String creativeTab, boolean alwaysEdible, List<ItemVariant> variants, List<String> requires, int useDuration, boolean eat, String container,
                      String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown, @Nullable ContainerDef holds, String rolls, String rollStore, boolean passesTurn, String locates,
                      String curioSlot, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, @Nullable PortwayItemDef portway, boolean sleepingBag, @Nullable ContainerUpgradeDef upgrade, int weight) {
    public boolean slotted() { return !curioSlot.isEmpty(); }

    public boolean worn() { return slotted() && (!wornEffects.isEmpty() || !wornAttributes.isEmpty()); }
}
