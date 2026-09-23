package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsSurface {
    private BeardRoadsSurface() {}

    private static final Map<World, Map<Long, Boolean>> SQUARE_DECK = new WeakHashMap<>();

    static boolean deckSquare(World world, StructureBoundingBox ew, StructureBoundingBox ns) {
        if (BeardSurface.unreadable(world)) { return false; }
        long key = ((long) ns.minX << 42) ^ ((long) (ew.minZ & 0x1FFFFF) << 21) ^ (ns.maxX & 0x1FFFFF);
        Map<Long, Boolean> decks = SQUARE_DECK.computeIfAbsent(world, held -> new HashMap<>());
        Boolean held = decks.get(key);
        if (held != null) { return held; }
        boolean deck = deckedOver(world, ew, true, ns.minX, ns.maxX) || deckedOver(world, ns, false, ew.minZ, ew.maxZ);
        decks.put(key, deck);
        return deck;
    }

    private static boolean deckedOver(World world, StructureBoundingBox box, boolean alongX, int rowLeast, int rowMost) {
        StructureComponent road = roadPiece(world, box);
        if (road == null) { return false; }
        BeardRoads.Grade grade = BeardRoadsGrade.chainGrade(world, road, alongX);
        if (grade == null) { return false; }
        for (int row = rowLeast; row <= rowMost; row++) {
            if (!grade.bridgedAt(row)) { return false; }
        }
        return rowMost >= rowLeast;
    }

    @Nullable private static StructureComponent roadPiece(World world, StructureBoundingBox box) {
        for (StructureComponent piece : BeardRoads.villagePieces(world)) {
            StructureBoundingBox other = piece.getBoundingBox();
            if (other.minX == box.minX && other.minZ == box.minZ && other.maxX == box.maxX && other.maxZ == box.maxZ) { return piece; }
        }
        return null;
    }

    static List<BeardCross> shapesAt(StructureBoundingBox box, boolean alongX, List<StructureBoundingBox> crossed) {
        List<BeardCross> shapes = new ArrayList<>(crossed.size() + 1);
        shapes.add(BeardCross.of(box, alongX));
        for (StructureBoundingBox road : crossed) {
            boolean roadAlongX = BeardPlots.roadAlongX(road);
            shapes.add(BeardCross.of(roadAlongX == alongX ? road : through(road, roadAlongX, box), roadAlongX));
        }
        return shapes;
    }

    private static StructureBoundingBox through(StructureBoundingBox road, boolean roadAlongX, StructureBoundingBox box) {
        StructureBoundingBox held = new StructureBoundingBox(road);
        int coreHalf = (BeardRoads.pathFullWidth() - 1) / 2 - BeardRoads.pathLineColumns() - BeardRoads.pathSidewalkWidth();
        if (roadAlongX) {
            int center = (box.minX + box.maxX) / 2;
            if (held.maxX >= box.minX - 1 && held.maxX < center + coreHalf) { held.maxX = center + coreHalf; }
            if (held.minX <= box.maxX + 1 && held.minX > center - coreHalf) { held.minX = center - coreHalf; }
        }
        else {
            int center = (box.minZ + box.maxZ) / 2;
            if (held.maxZ >= box.minZ - 1 && held.maxZ < center + coreHalf) { held.maxZ = center + coreHalf; }
            if (held.minZ <= box.maxZ + 1 && held.minZ > center - coreHalf) { held.minZ = center - coreHalf; }
        }
        return held;
    }

    static IBlockState mergeSurface(boolean alongX, int x, int z, int row, int center, IBlockState path) {
        int half = (BeardRoads.pathFullWidth() - 1) / 2;
        StructureBoundingBox band = alongX ? new StructureBoundingBox(row, 0, center - half, row, 0, center + half) : new StructureBoundingBox(center - half, 0, row, center + half, 0, row);
        BeardCross shape = BeardCross.of(band, alongX);
        int role = shape.role(x, z);
        if (role == BeardCross.WALK) { return BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        if (role == BeardCross.LINE) { return ContentBeard.axised(BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX); }
        if (shape.offMiddle(x, z)) { return path; }
        IBlockState middle = BeardRoads.pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, path);
        if (middle == path) { return path; }
        int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
        if (dash > 0 && Math.floorMod(row, dash + 1) == dash) { return path; }
        return ContentBeard.axised(middle, alongX);
    }

    @Nullable public static IBlockState dressSurface(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, IBlockState path, IBlockState planks, List<StructureBoundingBox> crossed) {
        StructureBoundingBox box = piece.getBoundingBox();
        BeardCross mine = BeardCross.of(box, alongX);
        if (mine.oversize || mine.width < 3 || BeardRoads.pathFullWidth() == 3) { return null; }
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        if (!mine.alley) {
            IBlockState stamped = stampAt(world, box, alongX, row, across, acrossCenter, mine.core, path, crossed);
            if (stamped != null) { return stamped; }
            IBlockState shared = overlapCell(world, box, alongX, row, across, mine.core, path, planks, crossed);
            if (shared != null) { return shared; }
        }
        List<BeardCross> shapes = shapesAt(box, alongX, crossed);
        BeardCross top = BeardCross.winner(shapes, x, z);
        if (top == null) { return null; }
        int role = top.role(x, z);
        if (role == BeardCross.WALK) { return BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        if (role == BeardCross.LINE) { return ContentBeard.axised(BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), top.alongX); }
        IBlockState surface = top.alley ? BeardRoads.alleyBlock(path) : path;
        if (top.alley || top.offMiddle(x, z) || BeardCross.crossedOver(shapes, top.alongX, x, z)) { return surface; }
        IBlockState center = BeardRoads.pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, surface);
        if (center == surface) { return surface; }
        int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
        if (dash > 0 && Math.floorMod(top.row(x, z), dash + 1) == dash) { return surface; }
        return ContentBeard.axised(center, top.alongX);
    }

    @Nullable public static IBlockState stampAt(World world, StructureBoundingBox box, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path, List<StructureBoundingBox> crossed) {
        if (Math.abs(across - acrossCenter) > core) { return null; }
        for (StructureBoundingBox road : crossed) {
            if (BeardPlots.roadAlongX(road) == alongX) { continue; }
            IBlockState cell = junctionCell(world, alongX, row, across, acrossCenter, core, alongX ? box : road, alongX ? road : box, box, crossed, path);
            if (cell != null) { return cell; }
        }
        return plazaMouth(world, box, alongX, row, across, acrossCenter, core, path);
    }

    static int arms(StructureBoundingBox ew, StructureBoundingBox ns, StructureBoundingBox mine, List<StructureBoundingBox> crossed) {
        int found = 0;
        for (int i = -1; i < crossed.size(); i++) {
            StructureBoundingBox c = i < 0 ? mine : crossed.get(i);
            if (BeardRoads.roadNarrow(c, BeardPlots.roadAlongX(c))) { continue; }
            if (BeardPlots.roadAlongX(c)) {
                if (c.maxZ < ew.minZ || c.minZ > ew.maxZ) { continue; }
                int center = (c.minZ + c.maxZ) / 2;
                if (center < ew.minZ || center > ew.maxZ) { continue; }
                if (c.minX < ns.minX && c.maxX >= ns.minX - 1) { found |= 1; }
                if (c.maxX > ns.maxX && c.minX <= ns.maxX + 1) { found |= 2; }
            }
            else {
                if (c.maxX < ns.minX || c.minX > ns.maxX) { continue; }
                int center = (c.minX + c.maxX) / 2;
                if (center < ns.minX || center > ns.maxX) { continue; }
                if (c.minZ < ew.minZ && c.maxZ >= ew.minZ - 1) { found |= 4; }
                if (c.maxZ > ew.maxZ && c.minZ <= ew.maxZ + 1) { found |= 8; }
            }
        }
        return found;
    }

    static boolean inSquare(StructureBoundingBox mine, boolean alongX, StructureBoundingBox road, int cellX, int cellZ) {
        StructureBoundingBox ew = alongX ? mine : road;
        StructureBoundingBox ns = alongX ? road : mine;
        return cellX >= ns.minX && cellX <= ns.maxX && cellZ >= ew.minZ && cellZ <= ew.maxZ;
    }

    static boolean squareAt(StructureBoundingBox box, boolean alongX, List<StructureBoundingBox> crossed, int x, int z) {
        for (StructureBoundingBox road : crossed) {
            if (BeardPlots.roadAlongX(road) == alongX) { continue; }
            if (!BeardRoads.roadNarrow(box, alongX) && BeardRoads.roadNarrow(road, !alongX)) { continue; }
            if (inSquare(box, alongX, road, x, z)) { return true; }
        }
        return false;
    }

    @Nullable static IBlockState overlapCell(World world, StructureBoundingBox box, boolean alongX, int row, int across, int core, IBlockState road0, IBlockState planks, List<StructureBoundingBox> crossed) {
        int cellX = alongX ? row : across;
        int cellZ = alongX ? across : row;
        for (StructureBoundingBox road : crossed) {
            if (BeardPlots.roadAlongX(road) == alongX) { continue; }
            if (!BeardRoads.roadNarrow(box, alongX) && BeardRoads.roadNarrow(road, !alongX)) { continue; }
            if (!inSquare(box, alongX, road, cellX, cellZ)) { continue; }
            StructureBoundingBox ew = alongX ? box : road;
            StructureBoundingBox ns = alongX ? road : box;
            boolean decked = deckSquare(world, ew, ns);
            IBlockState path = decked ? planks : road0;
            int centerX = (ns.minX + ns.maxX) / 2;
            int centerZ = (ew.minZ + ew.maxZ) / 2;
            int offX = Math.abs(cellX - centerX);
            int offZ = Math.abs(cellZ - centerZ);
            int reach = arms(ew, ns, box, crossed);
            boolean west = (reach & 1) != 0;
            boolean east = (reach & 2) != 0;
            boolean north = (reach & 4) != 0;
            boolean south = (reach & 8) != 0;
            boolean onEW = offZ <= core && (offX <= core || (cellX < centerX && west) || (cellX > centerX && east));
            boolean onNS = offX <= core && (offZ <= core || (cellZ < centerZ && north) || (cellZ > centerZ && south));
            if (onEW || onNS) {
                IBlockState flipped = junctionCell(world, !alongX, across, row, alongX ? centerX : centerZ, core, ew, ns, box, crossed, road0);
                if (flipped != null) { return flipped; }
                return path;
            }
            boolean armX = cellX > centerX ? east : cellX >= centerX || west;
            boolean armZ = cellZ > centerZ ? south : cellZ >= centerZ || north;
            int lines = BeardRoads.pathLineColumns();
            boolean bandX = lines > 0 && offX > core && offX <= core + lines;
            boolean bandZ = lines > 0 && offZ > core && offZ <= core + lines;
            boolean lined;
            boolean upright;
            if (armX && armZ) {
                lined = bandX || bandZ;
                upright = bandX;
            }
            else if (armX) {
                lined = bandZ;
                upright = false;
            }
            else if (armZ) {
                lined = bandX;
                upright = true;
            }
            else {
                lined = (bandX && offZ <= core + lines) || (bandZ && offX <= core + lines);
                upright = bandX;
            }
            if (lined) { return ContentBeard.axised(BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), !upright); }
            IBlockState walk = BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path);
            return decked ? BeardRoads.pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk) : walk;
        }
        return null;
    }

    @Nullable private static IBlockState junctionCell(World world, boolean alongX, int row, int across, int acrossCenter, int core, StructureBoundingBox ew, StructureBoundingBox ns, StructureBoundingBox mine, List<StructureBoundingBox> crossed, IBlockState road0) {
        if (Math.abs(across - acrossCenter) > core) { return null; }
        int found = arms(ew, ns, mine, crossed);
        if (Integer.bitCount(found) < 3) { return null; }
        if (BeardRoads.roadNarrow(ew, true) || BeardRoads.roadNarrow(ns, false) || deckSquare(world, ew, ns)) { return null; }
        int otherCenter = alongX ? (ns.minX + ns.maxX) / 2 : (ew.minZ + ew.maxZ) / 2;
        PathIntersectDef def = ContentPathIntersects.forJunction(world, (ns.minX + ns.maxX) / 2, (ew.minZ + ew.maxZ) / 2);
        if (def == null) { return null; }
        int otherCore = BeardRoads.roadCore(alongX ? ns : ew, !alongX);
        boolean beforeArm = (found & (alongX ? 1 : 4)) != 0;
        boolean afterArm = (found & (alongX ? 2 : 8)) != 0;
        if ((row <= otherCenter - otherCore - 1 && beforeArm) || (row >= otherCenter + otherCore + 1 && afterArm)) {
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, otherCenter - otherCore - 1, otherCenter + otherCore + 1, road0);
            if (fromMouth != null) { return fromMouth; }
        }
        return cornerCell(def, row, across, acrossCenter, core, otherCenter, otherCore, road0);
    }

    @Nullable private static IBlockState plazaMouth(World world, StructureBoundingBox box, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        int reach = ContentBeard.plazaReach();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(ContentBeard.components())) {
            if (acrossCenter + core < (alongX ? well.minZ : well.minX) || acrossCenter - core > (alongX ? well.maxZ : well.maxX)) { continue; }
            int before = (alongX ? well.minX : well.minZ) - reach - 1;
            int after = (alongX ? well.maxX : well.maxZ) + reach + 1;
            if (row > before && row < after) { continue; }
            PathIntersectDef def = ContentPathIntersects.forJunction(world, (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2);
            if (def == null) { return null; }
            if (def.mouth.length == 0 || row <= before - def.mouth.length || row >= after + def.mouth.length) { continue; }
            StructureBoundingBox square = new StructureBoundingBox(well.minX - reach, well.minY, well.minZ - reach, well.maxX + reach, well.maxY, well.maxZ + reach);
            List<StructureBoundingBox> radial = new ArrayList<>();
            for (StructureComponent other : BeardRoads.villagePieces(world)) {
                if (!(other instanceof StructureVillagePieces.Path)) { continue; }
                StructureBoundingBox held = other.getBoundingBox();
                if (held != box && held.intersectsWith(square.minX - 1, square.minZ - 1, square.maxX + 1, square.maxZ + 1)) { radial.add(held); }
            }
            if (Integer.bitCount(arms(square, square, box, radial)) < 3) { continue; }
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, before, after, path);
            if (fromMouth != null) { return fromMouth; }
        }
        return null;
    }

    @Nullable public static IBlockState mouthCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int before, int after, IBlockState path) {
        if (def.mouth.length == 0) { return null; }
        int line = -1;
        if (row <= before && row > before - def.mouth.length) { line = before - row; }
        if (row >= after && row < after + def.mouth.length) { line = row - after; }
        if (line < 0) { return null; }
        String cells = def.mouth[line];
        if (cells.isEmpty()) { return null; }
        return cellState(def, cells.charAt(Math.floorMod(across - (acrossCenter - core), cells.length())), path);
    }

    @Nullable public static IBlockState cornerCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int otherCenter, int otherCore, IBlockState path) {
        if (def.corner.length == 0 || row < otherCenter - otherCore || row > otherCenter + otherCore) { return null; }
        int fromRowEdge = Math.min(row - (otherCenter - otherCore), (otherCenter + otherCore) - row);
        int fromColEdge = core - Math.abs(across - acrossCenter);
        if (fromRowEdge >= def.corner.length) { return null; }
        String cells = def.corner[fromRowEdge];
        if (fromColEdge >= cells.length()) { return null; }
        return cellState(def, cells.charAt(fromColEdge), path);
    }

    @Nullable public static IBlockState cellState(PathIntersectDef def, char cell, IBlockState path) {
        if (cell == '.') { return null; }
        if (cell == 'r' || cell == 'c') { return path; }
        if (cell == 'l') { return BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path); }
        if (cell == 's') { return BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return def.legend.getOrDefault(cell, path);
    }
}
