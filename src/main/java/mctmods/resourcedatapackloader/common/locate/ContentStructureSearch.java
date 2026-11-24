package mctmods.resourcedatapackloader.common.locate;

import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.worldgen.generator.VanillaCompatibilityGenerator;
import mctmods.resourcedatapackloader.common.rubic.worldgen.interfaces.ICubeGenerator;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.IPackingStructureData;
import mctmods.resourcedatapackloader.common.villages.CityGrid;
import mctmods.resourcedatapackloader.common.villages.RailPiece;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentSites;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.core.integration.cubicchunks.CubicChunksWorlds;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Longs;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
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
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.common.WorldWorkerManager;

public final class ContentStructureSearch extends LocateWorker {
    static final long SLICE_NANOS = 40_000_000L;
    static final int SEA_LEVEL = 64;
    private static final int CHUNK_REACH = 512;
    private static final long BEEN_NEAR = 128L * 128L;
    private static final Map<String, Deque<BlockPos>> VISITED = new ConcurrentHashMap<>();
    private static ContentStructureSearch running;
    private static boolean reporting;
    private final MapGenStructure generator;
    private final boolean report;
    private final boolean cells;
    private final int spacing;
    private final int middleX;
    private final int middleZ;
    private final boolean findUnexplored;
    private final boolean skipHere;
    private final List<BlockPos> been;
    private final RingWalk walk;
    private int foundOn = Integer.MAX_VALUE;

    private ContentStructureSearch(EntityPlayerMP player, String name, MapGenStructure generator, boolean cells, int spacing, boolean findUnexplored, boolean skipHere, @Nullable Consumer<BlockPos> delivery) {
        super(player, name, delivery);
        this.been = skipHere ? been(player, name) : new ArrayList<>();
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
        this.walk = new RingWalk(middleX, middleZ);
    }

    public static boolean looking() { return running != null; }

    public static boolean point(EntityPlayerMP player, String name, String key) {
        if (looking()) { return false; }
        reporting = true;
        try { start(player, name, key, false, false); }
        finally { reporting = false; }
        return looking() || ContentDungeons.locateNames(player.world).contains(name);
    }

    public static void pointDungeon(ICommandSender sender, String name) {
        BlockPos from = sender.getPosition();
        point(sender, name, Coords.nearestPin(dungeonSpots(sender.getEntityWorld(), name, from), from.getX(), from.getZ(), 0, pin -> false));
    }

    private static List<long[]> dungeonSpots(World world, String name, BlockPos from) { return ContentDungeons.locateSpots(world, name, from, CHUNK_REACH * 16); }

    public static void start(EntityPlayerMP player, String name, String key, boolean findUnexplored, boolean skipHere) { start(player, name, key, findUnexplored, skipHere, null); }

    public static void start(EntityPlayerMP player, String name, String key, boolean findUnexplored, boolean skipHere, @Nullable Consumer<BlockPos> delivery) {
        World world = player.world;
        if (key != null) {
            List<long[]> pinned = ContentStructurePlacement.pins(key);
            if (pinned != null) {
                settleOnPins(player, name, pinned, ContentStructurePlacement.VILLAGES.equals(key) ? ContentBeard.BESIDE_WELL : 0, findUnexplored, skipHere, delivery);
                return;
            }
        }
        if (ContentDungeons.locateNames(world).contains(name)) {
            settleOnPins(player, name, dungeonSpots(world, name, player.getPosition()), 0, findUnexplored, skipHere, delivery);
            return;
        }
        MapGenStructure generator = generatorFor(world, name);
        if (generator == null) {
            if (delivery != null) { delivery.accept(null); }
            else { nothing(player, name); }
            return;
        }
        generator = theRealOne(generator);
        if (generator.world == null) { generator.world = world; }
        boolean cells = generator instanceof MapGenVillage && ContentBeard.wanted() && ContentBeard.adapts(world);
        int spacing = cells ? ContentSites.of(world, ContentBeard.villageSpacing(world)).spacing() : 0;
        ContentStructureSearch worker = new ContentStructureSearch(player, name, generator, cells, spacing, findUnexplored, skipHere, delivery);
        running = worker;
        WorldWorkerManager.addWorker(worker);
        tell(player, TextFormatting.GREEN, Lang.tr(player, reporting || delivery != null ? "rdpl.command.locatelooking" : "rdpl.command.gotolooking", name));
    }

