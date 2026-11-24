package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.rhino.Scriptable;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.PriorityQueue;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ScriptScheduler {
    private static final int MAX_PENDING = 1000;
    private static final Object[] NO_ARGS = new Object[0];
    private static final PriorityQueue<Entry> QUEUE = new PriorityQueue<>((a, b) -> a.due != b.due ? Long.compare(a.due, b.due) : Long.compare(a.order, b.order));
    private static long clock;
    private static long counter;
    private static boolean dropped;

    private ScriptScheduler() {}

    public static void schedule(int ticks, Function fn, Scriptable scope, MinecraftServer server, @Nullable UUID player) {
        if (QUEUE.size() >= MAX_PENDING) {
            if (!dropped) {
                dropped = true;
                ContentLog.LOGGER.error("Script scheduler: too many scheduled script functions, dropped");
            }
            return;
        }
        QUEUE.add(new Entry(clock + Math.max(1, ticks), counter++, fn, scope, server, player));
    }

    public static void clear() {
        QUEUE.clear();
        dropped = false;
    }

    @SubscribeEvent public static void onTick(ServerTickEvent.Post event) {
        clock++;
        while (!QUEUE.isEmpty() && QUEUE.peek().due <= clock) {
            Entry entry = QUEUE.poll();
            if (entry.player != null && entry.server.getPlayerList().getPlayer(entry.player) == null) { continue; }
            ScriptEngine.call(entry.fn, entry.scope, NO_ARGS);
        }
    }

    @SubscribeEvent public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) { clear(); }
    }

    private record Entry(long due, long order, Function fn, Scriptable scope, MinecraftServer server, @Nullable UUID player) {}
}
