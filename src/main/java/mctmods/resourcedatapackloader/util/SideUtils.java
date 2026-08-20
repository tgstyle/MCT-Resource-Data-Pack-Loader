package mctmods.resourcedatapackloader.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.function.Function;
import java.util.function.Supplier;

public class SideUtils {
    public static <T> T getForSide(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
        if (FMLCommonHandler.instance().getSide().isClient()) { return client.get().get(); }
        else { return server.get().get(); }
    }

    public static <T, R> R getForSide(T param, Supplier<Function<T, R>> client, Supplier<Function<T, R>> server) {
        if (FMLCommonHandler.instance().getSide().isClient()) { return client.get().apply(param); }
        else { return server.get().apply(param); }
    }

    public static void runForClient(Supplier<Runnable> toRun) {
        if (FMLCommonHandler.instance().getSide().isClient()) { toRun.get().run(); }
    }
}
