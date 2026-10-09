package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.util.ContentLog;

public final class MCADeadApi {
    private static boolean said;

    private MCADeadApi() {}

    public static void said() {
        if (said) { return; }
        said = true;
        ContentLog.LOGGER.info("MCA phones minecraftcomesalive.com for its update check, supporters list and crash-report upload. That site no longer exists, so every call would hang for the full connection timeout, about 20 seconds each. They are skipped instead, which is also where roughly 40 seconds of every game load was going");
    }
}
