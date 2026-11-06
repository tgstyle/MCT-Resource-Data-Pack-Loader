package mctmods.resourcedatapackloader.common.entities.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

public final class RankSteal {
    private RankSteal() {}

    public static void onOpen(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel server) || event.getEntity().isShiftKeyDown() || event.getUseBlock() == Event.Result.DENY) { return; }
        if (server.getBlockEntity(event.getPos()) instanceof Container) { OpenedContainers.mark(server, event.getPos()); }
    }
}
