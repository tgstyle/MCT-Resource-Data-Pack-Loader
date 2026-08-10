package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BeardKeep {
    private static final int REACH = 3;
    private static final Set<Long> HELD = new HashSet<>();
    private static StructureComponent watching = null;
    private static Map<Long, IBlockState> before = null;

    private BeardKeep() {}

    private static void hold(Set<Long> spots) {
        if (spots.isEmpty()) { return; }

        if (HELD.size() > 200000) { HELD.clear(); }
        HELD.addAll(spots);
    }

    public static void watch(World world, StructureComponent piece, StructureBoundingBox clip) {
        watching = null;
        before = null;
        if (!(world instanceof WorldServer)) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        int leastX = Math.max(box.minX - REACH, clip.minX);
        int mostX = Math.min(box.maxX + REACH, clip.maxX);
        int leastZ = Math.max(box.minZ - REACH, clip.minZ);
        int mostZ = Math.min(box.maxZ + REACH, clip.maxZ);
        if (leastX > mostX || leastZ > mostZ) { return; }

        int floor = Math.max(0, box.minY - REACH);
        int roof = Math.min(255, box.maxY + REACH);
        Map<Long, IBlockState> seen = new HashMap<>();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = leastX; x <= mostX; x++) {
            for (int z = leastZ; z <= mostZ; z++) {
                for (int y = floor; y <= roof; y++) {
                    at.setPos(x, y, z);
                    seen.put(packed(x, y, z), world.getBlockState(at));
                }
            }
        }
        watching = piece;
        before = seen;
    }

    public static void learn(World world) {
        StructureComponent piece = watching;
        Map<Long, IBlockState> seen = before;
        watching = null;
        before = null;
        if (piece == null || seen == null) { return; }

        Set<Long> mine = new HashSet<>();
        int found = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (Map.Entry<Long, IBlockState> entry : seen.entrySet()) {
            long key = entry.getKey();
            at.setPos((int) (key >> 38), (int) ((key >> 26) & 0xFFF), (int) (key << 38 >> 38));
            IBlockState now = world.getBlockState(at);
            if (now == entry.getValue() || now.getBlock() == Blocks.AIR) { continue; }

            mine.add(key);
            found++;
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

}
