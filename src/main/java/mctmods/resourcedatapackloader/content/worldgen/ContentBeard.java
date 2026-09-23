package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.ContentVillagePiece;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.village.RecurrentVillagePiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardClearing;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardCrown;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGround;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLinks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardOpen;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlaza;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRailsLay;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsEnds;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsPaving;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSeams;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.content.worldgen.beard.RecurrentPlots;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorBeardFields;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenVillage;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentBeard {
    public static final int BAND = 6;
    private static boolean layingBuilding;
    private static int peeks;
    private static final TemplateMemo<Boolean> WANTED = new TemplateMemo<>();
    private static Boolean recurrent;

    private static boolean recurrent() {
        if (recurrent == null) { recurrent = Loader.isModLoaded("reccomplex"); }
        return recurrent;
    }
    public static final int SITE_REACH = 2;
    public static final int SITE_TOLERANCE = 10;
    public static final int BESIDE_WELL = 4;
    private static final int ATTACH_GAP = 8;
    public static final int FACING_GAP = 2;
    public static final int SITE_SEPARATION = 8;
    public static final int FOOTING_COURSE = 1;
    public static final long NO_SITE = Long.MIN_VALUE;
    public static World samplerWorld;

    private ContentBeard() {}

    public static void layingBuilding(boolean now) { layingBuilding = now; }

    public static boolean spacedLayout() {
        if (!layingBuilding || !wanted()) { return false; }
        if (peeks < 30 && ContentLog.LOGGER.debugEnabled()) {
            peeks++;
            StackTraceElement[] frames = new Throwable().getStackTrace();
            String caller = frames.length > 3 ? frames[3].getClassName() + "." + frames[3].getMethodName() : "unknown";
            ContentLog.LOGGER.debug("The spaced layout test ran for {}", caller);
        }
        return true;
    }

    public static boolean roughGround(World world, int blockX, int blockZ, int halfWidth, int tolerance) {
        int middle = surfaceAt(world, blockX, blockZ);
        if (middle < 0) { return false; }
        int lowest = middle;
        int highest = middle;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int sampled = surfaceAt(world, blockX + dx * halfWidth, blockZ + dz * halfWidth);
                if (sampled < 0) { return false; }
                lowest = Math.min(lowest, sampled);
                highest = Math.max(highest, sampled);
                if (highest - lowest > tolerance) { return true; }
            }
        }
        return false;
    }

    public static int surfaceAt(World world, int blockX, int blockZ) { return BeardSurface.surfaceAt(world, blockX, blockZ); }

    public static ChunkGeneratorOverworld samplerFor(World world) { return BeardSurface.samplerFor(world); }

    public static boolean adapts(World world) { return samplerFor(world) != null; }

    public static int villageSpacing(World world) {
        IChunkGenerator maker = ContentStructureSearch.makerOf(world);
        if (!(maker instanceof ChunkGeneratorOverworld)) { return 32; }
        return ((IMapGenVillage) ((IChunkGeneratorBeardFields) maker).rdpl$villages()).rdpl$distance();
    }

    public static long siteIn(World world, ContentSites known, int cellX, int cellZ, int spacing) { return BeardSite.siteFor(world, known, cellX, cellZ, spacing); }

    private static final Map<Long, BeardCrown.Born> WINDOWS = new HashMap<>();

    private static StructureBoundingBox window(int chunkX, int chunkZ) {
        int blockX = (chunkX << 4) + 8;
        int blockZ = (chunkZ << 4) + 8;
        return new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
    }

    @SubscribeEvent public static void onBorn(PopulateChunkEvent.Pre event) {
        if (event.getWorld().isRemote || !wanted()) { return; }
        Collection<StructureStart> starts = ContentStructureSearch.villageStarts(event.getWorld());
        if (starts.isEmpty()) { return; }
        StructureBoundingBox window = window(event.getChunkX(), event.getChunkZ());
        for (StructureStart start : starts) {
            if (start != null && start.isSizeableStructure() && BeardCrown.crowns(start, window)) {
                WINDOWS.put(ChunkPos.asLong(event.getChunkX(), event.getChunkZ()), BeardCrown.bornWindow(event.getWorld(), window, new BlockPos.MutableBlockPos()));
                return;
            }
        }
    }

    @SubscribeEvent public static void onDressed(PopulateChunkEvent.Post event) {
        BeardCrown.Born born = WINDOWS.remove(ChunkPos.asLong(event.getChunkX(), event.getChunkZ()));
        if (event.getWorld().isRemote || !wanted()) { return; }
        Collection<StructureStart> starts = ContentStructureSearch.villageStarts(event.getWorld());
        if (starts.isEmpty()) { return; }
        StructureBoundingBox clip = window(event.getChunkX(), event.getChunkZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (StructureStart start : starts) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            building(start);
            try {
                List<StructureBoundingBox> repaved = BeardRoadsPaving.repairRoads(event.getWorld(), start);
                int seams = BeardSeams.levelSeams(start, event.getWorld(), clip, at);
                if (seams > 0) { ContentLog.LOGGER.debug("Filled {} block(s) of groove left between the plots of the village at {}, {}, where two aprons met without meeting", seams, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                int freed = BeardClearing.freeDoors(start, event.getWorld(), clip, at, repaved);
                if (freed > 0) { ContentLog.LOGGER.debug("Freed {} block(s) that had closed a doorway of the village at {}, {} after the roads were repaired", freed, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                int swept = BeardClearing.sweepOrphanedLeaves(start, event.getWorld(), clip, at);
                if (swept > 0) { ContentLog.LOGGER.debug("Swept {} orphaned leaf block(s) no trunk sustains around the village at {}, {}, left behind where a felled tree crossed a chunk edge", swept, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                for (StructureComponent piece : start.getComponents()) {
                    if (!(piece instanceof StructureVillagePieces.Village)) { continue; }
                    StructureBoundingBox box = piece.getBoundingBox();
                    int near = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece) ? 6 : 2;
                    if (box.minX - near > clip.maxX || box.maxX + near < clip.minX || box.minZ - near > clip.maxZ || box.maxZ + near < clip.minZ) { continue; }
                    if (BeardRails.isRail(piece)) {
                        int opened = BeardRailsLay.open((RailPiece) piece, event.getWorld(), start, clip);
                        if (opened > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) whole that had grown over railway line {} at {}, {} after it was laid", opened, ((RailPiece) piece).line(), box.minX, box.minZ); }
                        continue;
                    }
                    int felled = ContentBeardTrees.fellAround(event.getWorld(), start, piece, box, clip, at, false);
                    if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
                    if (piece instanceof StructureVillagePieces.Path) {
                        int[] ring = BeardClearing.openOver(start, piece, event.getWorld(), box, clip, at);
                        if (ring[0] > 0) { ContentLog.LOGGER.debug("Opened {} block(s) over the roadway of Path at {}, {}", ring[0], box.minX, box.minZ); }
                        if (CityGrowth.bulbWide(piece)) {
                            int banked = BeardRoadsEnds.bulbShoulder(piece, event.getWorld(), clip, at);
                            if (banked > 0) { ContentLog.LOGGER.debug("Banked {} block(s) of the shoulder around the cul-de-sac at {}, {} up to its pad", banked, box.minX, box.minZ); }
                        }
                        ContentBeardLamps.lampPosts(start, piece, event.getWorld(), box, clip, at);
                    }
                    else if (!box.intersectsWith(clip)) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} reaches into chunk {}, {} that its own box misses, so its ring is opened there now", piece.getClass().getSimpleName(), box.minX, box.minZ, event.getChunkX(), event.getChunkZ()); }
                        openAround(start, piece, event.getWorld(), clip);
                    }
                }
                int pits = BeardSeams.fillPlotPits(start, event.getWorld(), clip, at);
                if (pits > 0) { ContentLog.LOGGER.debug("Filled {} block(s) of pit left open inside the plots of the village at {}, {}, where a plot kept a column of its box clear that the rings around it skip", pits, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
            }
            finally { building(null); }
        }
        for (StructureStart start : starts) {
            if (start != null && start.isSizeableStructure() && BeardCrown.crowns(start, clip)) { crownWindow(start, event, clip, born); }
        }
    }

    private static void crownWindow(StructureStart start, PopulateChunkEvent.Post event, StructureBoundingBox clip, @Nullable BeardCrown.Born born) {
        if (born == null) {
            ContentLog.LOGGER.debug("The village at {}, {} reaches chunk {}, {} but no born surface was taken there before it was built, so its ground is not crowned there", start.getBoundingBox().minX, start.getBoundingBox().minZ, event.getChunkX(), event.getChunkZ());
            return;
        }
        building(start);
        try { BeardCrown.crownWindow(start, event.getWorld(), clip, born); }
        finally { building(null); }
    }

    public static void fellFor(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village)) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        int near = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece) ? 6 : 2;
        if (box.minX - near > clip.maxX || box.maxX + near < clip.minX || box.minZ - near > clip.maxZ || box.maxZ + near < clip.minZ) { return; }
        int felled = ContentBeardTrees.fellAround(world, start, piece, box, clip, new BlockPos.MutableBlockPos(), true);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before {} at {}, {} was built", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
    }

    public static boolean doorwayAt(World world, BlockPos.MutableBlockPos at, int x, int y, int z) {
        if (world.getBlockState(at.setPos(x, y, z)).getMaterial().isSolid()) { return false; }
        if (world.getBlockState(at.setPos(x, y + 1, z)).getMaterial().isSolid()) { return false; }
        if (!world.getBlockState(at.setPos(x, y + 2, z)).getMaterial().isSolid()) { return false; }
        if (!world.getBlockState(at.setPos(x, y - 1, z)).getMaterial().isSolid()) { return false; }
        boolean alongX = world.getBlockState(at.setPos(x - 1, y, z)).getMaterial().isSolid() && world.getBlockState(at.setPos(x + 1, y, z)).getMaterial().isSolid();
        if (alongX) { return true; }
        return world.getBlockState(at.setPos(x, y, z - 1)).getMaterial().isSolid() && world.getBlockState(at.setPos(x, y, z + 1)).getMaterial().isSolid();
    }

    public static IBlockState lampBlock() { return BeardRoads.pathBlock("villagePathLampBlock", Config.worldgen.villagePathLampBlock, Blocks.AIR.getDefaultState()); }

    public static IBlockState lampTop() { return BeardRoads.pathBlock("villagePathLampTopBlock", Config.worldgen.villagePathLampTopBlock, Blocks.AIR.getDefaultState()); }

    public static int lampHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathLampHeight", Config.worldgen.villagePathLampHeight)); }

    public static void wellPlaza(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) { BeardPlaza.wellPlaza(start, piece, world, clip); }

    public static boolean flatSite(World world, int chunkX, int chunkZ, int spacing) { return BeardSite.flatSite(world, chunkX, chunkZ, spacing); }

    public static BlockPos nearestSite(World world, BlockPos from, int spacing, boolean findUnexplored, long budgetNanos) { return BeardSite.nearestSite(world, from, spacing, findUnexplored, budgetNanos); }

    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) { return BeardSite.mansionCandidateNear(world, chunkX, chunkZ); }

    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) { return BeardSite.lowestIn(worldIn, minX, minZ, maxX, maxZ, clip); }

    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces, int sink, int allow) { return BeardSite.footingMisfit(box, pieces, sink, CityGrowth.give(), allow); }

    public static int footingAllow(StructureComponent piece) { return piece instanceof ContentVillagePiece ? ((ContentVillagePiece) piece).apron() : 2; }

    public static int footingSink(StructureComponent piece) {
        if (piece instanceof RecurrentVillagePiece) { return ((RecurrentVillagePiece) piece).footingSink(); }
        return recurrent() ? RecurrentPlots.sink(piece) : 0;
    }

    public static int groundCourse(StructureComponent piece) {
        if (piece instanceof RecurrentVillagePiece) { return ((RecurrentVillagePiece) piece).groundCourses(); }
        return recurrent() ? RecurrentPlots.groundCourse(piece) : 0;
    }

    public static int plotSeat(StructureComponent piece) {
        return recurrent() ? RecurrentPlots.seat(piece) : -1;
    }

    public static void foundAtBirth(World world, StructureStart start) { BeardSite.foundAtBirth(world, start); }

    public static boolean settling(StructureComponent piece) {
        return piece instanceof StructureVillagePieces.Village && !(piece instanceof StructureVillagePieces.Path) && !BeardRails.isRail(piece);
    }

    public static void openAround(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (piece.getClass().getEnclosingClass() == ComponentScatteredFeaturePieces.class) {
            settleFeature(start, piece, world, clip, "temples");
            return;
        }
        if (piece.getClass().getEnclosingClass() == WoodlandMansionPieces.class) {
            settleFeature(start, piece, world, clip, "mansions");
            return;
        }
        if (!settling(piece)) { return; }
        BeardOpen.around(start, piece, world, clip);
    }

    private static void settleFeature(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, String name) {
        ContentBeardSeat.loadModes();
        if (ContentBeardSeat.MODES.getOrDefault(name, ContentBeardSeat.Mode.NONE) == ContentBeardSeat.Mode.NONE) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int worked = BeardGround.bankRing(start, piece, world, box, clip, at);
        int freed = BeardClearing.openOver(start, piece, world, box, clip, at)[0];
        if (worked + freed > 0) { ContentLog.LOGGER.debug("Settled {} at {}, {} into its ground, {} block(s) banked or cut around it and {} opened over it", piece.getClass().getSimpleName(), box.minX, box.minZ, worked, freed); }
    }

    private static StructureStart CURRENT;
    private static List<StructureComponent> LAYING;

    public static void building(@Nullable StructureStart start) { CURRENT = start; }

    public static StructureStart current() { return CURRENT; }

    public static void laying(@Nullable List<StructureComponent> pieces) { LAYING = pieces; }

    @Nullable public static List<StructureComponent> laid() { return LAYING; }

    @Nullable public static List<StructureComponent> components() { return CURRENT != null ? CURRENT.getComponents() : LAYING; }

    @Nullable public static int[] facingStrip(StructureBoundingBox box, StructureBoundingBox near, int mostGap) {
        int lowX = Math.max(box.minX, near.minX);
        int highX = Math.min(box.maxX, near.maxX);
        int lowZ = Math.max(box.minZ, near.minZ);
        int highZ = Math.min(box.maxZ, near.maxZ);
        int overZ = near.minZ - box.maxZ;
        int underZ = box.minZ - near.maxZ;
        if (lowX <= highX && (facing(overZ, mostGap) || facing(underZ, mostGap))) {
            boolean beyond = near.minZ > box.maxZ;
            return new int[] { lowX, highX, beyond ? box.maxZ + 1 : near.maxZ + 1, beyond ? near.minZ - 1 : box.minZ - 1 };
        }
        int overX = near.minX - box.maxX;
        int underX = box.minX - near.maxX;
        if (lowZ <= highZ && (facing(overX, mostGap) || facing(underX, mostGap))) {
            boolean beyond = near.minX > box.maxX;
            return new int[] { beyond ? box.maxX + 1 : near.maxX + 1, beyond ? near.minX - 1 : box.minX - 1, lowZ, highZ };
        }
        return null;
    }

    private static boolean facing(int gap, int mostGap) { return gap >= FACING_GAP && gap <= mostGap; }

    public static int attachGap() { return Math.max(ATTACH_GAP, BeardRoads.pathFullWidth() + 2); }

    public static StructureBoundingBox strip(StructureBoundingBox box, boolean alongX, int from, int to) {
        return alongX ? new StructureBoundingBox(from, box.minY, box.minZ, to, box.maxY, box.maxZ) : new StructureBoundingBox(box.minX, box.minY, from, box.maxX, box.maxY, to);
    }

    @Nullable public static StructureBoundingBox beside(List<StructureComponent> own, StructureBoundingBox strip, boolean alongX, @Nullable StructureComponent laying) { return besideWithin(own, strip, alongX, ContentVillages.blockOf(laying)); }

    public static boolean hugsStreet(List<StructureComponent> own, StructureBoundingBox alley, boolean alongX) { return besideWithin(own, alley, alongX, 0) != null; }

    @Nullable private static StructureBoundingBox besideWithin(List<StructureComponent> own, StructureBoundingBox strip, boolean alongX, int side) {
        for (StructureComponent other : everyone(own)) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            if (BeardPlots.roadAlongX(held) != alongX || Math.min(held.maxX - held.minX, held.maxZ - held.minZ) + 1 <= 3) { continue; }
            int alongOverlap = Math.min(alongX ? strip.maxX : strip.maxZ, alongX ? held.maxX : held.maxZ) - Math.max(alongX ? strip.minX : strip.minZ, alongX ? held.minX : held.minZ);
            if (alongOverlap < 0) { continue; }
            int acrossGap = Math.max((alongX ? held.minZ : held.minX) - (alongX ? strip.maxZ : strip.maxX), (alongX ? strip.minZ : strip.minX) - (alongX ? held.maxZ : held.maxX));
            if (acrossGap > 0 && acrossGap - 1 < side + ContentVillages.blockOf(other)) { return held; }
        }
        return null;
    }

    @Nullable public static StructureComponent roadAt(List<StructureComponent> own, StructureBoundingBox box) {
        for (StructureComponent other : everyone(own)) {
            if (other instanceof StructureVillagePieces.Path && other.getBoundingBox() == box) { return other; }
        }
        return null;
    }

    public static List<StructureComponent> everyone(List<StructureComponent> own) {
        World world = samplerWorld;
        if (world == null) { return own; }
        List<StructureComponent> all = null;
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other.getComponents() == own) { continue; }
            if (all == null) { all = new ArrayList<>(own); }
            all.addAll(other.getComponents());
        }
        List<RailPiece> planned = BeardLinks.planned(world, own);
        if (planned.isEmpty()) { return all == null ? own : all; }
        if (all == null) { all = new ArrayList<>(own); }
        all.addAll(planned);
        return all;
    }

    public static List<StructureComponent> everyone(World world, @Nullable List<StructureComponent> own) {
        List<StructureComponent> all = own == null ? new ArrayList<>() : new ArrayList<>(own);
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other.getComponents() == own) { continue; }
            all.addAll(other.getComponents());
        }
        all.addAll(BeardLinks.planned(world, own));
        return all;
    }

    public static boolean taken(@Nullable List<StructureComponent> own, StructureBoundingBox box) {
        World world = samplerWorld;
        if (world == null) {
            ContentLog.LOGGER.debug("No world is held for the layout, so {} cannot be tested against other villages", box);
            return false;
        }
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other.getComponents() == own) { continue; }
            for (StructureComponent piece : other.getComponents()) {
                if (!piece.getBoundingBox().intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
                ContentLog.LOGGER.debug("{} would stand on {} of the village at {}, {}", box, piece.getClass().getSimpleName(), other.getBoundingBox().minX, other.getBoundingBox().minZ);
                return true;
            }
        }
        for (RailPiece piece : BeardLinks.planned(world, own)) {
            if (!piece.getBoundingBox().intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ) || BeardRails.buriedUnder(piece, box)) { continue; }
            ContentLog.LOGGER.debug("{} would stand on the railway link planned at {}", box, piece.getBoundingBox());
            return true;
        }
        return false;
    }

    public static int roomFor(@Nullable List<StructureComponent> own, StructureBoundingBox box, EnumFacing facing) {
        World world = samplerWorld;
        if (world == null) { return Integer.MAX_VALUE; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int room = Integer.MAX_VALUE;
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other.getComponents() == own) { continue; }
            for (StructureComponent piece : other.getComponents()) {
                StructureBoundingBox met = piece.getBoundingBox();
                if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
                int near = step > 0 ? (alongX ? met.minX : met.minZ) - 1 : (alongX ? met.maxX : met.maxZ) + 1;
                room = Math.min(room, Math.max(0, step > 0 ? near - from + 1 : from - near + 1));
            }
        }
        for (RailPiece piece : BeardLinks.planned(world, own)) {
            StructureBoundingBox met = piece.getBoundingBox();
            if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ) || BeardRails.buriedUnder(piece, box)) { continue; }
            int near = step > 0 ? (alongX ? met.minX : met.minZ) - 1 : (alongX ? met.maxX : met.maxZ) + 1;
            room = Math.min(room, Math.max(0, step > 0 ? near - from + 1 : from - near + 1));
        }
        return room;
    }

    public static int noiseAverage(World world, StructureBoundingBox box) {
        if (BeardSurface.unreadable(world)) { return Integer.MIN_VALUE; }
        long total = 0;
        int count = 0;
        for (int z = box.minZ; z <= box.maxZ; z++) {
            for (int x = box.minX; x <= box.maxX; x++) {
                int sampled = surfaceAt(world, x, z);
                if (sampled < 0) { continue; }
                total += sampled + 1;
                count++;
            }
        }
        return count == 0 ? Integer.MIN_VALUE : (int) (total / count);
    }

    @SuppressWarnings("unchecked") public static IBlockState axised(IBlockState state, boolean alongX) {
        for (net.minecraft.block.properties.IProperty<?> property : state.getPropertyKeys()) {
            if ("axis".equals(property.getName()) && property.getValueClass() == net.minecraft.util.EnumFacing.Axis.class) {
                return state.withProperty((net.minecraft.block.properties.IProperty<net.minecraft.util.EnumFacing.Axis>) property, alongX ? net.minecraft.util.EnumFacing.Axis.X : net.minecraft.util.EnumFacing.Axis.Z);
            }
        }
        return state;
    }

    @SuppressWarnings("unchecked") public static IBlockState faced(IBlockState state, EnumFacing facing) {
        for (net.minecraft.block.properties.IProperty<?> property : state.getPropertyKeys()) {
            if (!"facing".equals(property.getName()) || property.getValueClass() != EnumFacing.class) { continue; }
            net.minecraft.block.properties.IProperty<EnumFacing> which = (net.minecraft.block.properties.IProperty<EnumFacing>) property;
            if (which.getAllowedValues().contains(facing)) { return state.withProperty(which, facing); }
        }
        return state;
    }

    public static int plazaReach() { return 3 + (BeardRoads.pathFullWidth() - 3) / 2; }

    public static boolean wanted() { return WANTED.get(() -> ContentControl.flag(ContentControl.STRUCTURES, "terrainAdaptation", Config.worldgen.terrainAdaptation)); }
}
