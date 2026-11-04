package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;

import com.feed_the_beast.ftblib.events.team.ForgeTeamDeletedEvent;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FtbLibTeamDeleted {
    private FtbLibTeamDeleted() {}

    @SubscribeEvent public static void onDeleted(ForgeTeamDeletedEvent event) {
        ForgeTeam team = event.getTeam();
        if (OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !team.type.isPlayer) { return; }
        TeamDissolve.dissolveForeign(FMLCommonHandler.instance().getMinecraftServerInstance(), OwnerRecord.SOURCE_FTB, team.getUIDCode());
    }
}
