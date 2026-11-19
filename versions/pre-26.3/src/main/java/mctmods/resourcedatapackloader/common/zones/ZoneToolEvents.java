package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class ZoneToolEvents {
    private ZoneToolEvents() {}

    public static void register() { NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ZoneToolEvents::onTool); }

    private static void onTool(BlockEvent.BlockToolModificationEvent event) {
        if (event.isSimulated() || !(event.getPlayer() instanceof ServerPlayer player)) { return; }
        ZoneTool tool = of(event.getItemAbility());
        ZoneDef zone = tool == null ? null : ZoneTools.denying(player, event.getPos(), tool);
        if (zone == null || event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getItemAbility(), true) == null) { return; }
        event.setCanceled(true);
        ZoneGuard.tell(player, zone);
    }

    @Nullable private static ZoneTool of(ItemAbility ability) {
        if (ability == ItemAbilities.HOE_TILL) { return ZoneTool.HOE_TILL; }
        if (ability == ItemAbilities.AXE_STRIP) { return ZoneTool.AXE_STRIP; }
        return ability == ItemAbilities.SHOVEL_FLATTEN ? ZoneTool.SHOVEL_FLATTEN : null;
    }
}
