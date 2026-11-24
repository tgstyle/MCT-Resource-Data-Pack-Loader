package mctmods.resourcedatapackloader.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class MemberNames {
    private MemberNames() {}

    public static String readable(String runtimeName) { return runtimeName; }

    @Nullable public static Field field(Class<?> type, String key) {
        int dot = key.lastIndexOf('.');
        String owner = dot < 0 ? "" : key.substring(0, dot);
        String name = dot < 0 ? key : key.substring(dot + 1);
        for (Class<?> level : levels(type)) {
            if (!owner.isEmpty() && !level.getSimpleName().equals(owner) && !level.getName().equals(owner)) { continue; }
            for (Field field : level.getDeclaredFields()) {
                if (readable(field.getName()).equals(name)) { return field; }
            }
        }
        ContentLog.LOGGER.error("{} has no field named {}", type, key);
        return null;
    }

    @Nullable public static Field find(Class<?> type, String name, Predicate<Field> keep) {
        for (Class<?> level : levels(type)) {
            for (Field field : level.getDeclaredFields()) {
                if (keep.test(field) && readable(field.getName()).equals(name)) { return field; }
            }
        }
        return null;
    }

    @Nullable public static Method method(Class<?> type, String key) {
        int paren = key.indexOf('(');
        String name = paren < 0 ? key : key.substring(0, paren);
        List<String> wanted = paren < 0 ? null : parameters(key.substring(paren + 1, key.length() - (key.endsWith(")") ? 1 : 0)));
        for (Class<?> level : levels(type)) {
            List<Method> found = matching(Arrays.asList(level.getDeclaredMethods()), name, wanted);
            if (found.size() == 1) { return found.getFirst(); }
            if (found.size() > 1) {
                ContentLog.LOGGER.error("{} has several methods named {} ({}), name one with its parameter types", level.getName(), name, describe(found));
                return null;
            }
        }
        List<Method> defaults = new ArrayList<>();
        for (Class<?> level : levels(type)) {
            for (Class<?> face : level.getInterfaces()) { defaults(face, defaults); }
        }
        List<Method> found = matching(defaults, name, wanted);
        if (found.size() == 1) { return found.getFirst(); }
        if (found.size() > 1) {
            ContentLog.LOGGER.error("{} has several methods named {} ({}), name one with its parameter types", type, name, describe(found));
            return null;
        }
        ContentLog.LOGGER.error("{} has no method named {}", type, key);
        return null;
    }

    public static List<Method> methods(Class<?> type) {
        List<Method> all = new ArrayList<>();
        for (Class<?> level : levels(type)) { Collections.addAll(all, level.getDeclaredMethods()); }
        for (Class<?> level : levels(type)) {
            for (Class<?> face : level.getInterfaces()) { defaults(face, all); }
        }
        return all;
    }

    private static List<Class<?>> levels(Class<?> type) {
        List<Class<?>> levels = new ArrayList<>();
        for (Class<?> level = type; level != null; level = level.getSuperclass()) { levels.add(level); }
        return levels;
    }

    private static void defaults(Class<?> face, List<Method> into) {
        for (Method method : face.getDeclaredMethods()) {
            if (method.isDefault()) { into.add(method); }
        }
        for (Class<?> parent : face.getInterfaces()) { defaults(parent, into); }
    }

    private static List<Method> matching(List<Method> methods, String name, @Nullable List<String> wanted) {
        List<Method> found = new ArrayList<>();
        for (Method method : methods) {
            if (readable(method.getName()).equals(name) && (wanted == null || parameters(method).equals(wanted))) { found.add(method); }
        }
        return found;
    }

    private static List<String> parameters(String list) {
        List<String> names = new ArrayList<>();
        for (String part : list.split(",")) {
            if (!part.trim().isEmpty()) { names.add(part.trim()); }
        }
        return names;
    }

    private static List<String> parameters(Method method) {
        List<String> names = new ArrayList<>();
        for (Class<?> parameter : method.getParameterTypes()) { names.add(parameter.getSimpleName()); }
        return names;
    }

    private static String describe(List<Method> methods) {
        List<String> shown = new ArrayList<>();
        for (Method method : methods) { shown.add(readable(method.getName()) + "(" + String.join(",", parameters(method)) + ")"); }
        return String.join("; ", shown);
    }
}
