package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityMaps;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IDrawnRoad;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenVillageStart;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import com.google.gson.JsonObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class CityLayout {
    private static final Rotation[] TURNS = Rotation.values();
    private static final int WELL = 6;
    private static final int ALLEY = 3;
    private static final int ENDS_HINTED = 1;
    private static final int LOW_END = 2;
    private static final int HIGH_END = 4;
    private static final Set<String> MISSING = new HashSet<>();
    private static boolean laying;
    private static boolean drawn;

    private CityLayout() {}

    public static boolean laying() { return laying; }

    public static void laying(boolean now) { laying = now; }

    public static boolean drawn() { return drawn; }

    public static String named() { return ContentControl.text(ContentControl.VILLAGES, "villageLayout", Config.worldgen.villageLayout).trim(); }

    public static boolean wanted() { return !named().isEmpty(); }

    public static boolean lay(StructureStart held, World world, Random rand) {
        drawn = false;
        String name = named();
        if (name.isEmpty()) { return false; }
        CityMapDef def = ContentCityMaps.byName(name);
        if (def == null) {
            if (MISSING.add(name)) { ContentLog.LOGGER.error("villageLayout names city map '{}', which no pack provides, so the village grows as usual", name); }
            return false;
        }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return false; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        StructureBoundingBox well = startPiece.getBoundingBox();
        Rotation turn = TURNS[(int) (MathUtil.mix(world.getSeed(), well.minX, 7, well.minZ) & 3L)];
        CityMapDef.Kind[][] grid = def.turned(turn);
        char[][] marks = def.turnedMarks(turn);
        int deep = grid.length;
        int wide = grid[0].length;
        int anchorX = wide / 2;
        int anchorZ = deep / 2;
        boolean found = false;
        for (int z = 0; z < deep && !found; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] == CityMapDef.Kind.PLAZA) {
                    anchorX = x;
                    anchorZ = z;
                    found = true;
                    break;
                }
            }
        }
        int cell = def.cell;
        int inset = (cell - WELL) / 2;
        int originX = well.minX - anchorX * cell - inset;
        int originZ = well.minZ - anchorZ * cell - inset;
        int roadY = well.maxY - 4;
        int streets = 0;
        int alleys = 0;
        int plazas = 0;
        int plots = 0;
        boolean grows = false;
        boolean hinted = def.bulbHinted();
        Lifts lifts = liftGroups(grid, marks, def, originX, originZ);
        boolean[][] alongRow = new boolean[deep][wide];
        boolean[][] alongColumn = new boolean[deep][wide];
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; ) {
                if (!carries(grid[z][x])) {
                    x++;
                    continue;
                }
                int end = x;
                while (end + 1 < wide && carries(grid[z][end + 1])) { end++; }
                boolean anyStreet = false;
                for (int i = x; i <= end; i++) { anyStreet |= streetLike(grid[z][i]); }
                boolean stub = end == x && streetLike(grid[z][x]) && !carries(z > 0 ? grid[z - 1][x] : CityMapDef.Kind.OPEN) && !carries(z + 1 < deep ? grid[z + 1][x] : CityMapDef.Kind.OPEN);
                if (anyStreet && (end > x || stub)) {
                    JsonObject keys = keysAlong(def, grid, marks, z, x, z, end);
                    StructureComponent road = road(components, startPiece, rand, originX + x * cell, originX + (end + 1) * cell - 1, originZ + z * cell + cell / 2, roadY, BeardRoads.fullWidthFor(keys), true);
                    drawnRoad(road, hinted ? ends(grid[z][x], grid[z][end]) : 0, lifts.along(z, x, z, end), keys);
                    for (int i = x; i <= end; i++) { alongRow[z][i] = true; }
                    streets++;
                }
                x = end + 1;
            }
        }
        for (int x = 0; x < wide; x++) {
            for (int z = 0; z < deep; ) {
                if (!carries(grid[z][x])) {
                    z++;
                    continue;
                }
                int end = z;
                while (end + 1 < deep && carries(grid[end + 1][x])) { end++; }
                boolean anyStreet = false;
                for (int i = z; i <= end; i++) { anyStreet |= streetLike(grid[i][x]); }
                if (anyStreet && end > z) {
                    JsonObject keys = keysAlong(def, grid, marks, z, x, end, x);
                    StructureComponent road = road(components, startPiece, rand, originZ + z * cell, originZ + (end + 1) * cell - 1, originX + x * cell + cell / 2, roadY, BeardRoads.fullWidthFor(keys), false);
                    drawnRoad(road, hinted ? ends(grid[z][x], grid[end][x]) : 0, lifts.along(z, x, end, x), keys);
                    for (int i = z; i <= end; i++) { alongColumn[i][x] = true; }
                    streets++;
                }
                z = end + 1;
            }
        }
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.JUNCTION) { continue; }
                if (!alongRow[z][x]) {
                    JsonObject keys = def.keysOf(marks[z][x]);
                    StructureComponent road = road(components, startPiece, rand, originX + x * cell, originX + (x + 1) * cell - 1, originZ + z * cell + cell / 2, roadY, BeardRoads.fullWidthFor(keys), true);
                    drawnRoad(road, hinted ? ENDS_HINTED : 0, lifts.along(z, x, z, x), keys);
                    streets++;
                }
                if (!alongColumn[z][x]) {
                    JsonObject keys = def.keysOf(marks[z][x]);
                    StructureComponent road = road(components, startPiece, rand, originZ + z * cell, originZ + (z + 1) * cell - 1, originX + x * cell + cell / 2, roadY, BeardRoads.fullWidthFor(keys), false);
                    drawnRoad(road, hinted ? ENDS_HINTED : 0, lifts.along(z, x, z, x), keys);
                    streets++;
                }
            }
        }
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; ) {
                if (grid[z][x] != CityMapDef.Kind.ALLEY) {
                    x++;
                    continue;
                }
                int end = x;
                while (end + 1 < wide && grid[z][end + 1] == CityMapDef.Kind.ALLEY) { end++; }
                boolean vertical = end == x && ((z > 0 && grid[z - 1][x] == CityMapDef.Kind.ALLEY) || (z + 1 < deep && grid[z + 1][x] == CityMapDef.Kind.ALLEY));
                if (!vertical) {
                    road(components, startPiece, rand, originX + x * cell, originX + (end + 1) * cell - 1, originZ + z * cell + cell / 2, roadY, ALLEY, true);
                    alleys++;
                }
                x = end + 1;
            }
        }
        for (int x = 0; x < wide; x++) {
            for (int z = 0; z < deep; ) {
                if (grid[z][x] != CityMapDef.Kind.ALLEY) {
                    z++;
                    continue;
                }
                int end = z;
                while (end + 1 < deep && grid[end + 1][x] == CityMapDef.Kind.ALLEY) { end++; }
                if (end > z) {
                    road(components, startPiece, rand, originZ + z * cell, originZ + (end + 1) * cell - 1, originX + x * cell + cell / 2, roadY, ALLEY, false);
                    alleys++;
                }
                z = end + 1;
            }
        }
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.PLAZA || (x == anchorX && z == anchorZ)) { continue; }
                StructureVillagePieces.Well plaza = new StructureVillagePieces.Well(startPiece, 0, rand, originX + x * cell + inset, originZ + z * cell + inset);
                plaza.getBoundingBox().offset(0, well.minY - plaza.getBoundingBox().minY, 0);
                components.add(plaza);
                plazas++;
            }
        }
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                CityMapDef.Kind kind = grid[z][x];
                if (kind == CityMapDef.Kind.GROW) { grows = true; }
                if (kind != CityMapDef.Kind.PLOT) { continue; }
                CityMapDef.Cell mark = def.palette.get(marks[z][x]);
                if (mark == null) { continue; }
                int cellX = originX + x * cell;
                int cellZ = originZ + z * cell;
                String picked = PickDef.pick(mark.picks, SeededRandom.at(world, cellX, cellZ), "");
                VillageDef plot = ContentVillages.byName(picked);
                if (plot == null) {
                    if (MISSING.add(picked)) { ContentLog.LOGGER.error("City map {} names plot '{}', which no pack provides, leaving that cell open", def.key, picked); }
                    continue;
                }
                EnumFacing facing = facingFor(grid, x, z);
                BlockPos size = ContentVillages.plotSize(plot);
                StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(0, roadY, 0, 0, 0, 0, size.getX(), size.getY(), size.getZ(), facing);
                box.offset(cellX + cell / 2 - (box.minX + box.maxX) / 2, 0, cellZ + cell / 2 - (box.minZ + box.maxZ) / 2);
                if (BeardPlots.collides(components, box)) {
                    ContentLog.LOGGER.debug("City map {} cell {}, {} plot {} would overlap what is already laid, so the cell stays open", def.key, x, z, picked);
                    continue;
                }
                components.add(new ContentVillagePiece(startPiece, 0, box, facing, plot));
                plots++;
            }
        }
        if (held instanceof MapGenVillage.Start && streets + alleys + plazas + plots > 0) { ((IMapGenVillageStart) held).rdpl$setSizeable(true); }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("City map {} laid the village at {}, {} turned {}: {} street(s), {} alley(s), {} plaza(s) besides the start, {} plot(s), {} raised stretch(es){}", def.key, well.minX, well.minZ, turn, streets, alleys, plazas, plots, lifts.boxes.length, grows ? ", with cells left to grow" : "");
        drawn = !grows;
        return drawn;
    }

    private static final class Lifts {
        private final int[][] group;
        private final int[][] boxes;

        private Lifts(int[][] group, int[][] boxes) {
            this.group = group;
            this.boxes = boxes;
        }

        private int[] along(int fromZ, int fromX, int toZ, int toX) {
            Set<Integer> touched = new LinkedHashSet<>();
            for (int z = fromZ; z <= toZ; z++) {
                for (int x = fromX; x <= toX; x++) {
                    if (group[z][x] > 0) { touched.add(group[z][x]); }
                }
            }
            int[] packed = new int[touched.size() * 5];
            int at = 0;
            for (int id : touched) {
                System.arraycopy(boxes[id - 1], 0, packed, at, 5);
                at += 5;
            }
            return packed;
        }
    }

    private static Lifts liftGroups(CityMapDef.Kind[][] grid, char[][] marks, CityMapDef def, int originX, int originZ) {
        int deep = grid.length;
        int wide = grid[0].length;
        int[][] group = new int[deep][wide];
        List<int[]> boxes = new ArrayList<>();
        int[][] steps = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.ELEVATED || group[z][x] != 0) { continue; }
                int id = boxes.size() + 1;
                int[] box = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 0 };
                Deque<int[]> open = new ArrayDeque<>();
                open.push(new int[] { x, z });
                group[z][x] = id;
                while (!open.isEmpty()) {
                    int[] at = open.pop();
                    box[0] = Math.min(box[0], originX + at[0] * def.cell);
                    box[1] = Math.min(box[1], originZ + at[1] * def.cell);
                    box[2] = Math.max(box[2], originX + (at[0] + 1) * def.cell - 1);
                    box[3] = Math.max(box[3], originZ + (at[1] + 1) * def.cell - 1);
                    box[4] = Math.max(box[4], def.heightOf(marks[at[1]][at[0]]));
                    for (int[] step : steps) {
                        int nx = at[0] + step[0];
                        int nz = at[1] + step[1];
                        if (nx < 0 || nz < 0 || nx >= wide || nz >= deep || grid[nz][nx] != CityMapDef.Kind.ELEVATED || group[nz][nx] != 0) { continue; }
                        group[nz][nx] = id;
                        open.push(new int[] { nx, nz });
                    }
                }
                boxes.add(box);
            }
        }
        return new Lifts(group, boxes.toArray(new int[0][]));
    }

    private static int ends(CityMapDef.Kind low, CityMapDef.Kind high) { return ENDS_HINTED | (low == CityMapDef.Kind.BULB ? LOW_END : 0) | (high == CityMapDef.Kind.BULB ? HIGH_END : 0); }

    @Nullable private static JsonObject keysAlong(CityMapDef def, CityMapDef.Kind[][] grid, char[][] marks, int fromZ, int fromX, int toZ, int toX) {
        boolean alongX = fromZ == toZ;
        for (int z = fromZ; z <= toZ; z++) {
            for (int x = fromX; x <= toX; x++) {
                if (crossedAt(grid, x, z, alongX)) { continue; }
                JsonObject keys = def.keysOf(marks[z][x]);
                if (keys != null) { return keys; }
            }
        }
        return null;
    }

    private static boolean crossedAt(CityMapDef.Kind[][] grid, int x, int z, boolean alongX) {
        if (alongX) { return z > 0 && carries(grid[z - 1][x]) || z + 1 < grid.length && carries(grid[z + 1][x]); }
        return x > 0 && carries(grid[z][x - 1]) || x + 1 < grid[0].length && carries(grid[z][x + 1]);
    }

    private static void drawnRoad(StructureComponent road, int bulbEnds, int[] lifts, @Nullable JsonObject keys) {
        if (!(road instanceof IDrawnRoad)) { return; }
        if (keys != null) { ((IDrawnRoad) road).rdpl$keys(keys); }
        ((IDrawnRoad) road).rdpl$bulbEnds(bulbEnds);
        ((IDrawnRoad) road).rdpl$lifts(lifts);
    }

    public static boolean skipsBulb(StructureComponent road, boolean highEnd, Random roll) {
        int hints = road instanceof IDrawnRoad ? ((IDrawnRoad) road).rdpl$bulbEnds() : 0;
        if ((hints & ENDS_HINTED) == 0) { return roll.nextInt(4) == 3; }
        return (hints & (highEnd ? HIGH_END : LOW_END)) == 0;
    }

    public static int lift(World world, StructureComponent road, boolean alongX, int rowLeast, int acrossLeast, int acrossMost, int[] profile, boolean[] bridged, boolean[] held) {
        int[] lifts = road instanceof IDrawnRoad ? ((IDrawnRoad) road).rdpl$lifts() : new int[0];
        int lifted = 0;
        int across = (acrossLeast + acrossMost) / 2;
        for (int g = 0; g + 4 < lifts.length; g += 5) {
            if (alongX ? across < lifts[g + 1] || across > lifts[g + 3] : across < lifts[g] || across > lifts[g + 2]) { continue; }
            int[] ground = BeardGrade.noiseProfile(world, true, lifts[g], lifts[g + 2], lifts[g + 1], lifts[g + 3]);
            if (ground == null) { continue; }
            int highest = world.getSeaLevel();
            for (int level : ground) { highest = Math.max(highest, level); }
            int deck = highest + lifts[g + 4];
            int from = (alongX ? lifts[g] : lifts[g + 1]) - rowLeast;
            int to = (alongX ? lifts[g + 2] : lifts[g + 3]) - rowLeast;
            int pinned = pinnedWithin(held, from - lifts[g + 4], to + lifts[g + 4]);
            if (pinned >= 0) {
                ContentLog.LOGGER.debug("The raised stretch from {}, {} to {}, {} would lift row {} of the road at {}, {}, which a railway or a well holds at its own level, so the road stays at grade there", lifts[g], lifts[g + 1], lifts[g + 2], lifts[g + 3], rowLeast + pinned, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast);
                continue;
            }
            for (int i = 0; i < profile.length; i++) {
                if (i >= from && i <= to) {
                    profile[i] = deck;
                    bridged[i] = true;
                    lifted++;
                    continue;
                }
                int ramp = deck - (i < from ? from - i : i - to);
                if (profile[i] != Integer.MIN_VALUE && profile[i] < ramp) { profile[i] = ramp; }
            }
        }
        return lifted;
    }

    private static int pinnedWithin(boolean[] held, int from, int to) {
        for (int i = Math.max(0, from); i <= Math.min(held.length - 1, to); i++) {
            if (held[i]) { return i; }
        }
        return -1;
    }

    private static boolean carries(CityMapDef.Kind kind) { return kind == CityMapDef.Kind.PLAZA || streetLike(kind); }

    private static boolean streetLike(CityMapDef.Kind kind) { return kind == CityMapDef.Kind.STREET || kind == CityMapDef.Kind.JUNCTION || kind == CityMapDef.Kind.BULB || kind == CityMapDef.Kind.ELEVATED; }

    private static StructureComponent road(List<StructureComponent> components, StructureVillagePieces.Start startPiece, Random rand, int from, int to, int center, int y, int width, boolean alongX) {
        int half = (width - 1) / 2;
        StructureBoundingBox box = alongX
                ? new StructureBoundingBox(from, y, center - half, to, y + 2, center + half)
                : new StructureBoundingBox(center - half, y, from, center + half, y + 2, to);
        StructureVillagePieces.Path road = new StructureVillagePieces.Path(startPiece, 0, rand, box, alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
        components.add(road);
        return road;
    }

    private static EnumFacing facingFor(CityMapDef.Kind[][] grid, int x, int z) {
        int deep = grid.length;
        int wide = grid[0].length;
        if (z > 0 && carries(grid[z - 1][x])) { return EnumFacing.SOUTH; }
        if (z + 1 < deep && carries(grid[z + 1][x])) { return EnumFacing.NORTH; }
        if (x + 1 < wide && carries(grid[z][x + 1])) { return EnumFacing.WEST; }
        if (x > 0 && carries(grid[z][x - 1])) { return EnumFacing.EAST; }
        if (z > 0 && grid[z - 1][x] == CityMapDef.Kind.ALLEY) { return EnumFacing.SOUTH; }
        if (z + 1 < deep && grid[z + 1][x] == CityMapDef.Kind.ALLEY) { return EnumFacing.NORTH; }
        if (x + 1 < wide && grid[z][x + 1] == CityMapDef.Kind.ALLEY) { return EnumFacing.WEST; }
        return EnumFacing.EAST;
    }
}
