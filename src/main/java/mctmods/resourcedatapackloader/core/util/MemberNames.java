package mctmods.resourcedatapackloader.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;

public final class MemberNames {
    private static final String RESOURCE = "/resourcedatapackloader/script_names.gz";
    private static final String[] NONE = new String[0];
    private static volatile Map<String, String[]> names;

    private MemberNames() {}

    public static String[] readables(String runtimeName) { return table().getOrDefault(runtimeName, NONE); }

    public static String readable(String runtimeName) {
        String[] found = readables(runtimeName);
        return found.length == 0 ? runtimeName : found[0];
    }

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

    public static boolean hasMethod(Class<?> type, String name) {
        for (Method method : methods(type)) {
            if (readable(method.getName()).equals(name)) { return true; }
        }
        return false;
    }

    @Nullable public static Method method(Class<?> type, String key) {
        int paren = key.indexOf('(');
        String name = paren < 0 ? key : key.substring(0, paren);
        List<String> wanted = paren < 0 ? null : parameters(key.substring(paren + 1, key.length() - (key.endsWith(")") ? 1 : 0)));
        for (Class<?> level : levels(type)) {
            List<Method> found = new ArrayList<>();
            for (Method method : level.getDeclaredMethods()) {
                if (!method.isBridge() && readable(method.getName()).equals(name) && (wanted == null || parameters(method).equals(wanted))) { found.add(method); }
            }
            if (found.size() == 1) { return found.get(0); }
            if (found.size() > 1) {
                ContentLog.LOGGER.error("{} has several methods named {} ({}), name one with its parameter types", level.getName(), name, describe(found));
                return null;
            }
        }
        List<Method> defaults = new ArrayList<>();
        Set<Class<?>> seen = new HashSet<>();
        for (Class<?> level : levels(type)) { interfaces(level, defaults, seen); }
        List<Method> found = new ArrayList<>();
        for (Method method : defaults) {
            if (!method.isBridge() && readable(method.getName()).equals(name) && (wanted == null || parameters(method).equals(wanted))) { found.add(method); }
        }
        if (found.size() == 1) { return found.get(0); }
        if (found.size() > 1) {
            ContentLog.LOGGER.error("{} has several methods named {} ({}), name one with its parameter types", type.getName(), name, describe(found));
            return null;
        }
        ContentLog.LOGGER.error("{} has no method named {}", type, key);
        return null;
    }

    public static List<Method> methods(Class<?> type) {
        List<Method> all = new ArrayList<>();
        for (Class<?> level : levels(type)) { Collections.addAll(all, level.getDeclaredMethods()); }
        Set<Class<?>> seen = new HashSet<>();
        for (Class<?> level : levels(type)) { interfaces(level, all, seen); }
        return all;
    }

    private static List<Class<?>> levels(Class<?> type) {
        List<Class<?>> levels = new ArrayList<>();
        for (Class<?> level = type; level != null; level = level.getSuperclass()) { levels.add(level); }
        return levels;
    }

    private static void interfaces(Class<?> from, List<Method> into, Set<Class<?>> seen) {
        for (Class<?> each : from.getInterfaces()) {
            if (!seen.add(each)) { continue; }
            for (Method method : each.getDeclaredMethods()) {
                if (!method.isSynthetic() && !Modifier.isStatic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) { into.add(method); }
            }
            interfaces(each, into, seen);
        }
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

    private static Map<String, String[]> table() {
        Map<String, String[]> table = names;
        return table == null ? load() : table;
    }

    private static synchronized Map<String, String[]> load() {
        if (names != null) { return names; }
        Map<String, String[]> table = new HashMap<>();
        try (InputStream in = MemberNames.class.getResourceAsStream(RESOURCE)) {
            if (in == null) { throw new IOException("resource missing"); }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(in), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int space = line.indexOf(' ');
                    if (space > 0 && space < line.length() - 1) { table.put(line.substring(0, space), new String[]{line.substring(space + 1)}); }
                }
            }
        }
        catch (IOException e) {
            ContentLog.LOGGER.error("Script name table {} could not be read: {}", RESOURCE, e.getMessage());
            table = Collections.emptyMap();
        }
        names = table;
        return table;
    }
}
