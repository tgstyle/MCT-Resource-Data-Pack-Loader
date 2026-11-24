package mctmods.resourcedatapackloader.common.locate;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.common.WorldWorkerManager;

public final class ContentBiomeSearch extends LocateWorker {
    private static final int CHUNK_REACH = 400;
    private static final int CELLS = 4;
    private static ContentBiomeSearch running;
    private final Biome target;
    private final List<BlockPos> been;
    private final RingWalk walk;
    private Biome[] cells = new Biome[CELLS * CELLS];

    private ContentBiomeSearch(EntityPlayerMP player, String name, Biome target, boolean next, @Nullable Consumer<BlockPos> delivery) {
        super(player, name, delivery);
        this.target = target;
        this.been = new ArrayList<>();
        if (next) {
            been.addAll(ContentStructureSearch.been(player, name));
            been.add(player.getPosition());
        }
        this.walk = new RingWalk(MathHelper.floor(player.posX) >> 4, MathHelper.floor(player.posZ) >> 4);
    }

    public static boolean looking() { return running != null; }

    public static void start(EntityPlayerMP player, String name, Biome target, boolean next) { start(player, name, target, next, null); }

    public static void start(EntityPlayerMP player, String name, Biome target, boolean next, @Nullable Consumer<BlockPos> delivery) {
        running = new ContentBiomeSearch(player, name, target, next, delivery);
        WorldWorkerManager.addWorker(running);
    }

    @Override protected boolean scan() {
        long ending = System.nanoTime() + ContentStructureSearch.SLICE_NANOS;
        while (walk.ring() <= CHUNK_REACH) {
            while (walk.pending()) {
                if (System.nanoTime() >= ending) { return true; }
                long spot = walk.peek();
                walk.advance();
                consider(Longs.high(spot), Longs.low(spot));
            }
            walk.nextRing();
            if (best != null) { break; }
        }
        conclude(false);
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
            BlockPos at = new BlockPos(x, ContentStructureSearch.SEA_LEVEL, z);
            if (provider.getBiome(at) != target) { continue; }
            long away = Coords.awaySq(x, z, (long) player.posX, (long) player.posZ);
            if (away >= bestAway) { continue; }
            bestAway = away;
            best = at;
        }
    }

    @Override protected void release() {
        if (running == this) { running = null; }
    }
}
