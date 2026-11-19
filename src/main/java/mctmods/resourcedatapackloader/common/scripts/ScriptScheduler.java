package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.PriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ScriptScheduler {
    private static final int MAX_PENDING = 1000;
    private static final Object[] NO_ARGS = new Object[0];
    private static final PriorityQueue<Entry> QUEUE = new PriorityQueue<>((a, b) -> a.due() != b.due() ? Long.compare(a.due(), b.due()) : Long.compare(a.order(), b.order()));
    private static long clock;
    private static long counter;
    private static boolean dropped;

    private ScriptScheduler() {}

    public static void schedule(int ticks, Function fn, Scriptable scope, @Nullable Player player) {
        if (QUEUE.size() >= MAX_PENDING) {
            if (!dropped) {
                dropped = true;
                ContentLog.LOGGER.error("Script scheduler: too many scheduled script functions, dropped");
            }
            return;
        }
        QUEUE.add(new Entry(clock + Math.max(1, ticks), counter++, fn, scope, player));
    }

    public static void clear() {
        QUEUE.clear();
        dropped = false;
    }

    @SubscribeEvent public static void onStopping(ServerStoppingEvent event) { clear(); }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        clock++;
        while (!QUEUE.isEmpty() && QUEUE.peek().due() <= clock) {
            Entry entry = QUEUE.poll();
            if (entry.player() instanceof ServerPlayer who && !who.server.getPlayerList().getPlayers().contains(who)) { continue; }
            ScriptEngine.call(entry.fn(), entry.scope(), NO_ARGS);
        }
    }

    private record Entry(long due, long order, Function fn, Scriptable scope, @Nullable Player player) {}
}
