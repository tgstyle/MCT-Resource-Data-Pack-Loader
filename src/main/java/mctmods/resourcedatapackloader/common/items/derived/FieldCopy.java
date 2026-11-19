package mctmods.resourcedatapackloader.common.items.derived;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldCopy {
    private FieldCopy() {}

    public static void all(Object from, Object to) {
        for (Class<?> level = from.getClass(); level != null && level != Object.class; level = level.getSuperclass()) {
            for (Field field : level.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) { continue; }
                try {
                    field.setAccessible(true);
                    Object value = field.get(from);
                    field.set(to, value == from ? to : value);
                }
                catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Could not copy field " + field.getName() + " of " + level.getName() + ": " + e, e);
                }
            }
        }
    }
}
