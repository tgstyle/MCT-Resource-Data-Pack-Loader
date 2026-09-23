package mctmods.resourcedatapackloader.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;
import java.io.Serial;

public final class LightShards extends Long2ObjectOpenHashMap<DataLayer> {
    @Serial private static final long serialVersionUID = 1L;
    private static final int SHIFT = 2;
    private final Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<DataLayer>> table;

    public LightShards() { this(new Long2ObjectOpenHashMap<>(0)); }

    private LightShards(Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<DataLayer>> table) {
        super(0);
        this.table = table;
    }

    private static long shard(long section) { return ChunkPos.asLong(SectionPos.x(section) >> SHIFT, SectionPos.z(section) >> SHIFT); }

    @Override public DataLayer get(long section) {
        Long2ObjectOpenHashMap<DataLayer> held = table.get(shard(section));
        return held == null ? null : held.get(section);
    }

    @Override public boolean containsKey(long section) { return get(section) != null; }

    public LightShards with(Long2ObjectOpenHashMap<DataLayer> live, LongSet touched) {
        if (touched.isEmpty()) { return this; }
        Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<DataLayer>> next = table.clone();
        LongSet fresh = new LongOpenHashSet();
        LongIterator sections = touched.iterator();
        while (sections.hasNext()) {
            long section = sections.nextLong();
            long shard = shard(section);
            Long2ObjectOpenHashMap<DataLayer> held = next.get(shard);
            if (held == null) {
                held = new Long2ObjectOpenHashMap<>();
                next.put(shard, held);
                fresh.add(shard);
            }
            else if (fresh.add(shard)) {
                held = held.clone();
                next.put(shard, held);
            }
            DataLayer now = live.get(section);
            if (now == null) { held.remove(section); }
            else { held.put(section, now); }
        }
        LongIterator shards = fresh.iterator();
        while (shards.hasNext()) {
            long shard = shards.nextLong();
            Long2ObjectOpenHashMap<DataLayer> held = next.get(shard);
            if (held != null && held.isEmpty()) { next.remove(shard); }
        }
        return new LightShards(next);
    }
}
