package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.MemberNames;
import mctmods.resourcedatapackloader.core.util.Unsafes;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemCopier {
    private ItemCopier() {}

    public static Item copy(Item base, Class<?> type, ResourceLocation key) {
        Item copy = (Item) Unsafes.allocate(type);
        FieldCopy.all(base, copy);
        try { field("descriptionId").set(copy, null); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException("Could not copy item " + key + ": " + e, e); }
        return copy;
    }

    public static void attach(Item copy) {
        try { field("builtInRegistryHolder").set(copy, items().createIntrusiveHolder(copy)); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException("Could not attach item " + copy + ": " + e, e); }
    }

    public static void release(Item copy) {
        Field held = MemberNames.field(MappedRegistry.class, "unregisteredIntrusiveHolders");
        if (held == null) { return; }
        try {
            held.setAccessible(true);
            Object map = held.get(items());
            if (map instanceof Map<?, ?> pending) { pending.remove(copy); }
        }
        catch (ReflectiveOperationException e) { ContentLog.LOGGER.error("Could not release the registry holder of {}: {}", copy, e.toString()); }
    }

    @SuppressWarnings("unchecked") private static MappedRegistry<Item> items() { return (MappedRegistry<Item>) BuiltInRegistries.REGISTRY.get(Registries.ITEM.location()); }

    private static Field field(String name) {
        Field found = MemberNames.field(Item.class, name);
        if (found == null) { throw new IllegalStateException("Item has no field " + name); }
        found.setAccessible(true);
        return found;
    }
}
