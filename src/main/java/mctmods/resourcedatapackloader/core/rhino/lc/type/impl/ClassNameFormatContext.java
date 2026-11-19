package mctmods.resourcedatapackloader.core.rhino.lc.type.impl;

import mctmods.resourcedatapackloader.core.rhino.lc.type.ParameterizedTypeInfo;
import mctmods.resourcedatapackloader.core.rhino.lc.type.TypeFormatContext;
import mctmods.resourcedatapackloader.core.rhino.lc.type.TypeInfo;
import mctmods.resourcedatapackloader.core.rhino.lc.type.VariableTypeInfo;

/**
 * @author ZZZank
 */
public class ClassNameFormatContext implements TypeFormatContext {
    @Override
    public String getClassName(Class<?> c) {
        return c.getName();
    }

    @Override
    public void append(StringBuilder builder, TypeInfo type) {
        builder.append(type.asClass().getName());
    }

    @Override
    public void append(StringBuilder builder, TypeInfo type, boolean declaring) {
        builder.append(type.asClass().getName());
    }

    @Override
    public void appendParameterized(StringBuilder builder, ParameterizedTypeInfo type) {
        append(builder, type.rawType());
    }

    @Override
    public void appendVariable(StringBuilder builder, VariableTypeInfo type, boolean declaring) {
        append(builder, type.mainBound());
    }

    @Override
    public void appendArray(StringBuilder builder, TypeInfo type) {
        builder.append(type.asClass().getName());
    }

    @Override
    public String getFormattedNone() {
        return Object.class.getName();
    }
}
