package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.blastplaster.util.TreeCollector;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.village.RecurrentVillagePiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardBlocks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGround;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardOpen;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlaza;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.content.worldgen.beard.RecurrentPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.RoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorBeardFields;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructure;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenVillage;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureComponentBox;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
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
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ContentBeard {
    private static final int RADIUS = 12;
    private static final int SIZE = 24;
    private static final double SCALE = 8.0D;
    private static final float[] KERNEL = new float[SIZE * SIZE * SIZE];
    static {
        for (int z = 0; z < SIZE; z++) {
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) { KERNEL[z * SIZE * SIZE + x * SIZE + y] = (float) seed(x - RADIUS, (double) (y - RADIUS) + 0.5D, z - RADIUS); }
            }
        }
    }

    public enum Mode { NONE, BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE }

    public static final int BAND = 6;
    private static final Map<String, Mode> MODES = new LinkedHashMap<>();
    private static boolean modesLoaded;
    private static boolean applying;
    private static boolean layingBuilding;
    private static int peeks;
    private static WorldTemplateDef wantedFrom;
    private static boolean wantedHeld;
    private static boolean wantedKnown;
    private static Boolean recurrent;
    public static final int SITE_REACH = 2;
    public static final int SITE_TOLERANCE = 10;
    private static final int ATTACH_GAP = 8;
    public static final int FACING_GAP = 2;
    public static final int SITE_SEPARATION = 8;
    public static final int FOOTING_COURSE = 1;
    public static final long NO_SITE = Long.MIN_VALUE;
    private static final ChunkPrimer UNUSED = new ChunkPrimer();
    public static ChunkGeneratorOverworld sampler;
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
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return 32; }
        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        if (!(maker instanceof ChunkGeneratorOverworld)) { return 32; }
        return ((IMapGenVillage) ((IChunkGeneratorBeardFields) maker).rdpl$villages()).rdpl$distance();
    }

    public static long siteIn(World world, ContentSites known, int cellX, int cellZ, int spacing) { return BeardSite.siteFor(world, known, cellX, cellZ, spacing); }

    @SubscribeEvent public static void onDressed(PopulateChunkEvent.Post event) {
        if (event.getWorld().isRemote || !wanted()) { return; }
        Collection<StructureStart> starts = ContentStructureSearch.villageStarts(event.getWorld());
        if (starts.isEmpty()) { return; }
        int blockX = (event.getChunkX() << 4) + 8;
        int blockZ = (event.getChunkZ() << 4) + 8;
        StructureBoundingBox clip = new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (StructureStart start : starts) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            building(start);
            try {
                BeardRoads.repairRoads(event.getWorld(), start);
                int seams = BeardGround.levelSeams(start, event.getWorld(), clip, at);
                if (seams > 0) { ContentLog.LOGGER.debug("Filled {} block(s) of groove left between the plots of the village at {}, {}, where two aprons met without meeting", seams, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                int freed = BeardGround.freeDoors(start, event.getWorld(), clip, at);
                if (freed > 0) { ContentLog.LOGGER.debug("Freed {} block(s) that had closed a doorway of the village at {}, {} after the roads were repaired", freed, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                int swept = BeardGround.sweepOrphanedLeaves(start, event.getWorld(), clip, at);
                if (swept > 0) { ContentLog.LOGGER.debug("Swept {} orphaned leaf block(s) no trunk sustains around the village at {}, {}, left behind where a felled tree crossed a chunk edge", swept, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
                for (StructureComponent piece : start.getComponents()) {
                    if (!(piece instanceof StructureVillagePieces.Village)) { continue; }
                    StructureBoundingBox box = piece.getBoundingBox();
                    int near = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece) ? 6 : 2;
                    if (box.minX - near > clip.maxX || box.maxX + near < clip.minX || box.minZ - near > clip.maxZ || box.maxZ + near < clip.minZ) { continue; }
                    int felled = fellAround(event.getWorld(), start, piece, box, clip, at, false);
                    if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
                    if (piece instanceof StructureVillagePieces.Path) {
                        int[] ring = BeardGround.openOver(start, piece, event.getWorld(), box, clip, at);
                        if (ring[0] > 0) { ContentLog.LOGGER.debug("Opened {} block(s) over the roadway of Path at {}, {}", ring[0], box.minX, box.minZ); }
                        lampPosts(start, piece, event.getWorld(), box, clip, at);
                    }
                    else if (!box.intersectsWith(clip)) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} reaches into chunk {}, {} that its own box misses, so its ring is opened there now", piece.getClass().getSimpleName(), box.minX, box.minZ, event.getChunkX(), event.getChunkZ()); }
                        openAround(start, piece, event.getWorld(), clip);
                    }
                }
            }
            finally { building(null); }
        }
    }

    public static void fellFor(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village)) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        int near = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece) ? 6 : 2;
        if (box.minX - near > clip.maxX || box.maxX + near < clip.minX || box.minZ - near > clip.maxZ || box.maxZ + near < clip.minZ) { return; }
        int felled = fellAround(world, start, piece, box, clip, new BlockPos.MutableBlockPos(), true);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before {} at {}, {} was built", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
    }

    private static int fellAround(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, boolean bare) {
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        int top = piece instanceof StructureVillagePieces.Well ? box.maxY + 1 : box.maxY;
        boolean bulb = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece);
        int reach = bulb ? 6 : 2;
        int floor = bulb ? box.minY - 8 : box.minY + 1;
        for (int x = box.minX - reach; x <= box.maxX + reach; x++) {
            for (int z = box.minZ - reach; z <= box.maxZ + reach; z++) {
                for (int y = floor; y <= box.maxY + 16; y++) {
                    if (!bare && !bulb && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ && y <= top) { continue; }
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (mctmods.blastplaster.util.BlastPlasterUtil.isTreeWood(held)) { seeds.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.LEAVES) { canopy.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.VINE) { felled += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        Predicate<BlockPos> within = BeardPlots.outside(world, start, piece, box, !bare && !bulb, top);
        Set<BlockPos> felledLogs = new HashSet<>();
        for (BlockPos seed : seeds) {
            if (felledLogs.contains(seed)) { continue; }
            TreeCollector.Tree tree = TreeCollector.collect(world, seed, mctmods.blastplaster.Config.view(world).getMaxTreeSize(), within);
            for (BlockPos log : tree.logs) {
                felledLogs.add(log);
                at.setPos(log.getX(), log.getY(), log.getZ());
                felled += BeardBlocks.clearAt(world, at);
            }
            for (BlockPos leaf : tree.leaves) {
                at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
                felled += BeardBlocks.clearAt(world, at);
            }
            for (BlockPos log : tree.logs) {
                for (int x = log.getX() - 5; x <= log.getX() + 5; x++) {
                    for (int z = log.getZ() - 5; z <= log.getZ() + 5; z++) {
                        for (int y = log.getY() - 5; y <= log.getY() + 5; y++) {
                            at.setPos(x, y, z);
                            if (!within.test(at)) { continue; }
                            IBlockState held = world.getBlockState(at);
                            if (held.getMaterial() != Material.LEAVES) { continue; }
                            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
                            if (sustained(world, at.toImmutable(), within)) { continue; }
                            felled += BeardBlocks.clearAt(world, at);
                        }
                    }
                }
            }
        }
        for (BlockPos leaf : canopy) {
            IBlockState held = world.getBlockState(leaf);
            if (held.getMaterial() != Material.LEAVES) { continue; }
            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
            if (sustained(world, leaf, within)) { continue; }
            at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
            felled += BeardBlocks.clearAt(world, at);
        }
        return felled;
    }

    private static boolean sustained(World world, BlockPos leaf, Predicate<BlockPos> within) { return BeardGround.sustainer(world, leaf, within) != null; }

    private static void lampPosts(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (CityGrowth.bulbWide(piece)) {
            bulbLamps(start, piece, world, box, clip, at);
            return;
        }
        boolean alongX = BeardPlots.roadAlongX(box);
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        if (to - from < 4) { return; }
        Random rand = new Random(world.getSeed() ^ ((long) box.minX << 32) ^ box.minZ);
        List<Integer> along = new ArrayList<>();
        along.add(from);
        for (int step = from + 7 + rand.nextInt(6); step < to - 1; step += 7 + rand.nextInt(6)) { along.add(step); }
        along.add(to);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (!box.intersectsWith(met)) { continue; }
            int meeting = alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ);
            if (meeting >= from && meeting <= to && !along.contains(meeting)) { along.add(meeting); }
        }
        int span = (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1;
        int off = BeardRoads.pathSidewalkWidth() > 0 && span == BeardRoads.pathFullWidth() ? 0 : 1;
        BeardRoads.Grade grade = piece instanceof RoadLayout ? ((RoadLayout) piece).rdpl$layout() : null;
        if (grade == null) { grade = BeardRoads.roadProfile(world, piece, alongX, from, to, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, true); }
        int raised = 0;
        for (int spot : along) {
            int roadTop = grade == null ? Integer.MIN_VALUE : grade.at(spot);
            for (int side = 0; side < 2; side++) {
                int x = alongX ? spot : (side == 0 ? box.minX - off : box.maxX + off);
                int z = alongX ? (side == 0 ? box.minZ - off : box.maxZ + off) : spot;
                if (lampBlocked(world, start, piece, x, z)) { continue; }
                if (raise(start, piece, world, clip, at, x, z, box, roadTop)) {
                    raised++;
                    break;
                }
            }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) along the road at {}, {}", raised, box.minX, box.minZ); }
    }

    private static void bulbLamps(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        BeardRoads.Bulb bulb = BeardRoads.bulbAt(world, piece);
        if (bulb == null) { return; }
        int walk = BeardRoads.pathSidewalkWidth();
        int ring = walk > 0 ? bulb.r : bulb.r + 1;
        int spots = Math.max(1, (int) Math.round(2.0D * Math.PI * ring));
        Random rand = new Random(world.getSeed() ^ ((long) box.minX << 32) ^ box.minZ);
        int raised = 0;
        for (int step = 0; step + 4 < spots; step += 7 + rand.nextInt(6)) {
            double angle = 2.0D * Math.PI * step / spots;
            int x = Integer.MIN_VALUE;
            int z = Integer.MIN_VALUE;
            for (int back = 0; back <= 2 && x == Integer.MIN_VALUE; back++) {
                int tryX = bulb.cx + (int) Math.round((ring - back) * Math.cos(angle));
                int tryZ = bulb.cz + (int) Math.round((ring - back) * Math.sin(angle));
                if (walk > 0 && !bulb.pavedAt(tryX, tryZ)) { continue; }
                x = tryX;
                z = tryZ;
            }
            if (x == Integer.MIN_VALUE || bulb.throatAt(x, z)) { continue; }
            if (lampBlocked(world, start, piece, x, z)) { continue; }
            if (raise(start, piece, world, clip, at, x, z, box, bulb.level)) { raised++; }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) around the cul-de-sac at {}, {}", raised, box.minX, box.minZ); }
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

    private static boolean lampPost(World world, int x, int y, int z, StructureStart start, StructureComponent piece, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (lampStructure(world, x, y, z)) { return true; }
        ContentStates.Spec post = BeardRoads.pathSpec("villagePathLampBlock", Config.worldgen.villagePathLampBlock);
        if (post == null || post.state.getBlock() == Blocks.AIR) { return false; }
        int high = lampHeight();
        for (int step = 0; step < high; step++) {
            at.setPos(x, y + step, z);
            ContentStates.place(world, at.toImmutable(), post.state, post.tag);
            BeardKeep.holdSpot(x, y + step, z);
        }
        ContentStates.Spec head = BeardRoads.pathSpec("villagePathLampTopBlock", Config.worldgen.villagePathLampTopBlock);
        if (head != null && head.state.getBlock() != Blocks.AIR) {
            at.setPos(x, y + high, z);
            ContentStates.place(world, at.toImmutable(), head.state, head.tag);
            BeardKeep.holdSpot(x, y + high, z);
        }
        ContentStates.Spec side = BeardRoads.pathSpec("villagePathLampSideBlock", Config.worldgen.villagePathLampSideBlock);
        if (side == null || side.state.getBlock() == Blocks.AIR) { return true; }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            at.setPos(x + facing.getXOffset(), y + high, z + facing.getZOffset());
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
            if (world.getBlockState(at).getMaterial() != Material.AIR) { continue; }
            ContentStates.place(world, at.toImmutable(), faced(side.state, facing), side.tag);
            BeardKeep.holdSpot(at.getX(), at.getY(), at.getZ());
        }
        return true;
    }

    private static boolean lampStructure(World world, int x, int y, int z) {
        String named = ContentControl.text(ContentControl.VILLAGES, "villagePathLampStructure", Config.worldgen.villagePathLampStructure);
        if (named.isEmpty() || !(world instanceof WorldServer)) { return false; }
        WorldServer server = (WorldServer) world;
        Template loaded = server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(named));
        if (loaded == null) {
            ContentLog.LOGGER.error("villagePathLampStructure names '{}', which could not be loaded, so no lamp is placed", named);
            return false;
        }
        BlockPos span = loaded.getSize();
        BlockPos origin = new BlockPos(x - span.getX() / 2, y, z - span.getZ() / 2);
        loaded.addBlocksToWorld(world, origin, new PlacementSettings().setIgnoreEntities(true), 2);
        for (int stepX = 0; stepX < span.getX(); stepX++) {
            for (int stepY = 0; stepY < span.getY(); stepY++) {
                for (int stepZ = 0; stepZ < span.getZ(); stepZ++) { BeardKeep.holdSpot(origin.getX() + stepX, origin.getY() + stepY, origin.getZ() + stepZ); }
            }
        }
        return true;
    }

    public static IBlockState lampBlock() { return BeardRoads.pathBlock("villagePathLampBlock", Config.worldgen.villagePathLampBlock, Blocks.AIR.getDefaultState()); }

    public static IBlockState lampTop() { return BeardRoads.pathBlock("villagePathLampTopBlock", Config.worldgen.villagePathLampTopBlock, Blocks.AIR.getDefaultState()); }

    public static int lampHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathLampHeight", Config.worldgen.villagePathLampHeight)); }

    public static boolean beforeADoor(World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int bed, int z) {
        StructureBoundingBox reach = new StructureBoundingBox(clip.minX - 2, clip.minY, clip.minZ - 2, clip.maxX + 2, clip.maxY, clip.maxZ + 2);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int y = bed; y <= bed + 3; y++) {
                    at.setPos(x + dx, y, z + dz);
                    if (!reach.isVecInside(at)) { continue; }
                    if (world.getBlockState(at).getBlock() instanceof BlockDoor) { return true; }
                    if (doorwayAt(world, at, x + dx, y, z + dz)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean lampBlocked(World world, StructureStart start, StructureComponent piece, int x, int z) {
        if (inPlaza(start, x, z) || onPaving(start, piece, x, z)) { return true; }
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other == start) { continue; }
            if (inPlaza(other, x, z) || onPaving(other, piece, x, z)) { return true; }
        }
        return false;
    }

    private static boolean inPlaza(StructureStart start, int x, int z) { return BeardPlots.insidePlaza(start.getComponents(), x, z); }

    private static boolean onPaving(StructureStart start, StructureComponent piece, int x, int z) {
        int reach = (BeardRoads.pathFullWidth() - 3) / 2;
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox held = ((IStructureComponentBox) other).rdpl$box();
            if (held == null) { continue; }
            if (other == piece) {
                if (CityGrowth.bulbWide(other)) { continue; }
                if (x < held.minX || x > held.maxX || z < held.minZ || z > held.maxZ) { continue; }
                boolean alongX = BeardPlots.roadAlongX(held);
                int center = alongX ? (held.minZ + held.maxZ) / 2 : (held.minX + held.maxX) / 2;
                int span = (alongX ? held.maxZ - held.minZ : held.maxX - held.minX) + 1;
                int roadway = Math.min((span - 1) / 2, 1 + BeardRoads.pathExtraWidth() + BeardRoads.pathLineColumns());
                if (Math.abs((alongX ? z : x) - center) <= roadway) { return true; }
                continue;
            }
            if (x >= held.minX - reach && x <= held.maxX + reach && z >= held.minZ - reach && z <= held.maxZ + reach) { return true; }
        }
        return false;
    }

    private static boolean raise(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z, StructureBoundingBox box, int roadTop) {
        at.setPos(x, box.minY, z);
        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { return false; }
        int bed = Integer.MIN_VALUE;
        for (int y = box.minY + 6; y >= box.minY - 3; y--) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at)) { continue; }
            Material material = world.getBlockState(at).getMaterial();
            if (material.isLiquid()) { return false; }
            if (!material.isSolid()) { continue; }
            bed = y;
            break;
        }
        if (bed == Integer.MIN_VALUE) { return false; }
        for (int y = bed - 1; y >= bed - 3; y--) {
            at.setPos(x, y, z);
            if (!world.getBlockState(at).getMaterial().isSolid()) { return false; }
        }
        int stood = bed;
        at.setPos(x, stood, z);
        if (roadTop != Integer.MIN_VALUE && roadTop != bed && BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { bed = roadTop; }
        if (bed < stood) {
            for (int y = stood; y > bed; y--) {
                at.setPos(x, y, z);
                if (!clip.isVecInside(at) || BeardKeep.holds(x, y, z)) { return false; }
                if (!BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { return false; }
                BeardBlocks.clearAt(world, at);
            }
        }
        while (bed > box.minY - 3 && world.getBlockState(at.setPos(x, bed, z)).getBlock() == Blocks.AIR) {
            IBlockState under = world.getBlockState(at.setPos(x, bed - 1, z));
            if (!under.getMaterial().isSolid() || BeardBlocks.terrainBlock(under.getBlock())) { break; }
            bed--;
        }
        for (int y = bed + 1; y <= bed + lampHeight() + 1; y++) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { return false; }
            if (BeardKeep.holds(x, y, z)) { return false; }
            if (!world.getBlockState(at).getMaterial().isReplaceable() && world.getBlockState(at).getMaterial() != Material.AIR) { return false; }
        }
        if (beforeADoor(world, clip, at, x, bed, z)) { return false; }
        at.setPos(x, bed, z);
        if (!world.getBlockState(at).getMaterial().isSolid()) { return false; }
        return lampPost(world, x, bed + 1, z, start, piece, clip, at);
    }

    public static void wellPlaza(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) { BeardPlaza.wellPlaza(start, piece, world, clip); }

    public static Boolean flatSite(World world, int chunkX, int chunkZ, int spacing) { return BeardSite.flatSite(world, chunkX, chunkZ, spacing); }

    public static BlockPos nearestSite(World world, BlockPos from, int spacing, boolean findUnexplored, long budgetNanos) { return BeardSite.nearestSite(world, from, spacing, findUnexplored, budgetNanos); }

    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) { return BeardSite.mansionCandidateNear(world, chunkX, chunkZ); }

    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) { return BeardSite.lowestIn(worldIn, minX, minZ, maxX, maxZ, clip); }

    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces, int sink) { return BeardSite.footingMisfit(box, pieces, sink, CityGrowth.give()); }

    public static int footingSink(StructureComponent piece) {
        if (piece instanceof RecurrentVillagePiece) { return ((RecurrentVillagePiece) piece).footingSink(); }
        if (recurrent == null) { recurrent = Loader.isModLoaded("reccomplex"); }
        return recurrent ? RecurrentPlots.sink(piece) : 0;
    }

    public static int groundCourse(StructureComponent piece) {
        if (piece instanceof RecurrentVillagePiece) { return ((RecurrentVillagePiece) piece).groundCourses(); }
        if (recurrent == null) { recurrent = Loader.isModLoaded("reccomplex"); }
        return recurrent ? RecurrentPlots.groundCourse(piece) : 0;
    }

    public static int plotSeat(StructureComponent piece) {
        if (recurrent == null) { recurrent = Loader.isModLoaded("reccomplex"); }
        return recurrent ? RecurrentPlots.seat(piece) : -1;
    }

    public static void foundAtBirth(StructureStart start) { BeardSite.foundAtBirth(start); }

    public static void openAround(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (piece.getClass().getEnclosingClass() == ComponentScatteredFeaturePieces.class) {
            settleFeature(start, piece, world, clip, "temples");
            return;
        }
        if (piece.getClass().getEnclosingClass() == WoodlandMansionPieces.class) {
            settleFeature(start, piece, world, clip, "mansions");
            return;
        }
        if (!(piece instanceof StructureVillagePieces.Village) || piece instanceof StructureVillagePieces.Path) { return; }
        BeardOpen.around(start, piece, world, clip);
    }

    private static void settleFeature(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, String name) {
        loadModes();
        if (MODES.getOrDefault(name, Mode.NONE) == Mode.NONE) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int worked = BeardGround.bankRing(start, piece, world, box, clip, at);
        int freed = BeardGround.openOver(start, piece, world, box, clip, at)[0];
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

    public static void attach(StructureStart start, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }
        StructureBoundingBox box = ((IStructureComponentBox) piece).rdpl$box();
        if (box == null) { return; }
        boolean alongX = BeardPlots.roadAlongX(piece);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null) { continue; }
            boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
            if (!lined) { continue; }
            if (BeardPlots.roadAlongX(other) == alongX) {
                int ownCenter = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
                int metCenter = alongX ? (met.minZ + met.maxZ) / 2 : (met.minX + met.maxX) / 2;
                if (ownCenter != metCenter) { continue; }
            }
            int ahead = alongX ? met.minX - box.maxX : met.minZ - box.maxZ;
            int behind = alongX ? box.minX - met.maxX : box.minZ - met.maxZ;
            int from = (alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ)) + 1;
            int to = (alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ)) - 1;
            if (ahead > 1 && ahead <= attachGap() && free(start.getComponents(), piece, box, alongX, from, to) && uncrossed(start.getComponents(), piece, other, alongX, from, to, box)) {
                if (alongX) { box.maxX = met.minX - 1; }
                else { box.maxZ = met.minZ - 1; }
            }
            else if (behind > 1 && behind <= attachGap() && free(start.getComponents(), piece, box, alongX, from, to) && uncrossed(start.getComponents(), piece, other, alongX, from, to, box)) {
                if (alongX) { box.minX = met.maxX + 1; }
                else { box.minZ = met.maxZ + 1; }
            }
        }
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            square(start.getComponents(), piece, box, alongX, met);
        }
    }

    public static boolean joins(List<StructureComponent> pieces, StructureComponent piece, StructureComponent other) {
        if (!(piece instanceof StructureVillagePieces.Path) || !(other instanceof StructureVillagePieces.Path)) { return false; }
        StructureBoundingBox box = piece.getBoundingBox();
        StructureBoundingBox met = other.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
        if (!lined) { return false; }
        if (BeardPlots.roadAlongX(other) == alongX) {
            int ownCenter = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            int metCenter = alongX ? (met.minZ + met.maxZ) / 2 : (met.minX + met.maxX) / 2;
            if (ownCenter != metCenter) { return false; }
        }
        int gap = alongX ? Math.max(met.minX - box.maxX, box.minX - met.maxX) : Math.max(met.minZ - box.maxZ, box.minZ - met.maxZ);
        if (gap <= 1) { return true; }
        if (gap > attachGap()) { return false; }
        int from = (alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ)) + 1;
        int to = (alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ)) - 1;
        return free(pieces, piece, box, alongX, from, to) && uncrossed(pieces, piece, other, alongX, from, to, box);
    }

    private static boolean uncrossed(List<StructureComponent> pieces, StructureComponent piece, StructureComponent met, boolean alongX, int from, int to, StructureBoundingBox box) {
        int minX = alongX ? from : box.minX;
        int maxX = alongX ? to : box.maxX;
        int minZ = alongX ? box.minZ : from;
        int maxZ = alongX ? box.maxZ : to;
        for (StructureComponent other : pieces) {
            if (other == piece || other == met || !(other instanceof StructureVillagePieces.Path)) { continue; }
            if (BeardPlots.roadAlongX(other) == alongX) { continue; }
            if (other.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { return false; }
        }
        return true;
    }

    @Nullable private static int[] cornerRows(StructureBoundingBox box, boolean alongX, StructureBoundingBox met, boolean back) {
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int metAlongLeast = alongX ? met.minZ : met.minX;
        int metAlongMost = alongX ? met.maxZ : met.maxX;
        if (metAlongMost < acrossLeast - 1 || metAlongLeast > acrossMost + 1) { return null; }
        int metAcrossLeast = alongX ? met.minX : met.minZ;
        int metAcrossMost = alongX ? met.maxX : met.maxZ;
        if (back) {
            int least = alongX ? box.minX : box.minZ;
            return least > metAcrossLeast && least <= metAcrossMost ? new int[] { metAcrossLeast, least - 1 } : null;
        }
        int most = alongX ? box.maxX : box.maxZ;
        return most < metAcrossMost && most >= metAcrossLeast ? new int[] { most + 1, metAcrossMost } : null;
    }

    private static boolean plazaHeld(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX, int least, int most) {
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (StructureBoundingBox square : BeardPlots.plazaSquares(pieces)) {
            int alongLo = alongX ? square.minX : square.minZ;
            int alongHi = alongX ? square.maxX : square.maxZ;
            int acrossLo = alongX ? square.minZ : square.minX;
            int acrossHi = alongX ? square.maxZ : square.maxX;
            if (least >= alongLo && most <= alongHi && acrossLeast >= acrossLo && acrossMost <= acrossHi) { return true; }
        }
        return false;
    }

    private static boolean square(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureBoundingBox box, boolean alongX, StructureBoundingBox met) {
        boolean grew = false;
        int[] backRows = cornerRows(box, alongX, met, true);
        if (backRows != null && plazaHeld(pieces, box, alongX, backRows[0], backRows[1])) { backRows = null; }
        if (backRows != null) {
            if (free(pieces, piece, box, alongX, backRows[0], backRows[1])) {
                if (alongX) { box.minX = backRows[0]; }
                else { box.minZ = backRows[0]; }
                grew = true;
                ContentLog.LOGGER.debug("The road at {}, {} squares its corner back from {} to {} to line up with the road at {}, {}", box.minX, box.minZ, backRows[1] + 1, backRows[0], met.minX, met.minZ);
            }
            else { ContentLog.LOGGER.debug("The road at {}, {} keeps its corner at {} rather than squaring to {}, those rows being taken", box.minX, box.minZ, backRows[1] + 1, backRows[0]); }
        }
        int[] outRows = cornerRows(box, alongX, met, false);
        if (outRows != null && plazaHeld(pieces, box, alongX, outRows[0], outRows[1])) { outRows = null; }
        if (outRows != null) {
            if (free(pieces, piece, box, alongX, outRows[0], outRows[1])) {
                if (alongX) { box.maxX = outRows[1]; }
                else { box.maxZ = outRows[1]; }
                grew = true;
                ContentLog.LOGGER.debug("The road at {}, {} squares its corner out from {} to {} to line up with the road at {}, {}", box.minX, box.minZ, outRows[0] - 1, outRows[1], met.minX, met.minZ);
            }
            else { ContentLog.LOGGER.debug("The road at {}, {} keeps its corner at {} rather than squaring to {}, those rows being taken", box.minX, box.minZ, outRows[0] - 1, outRows[1]); }
        }
        return grew;
    }

    public static boolean claimCorners(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX) {
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            int[] back = cornerRows(box, alongX, met, true);
            if (back != null && !plazaHeld(pieces, box, alongX, back[0], back[1]) && !free(pieces, null, box, alongX, back[0], back[1])) { return false; }
            int[] out = cornerRows(box, alongX, met, false);
            if (out != null && !plazaHeld(pieces, box, alongX, out[0], out[1]) && !free(pieces, null, box, alongX, out[0], out[1])) { return false; }
            int[] metBack = cornerRows(met, !alongX, box, true);
            if (metBack != null && !plazaHeld(pieces, met, !alongX, metBack[0], metBack[1]) && !free(pieces, other, met, !alongX, metBack[0], metBack[1])) { return false; }
            int[] metOut = cornerRows(met, !alongX, box, false);
            if (metOut != null && !plazaHeld(pieces, met, !alongX, metOut[0], metOut[1]) && !free(pieces, other, met, !alongX, metOut[0], metOut[1])) { return false; }
        }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            square(pieces, null, box, alongX, met);
            if (square(pieces, other, met, !alongX, box) && other instanceof RoadLayout) { ((RoadLayout) other).rdpl$layout(null); }
        }
        return true;
    }

    private static boolean free(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureBoundingBox box, boolean alongX, int least, int most) {
        for (StructureComponent other : pieces) {
            if (other == piece) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            boolean acrossed = alongX ? held.maxZ >= box.minZ && held.minZ <= box.maxZ : held.maxX >= box.minX && held.minX <= box.maxX;
            boolean along = alongX ? held.maxX >= least && held.minX <= most : held.maxZ >= least && held.minZ <= most;
            if (!acrossed || !along) { continue; }
            if (!(other instanceof StructureVillagePieces.Path)) { return false; }
            if (BeardPlots.roadAlongX(other) != alongX) { continue; }
            return false;
        }
        return true;
    }

    public static int noiseAverage(World world, StructureBoundingBox box) {
        if (samplerFor(world) == null) { return Integer.MIN_VALUE; }
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

    public static boolean wanted() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (!wantedKnown || active != wantedFrom) {
            wantedHeld = ContentControl.flag(ContentControl.STRUCTURES, "terrainAdaptation", Config.worldgen.terrainAdaptation);
            wantedFrom = active;
            wantedKnown = true;
        }
        return wantedHeld;
    }

    public static void apply(World world, ChunkGeneratorOverworld generator, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        if (applying) { return; }
        applying = true;
        try { seat(world, generator, generators, names, heightMap, chunkX, chunkZ); }
        finally { applying = false; }
    }

    private static void seat(World world, ChunkGeneratorOverworld generator, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        loadModes();
        double[] before = heightMap.clone();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        StructureBoundingBox reach = new StructureBoundingBox(blockX - RADIUS, 0, blockZ - RADIUS, blockX + 15 + RADIUS, 255, blockZ + 15 + RADIUS);
        List<StructureBoundingBox> boxes = new ArrayList<>();
        List<Integer> bases = new ArrayList<>();
        List<Mode> modes = new ArrayList<>();
        for (int at = 0; at < generators.length; at++) {
            Mode mode = MODES.getOrDefault(names[at], Mode.NONE);
            if (mode == Mode.NONE) { continue; }
            sampler = generator;
            samplerWorld = world;
            generators[at].generate(world, chunkX, chunkZ, UNUSED);
            sampler = null;
            samplerWorld = null;
            for (StructureStart start : ((IMapGenStructure) generators[at]).rdpl$getStructureMap().values()) {
                if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(reach)) { continue; }
                for (StructureComponent piece : start.getComponents()) {
                    if (!piece.getBoundingBox().intersectsWith(reach)) { continue; }
                    if (piece instanceof StructureVillagePieces.Start) { continue; }
                    boxes.add(piece.getBoundingBox());
                    bases.add(piece.getBoundingBox().minY + 1);
                    modes.add(mode);
                }
            }
        }
        System.arraycopy(before, 0, heightMap, 0, heightMap.length);
        if (boxes.isEmpty()) { return; }
        if (ContentLog.LOGGER.debugEnabled()) {
            int lowest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            for (Integer base : bases) {
                lowest = Math.min(lowest, base);
                highest = Math.max(highest, base);
            }
            ContentLog.LOGGER.debug("Seating {} structure piece(s) under chunk {}, {}, on bases from y {} to y {}", boxes.size(), chunkX, chunkZ, lowest, highest);
        }
        int at = 0;
        for (int gridX = 0; gridX < 5; gridX++) {
            int x = blockX + gridX * 4;
            for (int gridZ = 0; gridZ < 5; gridZ++) {
                int z = blockZ + gridZ * 4;
                for (int gridY = 0; gridY < 33; gridY++) {
                    heightMap[at] += sink(boxes, bases, modes, x, gridY * 8, z) * SCALE;
                    at++;
                }
            }
        }
    }

    private static double sink(List<StructureBoundingBox> boxes, List<Integer> bases, List<Mode> modes, int x, int y, int z) {
        double sum = 0.0D;
        for (int i = 0; i < boxes.size(); i++) {
            StructureBoundingBox box = boxes.get(i);
            int dx = Math.max(0, Math.max(box.minX - x, x - box.maxX));
            int dz = Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
            if (dx > RADIUS || dz > RADIUS) { continue; }
            int base = bases.get(i);
            int dy = y - base;
            Mode mode = modes.get(i);
            if (mode == Mode.BURY) { sum += bury(dx, dy / 2.0D, dz); }
            else if (mode == Mode.BEARD_THIN) { sum += beard(dx, dy, dz, dy) * 0.8D; }
            else if (mode == Mode.BEARD_BOX) { sum += beard(dx, Math.max(0, Math.max(base - y, y - box.maxY)), dz, dy) * 0.8D; }
            else if (mode == Mode.ENCAPSULATE) { sum += bury(dx / 2.0D, Math.max(0, Math.max(box.minY - y, y - box.maxY)) / 2.0D, dz / 2.0D) * 0.8D; }
        }
        return sum;
    }

    private static double bury(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length >= 6.0D) { return 0.0D; }
        return 1.0D - length / 6.0D;
    }

    private static void loadModes() {
        if (modesLoaded) { return; }
        modesLoaded = true;
        MODES.put("villages", Mode.BEARD_THIN);
        MODES.put("mansions", Mode.BEARD_THIN);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation)) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                ContentLog.LOGGER.error("structureAdaptation entry '{}' is not structure=mode, ignoring it", entry);
                continue;
            }
            String name = parts[0].trim().toLowerCase(Locale.ROOT);
            String asked = parts[1].trim().toUpperCase(Locale.ROOT);
            if (("temples".equals(name) || "mansions".equals(name)) && !"NONE".equals(asked) && !"BEARD_THIN".equals(asked)) {
                ContentLog.LOGGER.error("structureAdaptation asks for {}={}, but that structure settles itself only as it is built, so the terrain cannot be shaped for it beforehand. Only beard_thin is offered there, which banks the ground around it once it stands", name, parts[1].trim());
                continue;
            }
            try { MODES.put(name, Mode.valueOf(asked)); }
            catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("structureAdaptation entry '{}' asks for mode '{}', which is not none, bury, beard_thin, beard_box or encapsulate, ignoring it", entry, parts[1].trim()); }
        }
    }

    private static double beard(int x, int y, int z, int height) {
        int atX = x + RADIUS;
        int atY = y + RADIUS;
        int atZ = z + RADIUS;
        if (atX < 0 || atX >= SIZE || atY < 0 || atY >= SIZE || atZ < 0 || atZ >= SIZE) { return 0.0D; }
        double lifted = (double) height + 0.5D;
        double squared = (double) x * (double) x + lifted * lifted + (double) z * (double) z;
        double falloff = -lifted * MathHelper.fastInvSqrt(squared / 2.0D) / 2.0D;
        return falloff * (double) KERNEL[atZ * SIZE * SIZE + atX * SIZE + atY];
    }

    private static double seed(int x, double y, int z) {
        double squared = (double) x * (double) x + y * y + (double) z * (double) z;
        return Math.pow(Math.E, -squared / 16.0D);
    }
}
