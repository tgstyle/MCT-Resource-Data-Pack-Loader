package mctmods.resourcedatapackloader.core.integration;

import mctmods.resourcedatapackloader.common.rubic.Rubic;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.mixin.integration.forge.IASMEventHandler;
import mctmods.resourcedatapackloader.mixin.integration.forge.IEventBus;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;

public class CompatHandler {
    public static final int VANILLA_HEIGHT = 256;
    private static final Set<String> FAKE_CHUNK_LOAD = ImmutableSet.of("zerocore", "reborncore");
    private static final Map<String, String> packageToModId = getPackageToModId();
    private static IEventListener[] fakeChunkLoadListeners;

    private static Map<String, String> getPackageToModId() { return Collections.unmodifiableMap(Loader.instance().getActiveModList().stream().flatMap(mod -> mod.getOwnedPackages().stream().map(pkg -> new AbstractMap.SimpleEntry<>(pkg, mod.getModId()))).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> a.equals(b) ? a : a + " or " + b))); }

    private static String getPackageName(Class<?> clazz) {
        String name = clazz.getName();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(0, dot);
    }

    public static Set<String> getModsForStacktrace(StackTraceElement[] stacktrace) {
        Set<String> mods = new HashSet<>();
        for (StackTraceElement traceElement : stacktrace) {
            String modid = modForClassName(traceElement.getClassName());
            if (modid != null && !modid.equals(IdNames.VANILLA) && !modid.equals("forge") && !modid.equals(Rubic.MODID)) { mods.add(modid); }
        }
        return mods;
    }

    public static String getModForStacktraceElement(StackTraceElement traceElement) {
        String modid = modForClassName(traceElement.getClassName());
        return modid != null ? modid : "(no mod)";
    }

    @Nullable private static String modForClassName(String className) {
        try { return packageToModId.get(getPackageName(Class.forName(className))); }
        catch (ClassNotFoundException ignored) { return null; }
    }

    public static void fakeVanillaHeight(World world) { ((IRubicWorldInternal.IServer) world).rdpl$fakeWorldHeight(VANILLA_HEIGHT); }

    public static void restoreHeight(World world) { ((IRubicWorldInternal.IServer) world).rdpl$fakeWorldHeight(0); }

    public static boolean postWithFakeWorldHeight(EventBus bus, World world, Event event) {
        if (!(bus instanceof IEventBus) || !GenHeights.rubic(world)) { return bus.post(event); }
        return postEvent(bus, world, event, CompatHandler::fakeVanillaHeight, CompatHandler::restoreHeight);
    }

    public static void onCubeLoad(ChunkEvent.Load load) {
        if (fakeChunkLoadListeners == null) {
            IEventListener[] found = getFakeEventListeners(load.getListenerList());
            fakeChunkLoadListeners = found == null ? new IEventListener[0] : found;
        }
        if (fakeChunkLoadListeners.length == 0) { return; }
        onChunkLoadImpl(load);
    }

    private static void onChunkLoadImpl(ChunkEvent.Load load) {
        IEventBus bus = (IEventBus) MinecraftForge.EVENT_BUS;
        if (bus.isShutdown()) { return; }
        dispatch(bus, MinecraftForge.EVENT_BUS, load, fakeChunkLoadListeners, () -> {}, () -> {});
    }

    private static boolean dispatch(IEventBus forgeEventBus, EventBus bus, Event event, IEventListener[] listeners, Runnable before, Runnable after) {
        int index = 0;
        try {
            for (; index < listeners.length; index++) {
                try {
                    IEventListener listener = listeners[index];
                    before.run();
                    listener.invoke(event);
                }
                finally { after.run(); }
            }
        }
        catch (Throwable throwable) {
            forgeEventBus.getExceptionHandler().handleException(bus, event, listeners, index, throwable);
            Throwables.throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
        return event.isCancelable() && event.isCanceled();
    }

    private static <T> boolean postEvent(EventBus bus, T ctx, Event event, Consumer<T> preEvt, Consumer<T> postEvt) {
        IEventBus forgeEventBus = (IEventBus) bus;
        if (forgeEventBus.isShutdown()) { return false; }
        IEventListener[] listeners = event.getListenerList().getListeners(forgeEventBus.getBusID());
        return dispatch(forgeEventBus, bus, event, listeners, () -> preEvt.accept(ctx), () -> postEvt.accept(ctx));
    }

    private static IEventListener[] getFakeEventListeners(ListenerList listenerList) {
        if (!(MinecraftForge.EVENT_BUS instanceof IEventBus)) {
            ContentLog.LOGGER.error("Failed to initialize CompatHandler! No event bus mixin!");
            return null;
        }
        IEventBus forgeEventBus = (IEventBus) MinecraftForge.EVENT_BUS;
        IEventListener[] listeners = listenerList.getListeners(forgeEventBus.getBusID());
        List<IEventListener> newList = new ArrayList<>();
        int index = 0;
        for (; index < listeners.length; index++) {
            IEventListener listener = listeners[index];
            if (listener instanceof IASMEventHandler) {
                IASMEventHandler handler = (IASMEventHandler) listener;
                String modid = handler.getOwner().getModId();
                if (modid.equals("forge")) {
                    String desc = handler.toString();
                    if (desc.startsWith("ASM: ") && desc.contains("@")) {
                        String modClass = desc.split("@")[0].substring("ASM: ".length());
                        try {
                            Class<?> cl = Class.forName(modClass);
                            String newModid = cl.getPackage() == null ? null : packageToModId.get(cl.getPackage().getName());
                            if (newModid != null) { modid = newModid; }
                        }
                        catch (ClassNotFoundException ignored) {}
                    }
                }
                if (FAKE_CHUNK_LOAD.contains(modid)) { newList.add(listener); }
            }
        }
        return newList.toArray(new IEventListener[0]);
    }
}
