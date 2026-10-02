package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.mixin.RDPLMixinPlugin;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import java.lang.reflect.Method;

public final class CubicChunksWorlds {
    private static final String WORLD = "io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld";
    private static Method asks;
    private static boolean looked;

    private CubicChunksWorlds() {}

    public static boolean cubic(World world) {
        if (!RDPLMixinPlugin.cubicChunksPresent()) { return false; }
        Method ask = ask();
        if (ask == null || !ask.getDeclaringClass().isInstance(world)) { return false; }
        try { return (Boolean) ask.invoke(world); }
        catch (ReflectiveOperationException failed) { return false; }
    }

    private static Method ask() {
        if (!looked) {
            looked = true;
            try { asks = Class.forName(WORLD).getMethod("isCubicWorld"); }
            catch (ReflectiveOperationException | LinkageError failed) { ContentLog.LOGGER.error("CubicChunks is installed but its worlds cannot be asked whether they are cubic, so each is treated as a plain one", failed); }
        }
        return asks;
    }
}
