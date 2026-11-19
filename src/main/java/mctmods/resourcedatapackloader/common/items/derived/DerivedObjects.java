package mctmods.resourcedatapackloader.common.items.derived;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class DerivedObjects {
    private static final Map<String, Object> MADE = new HashMap<>();

    private DerivedObjects() {}

    public static void put(Identifier owner, String path, Object made) { MADE.put(owner + "#" + path, made); }

    @Nullable public static Object get(Identifier item, String path) { return MADE.get(item + "#" + path); }
}
