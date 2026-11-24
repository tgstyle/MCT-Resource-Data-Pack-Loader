package mctmods.resourcedatapackloader.core.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.function.Function;
import java.util.function.Supplier;

public class SideUtils {
    public static boolean isClient() { return FMLCommonHandler.instance().getSide() == Side.CLIENT; }

    public static boolean serverEnd(TickEvent.WorldTickEvent event) { return event.phase == TickEvent.Phase.END && !event.world.isRemote; }

    public static <T> T getForSide(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) { return pick(isClient(), client, server); }

    public static <T, R> R getForSide(T param, Supplier<Function<T, R>> client, Supplier<Function<T, R>> server) { return isClient() ? client.get().apply(param) : server.get().apply(param); }

    public static void runForClient(Supplier<Runnable> toRun) { if (isClient()) { toRun.get().run(); } }

    private static <T> T pick(boolean onClient, Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) { return onClient ? client.get().get() : server.get().get(); }
}
