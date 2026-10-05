package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Longs;

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
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class CityGrid {
    private static final int DISTRICT_LEAST = 96;
    private static final int SHORTEST = 7;
    private static final int FORGET_TICKS = 1200;
    private static boolean alone;
    private static int ticks;

    private CityGrid() {}

    public static boolean on() { return ContentBeard.wanted() && ContentControl.number(ContentControl.VILLAGES, "villageCitySpacing", Config.worldgen.villageCitySpacing) == 1; }

    public static int district() { return Math.max(DISTRICT_LEAST, (2 * ContentVillages.largestPlot() + 2 * ContentBeard.plazaReach() + 2 * BeardRoads.pathFullWidth() + 15) / 16 * 16); }

    public static int range() { return Math.max(8, (district() >> 4) / 2 + 2); }

    public static boolean startsAt(int chunkX, int chunkZ) {
        int chunks = district() >> 4;
        return Math.floorMod(chunkX, chunks) == chunks / 2 && Math.floorMod(chunkZ, chunks) == chunks / 2;
    }

    public static long startIn(int cellX, int cellZ) {
        int chunks = district() >> 4;
        return Longs.pack(cellX * chunks + chunks / 2, cellZ * chunks + chunks / 2);
    }

    public static int cellOf(int chunk) { return Math.floorDiv(chunk, district() >> 4); }

    @Nullable public static BlockPos nearest(World world, BlockPos from, boolean findUnexplored) {
        int cellX = cellOf(from.getX() >> 4);
        int cellZ = cellOf(from.getZ() >> 4);
        for (int ring = 0; ring <= 100; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) { continue; }
                    long at = startIn(cellX + dx, cellZ + dz);
                    int chunkX = (int) (at >> 32);
                    int chunkZ = (int) at;
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
        return new StructureBoundingBox(minX, 0, minZ, minX + size - 1, 255, minZ + size - 1);
    }

    private static boolean inside(StructureBoundingBox bounds, StructureBoundingBox box) { return box.minX >= bounds.minX && box.maxX <= bounds.maxX && box.minZ >= bounds.minZ && box.maxZ <= bounds.maxZ; }

    public static boolean clipped(StructureVillagePieces.Start start, List<StructureComponent> pieces, StructureComponent placed) {
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
        if (alongX ? box.minZ < bounds.minZ || box.maxZ > bounds.maxZ : box.minX < bounds.minX || box.maxX > bounds.maxX) { return false; }
        int inset = BeardRoads.pathFullWidth() + 8;
        int least = (alongX ? bounds.minX : bounds.minZ) + inset;
        int most = (alongX ? bounds.maxX : bounds.maxZ) - inset;
        if ((alongX ? box.minX : box.minZ) >= least && (alongX ? box.maxX : box.maxZ) <= most) { return true; }
        if (!trimmed(box, alongX, least, most)) { return false; }
        ContentLog.LOGGER.debug("A side street of the district at {}, {} would reach the district's edge, where only the well's cross meets the next district, so it stops {} block(s) short as {}", bounds.minX, bounds.minZ, inset, box);
        return true;
    }

    private static boolean trimmed(StructureBoundingBox road, boolean alongX, int least, int most) {
        int low = Math.max(alongX ? road.minX : road.minZ, least);
        int high = Math.min(alongX ? road.maxX : road.maxZ, most);
        if (high - low + 1 < SHORTEST) { return false; }
        if (alongX) {
            road.minX = low;
            road.maxX = high;
        }
        else {
            road.minZ = low;
            road.maxZ = high;
        }
        return true;
    }

    public static void avenues(StructureStart held, World world, Random rand) {
        List<StructureComponent> pieces = held.getComponents();
        if (pieces.isEmpty() || !(pieces.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start start = (StructureVillagePieces.Start) pieces.get(0);
        StructureBoundingBox well = start.getBoundingBox();
        StructureBoundingBox bounds = bounds(well);
        int laid = 0;
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) { laid += avenue(start, pieces, rand, well, bounds, facing, seated(world, bounds, facing)); }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The district at {}, {} runs the four streets of its well's cross to its edges, laying {} new length(s) of avenue, so they meet the next districts' at the seams", bounds.minX, bounds.minZ, laid);
    }

    private static boolean seated(World world, StructureBoundingBox bounds, EnumFacing facing) {
        long at = startIn(cellOf(bounds.minX >> 4) + facing.getXOffset(), cellOf(bounds.minZ >> 4) + facing.getZOffset());
        int chunkX = (int) (at >> 32);
        int chunkZ = (int) at;
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, chunkX, chunkZ)) { return true; }
        return ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) && !ContentBeard.mansionCandidateNear(world, chunkX, chunkZ);
    }

    private static int avenue(StructureVillagePieces.Start start, List<StructureComponent> pieces, Random rand, StructureBoundingBox well, StructureBoundingBox bounds, EnumFacing facing, boolean seated) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = alongX ? facing.getXOffset() : facing.getZOffset();
        int row = alongX ? well.minZ + 2 : well.minX + 2;
        int half = (BeardRoads.pathFullWidth() - 1) / 2;
        int reach = step > 0 ? (alongX ? well.maxX : well.maxZ) + 1 : (alongX ? well.minX : well.minZ) - 1;
        int inset = seated ? 0 : BeardRoads.pathFullWidth() + 8;
        int edge = (step > 0 ? (alongX ? bounds.maxX : bounds.maxZ) : (alongX ? bounds.minX : bounds.minZ)) - step * inset;
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
                if ((alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2) != row) { continue; }
                int near = step > 0 ? (alongX ? road.minX : road.minZ) : (alongX ? road.maxX : road.maxZ);
                int far = step > 0 ? (alongX ? road.maxX : road.maxZ) : (alongX ? road.minX : road.minZ);
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
        if (street instanceof IRoadLayout) { ((IRoadLayout) street).rdpl$layout(null); }
    }

    private static StructureBoundingBox line(boolean alongX, int low, int high, int row, int half, StructureBoundingBox well) { return alongX ? new StructureBoundingBox(low, well.minY, row - half, high, well.maxY, row + half) : new StructureBoundingBox(row - half, well.minY, low, row + half, well.maxY, high); }

    private static int walked(List<StructureComponent> pieces, StructureBoundingBox line, EnumFacing facing) {
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        try { return BeardRoadsGrade.roadReach(line, facing); }
        finally { ContentBeard.laying(held); }
    }

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
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The district at {}, {} drops {} piece(s) laid past its edge, so nothing of it stands in the next district", bounds.minX, bounds.minZ, dropped);
    }

    public static boolean atEdge(List<StructureComponent> own, boolean alongX, int end, int dir) {
        StructureBoundingBox bounds = bounds(own.get(0).getBoundingBox());
        return end == (dir > 0 ? (alongX ? bounds.maxX : bounds.maxZ) : (alongX ? bounds.minX : bounds.minZ));
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
