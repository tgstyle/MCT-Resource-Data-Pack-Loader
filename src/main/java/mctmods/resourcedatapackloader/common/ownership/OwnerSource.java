package mctmods.resourcedatapackloader.common.ownership;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface OwnerSource {
    int teamOf(ServerPlayer player);

    default boolean allies(ServerPlayer player, int handle) { return false; }

    List<String> memberNames(int handle);

    @Nullable Integer byName(MinecraftServer server, String name);

    List<String> names(MinecraftServer server);
}
