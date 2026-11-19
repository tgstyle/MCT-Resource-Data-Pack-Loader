package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ZoneScreens {
    private static final Map<UUID, Click> CLICKED = new HashMap<>();

    private ZoneScreens() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onClick(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (ZoneRules.none(world) || ZoneRules.noCloses()) { return; }
        IBlockState state = world.getBlockState(event.getPos());
        if (ZoneGuard.holdsNothing(world, event.getPos(), state.getBlock())) { return; }
        Click click = CLICKED.computeIfAbsent(event.getEntityPlayer().getUniqueID(), id -> new Click());
        click.time = world.getTotalWorldTime();
        click.state = state;
        click.windowId = -1;
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        Click click = CLICKED.get(player.getUniqueID());
        if (click != null && click.windowId < 0 && click.time == player.world.getTotalWorldTime()) { click.windowId = event.getContainer().windowId; }
    }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP) || ZoneRules.noCloses()) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.openContainer == player.inventoryContainer || ZoneRules.none(player.world)) { return; }
        Click click = CLICKED.get(player.getUniqueID());
        if (click == null || click.windowId != player.openContainer.windowId) { return; }
        IBlockState state = click.state;
        ZoneDef zone = ZoneRules.covering(player, player.getPosition(), held -> held.opens != null && held.opens.close && held.opens.refusesBlock(state));
        if (zone == null) { return; }
        player.closeScreen();
        ZoneGuard.tell(player, zone);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { CLICKED.remove(event.player.getUniqueID()); }

    private static final class Click {
        private long time;
        @Nullable private IBlockState state;
        private int windowId = -1;
    }
}
