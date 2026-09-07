package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class BeardRails {
    private static final int MARGIN = 7;
    private static final int OVER = 6;
    private static final int FILL = 3;
    private static final int CLEAR = 4;
    private static final int CANOPY = 14;
    private static final int BESIDE = 2;
    private static final int LEG = 4;
    private static final int FILL_UNDER = 8;
    private static final int STRETCH = 32;
    private static final int BELOW = 24;
    private static final int ABOVE = 40;

    private BeardRails() {}

    public static int lines() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLines", Config.worldgen.villageRailLines)); }

    public static int spacing() { return Math.max(8, ContentControl.number(ContentControl.VILLAGES, "villageRailSpacing", Config.worldgen.villageRailSpacing)); }

    public static int width() { return Math.max(3, ContentControl.number(ContentControl.VILLAGES, "villageRailWidth", Config.worldgen.villageRailWidth)); }

    public static int climb() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailClimb", Config.worldgen.villageRailClimb)); }

    public static int tail() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailTail", Config.worldgen.villageRailTail)); }

    private static int tieRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailTieRun", Config.worldgen.villageRailTieRun)); }

    private static int powerRun() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailPowerRun", Config.worldgen.villageRailPowerRun)); }

    private static IBlockState tunnelBlock() { return BeardRoads.pathBlock("villageRailTunnelBlock", Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState()); }

    public static int tunnelDepth() {
        if (tunnelBlock().getBlock() == Blocks.AIR) { return 0; }
        return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailTunnelDepth", Config.worldgen.villageRailTunnelDepth));
    }

    private static boolean direction(Random rand) {
        String named = ContentControl.text(ContentControl.VILLAGES, "villageRailDirection", Config.worldgen.villageRailDirection).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "any".equals(named)) { return rand.nextBoolean(); }
        if (named.startsWith("e") || named.startsWith("w")) { return true; }
        if (named.startsWith("n") || named.startsWith("s")) { return false; }
        ContentLog.LOGGER.error("villageRailDirection '{}' is not ew, ns or any, so the lines run as they roll", named);
        return rand.nextBoolean();
    }

    public static void found(World world, StructureStart start, StructureVillagePieces.Start well, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        int lines = lines();
        if (lines <= 0) { return; }
        List<StructureComponent> components = start.getComponents();
        boolean alongX = direction(rand);
        StructureBoundingBox wellBox = well.getBoundingBox();
        int wellX = (wellBox.minX + wellBox.maxX) / 2;
        int wellZ = (wellBox.minZ + wellBox.maxZ) / 2;
        int nominal = BeardSite.wellNominal(wellBox);
        int spacing = spacing();
        int half = (width() - 1) / 2;
        int clear = ContentBeard.plazaReach() + Math.max(13, ContentVillages.largestPlot()) + BeardRoads.pathFullWidth() + half + 2;
        int reach = CityGrowth.march() + tail() + STRETCH;
        List<Integer> placed = new ArrayList<>();
        for (int line = 0; line < lines; line++) {
            int side = line % 2 == 0 ? 1 : -1;
            int candidate = side * (clear + (line / 2) * spacing + rand.nextInt(Math.max(1, spacing / 2)));
            boolean moved = true;
            while (moved) {
                moved = false;
                for (int other : placed) {
                    if (Math.abs(candidate - other) >= spacing) { continue; }
                    candidate += side * spacing;
                    moved = true;
                }
            }
            placed.add(candidate);
            int center = (alongX ? wellZ : wellX) + candidate;
            StructureBoundingBox box = alongX
                    ? new StructureBoundingBox(wellX - reach, nominal - BELOW, center - half, wellX + reach, nominal + ABOVE, center + half)
                    : new StructureBoundingBox(center - half, nominal - BELOW, wellZ - reach, center + half, nominal + ABOVE, wellZ + reach);
            components.add(new RailPiece(well, box, alongX, line));
            ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} is laid {} at {} {}, {} wide, before any street of the village", line, wellX, wellZ, alongX ? "east to west" : "north to south", alongX ? "z" : "x", center, width());
        }
    }

    public static void fit(World world, StructureStart start) {
        List<StructureComponent> components = start.getComponents();
        if (components.isEmpty()) { return; }
        int tail = tail();
        StructureBoundingBox wellBox = components.get(0).getBoundingBox();
        List<StructureComponent> everyone = ContentBeard.everyone(world, components);
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) piece;
            boolean alongX = rail.alongX();
            int least = Integer.MAX_VALUE;
            int most = Integer.MIN_VALUE;
            for (StructureComponent other : components) {
                if (other instanceof RailPiece) { continue; }
                StructureBoundingBox box = other.getBoundingBox();
                least = Math.min(least, alongX ? box.minX : box.minZ);
                most = Math.max(most, alongX ? box.maxX : box.maxZ);
            }
            if (least > most) { continue; }
            StructureBoundingBox box = rail.getBoundingBox();
            int from = Math.max(rail.rowLeast(), least - tail);
            int to = Math.min(rail.rowMost(), most + tail);
            int wellAt = alongX ? (wellBox.minX + wellBox.maxX) / 2 : (wellBox.minZ + wellBox.maxZ) / 2;
            for (StructureComponent other : everyone) {
                if (components.contains(other)) { continue; }
                StructureBoundingBox met = other.getBoundingBox();
                if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
                int metLeast = alongX ? met.minX : met.minZ;
                int metMost = alongX ? met.maxX : met.maxZ;
                if (metLeast > wellAt) { to = Math.min(to, metLeast - 1 - MARGIN); }
                else if (metMost < wellAt) { from = Math.max(from, metMost + 1 + MARGIN); }
                else { to = from - 1; }
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} stops short of {} of another village at {}, {}", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, other.getClass().getSimpleName(), met.minX, met.minZ);
            }
            if (from > to) {
                components.remove(piece);
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} has no room left beside the villages around it, so it is not laid", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ);
                continue;
            }
            if (alongX) {
                box.minX = from;
                box.maxX = to;
            }
            else {
                box.minZ = from;
                box.maxZ = to;
            }
            rail.regrade();
            ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} is fitted to rows {} to {} now the village is grown, {} beyond its last piece either way", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, from, to, tail);
        }
    }

    public static boolean crosses(StructureBoundingBox rail, StructureBoundingBox box) {
        boolean railAlongX = rail.maxX - rail.minX >= rail.maxZ - rail.minZ;
        boolean boxAlongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        if (railAlongX == boxAlongX) { return false; }
        int stripLeast = railAlongX ? rail.minZ : rail.minX;
        int stripMost = railAlongX ? rail.maxZ : rail.maxX;
        int boxLeast = railAlongX ? box.minZ : box.minX;
        int boxMost = railAlongX ? box.maxZ : box.maxX;
        return boxLeast <= stripLeast - MARGIN && boxMost >= stripMost + MARGIN;
    }

    public static boolean blocked(@Nullable List<StructureComponent> pieces, StructureBoundingBox box) {
        if (pieces == null) { return false; }
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof RailPiece)) { continue; }
            StructureBoundingBox rail = piece.getBoundingBox();
            if (!rail.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
            if (crosses(rail, box)) { continue; }
            ContentLog.LOGGER.debug("A road attempt {} would run onto railway line {} at {}, {} without crossing it clean, so it is refused", box, ((RailPiece) piece).line(), rail.minX, rail.minZ);
            return true;
        }
        return false;
    }

    public static boolean isRail(StructureComponent piece) { return piece instanceof RailPiece; }

    public static boolean meets(StructureBoundingBox rail, StructureBoundingBox box) {
        boolean railAlongX = rail.maxX - rail.minX >= rail.maxZ - rail.minZ;
        boolean boxAlongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        return railAlongX != boxAlongX && rail.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ);
    }

    @Nullable public static int[] lengths(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        if (pieces == null) { return null; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int wanted = rows;
        int shortest = rows;
        boolean met = false;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) piece;
            StructureBoundingBox strip = rail.getBoundingBox();
            if (rail.alongX() == alongX) {
                if (strip.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { return new int[] { -1 }; }
                continue;
            }
            int acrossLeast = alongX ? box.minZ : box.minX;
            int acrossMost = alongX ? box.maxZ : box.maxX;
            if (acrossMost < rail.rowLeast() || acrossLeast > rail.rowMost()) { continue; }
            int stripLeast = rail.acrossLeast();
            int stripMost = rail.acrossMost();
            int near = step > 0 ? stripLeast - from : from - stripMost;
            int far = step > 0 ? stripMost - from : from - stripLeast;
            if (far < 0 || near - MARGIN > rows - 1) { continue; }
            if (near <= 0 || acrossLeast < rail.rowLeast() || acrossMost > rail.rowMost()) { return new int[] { -1 }; }
            met = true;
            wanted = Math.max(wanted, far + 1 + MARGIN);
            shortest = Math.min(shortest, near - MARGIN);
        }
        if (!met) { return null; }
        return new int[] { wanted, shortest };
    }

    public static boolean fits(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int kept;
        try { kept = BeardRoads.roadReach(box, facing); }
        finally { ContentBeard.laying(held); }
        return kept >= rows && ContentBeard.roomFor(pieces, box, facing) >= rows;
    }

    public static boolean settle(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        int[] lengths = lengths(pieces, box, facing);
        if (lengths == null) { return true; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        for (int rows : lengths) {
            if (rows < 7) { continue; }
            StructureBoundingBox tried = new StructureBoundingBox(box);
            BeardLayout.trim(tried, alongX, facing, rows);
            if (!fits(pieces, tried, facing)) { continue; }
            BeardLayout.trim(box, alongX, facing, rows);
            ContentLog.LOGGER.debug("A road attempt facing {} is set to {} block(s) {} the railway line in its way", facing, rows, rows == lengths[0] ? "to cross" : "to stop short of");
            return true;
        }
        ContentLog.LOGGER.debug("A road attempt facing {} can neither cross the railway line in its way nor stop short of it, so it is refused", facing);
        return false;
    }

    public static boolean railBlock(IBlockState state) { return state.getBlock() instanceof BlockRailBase; }

    public static int hold(World world, @Nullable StructureComponent piece, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, int[] ground, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return 0; }
        int rowMost = start + profile.length - 1;
        int center = (acrossLeast + acrossMost) / 2;
        int depth = tunnelDepth();
        int pinned = 0;
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) other;
            if (rail.alongX() == alongX) { continue; }
            if (rail.rowLeast() > acrossMost || rail.rowMost() < acrossLeast) { continue; }
            int stripLeast = rail.acrossLeast();
            int stripMost = rail.acrossMost();
            if (stripMost < start || stripLeast > rowMost) { continue; }
            BeardRoads.Grade grade = rail.grade(world);
            if (grade == null) { continue; }
            int level = grade.at(center);
            if (level == Integer.MIN_VALUE) { continue; }
            if (grade.tunneledAt(center, depth)) {
                int middle = (Math.max(start, stripLeast) + Math.min(rowMost, stripMost)) / 2;
                int natural = middle >= start && middle <= rowMost ? profile[middle - start] : Integer.MIN_VALUE;
                if (natural != Integer.MIN_VALUE && natural >= level + OVER) { continue; }
            }
            for (int row = Math.max(start, stripLeast - 1); row <= Math.min(rowMost, stripMost + 1); row++) {
                int i = row - start;
                if (held[i]) { continue; }
                profile[i] = level;
                held[i] = true;
                pinned++;
            }
        }
        return pinned;
    }

    @Nullable public static BeardRoads.Grade profile(World world, RailPiece rail) {
        boolean alongX = rail.alongX();
        int rowLeast = rail.rowLeast();
        int[] ground = BeardGrade.noiseProfile(world, alongX, rowLeast, rail.rowMost(), rail.acrossLeast(), rail.acrossMost());
        if (ground == null) { return null; }
        int rows = ground.length;
        int sea = world.getSeaLevel();
        double[] base = new double[rows];
        int i = 0;
        while (i < rows) {
            if (ground[i] != Integer.MIN_VALUE) {
                base[i] = ground[i];
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && ground[end + 1] == Integer.MIN_VALUE) { end++; }
            int before = i > 0 ? ground[i - 1] : Integer.MIN_VALUE;
            int after = end + 1 < rows ? ground[end + 1] : Integer.MIN_VALUE;
            for (int at = i; at <= end; at++) {
                double level;
                if (before == Integer.MIN_VALUE && after == Integer.MIN_VALUE) { level = sea; }
                else if (before == Integer.MIN_VALUE) { level = after; }
                else if (after == Integer.MIN_VALUE) { level = before; }
                else { level = before + (after - before) * (at - i + 1) / (double) (end - i + 2); }
                base[at] = Math.max(sea, level);
            }
            i = end + 1;
        }
        int climb = climb();
        int window = Math.max(4, 2 * climb);
        double[] mean = new double[rows];
        for (int at = 0; at < rows; at++) {
            double total = 0.0D;
            int count = 0;
            for (int near = Math.max(0, at - window); near <= Math.min(rows - 1, at + window); near++) {
                total += base[near];
                count++;
            }
            mean[at] = total / count;
        }
        double step = 1.0D / climb;
        double[] lower = mean.clone();
        double[] upper = mean.clone();
        for (int at = 1; at < rows; at++) {
            lower[at] = Math.min(lower[at], lower[at - 1] + step);
            upper[at] = Math.max(upper[at], upper[at - 1] - step);
        }
        for (int at = rows - 2; at >= 0; at--) {
            lower[at] = Math.min(lower[at], lower[at + 1] + step);
            upper[at] = Math.max(upper[at], upper[at + 1] - step);
        }
        int[] profile = new int[rows];
        int level = (int) Math.round((lower[0] + upper[0]) / 2.0D);
        int lastStep = -climb;
        for (int at = 0; at < rows; at++) {
            double want = (lower[at] + upper[at]) / 2.0D;
            if (want >= level + 1 && at - lastStep >= climb) {
                level++;
                lastStep = at;
            }
            else if (want <= level - 1 && at - lastStep >= climb) {
                level--;
                lastStep = at;
            }
            profile[at] = level;
        }
        boolean[] fixed = new boolean[rows];
        int crossings = level(rail, rowLeast, profile, fixed);
        if (crossings > 0) {
            for (int at = 1; at < rows; at++) {
                if (fixed[at]) { continue; }
                profile[at] = Math.max(profile[at - 1] - 1, Math.min(profile[at - 1] + 1, profile[at]));
            }
            for (int at = rows - 2; at >= 0; at--) {
                if (fixed[at]) { continue; }
                profile[at] = Math.max(profile[at + 1] - 1, Math.min(profile[at + 1] + 1, profile[at]));
            }
        }
        boolean[] bridged = new boolean[rows];
        for (int at = 0; at < rows; at++) { bridged[at] = ground[at] == Integer.MIN_VALUE || profile[at] > ground[at] + FILL; }
        if (ContentLog.LOGGER.debugEnabled()) {
            int trestle = 0;
            int cut = 0;
            for (int at = 0; at < rows; at++) {
                if (bridged[at]) { trestle++; }
                else if (ground[at] > profile[at]) { cut++; }
            }
            ContentLog.LOGGER.debug("Railway line {} at {}, {} grades {} row(s) from y {} to y {}, climbing a block every {} row(s) at most, {} on trestle and {} in cut", rail.line(), rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rows, profile[0], profile[rows - 1], climb, trestle, cut);
        }
        return new BeardRoads.Grade(profile, ground, bridged, new boolean[rows], rowLeast, 0);
    }

    private static int level(RailPiece rail, int rowLeast, int[] profile, boolean[] fixed) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return 0; }
        boolean alongX = rail.alongX();
        int rows = profile.length;
        int crossings = 0;
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path) || BeardPlots.roadAlongX(other) == alongX) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (!road.intersectsWith(rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rail.getBoundingBox().maxX, rail.getBoundingBox().maxZ)) { continue; }
            int from = Math.max(0, (alongX ? road.minX : road.minZ) - 1 - rowLeast);
            int to = Math.min(rows - 1, (alongX ? road.maxX : road.maxZ) + 1 - rowLeast);
            if (from > to) { continue; }
            int center = Math.max(0, Math.min(rows - 1, (alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2) - rowLeast));
            int level = profile[center];
            for (int at = from; at <= to; at++) {
                profile[at] = level;
                fixed[at] = true;
            }
            crossings++;
        }
        return crossings;
    }

    public static void lay(RailPiece rail, World world, StructureBoundingBox clip) {
        if (!ContentBeard.wanted()) { return; }
        StructureBoundingBox box = rail.getBoundingBox();
        boolean alongX = rail.alongX();
        int least = Math.max(rail.rowLeast(), alongX ? clip.minX : clip.minZ);
        int most = Math.min(rail.rowMost(), alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }
        BeardRoads.Grade grade = rail.grade(world);
        if (grade == null) { return; }
        int acrossLeast = rail.acrossLeast();
        int acrossMost = rail.acrossMost();
        int center = (acrossLeast + acrossMost) / 2;
        boolean twin = acrossMost - acrossLeast + 1 >= 5;
        IBlockState bed = BeardRoads.pathBlock("villageRailBedBlock", Config.worldgen.villageRailBedBlock, Blocks.GRAVEL.getDefaultState());
        IBlockState tie = BeardRoads.pathBlock("villageRailTieBlock", Config.worldgen.villageRailTieBlock, Blocks.PLANKS.getDefaultState());
        IBlockState track = oriented(BeardRoads.pathBlock("villageRailBlock", Config.worldgen.villageRailBlock, Blocks.RAIL.getDefaultState()), alongX);
        IBlockState powered = powered(oriented(Blocks.GOLDEN_RAIL.getDefaultState(), alongX));
        IBlockState support = BeardRoads.pathBlock("villageRailSupportBlock", Config.worldgen.villageRailSupportBlock, Blocks.LOG.getDefaultState());
        IBlockState deck = BeardRoads.pathBlock("villageRailDeckBlock", Config.worldgen.villageRailDeckBlock, Blocks.PLANKS.getDefaultState());
        IBlockState barrier = BeardRoads.pathBlock("villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock, Blocks.AIR.getDefaultState());
        IBlockState lining = tunnelBlock();
        int tieRun = tieRun();
        int powerRun = track.getBlock() == Blocks.RAIL ? powerRun() : 0;
        int depth = tunnelDepth();
        List<StructureComponent> roads = new ArrayList<>();
        for (StructureComponent other : ContentBeard.everyone(world, ContentBeard.components())) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (road.intersectsWith(box.minX - 1, box.minZ - 1, box.maxX + 1, box.maxZ + 1)) { roads.add(other); }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        StructureStart holder = ContentBeard.current();
        Predicate<BlockPos> within = holder != null ? BeardPlots.outside(world, holder, rail, box, false, 0) : spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4);
        List<BlockPos> seeds = new ArrayList<>();
        int laid = 0;
        int trestled = 0;
        int lined = 0;
        int crossed = 0;
        for (int row = least; row <= most; row++) {
            int level = grade.at(row);
            if (level == Integer.MIN_VALUE) { continue; }
            boolean trestle = grade.bridgedAt(row);
            boolean tunnel = !trestle && grade.tunneledAt(row, depth) && !crossedAt(world, roads, alongX, row, center, level);
            boolean tieRow = Math.floorMod(row, tieRun) == 0;
            boolean powerRow = powerRun > 0 && Math.floorMod(row, powerRun) == 0;
            boolean legRow = Math.floorMod(row, LEG) == 0;
            for (int across = acrossLeast - 1; across <= acrossMost + 1; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.setPos(x, level, z);
                if (!clip.isVecInside(at)) { continue; }
                boolean verge = across < acrossLeast || across > acrossMost;
                boolean onRail = twin ? Math.abs(across - center) == 1 : across == center;
                boolean edge = across == acrossLeast || across == acrossMost;
                if (verge) {
                    if (trestle) { continue; }
                    if (tunnel) { lined += BeardRoads.tunnelWall(world, rail, at, x, z, level, lining); }
                    else { BeardRoads.vergeFill(world, rail, x, z, level, at); }
                    continue;
                }
                StructureComponent road = tunnel ? null : roadAt(roads, x, z);
                if (road != null) {
                    if (onRail) {
                        int atRoad = grade.at(alongX ? (road.getBoundingBox().minX + road.getBoundingBox().maxX) / 2 : (road.getBoundingBox().minZ + road.getBoundingBox().maxZ) / 2);
                        if (atRoad == Integer.MIN_VALUE) { atRoad = level; }
                        BeardKeep.letGo(x, atRoad + 1, z);
                        set(world, at, x, atRoad + 1, z, track);
                        crossed++;
                    }
                    continue;
                }
                if (trestle) {
                    clearAbove(world, at, x, z, level + 1, level + CLEAR, within, seeds);
                    laid += set(world, at, x, level, z, deck);
                    if (edge && legRow) { trestled += BeardRoads.piling(world, alongX, row, across, level - 1, support, at); }
                    if (onRail) { set(world, at, x, level + 1, z, track); }
                    else if (edge && barrier.getBlock() != Blocks.AIR) { set(world, at, x, level + 1, z, barrier); }
                    continue;
                }
                BlockPos top = GroundLevel.inWindow(world, new BlockPos(x, 64, z)).down();
                clearAbove(world, at, x, z, level + 1, tunnel ? level + CLEAR : Math.max(level + CLEAR, top.getY() + 2), within, seeds);
                BeardBlocks.fillUnder(world, at, x, z, level - 1, level - FILL_UNDER);
                IBlockState base = powerRow && onRail ? Blocks.REDSTONE_BLOCK.getDefaultState() : tieRow ? tie : bed;
                laid += set(world, at, x, level, z, base);
                if (onRail) { set(world, at, x, level + 1, z, powerRow ? powered : track); }
                if (tunnel) { lined += BeardRoads.roofCell(world, at, x, z, level + CLEAR + 1, lining); }
            }
        }
        int felled = seeds.isEmpty() ? 0 : ContentBeard.fellTrees(world, seeds, within, at);
        if (laid + crossed + felled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Laid railway line {} at {}, {} within its chunk: {} bed column(s), {} support block(s) under trestles, {} tunnel block(s), {} rail(s) across roads, {} tree block(s) felled whole where a tree stood over the bed", rail.line(), box.minX, box.minZ, laid, trestled, lined, crossed, felled); }
    }

    public static int open(RailPiece rail, World world, StructureStart start, StructureBoundingBox clip) {
        StructureBoundingBox box = rail.getBoundingBox();
        boolean alongX = rail.alongX();
        int least = Math.max(rail.rowLeast(), alongX ? clip.minX : clip.minZ);
        int most = Math.min(rail.rowMost(), alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return 0; }
        BeardRoads.Grade grade = rail.grade(world);
        if (grade == null) { return 0; }
        Predicate<BlockPos> within = BeardPlots.outside(world, start, rail, box, false, 0);
        List<BlockPos> seeds = new ArrayList<>();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int row = least; row <= most; row++) {
            int level = grade.at(row);
            if (level == Integer.MIN_VALUE) { continue; }
            for (int across = rail.acrossLeast() - BESIDE; across <= rail.acrossMost() + BESIDE; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.setPos(x, level, z);
                if (!clip.isVecInside(at)) { continue; }
                boolean beside = across < rail.acrossLeast() || across > rail.acrossMost();
                for (int y = level - 2; y <= level + CANOPY; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    if (above.getBlock() == Blocks.AIR || BeardKeep.holds(x, y, z)) { continue; }
                    if (beside) {
                        if (BlastPlasterUtil.isTreeWood(above) && !ContentVillageDecor.plantedAt(world, x, z)) { seeds.add(new BlockPos(x, y, z)); }
                        continue;
                    }
                    if (y > level) { tree(world, at, x, y, z, above, within, seeds); }
                }
            }
        }
        return seeds.isEmpty() ? 0 : ContentBeard.fellTrees(world, seeds, within, at);
    }

    @Nullable private static StructureComponent roadAt(List<StructureComponent> roads, int x, int z) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return road; }
        }
        return null;
    }

    private static boolean crossedAt(World world, List<StructureComponent> roads, boolean alongX, int row, int center, int level) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (row < (alongX ? box.minX : box.minZ) - 1 || row > (alongX ? box.maxX : box.maxZ) + 1) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            BeardRoads.Grade grade = road instanceof IRoadLayout ? ((IRoadLayout) road).rdpl$layout() : null;
            if (grade == null) { return true; }
            int at = grade.at(center);
            return at == Integer.MIN_VALUE || at < level + OVER;
        }
        return false;
    }

    private static int set(World world, BlockPos.MutableBlockPos at, int x, int y, int z, IBlockState state) {
        if (BeardKeep.holds(x, y, z)) { return 0; }
        at.setPos(x, y, z);
        if (world.getBlockState(at) == state) { return 0; }
        world.setBlockState(at, state, 2);
        return 1;
    }

    private static void clearAbove(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int to, Predicate<BlockPos> within, List<BlockPos> seeds) {
        int y = from;
        for (; y <= to; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            Block up = above.getBlock();
            if (up == Blocks.AIR || BeardKeep.holds(x, y, z) || railBlock(above)) { continue; }
            if (above.getMaterial().isLiquid()) { break; }
            if (tree(world, at, x, y, z, above, within, seeds)) { continue; }
            if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || !above.getMaterial().isSolid()) {
                BeardBlocks.note(world, at, "Laying a railway");
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                continue;
            }
            break;
        }
        for (; y <= from + CANOPY; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            if (above.getBlock() == Blocks.AIR || BeardKeep.holds(x, y, z)) { continue; }
            tree(world, at, x, y, z, above, within, seeds);
        }
    }

    private static boolean tree(World world, BlockPos.MutableBlockPos at, int x, int y, int z, IBlockState held, Predicate<BlockPos> within, List<BlockPos> seeds) {
        if (BlastPlasterUtil.isTreeWood(held)) {
            if (!ContentVillageDecor.plantedAt(world, x, z)) { seeds.add(new BlockPos(x, y, z)); }
            return true;
        }
        if (held.getMaterial() != Material.LEAVES) { return false; }
        BlockPos trunk = BeardGround.sustainer(world, new BlockPos(x, y, z), within);
        if (trunk == null) {
            BeardBlocks.note(world, at, "Laying a railway");
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        }
        else if (!ContentVillageDecor.plantedAt(world, trunk.getX(), trunk.getZ())) { seeds.add(trunk); }
        return true;
    }

    @SuppressWarnings("unchecked") private static IBlockState oriented(IBlockState state, boolean alongX) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property.getValueClass() != BlockRailBase.EnumRailDirection.class) { continue; }
            IProperty<BlockRailBase.EnumRailDirection> shape = (IProperty<BlockRailBase.EnumRailDirection>) property;
            BlockRailBase.EnumRailDirection wanted = alongX ? BlockRailBase.EnumRailDirection.EAST_WEST : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (shape.getAllowedValues().contains(wanted)) { return state.withProperty(shape, wanted); }
        }
        return ContentBeard.axised(state, alongX);
    }

    private static IBlockState powered(IBlockState state) {
        if (state.getPropertyKeys().contains(BlockRailPowered.POWERED)) { return state.withProperty(BlockRailPowered.POWERED, true); }
        return state;
    }
}
