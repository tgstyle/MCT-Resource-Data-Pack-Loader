package mctmods.resourcedatapackloader.common.ownership;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface OwnerSource {
    int teamOf(ServerPlayer player);

    default boolean allies(ServerPlayer player, int handle) { return false; }

    List<String> memberNames(MinecraftServer server, int handle);

    default int byName(MinecraftServer server, String name) { return 0; }

    default List<String> names(MinecraftServer server) { return List.of(); }
}
