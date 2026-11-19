package mctmods.resourcedatapackloader.common.scripts.engine;

final class ScriptTimeout extends Error {
    private static final long serialVersionUID = 1L;
    static final ScriptTimeout INSTANCE = new ScriptTimeout();

    private ScriptTimeout() {
        super("Script ran past its time limit", null, false, false);
    }
}
