package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.MemberNames;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;

public final class ItemCopier {
    private static final String DELEGATE = "net.minecraftforge.registries.RegistryDelegate";

    private ItemCopier() {}

    public static Item copy(Item base, Class<?> type, ResourceLocation key) {
        Item copy = (Item) FieldCopy.copy(base, type);
        try {
            Constructor<?> made = Class.forName(DELEGATE).getDeclaredConstructor(Object.class, Class.class);
            made.setAccessible(true);
            write(copy, "delegate", made.newInstance(copy, Item.class));
            write(copy, "registryName", null);
            copy.setRegistryName(key);
            copy.setTranslationKey(key.toString());
            write(copy, "properties", properties(base));
            write(copy, "toolClasses", new HashMap<>(toolClasses(base)));
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not copy item " + key + ": " + e, e);
        }
        return copy;
    }

    @SuppressWarnings("unchecked") private static Map<String, Integer> toolClasses(Item base) throws ReflectiveOperationException {
        return (Map<String, Integer>) field("toolClasses").get(base);
    }

    @SuppressWarnings("unchecked") private static RegistrySimple<ResourceLocation, IItemPropertyGetter> properties(Item base) throws ReflectiveOperationException {
        Object old = field("properties").get(base);
        Field objects = MemberNames.accessibleField(RegistrySimple.class, "registryObjects");
        RegistrySimple<ResourceLocation, IItemPropertyGetter> made = new RegistrySimple<>();
        for (Map.Entry<ResourceLocation, IItemPropertyGetter> entry : ((Map<ResourceLocation, IItemPropertyGetter>) objects.get(old)).entrySet()) { made.putObject(entry.getKey(), entry.getValue()); }
        return made;
    }

    private static void write(Item item, String name, Object value) throws ReflectiveOperationException { field(name).set(item, value); }

    private static Field field(String name) { return MemberNames.accessibleField(Item.class, name); }
}
