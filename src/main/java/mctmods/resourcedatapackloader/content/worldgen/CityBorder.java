package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class CityBorder {
    private static volatile Held held;

    private record Held(MinecraftServer server, double minX, double minZ, double maxX, double maxZ) {
        boolean on(MinecraftServer other) { return server == other; }

        boolean same(MinecraftServer other, WorldBorder live) { return server == other && minX == live.getMinX() && minZ == live.getMinZ() && maxX == live.getMaxX() && maxZ == live.getMaxZ(); }
    }

    private CityBorder() {}

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        WorldBorder live = server.overworld().getWorldBorder();
        Held known = held;
        if (known != null && known.same(server, live)) { return; }
        held = new Held(server, live.getMinX(), live.getMinZ(), live.getMaxX(), live.getMaxZ());
    }

    static boolean live() {
        Held known = held;
        return known != null && known.on(ServerLifecycleHooks.getCurrentServer());
    }

    static boolean beyond(int x, int z) {
        Held known = held;
        return known != null && (x < known.minX() || x >= known.maxX() || z < known.minZ() || z >= known.maxZ());
    }
}
