package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.MemberNames;
import mctmods.resourcedatapackloader.core.util.Unsafes;

import java.lang.reflect.Field;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class ItemCopier {
    private ItemCopier() {}

    static Item copy(Item base, Class<?> type) {
        Item copy = (Item) Unsafes.allocate(type);
        FieldCopy.all(base, copy);
        write(copy, "descriptionId", null);
        return copy;
    }

    static void holder(Item copy) { write(copy, "builtInRegistryHolder", BuiltInRegistries.ITEM.createIntrusiveHolder(copy)); }

    private static void write(Item item, String name, Object value) {
        Field found = MemberNames.field(Item.class, name);
        if (found == null) { throw new IllegalStateException("Item has no field " + name); }
        try {
            found.setAccessible(true);
            found.set(item, value);
        }
        catch (ReflectiveOperationException e) { throw new IllegalStateException("Could not write " + name + " of " + item.getClass().getName() + ": " + e, e); }
    }
}
