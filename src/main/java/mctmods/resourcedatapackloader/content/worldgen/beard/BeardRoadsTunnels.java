package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsTunnels {
    private BeardRoadsTunnels() {}

    static final int BORE = 4;
    private static final Map<World, Map<StructureBoundingBox, boolean[]>> HILLS = new WeakHashMap<>();

    public static boolean crossesHill(@Nullable List<StructureComponent> own, StructureBoundingBox box) {
        if (tunnelDepth() <= 0 || ContentBeard.samplerWorld == null) { return false; }
        for (StructureComponent other : ContentBeard.everyone(own)) {
            if (other instanceof StructureVillagePieces.Path && !CityGrowth.bulbWide(other) && frontsHill(other, box)) { return true; }
        }
        return false;
    }

    public static boolean frontsHill(@Nullable StructureComponent road, StructureBoundingBox plot) {
        World world = ContentBeard.samplerWorld;
        if (world == null || !(road instanceof StructureVillagePieces.Path) || tunnelDepth() <= 0) { return false; }
        StructureBoundingBox box = road.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(box);
        if (apart(box, plot, alongX)) { return false; }
        EnumFacing facing = road.getCoordBaseMode();
        boolean[] hill = HILLS.computeIfAbsent(world, held -> new HashMap<>()).computeIfAbsent(box, held -> hillRows(world, held, alongX, facing));
        int start = alongX ? box.minX : box.minZ;
        for (int row = Math.max(start, (alongX ? plot.minX : plot.minZ) - 1); row <= Math.min(start + hill.length - 1, (alongX ? plot.maxX : plot.maxZ) + 1); row++) {
            if (hill[row - start]) { return true; }
        }
        return false;
    }

    private static boolean apart(StructureBoundingBox box, StructureBoundingBox plot, boolean alongX) {
        return (alongX ? plot.minZ : plot.minX) > (alongX ? box.maxZ : box.maxX) + 3 || (alongX ? plot.maxZ : plot.maxX) < (alongX ? box.minZ : box.minX) - 3;
    }

    public static void standOff(World world, StructureStart start) {
        if (tunnelDepth() <= 0) { return; }
        List<StructureComponent> components = start.getComponents();
        List<StructureComponent> streets = new ArrayList<>();
        for (StructureComponent other : ContentBeard.everyone(world, components)) {
            if (other instanceof StructureVillagePieces.Path && other instanceof IRoadLayout && !CityGrowth.bulbWide(other)) { streets.add(other); }
        }
        for (StructureComponent plot : components.toArray(new StructureComponent[0])) {
            if (plot instanceof StructureVillagePieces.Road || plot instanceof StructureVillagePieces.Well || BeardRails.isRail(plot)) { continue; }
            StructureBoundingBox met = plot.getBoundingBox();
            StructureBoundingBox beside = null;
            for (StructureComponent road : streets) {
                BeardRoads.Grade grade = ((IRoadLayout) road).rdpl$layout();
                if (grade == null) { continue; }
                StructureBoundingBox street = road.getBoundingBox();
                boolean alongX = BeardPlots.roadAlongX(street);
                if (apart(street, met, alongX)) { continue; }
                String fronted = tunnelRows(grade, (alongX ? met.minX : met.minZ) - 1, (alongX ? met.maxX : met.maxZ) + 1);
                if (!fronted.isEmpty()) {
                    components.remove(plot);
                    ContentLog.LOGGER.debug("{} at {}, {}, {} to {}, {}, {} makes way for the street tunnel of the road at {}, {}: the road is roofed over the row(s){} its frontage stands on, so those rows are buried and offer it no way out", plot.getClass().getSimpleName(), met.minX, met.minY, met.minZ, met.maxX, met.maxY, met.maxZ, street.minX, street.minZ, fronted);
                    beside = null;
                    break;
                }
                if (beside == null && ContentLog.LOGGER.debugEnabled() && !tunnelRows(grade, grade.start(), grade.start() + grade.rows() - 1).isEmpty()) { beside = street; }
            }
            if (beside != null) { ContentLog.LOGGER.debug("{} at {}, {}, {} to {}, {}, {} stands beside the tunneled road at {}, {} and is kept: none of the road's roofed rows lie on its frontage", plot.getClass().getSimpleName(), met.minX, met.minY, met.minZ, met.maxX, met.maxY, met.maxZ, beside.minX, beside.minZ); }
        }
    }

    private static String tunnelRows(BeardRoads.Grade grade, int least, int most) {
        StringBuilder rows = new StringBuilder();
        for (int row = Math.max(grade.start(), least); row <= Math.min(grade.start() + grade.rows() - 1, most); row++) {
            if (tunnelAt(grade, row)) { rows.append(' ').append(row); }
        }
        return rows.toString();
    }

    private static boolean[] hillRows(World world, StructureBoundingBox box, boolean alongX, @Nullable EnumFacing facing) {
        int rowLeast = alongX ? box.minX : box.minZ;
        int rowMost = alongX ? box.maxX : box.maxZ;
        int[] profile = BeardGrade.noiseProfile(world, alongX, rowLeast, rowMost, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX);
        boolean[] hill = new boolean[rowMost - rowLeast + 1];
        if (profile == null) { return hill; }
        int[] ground = profile.clone();
        BeardGrade.flatRuns(world, alongX, rowLeast, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, profile);
        boolean[] bridged = BeardGrade.smooth(profile);
        boolean[] none = new boolean[profile.length];
        BeardGrade.settle(profile, none);
        BeardGrade.bore(profile, ground, none, bridged, tunnelDepth(), BeardRoadsGrade.farLow(facing, alongX), BeardRoadsGrade.farHigh(facing, alongX));
        BeardRoads.Grade grade = new BeardRoads.Grade(profile, ground, bridged, none, rowLeast, 0);
        int depth = tunnelDepth();
        for (int i = 0; i < hill.length; i++) { hill[i] = grade.tunneledAt(rowLeast + i, depth); }
        return hill;
    }

    private static boolean standsOn(List<StructureComponent> own, StructureBoundingBox strip) {
        for (StructureComponent other : own) {
            if (other.getBoundingBox().intersectsWith(strip.minX, strip.minZ, strip.maxX, strip.maxZ)) { return true; }
        }
        return false;
    }

    public static int throughRoom(List<StructureComponent> own, StructureBoundingBox box, EnumFacing facing) {
        if (tunnelDepth() <= 0) { return 0; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int room = 0;
        for (int extra = 7; extra <= BeardGrade.TUNNEL_REACH; extra += 7) {
            StructureBoundingBox longer = new StructureBoundingBox(box);
            BeardLayout.trim(longer, alongX, facing, rows + extra);
            StructureBoundingBox added = new StructureBoundingBox(longer);
            if (alongX && step > 0) { added.minX = box.maxX + 1; }
            else if (alongX) { added.maxX = box.minX - 1; }
            else if (step > 0) { added.minZ = box.maxZ + 1; }
            else { added.maxZ = box.minZ - 1; }
            if (standsOn(own, added) || ContentBeard.roomFor(own, longer, facing) < rows + extra) { break; }
            room = extra;
        }
        return room;
    }

    static int tunnelWall(World world, StructureComponent piece, BlockPos.MutableBlockPos at, int x, int z, int level, BeardRoads.Palette linings) {
        boolean buried = BeardRails.buried(piece);
        if (!buried) {
            if (BeardRoads.insidePlaza(x, z)) { return 0; }
            StructureStart holder = ContentBeard.current();
            if (holder != null && BeardPlots.underAnother(holder, piece, x, z)) { return 0; }
        }
        int laid = 0;
        for (int y = level; y <= level + BORE + 1; y++) {
            IBlockState lining = linings.pick(world, x, y, z);
            if (buried) { laid += boreCell(world, at, x, z, y, lining); }
            else { laid += roofCell(world, at, x, z, y, lining); }
        }
        return laid;
    }

    static void dropShortRuns(boolean[] tunnels) {
        int i = 0;
        while (i < tunnels.length) {
            if (!tunnels[i]) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < tunnels.length && tunnels[end + 1]) { end++; }
            if (end - i + 1 < BeardGrade.TUNNEL_LEAST) { Arrays.fill(tunnels, i, end + 1, false); }
            i = end + 1;
        }
    }

    static boolean[] tunnelLights(boolean[] tunnels, int start, int run) {
        boolean[] lit = new boolean[tunnels.length];
        int i = 0;
        while (i < tunnels.length) {
            if (!tunnels[i]) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < tunnels.length && tunnels[end + 1]) { end++; }
            boolean any = false;
            for (int k = i; k <= end; k++) {
                if (Math.floorMod(start + k, run) != 0) { continue; }
                lit[k] = true;
                any = true;
            }
            if (!any) { lit[(i + end) / 2] = true; }
            i = end + 1;
        }
        return lit;
    }

    static int roofCell(World world, BlockPos.MutableBlockPos at, int x, int z, int y, IBlockState block) {
        if (BeardKeep.holds(x, y, z)) { return 0; }
        return boreCell(world, at, x, z, y, block);
    }

    static int boreCell(World world, BlockPos.MutableBlockPos at, int x, int z, int y, IBlockState block) {
        at.setPos(x, y, z);
        world.setBlockState(at, block, 2);
        return 1;
    }

    static boolean uncrossedRow(List<StructureBoundingBox> crossed, boolean alongX, int row) {
        for (StructureBoundingBox other : crossed) {
            if (row >= (alongX ? other.minX : other.minZ) - 1 && row <= (alongX ? other.maxX : other.maxZ) + 1) { return false; }
        }
        return true;
    }

    public static int tunnelDepth() {
        if (tunnelBlock().getBlock() == Blocks.AIR) { return 0; }
        return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathTunnelDepth", Config.worldgen.villagePathTunnelDepth));
    }

    private static IBlockState tunnelBlock() { return BeardRoads.pathBlock("villagePathTunnelBlock", Config.worldgen.villagePathTunnelBlock, Blocks.AIR.getDefaultState()); }

    static BeardRoads.Palette tunnelPalette() { return BeardRoads.pathPalette("villagePathTunnelBlock", Config.worldgen.villagePathTunnelBlock, Blocks.AIR.getDefaultState()); }

    public static boolean tunnelAt(@Nullable BeardRoads.Grade grade, int row) { return grade != null && grade.tunneledAt(row, tunnelDepth()); }

    public static List<StructureComponent> tunnels(World world, StructureBoundingBox near) {
        List<StructureComponent> found = new ArrayList<>();
        if (!ContentBeard.wanted() || tunnelDepth() <= 0) { return found; }
        for (StructureComponent piece : BeardRoads.villagePieces(world)) {
            if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof IRoadLayout) || ((IRoadLayout) piece).rdpl$layout() == null) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (box.maxX < near.minX || box.minX > near.maxX || box.maxZ < near.minZ || box.minZ > near.maxZ) { continue; }
            found.add(piece);
        }
        return found;
    }

    public static boolean runsIntoTunnel(List<StructureComponent> roads, int x, int y, int z) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }
            BeardRoads.Grade grade = ((IRoadLayout) road).rdpl$layout();
            int row = BeardPlots.roadAlongX(road) ? x : z;
            if (grade != null && grade.tunneledAt(row, tunnelDepth()) && y >= grade.at(row) - 1) { return true; }
        }
        return false;
    }
}
