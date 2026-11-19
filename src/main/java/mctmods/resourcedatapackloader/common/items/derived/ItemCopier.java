package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.Unsafes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;

final class ItemCopier {
    private ItemCopier() {}

    static Item copy(Item base, Class<?> type, Identifier key, ItemDef def) {
        Item copy = (Item) Unsafes.allocate(type);
        FieldCopy.all(base, copy);
        ResourceKey<Item> from = BuiltInRegistries.ITEM.getResourceKey(base).orElseThrow();
        ResourceKey<Item> own = ResourceKey.create(Registries.ITEM, key);
        copy.builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(copy);
        copy.descriptionId = Util.makeDescriptionId("item", key);
        copy.defaultResource = null;
        ComponentCopy.carry(from, own, key, def);
        return copy;
    }
}
