package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;

final class ComponentCopy {
    private static final String ITEM_NAME = "minecraft:item_name";

    private ComponentCopy() {}

    @SuppressWarnings("unchecked")
    static void carry(ResourceKey<Item> from, ResourceKey<Item> own, Identifier key, ItemDef def) {
        DataComponentInitializers all = BuiltInRegistries.DATA_COMPONENT_INITIALIZERS;
        int found = 0;
        for (DataComponentInitializers.InitializerEntry<?> entry : List.copyOf(all.initializers)) {
            if (entry.key().equals(from)) {
                all.add(own, (DataComponentInitializers.Initializer<Item>) entry.initializer());
                found++;
            }
        }
        if (found == 0) { throw new IllegalStateException("Item " + from.identifier() + " has no component initializer to copy"); }
        DerivedDef derived = Objects.requireNonNull(def.derived());
        boolean named = !derived.name().isEmpty();
        if (named && derived.components().has(ITEM_NAME)) { ContentLog.LOGGER.warn("Derived item {} gives both a name and an item_name component, the component wins", key); }
        Component title = named ? Component.literal(derived.name()) : Component.translatable(Util.makeDescriptionId("item", key));
        all.add(own, (builder, context, _) -> {
            builder.set(DataComponents.ITEM_NAME, title).set(DataComponents.ITEM_MODEL, key);
            ComponentPatch.apply(builder, context, derived.components(), key);
        });
    }
}
