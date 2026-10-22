package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Longs;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.common.WorldWorkerManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentBiomeSearch implements WorldWorkerManager.IWorker {
    private static final int CHUNK_REACH = 400;
    private static final int CELLS = 4;
    private static ContentBiomeSearch running;
    private final EntityPlayerMP player;
    private final World world;
    private final String name;
    private final Consumer<BlockPos> delivery;
    private final Biome target;
    private final List<BlockPos> been;
    private final int middleX;
    private final int middleZ;
    private Biome[] cells = new Biome[CELLS * CELLS];
    private int ring;
    private int step;
    private BlockPos best;
    private long bestAway = Long.MAX_VALUE;
    private boolean over;

    private ContentBiomeSearch(EntityPlayerMP player, String name, Biome target, boolean next, @Nullable Consumer<BlockPos> delivery) {
        this.delivery = delivery;
        this.player = player;
        this.world = player.world;
        this.name = name;
        this.target = target;
        this.been = new ArrayList<>();
        if (next) {
            been.addAll(ContentStructureSearch.been(player, name));
            been.add(player.getPosition());
        }
        this.middleX = (int) Math.floor(player.posX) >> 4;
        this.middleZ = (int) Math.floor(player.posZ) >> 4;
    }

    public static boolean looking() { return running != null; }

    public static void start(EntityPlayerMP player, String name, Biome target, boolean next) { start(player, name, target, next, null); }

    public static void start(EntityPlayerMP player, String name, Biome target, boolean next, @Nullable Consumer<BlockPos> delivery) {
        running = new ContentBiomeSearch(player, name, target, next, delivery);
        WorldWorkerManager.addWorker(running);
    }

    @Override public boolean hasWork() { return !over; }

    @Override public boolean doWork() {
        if (player.hasDisconnected() || player.world != world) {
            finish();
            return false;
        }
        long ending = System.nanoTime() + ContentStructureSearch.SLICE_NANOS;
        while (ring <= CHUNK_REACH) {
            while (step < ContentStructureSearch.onRing(ring)) {
                if (System.nanoTime() >= ending) { return true; }
                long spot = ContentStructureSearch.ringSpot(middleX, middleZ, ring, step);
                step++;
                consider(Longs.high(spot), Longs.low(spot));
            }
            step = 0;
            ring++;
            if (best != null) { break; }
        }
        finish();
        if (delivery != null) { delivery.accept(best); }
        else { ContentStructureSearch.arrive(player, name, best); }
        return false;
    }

    private void consider(int chunkX, int chunkZ) {
        BiomeProvider provider = world.getBiomeProvider();
        cells = provider.getBiomesForGeneration(cells, chunkX * CELLS, chunkZ * CELLS, CELLS, CELLS);
        for (int cell = 0; cell < CELLS * CELLS; cell++) {
            if (cells[cell] != target) { continue; }
            int x = (chunkX * CELLS + cell % CELLS) * CELLS + CELLS / 2;
            int z = (chunkZ * CELLS + cell / CELLS) * CELLS + CELLS / 2;
            if (ContentStructureSearch.beenNear(been, x, z)) { continue; }
            BlockPos at = new BlockPos(x, 64, z);
            if (provider.getBiome(at) != target) { continue; }
            long awayX = x - (long) player.posX;
            long awayZ = z - (long) player.posZ;
            long away = awayX * awayX + awayZ * awayZ;
            if (away >= bestAway) { continue; }
            bestAway = away;
            best = at;
        }
    }

    private void finish() {
        over = true;
        if (running == this) { running = null; }
    }
}
