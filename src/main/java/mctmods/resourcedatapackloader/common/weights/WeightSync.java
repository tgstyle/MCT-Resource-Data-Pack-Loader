package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Objects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class WeightSync {
    private static final int INTERVAL = 20;
    private static final int STEP = 100;
    private static final PlayerValueSync<Snapshot> SYNC = new PlayerValueSync<>(WeightSync::snapshot, ContentWeights::any, WeightSync::send);
    private static int ticks;

    private WeightSync() {}

    private static final class Snapshot {
        final int total;
        final int limit;
        final int tier;
        final boolean exempt;

        Snapshot(int total, int limit, int tier, boolean exempt) {
            this.total = total;
            this.limit = limit;
            this.tier = tier;
            this.exempt = exempt;
        }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Snapshot)) { return false; }
            Snapshot that = (Snapshot) other;
            return total / STEP == that.total / STEP && limit == that.limit && tier == that.tier && exempt == that.exempt;
        }

        @Override public int hashCode() { return Objects.hash(total / STEP, limit, tier, exempt); }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ContentWeights.any()) { return; }
        if (++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers());
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) { SYNC.fresh((EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.player); }

    private static Snapshot snapshot(EntityPlayerMP player) {
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        return new Snapshot(entry.total, entry.limit, entry.tier, !PlayerWeights.counted(player, ContentWeights.settings()));
    }

    private static void send(EntityPlayerMP player, Snapshot shown) {
        boolean sent = RDPLNetwork.sendWeight(player, shown.total, shown.limit, shown.tier, shown.exempt);
        ContentLog.LOGGER.debug("weights: {} {} of {} tier {}, {}", player.getName(), shown.total, shown.limit, shown.tier, sent ? "sent" : "not reachable");
    }
}
