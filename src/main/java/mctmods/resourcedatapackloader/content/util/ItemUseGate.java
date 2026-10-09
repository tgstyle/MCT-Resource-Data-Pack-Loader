package mctmods.resourcedatapackloader.content.util;

import net.minecraft.util.TriState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ItemUseGate {
    private ItemUseGate() {}

    public static void cancel(ICancellableEvent event, boolean denied) { if (denied) { event.setCanceled(true); } }

    public static void denyUse(PlayerInteractEvent.RightClickBlock event, boolean denied) { if (denied) { event.setUseItem(TriState.FALSE); } }
}
