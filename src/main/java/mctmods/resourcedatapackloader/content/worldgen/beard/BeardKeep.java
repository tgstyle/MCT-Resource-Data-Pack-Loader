package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class BeardKeep {
    private static final int REACH = 3;
    private static final int CROWDED = 400000;
    private static final Set<Long> HELD = new LinkedHashSet<>();
    private static StructureComponent watching = null;
    private static IBlockState[] before = null;
    private static int fromX;
    private static int fromZ;
    private static int fromY;
    private static int acrossZ;
    private static int upright;

    private BeardKeep() {}

    private static void hold(Set<Long> spots) {
        if (spots.isEmpty()) { return; }
        if (HELD.size() + spots.size() > CROWDED) {
            int shed = 0;
            Iterator<Long> oldest = HELD.iterator();
            while (oldest.hasNext() && HELD.size() + spots.size() - shed > CROWDED) {
                oldest.next();
                oldest.remove();
                shed++;
            }
            ContentLog.LOGGER.debug("The set of blocks held against clearing reached its limit, so the {} oldest were let go rather than all of them", shed);
        }
        HELD.addAll(spots);
    }

    public static void holdSpot(int x, int y, int z) {
        Set<Long> spot = new HashSet<>();
        spot.add(packed(x, y, z));
        hold(spot);
    }

    public static void watch(World world, StructureComponent piece, StructureBoundingBox clip) {
        watching = null;
        before = null;
        if (!(world instanceof WorldServer)) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        boolean vanilla = settles(piece);
        int reach = vanilla ? REACH : 16;
        int leastX = Math.max(box.minX - reach, clip.minX);
        int mostX = Math.min(box.maxX + reach, clip.maxX);
        int leastZ = Math.max(box.minZ - reach, clip.minZ);
        int mostZ = Math.min(box.maxZ + reach, clip.maxZ);
        if (leastX > mostX || leastZ > mostZ) { return; }
        int floor;
        int roof;
        if (vanilla) {
            floor = Math.max(0, box.minY - REACH);
            roof = Math.min(255, box.maxY + REACH);
        }
        else {
            int ground = ground(world, leastX, mostX, leastZ, mostZ);
            floor = Math.max(0, Math.min(box.minY, ground) - 16);
            roof = Math.min(world.getActualHeight() - 1, Math.max(box.maxY, ground) + 48);
        }
        int wide = mostX - leastX + 1;
        int deep = mostZ - leastZ + 1;
        int tall = roof - floor + 1;
        IBlockState[] seen = new IBlockState[wide * deep * tall];
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int index = 0;
        for (int x = leastX; x <= mostX; x++) {
            for (int z = leastZ; z <= mostZ; z++) {
                for (int y = floor; y <= roof; y++) {
                    at.setPos(x, y, z);
                    seen[index++] = world.getBlockState(at);
                }
            }
        }
        watching = piece;
        before = seen;
        fromX = leastX;
        fromZ = leastZ;
        fromY = floor;
        acrossZ = deep;
        upright = tall;
    }

    private static boolean settles(StructureComponent piece) {
        Class<?> owner = piece.getClass().getEnclosingClass();
        return owner == StructureVillagePieces.class || owner == StructureMineshaftPieces.class || owner == StructureStrongholdPieces.class;
    }

    private static int ground(World world, int leastX, int mostX, int leastZ, int mostZ) {
        int highest = 0;
        for (int x = leastX; x <= mostX; x += 4) {
            for (int z = leastZ; z <= mostZ; z += 4) {
                int here = world.getChunk(x >> 4, z >> 4).getHeightValue(x & 15, z & 15);
                if (here > highest) { highest = here; }
            }
        }
        return highest;
    }

    public static void learn(World world) {
        StructureComponent piece = watching;
        IBlockState[] seen = before;
        watching = null;
        before = null;
        if (piece == null || seen == null) { return; }
        Set<Long> mine = new HashSet<>();
        int found = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int index = 0;
        for (int x = fromX; index < seen.length; x++) {
            for (int z = fromZ; z < fromZ + acrossZ; z++) {
                for (int y = fromY; y < fromY + upright; y++) {
                    IBlockState was = seen[index++];
                    at.setPos(x, y, z);
                    IBlockState now = world.getBlockState(at);
                    if (now == was || now.getBlock() == Blocks.AIR) { continue; }
                    mine.add(packed(x, y, z));
                    found++;
                }
            }
        }
        if (mine.isEmpty()) { return; }
        hold(mine);
        if (found > 0 && ContentLog.LOGGER.debugEnabled()) {
            StructureBoundingBox box = piece.getBoundingBox();
            int outside = 0;
            int leastY = Integer.MAX_VALUE;
            int mostY = Integer.MIN_VALUE;
            int leastZ = Integer.MAX_VALUE;
            int mostZ = Integer.MIN_VALUE;
            for (long key : mine) {
                int y = (int) ((key >> 26) & 0xFFF);
                int z = (int) (key << 38 >> 38);
                int x = (int) (key >> 38);
                if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { outside++; }
                leastY = Math.min(leastY, y);
                mostY = Math.max(mostY, y);
                leastZ = Math.min(leastZ, z);
                mostZ = Math.max(mostZ, z);
            }
            ContentLog.LOGGER.debug("Holding {} block(s) that {} at {}, {} laid down against any clearing, {} of them outside its box, reaching z {}..{} and y {}..{}", found, piece.getClass().getSimpleName(), box.minX, box.minZ, outside, leastZ, mostZ, leastY, mostY);
        }
    }

    private static long packed(int x, int y, int z) { return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF); }

    public static boolean holds(int x, int y, int z) { return HELD.contains(packed(x, y, z)); }

    public static StructureBoundingBox watchingBox() { return watching == null ? null : watching.getBoundingBox(); }

    public static String watchingName() { return watching == null ? null : watching.getClass().getSimpleName(); }
}
