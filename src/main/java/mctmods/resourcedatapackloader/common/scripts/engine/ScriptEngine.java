package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.rhino.ClassCache;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.rhino.RhinoException;
import mctmods.resourcedatapackloader.core.rhino.Script;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.rhino.ScriptableObject;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptEngine {
    public static final String CANCEL = "cancel";
    private static final ScriptFactory FACTORY = new ScriptFactory();
    private static final Map<String, Script> COMPILED = new ConcurrentHashMap<>();
    private static final Set<String> FAILED = ConcurrentHashMap.newKeySet();
    private static volatile ScriptableObject sharedScope;

    private ScriptEngine() {
    }

    public static void reload() {
        COMPILED.clear();
        FAILED.clear();
        try (Context cx = FACTORY.enterContext()) {
            sharedScope = cx.initSafeStandardObjects(null, true);
            ClassCache.get(sharedScope).setMemberRemapper(ScriptNames.get());
        }
    }

    @Nullable
    private static Script compile(ScriptRef ref) {
        String key = ref.key();
        if (FAILED.contains(key)) {
            return null;
        }
        Script cached = COMPILED.get(key);
        if (cached != null) {
            return cached;
        }
        String sourceName = sourceName(ref);
        String source = ref.inline;
        if (source == null) {
            source = PackManager.get().read(ref.namespace, ref.path.substring("scripts/".length(), ref.path.length() - ".js".length()), PackManager.SCRIPTS, "js");
            if (source == null) {
                ContentLog.LOGGER.error("Script {} not found", sourceName);
                FAILED.add(key);
                return null;
            }
        }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            Script script = cx.compileString(source, sourceName, 1, null);
            COMPILED.put(key, script);
            return script;
        } catch (RhinoException e) {
            ContentLog.LOGGER.error("Script {} did not compile: {}", sourceName, e.getMessage());
            FAILED.add(key);
            return null;
        }
    }

    public static String sourceName(ScriptRef ref) {
        return ref.path != null ? ref.namespace + ":" + ref.path : ref.key();
    }

    public static void prepare() {
        if (sharedScope == null) { reload(); }
    }

    @Nullable
    public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings) {
        return run(ref, bindings, false);
    }

    @Nullable
    public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError) {
        return execute(ref, bindings, disableOnError, false).verdict;
    }

    @Nullable
    public static String run(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError, boolean classAccess) {
        return execute(ref, bindings, disableOnError, classAccess).verdict;
    }

    public static boolean failed(@Nullable ScriptRef ref, Map<String, Object> bindings) {
        return execute(ref, bindings, false, false).failed;
    }

    private static final class Outcome {
        @Nullable private final String verdict;
        private final boolean failed;

        private Outcome(@Nullable String verdict, boolean failed) {
            this.verdict = verdict;
            this.failed = failed;
        }
    }

    private static Outcome execute(@Nullable ScriptRef ref, Map<String, Object> bindings, boolean disableOnError, boolean classAccess) {
        if (ref == null) {
            return new Outcome(null, false);
        }
        Script script = compile(ref);
        ScriptableObject shared = sharedScope;
        if (script == null || shared == null) {
            return new Outcome(null, true);
        }
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
        } catch (RhinoException e) {
            logFailure(sourceName(ref), e);
            turnOff(ref, disableOnError);
            return new Outcome(null, true);
        } catch (ScriptTimeout e) {
            ContentLog.LOGGER.error("Script {} took longer than {} ms, stopped", sourceName(ref), ScriptFactory.TIME_LIMIT_MS);
            turnOff(ref, disableOnError);
            return new Outcome(null, true);
        }
    }

    private static void turnOff(ScriptRef ref, boolean disableOnError) {
        if (!disableOnError) { return; }
        FAILED.add(ref.key());
        ContentLog.LOGGER.error("Script {} is turned off until the next reload", sourceName(ref));
    }

    @Nullable
    public static String call(@Nullable Function fn, Scriptable scope, Object[] args) {
        if (fn == null) {
            return null;
        }
        try (Context cx = FACTORY.enterContext()) {
            ScriptFactory.arm(cx);
            return verdict(fn.call(cx, scope, scope, args));
        } catch (RhinoException e) {
            logFailure("scheduled function", e);
            return null;
        } catch (ScriptTimeout e) {
            ContentLog.LOGGER.error("Script scheduled function took longer than {} ms, stopped", ScriptFactory.TIME_LIMIT_MS);
            return null;
        }
    }

    @Nullable
    private static String verdict(Object result) {
        return CANCEL.equals(Context.toString(result)) ? CANCEL : null;
    }

    private static void logFailure(String sourceName, RhinoException e) {
        ContentLog.LOGGER.error("Script {} failed: {}\n{}", sourceName, e.getMessage(), e.getScriptStackTrace());
    }
}
