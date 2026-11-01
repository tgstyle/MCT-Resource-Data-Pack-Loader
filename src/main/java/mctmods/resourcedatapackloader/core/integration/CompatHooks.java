package mctmods.resourcedatapackloader.core.integration;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraftforge.fml.common.Loader;

public final class CompatHooks {
    private CompatHooks() {}

    public static boolean present(String modId, String message) {
        if (!Loader.isModLoaded(modId)) { return false; }
        ContentLog.LOGGER.info(message);
        return true;
    }
}
