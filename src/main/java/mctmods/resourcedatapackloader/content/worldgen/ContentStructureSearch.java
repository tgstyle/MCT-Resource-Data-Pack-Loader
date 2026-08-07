package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.mixin.AccessorChunkGeneratorBeardFields;
import mctmods.resourcedatapackloader.mixin.AccessorChunkGeneratorEnd;
import mctmods.resourcedatapackloader.mixin.AccessorChunkGeneratorHell;
import mctmods.resourcedatapackloader.mixin.AccessorChunkGeneratorStructures;
import mctmods.resourcedatapackloader.mixin.AccessorMapGenBase;
import mctmods.resourcedatapackloader.mixin.AccessorMapGenStructureSpawn;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.WorldWorkerManager;
import java.util.List;

public final class ContentStructureSearch implements WorldWorkerManager.IWorker {
    private static final long SLICE_NANOS = 40_000_000L;
    private static final int CHUNK_REACH = 512;
    private static ContentStructureSearch running;
    private final EntityPlayerMP player;
    private final World world;
    private final String name;
    private final MapGenStructure generator;
    private final boolean cells;
    private final int spacing;
    private final int middleX;
    private final int middleZ;
    private final boolean findUnexplored;
    private int ring;
    private int step;
    private int foundOn = Integer.MAX_VALUE;
    private BlockPos best;
    private long bestAway = Long.MAX_VALUE;
    private boolean over;

    private ContentStructureSearch(EntityPlayerMP player, String name, MapGenStructure generator, boolean cells, int spacing, boolean findUnexplored) {
        this.player = player;
        this.world = player.world;
        this.name = name;
        this.generator = generator;
        this.cells = cells;
        this.spacing = spacing;
        this.findUnexplored = findUnexplored;
        int chunkX = (int) player.posX >> 4;
        int chunkZ = (int) player.posZ >> 4;
        this.middleX = cells ? Math.floorDiv(chunkX, spacing) : chunkX;
        this.middleZ = cells ? Math.floorDiv(chunkZ, spacing) : chunkZ;
    }

    public static boolean looking() { return running != null; }

    public static void start(EntityPlayerMP player, String name, String key, boolean findUnexplored) {
        World world = player.world;
        if (key != null) {
            List<long[]> pinned = ContentStructurePlacement.pins(key);
            if (pinned != null) {
                settleOnPins(player, name, pinned, findUnexplored);
                return;
            }
        }
        MapGenStructure generator = generatorFor(world, name);
        if (generator == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", name));
            return;
        }

        boolean cells = generator instanceof MapGenVillage && ContentBeard.wanted() && ContentBeard.adapts(world);
        int spacing = cells ? ContentBeard.villageSpacing(world) : 0;
        ContentStructureSearch worker = new ContentStructureSearch(player, name, generator, cells, spacing, findUnexplored);
        running = worker;
        WorldWorkerManager.addWorker(worker);
        tell(player, TextFormatting.GREEN, Lang.tr(player, "rdpl.command.gotolooking", name));
    }

    @Override public boolean hasWork() { return !over; }

    @Override public boolean doWork() {
        if (player.hasDisconnected() || player.world != world) {
            finish();
            return false;
        }

        long ending = System.nanoTime() + SLICE_NANOS;
        return cells ? cellRings(ending) : chunkRings(ending);
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
                if (findUnexplored && world.isChunkGeneratedAt(atX, atZ)) { continue; }

                MapGenBase.setupChunkSeed(world.getSeed(), ((AccessorMapGenBase) generator).rdpl$rand(), atX, atZ);
                if (!((AccessorMapGenStructureSpawn) generator).rdpl$canSpawnStructureAtCoords(atX, atZ)) { continue; }

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

    private static void settleOnPins(EntityPlayerMP player, String name, List<long[]> pinned, boolean findUnexplored) {
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        for (long[] pin : pinned) {
            int chunkX = (int) pin[0] >> 4;
            int chunkZ = (int) pin[1] >> 4;
            if (findUnexplored && player.world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }

            long awayX = pin[0] - (long) player.posX;
            long awayZ = pin[1] - (long) player.posZ;
            long away = awayX * awayX + awayZ * awayZ;
            if (away >= bestAway) { continue; }

            bestAway = away;
            best = new BlockPos((int) pin[0], 64, (int) pin[1]);
        }
        arrive(player, name, best);
    }

    private void settle() {
        finish();
        arrive(player, name, best);
    }

    private static void arrive(EntityPlayerMP player, String name, BlockPos best) {
        if (best == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonothing", name));
            return;
        }

        BlockPos ground = landing(player.world, best);
        if (ground == null) {
            tell(player, TextFormatting.RED, Lang.tr(player, "rdpl.command.gotonoground", name, best.getX(), best.getZ()));
            return;
        }
        player.setPositionAndUpdate(ground.getX() + 0.5D, ground.getY(), ground.getZ() + 0.5D);
        tell(player, TextFormatting.GREEN, Lang.tr(player, "rdpl.command.gotodone", name, ground.getX(), ground.getY(), ground.getZ()));
    }

    private void finish() {
        over = true;
        if (running == this) { running = null; }
    }

    public static BlockPos landing(World world, BlockPos found) {
        int start = world.provider.hasSkyLight() ? world.getActualHeight() - 1 : 118;
        for (int y = start; y > 0; y--) {
            BlockPos ground = new BlockPos(found.getX(), y, found.getZ());
            if (!world.getBlockState(ground).getMaterial().blocksMovement()) { continue; }
            if (world.isAirBlock(ground.up()) && world.isAirBlock(ground.up(2))) { return ground.up(); }
        }
        return null;
    }

    private static MapGenStructure generatorFor(World world, String name) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return null; }

        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        if (maker instanceof ChunkGeneratorOverworld) {
            if ("Village".equals(name)) { return ((AccessorChunkGeneratorBeardFields) maker).rdpl$villages(); }
            if ("Mansion".equals(name)) { return ((AccessorChunkGeneratorBeardFields) maker).rdpl$mansions(); }
            if ("Temple".equals(name)) { return ((AccessorChunkGeneratorStructures) maker).rdpl$temples(); }
            if ("Mineshaft".equals(name)) { return ((AccessorChunkGeneratorStructures) maker).rdpl$mineshafts(); }
            if ("Stronghold".equals(name)) { return ((AccessorChunkGeneratorStructures) maker).rdpl$strongholds(); }
            if ("Monument".equals(name)) { return ((AccessorChunkGeneratorStructures) maker).rdpl$monuments(); }
            return null;
        }
        if (maker instanceof ChunkGeneratorHell && "Fortress".equals(name)) { return ((AccessorChunkGeneratorHell) maker).rdpl$fortresses(); }
        if (maker instanceof ChunkGeneratorEnd && "EndCity".equals(name)) { return ((AccessorChunkGeneratorEnd) maker).rdpl$endCities(); }
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
