package mctmods.resourcedatapackloader.common.scripts.engine;

import java.io.Serial;

final class ScriptTimeout extends Error {
    @Serial private static final long serialVersionUID = 1L;
    static final ScriptTimeout INSTANCE = new ScriptTimeout();

    private ScriptTimeout() { super("Script ran past its time limit", null, false, false); }
}
