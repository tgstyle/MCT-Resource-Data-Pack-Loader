package mctmods.resourcedatapackloader.common.entities.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class RankSteal {
    private RankSteal() {}

    public static void onOpen(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel server) || event.getEntity().isShiftKeyDown() || event.getUseBlock() == TriState.FALSE) { return; }
        if (server.getBlockEntity(event.getPos()) instanceof Container) { OpenedContainers.mark(server, event.getPos()); }
    }
}
