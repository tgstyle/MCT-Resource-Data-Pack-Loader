package mctmods.resourcedatapackloader.core.rhino.lc.type.impl;

import java.util.List;
import mctmods.resourcedatapackloader.core.rhino.Collect;

public class EnumTypeInfo extends ClassTypeInfo {

    public static String getName(Object e) {
        return ((Enum<?>) e).name();
    }

    private List<Object> constants;

    public EnumTypeInfo(Class<?> type) {
        super(type);
    }

    @Override
    public boolean isEnum() {
        return true;
    }

    @Override
    public List<Object> enumConstants() {
        if (constants == null) {
            constants = Collect.listOf(asClass().getEnumConstants());
        }

        return constants;
    }
}
