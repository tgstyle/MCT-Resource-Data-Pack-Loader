package mctmods.resourcedatapackloader.core.integration.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import java.util.Optional;
import java.util.UUID;

final class FtbPartyTeams {
    private FtbPartyTeams() {}

    private static boolean managerMissing() { return !FTBTeamsAPI.api().isManagerLoaded(); }

    static String partyKeyOf(UUID player) {
        if (managerMissing()) { return ""; }
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(player).filter(Team::isPartyTeam).map(FtbPartyTeams::keyOf).orElse("");
    }

    static boolean partyExists(String key) { return party(key).isPresent(); }

    static String partyLabel(String key) { return party(key).map(team -> team.getName().getString()).orElse(key); }

    static boolean live(String key) { return managerMissing() || partyExists(key); }

    static String keyOf(Team team) { return team.getId().toString(); }

    private static Optional<Team> party(String key) { return FTBTeamsAPI.api().getManager().getTeamByID(UUID.fromString(key)).filter(Team::isPartyTeam); }
}
