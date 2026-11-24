package mctmods.resourcedatapackloader.common.rubic;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyStone;
import mctmods.resourcedatapackloader.common.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldServer;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.RDPLMixinPlugin;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.text.Listing;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.StartupQuery;

public final class RubicWorldControl {
    private RubicWorldControl() {}

    public static boolean wanted() {
        if (!ContentControl.flag(ContentControl.TERRAIN, "rubicWorld", false)) { return false; }
        if (Config.content.vanillaClients) {
            ContentLog.LOGGER.errorOnce(TOLD, "vanillaClients", "This pack asks for a rubic world, which vanilla clients cannot join. The world is made plain." + " Turn content.vanillaClients off, or remove rubicWorld from the pack.");
            return false;
        }
        if (!RDPLMixinPlugin.cubicChunksPresent()) { return true; }
        standDown("This pack needs a rubic world");
        return false;
    }

    private static final Set<String> TOLD = ConcurrentHashMap.newKeySet();

    public static void standDownIfWanted() { wanted(); }

    @Nullable public static WorldServer serverWorld(World world) { return world instanceof WorldServer ? (WorldServer) world : null; }

    public static void standDownForVanillaClients() { stop("This is a rubic world,\nwhich vanilla clients cannot join.\n\nIt was not loaded and nothing was changed.\n\nTurn content.vanillaClients off,\nor use a different world."); }

    private static void stop(String message) {
        ContentLog.LOGGER.error(message.replace("\n\n", " ").replace('\n', ' '));
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
        return Listing.allows(found, ContentControl.flag(ContentControl.TERRAIN, "rubicWorldDimensionsAreBlacklist", Config.worldgen.rubicWorldDimensionsAreBlacklist));
    }

    public static void standDownForSavedWorld() { standDown("This world is a rubic world"); }

    private static void standDown(String what) { stop(what + ",\nwhich cannot run with CubicChunks installed.\n\nRemove CubicChunks,\nor remove rubicWorld from the pack."); }

    private static final int DEFAULT_MIN_HEIGHT = -64;
    private static final int DEFAULT_MAX_HEIGHT = 320;

    public static int minHeight() { return heights()[0]; }

    public static int maxHeight() { return heights()[1]; }

    private static int[] heights() {
        int min = ContentControl.number(ContentControl.TERRAIN, "worldMinHeight", DEFAULT_MIN_HEIGHT);
        int max = ContentControl.number(ContentControl.TERRAIN, "worldMaxHeight", DEFAULT_MAX_HEIGHT);
        int limit = Config.worldgen.rubicHeightLimit;
        if (min > -limit && max < limit && min < max && Coords.cubeAligned(min) && Coords.cubeAligned(max)) { return new int[] {min, max}; }
        if (min >= max) { refuse("worldMinHeight " + min + " is not below worldMaxHeight " + max); }
        else if (!Coords.cubeAligned(min) || !Coords.cubeAligned(max)) { refuse("worldMinHeight " + min + " and worldMaxHeight " + max + " must both be whole cubes, so multiples of 16"); }
        else { refuse("worldMinHeight " + min + " and worldMaxHeight " + max + " reach past the " + limit + " block(s) either way that rubicHeightLimit allows"); }
        return new int[] {DEFAULT_MIN_HEIGHT, DEFAULT_MAX_HEIGHT};
    }

    private static void refuse(String why) {
        ContentLog.LOGGER.errorOnce(TOLD, "heights", "A pack asks for a rubic world whose {}, so the world is made from {} to {} instead", why, DEFAULT_MIN_HEIGHT, DEFAULT_MAX_HEIGHT);
    }

    public static int terrainOffsetCubes() {
        int offset = ContentControl.number(ContentControl.TERRAIN, "terrainOffset", Config.worldgen.terrainOffset);
        if (offset == 0) { return 0; }
        if (offset < 0 || !Coords.cubeAligned(offset)) {
            ContentLog.LOGGER.errorOnce(TOLD, "terrainOffset", "terrainOffset is {}, which is not a non-negative multiple of 16, so the terrain is not shifted", offset);
            return 0;
        }
        return Coords.blockToCube(offset);
    }

    public static int terrainOffsetBlocks() { return Coords.cubeToMinBlock(terrainOffsetCubes()); }


    public static int generatedCeiling(World world) {
        int actual = world.provider.getActualHeight();
        if (!GenHeights.rubic(world)) { return actual; }
        if (ContentControl.text(ContentControl.TERRAIN, SkyStone.STONE_KEY, Config.worldgen.skyStone).trim().isEmpty()) { return actual; }
        int top = ((IMinMaxHeight) world).rdpl$getMaxHeight();
        int[] band = ContentControl.numbers(ContentControl.TERRAIN, "skyHeights", Config.worldgen.skyHeights);
        if (band.length == 2 && band[0] < band[1]) { top = Math.min(top, band[1] + terrainOffsetBlocks() + 1); }
        return Math.max(actual, top);
    }

    public static boolean rubicWorld(ChunkProviderServer provider) { return provider instanceof CubeProviderServer && GenHeights.rubic(provider.world); }

    public static boolean makeColumnCubes(ChunkProviderServer provider, Chunk column) {
        if (((IColumnInternal) column).pregenDone()) { return false; }
        IRubicWorld world = (IRubicWorld) provider.world;
        ICubeProviderServer cubes = (ICubeProviderServer) provider;
        int lowest = Coords.blockToCube(world.rdpl$getMinHeight());
        int highest = Coords.topCubeY(world);
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

    public static boolean cubeSaved(ChunkProviderServer provider, int cubeX, int cubeY, int cubeZ) { return ((CubeProviderServer) provider).getCubeIO().cubeExists(cubeX, cubeY, cubeZ); }

    public static Path storagePath(WorldServer world) { return CubeProviderServer.storagePath(world); }

    public static int loadedCubes(ChunkProviderServer provider) { return ((CubeProviderServer) provider).getLoadedCubeCount(); }

    public static void rdpl$unloadOldCubes(ChunkProviderServer provider) { ((IRubicWorldServer) provider.world).rdpl$unloadOldCubes(); }

    public static void unloadColumnCubes(ChunkProviderServer provider, Chunk column) { ((CubeProviderServer) provider).unloadColumnCubes(column); }
}
