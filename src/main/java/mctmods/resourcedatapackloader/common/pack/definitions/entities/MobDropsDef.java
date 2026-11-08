package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.util.ItemMatch;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record MobDropsDef(ResourceLocation key, List<String> entities, List<ItemMatch> strip, boolean stripTagged, List<Add> adds) {
    public record Add(Item item, int min, int max, float chance, List<ItemMatch> killerHolds, List<String> damageTypes, boolean requireAll, List<ItemMatch> unlessDropped) {}
}
