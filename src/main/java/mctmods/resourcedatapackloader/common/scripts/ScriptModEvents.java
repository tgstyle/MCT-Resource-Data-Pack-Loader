package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptEventDef;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ScriptModEvents {
    private static final Map<Class<?>, List<ScriptEventDef>> BY_CLASS = new ConcurrentHashMap<>();
    private static final Set<Class<?>> RUNNING = new HashSet<>();

    private ScriptModEvents() {}

    public static void clear() { BY_CLASS.clear(); }

    public static void accept(ResourceLocation owner, ScriptEventDef def) {
        String name = def.className;
        if (name == null || name.isEmpty()) {
            ContentLog.LOGGER.error("{}: a mod_event needs a class, ignoring it", owner);
            return;
        }
        Class<?> found;
        try { found = Class.forName(name, false, ScriptModEvents.class.getClassLoader()); }
        catch (ClassNotFoundException | LinkageError e) {
            ContentLog.LOGGER.error("{}: the class {} was not found, ignoring it", owner, name);
            return;
        }
        if (!Event.class.isAssignableFrom(found) || Modifier.isAbstract(found.getModifiers())) {
            ContentLog.LOGGER.error("{}: the class {} is not a Forge event, ignoring it", owner, name);
            return;
        }
        BY_CLASS.computeIfAbsent(found, key -> new ArrayList<>()).add(def);
    }

    public static void onAny(Event event) {
        if (BY_CLASS.isEmpty()) { return; }
        List<ScriptEventDef> entries = BY_CLASS.get(event.getClass());
        if (entries != null) { fire(entries, event); }
    }

    private static void fire(List<ScriptEventDef> entries, Event event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return; }
        if (!server.isSameThread()) {
            if (server.isRunning()) { server.submit(() -> fire(entries, event)).join(); }
            return;
        }
        if (!(levelOf(server, event) instanceof ServerLevel level) || !RUNNING.add(event.getClass())) { return; }
        try {
            Entity entity = event instanceof EntityEvent entityEvent ? entityEvent.getEntity() : null;
            Player player = entity instanceof Player who ? who : null;
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("rdpl", new RdplApi(level, player));
            bindings.put("event", event);
            bindings.put("player", player);
            long time = level.getGameTime();
            boolean cancelled = false;
            for (ScriptEventDef entry : entries) {
                if (entry.every != 1 && time % entry.every != 0) { continue; }
                if ("cancel".equals(ScriptEngine.run(entry.script, bindings, true))) { cancelled = true; }
            }
            if (cancelled && event.isCancelable()) { event.setCanceled(true); }
        }
        finally { RUNNING.remove(event.getClass()); }
    }

    private static LevelAccessor levelOf(MinecraftServer server, Event event) {
        if (event instanceof EntityEvent entityEvent) { return entityEvent.getEntity().level(); }
        if (event instanceof LevelEvent levelEvent) { return levelEvent.getLevel(); }
        return server.overworld();
    }
}
