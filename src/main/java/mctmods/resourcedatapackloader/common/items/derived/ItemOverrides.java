package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;
import mctmods.resourcedatapackloader.core.util.MemberNames;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ItemOverrides {
    private ItemOverrides() {}

    public static List<ItemClassMaker.Override> plan(Class<?> base, ResourceLocation key, JsonObject returns, @Nullable Object source) {
        List<ItemClassMaker.Override> planned = new ArrayList<>();
        Set<Method> seen = new HashSet<>();
        for (Map.Entry<String, JsonElement> entry : returns.entrySet()) {
            Method method = MemberNames.method(base, ItemAliases.returns(entry.getKey()));
            if (method == null) { continue; }
            if (!seen.add(method)) {
                ContentLog.LOGGER.error("Derived item {} returns a constant for {} twice, leaving the second out", key, entry.getKey());
                continue;
            }
            String problem = refusal(method, base);
            if (problem != null) {
                ContentLog.LOGGER.error("Derived item {} returns a constant for {}, which {}, leaving it out", key, entry.getKey(), problem);
                continue;
            }
            Object constant = constant(method, entry.getKey(), entry.getValue(), key, source);
            if (constant != null) { planned.add(new ItemClassMaker.Override(method, constant, planned.size())); }
        }
        return planned;
    }

    public static void store(ResourceLocation key, List<ItemClassMaker.Override> planned) {
        Object[] values = new Object[planned.size()];
        for (ItemClassMaker.Override each : planned) { values[each.slot()] = each.constant(); }
        DerivedConstants.put(key.toString(), values);
    }

    @Nullable private static String refusal(Method method, Class<?> base) {
        int modifiers = method.getModifiers();
        Class<?> declaring = method.getDeclaringClass();
        if (Modifier.isStatic(modifiers)) { return "is static"; }
        if (Modifier.isFinal(modifiers)) { return "is final in " + declaring.getName(); }
        if (Modifier.isPrivate(modifiers)) { return "is private in " + declaring.getName(); }
        if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers) && !packageOf(declaring).equals(packageOf(base))) { return "is package-private in " + declaring.getName(); }
        if (method.getReturnType() == void.class) { return "returns nothing"; }
        return null;
    }

    private static String packageOf(Class<?> type) {
        String name = type.getName();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(0, dot);
    }

    @Nullable @SuppressWarnings({"unchecked", "rawtypes"}) private static Object constant(Method method, String name, JsonElement json, ResourceLocation key, @Nullable Object source) {
        Class<?> type = method.getReturnType();
        Type generic = method.getGenericReturnType();
        boolean text = json.isJsonPrimitive() && json.getAsJsonPrimitive().isString();
        if (json.isJsonObject()) { return object(method, name, json.getAsJsonObject(), key, source); }
        if (type == ItemStack.class && text) {
            String stack = json.getAsString();
            return new DerivedConstants.Lazy(stack, () -> {
                ItemStack found = ContentStacks.parse(key, stack, 1);
                return found.isEmpty() ? null : found;
            });
        }
        if (text && RegistryTypes.find(type, generic) != null) {
            String entry = json.getAsString();
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id == null) {
                mismatch(key, name, json, "not a " + type.getSimpleName());
                return null;
            }
            return new DerivedConstants.Lazy(entry, () -> RegistryTypes.lookup(type, generic, id));
        }
        if (type == Component.class && text) { return Component.literal(json.getAsString()); }
        if (type.isEnum() && text) {
            Object found = Enums.byName((Class) type, json.getAsString());
            if (found == null) { mismatch(key, name, json, "not a " + type.getSimpleName()); }
            return found;
        }
        ValueCoercer.Result result = ValueCoercer.coerce(json, type, generic, key);
        if (result.failed()) { mismatch(key, name, json, result.problem); }
        return result.value;
    }

    @Nullable private static Object object(Method method, String name, JsonObject spec, ResourceLocation key, @Nullable Object source) {
        if (method.getParameterCount() != 0) {
            ContentLog.LOGGER.error("Derived item {} returns an object for {}, which takes arguments, and only methods without arguments can, leaving it out", key, name);
            return null;
        }
        if (source == null) {
            ContentLog.LOGGER.error("Derived item {} returns an object for {}, which has no object to copy, leaving it out", key, name);
            return null;
        }
        try {
            method.setAccessible(true);
            Object current = method.invoke(source);
            if (current == null) {
                ContentLog.LOGGER.error("Derived item {} returns an object for {}, which returns nothing to copy, leaving it out", key, name);
                return null;
            }
            return ObjectPatch.nested(current, spec, key, "returns." + name);
        }
        catch (InvocationTargetException | IllegalAccessException e) {
            ContentLog.LOGGER.error("Derived item {} could not read {} to copy it, leaving it out: {}", key, name, e.toString());
            return null;
        }
    }

    private static void mismatch(ResourceLocation key, String name, JsonElement json, @Nullable String problem) {
        ContentLog.LOGGER.error("Derived item {} returns {} for {}, which is {}, leaving it out", key, json, name, problem);
    }
}
