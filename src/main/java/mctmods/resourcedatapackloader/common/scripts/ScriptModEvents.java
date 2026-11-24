package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptEventDef;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        BY_CLASS.computeIfAbsent(found, key -> new ArrayList<>()).add(def);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onAny(Event event) {
        if (BY_CLASS.isEmpty()) { return; }
        List<ScriptEventDef> entries = BY_CLASS.get(event.getClass());
        if (entries != null) { fire(entries, event); }
    }

    private static void fire(List<ScriptEventDef> entries, Event event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || !server.isCallingFromMinecraftThread()) { return; }
        World world = worldOf(server, event);
        if (!(world instanceof WorldServer) || !RUNNING.add(event.getClass())) { return; }
        try {
            Entity entity = event instanceof EntityEvent ? ((EntityEvent) event).getEntity() : null;
            EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
            Map<String, Object> bindings = ScriptEvents.bind((WorldServer) world, player);
            bindings.put("event", event);
            bindings.put(ScriptEvents.PLAYER, player);
            long time = world.getTotalWorldTime();
            boolean cancelled = false;
            for (ScriptEventDef entry : entries) {
                if (!entry.due(time)) { continue; }
                if (ScriptEngine.CANCEL.equals(ScriptEngine.run(entry.script, bindings, true))) { cancelled = true; }
            }
            if (cancelled && event.isCancelable()) { event.setCanceled(true); }
        } finally { RUNNING.remove(event.getClass()); }
    }

    private static World worldOf(MinecraftServer server, Event event) {
        if (event instanceof EntityEvent) { return ((EntityEvent) event).getEntity().world; }
        if (event instanceof WorldEvent) { return ((WorldEvent) event).getWorld(); }
        return server.getWorld(0);
    }
}
