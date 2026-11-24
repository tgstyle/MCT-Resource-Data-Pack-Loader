package mctmods.resourcedatapackloader.common.util;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ServerTicks {
    private ServerTicks() {}

    @Nullable public static MinecraftServer every(int interval) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server == null || server.getTickCounter() % interval != 0 ? null : server;
    }
}
