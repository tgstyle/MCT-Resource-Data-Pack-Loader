package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.entity.Standing;

import net.minecraft.entity.player.EntityPlayer;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    @Override public int teamOf(EntityPlayer player) {
        OwnerTable table = OwnerTable.get(player.getServer());
        if (table == null) { return 0; }
        int handle = OwnerTable.handleForTeam(player.getServer(), Standing.teamOf(player));
        return table.isLive(handle) ? handle : 0;
    }
}
