package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorBeardFields;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorFlatFields;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorEnd;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorHell;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorStructures;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenBase;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructure;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructureSpawn;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.WorldWorkerManager;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContentStructureSearch implements WorldWorkerManager.IWorker {
    private static final long SLICE_NANOS = 40_000_000L;
    private static final int CHUNK_REACH = 512;
    private static final long BEEN_NEAR = 128L * 128L;
    private static final Map<String, Deque<BlockPos>> VISITED = new ConcurrentHashMap<>();
    private static ContentStructureSearch running;
    private final EntityPlayerMP player;
    private final World world;
    private final String name;
    private final MapGenStructure generator;
    private final boolean report;
    private static boolean reporting;
    private final boolean cells;
    private final int spacing;
    private final int middleX;
    private final int middleZ;
    private final boolean findUnexplored;
    private final boolean skipHere;
    private final List<BlockPos> been;
    private int ring;
    private int step;
    private int foundOn = Integer.MAX_VALUE;
    private BlockPos best;
    private long bestAway = Long.MAX_VALUE;
    private boolean over;

    private ContentStructureSearch(EntityPlayerMP player, String name, MapGenStructure generator, boolean cells, int spacing, boolean findUnexplored, boolean skipHere) {
        this.been = skipHere ? been(player, name) : new ArrayList<>();
        this.player = player;
        this.world = player.world;
        this.name = name;
        this.generator = generator;
        this.cells = cells;
        this.spacing = spacing;
        this.findUnexplored = findUnexplored;
        this.skipHere = skipHere;
        int chunkX = (int) player.posX >> 4;
        int chunkZ = (int) player.posZ >> 4;
        this.report = reporting;
        this.middleX = cells ? Math.floorDiv(chunkX, spacing) : chunkX;
        this.middleZ = cells ? Math.floorDiv(chunkZ, spacing) : chunkZ;
    }

    public static boolean looking() { return running != null; }

    public static void start(EntityPlayerMP player, String name, String key, boolean findUnexplored) { start(player, name, key, findUnexplored, false); }

    public static boolean point(EntityPlayerMP player, String name, String key) {
        if (looking()) { return false; }
        reporting = true;
        try { start(player, name, key, false, false); }
        finally { reporting = false; }
        return looking();
    }

    public static void start(EntityPlayerMP player, String name, String key, boolean findUnexplored, boolean skipHere) {
        World world = player.world;
        if (key != null) {
            List<long[]> pinned = ContentStructurePlacement.pins(key);
            if (pinned != null) {
                settleOnPins(player, name, pinned, findUnexplored, skipHere);
                return;
            }
        }
        MapGenStructure generator = generatorFor(world, name);
        if (generator == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", name));
            return;
        }
        generator = theRealOne(generator);
        if (((IMapGenBase) generator).rdpl$getWorld() == null) { ((IMapGenBase) generator).rdpl$setWorld(world); }
        boolean cells = generator instanceof MapGenVillage && ContentBeard.wanted() && ContentBeard.adapts(world);
        int spacing = cells ? ContentSites.of(world, ContentBeard.villageSpacing(world)).spacing() : 0;
        ContentStructureSearch worker = new ContentStructureSearch(player, name, generator, cells, spacing, findUnexplored, skipHere);
        running = worker;
        WorldWorkerManager.addWorker(worker);
        tell(player, TextFormatting.GREEN, Lang.tr(player, reporting ? "rdpl.command.locatelooking" : "rdpl.command.gotolooking", name));
    }

    @Override public boolean hasWork() { return !over; }

    @Override public boolean doWork() {
        if (player.hasDisconnected() || player.world != world) {
            finish();
            return false;
        }
        long began = System.nanoTime();
        long ending = began + SLICE_NANOS;
        boolean more = cells ? cellRings(ending) : chunkRings(ending);
        long took = System.nanoTime() - began;
        if (took > SLICE_NANOS * 10L) { ContentLog.LOGGER.warn("Looking for the nearest {} held the server for {} ms in one go, far past the {} ms it is allowed. Whatever it asked the game for is slower than it should be", name, took / 1_000_000L, SLICE_NANOS / 1_000_000L); }
        return more;
    }

    private boolean cellRings(long ending) {
        ContentSites known = ContentSites.of(world, spacing);
        while (ring <= 100 && ring <= (foundOn == Integer.MAX_VALUE ? 100 : foundOn + 1)) {
            while (step < onRing(ring)) {
                long cell = ringSpot(step);
                int atX = (int) (cell >> 32);
                int atZ = (int) cell;
                if (known.get(packed(atX, atZ)) == null && System.nanoTime() >= ending) { return true; }
                step++;
                considerCell(known, atX, atZ);
            }
            step = 0;
            ring++;
        }
        settle();
        return false;
    }

    private boolean chunkRings(long ending) {
        while (ring <= CHUNK_REACH) {
            while (step < onRing(ring)) {
                if (System.nanoTime() >= ending) { return true; }
                long spot = ringSpot(step);
                step++;
                int atX = (int) (spot >> 32);
                int atZ = (int) spot;
                if (skipHere && Math.abs(atX - middleX) <= 8 && Math.abs(atZ - middleZ) <= 8) { continue; }
                if (beenNear(atX * 16 + 8, atZ * 16 + 8)) { continue; }
                if (findUnexplored && world.isChunkGeneratedAt(atX, atZ)) { continue; }
                MapGenBase.setupChunkSeed(world.getSeed(), ((IMapGenBase) generator).rdpl$rand(), atX, atZ);
                ((IMapGenBase) generator).rdpl$rand().nextInt();
                if (!((IMapGenStructureSpawn) generator).rdpl$canSpawnStructureAtCoords(atX, atZ)) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Chunk {}, {} says a {} may stand there. The game was asked as {}, pinned {}, ground managed {}", atX, atZ, name, generator.getClass().getName(), ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, atX, atZ), ContentBeard.wanted()); }
                StructureStart would = ((IMapGenStructureSpawn) generator).rdpl$getStructureStart(atX, atZ);
                if (!would.isSizeableStructure()) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A {} would stand on chunk {}, {} with {} piece(s) in it, {} of them not road, and the game counts that as worth keeping", name, atX, atZ, would.getComponents().size(), pieces(would)); }
                best = new BlockPos(atX * 16 + 8, 64, atZ * 16 + 8);
                settle();
                return false;
            }
            step = 0;
            ring++;
        }
        settle();
        return false;
    }

    private void considerCell(ContentSites known, int atX, int atZ) {
        long chosen = ContentBeard.siteIn(world, known, atX, atZ, spacing);
        if (chosen == ContentBeard.NO_SITE) { return; }
        int chunkX = (int) (chosen >> 32);
        int chunkZ = (int) chosen;
        if (skipHere && Math.floorDiv(chunkX, spacing) == middleX && Math.floorDiv(chunkZ, spacing) == middleZ) { return; }
        if (beenNear(chunkX * 16 + 8, chunkZ * 16 + 8)) { return; }
        if (findUnexplored && world.isChunkGeneratedAt(chunkX, chunkZ)) { return; }
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) || ContentBeard.mansionCandidateNear(world, chunkX, chunkZ)) { return; }
        long awayX = (chunkX * 16L + 8) - (long) player.posX;
        long awayZ = (chunkZ * 16L + 8) - (long) player.posZ;
        long away = awayX * awayX + awayZ * awayZ;
        if (away >= bestAway) { return; }
        bestAway = away;
        best = new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8);
        if (foundOn == Integer.MAX_VALUE) { foundOn = ring; }
    }

    private static void settleOnPins(EntityPlayerMP player, String name, List<long[]> pinned, boolean findUnexplored, boolean skipHere) {
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        for (long[] pin : pinned) {
            int chunkX = (int) pin[0] >> 4;
            int chunkZ = (int) pin[1] >> 4;
            if (findUnexplored && player.world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }
            long awayX = pin[0] - (long) player.posX;
            long awayZ = pin[1] - (long) player.posZ;
            long away = awayX * awayX + awayZ * awayZ;
            if (skipHere && away < BEEN_NEAR) { continue; }
            if (skipHere && beenNear(player, name, (int) pin[0], (int) pin[1])) { continue; }
            if (away >= bestAway) { continue; }
            bestAway = away;
            best = new BlockPos((int) pin[0], 64, (int) pin[1]);
        }
        arrive(player, name, best);
    }

    private void settle() {
        finish();
        if (report) { point(player, name, best); }
        else { arrive(player, name, best); }
    }

    private static int pieces(StructureStart start) {
        int standing = 0;
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Road)) { standing++; }
        }
        return standing;
    }

    private static void point(EntityPlayerMP player, String name, @Nullable BlockPos best) {
        if (best == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", name));
            return;
        }
        player.sendMessage(new TextComponentTranslation("commands.locate.success", name, best.getX(), best.getZ()));
    }

    private static void arrive(EntityPlayerMP player, String name, BlockPos best) {
        if (ContentPregen.busy()) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotomakingland"));
            return;
        }
        if (best == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", name));
            return;
        }
        BlockPos ground = landing(player.world, best);
        if (ground == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonoground", name, best.getX(), best.getZ()));
            return;
        }
        remember(player, name, best);
        player.setPositionAndUpdate(ground.getX() + 0.5D, stand(player.world, ground), ground.getZ() + 0.5D);
        tell(player, TextFormatting.GREEN, Lang.tr(player, "rdpl.command.gotodone", name, ground.getX(), ground.getY(), ground.getZ()));
    }

    public static void forget() { VISITED.clear(); }

    public static void remember(EntityPlayerMP player, String name, BlockPos site) {
        Deque<BlockPos> held = VISITED.computeIfAbsent(player.getUniqueID() + ":" + name, unused -> new ArrayDeque<>());
        BlockPos last = held.peekLast();
        if (last == null || last.distanceSq(site) > BEEN_NEAR) { held.addLast(site.toImmutable()); }
    }

    public static BlockPos stepBack(EntityPlayerMP player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUniqueID() + ":" + name);
        if (held == null || held.size() < 2) { return null; }
        held.pollLast();
        return held.peekLast();
    }

    private static List<BlockPos> been(EntityPlayerMP player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUniqueID() + ":" + name);
        return held == null ? new ArrayList<>() : new ArrayList<>(held);
    }

    private boolean beenNear(int x, int z) {
        for (BlockPos at : been) {
            long awayX = x - (long) at.getX();
            long awayZ = z - (long) at.getZ();
            if (awayX * awayX + awayZ * awayZ < BEEN_NEAR) { return true; }
        }
        return false;
    }

    private static boolean beenNear(EntityPlayerMP player, String name, int x, int z) {
        for (BlockPos at : been(player, name)) {
            long awayX = x - (long) at.getX();
            long awayZ = z - (long) at.getZ();
            if (awayX * awayX + awayZ * awayZ < BEEN_NEAR) { return true; }
        }
        return false;
    }

    private void finish() {
        over = true;
        if (running == this) { running = null; }
    }

    public static double stand(World world, BlockPos landing) {
        BlockPos below = landing.down();
        return below.getY() + world.getBlockState(below).getBoundingBox(world, below).maxY;
    }

    public static BlockPos landing(World world, BlockPos found) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) { world.getChunk((found.getX() >> 4) + dx, (found.getZ() >> 4) + dz); }
        }
        for (int reach = 0; reach <= 6; reach += 2) {
            for (int dx = -reach; dx <= reach; dx += Math.max(1, reach)) {
                for (int dz = -reach; dz <= reach; dz += Math.max(1, reach)) {
                    BlockPos footing = footing(world, found.getX() + dx, found.getZ() + dz);
                    if (footing != null) { return footing; }
                }
            }
        }
        return null;
    }

    private static boolean open(World world, BlockPos at) {
        Material material = world.getBlockState(at).getMaterial();
        return !material.blocksMovement() && !material.isLiquid();
    }

    @Nullable private static BlockPos footing(World world, int x, int z) {
        int start = world.provider.hasSkyLight() ? world.getActualHeight() - 1 : 118;
        for (int y = start; y > 0; y--) {
            BlockPos ground = new BlockPos(x, y, z);
            Material material = world.getBlockState(ground).getMaterial();
            if (!material.blocksMovement() && !material.isLiquid()) { continue; }
            if (open(world, ground.up()) && open(world, ground.up(2))) { return ground.up(); }
        }
        return null;
    }

    public static Collection<StructureStart> villageStarts(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return Collections.emptyList(); }
        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        MapGenVillage shell = null;
        if (maker instanceof ChunkGeneratorOverworld) { shell = ((IChunkGeneratorBeardFields) maker).rdpl$villages(); }
        else if (maker instanceof ChunkGeneratorFlat) {
            MapGenStructure flat = ((IChunkGeneratorFlatFields) maker).rdpl$structures().get("Village");
            if (flat instanceof MapGenVillage) { shell = (MapGenVillage) flat; }
        }
        if (shell == null) { return Collections.emptyList(); }
        MapGenStructure found = theRealOne(shell);
        if (!(found instanceof MapGenVillage)) { return Collections.emptyList(); }
        return ((IMapGenStructure) found).rdpl$getStructureMap().values();
    }

    public static MapGenStructure theRealOne(MapGenStructure wrapper) {
        MapGenStructure real = wrapper;
        for (int depth = 0; depth < 8; depth++) {
            MapGenStructure inside = delegateOf(real);
            if (inside == null || inside == real) { break; }
            ContentLog.LOGGER.debug("The game's {} is wrapped by another mod as {}, so the one underneath is asked instead", inside.getClass().getSimpleName(), real.getClass().getName());
            real = inside;
        }
        return real;
    }

    @Nullable private static MapGenStructure delegateOf(MapGenStructure held) {
        for (Class<?> owner = held.getClass(); owner != null && owner != Object.class; owner = owner.getSuperclass()) {
            for (Field field : owner.getDeclaredFields()) {
                if (!MapGenStructure.class.isAssignableFrom(field.getType())) { continue; }
                try {
                    field.setAccessible(true);
                    Object inside = field.get(held);
                    if (inside instanceof MapGenStructure && inside != held) { return (MapGenStructure) inside; }
                }
                catch (Exception ignored) { }
            }
        }
        return null;
    }

    private static MapGenStructure generatorFor(World world, String name) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return null; }
        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        if (maker instanceof ChunkGeneratorOverworld) {
            if ("Village".equals(name)) { return ((IChunkGeneratorBeardFields) maker).rdpl$villages(); }
            if ("Mansion".equals(name)) { return ((IChunkGeneratorBeardFields) maker).rdpl$mansions(); }
            if ("Temple".equals(name)) { return ((IChunkGeneratorStructures) maker).rdpl$temples(); }
            if ("Mineshaft".equals(name)) { return ((IChunkGeneratorStructures) maker).rdpl$mineshafts(); }
            if ("Stronghold".equals(name)) { return ((IChunkGeneratorStructures) maker).rdpl$strongholds(); }
            if ("Monument".equals(name)) { return ((IChunkGeneratorStructures) maker).rdpl$monuments(); }
            return null;
        }
        if (maker instanceof ChunkGeneratorHell && "Fortress".equals(name)) { return ((IChunkGeneratorHell) maker).rdpl$fortresses(); }
        if (maker instanceof ChunkGeneratorEnd && "EndCity".equals(name)) { return ((IChunkGeneratorEnd) maker).rdpl$endCities(); }
        return null;
    }

    private int onRing(int around) { return around == 0 ? 1 : around * 16; }

    private long ringSpot(int at) {
        if (ring == 0) { return packed(middleX, middleZ); }
        int side = ring * 2;
        int leg = at / side;
        int along = at % side;
        if (leg == 0) { return packed(middleX - ring + along, middleZ - ring); }
        if (leg == 1) { return packed(middleX + ring, middleZ - ring + along); }
        if (leg == 2) { return packed(middleX + ring - along, middleZ + ring); }
        return packed(middleX - ring, middleZ + ring - along);
    }

    private static long packed(int x, int z) { return ((long) x << 32) | (z & 0xFFFFFFFFL); }

    private static void tell(EntityPlayerMP player, TextFormatting color, String message) { player.sendMessage(new TextComponentString(color + message)); }
}
