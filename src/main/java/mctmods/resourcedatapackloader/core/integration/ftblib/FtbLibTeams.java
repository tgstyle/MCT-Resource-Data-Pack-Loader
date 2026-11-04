package mctmods.resourcedatapackloader.core.integration.ftblib;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import java.util.UUID;

public final class FtbLibTeams {
    private FtbLibTeams() {}

    public static String partyKeyOf(UUID id) {
        if (!Universe.loaded()) { return ""; }
        ForgePlayer player = Universe.get().getPlayer(id);
        return player != null && player.hasTeam() && player.team.type.isPlayer ? player.team.getUIDCode() : "";
    }

    public static boolean partyExists(String key) {
        ForgeTeam team = Universe.get().getTeam((short) Integer.parseInt(key, 16));
        return team.type.isPlayer && team.isValid();
    }

    public static String partyLabel(String key) { return Universe.get().getTeam((short) Integer.parseInt(key, 16)).getTitle().getUnformattedText(); }

    public static boolean live(String key) { return !Universe.loaded() || partyExists(key); }
}
