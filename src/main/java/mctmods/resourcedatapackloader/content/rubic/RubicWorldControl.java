package mctmods.resourcedatapackloader.content.rubic;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldServer;
import mctmods.resourcedatapackloader.mixin.RDPLMixinPlugin;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.StartupQuery;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RubicWorldControl {
    private RubicWorldControl() {}

    public static boolean wanted() {
        if (!ContentControl.flag(ContentControl.TERRAIN, "rubicWorld", false)) { return false; }
        if (Config.content.vanillaClients) {
            if (!VANILLA_CLIENTS_WARNED.getAndSet(true)) {
                ContentLog.LOGGER.error(vanillaClientsMessage("a pack asks for a rubic world")
                        + " This world is being made plain instead. Turn content.vanillaClients off if the rubic world is what you want,"
                        + " or take the rubicWorld setting out of the pack if serving clients without the mod is.");
            }
            return false;
        }
        if (!RDPLMixinPlugin.cubicChunksPresent()) { return true; }
        standDown();
        return false;
    }

    private static final AtomicBoolean VANILLA_CLIENTS_WARNED = new AtomicBoolean();

    /**
     * A rubic world is made of cubes, which a client without this mod has no way to be told about, so the two settings
     * cannot both hold.
     */
    private static String vanillaClientsMessage(String because) {
        return "content.vanillaClients is on, but " + because + "."
                + " A rubic world is made of cubes, and a client without this mod cannot be sent them: it would be turned away at login, or see nothing at all.";
    }

    /**
     * Only for a world already made as a rubic world, where carrying on would load its cubes as a plain world and ruin
     * the save. Making a new world simply does not turn rubic on, which needs no such stop.
     */
    public static void standDownForVanillaClients(String because) {
        String message = vanillaClientsMessage(because)
                + " Its cubes are on disk, so loading it as a plain world would ruin it: it is left untouched and nothing has been written."
                + " Turn content.vanillaClients off to load this world as the rubic world it is, or leave it alone and serve clients without the mod from a different world.";
        ContentLog.LOGGER.error(message);
        StartupQuery.notify(message);
        StartupQuery.abort();
    }

    public static boolean claims(int dimension) {
        int[] listed = ContentControl.numbers(ContentControl.TERRAIN, "rubicWorldDimensions", Config.worldgen.rubicWorldDimensions);
        if (listed.length == 0) { return true; }
        boolean found = false;
        for (int held : listed) {
            if (held == dimension) {
                found = true;
                break;
            }
        }
        return found != ContentControl.flag(ContentControl.TERRAIN, "rubicWorldDimensionsAreBlacklist", Config.worldgen.rubicWorldDimensionsAreBlacklist);
    }

    private static void standDown() {
        String message = "A pack asks for a rubic world, but CubicChunks is installed and this mod's cubic worlds have stood down for it."
                + " Running the two together is not supported: take CubicChunks out to use this mod's own cubic worlds, or take the rubicWorld setting"
                + " out of the pack to carry on with CubicChunks making them. Please do not report anything about this mod while CubicChunks is installed.";
        ContentLog.LOGGER.error(message);
        StartupQuery.notify(message);
        StartupQuery.abort();
    }

    private static final int DEFAULT_MIN_HEIGHT = -64;
    private static final int DEFAULT_MAX_HEIGHT = 320;

    public static int minHeight() { return heights()[0]; }

    public static int maxHeight() { return heights()[1]; }

    private static int[] heights() {
        int min = ContentControl.number(ContentControl.TERRAIN, "worldMinHeight", DEFAULT_MIN_HEIGHT);
        int max = ContentControl.number(ContentControl.TERRAIN, "worldMaxHeight", DEFAULT_MAX_HEIGHT);
        int limit = Config.worldgen.rubicHeightLimit;
        if (min > -limit && max < limit && min < max && (min & 15) == 0 && (max & 15) == 0) { return new int[] {min, max}; }
        if (min >= max) { refuse("worldMinHeight " + min + " is not below worldMaxHeight " + max); }
        else if ((min & 15) != 0 || (max & 15) != 0) { refuse("worldMinHeight " + min + " and worldMaxHeight " + max + " must both be whole cubes, so multiples of 16"); }
        else { refuse("worldMinHeight " + min + " and worldMaxHeight " + max + " reach past the " + limit + " block(s) either way that rubicHeightLimit allows"); }
        return new int[] {DEFAULT_MIN_HEIGHT, DEFAULT_MAX_HEIGHT};
    }

    private static void refuse(String why) {
        if (WARNED.compareAndSet(false, true)) {
            ContentLog.LOGGER.error("A pack asks for a rubic world whose {}, so the world is made from {} to {} instead", why, DEFAULT_MIN_HEIGHT, DEFAULT_MAX_HEIGHT);
        }
    }

    private static final AtomicBoolean WARNED = new AtomicBoolean();

    public static int terrainOffsetCubes() {
        int offset = ContentControl.number(ContentControl.TERRAIN, "terrainOffset", Config.worldgen.terrainOffset);
        if (offset == 0) { return 0; }
        if (offset < 0 || (offset & 15) != 0) {
            if (OFFSET_WARNED.compareAndSet(false, true)) { ContentLog.LOGGER.error("terrainOffset is {}, which is not a non-negative multiple of 16, so the terrain is not shifted", offset); }
            return 0;
        }
        return offset >> 4;
    }

    private static final AtomicBoolean OFFSET_WARNED = new AtomicBoolean();

    public static int generatedCeiling(World world) {
        int actual = world.provider.getActualHeight();
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return actual; }
        if (ContentControl.text(ContentControl.TERRAIN, "skyStone", Config.worldgen.skyStone).trim().isEmpty()) { return actual; }
        int top = ((IMinMaxHeight) world).rdpl$getMaxHeight();
        int[] band = ContentControl.numbers(ContentControl.TERRAIN, "skyHeights", Config.worldgen.skyHeights);
        if (band.length == 2 && band[0] < band[1]) { top = Math.min(top, band[1] + (terrainOffsetCubes() << 4) + 1); }
        return Math.max(actual, top);
    }

    public static boolean rubicWorld(ChunkProviderServer provider) {
        return provider instanceof CubeProviderServer && ((IRubicWorld) provider.world).rdpl$isRubicWorld();
    }

    public static boolean makeColumnCubes(ChunkProviderServer provider, Chunk column) {
        if (((IColumnInternal) column).pregenDone()) { return false; }
        IRubicWorld world = (IRubicWorld) provider.world;
        ICubeProviderServer cubes = (ICubeProviderServer) provider;
        int lowest = world.rdpl$getMinHeight() >> 4;
        int highest = (world.rdpl$getMaxHeight() >> 4) - 1;
        boolean worked = false;
        boolean whole = true;
        for (int cubeY = highest; cubeY >= lowest; cubeY--) {
            if (!worked) {
                ICube had = cubes.getCube(column.x, cubeY, column.z, ICubeProviderServer.Requirement.LOAD);
                if (had == null || !had.isFullyPopulated() || !had.isInitialLightingDone()) { worked = true; }
            }
            ICube after = cubes.getCube(column.x, cubeY, column.z, ICubeProviderServer.Requirement.LIGHT);
            if (after == null || !after.isFullyPopulated() || !after.isInitialLightingDone()) { whole = false; }
        }
        if (whole) {
            ((IColumnInternal) column).markPregenDone();
            column.setModified(true);
        }
        return worked;
    }

    public static int pendingSaves(ChunkProviderServer provider) { return ((CubeProviderServer) provider).getCubeIO().getPendingCubeCount(); }

    public static int loadedCubes(ChunkProviderServer provider) { return ((CubeProviderServer) provider).getLoadedCubeCount(); }

    public static void rdpl$unloadOldCubes(ChunkProviderServer provider) { ((IRubicWorldServer) provider.world).rdpl$unloadOldCubes(); }

    public static void unloadColumnCubes(ChunkProviderServer provider, Chunk column) { ((CubeProviderServer) provider).unloadColumnCubes(column); }
}
