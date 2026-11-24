package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.MemberNames;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class DerivedAdds {
    private static final List<Pending> PENDING = new ArrayList<>();

    private DerivedAdds() {}

    private record Pending(String target, Object object, ResourceLocation owner) {}

    static void queue(String target, Object object, ResourceLocation owner) { PENDING.add(new Pending(target, object, owner)); }

    public static void run() {
        for (Pending each : PENDING) {
            try { add(each); }
            catch (ReflectiveOperationException | RuntimeException | LinkageError e) { ContentLog.LOGGER.error("Derived item {} could not be added to {}: {}", each.owner, each.target, e.toString()); }
        }
        PENDING.clear();
    }

    private static void add(Pending each) throws ReflectiveOperationException {
        String target = each.target;
        boolean getter = target.endsWith("()");
        String named = getter ? target.substring(0, target.length() - 2) : target;
        int dot = named.lastIndexOf('.');
        if (dot <= 0 || dot == named.length() - 1) {
            ContentLog.LOGGER.error("Derived item {} adds itself to {}, which is not written <Class>.<member>, leaving it out", each.owner, target);
            return;
        }
        Class<?> type = Class.forName(named.substring(0, dot), true, DerivedAdds.class.getClassLoader());
        String member = named.substring(dot + 1);
        Object found = null;
        Type generic = null;
        boolean located = false;
        Field field = getter ? null : field(type, member);
        if (field != null) {
            field.setAccessible(true);
            found = field.get(null);
            generic = field.getGenericType();
            located = true;
        }
        Method method = located ? null : method(type, member);
        if (method != null) {
            method.setAccessible(true);
            found = method.invoke(null);
            generic = method.getGenericReturnType();
            located = true;
        }
        if (!located) {
            ContentLog.LOGGER.error("Derived item {} adds itself to {}, which is no static field or static getter without arguments, leaving it out", each.owner, target);
            return;
        }
        if (!(found instanceof Collection)) {
            ContentLog.LOGGER.error("Derived item {} adds itself to {}, which holds no collection, leaving it out", each.owner, target);
            return;
        }
        if (generic instanceof ParameterizedType) {
            Type element = ((ParameterizedType) generic).getActualTypeArguments()[0];
            if (element instanceof Class && !((Class<?>) element).isInstance(each.object)) {
                ContentLog.LOGGER.error("Derived item {} adds itself to {}, which holds {}, not {}, leaving it out", each.owner, target, ((Class<?>) element).getName(), each.object.getClass().getName());
                return;
            }
        }
        @SuppressWarnings("unchecked") Collection<Object> into = (Collection<Object>) found;
        if (!into.contains(each.object)) { into.add(each.object); }
    }

    @Nullable private static Field field(Class<?> type, String member) { return MemberNames.find(type, member, field -> Modifier.isStatic(field.getModifiers())); }

    @Nullable private static Method method(Class<?> type, String member) {
        for (Method method : MemberNames.methods(type)) {
            if (method.getParameterCount() == 0 && Modifier.isStatic(method.getModifiers()) && MemberNames.readable(method.getName()).equals(member)) { return method; }
        }
        return null;
    }
}
