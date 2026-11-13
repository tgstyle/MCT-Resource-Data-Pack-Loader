package mctmods.resourcedatapackloader.core.integration.ftblib;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FtbLibTeams {
    private FtbLibTeams() {}

    public static String partyKeyOf(UUID id) {
        if (!Universe.loaded()) { return ""; }
        ForgePlayer player = Universe.get().getPlayer(id);
        return player != null && player.hasTeam() && player.team.type.isPlayer ? player.team.getUIDCode() : "";
    }

    private static ForgeTeam party(String key) { return Universe.get().getTeam((short) Integer.parseInt(key, 16)); }

    public static boolean partyExists(String key) {
        ForgeTeam team = party(key);
        return team.type.isPlayer && team.isValid();
    }

    public static String partyLabel(String key) { return party(key).getTitle().getUnformattedText(); }

    public static boolean live(String key) { return !Universe.loaded() || partyExists(key); }

    public static boolean isAlly(UUID id, String key) {
        if (!Universe.loaded() || !partyExists(key)) { return false; }
        ForgePlayer player = Universe.get().getPlayer(id);
        return player != null && party(key).isAlly(player);
    }

    public static List<String> memberNames(String key) {
        List<String> names = new ArrayList<>();
        if (!Universe.loaded() || !partyExists(key)) { return names; }
        for (ForgePlayer member : party(key).getMembers()) { names.add(member.getName()); }
        return names;
    }

    public static List<String> partyIds() {
        List<String> ids = new ArrayList<>();
        if (!Universe.loaded()) { return ids; }
        for (ForgeTeam team : Universe.get().getTeams()) {
            if (team.type.isPlayer && team.isValid()) { ids.add(team.getId()); }
        }
        return ids;
    }

    public static String keyNamed(String name) {
        if (!Universe.loaded()) { return ""; }
        for (ForgeTeam team : Universe.get().getTeams()) {
            if (team.type.isPlayer && team.isValid() && (team.getId().equalsIgnoreCase(name) || team.getUIDCode().equalsIgnoreCase(name) || team.getTitle().getUnformattedText().equals(name))) { return team.getUIDCode(); }
        }
        return "";
    }
}
