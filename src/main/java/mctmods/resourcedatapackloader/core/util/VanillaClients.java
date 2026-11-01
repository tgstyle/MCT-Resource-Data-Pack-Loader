package mctmods.resourcedatapackloader.core.util;

import mctmods.resourcedatapackloader.core.config.Config;

public final class VanillaClients {
    private VanillaClients() {}

    public static void clientDrawn(Object owner, String key) {
        if (Config.content.vanillaClients) { ContentLog.LOGGER.warn("{} sets {}, which only clients with the mod draw; vanilla clients get the rest", owner, key); }
    }
}
