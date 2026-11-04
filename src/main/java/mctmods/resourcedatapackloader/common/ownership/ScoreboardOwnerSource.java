package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.entity.Standing;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    @Override public int teamOf(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        int handle = OwnerTable.handleForTeam(server, Standing.teamOf(player));
        return OwnerTable.get(server).isLive(handle) ? handle : 0;
    }
}
