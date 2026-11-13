package mctmods.resourcedatapackloader.core.integration.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

final class FtbPartyTeams {
    private FtbPartyTeams() {}

    private static boolean managerMissing() { return !FTBTeamsAPI.api().isManagerLoaded(); }

    static String partyKeyOf(UUID player) {
        if (managerMissing()) { return ""; }
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(player).filter(Team::isPartyTeam).map(FtbPartyTeams::keyOf).orElse("");
    }

    static boolean partyExists(String key) { return party(key).isPresent(); }

    static boolean allies(UUID player, String key) { return !managerMissing() && party(key).map(team -> team.getRankForPlayer(player).isAllyOrBetter()).orElse(false); }

    static List<String> memberNames(MinecraftServer server, String key) {
        List<String> names = new ArrayList<>();
        if (managerMissing() || server.getProfileCache() == null) { return names; }
        party(key).ifPresent(team -> {
            for (UUID member : team.getMembers()) { server.getProfileCache().get(member).ifPresent(profile -> names.add(profile.getName())); }
        });
        return names;
    }

    static Optional<Team> partyNamed(String name) {
        if (managerMissing()) { return Optional.empty(); }
        for (Team team : FTBTeamsAPI.api().getManager().getTeams()) {
            if (team.isPartyTeam() && (team.getName().getString().equalsIgnoreCase(name) || keyOf(team).equalsIgnoreCase(name))) { return Optional.of(team); }
        }
        return Optional.empty();
    }

    static List<String> partyNames() {
        List<String> names = new ArrayList<>();
        if (managerMissing()) { return names; }
        for (Team team : FTBTeamsAPI.api().getManager().getTeams()) {
            if (!team.isPartyTeam()) { continue; }
            String label = team.getName().getString();
            names.add(label.matches("[A-Za-z0-9_.+-]+") ? label : keyOf(team));
        }
        return names;
    }

    static String partyLabel(String key) { return party(key).map(team -> team.getName().getString()).orElse(key); }

    static boolean live(String key) { return managerMissing() || partyExists(key); }

    static String keyOf(Team team) { return team.getId().toString(); }

    private static Optional<Team> party(String key) { return FTBTeamsAPI.api().getManager().getTeamByID(UUID.fromString(key)).filter(Team::isPartyTeam); }
}
