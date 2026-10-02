package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.mixin.RDPLMixinPlugin;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

public final class CubicChunksWorlds {
    private static final String WORLD = "io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld";
    private static final String PROVIDER = "io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer";
    private static final String COMPATIBILITY = "io.github.opencubicchunks.cubicchunks.core.worldgen.generator.vanilla.VanillaCompatibilityGenerator";
    private static Method asks;
    private static boolean looked;
    private static Method cubes;
    private static Field vanilla;
    private static boolean sought;

    private CubicChunksWorlds() {}

    public static boolean cubic(World world) {
        if (!RDPLMixinPlugin.cubicChunksPresent()) { return false; }
        Method ask = ask();
        if (ask == null || !ask.getDeclaringClass().isInstance(world)) { return false; }
        try { return (Boolean) ask.invoke(world); }
        catch (ReflectiveOperationException failed) { return false; }
    }

    @Nullable public static IChunkGenerator maker(World world) {
        if (!cubic(world)) { return null; }
        seek();
        Object provider = world.getChunkProvider();
        if (cubes == null || vanilla == null || !cubes.getDeclaringClass().isInstance(provider)) { return null; }
        try {
            Object made = cubes.invoke(provider);
            return vanilla.getDeclaringClass().isInstance(made) ? (IChunkGenerator) vanilla.get(made) : null;
        }
        catch (ReflectiveOperationException failed) { return null; }
    }

    private static void seek() {
        if (sought) { return; }
        sought = true;
        try {
            cubes = Class.forName(PROVIDER).getMethod("getCubeGenerator");
            vanilla = Class.forName(COMPATIBILITY).getDeclaredField("vanilla");
            vanilla.setAccessible(true);
        }
        catch (ReflectiveOperationException | LinkageError | RuntimeException failed) {
            cubes = null;
            vanilla = null;
            ContentLog.LOGGER.error("CubicChunks is installed but the generator it makes its worlds with cannot be reached, so villages in its worlds are laid without their finishing passes", failed);
        }
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
