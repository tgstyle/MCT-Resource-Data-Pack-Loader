package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardBlocks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGround;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlaza;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.mixin.*;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import mctmods.blastplaster.util.TreeCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
    public static final int SITE_REACH = 2;
    public static final int SITE_TOLERANCE = 10;
    private static final int ATTACH_GAP = 8;
    private static final int FACING_GAP = 2;
    public static final int SITE_SEPARATION = 8;
    public static final long NO_SITE = Long.MIN_VALUE;
    private static final ChunkPrimer UNUSED = new ChunkPrimer();
    public static ChunkGeneratorOverworld sampler;
    public static World samplerWorld;

    private ContentBeard() {}

    public static void layingBuilding(boolean now) { layingBuilding = now; }

    private static final class Grade {
        private final int[] profile;
        private final int[] ground;
        private final boolean[] bridged;
        private final int start;
        private final int capped;
        private Grade(int[] profile, int[] ground, boolean[] bridged, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.start = start;
            this.capped = capped;
        }

        private int at(int row) { return profile[Math.max(0, Math.min(profile.length - 1, row - start))]; }
    }

    @Nullable private static Grade roadProfile(World world, @Nullable StructureComponent piece, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, boolean junctions) {
        int[] profile = BeardGrade.noiseProfile(world, alongX, rowLeast, rowMost, acrossLeast, acrossMost);
        if (profile == null) { return null; }

        int[] ground = profile.clone();
        BeardGrade.flatRuns(world, alongX, rowLeast, acrossLeast, acrossMost, profile);
        boolean[] bridged = BeardGrade.smooth(profile);
        boolean[] pinned = new boolean[profile.length];
        boolean[] plaza = new boolean[profile.length];
        int capped;
        if (junctions) {
            roadApron(world, piece, alongX, rowLeast, rowMost, acrossLeast, acrossMost, profile, pinned);
            clampToWell(alongX, rowLeast, acrossLeast, acrossMost, profile, plaza);
            for (int i = 0; i < pinned.length; i++) { if (plaza[i]) { pinned[i] = true; } }
            BeardGrade.settle(profile, pinned);
            boolean[] fixed = plaza.clone();
            capped = 0;
            for (int pass = 0; pass < 4; pass++) {
                int clamped = BeardGrade.capEmbankment(profile, ground, bridged, fixed);
                capped += clamped;
                if (clamped == 0) { break; }

                boolean[] hold = new boolean[profile.length];
                for (int i = 0; i < hold.length; i++) { hold[i] = fixed[i] || (pinned[i] && profile[i] <= ground[i] + BeardGrade.CAP); }
                BeardGrade.settle(profile, hold);
            }
        }
        else { capped = BeardGrade.capEmbankment(profile, ground, bridged, plaza); }
        return new Grade(profile, ground, bridged, rowLeast, capped);
    }

    public static int roadReach(StructureBoundingBox box, EnumFacing facing) {
        World world = samplerWorld;
        if (world == null || facing == null || samplerFor(world) == null) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} cannot run: world {}, facing {}, sampler {}", box.minX, box.minZ, world == null ? "none" : "held", facing, world == null || samplerFor(world) == null ? "none" : "held"); }
            return Integer.MAX_VALUE;
        }

        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (int length = rows; length >= 7; length -= 7) {
            int far = from + step * (length - 1);
            int rowLeast = Math.min(from, far);
            int rowMost = Math.max(from, far);
            Grade grade = roadProfile(world, null, alongX, rowLeast, rowMost, acrossLeast, acrossMost, true);
            if (grade == null) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} has no profile at length {}, so its full {} rows stand", box.minX, box.minZ, alongX ? "x" : "z", length, rows); }
                return rows;
            }
            if (ContentLog.LOGGER.debugEnabled()) {
                StringBuilder trace = new StringBuilder();
                for (int i = 0; i < grade.profile.length; i++) {
                    trace.append(' ').append(rowLeast + i).append(':');
                    trace.append(grade.ground[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.ground[i])).append('/');
                    trace.append(grade.profile[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.profile[i]));
                    if (grade.bridged[i]) { trace.append('b'); }
                }
                ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} at length {} is {}, capped {} row(s), as row:ground/graded:{}", box.minX, box.minZ, alongX ? "x" : "z", length, BeardGrade.walkable(grade.profile, grade.bridged) ? "walkable" : "too steep", grade.capped, trace);
            }
            if (BeardGrade.walkable(grade.profile, grade.bridged)) { return length; }
        }
        return 0;
    }





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
        for (int corner = 0; corner < 4; corner++) {
            int sampled = surfaceAt(world, blockX + ((corner & 1) == 0 ? -halfWidth : halfWidth), blockZ + ((corner & 2) == 0 ? -halfWidth : halfWidth));
            if (sampled < 0) { return false; }

            lowest = Math.min(lowest, sampled);
            highest = Math.max(highest, sampled);
        }
        return highest - lowest > tolerance;
    }


    public static int surfaceAt(World world, int blockX, int blockZ) { return BeardSurface.surfaceAt(world, blockX, blockZ); }

    private static ChunkGeneratorOverworld samplerFor(World world) { return BeardSurface.samplerFor(world); }

    public static boolean adapts(World world) { return samplerFor(world) != null; }

    public static int villageSpacing(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return 32; }

        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        if (!(maker instanceof ChunkGeneratorOverworld)) { return 32; }

        return ((AccessorMapGenVillage) ((AccessorChunkGeneratorBeardFields) maker).rdpl$villages()).rdpl$distance();
    }


    public static long siteIn(World world, ContentSites known, int cellX, int cellZ, int spacing) { return BeardSite.siteFor(world, known, cellX, cellZ, spacing); }




    @SubscribeEvent public static void onDressed(PopulateChunkEvent.Post event) {
        if (event.getWorld().isRemote || !wanted()) { return; }
        if (!(event.getWorld().getChunkProvider() instanceof ChunkProviderServer)) { return; }

        IChunkGenerator maker = ((ChunkProviderServer) event.getWorld().getChunkProvider()).chunkGenerator;
        if (!(maker instanceof ChunkGeneratorOverworld)) { return; }

        MapGenVillage villages = ((AccessorChunkGeneratorBeardFields) maker).rdpl$villages();
        int blockX = (event.getChunkX() << 4) + 8;
        int blockZ = (event.getChunkZ() << 4) + 8;
        StructureBoundingBox clip = new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (StructureStart start : ((AccessorMapGenStructure) villages).rdpl$getStructureMap().values()) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (!(piece instanceof StructureVillagePieces.Village)) { continue; }

                StructureBoundingBox box = piece.getBoundingBox();
                if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { continue; }

                int felled = fellAround(event.getWorld(), start, piece, box, clip, at, false);
                if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
                if (piece instanceof StructureVillagePieces.Path) {
                    int[] ring = BeardGround.openOver(start, piece, event.getWorld(), box, clip, at);
                    if (ring[0] > 0) { ContentLog.LOGGER.debug("Opened {} block(s) over the roadway of Path at {}, {}", ring[0], box.minX, box.minZ); }
                    lampPosts(start, piece, event.getWorld(), box, clip, at);
                }
                else if (!box.intersectsWith(clip)) {
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} reaches into chunk {}, {} that its own box misses, so its ring is opened there now", piece.getClass().getSimpleName(), box.minX, box.minZ, event.getChunkX(), event.getChunkZ()); }
                    building(start);
                    try { openAround(start, piece, event.getWorld(), clip); }
                    finally { building(null); }
                }
            }
        }
    }

    public static void fellFor(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village)) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { return; }

        int felled = fellAround(world, start, piece, box, clip, new BlockPos.MutableBlockPos(), true);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before {} at {}, {} was built", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
    }

    private static int fellAround(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, boolean bare) {
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        int top = piece instanceof StructureVillagePieces.Well ? box.maxY + 1 : box.maxY;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                for (int y = box.minY + 1; y <= box.maxY + 16; y++) {
                    if (!bare && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ && y <= top) { continue; }

                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    if (mctmods.blastplaster.Config.isLog(held)) { seeds.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.LEAVES) { canopy.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.VINE) { felled += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        boolean inOwn = !bare;
        Predicate<BlockPos> within = spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4)
                && !BeardPlots.insideAnother(start, piece, spot)
                && !(inOwn && spot.getX() >= box.minX && spot.getX() <= box.maxX && spot.getZ() >= box.minZ && spot.getZ() <= box.maxZ && spot.getY() <= top);
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
        boolean alongX = box.maxX - box.minX >= box.maxZ - box.minZ;
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
        int reach = (pathFullWidth() - 3) / 2;
        int off = pathSidewalkWidth() > 0 ? reach : reach + 1;
        Grade grade = roadProfile(world, piece, alongX, from, to, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, true);
        int raised = 0;
        for (int spot : along) {
            int roadTop = grade == null ? Integer.MIN_VALUE : grade.at(spot);
            for (int side = 0; side < 2; side++) {
                int x = alongX ? spot : (side == 0 ? box.minX - off : box.maxX + off);
                int z = alongX ? (side == 0 ? box.minZ - off : box.maxZ + off) : spot;
                if (inPlaza(start, x, z) || onPaving(start, piece, x, z)) { continue; }
                if (raise(start, piece, world, clip, at, x, z, box, roadTop)) {
                    raised++;
                    break;
                }
            }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) along the road at {}, {}", raised, box.minX, box.minZ); }
    }

    private static boolean beforeADoor(World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int bed, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int y = bed; y <= bed + 3; y++) {
                    at.setPos(x + dx, y, z + dz);
                    if (!clip.isVecInside(at)) { continue; }
                    if (world.getBlockState(at).getBlock() instanceof BlockDoor) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean inPlaza(StructureStart start, int x, int z) {
        if (start.getComponents().isEmpty()) { return false; }

        StructureBoundingBox well = start.getComponents().get(0).getBoundingBox();
        int reach = plazaReach();
        return x >= well.minX - reach && x <= well.maxX + reach && z >= well.minZ - reach && z <= well.maxZ + reach;
    }

    private static boolean onPaving(StructureStart start, StructureComponent piece, int x, int z) {
        int reach = (pathFullWidth() - 3) / 2;
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox held = ((AccessorStructureComponentBox) other).rdpl$box();
            if (held == null) { continue; }
            if (other == piece) {
                if (x >= held.minX && x <= held.maxX && z >= held.minZ && z <= held.maxZ) { return true; }
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

        int stood = bed;
        if (roadTop > bed) { bed = roadTop; }
        for (int y = bed + 1; y <= bed + 4; y++) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { return false; }
            if (BeardKeep.holds(x, y, z)) { return false; }
            if (!world.getBlockState(at).getMaterial().isReplaceable() && world.getBlockState(at).getMaterial() != Material.AIR) { return false; }
        }
        if (beforeADoor(world, clip, at, x, bed, z)) { return false; }

        if (bed > stood) { BeardBlocks.fillBank(world, at, x, z, bed, stood + 1, false); }
        for (int y = bed + 1; y <= bed + 3; y++) {
            at.setPos(x, y, z);
            world.setBlockState(at, Blocks.OAK_FENCE.getDefaultState(), 2);
        }
        at.setPos(x, bed + 4, z);
        world.setBlockState(at, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK), 2);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            at.setPos(x + facing.getXOffset(), bed + 4, z + facing.getZOffset());
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
            if (world.getBlockState(at).getMaterial() != Material.AIR) { continue; }

            world.setBlockState(at, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, facing), 2);
        }
        return true;
    }






    public static void wellPlaza(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) { BeardPlaza.wellPlaza(start, piece, world, clip); }

    public static Boolean flatSite(World world, int chunkX, int chunkZ, int spacing) { return BeardSite.flatSite(world, chunkX, chunkZ, spacing); }

    public static BlockPos nearestSite(World world, BlockPos from, int spacing, boolean findUnexplored, long budgetNanos) { return BeardSite.nearestSite(world, from, spacing, findUnexplored, budgetNanos); }

    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) { return BeardSite.mansionCandidateNear(world, chunkX, chunkZ); }

    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) { return BeardSite.lowestIn(worldIn, minX, minZ, maxX, maxZ, clip); }

    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces) { return BeardSite.footingMisfit(box, pieces); }

    public static void foundAtBirth(StructureStart start) { BeardSite.foundAtBirth(start); }

    public static void openAround(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village) || piece instanceof StructureVillagePieces.Path) { return; }

        opening(start, piece, world, clip);
    }

    private static void opening(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        if (piece.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("waystone")) { BeardGround.waystoneRing(start, piece, world, clip, box, at); }
        if (piece instanceof StructureVillagePieces.Start) {
            BeardPlaza.bankWell(start, piece, world, clip, box, at);
            return;
        }
        int grounded = 0;
        int eaves = 0;
        int width = box.maxX - box.minX + 1;
        int depth = box.maxZ - box.minZ + 1;
        int[] footings = new int[width * depth];
        int lowestFooting = Integer.MAX_VALUE;
        int known = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                at.setPos(x, box.minY, z);
                if (!clip.isVecInside(at)) {
                    footings[(x - box.minX) * depth + (z - box.minZ)] = Integer.MIN_VALUE;
                    continue;
                }
                int footing = Integer.MAX_VALUE;
                boolean fenced = false;
                for (int y = box.minY; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    Block held = world.getBlockState(at).getBlock();
                    if (held == Blocks.AIR || BeardBlocks.terrainBlock(held)) { continue; }

                    footing = y - 1;
                    fenced = held instanceof BlockFence;
                    break;
                }
                for (int y = box.minY + 1; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    if (held.getBlock() == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { continue; }
                    if (BeardBlocks.terrainBlock(held.getBlock()) || held.getMaterial() == Material.VINE) { eaves += BeardBlocks.clearAt(world, at); }
                }
                footings[(x - box.minX) * depth + (z - box.minZ)] = fenced && footing != Integer.MAX_VALUE ? -footing : footing;
                known++;
                if (footing < lowestFooting) { lowestFooting = footing; }
            }
        }
        boolean traced = ContentLog.LOGGER.debugEnabled();
        if (traced) { ContentLog.LOGGER.debug("Hook for {} box {},{},{} known {} lowest {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, known, lowestFooting == Integer.MAX_VALUE ? "none" : String.valueOf(lowestFooting - box.minY)); }
        if (lowestFooting == Integer.MAX_VALUE) { return; }

        StringBuilder trace = traced ? new StringBuilder() : null;
        int[] froms = new int[width * depth];
        int[] tops = new int[width * depth];
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                boolean fenced = footing < 0 && footing != Integer.MIN_VALUE;
                if (fenced) { footing = -footing; }
                froms[spot] = Integer.MIN_VALUE;
                tops[spot] = Integer.MIN_VALUE;
                if (footing == Integer.MIN_VALUE) { continue; }

                int from = box.minY - 1;
                froms[spot] = from;
                for (int y = from; y >= box.minY - 24; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    tops[spot] = y;
                    break;
                }
            }
        }
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                if (footing < 0 && footing != Integer.MIN_VALUE) { footing = -footing; }
                int from = froms[spot];
                char verdict = footing == Integer.MIN_VALUE ? 'c' : from == Integer.MIN_VALUE ? 't' : footing == Integer.MAX_VALUE ? 'y' : 'f';
                int stood = 0;
                if (from != Integer.MIN_VALUE) {
                    IBlockState ground = Blocks.DIRT.getDefaultState();
                    if (tops[spot] != Integer.MIN_VALUE) {
                        at.setPos(x, tops[spot], z);
                        IBlockState resting = world.getBlockState(at);
                        if (resting.isFullBlock()) { ground = resting; }
                    }
                    int floor = BeardPlots.restingFloor(tops, depth, spot, from);
                    for (int y = from; y >= floor; y--) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        boolean exposed = !world.getBlockState(at.up()).getMaterial().isSolid();
                        IBlockState laidAs = ground;
                        if (exposed && ground.getBlock() == Blocks.DIRT) { laidAs = Blocks.GRASS.getDefaultState(); }
                        else if (!exposed && ground.getBlock() == Blocks.GRASS) { laidAs = Blocks.DIRT.getDefaultState(); }
                        world.setBlockState(at, laidAs, 2);
                        stood++;
                    }
                    grounded += stood;
                }
                if (traced) { trace.append(x - box.minX).append(',').append(z - box.minZ).append('=').append(footing == Integer.MIN_VALUE ? "clip" : footing == Integer.MAX_VALUE ? "open" : String.valueOf(footing - box.minY)).append(verdict).append(stood > 0 ? "+" + stood : "").append(' '); }
            }
        }
        if (traced) { ContentLog.LOGGER.debug("Footing for {} box {},{},{} to {},{},{} lowest {} known {} filled {}: {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lowestFooting - box.minY, known, grounded, trace); }
        int overhead = BeardGround.liftOffRoof(start, piece, world, box, clip, at);
        int banked = BeardGround.bankRing(start, piece, world, box, clip, at);
        if (piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2) { BeardGround.soilField(piece, world, box, clip, at); }
        int[] ring = BeardGround.openOver(start, piece, world, box, clip, at);
        int opened = ring[0];
        int spared = ring[1];
        int notGround = ring[2];
        int hangingOver = ring[3];
        int doorways = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                if (x != box.minX && x != box.maxX && z != box.minZ && z != box.maxZ) { continue; }

                for (int y = box.minY; y <= box.maxY - 1; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { break; }
                    if (!(world.getBlockState(at).getBlock() instanceof BlockDoor)) { continue; }

                    int outX = x == box.minX ? -1 : x == box.maxX ? 1 : 0;
                    int outZ = outX != 0 ? 0 : z == box.minZ ? -1 : 1;
                    at.setPos(x + outX, y - 1, z + outZ);
                    boolean floored = false;
                    if (clip.isVecInside(at) && !BeardPlots.underRoad(start, piece, x + outX, z + outZ) && !BeardPlots.insideAnother(start, piece, at) && !world.getBlockState(at).getMaterial().isSolid() && !world.getBlockState(at).getMaterial().isLiquid()) {
                        if (BeardKeep.holds(x + outX, y - 1, z + outZ)) { continue; }

                        IBlockState floor = BeardBlocks.fillGround(world, x + outX, z + outZ);
                        if (floor.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { floor = Blocks.GRASS.getDefaultState(); }
                        world.setBlockState(at, floor, 2);
                        doorways++;
                        floored = true;
                    }
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} found its door at {}, {}, {} facing out {}, {} and {} the ground in front (spot held {})", piece.getClass().getSimpleName(), box.minX, box.minZ, x, y, z, outX, outZ, floored ? "floored" : "kept", world.getBlockState(at)); }
                    for (int step = 1; step <= 5; step++) {
                        if (BeardPlots.underRoad(start, piece, x + outX * step, z + outZ * step)) { break; }
                        for (int up = 0; up <= 3; up++) {
                            at.setPos(x + outX * step, y + up, z + outZ * step);
                            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }

                            Block held = world.getBlockState(at).getBlock();
                            if (BeardBlocks.opening(world.getBlockState(at).getMaterial()) || held == Blocks.GRASS_PATH || held == Blocks.GRAVEL) { doorways += BeardBlocks.clearAt(world, at); }
                        }
                    }
                    break;
                }
            }
        }
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            int[] strip = facingStrip(box, other.getBoundingBox(), FACING_GAP);
            if (strip == null) { continue; }

            int fromX = strip[0];
            int toX = strip[1];
            int fromZ = strip[2];
            int toZ = strip[3];
            for (int x = fromX; x <= toX; x++) {
                for (int z = fromZ; z <= toZ; z++) {
                    if (BeardPlots.underRoad(start, piece, x, z)) { continue; }

                    for (int y = box.minY + 1; y <= box.minY + 4; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                        if (clearable(world.getBlockState(at))) { doorways += BeardBlocks.clearAt(world, at); }
                    }
                }
            }
        }
        int bridged = 0;
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path || !(other instanceof StructureVillagePieces.Village)) { continue; }

            bridged += bridge(world, start, piece, box, ((AccessorStructureComponentBox) other).rdpl$box(), clip, at);
        }
        if (opened + eaves + spared + notGround + hangingOver + grounded + overhead + bridged + doorways + banked > 0) { ContentLog.LOGGER.debug("Opened {} block(s) around {} at {}, {}, spared {} inside neighbouring pieces, left {} that were not ground, stood {} block(s) of ground under it, banked {} up to its grade, lifted {} off its roof, bridged {} between it and a neighbour, freed {} in front of its doors, and the hillside still hangs over {} column(s)", opened + eaves, piece.getClass().getSimpleName(), box.minX, box.minZ, spared, notGround, grounded, banked, overhead, bridged, doorways, hangingOver); }
    }





    private static StructureStart CURRENT;
    private static List<StructureComponent> LAYING;

    public static void building(@Nullable StructureStart start) { CURRENT = start; }

    public static StructureStart current() { return CURRENT; }

    public static void laying(@Nullable List<StructureComponent> pieces) { LAYING = pieces; }

    @Nullable public static List<StructureComponent> laid() { return LAYING; }

    @Nullable private static List<StructureComponent> components() { return CURRENT != null ? CURRENT.getComponents() : LAYING; }






    @Nullable private static int[] facingStrip(StructureBoundingBox box, StructureBoundingBox near, int mostGap) {
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

    private static int bridge(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox near, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (near == null || Math.abs(box.minY - near.minY) > 2) { return 0; }

        int[] strip = facingStrip(box, near, 6);
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

    public static boolean pathChosen() { return !ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock).isEmpty(); }

    public static int pathExtraWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth)); }

    public static int pathLineColumns() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLineBlock", Config.worldgen.villagePathLineBlock).isEmpty() ? 0 : 1; }

    public static int pathSidewalkWidth() {
        if (ContentControl.text(ContentControl.VILLAGES, "villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock).isEmpty()) { return 0; }

        return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth));
    }

    public static int pathFullWidth() { return 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth()); }

    public static int pathMinimumWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth)); }

    public static IBlockState pathBlock(String key, String fromConfig, IBlockState vanilla) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig);
        if (named.isEmpty()) { return vanilla; }

        IBlockState state = ContentStates.parse(named, key);
        if (state == null) {
            ContentLog.LOGGER.error("{} '{}' is not a registered block, using the vanilla road block", key, named);
            return vanilla;
        }
        return state;
    }

    public static void attach(StructureStart start, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }

        StructureBoundingBox box = ((AccessorStructureComponentBox) piece).rdpl$box();
        if (box == null) { return; }

        boolean alongX = BeardPlots.roadAlongX(piece);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox met = ((AccessorStructureComponentBox) other).rdpl$box();
            if (met == null) { continue; }

            boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
            if (!lined) { continue; }

            int ahead = alongX ? met.minX - box.maxX : met.minZ - box.maxZ;
            int behind = alongX ? box.minX - met.maxX : box.minZ - met.maxZ;
            if (ahead > 1 && ahead <= ATTACH_GAP && free(start, piece, box, alongX, met)) {
                if (alongX) { box.maxX = met.minX - 1; }
                else { box.maxZ = met.minZ - 1; }
            }
            else if (behind > 1 && behind <= ATTACH_GAP && free(start, piece, box, alongX, met)) {
                if (alongX) { box.minX = met.maxX + 1; }
                else { box.minZ = met.maxZ + 1; }
            }
        }
    }

    private static boolean free(StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean alongX, StructureBoundingBox met) {
        int least = alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ);
        int most = alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox held = other.getBoundingBox();
            boolean acrossed = alongX ? held.maxZ >= box.minZ && held.minZ <= box.maxZ : held.maxX >= box.minX && held.minX <= box.maxX;
            boolean along = alongX ? held.maxX >= least && held.minX <= most : held.maxZ >= least && held.minZ <= most;
            if (acrossed && along) { return false; }
        }
        return true;
    }

    public static void pave(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, IBlockState planks, boolean chosenSurface) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        int least = Math.max(alongX ? box.minX : box.minZ, alongX ? clip.minX : clip.minZ);
        int most = Math.min(alongX ? box.maxX : box.maxZ, alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }

        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {}, {} across, with surface {} (chosen={}), support {}, bridge {}", box.minX, box.minZ, (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1, path, chosenSurface, gravel, planks); }
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int start = least;
        boolean computed = false;
        int[] ground;
        int[] profile;
        boolean[] bridged;
        int capped;
        Grade graded = roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, acrossLeast, acrossMost, true);
        if (graded != null) {
            start = alongX ? box.minX : box.minZ;
            computed = true;
            profile = graded.profile;
            ground = graded.ground;
            bridged = graded.bridged;
            capped = graded.capped;
        }
        else {
            int rows = most - least + 1;
            profile = new int[rows];
            for (int i = 0; i < rows; i++) {
                int found = Integer.MIN_VALUE;
                for (int across = acrossLeast; across <= acrossMost; across++) {
                    int x = alongX ? least + i : across;
                    int z = alongX ? across : least + i;
                    BlockPos spot = new BlockPos(x, 64, z);
                    if (!clip.isVecInside(spot)) { continue; }

                    BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                    if (top.getY() < world.getSeaLevel() - 1 || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
                    if (top.getY() > found) { found = top.getY(); }
                }
                profile[i] = found;
            }
            int before = roadAnchor(world, alongX, least - 1, acrossLeast, acrossMost, path, gravel);
            if (before != Integer.MIN_VALUE && profile[0] != Integer.MIN_VALUE) { profile[0] = Math.max(before - 1, Math.min(before + 1, profile[0])); }
            int after = roadAnchor(world, alongX, most + 1, acrossLeast, acrossMost, path, gravel);
            if (after != Integer.MIN_VALUE && profile[rows - 1] != Integer.MIN_VALUE) { profile[rows - 1] = Math.max(after - 1, Math.min(after + 1, profile[rows - 1])); }
            ground = profile.clone();
            bridged = BeardGrade.smooth(profile);
            capped = BeardGrade.capEmbankment(profile, ground, bridged, new boolean[profile.length]);
        }
        if (capped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Capped {} row(s) of the road at {}, {} to {} block(s) above their own ground", capped, box.minX, box.minZ, BeardGrade.CAP); }
        if (ContentLog.LOGGER.debugEnabled()) {
            StringBuilder trace = new StringBuilder();
            for (int i = 0; i < profile.length; i++) {
                if (i > 0) { trace.append(' '); }
                trace.append(start + i).append(':');
                if (ground[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(ground[i]); }
                trace.append('/');
                if (profile[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(profile[i]); }
                if (bridged[i]) { trace.append('b'); }
            }
            ContentLog.LOGGER.debug("Profile of the road at {}, {} along {}, computed {}, as row:ground/graded, capped {} row(s) at {}: {}", box.minX, box.minZ, alongX ? "x" : "z", computed, capped, BeardGrade.CAP, trace);
        }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} grades from y {} to y {} along its length", box.minX, box.minZ, profile[0] == Integer.MIN_VALUE ? "water" : profile[0], profile[profile.length - 1] == Integer.MIN_VALUE ? "water" : profile[profile.length - 1]); }
        int cut = 0;
        int filled = 0;
        int paved = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = Math.max(0, least - start); i < profile.length && start + i <= most; i++) {
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? start + i : across;
                int z = alongX ? across : start + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }

                BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
                if (profile[i] == Integer.MIN_VALUE) {
                    if (world.getBlockState(top).getMaterial().isLiquid()) { paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at); }
                    continue;
                }
                if (bridged[i]) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, at);
                    continue;
                }
                if (world.getBlockState(top).getMaterial().isLiquid()) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at);
                    continue;
                }

                int target = profile[i];
                at.setPos(x, target, z);
                IBlockState held = world.getBlockState(at);
                Block base = held.getBlock();
                if (held.getMaterial().isSolid() && held.getMaterial() != Material.WOOD && held.getMaterial() != Material.LEAVES && !BeardBlocks.terrainBlock(base) && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM) { continue; }

                for (int y = target + 1; y <= target + 4; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    Block up = above.getBlock();
                    if (up == Blocks.AIR) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (!BeardBlocks.terrainBlock(up) && above.getMaterial().isSolid() && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {} takes {} out of the air above it at {}, {}, {}", box.minX, box.minZ, up.getRegistryName(), x, y, z); }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        cut++;
                        continue;
                    }
                    break;
                }
                filled += BeardBlocks.fillUnder(world, at, x, z, target - 1, target - 8);
                at.setPos(x, target, z);
                boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
                IBlockState natural = chosenSurface ? path : pathForGround(world, x, z, path, gravel, earthy);
                IBlockState dressed = dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, (acrossLeast + acrossMost) / 2, natural);
                if (BeardKeep.holds(x, target, z)) { continue; }

                world.setBlockState(at, dressed != null ? dressed : natural, 2);
                paved++;
            }
        }
        if ((cut + filled + paved > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips", box.minX, box.minZ, paved, cut, filled); }
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

    @Nullable public static IBlockState dressSurface(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, IBlockState path) {
        int span = 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth());
        StructureBoundingBox box = piece.getBoundingBox();
        if ((alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1 != span || span == 3) { return null; }

        int offset = Math.abs(across - acrossCenter);
        int core = 1 + pathExtraWidth();
        char role = offset <= core ? (offset == 0 ? 'c' : 'r') : offset <= core + pathLineColumns() ? 'l' : 's';
        IBlockState stamped = stampAt(world, piece, alongX, row, across, acrossCenter, core, path);
        if (stamped != null) { return stamped; }
        if (role != 'r' && insideMouth(piece, alongX, row, across, acrossCenter)) { return path; }
        if (role == 'c') {
            IBlockState center = pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, path);
            if (center == path) { return path; }

            int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
            if (dash > 0 && Math.floorMod(row, dash + 1) == dash) { return path; }

            return axised(center, alongX);
        }
        if (role == 'l') { return axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX); }
        if (role == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return path;
    }

    private static boolean insideMouth(StructureComponent piece, boolean alongX, int row, int across, int acrossCenter) {
        List<StructureComponent> pieces = components();
        if (pieces == null) { return false; }

        boolean outward = across > acrossCenter;
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (otherAlongX == alongX) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            if (row < otherCenter - otherCore || row > otherCenter + otherCore) { continue; }

            int edge = alongX ? (outward ? road.minZ : road.maxZ) : (outward ? road.minX : road.maxX);
            int gap = outward ? edge - across : across - edge;
            if (gap >= -1 && gap <= 2 + pathLineColumns() + pathSidewalkWidth()) { return true; }
        }
        return false;
    }

    @Nullable private static IBlockState stampAt(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        List<StructureComponent> pieces = components();
        if (pieces == null || Math.abs(across - acrossCenter) > core) { return null; }

        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (otherAlongX == alongX) { continue; }

            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            PathIntersectDef def = ContentPathIntersects.forJunction(world, alongX ? otherCenter : acrossCenter, alongX ? acrossCenter : otherCenter);
            if (def == null) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int before = otherCenter - otherCore - 1;
            int after = otherCenter + otherCore + 1;
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, before, after, path);
            if (fromMouth != null) { return fromMouth; }

            IBlockState fromCorner = cornerCell(def, row, across, acrossCenter, core, otherCenter, otherCore, path);
            if (fromCorner != null) { return fromCorner; }
        }
        return null;
    }

    @Nullable private static IBlockState mouthCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int before, int after, IBlockState path) {
        if (def.mouth.length == 0) { return null; }

        int line = -1;
        if (row <= before && row > before - def.mouth.length) { line = before - row; }
        if (row >= after && row < after + def.mouth.length) { line = row - after; }
        if (line < 0) { return null; }

        String cells = def.mouth[line];
        if (cells.isEmpty()) { return null; }

        return cellState(def, cells.charAt(Math.floorMod(across - (acrossCenter - core), cells.length())), path);
    }

    @Nullable private static IBlockState cornerCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int otherCenter, int otherCore, IBlockState path) {
        if (def.corner.length == 0 || row < otherCenter - otherCore || row > otherCenter + otherCore) { return null; }

        int fromRowEdge = Math.min(row - (otherCenter - otherCore), (otherCenter + otherCore) - row);
        int fromColEdge = core - Math.abs(across - acrossCenter);
        if (fromRowEdge >= def.corner.length) { return null; }

        String cells = def.corner[fromRowEdge];
        if (fromColEdge >= cells.length()) { return null; }

        return cellState(def, cells.charAt(fromColEdge), path);
    }

    @Nullable private static IBlockState cellState(PathIntersectDef def, char cell, IBlockState path) {
        if (cell == '.') { return null; }
        if (cell == 'r' || cell == 'c') { return path; }
        if (cell == 'l') { return pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path); }
        if (cell == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return def.legend.getOrDefault(cell, path);
    }

    @SuppressWarnings("unchecked") private static IBlockState axised(IBlockState state, boolean alongX) {
        for (net.minecraft.block.properties.IProperty<?> property : state.getPropertyKeys()) {
            if ("axis".equals(property.getName()) && property.getValueClass() == net.minecraft.util.EnumFacing.Axis.class) {
                return state.withProperty((net.minecraft.block.properties.IProperty<net.minecraft.util.EnumFacing.Axis>) property, alongX ? net.minecraft.util.EnumFacing.Axis.X : net.minecraft.util.EnumFacing.Axis.Z);
            }
        }
        return state;
    }


    private static int chainGradeAt(World world, StructureComponent road, boolean alongX, int row) {
        StructureBoundingBox box = road.getBoundingBox();
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        Grade grade = roadProfile(world, road, alongX, least, most, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, false);
        if (grade == null) { return Integer.MIN_VALUE; }

        return grade.at(row);
    }

    public static int roadGradeBeside(World world, StructureBoundingBox box) {
        List<StructureComponent> pieces = components();
        if (pieces == null) { return Integer.MIN_VALUE; }

        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int gap = Math.max(Math.max(road.minX - box.maxX, box.minX - road.maxX), Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ));
            if (gap > 2) { continue; }

            boolean alongX = BeardPlots.roadAlongX(other);
            int start = alongX ? road.minX : road.minZ;
            Grade grade = roadProfile(world, other, alongX, start, alongX ? road.maxX : road.maxZ, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, true);
            if (grade == null) { return Integer.MIN_VALUE; }

            int center = alongX ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
            int row = Math.max(start, Math.min(start + grade.profile.length - 1, center));
            if (grade.profile[row - start] == Integer.MIN_VALUE) { continue; }

            return grade.profile[row - start] + 1;
        }
        return Integer.MIN_VALUE;
    }

    private static int deckBridge(World world, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckAt, IBlockState planks, BlockPos.MutableBlockPos at) {
        int deckY = deckAt;
        for (int lift = 0; lift < 8 && world.getBlockState(at.setPos(alongX ? row : across, deckY, alongX ? across : row)).getMaterial().isLiquid(); lift++) { deckY++; }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        if (world.getBlockState(at).getMaterial().isSolid()) { return 0; }

        int span = acrossMost - acrossLeast + 1;
        int laid = 0;
        IBlockState deck = planks;
        if (span > 3) {
            int center = (acrossLeast + acrossMost) / 2;
            int offset = Math.abs(across - center);
            if (offset > 1 + pathExtraWidth() + pathLineColumns()) {
                IBlockState walk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, planks);
                deck = pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk);
            }
            if (offset == (span - 1) / 2) {
                IBlockState barrier = pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
                if (barrier != planks) {
                    int height = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight));
                    for (int y = deckY + 1; y <= deckY + height; y++) {
                        at.setPos(alongX ? row : across, y, alongX ? across : row);
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        world.setBlockState(at, barrier, 2);
                        laid++;
                    }
                    at.setPos(alongX ? row : across, deckY, alongX ? across : row);
                }
            }
        }
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return laid; }

        world.setBlockState(at, deck, 2);
        return laid + 1;
    }

    public static int plazaReach() { return 3 + (pathFullWidth() - 3) / 2; }




    private static void roadApron(World world, @Nullable StructureComponent piece, boolean alongX, int start, int rowMost, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = components();
        if (pieces == null) { return; }

        StructureBoundingBox own = piece != null ? piece.getBoundingBox()
                : new StructureBoundingBox(alongX ? start : acrossLeast, 0, alongX ? acrossLeast : start, alongX ? rowMost : acrossMost, 0, alongX ? acrossMost : rowMost);
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(other);
            if (otherAlongX == alongX) { continue; }

            if (road.minX - 1 > own.maxX || own.minX - 1 > road.maxX || road.minZ - 1 > own.maxZ || own.minZ - 1 > road.maxZ) { continue; }

            int center = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            int anchorRow = Math.max(start, Math.min(start + profile.length - 1, center));
            int grade = profile[anchorRow - start];
            if (grade == Integer.MIN_VALUE) { continue; }

            int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
            int crossed = chainGradeAt(world, other, otherAlongX, crossRow);
            if (crossed != Integer.MIN_VALUE && crossed < grade) { grade = crossed; }

            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A junction apron holds the rows around {} to y {} for the road at {}, {}", center, grade, own.minX, own.minZ); }
            int reach = 1 + pathExtraWidth() + 3;
            for (int row = center - reach; row <= center + reach; row++) {
                if (row < start || row > start + profile.length - 1) { continue; }
                if (profile[row - start] != Integer.MIN_VALUE) {
                    profile[row - start] = grade;
                    held[row - start] = true;
                }
            }
        }
    }





    private static void clampToWell(boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = components();
        if (pieces == null || pieces.isEmpty()) { return; }

        StructureBoundingBox well = pieces.get(0).getBoundingBox();
        int reach = plazaReach();
        if (acrossMost < (alongX ? well.minZ : well.minX) - reach || acrossLeast > (alongX ? well.maxZ : well.maxX) + reach) { return; }

        int ground = well.maxY - 3;
        int rowLeast = (alongX ? well.minX : well.minZ) - reach;
        int rowMost = (alongX ? well.maxX : well.maxZ) + reach;
        int clamped = 0;
        for (int row = Math.max(start, rowLeast); row <= Math.min(start + profile.length - 1, rowMost); row++) {
            if (profile[row - start] == Integer.MIN_VALUE) { continue; }

            held[row - start] = true;
            if (profile[row - start] == ground) { continue; }

            profile[row - start] = ground;
            clamped++;
        }
        if (clamped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Clamped {} road row(s) beside the well to its ground at y {}", clamped, ground); }
    }

    private static int roadAnchor(World world, boolean alongX, int row, int acrossLeast, int acrossMost, IBlockState path, IBlockState gravel) {
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            BlockPos spot = new BlockPos(x, 64, z);
            if (!world.isBlockLoaded(spot)) { continue; }

            IBlockState held = world.getBlockState(world.getTopSolidOrLiquidBlock(spot).down());
            if (held == path || held == gravel) { return world.getTopSolidOrLiquidBlock(spot).down().getY(); }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean clearable(IBlockState held) {
        Block block = held.getBlock();
        if (block == Blocks.AIR) { return false; }
        if (block == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { return false; }

        return BeardBlocks.terrainBlock(block) || held.getMaterial() == Material.VINE || held.getMaterial() == Material.PLANTS;
    }

    public static IBlockState pathForGround(World world, int x, int z, IBlockState path, IBlockState gravel, boolean earthy) {
        Block ground = BeardBlocks.fillGround(world, x, z).getBlock();
        if (ground == Blocks.SAND) { return Blocks.SANDSTONE.getDefaultState(); }
        if (ground == Blocks.HARDENED_CLAY) { return Blocks.HARDENED_CLAY.getDefaultState(); }
        if (ground == Blocks.GRAVEL) { return Blocks.GRAVEL.getDefaultState(); }

        return earthy ? path : gravel;
    }








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
            for (StructureStart start : ((AccessorMapGenStructure) generators[at]).rdpl$getStructureMap().values()) {
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
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation)) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                ContentLog.LOGGER.error("structureAdaptation entry '{}' is not structure=mode, ignoring it", entry);
                continue;
            }

            String name = parts[0].trim().toLowerCase(Locale.ROOT);
            String asked = parts[1].trim().toUpperCase(Locale.ROOT);
            if ("temples".equals(name)) {
                ContentLog.LOGGER.error("structureAdaptation names temples, but they place themselves only as they are built, so terrain cannot adapt to them yet and the entry is ignored");
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
