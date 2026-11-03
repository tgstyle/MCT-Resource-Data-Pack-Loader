package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class WeightSync {
    private static final int INTERVAL = 20;
    private static final int STEP = 100;
    private static final PlayerValueSync<Snapshot> SYNC = new PlayerValueSync<>(WeightSync::snapshot, ContentWeights::any, WeightSync::send);
    private static int ticks;

    private WeightSync() {}

    private record Snapshot(int total, int limit, int tier, boolean exempt) {
        @Override public boolean equals(Object other) { return other instanceof Snapshot(int otherTotal, int otherLimit, int otherTier, boolean otherExempt) && total / STEP == otherTotal / STEP && limit == otherLimit && tier == otherTier && exempt == otherExempt; }

        @Override public int hashCode() { return Objects.hash(total / STEP, limit, tier, exempt); }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (!ContentWeights.any() || ++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(event.getServer().getPlayerList().getPlayers());
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { SYNC.fresh(player); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.getEntity()); }

    private static Snapshot snapshot(ServerPlayer player) {
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        return new Snapshot(entry.total, entry.limit, entry.tier, !PlayerWeights.counted(player, ContentWeights.settings()));
    }

    private static void send(ServerPlayer player, Snapshot shown) {
        boolean sent = RDPLNetwork.sendWeight(player, shown.total, shown.limit, shown.tier, shown.exempt);
        ContentLog.LOGGER.debug("weights: {} {} of {} tier {}, {}", player.getName().getString(), shown.total, shown.limit, shown.tier, sent ? "sent" : "not reachable");
    }
}
