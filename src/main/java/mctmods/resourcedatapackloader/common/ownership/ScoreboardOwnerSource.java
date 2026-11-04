package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.entity.Standing;

import net.minecraft.server.level.ServerPlayer;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    @Override public int teamOf(ServerPlayer player) {
        int handle = OwnerTable.handleForTeam(player.server, Standing.teamOf(player));
        return OwnerTable.get(player.server).isLive(handle) ? handle : 0;
    }
}
