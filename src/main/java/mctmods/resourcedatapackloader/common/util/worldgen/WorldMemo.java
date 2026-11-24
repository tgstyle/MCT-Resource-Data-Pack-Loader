package mctmods.resourcedatapackloader.common.util.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public final class WorldMemo<K, V> {
    private final Map<World, Map<K, V>> held = new WeakHashMap<>();

    @Nullable public V get(World world, K key, Supplier<V> make) {
        Map<K, V> cache = held.computeIfAbsent(world, ignored -> new HashMap<>());
        if (cache.containsKey(key)) { return cache.get(key); }
        V made = make.get();
        cache.put(key, made);
        return made;
    }
}
