package mctmods.resourcedatapackloader.client.util;

import net.minecraft.client.Minecraft;

public final class ClientResets {
    private ClientResets() {}

    public static void onDisconnect(Runnable reset) { Minecraft.getMinecraft().addScheduledTask(reset); }
}
