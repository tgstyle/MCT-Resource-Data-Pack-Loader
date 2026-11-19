package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ZoneScreens {
    private static final Map<UUID, Click> CLICKED = new HashMap<>();

    private ZoneScreens() {}

    public static void onClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (ZoneRules.none(level) || ZoneRules.noCloses()) { return; }
        BlockState state = level.getBlockState(event.getPos());
        if (ZoneGuard.holdsNothing(level, event.getPos(), state)) { return; }
        Click click = CLICKED.computeIfAbsent(event.getEntity().getUUID(), _ -> new Click());
        click.time = level.getGameTime();
        click.state = state;
        click.windowId = -1;
    }

    public static void onOpen(PlayerContainerEvent.Open event) {
        Click click = CLICKED.get(event.getEntity().getUUID());
        if (click != null && click.windowId < 0 && click.time == event.getEntity().level().getGameTime()) { click.windowId = event.getContainer().containerId; }
    }

    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ZoneRules.noCloses() || player.containerMenu == player.inventoryMenu || ZoneRules.none(player.level())) { return; }
        Click click = CLICKED.get(player.getUUID());
        if (click == null || click.windowId != player.containerMenu.containerId) { return; }
        BlockState state = click.state;
        ZoneDef zone = ZoneRules.covering(player, player.blockPosition(), held -> held.opens() != null && held.opens().close() && held.opens().refusesBlock(state));
        if (zone == null) { return; }
        player.closeContainer();
        ZoneGuard.tell(player, zone);
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { CLICKED.remove(event.getEntity().getUUID()); }

    private static final class Click {
        private long time;
        @Nullable private BlockState state;
        private int windowId = -1;
    }
}
