package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.util.world.StructureBoxes;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.BeardPlots;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoads;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoadsGrade;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class CityGrid {
    private static final int DISTRICT_LEAST = 96;
    private static final int SHORTEST = 7;
    private static final int FORGET_TICKS = 1200;
    private static boolean alone;
    private static int ticks;

    private CityGrid() {}

    public static boolean on() { return ContentBeard.wanted() && ContentControl.number(ContentControl.VILLAGES, "villageCitySpacing", Config.worldgen.villageCitySpacing) == 1; }

    public static int spacing() { return 2 * ContentVillages.largestPlot() + 2 * ContentBeard.plazaReach() + 2 * BeardRoads.pathFullWidth(); }

    public static int district() { return Math.max(DISTRICT_LEAST, (spacing() + 15) / 16 * 16); }

    private static int chunks() { return district() >> 4; }

    private static int startChunk(int chunks) { return chunks / 2; }

    private static int edgeInset() { return BeardRoads.pathFullWidth() + 8; }

    public static int range() { return Math.max(8, startChunk(chunks()) + 2); }

    public static boolean startsAt(int chunkX, int chunkZ) {
        int chunks = chunks();
        return Math.floorMod(chunkX, chunks) == startChunk(chunks) && Math.floorMod(chunkZ, chunks) == startChunk(chunks);
    }

    public static long startIn(int cellX, int cellZ) {
        int chunks = chunks();
        return Longs.pack(cellX * chunks + startChunk(chunks), cellZ * chunks + startChunk(chunks));
    }

    public static int cellOf(int chunk) { return Math.floorDiv(chunk, chunks()); }

    @Nullable public static BlockPos nearest(World world, BlockPos from, boolean findUnexplored) {
        int cellX = cellOf(from.getX() >> 4);
        int cellZ = cellOf(from.getZ() >> 4);
        for (int ring = 0; ring <= 100; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) { continue; }
                    long at = startIn(cellX + dx, cellZ + dz);
                    int chunkX = Longs.high(at);
                    int chunkZ = Longs.low(at);
                    if (findUnexplored && world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }
                    return new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);
                }
            }
        }
        return null;
    }

    public static void alone(boolean laying) { alone = laying; }

    public static boolean alone() { return alone; }

    static StructureBoundingBox bounds(StructureBoundingBox well) {
        int size = district();
        int minX = Math.floorDiv(well.minX, size) * size;
        int minZ = Math.floorDiv(well.minZ, size) * size;
        return new StructureBoundingBox(minX, 0, minZ, minX + size - 1, GenHeights.WORLD_TOP, minZ + size - 1);
    }

    private static boolean inside(StructureBoundingBox bounds, StructureBoundingBox box) { return box.minX >= bounds.minX && box.maxX <= bounds.maxX && box.minZ >= bounds.minZ && box.maxZ <= bounds.maxZ; }

    public static boolean clipped(StructureVillagePieces.Start start, List<StructureComponent> pieces, StructureComponent placed) {
        if (placed == null || start == null || !alone) { return false; }
        StructureBoundingBox bounds = bounds(start.getBoundingBox());
        StructureBoundingBox box = placed.getBoundingBox();
        if (kept(bounds, placed)) { return false; }
        pieces.remove(placed);
        start.pendingRoads.remove(placed);
        start.pendingHouses.remove(placed);
        ContentLog.LOGGER.debug("{} at {}, {} would leave the district at {}, {}, so it is not built", placed.getClass().getSimpleName(), box.minX, box.minZ, bounds.minX, bounds.minZ);
        return true;
    }

    private static boolean kept(StructureBoundingBox bounds, StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (!(piece instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(piece)) { return inside(bounds, box); }
        boolean alongX = BeardPlots.roadAlongX(piece);
        if (BeardPlots.acrossLeast(box, alongX) < BeardPlots.acrossLeast(bounds, alongX) || BeardPlots.acrossMost(box, alongX) > BeardPlots.acrossMost(bounds, alongX)) { return false; }
        int inset = edgeInset();
        int least = BeardPlots.least(bounds, alongX) + inset;
        int most = BeardPlots.most(bounds, alongX) - inset;
        if (BeardPlots.least(box, alongX) >= least && BeardPlots.most(box, alongX) <= most) { return true; }
        if (tooShort(box, alongX, least, most)) { return false; }
        ContentLog.LOGGER.debug("A side street of the district at {}, {} would reach the district's edge, where only the well's cross meets the next district, so it stops {} block(s) short as {}", bounds.minX, bounds.minZ, inset, box);
        return true;
    }

    private static boolean tooShort(StructureBoundingBox road, boolean alongX, int least, int most) {
        int low = Math.max(BeardPlots.least(road, alongX), least);
        int high = Math.min(BeardPlots.most(road, alongX), most);
        if (high - low + 1 < SHORTEST) { return true; }
        if (alongX) {
            road.minX = low;
            road.maxX = high;
        }
        else {
            road.minZ = low;
            road.maxZ = high;
        }
        return false;
    }

    public static void avenues(StructureStart held, World world, Random rand) {
        List<StructureComponent> pieces = held.getComponents();
        StructureVillagePieces.Start start = CityGrowth.startOf(pieces);
        if (start == null) { return; }
        StructureBoundingBox well = start.getBoundingBox();
        StructureBoundingBox bounds = bounds(well);
        int laid = 0;
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) { laid += avenue(start, pieces, rand, well, bounds, facing, seated(world, bounds, facing)); }
        held.updateBoundingBox();
        ContentLog.LOGGER.debug("The district at {}, {} runs the four streets of its well's cross to its edges, laying {} new length(s) of avenue, so they meet the next districts' at the seams", bounds.minX, bounds.minZ, laid);
    }

    private static boolean seated(World world, StructureBoundingBox bounds, EnumFacing facing) {
        long at = startIn(cellOf(bounds.minX >> 4) + facing.getXOffset(), cellOf(bounds.minZ >> 4) + facing.getZOffset());
        int chunkX = Longs.high(at);
        int chunkZ = Longs.low(at);
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, chunkX, chunkZ)) { return true; }
        return ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) && !ContentBeard.mansionCandidateNear(world, chunkX, chunkZ);
    }

    private static int avenue(StructureVillagePieces.Start start, List<StructureComponent> pieces, Random rand, StructureBoundingBox well, StructureBoundingBox bounds, EnumFacing facing, boolean seated) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = alongX ? facing.getXOffset() : facing.getZOffset();
        int row = BeardPlots.acrossLeast(well, alongX) + 2;
        int half = ContentBeard.streetHalf();
        int reach = step > 0 ? BeardPlots.most(well, alongX) + 1 : BeardPlots.least(well, alongX) - 1;
        int inset = seated ? 0 : edgeInset();
        int edge = (step > 0 ? BeardPlots.most(bounds, alongX) : BeardPlots.least(bounds, alongX)) - step * inset;
        if (!seated) { ContentLog.LOGGER.debug("The avenue {} of the district at {}, {} meets a district left empty, so it stops {} block(s) short of the edge and ends there as a dead end", facing, bounds.minX, bounds.minZ, inset); }
        int walked = walked(pieces, line(alongX, Math.min(reach, edge), Math.max(reach, edge), row, half, well), facing);
        boolean through = walked >= (edge - reach) * step + 1;
        if (!through) {
            edge = reach + step * (walked - 1);
            ContentLog.LOGGER.debug("The avenue {} of the district at {}, {} keeps a walkable slope for only {} row(s) from its well, so it stops there as a dead end", facing, bounds.minX, bounds.minZ, walked);
        }
        StructureComponent last = null;
        boolean grew = true;
        while (grew) {
            grew = false;
            for (StructureComponent piece : pieces) {
                if (!(piece instanceof StructureVillagePieces.Path) || BeardPlots.roadAlongX(piece) != alongX) { continue; }
                StructureBoundingBox road = piece.getBoundingBox();
                if (StructureBoxes.across(road, alongX) != row) { continue; }
                int near = step > 0 ? BeardPlots.least(road, alongX) : BeardPlots.most(road, alongX);
                int far = step > 0 ? BeardPlots.most(road, alongX) : BeardPlots.least(road, alongX);
                if ((near - reach) * step > 0 || (far - reach) * step < 0) { continue; }
                reach = far + step;
                last = piece;
                grew = true;
            }
        }
        if ((reach - edge) * step > 0 || !through && (edge - reach) * step + 1 < SHORTEST) { return 0; }
        if (last != null && CityGrowth.bulbWide(last)) { last = null; }
        StructureBoundingBox strip = line(alongX, Math.min(reach, edge), Math.max(reach, edge), row, half, well);
        int cleared = 0;
        for (StructureComponent piece : pieces.toArray(new StructureComponent[0])) {
            if (piece == start || piece == last || !piece.getBoundingBox().intersectsWith(strip.minX, strip.minZ, strip.maxX, strip.maxZ) || crosses(piece, alongX)) { continue; }
            pieces.remove(piece);
            cleared++;
        }
        StructureComponent lane = last == null ? new StructureVillagePieces.Path(start, 0, rand, strip, facing) : last;
        if (last == null) { pieces.add(lane); }
        else { lengthen(last, alongX, step, edge); }
        if (seated && through) { CitySeams.reserve(lane); }
        ContentLog.LOGGER.debug("The district at {}, {} lays the avenue {} {} from its well to its edge as {}, {} piece(s) making way", bounds.minX, bounds.minZ, strip, facing, last == null ? "a street of its own" : "the length of the street it continues, so both grade as one road", cleared);
        return 1;
    }

    private static void lengthen(StructureComponent street, boolean alongX, int step, int edge) {
        StructureBoundingBox box = street.getBoundingBox();
        if (alongX) {
            if (step > 0) { box.maxX = edge; }
            else { box.minX = edge; }
        }
        else {
            if (step > 0) { box.maxZ = edge; }
            else { box.minZ = edge; }
        }
        BeardRoads.clearLayout(street);
    }

    private static StructureBoundingBox line(boolean alongX, int low, int high, int row, int half, StructureBoundingBox well) { return ContentBeard.span(alongX, low, high, row - half, row + half, well.minY, well.maxY); }

    private static int walked(List<StructureComponent> pieces, StructureBoundingBox line, EnumFacing facing) { return ContentBeard.layingOf(pieces, () -> BeardRoadsGrade.roadReach(line, facing)); }

    private static boolean crosses(StructureComponent piece, boolean alongX) {
        if (piece instanceof StructureVillagePieces.Path) { return BeardPlots.roadAlongX(piece) != alongX && !CityGrowth.bulbWide(piece); }
        return piece instanceof StructureVillagePieces.Well || piece instanceof MergePiece || piece instanceof RailPiece;
    }

    public static void keepInside(StructureStart held) {
        List<StructureComponent> pieces = held.getComponents();
        if (pieces.isEmpty()) { return; }
        StructureBoundingBox bounds = bounds(pieces.get(0).getBoundingBox());
        int dropped = 0;
        for (StructureComponent piece : pieces.toArray(new StructureComponent[0])) {
            if (piece instanceof RailPiece || (CitySeams.reserved(piece) ? inside(bounds, piece.getBoundingBox()) : kept(bounds, piece))) { continue; }
            pieces.remove(piece);
            dropped++;
        }
        if (dropped == 0) { return; }
        held.updateBoundingBox();
        ContentLog.LOGGER.debug("The district at {}, {} drops {} piece(s) laid past its edge, so nothing of it stands in the next district", bounds.minX, bounds.minZ, dropped);
    }

    public static boolean atEdge(List<StructureComponent> own, boolean alongX, int end, int dir) {
        StructureBoundingBox bounds = bounds(own.get(0).getBoundingBox());
        return end == (dir > 0 ? BeardPlots.most(bounds, alongX) : BeardPlots.least(bounds, alongX));
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ++ticks < FORGET_TICKS) { return; }
        ticks = 0;
        if (!on()) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (WorldServer world : server.worlds) {
            if (ContentPregen.makingLand(world)) { continue; }
            List<EntityPlayer> players = world.playerEntities;
            long[] centers = new long[players.size() + 1];
            BlockPos spawn = world.getSpawnPoint();
            centers[0] = ChunkPos.asLong(spawn.getX() >> 4, spawn.getZ() >> 4);
            for (int i = 0; i < players.size(); i++) { centers[i + 1] = ChunkPos.asLong(players.get(i).chunkCoordX, players.get(i).chunkCoordZ); }
            ContentStructures.forgetFarStarts(world, centers);
        }
    }
}
