package mctmods.resourcedatapackloader.common.util.world;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public final class SpawnScreen {
    public static final String TEXT = "Preparing spawn area";
    public static final String LOG = TEXT + ": {}%";

    private SpawnScreen() {}

    public static void tell(@Nullable MinecraftServer server, int percent) {
        if (server == null) { return; }
        server.currentTask = TEXT;
        server.percentDone = percent;
        server.setUserMessage("menu.generatingTerrain");
    }
}
