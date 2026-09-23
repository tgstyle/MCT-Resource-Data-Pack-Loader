package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.ContentPierCargo;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class BeardRoadsDecks {
    private BeardRoadsDecks() {}

    private static final int PILING_REACH = 24;
    private static final int CORNER_REACH = 3;

    public static final class Pier {
        static final String[] NAMES = { "railed", "pilings", "boardwalk" };
        final int from;
        final int to;
        final boolean outward;
        final int style;

        Pier(int from, int to, boolean outward, int style) {
            this.from = from;
            this.to = to;
            this.outward = outward;
            this.style = style;
        }

        static int styleOf(String name) {
            for (int i = 0; i < NAMES.length; i++) { if (NAMES[i].equals(name)) { return i; } }
            return -1;
        }

        boolean covers(int row) { return row >= from && row <= to; }

        boolean endRow(int row) { return row == (outward ? to : from); }

        boolean postRow(int row) { return (outward ? to - row : row - from) % 4 == 0; }

        int edgeIn(int span) { return style == 2 ? railEdge(span) : (span - 1) / 2; }
    }

    @Nullable static Pier pierFor(World world, StructureComponent piece, boolean alongX, StructureBoundingBox box, BeardRoads.Grade graded) {
        if (BeardRoads.roadNarrow(box, alongX)) { return null; }
        String[] wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathPiers", Config.worldgen.villagePathPiers);
        if (wanted.length == 0) { return null; }
        List<Integer> styles = new ArrayList<>();
        for (String name : wanted) {
            int style = Pier.styleOf(name);
            if (style >= 0 && !styles.contains(style)) { styles.add(style); }
        }
        if (styles.isEmpty()) { return null; }
        int rows = graded.rows();
        List<StructureComponent> nearby = BeardRoads.villagePieces(world);
        for (int side = 0; side < 2; side++) {
            boolean outward = side == 1;
            int i = outward ? rows - 1 : 0;
            if (graded.profile[i] != Integer.MIN_VALUE && !graded.bridged[i] && graded.ground[i] != Integer.MIN_VALUE) { continue; }
            int end = outward ? graded.start + rows - 1 : graded.start;
            if (blockedAt(nearby, piece, alongX, box, outward ? end + 1 : end - 1)) { continue; }
            int held = 0;
            while (i >= 0 && i < rows && (graded.profile[i] == Integer.MIN_VALUE || graded.bridged[i] || graded.ground[i] == Integer.MIN_VALUE)) {
                if (blockedAt(nearby, piece, alongX, box, graded.start + i)) { break; }
                held++;
                i += outward ? -1 : 1;
            }
            if (held == 0) { continue; }
            int from = outward ? end - held + 1 : end;
            int to = outward ? end : end + held - 1;
            int endX = alongX ? (outward ? box.maxX : box.minX) : (box.minX + box.maxX) / 2;
            int endZ = alongX ? (box.minZ + box.maxZ) / 2 : (outward ? box.maxZ : box.minZ);
            int style = styles.get(SeededRandom.at(world, endX, endZ).nextInt(styles.size()));
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} dead-ends over water, so its {} bridged row(s) from {} to {} become a {} pier", box.minX, box.minZ, held, from, to, Pier.NAMES[style]); }
            return new Pier(from, to, outward, style);
        }
        return null;
    }

    public static void pierOut(World world, StructureStart start) {
        if (!ContentBeard.wanted()) { return; }
        String[] wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathPiers", Config.worldgen.villagePathPiers);
        if (wanted.length == 0 || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> nearby = ContentBeard.everyone(world, start.getComponents());
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            pierOut(world, piece, BeardPlots.roadAlongX(piece), piece.getBoundingBox(), nearby);
        }
    }

    private static void pierOut(World world, StructureComponent piece, boolean alongX, StructureBoundingBox box, List<StructureComponent> nearby) {
        int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        for (int side = 0; side < 2; side++) {
            boolean outward = side == 1;
            int dir = outward ? 1 : -1;
            int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
            int far = alongX ? (outward ? box.minX : box.maxX) : (outward ? box.minZ : box.maxZ);
            int shore = Integer.MIN_VALUE;
            for (int row = end; dir > 0 ? row >= far : row <= far; row -= dir) {
                if (Math.abs(end - row) > 64) { break; }
                if (landAt(world, alongX ? row : center, alongX ? center : row)) {
                    shore = row;
                    break;
                }
            }
            if (shore == Integer.MIN_VALUE) { continue; }
            Random roll = SeededRandom.at(world, alongX ? shore : center, alongX ? center : shore);
            int target = shore + dir * (8 + roll.nextInt(17));
            if (dir > 0 ? target <= end : target >= end) { continue; }
            int grown = end;
            for (int row = end + dir; dir > 0 ? row <= target : row >= target; row += dir) {
                if (blockedAt(nearby, piece, alongX, box, row) || standsAt(nearby, piece, alongX, box, row) || landAt(world, alongX ? row : center, alongX ? center : row)) { break; }
                grown = row;
            }
            if (grown == end || Math.abs(grown - shore) < 8) { continue; }
            if (alongX) { if (outward) { box.maxX = grown; } else { box.minX = grown; } }
            else { if (outward) { box.maxZ = grown; } else { box.minZ = grown; } }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} runs its end out {} row(s) over open water for a pier", box.minX, box.minZ, Math.abs(grown - end)); }
        }
    }

    private static boolean landAt(World world, int x, int z) { return BeardSurface.surfaceAt(world, x, z) >= world.getSeaLevel() - 1; }

    public static boolean frameAt(@Nullable BeardRoads.Grade grade, int row) {
        if (grade == null) { return false; }
        int i = row - grade.start;
        if (i < 0 || i >= grade.profile.length) { return false; }
        return bridgeFrames(grade.profile, grade.bridged)[i];
    }

    private static boolean standsAt(List<StructureComponent> pieces, StructureComponent piece, boolean alongX, StructureBoundingBox box, int row) {
        int minX = alongX ? row : box.minX;
        int maxX = alongX ? row : box.maxX;
        int minZ = alongX ? box.minZ : row;
        int maxZ = alongX ? box.maxZ : row;
        for (StructureComponent other : pieces) {
            if (other != piece && other.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { return true; }
        }
        return false;
    }

    private static boolean blockedAt(List<StructureComponent> pieces, StructureComponent piece, boolean alongX, StructureBoundingBox box, int row) {
        int reach = ContentBeard.plazaReach();
        int minX = alongX ? row : box.minX - 1;
        int maxX = alongX ? row : box.maxX + 1;
        int minZ = alongX ? box.minZ - 1 : row;
        int maxZ = alongX ? box.maxZ + 1 : row;
        for (StructureComponent other : pieces) {
            if (other == piece) { continue; }
            boolean plaza = other instanceof StructureVillagePieces.Well;
            if (!plaza && !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            int pad = plaza ? reach : 0;
            if (held.maxX + pad >= minX && held.minX - pad <= maxX && held.maxZ + pad >= minZ && held.minZ - pad <= maxZ) { return true; }
        }
        return false;
    }

    static boolean onPiling(int row, int across, int acrossLeast, int acrossMost) {
        int middle = (acrossLeast + acrossMost) / 2;
        int edge = railEdge(acrossMost - acrossLeast + 1);
        if (edge < 1) { return true; }
        return Math.floorMod(row, 4) == 0 && Math.abs(across - middle) == edge;
    }

    static boolean[] bridgeFrames(int[] profile, boolean[] bridged) {
        int rows = profile.length;
        boolean[] frames = new boolean[rows];
        IBlockState frame = BeardRoads.pathBlock("villagePathBridgeFrameBlock", Config.worldgen.villagePathBridgeFrameBlock, Blocks.AIR.getDefaultState());
        if (frame.getBlock() == Blocks.AIR) { return frames; }
        int least = Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameLeast", Config.worldgen.villagePathBridgeFrameLeast));
        int run = Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameRun", Config.worldgen.villagePathBridgeFrameRun));
        for (int i = 0; i < rows; i++) { frames[i] = decking(profile, bridged, i); }
        return frameRows(frames, least, run);
    }

    static boolean[] frameRows(boolean[] decking, int least, int run) {
        int rows = decking.length;
        boolean[] frames = new boolean[rows];
        for (int i = 0; i < rows; i++) {
            if (!decking[i]) { continue; }
            int end = i;
            while (end + 1 < rows && decking[end + 1]) { end++; }
            int span = end - i + 1;
            if (span >= least) {
                int count = Math.max(1, span / run);
                int spread = (count - 1) * run;
                int first = i + (span - 1 - spread) / 2;
                for (int at = 0; at < count; at++) {
                    int mark = first + at * run;
                    if (mark >= i && mark <= end) { frames[mark] = true; }
                }
            }
            i = end;
        }
        return frames;
    }

    static int bridgeFrame(World world, StructureComponent piece, boolean alongX, int row, int acrossLeast, int acrossMost, int deckY, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        BeardRoads.Palette posts = BeardRoads.pathPalette("villagePathBridgeFrameBlock", Config.worldgen.villagePathBridgeFrameBlock, Blocks.AIR.getDefaultState());
        if (posts.first().getBlock() == Blocks.AIR) { return 0; }
        BeardRoads.Palette beams = ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeFrameTopBlock", Config.worldgen.villagePathBridgeFrameTopBlock, BeardBiome.building()).trim().isEmpty() ? posts : BeardRoads.pathPalette("villagePathBridgeFrameTopBlock", Config.worldgen.villagePathBridgeFrameTopBlock, posts.first());
        int height = Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameHeight", Config.worldgen.villagePathBridgeFrameHeight));
        StructureStart holder = ContentBeard.current();
        int laid = 0;
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            if (!clip.isVecInside(at.setPos(x, deckY, z))) { continue; }
            if (BeardRoads.insidePlaza(x, z)) { continue; }
            if (holder != null && BeardPlots.underAnother(holder, piece, x, z)) { continue; }
            boolean edge = across == acrossLeast || across == acrossMost;
            if (edge) {
                for (int y = deckY + 1; y <= deckY + height; y++) {
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    at.setPos(x, y, z);
                    world.setBlockState(at, posts.pick(world, x, y, z), 2);
                    laid++;
                }
            }
            if (BeardKeep.holds(x, deckY + height + 1, z)) { continue; }
            at.setPos(x, deckY + height + 1, z);
            world.setBlockState(at, beams.pick(world, x, deckY + height + 1, z), 2);
            laid++;
        }
        return laid;
    }

    static boolean[] bridgeLegs(int[] profile, boolean[] bridged) {
        int rows = profile.length;
        boolean[] legs = new boolean[rows];
        for (int i = 0; i < rows; i++) {
            if (!decking(profile, bridged, i)) { continue; }
            int end = i;
            while (end + 1 < rows && decking(profile, bridged, end + 1)) { end++; }
            for (int at = i; at <= end; at += 4) { legs[at] = true; }
            legs[end] = true;
            i = end;
        }
        return legs;
    }

    private static boolean decking(int[] profile, boolean[] bridged, int i) { return bridged[i] || profile[i] == Integer.MIN_VALUE; }

    private static boolean overDryDrop(World world, BlockPos.MutableBlockPos at, int x, int deckY, int z) {
        for (int y = deckY - 1; y >= deckY - PILING_REACH && y >= 1; y--) {
            IBlockState held = world.getBlockState(at.setPos(x, y, z));
            if (held.getMaterial().isLiquid()) { return false; }
            if (held.getMaterial().isSolid()) { return deckY - y > BeardRoadsPaving.FILL_UNDER; }
        }
        return false;
    }

    static int piling(World world, boolean alongX, int row, int across, int fromY, IBlockState support, BlockPos.MutableBlockPos at) {
        int floor = BeardRails.boreFloor(world, alongX ? row : across, alongX ? across : row);
        int laid = 0;
        for (int y = fromY; y >= fromY - PILING_REACH && y >= floor; y--) {
            at.setPos(alongX ? row : across, y, alongX ? across : row);
            IBlockState held = world.getBlockState(at);
            if (held.getMaterial().isSolid()) { break; }
            world.setBlockState(at, support, 2);
            laid++;
        }
        return laid;
    }

    private static int groundToPlot(World world, BlockPos.MutableBlockPos at, int x, int z, int level, int floor) {
        int laid = 0;
        for (int y = level; y >= level - 8 && y >= floor; y--) {
            at.setPos(x, y, z);
            IBlockState held = world.getBlockState(at);
            if (held.getMaterial().isSolid() && !held.getMaterial().isLiquid()) { break; }
            if (BeardKeep.holds(x, y, z)) { break; }
            world.setBlockState(at, BeardBlocks.fillAt(world, x, y, level, z, false), 2);
            laid++;
        }
        at.setPos(x, level, z);
        return laid;
    }

    private static void openRail(World world, boolean alongX, StructureBoundingBox roadBox, StructureBoundingBox plotBox, int x, int z, int level, BlockPos.MutableBlockPos at) {
        IBlockState barrier = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, Blocks.AIR.getDefaultState());
        if (barrier.getBlock() == Blocks.AIR) { return; }
        int edge = alongX ? (roadBox.maxZ < plotBox.minZ ? roadBox.maxZ : roadBox.minZ) : (roadBox.maxX < plotBox.minX ? roadBox.maxX : roadBox.minX);
        int height = BeardRoads.barrierHeight();
        for (int up = 1; up <= height; up++) {
            at.setPos(alongX ? x : edge, level + up, alongX ? edge : z);
            if (world.getBlockState(at) != barrier) { continue; }
            BeardKeep.letGo(at.getX(), at.getY(), at.getZ());
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        }
    }

    public static int deckToPlot(World world, StructureStart start, StructureComponent plot, StructureBoundingBox plotBox, StructureComponent road, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        StructureBoundingBox roadBox = road.getBoundingBox();
        int[] strip = ContentBeard.facingStrip(plotBox, roadBox, 6);
        if (strip == null) { return 0; }
        boolean alongX = BeardPlots.roadAlongX(roadBox);
        BeardRoads.Grade grade = road instanceof IRoadLayout ? ((IRoadLayout) road).rdpl$layout() : null;
        if (grade == null) { grade = BeardRoadsGrade.chainGrade(world, road, alongX); }
        if (grade == null) { return 0; }
        IBlockState planks = BeardRoads.pathBlock("villagePathBridgeBlock", Config.worldgen.villagePathBridgeBlock, Blocks.PLANKS.getDefaultState());
        int minX = alongX ? strip[0] - CORNER_REACH : strip[0];
        int maxX = alongX ? strip[1] + CORNER_REACH : strip[1];
        int minZ = alongX ? strip[2] : strip[2] - CORNER_REACH;
        int maxZ = alongX ? strip[3] : strip[3] + CORNER_REACH;
        List<RailPiece> bores = BeardRails.subways(world, new StructureBoundingBox(minX, 0, minZ, maxX, 255, maxZ));
        int decked = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (outsideStrip(strip, x, z) && !BeardPlots.besideRoad(start, plot, x, z)) { continue; }
                int row = alongX ? x : z;
                boolean bridged = grade.bridgedAt(row);
                int level = grade.at(row);
                if (level == Integer.MIN_VALUE) { level = grade.deckAt(row); }
                if (level == Integer.MIN_VALUE) { continue; }
                int floor = bores.isEmpty() ? 1 : BeardRails.boreFloor(world, bores, x, z);
                if (level < floor) { continue; }
                at.setPos(x, level, z);
                if (!clip.isVecInside(at) || BeardKeep.holds(x, level, z)) { continue; }
                if (BeardPlots.underRoad(start, plot, x, z) || BeardPlots.insideAnother(start, plot, at)) { continue; }
                IBlockState held = world.getBlockState(at);
                boolean doorstep = held.getBlock() instanceof BlockStairs;
                if (held.getMaterial().isSolid() && !held.getMaterial().isLiquid() && !doorstep) { continue; }
                if (doorstep) { BeardKeep.letGo(x, level, z); }
                boolean standing = grounded(world, at, x, level, z);
                at.setPos(x, level, z);
                if (!bridged || standing) {
                    decked += groundToPlot(world, at, x, z, level, floor);
                    continue;
                }
                world.setBlockState(at, planks, 2);
                BeardKeep.holdSpot(x, level, z);
                decked++;
                openRail(world, alongX, roadBox, plotBox, x, z, level, at);
                for (int up = 1; up <= 2; up++) {
                    at.setPos(x, level + up, z);
                    if (!clip.isVecInside(at) || BeardKeep.holds(x, level + up, z)) { break; }
                    if (!world.getBlockState(at).getMaterial().isLiquid()) { break; }
                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
        return decked;
    }

    private static boolean outsideStrip(int[] strip, int x, int z) { return x < strip[0] || x > strip[1] || z < strip[2] || z > strip[3]; }

    public static int bridge(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox near, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (near == null || Math.abs(box.minY - near.minY) > 2) { return 0; }
        int[] strip = ContentBeard.facingStrip(box, near, 6);
        if (strip == null) { return 0; }
        int fromX = strip[0];
        int toX = strip[1];
        int fromZ = strip[2];
        int toZ = strip[3];
        int cleared = 0;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                if (BeardPlots.underRoad(start, piece, x, z)) { continue; }
                int toBox = Math.max(0, Math.max(box.minX - x, x - box.maxX)) + Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
                int toNear = Math.max(0, Math.max(near.minX - x, x - near.maxX)) + Math.max(0, Math.max(near.minZ - z, z - near.maxZ));
                int base = toBox <= toNear ? box.minY : near.minY;
                int bed = BeardGround.roadTop(world, start, at, x, z, base + 1, base + 12);
                for (int y = bed == Integer.MIN_VALUE ? base + 1 : bed + 1; y <= base + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (BeardBlocks.opening(held.getMaterial()) || BeardBlocks.overhang(held)) { cleared += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        return cleared;
    }

    public static int deckBridge(World world, StructureBoundingBox box, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckAt, IBlockState planks, IBlockState support, BlockPos.MutableBlockPos at, @Nullable Pier dock, List<StructureBoundingBox> crossed, boolean leg, boolean toGround) {
        int deckY = deckAt;
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        for (int lift = 0; lift < 8; lift++) {
            if (!world.getBlockState(at.setPos(x, deckY, z)).getMaterial().isLiquid()) { break; }
            deckY++;
        }
        for (int y = deckY; y <= deckY + 4; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            if (!above.getMaterial().isSolid() || BeardKeep.holds(x, y, z)) { continue; }
            if (!BeardRoads.clearable(above)) { break; }
            BeardBlocks.note(world, at, "Decking the road");
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        }
        at.setPos(x, deckY, z);
        if (world.getBlockState(at).getMaterial().isSolid()) { return 0; }
        IBlockState decked = deckState(world, box, alongX, row, across, acrossLeast, acrossMost, planks, dock, crossed);
        if (decked == null) { return 0; }
        int laid = 0;
        if (bridgeDress(decked, planks)) { laid = deckRail(world, box, alongX, row, across, acrossLeast, acrossMost, deckY, planks, support, at, dock, crossed, leg, toGround); }
        else {
            unrail(world, at, x, deckY + 1, z, planks);
            at.setPos(x, deckY, z);
        }
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return laid; }
        world.setBlockState(at, decked, 2);
        if (dock != null && cargoOn(world, alongX, row, across, acrossLeast, acrossMost, deckY, dock)) { laid++; }
        return laid + 1;
    }

    private static boolean cargoOn(World world, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckY, Pier dock) {
        if (!dock.covers(row) || dock.endRow(row) || Math.floorMod(row, 2) != 0 || !ContentPierCargo.wanted()) { return false; }
        int middle = (acrossLeast + acrossMost) / 2;
        int edge = dock.edgeIn(acrossMost - acrossLeast + 1);
        if (edge < 2 || Math.abs(across - middle) != edge - 1) { return false; }
        boolean near = across < middle;
        EnumFacing facing = alongX ? (near ? EnumFacing.SOUTH : EnumFacing.NORTH) : (near ? EnumFacing.EAST : EnumFacing.WEST);
        return ContentPierCargo.place(world, alongX ? row : across, deckY + 1, alongX ? across : row, facing);
    }

    @Nullable static IBlockState deckState(World world, StructureBoundingBox box, boolean alongX, int row, int across, int acrossLeast, int acrossMost, IBlockState planks, @Nullable Pier dock, List<StructureBoundingBox> crossed) {
        int acrossCenter = (acrossLeast + acrossMost) / 2;
        if (dock != null && dock.covers(row)) {
            if (dock.style == 2) {
                int edge = railEdge(acrossMost - acrossLeast + 1);
                if (Math.abs(across - acrossCenter) > edge) { return null; }
            }
            return planks;
        }
        BeardCross mine = BeardCross.of(box, alongX);
        if (mine.oversize || mine.width < 3 || BeardRoads.pathFullWidth() == 3) { return planks; }
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        if (!mine.alley) {
            IBlockState stamped = BeardRoadsSurface.stampAt(world, box, alongX, row, across, acrossCenter, mine.core, planks, crossed);
            if (stamped != null) { return stamped; }
            IBlockState shared = BeardRoadsSurface.overlapCell(world, box, alongX, row, across, mine.core, planks, planks, crossed);
            if (shared != null) { return shared; }
        }
        List<BeardCross> shapes = BeardRoadsSurface.shapesAt(box, alongX, crossed);
        BeardCross top = BeardCross.winner(shapes, x, z);
        if (top == null) { return planks; }
        int role = top.role(x, z);
        if (role == BeardCross.WALK) {
            IBlockState walk = BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, planks);
            return BeardRoads.pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk);
        }
        if (role == BeardCross.LINE) { return ContentBeard.axised(BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, planks), top.alongX); }
        if (top.alley || top.offMiddle(x, z) || BeardCross.crossedOver(shapes, top.alongX, x, z)) { return planks; }
        IBlockState center = BeardRoads.pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, planks);
        if (center == planks) { return planks; }
        int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
        if (dash > 0 && Math.floorMod(top.row(x, z), dash + 1) == dash) { return planks; }
        return ContentBeard.axised(center, top.alongX);
    }

    static boolean bridgeDress(IBlockState decked, IBlockState planks) {
        return decked == planks || decked == BeardRoads.pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, planks);
    }

    private static boolean grounded(World world, BlockPos.MutableBlockPos at, int x, int y, int z) {
        if (y <= 1) { return false; }
        if (world.getBlockState(at.setPos(x, y - 1, z)).getMaterial().isSolid()) { return true; }
        if (world.isAirBlock(at.setPos(x, y - 1, z)) || y == 2) { return false; }
        return world.getBlockState(at.setPos(x, y - 2, z)).getMaterial().isSolid();
    }

    static void unrail(World world, BlockPos.MutableBlockPos at, int x, int y, int z, IBlockState planks) {
        IBlockState barrier = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
        if (barrier == planks || BeardKeep.holds(x, y, z)) { return; }
        int railing = BeardRoads.barrierHeight();
        int high = 0;
        while (high <= railing && world.getBlockState(at.setPos(x, y + high, z)) == barrier) { high++; }
        if (high > railing) { return; }
        for (int up = 0; up < high; up++) { world.setBlockState(at.setPos(x, y + up, z), Blocks.AIR.getDefaultState(), 2); }
    }

    static int deckRail(World world, StructureBoundingBox box, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckY, IBlockState planks, IBlockState support, BlockPos.MutableBlockPos at, @Nullable Pier dock, List<StructureBoundingBox> crossed, boolean leg, boolean toGround) {
        if (BeardRoads.roadNarrow(box, alongX) && BeardRoadsSurface.squareAt(box, alongX, crossed, alongX ? row : across, alongX ? across : row)) { return 0; }
        int span = acrossMost - acrossLeast + 1;
        int offset = Math.abs(across - (acrossLeast + acrossMost) / 2);
        int cellX = alongX ? row : across;
        int cellZ = alongX ? across : row;
        if (BeardRoadsSurface.squareAt(box, alongX, crossed, cellX, cellZ)) {
            boolean corner = false;
            for (StructureBoundingBox road : crossed) {
                if (BeardPlots.roadAlongX(road) == alongX) { continue; }
                if (!BeardRoadsSurface.inSquare(box, alongX, road, cellX, cellZ)) { continue; }
                StructureBoundingBox ew = alongX ? box : road;
                StructureBoundingBox ns = alongX ? road : box;
                if (!BeardRoadsSurface.deckSquare(world, ew, ns)) { corner = false; break; }
                int centerX = (ns.minX + ns.maxX) / 2;
                int centerZ = (ew.minZ + ew.maxZ) / 2;
                int offX = Math.abs(cellX - centerX);
                int offZ = Math.abs(cellZ - centerZ);
                int core = 1 + BeardRoads.pathExtraWidth();
                int reach = BeardRoadsSurface.arms(ew, ns, box, crossed);
                boolean west = (reach & 1) != 0;
                boolean east = (reach & 2) != 0;
                boolean north = (reach & 4) != 0;
                boolean south = (reach & 8) != 0;
                boolean onEW = offZ <= core && (offX <= core || (cellX < centerX && west) || (cellX > centerX && east));
                boolean onNS = offX <= core && (offZ <= core || (cellZ < centerZ && north) || (cellZ > centerZ && south));
                if (onEW || onNS) { corner = false; break; }
                boolean armX = cellX > centerX ? east : cellX >= centerX || west;
                boolean armZ = cellZ > centerZ ? south : cellZ >= centerZ || north;
                int halfX = (ns.maxX - ns.minX) / 2;
                int halfZ = (ew.maxZ - ew.minZ) / 2;
                if (!armX && offX == halfX) { corner = true; }
                else if (!armZ && offZ == halfZ) { corner = true; }
                else if (armX && armZ && offX == halfX && offZ == halfZ) { corner = true; }
            }
            if (!corner) {
                at.setPos(cellX, deckY, cellZ);
                return 0;
            }
            IBlockState barrier = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
            if (barrier == planks) {
                at.setPos(cellX, deckY, cellZ);
                return 0;
            }
            int laid = raise(world, barrier, cellX, deckY, cellZ, at);
            at.setPos(cellX, deckY, cellZ);
            return laid;
        }
        if (dock != null && dock.covers(row)) {
            int edge = dock.edgeIn(span);
            if (offset > edge) { return 0; }
            int laid = 0;
            boolean rail;
            if (dock.endRow(row)) { rail = true; }
            else if (offset != edge) { rail = false; }
            else { rail = dock.style != 1 || dock.postRow(row); }
            IBlockState barrier = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
            if (rail && barrier != planks) { laid += raise(world, barrier, alongX ? row : across, deckY, alongX ? across : row, at); }
            if (offset == edge && dock.postRow(row)) { laid += piling(world, alongX, row, across, deckY - 1, support, at); }
            at.setPos(alongX ? row : across, deckY, alongX ? across : row);
            return laid;
        }
        if (span <= 3 || offset != (span - 1) / 2) { return 0; }
        int laid = 0;
        if (toGround || (leg && overDryDrop(world, at, alongX ? row : across, deckY, alongX ? across : row))) { laid += piling(world, alongX, row, across, deckY - 1, support, at); }
        IBlockState barrier = BeardRoads.pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
        if (barrier != planks && BeardStations.edgeRailed(box, alongX, row, across > (acrossLeast + acrossMost) / 2)) { laid += raise(world, barrier, alongX ? row : across, deckY, alongX ? across : row, at); }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        return laid;
    }

    private static int railEdge(int span) { return Math.min(1 + BeardRoads.pathExtraWidth() + 1, (span - 1) / 2); }

    private static int raise(World world, IBlockState barrier, int x, int deckY, int z, BlockPos.MutableBlockPos at) {
        int laid = 0;
        for (int y = deckY + 1; y <= deckY + BeardRoads.barrierHeight(); y++) {
            at.setPos(x, y, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            world.setBlockState(at, barrier, 2);
            laid++;
        }
        return laid;
    }
}
