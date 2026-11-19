package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class ZoneTools {
    private ZoneTools() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onTool(BlockEvent.BlockToolModificationEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.isSimulated() || ZoneRules.none(event.getLevel())) { return; }
        ZoneTool tool = tool(event.getItemAbility());
        if (tool == null) { return; }
        ZoneDef zone = ZoneRules.covering(player, event.getPos(), held -> held.denyTools().contains(tool));
        if (zone != null && event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getItemAbility(), true) != null) {
            event.setCanceled(true);
            ZoneGuard.tell(player, zone);
        }
    }

    @Nullable private static ZoneTool tool(ItemAbility ability) {
        if (ability == ItemAbilities.HOE_TILL) { return ZoneTool.HOE_TILL; }
        if (ability == ItemAbilities.AXE_STRIP) { return ZoneTool.AXE_STRIP; }
        return ability == ItemAbilities.SHOVEL_FLATTEN ? ZoneTool.SHOVEL_FLATTEN : null;
    }
}
