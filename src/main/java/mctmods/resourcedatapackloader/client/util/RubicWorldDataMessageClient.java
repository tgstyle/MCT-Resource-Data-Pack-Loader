package mctmods.resourcedatapackloader.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

public final class RubicWorldDataMessageClient {
    private RubicWorldDataMessageClient() {}

    public static void reloadRenderers(World world) { Minecraft.getMinecraft().renderGlobal.setWorldAndLoadRenderers((WorldClient) world); }
}
