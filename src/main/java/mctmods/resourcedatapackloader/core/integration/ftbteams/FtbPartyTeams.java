package mctmods.resourcedatapackloader.core.integration.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamRank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

public final class FtbPartyTeams {
    private FtbPartyTeams() {}

    private static boolean managerMissing() { return !FTBTeamsAPI.api().isManagerLoaded(); }

    public static String partyKeyOf(UUID player) {
        if (managerMissing()) { return ""; }
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(player).filter(Team::isPartyTeam).map(FtbPartyTeams::keyOf).orElse("");
    }

    public static String keyOf(Team team) { return team.getId().toString(); }

    public static boolean partyExists(String key) { return party(key).isPresent(); }

    public static String partyLabel(String key) { return party(key).map(team -> team.getName().getString()).orElse(key); }

    public static boolean live(String key) { return managerMissing() || partyExists(key); }

    public static boolean allies(UUID player, String key) {
        if (managerMissing()) { return false; }
        return party(key).map(team -> {
            TeamRank rank = team.getRankForPlayer(player);
            return rank == TeamRank.ALLY || rank.isMemberOrBetter();
        }).orElse(false);
    }

    public static List<String> memberNames(MinecraftServer server, String key) {
        if (managerMissing() || server.getProfileCache() == null) { return List.of(); }
        List<String> names = new ArrayList<>();
        for (UUID member : party(key).map(Team::getMembers).orElse(Set.of())) { server.getProfileCache().get(member).ifPresent(profile -> names.add(profile.getName())); }
        return names;
    }

    public static String keyNamed(String name) {
        if (managerMissing()) { return ""; }
        for (Team team : FTBTeamsAPI.api().getManager().getTeams()) {
            if (team.isPartyTeam() && (team.getShortName().equals(name) || keyOf(team).equals(name) || team.getName().getString().equals(name))) { return keyOf(team); }
        }
        return "";
    }

    public static List<String> partyNames() {
        if (managerMissing()) { return List.of(); }
        return FTBTeamsAPI.api().getManager().getTeams().stream().filter(Team::isPartyTeam).map(Team::getShortName).toList();
    }

    private static Optional<Team> party(String key) { return FTBTeamsAPI.api().getManager().getTeamByID(UUID.fromString(key)).filter(Team::isPartyTeam); }
}
