package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class ValueCoercer {
    private ValueCoercer() {}

    public static final class Result {
        @Nullable public final Object value;
        @Nullable public final String problem;

        private Result(@Nullable Object value, @Nullable String problem) {
            this.value = value;
            this.problem = problem;
        }

        public boolean failed() { return problem != null; }
    }

    public static Result coerce(JsonElement element, Class<?> type, Type generic, ResourceLocation owner) {
        if (element.isJsonArray()) { return array(element.getAsJsonArray(), type, generic, owner); }
        if (!element.isJsonPrimitive()) { return no(type); }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (type.isPrimitive() || isBox(type)) { return number(primitive, type); }
        if (type == String.class) { return primitive.isString() ? yes(primitive.getAsString()) : no(type); }
        if (!primitive.isString()) { return no(type); }
        String text = primitive.getAsString();
        if (type.isEnum()) { return named(type, text); }
        if (type == ResourceLocation.class) { return yes(new ResourceLocation(text)); }
        if (type == ItemStack.class) {
            ItemStack stack = ContentStacks.parse(owner, text, 1);
            return stack.isEmpty() ? no(type) : yes(stack);
        }
        if (IForgeRegistryEntry.class.isAssignableFrom(type)) { return entry(type, text); }
        return no(type);
    }

    private static Result array(JsonArray json, Class<?> type, Type generic, ResourceLocation owner) {
        Class<?> element;
        Type elementGeneric;
        if (type.isArray()) {
            element = type.getComponentType();
            elementGeneric = element;
        }
        else if (Collection.class.isAssignableFrom(type) && generic instanceof ParameterizedType && ((ParameterizedType) generic).getActualTypeArguments()[0] instanceof Class) {
            elementGeneric = ((ParameterizedType) generic).getActualTypeArguments()[0];
            element = (Class<?>) elementGeneric;
        }
        else { return no(type); }
        List<Object> values = new ArrayList<>();
        for (JsonElement each : json) {
            Result one = coerce(each, element, elementGeneric, owner);
            if (one.failed()) { return one; }
            values.add(one.value);
        }
        if (type.isArray()) {
            Object made = Array.newInstance(element, values.size());
            for (int i = 0; i < values.size(); i++) { Array.set(made, i, values.get(i)); }
            return yes(made);
        }
        if (type == Set.class || type == LinkedHashSet.class) { return yes(new LinkedHashSet<>(values)); }
        if (type == List.class || type == Collection.class || type == ArrayList.class) { return yes(values); }
        return no(type);
    }

    private static Result named(Class<?> type, String text) {
        for (Object constant : type.getEnumConstants()) {
            if (((Enum<?>) constant).name().equalsIgnoreCase(text.trim())) { return yes(constant); }
        }
        return no(type);
    }

    private static Result entry(Class<?> type, String text) {
        IForgeRegistry<?> registry = registry(type);
        ResourceLocation name = new ResourceLocation(text);
        if (registry == null || !registry.containsKey(name)) { return no(type); }
        Object found = registry.getValue(name);
        return type.isInstance(found) ? yes(found) : no(type);
    }

    @Nullable @SuppressWarnings({"unchecked", "rawtypes"}) static IForgeRegistry<?> registry(Class<?> type) {
        for (Class<?> level = type; level != null && IForgeRegistryEntry.class.isAssignableFrom(level); level = level.getSuperclass()) {
            IForgeRegistry<?> found = GameRegistry.findRegistry((Class) level);
            if (found != null) { return found; }
        }
        return null;
    }

    private static boolean isBox(Class<?> type) {
        return type == Boolean.class || type == Character.class || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class;
    }

    private static Result number(JsonPrimitive json, Class<?> type) {
        if (type == boolean.class || type == Boolean.class) { return json.isBoolean() ? yes(json.getAsBoolean()) : no(type); }
        if (type == char.class || type == Character.class) { return json.isString() && json.getAsString().length() == 1 ? yes(json.getAsString().charAt(0)) : no(type); }
        if (!json.isNumber()) { return no(type); }
        BigDecimal value = json.getAsBigDecimal();
        if (type == float.class || type == Float.class) {
            float narrowed = value.floatValue();
            return Float.isInfinite(narrowed) ? no(type) : yes(narrowed);
        }
        if (type == double.class || type == Double.class) {
            double wide = value.doubleValue();
            return Double.isInfinite(wide) ? no(type) : yes(wide);
        }
        if (value.stripTrailingZeros().scale() > 0) { return no(type); }
        try {
            long whole = value.longValueExact();
            if (type == long.class || type == Long.class) { return yes(whole); }
            if (type == int.class || type == Integer.class) { return whole == (int) whole ? yes((int) whole) : no(type); }
            if (type == short.class || type == Short.class) { return whole == (short) whole ? yes((short) whole) : no(type); }
            return whole == (byte) whole ? yes((byte) whole) : no(type);
        }
        catch (ArithmeticException e) { return no(type); }
    }

    private static Result yes(Object value) { return new Result(value, null); }

    private static Result no(Class<?> type) { return new Result(null, "not a " + type.getSimpleName()); }
}