    @Override protected boolean scan() {
        long began = System.nanoTime();
        long ending = began + SLICE_NANOS;
        boolean more = cells ? cellRings(ending) : chunkRings(ending);
        long took = System.nanoTime() - began;
        if (took > SLICE_NANOS * 10L) { ContentLog.LOGGER.warn("Looking for the nearest {} held the server for {} ms in one go, far past the {} ms it is allowed. Whatever it asked the game for is slower than it should be", name, took / 1_000_000L, SLICE_NANOS / 1_000_000L); }
        return more;
    }

    private boolean cellRings(long ending) {
        ContentSites known = ContentSites.of(world, spacing);
        while (walk.ring() <= 100 && walk.ring() <= (foundOn == Integer.MAX_VALUE ? 100 : foundOn + 1)) {
            while (walk.pending()) {
                long cell = walk.peek();
                int atX = Longs.high(cell);
                int atZ = Longs.low(cell);
                if (known.get(Longs.pack(atX, atZ)) == null && System.nanoTime() >= ending) { return true; }
                walk.advance();
                considerCell(known, atX, atZ);
            }
            walk.nextRing();
        }
        settle();
        return false;
    }

    private boolean chunkRings(long ending) {
        while (walk.ring() <= CHUNK_REACH) {
            while (walk.pending()) {
                if (System.nanoTime() >= ending) { return true; }
                long spot = walk.peek();
                walk.advance();
                int atX = Longs.high(spot);
                int atZ = Longs.low(spot);
                if (skipHere && Math.abs(atX - middleX) <= 8 && Math.abs(atZ - middleZ) <= 8) { continue; }
                if (beenNear(atX * 16 + 8, atZ * 16 + 8)) { continue; }
                if (findUnexplored && world.isChunkGeneratedAt(atX, atZ)) { continue; }
                MapGenBase.setupChunkSeed(world.getSeed(), generator.rand, atX, atZ);
                generator.rand.nextInt();
                if (!generator.canSpawnStructureAtCoords(atX, atZ)) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Chunk {}, {} says a {} may stand there. The game was asked as {}, pinned {}, ground managed {}", atX, atZ, name, generator.getClass().getName(), ContentStructurePlacement.pinned(ContentStructurePlacement.VILLAGES, atX, atZ), ContentBeard.wanted()); }
                StructureStart would = generator.getStructureStart(atX, atZ);
                if (!would.isSizeableStructure()) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A {} would stand on chunk {}, {} with {} piece(s) in it, {} of them not road, and the game counts that as worth keeping", name, atX, atZ, would.getComponents().size(), pieces(would)); }
                best = new BlockPos(atX * 16 + 8, SEA_LEVEL, atZ * 16 + 8);
                settle();
                return false;
            }
            walk.nextRing();
        }
        settle();
        return false;
    }

