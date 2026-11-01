package mctmods.resourcedatapackloader.common.entities.orders;

import mctmods.resourcedatapackloader.common.cards.CardEvents;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.OrderDef;
import mctmods.resourcedatapackloader.common.scoring.ContentScoring;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.loot.Rewards;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

final class OrderRewards {
    private OrderRewards() {}

    static void give(ServerLevel level, WorkOrder order) {
        OrderDef def = order.def;
        List<ServerPlayer> earners = earners(level, order);
        ContentLog.LOGGER.info("The order {} is filled, and {} player(s) are rewarded", def.name(), earners.size());
        for (ServerPlayer player : earners) {
            ContentScoring.orderDone(player, def.points());
            Rewards.loot(player.level(), player, def.loot(), "Order " + def.name());
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
