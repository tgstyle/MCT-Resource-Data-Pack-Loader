package mctmods.resourcedatapackloader.content.util;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

public final class ItemUseGate {
    private ItemUseGate() {}

    public static void cancel(Event event, boolean denied) { if (denied) { event.setCanceled(true); } }

    public static void denyUse(PlayerInteractEvent.RightClickBlock event, boolean denied) { if (denied) { event.setUseItem(Event.Result.DENY); } }
}
