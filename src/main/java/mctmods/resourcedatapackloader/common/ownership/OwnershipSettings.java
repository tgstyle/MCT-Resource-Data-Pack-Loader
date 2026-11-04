package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.BlockSelection;
import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.block.Block;

public final class OwnershipSettings {
    public enum Who { TEAM, TEAMORPLAYER, PLAYER }

    public enum Dissolve { KEEP, RELEASE, REMOVE }

    public enum JoinTeam { KEEP, MOVE }

    public enum RemoveAs { DROP, VANISH }

    public enum TeamSource { SCOREBOARD, FTB }

    public enum FtbDeleted { FOLLOW, KEEP, RELEASE, REMOVE }

    private static final String GROUP = ContentControl.OWNERSHIP;
    private static volatile View view = build();

    private OwnershipSettings() {}

    private static final class View {
        private BlockSelection blocks;
        private Who who;
        private JoinTeam joinTeam;
        private Dissolve dissolve;
        private RemoveAs removeAs;
        private int perTick;
        private int checkTicks;
        private TeamSource teamSource;
        private FtbDeleted onFtbTeamDeleted;
    }

    public static void reload() { view = build(); }

    public static boolean any() { return view.blocks.any(); }

    public static boolean tags(Block block) { return view.blocks.matches(block); }

    public static Who who() { return view.who; }

    public static boolean joinKeep() { return view.joinTeam == JoinTeam.KEEP; }

    public static Dissolve dissolveMode() { return view.dissolve; }

    public static boolean removeVanish() { return view.removeAs == RemoveAs.VANISH; }

    public static int perTick() { return view.perTick; }

    public static int checkTicks() { return view.checkTicks; }

    public static TeamSource teamSource() { return view.teamSource; }

    public static Dissolve ftbDissolveMode(Dissolve follow) {
        switch (view.onFtbTeamDeleted) {
            case KEEP: return Dissolve.KEEP;
            case RELEASE: return Dissolve.RELEASE;
            case REMOVE: return Dissolve.REMOVE;
            default: return follow;
        }
    }

    public static Dissolve dissolveMode(String source) { return OwnerRecord.SOURCE_FTB.equals(source) ? ftbDissolveMode(dissolveMode()) : dissolveMode(); }

    public static boolean keepsEverything() { return dissolveMode() == Dissolve.KEEP && ftbDissolveMode(Dissolve.KEEP) == Dissolve.KEEP; }

    private static View build() {
        View built = new View();
        built.blocks = new BlockSelection("ownership", "tags", ContentControl.list(GROUP, "blocks", Config.ownership.blocks), ResourceDataPackLoader.id("ownership"));
        built.who = ContentControl.choice(GROUP, "who", Config.ownership.who, Who.TEAM);
        built.joinTeam = ContentControl.choice(GROUP, "onJoinTeam", Config.ownership.onJoinTeam, JoinTeam.KEEP);
        built.dissolve = ContentControl.choice(GROUP, "onDissolve", Config.ownership.onDissolve, Dissolve.KEEP);
        built.removeAs = ContentControl.choice(GROUP, "removeAs", Config.ownership.removeAs, RemoveAs.DROP);
        built.perTick = ContentControl.ranged(GROUP, "dissolvePerTick", Config.ownership.dissolvePerTick, 1, 100000);
        built.checkTicks = ContentControl.ranged(GROUP, "checkTicks", Config.ownership.checkTicks, 1, 1200);
        built.teamSource = ContentControl.choice(GROUP, "teamSource", "scoreboard", TeamSource.SCOREBOARD);
        built.onFtbTeamDeleted = ContentControl.choice(GROUP, "onFtbTeamDeleted", "follow", FtbDeleted.FOLLOW);
        return built;
    }
}
