package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;

public final class DerivedConstants {
    public static final Object MISSING = new Object();
    private static final Map<String, Object[]> STORE = new HashMap<>();

    private DerivedConstants() {}

    public static void put(String owner, Object[] values) { STORE.put(owner, values); }

    public static Object value(String owner, int slot) {
        Object[] values = STORE.get(owner);
        if (values == null || slot < 0 || slot >= values.length) { return MISSING; }
        Object stored = values[slot];
        if (stored instanceof Lazy) {
            stored = ((Lazy) stored).resolve(owner);
            values[slot] = stored;
        }
        return stored instanceof ItemStack ? ((ItemStack) stored).copy() : stored;
    }

    public static void resolveAll() {
        for (Object[] values : STORE.values()) {
            for (int slot = 0; slot < values.length; slot++) {
                Object found = values[slot] instanceof Lazy ? ((Lazy) values[slot]).source.get() : null;
                if (found != null) { values[slot] = found; }
            }
        }
    }

    public static final class Lazy {
        private final String described;
        private final Supplier<Object> source;

        public Lazy(String described, Supplier<Object> source) {
            this.described = described;
            this.source = source;
        }

        Object resolve(String owner) {
            Object found = source.get();
            if (found != null) { return found; }
            ContentLog.LOGGER.error("Derived item {} returns {}, which is not registered, the base behavior stays", owner, described);
            return MISSING;
        }
    }
}
