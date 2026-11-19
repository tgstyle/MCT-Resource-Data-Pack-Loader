package mctmods.resourcedatapackloader.common.pack.definitions.items;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record ItemDef(Identifier key, String type, String creativeTab, boolean alwaysEdible, List<ItemVariant> variants, List<String> requires, int useDuration, boolean eat, String container,
                      String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes, float attackSpeed, int cooldown, @Nullable ContainerDef holds, String rolls, String rollStore, boolean passesTurn, LocatorDef locator,
                      String curioSlot, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, @Nullable PortwayItemDef portway, boolean sleepingBag, @Nullable ContainerUpgradeDef upgrade, int weight, @Nullable FireworkDef firework, @Nullable SeasonClockDef seasonClock, String runs, @Nullable ScriptRef script, @Nullable RecordDef record, @Nullable DerivedDef derived) {
    public static ItemDef derived(Identifier key, String creativeTab, List<String> requires, String curioSlot, List<PotionEffectDef> wornEffects, List<AttributeDef> wornAttributes, @Nullable WeaponDef weapon, int weight, DerivedDef derived) {
        return new ItemDef(key, "basic", creativeTab, false, List.of(), requires, 32, false, "", "", "", "", "", "minecraft:farmland", List.of(), Float.NaN, 0, null, "", "", false, new LocatorDef("", false, "", 0), curioSlot, wornEffects, wornAttributes, weapon, null, false, null, weight, null, null, "", null, null, derived);
    }

    public String locates() { return locator.place(); }

    public boolean acts() { return !runs.isEmpty() || script != null; }

    public boolean slotted() { return !curioSlot.isEmpty(); }

    public boolean worn() { return slotted() && (!wornEffects.isEmpty() || !wornAttributes.isEmpty()); }
}
