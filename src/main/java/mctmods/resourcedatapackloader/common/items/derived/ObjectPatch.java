package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.MemberNames;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.primitives.Primitives;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class ObjectPatch {
    static final String SET = "set";
    static final String RETURNS = "returns";
    static final String ADD_TO = "addTo";
    private static final List<String> SPEC_KEYS = Collections.unmodifiableList(Arrays.asList(SET, RETURNS, ADD_TO));
    private static final JsonObject NONE = new JsonObject();

    private ObjectPatch() {}

    public static void apply(Object target, JsonObject set, @Nullable String addTo, ResourceLocation owner, String path) {
        Set<Object> owned = Collections.newSetFromMap(new IdentityHashMap<>());
        owned.add(target);
        for (Map.Entry<String, JsonElement> entry : set.entrySet()) {
            try { assign(target, entry.getKey().split("\\."), entry.getValue(), owned, owner, path); }
            catch (ReflectiveOperationException | RuntimeException e) { ContentLog.LOGGER.error("Derived item {} could not set {}, leaving it out: {}", owner, join(path, entry.getKey()), e.toString()); }
        }
        if (addTo != null) { DerivedAdds.queue(addTo, target, owner); }
    }

    @Nullable static Object nested(Object source, JsonObject spec, ResourceLocation owner, String path) {
        Json.within("derived item", owner, () -> {
            Json.unknownKeys(spec, path, SPEC_KEYS);
            return null;
        });
        JsonObject returns = member(spec, RETURNS, owner, path);
        Object made = copy(source, owner, path, returns);
        if (made == null) { return null; }
        String addTo = Json.getString(spec, ADD_TO, "").trim();
        apply(made, member(spec, SET, owner, path), addTo.isEmpty() ? null : addTo, owner, path);
        DerivedObjects.put(owner, path, made);
        return made;
    }

    private static void assign(Object target, String[] segments, JsonElement json, Set<Object> owned, ResourceLocation owner, String path) throws ReflectiveOperationException {
        Object holder = target;
        String walked = path;
        for (int i = 0; i < segments.length - 1; i++) {
            walked = join(walked, segments[i]);
            Field field = writable(holder, segments[i], owner, walked);
            if (field == null) { return; }
            Object value = field.get(holder);
            if (value == null) {
                DerivedLog.skip(owner, "sets below {}, which holds nothing", walked);
                return;
            }
            if (!owned.contains(value)) {
                Object clone = copy(value, owner, walked, NONE);
                if (clone == null) { return; }
                field.set(holder, clone);
                owned.add(clone);
                value = clone;
            }
            holder = value;
        }
        walked = join(walked, segments[segments.length - 1]);
        Field field = writable(holder, segments[segments.length - 1], owner, walked);
        if (field == null) { return; }
        Object value = valueFor(field, holder, json, owner, walked);
        if (value != DerivedConstants.MISSING) { field.set(holder, value); }
    }

    @Nullable private static Field writable(Object holder, String name, ResourceLocation owner, String path) {
        Field field = MemberNames.field(holder.getClass(), name);
        if (field == null) { return null; }
        if (Modifier.isStatic(field.getModifiers())) {
            DerivedLog.skip(owner, "sets {}, but static fields are shared by every item, not written", path);
            return null;
        }
        field.setAccessible(true);
        return field;
    }

    private static Object valueFor(Field field, Object holder, JsonElement json, ResourceLocation owner, String path) throws ReflectiveOperationException {
        Class<?> type = field.getType();
        if (json.isJsonObject()) {
            Object current = field.get(holder);
            if (current == null) {
                DerivedLog.skip(owner, "sets {} to an object, but it holds nothing to copy", path);
                return DerivedConstants.MISSING;
            }
            Object made = nested(current, json.getAsJsonObject(), owner, path);
            return made == null ? DerivedConstants.MISSING : made;
        }
        if (reuses(type, json)) { return reuse(type, json.getAsString(), owner, path); }
        ValueCoercer.Result result = ValueCoercer.coerce(json, type, field.getGenericType(), owner);
        if (result.failed()) {
            ContentLog.LOGGER.error("{} sets {} to {}, which is {}, leaving it out", owner, path, json, result.problem);
            return DerivedConstants.MISSING;
        }
        return result.value == null ? DerivedConstants.MISSING : result.value;
    }

    private static boolean reuses(Class<?> type, JsonElement json) {
        if (!Json.isString(json) || json.getAsString().indexOf(':') < 0) { return false; }
        return !Primitives.unwrap(type).isPrimitive() && type != String.class && type != ResourceLocation.class && type != ItemStack.class && !type.isArray() && !Collection.class.isAssignableFrom(type) && !IForgeRegistryEntry.class.isAssignableFrom(type);
    }

    private static Object reuse(Class<?> type, String text, ResourceLocation owner, String path) {
        Object found = null;
        try { found = DerivedObjects.get(new ResourceLocation(text), path); }
        catch (RuntimeException notAName) { ContentLog.LOGGER.debug("{} is not an item name", text); }
        if (found == null || !type.isInstance(found)) {
            DerivedLog.skip(owner, "sets {} to the object of {}, which is no derived item that built a {} there", path, text, type.getSimpleName());
            return DerivedConstants.MISSING;
        }
        return found;
    }

    @Nullable private static Object copy(Object value, ResourceLocation owner, String path, JsonObject returns) {
        Class<?> type = value.getClass();
        if (type.isArray() || value instanceof Collection || value instanceof Map) {
            DerivedLog.skip(owner, "reaches {} through an array or collection, but paths do not go through arrays", path);
            return null;
        }
        if (returns.size() == 0 && MaterialCopies.isMaterial(value)) { return MaterialCopies.copy(value, owner, path); }
        if (value instanceof Enum || type.getName().startsWith("java.")) {
            DerivedLog.skip(owner, "changes {}, a {}, which cannot be copied", path, type.getName());
            return null;
        }
        if (returns.size() != 0) {
            ResourceLocation key = new ResourceLocation(owner.getNamespace(), owner.getPath() + "_" + IdNames.javaSafe(path));
            List<ItemClassMaker.Override> overrides = ItemOverrides.plan(type, key, returns, value);
            if (!overrides.isEmpty()) {
                Class<?> made = ItemOverrides.makeStored(type, key, overrides);
                if (made == null) { return null; }
                type = made;
            }
        }
        return FieldCopy.copy(value, type);
    }

    private static JsonObject member(JsonObject spec, String name, ResourceLocation owner, String path) {
        if (!spec.has(name)) { return NONE; }
        if (!spec.get(name).isJsonObject()) {
            ContentLog.LOGGER.error("The {} of {} in derived item {} is not an object, so it is treated as empty", name, path, owner);
            return NONE;
        }
        return spec.getAsJsonObject(name);
    }

    private static String join(String path, String name) { return path.isEmpty() ? name : path + "." + name; }
}
