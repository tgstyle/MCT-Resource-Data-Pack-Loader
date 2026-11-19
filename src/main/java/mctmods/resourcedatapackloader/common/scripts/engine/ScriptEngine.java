package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.rhino.NativeJSON;
import mctmods.resourcedatapackloader.core.rhino.RhinoException;
import mctmods.resourcedatapackloader.core.rhino.Script;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.rhino.ScriptableObject;
import mctmods.resourcedatapackloader.core.rhino.Undefined;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ScriptEngine {
    private static final ScriptFactory FACTORY = new ScriptFactory();
    private static final Map<String, Script> COMPILED = new ConcurrentHashMap<>();
    private static final Set<String> FAILED = ConcurrentHashMap.newKeySet();
    private static volatile ScriptableObject sharedScope;

    private ScriptEngine() {}

    public static void reload() {
        COMPILED.clear();
        FAILED.clear();
        try (Context cx = FACTORY.enterContext()) {
            sharedScope = cx.initSafeStandardObjects(null, true);
        }
    }

    @Nullable private static String read(ScriptRef ref) {
        byte[] bytes = PackManager.get().bytes(PackManager.CONTENT, ref.namespace, ref.path);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    @Nullable private static Script compile(ScriptRef ref) {
        String key = ref.key();
        if (FAILED.contains(key)) { return null; }
        Script cached = COMPILED.get(key);
        if (cached != null) { return cached; }
        String sourceName = sourceName(ref);
        String source = ref.inline;
        if (source == null) {
            source = read(ref);
            if (source == null) {
                FAILED.add(key);
                ContentLog.LOGGER.error("Script {} not found", sourceName);
                return null;
            }
        }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            Script script = cx.compileString(source, sourceName, 1, null);
            COMPILED.put(key, script);
            return script;
        }
        catch (RhinoException e) {
            FAILED.add(key);
            ContentLog.LOGGER.error("Script {} did not compile: {}", sourceName, e.getMessage());
            return null;
        }
    }

    public static String sourceName(ScriptRef ref) { return ref.path != null ? ref.namespace + ":" + ref.path : ref.key(); }

    public static void prepare() {
        if (sharedScope == null) { reload(); }
    }

    @Nullable public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings) { return run(ref, bindings, false); }

    @Nullable public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError) { return execute(ref, bindings, disableOnError, false).verdict; }

    @Nullable public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError, boolean classAccess) { return execute(ref, bindings, disableOnError, classAccess).verdict; }

    public static boolean failed(@Nullable ScriptRef ref, Map<String, Object> bindings) { return execute(ref, bindings, false, false).failed; }

    private record Outcome(@Nullable String verdict, boolean failed) {}

    private static Outcome execute(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError, boolean classAccess) {
        if (ref == null) { return new Outcome(null, false); }
        Script script = compile(ref);
        ScriptableObject shared = sharedScope;
        if (script == null || shared == null) { return new Outcome(null, true); }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            Scriptable scope = cx.newObject(shared);
            scope.setPrototype(shared);
            scope.setParentScope(null);
            for (Map.Entry<String, Object> binding : bindings.entrySet()) {
                ScriptableObject.putProperty(scope, binding.getKey(), Context.javaToJS(binding.getValue(), scope));
            }
            if (classAccess) { ScriptableObject.putProperty(scope, "java", new ScriptJavaFunction()); }
            return new Outcome(verdict(script.exec(cx, scope, scope)), false);
        }
        catch (RhinoException e) {
            logFailure(sourceName(ref), e);
            turnOff(ref, disableOnError);
            return new Outcome(null, true);
        }
        catch (ScriptTimeout e) {
            ContentLog.LOGGER.error("Script {} took longer than 50 ms, stopped", sourceName(ref));
            turnOff(ref, disableOnError);
            return new Outcome(null, true);
        }
    }

    public record Called(@Nullable String text, @Nullable String failure) {}

    public static Called callWith(ScriptRef ref, String argument) {
        Script script = compile(ref);
        ScriptableObject shared = sharedScope;
        if (script == null || shared == null) { return new Called(null, "it did not compile or was not found"); }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            Scriptable scope = cx.newObject(shared);
            scope.setPrototype(shared);
            scope.setParentScope(null);
            if (!(script.exec(cx, scope, scope) instanceof Function fn)) { return new Called(null, "it does not evaluate to a function"); }
            return new Called(text(cx, scope, fn.call(cx, scope, scope, new Object[] {argument})), null);
        }
        catch (RhinoException e) { FAILED.add(ref.key()); return new Called(null, e.getMessage()); }
        catch (ScriptTimeout e) { FAILED.add(ref.key()); return new Called(null, "it took longer than 50 ms"); }
    }

    @Nullable private static String text(Context cx, Scriptable scope, @Nullable Object result) {
        if (result == null || Undefined.isUndefined(result)) { return null; }
        if (result instanceof Scriptable && !(result instanceof Function)) { return Context.toString(NativeJSON.stringify(cx, scope, result, null, null)); }
        return Context.toString(result);
    }

    private static void turnOff(ScriptRef ref, boolean disableOnError) {
        if (!disableOnError) { return; }
        FAILED.add(ref.key());
        ContentLog.LOGGER.error("Script {} is turned off until the next reload", sourceName(ref));
    }

    @Nullable public static String call(@Nullable Function fn, Scriptable scope, Object[] args) {
        if (fn == null) { return null; }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            return verdict(fn.call(cx, scope, scope, args));
        }
        catch (RhinoException e) {
            logFailure("scheduled function", e);
            return null;
        }
        catch (ScriptTimeout e) {
            ContentLog.LOGGER.error("Script scheduled function took longer than 50 ms, stopped");
            return null;
        }
    }

    @Nullable private static String verdict(Object result) { return "cancel".equals(Context.toString(result)) ? "cancel" : null; }

    private static void logFailure(String sourceName, RhinoException e) { ContentLog.LOGGER.error("Script {} failed: {}\n{}", sourceName, e.getMessage(), e.getScriptStackTrace()); }
}
