package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class FtbOwnerSource implements OwnerSource {
    @Override public int teamOf(ServerPlayer player) {
        String key = FtbPartyTeams.partyKeyOf(player.getUUID());
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(player.level().getServer(), OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }

    @Override public boolean allies(ServerPlayer player, int handle) {
        String key = keyOf(player.level().getServer(), handle);
        return !key.isEmpty() && FtbPartyTeams.allyOf(key, player.getUUID());
    }

    @Override public List<String> memberNames(int handle) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        String key = server == null ? "" : keyOf(server, handle);
        if (key.isEmpty()) { return ScoreboardOwnerSource.INSTANCE.memberNames(handle); }
        List<String> names = new ArrayList<>();
        for (UUID id : FtbPartyTeams.memberIds(key)) { names.add(server.services().nameToIdCache().get(id).map(NameAndId::name).orElse(id.toString())); }
        return names;
    }

    @Override @Nullable public Integer byName(MinecraftServer server, String name) {
        String key = FtbPartyTeams.keyNamed(name);
        if (key.isEmpty()) { return ScoreboardOwnerSource.INSTANCE.byName(server, name); }
        return OwnerTable.handleForSource(server, OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }

    @Override public List<String> names(MinecraftServer server) {
        List<String> names = new ArrayList<>(FtbPartyTeams.shortNames());
        names.addAll(ScoreboardOwnerSource.INSTANCE.names(server));
        return names;
    }

    private static String keyOf(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        int live = table.resolve(handle);
        return OwnerRecord.SOURCE_FTB.equals(table.sourceOf(live)) ? table.keyOf(live) : "";
    }
}
