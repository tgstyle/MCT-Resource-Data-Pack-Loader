package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.core.rhino.BaseFunction;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.NativeJavaClass;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.rhino.ScriptableObject;

final class ScriptJavaFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        String name = args.length > 0 ? Context.toString(args[0]) : "";
        Class<?> found;
        try {
            found = Class.forName(name, true, ScriptJavaFunction.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw Context.reportRuntimeError("The class " + name + " was not found");
        }
        return new NativeJavaClass(ScriptableObject.getTopLevelScope(scope), found);
    }
}
