package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

final class DerivedLog {
    private DerivedLog() {}

    static void skip(Object owner, String what, Object... args) {
        Object[] all = new Object[args.length + 1];
        all[0] = owner;
        System.arraycopy(args, 0, all, 1, args.length);
        ContentLog.LOGGER.error("Derived item {} " + what + ", leaving it out", all);
    }
}
