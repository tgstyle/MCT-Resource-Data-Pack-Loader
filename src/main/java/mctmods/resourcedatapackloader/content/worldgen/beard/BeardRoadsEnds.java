package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CitySeams;
import mctmods.resourcedatapackloader.content.village.MergePiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsEnds {
    private BeardRoadsEnds() {}

    public static final class Bulb {
        public final StructureBoundingBox box;
        public final boolean alongX;
        public final int level;
        public final int cx;
        public final int cz;
        public final int r;
        final List<int[]> mouths = new ArrayList<>();
        Bulb(StructureBoundingBox box, boolean alongX, int level, int cx, int cz, int r) {
            this.box = box;
            this.alongX = alongX;
            this.level = level;
            this.cx = cx;
            this.cz = cz;
            this.r = r;
        }

        public int away(int x, int z) { return (x - cx) * (x - cx) + (z - cz) * (z - cz); }

        public int mouthOffset(int x, int z) {
            int half = (BeardRoads.pathFullWidth() - 1) / 2;
            for (int[] mouth : mouths) {
                boolean mouthX = mouth[0] == 1;
                boolean high = mouth[1] == 1;
                int off = (mouthX ? z : x) - mouth[2];
                if (Math.abs(off) > half) { continue; }
                if (mouthX ? (high ? x >= cx : x <= cx) : (high ? z >= cz : z <= cz)) { return off; }
            }
            return Integer.MIN_VALUE;
        }

        public boolean throatAt(int x, int z) { return mouthOffset(x, z) != Integer.MIN_VALUE; }

        public boolean pavedAt(int x, int z) {
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { return false; }
            return away(x, z) <= r * r + r || throatAt(x, z);
        }
    }

    @Nullable public static Bulb bulbAt(World world, StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        int level = Integer.MIN_VALUE;
        int entry = Integer.MIN_VALUE;
        for (StructureComponent other : BeardRoads.villagePieces(world)) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (BeardPlots.roadAlongX(road) != alongX) { continue; }
            boolean ahead = alongX ? road.minX == box.maxX + 1 : road.minZ == box.maxZ + 1;
            boolean behind = alongX ? road.maxX == box.minX - 1 : road.maxZ == box.minZ - 1;
            if (!ahead && !behind) { continue; }
            int roadCenter = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            int bulbCenter = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            if (Math.abs(roadCenter - bulbCenter) > 1) { continue; }
            BeardRoads.Grade grade = BeardRoadsGrade.chainGrade(world, other, alongX);
            if (grade == null) { continue; }
            int mouth = ahead ? (alongX ? road.minX : road.minZ) : (alongX ? road.maxX : road.maxZ);
            int found = grade.at(mouth);
            if (found == Integer.MIN_VALUE) { found = grade.deckAt(mouth); }
            if (found == Integer.MIN_VALUE) { continue; }
            level = found;
            entry = behind ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
            break;
        }
        if (level == Integer.MIN_VALUE) { level = ContentBeard.noiseAverage(world, box); }
        if (level == Integer.MIN_VALUE) { return null; }
        int r = Math.min(box.maxX - box.minX, box.maxZ - box.minZ) / 2;
        int cx = (box.minX + box.maxX) / 2;
        int cz = (box.minZ + box.maxZ) / 2;
        if (entry != Integer.MIN_VALUE) {
            if (alongX) { cx = entry == box.minX ? box.minX + r : box.maxX - r; }
            else { cz = entry == box.minZ ? box.minZ + r : box.maxZ - r; }
        }
        Bulb bulb = new Bulb(box, alongX, level, cx, cz, r);
        for (StructureComponent other : BeardRoads.villagePieces(world)) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(other)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            boolean roadX = BeardPlots.roadAlongX(other);
            if (BeardRoads.roadNarrow(road, roadX)) { continue; }
            int center = roadX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            if (roadX ? (center < box.minZ || center > box.maxZ) : (center < box.minX || center > box.maxX)) { continue; }
            if (roadX && road.minX == box.maxX + 1) { bulb.mouths.add(new int[] { 1, 1, center }); }
            else if (roadX && road.maxX == box.minX - 1) { bulb.mouths.add(new int[] { 1, 0, center }); }
            else if (!roadX && road.minZ == box.maxZ + 1) { bulb.mouths.add(new int[] { 0, 1, center }); }
            else if (!roadX && road.maxZ == box.minZ - 1) { bulb.mouths.add(new int[] { 0, 0, center }); }
        }
        return bulb;
    }

    static void paveBulb(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, boolean chosenSurface) {
        Bulb bulb = bulbAt(world, piece);
        if (bulb == null) { return; }
        StructureBoundingBox box = bulb.box;
        boolean alongX = bulb.alongX;
        int level = bulb.level;
        int r = bulb.r;
        int cx = bulb.cx;
        int cz = bulb.cz;
        int walk = BeardRoads.pathSidewalkWidth();
        int lines = BeardRoads.pathLineColumns();
        int core = r - walk - lines;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int paved = 0;
        int lane = 1 + BeardRoads.pathExtraWidth();
        IBlockState lineBlock = ContentBeard.axised(BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX);
        IBlockState walkBlock = BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path);
        for (int z = Math.max(box.minZ, clip.minZ); z <= Math.min(box.maxZ, clip.maxZ); z++) {
            for (int x = Math.max(box.minX, clip.minX); x <= Math.min(box.maxX, clip.maxX); x++) {
                int dx = x - cx;
                int dz = z - cz;
                int d2 = dx * dx + dz * dz;
                int mouth = bulb.mouthOffset(x, z);
                boolean throat = mouth != Integer.MIN_VALUE;
                int acrossOff = throat ? Math.abs(mouth) : (alongX ? Math.abs(dz) : Math.abs(dx));
                if (!throat && d2 > r * r + r) { continue; }
                if (BeardRoads.insidePlaza(x, z)) { continue; }
                if (BeardKeep.holds(x, level, z)) { continue; }
                IBlockState roadway = chosenSurface ? path : BeardRoads.pathForGround(world, x, z, path, gravel, true);
                IBlockState chosen;
                if (d2 > r * r + r) {
                    if (acrossOff <= lane) { chosen = roadway; }
                    else if (acrossOff <= lane + lines) { chosen = lineBlock; }
                    else { chosen = walkBlock; }
                }
                else {
                    boolean opening = throat && acrossOff <= lane + lines;
                    if (!opening && d2 > (r - walk) * (r - walk) + r - walk) { chosen = walkBlock; }
                    else if (!opening && lines > 0 && d2 > core * core + core) { chosen = lineBlock; }
                    else if (opening && d2 > core * core + core && acrossOff > lane) { chosen = lineBlock; }
                    else { chosen = roadway; }
                }
                BlockPos ground = GroundLevel.inWindow(world, new BlockPos(x, 64, z)).down();
                int clearTo = Math.max(level + 4, ground.getY() + 2);
                for (int y = level + 1; y <= clearTo; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    if (above.getBlock() == Blocks.AIR) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (BeardBlocks.terrainBlock(above.getBlock()) || above.getBlock() == Blocks.GRASS_PATH || above.getBlock() == Blocks.SANDSTONE || above.getBlock() == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        continue;
                    }
                    break;
                }
                at.setPos(x, level, z);
                world.setBlockState(at, chosen, 2);
                BeardBlocks.fillUnder(world, at, x, z, level - 1, level - BeardRoadsPaving.FILL_UNDER);
                paved++;
            }
        }
        int filled = bulbVerge(world, piece, bulb, clip, at);
        if ((paved > 0 || filled > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paved {} column(s) of the cul-de-sac at {}, {} at y {}, and filled {} block(s) into its box corners and the verge around its circle", paved, box.minX, box.minZ, level, filled); }
    }

    public static int bulbShoulder(StructureComponent piece, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        Bulb bulb = bulbAt(world, piece);
        return bulb == null ? 0 : bulbVerge(world, piece, bulb, clip, at);
    }

    private static int bulbVerge(World world, StructureComponent piece, Bulb bulb, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        StructureBoundingBox box = bulb.box;
        int verge = CityGrowth.verge();
        int shoulder = bulb.r + verge;
        int filled = 0;
        for (int z = Math.max(box.minZ - verge, clip.minZ); z <= Math.min(box.maxZ + verge, clip.maxZ); z++) {
            for (int x = Math.max(box.minX - verge, clip.minX); x <= Math.min(box.maxX + verge, clip.maxX); x++) {
                if (bulb.pavedAt(x, z)) { continue; }
                boolean inside = x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ;
                if (!inside && bulb.away(x, z) > shoulder * shoulder + shoulder) { continue; }
                filled += BeardRoadsPaving.vergeFill(world, piece, x, z, bulb.level, at);
            }
        }
        return filled;
    }

    private static boolean deadEnd(List<StructureComponent> nearby, StructureComponent piece, boolean alongX, int row) {
        StructureBoundingBox box = piece.getBoundingBox();
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        if (row != least && row != most) { return false; }
        int beyond = row == least ? row - 1 : row + 1;
        for (StructureComponent other : nearby) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path || other instanceof MergePiece)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            int lo = alongX ? met.minX : met.minZ;
            int hi = alongX ? met.maxX : met.maxZ;
            if (beyond < lo - 1 || beyond > hi + 1) { continue; }
            int acrossLo = alongX ? met.minZ : met.minX;
            int acrossHi = alongX ? met.maxZ : met.maxX;
            if (acrossHi < (alongX ? box.minZ : box.minX) || acrossLo > (alongX ? box.maxZ : box.maxX)) { continue; }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The end row {} of the road at {}, {} is no dead end: {} at {} sits beyond it", row, box.minX, box.minZ, other.getClass().getSimpleName(), met); }
            return false;
        }
        return true;
    }

    static final class DeadEndCap {
        final List<StructureComponent> nearby;
        final IBlockState walk;
        final IBlockState rail;
        final List<String> usable;
        final boolean seamLow;
        final boolean seamHigh;

        DeadEndCap(List<StructureComponent> nearby, IBlockState walk, IBlockState rail, List<String> usable, boolean seamLow, boolean seamHigh) {
            this.nearby = nearby;
            this.walk = walk;
            this.rail = rail;
            this.usable = usable;
            this.seamLow = seamLow;
            this.seamHigh = seamHigh;
        }
    }

    @Nullable static DeadEndCap deadEndCap(World world, List<StructureComponent> nearby, StructureComponent piece, boolean alongX) {
        String[] wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathDeadEnds", Config.worldgen.villagePathDeadEnds);
        if (wanted.length == 0) { return null; }
        IBlockState walk = BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, Blocks.AIR.getDefaultState());
        IBlockState rail = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, Blocks.AIR.getDefaultState());
        List<String> usable = new ArrayList<>(wanted.length);
        for (String asked : wanted) {
            if ("sidewalk".equals(asked) && walk.getBlock() != Blocks.AIR && !BeardRoads.roadNarrow(piece.getBoundingBox(), alongX)) { usable.add(asked); }
            else if ("barrier".equals(asked) && rail.getBlock() != Blocks.AIR) { usable.add(asked); }
        }
        if (usable.isEmpty()) { return null; }
        StructureBoundingBox box = piece.getBoundingBox();
        boolean seamLow = false;
        boolean seamHigh = false;
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (!village.getComponents().contains(piece)) { continue; }
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            seamLow = CitySeams.facesNeighbor(world, village.getComponents(), alongX, alongX ? box.minX : box.minZ, -1, center);
            seamHigh = CitySeams.facesNeighbor(world, village.getComponents(), alongX, alongX ? box.maxX : box.maxZ, 1, center);
            break;
        }
        return new DeadEndCap(nearby, walk, rail, usable, seamLow, seamHigh);
    }

    static int deadEndCap(@Nullable DeadEndCap cap, World world, StructureComponent piece, boolean alongX, int row, int across, int acrossLeast, int level, BlockPos.MutableBlockPos at) {
        if (cap == null || !deadEnd(cap.nearby, piece, alongX, row)) { return 0; }
        StructureBoundingBox box = piece.getBoundingBox();
        if (row == (alongX ? box.minX : box.minZ) ? cap.seamLow : cap.seamHigh) { return 0; }
        IBlockState walk = cap.walk;
        IBlockState rail = cap.rail;
        List<String> usable = cap.usable;
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        String style = usable.get(Math.floorMod(SeededRandom.at(world, alongX ? row : acrossLeast, alongX ? acrossLeast : row).nextInt(Integer.MAX_VALUE), usable.size()));
        if (across == acrossLeast && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The end row {} of the road at {}, {} closes as {}", row, box.minX, box.minZ, style); }
        if ("sidewalk".equals(style)) {
            at.setPos(x, level, z);
            world.setBlockState(at, walk, 2);
            BeardKeep.holdSpot(x, level, z);
            return 1;
        }
        int high = BeardRoads.barrierHeight();
        int laid = 0;
        for (int step = 1; step <= high; step++) {
            at.setPos(x, level + step, z);
            if (BeardKeep.holds(x, level + step, z)) { break; }
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            world.setBlockState(at, rail, 2);
            BeardKeep.holdSpot(x, level + step, z);
            laid++;
        }
        return laid;
    }
}
