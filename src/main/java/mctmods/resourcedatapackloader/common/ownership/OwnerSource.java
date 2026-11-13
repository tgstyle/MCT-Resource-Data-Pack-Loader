package mctmods.resourcedatapackloader.common.ownership;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public interface OwnerSource {
    int teamOf(EntityPlayer player);

    Collection<String> memberNames(@Nullable MinecraftServer server, int handle);

    default boolean allies(EntityPlayer player, int handle) { return false; }

    default int byName(@Nullable MinecraftServer server, String name) { return 0; }

    default Collection<String> names(@Nullable MinecraftServer server) { return Collections.emptyList(); }
}
