package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigSavedEvent;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FtbLibTeamRenamed {
    private FtbLibTeamRenamed() {}

    @SubscribeEvent public static void onSaved(ForgeTeamConfigSavedEvent event) {
        ForgeTeam team = event.getTeam();
        if (FtbLibTeams.ignored(team)) { return; }
        OwnerTable.relabelSource(FMLCommonHandler.instance().getMinecraftServerInstance(), OwnerRecord.SOURCE_FTB, team.getUIDCode(), team.getTitle().getUnformattedText());
    }
}
