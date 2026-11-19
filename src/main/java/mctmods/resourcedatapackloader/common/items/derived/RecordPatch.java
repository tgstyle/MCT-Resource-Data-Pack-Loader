package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

final class RecordPatch {
    private RecordPatch() {}

    static boolean holds(Object value) { return value.getClass().isRecord() || value instanceof Holder<?> holder && holder.value().getClass().isRecord(); }

    @Nullable static Object rebuild(Object value, JsonObject subSet, ResourceLocation owner, String path) {
        if (value instanceof Holder<?> holder) {
            Object inner = rebuild(holder.value(), subSet, owner, path);
            return inner == null ? null : Holder.direct(inner);
        }
        Class<?> type = value.getClass();
        RecordComponent[] parts = type.getRecordComponents();
        Object[] values = new Object[parts.length];
        Class<?>[] types = new Class<?>[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                Method accessor = parts[i].getAccessor();
                accessor.setAccessible(true);
                values[i] = accessor.invoke(value);
                types[i] = parts[i].getType();
            }
            for (Map.Entry<String, JsonElement> entry : subSet.entrySet()) { assign(parts, values, entry.getKey(), entry.getValue(), owner, path); }
            Constructor<?> constructor = type.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(values);
        }
        catch (ReflectiveOperationException e) { throw new IllegalStateException("Could not rebuild " + type.getName() + ": " + e, e); }
    }

    private static void assign(RecordComponent[] parts, Object[] values, String key, JsonElement json, ResourceLocation owner, String path) {
        int dot = key.indexOf('.');
        String name = dot < 0 ? key : key.substring(0, dot);
        String shown = path.isEmpty() ? key : path + "." + key;
        int index = -1;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].getName().equals(name)) { index = i; }
        }
        if (index < 0) {
            ContentLog.LOGGER.error("Derived item {} sets {}, but there is no field named {} there, leaving it out", owner, shown, name);
            return;
        }
        if (dot < 0) {
            ValueCoercer.Result result = ValueCoercer.coerce(json, parts[index].getType(), parts[index].getGenericType(), owner);
            if (result.failed()) { ContentLog.LOGGER.error("{} sets {} to {}, which is {}, leaving it out", owner, shown, json, result.problem); }
            else { values[index] = result.value; }
            return;
        }
        Object current = values[index];
        if (current == null || !holds(current)) {
            ContentLog.LOGGER.error("Derived item {} sets {}, but paths do not go through {}, leaving it out", owner, shown, parts[index].getType().getSimpleName());
            return;
        }
        JsonObject rest = new JsonObject();
        rest.add(key.substring(dot + 1), json);
        Object made = rebuild(current, rest, owner, path.isEmpty() ? name : path + "." + name);
        if (made != null) { values[index] = made; }
    }
}
