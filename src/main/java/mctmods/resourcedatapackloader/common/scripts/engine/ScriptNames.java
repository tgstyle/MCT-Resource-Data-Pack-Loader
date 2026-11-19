package mctmods.resourcedatapackloader.common.scripts.engine;

import mctmods.resourcedatapackloader.core.rhino.MemberRemapper;
import mctmods.resourcedatapackloader.core.util.MemberNames;

public final class ScriptNames implements MemberRemapper {
    private static final ScriptNames INSTANCE = new ScriptNames();

    private ScriptNames() {
    }

    public static ScriptNames get() {
        return INSTANCE;
    }

    @Override
    public String[] readableNames(Class<?> owner, String runtimeName) {
        return MemberNames.readables(runtimeName);
    }
}
