package mctmods.resourcedatapackloader.core.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class Unsafes {
    private static MethodHandle allocator;

    private Unsafes() {}

    public static <T> T allocate(Class<T> type) {
        try {
            return type.cast(allocator().invoke(type));
        } catch (Throwable e) {
            throw new IllegalStateException("Could not allocate " + type.getName() + ": " + e, e);
        }
    }

    private static synchronized MethodHandle allocator() throws ReflectiveOperationException {
        if (allocator == null) {
            Class<?> unsafe = Class.forName("sun.misc.Unsafe");
            Field field = unsafe.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            allocator = MethodHandles.lookup().findVirtual(unsafe, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(field.get(null));
        }
        return allocator;
    }
}
