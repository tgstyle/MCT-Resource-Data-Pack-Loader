package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class CityPlotDeck {
    private static final int APPROACH = 4;
    private static final int DECK_HEAD = 3;
    private static final int DECK_FILL = 8;
    private CityPlotDeck() {}

    public static void approach(WorldGenLevel level, BoundingBox box, int seat, int[] strip) {
        if (strip.length < 4) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(strip[0], box.minX()); x <= Math.min(strip[2], box.maxX()); x++) {
            for (int z = Math.max(strip[1], box.minZ()); z <= Math.min(strip[3], box.maxZ()); z++) {
                for (int y = seat + 1; y <= seat + APPROACH; y++) {
                    at.set(x, y, z);
                    if (CityPlotGround.clearable(level.getBlockState(at))) { level.setBlock(at, Blocks.AIR.defaultBlockState(), 2); }
                }
            }
        }
    }

    public static void deck(WorldGenLevel level, BoundingBox box, int[] strip, int[] deck, byte[] decked, int[] keep) {
        if (strip.length < 4 || deck.length <= DECK_HEAD) { return; }
        boolean alongX = deck[0] == 1;
        int first = deck[1];
        int edge = deck[2];
        BlockState planks = CityPalette.stateOr(ContentCity.bridgeBlock(), Blocks.OAK_PLANKS.defaultBlockState());
        BlockState barrier = CityPalette.state(ContentCity.bridgeBarrierBlock());
        int tall = ContentCity.bridgeBarrierHeight();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int last = first + decked.length - 1;
        for (int x = Math.max(alongX ? first : strip[0], box.minX()); x <= Math.min(alongX ? last : strip[2], box.maxX()); x++) {
            for (int z = Math.max(alongX ? strip[1] : first, box.minZ()); z <= Math.min(alongX ? strip[3] : last, box.maxZ()); z++) {
                int index = (alongX ? x : z) - first;
                if (index < 0 || index >= decked.length || CityPlotGround.kept(keep, x, z) || pastStrip(strip, alongX, x, z, edge)) { continue; }
                int seat = deck[DECK_HEAD + index];
                if (seat == Integer.MIN_VALUE) { continue; }
                BlockState held = level.getBlockState(at.set(x, seat, z));
                boolean doorstep = held.getBlock() instanceof StairBlock;
                if (CityPlotGround.solid(held) && !doorstep) { continue; }
                if (decked[index] == 0 || grounded(level, at, x, seat, z)) {
                    groundTo(level, box, at, x, seat, z);
                    continue;
                }
                level.setBlock(at.set(x, seat, z), planks, 2);
                if (barrier != null) { unrail(level, box, at, alongX ? x : edge, seat + 1, alongX ? edge : z, barrier, tall); }
                for (int up = 1; up <= 2; up++) {
                    at.set(x, seat + up, z);
                    if (!CityPlotGround.liquid(level.getBlockState(at))) { break; }
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private static boolean pastStrip(int[] strip, boolean alongX, int x, int z, int edge) {
        int along = alongX ? x : z;
        boolean beside = Math.abs((alongX ? z : x) - edge) == 1;
        return !beside && (along < (alongX ? strip[0] : strip[1]) || along > (alongX ? strip[2] : strip[3]));
    }

    private static void unrail(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at, int x, int y, int z, BlockState barrier, int tall) {
        if (!box.isInside(at.set(x, y, z))) { return; }
        int high = 0;
        while (high <= tall && level.getBlockState(at.set(x, y + high, z)) == barrier) { high++; }
        if (high > tall) { return; }
        for (int up = 0; up < high; up++) {
            at.set(x, y + up, z);
            if (box.isInside(at)) { level.setBlock(at, Blocks.AIR.defaultBlockState(), 2); }
        }
    }

    private static boolean grounded(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int y, int z) {
        if (CityPlotGround.solid(level.getBlockState(at.set(x, y - 1, z)))) { return true; }
        if (level.getBlockState(at).isAir()) { return false; }
        return CityPlotGround.solid(level.getBlockState(at.set(x, y - 2, z)));
    }

    private static void groundTo(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at, int x, int seat, int z) {
        int footing = seat - DECK_FILL - 1;
        for (int y = seat; y >= seat - DECK_FILL; y--) {
            if (CityPlotGround.solid(level.getBlockState(at.set(x, y, z)))) {
                footing = y;
                break;
            }
        }
        for (int y = seat; y > footing; y--) {
            at.set(x, y, z);
            if (!box.isInside(at)) { continue; }
            level.setBlock(at, CityPlotGround.verge(level, at, false, x, y, z), 2);
        }
    }
}
