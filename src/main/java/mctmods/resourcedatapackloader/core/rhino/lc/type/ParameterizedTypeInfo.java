package mctmods.resourcedatapackloader.core.rhino.lc.type;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mctmods.resourcedatapackloader.core.rhino.Collect;
import mctmods.resourcedatapackloader.core.rhino.FunctionObject;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;

/**
 * a {@link TypeInfo} implementation representing {@link ParameterizedType}
 *
 * @see ParameterizedType
 * @author ZZZank
 */
public interface ParameterizedTypeInfo extends TypeInfo {

    /**
     * @see ParameterizedType#getRawType()
     */
    TypeInfo rawType();

    /**
     * @see ParameterizedType#getActualTypeArguments()
     */
    List<TypeInfo> params();

    @Override
    default TypeInfo param(int index) {
        final List<TypeInfo> params = params();
        return index >= 0 && index < params.size() ? params.get(index) : TypeInfo.NONE;
    }

    /**
     * @see ParameterizedType#getOwnerType()
     */
    TypeInfo ownerType();

    /**
     * Extract consolidation mapping based on {@link #params()} and {@link
     * Class#getTypeParameters()}
     *
     * <p>Example: {@code type} is {@code List<String>}, calling {@code ((ParameterizedTypeInfo)
     * type).extractConsolidationMapping(TypeInfoFactory.GLOBAL) } will give {@code E -> String},
     * where the {@code E} the type variable declared by {@link List}
     */
    default Map<VariableTypeInfo, TypeInfo> extractConsolidationMapping(TypeInfoFactory factory) {
        java.lang.reflect.TypeVariable<? extends Class<?>>[] typeVariables = this.asClass().getTypeParameters();
        List<TypeInfo> actualParams = this.params();

        int len = typeVariables.length;
        if (len != actualParams.size()) {
            throw new IllegalStateException(
                    String.format(
                            "Expecting %s type params for class '%s', but got %s",
                            len, this.asClass().getName(), actualParams.size()));
        }

        Map<VariableTypeInfo, TypeInfo> ownerTypeMapping;
        TypeInfo ownerType = this.ownerType();
        if (ownerType instanceof ParameterizedTypeInfo) {
            ownerTypeMapping =
                    ((ParameterizedTypeInfo) ownerType).extractConsolidationMapping(factory);
        } else {
            ownerTypeMapping = Collect.mapOf();
        }

        if (len == 1 && ownerTypeMapping.isEmpty()) {
            return Collect.mapOf((VariableTypeInfo) factory.create(typeVariables[0]), actualParams.get(0));
        }

        HashMap<VariableTypeInfo, TypeInfo> mapping = new HashMap<VariableTypeInfo, TypeInfo>();
        for (int i = 0; i < len; i++) {
            mapping.put((VariableTypeInfo) factory.create(typeVariables[i]), actualParams.get(i));
        }
        mapping.putAll(ownerTypeMapping);
        return mapping;
    }

    @Override
    default boolean isInterface() {
        return rawType().isInterface();
    }

    @Override
    default boolean isFunctionalInterface() {
        return rawType().isFunctionalInterface();
    }

    @Override
    default boolean isAssignableFrom(TypeInfo another) {
        return rawType().isAssignableFrom(another);
    }

    @Override
    default boolean isInstance(Object o) {
        return rawType().isInstance(o);
    }

    /**
     * none of the base types is parameterized, unless this object is an implementation of {@link
     * Scriptable} with type variables, which is incredibly rare but still possible
     */
    @Override
    default int getTypeTag() {
        if (Scriptable.class.isAssignableFrom(asClass())) {
            return FunctionObject.JAVA_SCRIPTABLE_TYPE;
        }
        return FunctionObject.JAVA_UNSUPPORTED_TYPE;
    }
}