    private void considerCell(ContentSites known, int atX, int atZ) {
        long chosen = ContentBeard.siteIn(world, known, atX, atZ, spacing);
        if (chosen == ContentBeard.NO_SITE) { return; }
        int chunkX = Longs.high(chosen);
        int chunkZ = Longs.low(chosen);
        if (skipHere && Math.floorDiv(chunkX, spacing) == middleX && Math.floorDiv(chunkZ, spacing) == middleZ) { return; }
        if (beenNear(chunkX * 16 + 8, chunkZ * 16 + 8)) { return; }
        if (findUnexplored && world.isChunkGeneratedAt(chunkX, chunkZ)) { return; }
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) || ContentBeard.mansionCandidateNear(world, chunkX, chunkZ)) { return; }
        long away = Coords.chunkCentreAway(chunkX, chunkZ, (long) player.posX, (long) player.posZ);
        if (away >= bestAway) { return; }
        bestAway = away;
        best = new BlockPos(chunkX * 16 + 8, SEA_LEVEL, chunkZ * 16 + 8);
        if (foundOn == Integer.MAX_VALUE) { foundOn = walk.ring(); }
    }

    private static void settleOnPins(EntityPlayerMP player, String name, List<long[]> pinned, int beside, boolean findUnexplored, boolean skipHere, @Nullable Consumer<BlockPos> delivery) {
        List<BlockPos> visited = skipHere ? been(player, name) : Collections.emptyList();
        long fromX = (long) player.posX;
        long fromZ = (long) player.posZ;
        BlockPos best = Coords.nearestPin(pinned, fromX, fromZ, beside, pin -> findUnexplored && player.world.isChunkGeneratedAt((int) pin[0] >> 4, (int) pin[1] >> 4) || skipHere && (Coords.awaySq(pin[0], pin[1], fromX, fromZ) < BEEN_NEAR || beenNear(visited, (int) pin[0], (int) pin[1])));
        if (delivery != null) { delivery.accept(best); }
        else if (reporting) { point(player, name, best); }
        else { arrive(player, name, best); }
    }

    private void settle() { conclude(report); }

    private static int pieces(StructureStart start) {
        int standing = 0;
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Road)) { standing++; }
        }
        return standing;
    }

    static void point(ICommandSender sender, String name, @Nullable BlockPos best) {
        if (best == null) {
            nothing(sender, name);
            return;
        }
        sender.sendMessage(new TextComponentTranslation("commands.locate.success", name, best.getX(), best.getZ()));
    }

    static void arrive(EntityPlayerMP player, String name, BlockPos best) {
        if (ContentPregen.busy()) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotomakingland"));
            return;
        }
        if (best == null) {
            nothing(player, name);
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

    static List<BlockPos> been(EntityPlayerMP player, String name) {
        Deque<BlockPos> held = VISITED.get(player.getUniqueID() + ":" + name);
        return held == null ? new ArrayList<>() : new ArrayList<>(held);
    }

    private boolean beenNear(int x, int z) { return beenNear(been, x, z); }

    static boolean beenNear(List<BlockPos> been, int x, int z) {
        for (BlockPos at : been) {
            if (Coords.awaySq(x, z, at.getX(), at.getZ()) < BEEN_NEAR) { return true; }
        }
        return false;
    }

    @Override protected void release() {
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

    public static boolean open(World world, BlockPos at) {
        Material material = world.getBlockState(at).getMaterial();
        return !material.blocksMovement() && !material.isLiquid();
    }

    @Nullable private static BlockPos footing(World world, int x, int z) {
        int start = world.provider.hasSkyLight() ? world.getActualHeight() - 1 : 118;
        BlockPos.MutableBlockPos ground = new BlockPos.MutableBlockPos();
        for (int y = start; y > 0; y--) {
            if (open(world, ground.setPos(x, y, z))) { continue; }
            if (open(world, ground.setPos(x, y + 1, z)) && open(world, ground.setPos(x, y + 2, z))) { return new BlockPos(x, y + 1, z); }
        }
        return null;
    }

    @Nullable public static IChunkGenerator makerOf(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return null; }
        if (world.getChunkProvider() instanceof CubeProviderServer) {
            ICubeGenerator cubes = ((CubeProviderServer) world.getChunkProvider()).getCubeGenerator();
            if (cubes instanceof VanillaCompatibilityGenerator) { return ((VanillaCompatibilityGenerator) cubes).vanilla(); }
        }
        IChunkGenerator cubic = CubicChunksWorlds.maker(world);
        if (cubic != null) { return cubic; }
        return ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
    }

    public static boolean anyOtherOver(World world, @Nullable List<StructureComponent> own, StructureBoundingBox box) {
        for (StructureStart village : villageStarts(world)) {
            if (village.getComponents() == own) { continue; }
            for (StructureComponent piece : village.getComponents()) {
                if (piece.getBoundingBox().intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { return true; }
            }
        }
        return false;
    }

    public static boolean anyVillage(World world, StructureStart held, Predicate<StructureStart> test) {
        if (test.test(held)) { return true; }
        for (StructureStart village : villageStarts(world)) {
            if (village != held && test.test(village)) { return true; }
        }
        return false;
    }

    @Nullable public static StructureBoundingBox villageRoadIn(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (StructureStart start : villageStarts(world)) {
            if (!start.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (!(piece instanceof StructureVillagePieces.Path) && !(piece instanceof RailPiece)) { continue; }
                StructureBoundingBox box = piece.getBoundingBox();
                if (maxY < box.minY || minY > box.maxY) { continue; }
                if (box.intersectsWith(minX, minZ, maxX, maxZ)) { return box; }
            }
        }
        return null;
    }

    public static boolean spareRoad(World world, BlockPos position, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, String message) {
        if (world.isRemote || !ContentBeard.wanted()) { return false; }
        StructureBoundingBox box = villageRoadIn(world, minX, minY, minZ, maxX, maxY, maxZ);
        if (box == null) { return false; }
        ContentLog.LOGGER.debug(message, position.getX(), position.getY(), position.getZ(), box.minX, box.minZ);
        return true;
    }

    public static Collection<StructureStart> villageStarts(World world) {
        MapGenStructure found = villages(world);
        if (found == null || CityGrid.alone()) { return Collections.emptyList(); }
        return found.structureMap.values();
    }

    public static void store(World world, StructureStart village) {
        MapGenStructure found = villages(world);
        if (found == null || found.structureData == null) { return; }
        found.setStructureStart(village.getChunkPosX(), village.getChunkPosZ(), village);
    }

    public static boolean villageFoundedAt(World world, int chunkX, int chunkZ) {
        MapGenStructure found = villages(world);
        if (found == null) { return false; }
        MapGenStructureData data = found.structureData;
        return data != null && ((IPackingStructureData) data).rdpl$startWithin(chunkX, chunkZ, 1);
    }

    @Nullable private static MapGenStructure villages(World world) {
        MapGenVillage shell = villageShell(makerOf(world));
        if (shell == null) { return null; }
        MapGenStructure found = theRealOne(shell);
        return found instanceof MapGenVillage ? found : null;
    }

    @Nullable public static MapGenVillage villageShell(@Nullable IChunkGenerator maker) {
        if (maker instanceof ChunkGeneratorOverworld) {
            ChunkGeneratorOverworld fields = (ChunkGeneratorOverworld) maker;
            return fields.mapFeaturesEnabled ? fields.villageGenerator : null;
        }
        if (!(maker instanceof ChunkGeneratorFlat)) { return null; }
        MapGenStructure flat = ((ChunkGeneratorFlat) maker).structureGenerators.get("Village");
        return flat instanceof MapGenVillage ? (MapGenVillage) flat : null;
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
                catch (ReflectiveOperationException | RuntimeException ex) { ContentLog.LOGGER.debug("Could not read the structure field {} of {}", field.getName(), held.getClass().getName(), ex); }
            }
        }
        return null;
    }

    private static MapGenStructure generatorFor(World world, String name) {
        IChunkGenerator maker = makerOf(world);
        if (maker == null) { return null; }
        if (maker instanceof ChunkGeneratorOverworld) {
            ChunkGeneratorOverworld overworld = (ChunkGeneratorOverworld) maker;
            if ("Village".equals(name)) { return overworld.villageGenerator; }
            if ("Mansion".equals(name)) { return overworld.woodlandMansionGenerator; }
            if ("Temple".equals(name)) { return overworld.scatteredFeatureGenerator; }
            if ("Mineshaft".equals(name)) { return overworld.mineshaftGenerator; }
            if ("Stronghold".equals(name)) { return overworld.strongholdGenerator; }
            if ("Monument".equals(name)) { return overworld.oceanMonumentGenerator; }
            return null;
        }
        if (maker instanceof ChunkGeneratorHell && "Fortress".equals(name)) { return ((ChunkGeneratorHell) maker).genNetherBridge; }
        if (maker instanceof ChunkGeneratorEnd && "EndCity".equals(name)) { return ((ChunkGeneratorEnd) maker).endCityGen; }
        return null;
    }

    private static void nothing(ICommandSender sender, String name) { tell(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.command.gotonothing", name)); }

    private static void tell(ICommandSender sender, TextFormatting color, String message) { sender.sendMessage(new TextComponentString(color + message)); }
}
