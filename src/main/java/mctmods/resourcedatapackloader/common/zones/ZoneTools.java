package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent;

public final class ZoneTools {
    private ZoneTools() {}

    public static void onTool(BlockEvent.BlockToolModificationEvent event) {
        Player player = event.getPlayer();
        ZoneTool tool = ZoneTool.of(event.getToolAction());
        if (event.isSimulated() || player == null || tool == null || ZoneRules.none(player.level())) { return; }
        ZoneDef zone = ZoneRules.covering(player, event.getPos(), held -> held.denyTools().contains(tool));
        if (zone != null && event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true) != null) {
            event.setCanceled(true);
            ZoneGuard.tell(player, zone);
        }
    }
}
