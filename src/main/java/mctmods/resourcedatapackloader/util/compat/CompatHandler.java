package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IASMEventHandler;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEventBus;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;

public class CompatHandler {
    private static final int VANILLA_HEIGHT = 256;
    private static final Set<String> FAKE_CHUNK_LOAD = ImmutableSet.of("zerocore");
    private static final Map<String, String> packageToModId = getPackageToModId();
    private static IEventListener[] fakeChunkLoadListeners;

    private static Map<String, String> getPackageToModId() {
        return Collections.unmodifiableMap(Loader.instance().getActiveModList().stream()
                .flatMap(mod -> mod.getOwnedPackages().stream().map(pkg -> new AbstractMap.SimpleEntry<>(pkg, mod.getModId())))
                .collect(Collectors
                        .toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> a.equals(b) ? a : a + " or " + b)));
    }

    private static String getPackageName(Class<?> clazz) {
        String name = clazz.getName();
        int dot = name.lastIndexOf('.');
        return dot < 0
                ? ""
                : name.substring(0, dot);
    }

    public static Set<String> getModsForStacktrace(StackTraceElement[] stacktrace) {
        Set<String> mods = new HashSet<>();
        for (StackTraceElement traceElement : stacktrace) {
            try {
                Class<?> cl = Class.forName(traceElement.getClassName());
                String modid = packageToModId.get(getPackageName(cl));
                if (modid != null && !modid.equals("minecraft") && !modid.equals("forge") && !modid.equals("resourcedatapackloader")) { mods.add(modid); }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return mods;
    }

    public static String getModForStacktraceElement(StackTraceElement traceElement) {
        try {
            Class<?> cl = Class.forName(traceElement.getClassName());
            String modid = packageToModId.get(getPackageName(cl));
            if (modid != null) { return modid; }
        } catch (ClassNotFoundException ignored) {
        }
        return "(no mod)";
    }

    public static void beforePopulate(World world) { ((IRubicWorldInternal.Server) world).rdpl$fakeWorldHeight(VANILLA_HEIGHT); }

    public static void afterPopulate(World world) { ((IRubicWorldInternal.Server) world).rdpl$fakeWorldHeight(0); }

    public static void beforeGenerate(World world) { ((IRubicWorldInternal.Server) world).rdpl$fakeWorldHeight(VANILLA_HEIGHT); }

    public static void afterGenerate(World world) { ((IRubicWorldInternal.Server) world).rdpl$fakeWorldHeight(0); }

    public static void postChunkPopulatePreWithFakeWorldHeight(PopulateChunkEvent.Pre event) {
        if (!(MinecraftForge.EVENT_BUS instanceof IEventBus)) { MinecraftForge.EVENT_BUS.post(event); }
        postEventPerModFakeHeight(event.getWorld(), event);
    }

    public static void postBiomeDecorateWithFakeWorldHeight(DecorateBiomeEvent.Decorate event) {
        if (!(MinecraftForge.EVENT_BUS instanceof IEventBus)) { MinecraftForge.EVENT_BUS.post(event); }
        postEventPerModFakeHeight(event.getWorld(), event);
    }

    public static boolean postBiomePostDecorateWithFakeWorldHeight(DecorateBiomeEvent.Post event) {
        if (!(MinecraftForge.EVENT_BUS instanceof IEventBus)) { MinecraftForge.EVENT_BUS.post(event); }
        return postEventPerModFakeHeight(event.getWorld(), event);
    }

    private static boolean postEventPerModFakeHeight(World world, Event event) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return MinecraftForge.EVENT_BUS.post(event); }
        return postEvent((IRubicWorldInternal.Server) world, event, w -> w.rdpl$fakeWorldHeight(VANILLA_HEIGHT), w -> w.rdpl$fakeWorldHeight(0));
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
        int i = -1;
        try {
            for (i = 0; i < fakeChunkLoadListeners.length; i++) {
                IEventListener fakeChunkLoadListener = fakeChunkLoadListeners[i];
                fakeChunkLoadListener.invoke(load);
            }
        } catch (Throwable throwable) {
            bus.getExceptionHandler().handleException(MinecraftForge.EVENT_BUS, load, fakeChunkLoadListeners, i, throwable);
            Throwables.throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
    }

    private static <T> boolean postEvent(T ctx, Event event, Consumer<T> preEvt, Consumer<T> postEvt) {
        IEventBus forgeEventBus = (IEventBus) MinecraftForge.EVENT_BUS;
        if (forgeEventBus.isShutdown()) { return false; }
        IEventListener[] listeners = event.getListenerList().getListeners(forgeEventBus.getBusID());
        int index = 0;
        try {
            for (; index < listeners.length; index++) {
                try {
                    IEventListener listener = listeners[index];
                    preEvt.accept(ctx);
                    listener.invoke(event);
                } finally {
                    postEvt.accept(ctx);
                }
            }
        } catch (Throwable throwable) {
            forgeEventBus.getExceptionHandler().handleException(MinecraftForge.EVENT_BUS, event, listeners, index, throwable);
            Throwables.throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
        return event.isCancelable() && event.isCanceled();
    }

    private static IEventListener[] getFakeEventListeners(ListenerList listenerList) {
        if (!(MinecraftForge.EVENT_BUS instanceof IEventBus)) {
            Rubic.LOGGER.error("Failed to initialize CompatHandler! No event bus mixin!");
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
                            String newModid = cl.getPackage() == null ? null : getPackageToModId().get(cl.getPackage().getName());
                            if (newModid != null) { modid = newModid; }
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
                if (FAKE_CHUNK_LOAD.contains(modid)) { newList.add(listener); }
            }
        }
        return newList.toArray(new IEventListener[0]);
    }
}
