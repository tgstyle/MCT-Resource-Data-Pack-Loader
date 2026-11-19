package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ZoneScreens {
    private static final Map<UUID, Click> CLICKED = new HashMap<>();

    private ZoneScreens() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (ZoneRules.none(level) || ZoneRules.noCloses()) { return; }
        BlockState state = level.getBlockState(event.getPos());
        if (ZoneGuard.holdsNothing(level, event.getPos(), state)) { return; }
        Click click = CLICKED.computeIfAbsent(event.getEntity().getUUID(), id -> new Click());
        click.time = level.getGameTime();
        click.state = state;
        click.containerId = -1;
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        Click click = CLICKED.get(player.getUUID());
        if (click != null && click.containerId < 0 && click.time == player.level().getGameTime()) { click.containerId = event.getContainer().containerId; }
    }

    @SubscribeEvent public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || ZoneRules.noCloses()) { return; }
        if (player.containerMenu == player.inventoryMenu || ZoneRules.none(player.level())) { return; }
        Click click = CLICKED.get(player.getUUID());
        if (click == null || click.containerId != player.containerMenu.containerId) { return; }
        BlockState state = click.state;
        ZoneDef zone = ZoneRules.covering(player, player.blockPosition(), held -> held.opens() != null && held.opens().close && held.opens().refuses(state));
        if (zone == null) { return; }
        player.closeContainer();
        ZoneGuard.tell(player, zone);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { CLICKED.remove(event.getEntity().getUUID()); }

    private static final class Click {
        private long time;
        @Nullable private BlockState state;
        private int containerId = -1;
    }
}
