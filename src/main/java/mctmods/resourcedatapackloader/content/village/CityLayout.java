package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityMaps;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenVillageStart;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class CityLayout {
    private static final Rotation[] TURNS = { Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90 };
    private static final int WELL = 6;
    private static final int ALLEY = 3;
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
        int width = BeardRoads.pathFullWidth();
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; ) {
                if (!carries(grid[z][x])) {
                    x++;
                    continue;
                }
                int end = x;
                while (end + 1 < wide && carries(grid[z][end + 1])) { end++; }
                boolean anyStreet = false;
                for (int i = x; i <= end; i++) { anyStreet |= grid[z][i] == CityMapDef.Kind.STREET; }
                boolean stub = end == x && grid[z][x] == CityMapDef.Kind.STREET && !carries(z > 0 ? grid[z - 1][x] : CityMapDef.Kind.OPEN) && !carries(z + 1 < deep ? grid[z + 1][x] : CityMapDef.Kind.OPEN);
                if (anyStreet && (end > x || stub)) {
                    road(components, startPiece, rand, originX + x * cell, originX + (end + 1) * cell - 1, originZ + z * cell + cell / 2, roadY, width, true);
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
                for (int i = z; i <= end; i++) { anyStreet |= grid[i][x] == CityMapDef.Kind.STREET; }
                if (anyStreet && end > z) {
                    road(components, startPiece, rand, originZ + z * cell, originZ + (end + 1) * cell - 1, originX + x * cell + cell / 2, roadY, width, false);
                    streets++;
                }
                z = end + 1;
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
        ContentLog.LOGGER.debug("City map {} laid the village at {}, {} turned {}: {} street(s), {} alley(s), {} plaza(s) besides the start, {} plot(s){}", def.key, well.minX, well.minZ, turn, streets, alleys, plazas, plots, grows ? ", with cells left to grow" : "");
        drawn = !grows;
        return drawn;
    }

    private static boolean carries(CityMapDef.Kind kind) { return kind == CityMapDef.Kind.STREET || kind == CityMapDef.Kind.PLAZA; }

    private static void road(List<StructureComponent> components, StructureVillagePieces.Start startPiece, Random rand, int from, int to, int center, int y, int width, boolean alongX) {
        int half = (width - 1) / 2;
        StructureBoundingBox box = alongX
                ? new StructureBoundingBox(from, y, center - half, to, y + 2, center + half)
                : new StructureBoundingBox(center - half, y, from, center + half, y + 2, to);
        components.add(new StructureVillagePieces.Path(startPiece, 0, rand, box, alongX ? EnumFacing.EAST : EnumFacing.SOUTH));
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
