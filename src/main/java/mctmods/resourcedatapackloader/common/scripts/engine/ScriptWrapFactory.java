package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.rhino.WrapFactory;
import mctmods.resourcedatapackloader.core.rhino.lc.type.TypeInfo;
import java.lang.reflect.AccessibleObject;

public final class ScriptWrapFactory extends WrapFactory {
    public ScriptWrapFactory() {
        setJavaPrimitiveWrap(false);
    }

    private static boolean forbidden(Object obj) {
        return obj instanceof AccessibleObject
                || obj instanceof ClassLoader
                || obj instanceof Class
                || obj instanceof Thread
                || obj instanceof Runtime
                || obj instanceof System;
    }

    private static void check(Object obj) {
        if (obj == null
                || obj instanceof String
                || obj instanceof Number
                || obj instanceof Boolean
                || obj instanceof Scriptable) {
            return;
        }
        if (forbidden(obj)) { throw Context.reportRuntimeError("Scripts may not use " + obj.getClass().getName()); }
    }

    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, TypeInfo staticType) {
        check(obj);
        return super.wrap(cx, scope, obj, staticType);
    }

    @Override
    public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
        check(obj);
        return super.wrapNewObject(cx, scope, obj);
    }

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, TypeInfo staticType) {
        check(javaObject);
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

    @Override
    public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
        throw Context.reportRuntimeError("Scripts may not use " + javaClass.getName());
    }
}
