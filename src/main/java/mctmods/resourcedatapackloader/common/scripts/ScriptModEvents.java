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
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class ScriptModEvents {
    private static final Map<Class<?>, List<ScriptEventDef>> BY_CLASS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Consumer<?>> LISTENERS = new ConcurrentHashMap<>();
    private static final Set<Class<?>> RUNNING = new HashSet<>();

    private ScriptModEvents() {}

    public static void clear() {
        BY_CLASS.clear();
        for (Consumer<?> listener : LISTENERS.values()) { NeoForge.EVENT_BUS.unregister(listener); }
        LISTENERS.clear();
    }

    public static void accept(ResourceLocation owner, ScriptEventDef def) {
        String name = def.className;
        if (name == null || name.isEmpty()) {
            ContentLog.LOGGER.error("{}: a mod_event needs a class, ignoring it", owner);
            return;
        }
        Class<?> found;
        try {
            found = Class.forName(name, false, ScriptModEvents.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            ContentLog.LOGGER.error("{}: the class {} was not found, ignoring it", owner, name);
            return;
        }
        if (!Event.class.isAssignableFrom(found) || Modifier.isAbstract(found.getModifiers())) {
            ContentLog.LOGGER.error("{}: the class {} is not a Forge event, ignoring it", owner, name);
            return;
        }
        if (unheard(found.asSubclass(Event.class))) {
            ContentLog.LOGGER.error("{}: the class {} is not a Forge event, ignoring it", owner, name);
            return;
        }
        BY_CLASS.computeIfAbsent(found, key -> new ArrayList<>()).add(def);
    }

    private static <T extends Event> boolean unheard(Class<T> type) {
        if (LISTENERS.containsKey(type)) { return false; }
        Consumer<T> listener = event -> onAny(type, event);
        try {
            NeoForge.EVENT_BUS.addListener(EventPriority.LOW, false, type, listener);
        } catch (IllegalArgumentException e) {
            return true;
        }
        LISTENERS.put(type, listener);
        return false;
    }

    private static void onAny(Class<?> type, Event event) {
        if (event.getClass() != type) { return; }
        List<ScriptEventDef> entries = BY_CLASS.get(type);
        if (entries != null) { fire(entries, event); }
    }

    private static void fire(List<ScriptEventDef> entries, Event event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isSameThread()) { return; }
        if (!(levelOf(server, event) instanceof ServerLevel level) || !RUNNING.add(event.getClass())) { return; }
        try {
            Entity entity = event instanceof EntityEvent entityEvent ? entityEvent.getEntity() : null;
            Player player = entity instanceof Player found ? found : null;
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
            if (cancelled && event instanceof ICancellableEvent cancellable) { cancellable.setCanceled(true); }
        } finally { RUNNING.remove(event.getClass()); }
    }

    private static LevelAccessor levelOf(MinecraftServer server, Event event) {
        if (event instanceof EntityEvent entityEvent) { return entityEvent.getEntity().level(); }
        if (event instanceof LevelEvent levelEvent) { return levelEvent.getLevel(); }
        return server.overworld();
    }
}
