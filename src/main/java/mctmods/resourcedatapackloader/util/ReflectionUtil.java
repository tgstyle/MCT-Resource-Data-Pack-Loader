package mctmods.resourcedatapackloader.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public class ReflectionUtil {
    @SuppressWarnings("unchecked") public static <T> T cast(Object in) { return (T) in; }

    public static <T> Class<? extends T> getClassOrDefault(String name, Class<? extends T> cl) {
        try {
            return cast(Class.forName(name));
        } catch (ClassNotFoundException ex) {
            return cl;
        }
    }

    public static MethodHandle constructHandle(Class<?> owner, Class<?>... args) {
        try {
            Constructor<?> constr = owner.getDeclaredConstructor(args);
            constr.setAccessible(true);
            return MethodHandles.lookup().unreflectConstructor(constr);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
