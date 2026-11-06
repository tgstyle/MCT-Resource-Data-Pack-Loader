package mctmods.resourcedatapackloader.common.entities.behavior;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class RankSteal {
    private RankSteal() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote || event.getEntityPlayer().isSneaking() || event.getUseBlock() == Event.Result.DENY) { return; }
        if (event.getWorld().getTileEntity(event.getPos()) instanceof IInventory) { OpenedContainers.mark(event.getWorld(), event.getPos()); }
    }
}
