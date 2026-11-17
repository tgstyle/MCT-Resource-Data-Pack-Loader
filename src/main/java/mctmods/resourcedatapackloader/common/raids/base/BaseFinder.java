package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.ownership.claims.ForeignClaimsHolder;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.util.world.Coords;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;

public final class BaseFinder {
    private static final int TTL = 1200;
    private static final int CLUSTER_CAP = 64;
    private static final int CLAIM_CAP = 100;
    private static final int CHEST_CAP = 8;
    private static final int MINUTE_CAP = 60;
    private static final int TICKS_PER_MINUTE = 1200;
    private static final int TICKS_PER_DAY = 24000;
    private static final int CELL = 16;
    private static final Map<WorldServer, Cache> caches = new WeakHashMap<>();

    private BaseFinder() {}

    @Nullable public static Base find(WorldServer world, EntityPlayerMP player, BaseDef def) {
        int mine = RaidOwner.of(player);
        if (mine == 0) { return null; }
        Cache cache = cache(world);
        long now = world.getTotalWorldTime();
        Entry entry = cache.entries.get(mine);
        if (entry == null || entry.def != def || now >= entry.until) { entry = store(world, cache, player, def, mine, now); }
        Base base = entry.base;
        if (base == null || base.score() < def.minScore) { return null; }
        if ("claimed".equals(def.claims) && !base.claimed() || "unclaimed".equals(def.claims) && base.claimed()) { return null; }
        return base;
    }

