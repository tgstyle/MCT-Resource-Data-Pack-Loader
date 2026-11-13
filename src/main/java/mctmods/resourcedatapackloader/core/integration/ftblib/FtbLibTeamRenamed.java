package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;

import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigSavedEvent;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FtbLibTeamRenamed {
    private FtbLibTeamRenamed() {}

    @SubscribeEvent public static void onSaved(ForgeTeamConfigSavedEvent event) {
        ForgeTeam team = event.getTeam();
        if (OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !team.type.isPlayer) { return; }
        OwnerTable.relabelSource(FMLCommonHandler.instance().getMinecraftServerInstance(), OwnerRecord.SOURCE_FTB, team.getUIDCode(), team.getTitle().getUnformattedText());
    }
}
