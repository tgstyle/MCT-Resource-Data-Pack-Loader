package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Rewards;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import java.util.ArrayList;
import java.util.List;

final class OrderRewards {
    private OrderRewards() {}

    static void give(ServerLevel level, WorkOrder order) {
        OrderDef def = order.def;
        List<ServerPlayer> earners = earners(level, order);
        ContentLog.LOGGER.info("The order {} is filled, and {} player(s) are rewarded", def.name(), earners.size());
        for (ServerPlayer player : earners) {
            ContentScoring.orderDone(player, def.points());
            Rewards.loot(player.serverLevel(), player, def.loot(), "Order " + def.name());
            if (!def.wins().isEmpty()) { Functions.runAs(player, def.wins(), "Order " + def.name()); }
            CardEvents.orderDone(player, def.sign());
        }
    }

    private static List<ServerPlayer> earners(ServerLevel level, WorkOrder order) {
        List<ServerPlayer> earners = new ArrayList<>();
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (order.team.isEmpty()) {
                if (player.getUUID().equals(order.owner)) { earners.add(player); }
                continue;
            }
            PlayerTeam side = ContentOrders.teamOf(player);
            if (side != null && side.getName().equals(order.team)) { earners.add(player); }
        }
        return earners;
    }
}
