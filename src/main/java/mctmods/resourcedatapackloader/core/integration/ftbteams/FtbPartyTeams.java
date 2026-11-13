package mctmods.resourcedatapackloader.core.integration.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import dev.ftb.mods.ftbteams.api.TeamRank;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class FtbPartyTeams {
    private FtbPartyTeams() {}

    private static boolean managerMissing() { return !FTBTeamsAPI.api().isManagerLoaded(); }

    public static String partyKeyOf(UUID id) {
        if (managerMissing()) { return ""; }
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(id).filter(Team::isPartyTeam).map(FtbPartyTeams::keyOf).orElse("");
    }

    public static boolean partyExists(String key) { return party(key).isPresent(); }

    public static String partyLabel(String key) { return party(key).map(team -> team.getName().getString()).orElse(key); }

    public static boolean live(String key) { return managerMissing() || partyExists(key); }

    public static String keyOf(Team team) { return team.getId().toString(); }

    public static boolean allyOf(String key, UUID id) { return !managerMissing() && party(key).map(team -> team.getRankForPlayer(id) == TeamRank.ALLY).orElse(false); }

    public static Set<UUID> memberIds(String key) { return managerMissing() ? Set.of() : party(key).map(Team::getMembers).orElse(Set.of()); }

    public static String keyNamed(String name) {
        if (managerMissing()) { return ""; }
        TeamManager manager = FTBTeamsAPI.api().getManager();
        Optional<Team> found = manager.getTeamByName(name).filter(Team::isPartyTeam);
        if (found.isEmpty()) {
            found = manager.getTeams().stream().filter(team -> team.isPartyTeam() && (team.getName().getString().equalsIgnoreCase(name) || keyOf(team).equalsIgnoreCase(name))).findFirst();
        }
        return found.map(FtbPartyTeams::keyOf).orElse("");
    }

    public static List<String> shortNames() {
        if (managerMissing()) { return List.of(); }
        return FTBTeamsAPI.api().getManager().getTeams().stream().filter(Team::isPartyTeam).map(Team::getShortName).toList();
    }

    private static Optional<Team> party(String key) { return FTBTeamsAPI.api().getManager().getTeamByID(UUID.fromString(key)).filter(Team::isPartyTeam); }
}