    public static void refresh(WorldServer world, BaseDef def) {
        Cache cache = cache(world);
        long now = world.getTotalWorldTime();
        if (now < cache.next) { return; }
        cache.next = now + TTL;
        long revision = BaseLedger.get(world).revision();
        if (revision == cache.revision) { return; }
        cache.revision = revision;
        cache.entries.values().removeIf(entry -> now >= entry.until);
        for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, online -> true)) {
            int mine = RaidOwner.of(player);
            if (mine != 0) { store(world, cache, player, def, mine, now); }
        }
    }

    private static Cache cache(WorldServer world) { return caches.computeIfAbsent(world, ignored -> new Cache()); }

    private static Entry store(WorldServer world, Cache cache, EntityPlayerMP player, BaseDef def, int mine, long now) {
        Entry entry = new Entry(def, new Search(world, player, def, mine, now).run(), now + TTL);
        cache.entries.put(mine, entry);
        return entry;
    }

    private static final class Cache {
        private final Map<Integer, Entry> entries = new HashMap<>();
        private long revision = -1;
        private long next;
    }

    private static final class Entry {
        private final BaseDef def;
        @Nullable private final Base base;
        private final long until;

        private Entry(BaseDef def, @Nullable Base base, long until) {
            this.def = def;
            this.base = base;
            this.until = until;
        }
    }

    private static final class Cluster {
        private final LongArrayList keys = new LongArrayList();
        private final DoubleArrayList weights = new DoubleArrayList();
        private double score;

        private void add(long key, double weight, double cellScore) {
            keys.add(key);
            weights.add(weight);
            score += cellScore;
        }
    }

    private static final class Search {
        private final WorldServer world;
        private final EntityPlayerMP player;
        private final BaseDef def;
        private final int mine;
        private final long now;
        private final MinecraftServer server;
        private final BaseLedger ledger;
        private final Long2IntOpenHashMap bedCells = new Long2IntOpenHashMap();

        private Search(WorldServer world, EntityPlayerMP player, BaseDef def, int mine, long now) {
            this.world = world;
            this.player = player;
            this.def = def;
            this.mine = mine;
            this.now = now;
            this.server = world.getMinecraftServer();
            this.ledger = BaseLedger.get(world);
            for (BlockPos bed : ledger.beds().values()) { bedCells.addTo(BaseLedger.key(bed), 1); }
        }

        @Nullable private Base run() {
            Cluster best = null;
            LongOpenHashSet seen = new LongOpenHashSet();
            for (long key : candidates()) {
                if (seen.contains(key) || joinScore(key) <= def.cellFloor) { continue; }
                Cluster cluster = flood(key, seen);
                if (best == null || cluster.score > best.score) { best = cluster; }
            }
            return best == null ? claimOnly() : finish(best);
        }

        private LongArrayList candidates() {
            BlockPos bed = ledger.bed(player.getUniqueID());
            BlockPos at = bed != null ? bed : player.getPosition();
            int reach = bed != null ? (def.radius + CELL - 1) / CELL : 1;
            int cx = Coords.blockToCube(at.getX());
            int cz = Coords.blockToCube(at.getZ());
            LongArrayList list = new LongArrayList();
            for (int dx = -reach; dx <= reach; dx++) {
                for (int dz = -reach; dz <= reach; dz++) { list.add(ChunkPos.asLong(cx + dx, cz + dz)); }
            }
            return list;
        }

        private Cluster flood(long start, LongOpenHashSet seen) {
            Cluster cluster = new Cluster();
            LongArrayList queue = new LongArrayList();
            queue.add(start);
            seen.add(start);
            for (int i = 0; i < queue.size() && cluster.keys.size() < CLUSTER_CAP; i++) {
                long key = queue.getLong(i);
                double score = joinScore(key);
                cluster.add(key, score, score);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        long next = ChunkPos.asLong(x(key) + dx, z(key) + dz);
                        if (seen.add(next) && joinScore(next) > def.cellFloor) { queue.add(next); }
                    }
                }
            }
            return cluster;
        }

        private double joinScore(long key) {
            BaseLedger.Cell cell = ledger.cell(world, key);
            int beds = bedCells.get(key);
            if (cell == null && beds == 0 || cell != null && beds == 0 && cell.ticks() == 0 && cell.chest() == 0) { return Double.NEGATIVE_INFINITY; }
            BaseDef.Weights w = def.weights;
            double score = beds * (w.bed + w.spawn);
            if (cell != null) {
                boolean real = cell.ticks() >= TICKS_PER_MINUTE || beds > 0 || cell.chest() > 0;
                double raw = Math.min(cell.chest(), CHEST_CAP) * w.chest + (real ? cell.placed() * w.placed : 0) + Math.min(cell.ticks() / TICKS_PER_MINUTE, MINUTE_CAP) * w.minutes;
                score += raw * decay(cell);
            }
            return score;
        }

        private double decay(BaseLedger.Cell cell) { return def.decayDays <= 0 ? 1 : Math.max(0, 1 - (now - cell.last()) / (def.decayDays * TICKS_PER_DAY)); }

        @Nullable private Base finish(Cluster cluster) {
            double sx = 0;
            double sz = 0;
            double total = 0;
            for (int i = 0; i < cluster.keys.size(); i++) {
                double weight = cluster.weights.getDouble(i);
                sx += weight * centerX(cluster.keys.getLong(i));
                sz += weight * centerZ(cluster.keys.getLong(i));
                total += weight;
            }
            int x = (int) Math.floor(sx / total);
            int z = (int) Math.floor(sz / total);
            BlockPos flat = new BlockPos(x, 0, z);
            BlockPos center = world.isBlockLoaded(flat) ? world.getTopSolidOrLiquidBlock(flat) : new BlockPos(x, (int) player.posY, z);
            double far = 0;
            for (long key : cluster.keys) { far = Math.max(far, Math.hypot(centerX(key) - x, centerZ(key) - z)); }
            int radius = Math.max(def.radius, (int) Math.ceil(far) + CELL);
            int covers = ClaimGuard.covers(world, center);
            int claimOwner = covers == 0 ? 0 : RaidOwner.of(server, covers);
            boolean claimed = covers != 0 || ForeignClaimsHolder.get().isClaimed(world, Coords.blockToCube(x), Coords.blockToCube(z));
            int owner = claimOwner != 0 ? claimOwner : topOwner(cluster);
            if (owner == 0) { owner = mine; }
            if (!def.share && !RaidOwner.same(server, owner, mine) && !ClaimOwners.isMember(player, owner)) { return null; }
            double score = cluster.score;
            if (claimOwner != 0 && ClaimOwners.isMember(player, claimOwner)) { score += claimWeight(claimOwner); }
            return new Base(center, radius, score, owner, claimed);
        }

        @Nullable private Base claimOnly() {
            ClaimIndex index = ClaimIndex.obtain(world);
            if (index == null || index.claimCountOf(mine) == 0) { return null; }
            Cluster cluster = new Cluster();
            for (ChunkPos chunk : index.chunksOf(mine)) {
                if (cluster.keys.size() >= CLUSTER_CAP) { break; }
                cluster.add(ChunkPos.asLong(chunk.x, chunk.z), 1, 0);
            }
            return finish(cluster);
        }

        private double claimWeight(int owner) {
            ClaimIndex index = ClaimIndex.obtain(world);
            return index == null ? 0 : Math.min(index.claimCountOf(owner), CLAIM_CAP) * def.weights.claim;
        }

        private int topOwner(Cluster cluster) {
            Int2IntOpenHashMap tally = new Int2IntOpenHashMap();
            for (long key : cluster.keys) {
                BaseLedger.Cell cell = ledger.cell(world, key);
                if (cell != null && cell.top() != 0) { tally.addTo(cell.top(), cell.shareOf(cell.top())); }
            }
            int best = 0;
            int share = 0;
            for (Int2IntOpenHashMap.Entry entry : tally.int2IntEntrySet()) {
                if (entry.getIntValue() > share) {
                    share = entry.getIntValue();
                    best = entry.getIntKey();
                }
            }
            return best;
        }
    }

    private static int x(long key) { return (int) key; }

    private static int z(long key) { return (int) (key >> 32); }

    private static int centerX(long key) { return x(key) * CELL + CELL / 2; }

    private static int centerZ(long key) { return z(key) * CELL + CELL / 2; }
}
