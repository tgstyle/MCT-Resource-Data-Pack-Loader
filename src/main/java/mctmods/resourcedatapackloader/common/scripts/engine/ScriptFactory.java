package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.ContextFactory;
import mctmods.resourcedatapackloader.core.rhino.WrapFactory;

public class ScriptFactory extends ContextFactory {
    static final long TIME_LIMIT_MS = 50L;
    private static final long TIME_LIMIT_NANOS = TIME_LIMIT_MS * 1_000_000L;
    private static final int OBSERVER_THRESHOLD = 10000;
    private static final int MAX_STACK_DEPTH = 200;
    private static final WrapFactory WRAP_FACTORY = new ScriptWrapFactory();

    private static final class TimedContext extends Context {
        private long deadline;

        private TimedContext(ContextFactory factory) { super(factory); }
    }

    public static void arm(Context cx) { ((TimedContext) cx).deadline = System.nanoTime() + TIME_LIMIT_NANOS; }

    @Override
    protected Context makeContext() {
        TimedContext cx = new TimedContext(this);
        cx.setInterpretedMode(true);
        cx.setInstructionObserverThreshold(OBSERVER_THRESHOLD);
        cx.setMaximumInterpreterStackDepth(MAX_STACK_DEPTH);
        cx.setWrapFactory(WRAP_FACTORY);
        return cx;
    }

    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        if (System.nanoTime() > ((TimedContext) cx).deadline) { throw ScriptTimeout.INSTANCE; }
    }
}
