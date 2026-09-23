package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import javax.annotation.Nullable;

public final class BeardCrown {
    private static final int CROWN_REACH = 4;
    private BeardCrown() {}

    public static final class Born {
        private final int minX;
        private final int minZ;
        private final int deep;
        private final IBlockState[] tops;
        private final int[] ys;
        private Born(StructureBoundingBox window) {
            minX = window.minX;
            minZ = window.minZ;
            deep = window.maxZ - window.minZ + 1;
            tops = new IBlockState[(window.maxX - window.minX + 1) * deep];
            ys = new int[tops.length];
        }
        private void saw(int x, int z, int y, IBlockState held) {
            int spot = (x - minX) * deep + (z - minZ);
            tops[spot] = held;
            ys[spot] = y;
        }
        @Nullable public IBlockState top(int x, int z) { return tops[(x - minX) * deep + (z - minZ)]; }
        public int topY(int x, int z) { return ys[(x - minX) * deep + (z - minZ)]; }
    }

    public static Born bornWindow(World world, StructureBoundingBox window, BlockPos.MutableBlockPos at) {
        Born born = new Born(window);
        for (int x = window.minX; x <= window.maxX; x++) {
            for (int z = window.minZ; z <= window.maxZ; z++) {
                int top = BeardBlocks.bornTop(world, at, x, z, 1, world.getHeight(x, z));
                if (top == Integer.MIN_VALUE) { continue; }
                at.setPos(x, top, z);
                born.saw(x, z, top, world.getBlockState(at));
            }
        }
        return born;
    }

    static boolean crownable(StructureComponent piece) { return ContentBeard.settling(piece) && !(piece instanceof StructureVillagePieces.Start); }

    private static boolean reaches(StructureBoundingBox box, StructureBoundingBox clip) { return box.intersectsWith(clip.minX - CROWN_REACH, clip.minZ - CROWN_REACH, clip.maxX + CROWN_REACH, clip.maxZ + CROWN_REACH); }

    public static boolean crowns(StructureStart start, StructureBoundingBox clip) {
        for (StructureComponent piece : start.getComponents()) {
            if (crownable(piece) && reaches(piece.getBoundingBox(), clip)) { return true; }
        }
        return false;
    }

    public static void crownWindow(StructureStart start, World world, StructureBoundingBox clip, Born born) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (StructureComponent piece : start.getComponents()) {
            if (!crownable(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (!reaches(box, clip)) { continue; }
            Crowning crowning = crown(start, piece, world, box, clip, at, born);
            if (crowning.crowned() + crowning.passed() > 0) { ContentLog.LOGGER.debug("Crowned {} block(s) around {} at {}, {} back with the surface the land was born with, passed over {}", crowning.crowned(), piece.getClass().getSimpleName(), box.minX, box.minZ, crowning.skips()); }
        }
    }

    public static final class Crowning {
        private final int[] tally = new int[BeardBlocks.Crowned.values().length];
        private final int[][] first = new int[BeardBlocks.Crowned.values().length][];
        private void took(BeardBlocks.Crowned why, int x, int y, int z) {
            tally[why.ordinal()]++;
            if (first[why.ordinal()] == null) { first[why.ordinal()] = new int[] {x, y, z}; }
        }
        public int crowned() { return tally[BeardBlocks.Crowned.WRITTEN.ordinal()]; }
        public int passed() {
            int passed = 0;
            for (BeardBlocks.Crowned why : BeardBlocks.Crowned.values()) {
                if (why == BeardBlocks.Crowned.WRITTEN || why == BeardBlocks.Crowned.OFF_CLIP) { continue; }
                passed += tally[why.ordinal()];
            }
            return passed;
        }
        public String skips() {
            StringBuilder said = new StringBuilder();
            for (BeardBlocks.Crowned why : BeardBlocks.Crowned.values()) {
                if (why == BeardBlocks.Crowned.WRITTEN || tally[why.ordinal()] == 0) { continue; }
                if (said.length() > 0) { said.append(", "); }
                int[] spot = first[why.ordinal()];
                said.append(tally[why.ordinal()]).append(' ').append(why.said()).append(" (first at ").append(spot[0]).append(", ").append(spot[1]).append(", ").append(spot[2]).append(')');
            }
            return said.length() == 0 ? "none" : said.toString();
        }
    }

    public static Crowning crown(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, Born born) {
        Crowning crowning = new Crowning();
        int reach = CROWN_REACH;
        int floor = box.minY - 24;
        int roof = box.maxY + 16;
        for (int x = box.minX - reach; x <= box.maxX + reach; x++) {
            for (int z = box.minZ - reach; z <= box.maxZ + reach; z++) {
                at.setPos(x, box.minY, z);
                if (!clip.isVecInside(at)) {
                    crowning.took(BeardBlocks.Crowned.OFF_CLIP, x, box.minY, z);
                    continue;
                }
                IBlockState was = born.top(x, z);
                if (was == null || born.topY(x, z) < floor) {
                    crowning.took(BeardBlocks.Crowned.BORN_UNKNOWN, x, box.minY, z);
                    continue;
                }
                if (born.topY(x, z) > roof) {
                    crowning.took(BeardBlocks.Crowned.BORN_UNDER_SOIL, x, box.minY, z);
                    continue;
                }
                if (BeardPlots.underRoad(start, piece, x, z)) {
                    crowning.took(BeardBlocks.Crowned.UNDER_ROAD, x, box.minY, z);
                    continue;
                }
                int top = BeardBlocks.groundTop(world, at, x, z, floor, roof);
                if (top == Integer.MIN_VALUE) {
                    crowning.took(BeardBlocks.Crowned.TOP_NOT_GROUND, x, box.minY, z);
                    continue;
                }
                at.setPos(x, top, z);
                if (BeardPlots.insideAnother(start, piece, at)) {
                    crowning.took(BeardBlocks.Crowned.INSIDE_PIECE, x, top, z);
                    continue;
                }
                crowning.took(BeardBlocks.crownGround(world, at, x, top, z, was), x, top, z);
            }
        }
        return crowning;
    }
}
