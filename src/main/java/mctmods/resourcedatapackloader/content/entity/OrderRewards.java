package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Rewards;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import java.util.ArrayList;
import java.util.List;

final class OrderRewards {
    private OrderRewards() {}

    static void give(World world, WorkOrder order) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null || !(world instanceof WorldServer)) { return; }
        OrderDef def = order.def;
        List<EntityPlayerMP> earners = earners(server, order);
        ContentLog.LOGGER.info("The order {} is filled, and {} player(s) are rewarded", def.name, earners.size());
        for (EntityPlayerMP player : earners) {
            ContentScoring.orderDone(player, def.points);
            Rewards.loot((WorldServer) player.world, player, def.loot);
            if (!def.wins.isEmpty()) { Functions.runAs(player, def.wins, "Order " + def.name); }
            CardEvents.orderDone(player, def.sign);
        }
    }

    private static List<EntityPlayerMP> earners(MinecraftServer server, WorkOrder order) {
        List<EntityPlayerMP> earners = new ArrayList<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (order.team.isEmpty()) {
                if (player.getUniqueID().equals(order.owner)) { earners.add(player); }
                continue;
            }
            ScorePlayerTeam side = ContentOrders.teamOf(player);
            if (side != null && side.getName().equals(order.team)) { earners.add(player); }
        }
        return earners;
    }
}
