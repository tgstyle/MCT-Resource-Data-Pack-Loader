package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.PriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ScriptScheduler {
    private static final int MAX_PENDING = 1000;
    private static final Object[] NO_ARGS = new Object[0];
    private static final PriorityQueue<Entry> QUEUE = new PriorityQueue<>((a, b) -> a.due != b.due ? Long.compare(a.due, b.due) : Long.compare(a.order, b.order));
    private static long clock;
    private static long counter;
    private static boolean dropped;

    private ScriptScheduler() {}

    public static void schedule(int ticks, Function fn, Scriptable scope, @Nullable EntityPlayer player) {
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

    @SubscribeEvent public static void onUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) { clear(); }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        clock++;
        while (!QUEUE.isEmpty() && QUEUE.peek().due <= clock) {
            Entry entry = QUEUE.poll();
            if (entry.player != null && !online(entry.player)) { continue; }
            ScriptEngine.call(entry.fn, entry.scope, NO_ARGS);
        }
    }

    private static boolean online(EntityPlayer player) {
        MinecraftServer server = player.getServer();
        return server != null && server.getPlayerList().getPlayerByUUID(player.getUniqueID()) == player;
    }

    private static final class Entry {
        private final long due;
        private final long order;
        private final Function fn;
        private final Scriptable scope;
        @Nullable private final EntityPlayer player;

        private Entry(long due, long order, Function fn, Scriptable scope, @Nullable EntityPlayer player) {
            this.player = player;
            this.due = due;
            this.order = order;
            this.fn = fn;
            this.scope = scope;
        }
    }
}
